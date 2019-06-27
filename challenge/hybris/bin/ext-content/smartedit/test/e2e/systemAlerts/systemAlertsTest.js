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
describe("System Alerts -", function() {

    var alertsComponent = require('../utils/components/systemAlertsComponentObject');
    var testPage = require('./systemAlertsTestPageObject');

    beforeEach(function() {
        testPage.actions.navigate();
        testPage.actions.resetForm();
    });

    describe('Hiding and showing -', function() {

        it('A basic alert can be displayed', function() {
            testPage.showAlert('test alert message');
            alertsComponent.assertions.assertTotalNumberOfAlerts(1);
        });

        it('2 alerts can both be displayed (stacked)', function() {
            var s1 = 'test alert messages 1';
            var s2 = 'test alert messages 2';
            testPage.showAlert(s1, "danger", false, 5000);
            testPage.showAlert(s2, "warning", false, 5000);

            alertsComponent.assertions.assertTotalNumberOfAlerts(2);
            alertsComponent.assertions.assertAlertTextByIndex(1, s1);
            alertsComponent.assertions.assertAlertTextByIndex(0, s2);
        });

        it('Given 3 alerts displayed, if second one manually dismissed, only first and 3rd remain', function() {

            var alertMessage0 = 'alert 0';
            var alertMessage1 = 'alert 1';
            var alertMessage2 = 'alert 2';

            testPage.showAlert(alertMessage0);
            testPage.showAlert(alertMessage1);
            testPage.showAlert(alertMessage2);
            alertsComponent.actions.closeAlertByIndex(1);

            alertsComponent.assertions.assertTotalNumberOfAlerts(2);
            alertsComponent.assertions.assertAlertTextByIndex(1, alertMessage0);
            alertsComponent.assertions.assertAlertTextByIndex(0, alertMessage2);
        });

        it('Alert is automatically removed after timeout', function() {

            // Is this potentially flaky? probably...
            // If system really slow, maybe 1000 is not enough to display
            // but if we make it longer... slow test suit
            testPage.showAlert('alert to timeout', null, null, 1000);

            alertsComponent.assertions.assertTotalNumberOfAlerts(1);
            alertsComponent.assertions.assertNoAlertsDisplayed();
        });

        it('Alert shows dismiss X button for closeable alerts', function() {
            testPage.showAlert('some default alert', null, true);

            alertsComponent.assertions.assertAlertCloseabilityByIndex(0, true);
        });

        it('Alert does not show dismiss X button for non-closeable alerts', function() {
            testPage.showAlert('some default alert', null, false);
            alertsComponent.assertions.assertAlertCloseabilityByIndex(0, false);
        });

    });

    describe('is rendering ', function() {

        it('message', function() {
            var expectedString = 'The number is 12,345.123.';

            testPage.showAlert(expectedString, null, null, 5000);
            alertsComponent.assertions.assertAlertTextByIndex(0, expectedString);
        });

        it('message placeholder', function() {
            var expectedString = 'The number is 12,345.123.';

            testPage.showAlert('The number is {{ value }}.', null, null, 5000, null, null, '{ value: "12,345.123" }');
            alertsComponent.assertions.assertAlertTextByIndex(0, expectedString);
        });

        it('template', function() {
            var alertTemplate = '<div>The number is {{ "12345.12345" | number: 3 }}.</div>';
            var expectedString = 'The number is 12,345.123.';

            testPage.showAlert(null, null, null, 5000, alertTemplate);
            alertsComponent.assertions.assertAlertTextByIndex(0, expectedString);
        });

        it('templateUrl', function() {
            var expectedString = 'The number is 12,345.12.';

            testPage.showAlert(null, null, null, 5000, null, 'systemAlertsTestTemplate.html');
            alertsComponent.assertions.assertAlertTextByIndex(0, expectedString);
        });

    });

    describe('Alert types -', function() {

        it('Will style the 4 alert types', function() {
            testPage.showAlert('danger alert', "danger");
            testPage.showAlert('info alert', "info");
            testPage.showAlert('warning alert', "warning");
            testPage.showAlert('success alert', "success");

            alertsComponent.assertions.assertAlertIsOfTypeByIndex(3, 'danger');
            alertsComponent.assertions.assertAlertIsOfTypeByIndex(2, 'info');
            alertsComponent.assertions.assertAlertIsOfTypeByIndex(1, 'warning');
            alertsComponent.assertions.assertAlertIsOfTypeByIndex(0, 'success');
        });


    });


});
