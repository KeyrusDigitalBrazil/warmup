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
var InflectionPoint = function() {

    this.pageURI = 'test/e2e/inflectionPoint/inflectionPointTest.html';
    browser.get(this.pageURI);
};

InflectionPoint.prototype = Object.create({}, {

    inflectionMenu: {
        get: function() {
            return element(by.id('inflectionPtDropdown'));
        }

    },
    firstInflectionDevice: {
        get: function() {
            return element(by.xpath("//*[contains(@src, 'res_phone')]"));
        }
    },
    firstDeviceWidth: {
        get: function() {
            return '480px';
        }
    },
    iframeWidth: {
        get: function() {
            return element(by.tagName('iframe')).getCssValue('width');
        }
    }

});

module.exports = InflectionPoint;
