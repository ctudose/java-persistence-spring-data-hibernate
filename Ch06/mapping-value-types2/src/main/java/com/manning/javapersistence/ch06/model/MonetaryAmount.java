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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/* 
   This value-typed class should be <code>java.io.Serializable</code>: When Hibernate stores entity
   instance data in the shared second-level cache (see <a href="#Caching"/>), it <em>disassembles</em>
   the entity's state. If an entity has a <code>MonetaryAmount</code> property, the serialized
   representation of the property value will be stored in the second-level cache region. When entity
   data is retrieved from the cache region, the property value will be deserialized and reassembled.
 */
public class MonetaryAmount implements Serializable {

    /*
        The class does not need a special constructor, you can make it immutable, even with
        <code>final</code> fields, as your code will be the only place an instance is created.
     */
    private final BigDecimal value;
    private final Currency currency;

    public MonetaryAmount(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    /*
        You should implement the <code>equals()</code> and <code>hashCode()</code>
        methods, and compare monetary amounts "by value".
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonetaryAmount that = (MonetaryAmount) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    /*
            You will need a <code>String</code> representation of a monetary
            amount. Implement the <code>toString()</code> method and a static method to
            create an instance from a <code>String</code>.
         */
    @Override
    public String toString() {
        return value + " " + currency;
    }

    public static MonetaryAmount fromString(String s) {
        String[] split = s.split(" ");
        return new MonetaryAmount(
                new BigDecimal(split[0]),
                Currency.getInstance(split[1])
        );
    }
}

