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

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Item {

    /* 
       The <code>Item</code> entity defaults to field access, the <code>@Id</code> is on a field.
    */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;

    /*
       The <code>@Access(AccessType.PROPERTY)</code> setting on the <code>name</code> field switches this
       particular property to runtime access through getter/setter methods by the JPA provider.
    */
    @Access(AccessType.PROPERTY)
    @Column(name = "ITEM_NAME") // Mappings are still expected here!
    private String name;

    @NotNull
    @org.hibernate.annotations.Type(
            type = "monetary_amount_usd"
    )
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "BUYNOWPRICE_AMOUNT"),
            @Column(name = "BUYNOWPRICE_CURRENCY", length = 3)
    })
    private MonetaryAmount buyNowPrice;

    @OneToMany(mappedBy = "item",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true) // Includes CascadeType.REMOVE
    private Set<Bid> bids = new HashSet<>();

    @NotNull
    @Basic(fetch = FetchType.LAZY) // Defaults to EAGER
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING) // Defaults to ORDINAL
    private AuctionType auctionType = AuctionType.HIGHEST_BID;

    @Formula(
            "CONCAT(SUBSTR(DESCRIPTION, 1, 12), '...')"
    )
    private String shortDescription;

    @Formula(
            "(SELECT AVG(B.AMOUNT) FROM BID B WHERE B.ITEM_ID = ID)"
    )
    private BigDecimal averageBidAmount;

    @Column(name = "IMPERIALWEIGHT")
    @ColumnTransformer(
            read = "IMPERIALWEIGHT / 2.20462",
            write = "? * 2.20462"
    )
    private double metricWeight;

    @CreationTimestamp
    private LocalDate createdOn;

    @UpdateTimestamp
    private LocalDateTime lastModified;

    @NotNull
    @org.hibernate.annotations.Type(
            type = "monetary_amount_eur"
    )
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "INITIALPRICE_AMOUNT"),
            @Column(name = "INITIALPRICE_CURRENCY", length = 3)
    })
    private MonetaryAmount initialPrice;

    /* 
        Hibernate will call <code>getName()</code> and <code>setName()</code> when loading and storing items.
    */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name =
                !name.startsWith("AUCTION: ") ? "AUCTION: " + name : name;
    }

    public MonetaryAmount getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(MonetaryAmount buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public BigDecimal getAverageBidAmount() {
        return averageBidAmount;
    }

    public double getMetricWeight() {
        return metricWeight;
    }

    public void setMetricWeight(double metricWeight) {
        this.metricWeight = metricWeight;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public MonetaryAmount getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(MonetaryAmount initialPrice) {
        this.initialPrice = initialPrice;
    }
}
