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
package com.manning.javapersistence.ch16;

import com.manning.javapersistence.ch16.repositories.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import com.manning.javapersistence.ch16.beans.CsvDataLoader;
import com.manning.javapersistence.ch16.model.Address;
import com.manning.javapersistence.ch16.model.Auction;
import com.manning.javapersistence.ch16.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@Import(CsvDataLoader.class)
public class Application {

    @Autowired
    private Auction auction;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationRunner configureRepository(UserRepository userRepository) {
        AtomicInteger number = new AtomicInteger(1);
        return args -> {

            for (User user : auction.getUsers()) {
                user.setAddress(generateAddress(String.valueOf(number.getAndIncrement())));
                userRepository.save(user);
            }

        };
    }

    private static Address generateAddress(String number) {
        return new Address(number + " Flowers Streets", "12345", "Boston", "MA");
    }

}
