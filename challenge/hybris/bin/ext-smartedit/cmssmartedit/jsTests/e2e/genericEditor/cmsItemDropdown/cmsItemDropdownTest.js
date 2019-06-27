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
describe('CmsItemDropdown -', function() {

    // ---------------------------------------------------------------
    // Variables
    // ---------------------------------------------------------------
    var page = e2e.pageObjects.GenericEditor;
    var genericEditor = e2e.componentObjects.genericEditor;
    var cmsItemDropdown = e2e.componentObjects.cmsItemDropdown;
    var cmsLink = e2e.componentObjects.cmsLink;
    var confirmationModal = e2e.componentObjects.confirmationModal;
    var genericEditorBreadcrumb = e2e.componentObjects.genericEditorBreadcrumb;

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    var BANNER_FIELD_ID = 'banner';
    var BANNER_1 = 'banner1';
    var BANNER_1_NAME = 'BANNER 1';
    var BANNER_1_TYPE = 'ResponsiveBannerComponent';
    var BANNER_2 = 'banner2';
    var BANNER_2_UID = 'COMP_1';

    var TABS_FIELD_ID = 'tabs';
    var TAB_1 = 'tab1';
    var TAB_1_NAME = 'TAB 1';
    var TAB_1_TYPE = 'CmsTab';
    var TAB_2 = 'tab2';
    var TAB_2_NAME = 'TAB2';
    var TAB_3 = 'tab3';

    var LINKS_FIELD_ID = 'links';
    var LINK_1 = 'link1';
    var LINK_1_URL = 'some link1 url';

    var SINGLE_SELECT = cmsItemDropdown.constants.DROPDOWN_TYPE.SINGLE_SELECT;
    var MULTI_SELECT = cmsItemDropdown.constants.DROPDOWN_TYPE.MULTI_SELECT;
    var BANNER_COMPONENT_TYPE = 'Banner Component';

    // ---------------------------------------------------------------
    // Set Up
    // ---------------------------------------------------------------
    beforeEach(function(done) {
        browser.bootstrap(__dirname).then(function() {
            done();
        });
    });

    beforeEach(function() {
        require("../commonFunctions.js");
    });

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    describe('Common - ', function() {

        it('GIVEN the editor has cmsItemDropdown items WHEN the editor is opened THEN the save button is disabled', function() {
            // GIVEN
            page.actions.openGenericEditor();

            // THEN
            genericEditor.assertions.saveIsDisabled();
        });

    });

    describe('Single Select - ', function() {

        // Variables
        var BANNER_1_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Banner 1'
        }, {
            qualifier: 'image',
            type: 'shortString',
            value: 'some image path'
        }, {
            qualifier: 'rotate',
            type: 'boolean',
            value: true
        }];

        var NEW_BANNER_1_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Banner 1'
        }, {
            qualifier: 'image',
            type: 'shortString',
            value: 'some different image path'
        }, {
            qualifier: 'rotate',
            type: 'boolean',
            value: true
        }];

        var BANNER_2_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Banner 2'
        }, {
            qualifier: 'image',
            type: 'shortString',
            value: 'some different image path'
        }];

        it('WHEN the user selects an item in the list THEN the item is selected', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.assertions.isEmpty(BANNER_FIELD_ID);

            // WHEN
            cmsItemDropdown.actions.selectItemInDropdown(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);

            // THEN
            cmsItemDropdown.assertions.isNotEmpty(BANNER_FIELD_ID);
            cmsItemDropdown.assertions.itemIsSelected(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
        });

        it('GIVEN an item is selected WHEN the user clicks on it THEN it opens a nested editor', function() {
            // GIVEN
            var expectedNestingLevel = 2;
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);

            // WHEN
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);

            // THEN
            genericEditor.assertions.topEditorIsNested(expectedNestingLevel);
            genericEditorBreadcrumb.assertions.componentIsDisplayedInCorrectNestingLevel(BANNER_1_NAME, BANNER_1_TYPE, expectedNestingLevel);
        });

        it('GIVEN an item is selected and opened in a nested editor WHEN the user cancels it THEN the editor must close without any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            genericEditor.assertions.openEditorHasRightData(BANNER_1_DATA);

            // WHEN
            genericEditor.actions.setEditorData([NEW_BANNER_1_DATA[1]]); // The only change is in the second field.
            genericEditor.actions.cancel();
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            genericEditor.assertions.openEditorHasRightData(BANNER_1_DATA);
        });

        it('GIVEN an item is selected and opened in a nested editor WHEN the user saves it THEN the editor must close saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            genericEditor.assertions.openEditorHasRightData(BANNER_1_DATA);

            // WHEN
            genericEditor.actions.setEditorData([NEW_BANNER_1_DATA[1]]); // The only change is in the second field.
            genericEditor.actions.save();

            // THEN
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            genericEditor.assertions.openEditorHasRightData(NEW_BANNER_1_DATA);
        });

        it('GIVEN a new item is created from the dropdown WHEN the user cancels it THEN the editor must close without saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();

            // WHEN
            cmsItemDropdown.actions.openNewNestedComponentOfTypeFromDropdown(BANNER_FIELD_ID, BANNER_2, BANNER_COMPONENT_TYPE);
            genericEditor.actions.setEditorData(BANNER_2_DATA);
            genericEditor.actions.cancel();
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            cmsItemDropdown.assertions.isEmpty(BANNER_FIELD_ID);
        });

        it('GIVEN a new item is created from the dropdown WHEN the user saves it THEN the editor must close saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();

            // WHEN
            cmsItemDropdown.actions.openNewNestedComponentOfTypeFromDropdown(BANNER_FIELD_ID, BANNER_2, BANNER_COMPONENT_TYPE);

            var titleType = 'bannercomponent';
            genericEditor.actions.waitForEditorModalWithComponentNameToBeOpen(titleType);
            genericEditor.actions.setEditorData(BANNER_2_DATA);
            genericEditor.actions.save();
            genericEditor.actions.waitForEditorModalWithComponentNameToBeClosed(titleType);

            // THEN
            cmsItemDropdown.assertions.itemIsSelected(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_2_UID);
            cmsItemDropdown.actions.clickSelectedItem(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_2_UID);
            genericEditor.assertions.openEditorHasRightData(BANNER_2_DATA);
        });

    });

    describe('Multi Select - ', function() {

        var TAB_2_UID = 'COMP_1';

        var TAB_1_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Tab 1'
        }, {
            qualifier: 'title',
            type: 'shortString',
            value: 'This is tab1'
        }];

        var NEW_TAB_1_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Tab 1'
        }, {
            qualifier: 'title',
            type: 'shortString',
            value: 'This is the new tab 1 content'
        }];

        var TAB_2_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Tab 2'
        }, {
            qualifier: 'title',
            type: 'shortString',
            value: 'This is the new tab 2 content'
        }];

        it('WHEN the user selects an item in the list THEN the item is selected', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.assertions.isEmpty(TABS_FIELD_ID, MULTI_SELECT);

            // WHEN
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);

            // THEN
            cmsItemDropdown.assertions.isNotEmpty(TABS_FIELD_ID, MULTI_SELECT);
            cmsItemDropdown.assertions.itemIsSelected(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
        });

        it('GIVEN the list has items selected WHEN the user clicks on remove THEN the item is removed from the list', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);

            // WHEN
            cmsItemDropdown.actions.removeSelectedItemInDropdown(TABS_FIELD_ID, TAB_1);

            // THEN
            cmsItemDropdown.assertions.isEmpty(TABS_FIELD_ID, MULTI_SELECT);
        });

        it('GIVEN an item is selected WHEN the user clicks on it THEN it opens a nested editor', function() {
            // GIVEN
            var expectedNestingLevel = 2;
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);

            // WHEN
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_1);

            // THEN
            genericEditor.assertions.topEditorIsNested(expectedNestingLevel);
            genericEditorBreadcrumb.assertions.componentIsDisplayedInCorrectNestingLevel(TAB_1_NAME, TAB_1_TYPE, expectedNestingLevel);
        });

        it('GIVEN an item is selected and opened in a nested editor WHEN the user cancels it THEN the editor must close without any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            genericEditor.assertions.openEditorHasRightData(TAB_1_DATA);

            // WHEN
            genericEditor.actions.setEditorData([NEW_TAB_1_DATA[1]]); // The only change is in the second field.
            genericEditor.actions.cancel();
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            genericEditor.assertions.openEditorHasRightData(TAB_1_DATA);
        });

        it('GIVEN an item is selected and opened in a nested editor WHEN the user saves it THEN the editor must close saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            genericEditor.assertions.openEditorHasRightData(TAB_1_DATA);

            // WHEN
            genericEditor.actions.setEditorData([NEW_TAB_1_DATA[1]]); // The only change is in the second field.
            genericEditor.actions.save();

            // THEN
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            genericEditor.assertions.openEditorHasRightData(NEW_TAB_1_DATA);
        });

        it('GIVEN a new item is created from the dropdown WHEN the user cancels it THEN the editor must close without saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();

            // WHEN
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_2);
            genericEditor.actions.setEditorData(TAB_2_DATA);
            genericEditor.actions.cancel();
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            cmsItemDropdown.assertions.isEmpty(TABS_FIELD_ID, MULTI_SELECT);
        });

        it('GIVEN a new item is created from the dropdown WHEN the user saves it THEN the editor must close saving any changes', function() {
            // GIVEN
            page.actions.openGenericEditor();

            // WHEN
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_2);
            genericEditor.actions.setEditorData(TAB_2_DATA);
            genericEditor.actions.save();

            // THEN
            cmsItemDropdown.assertions.itemIsSelected(TABS_FIELD_ID, MULTI_SELECT, TAB_2_UID);
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_2_UID);
            genericEditor.assertions.openEditorHasRightData(TAB_2_DATA);
        });
    });

    describe('Nested Elements - ', function() {

        var TAB_3_INVALID_DATA = [{
            qualifier: 'title',
            type: 'shortString',
            value: 'some invalid title'
        }];

        var TAB_3_VALID_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Tab 3'
        }, {
            qualifier: 'title',
            type: 'shortString',
            value: 'some valid title'
        }];

        var TAB_2_VALID_DATA = [{
            qualifier: 'name',
            type: 'shortString',
            value: 'Tab 2'
        }, {
            qualifier: 'title',
            type: 'shortString',
            value: 'some valid title'
        }];

        var TAB_2_UID = 'COMP_2';
        var TAB_3_UID = 'COMP_1';
        var FIELD_WITH_ERROR_QUALIFIER = 'title';

        it('GIVEN several nested component editors are open WHEN the top one has a validation error THEN it should not affect lower level component editors', function() {
            // GIVEN
            var expectedNestingLevel = 2;
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_2);
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_3);

            // WHEN
            genericEditor.actions.setEditorData(TAB_3_INVALID_DATA);
            genericEditor.actions.save();
            genericEditor.assertions.fieldHasValidationErrorsInTab(FIELD_WITH_ERROR_QUALIFIER, null, 1);

            genericEditor.actions.cancel();
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            genericEditorBreadcrumb.assertions.componentIsDisplayedInCorrectNestingLevel(TAB_2_NAME, TAB_1_TYPE, expectedNestingLevel);
            genericEditor.assertions.fieldHasNoValidationErrors(FIELD_WITH_ERROR_QUALIFIER);
        });

        it('GIVEN several nested component editors are open WHEN they are saved THEN the changes are persisted', function() {
            // GIVEN
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_2);
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(TABS_FIELD_ID, TAB_3);

            // WHEN
            genericEditor.actions.setEditorData(TAB_3_VALID_DATA);
            genericEditor.actions.save();
            genericEditor.actions.waitForNumberOfEditorModals(2);

            genericEditor.actions.setEditorData(TAB_2_VALID_DATA);
            genericEditor.actions.save();
            genericEditor.actions.waitForNumberOfEditorModals(1);

            // THEN
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_2_UID);
            genericEditor.assertions.openEditorHasRightData(TAB_2_VALID_DATA);
            cmsItemDropdown.actions.clickSelectedItem(TABS_FIELD_ID, MULTI_SELECT, TAB_3_UID);
            genericEditor.assertions.openEditorHasRightData(TAB_3_VALID_DATA);
        });
    });

    describe('Nested CmsLinkComponent - ', function() {

        // There was an issue with nested CMSLinkComponents disrupting the structure of the parent editors. This test ensures 
        // this regression is prevented. 
        it('GIVEN editor already has data WHEN a nested CMSLinkComponent is added THEN editor data is not disrupted', function() {
            // GIVEN 
            page.actions.openGenericEditor();
            cmsItemDropdown.actions.selectItemInDropdown(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            cmsItemDropdown.actions.selectItemInDropdown(TABS_FIELD_ID, MULTI_SELECT, TAB_1);

            // WHEN 
            cmsItemDropdown.actions.openNewNestedComponentFromDropdown(LINKS_FIELD_ID, LINK_1);
            cmsLink.actions.setExternalLinkData(LINK_1, LINK_1_URL);
            genericEditor.actions.save();

            // THEN 
            cmsItemDropdown.assertions.itemIsSelected(BANNER_FIELD_ID, SINGLE_SELECT, BANNER_1);
            cmsItemDropdown.assertions.itemIsSelected(TABS_FIELD_ID, MULTI_SELECT, TAB_1);
            cmsItemDropdown.assertions.itemIsSelected(LINKS_FIELD_ID, MULTI_SELECT, LINK_1);
        });
    });
});
