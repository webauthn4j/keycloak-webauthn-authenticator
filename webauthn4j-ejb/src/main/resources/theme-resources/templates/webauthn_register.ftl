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
    </form>
    <script type="text/javascript" src="${url.resourcesPath}/node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/base64url.js"></script>
    <script type="text/javascript">
        
        var challenge = "${challenge}";
        var userid = "${userid}";
        var username = "${username}";
        var origin = "${origin}"
        var publicKey = {
            challenge: base64url.decode(challenge),
            rp: {
                name:  "Keycloak"
            },
            user: {
                id:  Uint8Array.from(userid, c => c.charCodeAt(0)),
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
            timeout : 60000,
            excludeCredentials: [],
            extensions: {}
        };

        navigator.credentials.create({publicKey})
            .then(function(result) {
                window.result = result;
                console.log(result.response);
                var clientDataJSON = result.response.clientDataJSON;
                var attestationObject = result.response.attestationObject;

                $("#clientDataJSON").val(base64url.encode(clientDataJSON));
                $("#attestationObject").val(base64url.encode(attestationObject));
                $("#register").submit();

            })
            .catch(function(err) {
                console.log(err);
            });


    </script>

    </#if>
    </@layout.registrationLayout>