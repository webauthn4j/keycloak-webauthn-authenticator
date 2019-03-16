package org.keycloak.authenticator;


import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.UriUtils;
import org.keycloak.credential.WebAuthnCredentialModel;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.Urls;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebAuthn4jAuthenticator implements Authenticator {
    private static final String AUTH_NOTE = "WEBAUTH_CHALLENGE";

    private KeycloakSession session;

    public WebAuthn4jAuthenticator(KeycloakSession session) {
        this.session = session;
    }

    private Map<String, String> generateParameters(RealmModel realm, URI baseUri) {
        Map<String, String> params = new HashMap<>();
        Challenge challenge = new DefaultChallenge();
        params.put("challenge", Base64Url.encode(challenge.getValue()));
        params.put("rpId", baseUri.getHost());
        params.put("origin", UriUtils.getOrigin(baseUri));
        params.put("settingsPath", Urls.realmBase(baseUri).path(realm.getName()).path("webauthn-settings").path("register").build().toString());
        return params;
    }


    public void authenticate(AuthenticationFlowContext context) {
        LoginFormsProvider form = context.form();
        Map<String, String> params = generateParameters(context.getRealm(), context.getUriInfo().getBaseUri());
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE, params.get("challenge"));
        params.forEach(form::setAttribute);
        context.challenge(form.createForm("webauthn.ftl"));
    }

    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters();

        String baseUrl = UriUtils.getOrigin(context.getUriInfo().getBaseUri());
        String rpId = context.getUriInfo().getBaseUri().getHost();

        Origin origin = new Origin(baseUrl);
        Challenge challenge = new DefaultChallenge(context.getAuthenticationSession().getAuthNote(AUTH_NOTE));
        ServerProperty server = new ServerProperty(origin, rpId, challenge, null);

        byte[] credentialId = Base64Url.decode(params.getFirst("credentialId"));
        byte[] clientDataJSON = Base64Url.decode(params.getFirst("clientDataJSON"));
        byte[] authenticatorData = Base64Url.decode(params.getFirst("authenticatorData"));
        byte[] signature = Base64Url.decode(params.getFirst("signature"));

        String userId = params.getFirst("userHandle");
        UserModel user = session.users().getUserById(userId, context.getRealm());
        WebAuthnAuthenticationContext authenticationContext = new WebAuthnAuthenticationContext(
                credentialId,
                clientDataJSON,
                authenticatorData,
                signature,
                server,
                true
        );

        WebAuthnCredentialModel cred = new WebAuthnCredentialModel();
        cred.setAuthenticationContext(authenticationContext);

        boolean result = session.userCredentialManager().isValid(context.getRealm(), user, cred);
        if (result) {
            context.setUser(user);
            context.success();
        } else {
            context.cancelLogin();
        }
    }

    public boolean requiresUser() {
        return false;
    }

    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    public void close() {

    }
}
