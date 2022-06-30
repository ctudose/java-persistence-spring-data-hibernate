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
package com.manning.javapersistence.ch08.embeddablesetofstrings;

import com.manning.javapersistence.ch08.configuration.embeddablesetofstrings.SpringDataConfiguration;
import com.manning.javapersistence.ch08.repositories.embeddablesetofstrings.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class MappingCollectionsSpringDataJPATest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void storeLoadEntities() {
        User john = new User("john");
        Address address = new Address("Flowers Street", "01246", "Boston");
        address.addContact("John Smith");
        address.addContact("Jane Smith");
        john.setAddress(address);

        userRepository.save(john);

        List<User> users = userRepository.findAll();

        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals("Flowers Street", users.get(0).getAddress().getStreet()),
                () -> assertEquals("01246", users.get(0).getAddress().getZipcode()),
                () -> assertEquals("Boston", users.get(0).getAddress().getCity()),
                () -> assertEquals(2, users.get(0).getAddress().getContacts().size())
        );

    }
}
