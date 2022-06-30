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
package com.manning.javapersistence.ch13.filtering.callback;

import javax.persistence.PostPersist;

/**
 * An entity listener class must have either no constructor or a public no-argument
 * constructor. It doesn't have to implement any special interfaces. An entity listener
 * is stateless; the JPA engine automatically creates and destroys it.
 */
public class PersistEntityListener {

    /* 
       You may annotate any method of an entity listener class as a callback method
       for persistence life cycle events. The <code>notifyAdmin()</code> method is
       invoked after a new entity instance is stored in the database.
     */
    @PostPersist
    public void logMessage(Object entityInstance) {

        /* 
           Because event listener classes are stateless, it can be difficult to get
           more contextual information when you need it. Here, you want the currently
           logged in user, and access to log information. A primitive
           solution is to use thread-local variables and singletons;  you can find the source for
           <code>CurrentUser</code> and <code>Log</code> in the example code.
         */
        User currentUser = CurrentUser.INSTANCE.get();
        Log log = Log.INSTANCE;

        log.save(
                "Entity instance persisted by "
                        + currentUser.getUsername()
                        + ": "
                        + entityInstance
        );
    }

}
