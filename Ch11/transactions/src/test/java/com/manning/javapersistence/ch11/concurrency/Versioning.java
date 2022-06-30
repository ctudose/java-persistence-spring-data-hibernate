/*
 * ========================================================================
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.manning.javapersistence.ch11.concurrency;

import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Versioning {

    static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch11");

    ConcurrencyTestData storeCategoriesAndItems() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        ConcurrencyTestData testData = new ConcurrencyTestData();
        testData.categories = new TestData(new Long[3]);
        testData.items = new TestData(new Long[5]);
        for (int i = 1; i <= testData.categories.identifiers.length; i++) {
            Category category = new Category();
            category.setName("Category: " + i);
            em.persist(category);
            testData.categories.identifiers[i - 1] = category.getId();
            for (int j = 1; j <= testData.categories.identifiers.length; j++) {
                Item item = new Item("Item " + j);
                item.setCategory(category);
                item.setBuyNowPrice(new BigDecimal(10 + j));
                em.persist(item);
                testData.items.identifiers[(i - 1) + (j - 1)] = item.getId();
            }
        }
        em.getTransaction().commit();
        em.close();
        return testData;
    }

    private TestData storeItemAndBids() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Long[] ids = new Long[1];
        Item item = new Item("Some Item");
        em.persist(item);
        ids[0] = item.getId();
        for (int i = 1; i <= 3; i++) {
            Bid bid = new Bid(new BigDecimal(10 + i), item);
            em.persist(bid);
        }
        em.getTransaction().commit();
        em.close();
        return new TestData(ids);
    }

    private Bid queryHighestBid(EntityManager em, Item item) {
        // Can't scroll with cursors in JPA, have to use setMaxResult()
        try {
            return (Bid) em.createQuery(
                    "select b from Bid b" +
                            " where b.item = :itm" +
                            " order by b.amount desc"
            )
                    .setParameter("itm", item)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Test
    void firstCommitWins() throws ExecutionException, InterruptedException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Item someItem = new Item("Some Item");
        em.persist(someItem);
        em.getTransaction().commit();
        em.close();
        final Long ITEM_ID = someItem.getId();

        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();

        /*
           Retrieving an entity instance by identifier loads the current version from the
           database with a <code>SELECT</code>.
        */
        Item item = em1.find(Item.class, ITEM_ID);
        // select * from ITEM where ID = ?

        /*
           The current version of the <code>Item</code> instance is 0.
        */
        assertEquals(0, item.getVersion());

        item.setName("New Name");

        // The concurrent second unit of work doing the same
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                EntityManager em2 = emf.createEntityManager();
                em2.getTransaction().begin();

                Item item1 = em2.find(Item.class, ITEM_ID);
                // select * from ITEM where ID = ?

                assertEquals(0, item1.getVersion());

                item1.setName("Other Name");

                em2.getTransaction().commit();
                // update ITEM set NAME = ?, VERSION = 1 where ID = ? and VERSION = 0
                // This succeeds, there is a row with ID = ? and VERSION = 0 in the database!
                em2.close();

            } catch (Exception ex) {
                // This shouldn't happen, this commit should win!
                throw new RuntimeException("Concurrent operation failure: " + ex, ex);
            }
            return null;
        }).get();

        /*
           When the persistence context is flushed Hibernate will detect the dirty
           <code>Item</code> instance and increment its version to 1. The SQL
           <code>UPDATE</code> now performs the version check, storing the new version
           in the database, but only if the database version is still 0.
        */
        assertThrows(OptimisticLockException.class, () -> em1.flush());
        // update ITEM set NAME = ?, VERSION = 1 where ID = ? and VERSION = 0

    }

    @Test
    void manualVersionChecking() throws ExecutionException, InterruptedException {
        final ConcurrencyTestData testData = storeCategoriesAndItems();
        Long[] CATEGORIES = testData.categories.identifiers;

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Long categoryId : CATEGORIES) {

                /*
                   For each <code>Category</code>, query all <code>Item</code> instances with
                   an <code>OPTIMISTIC</code> lock mode. Hibernate now knows it has to
                   check each <code>Item</code> at flush time.
                 */
            List<Item> items =
                    em.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                            .setLockMode(LockModeType.OPTIMISTIC)
                            .setParameter("catId", categoryId)
                            .getResultList();

            for (Item item : items)
                totalPrice = totalPrice.add(item.getBuyNowPrice());

            // Now a concurrent transaction will move an item to another category
            if (categoryId.equals(testData.categories.getFirstId())) {
                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        EntityManager em1 = emf.createEntityManager();
                        em1.getTransaction().begin();

                        // Moving the first item from the first category into the last category
                        List<Item> items1 =
                                em1.createQuery("select i from Item i where i.category.id = :catId", Item.class)
                                        .setParameter("catId", testData.categories.getFirstId())
                                        .getResultList();

                        Category lastCategory = em1.getReference(
                                Category.class, testData.categories.getLastId()
                        );

                        items1.iterator().next().setCategory(lastCategory);

                        em1.getTransaction().commit();
                        em1.close();
                    } catch (Exception ex) {
                        // This shouldn't happen, this commit should win!
                        throw new RuntimeException("Concurrent operation failure: " + ex, ex);
                    }
                    return null;
                }).get();
            }
        }

            /*
               For each <code>Item</code> loaded earlier with the locking query, Hibernate will
               now execute a <code>SELECT</code> during flushing. It checks if the database
               version of each <code>ITEM</code> row is still the same as when it was loaded
               earlier. If any <code>ITEM</code> row has a different version, or the row doesn't
               exist anymore, an <code>OptimisticLockException</code> will be thrown.
             */
        em.getTransaction().commit();
        em.close();

        assertEquals("108.00", totalPrice.toString());
    }

    @Test
    void forceIncrement() throws Throwable {
        TestData testData = storeItemAndBids();
        Long ITEM_ID = testData.getFirstId();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        /*
           The <code>find()</code> method accepts a <code>LockModeType</code>. The
           <code>OPTIMISTIC_FORCE_INCREMENT</code> mode tells Hibernate that the version
           of the retrieved <code>Item</code> should be incremented after loading,
           even if it's never modified in the unit of work.
        */
        Item item = em.find(
                Item.class,
                ITEM_ID,
                LockModeType.OPTIMISTIC_FORCE_INCREMENT
        );

        Bid highestBid = queryHighestBid(em, item);

        // Now a concurrent transaction will place a bid for this item, and
        // succeed because the first commit wins!
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                EntityManager em1 = emf.createEntityManager();
                em1.getTransaction().begin();

                Item item1 = em1.find(
                        Item.class,
                        testData.getFirstId(),
                        LockModeType.OPTIMISTIC_FORCE_INCREMENT
                );
                Bid highestBid1 = queryHighestBid(em1, item1);

                Bid newBid = new Bid(
                        new BigDecimal("44.44"),
                        item1,
                        highestBid1
                );
                em1.persist(newBid);

                em1.getTransaction().commit();
                em1.close();
            } catch (Exception ex) {
                // This shouldn't happen, this commit should win!
                throw new RuntimeException("Concurrent operation failure: " + ex, ex);
            }
            return null;
        }).get();

        /*
           The code persists a new <code>Bid</code> instance; this does not affect
           any values of the <code>Item</code> instance. A new row will be inserted
           into the <code>BID</code> table. Hibernate would not detect concurrently
           made bids at all without a forced version increment of the
           <code>Item</code>. We also use a checked exception to validate the
           new bid amount; it must be greater than the currently highest bid.
        */
        Bid newBid = new Bid(
                new BigDecimal("45.45"),
                item,
                highestBid
        );
        em.persist(newBid);

        /*
            When flushing the persistence context, Hibernate will execute an
            <code>INSERT</code> for the new <code>Bid</code> and force an
            <code>UPDATE</code> of the <code>Item</code> with a version check.
            If someone modified the <code>Item</code> concurrently, or placed a
            <code>Bid</code> concurrently with this procedure, Hibernate throws
            an exception.
        */
        assertThrows(RollbackException.class, () -> em.getTransaction().commit());
        em.close();
    }

}
