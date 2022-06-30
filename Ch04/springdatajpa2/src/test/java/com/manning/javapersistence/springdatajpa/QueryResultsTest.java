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
package com.manning.javapersistence.springdatajpa;

import com.manning.javapersistence.springdatajpa.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryResultsTest extends SpringDataJpaApplicationTests {

    @Test
    void testStreamable() {
        try (Stream<User> result = userRepository.findByEmailContaining("someother")
                .and(userRepository.findByLevel(2))
                .stream().distinct()) {
            assertEquals(6, result.count());
        }
    }

    @Test
    void testNumberOfUsersByActivity() {
        int active = userRepository.findNumberOfUsersByActivity(true);
        int inactive = userRepository.findNumberOfUsersByActivity(false);

        assertAll(
                () -> assertEquals(8, active),
                () -> assertEquals(2, inactive)
        );
    }

    @Test
    void testUsersByLevelAndActivity() {
        List<User> userList1 = userRepository.findByLevelAndActive(1, true);
        List<User> userList2 = userRepository.findByLevelAndActive(2, true);
        List<User> userList3 = userRepository.findByLevelAndActive(2, false);

        assertAll(
                () -> assertEquals(2, userList1.size()),
                () -> assertEquals(2, userList2.size()),
                () -> assertEquals(1, userList3.size())
        );
    }

    @Test
    void testNumberOfUsersByActivityNative() {
        int active = userRepository.findNumberOfUsersByActivityNative(true);
        int inactive = userRepository.findNumberOfUsersByActivityNative(false);

        assertAll(
                () -> assertEquals(8, active),
                () -> assertEquals(2, inactive)
        );
    }

    @Test
    void testFindByAsArrayAndSort() {
        List<Object[]> usersList1 = userRepository.findByAsArrayAndSort("ar", Sort.by("username"));
        List<Object[]> usersList2 = userRepository.findByAsArrayAndSort("ar", Sort.by("email_length").descending());
        List<Object[]> usersList3 = userRepository.findByAsArrayAndSort("ar", JpaSort.unsafe("LENGTH(u.email)"));
        assertAll(
                () -> assertEquals(2, usersList1.size()),
                () -> assertEquals("darren", usersList1.get(0)[0]),
                () -> assertEquals(21, usersList1.get(0)[1]),
                () -> assertEquals(2, usersList2.size()),
                () -> assertEquals("marion", usersList2.get(0)[0]),
                () -> assertEquals(26, usersList2.get(0)[1]),
                () -> assertEquals(2, usersList3.size()),
                () -> assertEquals("darren", usersList3.get(0)[0]),
                () -> assertEquals(21, usersList3.get(0)[1])
        );
    }
}
