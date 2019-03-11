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
