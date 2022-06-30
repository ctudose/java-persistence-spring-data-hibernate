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
package com.manning.javapersistence.springdatamongodb;

import com.manning.javapersistence.springdatamongodb.model.Address;
import com.manning.javapersistence.springdatamongodb.model.User;
import com.manning.javapersistence.springdatamongodb.repositories.AddressRepository;
import com.manning.javapersistence.springdatamongodb.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class SpringDataMongoDBApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    private static Address address = new Address("Flowers Street", "1234567", "Boston", "MA");

    @BeforeAll
    void beforeAll() {
        addressRepository.save(address);
        userRepository.saveAll(generateUsers());
    }

    private static List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        User john = new User("john", "John", "Smith");
        john.setRegistrationDate(LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@somedomain.com");
        john.setLevel(1);
        john.setActive(true);
        john.setPassword("password1");
        john.setAddress(address);

        User mike = new User("mike", "Mike", "Nolan");
        mike.setRegistrationDate(LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@somedomain.com");
        mike.setLevel(3);
        mike.setActive(true);
        mike.setPassword("password2");
        mike.setAddress(address);

        User james = new User("james", "James", "Woods");
        james.setRegistrationDate(LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        james.setPassword("password3");
        james.setAddress(address);

        User katie = new User("katie", "Katie", "Sposato");
        katie.setRegistrationDate(LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        katie.setPassword("password4");
        katie.setAddress(address);

        User beth = new User("beth", "Beth", "Gerrard");
        beth.setRegistrationDate(LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);
        beth.setPassword("password5");

        User julius = new User("julius", "Julius", "Graves");
        julius.setRegistrationDate(LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);
        julius.setPassword("password6");

        User darren = new User("darren", "Darren", "Perkins");
        darren.setRegistrationDate(LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);
        darren.setPassword("password7");

        User marion = new User("marion", "Marion", "Sherman");
        marion.setRegistrationDate(LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);
        marion.setPassword("password8");

        User stephanie = new User("stephanie", "Stephanie", "Christensen");
        stephanie.setRegistrationDate(LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);
        stephanie.setPassword("password9");

        User burk = new User("burk", "Burk", "Smith");
        burk.setRegistrationDate(LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);
        burk.setPassword("password10");

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
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }


}
