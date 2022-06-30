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
package com.manning.javapersistence.ch09.onetomany.embeddablejointable;

import com.manning.javapersistence.ch09.repositories.onetomany.embeddablejointable.ShipmentRepository;
import com.manning.javapersistence.ch09.repositories.onetomany.embeddablejointable.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Transactional
    public void storeLoadEntities() {
        User user = new User("John Smith");
        Address deliveryAddress = new Address("Flowers Street", "01246", "Boston");
        user.setShippingAddress(deliveryAddress);
        userRepository.save(user);

        Shipment firstShipment = new Shipment();
        deliveryAddress.addDelivery(firstShipment);
        shipmentRepository.save(firstShipment);

        Shipment secondShipment = new Shipment();
        deliveryAddress.addDelivery(secondShipment);
        shipmentRepository.save(secondShipment);

        User johnsmith = userRepository.findById(user.getId()).get();

        assertEquals(2, johnsmith.getShippingAddress().getDeliveries().size());
    }
}
