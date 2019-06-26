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
describe('preview resource error interceptor', function() {
    var $http;
    var $q;
    var previewErrorInterceptor;
    var iframeManagerService;
    var PREVIEW_RESOURCE_URI = '/previewwebservices/v1/preview';
    var sharedDataService;

    beforeEach(angular.mock.module('previewErrorInterceptorModule', function($provide) {
        iframeManagerService = jasmine.createSpyObj('iframeManagerService', ['setCurrentLocation']);
        $provide.value('iframeManagerService', iframeManagerService);

        $http = jasmine.createSpy('$http');
        $provide.value('$http', $http);

        sharedDataService = jasmine.createSpyObj('sharedDataService', ['update']);
        $provide.value("sharedDataService", sharedDataService);
    }));

    beforeEach(angular.mock.module('resourceLocationsModule', function($provide) {
        $provide.value(PREVIEW_RESOURCE_URI, PREVIEW_RESOURCE_URI);
    }));

    beforeEach(inject(function(_previewErrorInterceptor_, _$q_) {
        previewErrorInterceptor = _previewErrorInterceptor_;
        $q = _$q_;
    }));

    it('should match predicate for a xhr request to preview resource uri that returns a 400 response code with a pageId and an unknow identifier error type', function() {
        // GIVEN
        var mockResponse = {
            config: {
                method: 'GET',
                url: PREVIEW_RESOURCE_URI,
                data: {
                    pageId: 1
                }
            },
            data: {
                errors: [{
                    type: 'UnknownIdentifierError'
                }]
            },
            status: 400
        };

        // WHEN
        var matchPredicate = previewErrorInterceptor.predicate(mockResponse);

        // THEN
        expect(matchPredicate).toBe(true);
    });

    it('should not match predicate for a xhr request to a non preview resource uri with a 400 response code', function() {
        // GIVEN
        var mockResponse = {
            config: {
                method: 'GET',
                url: '/any_url',
                data: {}
            },
            data: {},
            status: 400
        };

        // WHEN
        var matchPredicate = previewErrorInterceptor.predicate(mockResponse);

        // THEN
        expect(matchPredicate).toBe(false);
    });

    it('should set iframeManagerService current location to null for a match predicate', function() {
        var mockResponse = {
            config: {
                method: 'GET',
                url: PREVIEW_RESOURCE_URI,
                data: {
                    pageId: 1
                }
            },
            data: {
                errors: [{
                    type: 'UnknownIdentifierError'
                }]
            },
            status: 400
        };
        var finalResponse = {
            mockValue: 1
        };

        var experience = {
            pageId: 'pageId',
            bla: 'bli'
        };

        sharedDataService.update.and.returnValue($q.when());

        $http.and.returnValue($q.when(finalResponse));

        expect(previewErrorInterceptor.responseError(mockResponse)).toBeResolvedWithData(finalResponse);

        var callback = sharedDataService.update.calls.argsFor(0)[1];

        callback(experience);
        expect(experience.pageId).toBeUndefined();

        expect(iframeManagerService.setCurrentLocation).toHaveBeenCalledWith(null);

        expect($http).toHaveBeenCalledWith(mockResponse.config);
    });

});
