package org.keycloak.models.jpa.converter;

import com.webauthn4j.data.attestation.authenticator.AAGUID;

import javax.persistence.AttributeConverter;

public class AAGUIDConverter implements AttributeConverter<AAGUID, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(AAGUID aaguid) {
        return aaguid.getBytes();
    }

    @Override
    public AAGUID convertToEntityAttribute(byte[] bytes) {
        return new AAGUID(bytes);
    }
}
