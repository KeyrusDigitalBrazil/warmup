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
var page = {};
page.elements = {
    getNameInput: function() {
        return element(by.css("[id='name-shortstring']"));
    },
    getHeadline: function() {
        return element(by.css("[id='headline-shortstring']"));
    },
    getGenericEditorStatus: function() {
        return element(by.css(".generic-editor-status"));
    },
    getNewStructureButton: function() {
        return element(by.id('set-new-structure-button'));
    },
    getNewStructureApiButton: function() {
        return element(by.id('set-new-structure-api-button'));
    },
    getPostModeButton: function() {
        return element(by.id('post-mode-button'));
    },
    getReloadButton: function() {
        return element(by.id('load-button'));
    },
    getSubmitButton: function() {
        return element(by.id('submit'));
    },
    getRichText: function() {
        return element(by.tagName('se-rich-text-field'));
    },
    getActiveCheckbox: function() {
        return element(by.css("[id='active-checkbox']"));
    },
    getTextArea: function() {
        return element(by.tagName('textarea'));
    },
    getCustomFieldInput: function() {
        return element(by.css("[id='componentCustomField-shortstring']"));
    }
};
page.actions = {
    openAndBeReady: function() {
        return browser.get('test/e2e/genericEditor/reload/index.html');
    },
    setNewStructure: function() {
        return browser.click(page.elements.getNewStructureButton());
    },
    setNewStructureApi: function() {
        return browser.click(page.elements.getNewStructureApiButton());
    },
    setPOSTMode: function() {
        return browser.click(page.elements.getPostModeButton());
    },
    clickReloadButton: function() {
        return browser.click(page.elements.getReloadButton());
    },
    submit: function() {
        return browser.click(page.elements.getSubmitButton());
    }
};
page.assertions = {
    assertNameInputIsDisplayed: function() {
        return expect(page.elements.getNameInput().isPresent()).toBe(true);
    },
    assertNameInputIsNotDisplayed: function() {
        return browser.waitForAbsence(page.elements.getNameInput());
    },
    assertRichTextEditorIsDisplayed: function() {
        return expect(page.elements.getRichText().isPresent()).toBe(true);
    },
    assertHeadlineIsDisplayed: function() {
        return expect(page.elements.getHeadline().isPresent()).toBe(true);
    },
    assertActiveCheckboxIsDisplayed: function() {
        return expect(page.elements.getActiveCheckbox().isPresent()).toBe(true);
    },
    assertTextAreaIsDisplayed: function() {
        return expect(page.elements.getTextArea().isPresent()).toBe(true);
    }
};

module.exports = page;
