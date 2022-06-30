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
package com.manning.javapersistence.ch09.onetoone.jointable;

import com.manning.javapersistence.ch09.configuration.onetoone.jointable.SpringDataConfiguration;
import com.manning.javapersistence.ch09.repositories.onetoone.jointable.ItemRepository;
import com.manning.javapersistence.ch09.repositories.onetoone.jointable.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class AdvancedMappingSpringDataJPATest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testStoreLoadEntities() {

        Shipment shipment = new Shipment();
        shipmentRepository.save(shipment);

        Item item = new Item("Foo");
        itemRepository.save(item);

        Shipment auctionShipment = new Shipment(item);
        shipmentRepository.save(auctionShipment);

        Item item2 = itemRepository.findById(item.getId()).get();
        Shipment shipment2 = shipmentRepository.findById(shipment.getId()).get();
        Shipment auctionShipment2 = shipmentRepository.findShipmentWithItem(auctionShipment.getId());

        assertAll(
                () -> assertNull(shipment2.getAuction()),
                () -> assertEquals(item2.getId(), auctionShipment2.getAuction().getId()),
                () -> assertEquals(item2.getName(), auctionShipment2.getAuction().getName())
        );

    }
}
