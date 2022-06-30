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
package com.manning.javapersistence.ch14;

import com.manning.javapersistence.ch14.dao.ItemDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
public class DatabaseService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ItemDao itemDao;

    public void init() {
        for (int i = 0; i < 10; i++) {
            String itemName = "Item " + (i + 1);
            Item item = new Item();
            item.setName(itemName);
            Bid bid1 = new Bid(new BigDecimal("1000.00"), item);
            Bid bid2 = new Bid(new BigDecimal("1100.00"), item);

            itemDao.insert(item);
        }
    }

    public void clear() {
        sessionFactory.getCurrentSession().createQuery("delete from Bid b").executeUpdate();
        sessionFactory.getCurrentSession().createQuery("delete from Item i").executeUpdate();
    }

}
