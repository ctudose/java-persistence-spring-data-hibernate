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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class BidDaoImpl implements BidDao {
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    public Bid getById(long id) {
        return em.find(Bid.class, id);
    }

    @Override
    public List<Bid> getAll() {
        return em.createQuery("from Bid", Bid.class).getResultList();
    }

    @Override
    public void insert(Bid bid) {
        em.persist(bid);
    }

    @Override
    public void update(long id, String amount) {
        Bid bid = em.find(Bid.class, id);
        bid.setAmount(new BigDecimal(amount));
        em.persist(bid);
    }

    @Override
    public void delete(Bid bid) {
        em.remove(bid);
    }

    @Override
    public List<Bid> findByAmount(String amount) {
        return em.createQuery("from Bid where amount=:amount", Bid.class)
                .setParameter("amount", new BigDecimal(amount)).getResultList();
    }
}
