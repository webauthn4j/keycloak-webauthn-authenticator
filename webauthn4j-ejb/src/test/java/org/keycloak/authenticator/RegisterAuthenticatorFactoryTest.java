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

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;

import org.junit.Test;
import org.junit.Assert;

import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

public class RegisterAuthenticatorFactoryTest {

    @Test
    public void test_getDisplayType() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertEquals("Webauthn Register", factory.getDisplayType());
    }

    @Test
    public void test_getReferenceCategory() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertEquals(null, factory.getReferenceCategory());
    }

    @Test
    public void test_isConfigurable() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertFalse(factory.isConfigurable());
    }

    @Test
    public void test_getRequirementChoices() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        AuthenticationExecutionModel.Requirement[] requirements = {
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED,
        };
        Assert.assertArrayEquals(requirements, factory.getRequirementChoices());
    }

    @Test
    public void test_isUserSetupAllowed() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertFalse(factory.isUserSetupAllowed());
    }

    @Test
    public void test_getHelpText() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertEquals("Register for WebAuthn", factory.getHelpText());
    }

    @Test
    public void test_getConfigProperties() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertTrue(factory.getConfigProperties().isEmpty());
    }

    @Test
    public void test_init() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        try {
            factory.init(null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_create() throws Exception {
        KeycloakSession session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);

        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertNotNull(factory.create(session));
    }

    @Test
    public void test_postInit() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        try {
            factory.postInit(null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_close() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        try {
            factory.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_getId() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertEquals(RegisterAuthenticatorFactory.PROVIDER_ID, factory.getId());
    }
}
