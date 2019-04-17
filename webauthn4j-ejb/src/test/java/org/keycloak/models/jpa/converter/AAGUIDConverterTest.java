/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.models.jpa.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import org.keycloak.models.utils.KeycloakModelUtils;

import com.webauthn4j.data.attestation.authenticator.AAGUID;

public class AAGUIDConverterTest {

    @Test
    void test_convert() throws Exception {
        AAGUIDConverter converter = new AAGUIDConverter();
        byte[] aaguidBytes = converter.convertToDatabaseColumn(new AAGUID(KeycloakModelUtils.generateSecret(16)));
        AAGUID aaguidEntity = converter.convertToEntityAttribute(aaguidBytes);
        assertArrayEquals(aaguidBytes, converter.convertToDatabaseColumn(aaguidEntity));
    }
}
