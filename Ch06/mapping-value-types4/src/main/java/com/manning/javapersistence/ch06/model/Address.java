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
package com.manning.javapersistence.ch06.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Instead of <code>@Entity</code>, this component POJO is marked with <code>@Embeddable</code>. It
 * has no identifier property.
 */
@Embeddable
public class Address {

    @NotNull // Ignored for DDL generation!
    @Column(nullable = false) // Used for DDL generation!
    private String street;

    @NotNull
    @AttributeOverride(
            name = "name",
            column = @Column(name = "CITY", nullable = false)
    )
    private City city;

    /**
     * Hibernate will call this no-argument constructor to create an instance, and then
     * populate the fields directly.
     */
    public Address() {
    }

    /**
     * You can have additional (public) constructors for convenience.
     */
    public Address(String street, City city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
