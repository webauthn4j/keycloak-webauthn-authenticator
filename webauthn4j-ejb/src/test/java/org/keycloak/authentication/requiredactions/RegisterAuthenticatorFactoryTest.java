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

package org.keycloak.authentication.requiredactions;

import org.keycloak.OAuth2Constants;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;

import org.junit.Test;
import org.junit.Assert;

import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

public class RegisterAuthenticatorFactoryTest {

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

    @Test
    public void test_createDispley() throws Exception {
        KeycloakSession session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);

        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertNotNull(factory.createDisplay(session, null));
        Assert.assertNull(factory.createDisplay(session, OAuth2Constants.DISPLAY_CONSOLE));
        Assert.assertNull(factory.createDisplay(session, "fujiyama"));
    }

    @Test
    public void test_getDisplayText() throws Exception {
        RegisterAuthenticatorFactory factory = new RegisterAuthenticatorFactory();
        Assert.assertEquals("Webauthn Register", factory.getDisplayText());
    }
}
