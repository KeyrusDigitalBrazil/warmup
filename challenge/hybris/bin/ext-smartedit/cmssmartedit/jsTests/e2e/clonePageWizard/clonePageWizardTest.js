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
describe('Clone Page Wizard', function() {

    var clonePageWizard = e2e.pageObjects.clonePageWizard;
    var perspective = e2e.componentObjects.modeSelector;
    var sfBuilder = e2e.se.componentObjects.sfBuilder;

    beforeEach(function(done) {
        browser.waitForAngularEnabled(true);
        browser.bootstrap(__dirname).then(function() {
            done();
        });
    });

    describe('Content Page with Single Target Catalog Versions', function() {

        beforeEach(function(done) {
            clonePageWizard.actions.openAndBeReady(done);
        });

        describe('Clone Option Step', function() {

            it('GIVEN Clone Page Wizard is opened for a primary page THEN "Clone Options" step is displayed AND Condition equals "Variation" by default AND current primary page should be preselected', function() {
                // GIVEN
                expect(clonePageWizard.util.isWindowOpen()).toBeTruthy();

                // THEN
                clonePageWizard.assertions.currentStepTextIs('CLONE OPTIONS');
                expect(clonePageWizard.elements.getSelectedConditionOption().getText()).toBe("Variation");
                expect(clonePageWizard.elements.getAssociatedPrimaryPage().getText()).toBe("Homepage");
            });
        });

        describe('Next Step Information page condition "Variation" is selected in "Clone Options" step', function() {

            beforeEach(function(done) {
                clonePageWizard.actions.clickNext().then(function() {
                    done();
                });
            });

            it('GIVEN the user is on the Clone Options step selected Variation page condition WHEN he is on the Information step THEN the page label field is disabled', function() {
                clonePageWizard.assertions.currentStepTextIs('INFORMATION');

                clonePageWizard.assertions.contentPageLabelFieldEnabledToBeFalse();

                expect(clonePageWizard.elements.getNameFieldText()).toBe('Homepage');
                expect(clonePageWizard.elements.getLabelFieldText()).toBe('i-love-pandas');
                expect(clonePageWizard.elements.getUidFieldText()).toBe('');
            });
        });

        describe('Next Step Information page condition "Primary" is selected in "Clone Options" step', function() {

            beforeEach(function(done) {

                clonePageWizard.actions.selectPrimaryCondition().then(function() {})
                    .then(function() {
                        clonePageWizard.actions.clickNext();
                    })
                    .then(function() {
                        done();
                    });
            });

            it('GIVEN the user is on the Clone Options step selected Primary page condition WHEN he is on the Information step THEN the page label field is enabled', function() {
                clonePageWizard.assertions.currentStepTextIs('INFORMATION');

                clonePageWizard.assertions.contentPageLabelFieldEnabledToBeTrue();

                expect(clonePageWizard.elements.getNameFieldText()).toBe('Homepage');
                expect(clonePageWizard.elements.getLabelFieldText()).toBe('i-love-pandas');
                expect(clonePageWizard.elements.getUidFieldText()).toBe('');
            });
        });

        describe('3rd Step Restrictions page condition "Variation" selected from initial step', function() {

            beforeEach(function(done) {
                clonePageWizard.actions.clickNext().then(function() {})
                    .then(function() {
                        clonePageWizard.actions.clickNext();
                    })
                    .then(function() {
                        done();
                    });
            });

            it('GIVEN the user is on the Clone Options step WHEN he clicks next THEN he is presented with the Page Restrictions step that has two restrictions coming from the page being cloned', function() {
                clonePageWizard.assertions.currentStepTextIs('RESTRICTIONS');

                var restrictionLabels = clonePageWizard.elements.getRestrictionsLabelsList();
                expect(restrictionLabels.get(0).getText()).toBe("SOME TIME RESTRICTION A");
                expect(restrictionLabels.get(1).getText()).toBe("ANOTHER TIME B");
            });

        });

        describe('Create Clone Page full save operation', function() {

            beforeEach(function(done) {
                clonePageWizard.actions.clickNext().then(function() {})
                    .then(function() {
                        done();
                    });
            });

            it('GIVEN the user is on the Information step and enter wrong uid WHEN he clicks done THEN then a validation error is throw and INFORMATION tab is shown', function() {

                clonePageWizard.actions.enterTextInUidField('trump');

                clonePageWizard.actions.clickNext().then(function() {
                    clonePageWizard.actions.submit().then(function() {
                        clonePageWizard.assertions.currentStepTextIs('INFORMATION');
                        expect(clonePageWizard.elements.getUidErrorsText()).toBe('No Trump jokes plz.');
                        clonePageWizard.assertions.assertWindowIsOpen();
                    });
                });
            });

            it('GIVEN the user is on the Restrictions step WHEN he clicks done THEN the wizard is removed', function() {
                clonePageWizard.actions.clickNext().then(function() {
                    clonePageWizard.actions.submit().then(function() {
                        clonePageWizard.assertions.assertWindowIsClosed();
                    });
                });
            });
        });

        describe('Page cloned in a catalog different from the current one', function() {
            beforeEach(function(done) {
                clonePageWizard.actions.clickNext().then(function() {})
                    .then(function() {
                        done();
                    });
            });

            it("GIVEN a page got cloned in a catalog different from the current one " +
                "WHEN the user clicks 'Done' " +
                "THEN both a 'clone page' success alert and an info 'navigate to localized page' actionable alert should be displayed",
                function() {
                    clonePageWizard.actions.enterTextInUidField('targetCloneUid');
                    clonePageWizard.actions.clickNext().then(function() {
                        clonePageWizard.actions.submit().then(function() {
                            clonePageWizard.assertions.assertCloneSuccessAlertIsDisplayed();
                            clonePageWizard.assertions.assertCloneActionableAlertIsDisplayed();
                        });
                    });
                });
        });

        describe('Page cloned in the current catalog', function() {
            beforeEach(function(done) {
                clonePageWizard.actions.clickNext().then(function() {})
                    .then(function() {
                        done();
                    });
            });

            it("GIVEN a page got cloned in the current catalog " +
                "WHEN the user clicks 'Done' " +
                "THEN a 'clone page' success alert message should be displayed",
                function() {
                    clonePageWizard.actions.clickNext().then(function() {
                        clonePageWizard.actions.submit().then(function() {
                            clonePageWizard.assertions.assertCloneSuccessAlertIsDisplayed();
                            clonePageWizard.assertions.assertCloneActionableAlertIsNotDisplayed();
                        });
                    });
                });
        });
    });

    describe('Content Page with Multiple Target Catalogs', function() {

        beforeEach(function(done) {
            browser.waitForWholeAppToBeReady().then(function() {
                return perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
            }).then(function() {
                return sfBuilder.actions.changePageIdAndCatalogVersion('homepage_global_online', 'apparelContentCatalog/Online');
            }).then(function() {
                clonePageWizard.actions.openClonePageWizard();
                done();
            });
        });

        describe('Clone Option Step', function() {

            it('GIVEN Clone Page Wizard is opened for a primary content page WHEN target catalog version selector is displayed AND the current catalog version is selected THEN Condition equals "Variation" by default', function() {
                // GIVEN
                expect(clonePageWizard.util.isWindowOpen()).toBeTruthy();

                // WHEN
                expect(clonePageWizard.elements.getTargetCatalogAndVersionToggle().isDisplayed()).toBe(true);

                // THEN
                expect(clonePageWizard.elements.getTargetCatalogAndVersionOption().getText()).toBe('Apparel Content Catalog - Online');
                expect(clonePageWizard.elements.getSelectedConditionOption().getText()).toBe('Variation');
            });

            it('GIVEN Clone Page Wizard is opened for a primary content page WHEN target catalog version selector is displayed AND the current catalog version is not selected THEN Condition equals "Primary" by default', function() {
                // GIVEN
                expect(clonePageWizard.util.isWindowOpen()).toBeTruthy();

                //WHEN
                expect(clonePageWizard.elements.getTargetCatalogAndVersionToggle().isDisplayed()).toBe(true);
                clonePageWizard.actions.selectTargetCatalogAndVersion('Apparel UK Content Catalog - Online');

                // THEN
                expect(clonePageWizard.elements.getTargetCatalogAndVersionOption().getText()).toBe('Apparel UK Content Catalog - Online');
                expect(clonePageWizard.elements.getSelectedConditionOption().getText()).toBe('Primary');
            });
        });

        describe('Next Step Information', function() {

            beforeEach(function(done) {
                clonePageWizard.actions.selectTargetCatalogAndVersion('Apparel DE Content Catalog - Online').then(function() {
                    return clonePageWizard.actions.clickNext();
                }).then(function() {
                    done();
                });
            });

            it('GIVEN the user is on the Information step AND a target catalog and version is selected WHEN cloning a primary content page that already exists in the selected catalog version with the same label THEN a warning should display under the dropdown', function() {
                // GIVEN
                clonePageWizard.assertions.currentStepTextIs('INFORMATION');

                // THEN
                expect(clonePageWizard.elements.getLabelFieldWarningText()).toBe('A page with this label already exists. If you proceed, it will overwrite the existing page');
            });

            it('GIVEN the user is on the Information step AND a target catalog and version is selected WHEN cloning a primary content page AND changing the label that does not exist in the selected catalog version THEN no warning messages should display', function() {
                // GIVEN
                clonePageWizard.assertions.currentStepTextIs('INFORMATION');

                // WHEN
                clonePageWizard.actions.enterTextInLabelField();

                // THEN
                expect(clonePageWizard.elements.getFieldWarningsCount()).toBe(0);
            });
        });
    });
});
