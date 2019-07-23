    <#import "template.ftl" as layout>
    <@layout.registrationLayout; section>
    <#if section = "title">
     title
    <#elseif section = "header">
    ${msg("loginTitleHtml", realm.name)}
    <#elseif section = "form">

    <form id="webauth" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
        <div class="${properties.kcFormGroupClass!}">
        </div>
        <input type="hidden" id="clientDataJSON" name="clientDataJSON"/>
        <input type="hidden" id="authenticatorData" name="authenticatorData"/>
        <input type="hidden" id="signature" name="signature"/>
        <input type="hidden" id="credentialId" name="credentialId"/>
        <input type="hidden" id="userHandle" name="userHandle"/>
        <input type="hidden" id="error" name="error"/>
    </form>

    <#if authenticators??>
        <form id="authn_select">
            <table class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Use</th>
                        <th>Authenticator Label</th>
                    </tr>
                </thead>
                <tbody>
                    <#list authenticators.authenticators as authenticator>
                        <tr>
                            <td>
                                <input type="checkbox" name="authn_use_chk" value="${authenticator.credentialId}" checked/>
                            </td>
                            <td>
                                ${authenticator.label}
                            </td>
                        </tr>
                    </#list>
                </tbody>
            </table>
            <input type="button" value="Authenticate" onclick="checkAllowCredentials();">
        </form>
    </#if>

    <script type="text/javascript" src="${url.resourcesPath}/node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/base64url.js"></script>
    <script type="text/javascript">

    window.onload = function doAuhenticateAutomatically() {
        let isUserIdentified = ${isUserIdentified};
        if (!isUserIdentified) doAuthenticate([]);
    }

    function checkAllowCredentials() {
        let allowCredentials = [];
        let authn_use = document.forms['authn_select'].authn_use_chk;
        if (authn_use.length === undefined && authn_use.checked) {
            allowCredentials.push({
                id: base64url.decode(authn_use.value, { loose: true }),
                type: 'public-key',
            })
        }
        doAuthenticate(allowCredentials);
    }

    function doAuthenticate(allowCredentials) {
        let challenge = "${challenge}";
        let rpId = "${rpId}";
        let origin = "${origin}";
        let publicKey = {
            challenge: base64url.decode(challenge, { loose: true }),
            rpId: rpId,
            userVerification : "preferred",
        };

        if (allowCredentials.length) {
            publicKey.allowCredentials = allowCredentials;
        }

        navigator.credentials.get({publicKey})
            .then(function(result) {
                window.result = result;
                console.log(result);

                let clientDataJSON = result.response.clientDataJSON;
                let authenticatorData = result.response.authenticatorData;
                let signature = result.response.signature;

                $("#clientDataJSON").val(base64url.encode(new Uint8Array(clientDataJSON), { pad: false }));
                $("#authenticatorData").val(base64url.encode(new Uint8Array(authenticatorData), { pad: false }));
                $("#signature").val(base64url.encode(new Uint8Array(signature), { pad: false }));
                $("#credentialId").val(result.id);
                if(result.response.userHandle) {
                    $("#userHandle").val(base64url.encode(new Uint8Array(result.response.userHandle), { pad: false }));
                }
                $("#webauth").submit();
            })
            .catch(function(err) {
                console.log(err);
                $("#error").val(err);
                $("#webauth").submit();
            })
        ;
    }

    </script>
    <#elseif section = "info">

    </#if>
    </@layout.registrationLayout>
