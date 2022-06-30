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

import com.manning.javapersistence.ch07.configuration.SpringDataConfiguration;
import com.manning.javapersistence.ch07.model.BankAccount;
import com.manning.javapersistence.ch07.model.CreditCard;
import com.manning.javapersistence.ch07.repositories.BankAccountRepository;
import com.manning.javapersistence.ch07.repositories.CreditCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class MappingInheritanceSpringDataJPATest {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void storeLoadEntities() {

        CreditCard creditCard = new CreditCard("John Smith", "123456789", "10", "2030");
        creditCardRepository.save(creditCard);

        BankAccount bankAccount = new BankAccount("Mike Johnson", "12345", "Delta Bank", "BANKXY12");
        bankAccountRepository.save(bankAccount);

        List<CreditCard> creditCards = creditCardRepository.findByOwner("John Smith");
        List<BankAccount> bankAccounts = bankAccountRepository.findByOwner("Mike Johnson");
        List<CreditCard> creditCards2 = creditCardRepository.findByCardNumber("123456789");
        List<BankAccount> bankAccounts2 = bankAccountRepository.findBySwift("BANKXY12");

        assertAll(
                () -> assertEquals(1, creditCards.size()),
                () -> assertEquals("123456789", creditCards.get(0).getCardNumber()),
                () -> assertEquals(1, bankAccounts.size()),
                () -> assertEquals("12345", bankAccounts.get(0).getAccount()),
                () -> assertEquals(1, creditCards2.size()),
                () -> assertEquals("John Smith", creditCards2.get(0).getOwner()),
                () -> assertEquals(1, bankAccounts2.size()),
                () -> assertEquals("Mike Johnson", bankAccounts2.get(0).getOwner())
        );

    }

}
