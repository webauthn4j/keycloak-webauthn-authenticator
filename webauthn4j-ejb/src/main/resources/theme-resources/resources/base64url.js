'use strict';
var base64url = base64url || {};
(function(base64url){

    
    base64url.encode = function (array) {
        return btoa(
                  new Uint8Array(array)
                  .reduce((data, byte) => data + String.fromCharCode(byte), '')
               ).replace(/\+/g, "-").replace(/\//g, "_").replace(/\=+$/,'');
    }

    base64url.decode = function(str) {
         var str = (str + '===')
            .slice(0, str.length + (str.length % 4))
            .replace(/-/g, '+')
            .replace(/_/g, '/');
         return Uint8Array.from(atob(str), c => c.charCodeAt(0));
    }

    return base64url;

}(base64url));