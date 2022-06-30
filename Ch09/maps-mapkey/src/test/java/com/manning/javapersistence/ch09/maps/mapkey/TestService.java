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
package com.manning.javapersistence.ch09.maps.mapkey;

import com.manning.javapersistence.ch09.repositories.maps.mapkey.BidRepository;
import com.manning.javapersistence.ch09.repositories.maps.mapkey.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class TestService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Transactional
    public void storeLoadEntities() {
        Item someItem = new Item("Some Item");
        itemRepository.save(someItem);

        Bid someBid = new Bid(new BigDecimal("123.00"), someItem);
        bidRepository.save(someBid);
        someItem.addBid(someBid.getId(), someBid);

        Bid secondBid = new Bid(new BigDecimal("456.00"), someItem);
        bidRepository.save(secondBid);
        someItem.addBid(secondBid.getId(), secondBid);

        Item item = itemRepository.findById(someItem.getId()).get();

        assertEquals(2, item.getBids().size());

        for (Map.Entry<Long, Bid> entry : item.getBids().entrySet()) {
            // The key is the identifier of each Bid
            assertEquals(entry.getKey(), entry.getValue().getId());
        }
    }


}
