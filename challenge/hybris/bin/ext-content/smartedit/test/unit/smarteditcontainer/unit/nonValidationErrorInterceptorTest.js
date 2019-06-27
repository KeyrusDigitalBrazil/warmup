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
describe('non validation error interceptor', function() {
    var alertService;
    var nonValidationErrorInterceptor;

    beforeEach(angular.mock.module('nonvalidationErrorInterceptorModule', function($provide) {
        alertService = jasmine.createSpyObj('alertService', ['showDanger']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(inject(function(_nonValidationErrorInterceptor_) {
        nonValidationErrorInterceptor = _nonValidationErrorInterceptor_;
    }));

    it('should match predicate for a GET xhr request with a HTTP Error 400', function() {
        // GIVEN
        var matchMockResponse = {
            status: 400
        };

        // WHEN
        var matchPredicate = nonValidationErrorInterceptor.predicate(matchMockResponse);

        // THEN
        expect(matchPredicate).toBe(true);
    });

    it('should not match predicate for a GET xhr request with a HTTP Error 401 or 404', function() {
        // GIVEN
        var predicate;
        [401, 404].forEach(function(status) {
            // WHEN
            predicate = nonValidationErrorInterceptor.predicate({
                status: status
            });

            // THEN
            expect(predicate).toBe(false);
        });
    });

    it('should display only non validation errors in alert messages and reject the promise', function() {
        // GIVEN
        var mockResponse = {
            status: 400,
            data: {
                errors: [{
                    'message': 'This field cannot contain special characters',
                    'type': 'ValidationError'
                }, {
                    'message': 'This is the second validation error',
                    'type': 'NonValidationError'
                }]
            }
        };

        // WHEN
        var promise = nonValidationErrorInterceptor.responseError(mockResponse);

        // THEN
        expect(alertService.showDanger.calls.count()).toEqual(1);
        expect(alertService.showDanger).toHaveBeenCalledWith(jasmine.objectContaining({
            message: 'This is the second validation error'
        }));
        expect(promise).toBeRejectedWithData(mockResponse);
    });

    it('should not display any alert messages if the response errors are only validation errors and reject the promise', function() {
        // GIVEN
        var mockResponse = {
            status: 400,
            data: {
                errors: [{
                    'message': 'This field cannot contain special characters',
                    'type': 'ValidationError'
                }, {
                    'message': 'This field is required',
                    'type': 'ValidationError'
                }]
            }
        };

        // WHEN
        var promise = nonValidationErrorInterceptor.responseError(mockResponse);

        // THEN
        expect(alertService.showDanger).not.toHaveBeenCalled();
        expect(promise).toBeRejectedWithData(mockResponse);
    });
});
