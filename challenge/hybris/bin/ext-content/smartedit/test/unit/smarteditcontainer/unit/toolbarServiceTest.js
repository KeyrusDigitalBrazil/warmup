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
describe('test outer toolbarService Module', function() {

    var $rootScope, $q, $log, gatewayProxy, toolbarServiceFactory;

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);

        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

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

    it('on first acquisition of a new ToolbarServiceInstance, it is registered with the gateway proxy', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(toolbarService, ["addAliases", "removeItemByKey", "removeAliasByKey", "_removeItemOnInner", "triggerActionOnInner"]);
    });

    it('on change of aliases in setAliases, the onAliasChange callback is triggered', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        var onAliasesChange = jasmine.createSpy('onAliasesChange');

        toolbarService.setOnAliasesChange(onAliasesChange);
        toolbarService.addItems([{
            key: 'somekey',
            i18nKey: 'i18nKey1',
            callback: function() {}
        }]);

        expect(onAliasesChange).toHaveBeenCalled();
    });

    it('triggerAction triggers the associated action for the given key if it exists on the outer toolbar', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        var someAction = jasmine.createSpy('someAction');
        spyOn(toolbarService, 'triggerActionOnInner');

        toolbarService.addItems([{
            key: 'key1',
            i8nKey: 'i18nKey1',
            callback: someAction
        }]);

        toolbarService.triggerAction({
            key: 'key1',
            name: 'i18nKey1'
        });

        expect(someAction).toHaveBeenCalled();
        expect(toolbarService.triggerActionOnInner).not.toHaveBeenCalled();
    });

    it('triggerAction dispatches the associated action for the given key on the inner toolbar if it does not exist on the outer toolbar', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        var someAction = jasmine.createSpy('someAction');
        spyOn(toolbarService, 'triggerActionOnInner');

        toolbarService.addItems([{
            i8nKey: 'i18nKey2',
            callback: someAction
        }]);

        toolbarService.triggerAction({
            key: 'key1',
            name: 'i18nKey1'
        });

        expect(someAction).not.toHaveBeenCalled();
        expect(toolbarService.triggerActionOnInner).toHaveBeenCalledWith({
            key: 'key1',
            name: 'i18nKey1'
        });
    });

    it('triggerActionOnInner is an empty function', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        expect(toolbarService.triggerActionOnInner).toBeEmptyFunction();
    });

    it('adding 2 actions with the no priority gives them the same default priority and triggers a warning', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        spyOn($log, 'warn');

        toolbarService.addItems([{
            key: 'key1',
            i18nKey: 'i18nKey1',
            callback: function() {},
        }]);

        toolbarService.addItems([{
            key: 'key2',
            i18nKey: 'i18nKey2',
            callback: function() {},
        }]);

        expect($log.warn).toHaveBeenCalled();
        expect($log.warn.calls.argsFor(0)[0]).toContain('WARNING: In toolBar1 the items ');
        expect($log.warn.calls.argsFor(0)[0]).toContain('key2');
        expect($log.warn.calls.argsFor(0)[0]).toContain('key1');
        expect($log.warn.calls.argsFor(0)[0]).toContain('have the same priority');
        expect(toolbarService.aliases[0].priority).toBe(500);
        expect(toolbarService.aliases[1].priority).toBe(500);

    });

    it('toolbarService will properly sort actions based on their priority', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        toolbarService.addItems([{
            key: 'key1',
            i18nKey: 'i18nKey1',
            callback: function() {},
            priority: 99
        }, {
            key: 'key2',
            i18nKey: 'i18nKey2',
            callback: function() {},
            priority: 1
        }, {
            key: 'key3',
            i18nKey: 'i18nKey1',
            callback: function() {},
            priority: 75
        }]);

        expect(toolbarService.aliases[0].priority).toBe(1);
        expect(toolbarService.aliases[1].priority).toBe(75);
        expect(toolbarService.aliases[2].priority).toBe(99);
    });

    it('removeItemByKey calls _removeItemOnInner when the key is not found in the outer frame', function() {
        // Arrange
        var invalidKey = 'some Invalid Key';
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        toolbarService.actions = {};
        spyOn(toolbarService, '_removeItemOnInner');

        // Act
        toolbarService.removeItemByKey(invalidKey);

        // Assert
        expect(toolbarService._removeItemOnInner).toHaveBeenCalledWith(invalidKey);
    });

    it('removeItemByKey removes the action and calls removeAliasByKey', function() {
        // Arrange
        var key = 'someKey';
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');
        spyOn(toolbarService, 'removeAliasByKey');
        toolbarService.actions = {
            'someKey': {}
        };

        // Act
        toolbarService.removeItemByKey(key);

        // Assert
        expect(key in toolbarService.actions).toBe(false);
        expect(toolbarService.removeAliasByKey).toHaveBeenCalledWith(key);
    });


    it('toolbarService will add actions with icon image and icon font', function() {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolBar1');

        toolbarService.addItems([{
            key: 'key1',
            i18nKey: 'i18nKey1',
            type: 'ACTION',
            iconClassName: 'testIconClass',
            callback: function() {}
        }, {
            key: 'key2',
            i18nKey: 'i18nKey2',
            type: 'ACTION',
            icons: ['icon-file-name'],
            section: 'right',
            callback: function() {}
        }, {
            key: 'key3',
            i18nKey: 'i18nKey1',
            type: 'ACTION',
            callback: function() {},
            section: 'left'
        }, {
            key: 'key4',
            i18nKey: 'i18nKey1',
            type: 'HYBRID_ACTION',
            callback: function() {},
            icons: ['icon-file-name'],
            section: 'left'
        }]);

        expect(toolbarService.aliases[0].type).toBe('ACTION');
        expect(toolbarService.aliases[0].iconClassName).toBe('testIconClass');
        expect(toolbarService.aliases[0].section).toBe('left');
        expect(toolbarService.aliases[1].type).toBe('ACTION');
        expect(toolbarService.aliases[1].icons[0]).toBe('icon-file-name');
        expect(toolbarService.aliases[1].section).toBe('right');
        expect(toolbarService.aliases[2].section).toBe('left');
        expect(toolbarService.aliases[3].type).toBe('HYBRID_ACTION');
        expect(toolbarService.aliases[3].icons[0]).toBe('icon-file-name');
    });
});
