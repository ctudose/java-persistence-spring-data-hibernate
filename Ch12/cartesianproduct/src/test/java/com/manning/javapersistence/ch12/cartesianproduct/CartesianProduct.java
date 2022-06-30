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
package com.manning.javapersistence.ch12.cartesianproduct;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartesianProduct {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch12");

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
        item.addImage("foo.jpg");
        item.addImage("bar.jpg");
        item.addImage("baz.jpg");
        em.persist(item);
        itemIds[0] = item.getId();
        for (int i = 1; i <= 3; i++) {
            Bid bid = new Bid(item, new BigDecimal(9 + i));
            item.getBids().add(bid);
            em.persist(bid);
        }

        item = new Item("Item Two", LocalDate.now().plusDays(1), johndoe);
        item.addImage("a.jpg");
        item.addImage("b.jpg");
        em.persist(item);
        itemIds[1] = item.getId();
        for (int i = 1; i <= 1; i++) {
            Bid bid = new Bid(item, new BigDecimal(2 + i));
            item.getBids().add(bid);
            em.persist(bid);
        }

        item = new Item("Item Three", LocalDate.now().plusDays(2), janeroe);
        em.persist(item);
        itemIds[2] = item.getId();

        em.getTransaction().commit();
        em.close();

        FetchTestData testData = new FetchTestData();
        testData.items = new TestData(itemIds);
        testData.users = new TestData(userIds);
        return testData;
    }

    @Test
    public void fetchCollections() {
        FetchTestData testData = storeTestData();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long ITEM_ID = testData.items.getFirstId();

        Item item = em.find(Item.class, ITEM_ID);
        // select i.*, b.*, img.*
        //  from ITEM i
        //   left outer join BID b on b.ITEM_ID = i.ID
        //   left outer join IMAGE img on img.ITEM_ID = i.ID
        //  where i.ID = ?

        em.detach(item);

        assertEquals(3, item.getImages().size());
        assertEquals(3, item.getBids().size());

        em.getTransaction().commit();
        em.close();
    }

}
