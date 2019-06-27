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
function RestrictionPickerPageObject(mockedRestrictionTypes, mockedRestrictions, element) {
    var RESTRICTION_RESULT_ID_PREFIX = "#restriction-search-result-id-";
    var uiSelectPO = new UiSelectPageObject(element);
    var self = this;
    this.elements = {

        getSearchField: function() {
            return element.find('#search-field-id input');
        },
        getRestrictionSearchResult: function(restrictionId) {
            return element.find(RESTRICTION_RESULT_ID_PREFIX + restrictionId);
        }
    };
    this.actions = {
        openUiSelect: function() {
            uiSelectPO.clickSelectToggle();
        },
        selectFirstRestrictionType: function() {
            uiSelectPO.clickSelectElement(0);
        },
        enterSearchValue: function(val) {
            self.elements.getSearchField().val(val).change();
        },
        selectFirstSearchResult: function() {
            self.elements.getRestrictionSearchResult(self.getFirstRestrictionOfFirstRestrictionType().uid).click();
        }
    };
    this.assertions = {
        assertFirstUiSelectTextEquals: function() {
            expect(uiSelectPO.getSelectElement(0).text().trim()).toBe(mockedRestrictionTypes.restrictionTypes[0].name.en);
        },
        assertSearchFieldDisplayed: function() {
            expect(self.isElementVisible(self.elements.getSearchField())).toBe(true);
        },
        assertSearchResultsContains: function(restrictionId) {
            expect(self.isElementVisible(self.elements.getRestrictionSearchResult(restrictionId))).toBe(true);
        }
    };

    this.isElementVisible = function(selector) {
        if (selector) {
            return selector.is(':visible');
        }
        return false;
    };

    this.getSearchStringForFirstRestrictionType = function() {
        return this.getFirstRestrictionOfFirstRestrictionType().name.charAt(0);
    };

    this.getFirstRestrictionTypeId = function() {
        return mockedRestrictionTypes.restrictionTypes[0].code;
    };

    this.getFirstRestrictionOfFirstRestrictionType = function() {
        return this.getMockRestrictionsForType(this.getFirstRestrictionTypeId())[0];
    };

    this.getMockRestrictionsForType = function(type) {
        return mockedRestrictions.restrictions.filter(function(restriction) {
            return restriction.typeCode === type;
        });
    };
}
