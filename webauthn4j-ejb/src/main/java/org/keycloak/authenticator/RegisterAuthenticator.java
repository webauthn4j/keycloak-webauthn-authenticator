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

import com.webauthn4j.response.WebAuthnRegistrationContext;
import com.webauthn4j.response.client.Origin;
import com.webauthn4j.response.client.challenge.Challenge;
import com.webauthn4j.response.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.UriUtils;
import org.keycloak.credential.WebAuthnCredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RegisterAuthenticator implements Authenticator {
    private static final Logger logger = Logger.getLogger(RegisterAuthenticator.class);

    private static final String AUTH_NOTE = "WEBAUTH_CHALLENGE";

    private final KeycloakSession session;

    public RegisterAuthenticator(KeycloakSession session) {
        this.session = session;
    }

    private Map<String, String> generateParameter(AuthenticationFlowContext context){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userid",context.getUser().getId());
        parameters.put("username", context.getUser().getUsername());
        Challenge challenge = new DefaultChallenge();
        String challengeValue = Base64Url.encode(challenge.getValue());
        parameters.put("challenge", challengeValue);
        String origin = context.getUriInfo().getBaseUri().getHost();
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE, challengeValue);
        return parameters;
    }
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String userid = context.getUser().getId();
        String username = context.getUser().getUsername();
        Challenge challenge = new DefaultChallenge();
        String challengeValue = Base64Url.encode(challenge.getValue());
        String origin = context.getUriInfo().getBaseUri().getHost();
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE, challengeValue);

        Response form = context.form()
                .setAttribute("origin", origin)
                .setAttribute("challenge", challengeValue)
                .setAttribute("userid", userid)
                .setAttribute("username", username)
                .createForm("webauthn_register.ftl");
        context.challenge(form);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters();

        // receive error from navigator.credentials.create()
        String error = params.getFirst("error");
        if (error != null && !error.isEmpty()) {
            throw new AuthenticationFlowException("exception raised from navigator.credentials.create() : " + error, AuthenticationFlowError.INVALID_CREDENTIALS);
        }

        String baseUrl = UriUtils.getOrigin(context.getUriInfo().getBaseUri());
        String rpId = context.getUriInfo().getBaseUri().getHost();
        byte[] clientDataJSON = Base64.getUrlDecoder().decode(params.getFirst("clientDataJSON"));
        byte[] attestationObject = Base64.getUrlDecoder().decode(params.getFirst("attestationObject"));
        String publicKeyCredentialId = params.getFirst("publicKeyCredentialId");
        context.getUser().setSingleAttribute("PUBLIC_KEY_CREDENTIAL_ID", publicKeyCredentialId);
        logger.debugv("publicKeyCredentialId = {0}", context.getUser().getAttribute("PUBLIC_KEY_CREDENTIAL_ID").get(0));

        Origin origin = new Origin(baseUrl);
        Challenge challenge = new DefaultChallenge(context.getAuthenticationSession().getAuthNote(AUTH_NOTE));
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        try {
            WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(clientDataJSON, attestationObject, serverProperty, false);
            WebAuthnRegistrationContextValidator webAuthnRegistrationContextValidator = WebAuthnRegistrationContextValidator.createNonStrictRegistrationContextValidator();
            WebAuthnRegistrationContextValidationResponse response = webAuthnRegistrationContextValidator.validate(registrationContext);

            WebAuthnCredentialModel credential = new WebAuthnCredentialModel();

            credential.setAttestedCredentialData(response.getAttestationObject().getAuthenticatorData().getAttestedCredentialData());
            credential.setAttestationStatement(response.getAttestationObject().getAttestationStatement());
            credential.setCount(response.getAttestationObject().getAuthenticatorData().getSignCount());

            this.session.userCredentialManager().updateCredential(context.getRealm(), context.getUser(), credential);
            context.success();
        } catch (Exception me) {
            me.printStackTrace();
            String userid = context.getUser().getId();
            String username = context.getUser().getUsername();
            Challenge ch = new DefaultChallenge();
            String challengeValue = Base64.getUrlEncoder().withoutPadding().encodeToString(ch.getValue());
            String ori = UriUtils.getOrigin(context.getUriInfo().getBaseUri());
            context.getAuthenticationSession().setAuthNote("challenge", challengeValue);

            Response form = context.form()
                    .setAttribute("origin", ori)
                    .setAttribute("challenge", challengeValue)
                    .setAttribute("userid", userid)
                    .setAttribute("username", username)
                    .createForm("webauthn_register.ftl");           // TODO: treat error cases
            context.challenge(form);
        }
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }

}
