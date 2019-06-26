angular.module('personalizationsmarteditExternalComponentDecoratorModule', [])
    .directive('personalizationsmarteditExternalComponentDecorator', function() {
        return {
            templateUrl: 'externalComponentDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'externalComponentDecoratorController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '=',
                componentAttributes: '<'
            }
        };
    });
