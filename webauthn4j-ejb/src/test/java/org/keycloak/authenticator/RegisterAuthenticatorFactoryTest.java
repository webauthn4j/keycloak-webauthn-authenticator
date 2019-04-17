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

package org.keycloak.authenticator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mockito;

public class RegisterAuthenticatorFactoryTest {

    @Test
    public void test_getDisplayType() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertEquals("Webauthn Register", factory.getDisplayType());
    }

    @Test
    public void test_getReferenceCategory() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertEquals(null, factory.getReferenceCategory());
    }

    @Test
    public void test_isConfigurable() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertFalse(factory.isConfigurable());
    }

    @Test
    public void test_getRequirementChoices() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        AuthenticationExecutionModel.Requirement[] requirements = {
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED,
        };
        assertArrayEquals(requirements, factory.getRequirementChoices());
    }

    @Test
    public void test_isUserSetupAllowed() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertFalse(factory.isUserSetupAllowed());
    }

    @Test
    public void test_getHelpText() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertEquals("Register for WebAuthn", factory.getHelpText());
    }

    @Test
    public void test_getConfigProperties() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertTrue(factory.getConfigProperties().isEmpty());
    }

    @Test
    public void test_init() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertDoesNotThrow(() -> factory.init(null));
    }

    @Test
    public void test_create() throws Exception {
        KeycloakSession session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);

        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertNotNull(factory.create(session));
    }

    @Test
    public void test_postInit() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertDoesNotThrow(() -> factory.postInit(null));
    }

    @Test
    public void test_close() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertDoesNotThrow(() -> factory.close());
    }

    @Test
    public void test_getId() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        assertEquals(RegisterAuthenticatorFactory.PROVIDER_ID, factory.getId());
    }
}
