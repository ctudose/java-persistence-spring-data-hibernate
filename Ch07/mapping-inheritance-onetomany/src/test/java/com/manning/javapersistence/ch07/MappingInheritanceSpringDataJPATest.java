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
import com.manning.javapersistence.ch07.model.BillingDetails;
import com.manning.javapersistence.ch07.model.CreditCard;
import com.manning.javapersistence.ch07.model.User;
import com.manning.javapersistence.ch07.repositories.BillingDetailsRepository;
import com.manning.javapersistence.ch07.repositories.CreditCardRepository;
import com.manning.javapersistence.ch07.repositories.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private BillingDetailsRepository<BillingDetails, Long> billingDetailsRepository;

    @Test
    void storeLoadEntities() {

        CreditCard creditCard = new CreditCard(
                "John Smith", "1234123412341234", "06", "2015"
        );
        User john = new User("John Smith");
        creditCard.setUser(john);
        john.addBillingDetail(creditCard);

        userRepository.save(john);
        creditCardRepository.save(creditCard);

        List<User> users = userRepository.findAll();

        List<BillingDetails> billingDetails = billingDetailsRepository.findByOwner(users.get(0).getUsername());

        assertAll(
                () -> assertEquals(1, users.size()),
                () -> assertEquals(1, billingDetails.size())
        );

    }

}
