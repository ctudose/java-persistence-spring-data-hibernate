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
package com.manning.javapersistence.ch12.fetchloadgraph;

import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FetchLoadGraph {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch12");

    private FetchTestData storeTestData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long[] itemIds = new Long[3];
        Long[] userIds = new Long[3];
        Long[] bidIds = new Long[3];

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
            bidIds[i - 1] = bid.getId();
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
        testData.bids = new TestData(bidIds);
        testData.users = new TestData(userIds);
        return testData;
    }

    @Test
    public void loadItem() {
        FetchTestData testData = storeTestData();
        Long ITEM_ID = testData.items.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "javax.persistence.loadgraph",
                    em.getEntityGraph(Item.class.getSimpleName()) // "Item"
            );

            Item item = em.find(Item.class, ITEM_ID, properties);
            // select * from ITEM where ID = ?

            assertTrue(persistenceUtil.isLoaded(item));
            assertTrue(persistenceUtil.isLoaded(item, "name"));
            assertTrue(persistenceUtil.isLoaded(item, "auctionEnd"));
            assertFalse(persistenceUtil.isLoaded(item, "seller"));
            assertFalse(persistenceUtil.isLoaded(item, "bids"));

            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);

            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.loadgraph", itemGraph);

            Item item = em.find(Item.class, ITEM_ID, properties);

            assertTrue(persistenceUtil.isLoaded(item));
            assertTrue(persistenceUtil.isLoaded(item, "name"));
            assertTrue(persistenceUtil.isLoaded(item, "auctionEnd"));
            assertFalse(persistenceUtil.isLoaded(item, "seller"));
            assertFalse(persistenceUtil.isLoaded(item, "bids"));

            em.getTransaction().commit();
            em.close();
        }
    }


    @Test
    public void loadItemSeller() {
        FetchTestData testData = storeTestData();
        Long ITEM_ID = testData.items.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "javax.persistence.loadgraph",
                    em.getEntityGraph("ItemSeller")
            );

            Item item = em.find(Item.class, ITEM_ID, properties);
            // select i.*, u.*
            //  from ITEM i
            //   inner join USERS u on u.ID = i.SELLER_ID
            // where i.ID = ?

            assertTrue(persistenceUtil.isLoaded(item));
            assertTrue(persistenceUtil.isLoaded(item, "name"));
            assertTrue(persistenceUtil.isLoaded(item, "auctionEnd"));
            assertTrue(persistenceUtil.isLoaded(item, "seller"));
            assertFalse(persistenceUtil.isLoaded(item, "bids"));

            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);
            itemGraph.addAttributeNodes(Item_.seller); // Static metamodel

            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.loadgraph", itemGraph);

            Item item = em.find(Item.class, ITEM_ID, properties);
            // select i.*, u.*
            //  from ITEM i
            //   inner join USERS u on u.ID = i.SELLER_ID
            // where i.ID = ?

            assertTrue(persistenceUtil.isLoaded(item));
            assertTrue(persistenceUtil.isLoaded(item, "name"));
            assertTrue(persistenceUtil.isLoaded(item, "auctionEnd"));
            assertTrue(persistenceUtil.isLoaded(item, "seller"));
            assertFalse(persistenceUtil.isLoaded(item, "bids"));

            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            EntityGraph<Item> itemGraph = em.createEntityGraph(Item.class);
            itemGraph.addAttributeNodes("seller");

            List<Item> items =
                    em.createQuery("select i from Item i", Item.class)
                            .setHint("javax.persistence.loadgraph", itemGraph)
                            .getResultList();
            // select i.*, u.*
            //  from ITEM i
            //   left outer join USERS u on u.ID = i.SELLER_ID

            assertEquals(3, items.size());

            for (Item item : items) {
                assertTrue(persistenceUtil.isLoaded(item));
                assertTrue(persistenceUtil.isLoaded(item, "name"));
                assertTrue(persistenceUtil.isLoaded(item, "auctionEnd"));
                assertTrue(persistenceUtil.isLoaded(item, "seller"));
                assertFalse(persistenceUtil.isLoaded(item, "bids"));
            }

            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void loadBidBidderItem() {
        FetchTestData testData = storeTestData();
        Long BID_ID = testData.bids.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "javax.persistence.loadgraph",
                    em.getEntityGraph("BidBidderItem")
            );

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertTrue(persistenceUtil.isLoaded(bid));
            assertTrue(persistenceUtil.isLoaded(bid, "amount"));
            assertTrue(persistenceUtil.isLoaded(bid, "bidder"));
            assertTrue(persistenceUtil.isLoaded(bid, "item"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "name"));
            assertFalse(persistenceUtil.isLoaded(bid.getItem(), "seller"));

            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            EntityGraph<Bid> bidGraph = em.createEntityGraph(Bid.class);
            bidGraph.addAttributeNodes("bidder", "item");

            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.loadgraph", bidGraph);

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertTrue(persistenceUtil.isLoaded(bid));
            assertTrue(persistenceUtil.isLoaded(bid, "amount"));
            assertTrue(persistenceUtil.isLoaded(bid, "bidder"));
            assertTrue(persistenceUtil.isLoaded(bid, "item"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "name"));
            assertFalse(persistenceUtil.isLoaded(bid.getItem(), "seller"));

            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    public void loadBidBidderItemSellerBids() throws Exception {
        FetchTestData testData = storeTestData();
        Long BID_ID = testData.bids.getFirstId();
        PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Map<String, Object> properties = new HashMap<>();
            properties.put(
                    "javax.persistence.loadgraph",
                    em.getEntityGraph("BidBidderItemSellerBids")
            );

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertTrue(persistenceUtil.isLoaded(bid));
            assertTrue(persistenceUtil.isLoaded(bid, "amount"));
            assertTrue(persistenceUtil.isLoaded(bid, "bidder"));
            assertTrue(persistenceUtil.isLoaded(bid, "item"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "name"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "seller"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem().getSeller(), "username"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "bids"));

            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            EntityGraph<Bid> bidGraph = em.createEntityGraph(Bid.class);
            bidGraph.addAttributeNodes(Bid_.bidder, Bid_.item);
            Subgraph<Item> itemGraph = bidGraph.addSubgraph(Bid_.item);
            itemGraph.addAttributeNodes(Item_.seller, Item_.bids);

            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.loadgraph", bidGraph);

            Bid bid = em.find(Bid.class, BID_ID, properties);

            assertTrue(persistenceUtil.isLoaded(bid));
            assertTrue(persistenceUtil.isLoaded(bid, "amount"));
            assertTrue(persistenceUtil.isLoaded(bid, "bidder"));
            assertTrue(persistenceUtil.isLoaded(bid, "item"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "name"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "seller"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem().getSeller(), "username"));
            assertTrue(persistenceUtil.isLoaded(bid.getItem(), "bids"));

            em.getTransaction().commit();
            em.close();
        }
    }
}
