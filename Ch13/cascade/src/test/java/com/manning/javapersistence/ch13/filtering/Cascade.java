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

import com.manning.javapersistence.ch13.filtering.cascade.*;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class Cascade {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ch13");

    @Test
    public void detachMerge() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Long ITEM_ID;
        {
            User user = new User("johndoe");
            em.persist(user);

            Item item = new Item("Some Item", user);
            em.persist(item);
            ITEM_ID = item.getId();

            Bid firstBid = new Bid(new BigDecimal("99.00"), item);
            item.addBid(firstBid);
            em.persist(firstBid);

            Bid secondBid = new Bid(new BigDecimal("100.00"), item);
            item.addBid(secondBid);
            em.persist(secondBid);

            em.flush();
        }
        em.clear();

        Item item = em.find(Item.class, ITEM_ID);
        assertEquals(2, item.getBids().size()); // Initializes bids
        em.detach(item);

        em.clear();

        item.setName("New Name");

        Bid bid = new Bid(new BigDecimal("101.00"), item);
        item.addBid(bid);

            /* 
               Hibernate merges the detached <code>item</code>: First, it checks if the
               persistence context already contains an <code>Item</code> with the given
               identifier value. In this case, there isn't any, so the <code>Item</code>
               is loaded from the database. Hibernate is smart enough to know that
               it will also need the <code>bids</code> during merging, so it fetches them
               right away in the same SQL query. Hibernate then copies the detached <code>item</code>
               values onto the loaded instance, which it returns to you in persistent state.
               The same procedure is applied to every <code>Bid</code>, and Hibernate
               will detect that one of the <code>bids</code> is new.
             */
        Item mergedItem = em.merge(item);
        // select i.*, b.*
        //  from ITEM i
        //    left outer join BID b on i.ID = b.ITEM_ID
        //  where i.ID = ?

            /* 
               Hibernate made the new <code>Bid</code> persistent during merging, it
               now has an identifier value assigned.
             */
        for (Bid b : mergedItem.getBids()) {
            assertNotNull(b.getId());
        }

            /* 
               When you flush the persistence context, Hibernate detects that the
               <code>name</code> of the <code>Item</code> changed during merging.
               The new <code>Bid</code> will also be stored.
             */
        em.flush();
        // update ITEM set NAME = ? where ID = ?
        // insert into BID values (?, ?, ?, ...)

        em.clear();

        item = em.find(Item.class, ITEM_ID);
        assertEquals("New Name", item.getName());
        assertEquals(3, item.getBids().size());

        em.getTransaction().commit();
        em.close();

    }

    @Test
    public void refresh() throws Throwable {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long USER_ID;
        Long CREDIT_CARD_ID = null;
        {

            User user = new User("johndoe");
            user.addBillingDetails(
                    new CreditCard("John Doe", "1234567890", "11", "2020")
            );
            user.addBillingDetails(
                    new BankAccount("John Doe", "45678", "Some Bank", "1234")
            );
            em.persist(user);
            em.flush();

            USER_ID = user.getId();
            for (BillingDetails bd : user.getBillingDetails()) {
                if (bd instanceof CreditCard)
                    CREDIT_CARD_ID = bd.getId();
            }
            assertNotNull(CREDIT_CARD_ID);
        }
        em.getTransaction().commit();
        em.close();
        // Locks from INSERTs must be released, commit and start a new unit of work

        em = emf.createEntityManager();
        em.getTransaction().begin();

            /*
               An instance of <code>User</code> is loaded from the database.
             */
        User user = em.find(User.class, USER_ID);

            /*
               Its lazy <code>billingDetails</code> collection is initialized when
               you iterate through the elements or when you call <code>size()</code>.
             */
        assertEquals(2, user.getBillingDetails().size());
        for (BillingDetails bd : user.getBillingDetails()) {
            assertEquals("John Doe", bd.getOwner());
        }

        // Someone modifies the billing information in the database!
        Long SOME_USER_ID = USER_ID;
        // In a separate transaction, so no locks are held in the database on the
        // updated/deleted rows and we can SELECT them again in the original transaction
        Executors.newSingleThreadExecutor().submit(() -> {

            EntityManager em1 = emf.createEntityManager();
            em1.getTransaction().begin();

            em1.unwrap(Session.class).doWork(con -> {
                PreparedStatement ps;

                        /* Delete the credit card, this will cause the refresh to
                           fail with EntityNotFoundException!
                        ps = con.prepareStatement(
                            "delete from CREDITCARD where ID = ?"
                        );
                        ps.setLong(1, SOME_CREDIT_CARD_ID);
                        ps.executeUpdate();
                        ps = con.prepareStatement(
                            "delete from BILLINGDETAILS where ID = ?"
                        );
                        ps.setLong(1, SOME_CREDIT_CARD_ID);
                        ps.executeUpdate();
                        */

                // Update the bank account
                ps = con.prepareStatement(
                        "update BILLINGDETAILS set OWNER = ? where USER_ID = ?"
                );
                ps.setString(1, "Doe John");
                ps.setLong(2, SOME_USER_ID);
                ps.executeUpdate();
            });

            em1.getTransaction().commit();
            em1.close();

            return null;
        }).get();


            /*
               When you <code>refresh()</code> the managed <code>User</code> instance,
               Hibernate cascades the operation to the managed <code>BillingDetails</code>
               and refreshes each with a SQL <code>SELECT</code>. If one of these instances
               is no longer in the database, Hibernate throws an <code>EntityNotFoundException</code>.
               Then, Hibernate refreshes the <code>User</code> instance and eagerly
               loads the whole <code>billingDetails</code> collection to discover any
               new <code>BillingDetails</code>.
             */
        em.refresh(user);
        // select * from CREDITCARD join BILLINGDETAILS where ID = ?
        // select * from BANKACCOUNT join BILLINGDETAILS where ID = ?
        // select * from USERS
        //  left outer join BILLINGDETAILS
        //  left outer join CREDITCARD
        //  left outer JOIN BANKACCOUNT
        // where ID = ?

        for (BillingDetails bd : user.getBillingDetails()) {
            assertEquals("Doe John", bd.getOwner());
        }

        em.getTransaction().commit();
        em.close();

    }

    @Test
    public void replicate() {
        Long ITEM_ID;

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User("johndoe");
        em.persist(user);

        Item item = new Item("Some Item", user);
        em.persist(item);
        ITEM_ID = item.getId();

        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        em.getTransaction().begin();

        item = em.find(Item.class, ITEM_ID);

        // Initialize the lazy Item#seller
        assertNotNull(item.getSeller().getUsername());

        em.getTransaction().commit();
        em.close();

        EntityManager otherDatabase = emf.createEntityManager();
        otherDatabase.getTransaction().begin();

        otherDatabase.unwrap(Session.class)
                .replicate(item, ReplicationMode.OVERWRITE);
        // select ID from ITEM where ID = ?
        // select ID from USERS where ID = ?

        otherDatabase.getTransaction().commit();
        // update ITEM set NAME = ?, SELLER_ID = ?, ... where ID = ?
        // update USERS set USERNAME = ?, ... where ID = ?
        otherDatabase.close();

    }

}
