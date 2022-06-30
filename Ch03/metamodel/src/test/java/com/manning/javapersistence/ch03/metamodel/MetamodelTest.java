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
package com.manning.javapersistence.ch03.metamodel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MetamodelTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("ch03.metamodel");
    }

    @Test
    public void accessDynamicMetamodel() {

        Metamodel metamodel = emf.getMetamodel();
        Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
        ManagedType<?> itemType = managedTypes.iterator().next();

        assertAll(() -> assertEquals(1, managedTypes.size()),
                () -> assertEquals(
                        Type.PersistenceType.ENTITY,
                        itemType.getPersistenceType()));


        SingularAttribute<?, ?> idAttribute =
                itemType.getSingularAttribute("id");
        assertFalse(
                idAttribute.isOptional()
        );

        SingularAttribute<?, ?> nameAttribute =
                itemType.getSingularAttribute("name");

        assertAll(() -> assertEquals(String.class, nameAttribute.getJavaType()),
                () -> assertEquals(
                        Attribute.PersistentAttributeType.BASIC,
                        nameAttribute.getPersistentAttributeType()
                ));


        SingularAttribute<?, ?> auctionEndAttribute =
                itemType.getSingularAttribute("auctionEnd");
        assertAll(() -> assertEquals(Date.class, auctionEndAttribute.getJavaType()),
                () -> assertFalse(auctionEndAttribute.isCollection()),
                () -> assertFalse(auctionEndAttribute.isAssociation())
        );

    }

    @Test
    public void accessStaticMetamodel() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        query.select(fromItem);
        List<Item> items = em.createQuery(query).getResultList();

        assertEquals(2, items.size());
    }

    @Test
    public void testItemsPattern() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        Path<String> namePath = fromItem.get("name");
        query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));
        List<Item> items = em.createQuery(query).setParameter("pattern", "%Item 1%").getResultList();
        assertAll(() -> assertEquals(1, items.size()),
                () -> assertEquals("Item 1", items.iterator().next().getName()));
    }

    @Test
    public void testItemsPatternWithGeneratedClass() {
        EntityManager em = emf.createEntityManager();
        deleteItems(em);
        persistItems(em);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> query = cb.createQuery(Item.class);
        Root<Item> fromItem = query.from(Item.class);
        Path<String> namePath = fromItem.get(Item_.name);
        query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));
        List<Item> items = em.createQuery(query).setParameter("pattern", "%Item 1%").getResultList();
        assertAll(() -> assertEquals(1, items.size()),
                () -> assertEquals("Item 1", items.iterator().next().getName()));
    }

    private void deleteItems(EntityManager em) {
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Item> query = cb.createCriteriaDelete(Item.class);
        query.from(Item.class);
        em.createQuery(query).executeUpdate();
        em.getTransaction().commit();
    }

    private void persistItems(EntityManager em) {
        em.getTransaction().begin();
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setAuctionEnd(tomorrow());

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setAuctionEnd(tomorrow());

        em.persist(item1);
        em.persist(item2);
        em.getTransaction().commit();
    }

    private Date tomorrow() {
        return new Date(new Date().getTime() + (1000 * 60 * 60 * 24));
    }

    @AfterAll
    static void afterAll() {
        emf.close();
    }
}
