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
package com.manning.javapersistence.testing;

import com.manning.javapersistence.testing.model.User;
import com.manning.javapersistence.testing.repositories.UserRepository;
import com.manning.javapersistence.testing.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static com.manning.javapersistence.testing.UsersHelper.buildUsersList;
import static com.manning.javapersistence.testing.UsersHelper.getIterations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TransactionalTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("beforeAll, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }


    @RepeatedTest(2)
    void storeRetrieve() {
        List<User> users = buildUsersList();
        userRepository.saveAll(users);
        assertEquals(getIterations(), userRepository.findAll().size());

        userService.saveTransactionally(users.get(0));

        System.out.println("end of method, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @AfterEach
    void afterEach() {
        System.out.println("afterEach, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @AfterAll
    static void afterAll() {
        System.out.println("afterAll, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

}
