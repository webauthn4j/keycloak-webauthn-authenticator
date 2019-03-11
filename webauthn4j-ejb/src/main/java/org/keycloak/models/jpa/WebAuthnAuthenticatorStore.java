package org.keycloak.models.jpa;

import org.keycloak.credential.WebAuthnCredentialModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;

public interface WebAuthnAuthenticatorStore {
    List<WebAuthnCredentialModel> getAuthenticatorByUser(RealmModel realm, UserModel user);
    WebAuthnCredentialModel registerAuthenticator(RealmModel realm, UserModel user, WebAuthnCredentialModel authenticator);
    WebAuthnCredentialModel updateCounter(RealmModel realms, UserModel user, WebAuthnCredentialModel authenticator);
}
