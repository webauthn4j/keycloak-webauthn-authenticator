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

import java.net.URI;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.keycloak.WebAuthnConstants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.mockito.Mockito;

public class RegisterAuthenticatorTest {

    private KeycloakSession session;
    private RegisterAuthenticator authenticator;
    private AuthenticationFlowContext context;

    private static final String ChallengeSample = "q8b_snApQBGDSlHKrUPXDA"; // used to generate the sample below
    private static final String ClientDataJSONSample
        = "eyJjaGFsbGVuZ2UiOiJxOGJfc25BcFFCR0RTbEhLclVQWERBIiwib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwidHlwZSI6IndlYmF1dGhuLmNyZWF0ZSJ9";
    private static final String AttestationObjectSample
        = "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVjESZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2NBAAAAAAAAAAAAAAAAAAAAAAAAAAAAQNl5cq57gFloyTRaRzspkmVtaFjseFuas8LzmCa9_M40tZHwnOxuDFLj__IQkmCi9bwtXfxGU8L3IbXoJf-R1v6lAQIDJiABIVggHRj3_pRuFc4STvzzqO3WgO9cnj7u9R4OogbtOc4qA5kiWCAniOpK656_61Qnmx4hkWffohlH4JDbuytCpCtf9jrruA";
    private static final String PublicKeyCredentialIdSample
        = "2XlyrnuAWWjJNFpHOymSZW1oWOx4W5qzwvOYJr38zjS1kfCc7G4MUuP_8hCSYKL1vC1d_EZTwvchtegl_5HW_g";

    @Before
    public void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);
        this.authenticator = new RegisterAuthenticator(session);
        this.context = mock(AuthenticationFlowContext.class, Mockito.RETURNS_DEEP_STUBS);
        // avoid NPE
        when(context.getUriInfo().getBaseUri()).thenReturn(new URI("http://localhost:8080"));
        when(context.getRealm().getName()).thenReturn("webauthn");
    }

    @Test
    public void test_authenticate() throws Exception {
        // test
        try {
            authenticator.authenticate(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context).challenge(any(Response.class));
    }

    @Test
    public void test_action_navigator_credentials_create_error() throws Exception {
        // set up mock
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add(WebAuthnConstants.ERROR, "NotAllowedError");
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        // test
        try {
            authenticator.action(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
    }

    @Test
    public void test_action() throws Exception {
        // set up mock
        MultivaluedMap<String, String> params = getSimulatedParametersFromRegistrationResponse();
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
                .thenReturn(RegisterAuthenticatorTest.ChallengeSample);

        // test
        try {
            authenticator.action(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context).success();
    }

    @Test
    public void test_action_webauthn4j_validation_fails() throws Exception {
        // setup mock
        MultivaluedMap<String, String> params = getSimulatedParametersFromRegistrationResponse();
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE)).thenReturn("7777777777777777");

        // test
        try {
            authenticator.action(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
    }

    @Test
    public void test_requiresUser() throws Exception {
        // test
        Assert.assertTrue(authenticator.requiresUser());
    }

    @Test
    public void test_close() throws Exception {
        // test
        try {
            authenticator.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_configuredFor() throws Exception {
        // test
        Assert.assertTrue(authenticator.configuredFor(session, mock(RealmModel.class), mock(UserModel.class)));
    }

    @Test
    public void test_setRequiredActions() throws Exception {
        // test
        try {
            authenticator.setRequiredActions(session, mock(RealmModel.class), mock(UserModel.class));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private MultivaluedMap<String, String> getSimulatedParametersFromRegistrationResponse() {
        return getSimulatedParametersFromRegistrationResponse(null, null, null);
    }

    private MultivaluedMap<String, String> getSimulatedParametersFromRegistrationResponse(
            String clientDataJSON,
            String attestationObject,
            String publicKeyCredentialId) {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        if (clientDataJSON == null) clientDataJSON = ClientDataJSONSample;
        params.add(WebAuthnConstants.CLIENT_DATA_JSON, clientDataJSON);
        if (attestationObject == null) attestationObject = AttestationObjectSample;
        params.add(WebAuthnConstants.ATTESTATION_OBJECT, attestationObject);
        if (publicKeyCredentialId == null) publicKeyCredentialId = PublicKeyCredentialIdSample;
        params.add(WebAuthnConstants.PUBLIC_KEY_CREDENTIAL_ID, publicKeyCredentialId);
        return params;
    }
}
