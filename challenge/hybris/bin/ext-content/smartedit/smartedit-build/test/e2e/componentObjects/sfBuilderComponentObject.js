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
module.exports = function() {

    var clickThrough = require('./clickThroughOverlayComponentObject');

    function forceClickThroughOverlay(element) {
        return clickThrough.utils.clickThroughOverlay(element);
    }

    var elements = {
        addComponent: {
            button: function() {
                return browser.findElement(by.id('sf-builder-add-component-button'));
            },
            queueButton: function() {
                return browser.findElement(by.id('sf-builder-queue-add-component-button'));
            },
            alias: function() {
                return browser.findElement(by.id('sf-builder-add-component-alias'));
            },
            parentId: function() {
                return browser.findElement(by.id('sf-builder-add-component-parent'));
            }
        },
        removeComponent: {
            button: function() {
                return browser.findElement(by.id('sf-builder-remove-component-button'));
            },
            queueButton: function() {
                return browser.findElement(by.id('sf-builder-queue-remove-component-button'));
            },
            id: function() {
                return browser.findElement(by.id('sf-builder-remove-component-id'));
            },
            parentId: function() {
                return browser.findElement(by.id('sf-builder-remove-component-parent'));
            }
        },
        rerenderComponent: {
            button: function() {
                return browser.findElement(by.id('sf-builder-rerender-component-button'));
            },
            id: function() {
                return browser.findElement(by.id('sf-builder-rerender-component-id'));
            },
            parentId: function() {
                return browser.findElement(by.id('sf-builder-rerender-component-parent'));
            }
        },
        pageIdAndCatalogVersion: {
            button: function() {
                return browser.findElement(by.id('sf-builder-update-page-id-button'));
            },
            pageId: function() {
                return browser.findElement(by.id('sf-builder-update-page-id'));
            },
            catalogVersionUuid: function() {
                return browser.findElement(by.id('sf-builder-update-catalog-version'));
            }
        },
        pageBuilderConfig: {
            button: function() {
                return browser.findElement(by.id('sf-builder-change-page-button'));
            },
            layout: function() {
                return browser.findElement(by.id('sf-builder-change-page-layout'));
            },
            delays: function() {
                return browser.findElement(by.id('sf-builder-change-page-delays'));
            },
            render: function() {
                return browser.findElement(by.id('sf-builder-change-page-render'));
            }
        }
    };

    var page = {};

    page.actions = {

        // =========== COMPONENT ACTIONS =============

        addComponent: function(alias, parentId) {
            browser.switchToIFrame();
            browser.sendKeys(elements.addComponent.alias(), alias);
            browser.sendKeys(elements.addComponent.parentId(), parentId);
            return forceClickThroughOverlay(elements.addComponent.button());
        },
        queueAddComponent: function(alias, parentId) {
            browser.switchToIFrame();
            browser.sendKeys(elements.addComponent.alias(), alias);
            browser.sendKeys(elements.addComponent.parentId(), parentId);
            return forceClickThroughOverlay(elements.addComponent.queueButton());
        },
        removeComponent: function(id, parentId) {
            browser.switchToIFrame();
            browser.sendKeys(elements.removeComponent.id(), id);
            browser.sendKeys(elements.removeComponent.parentId(), parentId);
            return forceClickThroughOverlay(elements.removeComponent.button());
        },
        queueRemoveComponent: function(id, parentId) {
            browser.switchToIFrame();
            browser.sendKeys(elements.removeComponent.id(), id);
            browser.sendKeys(elements.removeComponent.parentId(), parentId);
            return forceClickThroughOverlay(elements.removeComponent.queueButton());
        },
        rerenderComponent: function(id, parentId) {
            browser.switchToIFrame();
            browser.sendKeys(elements.rerenderComponent.id(), id);
            browser.sendKeys(elements.rerenderComponent.parentId(), parentId);
            return forceClickThroughOverlay(elements.rerenderComponent.button());
        },

        // =========== COMPONENT ACTIONS =============

        changePageIdAndCatalogVersion: function(pageId, catalogVersionUuid) {
            browser.switchToIFrame();
            browser.sendKeys(elements.pageIdAndCatalogVersion.pageId(), pageId);
            browser.sendKeys(elements.pageIdAndCatalogVersion.catalogVersionUuid(), catalogVersionUuid);
            return forceClickThroughOverlay(elements.pageIdAndCatalogVersion.button());
        },
        changePageIdWithoutInteration: function(id) {
            return browser.executeScript("sfBuilder.updatePageId(arguments[0]);", id);
        },
        changePage: function(layout, delay, renderer) {
            browser.switchToIFrame();
            browser.sendKeys(elements.pageBuilderConfig.layout(), layout);
            browser.sendKeys(elements.pageBuilderConfig.delays(), delay);
            browser.sendKeys(elements.pageBuilderConfig.render(), renderer);
            return forceClickThroughOverlay(elements.pageBuilderConfig.button());
        }


    };

    return page;
}();
