(function(XHR) {
            "use strict";

            var element = document.createElement('div');
            element.id = "interceptedResponse";
            element.appendChild(document.createTextNode(""));
            document.body.appendChild(element);

            var open = XHR.prototype.open;
            var send = XHR.prototype.send;

            XHR.prototype.open = function(method, url, async, user, pass) {
                this._url = url; // want to track the url requested
                open.call(this, method, url, async, user, pass);
            };

            XHR.prototype.send = function(data) {
                var self = this;
                var oldOnReadyStateChange;
                var url = this._url;

                function onReadyStateChange() {
//                debugger;
// && url.indexOf('confirm') !== -1
                    if(self.status === 200 && self.readyState == 4 ) {
                        document.getElementById("interceptedResponse").innerHTML += '{"data":' + self.responseText + '}*****';
                        document.getElementById("interceptedResponse").setAttribute("ready", "true");
                    }

                    if (oldOnReadyStateChange) {
                        console.log('oldOnReadyStateChange')
                        oldOnReadyStateChange();
                    }
                }

                if(this.addEventListener) {
                    this.addEventListener("readystatechange", onReadyStateChange, false);
                } else {
                    oldOnReadyStateChange = this.onreadystatechange;
                    this.onreadystatechange = onReadyStateChange;
                }
                send.call(this, data);
            }
        })(XMLHttpRequest)