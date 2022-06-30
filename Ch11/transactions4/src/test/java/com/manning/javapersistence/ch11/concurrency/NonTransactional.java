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
package com.manning.javapersistence.ch11.concurrency;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NonTransactional {

    private static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch11");

    @Test
    public void autoCommit() {
        Long ITEM_ID;
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Item someItem = new Item("Original Name");
        em.persist(someItem);
        em.getTransaction().commit();
        em.close();
        ITEM_ID = someItem.getId();

        // Reading data in auto-commit mode
        {
            /*
               No transaction is active when we create the <code>EntityManager</code>. The
               persistence context is now in a special <em>unsynchronized</em> mode, Hibernate
               will not flush automatically at any time.
             */
            em = emf.createEntityManager();

            /*
               You can access the database to read data; this operation will execute a
               <code>SELECT</code> statement, sent to the database in auto-commit mode.
             */
            Item item = em.find(Item.class, ITEM_ID);
            item.setName("New Name");

            /*
               Usually Hibernate would flush the persistence context when you execute a
               <code>Query</code>. However, because the context is <em>unsynchronized</em>,
               flushing will not occur and the query will return the old, original database
               value. Queries with scalar results are not repeatable, you'll see whatever
               values are present in the database and given to Hibernate in the
               <code>ResultSet</code>. Note that this isn't a repeatable read either if
               you are in <em>synchronized</em> mode.
             */
            assertEquals(
                    "Original Name",
                    em.createQuery("select i.name from Item i where i.id = :id", String.class)
                            .setParameter("id", ITEM_ID).getSingleResult()
            );

            /*
               Retrieving a managed entity instance involves a lookup, during JDBC
               result set marshaling, in the current persistence context. The
               already loaded <code>Item</code> instance with the changed name will
               be returned from the persistence context, values from the database
               will be ignored. This is a repeatable read of an entity instance,
               even without a system transaction.
             */
            assertEquals(
                    "New Name",
                    em.createQuery("select i from Item i where i.id = :id", Item.class)
                            .setParameter("id", ITEM_ID).getSingleResult().getName()
            );

            /*
               If you try to flush the persistence context manually, to store the new
               <code>Item#name</code>, Hibernate will throw a
               <code>javax.persistence.TransactionRequiredException</code>. You are
               prevented from executing an <code>UPDATE</code> statement in
               <em>unsynchronized</em> mode, as you wouldn't be able to roll back the change.
            */
            // em.flush();

            /*
               You can roll back the change you made with the <code>refresh()</code>
               method, it loads the current <code>Item</code> state from the database
               and overwrites the change you have made in memory.
             */
            em.refresh(item);
            assertEquals("Original Name", item.getName());

            em.close();
        }

        // Queueing modifications
        {
            em = emf.createEntityManager();

            Item newItem = new Item("New Item");
            /*
               You can call <code>persist()</code> to save a transient entity instance with an
               unsynchronized persistence context. Hibernate will only fetch a new identifier
               value, typically by calling a database sequence, and assign it to the instance.
               The instance is now in persistent state in the context but the SQL
               <code>INSERT</code> hasn't happened. Note that this is only possible with
               <em>pre-insert</em> identifier generators; see <a href="#GeneratorStrategies"/>.
            */
            em.persist(newItem);
            assertNotNull(newItem.getId());

            /*
               When you are ready to store the changes, join the persistence context with
               a transaction. Synchronization and flushing will occur as usual, when the
               transaction commits. Hibernate writes all queued operations to the database.
             */
            em.getTransaction().begin();
            if (!em.isJoinedToTransaction()) {
              em.joinTransaction();
            }
            em.getTransaction().commit(); // Flush!
            em.close();
        }

        {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            assertEquals("Original Name", em.find(Item.class, ITEM_ID).getName());
            assertEquals(2L, em.createQuery("select count(i) from Item i").getSingleResult());
            em.getTransaction().commit();
            em.close();
        }

        // Queueing merged changes of a detached entity
        {
            EntityManager tmp = emf.createEntityManager();
            Item detachedItem = tmp.find(Item.class, ITEM_ID);
            tmp.close();

            detachedItem.setName("New Name");
            em = emf.createEntityManager();

            Item mergedItem = em.merge(detachedItem);
            em.getTransaction().begin();
            em.joinTransaction();
            em.getTransaction().commit(); // Flush!
            em.close();
        }

        {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            assertEquals("New Name", em.find(Item.class, ITEM_ID).getName());
            em.getTransaction().commit();
            em.close();
        }

        // Queueing removal of entity instances and DELETE operations
        {
            em = emf.createEntityManager();

            Item item = em.find(Item.class, ITEM_ID);
            em.remove(item);

            em.getTransaction().begin();
            em.joinTransaction();
            em.getTransaction().commit(); // Flush!
            em.close();
        }

        {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            assertEquals(1L, em.createQuery("select count(i) from Item i").getSingleResult());
            em.getTransaction().commit();
            em.close();
        }
    }
}
