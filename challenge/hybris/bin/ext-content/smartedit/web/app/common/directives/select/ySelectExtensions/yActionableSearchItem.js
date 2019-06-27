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
/**
 * @ngdoc overview
 * @name yActionableSearchItemModule
 * @requires smarteditServicesModule
 * @description
 * This module defines the {@link yActionableSearchItemModule.directive:yActionableSearchItem yActionableSearchItem} component
 **/
angular.module("yActionableSearchItemModule", ['smarteditServicesModule'])


    .controller('yActionableSearchItemController', function($scope, systemEventService) {

        var defaultEventId = 'yActionableSearchItem_ACTION_CREATE';
        var defaultActionText = 'se.yationablesearchitem.action.create';

        this.getActionText = function() {
            return this.actionText || defaultActionText;
        }.bind(this);

        this.showForm = function() {
            return this.uiSelect && this.uiSelect.search && this.uiSelect.search.length > 0;
        }.bind(this);

        this.getInputText = function() {
            return this.uiSelect.search;
        };

        this.buttonPressed = function() {
            var evtId = this.eventId || defaultEventId;
            systemEventService.publishAsync(evtId, this.uiSelect.search || "");
            this.uiSelect.close();
        }.bind(this);

    })

    /**
     * @ngdoc directive
     * @name yActionableSearchItemModule.directive:yActionableSearchItem
     * @restrict E
     * @scope
     * @param {@String=} [eventId='yActionableSearchItem_ACTION_CREATE'] The event ID that is triggered on the
     * systemEventService when the button is pressed
     * @param {@String=} [actionText='se.yationablesearchitem.action.create'] The i18n key label for the button
     * @description
     * The yActionableSearchItem Angular component is designed to work with the ySelect drop down. It allows you to add
     * a button in the resultsHeader aread of the ySelect's drop-down, and trigger a user-defined action when pressed.
     */
    .component('yActionableSearchItem', {
        templateUrl: 'yActionableSearchItemTemplate.html',
        require: {
            ySelect: '^ySelect',
            uiSelect: '^uiSelect'
        },
        controller: 'yActionableSearchItemController',
        bindings: {
            eventId: '@?',
            actionText: '@?'
        }
    });
