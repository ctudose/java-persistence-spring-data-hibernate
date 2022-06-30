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
package com.manning.javapersistence.querydsl;

import com.manning.javapersistence.querydsl.model.Address;
import com.manning.javapersistence.querydsl.model.Bid;
import com.manning.javapersistence.querydsl.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class GenerateUsers {

    public static Address address = new Address("Flowers Street", "1234567", "Boston", "MA");

    public static List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        User john = new User("john", "John", "Smith");
        john.setRegistrationDate(LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@somedomain.com");
        john.setLevel(1);
        john.setActive(true);
        john.setAddress(address);

        Bid bid1 = new Bid(new BigDecimal(100));
        bid1.setUser(john);
        john.addBid(bid1);

        Bid bid2 = new Bid(new BigDecimal(110));
        bid2.setUser(john);
        john.addBid(bid2);

        User mike = new User("mike", "Mike", "Nolan");
        mike.setRegistrationDate(LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@somedomain.com");
        mike.setLevel(3);
        mike.setActive(true);
        mike.setAddress(address);

        Bid bid3 = new Bid(new BigDecimal(120));
        bid3.setUser(mike);
        mike.addBid(bid3);

        User james = new User("james", "James", "Woods");
        james.setRegistrationDate(LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        james.setAddress(address);

        User katie = new User("katie", "Katie", "Sposato");
        katie.setRegistrationDate(LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        katie.setAddress(address);

        Bid bid4 = new Bid(new BigDecimal(120));
        bid4.setUser(katie);
        katie.addBid(bid4);

        User beth = new User("beth", "Beth", "Gerrard");
        beth.setRegistrationDate(LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);

        Bid bid5 = new Bid(new BigDecimal(112));
        bid5.setUser(beth);
        beth.addBid(bid5);

        User julius = new User("julius", "Julius", "Graves");
        julius.setRegistrationDate(LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);

        User darren = new User("darren", "Darren", "Perkins");
        darren.setRegistrationDate(LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);

        Bid bid6 = new Bid(new BigDecimal(114));
        bid6.setUser(darren);
        darren.addBid(bid6);

        Bid bid7 = new Bid(new BigDecimal(116));
        bid7.setUser(darren);
        darren.addBid(bid7);

        User marion = new User("marion", "Marion", "Sherman");
        marion.setRegistrationDate(LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);

        User stephanie = new User("stephanie", "Stephanie", "Christensen");
        stephanie.setRegistrationDate(LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);

        Bid bid8 = new Bid(new BigDecimal(111));
        bid8.setUser(stephanie);
        stephanie.addBid(bid8);

        Bid bid9 = new Bid(new BigDecimal(118));
        bid9.setUser(stephanie);
        stephanie.addBid(bid9);

        User burk = new User("burk", "Burk", "Smith");
        burk.setRegistrationDate(LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);

        Bid bid10 = new Bid(new BigDecimal(105));
        bid10.setUser(burk);
        burk.addBid(bid10);

        users.add(john);
        users.add(mike);
        users.add(james);
        users.add(katie);
        users.add(beth);
        users.add(julius);
        users.add(darren);
        users.add(marion);
        users.add(stephanie);
        users.add(burk);

        return users;
    }

}
