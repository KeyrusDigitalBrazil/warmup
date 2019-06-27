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
/* jshint undef:false */
var yDropDownComponent;
if (typeof require !== 'undefined') {
    yDropDownComponent = require('./yDropDownComponentObject');
}

module.exports = (function() {
    var cmsLinkObject = {};

    cmsLinkObject.assertions = {
        // Product Catalog field
        productCatalogSelectorIsPresent: function() {
            expect(cmsLinkObject.elements.getProductCatalogField().isPresent()).toBe(true,
                "Expected product catalog selector to be visible");
        },

        // Category field
        categoryIsPresent: function() {
            expect(cmsLinkObject.elements.getItemsSelectorDropdownField().isPresent()).toBe(true,
                "Expected category to be visible");
        },

        // Product field
        productIsPresent: function() {
            expect(cmsLinkObject.elements.getItemsSelectorDropdownField().isPresent()).toBe(true,
                "Expected product to be visible");
        },

        // External Link field
        externalLinkIsPresent: function() {
            expect(getShortString('url').isPresent()).toBe(true,
                "Expected external link to be visible");
        },
        externalLinkIsEmpty: function() {
            expect(getShortString('url').getAttribute('value')).toBe('',
                "Expect external url link to be empty");
        },

        // Content Page field
        contentPageIsPresent: function() {
            expect(cmsLinkObject.elements.getContentPageField().isPresent()).toBe(true,
                "Expected content page to be visible");
        },
        contentPageIsPopulated: function() {
            expect(cmsLinkObject.elements.getContentPageItems().count()).not.toBe(0,
                "Expected content page to be populated");
        },

        //Component name field
        componentNameContainsText: function(text) {
            expect(getLocalizedShortString('linkName').getAttribute('value')).toContain(text,
                "Expect url link name to contain text \"" + text + "\"");
        }
    };

    cmsLinkObject.actions = {
        chooseMode: function(mode) {
            var el = cmsLinkObject.elements.getCmsLinkToField();
            return yDropDownComponent.actions.selectDropdownItemByText(el, mode);
        },

        //UrlLinkName field
        enterComponentName: function(text) {
            return enterLocalizedShortStringText('linkName', text);
        },

        enterExternalLinkUrlField: function(text) {
            return enterShortStringText('url', text);
        },

        setExternalLinkData: function(linkName, url) {
            return this.chooseMode('External').then(function() {
                return this.enterComponentName(linkName).then(function() {
                    return this.enterExternalLinkUrlField(url);
                }.bind(this));
            }.bind(this));
        }
    };

    cmsLinkObject.elements = {
        // Product Catalog field
        getProductCatalogField: function() {
            return browser.findElement(by.css('se-dropdown[data-id="se-catalog-selector-dropdown"] y-select'), false);
        },

        getItemsSelectorDropdownField: function() {
            return browser.findElement(by.css('se-dropdown[data-id="se-items-selector-dropdown"] y-select'));
        },

        // CMSLinkTo field
        getCmsLinkToField: function() {
            return element(by.tagName('cms-link-to-select'));
        },

        // Content Page field
        getContentPageField: function() {
            return element(by.id('contentPage'));
        },
        getContentPageItems: function() {
            return yDropDownComponent.elements.getDropdownItems(this.getContentPageField());
        }
    };

    return cmsLinkObject;
}());
