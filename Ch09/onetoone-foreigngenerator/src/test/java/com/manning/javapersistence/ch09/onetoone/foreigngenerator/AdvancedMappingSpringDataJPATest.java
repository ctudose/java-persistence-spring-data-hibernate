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
package com.manning.javapersistence.ch09.onetoone.foreigngenerator;

import com.manning.javapersistence.ch09.configuration.onetoone.foreigngenerator.SpringDataConfiguration;
import com.manning.javapersistence.ch09.repositories.onetoone.foreigngenerator.AddressRepository;
import com.manning.javapersistence.ch09.repositories.onetoone.foreigngenerator.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class AdvancedMappingSpringDataJPATest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void testStoreLoadEntities() {

        User john = new User("John Smith");

        Address address =
                new Address(john, "Flowers Street", "01246", "Boston");

        john.setShippingAddress(address);

        userRepository.save(john);

        User user = userRepository.findById(john.getId()).get();
        Address address2 = addressRepository.findById(address.getId()).get();

        assertAll(
                () -> assertEquals("Flowers Street", user.getShippingAddress().getStreet()),
                () -> assertEquals("01246", user.getShippingAddress().getZipcode()),
                () -> assertEquals("Boston", user.getShippingAddress().getCity()),
                () -> assertEquals("Flowers Street", address2.getStreet()),
                () -> assertEquals("01246", address2.getZipcode()),
                () -> assertEquals("Boston", address2.getCity())
        );

    }
}
