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
package com.manning.javapersistence.ch07.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Embeddable
@AttributeOverride(name = "name",
        column = @Column(name = "DIMENSIONS_NAME"))
@AttributeOverride(name = "symbol",
        column = @Column(name = "DIMENSIONS_SYMBOL"))
public class Dimensions extends Measurement {

    public static Dimensions centimeters(BigDecimal width, BigDecimal height, BigDecimal depth) {
        return new Dimensions("centimeters", "cm", width, height, depth);
    }

    public static Dimensions inches(BigDecimal width, BigDecimal height, BigDecimal depth) {
        return new Dimensions("inches", "\"", width, height, depth);
    }

    @NotNull
    private BigDecimal depth;

    @NotNull
    private BigDecimal height;

    @NotNull
    private BigDecimal width;

    public Dimensions() {
    }

    public Dimensions(String name, String symbol, BigDecimal width, BigDecimal height, BigDecimal depth) {
        super(name, symbol);
        this.height = height;
        this.width = width;
        this.depth = depth;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return String.format("W:%s%s x H:%s%s x D:%s%s", this.height, this.getSymbol(), this.width, this.getSymbol(), this.depth, this.getSymbol());
    }
}
