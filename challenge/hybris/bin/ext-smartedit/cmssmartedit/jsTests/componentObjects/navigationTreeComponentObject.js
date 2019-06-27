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
var landingPage;
if (typeof require !== 'undefined') {
    landingPage = require('../pageObjects/landingPagePageObject.js');
}

module.exports = {

    collapseButtonClass: 'glyphicon-chevron-up',
    expandButtonClass: 'glyphicon-chevron-down',
    moreButtonClass: 'hyicon-more',
    addNewTopLevelButton: 'Add New Top Level',

    _getNode: function(nodeLabel) {
        return by.xpath("//span[.='" + nodeLabel + "']/preceding-sibling::a");
    },
    _getNodeNames: function(array, rows, index) {
        var deferred = protractor.promise.defer();
        if (rows[index]) {
            rows[index].getText().then(function(value) {
                array.push(value);
                if (index < rows.length - 1) {
                    this._getNodeNames(array, rows, index + 1).then(function(array) {
                        deferred.fulfill(array);
                    });
                } else {
                    deferred.fulfill(array);
                }
            }.bind(this));
        } else {
            deferred.fulfill(array);
        }
        return deferred.promise;
    },
    _getNodeHandle: function(nodeLabel) {
        return by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]");
    },
    _getNodeTitle: function(nodeLabel) {
        var deferred = protractor.promise.defer();
        var selector = "//div/span[.='" + nodeLabel + "']/ancestor::li[1]/div/div[5]";
        element(by.xpath(selector)).getText().then(function(value) {
            deferred.fulfill(value);
        });
        return deferred.promise;
    },
    clickOnLinkHavingClass: function(nodeLabel, cssClass) {
        return browser.click(by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/div/a/span[contains(@class, '" + cssClass + "')]"));
    },
    clickOnLinkHavingContent: function(nodeLabel, anchorLabel) {
        return browser.click(by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/a[contains(.,'" + anchorLabel + "')]"));
    },
    clickOnLinkHavingClassInRenderer: function(nodeLabel, cssClass) {
        return browser.click(by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/div/y-drop-down-menu/div/button/span[contains(@class, '" + cssClass + "')]"));
    },
    clickNodeByText: function(nodeLabel) {
        return browser.click(this._getNode(nodeLabel));
    },
    expand: function(nodeLabel) {
        return this.clickOnLinkHavingClass(nodeLabel, this.expandButtonClass);
    },
    collapse: function(nodeLabel) {
        return this.clickOnLinkHavingClass(nodeLabel, this.collapseButtonClass);
    },
    waitForyTreeToBePresent: function() {
        browser.waitForPresence(by.xpath("//ytree/div/div/div/ol/li/div/div[2]/span"));
    },
    getChildrenNames: function(nodeLabel) {
        this.waitForyTreeToBePresent();
        var deferred = protractor.promise.defer();
        var selector;
        if (nodeLabel) {
            selector = by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::li[1]/ol/li/div/div[4]/span");
        } else {
            selector = by.xpath("//ytree/div/div/div/ol/li/div/div[4]/span");
        }
        browser.waitForPresence(selector);

        element.all(selector).then(function(children) {
            this._getNodeNames([], children, 0).then(function(array) {
                deferred.fulfill(array);
            });
        }.bind(this));
        return deferred.promise;
    },
    getNodeTitles: function(nodeLabels) {
        var deferred = protractor.promise.defer();
        var promises = [];
        var titles = [];

        nodeLabels.forEach(function(nodeLabel) {
            this._getNodeTitle(nodeLabel).then(function(value) {
                promises.push(value);
                deferred.fulfill(promises);
            });
        }.bind(this));

        return deferred.promise;
    },
    getChildrenCount: function(nodeLabel) {
        return this.getChildrenNames(nodeLabel).then(function(children) {
            return children.length;
        });
    },
    getNthChild: function(nodeLabel, index) {
        return this.getChildrenNames(nodeLabel).then(function(children) {
            return children[index];
        });
    },
    clickMoreMenu: function(nodeLabel) {
        return this.clickOnLinkHavingClassInRenderer(nodeLabel, this.moreButtonClass);
    },
    getMoreMenuOptions: function(nodeLabel) {
        var deferred = protractor.promise.defer();
        var selector;
        if (nodeLabel) {
            selector = by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/div/y-drop-down-menu/div/ul/li");
        } else {
            selector = by.xpath("//ytree/div/ol/li/div/div");
        }
        browser.waitForPresence(selector);
        element.all(selector).then(function(options) {
            this._getNodeNames([], options, 0).then(function(array) {
                deferred.fulfill(array);
            });

        }.bind(this));
        return deferred.promise;
    },
    getMoreMenuOptionsCount: function(nodeLabel) {
        return this.getMoreMenuOptions(nodeLabel).then(function(children) {
            return children.length;
        });
    },
    clickMoreMenuItem: function(nodeLabel, option) {
        browser.click(this.getDropdownItemByRowAndLabel(nodeLabel, option));
    },
    navigateToFirstCatalogNavigationEditor: function() {
        return landingPage.actions.navigateToFirstNavigationManagementPage();
    },
    clickAddNewTopLevel: function() {
        browser.click(by.cssContainingText('span', this.addNewTopLevelButton));
        return browser.waitUntilModalAppears();
    },
    assertNodeHasOrderedChildren: function(node, expectedChildren) {
        // yjQuery escapes angular lifecycle, wait for children to be updated
        browser.driver.wait(function() {
            return this.getChildrenNames(node).then(function(children) {
                return children.toString() === expectedChildren.toString();
            }, function() {
                return false;
            });
        }.bind(this), 5000);
        this.getChildrenNames(node).then(function(children) {
            expect(children.toString()).toEqual(expectedChildren.toString());
        });
    },
    startDraggingNode: function(nodeLabel) {
        var node = browser.findElement(this._getNodeHandle(nodeLabel));
        return browser.actions().mouseMove(node).mouseDown().perform().then(function() {
            return browser.sleep(this.dragDelay);
        }.bind(this));
    },
    moveMouseToNode: function(nodeLabel) {
        var node = browser.findElement(this._getNodeHandle(nodeLabel));
        return browser.actions().mouseMove(node).perform().then(function() {
            return browser.actions().mouseMove(this.dragAndDropOffsetFix).perform();
        }.bind(this));
    },
    mouseUp: function() {
        return browser.actions().mouseUp().perform();
    },
    dragAndDropOffsetFix: {
        x: 1,
        y: -11
    },
    dragDelay: 200,
    modal: function() {
        return element(by.css('.modal-dialog'));
    },
    modalIsOpen: function() {
        return this.modal() !== null;
    },
    confirmModal: function() {
        return browser.click(element(by.css('#confirmOk')));
    },
    cancelModal: function() {
        return browser.click(element(by.css('#confirmCancel')));
    },
    clickOnDeleteConfirmation: function(buttonMessage) {
        var buttonId = (buttonMessage === 'Ok' ? 'confirmOk' : 'confirmCancel');
        return browser.click(element(by.id(buttonId)));
    },
    getDeleteConfirmationMessage: function() {
        return element(by.id('confirmationModalDescription')).getText();
    },

    // Elements
    getAddNewTopLevelButton: function() {
        return browser.findElement(by.css('.ySEAdd-Nav-button'));
    },

    // Assertions
    assertNavigationManagementPageIsDisplayed: function() {
        expect(this.getAddNewTopLevelButton().isPresent()).toBe(true);
    },

    getRowByName: function(rowName) {
        return browser.findElement(by.cssContainingText(".desktopLayout > ol[data-ui-tree-nodes] > li div[ui-tree-handle]", rowName));
    },

    getDropdownItemByRowAndLabel: function(rowName, optionLabel) {
        return this.getRowByName(rowName).all(by.cssContainingText(".dropdown-menu a", optionLabel)).first();
    }

};
