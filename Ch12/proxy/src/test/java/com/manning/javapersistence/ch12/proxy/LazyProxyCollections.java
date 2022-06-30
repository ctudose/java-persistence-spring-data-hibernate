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
package com.manning.javapersistence.ch12.proxy;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceUtil;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxyHelper;

import static org.junit.jupiter.api.Assertions.*;

public class LazyProxyCollections {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch12");

    private FetchTestData storeTestData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long[] categoryIds = new Long[3];
        Long[] itemIds = new Long[3];
        Long[] userIds = new Long[3];

        User johndoe = new User("johndoe");
        em.persist(johndoe);
        userIds[0] = johndoe.getId();

        User janeroe = new User("janeroe");
        em.persist(janeroe);
        userIds[1] = janeroe.getId();

        User robertdoe = new User("robertdoe");
        em.persist(robertdoe);
        userIds[2] = robertdoe.getId();

        Category category = new Category("Category One");
        em.persist(category);
        categoryIds[0] = category.getId();

        Item item = new Item("Item One", LocalDate.now().plusDays(1), johndoe);
        em.persist(item);
        itemIds[0] = item.getId();
        category.addItem(item);
        item.addCategory(category);
        for (int i = 1; i <= 3; i++) {
            Bid bid = new Bid(item, robertdoe, new BigDecimal(9 + i));
            item.addBid(bid);
            em.persist(bid);
        }

        category = new Category("Category Two");
        em.persist(category);
        categoryIds[1] = category.getId();

        item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
        em.persist(item);
        itemIds[1] = item.getId();
        category.addItem(item);
        item.addCategory(category);
        for (int i = 1; i <= 1; i++) {
            Bid bid = new Bid(item, janeroe, new BigDecimal(2 + i));
            item.addBid(bid);
            em.persist(bid);
        }

        item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
        em.persist(item);
        itemIds[2] = item.getId();
        category.addItem(item);
        item.addCategory(category);

        category = new Category("Category Three");
        em.persist(category);
        categoryIds[2] = category.getId();

        em.getTransaction().commit();
        em.close();

        FetchTestData testData = new FetchTestData();
        testData.items = new TestData(itemIds);
        testData.users = new TestData(userIds);
        return testData;
    }

    @Test
    public void lazyCollections() {
        FetchTestData testData = storeTestData();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long ITEM_ID = testData.items.getFirstId();

        {
            Item item = em.find(Item.class, ITEM_ID);
            // select * from ITEM where ID = ?

            Set<Bid> bids = item.getBids(); // Collection is not initialized
            PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
            assertFalse(persistenceUtil.isLoaded(item, "bids"));

            // It's a Set
            assertTrue(Set.class.isAssignableFrom(bids.getClass()));

            // It's not a HashSet
            assertNotEquals(HashSet.class, bids.getClass());
            assertEquals(org.hibernate.collection.internal.PersistentSet.class, bids.getClass());

            Bid firstBid = bids.iterator().next();
            // select * from BID where ITEM_ID = ?

            // Alternative: Hibernate.initialize(bids);
        }
        em.clear();
        {
            Item item = em.find(Item.class, ITEM_ID);
            // select * from ITEM where ID = ?

            assertEquals(3, item.getBids().size());
            // select count(b) from BID b where b.ITEM_ID = ?
        }

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void lazyEntityProxies() {
        FetchTestData testData = storeTestData();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long ITEM_ID = testData.items.getFirstId();
        Long USER_ID = testData.users.getFirstId();

        {
            Item item = em.getReference(Item.class, ITEM_ID); // No SELECT

            // Calling identifier getter (no field access!) doesn't trigger initialization
            assertEquals(ITEM_ID, item.getId());

            // The class is runtime generated, named something like: Item$HibernateProxy$BLsrPly8
            assertNotEquals(Item.class, item.getClass());

            assertEquals(
                    Item.class,
                    HibernateProxyHelper.getClassWithoutInitializingProxy(item)
            );

            PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
            assertFalse(persistenceUtil.isLoaded(item));
            assertFalse(persistenceUtil.isLoaded(item, "seller"));

            assertFalse(Hibernate.isInitialized(item));
            // Would trigger initialization of item!
            // assertFalse(Hibernate.isInitialized(item.getSeller()));

            Hibernate.initialize(item);
            // select * from ITEM where ID = ?

            // Let's make sure the default EAGER of @ManyToOne has been overriden with LAZY
            assertFalse(Hibernate.isInitialized(item.getSeller()));

            Hibernate.initialize(item.getSeller());
            // select * from USERS where ID = ?
        }
        em.clear();
        {
                /*
                   An <code>Item</code> entity instance is loaded in the persistence context, its
                   <code>seller</code> is not initialized, it's a <code>User</code> proxy.
                 */
            Item item = em.find(Item.class, ITEM_ID);
            // select * from ITEM where ID = ?

                /*
                   You can manually detach the data from the persistence context, or close the
                   persistence context and detach everything.
                 */
            em.detach(item);
            em.detach(item.getSeller());
            // em.close();

                /*
                   The static <code>PersistenceUtil</code> helper works without a persistence
                   context, you can check at any time if the data you want to access has
                   actually been loaded.
                 */
            PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
            assertTrue(persistenceUtil.isLoaded(item));
            assertFalse(persistenceUtil.isLoaded(item, "seller"));

                /*
                   In detached state, you can call the identifier getter method of the
                   <code>User</code> proxy. However, calling any other method on the proxy,
                   such as <code>getUsername()</code>, will throw a <code>LazyInitializationException</code>.
                   Data can only be loaded on-demand while the persistence context manages the proxy, not in detached
                   state.
                 */
            assertEquals(USER_ID, item.getSeller().getId());
            // Throws exception!
            //assertNotNull(item.getSeller().getUsername());
        }
        em.clear();
        {
            // There is no SQL SELECT in this procedure, only one INSERT!
            Item item = em.getReference(Item.class, ITEM_ID);
            User user = em.getReference(User.class, USER_ID);

            Bid newBid = new Bid(new BigDecimal("99.00"));
            newBid.setItem(item);
            newBid.setBidder(user);

            em.persist(newBid);
            // insert into BID values (?, ? ,? , ...)

            em.flush();
            em.clear();
            assertEquals(0, em.find(Bid.class, newBid.getId()).getAmount().compareTo(new BigDecimal("99")));
        }

        em.getTransaction().commit();
        em.close();
    }
}
