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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.webauthn4j.data.attestation.authenticator.CredentialPublicKey;
import com.webauthn4j.data.attestation.authenticator.Curve;
import com.webauthn4j.data.attestation.authenticator.EC2CredentialPublicKey;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;

public class CredentialPublicKeyConverterTest {

    @Test
    void test_converter() {
        EC2CredentialPublicKey credentialPublicKey = EC2CredentialPublicKey.createFromUncompressedECCKey(createECCredentialPublicKey().getBytes());
        CredentialPublicKeyConverter converter = new CredentialPublicKeyConverter();
        String stringifiedKey = converter.convertToDatabaseColumn(credentialPublicKey);
        CredentialPublicKey key = converter.convertToEntityAttribute(stringifiedKey);
        assertEquals(stringifiedKey, converter.convertToDatabaseColumn(key));
    }

    public static EC2CredentialPublicKey createECCredentialPublicKey() {
        return new EC2CredentialPublicKey(
                null,
                COSEAlgorithmIdentifier.ES256,
                null,
                null,
                Curve.SECP256R1,
                new byte[32],
                new byte[32]
        );
    }
}
