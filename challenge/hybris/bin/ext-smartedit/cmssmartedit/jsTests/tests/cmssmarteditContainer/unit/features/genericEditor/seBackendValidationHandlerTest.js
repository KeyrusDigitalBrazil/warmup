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
/* jshint unused:false, undef:false */
describe('seBackendValidationHandler', function() {
    var seBackendValidationHandler;
    var RESPONSE = {
        data: {
            errors: [{
                type: 'ModelError'
            }, {
                type: 'ValidationError',
                message: 'no subject provided'
            }, {
                type: 'ValidationError',
                subject: 'someSubject',
                message: 'some message'
            }]
        }
    };

    beforeEach(angular.mock.module('seBackendValidationHandlerModule'));

    beforeEach(inject(function(_seBackendValidationHandler_) {
        seBackendValidationHandler = _seBackendValidationHandler_;
    }));

    describe('handleResponse', function() {
        it('should transform a response into a list of validation errors filtering on type ValidationError', function() {
            expect(seBackendValidationHandler.handleResponse(RESPONSE)).toEqual([{
                subject: 'someSubject',
                message: 'some message'
            }]);
        });

        it('should append the validation errors to an errors context list if one is provided', function() {
            var errorsContext = [];
            seBackendValidationHandler.handleResponse(RESPONSE, errorsContext);
            expect(errorsContext).toEqual([{
                subject: 'someSubject',
                message: 'some message'
            }]);
        });
    });
});
