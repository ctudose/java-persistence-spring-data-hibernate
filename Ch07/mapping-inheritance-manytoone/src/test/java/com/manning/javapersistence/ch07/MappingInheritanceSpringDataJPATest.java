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
import com.manning.javapersistence.ch07.model.CreditCard;
import com.manning.javapersistence.ch07.model.User;
import com.manning.javapersistence.ch07.repositories.CreditCardRepository;
import com.manning.javapersistence.ch07.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringDataConfiguration.class})
public class MappingInheritanceSpringDataJPATest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Test
    void storeLoadEntities() {

        CreditCard creditCard = new CreditCard(
                "John Smith", "123456789", "10", "2030"
        );
        User john = new User("John Smith");
        john.setDefaultBilling(creditCard);

        creditCardRepository.save(creditCard);
        userRepository.save(john);

        User user = userRepository.findById(john.getId()).get();

        System.out.println(user.getDefaultBilling());

        user.getDefaultBilling().pay(123);

        assertAll(
                () -> assertEquals("John Smith", user.getDefaultBilling().getOwner())
        );

    }

}
