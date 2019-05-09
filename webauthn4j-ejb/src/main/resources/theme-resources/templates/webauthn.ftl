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

    <script type="text/javascript" src="${url.resourcesPath}/node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/base64url.js"></script>
    <script type="text/javascript">

        window.onload = function doAuthenticate() {

            var challenge = "${challenge}";
            var rpId = "${rpId}";
            var origin = "${origin}";
            var publicKeyCredentialId = "${publicKeyCredentialId}";
            var publicKey;

            if (publicKeyCredentialId != '') {
                publicKey = {
                    challenge: base64url.decode(challenge, { loose: true }),
                    rpId: rpId,
                    allowCredentials: [{
                        id: base64url.decode(publicKeyCredentialId, { loose: true }),
                        type: 'public-key',
                        transports: ['usb', 'ble', 'nfc', 'internal'],
                    }],
                    timeout : 60000,
                };
            } else {
                publicKey = {
                    challenge: base64url.decode(challenge, { loose: true }),
                    rpId: rpId,
                    timeout : 60000,
                };
            };
            console.log(publicKey);

            navigator.credentials.get({publicKey})
                .then(function(result) {
                    window.result = result;
                    console.log(result);

                    var clientDataJSON = result.response.clientDataJSON;
                    var authenticatorData = result.response.authenticatorData;
                    var signature = result.response.signature;

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
