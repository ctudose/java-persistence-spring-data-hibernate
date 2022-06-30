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
package com.manning.javapersistence.ch14.configuration;

import com.manning.javapersistence.ch14.DatabaseService;
import com.manning.javapersistence.ch14.dao.BidDao;
import com.manning.javapersistence.ch14.dao.BidDaoImpl;
import com.manning.javapersistence.ch14.dao.ItemDao;
import com.manning.javapersistence.ch14.dao.ItemDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@EnableTransactionManagement
public class SpringConfiguration {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/CH14_SPRING_HIBERNATE?serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public DatabaseService databaseService() {
        return new DatabaseService();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("ch14");
        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.manning.javapersistence.ch14");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public ItemDao itemDao() {
        return new ItemDaoImpl();
    }

    @Bean
    public BidDao bidDao() {
        return new BidDaoImpl();
    }
}
