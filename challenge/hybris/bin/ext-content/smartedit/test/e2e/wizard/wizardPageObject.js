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
var PageObject = require('../PageObject');

var ModalWizardPageObject = function() {

    this.NEXT_ID = 'ACTION_NEXT';
    this.BACK_ID = 'ACTION_BACK';
    this.DONE_ID = 'ACTION_DONE';
    this.WIZARD_ID = 'yModalWizard';

    this.pageURI = 'test/e2e/wizard';

    this.open = function() {
        browser.get(this.pageURI);
    };
};
ModalWizardPageObject.prototype = Object.create(PageObject.prototype);


// ----------  SELECTORS/ELEMENTS  -----------


ModalWizardPageObject.prototype.elements = {
    nameStep: element(by.id('nameTemplate')),
    genderStep: element(by.id('genderTemplate')),
    offendedStep: element(by.id('offendedTemplate')),
    ageStep: element(by.id('ageTemplate')),
    nameNavbar: element(by.id('NAV-name')),
    ageNavbar: element(by.id('NAV-age'))
};


// ----------------  ACTIONS  ----------------

ModalWizardPageObject.prototype.openWizard = function() {
    return browser.click(by.id('wizard'));
};

ModalWizardPageObject.prototype.openToGenderStep = function() {
    this.openWizard();
    this.makeNameStepValid();
    this.clickNext(); //gender
};

ModalWizardPageObject.prototype.openToAgeStep = function() {
    this.openToGenderStep();
    this.clickNext(); //age
};

ModalWizardPageObject.prototype.openToSummaryStep = function() {
    this.openToAgeStep();
    this.clickNext(); //summary
};

ModalWizardPageObject.prototype.makeNameStepValid = function() {
    element(by.id('nameInput')).sendKeys('testing');
};

ModalWizardPageObject.prototype.clickNavbar = function(navElement) {
    return browser.click(navElement);
};

ModalWizardPageObject.prototype.clickNext = function() {
    browser.click(by.id(this.NEXT_ID));
};

ModalWizardPageObject.prototype.clickBack = function() {
    browser.click(by.id(this.BACK_ID));
};

ModalWizardPageObject.prototype.clickCancel = function() {
    browser.click(by.css('.close'));
};

ModalWizardPageObject.prototype.clickDone = function() {
    browser.waitForPresence(by.id(this.DONE_ID));
    return browser.click(by.id(this.DONE_ID));
};

ModalWizardPageObject.prototype.clickOffendedToggle = function() {
    browser.click(by.id('offendedCheck'));
};

// ---------------  ASSERTIONS  --------------

ModalWizardPageObject.prototype.assertWizardDisplayed = function() {
    browser.waitUntil(function() {
        return element(by.id(this.WIZARD_ID)).isDisplayed();
    }.bind(this));
    expect(element(by.id(this.WIZARD_ID)).isDisplayed()).toBeTruthy();
};

ModalWizardPageObject.prototype.assertWizardNotDisplayed = function() {
    browser.waitForAbsence(element(by.id(this.NEXT_ID)));
    browser.waitForAbsence(element(by.id(this.NEXT_ID)));
};

ModalWizardPageObject.prototype.assertStepDisplayed = function(template) {
    expect(template.isDisplayed()).toBeTruthy();
};

ModalWizardPageObject.prototype.assertNextIsEnabled = function() {
    expect(element(by.id(this.NEXT_ID)).isEnabled()).toBeTruthy();
};

ModalWizardPageObject.prototype.assertNextIsDisabled = function() {
    expect(element(by.id(this.NEXT_ID)).isEnabled()).toBeFalsy();
};

ModalWizardPageObject.prototype.assertNextIsPresent = function() {
    expect(element(by.id(this.NEXT_ID)).isPresent()).toBeTruthy();
};

ModalWizardPageObject.prototype.assertNextIsNotPresent = function() {
    browser.waitForAbsence(element(by.id(this.NEXT_ID)));
    browser.waitForAbsence(element(by.id(this.NEXT_ID)));
};

ModalWizardPageObject.prototype.assertDoneIsPresent = function() {
    browser.waitUntil(function() {
        return element(by.id(this.DONE_ID)).isPresent();
    }.bind(this));
    expect(element(by.id(this.DONE_ID)).isPresent()).toBeTruthy();

};

ModalWizardPageObject.prototype.assertDoneIsNotPresent = function() {
    browser.waitForAbsence(element(by.id(this.DONE_ID)));
};

module.exports = ModalWizardPageObject;
