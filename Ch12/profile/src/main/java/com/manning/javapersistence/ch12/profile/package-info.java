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
@org.hibernate.annotations.FetchProfiles({
    /* 
        Each profile has a name, this is a simple string we have isolated in a constant.
     */
        @FetchProfile(name = Item.PROFILE_JOIN_SELLER,
                /*
                     Each override in a profile names one entity association or collection.
                 */
                fetchOverrides = @FetchProfile.FetchOverride(
                        entity = Item.class,
                        association = "seller",
                        mode = FetchMode.JOIN
                )),

        @FetchProfile(name = Item.PROFILE_JOIN_BIDS,
                fetchOverrides = @FetchProfile.FetchOverride(
                        entity = Item.class,
                        association = "bids",
                        mode = FetchMode.JOIN
                ))
})

package com.manning.javapersistence.ch12.profile;

import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;