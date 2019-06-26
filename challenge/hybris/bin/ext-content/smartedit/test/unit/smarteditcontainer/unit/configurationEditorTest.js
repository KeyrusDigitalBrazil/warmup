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
describe('ConfigurationEditor', function() {

    var $rootScope, $q, editor, editorCRUDService;
    var dataGet = [{
        key: '1',
        value: "{\"location\":\"uri\"}",
        id: '1'
    }, {
        key: 'otherkey',
        value: "{malformed}",
        id: '3'
    }];
    var dataUpdate = {
        key: '1',
        value: '2',
        id: '2'
    };

    var loadConfigManagerService = {
        loadAsArray: function() {
            var deferred = $q.defer();
            deferred.resolve(dataGet);
            return deferred.promise;
        }
    };

    beforeEach(angular.mock.module('loadConfigModule', function($provide) {
        $provide.value('loadConfigManagerService', loadConfigManagerService);
    }));

    beforeEach(angular.mock.module('administrationModule', function($provide) {


        editorCRUDService = jasmine.createSpyObj('resourceMock', ['query', 'update', 'save', 'remove']);
        editorCRUDService.query.and.callFake(function() {
            var deferred = $q.defer();
            deferred.resolve(dataGet);
            return {
                '$promise': deferred.promise
            };
        });
        editorCRUDService.update.and.callFake(function() {
            var deferred = $q.defer();
            deferred.resolve(dataUpdate);
            return {
                '$promise': deferred.promise
            };
        });
        editorCRUDService.save.and.callFake(function() {
            var deferred = $q.defer();
            deferred.resolve(dataUpdate);
            return {
                '$promise': deferred.promise
            };
        });
        editorCRUDService.remove.and.callFake(function() {
            var deferred = $q.defer();
            deferred.resolve();
            return {
                '$promise': deferred.promise
            };
        });
        var resourceFunction = function() {
            return editorCRUDService;
        };

        $provide.value('$resource', resourceFunction);

    }));
    beforeEach(inject(function(_$rootScope_, _$q_, configurationService) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        editor = configurationService;
    }));

    it('ConfigurationEditor initializes with the expected editor CRUD REST service', function() {

        expect(editor.editorCRUDService).toBe(editorCRUDService);

    });

    it('calling reset() set component to prior pristine sate and call $setPristine on the component form', function() {

        var pristine = {
            key: '1',
            value: '2'
        };
        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);

        editor.pristine = pristine;

        editor._reset(configurationForm);
        expect(editor.configuration).not.toBe(pristine);
        expect(editor.configuration).toEqualData(pristine);
        expect(configurationForm.$setPristine).toHaveBeenCalled();

    });

    it('successful loadAndPresent will set pristine state and model to the return a prettified value of the REST call and set errors for non JSON parsable data', function() {

        spyOn(loadConfigManagerService, "loadAsArray").and.callThrough();

        editor.loadAndPresent();
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editor.pristine).toEqualData([{
            key: '1',
            value: '{\n  \"location\": \"uri\"\n}',
            id: '1'
        }, {
            key: 'otherkey',
            value: '{malformed}',
            id: '3',
            errors: {
                values: [{
                    message: 'se.configurationform.json.parse.error'
                }]
            }
        }]);
        expect(editor.configuration).toEqualData([{
            key: '1',
            value: '{\n  \"location\": \"uri\"\n}',
            id: '1'
        }, {
            key: 'otherkey',
            value: '{malformed}',
            id: '3',
            errors: {
                values: [{
                    message: 'se.configurationform.json.parse.error'
                }]
            }
        }]);

        expect(loadConfigManagerService.loadAsArray).toHaveBeenCalled();

    });

    it("delete will tag the entity 'toDelete' and set the form to dirty", function() {

        var configuration = [{
            key: 'a',
            value: '3',
            id: '1'
        }, {
            key: 'b',
            value: '4',
            id: '2'
        }];

        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setDirty']);
        configurationForm.$dirty = true;
        configurationForm.$valid = true;

        editor.removeEntry(configuration[0], configurationForm);

        expect(configuration[0].toDelete).toBe(true);
        expect(configurationForm.$setDirty).toHaveBeenCalled();
    });

    it("delete will filter out the new entry from the configuration list ", function() {

        var configuration = [{
            key: 'a',
            value: '3',
            id: '1',
            isNew: true
        }, {
            key: 'b',
            value: '4',
            id: '2'
        }];

        editor.configuration = configuration;
        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setDirty']);
        configurationForm.$dirty = true;
        configurationForm.$valid = true;

        editor.removeEntry(configuration[0], configurationForm);
        $rootScope.$digest();
        expect(editor.configuration).toEqual([{
            key: 'b',
            value: '4',
            id: '2'
        }]);
    });

    it('submit will do nothing if configurationForm is not dirty', function() {

        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);
        configurationForm.$dirty = false;
        configurationForm.$valid = true;

        editor.submit(configurationForm);

        expect(editorCRUDService.update).not.toHaveBeenCalled();
        expect(configurationForm.$setPristine).not.toHaveBeenCalled();

    });

    it('submit will do nothing if configurationForm is not valid', function() {

        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);
        configurationForm.$dirty = true;
        configurationForm.$valid = false;

        editor.submit(configurationForm);

        expect(editorCRUDService.update).not.toHaveBeenCalled();
        expect(configurationForm.$setPristine).not.toHaveBeenCalled();

    });

    it('submit will do nothing if duplicate keys are found and errors will be appended', function() {

        var configuration = [{
            key: 'a',
            value: '3',
            id: '1'
        }, {
            key: 'b',
            value: '4',
        }, {
            key: 'a',
            value: '5'
        }];
        editor.configuration = configuration;
        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);
        configurationForm.$dirty = true;
        configurationForm.$valid = true;

        editor.submit(configurationForm);

        $rootScope.$digest();

        expect(editor.configuration).toEqualData([{
            key: 'a',
            value: '3',
            id: '1'
        }, {
            key: 'b',
            value: '4'
        }, {
            key: 'a',
            value: '5',
            errors: {
                keys: [{
                    message: 'se.configurationform.duplicate.entry.error'
                }]
            }
        }]);

        expect(editorCRUDService.save).not.toHaveBeenCalled();
        expect(editorCRUDService.update).not.toHaveBeenCalled();
        expect(configurationForm.$setPristine).not.toHaveBeenCalled();

    });


    it("submit will call remove if 'toDelete', update if isNew not present and save if isNew is present, and send error if value is not JSON parsable", function() {

        var configuration = [{
            key: 'a',
            value: '3'
        }, {
            key: 'b',
            value: '4',
            isNew: true
        }, {
            key: 'c',
            value: '5',
            toDelete: true
        }, {
            key: 'otherkey',
            value: "{malformed}"
        }];
        editor.configuration = configuration;
        spyOn(editor, 'loadAndPresent').and.returnValue();
        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);
        configurationForm.$dirty = true;
        configurationForm.$valid = true;

        editor.submit(configurationForm);
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorCRUDService.update).toHaveBeenCalledWith({
            key: 'a'
        }, {
            key: 'a',
            value: '3',
            secured: false
        });
        expect(editorCRUDService.save).toHaveBeenCalledWith({}, {
            key: 'b',
            value: '4',
            secured: false
        });
        expect(editorCRUDService.remove).toHaveBeenCalledWith({
            key: 'c'
        }, undefined);

        expect(editor.configuration).toEqualData([{
            key: 'a',
            value: '3',
            hasErrors: false
        }, {
            key: 'b',
            value: '4',
            hasErrors: false
        }, {
            key: 'otherkey',
            value: '{malformed}',
            errors: {
                values: [{
                    message: 'se.configurationform.json.parse.error'
                }]
            },
            hasErrors: true
        }]);
    });


    it('WHEN submit is called with empty key and empty value in the configuration field THEN the editor will respond with error message for both key and value and expect save and update of the editorCRUDService not to be called', function() {

        var configuration = [{
            key: '',
            value: '',
        }, {
            key: 'b',
            value: '4',
            isNew: true
        }, {
            key: 'c',
            value: '5'
        }];
        editor.configuration = configuration;
        spyOn(editor, 'loadAndPresent').and.returnValue();
        var configurationForm = jasmine.createSpyObj('configurationForm', ['$setPristine']);
        configurationForm.$dirty = true;
        configurationForm.$valid = false;
        configurationForm.$invalid = true;

        editor.submit(configurationForm);
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editor.configuration).toEqualData([{
            key: '',
            value: '',
            errors: {
                keys: [{
                    message: 'se.configurationform.required.entry.error'
                }],
                values: [{
                    message: 'se.configurationform.required.entry.error'
                }]
            },
            hasErrors: true
        }, {
            key: 'b',
            value: '4',
            isNew: true
        }, {
            key: 'c',
            value: '5',
        }]);

        expect(editorCRUDService.save).not.toHaveBeenCalled();
        expect(editorCRUDService.update).not.toHaveBeenCalled();
        expect(configurationForm.$setPristine).not.toHaveBeenCalled();

    });

    it('WHEN _validateUserInput is called with a valid entry value THEN it will set the entry.requiresUserCheck only if the user entered an absolute URL', function() {
        // Arrange
        var entryAbsoluteURL1 = {
            value: '"http://url.js"'
        };
        var entryAbsoluteURL2 = {
            value: '"https://url.js"'
        };
        var entryAbsoluteURL3 = {
            value: '{' +
                '"smartEditLocation": "http://cmssmartedit/cmssmartedit/js/cmssmartedit.js"' +
                '}'
        };
        var entryRelativeURL1 = {
            value: '"something else"'
        };
        var entryRelativeURL2 = {
            value: '"/path/to/file.js"'
        };
        var entryRelativeURL3 = {
            value: '{' +
                '"smartEditLocation": "/cmssmartedit/cmssmartedit/js/cmssmartedit.js"' +
                '}'
        };

        // Act/Assert

        editor._validateUserInput(entryAbsoluteURL1);
        expect(entryAbsoluteURL1.requiresUserCheck).toBe(true);

        editor._validateUserInput(entryAbsoluteURL2);
        expect(entryAbsoluteURL2.requiresUserCheck).toBe(true);

        editor._validateUserInput(entryAbsoluteURL3);
        expect(entryAbsoluteURL3.requiresUserCheck).toBe(true);

        editor._validateUserInput(entryRelativeURL1);
        expect(entryRelativeURL1.requiresUserCheck).toBe(false);

        editor._validateUserInput(entryRelativeURL2);
        expect(entryRelativeURL2.requiresUserCheck).toBe(false);

        editor._validateUserInput(entryRelativeURL3);
        expect(entryRelativeURL3.requiresUserCheck).toBe(false);
    });

    it('WHEN _validate is called THEN it will throw an exception if the entry had to be checked by the user but it wasn not', function() {
        // Arrange
        var validEntry1 = {
            requiresUserCheck: false,
            value: '"some json"'
        };
        var validEntry2 = {
            requiresUserCheck: true,
            isCheckedByUser: true,
            value: '"some json"'
        };
        var invalidEntry1 = {
            requiresUserCheck: true,
            isCheckedByUser: false,
            value: '"some json"'
        };

        // Act/Assert

        expect(function() {
            editor._validate(validEntry1);
        }).not.toThrow();
        expect(function() {
            editor._validate(validEntry2);
        }).not.toThrow();
        expect(function() {
            editor._validate(invalidEntry1);
        }).toThrow();
    });

});
