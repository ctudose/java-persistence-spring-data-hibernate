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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;

@Repository
@Transactional
public class ItemDaoImpl implements ItemDao {
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    public Item getById(long id) {
        return em.find(Item.class, id);
    }

    @Override
    public List<Item> getAll() {
        return em.createQuery("from Item", Item.class).getResultList();
    }

    @Override
    public void insert(Item item) {
        em.persist(item);
        for (Bid bid : item.getBids()) {
            em.persist(bid);
        }
    }

    @Override
    public void update(long id, String name) {
        Item item = em.find(Item.class, id);
        item.setName(name);
        em.persist(item);
    }

    @Override
    public void delete(Item item) {
        for (Bid bid : item.getBids()) {
            em.remove(bid);
        }
        em.remove(item);
    }

    @Override
    public Item findByName(String name) {
        return em.createQuery("from Item where name=:name", Item.class)
                .setParameter("name", name).getSingleResult();
    }
}
