package org.keycloak.endpoint;

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
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
