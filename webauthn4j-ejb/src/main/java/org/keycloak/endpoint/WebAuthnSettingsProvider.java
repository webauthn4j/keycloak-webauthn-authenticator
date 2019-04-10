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

package org.keycloak.endpoint;

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.request.AuthenticatorSelectionCriteria;
import com.webauthn4j.request.PublicKeyCredentialCreationOptions;
import com.webauthn4j.request.PublicKeyCredentialParameters;
import com.webauthn4j.request.PublicKeyCredentialType;
import com.webauthn4j.response.attestation.statement.COSEAlgorithmIdentifier;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class WebAuthnSettingsProvider implements RealmResourceProvider {

    private KeycloakSession session ;

    public WebAuthnSettingsProvider(KeycloakSession session){
        this.session = session;
    }
    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public PublicKeyCredentialCreationOptions getRegistrationSetting() {
        List<PublicKeyCredentialParameters> publickey = new ArrayList<>();
        publickey.add(new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256));
        publickey.add(new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256));
        AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria(null,true, null);
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = new PublicKeyCredentialCreationOptions(null,null,null,publickey, null,null,authenticatorSelectionCriteria,null, null);

        return publicKeyCredentialCreationOptions;
    }

    @Override
    public void close() {

    }
}
