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

        <div class="${properties.kcFormGroupClass!}">
            <input type="button" value="Authenticate" id="authenticate"/>
        </div>
    </form>
        <div id="kc-registration">
            <span><a href="${url.registrationUrl}">Register</a></span>
        </div>
    <script type="text/javascript" src="${url.resourcesPath}/node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/base64url.js"></script>
    <script type="text/javascript">

       function doAuthenticate() {

        var challenge = "${challenge}";
        var rpId = "${rpId}";
        var origin = "${origin}";
        var publicKey = {
            challenge: base64url.decode(challenge),
            rpId: rpId,
            timeout : 60000,
        };

        navigator.credentials.get({publicKey})
            .then(function(result) {
                window.result = result;
                console.log(result);

                var clientDataJSON = result.response.clientDataJSON;
                var authenticatorData = result.response.authenticatorData;
                var signature = result.response.signature;

                $("#clientDataJSON").val(base64url.encode(clientDataJSON));
                $("#authenticatorData").val(base64url.encode(authenticatorData));
                $("#signature").val(base64url.encode(signature));
                $("#credentialId").val(result.id);
                if(result.response.userHandle) {
                    $("#userHandle").val(String.fromCharCode.apply("", new Uint8Array(result.response.userHandle)));
                }
                
                $("#webauth").submit();

            })
            .catch(function(err) {
                console.log(err);
            });
        }
        $("#authenticate").click(function() {
            doAuthenticate();
        });
    </script>
    <#elseif section = "info">

    </#if>
    </@layout.registrationLayout>
