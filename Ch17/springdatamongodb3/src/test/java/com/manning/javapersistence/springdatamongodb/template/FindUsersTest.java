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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FindUsersTest extends SpringDataMongoDBApplicationTests {

    @Test
    void testFindAll() {
        List<User> users = mongoTemplate.find(new Query(), User.class);
        assertEquals(10, users.size());
    }

    @Test
    void testFindUser() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("beth"));

        List<User> users = mongoTemplate.find(query, User.class);
        assertAll(
                () -> assertNotNull(users.get(0).getId()),
                () -> assertEquals("beth", users.get(0).getUsername())
        );
    }

    @Test
    void testFindAllByOrderByUsernameAsc() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "username"));

        List<User> users = mongoTemplate.find(query, User.class);
        assertAll(() -> assertEquals(10, users.size()),
                () -> assertEquals("beth", users.get(0).getUsername()),
                () -> assertEquals("stephanie", users.get(users.size() - 1).getUsername()));
    }

    @Test
    void testFindByRegistrationDateBetween() {
        Query query = new Query();
        query.addCriteria(Criteria.where("registrationDate").gte(LocalDate.of(2020, Month.JULY, 1)).lt(LocalDate.of(2020, Month.DECEMBER, 31)));

        List<User> users = mongoTemplate.find(query, User.class);
        assertEquals(4, users.size());
    }

    @Test
    void testFindByUsernameEmail() {
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("username").is("mike").andOperator(Criteria.where("email").is("mike@somedomain.com")));
        List<User> usersList1 = mongoTemplate.find(query1, User.class);

        Query query2 = new Query(new Criteria().orOperator(Criteria.where("username").is("mike"), Criteria.where("email").is("beth@somedomain.com")));
        List<User> usersList2 = mongoTemplate.find(query2, User.class);

        Query query3 = new Query(new Criteria().orOperator(Criteria.where("username").is("mike"), Criteria.where("email").is("beth@somedomain.com")));
        query3.addCriteria(Criteria.where("username").is("mike").andOperator(Criteria.where("email").is("beth@somedomain.com")));
        List<User> usersList3 = mongoTemplate.find(query3, User.class);

        Query query4 = new Query(new Criteria().orOperator(Criteria.where("username").is("beth"), Criteria.where("email").is("beth@somedomain.com")));
        List<User> usersList4 = mongoTemplate.find(query4, User.class);

        assertAll(
                () -> assertEquals(1, usersList1.size()),
                () -> assertEquals(2, usersList2.size()),
                () -> assertEquals(0, usersList3.size()),
                () -> assertEquals(1, usersList4.size())
        );
    }

    @Test
    void testFindByUsernameIgnoreCase() {
        Query query = new Query();
        Criteria regex = Criteria.where("username").regex("MIKE", "i");
        query.addCriteria(regex);
        List<User> users = mongoTemplate.find(query, User.class);

        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals("mike", users.get(0).getUsername())
        );
    }

    @Test
    void testFindByLevelOrderByUsernameDesc() {
        Query query = new Query();
        query.addCriteria(Criteria.where("level").is(1)).with(Sort.by(Sort.Direction.DESC, "username"));
        List<User> users = mongoTemplate.find(query, User.class);

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals("john", users.get(0).getUsername()),
                () -> assertEquals("burk", users.get(1).getUsername())
        );
    }

    @Test
    void testFindByLevelGreaterThanEqual() {
        Query query = new Query();
        query.addCriteria(Criteria.where("level").gte(3));
        List<User> users = mongoTemplate.find(query, User.class);

        assertEquals(5, users.size());
    }

    @Test
    void testFindByUsername() {
        Query query1 = new Query();
        Criteria regex1 = Criteria.where("username").regex(".*ar.*");
        query1.addCriteria(regex1);
        List<User> usersContaining = mongoTemplate.find(query1, User.class);

        Query query2 = new Query();
        Criteria regex2 = Criteria.where("username").regex("^b.*");
        query2.addCriteria(regex2);
        List<User> usersStarting = mongoTemplate.find(query2, User.class);

        Query query3 = new Query();
        Criteria regex3 = Criteria.where("username").regex("ie$");
        query3.addCriteria(regex3);
        List<User> usersEnding = mongoTemplate.find(query3, User.class);

        assertAll(
                () -> assertEquals(2, usersContaining.size()),
                () -> assertEquals(2, usersStarting.size()),
                () -> assertEquals(2, usersEnding.size())
        );
    }

    @Test
    void testFindByActive() {
        List<User> usersActive = mongoTemplate.find(new Query().addCriteria(Criteria.where("active").is(Boolean.TRUE)), User.class);
        List<User> usersNotActive = mongoTemplate.find(new Query().addCriteria(Criteria.where("active").is(Boolean.FALSE)), User.class);

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

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("registrationDate").in(dates));
        List<User> usersList1 = mongoTemplate.find(query1, User.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("registrationDate").not().in(dates));
        List<User> usersList2 = mongoTemplate.find(query2, User.class);

        assertAll(
                () -> assertEquals(3, usersList1.size()),
                () -> assertEquals(7, usersList2.size())
        );
    }

    @Test
    void findByLastName() {
        assertEquals(2, mongoTemplate.find(new Query().addCriteria(Criteria.where("lastName").is("Smith")), User.class).size());
    }

}
