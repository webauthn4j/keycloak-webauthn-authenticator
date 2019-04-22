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

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class WebAuthnSettingsProviderFactoryTest {

    private KeycloakSession session;
    private WebAuthnSettingsProviderFactory factory;

    @Before
    public void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class);
        this.factory = new WebAuthnSettingsProviderFactory();
    }

    @Test
    public void test_create() {
        Assert.assertNotNull(factory.create(session));
    }

    @Test
    public void test_init() {
        try {
        	factory.init(mock(Config.Scope.class));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_postInit() {
        try {
            factory.postInit(mock(KeycloakSessionFactory.class));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_close() {
        try {
            factory.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_getId() throws Exception {
        Assert.assertEquals("webauthn-settings", factory.getId());
    }
}
