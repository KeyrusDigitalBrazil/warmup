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
angular.module('abAnalyticsDecoratorControllerModule', ['abAnalyticsServiceModule'])
    .controller('abAnalyticsDecoratorController', function(abAnalyticsService) {
        this.title = 'AB Analytics';
        this.contentTemplate = 'abAnalyticsDecoratorContentTemplate.html';

        this.$onInit = function() {
            abAnalyticsService.getABAnalyticsForComponent(this.smarteditComponentId).then(function(analytics) {
                this.abAnalytics = 'A: ' + analytics.aValue + ' B: ' + analytics.bValue;
            }.bind(this));
        };
    });
