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
describe('retry interceptor', function() {
    var $httpBackend;
    var $injector;
    var $q;
    var $timeout;
    var retryInterceptor;
    var operationContextService;

    var CUSTOM_URI = '/ABC.com';
    var CUSTOM_PREDICATE = function(httpObj) {
        return httpObj.status === 500 && httpObj.config.url === CUSTOM_URI;
    };
    var CUSTOM_MOCK_RESPONSE = {
        config: {
            method: 'GET',
            url: '/ABC.com'
        },
        status: 500
    };

    beforeEach(function() {
        angular.mock.module('alertServiceModule');
    });

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        operationContextService = jasmine.createSpyObj('operationContextService', ['findOperationContext', 'register']);
        $provide.value('operationContextService', operationContextService);
    }));

    beforeEach(angular.mock.module('retryInterceptorModule', function($provide) {
        var $translate = jasmine.createSpyObj('$translate', ['instant']);
        $provide.value("$translate", $translate);
    }));

    beforeEach(inject(function(_retryInterceptor_, _$q_, _$timeout_, _$injector_, _$httpBackend_) {
        $httpBackend = _$httpBackend_;
        $injector = _$injector_;
        $q = _$q_;
        $timeout = _$timeout_;
        retryInterceptor = _retryInterceptor_;
    }));

    it('should throw an error if trying to register without passing a predicate function', function() {
        var expectedErrorFunction = function() {
            retryInterceptor.register();
        };
        expect(expectedErrorFunction).toThrowError('retryInterceptor.register error: predicate must be a function');
    });

    it('should throw an error if trying to register without passing a strategyHolder function', function() {
        var expectedErrorFunction = function() {
            retryInterceptor.register(CUSTOM_PREDICATE);
        };
        expect(expectedErrorFunction).toThrowError('retryInterceptor.register error: strategyHolder must be a function');
    });

    it('should match predicate if there is one predicate/strategyHolder matches', function() {
        var MockStrategyHolder = function() {};
        retryInterceptor.register(CUSTOM_PREDICATE, MockStrategyHolder);

        expect(retryInterceptor.predicate(CUSTOM_MOCK_RESPONSE)).toBe(true);
    });

    it('should match predicate if there is one predicate/strategyHolder with an operation context matches', function() {
        operationContextService.findOperationContext.and.returnValue('TOOLING');

        var predicate = function(response, operationContext) {
            return operationContext === 'TOOLING';
        };

        var MockStrategyHolder = function() {};
        retryInterceptor.register(predicate, MockStrategyHolder);

        expect(retryInterceptor.predicate(CUSTOM_MOCK_RESPONSE)).toBe(true);
    });

    it('should be able to chain the register function', function() {
        expect(retryInterceptor
                .register(CUSTOM_PREDICATE, angular.noop)
                .register(function() {
                    return true;
                }, angular.noop))
            .toEqual(retryInterceptor);
    });

    it('should be able to register a predicate/strategyHolder and handle a reponse error that match the given predicate', function() {
        var MockStrategyHolder = function() {
            this.canRetry = function() {
                return true;
            };
            this.calculateNextDelay = function() {
                return 1500;
            };
        };
        retryInterceptor.register(CUSTOM_PREDICATE, MockStrategyHolder);
        var mockResponse = {
            config: {
                method: 'GET',
                url: CUSTOM_URI
            },
            status: 500
        };

        var expectedResponse = angular.copy(mockResponse);
        expectedResponse.config.retryStrategy = new MockStrategyHolder();

        var finalResponse = {
            mockValue: 1
        };
        $httpBackend.expectGET(mockResponse.config.url).respond(finalResponse);

        retryInterceptor.responseError(mockResponse).then(function(success) {
            expect(success.data).toEqual(finalResponse);
        }, function(error) {
            expect(error).fail('the request should have been successful');
        });

        $timeout.flush();
        $httpBackend.flush();
    });

    it('should be able to handle a request error when the reponse error config already has an existing retryStrategy instance', function() {
        var MockStrategyHolder = function() {
            this.attemptCount = 0;
            this.canRetry = function() {
                return true;
            };
            this.calculateNextDelay = function() {
                return 2000;
            };
        };
        var retryStrategy = new MockStrategyHolder();

        var mockResponse = {
            config: {
                method: 'GET',
                url: CUSTOM_URI,
                retryStrategy: retryStrategy
            },
            status: 500
        };
        var retryStrategyCanRetrySpy = spyOn(retryStrategy, 'canRetry').and.callThrough();
        var retryStrategyCalculateSpy = spyOn(retryStrategy, 'calculateNextDelay').and.callThrough();

        var finalResponse = {
            mockValue: 1
        };
        $httpBackend.expectGET(mockResponse.config.url).respond(finalResponse);

        retryInterceptor.responseError(mockResponse).then(function(success) {
            expect(success.data).toEqual(finalResponse);
        }, function(error) {
            expect(error).fail('the request should have been successful');
        });

        expect(retryStrategyCanRetrySpy).toHaveBeenCalled();
        expect(retryStrategyCalculateSpy).toHaveBeenCalled();
        expect(retryStrategy.attemptCount).toEqual(1);

        $timeout.flush();
        $httpBackend.flush();
    });
});
