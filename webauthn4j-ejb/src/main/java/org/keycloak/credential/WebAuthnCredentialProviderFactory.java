package org.keycloak.credential;

import org.keycloak.models.KeycloakSession;

public class WebAuthnCredentialProviderFactory implements CredentialProviderFactory<WebAuthnCredentialProvider> {
    @Override
    public CredentialProvider create(KeycloakSession session) {
        return new WebAuthnCredentialProvider(session);
    }

    @Override
    public String getId() {
        return "keycloak-webauthn";
    }
}
