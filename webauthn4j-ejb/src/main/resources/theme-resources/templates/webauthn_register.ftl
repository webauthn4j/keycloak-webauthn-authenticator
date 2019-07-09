    <#import "template.ftl" as layout>
    <@layout.registrationLayout; section>
    <#if section = "title">
     title
    <#elseif section = "header">
    ${msg("loginTitleHtml", realm.name)}
    <#elseif section = "form">
    <form id="register" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
        <div class="${properties.kcFormGroupClass!}">
        </div>
        <input type="hidden" id="clientDataJSON" name="clientDataJSON"/>
        <input type="hidden" id="attestationObject" name="attestationObject"/>
        <input type="hidden" id="publicKeyCredentialId" name="publicKeyCredentialId"/>
        <input type="hidden" id="error" name="error"/>
    </form>
    <script type="text/javascript" src="${url.resourcesPath}/node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/base64url.js"></script>
    <script type="text/javascript">
        var challenge = "${challenge}";
        var userid = "${userid}";
        var username = "${username}";
        var origin = "${origin}";
        var requireResidentKey = window.confirm("If you wish to store your ID and its credential in your authenticator, push OK button.\n NOTECE: Not all authenticator can do that. Please make sure whether your autheticator has its capability.");
        var publicKey = {
            challenge: base64url.decode(challenge, { loose: true }),
            rp: {
                name:  "Keycloak"
            },
            user: {
                id:  base64url.decode(userid, { loose: true }),
                name: username,
                displayName: username
            },
            pubKeyCredParams: [
                {
                    type: "public-key",
                    alg: -257
                },
                {
                    type: "public-key",
                    alg: -7
                }
            ],
            authenticatorSelection: {
                requireResidentKey: requireResidentKey
            },
            excludeCredentials: [],
            extensions: {}
        };

        navigator.credentials.create({publicKey})
            .then(function(result) {
                window.result = result;
                console.log(result.response);
                var clientDataJSON = result.response.clientDataJSON;
                var attestationObject = result.response.attestationObject;
                var publicKeyCredentialId = result.rawId;

                $("#clientDataJSON").val(base64url.encode(new Uint8Array(clientDataJSON), { pad: false }));
                $("#attestationObject").val(base64url.encode(new Uint8Array(attestationObject), { pad: false }));
                $("#publicKeyCredentialId").val(base64url.encode(new Uint8Array(publicKeyCredentialId), { pad: false }));
                $("#register").submit();

            })
            .catch(function(err) {
                console.log(err);
                $("#error").val(err);
                $("#register").submit();

            });


    </script>

    </#if>
    </@layout.registrationLayout>