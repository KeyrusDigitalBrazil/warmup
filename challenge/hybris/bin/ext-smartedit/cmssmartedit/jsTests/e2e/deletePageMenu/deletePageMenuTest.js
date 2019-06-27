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
describe('Delete Page Menu', function() {

    var confirmationModal = e2e.componentObjects.confirmationModal;
    var pageList = e2e.pageObjects.PageList;
    var perspective = e2e.componentObjects.modeSelector;
    var popover = e2e.componentObjects.popover;
    var storefront = e2e.componentObjects.storefront;
    var systemAlerts = e2e.componentObjects.systemAlerts;
    var deletePageMenu = e2e.pageObjects.DeletePageMenu;

    var DELETABLE_PAGE_NAME = "Some Other Page";

    beforeEach(function() {
        browser.bootstrap(__dirname);
        browser.waitForWholeAppToBeReady();
    });

    afterEach(function() {
        browser.waitForAngularEnabled(true);
    });

    describe('Non-Deletable page', function() {

        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                done();
            });
        });

        afterEach(function(done) {
            perspective.select(perspective.PREVIEW_PERSPECTIVE).then(function() {
                done();
            });
        });

        it('GIVEN the user is on the storefront view of a non-deletable page THEN the "move to trash" option is rendered inactive AND I expect a popover to get rendered on hover with a meaningful message.',
            function() {
                deletePageMenu.assertions.deletePageMenuIconIsInactive();
                deletePageMenu.actions.hoverOnDeletePageMenu();
                popover.assertions.isDisplayedWithProvidedText('se.cms.tooltip.current.homepage.movetotrash');

            });

    });

    describe('Deletable page', function() {

        beforeEach(function(done) {
            storefront.actions.goToSecondPage().then(function() {
                perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                    done();
                });
            });
        });

        describe('Move to trash toolbar item is enabled', function() {

            afterEach(function(done) {
                perspective.select(perspective.PREVIEW_PERSPECTIVE).then(function() {
                    done();
                });
            });

            it('GIVEN the user is on the storefront page of a deletable page THEN the "move to trash" option is rendered active AND I expect no popover anchor to be found for the "move to trash" option.',
                function() {

                    deletePageMenu.assertions.deletePageMenuIconIsActive();
                    deletePageMenu.actions.hoverOnDeletePageMenu();
                    deletePageMenu.assertions.deletePageMenuIconPopoverAnchorIsNotPresent();

                }
            );

        });

        describe('WHEN doing a soft delete of a page THEN it should be visible in trash page view', function() {

            it('GIVEN the user is on the storefront content page of a deletable page WHEN I trigger and confirm the soft deletion of the page THEN I am expected to be redirected to the page list view AND the deleted page is to be listed on the trash page view.',
                function() {

                    // WHEN
                    deletePageMenu.actions.clickOnDeletePageMenu();
                    confirmationModal.actions.confirmConfirmationModal();
                    systemAlerts.actions.flush();
                    pageList.actions.clickOnTrashViewLink();

                    // THEN
                    pageList.assertions.pageRowIsRenderedByPageName(DELETABLE_PAGE_NAME);

                }

            );

        });

    });

});
