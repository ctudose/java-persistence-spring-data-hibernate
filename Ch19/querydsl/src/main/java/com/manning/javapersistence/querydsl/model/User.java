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
package com.manning.javapersistence.querydsl.model;

import com.manning.javapersistence.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private LocalDate registrationDate;

    @Embedded
    @Getter
    @Setter
    private Address address;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private int level;

    @Getter
    @Setter
    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Bid> bids = new HashSet<>();

    public User(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }
}
