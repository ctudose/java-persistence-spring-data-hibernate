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
package com.manning.javapersistence.ch08.onetomany.orphanremoval;

import com.manning.javapersistence.ch08.configuration.onetomany.orphanremoval.SpringDataConfiguration;
import com.manning.javapersistence.ch08.repositories.onetomany.orphanremoval.BidRepository;
import com.manning.javapersistence.ch08.repositories.onetomany.orphanremoval.ItemRepository;
import com.manning.javapersistence.ch08.repositories.onetomany.orphanremoval.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class MappingAssociationsSpringDataJPATest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Test
    void storeLoadEntities() {

        User john = new User("John Smith");
        userRepository.save(john);

        Item item = new Item("Foo");

        Bid bid = new Bid(BigDecimal.valueOf(100), item);
        Bid bid2 = new Bid(BigDecimal.valueOf(200), item);
        item.addBid(bid);
        bid.setBidder(john);
        item.addBid(bid2);
        bid2.setBidder(john);

        itemRepository.save(item);

        List<Item> items = itemRepository.findAll();
        Set<Bid> bids = bidRepository.findByItem(item);
        User user = userRepository.findUserWithBids(john.getId());

        assertAll(
                () -> assertEquals(1, items.size()),
                () -> assertEquals(2, bids.size()),
                () -> assertEquals(2, user.getBids().size())
        );

        Item item1 = itemRepository.findItemWithBids(item.getId());
        Bid firstBid = item1.getBids().iterator().next();
        item1.removeBid(firstBid);

        itemRepository.save(item1);

        List<Item> items2 = itemRepository.findAll();
        List<Bid> bids2 = bidRepository.findAll();


        assertAll(
                () -> assertEquals(1, items2.size()),
                () -> assertEquals(1, bids2.size()),
                () -> assertEquals(2, user.getBids().size())
                //FAILURE
                //() -> assertEquals(1, user.getBids().size())
        );
    }
}
