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

    var componentMenu = {};

    // --------------------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------------------
    var componentTypes = ['PARAGRAPH COMPONENT', 'SIMPLE BANNER COMPONENT'];
    var components = {
        apparelOnline: [
            'Component1', 'Component2', 'Component3', 'Component4', 'Component5',
            'Component6', 'Component7', 'Component8', 'Component9', 'Component10',
            'Component11', 'Component12', 'Component13', 'Component14', 'Component15',
            'Component16', 'Component17', 'Component18', 'Component19', 'Component20',
            'Component21', 'Component22', 'Component23'
        ],
        apparelUkStaged: [
            'Component1', 'Component2', 'Component3', 'Component4', 'Component5',
            'Component6', 'Component7', 'Component8', 'Component9', 'Component10',
            'Component11', 'Component12', 'Component13', 'Component14', 'Component15',
            'Component16', 'Component17', 'Component18', 'Component19', 'Component20'
        ]
    };

    // --------------------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------------------
    componentMenu.constants = {
        SEARCH_DEBOUNCE_TIME: 1000,
        PAGE_SIZE: 10,

        APPAREL_ONLINE: 'apparelOnline',
        APPAREL_UK_STAGED: 'apparelUkStaged',
        APPAREL_ONLINE_NAME: 'Apparel Content Catalog - Online',
        APPAREL_UK_STAGED_NAME: 'Apparel UK Content Catalog - Staged'
    };

    // --------------------------------------------------------------------------------------------------
    // Elements
    // --------------------------------------------------------------------------------------------------
    componentMenu.elements = {
        getComponentMenuToolbarItem: function() {
            return browser.findElement(by.css('[data-item-key="se.cms.componentMenuTemplate"]'));
        },
        getComponentMenuButton: function() {
            return this.getComponentMenuToolbarItem().element(by.css('.toolbar-action--button'));
        },
        getComponentMenu: function() {
            return browser.findElement(by.css('.se-component-menu'));
        },

        // Components Types Tab
        getComponentTypesTabHeaderSelector: function() {
            return by.css('.nav-tabs [data-tab-id="componentTypesTab"]');
        },
        getComponentTypesTabHeader: function() {
            return this.getComponentMenuToolbarItem().element(this.getComponentTypesTabHeaderSelector());
        },
        getComponentsTypesTab: function() {
            return browser.findElement(by.css('y-tab[data-tab-id="componentTypesTab"]'));
        },
        getComponentTypesContainer: function() {
            return browser.findElement(by.css('.se-component-menu--results__types'));
        },
        getComponentTypes: function() {
            var typesSelector = by.css('.se-component-item--details-type');

            return this.getComponentTypesContainer().all(typesSelector).then(function(types) {
                return this.getItemsText(types);
            }.bind(this));
        },
        getComponentTypesSearchBox: function() {
            return this.getComponentsTypesTab().element(by.css('.se-component-search--input'));
        },

        // Components Tab
        getComponentsTabHeaderSelector: function() {
            return by.css('.nav-tabs [data-tab-id="componentsTab"]');
        },
        getComponentsTabHeader: function() {
            return this.getComponentMenuToolbarItem().element(this.getComponentsTabHeaderSelector());
        },
        getComponentsTab: function() {
            return browser.findElement(by.css('y-tab[data-tab-id="componentsTab"]'));
        },
        getCatalogVersionDropdown: function() {
            return this.getComponentsTab().element(by.css('.se-component-menu--select'));
        },
        getCatalogVersionOption: function(catalogVersionName) {
            var selector = "//*[contains(@class, 'ui-select-choices-row') and contains(@class, 'select2-result-label')]//*[text()='" + catalogVersionName + "']";
            return browser.findElement(by.xpath(selector));
        },
        getComponentsContainer: function() {
            return browser.findElement(by.css('.se-component-menu--item-container'));
        },
        getComponents: function() {
            var componentsSelector = by.css('.se-component-item--details-name');

            return element.all(componentsSelector).then(function(components) {
                return this.getItemsText(components);
            }.bind(this));
        },
        getComponentsSearchBox: function() {
            return this.getComponentsTab().element(by.css('.se-component-search--input'));
        },
        getComponentsScrollElement: function() {
            return browser.findElement(by.css('.se-component-menu--infinite-scroll > div'));
        },

        // Others
        getItemsText: function(items) {
            return protractor.promise.all(items.map(function(item) {
                return item.getText();
            }));
        },
        getCloneOnDropAction: function() {
            return element(by.css(".se-component-menu--clone-on-drop"));
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Actions
    // --------------------------------------------------------------------------------------------------
    componentMenu.actions = {
        openMenu: function() {
            return browser.click(componentMenu.elements.getComponentMenuButton(), 'Cannot open component menu - component menu button not found.');
        },

        // Component Types
        selectComponentTypesTab: function() {
            return browser.click(componentMenu.elements.getComponentTypesTabHeader());
        },
        searchComponentTypes: function(searchTerm) {
            return browser.sendKeys(componentMenu.elements.getComponentTypesSearchBox(), searchTerm, 'Cannot find component types tab search box.').then(function() {
                return browser.sleep(componentMenu.constants.SEARCH_DEBOUNCE_TIME);
            });
        },

        // Components
        selectComponentsTab: function() {
            return browser.click(componentMenu.elements.getComponentsTabHeader());
        },
        openCatalogVersionDropdown: function() {
            return browser.click(componentMenu.elements.getCatalogVersionDropdown());
        },
        selectCatalogVersion: function(catalogVersionName) {
            return this.openCatalogVersionDropdown().then(function() {
                return browser.click(componentMenu.elements.getCatalogVersionOption(catalogVersionName));
            });
        },
        searchComponents: function(searchTerm) {
            return browser.sendKeys(componentMenu.elements.getComponentsSearchBox(), searchTerm, 'Cannot find component types tab search box.').then(function() {
                return browser.sleep(componentMenu.constants.SEARCH_DEBOUNCE_TIME);
            });
        },
        scrollComponents: function(catalogVersionName, pages) {
            var collectionSize = components[catalogVersionName].length;
            var expectedNumComponents = Math.min(pages * componentMenu.constants.PAGE_SIZE, collectionSize);

            var currentNumOfComponents = 0;

            return browser.waitUntil(function() {
                return componentMenu.elements.getComponents().then(function(components) {
                    if (components.length > currentNumOfComponents) {
                        currentNumOfComponents = components.length;
                        browser.scrollToBottom(componentMenu.elements.getComponentsScrollElement());
                    }

                    return components.length >= expectedNumComponents;
                });
            }, 'failed to scroll all components');
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Assertions
    // --------------------------------------------------------------------------------------------------
    componentMenu.assertions = {
        assertComponentMenuIsInToolbar: function() {
            expect(componentMenu.elements.getComponentMenuButton().isPresent()).toBe(true, 'Expected component menu button to be displayed');
        },
        assertComponentMenuIsDisplayed: function() {
            expect(componentMenu.elements.getComponentMenu().isDisplayed()).toBe(true, 'Expected component menu to be displayed.');
        },

        // Component Types
        assertComponentMenuIsInComponentTypesTab: function() {
            expect(componentMenu.elements.getComponentTypesTabHeaderSelector()).toContainClass('active');
        },
        assertComponentTypesTabHasSupportedTypes: function(filter) {
            var expectedItems = componentMenu.utils.filterComponentTypes(filter);
            expect(componentMenu.elements.getComponentTypes()).toEqual(expectedItems);
        },

        // Components Tab
        assertComponentMenuIsInComponentsTab: function() {
            expect(componentMenu.elements.getComponentsTabHeaderSelector()).toContainClass('active');
        },
        assertCatalogVersionsSelectorIsDisplayed: function() {
            expect(componentMenu.elements.getCatalogVersionDropdown().isDisplayed()).toBe(true);
        },
        assertComponentsTabHasExistingComponentsForCatalogVersion: function(catalogVersionName, pageNum, filter) {
            var expectedItems = componentMenu.utils.filterComponents(catalogVersionName, pageNum, filter);
            expect(componentMenu.elements.getComponents()).toEqual(expectedItems);
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Utils
    // --------------------------------------------------------------------------------------------------
    componentMenu.utils = {
        filterComponentTypes: function(filter) {
            return this.filterItems(componentTypes, filter);
        },
        filterComponents: function(catalogVersionName, pageNum, filter) {
            var collection = components[catalogVersionName];
            var numberOfComponents = pageNum * componentMenu.constants.PAGE_SIZE;

            return this.filterItems(collection, filter).slice(0, numberOfComponents);
        },
        filterItems: function(collection, filter) {
            if (!filter || filter === '') {
                return collection;
            }

            return collection.filter(function(item) {
                return item.indexOf(filter) !== -1;
            });
        },
    };

    return componentMenu;
}());
