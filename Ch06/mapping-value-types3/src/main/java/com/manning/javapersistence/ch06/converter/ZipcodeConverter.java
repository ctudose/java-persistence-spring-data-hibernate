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
package com.manning.javapersistence.ch06.converter;

import com.manning.javapersistence.ch06.model.GermanZipcode;
import com.manning.javapersistence.ch06.model.SwissZipcode;
import com.manning.javapersistence.ch06.model.Zipcode;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ZipcodeConverter
        implements AttributeConverter<Zipcode, String> {

    @Override
    public String convertToDatabaseColumn(Zipcode attribute) {
        return attribute.getValue();
    }

    @Override
    public Zipcode convertToEntityAttribute(String s) {
        if (s.length() == 5)
            return new GermanZipcode(s);
        else if (s.length() == 4)
            return new SwissZipcode(s);
        throw new IllegalArgumentException(
                "Unsupported zipcode in database: " + s
        );
    }
}
