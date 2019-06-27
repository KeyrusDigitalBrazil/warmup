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
var genericEditor, catalogAwareSelector;
if (typeof require !== 'undefined') {
    genericEditor = require('./genericEditorComponentObject.js');
    catalogAwareSelector = require('./catalogAwareSelectorComponentObject.js');
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {
        TITLE_FIELD_ID: 'title',
        CATEGORY_SELECTOR_ID: 'categories',
        PRODUCT_SELECTOR_ID: 'products'
    };

    componentObject.elements = {

    };

    componentObject.actions = {
        prepareApp: function(done, isProductsEditable, isCategoriesEditable) {
            browser.executeScript('window.sessionStorage.setItem("productsEditable", arguments[0])', isProductsEditable);
            browser.executeScript('window.sessionStorage.setItem("categoriesEditable", arguments[0])', isCategoriesEditable);
            done();
        },
        setTitle: function(titleValues) {
            return genericEditor.actions.setTextValueInLocalizedField(componentObject.constants.TITLE_FIELD_ID, titleValues);
        },
        setProducts: function(productsList) {
            return catalogAwareSelector.actions.selectItems(componentObject.constants.PRODUCT_SELECTOR_ID, productsList).then(function() {
                return catalogAwareSelector.actions.clickAddItemsButton();
            });
        },
        setCategories: function(categoriesList) {
            return catalogAwareSelector.actions.selectItems(componentObject.constants.CATEGORY_SELECTOR_ID, categoriesList).then(function() {
                return catalogAwareSelector.actions.clickAddItemsButton();
            });
        },
        setProductCarouselData: function(productCarouselData) {
            return componentObject.actions.setTitle(productCarouselData.title).then(function() {
                return componentObject.actions.setProducts(productCarouselData.products);
            }).then(function() {
                return componentObject.actions.setCategories(productCarouselData.categories);
            });
        },
        moveProductUp: function(itemIndex) {
            return catalogAwareSelector.actions.moveItemUp(componentObject.constants.PRODUCT_SELECTOR_ID, itemIndex);
        },
        moveProductDown: function(itemIndex) {
            return catalogAwareSelector.actions.moveItemDown(componentObject.constants.PRODUCT_SELECTOR_ID, itemIndex);
        },
        deleteProduct: function(itemIndex) {
            return catalogAwareSelector.actions.deleteItem(componentObject.constants.PRODUCT_SELECTOR_ID, itemIndex);
        },
        moveProductToPosition: function(originalIndex, newIndex) {
            return catalogAwareSelector.actions.dragAndDropItemToPosition(componentObject.constants.PRODUCT_SELECTOR_ID, originalIndex, newIndex);
        },
        deleteCategory: function(itemIndex) {
            return catalogAwareSelector.actions.deleteItem(componentObject.constants.CATEGORY_SELECTOR_ID, itemIndex);
        }
    };

    componentObject.assertions = {
        titleIsEmpty: function() {
            genericEditor.assertions.localizedFieldIsEmpty(componentObject.constants.TITLE_FIELD_ID);
        },
        productsListIsEmpty: function() {
            return catalogAwareSelector.assertions.isEmpty(componentObject.constants.PRODUCT_SELECTOR_ID);
        },
        categoriesListIsEmpty: function() {
            return catalogAwareSelector.assertions.isEmpty(componentObject.constants.CATEGORY_SELECTOR_ID);
        },
        componentIsEmpty: function() {
            componentObject.assertions.titleIsEmpty();
            componentObject.assertions.productsListIsEmpty();
            componentObject.assertions.categoriesListIsEmpty();
        },
        hasTitle: function(expectedTitle) {
            genericEditor.assertions.localizedFieldHasExpectedValues(componentObject.constants.TITLE_FIELD_ID, expectedTitle);
        },
        hasProducts: function(expectedProductsList) {
            catalogAwareSelector.assertions.itemsAreSelected(componentObject.constants.PRODUCT_SELECTOR_ID, expectedProductsList);
        },
        hasCategories: function(expectedCategoriesList) {
            catalogAwareSelector.assertions.itemsAreSelected(componentObject.constants.CATEGORY_SELECTOR_ID, expectedCategoriesList);
        },
        hasRightData: function(expectedComponentData) {
            componentObject.assertions.hasTitle(expectedComponentData.title);
            componentObject.assertions.hasProducts(expectedComponentData.products);
            componentObject.assertions.hasCategories(expectedComponentData.categories);
        },
        productsAreInRightOrder: function(expectedProductsList) {
            catalogAwareSelector.assertions.itemsAreInRightOrder(componentObject.constants.PRODUCT_SELECTOR_ID, expectedProductsList);
        },
        categoriesAreInRightOrder: function(expectedCategoriesList) {
            catalogAwareSelector.assertions.itemsAreInRightOrder(componentObject.constants.CATEGORY_SELECTOR_ID, expectedCategoriesList);
        },
        productsAddButtonIsNotDisplayed: function() {
            return catalogAwareSelector.assertions.addItemsButtonIsNotDisplayed(componentObject.constants.PRODUCT_SELECTOR_ID);
        },
        categoriesAddButtonIsNotDisplayed: function() {
            return catalogAwareSelector.assertions.addItemsButtonIsNotDisplayed(componentObject.constants.CATEGORY_SELECTOR_ID);
        }
    };

    componentObject.utils = {};

    return componentObject;

}());
