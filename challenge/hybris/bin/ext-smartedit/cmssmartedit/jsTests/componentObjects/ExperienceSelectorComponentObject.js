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

    var experienceSelectorObject = {};

    experienceSelectorObject.assertions = {
        contentCatalogDropdownIsEditabled: function() {
            expect(experienceSelectorObject.elements.contentCatalogDropdown().getAttribute('disabled')).toBeFalsy();
        },
        contentCatalogDropdownIsNotEditabled: function() {
            expect(experienceSelectorObject.elements.contentCatalogDropdown().getAttribute('disabled')).toBeTruthy();
        }
    };

    experienceSelectorObject.actions = {
        openExperienceSelector: function() {
            return browser.waitUntilNoModal().then(function() {
                return browser.click(experienceSelectorObject.elements.experienceSelectorButtonSelector()).then(function() {
                    browser.waitUntil(EC.presenceOf(experienceSelectorObject.elements.contentCatalogDropdown()), 'Expected modal to be opened');
                });
            });
        }
    };

    experienceSelectorObject.elements = {
        experienceSelectorButtonSelector: function() {
            return by.id('experience-selector-btn', 'Experience Selector button not found');
        },
        submitButtonSelector: function() {
            return by.id('submit', 'Experience Selector Submit Button not found');
        },
        cancelButtonSelector: function() {
            return by.id('cancel', 'Experience Selector Cancel Button not found');
        },
        contentCatalogDropdown: function() {
            return element(by.css('div[id="previewCatalog"] .se-generic-editor-dropdown'));
        },
    };

    return experienceSelectorObject;

})();
