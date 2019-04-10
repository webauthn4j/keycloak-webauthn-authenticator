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

package org.keycloak.models.jpa;

import com.webauthn4j.response.attestation.authenticator.AttestedCredentialData;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.credential.WebAuthnCredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class JpaWebAuthnAuthenticatorStore implements WebAuthnAuthenticatorStore {
    private EntityManager em;

    public JpaWebAuthnAuthenticatorStore(KeycloakSession session) {
        this.em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }


    @Override
    public List<WebAuthnCredentialModel> getAuthenticatorByUser(RealmModel realm, UserModel user) {
        UserEntity userEntity = em.getReference(UserEntity.class, user.getId());
        TypedQuery<AuthenticatorEntity> query = em.createNamedQuery("authenticatorByUser", AuthenticatorEntity.class)
                .setParameter("user", userEntity);
        List<AuthenticatorEntity> result = query.getResultList();
        List<WebAuthnCredentialModel> ret = result.stream().map(this::toModel).collect(Collectors.toList());
        return ret;
    }

    @Override
    public WebAuthnCredentialModel registerAuthenticator(RealmModel realm, UserModel user, WebAuthnCredentialModel authenticator) {

        AuthenticatorEntity entity = new AuthenticatorEntity();
        entity.setId(KeycloakModelUtils.generateId());
        entity.setCounter(authenticator.getCount());
        entity.setAttestationStatement(authenticator.getAttestationStatement());
        entity.setAaguid(authenticator.getAttestedCredentialData().getAaguid());
        entity.setCredentialId(authenticator.getAttestedCredentialData().getCredentialId());
        entity.setCredentialPublicKey(authenticator.getAttestedCredentialData().getCredentialPublicKey());
        UserEntity userRef = em.getReference(UserEntity.class, user.getId());
        entity.setUser(userRef);
        em.persist(entity);

        return toModel(entity);
    }

    public WebAuthnCredentialModel updateCounter(RealmModel realms, UserModel user, WebAuthnCredentialModel authenticator) {
        AuthenticatorEntity entity = em.find(AuthenticatorEntity.class, authenticator.getAuthenticatorId());
        long count = entity.getCounter() + 1;
        entity.setCounter(count);
        em.persist(entity);

        return toModel(entity);
    }

    private WebAuthnCredentialModel toModel(AuthenticatorEntity entity) {
        WebAuthnCredentialModel model = new WebAuthnCredentialModel();
        AttestedCredentialData credential = new AttestedCredentialData(entity.getAaguid(), entity.getCredentialId(), entity.getCredentialPublicKey());
        model.setAttestedCredentialData(credential);
        model.setAttestationStatement(entity.getAttestationStatement());
        model.setCount(entity.getCounter());
        model.setAuthenticatorId(entity.id);
        return model;
    }
}
