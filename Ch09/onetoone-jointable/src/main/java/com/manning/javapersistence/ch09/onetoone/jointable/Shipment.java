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
package com.manning.javapersistence.ch09.onetoone.jointable;

import com.manning.javapersistence.ch09.Constants;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Shipment {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @NotNull
    @CreationTimestamp
    private LocalDateTime createdOn;

    @NotNull
    private ShipmentState shipmentState = ShipmentState.TRANSIT;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ITEM_SHIPMENT", // Required!
            joinColumns =
            @JoinColumn(name = "SHIPMENT_ID"),  // Defaults to ID
            inverseJoinColumns =
            @JoinColumn(name = "ITEM_ID",  // Defaults to AUCTION_ID
                    nullable = false,
                    unique = true)
    )
    private Item auction;

    public Shipment() {
    }

    public Shipment(Item auction) {
        this.auction = auction;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public ShipmentState getShipmentState() {
        return shipmentState;
    }

    public void setShipmentState(ShipmentState shipmentState) {
        this.shipmentState = shipmentState;
    }

    public Item getAuction() {
        return auction;
    }

    public void setAuction(Item auction) {
        this.auction = auction;
    }
}
