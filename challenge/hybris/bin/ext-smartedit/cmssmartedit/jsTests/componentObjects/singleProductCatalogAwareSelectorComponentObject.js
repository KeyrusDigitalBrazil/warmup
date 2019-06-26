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
var yDropDownComponent;
if (typeof require !== 'undefined') {
    yDropDownComponent = require('./yDropDownComponentObject');
}

module.exports = (function() {
    var componentObject = {};

    componentObject.assertions = {
        productCatalogSelectorIsPresent: function() {
            expect(componentObject.elements.getProductCatalogField().isPresent()).toBe(true,
                "Expected product catalog selector to be visible");
        },
        productCatalogLabelHasText: function(expectedText) {
            expect(componentObject.elements.getProductCatalogLabel().getText()).toBe(expectedText,
                'Expected product catalog text to be ' + expectedText);
        },
        productCatalogIsNotSelected: function() {
            expect(componentObject.elements.getProductCatalogSelectedItem().getText()).toBe("",
                "Expected catalog not to be selected");
        },
        productCatalogHasSelectedItem: function(expectedItemText) {
            expect(componentObject.elements.getProductCatalogSelectedItem().getText()).toBe(expectedItemText,
                "Expected product catalog selected item item to be " + expectedItemText);
        },
        productIsPopulated: function() {
            expect(componentObject.elements.getProductItems().count()).not.toBe(0,
                "Expected product to be populated");
        },
        productHasSelectedItem: function(expectedItemText) {
            expect(componentObject.elements.getProductSelectedItem().getText()).toBe(expectedItemText,
                "Expected product selected item item to be " + expectedItemText);
        },
        productIsPresent: function() {
            expect(componentObject.elements.getProductField().isPresent()).toBe(true,
                "Expected product to be visible");
        },
        productIsNotPopulated: function() {
            expect(componentObject.elements.getProductItems().count()).toBe(0,
                "Expected product not to be populated");
        },
        categoryIsPopulated: function() {
            expect(componentObject.elements.getCategoryItems().count()).not.toBe(0,
                "Expected category to be populated");
        },
        categoryIsPresent: function() {
            expect(componentObject.elements.getCategoryField().isPresent()).toBe(true,
                "Expected category to be visible");
        },
        categoryIsNotPopulated: function() {
            expect(componentObject.elements.getCategoryItems().count()).toBe(0,
                "Expected category not to be populated");
        }
    };

    componentObject.actions = {
        selectProductCatalog: function() {
            var el = componentObject.elements.getProductCatalogField();
            return yDropDownComponent.actions.selectDropdownItemByText(el, 'Apparel Product Catalog');
        }
    };

    componentObject.elements = {
        getProductCatalogField: function() {
            return browser.findElement(by.css('se-dropdown[data-id="se-catalog-selector-dropdown"] y-select'), false);
        },
        getProductCatalogLabel: function() {
            return browser.findElement(by.css('#product-catalog label'));
        },
        getProductField: function() {
            return browser.findElement(by.css('se-dropdown[data-id="se-items-selector-dropdown"] y-select'));
        },
        getProductItems: function() {
            return yDropDownComponent.elements.getDropdownItems(this.getProductField());
        },
        getCategoryField: function() {
            return browser.findElement(by.css('se-dropdown[data-id="se-items-selector-dropdown"] y-select'));
        },
        getCategoryItems: function() {
            return yDropDownComponent.elements.getDropdownItems(this.getCategoryField());
        },
        getProductCatalogSelectedItem: function() {
            var el = this.getProductCatalogField();
            return el.element(by.css('.y-select-item-printer'));
        },
        getProductSelectedItem: function() {
            var el = this.getProductField();
            return el.element(by.css('.y-select-item-printer'));
        }
    };

    return componentObject;
}());
