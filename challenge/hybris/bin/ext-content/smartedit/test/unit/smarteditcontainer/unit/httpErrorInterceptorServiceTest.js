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
describe('http error interceptor service', function() {
    var $httpProvider;
    var $injector;
    var $q;
    var $rootScope;
    var httpErrorInterceptor;
    var httpErrorInterceptorService;
    var ERROR_COUNTER = 0;

    beforeEach(angular.mock.module('interceptorHelperModule'));

    beforeEach(angular.mock.module('httpErrorInterceptorServiceModule', function($provide, _$httpProvider_) {
        $httpProvider = _$httpProvider_;

        $provide.value('customErrorInterceptor', {
            predicate: function() {
                return true;
            },
            responseError: function(response) {
                return $q.when(response);
            }
        });

        ERROR_COUNTER = 0;
    }));

    beforeEach(inject(function(_$q_, _httpErrorInterceptor_, _httpErrorInterceptorService_, _$rootScope_, _$injector_) {
        $q = _$q_;
        httpErrorInterceptor = _httpErrorInterceptor_;
        httpErrorInterceptorService = _httpErrorInterceptorService_;
        $rootScope = _$rootScope_;
        $injector = _$injector_;
    }));

    function getErrorInterceptorMock(rejectPromise) {
        return {
            predicate: function(response) {
                return response.status === 400;
            },
            responseError: function(response) {
                // mutating the response error
                response.data.errors.push(++ERROR_COUNTER);
                if (rejectPromise) {
                    return $q.reject(response);
                } else {
                    return $q.when(response);
                }
            }
        };
    }

    it('$httpProvider should be loaded with only one interceptor', function() {
        expect($httpProvider.interceptors).toContain('httpErrorInterceptor');
    });

    it('should call httpErrorInterceptorService.responseError when an error is intercepted', function() {
        var responseErrorSpy = spyOn(httpErrorInterceptorService, 'responseError').and.callThrough();

        //GIVEN
        var RESPONSE_MOCK = {
            config: {
                url: '/any_url'
            },
            status: 400
        };

        //WHEN
        var responseErrorPromise = httpErrorInterceptor.responseError(RESPONSE_MOCK);
        $rootScope.$digest();

        //THEN
        expect(responseErrorPromise).toBeRejectedWithData(RESPONSE_MOCK);
        expect(responseErrorSpy).toHaveBeenCalledWith(RESPONSE_MOCK);
    });

    it('should be able to register interceptors', function() {
        //WHEN
        var errorInterceptorMock1 = getErrorInterceptorMock(true);
        var errorInterceptorMock2 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(errorInterceptorMock1);
        httpErrorInterceptorService.addInterceptor(errorInterceptorMock2);

        //THEN
        expect(httpErrorInterceptorService._errorInterceptors.length).toEqual(2);
        expect(httpErrorInterceptorService._errorInterceptors).toEqual([errorInterceptorMock2, errorInterceptorMock1]);
    });

    it('should be able to register an interceptor with angular recipe', function() {
        httpErrorInterceptorService.addInterceptor('customErrorInterceptor');

        //THEN
        expect(httpErrorInterceptorService._errorInterceptors.length).toEqual(1);
        expect(httpErrorInterceptorService._errorInterceptors[0]).toEqual($injector.get('customErrorInterceptor'));
    });

    it('should throw an error if trying to register an interceptor which does not expose a predicate function', function() {
        //GIVEN
        var expectedErrorFunction = function() {
            httpErrorInterceptorService.addInterceptor({
                responseError: angular.noop
            });
        };

        //THEN
        expect(expectedErrorFunction).toThrowError('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.predicate');
    });

    it('should throw an error if trying to register an interceptor which does not expose a responseError function', function() {
        //GIVEN
        var expectedErrorFunction = function() {
            httpErrorInterceptorService.addInterceptor({
                predicate: angular.noop
            });
        };

        //THEN
        expect(expectedErrorFunction).toThrowError('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.responseError');
    });

    it('should be able to unregister an interceptor', function() {
        //GIVEN
        var interceptor1 = getErrorInterceptorMock(true);
        interceptor1.id = 1;

        var interceptor2 = getErrorInterceptorMock(true);
        interceptor2.id = 2;

        var unregisterErrorInterceptor1 = httpErrorInterceptorService.addInterceptor(interceptor1);
        httpErrorInterceptorService.addInterceptor(interceptor2);

        //WHEN
        unregisterErrorInterceptor1();

        //THEN
        expect(httpErrorInterceptorService._errorInterceptors).toEqual([interceptor2]);
    });

    it('should reject the response error promise when there is no interceptors available', function() {
        //GIVEN
        var RESPONSE_MOCK = {
            status: 400,
            data: {
                errors: []
            }
        };

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError(RESPONSE_MOCK);

        //THEN
        expect(responseErrorPromise).toBeRejectedWithData(RESPONSE_MOCK);
    });

    it('should reject the responseError promise when no predicate matches the response', function() {
        //GIVEN
        var RESPONSE_STATUS_500_MOCK = {
            status: 500,
            data: {
                errors: []
            }
        };
        // The Interceptor listen only on response.status '400'
        var errorStatus400InterceptorMock = getErrorInterceptorMock(true);
        httpErrorInterceptorService.addInterceptor(errorStatus400InterceptorMock);

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError(RESPONSE_STATUS_500_MOCK);

        //THEN
        expect(responseErrorPromise).toBeRejectedWithData(RESPONSE_STATUS_500_MOCK);
    });

    it('should reject the responseError promise with expected data if all interceptors reject the response', function() {
        //GIVEN
        var RESPONSE_MOCK = {
            status: 400,
            data: {
                errors: []
            }
        };

        // getErrorInterceptorMock function mutate the response error array
        var expectedResponse = angular.copy(RESPONSE_MOCK);
        expectedResponse.data.errors = [1, 2];

        var interceptorMock1 = getErrorInterceptorMock(true);
        var interceptorMock2 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(interceptorMock1);
        httpErrorInterceptorService.addInterceptor(interceptorMock2);

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError(RESPONSE_MOCK);
        $rootScope.$digest();

        //THEN
        expect(responseErrorPromise).toBeRejectedWithData(expectedResponse);
    });

    it('should resolve the responseError promise with expected data if one interceptor resolve the response and should not call subsequent interceptors', function() {
        //GIVEN
        var interceptorMock1 = getErrorInterceptorMock(true);
        var interceptorMock2 = getErrorInterceptorMock(false);
        var interceptorMock3 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(interceptorMock1);
        httpErrorInterceptorService.addInterceptor(interceptorMock2);
        httpErrorInterceptorService.addInterceptor(interceptorMock3);

        // last interceptor to be called is the first added
        var lastInterceptorResponseErrorSpy = spyOn(interceptorMock1, 'responseError');

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError({
            status: 400,
            data: {
                errors: []
            }
        });
        $rootScope.$digest();

        //THEN
        expect(responseErrorPromise).toBeResolvedWithData({
            status: 400,
            data: {
                errors: [1, 2] // getErrorInterceptorMock function mutate the response error array
            }
        });
        expect(lastInterceptorResponseErrorSpy).not.toHaveBeenCalled();
    });

    it('should resolve the responseError promise if one interceptor resolve the response', function() {
        //GIVEN
        var RESPONSE_MOCK = {
            config: {
                url: '/any_url'
            },
            status: 400,
            data: {
                errors: []
            }
        };
        // getErrorInterceptorMock function mutate the response error array
        var expectedResponse = angular.copy(RESPONSE_MOCK);
        expectedResponse.data.errors = [1];

        var errorInterceptorMock = getErrorInterceptorMock(false);
        httpErrorInterceptorService.addInterceptor(errorInterceptorMock);

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError(RESPONSE_MOCK);

        //THEN
        expect(responseErrorPromise).toBeResolvedWithData(expectedResponse);
    });

    it('should resolve the responseError promise if adding a interceptor with angular recipe resolve the response', function() {
        //GIVEN
        var RESPONSE_MOCK = {
            config: {
                url: '/any_url'
            },
            status: 400,
            data: {
                errors: []
            }
        };
        httpErrorInterceptorService.addInterceptor('customErrorInterceptor');

        //WHEN
        var responseErrorPromise = httpErrorInterceptorService.responseError(RESPONSE_MOCK);

        //THEN
        expect(responseErrorPromise).toBeResolvedWithData(RESPONSE_MOCK);
    });

});
