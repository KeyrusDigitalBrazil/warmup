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
describe('linear retry policy service', function() {
    var linearRetry;

    beforeEach(angular.mock.module('linearRetrylModule'));

    beforeEach(inject(function(_linearRetry_) {
        linearRetry = _linearRetry_;
    }));

    it('the calculateNextDelay should return a proper delay based on the given arguments', function() {
        //attemptCount, retryInterval, maxBackoff, minBackoff
        // 2000+, 4000+, 6000+, 8000+, 10000+, 12000+
        var firstDelay = linearRetry.calculateNextDelay(2, 2000, 65000, 5);
        expect(firstDelay < 5000 && firstDelay > 4000).toBeTruthy();
        var secondDelay = linearRetry.calculateNextDelay(4, 2000, 65000, 5);
        expect(secondDelay < 9000 && secondDelay > 8000).toBeTruthy();

    });

    it('the calculateNextDelay should fall back to maxBackoff if the generated delay is more than the maxBackoff', function() {
        //attemptCount, retryInterval, maxBackoff, minBackoff
        // 2000+, 4000+, 6000+, 8000+, 10000+, 12000+
        var delay = linearRetry.calculateNextDelay(12, 2000, 1234);
        expect(delay).toBe(1234);

    });

    it('the calculateNextDelay should work given only an attemptCount', function() {
        //attemptCount, retryInterval, maxBackoff, minBackoff
        // 500+, 1000+, 1500+, 2000+, 2500+, 3000+
        var delay = linearRetry.calculateNextDelay(5);
        expect(delay < 3000 && delay >= 2500).toBeTruthy();

    });

    it('the canRetry should return false the attemptCount is larger than the max', function() {

        //attemptCount, maxAttempt
        var delay = linearRetry.canRetry(3, 2);
        expect(delay).toBeFalsy();

    });

    it('the canRetry should return false the attemptCount is larger than the max', function() {

        //attemptCount, maxAttempt
        var delay = linearRetry.canRetry(6);
        expect(delay).toBeFalsy();

    });
});
