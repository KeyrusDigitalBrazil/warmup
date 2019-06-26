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
describe('decoratorServiceModule', function() {

    var $q, $rootScope, decoratorService;

    beforeEach(angular.mock.module('decoratorServiceModule'));
    beforeEach(inject(function(_decoratorService_, _$q_, _$rootScope_) {
        decoratorService = _decoratorService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    function setupAsyncOnGetDecoratorsForComponent(componentType, componentId) {
        var promise = decoratorService.getDecoratorsForComponent(componentType, componentId);
        var promiseThen = jasmine.createSpy('success');
        promise.then(promiseThen);
        $rootScope.$digest();
        return promiseThen;
    }

    it('getDecoratorsForComponent will retain a unique set of decorators for a given type', function() {

        decoratorService._activeDecorators = {
            'decorator0': {},
            'decorator1': {},
            'decorator2': {},
            'decorator3': {}
        };
        decoratorService.addMappings({
            'type1': ['decorator1', 'decorator2'],
            'type2': ['decorator0'],
        });
        decoratorService.addMappings({
            'type1': ['decorator2', 'decorator3'],
        });

        var promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2', 'decorator3']);
    });

    it('getDecoratorsForComponent will remove decorator from activeDecorators when displayCondtion function returns false', function() {

        //GIVEN
        var deferred = $q.defer();
        deferred.resolve(false);
        decoratorService._activeDecorators = {
            'decorator0': {
                displayCondition: function() {
                    return deferred.promise;
                }
            },
            'decorator1': {},
            'decorator2': {}
        };
        decoratorService.addMappings({
            'type1': ['decorator0', 'decorator1', 'decorator2']
        });

        //WHEN
        var promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        //THEN
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2']);
    });

    it('activeDecorator.displayConditon should be called with parameters componentType and componentId', function() {

        //GIVEN
        var deferred = $q.defer();
        deferred.resolve(true);
        var decorator0 = {
            displayCondition: function() {
                return deferred.promise;
            }
        };
        spyOn(decorator0, 'displayCondition').and.callThrough();

        decoratorService._activeDecorators = {
            'decorator0': decorator0
        };

        decoratorService.addMappings({
            'type1': ['decorator0']
        });
        expect(decorator0.displayCondition).not.toHaveBeenCalled();

        //WHEN
        decoratorService.getDecoratorsForComponent('type1', 'id123');

        $rootScope.$digest();
        //THEN
        expect(decorator0.displayCondition).toHaveBeenCalledWith('type1', 'id123');
    });

    it('activeDecorator.displayCondition should log error if displayCondition does not return boolean or not a function', function() {

        var decorator0 = {
            displayCondition: "I am not a function"
        };

        decoratorService._activeDecorators = {
            'decorator0': decorator0
        };
        decoratorService.addMappings({
            'type1': ['decorator0']
        });

        expect(function() {
            decoratorService.getDecoratorsForComponent('type1');
        }).toThrow(new Error("The active decorator's displayCondition property must be a function and must return a boolean"));
    });

    it('getDecoratorsForComponent will retain a unique set of decorators from all matching regexps', function() {

        decoratorService._activeDecorators = {
            'decorator1': {},
            'decorator2': {},
            'decorator3': {},
            'decorator4': {},
            'decorator5': {},
            'decorator6': {}
        };
        decoratorService.addMappings({
            '*Suffix': ['decorator1', 'decorator2'],
            '.*Suffix': ['decorator2', 'decorator3'],
            'TypeSuffix': ['decorator3', 'decorator4'],
            '^((?!Middle).)*$': ['decorator4', 'decorator5'],
            'PrefixType': ['decorator5', 'decorator6'],
        });

        var promiseThen = setupAsyncOnGetDecoratorsForComponent('TypeSuffix');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2', 'decorator3', 'decorator4', 'decorator5']);

        promiseThen = setupAsyncOnGetDecoratorsForComponent('TypeSuffixes');
        expect(promiseThen).toHaveBeenCalledWith(['decorator2', 'decorator3', 'decorator4', 'decorator5']);

        promiseThen = setupAsyncOnGetDecoratorsForComponent('MiddleTypeSuffix');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2', 'decorator3']);

    });

    xit('getDecoratorsForComponent decorator callback function to remove decorator from active components', function() {

        decoratorService._activeDecorators = ['decorator1', 'decorator2'];
        decoratorService.addMappings({
            'type1': ['decorator1', 'decorator2']
        });

        expect(decoratorService.getDecoratorsForComponent('type1')).toEqual(['decorator1']);
    });


    it('enable adds decorators to the Array of active decorators and can be invoked multiple times', function() {

        var displayCondition = {
            'displayCondition': undefined
        };
        expect(decoratorService._activeDecorators).toEqual({});
        decoratorService.enable('key1');
        expect(decoratorService._activeDecorators).toEqual({
            'key1': displayCondition
        });
        decoratorService.enable('key2');
        expect(decoratorService._activeDecorators).toEqual({
            'key1': displayCondition,
            'key2': displayCondition
        });
        decoratorService.enable('key1');
        expect(decoratorService._activeDecorators).toEqual({
            'key1': displayCondition,
            'key2': displayCondition
        });
    });

    it('disable removes decorators from the Array of active decorators and can be invoked multiple times', function() {

        var displayCondition = {
            'displayCondition': undefined
        };
        decoratorService._activeDecorators = {
            'key1': displayCondition,
            'key2': displayCondition,
            'key3': displayCondition
        };
        decoratorService.disable('key1');
        expect(decoratorService._activeDecorators).toEqual({
            'key2': displayCondition,
            'key3': displayCondition
        });
        decoratorService.disable('key2');
        expect(decoratorService._activeDecorators).toEqual({
            'key3': displayCondition
        });
        decoratorService.disable('key1');
        expect(decoratorService._activeDecorators).toEqual({
            'key3': displayCondition
        });
    });

    it('getDecoratorsForComponent will filter based on enabled activeDecorators', function() {

        decoratorService.addMappings({
            'type1': ['decorator1', 'decorator2'],
        });

        var promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith([]);

        decoratorService.enable('decorator1');

        promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1']);

        decoratorService.enable('decorator2');

        promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2']);

        decoratorService.enable('decorator3');

        promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith(['decorator1', 'decorator2']);

    });

    it('getDecoratorsForComponent will filter based on disabled activeDecorators', function() {

        decoratorService.addMappings({
            'type1': ['decorator1', 'decorator2'],
        });

        decoratorService.enable('decorator2');

        var promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith(['decorator2']);

        decoratorService.disable('decorator2');

        promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith([]);

        decoratorService.disable('decorator3');

        promiseThen = setupAsyncOnGetDecoratorsForComponent('type1');
        expect(promiseThen).toHaveBeenCalledWith([]);

    });

});
