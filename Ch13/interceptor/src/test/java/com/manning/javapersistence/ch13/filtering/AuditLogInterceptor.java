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
package com.manning.javapersistence.ch13.filtering;

import com.manning.javapersistence.ch13.filtering.interceptor.AuditLogRecord;
import com.manning.javapersistence.ch13.filtering.interceptor.Auditable;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AuditLogInterceptor extends EmptyInterceptor {

    /* 
       You need to access the database to write the audit log, so this interceptor
       needs a Hibernate <code>Session</code>. You also want to store the identifier
       of the currently logged-in user in each audit log record. The <code>inserts</code>
       and <code>updates</code> instance variables are collections where this interceptor
       will hold its internal state.
     */
    private Session currentSession;
    private Long currentUserId;
    private Set<Auditable> inserts = new HashSet<>();
    private Set<Auditable> updates = new HashSet<>();

    public void setCurrentSession(Session session) {
        this.currentSession = session;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    /* 
       This method is called when an entity instance is made persistent.
     */
    @Override
    public boolean onSave(Object entity, Serializable id,
                          Object[] state, String[] propertyNames, Type[] types)
            throws CallbackException {

        if (entity instanceof Auditable aud) {
            inserts.add(aud);
        }

        return false; // We didn't modify the state
    }

    /* 
       This method is called when an entity instance is detected as dirty
       during flushing of the persistence context.
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types)
            throws CallbackException {

        if (entity instanceof Auditable aud) {
            updates.add(aud);
        }

        return false; // We didn't modify the currentState
    }

    /* 
       This method is called after flushing of the persistence context is complete.
       Here, you write the audit log records for all insertions and updates you
       collected earlier.
     */
    @Override
    public void postFlush(@SuppressWarnings("rawtypes") Iterator iterator) throws CallbackException {

        /* 
           You are not allowed to access the original persistence context, the
           <code>Session</code> that is currently executing this interceptor.
           The <code>Session</code> is in a fragile state during interceptor calls.
           Hibernate allows you to create a new <code>Session</code> that
           inherits some information from the original <code>Session</code> with
           the <code>sessionWithOptions()</code> method. Here the new temporary
           <code>Session</code> works with the same transaction and database
           connection as the original <code>Session</code>.
         */
        Session tempSession =
                currentSession.sessionWithOptions()
                        .connection()
                        .openSession();

        try {
            /* 
               You store a new <code>AuditLogRecord</code> for each insertion and
               update using the temporary <code>Session</code>.
             */
            for (Auditable entity : inserts) {
                tempSession.persist(
                        new AuditLogRecord("insert", entity, currentUserId)
                );
            }
            for (Auditable entity : updates) {
                tempSession.persist(
                        new AuditLogRecord("update", entity, currentUserId)
                );
            }

            /* 
               You flush and close the temporary <code>Session</code>
               independently from the original <code>Session</code>.
             */
            tempSession.flush();
        } finally {
            tempSession.close();
            inserts.clear();
            updates.clear();
        }
    }
}
