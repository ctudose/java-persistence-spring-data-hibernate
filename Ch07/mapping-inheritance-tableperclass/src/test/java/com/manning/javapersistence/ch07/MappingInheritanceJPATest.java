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
package com.manning.javapersistence.ch07;

import com.manning.javapersistence.ch07.model.BankAccount;
import com.manning.javapersistence.ch07.model.BillingDetails;
import com.manning.javapersistence.ch07.model.CreditCard;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappingInheritanceJPATest {

    @Test
    public void storeLoadEntities() {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ch07.mapping_inheritance");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            CreditCard creditCard = new CreditCard("John Smith", "123456789", "10", "2030");
            BankAccount bankAccount = new BankAccount("Mike Johnson", "12345", "Delta Bank", "BANKXY12");
            em.persist(creditCard);
            em.persist(bankAccount);

            em.getTransaction().commit();
            em.refresh(creditCard);
            em.refresh(bankAccount);

            em.getTransaction().begin();

            List<BillingDetails> billingDetails =
                    em.createQuery("select bd from BillingDetails bd", BillingDetails.class)
                            .getResultList();

            em.getTransaction().commit();

            assertAll(
                    () -> assertEquals(2, billingDetails.size())
            );
        } finally {
            em.close();
            emf.close();
        }
    }
}
