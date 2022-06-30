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
package com.manning.javapersistence.ch13.filtering;

import com.manning.javapersistence.ch13.filtering.interceptor.AuditLogRecord;
import com.manning.javapersistence.ch13.filtering.interceptor.Item;
import com.manning.javapersistence.ch13.filtering.interceptor.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuditLogging {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch13");

    @Test
    public void writeAuditLog() {
        Long CURRENT_USER_ID;
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            User currentUser = new User("johndoe");
            em.persist(currentUser);
            em.getTransaction().commit();
            em.close();
            CURRENT_USER_ID = currentUser.getId();
        }

        EntityManager em = emf.createEntityManager();

        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);

        Session session = sessionFactory.withOptions().interceptor(new AuditLogInterceptor()).openSession();

        AuditLogInterceptor interceptor =
                (AuditLogInterceptor) ((SessionImplementor) session).getInterceptor();
        interceptor.setCurrentSession(session);
        interceptor.setCurrentUserId(CURRENT_USER_ID);

        session.beginTransaction();
        Item item0 = new Item("Foo");
        session.persist(item0);
        session.getTransaction().commit();

        em.getTransaction().begin();
        List<AuditLogRecord> logs = em.createQuery(
                "select lr from AuditLogRecord lr",
                AuditLogRecord.class
        ).getResultList();
        assertEquals(1, logs.size());
        assertEquals("insert", logs.get(0).getMessage());
        assertEquals(Item.class, logs.get(0).getEntityClass());
        assertEquals(item0.getId(), logs.get(0).getEntityId());
        assertEquals(CURRENT_USER_ID, logs.get(0).getUserId());
        em.createQuery("delete AuditLogRecord").executeUpdate();
        em.getTransaction().commit();

        session.beginTransaction();
        Item item = session.find(Item.class, item0.getId());
        item.setName("Bar");
        session.persist(item);
        session.getTransaction().commit();
        session.close();

        em.getTransaction().begin();
        logs = em.createQuery(
                "select lr from AuditLogRecord lr",
                AuditLogRecord.class
        ).getResultList();
        assertEquals(1, logs.size());
        assertEquals("update", logs.get(0).getMessage());
        assertEquals(Item.class, logs.get(0).getEntityClass());
        assertEquals(item.getId(), logs.get(0).getEntityId());
        assertEquals(CURRENT_USER_ID, logs.get(0).getUserId());
        em.getTransaction().commit();
        em.close();

    }

}
