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

import com.manning.javapersistence.springdatamongodb.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FindUsersTest extends SpringDataMongoDBApplicationTests {

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertEquals(10, users.size());
    }

    @Test
    void testFindUser() {
        User beth = userRepository.findByUsername("beth").get();
        assertAll(
                () -> assertNotNull(beth.getId()),
                () -> assertEquals("beth", beth.getUsername())
        );
    }

    @Test
    void testFindAllByOrderByUsernameAsc() {
        List<User> users = userRepository.findAllByOrderByUsernameAsc();
        assertAll(() -> assertEquals(10, users.size()),
                () -> assertEquals("beth", users.get(0).getUsername()),
                () -> assertEquals("stephanie", users.get(users.size() - 1).getUsername()));
    }

    @Test
    void testFindByRegistrationDateBetween() {
        List<User> users = userRepository.findByRegistrationDateBetween(
                LocalDate.of(2020, Month.JULY, 1),
                LocalDate.of(2020, Month.DECEMBER, 31));
        assertEquals(4, users.size());
    }

    @Test
    void testFindByUsernameEmail() {
        List<User> usersList1 = userRepository.findByUsernameAndEmail("mike", "mike@somedomain.com");
        List<User> usersList2 = userRepository.findByUsernameOrEmail("mike", "beth@somedomain.com");
        List<User> usersList3 = userRepository.findByUsernameAndEmail("mike", "beth@somedomain.com");
        List<User> usersList4 = userRepository.findByUsernameOrEmail("beth", "beth@somedomain.com");

        assertAll(
                () -> assertEquals(1, usersList1.size()),
                () -> assertEquals(2, usersList2.size()),
                () -> assertEquals(0, usersList3.size()),
                () -> assertEquals(1, usersList4.size())
        );
    }

    @Test
    void testFindByUsernameIgnoreCase() {
        List<User> users = userRepository.findByUsernameIgnoreCase("MIKE");

        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals("mike", users.get(0).getUsername())
        );
    }

    @Test
    void testFindByLevelOrderByUsernameDesc() {
        List<User> users = userRepository.findByLevelOrderByUsernameDesc(1);

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals("john", users.get(0).getUsername()),
                () -> assertEquals("burk", users.get(1).getUsername())
        );
    }

    @Test
    void testFindByLevelGreaterThanEqual() {
        List<User> users = userRepository.findByLevelGreaterThanEqual(3);

        assertEquals(5, users.size());
    }

    @Test
    void testFindByUsername() {
        List<User> usersContaining = userRepository.findByUsernameContaining("ar");
        List<User> usersLike = userRepository.findByUsernameLike("ar");
        List<User> usersStarting = userRepository.findByUsernameStartingWith("b");
        List<User> usersEnding = userRepository.findByUsernameEndingWith("ie");

        assertAll(
                () -> assertEquals(2, usersContaining.size()),
                () -> assertEquals(2, usersLike.size()),
                () -> assertEquals(2, usersStarting.size()),
                () -> assertEquals(2, usersEnding.size())
        );
    }

    @Test
    void testFindByActive() {
        List<User> usersActive = userRepository.findByActive(true);
        List<User> usersNotActive = userRepository.findByActive(false);

        assertAll(
                () -> assertEquals(8, usersActive.size()),
                () -> assertEquals(2, usersNotActive.size())
        );
    }

    @Test
    void testFindByRegistrationDateInNotIn() {
        LocalDate date1 = LocalDate.of(2020, Month.JANUARY, 18);
        LocalDate date2 = LocalDate.of(2021, Month.JANUARY, 5);

        List<LocalDate> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);

        List<User> usersList1 = userRepository.findByRegistrationDateIn(dates);
        List<User> usersList2 = userRepository.findByRegistrationDateNotIn(dates);

        assertAll(
                () -> assertEquals(3, usersList1.size()),
                () -> assertEquals(7, usersList2.size())
        );
    }

    @Test
    void findByLastName() {
        assertEquals(2, userRepository.findByLastName("Smith").size());
    }

}
