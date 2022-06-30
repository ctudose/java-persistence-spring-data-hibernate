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
package com.manning.javapersistence.springdatamongodb.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@NoArgsConstructor
@Document
@CompoundIndexes({
        @CompoundIndex(name = "username_email", def = "{'username' : 1, 'email': 1}"),
        @CompoundIndex(name = "lastName_firstName", def = "{'lastName' : 1, 'firstName': 1}")
})
public class User {

    @Id
    @Getter
    private String id;

    @Getter
    @Setter
    @Indexed(direction = IndexDirection.ASCENDING)
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

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    @Indexed(direction = IndexDirection.DESCENDING)
    private int level;

    @Getter
    @Setter
    private boolean active;

    @Getter
    @Setter
    @Transient
    private String password;

    @Getter
    @Setter
    private String ip;

    @DBRef
    @Field("address")
    @Getter
    @Setter
    private Address address;

    public User(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @PersistenceConstructor
    public User(String username, String firstName, String lastName, @Value("#root.ip ?: '192.168.1.100'") String ip) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ip = ip;
    }

}
