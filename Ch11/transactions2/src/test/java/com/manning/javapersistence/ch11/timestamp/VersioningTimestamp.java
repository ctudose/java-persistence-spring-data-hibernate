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
package com.manning.javapersistence.ch11.timestamp;

import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersioningTimestamp {

    static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch11");

    @Test
    void firstCommitWins() throws ExecutionException, InterruptedException {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Item someItem = new Item("Some Item");
        em.persist(someItem);
        em.getTransaction().commit();
        em.close();
        final Long ITEM_ID = someItem.getId();

        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();

        // Load an item and change its name
        Item item = em1.find(Item.class, ITEM_ID);
        item.setName("New Name");

        // The concurrent second unit of work doing the same
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                EntityManager em2 = emf.createEntityManager();
                em2.getTransaction().begin();

                Item item1 = em2.find(Item.class, ITEM_ID);
                // select * from ITEM where ID = ?

                item1.setName("Other Name");

                em2.getTransaction().commit();
                em2.close();

            } catch (Exception ex) {
                // This shouldn't happen, this commit should win!
                throw new RuntimeException("Concurrent operation failure: " + ex, ex);
            }
            return null;
        }).get();

        assertThrows(OptimisticLockException.class, () -> em1.flush());

    }

}
