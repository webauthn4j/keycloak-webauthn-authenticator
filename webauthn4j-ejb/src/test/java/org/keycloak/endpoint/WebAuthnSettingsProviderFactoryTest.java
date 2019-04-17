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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class WebAuthnSettingsProviderFactoryTest {

    private KeycloakSession session;
    private WebAuthnSettingsProviderFactory factory;

    @BeforeEach
    void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class);
        this.factory = new WebAuthnSettingsProviderFactory();
    }

    @Test
    void test_create() throws Exception {
        assertNotNull(factory.create(session));
    }

    @Test
    void test_init() {
        assertDoesNotThrow(() -> factory.init(mock(Config.Scope.class)));
    }

    @Test
    void test_postInit() {
        assertDoesNotThrow(() -> factory.postInit(mock(KeycloakSessionFactory.class)));
    }

    @Test
    void test_close() {
        assertDoesNotThrow(() -> factory.close());
    }

    @Test
    void test_getId() {
        assertEquals("webauthn-settings", factory.getId());
    }
}
