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
package com.manning.javapersistence.testing;

import com.manning.javapersistence.testing.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UsersHelper {

    static int getIterations() {
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return Integer.parseInt(prop.getProperty("iterations"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<User> buildUsersList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < getIterations(); i++) {
            User user = new User("User" + i);
            users.add(user);
        }
        return users;
    }
}
