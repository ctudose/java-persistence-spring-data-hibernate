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
package com.manning.javapersistence.hibernateogm;

import com.manning.javapersistence.hibernateogm.model.Address;
import com.manning.javapersistence.hibernateogm.model.Bid;
import com.manning.javapersistence.hibernateogm.model.Item;
import com.manning.javapersistence.hibernateogm.model.User;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateOGMTest {

    private static EntityManagerFactory entityManagerFactory;
    private User john;
    private Item item;
    private Bid bid1, bid2;

    @BeforeAll
    static void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("ch18.hibernate_ogm");
    }

    @BeforeEach
    void beforeEach() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            john = new User("John", "Smith");
            john.setAddress(new Address("Flowers Street", "12345", "Boston"));

            bid1 = new Bid(BigDecimal.valueOf(1000));
            bid2 = new Bid(BigDecimal.valueOf(2000));

            item = new Item("Item1");

            bid1.setItem(item);
            item.addBid(bid1);

            bid2.setItem(item);
            item.addBid(bid2);

            bid1.setUser(john);
            john.addBid(bid1);

            bid2.setUser(john);
            john.addBid(bid2);

            entityManager.persist(item);
            entityManager.persist(john);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @Test
    void testCRUDOperations() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            User fetchedUser = entityManager.find(User.class, john.getId());
            Item fetchedItem = entityManager.find(Item.class, item.getId());
            Bid fetchedBid1 = entityManager.find(Bid.class, bid1.getId());
            Bid fetchedBid2 = entityManager.find(Bid.class, bid2.getId());
            assertAll(
                    () -> assertNotNull(fetchedUser),
                    () -> assertEquals("John", fetchedUser.getFirstName()),
                    () -> assertEquals("Smith", fetchedUser.getLastName()),
                    () -> assertNotNull(fetchedItem),
                    () -> assertEquals("Item1", fetchedItem.getName()),
                    () -> assertNotNull(fetchedBid1),
                    () -> assertEquals(new BigDecimal(1000), fetchedBid1.getAmount()),
                    () -> assertNotNull(fetchedBid2),
                    () -> assertEquals(new BigDecimal(2000), fetchedBid2.getAmount())
            );
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @Test
    void testJPQLQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            List<Bid> bids = entityManager.createQuery("SELECT b FROM Bid b ORDER BY b.amount DESC", Bid.class).getResultList();
            Item item = entityManager.createQuery("SELECT i FROM Item i", Item.class).getSingleResult();
            User user = entityManager.createQuery("SELECT u FROM User u", User.class).getSingleResult();

            assertAll(() -> assertEquals(2, bids.size()),
                    () -> assertEquals(new BigDecimal(2000), bids.get(0).getAmount()),
                    () -> assertEquals(new BigDecimal(1000), bids.get(1).getAmount()),
                    () -> assertEquals("Item1", item.getName()),
                    () -> assertEquals("John", user.getFirstName()),
                    () -> assertEquals("Smith", user.getLastName())
            );
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @AfterEach
    void afterEach() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            User fetchedUser = entityManager.find(User.class, john.getId());
            Item fetchedItem = entityManager.find(Item.class, item.getId());
            Bid fetchedBid1 = entityManager.find(Bid.class, bid1.getId());
            Bid fetchedBid2 = entityManager.find(Bid.class, bid2.getId());

            entityManager.remove(fetchedBid1);
            entityManager.remove(fetchedBid2);
            entityManager.remove(fetchedItem);
            entityManager.remove(fetchedUser);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @AfterAll
    static void tearDown() {
        entityManagerFactory.close();
    }
}
