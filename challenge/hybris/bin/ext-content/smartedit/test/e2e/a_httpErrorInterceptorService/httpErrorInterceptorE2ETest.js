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
describe('HTTP Error Interceptor -', function() {
    var alertsComponent = require('../utils/components/systemAlertsComponentObject');
    var testPage = require('./httpErrorInterceptorPageObject');

    beforeEach(function() {
        testPage.actions.navigate();
    });

    describe('default interceptors -', function() {
        it('WHEN a resource not found error (404) is triggered for Content-type json THEN I expect to see an alert message', function() {
            testPage.actions.triggerError404Json().then(function() {
                alertsComponent.assertions.assertTotalNumberOfAlerts(1);
                alertsComponent.assertions.assertAlertTextByIndex(0, 'Your request could not be processed! Please try again later!');
            });
        });

        it('WHEN a resource not found error (404) is triggered for Content-type html THEN no alert message is displayed', function() {
            testPage.actions.triggerError404Html();
            alertsComponent.assertions.assertNoAlertsDisplayed();
        });

        it('WHEN a bad request (400) is triggered for Content-type json THEN I expect to see an alert message', function() {
            testPage.actions.triggerError400Json().then(function() {
                alertsComponent.assertions.assertTotalNumberOfAlerts(1);
                alertsComponent.assertions.assertAlertTextByIndex(0, 'error: bad request');
            });
        });
    });

    describe('custom interceptors', function() {
        it('WHEN I add a custom interceptor for 501 errors of Content-type json AND a 501 error of Content-type json is triggered THEN I expect to see an alert message', function() {
            testPage.actions.triggerError501Json().then(function() {
                alertsComponent.assertions.assertTotalNumberOfAlerts(1);
                alertsComponent.assertions.assertAlertTextByIndex(0, 'error: 501 bad request');
            });
        });
    });

    describe('graceful degradation -', function() {
        it('GIVEN a custom retry strategy is registered for 503 error WHEN a 503 error is triggered that correspond to a operation context THEN I expect to see a message when maximum of retry is reached', function() {
            testPage.actions.triggerError503().then(function() {
                expect(testPage.elements.getGraceFulDegradationStatus().getText()).toBe('FAILED');
            });
        });

        it('GIVEN a custom retry strategy is registered for a request that fails twice before being successfull WHEN the request is made THEN I expect to see a retry in progress and THEN a success message', function() {
            testPage.actions.triggerError502().then(function() {
                expect(testPage.elements.getGraceFulDegradationStatus().getText()).toBe('PASSED');
            });
        });
    });
});
