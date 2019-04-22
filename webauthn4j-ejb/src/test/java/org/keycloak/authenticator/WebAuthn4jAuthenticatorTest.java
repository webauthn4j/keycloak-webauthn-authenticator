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
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.keycloak.WebAuthnConstants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.common.util.Base64Url;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.mockito.Mockito;

public class WebAuthn4jAuthenticatorTest {

    private KeycloakSession session;
    private WebAuthn4jAuthenticator authenticator;
    private AuthenticationFlowContext context;

    @Before
    public void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);
        this.authenticator = new WebAuthn4jAuthenticator(session);
        this.context = mock(AuthenticationFlowContext.class, Mockito.RETURNS_DEEP_STUBS);
        // avoid NPE
        when(context.getUriInfo().getBaseUri()).thenReturn(new URI("http://localhost:8080"));
        when(context.getRealm().getName()).thenReturn("webauthn");
    }

    @Test
    public void test_authenticate_2factor() throws Exception {
        // set up mock
        List<String> publicKeyCredentialIds = new ArrayList<>();
        publicKeyCredentialIds.add(getRandomString(32));
        when(context.getUser().getAttribute(WebAuthnConstants.PUBKEY_CRED_ID_ATTR)).thenReturn(publicKeyCredentialIds);

        // test
        try {
        	authenticator.authenticate(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context).challenge(any(Response.class));
    }

    @Test
    public void test_authenticate_2factor_publickey_not_registered() throws Exception {
        // set up mock
        when(context.getUser().getAttribute(WebAuthnConstants.PUBKEY_CRED_ID_ATTR)).thenReturn(null);

        // test
        try {
            authenticator.authenticate(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
        when(context.getUser().getAttribute(WebAuthnConstants.PUBKEY_CRED_ID_ATTR)).thenReturn(new ArrayList<String>());
        try {
            authenticator.authenticate(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
    }

    @Test
    public void test_authenticate_2factor_multiple_publicKey_registered() throws Exception {
        // set up mock
        List<String> publicKeyCredentialIds = new ArrayList<>();
        publicKeyCredentialIds.add(getRandomString(32));
        publicKeyCredentialIds.add(getRandomString(32));
        when(context.getUser().getAttribute(WebAuthnConstants.PUBKEY_CRED_ID_ATTR)).thenReturn(publicKeyCredentialIds);

        // test
        try {
            authenticator.authenticate(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
    }

    @Test
    public void test_authenticate_passwordless() throws Exception {
        // set up mock
        when(context.getUser()).thenReturn(null);

        // test
        try {
            authenticator.authenticate(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context).challenge(any(Response.class));
    }

    @Test
    public void test_action_passwordless() throws Exception {
        // set up mock
        when(session.userCredentialManager()
                .isValid(any(RealmModel.class), any(UserModel.class), Mockito.<CredentialInput>anyVararg()))
                .thenReturn(true);

        MultivaluedMap<String, String> params = getSimulatedParametersFromAuthenticationResponse();
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
                .thenReturn(getRandomString(32));

        // test
        try {
            authenticator.action(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context, times(1)).setUser(any(UserModel.class));
        verify(context).success();
    }

    
    @Test
    public void test_action_credential_not_valid() throws Exception {
        // set up mock
        when(session.userCredentialManager()
                .isValid(Mockito.any(RealmModel.class), any(UserModel.class), Mockito.<CredentialInput>anyVararg()))
                .thenThrow(new AuthenticationFlowException("unknown user authenticated by the authenticator", AuthenticationFlowError.UNKNOWN_USER));

        MultivaluedMap<String, String> params = getSimulatedParametersFromAuthenticationResponse();
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
                .thenReturn(getRandomString(32));

        // test
        try {
            authenticator.action(context);
            Assert.fail();
        } catch (AuthenticationFlowException e) {
            // NOP
        }
    }

    @Test
    public void test_action_credential_validation_fail() throws Exception {
        // set up mock
        when(session.userCredentialManager()
                .isValid(any(RealmModel.class), any(UserModel.class), Mockito.<CredentialInput>anyVararg()))
                .thenReturn(false);

        MultivaluedMap<String, String> params = getSimulatedParametersFromAuthenticationResponse();
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
                .thenReturn(getRandomString(32));

        // test
        try {
            authenticator.action(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(context).cancelLogin();
    }

    @Test
    public void test_action_navigator_credentials_get_error() throws Exception {
        // set up mock
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("error", "The user attempted to use an authenticator that recognized none of the provided credentials");
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
    public void test_action_2factor_residentkey() throws Exception {
        // set up mock
        when(session.userCredentialManager()
                .isValid(any(RealmModel.class), any(UserModel.class), Mockito.<CredentialInput>anyVararg()))
                .thenReturn(true);

        String userId = getRandomString(32);
        MultivaluedMap<String, String> params = getSimulatedParametersFromAuthenticationResponse(userId);
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
            .thenReturn(getRandomString(32));

        UserModel user = mock(UserModel.class);
        when(user.getId()).thenReturn(userId);
        when(context.getUser()).thenReturn(user);

        // test
        try {
            authenticator.action(context);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(context, times(1)).setUser(any(UserModel.class));
        verify(context).success();
    }

    @Test
    public void test_action_2factor_residentkey_different_user_authenticated() throws Exception {
        // set up mock
        String userId = getRandomString(32);
        MultivaluedMap<String, String> params = getSimulatedParametersFromAuthenticationResponse(userId);
        when(context.getHttpRequest().getDecodedFormParameters()).thenReturn(params);

        when(context.getAuthenticationSession().getAuthNote(WebAuthnConstants.AUTH_CHALLENGE_NOTE))
                .thenReturn(getRandomString(32));

        UserModel user = mock(UserModel.class);
        when(user.getId()).thenReturn(getRandomString(32));
        when(context.getUser()).thenReturn(user);

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
        Assert.assertFalse(authenticator.requiresUser());
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

    private String getRandomString(int sizeInByte) {
        return Base64Url.encode(KeycloakModelUtils.generateSecret(sizeInByte));
    }

    private MultivaluedMap<String, String> getSimulatedParametersFromAuthenticationResponse(String userHandle) {
        return getSimulatedParametersFromAuthenticationResponse(null, null, null, null, userHandle);
    }
 
    private MultivaluedMap<String, String> getSimulatedParametersFromAuthenticationResponse() {
        return getSimulatedParametersFromAuthenticationResponse(null, null, null, null, null);
    }

    private MultivaluedMap<String, String> getSimulatedParametersFromAuthenticationResponse(
            String clientDataJSON,
            String authenticatorData,
            String signature,
            String credentialId,
            String userHandle) {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        if (clientDataJSON == null) clientDataJSON = getRandomString(32);
        params.add(WebAuthnConstants.CLIENT_DATA_JSON, clientDataJSON);
        if (authenticatorData == null) authenticatorData = getRandomString(32);
        params.add(WebAuthnConstants.AUTHENTICATOR_DATA, authenticatorData);
        if (signature == null) signature = getRandomString(32);
        params.add(WebAuthnConstants.SIGNATURE, signature);
        if (credentialId == null) credentialId = getRandomString(32);
        params.add(WebAuthnConstants.CREDENTIAL_ID, credentialId);
        if (userHandle != null) params.add(WebAuthnConstants.USER_HANDLE, userHandle);
        return params;
    }

}
