angular.module('hasMulticatalogModule', [
        'personalizationsmarteditServicesModule'
    ])
    .controller('hasMulticatalogController', function(personalizationsmarteditContextService) {
        var self = this;

        var getSeExperienceData = function() {
            return personalizationsmarteditContextService.getSeData().seExperienceData;
        };

        //Lifecycle methods
        this.$onInit = function() {
            self.hasMulticatalog = getSeExperienceData().siteDescriptor.contentCatalogs.length > 1;
        };

    })
    .component('hasMulticatalog', {
        templateUrl: 'hasMulticatalogTemplate.html',
        controller: 'hasMulticatalogController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {}
    });
