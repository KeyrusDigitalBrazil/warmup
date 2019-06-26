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
describe('Versioning Perspective - \n', function() {

    var page = e2e.pageObjects.PageVersioningMenu;
    var experienceSelector = e2e.componentObjects.ExperienceSelector;
    var perspective = e2e.componentObjects.modeSelector;
    var pageVersions = e2e.componentObjects.pageVersions;

    var VERSION_LABEL = 'New - Version 2';


    describe('Do not consider type permissions', function() {
        beforeEach(function(done) {
            pageVersions.utils.setCMSVersionTypePermission({
                read: true,
                change: true,
                create: true,
                remove: true
            });
            page.actions.navigateToPage(true, __dirname, done);
        });

        it('GIVEN no version is selected WHEN experience selector is opened THEN the content catalog dropdown is enabled', function() {
            // WHEN
            experienceSelector.actions.openExperienceSelector();

            // THEN
            experienceSelector.assertions.contentCatalogDropdownIsEditabled();
        });

        it('GIVEN a version is selected WHEN experience selector is opened THEN the content catalog dropdown is disabled', function() {
            // GIVEN
            perspective.selectVersioningPerspective();
            pageVersions.actions.openMenu();
            pageVersions.actions.selectPageVersionByLabel(VERSION_LABEL);

            // WHEN
            experienceSelector.actions.openExperienceSelector();

            // THEN
            experienceSelector.assertions.contentCatalogDropdownIsNotEditabled();
        });
    });

    describe('CMS Version read permission is not available', function() {
        beforeEach(function(done) {
            pageVersions.utils.setCMSVersionTypePermission({
                read: false,
                change: true,
                create: true,
                remove: true
            });
            page.actions.navigateToPage(true, __dirname, done);
        });
        it('GIVEN user does not have READ permission THEN version perspective is not available', function() {
            // WHEN
            perspective.actions.openPerspectiveList();

            // THEN
            perspective.assertions.expectVersioningModeIsAbsent();
        });
    });

    describe('CMS Version read permission is available', function() {
        beforeEach(function(done) {
            pageVersions.utils.setCMSVersionTypePermission({
                read: true,
                change: true,
                create: true,
                remove: true
            });
            page.actions.navigateToPage(true, __dirname, done);
        });
        it('GIVEN user has READ permission THEN version perspective is available', function() {
            // WHEN
            perspective.actions.openPerspectiveList();

            // THEN
            perspective.assertions.expectVersioningModeIsPresent();
        });
    });
});
