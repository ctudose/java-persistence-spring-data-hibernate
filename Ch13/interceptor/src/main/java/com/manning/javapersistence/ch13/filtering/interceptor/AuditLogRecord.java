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
package com.manning.javapersistence.ch13.filtering.interceptor;

import com.manning.javapersistence.ch13.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class AuditLogRecord {

    @Id
    @GeneratedValue(generator = Constants.ID_GENERATOR)
    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Long entityId;

    @NotNull
    private Class<? extends Auditable> entityClass;

    @NotNull
    private Long userId;

    @NotNull
    private LocalDateTime createdOn = LocalDateTime.now();

    public AuditLogRecord() {
    }

    public AuditLogRecord(String message,
                          Auditable entityInstance,
                          Long userId) {
        this.message = message;
        this.entityId = entityInstance.getId();
        this.entityClass = entityInstance.getClass();
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Class<? extends Auditable> getEntityClass() {
        return entityClass;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
}
