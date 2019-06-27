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
/* jshint unused:false, undef:false */
angular.module('customViewModule', ['templateCacheDecoratorModule', 'ngMockE2E', 'ui.bootstrap', 'editorTabsetModule', 'loadConfigModule', 'translationServiceModule'])
    .constant('PATH_TO_CUSTOM_VIEW', 'editorTabset/customView.html')
    .controller('customViewController', function(editorTabsetService, restServiceFactory, sharedDataService, $scope) {
        restServiceFactory.setDomain('thedomain');
        sharedDataService.set('experience', {
            siteDescriptor: {
                uid: 'someSiteUid'
            },
            catalogDescriptor: {
                catalogId: 'somecatalogId',
                catalogVersion: 'someCatalogVersion'
            }
        });

        var vm = this;

        vm.data = {
            id: 'smarteditObjectId',
        };


        // Register tabs (This will be done somewhere else)
        vm.tabs = [{
            id: 'tab1',
            title: 'tab1.name',
            templateUrl: 'tab1Template.html'
        }, {
            id: 'tab2',
            title: 'tab2.name',
            templateUrl: 'tab2Template.html'
        }, {
            id: 'tab3',
            title: 'tab3.name',
            templateUrl: 'tab3Template.html'
        }, {
            id: 'tab4',
            title: 'tab4.name',
            templateUrl: 'tab4Template.html'
        }, {
            id: 'tab5',
            title: 'tab5.name',
            templateUrl: 'tab5Template.html'
        }, {
            id: 'tab6',
            title: 'tab6.name',
            templateUrl: 'tab6Template.html'
        }, {
            id: 'tab7',
            title: 'tab7.name',
            templateUrl: 'tab7Template.html'
        }];

    })
    .directive('testTab', function($q) {
        return {
            restrict: 'E',
            transclude: false,
            scope: {
                innerSave: '=',
                innerReset: '='
            },
            link: function(scope, elem, attr) {
                scope.innerSave = function() {
                    return $q.reject("Save from Test Tab1 error");
                };

                scope.innerReset = function() {
                    return $q.reject("Reset from Test Tab1 error");
                };
            }

        };
    })
    .directive('testTab2', function($q) {
        return {
            restrict: 'E',
            transclude: false,
            scope: {
                insideSave: '=',
                insideReset: '='
            },
            template: "<div>Tab 2 Content</div>",
            link: function(scope, elem, attr) {
                scope.insideSave = function() {
                    return $q.when("Save from Test Tab2");
                };

                scope.insideReset = function() {
                    return $q.when("Reset from Test Tab2");
                };
            }
        };
    })
    .directive('testTab3', function($q) {
        return {
            restrict: 'E',
            transclude: false,
            scope: {
                insideSave: '=',
                insideReset: '='
            },
            template: "<div>Tab 3 Content</div>",
            link: function(scope, elem, attr) {
                scope.insideSave = function() {
                    return $q.reject("Save from Test Tab3 error");
                };

                scope.insideReset = function() {
                    return $q.reject("Reset from Test Tab3 error");
                };
            }
        };
    })
    .run(function($templateCache) {
        'use strict';

        $templateCache.put('tab1Template.html',
            "<test-tab class='sm-tab-content' inner-save='onSave' inner-reset='onReset'>Tab 1 Content</test-tab>"
        );

        $templateCache.put('tab2Template.html',
            "<div>" +
            "<test-tab2 class='sm-tab-content' inside-save='onSave' inside-reset='onReset'></test-tab2>" +
            "</div>"
        );

        $templateCache.put('tab3Template.html',
            "<div>" +
            "<test-tab3 class='sm-tab-content' inside-save='onSave' inside-reset='onReset'></test-tab3>" +
            "</div>"
        );

        $templateCache.put('tab4Template.html',
            "<test-tab class='sm-tab-content' inner-save='onSave' inner-reset='onReset'>Tab 4 Content</test-tab>"
        );
        $templateCache.put('tab5Template.html',
            "<test-tab class='sm-tab-content' inner-save='onSave' inner-reset='onReset'>Tab 5 Content</test-tab>"
        );
        $templateCache.put('tab6Template.html',
            "<test-tab class='sm-tab-content' inner-save='onSave' inner-reset='onReset'>Tab 6 Content</test-tab>"
        );
        $templateCache.put('tab7Template.html',
            "<test-tab class='sm-tab-content' inner-save='onSave' inner-reset='onReset'>Tab 7 Content</test-tab>"
        );
    });
angular.module('smarteditcontainer').requires.push('customViewModule');
