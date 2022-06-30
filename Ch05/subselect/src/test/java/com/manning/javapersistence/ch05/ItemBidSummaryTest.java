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
package com.manning.javapersistence.ch05;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;

import com.manning.javapersistence.ch05.model.Bid;
import com.manning.javapersistence.ch05.model.Item;
import com.manning.javapersistence.ch05.model.ItemBidSummary;

public class ItemBidSummaryTest {

    @Test
    public void itemBidSummaryTest() {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ch05.subselect");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Item item = new Item();
            item.setName("Some Item");
            item.setAuctionEnd(Helper.tomorrow());

            Bid bid1 = new Bid(new BigDecimal(1000.0), item);
            Bid bid2 = new Bid(new BigDecimal(1100.0), item);

            em.persist(item);
            em.persist(bid1);
            em.persist(bid2);

            em.getTransaction().commit();
            em.getTransaction().begin();

            TypedQuery<ItemBidSummary> query =
                    em.createQuery("select ibs from ItemBidSummary ibs where ibs.itemId = :id",
                    ItemBidSummary.class);
            ItemBidSummary itemBidSummary =
                    query.setParameter("id", 1000L).getSingleResult();

            assertAll(
                    () -> assertEquals(1000, itemBidSummary.getItemId()),
                    () -> assertEquals("Some Item", itemBidSummary.getName()),
                    () -> assertEquals(2, itemBidSummary.getNumberOfBids())
            );

            em.getTransaction().commit();

        } finally {
            em.close();
            emf.close();
        }
    }
}
