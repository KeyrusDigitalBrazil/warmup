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
describe('test inner toolbarService Module', function() {

    var $rootScope, $q, $log, gatewayProxy, toolbarServiceFactory;

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {

        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));
    beforeEach(angular.mock.module('toolbarInterfaceModule'));
    beforeEach(angular.mock.module('toolbarModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$log_, _toolbarServiceFactory_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        $log = _$log_;
        toolbarServiceFactory = _toolbarServiceFactory_;
    }));

    it('factory called twice on the same toolbar name returns the same instance', function() {

        expect(toolbarServiceFactory.getToolbarService('toolBar1')).toBe(toolbarServiceFactory.getToolbarService('toolBar1'));
    });

    it('factory called twice on different toolbar names returns different instances', function() {

        expect(toolbarServiceFactory.getToolbarService('toolBar1')).not.toBe(toolbarServiceFactory.getToolbarService('toolBar2'));
    });

    it('on first acquisiion of a new ToolbarServiceInstance, it is registered with the gateway proxy', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(toolbarService, ["addAliases", "removeItemByKey", "removeAliasByKey", "_removeItemOnInner", "triggerActionOnInner"]);
    });

    it('addAliases is an empty function', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(toolbarService.addAliases).toBeEmptyFunction();
    });

    it('removeAliasByKey is an empty function', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(toolbarService.removeAliasByKey).toBeEmptyFunction();
    });

    it('triggerActionOnInner will call a callback associated with a given key', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        var callbacks = {
            callback1: function() {}
        };

        spyOn(callbacks, 'callback1');

        toolbarService.addItems([{
            key: 'key1',
            i18nKey: 'i18nKey1',
            callback: callbacks.callback1
        }]);

        toolbarService.triggerActionOnInner({
            key: 'key1'
        });

        expect(callbacks.callback1).toHaveBeenCalled();
    });

    it('triggerActionOnInner will log an error if an action does not exist for the given key', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        spyOn($log, 'error');

        toolbarService.triggerActionOnInner({
            key: 'nonExistentKey'
        });

        expect($log.error).toHaveBeenCalledWith('triggerActionByKey() - Failed to find action for key nonExistentKey');
    });

    it('_removeItemOnInner logs an error when key is not found', function() {
        // Arrange
        var invalidKey = 'some Invalid Key';
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        toolbarService.actions = {};
        spyOn($log, 'warn');

        // Act
        toolbarService._removeItemOnInner(invalidKey);

        // Assert
        expect($log.warn).toHaveBeenCalledWith('removeItemByKey() - Failed to find action for key ' + invalidKey);
    });

    it('_removeItemOnInner removes the action', function() {
        // Arrange
        var key = 'someKey';
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        toolbarService.actions = {
            'someKey': {}
        };

        // Act
        toolbarService._removeItemOnInner(key);

        // Assert
        expect(key in toolbarService.actions).toBe(false);
    });

});
