package org.keycloak.credential;


import com.webauthn4j.data.WebAuthnAuthenticationContext;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;

public class WebAuthnCredentialModel implements CredentialInput {

    public static final String WEBAUTHN_CREDENTIAL_TYPE = "webauthn";
    private AttestedCredentialData attestedCredentialData;
    private AttestationStatement attestationStatement;
    private WebAuthnAuthenticationContext authenticationContext;
    private long count;
    private String authenticatorId;

    @Override
    public String getType() {
        return WEBAUTHN_CREDENTIAL_TYPE;
    }

    public WebAuthnCredentialModel() {

    }

    public AttestedCredentialData getAttestedCredentialData() {
        return attestedCredentialData;
    }

    public AttestationStatement getAttestationStatement() {
        return attestationStatement;
    }

    public long getCount() {
        return count;
    }

    public WebAuthnAuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public void setAuthenticationContext(WebAuthnAuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void setAttestedCredentialData(AttestedCredentialData attestedCredentialData) {
        this.attestedCredentialData = attestedCredentialData;
    }

    public void setAttestationStatement(AttestationStatement attestationStatement) {
        this.attestationStatement = attestationStatement;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getAuthenticatorId() {
        return authenticatorId;
    }

    public void setAuthenticatorId(String authenticatorId) {
        this.authenticatorId = authenticatorId;
    }
}
