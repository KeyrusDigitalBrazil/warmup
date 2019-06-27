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
var InflectionPoint = require('./inflectionPointObject.js');

describe(
    'end-to-end Test for inflection point module',
    function() {
        var page;
        beforeEach(function() {

            page = new InflectionPoint();
            browser.waitForWholeAppToBeReady();

        });

        it(
            "Upon loading SmartEdit, inflection-point-selector should be displayed and select the first option. On selection width of the iframe should be changed",
            function() {
                browser.click(page.inflectionMenu);
                browser.click(page.firstInflectionDevice);
                expect(page.iframeWidth).toBe(page.firstDeviceWidth);

            });


    });
