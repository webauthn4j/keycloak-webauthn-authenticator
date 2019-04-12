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

package org.keycloak.models.jpa.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.attestation.statement.AndroidKeyAttestationStatement;
import com.webauthn4j.data.attestation.statement.AndroidSafetyNetAttestationStatement;
import com.webauthn4j.data.attestation.statement.FIDOU2FAttestationStatement;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.attestation.statement.PackedAttestationStatement;
import com.webauthn4j.data.attestation.statement.TPMAttestationStatement;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;

public class JsonConverter implements Serializable {

    private static final String INPUT_MISMATCH_ERROR_MESSAGE = "Input data does not match expected form";

    private ObjectMapper jsonMapper;

    private boolean jsonMapperInitialized = false;

    public JsonConverter() {
        this.jsonMapper = new ObjectMapper(new JsonFactory());
    }

    @SuppressWarnings("unchecked")
    public <T> T readValue(String src, Class valueType) {
        try {
            return (T) getJsonMapper().readValue(src, valueType);
        } catch (MismatchedInputException | JsonParseException e) {
            throw new DataConversionException(INPUT_MISMATCH_ERROR_MESSAGE, e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String writeValueAsString(Object value) {
        try {
            return getJsonMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ObjectMapper getJsonMapper() {
        if(!jsonMapperInitialized) {
            jsonMapper.registerModule(new JsonModule());
            jsonMapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, false);
            jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jsonMapperInitialized = true;
        }
        return jsonMapper;
    }

    class JsonModule extends SimpleModule {
        JsonModule() {
            super("JsonModule");
            this.registerSubtypes(new NamedType(FIDOU2FAttestationStatement.class, FIDOU2FAttestationStatement.FORMAT));
            this.registerSubtypes(new NamedType(PackedAttestationStatement.class, PackedAttestationStatement.FORMAT));
            this.registerSubtypes(new NamedType(AndroidKeyAttestationStatement.class, AndroidKeyAttestationStatement.FORMAT));
            this.registerSubtypes(new NamedType(AndroidSafetyNetAttestationStatement.class, AndroidSafetyNetAttestationStatement.FORMAT));
            this.registerSubtypes(new NamedType(TPMAttestationStatement.class, TPMAttestationStatement.FORMAT));
            this.registerSubtypes(new NamedType(NoneAttestationStatement.class, NoneAttestationStatement.FORMAT));
        }
    }

}
