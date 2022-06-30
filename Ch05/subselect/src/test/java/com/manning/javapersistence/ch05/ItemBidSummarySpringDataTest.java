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

import com.manning.javapersistence.ch05.configuration.SpringDataConfiguration;
import com.manning.javapersistence.ch05.model.Bid;
import com.manning.javapersistence.ch05.model.Item;
import com.manning.javapersistence.ch05.model.ItemBidSummary;
import com.manning.javapersistence.ch05.repositories.BidRepository;
import com.manning.javapersistence.ch05.repositories.ItemBidSummaryRepository;
import com.manning.javapersistence.ch05.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class ItemBidSummarySpringDataTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ItemBidSummaryRepository itemBidSummaryRepository;

    @Test
    public void itemBidSummaryTest() {

        Item item = new Item();
        item.setName("Some Item");
        item.setAuctionEnd(Helper.tomorrow());

        Bid bid1 = new Bid(new BigDecimal(1000.0), item);
        Bid bid2 = new Bid(new BigDecimal(1100.0), item);

        itemRepository.save(item);
        bidRepository.save(bid1);
        bidRepository.save(bid2);

        Optional<ItemBidSummary> itemBidSummary = itemBidSummaryRepository.findById(1000L);

        assertAll(
                () -> assertEquals(1000, itemBidSummary.get().getItemId()),
                () -> assertEquals("Some Item", itemBidSummary.get().getName()),
                () -> assertEquals(2, itemBidSummary.get().getNumberOfBids())
        );

    }
}