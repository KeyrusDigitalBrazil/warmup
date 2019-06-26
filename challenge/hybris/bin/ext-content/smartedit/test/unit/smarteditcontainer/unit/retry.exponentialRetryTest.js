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
describe('exponential retry policy service', function() {
    var exponentialRetry;

    beforeEach(angular.mock.module('exponentialRetrylModule'));

    beforeEach(inject(function(_exponentialRetry_) {
        exponentialRetry = _exponentialRetry_;
    }));

    it('the calculateNextDelay should return a proper delay based on the given arguments', function() {
        //attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        var delay = exponentialRetry.calculateNextDelay(2, 65000, 5);
        expect(delay < 5000 && delay >= 4000).toBeTruthy();

    });

    it('the calculateNextDelay should fall back to maxBackoff if the generated delay is more than the maxBackoff', function() {
        //attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        var delay = exponentialRetry.calculateNextDelay(2, 3000, 5);
        expect(delay).toBe(3000);

    });

    it('the calculateNextDelay should work given only an attemptCount', function() {
        //attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        var delay = exponentialRetry.calculateNextDelay(2);
        expect(delay < 5000 && delay >= 4000).toBeTruthy();

    });

    it('the canRetry should return false the attemptCount is larger than the max', function() {

        //attemptCount, maxAttempt
        var delay = exponentialRetry.canRetry(3, 2);
        expect(delay).toBeFalsy();

    });

    it('the canRetry should return false the attemptCount is larger than the max', function() {

        //attemptCount, maxAttempt
        var delay = exponentialRetry.canRetry(6);
        expect(delay).toBeFalsy();

    });
});
