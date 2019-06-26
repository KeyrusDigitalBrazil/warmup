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
describe('Integration of toolbar directives into the framework', function() {

    beforeEach(function() {
        browser.get('test/e2e/toolbars/frameworkIntegration/frameworkIntegrationTest.html');
    });

    /*seems to break with new double bootstrapping of smarteditcontainer*/
    describe('availability of SmartEdit title toolbar and experience selector toolbar', function() {

        it('SmartEdit title toolbar and experience selector toolbar exists and are correctly bootstrapped', function() {
            browser.waitForVisibility(by.css("div.ySmartEditHeaderToolbar"));
            browser.waitForVisibility(by.css("div.ySmartEditExperienceToolbar"));
            browser.waitForVisibility(by.css("div.ySmartEditPerspectiveToolbar"));
        });
    });
});
