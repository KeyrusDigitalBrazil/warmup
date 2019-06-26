/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
angular.module('dummyCmsDecorators', [
        'textDisplayDecorator',
    ])
    .factory('DummyServiceClass', function() {

        var DummyServiceClass = function() {};

        DummyServiceClass.prototype.getDecoratorClass = function() {
            return "greenBackground";
        };

        return DummyServiceClass;
    })
    .run(function(decoratorService) {
        decoratorService.addMappings({
            'componentType1': ['textDisplay']
        });

        decoratorService.enable('textDisplay');
    });
angular.module('textDisplayDecorator', ['decoratortemplates', 'translationServiceModule'])
    .directive('textDisplay', function() {
        return {
            templateUrl: 'textDisplayDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            bindToController: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },
            controllerAs: 'cont',
            controller: function(DummyServiceClass) {
                var service = new DummyServiceClass();
                this.$onInit = function() {
                    this.textDisplayContent = this.smarteditComponentId + "_Text_from_dummy_decorator";
                    this.class = service.getDecoratorClass();
                };
            }
        };
    });
angular.module('decoratortemplates', []).run(function($templateCache) {
    'use strict';

    $templateCache.put('textDisplayDecoratorTemplate.html',
        "<div class='{{cont.class}}'>\n" +
        "<div class=\"row\" data-ng-if=\"!active\">\n" +
        "</div>\n" +
        "{{cont.textDisplayContent}}\n" +
        "<div data-ng-transclude></div>\n" +
        "</div>"
    );
});
