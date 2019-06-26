angular.module('personalizationsmarteditCustomizeViewServiceModule', [
        'smarteditRootModule'
    ])
    .factory('personalizationsmarteditCustomizeViewProxy', function(gatewayProxy) {
        var proxy = function() {
            this.gatewayId = 'personalizationsmarteditCustomizeViewGateway';
            gatewayProxy.initForService(this);
        };

        proxy.prototype.getSourceContainersInfo = function() {};

        return new proxy();
    });
