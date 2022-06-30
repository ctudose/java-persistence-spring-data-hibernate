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

import com.manning.javapersistence.testing.listeners.DatabaseOperationsListener;
import com.manning.javapersistence.testing.model.User;
import com.manning.javapersistence.testing.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.manning.javapersistence.testing.UsersHelper.buildUsersList;
import static com.manning.javapersistence.testing.UsersHelper.getIterations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestExecutionListeners(value = {
        DatabaseOperationsListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class ListenersTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        System.out.println("@BeforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("@BeforeEach");
    }

    @Test
    void storeUpdateRetrieve() {
        TestContextManager testContextManager = new TestContextManager(getClass());
        System.out.println("testContextManager.getTestExecutionListeners().size() = " + testContextManager.getTestExecutionListeners().size());
        for (TestExecutionListener listener : testContextManager.getTestExecutionListeners()) {
            System.out.println(listener);
        }

        List<User> users = buildUsersList();
        userRepository.saveAll(users);
        assertEquals(getIterations(), userRepository.findAll().size());
    }

    @AfterEach
    void afterEach() {
        System.out.println("@AfterEach");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("@AfterAll");
    }

}
