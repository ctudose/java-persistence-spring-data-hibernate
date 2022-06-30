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
package com.manning.javapersistence.ch09.onetomany.list;

import com.manning.javapersistence.ch09.repositories.onetomany.list.BidRepository;
import com.manning.javapersistence.ch09.repositories.onetomany.list.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class TestService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Transactional
    public void storeLoadEntities() {

        Item item = new Item("Foo");
        itemRepository.save(item);

        Bid someBid = new Bid(new BigDecimal("123.00"), item);
        item.addBid(someBid);
        bidRepository.save(someBid);

        Bid secondBid = new Bid(new BigDecimal("456.00"), item);
        item.addBid(secondBid);
        bidRepository.save(secondBid);

        Item item2 = itemRepository.findItemWithBids(item.getId());

        assertAll(
                () -> assertEquals(2, item2.getBids().size()),
                () -> assertEquals(0, item2.getBids().get(0).getAmount().compareTo(new BigDecimal("123"))),
                () -> assertEquals(0, item2.getBids().get(1).getAmount().compareTo(new BigDecimal("456")))
        );

    }
}
