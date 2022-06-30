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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;

@Repository
@Transactional
public abstract class AbstractGenericDao<T> implements GenericDao<T> {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager em;

    private Class<T> clazz;

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getById(long id) {
        return em.createQuery("SELECT e FROM " + clazz.getName() + " e WHERE e.id = :id", clazz)
                .setParameter("id", id).getSingleResult();
    }

    @Override
    public List<T> getAll() {
        return em.createQuery("from " + clazz.getName(), clazz).getResultList();
    }

    @Override
    public void insert(T entity) {
        em.persist(entity);
    }

    @Override
    public void delete(T entity) {
        em.remove(entity);
    }

    @Override
    public void update(long id, String propertyName, Object propertyValue) {
        em.createQuery("UPDATE " + clazz.getName() + " e SET e." + propertyName + " = :propertyValue WHERE e.id = :id")
                .setParameter("propertyValue", propertyValue)
                .setParameter("id", id).executeUpdate();
    }

    @Override
    public List<T> findByProperty(String propertyName, Object propertyValue) {
        return em.createQuery("SELECT e FROM " + clazz.getName() + " e WHERE e." + propertyName + " = :propertyValue", clazz)
                .setParameter("propertyValue", propertyValue).getResultList();
    }

}
