package org.keycloak.forms.login.freemarker.model;

import java.util.LinkedList;
import java.util.List;

import org.keycloak.WebAuthnConstants;
import org.keycloak.models.UserModel;

public class WebAuthnAuthenticatorsBean {
    private List<WebAuthnAuthenticatorBean> authenticators = new LinkedList<WebAuthnAuthenticatorBean>();

    public WebAuthnAuthenticatorsBean(UserModel user) {
        // should consider multiple credentials in the future, but only single credential supported now.
        List<String> credentialIds = user.getAttribute(WebAuthnConstants.PUBKEY_CRED_ID_ATTR);
        List<String> labels = user.getAttribute(WebAuthnConstants.PUBKEY_CRED_LABEL_ATTR);
        if (credentialIds != null && credentialIds.size() == 1 && !credentialIds.get(0).isEmpty()) {
            String credentialId = credentialIds.get(0);
            String label = (labels.size() == 1 && !labels.get(0).isEmpty()) ? labels.get(0) : "label missing";
            authenticators.add(new WebAuthnAuthenticatorBean(credentialId, label));
        }
    }

    public List<WebAuthnAuthenticatorBean> getAuthenticators() {
        return authenticators;
    }

    public static class WebAuthnAuthenticatorBean {
        private final String credentialId;
        private final String label;

        public WebAuthnAuthenticatorBean(String credentialId, String label) {
            this.credentialId = credentialId;
            this.label = label;
        }

        public String getCredentialId() {
            return this.credentialId;
        }

        public String getLabel() {
            return this.label;
        }
    }
}
