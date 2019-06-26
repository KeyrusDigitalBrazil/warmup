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
 * @name tabsetModule
 * @description
 *
 * The Tabset module provides the directives required to display a group of tabsets within a tabset. The
 * {@link tabsetModule.directive:yTabset yTabset} is of particular interest to SmartEdit developers
 * because this directive is responsible for displaying and organizing tabs.
 *
 */
angular.module('tabsetModule', ['ui.bootstrap', 'coretemplates', 'functionsModule', 'translationServiceModule'])

    /**
     * @ngdoc directive
     * @name tabsetModule.directive:yTabset
     * @scope
     * @restrict E
     * @element smartedit-tabset
     *
     * @description
     * The directive responsible for displaying and organizing tabs within a tabset. A specified number of tabs will
     * display a tab header. If there are more tabs than the maximum number defined, the remaining tabs will be grouped
     * in a drop-down menu with the header "More". When a user clicks on a tab header or an item from the drop-down
     * menu, the content of the tabset changes to the body of the selected tab.
     *
     * Note: The body of each tab is wrapped within a {@link tabsetModule.directive:yTab yTab} directive.
     *
     * @param {Object} model Custom data to be passed to each tab. Neither the smartedit-tabset directive or the
     * smartedit-tab directive can modify this value. The tabs' contents determine how to parse and use this object.
     * @param {Object[]} tabsList A list that contains the configuration for each of the tabs to be displayed in the tabset.
     * @param {string} tabsList.id The ID used to track the tab within the tabset.
     * @param {String} tabsList.title The tab header.
     * @param {String} tabsList.templateUrl Path to the HTML fragment to be displayed as the tab content.
     * @param {boolean} tabsList.hasErrors Flag that indicates whether a visual error is to be displayed in the tab or not.
     * @param {Function} tabControl  An optional parameter. A function that will be called with scope as its parameter.
     * It allows the caller to register extra functionality to be executed in the child tabs.
     * @param {Number} numTabsDisplayed The number of tabs for which tab headers will be displayed. The remaining tab
     * headers will be grouped within the 'MORE' drop-down menu.
     *
     */
    .component('yTabset', {
        transclude: false,
        templateUrl: 'yTabsetTemplate.html',
        controller: 'yTabsetController',
        controllerAs: 'yTabset',
        bindings: {
            model: '=',
            tabsList: '<',
            tabControl: '=',
            numTabsDisplayed: '@'
        }
    })
    .controller('yTabsetController', function() {

        this.isActiveInMoreTab = function() {
            return this.tabsList.indexOf(this.selectedTab) >= (this.numTabsDisplayed - 1);
        };

        this.$onChanges = function() {
            if (this.tabsList && this.tabsList.length > 0) {
                var tabAlreadySelected = false;

                for (var tabIdx in this.tabsList) {
                    if (this.tabsList.hasOwnProperty(tabIdx)) {
                        if (this.tabsList[tabIdx].active) {
                            this.selectedTab = this.tabsList[tabIdx];
                            tabAlreadySelected = true;
                        }
                    }
                }

                if (!tabAlreadySelected) {
                    this.selectedTab = this.tabsList[0];
                    for (tabIdx in this.tabsList) {
                        if (this.tabsList.hasOwnProperty(tabIdx)) {
                            var tab = this.tabsList[tabIdx];
                            tab.active = (tabIdx === '0');
                            tab.hasErrors = false;
                        }
                    }
                }

            }

        };

        this.selectTab = function(tabToSelect) {
            if (tabToSelect) {
                if (!this.selectedTab.active) {
                    // If a tab is made active 'manually' from outside this controller, there can be a mismatch. 
                    // This method finds the tab that is actually selected. 
                    this.findSelectedTab();
                }
                this.selectedTab.active = false;
                this.selectedTab = tabToSelect;
                this.selectedTab.active = true;
            }
        };

        this.dropDownHasErrors = function() {
            var tabsInDropDown = this.tabsList.slice(this.numTabsDisplayed - 1);

            return tabsInDropDown.some(function(tab) {
                return tab.hasErrors;
            });
        };

        this.markTabsInError = function(tabsInErrorList) {
            this.resetTabErrors();

            var tabId;
            var tabFilter = function(tab) {
                return (tab.id === tabId);
            };

            for (var idx in tabsInErrorList) {
                if (tabsInErrorList.hasOwnProperty(idx)) {
                    tabId = tabsInErrorList[idx];
                    var resultTabs = this.tabsList.filter(tabFilter);

                    if (resultTabs[0]) {
                        resultTabs[0].hasErrors = true;
                    }
                }
            }
        };

        this.resetTabErrors = function() {
            for (var tabKey in this.tabsList) {
                if (this.tabsList.hasOwnProperty(tabKey)) {
                    this.tabsList[tabKey].hasErrors = false;
                }
            }
        };

        this.findSelectedTab = function() {
            var selectedTab = this.tabsList.filter(function(tab) {
                return tab.active;
            })[0];

            if (selectedTab) {
                this.selectedTab = selectedTab;
            }
        };

    })
    .controller('yTabController', function($templateCache, $compile, $log, $scope, isBlank, $element) {
        this.$onInit = function() {
            var template = $templateCache.get(this.content);
            if (isBlank(template)) {
                throw "did not find cached template for file " + this.content + " when building yTab " + this.tabId;
            }
            $scope.model = this.model;
            $scope.tabId = this.tabId;
            $element.append($compile(template)($scope));

            if (this.tabControl) {
                try {
                    this.tabControl.bind($scope)($scope);
                } catch (e) {
                    $log.error(e);
                }
            }
        };
    })
    /**
     * @ngdoc directive
     * @name tabsetModule.directive:yTab
     * @scope
     * @restrict E
     * @element smartedit-tab
     *
     * @description
     * The directive  responsible for wrapping the content of a tab within a
     * {@link tabsetModule.directive:yTabset yTabset} directive.
     *
     * @param {Number} tabId The ID used to track the tab within the tabset. It must match the ID used in the tabset.
     * @param {String} content Path to the HTML fragment to be displayed as the tab content.
     * @param {Object} model Custom data. Neither the smartedit-tabset directive or the smartedit-tab directive
     * can modify this value. The tabs' contents determine how to parse and use this object.
     * @param {function} tabControl An optional parameter. A function that will be called with scope as its parameter.
     * It allows the caller to register extra functionality to be executed in the tab.
     *
     */
    .component('yTab', {
        transclude: true,
        controller: 'yTabController',
        controllerAs: 'yTab',
        bindings: {
            content: '=',
            model: '=',
            tabControl: '=',
            tabId: '@'
        }
    });
