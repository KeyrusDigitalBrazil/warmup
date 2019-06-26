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
describe('resource not found validation error interceptor', function() {
    var LANGUAGE_RESOURCE_URI = '/cmswebservices/v1/sites/:siteUID/languages';
    var alertService;
    var resourceNotFoundErrorInterceptor;

    beforeEach(angular.mock.module('resourceNotFoundErrorInterceptorModule', function($provide) {
        alertService = jasmine.createSpyObj('alertService', ['showDanger']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(inject(function(_resourceNotFoundErrorInterceptor_) {
        resourceNotFoundErrorInterceptor = _resourceNotFoundErrorInterceptor_;
    }));

    function assertPredicate(responseMockData, truthy) {
        // GIVEN
        var mockResponse = {
            config: {
                method: responseMockData.method,
                url: responseMockData.url || '/any_url'
            },
            status: responseMockData.status,
            headers: function(header) {
                if (header === 'Content-type') {
                    return responseMockData.contentType;
                }
            }
        };

        // WHEN
        var matchPredicate = resourceNotFoundErrorInterceptor.predicate(mockResponse);

        // THEN
        expect(matchPredicate).toBe(truthy);
    }

    it('should match predicate for a GET xhr request with a 404 response code and text/json as Content-type', function() {
        assertPredicate({
            method: 'GET',
            status: 404,
            contentType: 'text/json'
        }, true);
    });

    it('should not match predicate for a GET xhr request with a 404 response code and text/html as Content-type', function() {
        assertPredicate({
            method: 'GET',
            status: 404,
            contentType: 'text/html'
        }, false);
    });

    it('should not match predicate for a POST xhr request with a 404 response code and text/json as Content-type', function() {
        assertPredicate({
            method: 'POST',
            status: 404,
            contentType: 'text/json'
        }, true);
    });

    it('should not match predicate for a PUT xhr request with a 404 response code and text/json as Content-type', function() {
        assertPredicate({
            method: 'PUT',
            status: 404,
            contentType: 'text/json'
        }, true);
    });

    it('should not match predicate for a DELETE xhr request with a 404 response code and text/json as Content-type', function() {
        assertPredicate({
            method: 'DELETE',
            status: 404,
            contentType: 'text/json'
        }, true);
    });

    it('should not match predicate for a GET xhr request with a 404 response code to a language resource uri', function() {
        assertPredicate({
            method: 'GET',
            status: 404,
            contentType: 'text/json',
            url: LANGUAGE_RESOURCE_URI
        }, false);
    });

    it('should display error message in alert during 10 seconds and reject the promise', function() {
        // GIVEN
        var mockResponse = {
            config: {
                method: 'GET',
                url: '/any_url'
            },
            status: 404,
            headers: function(header) {
                if (header === 'Content-type') {
                    return 'text/json';
                }
            },
            message: 'any error message'
        };

        // WHEN
        var promise = resourceNotFoundErrorInterceptor.responseError(mockResponse);

        // THEN
        expect(alertService.showDanger).toHaveBeenCalledWith({
            message: mockResponse.message,
            timeout: 10000
        });
        expect(promise).toBeRejectedWithData(mockResponse);
    });
});
