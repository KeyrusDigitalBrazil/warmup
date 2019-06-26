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
var catalogAwareSelector, dateTimeSelector;
if (typeof require !== 'undefined') {
    catalogAwareSelector = require('./catalogAwareSelectorComponentObject.js');
    dateTimeSelector = require('./dateTimeSelectorComponentObject.js');
}

module.exports = (function() {

    var restrictionsEditor = {};

    restrictionsEditor.constants = {
        EXISTING_TIME_RESTRICTION_NAME: 'Some Time restriction A',
        NEW_TIME_RESTRICTION_NAME: 'New Time Restriction',
        EXISTING_CATEGORY_RESTRICTION_NAME: 'some cat restriction',
        NEW_CATEGORY_RESTRICTION_NAME: 'New Category Restriction',
        EXISTING_USER_RESTRICTION_NAME: 'User restriction 2',
        EXISTING_USERGROUP_RESTRICTION_NAME: 'User Group Restriction 1',
        NEW_USERGROUP_RESTRICTION_NAME: 'New User Group Restriction',

        // Restriction Types
        TIME_RESTRICTION_TYPE: 'Time Restriction',
        CATEGORY_RESTRICTION_TYPE: 'category Restriction',
        USER_RESTRICTION_TYPE: 'User Restriction',
        USERGROUP_RESTRICTION_TYPE: 'User Group Restriction',

        // CSS Classes
        RESTRICTION_TYPE_SELECTOR_ID: '#restriction-type-selector',
        RESTRICTION_NAME_SELECTOR_ID: '#restriction-name-selector',
        RESTRICTION_TABLE_ITEM: {
            HEADER: '.ySERestrictionsNameHeader',
            DESCRIPTION: '.ySERestrictionsDescription'
        },

        // Others
        CATEGORIES_SELECTOR_ID: 'categories'
    };

    restrictionsEditor.elements = {
        // Restrictions List
        getAddNewButton: function() {
            return element(by.css('.y-add-btn'));
        },
        getRestrictionListSize: function() {
            return element.all(by.css('.se-restrictions-list--item')).count();
        },
        getRestrictionInListByName: function(restrictionName) {
            var restrictionElements = element.all(by.css('.se-restrictions-list--item'));
            return restrictionElements.filter(function(elem) {
                return elem.element(by.css(restrictionsEditor.constants.RESTRICTION_TABLE_ITEM.HEADER)).getText().then(function(text) {
                    return text.toUpperCase() === restrictionName.toUpperCase();
                });
            }).first();
        },
        getRestrictionDescriptionByName: function(restrictionName) {
            return restrictionsEditor.elements.getRestrictionInListByName(restrictionName).element(by.css(restrictionsEditor.constants.RESTRICTION_TABLE_ITEM.DESCRIPTION));
        },
        getRestrictionMenuByName: function(restrictionName) {
            return restrictionsEditor.elements.getRestrictionInListByName(restrictionName).all(by.css('button.dropdown-toggle')).first();
        },
        getRestrictionEditButtonByName: function(restrictionName) {
            return restrictionsEditor.elements.getRestrictionInListByName(restrictionName).all(by.cssContainingText('a', 'Edit')).first();
        },
        getRestrictionDeleteButtonByName: function(restrictionName) {
            return restrictionsEditor.elements.getRestrictionInListByName(restrictionName).all(by.cssContainingText('a', 'Remove')).first();
        },
        getApplyRuleDropdown: function() {
            return element(by.css('.ySERestriction-select .ui-select-toggle'));
        },
        getAnyRestrictionOption: function() {
            return element(by.cssContainingText('.ySERestriction-select li div', 'Apply any restriction'));
        },
        getClearAllButton: function() {
            return element(by.css('.cms-clean-btn'));
        },
        getRestrictionsWithErrorsCount: function() {
            return element.all(by.css(restrictionsEditor.constants.RESTRICTION_TABLE_ITEM.HEADER + '.error-input')).count();
        },

        // Panel
        getSliderPanel: function() {
            return element(by.css('.sliderpanel-container'));
        },
        getRestrictionTypeDropdown: function() {
            return element(by.css(restrictionsEditor.constants.RESTRICTION_TYPE_SELECTOR_ID + ' .ui-select-toggle'));
        },
        getRestrictionTypeOption: function(optionValue) {
            return element(by.cssContainingText(restrictionsEditor.constants.RESTRICTION_TYPE_SELECTOR_ID + ' ul li span', optionValue));
        },
        getRestrictionsSearchBox: function() {
            return element(by.css(restrictionsEditor.constants.RESTRICTION_NAME_SELECTOR_ID + ' .ui-select-toggle'));
        },
        getRestrictionsSearchBoxInput: function() {
            return element(by.css(restrictionsEditor.constants.RESTRICTION_NAME_SELECTOR_ID + ' .ui-select-search'));
        },
        getRestrictionSearchBoxOption: function(optionValue) {
            return element(by.cssContainingText(restrictionsEditor.constants.RESTRICTION_NAME_SELECTOR_ID + ' ul li span', optionValue));
        },
        getRestrictionSearchBoxNewButton: function() {
            return element(by.cssContainingText('button', 'yationablesearchitem.action.create'));
        },
        getAddRestrictionButton: function() {
            return element(by.cssContainingText('button', 'se.cms.restriction.management.panel.button.add'));
        },
        getUpdateRestrictionButton: function() {
            return element(by.cssContainingText('button', 'se.cms.restriction.management.panel.button.save'));
        },
        getErrorMessageByFieldName: function(fieldName) {
            return element(by.css('#' + fieldName + ' .se-help-block--has-error')).getText();
        },
        getSliderPanelCloseButton: function() {
            return element(by.css('.btn-sliderpanel__close'));
        },

        // All Restrictions
        getRestrictionNameField: function() {
            return element(by.css('input#name-shortstring'));
        },

        // Time Restrictions
        getTimeRestrictionActiveFromField: function() {
            return element(by.css('div#date-time-picker-activeFrom input.se-date-field--input'));
        },
        getTimeRestrictionActiveUntilField: function() {
            return element(by.css('div#date-time-picker-activeUntil input.se-date-field--input'));
        },

        // Category Restriction
        getCategoryRestrictionRecursiveToggle: function() {
            return element(by.css('input#recursive-checkbox'));
        },
        getCategoryRestrictionCategoriesSelector: function() {
            return element(by.css('y-editable-list[data-id="categories_list"]'));
        },
        getMenuForCategory: function(categoryName) {
            return restrictionsEditor.elements.getCategoryRestrictionCategoriesSelector()
                .element(by.cssContainingText('ol li .se-product-row__product', categoryName))
                .element(by.xpath('..'))
                .element(by.css('.y-dropdown-more-menu'));
        },
        getDeleteCategoryButton: function(categoryName) {
            return restrictionsEditor.elements.getMenuForCategory(categoryName)
                .element(by.cssContainingText('.y-dropdown-more-menu a', 'Delete'));
        },

        // User Group Restrictions
        getUserGroupRestrictionApplySubGroupsToggle: function() {
            return element(by.css('input#includeSubgroups-checkbox'));
        },
        getUserGroupsSelector: function() {
            return element(by.css('#userGroups se-dropdown y-select'));
        },
        getUserGroupsSelectorClickableArea: function() {
            return element(by.css('#userGroups .ui-select-search'));
        },
        getUserGroupInDropdown: function(userGroup) {
            return restrictionsEditor.elements.getUserGroupsSelector()
                .all(by.cssContainingText('ul li span', userGroup)).first();
        },
        getUserGroupLabelInSelector: function(userGroupName) {
            return restrictionsEditor.elements.getUserGroupsSelector()
                .element(by.cssContainingText('item-printer span', userGroupName));
        },
        getUserGroupDeleteButton: function(userGroupName) {
            return restrictionsEditor.elements.getUserGroupsSelector()
                .element(by.xpath("//li[descendant::item-printer/descendant::span[contains(text(),'" + userGroupName + "')]]/a"));
        },
        getUserGroupsSelectorInput: function() {
            return restrictionsEditor.elements.getUserGroupsSelector()
                .element(by.css('.select2-search-field input'));
        },

        // Other Restrictions
        getNotEditableWarning: function() {
            return element(by.cssContainingText('div', 'se.cms.restriction.management.select.type.not.supported.warning'));
        },

        // Message
        getRestrictionsYMessage: function() {
            return element(by.css('restrictions-editor y-message'));
        }
    };

    restrictionsEditor.actions = {
        // Restriction List Actions
        openRestrictionDropdownMenu: function(restrictionName) {
            return browser.click(restrictionsEditor.elements.getRestrictionMenuByName(restrictionName));
        },
        clickAddNewRestrictionButton: function() {
            return browser.click(restrictionsEditor.elements.getAddNewButton());
        },
        openSliderPanel: function() {
            restrictionsEditor.actions.clickAddNewRestrictionButton();
            return browser.waitForVisibility(restrictionsEditor.elements.getSliderPanel());
        },
        openRestrictionForEditing: function(restrictionName) {
            return restrictionsEditor.actions.openRestrictionDropdownMenu(restrictionName).then(function() {
                browser.click(restrictionsEditor.elements.getRestrictionEditButtonByName(restrictionName));
            });
        },
        removeRestriction: function(restrictionName) {
            return restrictionsEditor.actions.openRestrictionDropdownMenu(restrictionName).then(function() {
                return browser.click(restrictionsEditor.elements.getRestrictionDeleteButtonByName(restrictionName));
            });
        },
        openRuleDropdown: function() {
            return browser.click(restrictionsEditor.elements.getApplyRuleDropdown());
        },
        applyAnyRestriction: function() {
            return restrictionsEditor.actions.openRuleDropdown().then(function() {
                return browser.click(restrictionsEditor.elements.getAnyRestrictionOption());
            });
        },
        clickClearAllButton: function() {
            return browser.click(restrictionsEditor.elements.getClearAllButton());
        },

        // Restriction Editing Panel
        openRestrictionTypeDropdown: function() {
            return browser.click(restrictionsEditor.elements.getRestrictionTypeDropdown());
        },
        openRestrictionsSearchBox: function() {
            return browser.click(restrictionsEditor.elements.getRestrictionsSearchBox());
        },
        selectRestrictionType: function(restrictionType) {
            return restrictionsEditor.actions.openRestrictionTypeDropdown()
                .then(function() {
                    return browser.click(restrictionsEditor.elements.getRestrictionTypeOption(restrictionType));
                });
        },
        selectRestrictionInSearchBoxByExactName: function(restrictionName) {
            return restrictionsEditor.actions.openRestrictionsSearchBox().then(function() {
                return browser.click(restrictionsEditor.elements.getRestrictionSearchBoxOption(restrictionName));
            });
        },
        searchRestriction: function(searchTerm) {
            return restrictionsEditor.actions.openRestrictionsSearchBox().then(function() {
                browser.sendKeys(restrictionsEditor.elements.getRestrictionsSearchBoxInput(), searchTerm);
            });
        },
        createEmptyRestriction: function(restrictionName) {
            return restrictionsEditor.actions.searchRestriction(restrictionName).then(function() {
                return browser.click(restrictionsEditor.elements.getRestrictionSearchBoxNewButton());
            });
        },
        clickAddRestrictionButton: function() {
            return browser.click(restrictionsEditor.elements.getAddRestrictionButton());
        },
        clickUpdateRestrictionButton: function() {
            return browser.click(restrictionsEditor.elements.getUpdateRestrictionButton());
        },
        clickSliderPanelCloseButton: function() {
            return browser.click(restrictionsEditor.elements.getSliderPanelCloseButton());
        },

        // Time Restrictions
        setTimeRestrictionData: function(restrictionData) {
            browser.clear(restrictionsEditor.elements.getTimeRestrictionActiveFromField());
            browser.clear(restrictionsEditor.elements.getTimeRestrictionActiveUntilField());

            dateTimeSelector.actions.setDateTimeInField('activeFrom', restrictionData.activeFrom);
            dateTimeSelector.actions.setDateTimeInField('activeUntil', restrictionData.activeUntil);
        },
        addExistingTimeRestriction: function(restrictionName) {
            var restrictionType = restrictionsEditor.constants.TIME_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.selectRestrictionInSearchBoxByExactName(restrictionName);
                });
            });
        },
        createNewTimeRestriction: function(newRestrictionName, timeRestrictionData) {
            var restrictionType = restrictionsEditor.constants.TIME_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.createEmptyRestriction(newRestrictionName).then(function() {
                        return restrictionsEditor.actions.setTimeRestrictionData(timeRestrictionData);
                    });
                });
            });
        },
        updateTimeRestrictionData: function(restrictionName, timeRestrictionData) {
            return restrictionsEditor.actions.openRestrictionForEditing(restrictionName).then(function() {
                return restrictionsEditor.actions.setTimeRestrictionData(timeRestrictionData);
            });
        },

        // Category Restrictions
        addExistingCategoryRestriction: function(restrictionName) {
            var restrictionType = restrictionsEditor.constants.CATEGORY_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.selectRestrictionInSearchBoxByExactName(restrictionName);
                });
            });
        },
        setCategoryRestrictionData: function(restrictionData) {
            restrictionsEditor.utils.toggleBooleanField(restrictionsEditor.elements.getCategoryRestrictionRecursiveToggle(), restrictionData.recursive).then(function() {
                return catalogAwareSelector.actions.selectItems(restrictionsEditor.constants.CATEGORIES_SELECTOR_ID, restrictionData.categories).then(function() {
                    catalogAwareSelector.actions.clickAddItemsButton();
                });
            });
        },
        createNewCategoryRestriction: function(newRestrictionName, categoryRestrictionData) {
            var restrictionType = restrictionsEditor.constants.CATEGORY_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.createEmptyRestriction(newRestrictionName).then(function() {
                        return restrictionsEditor.actions.setCategoryRestrictionData(categoryRestrictionData);
                    });
                });
            });
        },
        openMenuForCategory: function(categoryName) {
            return browser.click(restrictionsEditor.elements.getMenuForCategory(categoryName));
        },
        removeCategory: function(categoryName) {
            return restrictionsEditor.actions.openMenuForCategory(categoryName).then(function() {
                return browser.click(restrictionsEditor.elements.getDeleteCategoryButton(categoryName));
            });
        },
        removeCategoryFromRestriction: function(restrictionName, categoryName) {
            return restrictionsEditor.actions.openRestrictionForEditing(restrictionName).then(function() {
                return restrictionsEditor.actions.removeCategory(categoryName);
            });
        },

        // UserGroup Restrictions
        selectUserGroupInDropdown: function(userGroupName) {
            return browser.click(restrictionsEditor.elements.getUserGroupInDropdown(userGroupName));
        },
        openUserGroupsDropdown: function() {
            return browser.click(restrictionsEditor.elements.getUserGroupsSelectorClickableArea());
        },
        selectUserGroups: function(userGroups) {
            var promisesToResolve = [];

            userGroups.forEach(function(userGroup) {
                promisesToResolve.push(restrictionsEditor.actions.openUserGroupsDropdown().then(function() {
                    return restrictionsEditor.actions.selectUserGroupInDropdown(userGroup);
                }));
            });

            return protractor.promise.all(promisesToResolve);
        },
        addExistingUserGroupRestriction: function(restrictionName) {
            var restrictionType = restrictionsEditor.constants.USERGROUP_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.selectRestrictionInSearchBoxByExactName(restrictionName);
                });
            });
        },
        setUserGroupRestrictionData: function(restrictionData) {
            restrictionsEditor.utils.toggleBooleanField(restrictionsEditor.elements.getUserGroupRestrictionApplySubGroupsToggle(), restrictionData.applyRestriction).then(function() {
                return restrictionsEditor.actions.selectUserGroups(restrictionData.userGroups);
            });
        },
        createNewUserGroupRestriction: function(newRestrictionName, userGroupRestrictionData) {
            var restrictionType = restrictionsEditor.constants.USERGROUP_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.createEmptyRestriction(newRestrictionName).then(function() {
                        return restrictionsEditor.actions.setUserGroupRestrictionData(userGroupRestrictionData);
                    });
                });
            });
        },
        removeUserGroup: function(userGroupToDelete) {
            return browser.click(restrictionsEditor.elements.getUserGroupDeleteButton(userGroupToDelete));
        },
        removeUserGroupFromRestriction: function(restrictionName, userGroupToDelete) {
            return restrictionsEditor.actions.openRestrictionForEditing(restrictionName).then(function() {
                return restrictionsEditor.actions.removeUserGroup(userGroupToDelete);
            });
        },

        // Other Restrictions
        addExistingNonSupportedRestriction: function(restrictionName) {
            var restrictionType = restrictionsEditor.constants.USER_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.selectRestrictionInSearchBoxByExactName(restrictionName);
                });
            });
        },
        tryToCreateNewNonSupportedRestriction: function(newRestrictionName) {
            var restrictionType = restrictionsEditor.constants.USER_RESTRICTION_TYPE;
            return restrictionsEditor.actions.openSliderPanel().then(function() {
                return restrictionsEditor.actions.selectRestrictionType(restrictionType).then(function() {
                    return restrictionsEditor.actions.searchRestriction(newRestrictionName);
                });
            });
        }
    };

    restrictionsEditor.assertions = {
        listHasExpectedNumberOfRestrictions: function(expectedNumberOfRestrictions) {
            expect(restrictionsEditor.elements.getRestrictionListSize()).toBe(expectedNumberOfRestrictions,
                'Expected number of restrictions in the list to be ' + expectedNumberOfRestrictions);
        },
        restrictionIsNotInList: function(restrictionName) {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionInListByName(restrictionName), 'Expected restriction ' + restrictionName + ' not to be in the list of selected restrictions');
        },
        restrictionIsInList: function(restrictionName) {
            expect(restrictionsEditor.elements.getRestrictionInListByName(restrictionName).isPresent()).toBe(true,
                'Expected restriction ' + restrictionName + ' to be in the list of selected restrictions');
        },

        // Time Restrictions
        timeRestrictionPanelIsNotEditable: function() {
            expect(restrictionsEditor.utils.isTextFieldEditable(restrictionsEditor.elements.getRestrictionNameField())).toBe(false, 'Expected name field to be disabled');
            expect(restrictionsEditor.utils.isDateFieldEditable(restrictionsEditor.elements.getTimeRestrictionActiveFromField())).toBe(false, 'Expected active from field to be disabled');
            expect(restrictionsEditor.utils.isDateFieldEditable(restrictionsEditor.elements.getTimeRestrictionActiveUntilField())).toBe(false, 'Expected active until field to be disabled');
        },
        timeRestrictionHasRightData: function(restrictionName, restrictionData) {
            var expectedLabel = 'Page only applies from ' + restrictionData.activeFrom + ' to ' + restrictionData.activeUntil;

            expect(restrictionsEditor.elements.getRestrictionDescriptionByName(restrictionName).getText()).toBe(expectedLabel);
        },
        timeRestrictionIsInEditMode: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionTypeDropdown(), 'Expected type dropdown not to be present in edit mode');
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionsSearchBox(), 'Expected restriction search box not to be present in edit mode');

            expect(restrictionsEditor.elements.getRestrictionNameField().isPresent()).toBe(true, 'Expected nameField to be present in edit mode');
            expect(restrictionsEditor.elements.getTimeRestrictionActiveFromField().isPresent()).toBe(true, 'Expected activeFromField to be present in edit mode');
            expect(restrictionsEditor.elements.getTimeRestrictionActiveUntilField().isPresent()).toBe(true, 'Expected activeUnitlField to be present in edit mode');
        },

        // Category Restrictions
        categoryRestrictionPanelIsNotEditable: function() {
            expect(restrictionsEditor.utils.isTextFieldEditable(restrictionsEditor.elements.getRestrictionNameField()))
                .toBe(false, 'Expected name field to be disabled');
            expect(restrictionsEditor.utils.isToggleFieldEditable(restrictionsEditor.elements.getCategoryRestrictionRecursiveToggle()))
                .toBe(false, 'Expected toggle to be disabled');
            expect(restrictionsEditor.utils.isCategoriesSelectorEditable(restrictionsEditor.elements.getCategoryRestrictionCategoriesSelector()))
                .toBe(false, 'Expected categories selector to be disabled');
        },
        categoryRestrictionHasRightData: function(restrictionName, restrictionData) {
            var expectedLabel = 'Page only applies on categories: ' + restrictionData.categories.Staged + ';';
            if (restrictionData.categories.Online) {
                expectedLabel += ' ' + restrictionData.categories.Online + ';';
            }

            expect(restrictionsEditor.elements.getRestrictionDescriptionByName(restrictionName).getText()).toBe(expectedLabel);
        },
        categoryRestrictionIsInEditMode: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionTypeDropdown(), 'Expected type dropdown not to be present in edit mode');
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionsSearchBox(), 'Expected restriction search box not to be present in edit mode');

            expect(restrictionsEditor.elements.getRestrictionNameField().isPresent()).toBe(true, 'Expected nameField to be present in edit mode');
            expect(restrictionsEditor.elements.getCategoryRestrictionRecursiveToggle().isPresent()).toBe(true, 'Expected recursiveToggle to be present in edit mode');
            expect(restrictionsEditor.elements.getCategoryRestrictionCategoriesSelector().isPresent()).toBe(true, 'Expected categoriesSelector to be present in edit mode');
        },

        // Usergroup Restrictions
        userGroupRestrictionPanelIsNotEditable: function() {
            expect(restrictionsEditor.utils.isTextFieldEditable(restrictionsEditor.elements.getRestrictionNameField()))
                .toBe(false, 'Expected name field to be disabled');
            expect(restrictionsEditor.utils.isToggleFieldEditable(restrictionsEditor.elements.getUserGroupRestrictionApplySubGroupsToggle()))
                .toBe(false, 'Expected toggle to be disabled');
            expect(restrictionsEditor.utils.isDropdownEditable())
                .toBe(false, 'Expected dropdown to be disabled');
        },
        userGroupRestrictionHasRightData: function(restrictionName, restrictionData) {
            var expectedLabel = 'Page only applies on usergroups:';
            restrictionData.userGroups.forEach(function(userGroupName) {
                expectedLabel += ' (' + userGroupName + ');';
            });

            expect(restrictionsEditor.elements.getRestrictionDescriptionByName(restrictionName).getText()).toBe(expectedLabel);
        },
        userGroupRestrictionIsInEditMode: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionTypeDropdown(), 'Expected type dropdown not to be present in edit mode');
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionsSearchBox(), 'Expected restriction search box not to be present in edit mode');

            expect(restrictionsEditor.elements.getRestrictionNameField().isPresent()).toBe(true, 'Expected nameField to be present in edit mode');
            expect(restrictionsEditor.elements.getUserGroupRestrictionApplySubGroupsToggle().isPresent()).toBe(true, 'Expected applySubgroupsToggle to be present in edit mode');
            expect(restrictionsEditor.elements.getUserGroupsSelector().isPresent()).toBe(true, 'Expected groupsSelector to be present in edit mode');
        },
        userGroupSelectorContainsGroups: function(userGroups) {
            userGroups.forEach(function(userGroup) {
                expect(restrictionsEditor.elements.getUserGroupLabelInSelector(userGroup).isPresent()).toBe(true, 'Expected user ' + userGroup + ' group to be selected');
            });
        },

        // Others
        restrictionPanelIsEmptyAndNotEditable: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionNameField(), 'Expected nameField not to be present');
        },
        restrictionCannotBeEdited: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionTypeDropdown(), 'Expected type dropdown not to be present in edit mode');
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionsSearchBox(), 'Expected restriction search box not to be present in edit mode');
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionNameField(), 'Expected nameField not to be present');

            expect(restrictionsEditor.elements.getNotEditableWarning().isPresent()).toBe(true, 'Expected warning to be displayed.');
        },

        // Message
        assertRestrictionsYMessageIsDisplayed: function() {
            expect(restrictionsEditor.elements.getRestrictionsYMessage().isPresent()).toBe(true);
        },
        assertRestrictionsYMessageIsNotDisplayed: function() {
            browser.waitForAbsence(restrictionsEditor.elements.getRestrictionsYMessage());

        }

    };

    restrictionsEditor.utils = {
        isTextFieldEditable: function(field) {
            return field.getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        isDateFieldEditable: function(field) {
            var DISABLED_CLASS_NAME = 'se-input--is-disabled';
            return field.getAttribute('class').then(function(classes) {
                return classes.split(' ').indexOf(DISABLED_CLASS_NAME) !== -1;
            }).then(function(fieldHasDisabledClass) {
                return !fieldHasDisabledClass;
            });
        },
        isToggleFieldEditable: function(field) {
            return field.getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        isCategoriesSelectorEditable: function(field) {
            var DISABLED_CLASS_NAME = 'y-editable-list-disabled';
            return field.all(by.css('div')).first().getAttribute('class').then(function(classes) {
                return classes.split(' ').indexOf(DISABLED_CLASS_NAME) !== -1;
            }).then(function(itemHasDisabledClass) {
                return !itemHasDisabledClass;
            });
        },
        isDropdownEditable: function() {
            return restrictionsEditor.elements.getUserGroupsSelectorInput().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        toggleBooleanField: function(field, toggleOn) {
            var SELECTED_CLASS_NAME = 'ng-not-empty';
            return field.getAttribute('class').then(function(classes) {
                return classes.split(' ').indexOf(SELECTED_CLASS_NAME) !== -1;
            }).then(function(isSelected) {
                if (toggleOn !== isSelected) {
                    return browser.click(field.element(by.xpath('..')));
                }
            });
        }
    };

    return restrictionsEditor;

}());
