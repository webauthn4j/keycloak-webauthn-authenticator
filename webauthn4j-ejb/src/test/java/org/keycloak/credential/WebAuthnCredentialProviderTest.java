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

package org.keycloak.credential;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;

import org.keycloak.common.util.Base64Url;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import org.mockito.Mockito;

import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.WebAuthnRegistrationContext;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;

public class WebAuthnCredentialProviderTest {

    private KeycloakSession session;
    private WebAuthnCredentialProvider provider;

    @Before
    public void setupMock() throws Exception {
        this.session = mock(KeycloakSession.class, Mockito.RETURNS_DEEP_STUBS);
        this.provider = new WebAuthnCredentialProvider(session);
    }

    @Test
    public void test_updateCredential() throws Exception {
        // set up mock
        CredentialModel credModel = mock(CredentialModel.class);
        WebAuthnCredentialModel model = getValidWebAuthnCredentialModel();
        when(session.userCredentialManager()
                .createCredential(any(RealmModel.class), any(UserModel.class), any(CredentialModel.class)))
                .thenReturn(credModel);

        // test
        Assert.assertTrue(provider.updateCredential(mock(RealmModel.class), mock(UserModel.class), model));
    }

    @Test
    public void test_updateCredential_input_null() throws Exception {
        // test
        Assert.assertFalse(provider.updateCredential(mock(RealmModel.class), mock(UserModel.class), null));
    }

    @Test
    public void test_updateCredential_input_invalid_type() throws Exception {
        // set up mock
        WebAuthnCredentialModel model = mock(WebAuthnCredentialModel.class);
        when(model.getType()).thenReturn("unknown");

        // test
        Assert.assertFalse(provider.updateCredential(mock(RealmModel.class), mock(UserModel.class), model));
    }

    @Test
    public void test_disableCredentialType() throws Exception {
        // set up mock
        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString()))
                .thenReturn(Arrays.asList(mock(CredentialModel.class)));
        when(session.userCredentialManager()
                .removeStoredCredential(any(RealmModel.class), any(UserModel.class), anyString()))
                .thenReturn(true);

        // test
        try {
            provider.disableCredentialType(mock(RealmModel.class), mock(UserModel.class), WebAuthnCredentialModel.WEBAUTHN_CREDENTIAL_TYPE);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(session.userCredentialManager(), times(1)).removeStoredCredential(any(RealmModel.class), any(UserModel.class), anyString());
    }

    @Test
    public void test_disableCredentialType_unknown_type() throws Exception {
        // test
        try {
            provider.disableCredentialType(mock(RealmModel.class), mock(UserModel.class), "unknown");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        verify(session.userCredentialManager(), never()).removeStoredCredential(any(RealmModel.class), any(UserModel.class), anyString());
    }

    @Test
    public void test_isConfiguredFor() {
        // set up mock
        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString())
                .isEmpty())
                .thenReturn(false);

        // test
        try {
            provider.isConfiguredFor(mock(RealmModel.class), mock(UserModel.class), WebAuthnCredentialModel.WEBAUTHN_CREDENTIAL_TYPE);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_isConfiguredFor_empty() {
        // set up mock
        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString())
                .isEmpty())
                .thenReturn(true);

        // test
        try {
            provider.isConfiguredFor(mock(RealmModel.class), mock(UserModel.class), WebAuthnCredentialModel.WEBAUTHN_CREDENTIAL_TYPE);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_getDisableableCredentialTypes() {
        // set up mock
        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString())
                .isEmpty())
                .thenReturn(false);

        // test
        Assert.assertFalse(provider.getDisableableCredentialTypes(mock(RealmModel.class), mock(UserModel.class)).isEmpty());
    }

    @Test
    public void test_getDisableableCredentialTypes_empty() {
        // set up mock
        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString())
                .isEmpty())
                .thenReturn(true);

        // test
        Assert.assertTrue(provider.getDisableableCredentialTypes(mock(RealmModel.class), mock(UserModel.class)).isEmpty());
    }

    @Test
    public void test_isConfiguredFor_unknown_type() {
        // test
        try {
        	provider.isConfiguredFor(mock(RealmModel.class), mock(UserModel.class), "unknown");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_isValid() throws Exception {
        // set up mock
        // mimic valid model created on Authentication
        WebAuthnAuthenticationContext authenticationContext = getValidWebAuthnAuthenticationContext();
        WebAuthnCredentialModel input = new WebAuthnCredentialModel();
        input.setAuthenticationContext(authenticationContext);

        // mimic valid model created on Registration
        WebAuthnCredentialModel model = getValidWebAuthnCredentialModel();
        Method method = WebAuthnCredentialProvider.class.getDeclaredMethod("createCredentialModel", CredentialInput.class);
        method.setAccessible(true);
        CredentialModel credModel = (CredentialModel)method.invoke(provider, model);

        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString()))
                .thenReturn(Arrays.asList(credModel));

        // test
        Assert.assertTrue(provider.isValid(mock(RealmModel.class), mock(UserModel.class), input));
    }

    @Test
    public void test_isValid_credential_id_unmatch() throws Exception {
        // set up mock
        // mimic invalid model created on Authentication
        WebAuthnAuthenticationContext authenticationContext = getValidWebAuthnAuthenticationContext("atgcatgcatgcatgc");
        WebAuthnCredentialModel input = new WebAuthnCredentialModel();
        input.setAuthenticationContext(authenticationContext);

        // mimic valid model created on Registration
        WebAuthnCredentialModel model = getValidWebAuthnCredentialModel();
        Method method = WebAuthnCredentialProvider.class.getDeclaredMethod("createCredentialModel", CredentialInput.class);
        method.setAccessible(true);
        CredentialModel credModel = (CredentialModel)method.invoke(provider, model);

        when(session.userCredentialManager()
                .getStoredCredentialsByType(any(RealmModel.class), any(UserModel.class), anyString()))
                .thenReturn(Arrays.asList(credModel));

        // test
        Assert.assertFalse(provider.isValid(mock(RealmModel.class), mock(UserModel.class), input));
    }

    private WebAuthnCredentialModel getValidWebAuthnCredentialModel() {
        // mimic valid model created on Registration
        byte[] clientDataJSON = Base64.getUrlDecoder().decode("eyJjaGFsbGVuZ2UiOiJxOGJfc25BcFFCR0RTbEhLclVQWERBIiwib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwidHlwZSI6IndlYmF1dGhuLmNyZWF0ZSJ9");
        byte[] attestationObject = Base64.getUrlDecoder().decode("o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVjESZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2NBAAAAAAAAAAAAAAAAAAAAAAAAAAAAQNl5cq57gFloyTRaRzspkmVtaFjseFuas8LzmCa9_M40tZHwnOxuDFLj__IQkmCi9bwtXfxGU8L3IbXoJf-R1v6lAQIDJiABIVggHRj3_pRuFc4STvzzqO3WgO9cnj7u9R4OogbtOc4qA5kiWCAniOpK656_61Qnmx4hkWffohlH4JDbuytCpCtf9jrruA");

        Origin origin = new Origin("http://localhost:8080");
        Challenge challenge = new DefaultChallenge("q8b_snApQBGDSlHKrUPXDA");
        ServerProperty serverProperty = new ServerProperty(origin, "localhost", challenge, null);

        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(clientDataJSON, attestationObject, serverProperty, false);
        WebAuthnRegistrationContextValidator webAuthnRegistrationContextValidator = WebAuthnRegistrationContextValidator.createNonStrictRegistrationContextValidator();
        WebAuthnRegistrationContextValidationResponse response = webAuthnRegistrationContextValidator.validate(registrationContext);

        WebAuthnCredentialModel credential = new WebAuthnCredentialModel();
        credential.setAttestedCredentialData(response.getAttestationObject().getAuthenticatorData().getAttestedCredentialData());
        credential.setAttestationStatement(response.getAttestationObject().getAttestationStatement());
        credential.setCount(response.getAttestationObject().getAuthenticatorData().getSignCount());

        return credential;
    }

    private WebAuthnAuthenticationContext getValidWebAuthnAuthenticationContext() {
        // mimic valid model created on Authentication
        return getValidWebAuthnAuthenticationContext("2XlyrnuAWWjJNFpHOymSZW1oWOx4W5qzwvOYJr38zjS1kfCc7G4MUuP_8hCSYKL1vC1d_EZTwvchtegl_5HW_g");
    }

    private WebAuthnAuthenticationContext getValidWebAuthnAuthenticationContext(String base64UrlCredentialId) {
        // mimic valid or invalid model created on Authentication
        byte[] credentialId = Base64Url.decode(base64UrlCredentialId);
        byte[] clientDataJSON = Base64Url.decode("eyJjaGFsbGVuZ2UiOiJ0R3o3R3RUQVE2T3FwVHpoOEtLQnFRIiwib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwidHlwZSI6IndlYmF1dGhuLmdldCJ9");
        byte[] authenticatorData = Base64Url.decode("SZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2MBAAAAdg");
        byte[] signature = Base64Url.decode("MEUCIEaZhQ5dXi_C3IxU68ujLLt0DEcyk2EFPz_y45wYUA7AAiEAwkX86OFwpNzPRjSljTaTJVvZ_x9E6xnKhSmsKkUgmlo");
        Origin origin = new Origin("http://localhost:8080");
        Challenge challenge = new DefaultChallenge("tGz7GtTAQ6OqpTzh8KKBqQ");
        ServerProperty server = new ServerProperty(origin, "localhost", challenge, null);
        WebAuthnAuthenticationContext authenticationContext = new WebAuthnAuthenticationContext(
                credentialId,
                clientDataJSON,
                authenticatorData,
                signature,
                server,
                false
        );
        return authenticationContext;
    }

}
