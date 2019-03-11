package org.keycloak.credential;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.JpaWebAuthnAuthenticatorStore;
import org.keycloak.models.jpa.WebAuthnAuthenticatorStore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebAuthnCredentialProvider implements CredentialProvider, CredentialInputValidator, CredentialInputUpdater {

    private final KeycloakSession session;
    private final WebAuthnAuthenticatorStore store;


    public WebAuthnCredentialProvider(KeycloakSession session) {
        this.session = session;
        this.store = new JpaWebAuthnAuthenticatorStore(session);

    }


    @Override
    public boolean supportsCredentialType(String credentialType) {
        return WebAuthnCredentialModel.WEBAUTHN_CREDENTIAL_TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!WebAuthnCredentialModel.class.isInstance(input)) {
            return false;
        }
        WebAuthnCredentialModel credential = WebAuthnCredentialModel.class.cast(input);
        WebAuthnCredentialModel result = store.registerAuthenticator(realm, user, credential);
        return result != null;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return new HashSet<>();
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) return false;
        List<WebAuthnCredentialModel> authenticators = store.getAuthenticatorByUser(realm, user);
        return !authenticators.isEmpty();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!WebAuthnCredentialModel.class.isInstance(input)) return false;
        WebAuthnCredentialModel context = WebAuthnCredentialModel.class.cast(input);
        List<WebAuthnCredentialModel> auths = store.getAuthenticatorByUser(realm, user);

        WebAuthnAuthenticationContextValidator webAuthnAuthenticationContextValidator =
                new WebAuthnAuthenticationContextValidator();
        try {
            for (WebAuthnCredentialModel auth : auths) {

                byte[] credentialId = auth.getAttestedCredentialData().getCredentialId();
                if (Arrays.equals(credentialId, context.getAuthenticationContext().getCredentialId())) {
                    Authenticator authenticator = new AuthenticatorImpl(
                            auth.getAttestedCredentialData(),
                            auth.getAttestationStatement(),
                            auth.getCount()
                    );

                    WebAuthnAuthenticationContextValidationResponse response =
                            webAuthnAuthenticationContextValidator.validate(
                                    context.getAuthenticationContext(),
                                    authenticator);
                    store.updateCounter(realm, user, auth);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
