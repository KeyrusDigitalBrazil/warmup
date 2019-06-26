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

    var componentObject = {};

    componentObject.elements = {
        getSmarteditOverlayCSSMatcherForComponent: function(componentId) {
            return "#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + componentId + "'] ";
        },
        getMoreItemsButton: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".cmsx-ctx-more-btn"));
            });
        },
        getRemoveButtonForComponentId: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".removebutton"));
            });
        },
        getMoveButtonForComponentId: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".movebutton"));
            });
        },
        getEditButtonForComponentId: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".editbutton"));
            });
        },
        getCloneButtonForComponentId: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return componentObject.elements.getMoreItemsButton(componentId).then(function(moreButton) {
                    return moreButton.isPresent().then(function(isPresent) {
                        if (!isPresent) {
                            return moreButton;
                        }
                        return browser.click(by.css(moreButton)).then(function() {
                            return element(by.css(".se-contextual-more-menu--icon.clonebutton"));
                        });
                    });
                });
            });
        },
        getExternalComponentButtonForComponentId: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".externalcomponentbutton"));
            });
        },
        getNumContextualMenuItemsForComponentId: function(componentId) {
            return element.all(by.css(componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId) + ".cmsx-ctx-btns")).count();
        }
    };

    componentObject.actions = {
        clickRemoveButton: function(componentId) {
            return browser.switchToIFrame().then(function() {
                var cloneSelector = componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId);
                var selector = cloneSelector + ".removebutton";
                browser.waitForVisibility(cloneSelector);
                return browser.moveTo(selector).then(function() {
                    return browser.click(by.css(selector));
                });
            });
        },
        clickCloneButton: function(componentId) {
            return browser.switchToIFrame().then(function() {
                var componentSelector = componentObject.elements.getSmarteditOverlayCSSMatcherForComponent(componentId);
                var selector = componentSelector + ".cmsx-ctx-more-btn";
                browser.waitForVisibility(selector);
                return browser.moveTo(selector).then(function() {
                    return browser.click(by.css(selector)).then(function() {
                        selector = ".se-contextual-more-menu--item .se-contextual-more-menu--icon.clonebutton";
                        browser.waitForVisibility(selector);
                        return browser.moveTo(selector).then(function() {
                            return browser.click(by.css(selector));
                        });
                    });
                });
            });
        },
        clickExternalComponentButton: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return componentObject.elements.getExternalComponentButtonForComponentId(componentId).then(function(externalComponentButton) {
                    return browser.actions()
                        .mouseMove(externalComponentButton)
                        .click()
                        .perform();
                });
            });
        }
    };

    componentObject.assertions = {
        removeMenuItemForComponentIdLoadedRightImg: function(componentID) {
            componentObject.elements.getRemoveButtonForComponentId(componentID).then(function(removeButton) {
                expect(removeButton.getAttribute('class')).toContain('hyicon-removelg');
            });
        },
        editMenuItemForComponentIdLoadedRightImg: function(componentID) {
            componentObject.elements.getEditButtonForComponentId(componentID).then(function(editButton) {
                // this button is still not using the assets service.
                expect(editButton.getAttribute('class')).toContain('hyicon-edit');
            });
        },
        moveMenuItemForComponentIdLoadedRightImg: function(componentID) {
            componentObject.elements.getMoveButtonForComponentId(componentID).then(function(moveButton) {
                expect(moveButton.getAttribute('class')).toContain('hyicon-dragdroplg');
            });
        },
        externalComponentMenuItemForComponentIdLoadedRightImg: function(componentID) {
            componentObject.elements.getExternalComponentButtonForComponentId(componentID).then(function(moveButton) {
                expect(moveButton.getAttribute('class')).toContain('hyicon-globe');
            });
        },
        externalComponentToShowParentCatalogDetails: function(componentID, catalogVersionName) {
            componentObject.actions.clickExternalComponentButton(componentID).then(function() {
                browser.waitUntil(function() {
                    return browser.findElement(by.css('external-component-button')).getText().then(function(text) {
                        return text === catalogVersionName;
                    });
                }, "external component button text not found");
            });
        },
        assertMoreItemsButtonIsNotPresent: function(componentID) {
            componentObject.elements.getMoreItemsButton(componentID).then(function(moreItemsButtons) {
                browser.waitForAbsence(moreItemsButtons);
            });
        },
        assertEditButtonIsNotPresent: function(componentId) {
            componentObject.elements.getEditButtonForComponentId(componentId).then(function(editButton) {
                browser.waitForAbsence(editButton);
            });
        },
        assertMoveButtonIsNotPresent: function(componentId) {
            componentObject.elements.getMoveButtonForComponentId(componentId).then(function(moveButton) {
                browser.waitForAbsence(moveButton);
            });
        },
        assertRemoveButtonIsNotPresent: function(componentId) {
            componentObject.elements.getRemoveButtonForComponentId(componentId).then(function(removeButton) {
                browser.waitForAbsence(removeButton);
            });
        }
    };

    return componentObject;
})();
