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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class TransactionsManagementTest {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private LogRepository logRepository;

    @BeforeTransaction
    void beforeTransaction() {
        Assumptions.assumeFalse(TransactionSynchronizationManager.isActualTransactionActive());
//        logRepository.save(new Log("@BeforeTransaction"));
    }


    @RepeatedTest(2)
//    @Rollback(value = false)
//    @Commit
    void storeUpdateRetrieve() {
        Assumptions.assumeTrue(TransactionSynchronizationManager.isActualTransactionActive());

        List<User> users = UsersHelper.buildUsersList();
        userRepository.saveAll(users);

        for (User user : users) {
            user.setName("Updated " + user.getName());
        }

        userRepository.saveAll(users);

        Assertions.assertEquals(UsersHelper.getIterations(), userRepository.findAll().size());

    }

    @AfterTransaction
    void afterTransaction() {
        Assumptions.assumeFalse(TransactionSynchronizationManager.isActualTransactionActive());
//        logRepository.save(new Log("@AfterTransaction"));
    }

}
