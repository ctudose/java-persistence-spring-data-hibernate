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
package com.manning.javapersistence.ch16.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Auction {

    private String auctionNumber;
    private int seats;
    private Set<User> users = new HashSet<>();

    public Auction(String auctionNumber, int seats) {
        this.auctionNumber = auctionNumber;
        this.seats = seats;
    }

    public String getAuctionNumber() {
        return auctionNumber;
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public boolean addUser(User user) {
        if (users.size() >= seats) {
            throw new RuntimeException("Cannot add more users than the capacity of the auction!");
        }
        return users.add(user);
    }

    public boolean removeUser(User user) {
        return users.remove(user);
    }

    @Override
    public String toString() {
        return "Auction " + getAuctionNumber();
    }

}
