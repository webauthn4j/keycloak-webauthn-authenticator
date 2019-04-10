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

package org.keycloak.models.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Collections;
import java.util.List;

public class AuthenticatorEntityProvider implements JpaEntityProvider {


    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(AuthenticatorEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/webauthn-changelog.xml";
    }

    @Override
    public String getFactoryId() {
        return AuthenticatorEntityProviderFactory.ID;
    }

    @Override
    public void close() {

    }
}
