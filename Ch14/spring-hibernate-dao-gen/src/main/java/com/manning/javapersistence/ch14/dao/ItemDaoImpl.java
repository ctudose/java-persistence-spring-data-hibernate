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
package com.manning.javapersistence.ch14.dao;

import com.manning.javapersistence.ch14.Bid;
import com.manning.javapersistence.ch14.Item;

public class ItemDaoImpl extends AbstractGenericDao<Item> {

    public ItemDaoImpl() {
        setClazz(Item.class);
    }

    @Override
    public void insert(Item item) {
        sessionFactory.getCurrentSession().persist(item);
        for (Bid bid : item.getBids()) {
            sessionFactory.getCurrentSession().persist(bid);
        }
    }

    @Override
    public void delete(Item item) {
        sessionFactory.getCurrentSession().createQuery("delete from Bid b where b.item.id = :id").
                setParameter("id", item.getId()).executeUpdate();
        sessionFactory.getCurrentSession().createQuery("delete from Item i where i.id = :id").
                setParameter("id", item.getId()).executeUpdate();
    }

}
