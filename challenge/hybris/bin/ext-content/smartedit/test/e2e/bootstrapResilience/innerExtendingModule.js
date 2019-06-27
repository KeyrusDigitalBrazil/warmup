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
angular.module('innerExtendingModule', []);
angular.module('textDisplayDecorator', ['decoratortemplates', 'translationServiceModule'])
    .run(function(DummyServiceClass) {
        DummyServiceClass.prototype.getDecoratorClass = function() {
            console.info("override");
            return "redBackground";
        };
    })
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
                    this.textDisplayContent = this.smarteditComponentId + "_Text_from_overriden_dummy_decorator";
                    this.class = service.getDecoratorClass();
                };
            }
        };
    });
