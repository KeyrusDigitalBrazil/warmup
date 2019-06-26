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
describe('loadConfigurationService', function() {

    var $rootScope, $q, LoadConfigManager, editorViewService, sharedDataService;
    var dataGet;

    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['set']);
        $provide.value('sharedDataService', sharedDataService);
    }));

    beforeEach(angular.mock.module('loadConfigModule', function($provide) {
        dataGet = [{
            key: '1',
            value: "{\"location\":\"uri\"}",
            id: '1'
        }, {
            key: 'otherkey',
            value: "{malformed\"key\":\"value\"}",
            id: '2'
        }];

        editorViewService = jasmine.createSpyObj('resourceMock', ['query']);
        editorViewService.query.and.callFake(function() {
            var deferred = $q.defer();
            deferred.resolve(dataGet);
            return {
                '$promise': deferred.promise
            };
        });

        var resourceFunction = function() {
            return editorViewService;
        };

        $provide.value('$resource', resourceFunction);
    }));

    beforeEach(inject(function(_$rootScope_, _$q_, _LoadConfigManager_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        LoadConfigManager = _LoadConfigManager_;
    }));

    it('LoadConfigManager initializes with the expected editor View REST service', function() {

        var editor = new LoadConfigManager();
        expect(editor.editorViewService).toBe(editorViewService);

    });

    it('successful loadAsArray will load and only parse stringified JSON content from REST call response', function() {

        var editor = new LoadConfigManager();

        editor.loadAsArray().then(function(response) {
            expect(response).toEqualData([{
                key: '1',
                value: '{\"location\":\"uri\"}',
                id: '1'
            }, {
                key: 'otherkey',
                value: '{malformed"key":"value"}',
                id: '2'
            }]);
        }, function() {
            expect().fail();
        });
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorViewService.query).toHaveBeenCalled();

    });

    it('successful loadAsObject will only load and parse stringified JSON content and calculate domain and smarteditroot', function() {

        sharedDataService.set.and.returnValue($q.when());
        var editor = new LoadConfigManager();
        spyOn(editor, '_getLocation').and.returnValue("somedomain/smartedit");
        editor.loadAsObject().then(function(response) {
            expect(response).toEqualData({
                1: {
                    location: 'uri'
                },
                domain: 'somedomain',
                smarteditroot: 'somedomain/smartedit'
            });
        }, function() {
            expect().fail();
        });
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorViewService.query).toHaveBeenCalled();
        expect(sharedDataService.set).toHaveBeenCalledWith('configuration', {
            1: {
                location: 'uri'
            },
            domain: 'somedomain',
            smarteditroot: 'somedomain/smartedit'
        });
        expect(editor._getLocation).toHaveBeenCalled();

    });
    it('successful loadAsObject will only load and parse stringified JSON content and convert it to object and share it through sharedDataService', function() {

        dataGet = [{
            key: '1',
            value: "{\"location\":\"uri\"}",
            id: '1'
        }, {
            key: 'domain',
            value: "\"somedomain\"",
            id: '2'
        }];

        sharedDataService.set.and.returnValue($q.when());
        var editor = new LoadConfigManager();

        spyOn(editor, '_getLocation').and.returnValue("http://domain/smartedit");
        editor.loadAsObject().then(function(response) {
            expect(response).toEqualData({
                1: {
                    location: 'uri'
                },
                domain: 'http://domain',
                smarteditroot: 'http://domain/smartedit'
            });
        }, function() {
            expect().fail();
        });
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorViewService.query).toHaveBeenCalled();
        expect(sharedDataService.set).toHaveBeenCalledWith('configuration', {
            1: {
                location: 'uri'
            },
            domain: 'http://domain',
            smarteditroot: 'http://domain/smartedit'
        });

    });

});
