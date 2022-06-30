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
package com.manning.javapersistence.querydsl;

import com.manning.javapersistence.querydsl.configuration.SpringDataConfiguration;
import com.manning.javapersistence.querydsl.model.QBid;
import com.manning.javapersistence.querydsl.model.QUser;
import com.manning.javapersistence.querydsl.model.User;
import com.manning.javapersistence.querydsl.repositories.UserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static com.manning.javapersistence.querydsl.GenerateUsers.generateUsers;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
class QuerydslTest {

    @Autowired
    private UserRepository userRepository;

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ch19.querydsl");

    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @BeforeAll
    void beforeAll() {
        userRepository.saveAll(generateUsers());
    }

    @BeforeEach
    void beforeEach() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Test
    void testFindByUsername() {
        User fetchedUser = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq("john"))
                .fetchOne();

        assertAll(
                () -> assertNotNull(fetchedUser),
                () -> assertEquals("john", fetchedUser.getUsername()),
                () -> assertEquals("John", fetchedUser.getFirstName()),
                () -> assertEquals("Smith", fetchedUser.getLastName()),
                () -> assertEquals(2, fetchedUser.getBids().size())
        );
    }

    @Test
    void testByLevelAndActive() {
        List<User> users = (List<User>) queryFactory.from(QUser.user)
                .where(QUser.user.level.eq(3).and(QUser.user.active.eq(true))).fetch();
        assertEquals(1, users.size());
    }

    @Test
    void testOrderByUsername() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .orderBy(QUser.user.username.asc())
                .fetch();

        assertAll(
                () -> assertEquals(users.size(), 10),
                () -> assertEquals("beth", users.get(0).getUsername()),
                () -> assertEquals("burk", users.get(1).getUsername()),
                () -> assertEquals("mike", users.get(8).getUsername()),
                () -> assertEquals("stephanie", users.get(9).getUsername())
        );
    }

    @Test
    void testGroupByBidAmount() {
        NumberPath<Long> count = Expressions.numberPath(Long.class, "bids");

        List<Tuple> userBidsGroupByAmount = queryFactory.select(QBid.bid.amount, QBid.bid.id.count().as(count))
                .from(QBid.bid)
                .groupBy(QBid.bid.amount)
                .orderBy(count.desc())
                .fetch();

        assertAll(
                () -> assertEquals(new BigDecimal("120.00"), userBidsGroupByAmount.get(0).get(QBid.bid.amount)),
                () -> assertEquals(2, userBidsGroupByAmount.get(0).get(count))
        );
    }

    @Test
    void testAggregateBidAmount() {
        assertAll(
                () -> assertEquals(new BigDecimal("120.00"), queryFactory.from(QBid.bid).select(QBid.bid.amount.max()).fetchOne()),
                () -> assertEquals(new BigDecimal("100.00"), queryFactory.from(QBid.bid).select(QBid.bid.amount.min()).fetchOne()),
                () -> assertEquals(112.6, queryFactory.from(QBid.bid).select(QBid.bid.amount.avg()).fetchOne())
        );
    }

    @Test
    void testSubquery() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.id.in(
                        JPAExpressions.select(QBid.bid.user.id)
                                .from(QBid.bid)
                                .where(QBid.bid.amount.eq(new BigDecimal("120.00")))))
                .fetch();

        List<User> otherUsers = queryFactory.selectFrom(QUser.user)
                .where(QUser.user.id.in(
                        JPAExpressions.select(QBid.bid.user.id)
                                .from(QBid.bid)
                                .where(QBid.bid.amount.eq(new BigDecimal("105.00")))))
                .fetch();

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals(1, otherUsers.size()),
                () -> assertEquals("burk", otherUsers.get(0).getUsername())
        );
    }


    @Test
    void testJoin() {
        List<User> users = queryFactory.selectFrom(QUser.user)
                .innerJoin(QUser.user.bids, QBid.bid)
                .on(QBid.bid.amount.eq(new BigDecimal("120.00")))
                .fetch();

        List<User> otherUsers = queryFactory.selectFrom(QUser.user)
                .innerJoin(QUser.user.bids, QBid.bid)
                .on(QBid.bid.amount.eq(new BigDecimal("105.00")))
                .fetch();

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals(1, otherUsers.size()),
                () -> assertEquals("burk", otherUsers.get(0).getUsername())
        );
    }

    @Test
    void testUpdate() {
        queryFactory.update(QUser.user)
                .where(QUser.user.username.eq("john"))
                .set(QUser.user.email, "john@someotherdomain.com")
                .execute();

        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();

        assertEquals("john@someotherdomain.com",
                queryFactory.select(QUser.user.email)
                        .from(QUser.user)
                        .where(QUser.user.username.eq("john"))
                        .fetchOne());
    }

    @Test
    void testDelete() {
        // http://querydsl.com/static/querydsl/latest/reference/html/
        // Section 2.1.11 on DELETE queries using JPA:
        // "DML clauses in JPA don't take JPA level cascade rules into account
        // and don't provide fine-grained second level cache interaction."
        // Therefore, the cascade attribute of the bids @OneToMany annotation
        // in class User is ignored, so it becomes necessary to select user
        // Burk and manually delete his bids before deleting it through a
        // QueryDSL delete query.
        User burk = (User) queryFactory.from(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .fetchOne();
        if (burk != null) {
            queryFactory.delete(QBid.bid)
                    .where(QBid.bid.user.eq(burk))
                    .execute();
        }
        // End of the bug workaround
        queryFactory.delete(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .execute();

        // If user Burk were to be deleted through the standard
        // UserRepository delete() method, the @OneToMany cascade
        // attribute would be properly taken into account and no
        // manual handling of his bids would be needed.
        //userRepository.delete(burk);

        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();

        assertNull(queryFactory.selectFrom(QUser.user)
                .where(QUser.user.username.eq("burk"))
                .fetchOne());
    }

    @AfterEach
    void afterEach() {
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
    }

}
