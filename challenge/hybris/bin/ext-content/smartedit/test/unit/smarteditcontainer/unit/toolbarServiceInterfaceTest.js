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
describe('outer toolbarInterfaceModule', function() {

    var $rootScope, $log, ToolbarServiceInterface;

    beforeEach(angular.mock.module('toolbarInterfaceModule'));
    beforeEach(inject(function(_$rootScope_, _ToolbarServiceInterface_, _$log_) {
        $rootScope = _$rootScope_;
        ToolbarServiceInterface = _ToolbarServiceInterface_;
        $log = _$log_;
    }));

    it('ToolbarServiceInterface declares the expected set of empty functions', function() {
        expect(ToolbarServiceInterface.prototype.addAliases).toBeEmptyFunction();
        expect(ToolbarServiceInterface.prototype.removeItemByKey).toBeEmptyFunction();
        expect(ToolbarServiceInterface.prototype.removeAliasByKey).toBeEmptyFunction();
        expect(ToolbarServiceInterface.prototype.addItemsStyling).toBeEmptyFunction();
        expect(ToolbarServiceInterface.prototype.triggerActionOnInner).toBeEmptyFunction();
    });

    it('ToolbarServiceInterface.addItems converts actions into aliases (key-callback mapping of actions) before appending them by means of addAliases', function() {

        var toolbarService = new ToolbarServiceInterface();
        toolbarService.actions = {};
        toolbarService.aliases = [];

        spyOn(toolbarService, 'addAliases').and.callThrough();
        spyOn(toolbarService, 'getAliases').and.callThrough();

        var callback1 = function() {};
        var callback2 = function() {};

        expect(toolbarService.getAliases()).toEqualData([]);

        // Execution
        toolbarService.addItems([{
            key: 'key1',
            nameI18nKey: 'somenameI18nKey1',
            descriptionI18nKey: 'somedescriptionI18nKey1',
            callback: callback1,
            icons: 'icons1',
            type: 'type1',
            include: 'include1'
        }]);

        toolbarService.addItems([{
            key: 'key2',
            nameI18nKey: 'somenameI18nKey2',
            descriptionI18nKey: 'somedescriptionI18nKey2',
            callback: callback2,
            icons: 'icons2',
            type: 'type2',
            include: 'include2'
        }]);

        // Tests
        expect(toolbarService.addAliases.calls.argsFor(0)[0]).toEqualData([{
            key: 'key1',
            name: 'somenameI18nKey1',
            description: 'somedescriptionI18nKey1',
            icons: 'icons1',
            type: 'type1',
            include: 'include1',
            priority: 500,
            section: 'left',
            isOpen: false,
            keepAliveOnClose: false
        }]);


        expect(toolbarService.getItems()).toEqualData({
            'key1': callback1
        });

        expect(toolbarService.addAliases.calls.argsFor(1)[0]).toEqualData([{
            key: 'key2',
            name: 'somenameI18nKey2',
            description: 'somedescriptionI18nKey2',
            icons: 'icons2',
            type: 'type2',
            include: 'include2',
            priority: 500,
            section: 'left',
            isOpen: false,
            keepAliveOnClose: false
        }]);


        expect(toolbarService.getItems()).toEqualData({
            'key1': callback1,
            'key2': callback2
        });
    });

    it('addItems logs an error when key is not provided in the configuration', function() {
        // Arrange
        var toolbarService = new ToolbarServiceInterface();

        spyOn($log, 'error');
        spyOn(toolbarService, 'addAliases');

        var callbacks = {
            callback1: function() {}
        };

        // Act
        toolbarService.addItems([{
            callback: callbacks.callback1
        }]);

        // Assert
        expect(toolbarService.addAliases).not.toHaveBeenCalled();
        expect($log.error).toHaveBeenCalledWith('addItems() - Cannot add action without key.');
    });

});
