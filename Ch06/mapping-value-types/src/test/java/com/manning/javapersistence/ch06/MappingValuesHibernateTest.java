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
package com.manning.javapersistence.ch06;

import com.manning.javapersistence.ch06.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MappingValuesHibernateTest {

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure().addAnnotatedClass(User.class).addAnnotatedClass(Item.class).addAnnotatedClass(Bid.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    public void storeLoadEntities() {

        try (SessionFactory sessionFactory = createSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = new User();
            user.setUsername("username");
            user.setHomeAddress(new Address("Flowers Street", "12345", "Boston"));

            Item item = new Item();
            item.setName("Some Item");
            item.setMetricWeight(2);
            item.setDescription("descriptiondescription");
            session.persist(user);
            session.persist(item);

            session.getTransaction().commit();

            session.refresh(user);
            session.refresh(item);

            session.beginTransaction();

            List<User> users =
                    session.createQuery("select u from User u", User.class)
                            .getResultList();

            List<Item> items =
                    session.createQuery("select i from Item i where i.metricWeight = :w", Item.class)
                            .setParameter("w", 2.0)
                            .getResultList();


            session.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, users.size()),
                    () -> assertEquals("username", users.get(0).getUsername()),
                    () -> assertEquals("Flowers Street", users.get(0).getHomeAddress().getStreet()),
                    () -> assertEquals("12345", users.get(0).getHomeAddress().getZipcode()),
                    () -> assertEquals("Boston", users.get(0).getHomeAddress().getCity()),
                    () -> assertEquals(1, items.size()),
                    () -> assertEquals("AUCTION: Some Item", items.get(0).getName()),
                    () -> assertEquals("descriptiondescription", items.get(0).getDescription()),
                    () -> assertEquals(AuctionType.HIGHEST_BID, items.get(0).getAuctionType()),
                    () -> assertEquals("descriptiond...", items.get(0).getShortDescription()),
                    () -> assertEquals(2.0, items.get(0).getMetricWeight()),
                    () -> assertEquals(LocalDate.now(), items.get(0).getCreatedOn()),
                    () -> assertTrue(ChronoUnit.SECONDS.between(LocalDateTime.now(), items.get(0).getLastModified()) < 1),
                    () -> assertEquals(new BigDecimal("1.00"), items.get(0).getInitialPrice())
            );

        }
    }
}
