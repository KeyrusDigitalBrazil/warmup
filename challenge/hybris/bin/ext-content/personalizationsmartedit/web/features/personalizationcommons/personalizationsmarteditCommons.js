angular.module('personalizationsmarteditCommons', [
        'personalizationcommonsTemplates',
        'yjqueryModule',
        'alertServiceModule',
        'seConstantsModule',
        'l10nModule',
        'smarteditServicesModule',
        'personalizationsmarteditCommonsModule'
    ]).run(function($rootScope, PERSONALIZATION_MODEL_STATUS_CODES) {
        $rootScope.PERSONALIZATION_MODEL_STATUS_CODES = PERSONALIZATION_MODEL_STATUS_CODES;
    })
    .filter('statusNotDeleted', function(personalizationsmarteditUtils) {
        return function(value) {
            if (angular.isArray(value)) {
                return personalizationsmarteditUtils.getVisibleItems(value);
            }
            return value;
        };
    }).directive('negate', [
        function() {
            return {
                require: 'ngModel',
                link: function(scope, element, attribute, ngModelController) {
                    ngModelController.$isEmpty = function(value) {
                        return !!value;
                    };

                    ngModelController.$formatters.unshift(function(value) {
                        return !value;
                    });

                    ngModelController.$parsers.unshift(function(value) {
                        return !value;
                    });
                }
            };
        }
    ])
    .directive('personalizationCurrentElement', [
        function() {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    if (attrs.personalizationCurrentElement) {
                        scope.$eval(attrs.personalizationCurrentElement)(element);
                    }
                }
            };
        }
    ])
    .directive("personalizationPreventParentScroll", function() {
        return {
            restrict: "A",
            scope: false,
            link: function(scope, elem) {

                var onWheel = function(event) {
                    var originalEventCondition = event.originalEvent && (event.originalEvent.wheelDeltaY || event.originalEvent.wheelDelta);
                    var IEEventCondition = -(event.deltaY || event.delta) || 0;
                    elem[0].scrollTop -= (event.wheelDeltaY || originalEventCondition || event.wheelDelta || IEEventCondition);
                    event.stopPropagation();
                    event.preventDefault();
                    event.returnValue = false;
                };

                elem[0].addEventListener('wheel', onWheel, false);

                scope.$on('$destroy', function() {
                    elem[0].removeEventListener('wheel', onWheel, false);
                });

            }
        };
    });
