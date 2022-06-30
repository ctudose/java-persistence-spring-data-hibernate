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
package com.manning.javapersistence.ch15;

import com.manning.javapersistence.ch15.model.Address;
import com.manning.javapersistence.ch15.model.User;
import com.manning.javapersistence.ch15.repositories.AddressManyToManyRepository;
import com.manning.javapersistence.ch15.repositories.UserAddressManyToManyRepository;
import com.manning.javapersistence.ch15.repositories.UserManyToManyRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAddressManyToManyTest {

    @Autowired
    private UserAddressManyToManyRepository userAddressManyToManyRepository;
    
    @Autowired
    private AddressManyToManyRepository addressManyToManyRepository;

    @Autowired
    private UserManyToManyRepository userManyToManyRepository;

    private static List<User> users = new ArrayList<>();

    private static Address address1 = generateAddress("11");
    private static Address address2 = generateAddress("22");
    private static Address address3 = generateAddress("33");

    @BeforeAll
    void beforeAll() {
        addressManyToManyRepository.save(address1);
        addressManyToManyRepository.save(address2);
        addressManyToManyRepository.save(address3);
        userManyToManyRepository.saveAll(generateUsers());
    }

    @Test
    void manyToManyTest() {

        assertAll(
                () -> assertEquals(10, userManyToManyRepository.count()),
                () -> assertEquals(3, addressManyToManyRepository.count()),
                () -> assertEquals(20, userAddressManyToManyRepository.count()),
                () -> assertEquals(2, userAddressManyToManyRepository.countByUserId(users.get(0).getId()))
        );

    }

    private static Address generateAddress(String number) {
        Address address = new Address();
        address.setCity("New York");
        address.setStreet(number + ", 5th Avenue");
        return address;
    }

    private static List<User> generateUsers() {
        User john = new User("john", LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@somedomain.com");
        john.setLevel(1);
        john.setActive(true);
        john.addAddress(address1);
        john.addAddress(address2);

        User mike = new User("mike", LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@somedomain.com");
        mike.setLevel(3);
        mike.setActive(true);
        mike.addAddress(address1);
        mike.addAddress(address3);

        User james = new User("james", LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        james.addAddress(address2);
        james.addAddress(address3);

        User katie = new User("katie", LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        katie.addAddress(address1);
        katie.addAddress(address2);

        User beth = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);
        beth.addAddress(address1);
        beth.addAddress(address3);

        User julius = new User("julius", LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);
        julius.addAddress(address2);
        julius.addAddress(address3);

        User darren = new User("darren", LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);
        darren.addAddress(address1);
        darren.addAddress(address2);

        User marion = new User("marion", LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);
        marion.addAddress(address1);
        marion.addAddress(address3);

        User stephanie = new User("stephanie", LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);
        stephanie.addAddress(address2);
        stephanie.addAddress(address3);

        User burk = new User("burk", LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);
        burk.addAddress(address1);
        burk.addAddress(address2);

        users.add(john);
        users.add(mike);
        users.add(james);
        users.add(katie);
        users.add(beth);
        users.add(julius);
        users.add(darren);
        users.add(marion);
        users.add(stephanie);
        users.add(burk);

        return users;
    }

    @AfterAll
    void afterAll() {
        addressManyToManyRepository.deleteAll();
        userManyToManyRepository.deleteAll();
    }

}
