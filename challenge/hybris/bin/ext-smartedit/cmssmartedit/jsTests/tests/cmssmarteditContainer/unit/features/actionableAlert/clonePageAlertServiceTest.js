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
describe('clonePageAlertService', function() {

    var fixture;
    var clonePageAlertService;
    var mockActionableAlertService;
    var mockExperienceService;
    var mockCatalogService;

    var $q, $rootScope;

    var MOCKED_CLONED_PAGE_INFO = {
        catalogVersion: "MOCKED_CATALOG_VERSION",
        uid: "MOCKED_UID"
    };

    var MOCKED_SITE_ID = 'some site id';
    var MOCKED_CATALOG_ID = 'some catalog id';
    var MOCKED_VERSION = 'some version';

    var MOCKED_CATALOG_VERSION = {
        siteId: MOCKED_SITE_ID,
        catalogId: MOCKED_CATALOG_ID,
        catalogName: {
            en: "MOCKED_CATALOG_NAME_EN"
        },
        version: MOCKED_VERSION
    };

    beforeEach(angular.mock.module("functionsModule"));
    beforeEach(angular.mock.module("l10nModule"));

    beforeEach(function() {

        fixture = AngularUnitTestHelper.prepareModule('clonePageAlertServiceModule')
            .mock('actionableAlertService', 'displayActionableAlert')
            .mock('experienceService', 'loadExperience')
            .mock('catalogService', 'getCatalogVersionByUuid')
            .service('clonePageAlertService');

        clonePageAlertService = fixture.service;
        mockActionableAlertService = fixture.mocks.actionableAlertService;
        mockExperienceService = fixture.mocks.experienceService;
        mockCatalogService = fixture.mocks.catalogService;
        $q = fixture.injected.$q;
        $rootScope = fixture.injected.$rootScope;

        mockCatalogService.getCatalogVersionByUuid.and.callFake(function() {
            return $q.when(MOCKED_CATALOG_VERSION);
        });
    });

    describe('displayClonePageAlert', function() {

        it("should trigger 'mockActionableAlertService.displayActionableAlert' with the proper arguments", function() {

            // Act
            clonePageAlertService.displayClonePageAlert(MOCKED_CLONED_PAGE_INFO);
            fixture.detectChanges();

            // Assert
            expect(mockCatalogService.getCatalogVersionByUuid).toHaveBeenCalledWith("MOCKED_CATALOG_VERSION");
            expect(mockActionableAlertService.displayActionableAlert).toHaveBeenCalledWith({
                controller: ['experienceService', 'l10nFilter', jasmine.any(Function)]
            });

        });

    });

    describe("injected 'onClick()' method", function() {

        it("should have a description, an hyperlinkLabel and a controller function", function() {

            // Act
            clonePageAlertService.displayClonePageAlert(MOCKED_CLONED_PAGE_INFO);
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(mockExperienceService);

            // Assert
            expect(mockCatalogService.getCatalogVersionByUuid).toHaveBeenCalledWith('MOCKED_CATALOG_VERSION');
            expect(controllerInstance.description).toBe("se.cms.clonepage.alert.info.description");
            expect(controllerInstance.descriptionDetails).toEqual({
                catalogName: 'MOCKED_CATALOG_NAME_EN',
                catalogVersion: 'some version'
            });
            expect(controllerInstance.hyperlinkLabel).toBe("se.cms.clonepage.alert.info.hyperlink");
            expect(controllerInstance.onClick).not.toBeNull();

        });

        it("should throw an exception if provided uid is 'blank'", function() {

            // Act
            clonePageAlertService.displayClonePageAlert({
                catalogVersion: "MOCKED_CATALOG_VERSION"
            });
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(mockExperienceService);

            // Assert
            expect(function() {
                controllerInstance.onClick();
            }).toThrow("clonePageAlertService.checkAndAlertOnClonePage - missing required parameter 'uid'");

        });

        it("should trigger 'experienceService.updateExperiencePageId' method when uid properly provided", function() {

            // Act
            clonePageAlertService.displayClonePageAlert(MOCKED_CLONED_PAGE_INFO);
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(mockExperienceService);

            controllerInstance.onClick();
            $rootScope.$digest();

            // Assert
            expect(mockExperienceService.loadExperience).toHaveBeenCalledWith({
                siteId: MOCKED_SITE_ID,
                catalogId: MOCKED_CATALOG_ID,
                catalogVersion: MOCKED_VERSION,
                pageId: MOCKED_CLONED_PAGE_INFO.uid
            });
        });

    });

});
