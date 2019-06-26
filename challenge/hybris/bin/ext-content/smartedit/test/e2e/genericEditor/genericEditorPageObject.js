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
var path = require('path');

module.exports = (function() {

    var pageObject = {};

    pageObject.constants = {};
    pageObject.elements = {};

    pageObject.actions = {
        bootstrap: function(testName, done) {
            var pathToTest = path.join('smarteditcontainerJSTests/e2e/genericEditor', testName, 'genericEditorTest.html');
            browser.get(pathToTest);
            done();
        }
    };


    return pageObject;
})();
