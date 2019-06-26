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
describe('seObjectValidatorFactory', function() {
    var seObjectValidatorFactory, frontendValidator;
    var VALIDATORS = [{
        subject: 'someSubject',
        message: 'someMessage',
        validate: function(val) {
            return !!val;
        }
    }];

    beforeEach(angular.mock.module('seObjectValidatorFactoryModule'));

    beforeEach(inject(function(_seObjectValidatorFactory_) {
        seObjectValidatorFactory = _seObjectValidatorFactory_;
    }));

    beforeEach(function() {
        frontendValidator = seObjectValidatorFactory.build(VALIDATORS);
    });

    describe('build', function() {
        it('should return a validator with a validate function', function() {
            expect(frontendValidator).toEqual({
                validate: jasmine.any(Function)
            });
        });
    });

    describe('a built validator', function() {
        it('should return true if all validators pass', function() {
            expect(frontendValidator.validate({
                someSubject: 'valid'
            })).toBe(true);
        });

        it('should leave the errors context unaltered if it is provided and all validators pass', function() {
            var errorsContext = [];
            expect(frontendValidator.validate({
                someSubject: 'valid'
            }, errorsContext)).toBe(true);
            expect(errorsContext).toEqual([]);
        });

        it('should return false if any validator fails', function() {
            expect(frontendValidator.validate({})).toBe(false);
        });

        it('should append to the errors context a message and subject if one is provided and any validator fails', function() {
            var errorsContext = [];
            expect(frontendValidator.validate({}, errorsContext)).toBe(false);
            expect(errorsContext).toEqual([{
                subject: 'someSubject',
                message: 'someMessage'
            }]);
        });
    });
});
