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
module.exports = (function() {

    var pageVersioningMenu = {};

    pageVersioningMenu.constants = {};

    pageVersioningMenu.elements = {};

    pageVersioningMenu.actions = {
        navigateToPage: function(pageHasEmptyVersionList, dirname, done) {
            return browser.bootstrap(dirname).then(function() {
                browser.executeScript('window.sessionStorage.setItem("emptyVersionList", arguments[0])', !!pageHasEmptyVersionList);
                browser.waitForWholeAppToBeReady().then(function() {
                    done();
                });
            });
        }
    };

    pageVersioningMenu.assertions = {};

    return pageVersioningMenu;

}());
