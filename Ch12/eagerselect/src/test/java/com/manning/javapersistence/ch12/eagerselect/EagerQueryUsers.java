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
package com.manning.javapersistence.ch12.eagerselect;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EagerQueryUsers {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch12_1");

    private FetchTestData storeTestData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

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

        Item item = new Item("Item One", LocalDate.now().plusDays(1), johndoe);
        em.persist(item);
        itemIds[0] = item.getId();
        for (int i = 1; i <= 3; i++) {
            Bid bid = new Bid(item, robertdoe, new BigDecimal(9 + i));
            item.addBid(bid);
            em.persist(bid);
        }

        item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
        em.persist(item);
        itemIds[1] = item.getId();
        for (int i = 1; i <= 1; i++) {
            Bid bid = new Bid(item, janeroe, new BigDecimal(2 + i));
            item.addBid(bid);
            em.persist(bid);
        }

        item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
        em.persist(item);
        itemIds[2] = item.getId();
        for (int i = 1; i <= 1; i++) {
            Bid bid = new Bid(item, johndoe, new BigDecimal(3 + i));
            item.addBid(bid);
            em.persist(bid);
        }

        em.getTransaction().commit();
        em.close();

        FetchTestData testData = new FetchTestData();
        testData.items = new TestData(itemIds);
        testData.users = new TestData(userIds);
        return testData;
    }

    @Test
    public void fetchUsers() {
        storeTestData();

        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            List<Item> items =
                    em.createQuery("select i from Item i join fetch i.seller", Item.class)
                            .getResultList();
            // select i.*, u.*
            //  from ITEM i
            //   inner join USERS u on u.ID = i.SELLER_ID
            //  where i.ID = ?

            em.close(); // Detach all

            for (Item item : items) {
                assertNotNull(item.getSeller().getUsername());
            }
        }
        {
            EntityManager em = emf.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Item> criteria = cb.createQuery(Item.class);

            Root<Item> i = criteria.from(Item.class);
            i.fetch("seller");
            criteria.select(i);

            List<Item> items = em.createQuery(criteria).getResultList();

            em.close(); // Detach all

            for (Item item : items) {
                assertNotNull(item.getSeller().getUsername());
            }
        }
    }


}
