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
    <script type="text/javascript">
       function base64urlFromByteArray(array) {
           return btoa(
                     new Uint8Array(array)
                     .reduce((data, byte) => data + String.fromCharCode(byte), '')
                  ).replace(/\+/g, "-").replace(/\//g, "_").replace(/\=+$/,'');
       }

       function byteArrayFromBase64url(str) {
            var str = (str + '===').slice(0, str.length + (str.length % 4))
            .replace(/-/g, '+').replace(/_/g, '/');
            return Uint8Array.from(atob(str), c => c.charCodeAt(0));
       }

        var challenge = "${challenge}";
        var userid = "${userid}";
        var username = "${username}";
        var origin = "${origin}"
        var publicKey = {
            challenge: byteArrayFromBase64url(challenge),
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

                document.getElementById("clientDataJSON").value = base64urlFromByteArray(clientDataJSON);
                document.getElementById("attestationObject").value = base64urlFromByteArray(attestationObject);
                document.getElementById("register").submit();

            })
            .catch(function(err) {
                console.log(err);
            });


    </script>

    </#if>
    </@layout.registrationLayout>