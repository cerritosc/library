!function(e){var t={};function n(r){if(t[r])return t[r].exports;var o=t[r]={i:r,l:!1,exports:{}};return e[r].call(o.exports,o,o.exports,n),o.l=!0,o.exports}n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(r,o,function(t){return e[t]}.bind(null,o));return r},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="",n(n.s=85)}({85:function(e,t){const n=document.createElement("link");n.href="css/"+function(){const e=function(e){const t=e+"=",n=document.cookie.split(";");for(let e=0;e<n.length;e++){const r=n[e];for(;" "==r.charAt(0);)r=r.substring(1,r.length);if(0==r.indexOf(t))return r.substring(t.length,r.length)}return null}("theme"),t=function(e){let t=void 0;return location.search.substr(1).split("&").some((function(n){return n.split("=")[0]==e&&(t=n.split("=")[1])})),t}("theme");return t?(function(e,t,n){let r="";if(n){const e=new Date;e.setTime(e.getTime()+24*n*60*60*1e3),r="; expires="+e.toUTCString()}document.cookie=e+"="+(t||"")+r+"; path=/"}("theme",t,7),t):e||"modern"}()+".css",n.type="text/css",n.rel="stylesheet",document.getElementsByTagName("head")[0].appendChild(n)}});
//# sourceMappingURL=settings.js.map