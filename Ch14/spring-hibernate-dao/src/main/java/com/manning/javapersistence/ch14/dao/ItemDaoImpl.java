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
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ItemDaoImpl implements ItemDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Item getById(long id) {
        return sessionFactory.getCurrentSession().get(Item.class, id);
    }

    @Override
    public List<Item> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Item", Item.class).list();
    }

    @Override
    public void insert(Item item) {
        sessionFactory.getCurrentSession().persist(item);
        for (Bid bid : item.getBids()) {
            sessionFactory.getCurrentSession().persist(bid);
        }
    }

    @Override
    public void update(long id, String name) {
        Item item = sessionFactory.getCurrentSession().get(Item.class, id);
        item.setName(name);
        sessionFactory.getCurrentSession().update(item);
    }

    @Override
    public void delete(Item item) {
        sessionFactory.getCurrentSession().createQuery("delete from Bid b where b.item.id = :id").
                setParameter("id", item.getId()).executeUpdate();
        sessionFactory.getCurrentSession().createQuery("delete from Item i where i.id = :id").
                setParameter("id", item.getId()).executeUpdate();
    }

    @Override
    public Item findByName(String name) {
        return sessionFactory.getCurrentSession().createQuery("from Item where name=:name", Item.class)
                .setParameter("name", name).uniqueResult();
    }
}
