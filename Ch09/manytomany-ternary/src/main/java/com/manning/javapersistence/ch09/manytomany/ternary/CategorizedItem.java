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

package com.manning.javapersistence.ch09.manytomany.ternary;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class CategorizedItem {

    @ManyToOne
    @JoinColumn(
            name = "ITEM_ID",
            nullable = false, updatable = false
    )
    private Item item;

    @ManyToOne
    @JoinColumn(
            name = "USER_ID",
            updatable = false
    )
    @NotNull // Doesn't generate SQL constraint, so not part of the PK!
    private User addedBy;

    @Column(updatable = false)
    @NotNull // Doesn't generate SQL constraint, so not part of the PK!
    private LocalDateTime addedOn = LocalDateTime.now();

    public CategorizedItem() {
    }

    public CategorizedItem(User addedBy,
                           Item item) {
        this.addedBy = addedBy;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public LocalDateTime getAddedOn() {
        return addedOn;
    }

    // Careful! Equality as shown here is not 'detached' safe!
    // Don't put detached instances into a HashSet! Or, if you
    // really have to compare detached instances, make sure they
    // were all loaded in the same persistence context.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // We are comparing instances by Java identity, not by primary key
        // equality. The scope where Java identity is the same as primary
        // key equality is the persistence context, not when instances are
        // in detached state!
        CategorizedItem that = (CategorizedItem) o;
        return item.equals(that.item) &&
                addedBy.equals(that.addedBy) &&
                addedOn.equals(that.addedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, addedBy, addedOn);
    }
}
