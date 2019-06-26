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
describe('seValidationErrorParser', function() {

    var seValidationErrorParser;

    beforeEach(angular.mock.module('genericEditorServicesModule'));

    beforeEach(inject(function(_seValidationErrorParser_) {
        seValidationErrorParser = _seValidationErrorParser_;
    }));

    describe('parse', function() {
        var MESSAGE = 'Some validation error occurred. FirstKey: [SomeValue]. SecondKey: [SomeOtherValue].';
        var parsedError;

        beforeEach(function() {
            parsedError = seValidationErrorParser.parse(MESSAGE);
        });

        it('should parse the details from the message and strip the message', function() {
            expect(parsedError.message).toBe('Some validation error occurred.');
            expect(parsedError.firstkey).toBe('SomeValue');
            expect(parsedError.secondkey).toBe('SomeOtherValue');
        });
    });
});
