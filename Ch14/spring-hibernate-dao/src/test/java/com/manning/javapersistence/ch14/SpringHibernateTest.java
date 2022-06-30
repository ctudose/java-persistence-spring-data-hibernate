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
package com.manning.javapersistence.ch14;

import com.manning.javapersistence.ch14.configuration.SpringConfiguration;
import com.manning.javapersistence.ch14.dao.BidDao;
import com.manning.javapersistence.ch14.dao.ItemDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfiguration.class})
//@ContextConfiguration("classpath:application-context.xml")
public class SpringHibernateTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BidDao bidDao;

    @BeforeEach
    public void setUp() {
        databaseService.init();
    }

    @Test
    public void testInsertItems() {
        List<Item> itemsList = itemDao.getAll();
        List<Bid> bidsList = bidDao.getAll();
        assertAll(
                () -> assertNotNull(itemsList),
                () -> assertEquals(10, itemsList.size()),
                () -> assertNotNull(itemDao.findByName("Item 1")),
                () -> assertNotNull(bidsList),
                () -> assertEquals(20, bidsList.size()),
                () -> assertEquals(10, bidDao.findByAmount("1000.00").size()));
    }

    @Test
    public void testDeleteItem() {
        itemDao.delete(itemDao.findByName("Item 2"));
        assertNull(itemDao.findByName("Item 2"));
    }

    @Test
    public void testUpdateItem() {
        Item item1 = itemDao.findByName("Item 1");
        itemDao.update(item1.getId(), "Item 1_updated");
        assertEquals("Item 1_updated", itemDao.getById(item1.getId()).getName());
    }

    @Test
    public void testInsertBid() {
        Item item3 = itemDao.findByName("Item 3");
        Bid newBid = new Bid(new BigDecimal("2000.00"), item3);
        bidDao.insert(newBid);
        assertAll(
                () -> assertEquals(new BigDecimal("2000.00"), bidDao.getById(newBid.getId()).getAmount()),
                () -> assertEquals(21, bidDao.getAll().size()));
    }

    @Test
    public void testUpdateBid() {
        Bid bid = bidDao.findByAmount("1000.00").get(0);
        bidDao.update(bid.getId(), "1200.00");
        assertEquals(new BigDecimal("1200.00"), bidDao.getById(bid.getId()).getAmount());
    }

    @Test
    public void testDeleteBid() {
        Bid bid = bidDao.findByAmount("1000.00").get(0);
        bidDao.delete(bid);
        assertEquals(19, bidDao.getAll().size());
    }

    @AfterEach
    public void dropDown() {
        databaseService.clear();
    }

}
