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
angular.module('scanner', [])
    .constant('TEST_TARGET_SELECTOR', ".smartEditComponent[data-smartedit-component-type='ContentSlot']")
    .run(function(inViewElementObserver, TEST_TARGET_SELECTOR) {

        inViewElementObserver.addSelector(TEST_TARGET_SELECTOR);
    })
    .controller('scannerDebugController', function($interval, inViewElementObserver, yjQuery, TEST_TARGET_SELECTOR) {

        this.inPageElements = [];
        this.visibleElements = [];

        this.$onInit = function() {
            this.showDebug = true;
            this.toggleDebug = function() {
                this.showDebug = !this.showDebug;
            };

            this.getReallyEligibleElements = function() {
                return yjQuery(TEST_TARGET_SELECTOR).length;
            };

            this.getEligibleElements = function() {
                return inViewElementObserver.getAllElements().length;
            };

            this.getInViewElements = function() {
                return inViewElementObserver.getInViewElements().length;
            };

            this.removeFirstComponent = function() {
                yjQuery(TEST_TARGET_SELECTOR + ':first').remove();
            };

            this.addComponentAsFirst = function() {
                yjQuery('body').prepend(yjQuery("<div class='smartEditComponent' data-smartedit-component-type='ContentSlot'>AAAAAA</div>"));
            };

        };
    })
    .component('e2eDebugger', {
        template: '<button class="btn btn-info btn-sm toggleDebug" ng-click="ctrl.toggleDebug();"><span ng-if="!ctrl.showDebug">open</span><span ng-if="ctrl.showDebug">close</span> debug</button>' +
            '<div ng-show="ctrl.showDebug">' +
            '<button id="removeFirstComponent" class="btn btn-info btn-sm" ng-click="ctrl.removeFirstComponent();">remove first eligible component</button>' +
            '<button id="addComponentAsFirst"  class="btn btn-info btn-sm" ng-click="ctrl.addComponentAsFirst();">add eligible component as first</button>' +
            '<pre>Total eligible components in page: <div id="total-eligible-components"><strong>{{ctrl.getReallyEligibleElements()}}</strong></div></pre>' +
            '<pre>Total eligible components according to observer: <div id="total-eligible-components-from-observer"><strong>{{ctrl.getEligibleElements()}}</strong></div></pre>' +
            '<pre>Total visible components according to observer: <div id="total-visible-eligible-components-from-observer"><strong>{{ctrl.getInViewElements()}}</strong></div><span data-ng-if="false">{{ctrl.getVisibleSmartEditComponents() | json}}</span></pre>' +
            '</div>',
        controller: 'scannerDebugController',
        controllerAs: 'ctrl'
    });
