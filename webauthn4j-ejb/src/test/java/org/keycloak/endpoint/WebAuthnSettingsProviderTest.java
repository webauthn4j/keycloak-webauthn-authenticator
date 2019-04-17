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

package org.keycloak.endpoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;

import com.webauthn4j.data.PublicKeyCredentialCreationOptions;

public class WebAuthnSettingsProviderTest {

    private KeycloakSession session;
    private WebAuthnSettingsProvider provider;

    @BeforeEach
    void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class);
        this.provider = new WebAuthnSettingsProvider(session);
    }

    @Test
    void test_getRegistrationSetting() throws Exception {
        PublicKeyCredentialCreationOptions options = provider.getRegistrationSetting();
        assertTrue(isValidOptions(options));
    }

    @Test
    void test_getResource() {
        assertEquals(provider, provider.getResource());
    }

    @Test
    void test_close() {
        assertDoesNotThrow(() -> provider.close());
    }

    private boolean isValidOptions(PublicKeyCredentialCreationOptions options) {
        if (options != null) return true;
        return false;
    }
}
