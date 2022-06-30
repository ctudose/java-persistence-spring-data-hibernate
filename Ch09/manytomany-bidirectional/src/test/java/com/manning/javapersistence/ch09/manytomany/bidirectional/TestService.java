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
package com.manning.javapersistence.ch09.manytomany.bidirectional;

import com.manning.javapersistence.ch09.repositories.manytomany.bidirectional.CategoryRepository;
import com.manning.javapersistence.ch09.repositories.manytomany.bidirectional.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class TestService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public void storeLoadEntities() {
        Category someCategory = new Category("Some Category");
        Category otherCategory = new Category("Other Category");

        Item someItem = new Item("Some Item");
        Item otherItem = new Item("Other Item");

        someCategory.addItem(someItem);
        someItem.addCategory(someCategory);

        someCategory.addItem(otherItem);
        otherItem.addCategory(someCategory);

        otherCategory.addItem(someItem);
        someItem.addCategory(otherCategory);

        categoryRepository.save(someCategory);
        categoryRepository.save(otherCategory);

        Category category1 = categoryRepository.findById(someCategory.getId()).get();
        Category category2 = categoryRepository.findById(otherCategory.getId()).get();

        Item item1 = itemRepository.findById(someItem.getId()).get();
        Item item2 = itemRepository.findById(otherItem.getId()).get();

        assertAll(
                () -> assertEquals(2, category1.getItems().size()),
                () -> assertEquals(2, item1.getCategories().size()),
                () -> assertEquals(1, category2.getItems().size()),
                () -> assertEquals(1, item2.getCategories().size()),
                () -> assertEquals(item1, category2.getItems().iterator().next()),
                () -> assertEquals(category1, item2.getCategories().iterator().next())
        );
    }

}
