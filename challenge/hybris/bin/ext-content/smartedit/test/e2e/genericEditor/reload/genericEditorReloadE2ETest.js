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
var currentPage = require('./reloadPageObject.js');
describe('E2E Test for decorator service module', function() {
    beforeEach(function(done) {
        currentPage.actions.openAndBeReady().then(function() {
            done();
        });
    });

    it('WHEN setting a new structure THEN the Generic Editor automatically reloads with the new structure', function() {
        currentPage.actions.setNewStructure().then(function() {
            currentPage.assertions.assertNameInputIsDisplayed();
            expect(currentPage.elements.getNameInput().getAttribute('value')).toBe('Any new name');
            currentPage.assertions.assertRichTextEditorIsDisplayed();
        });
    });

    it('WHEN setting a new structure api THEN the Generic Editor automatically reloads the new structure api', function() {
        currentPage.actions.setNewStructureApi().then(function() {
            currentPage.assertions.assertNameInputIsNotDisplayed();
            currentPage.assertions.assertHeadlineIsDisplayed();
            currentPage.assertions.assertActiveCheckboxIsDisplayed();
            currentPage.assertions.assertTextAreaIsDisplayed();
            expect(currentPage.elements.getHeadline().getAttribute('value')).toBe('Any headline');
        });
    });

    /**
     * sucess message displayed assert that the payload passed has expected keys (need to check value too?)
     */
    it('WHEN setting a new structure AND submitting a modified content THEN the Generic Editor a success message will be displayed', function() {
        currentPage.actions.setNewStructure().then(function() {
            currentPage.elements.getNameInput().clear().sendKeys('some new name');
            expect(currentPage.elements.getNameInput().getAttribute('value')).toBe('some new name');
            currentPage.actions.submit().then(function() {
                expect(currentPage.elements.getGenericEditorStatus().getText('value')).toBe('PASSED');
            });
        });
    });

    it('GIVEN a new structure is set for a new component AND the User modify the content WHEN the structure change and the modified content is passed THEN the modified content is up to date', function() {
        currentPage.actions.setPOSTMode().then(function() {
            currentPage.elements.getNameInput().clear().sendKeys('new component name');
            currentPage.actions.clickReloadButton().then(function() {
                currentPage.assertions.assertRichTextEditorIsDisplayed();
                expect(currentPage.elements.getNameInput().getAttribute('value')).toBe('new component name');
                currentPage.elements.getCustomFieldInput().sendKeys('custom value');
                currentPage.actions.submit().then(function() {
                    expect(currentPage.elements.getGenericEditorStatus().getText('value')).toBe('PASSED');
                });
            });
        });
    });
});
