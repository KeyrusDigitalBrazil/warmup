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
angular.module('outerapp', ['ngMockE2E', 'smarteditcontainer'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .controller('defaultController', function($rootScope, $scope, toolbarServiceFactory, $httpBackend, I18N_RESOURCE_URI, languageService, crossFrameEventService, SHOW_TOOLBAR_ITEM_CONTEXT, HIDE_TOOLBAR_ITEM_CONTEXT) {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolbar');

        var testRoot = "../../test/e2e/toolbars/itemMechanism/";

        $scope.sendActions = function() {
            toolbarService.addItems([{
                key: 'toolbar.action.action5',
                type: 'ACTION',
                nameI18nKey: 'toolbar.action.action5',
                callback: function() {
                    $scope.message = 'Action 5 called';
                },
                icons: [testRoot + 'icon5.png'],
                contextTemplateUrl: testRoot + 'action5ContextTemplate.html'
            }, {
                key: 'toolbar.standardTemplate',
                type: 'TEMPLATE',
                include: testRoot + 'standardTemplate.html'
            }, {
                key: 'toolbar.action.action6',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'toolbar.action.action6',
                callback: function() {
                    $scope.message = 'Action 6 called';
                },
                icons: [testRoot + 'icon6.png'],
                include: testRoot + 'hybridActionTemplate.html',
                contextTemplate: '<div>Hybrid Action 6 - Context</div>'
            }, {
                key: 'toolbar.action.action8',
                type: 'ACTION',
                nameI18nKey: 'Icon Test',
                callback: function() {
                    $scope.message = 'Action 8 called';
                },
                iconClassName: 'hyicon hyicon-clone se-toolbar-menu-ddlb--button__icon'
            }]);
        };

        $scope.removeActions = function() {
            toolbarService.removeItemByKey('toolbar.standardTemplate');
            toolbarService.removeItemByKey('toolbar.action.action5');
        };

        $scope.showActionToolbarContext5 = function() {
            crossFrameEventService.publish(SHOW_TOOLBAR_ITEM_CONTEXT, 'toolbar.action.action5');
        };

        $scope.showHybridActionToolbarContext = function() {
            crossFrameEventService.publish(SHOW_TOOLBAR_ITEM_CONTEXT, 'toolbar.action.action6');
        };

        $scope.hideHybridActionToolbarContext = function() {
            crossFrameEventService.publish(HIDE_TOOLBAR_ITEM_CONTEXT, 'toolbar.action.action6');
        };

        $httpBackend.whenGET(/configuration/).respond([{
            "value": "[\"*\"]",
            "key": "whiteListedStorefronts"
        }, {
            "value": "\"somepath\"",
            "key": "i18nAPIRoot"
        }]);

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "toolbar.action.action3": "action3",
            "toolbar.action.action4": "action4",
            "toolbar.action.action5": "action5",
            "toolbar.action.action6": "action6"
        });

        $httpBackend.whenGET(/.*/).passThrough();
    });
