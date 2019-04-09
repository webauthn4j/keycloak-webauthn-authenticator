package org.keycloak.models.jpa;

import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.CredentialPublicKey;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.jpa.converter.AAGUIDConverter;
import org.keycloak.models.jpa.converter.AttestationStatementConverter;
import org.keycloak.models.jpa.converter.CredentialPublicKeyConverter;

import javax.persistence.*;

@NamedQueries({
    @NamedQuery(name="authenticatorByUser", query="select auth from AuthenticatorEntity auth where auth.user=:user")
})
@Entity
@Table(name="WEBAUTHN_AUTHENTICATOR")
public class AuthenticatorEntity {

    @Id
    @Column(name="ID")
    protected String id;

    @Lob
    @Column(name="ATTESTATION_STATEMENT")
    @Convert(converter = AttestationStatementConverter.class)
    protected AttestationStatement attestationStatement;

    @Lob
    @Column(name="AAGUID")
    @Convert(converter = AAGUIDConverter.class)
    protected AAGUID aaguid;

    @Lob
    @Column(name="CREDENTIAL_ID")
    protected byte[] credentialId;

    @Lob
    @Column(name="CREDENTIAL_PUBLIC_KEY")
    @Convert(converter = CredentialPublicKeyConverter.class)
    protected CredentialPublicKey credentialPublicKey;

    @Column(name="COUNTER")
    protected long counter;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private UserEntity user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AttestationStatement getAttestationStatement() {
        return attestationStatement;
    }

    public void setAttestationStatement(AttestationStatement attestationStatement) {
        this.attestationStatement = attestationStatement;
    }

    public AAGUID getAaguid() {
        return aaguid;
    }

    public void setAaguid(AAGUID aaguid) {
        this.aaguid = aaguid;
    }

    public byte[] getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(byte[] credentialId) {
        this.credentialId = credentialId;
    }

    public CredentialPublicKey getCredentialPublicKey() {
        return credentialPublicKey;
    }

    public void setCredentialPublicKey(CredentialPublicKey credentialPublicKey) {
        this.credentialPublicKey = credentialPublicKey;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        AuthenticatorEntity that = (AuthenticatorEntity)o;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
