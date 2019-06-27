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
/* jshint unused:false, undef:false */
var navigationEditor = function() {

    this.pageURI = 'jsTests/tests/cmssmarteditContainer/e2e/features/navigation/navigationEditor/navigationEditorTest.html';
    browser.get(this.pageURI);

};

navigationEditor.prototype = {

    totalNodesCount: function() {
        return element.all(by.css('.angular-ui-tree-nodes .angular-ui-tree-node')).count();
    },
    getChildrenNodes: function(key) {
        var index = parseInt(key) + 1;
        return element.all(by.css('.angular-ui-tree-nodes .angular-ui-tree-node:nth-child(' + index + ') > ol > li')).count();
    },
    getMoreMenu: function(key) {
        var index = parseInt(key) + 1;
        return element(by.css('.angular-ui-tree-nodes .angular-ui-tree-node:nth-child(' + index + ') [data-dropdown-menu]'));
    },
    getMoreMenuOptionsCount: function(key) {
        var index = parseInt(key) + 1;
        return element.all(by.css('.angular-ui-tree-nodes .angular-ui-tree-node:nth-child(' + index + ') [data-dropdown-menu] > li:not(.divider)')).count();
    },
    getMoreMenuOptionByText: function(key, text) {
        var index = parseInt(key) + 1;
        var dropDown = element(by.css('.angular-ui-tree-nodes .angular-ui-tree-node:nth-child(' + index + ') [data-dropdown-menu]'));
        return dropDown.element(by.cssContainingText('li > a', text));
    },
    getNodeName: function(key) {
        var index = parseInt(key) + 1;
        return element(by.css('.angular-ui-tree-nodes .angular-ui-tree-node:nth-child(' + index + ') > div > span')).getText();
    }

};

module.exports = navigationEditor;
