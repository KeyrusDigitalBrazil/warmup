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
var fieldEditor = e2e.componentObjects.restrictionsEditor;

describe('Test Editor Modal Window', function() {

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    it("Given the editor callback is attached to a button assigned to a component - " +
        "When the button is clicked - " +
        "Then the Editor Modal is displayed with Save button visible.",
        function() {
            openEditorModalForCMSParagraphComponent();
            expect(element(by.id('save')).isDisplayed()).toBe(true);
        });

    it("Given the editor callback is attached to a button assigned to a component - " +
        "When the button is clicked - " +
        "Then the Editor Modal is displayed with the Save button disabled.",
        function() {
            openEditorModalForCMSParagraphComponent();
            expect(element(by.id('save')).isEnabled()).toBe(false);
        });

    it("Given the editor callback is attached to a button assigned to a component - " +
        "When the button is clicked - " +
        "Then the Generic Editor is loaded in the modal content for the given component",
        function() {
            openEditorModalForCMSParagraphComponent();
            expect(element(by.name('id')).getAttribute('value')).toBe('MyParagraph');
        });

    it("Given the Editor Modal is visible - " +
        "And the Save button is disabled - " +
        "When I make changes to the content - " +
        "The save button is enabled",
        function() {
            openEditorModalForCMSParagraphComponent();
            expect(element(by.id('save')).isEnabled()).toBe(false);
            makeChangesFromPristineState();
            expect(element(by.id('save')).isEnabled()).toBe(true);
        });

    it('Given the Save button is enabled - ' +
        'When I click the Save Button - ' +
        'And the save succeeds - ' +
        'Then the editor modal closes',
        function() {
            mockBackendToPassSave();
            openEditorAndMakeChangesAndClickSave();
            browser.wait(EC.stalenessOf(element(by.css('.modal-dialog'))), 5000);
            browser.waitForAbsence(element(by.css('.modal-dialog')));
        });

    it('Given the Save button is enabled - ' +
        'When I click the Save Button - ' +
        'And the save fails - ' +
        'Then the editor modal remains open',
        function() {
            mockBackendToFailSave();
            openEditorAndMakeChangesAndClickSave();
            assertModalIsPresent();
        });

    it('Given the Editor Modal is open - ' +
        'And there are no changes to commit - ' +
        'When I click the Cancel button - ' +
        'Then the modal window is closed',
        function() {
            openEditorAndClickCancel();
            assertModalIsNotPresent();
        });

    it('Given the Editor Modal is open - ' +
        'And there are changes to commit - ' +
        'When I click the Cancel button - ' +
        'Then a cancel confirmation modal appears',
        function() {
            openEditorAndMakeChangesAndClickCancel();
            expect(element(by.id('confirmationModalDescription')).isPresent()).toBe(true);
        });

    it('Given the Editor Cancel confirmation modal is visible - ' +
        'When I click OK to confirm the cancellation - ' +
        'Then the modal window is closed',
        function() {
            openEditorAndMakeChangesAndClickCancelAndConfirm();
            assertModalIsNotPresent();
        });

    it('Given the Editor Cancel confirmation modal is visible - ' +
        'When I click the Cancel button to dismiss the cancellation - ' +
        'Then the modal window remains open',
        function() {
            openEditorAndMakeChangesAndClickCancelAndDismiss();
            assertModalIsPresent();
        });

    it('Given the Editor Modal is open - ' +
        'And there are no changes to commit - ' +
        'When I click the X button - ' +
        'Then the modal window is closed',
        function() {
            openEditorAndClickXButton();
            assertModalIsNotPresent();
        });

    it('Given the Editor Modal is open - ' +
        'And there are changes to commit - ' +
        'When I click the X button - ' +
        'Then a cancel confirmation modal appears',
        function() {
            openEditorAndMakeChangesAndClickXButton();
            expect(element(by.id('confirmationModalDescription')).isPresent()).toBe(true);
        });

    it('Given the Editor Cancel confirmation modal is visible after clicking the X Button - ' +
        'When I click OK to confirm the cancellation - ' +
        'Then the modal window is closed',
        function() {
            openEditorAndMakeChangesAndClickXButtonAndConfirm();
            assertModalIsNotPresent();
        });

    it('Given the Editor Cancel confirmation modal is visible after clicking the X Button - ' +
        'When I click the Cancel button to dismiss the cancellation - ' +
        'Then the modal window remains open',
        function() {
            openEditorAndMakeChangesAndClickXButtonAndDismiss();
            assertModalIsPresent();
        });

    it('Given the Editor modifies a tab - ' +
        'WHEN I click the save button and the system returns an unrelated validation error - ' +
        'THEN an error will be highlighted in the faulty tab',
        function() {
            clickTriggerUnrelatedValidationErrorsButton();
            openEditorModalForCMSParagraphComponent();
            moveToTab('visibility');
            element(by.name('id')).sendKeys('blabla');
            moveToTab('default');

            clickSave();

            // Expect generic tab to be modified.
            browser.wait(EC.presenceOf(element(by.css('li[data-tab-id="default"] a.sm-tab-error'))), 5000);
            expect(element(by.css('li[data-tab-id="default"] a.sm-tab-error')).isPresent()).toBe(true);

            expect(fieldEditor.elements.getErrorMessageByFieldName('headline').getText()).toBe('This is an unrelated validation error');
        });

    it('Given the Editor modifies two tabs (with the same component)' +
        'When I click the save button ' +
        'Then both tabs should be saved',
        function() {
            //execute
            openEditorModalForCMSParagraphComponent();
            makeChangesFromPristineState();

            var firstTabText = getFirstTabText();
            var componentUid = getComponentUid();
            clickSave();
            openEditorModalForCMSParagraphComponent();

            //assert
            expect(firstTabText).toEqual(getFirstTabText(), "First tab should be saved");
            expect(componentUid).toEqual(getComponentUid(), "Second tab should be saved");
        });

    // Helper Functions
    function openEditorAndMakeChangesAndClickCancelAndConfirm() {
        openEditorAndMakeChangesAndClickCancel();
        confirmConfirmationModal();
    }

    function openEditorAndMakeChangesAndClickCancelAndDismiss() {
        openEditorAndMakeChangesAndClickCancel();
        dismissConfirmationModal();
    }

    function openEditorAndMakeChangesAndClickSave() {
        openEditorModalForCMSParagraphComponent();
        makeChangesFromPristineState();
        clickSave();
    }

    function openEditorAndMakeChangesAndClickCancel() {
        openEditorModalForCMSParagraphComponent();
        makeChangesFromPristineState();
        clickCancel();
    }

    function openEditorAndClickCancel() {
        openEditorModalForCMSParagraphComponent();
        clickCancel();
    }

    function openEditorAndClickXButton() {
        openEditorModalForCMSParagraphComponent();
        clickXButton();
    }

    function openEditorAndMakeChangesAndClickXButton() {
        openEditorModalForCMSParagraphComponent();
        makeChangesFromPristineState();
        clickXButton();
    }

    function openEditorAndMakeChangesAndClickXButtonAndConfirm() {
        openEditorAndMakeChangesAndClickXButton();
        confirmConfirmationModal();
    }

    function openEditorAndMakeChangesAndClickXButtonAndDismiss() {
        openEditorAndMakeChangesAndClickXButton();
        dismissConfirmationModal();
    }

    function assertModalIsNotPresent() {
        browser.wait(EC.stalenessOf(element(by.css('.modal-dialog'))), 5000, 'Expected modal to not be present');
        browser.waitForAbsence(element(by.css('.modal-dialog')));
    }

    function assertModalIsPresent() {
        browser.wait(EC.presenceOf(element(by.css('.modal-dialog'))), 5000, 'Expected modal to be present');
        expect(element(by.css('.modal-dialog')).isPresent()).toBe(true);
    }

    function moveToTab(tabId) {
        browser.click(by.css('li[data-tab-id="' + tabId + '"]'));
    }

    function openEditorModalForCMSParagraphComponent() {
        browser.click(by.id('openCMSParagraphComponentEditorModal'));
        browser.click(by.css('li[data-tab-id="default"]'));
    }

    function makeChangesFromPristineState() {
        moveToTab("default");
        element(by.name('headline')).sendKeys('blabla');
        moveToTab("visibility");
        element(by.name('id')).sendKeys('dodo');
    }

    function getFirstTabText() {
        return element(by.name('headline')).getAttribute('value');
    }

    function getComponentUid() {
        return element(by.id('id-shortstring')).getAttribute('value');
    }

    function clickSave() {
        browser.click(by.id('save'));
    }

    function clickCancel() {
        browser.click(by.id('cancel'));
    }

    function clickXButton() {
        browser.click(by.css(".close"));
    }

    function confirmConfirmationModal() {
        browser.click(by.id('confirmOk'));
    }

    function dismissConfirmationModal() {
        browser.click(by.id('confirmCancel'));
    }

    function clickTriggerUnrelatedValidationErrorsButton() {
        browser.click(by.id('unrelatedValidation'));
    }

    function attachFailingSaveCallback() {
        browser.click(by.id('attachFailingSaveCallback'));
    }

    function mockBackendToPassSave() {
        browser.click(by.id('mockBackendToPassSave'));
    }

    function mockBackendToFailSave() {
        browser.click(by.id('mockBackendToFailSave'));
    }

});
