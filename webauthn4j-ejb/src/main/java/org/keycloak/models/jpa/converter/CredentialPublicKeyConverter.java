package org.keycloak.models.jpa.converter;

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.attestation.authenticator.CredentialPublicKey;

import javax.persistence.AttributeConverter;

public class CredentialPublicKeyConverter implements AttributeConverter<CredentialPublicKey, String> {
    JsonConverter converter = new JsonConverter();
    @Override
    public String convertToDatabaseColumn(CredentialPublicKey credentialPublicKey) {
        return converter.writeValueAsString(credentialPublicKey);
    }

    @Override
    public CredentialPublicKey convertToEntityAttribute(String s) {
        return converter.readValue(s, CredentialPublicKey.class);
    }
}
