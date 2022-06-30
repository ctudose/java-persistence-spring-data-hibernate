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
package com.manning.javapersistence.springdatamongodb.template;

import com.manning.javapersistence.springdatamongodb.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FindUsersSortingAndPagingTest extends SpringDataMongoDBApplicationTests {

    @Test
    void testOrder() {
        Query query1 = new Query();
        query1.with(Sort.by(Sort.Direction.ASC, "username"));
        User user1 = mongoTemplate.find(query1, User.class).get(0);

        Query query2 = new Query();
        query2.with(Sort.by(Sort.Direction.DESC, "registrationDate"));
        User user2 = mongoTemplate.find(query2, User.class).get(0);

        Query query3 = new Query();
        query3.addCriteria(Criteria.where("level").is(2));
        query3.limit(2);
        List<User> users = mongoTemplate.find(query3, User.class);

        Pageable pageableRequest = PageRequest.of(1, 3);
        Query query4 = new Query();
        query4.with(pageableRequest);
        List<User> usersPage = mongoTemplate.find(query4, User.class);

        assertAll(
                () -> assertEquals("beth", user1.getUsername()),
                () -> assertEquals("julius", user2.getUsername()),
                () -> assertEquals(2, users.size()),
                () -> assertEquals(3, usersPage.size()),
                () -> assertEquals("katie", usersPage.get(0).getUsername()),
                () -> assertEquals("beth", usersPage.get(1).getUsername())
        );

    }

    @Test
    void testFindByLevel() {
        Query query = new Query();
        query.addCriteria(Criteria.where("level").is(3));
        query.with(Sort.by(Sort.Direction.DESC, "registrationDate"));
        List<User> users = mongoTemplate.find(query, User.class);

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals("james", users.get(0).getUsername())
        );

    }

    @Test
    void testFindByActive() {
        Pageable pageableRequest = PageRequest.of(1, 4);
        Query query = new Query();
        query.addCriteria(Criteria.where("active").is(Boolean.TRUE));
        query.with(pageableRequest);
        query.with(Sort.by(Sort.Direction.ASC, "registrationDate"));
        List<User> users = mongoTemplate.find(query, User.class);

        assertAll(
                () -> assertEquals(4, users.size()),
                () -> assertEquals("burk", users.get(0).getUsername())
        );

    }
}
