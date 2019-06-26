angular.module('personalizationsmarteditCustomizeViewServiceModule', [
        'personalizationsmarteditServicesModule',
        'smarteditRootModule'
    ])
    .factory('personalizationsmarteditCustomizeViewProxy', function(gatewayProxy, personalizationsmarteditCustomizeViewHelper) {

        var personalizationsmarteditCustomizeView = function() { //NOSONAR
            this.gatewayId = "personalizationsmarteditCustomizeViewGateway";
            gatewayProxy.initForService(this);
        };

        personalizationsmarteditCustomizeView.prototype.getSourceContainersInfo = function() {
            return personalizationsmarteditCustomizeViewHelper.getSourceContainersInfo();
        };

        return new personalizationsmarteditCustomizeView();
    });
