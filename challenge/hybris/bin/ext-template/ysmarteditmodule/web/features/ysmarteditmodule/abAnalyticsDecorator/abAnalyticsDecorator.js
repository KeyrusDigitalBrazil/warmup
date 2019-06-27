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
angular.module('abAnalyticsDecoratorModule', ['ysmarteditmoduleTemplates', 'ui.bootstrap', 'pascalprecht.translate', 'abAnalyticsDecoratorControllerModule'])
    .directive('abAnalyticsDecorator', function() {
        return {
            templateUrl: 'abAnalyticsDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'abAnalyticsDecoratorController',
            controllerAs: '$ctrl',
            bindToController: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                smarteditProperties: '@',
                active: '<'
            }
        };
    });
