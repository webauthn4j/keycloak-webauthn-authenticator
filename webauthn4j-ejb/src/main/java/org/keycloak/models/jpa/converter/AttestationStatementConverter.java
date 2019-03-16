package org.keycloak.models.jpa.converter;

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.attestation.statement.AttestationStatement;

import javax.persistence.AttributeConverter;

public class AttestationStatementConverter implements AttributeConverter<AttestationStatement, String> {

    private JsonConverter converter = new JsonConverter();

    @Override
    public String convertToDatabaseColumn(AttestationStatement attribute) {
        return converter.writeValueAsString(attribute);
    }

    @Override
    public AttestationStatement convertToEntityAttribute(String dbData) {
        return converter.readValue(dbData, AttestationStatement.class);
    }
}
