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
package com.manning.javapersistence.testing.listeners;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DatabaseOperationsListener implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
        System.out.println("beforeTestClass, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        System.out.println("afterTestClass, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        System.out.println("beforeTestMethod, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        System.out.println("afterTestMethod, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void beforeTestExecution(TestContext testContext) {
        System.out.println("beforeTestExecution, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void afterTestExecution(TestContext testContext) {
        System.out.println("afterTestExecution, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
        System.out.println("prepareTestInstance, transaction active = " + TransactionSynchronizationManager.isActualTransactionActive());
    }

}
