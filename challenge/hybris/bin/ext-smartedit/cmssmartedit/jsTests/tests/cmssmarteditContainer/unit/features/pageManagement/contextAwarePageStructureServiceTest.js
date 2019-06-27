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
describe('contextAwarePageStructureService', function() {

    var contextAwarePageStructureService;
    var mockTypeStructureRestService;
    var mockPageService;
    var PageStructureMocks = require('../common/pageStructureMocks');

    beforeEach(function() {
        var fixture = AngularUnitTestHelper.prepareModule('contextAwarePageStructureServiceModule')
            .mock('typeStructureRestService', 'getStructureByType')
            .mock('pageService', 'isPagePrimary')
            .service('contextAwarePageStructureService');

        contextAwarePageStructureService = fixture.service;
        mockTypeStructureRestService = fixture.mocks.typeStructureRestService;
        mockPageService = fixture.mocks.pageService;
    });

    describe('getPageStructureForNewPage', function() {
        it('should return the re-ordered fields from the API, with the label disabled, for a variation page', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(false);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForNewPage('ContentPage', false);

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForNewVariationPage());
        });

        it('should return the re-ordered fields from the API, with the label enabled, for a primary page', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForNewPage('ContentPage', true);

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForNewPrimaryPage());
        });

        it('should return the re-ordered fields from the API, with the label disabled, for a primary page when user had read-only permission on label attribute', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFieldsWithReadOnly());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForNewPage('ContentPage', true);

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForNewPrimaryPageWithLabelReadOnlyPermission());
        });
    });

    describe('getPageStructureForPageEditing', function() {
        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to false, for a variation page', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(false);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForPageEditing('ContentPage', 'dummyId');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForEditingVariationPage());
        });

        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to true, for a primary page', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForPageEditing('ContentPage', 'dummyId');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForEditingPrimaryPage());
        });

        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to false, for a primary page when user had read-only permission on label attribute', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFieldsWithReadOnly());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForPageEditing('ContentPage', 'dummyId');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForEditingPrimaryPageWithLabelReadOnlyPermission());
        });
    });

    describe('getPageStructureForViewing', function() {
        it('should return the re-ordered fields from the API, disabling all fields for editing, for a page', function() {
            // Arrange
            mockPageService.isPagePrimary.and.returnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.and.returnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForViewing('ContentPage');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForViewing());
        });
    });

    function getExpectedStructureForNewVariationPage() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForNewPrimaryPage() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": true
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForNewPrimaryPageWithLabelReadOnlyPermission() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingVariationPage() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }, {
                cmsStructureType: "DisplayConditionEditor",
                i18nKey: "type.abstractpage.displayCondition.name",
                qualifier: "displayCondition"
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.creationtime.name",
                "localized": false,
                "qualifier": "creationtime",
                "editable": false
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.modifiedtime.name",
                "localized": false,
                "qualifier": "modifiedtime",
                "editable": false
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingPrimaryPage() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": true
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }, {
                cmsStructureType: "DisplayConditionEditor",
                i18nKey: "type.abstractpage.displayCondition.name",
                qualifier: "displayCondition"
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.creationtime.name",
                "localized": false,
                "qualifier": "creationtime",
                "editable": false
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.modifiedtime.name",
                "localized": false,
                "qualifier": "modifiedtime",
                "editable": false
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingPrimaryPageWithLabelReadOnlyPermission() {
        return {
            attributes: [{
                cmsStructureType: "ShortString",
                i18nKey: "se.cms.pageinfo.page.type",
                qualifier: "typeCode",
                editable: false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.name.name",
                "localized": false,
                "qualifier": "name"
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.contentpage.label.name",
                "localized": false,
                "qualifier": "label",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.uid.name",
                "localized": false,
                "qualifier": "uid",
                "editable": false
            }, {
                "cmsStructureType": "ShortString",
                "i18nKey": "type.abstractpage.title.name",
                "localized": true,
                "qualifier": "title"
            }, {
                cmsStructureType: "DisplayConditionEditor",
                i18nKey: "type.abstractpage.displayCondition.name",
                qualifier: "displayCondition"
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.creationtime.name",
                "localized": false,
                "qualifier": "creationtime",
                "editable": false
            }, {
                "cmsStructureType": "DateTime",
                "i18nKey": "type.abstractpage.modifiedtime.name",
                "localized": false,
                "qualifier": "modifiedtime",
                "editable": false
            }],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForViewing() {
        return {
            attributes: [{
                cmsStructureType: 'ShortString',
                i18nKey: 'type.abstractpage.name.name',
                localized: false,
                qualifier: 'name',
                editable: false
            }, {
                cmsStructureType: 'ShortString',
                i18nKey: 'type.contentpage.label.name',
                localized: false,
                qualifier: 'label',
                editable: false
            }, {
                cmsStructureType: 'ShortString',
                i18nKey: 'type.abstractpage.uid.name',
                localized: false,
                qualifier: 'uid',
                editable: false
            }, {
                cmsStructureType: 'ShortString',
                i18nKey: 'type.abstractpage.title.name',
                localized: true,
                qualifier: 'title',
                editable: false
            }, {
                cmsStructureType: 'DateTime',
                i18nKey: 'type.abstractpage.creationtime.name',
                localized: false,
                qualifier: 'creationtime',
                editable: false
            }, {
                cmsStructureType: 'DateTime',
                i18nKey: 'type.abstractpage.modifiedtime.name',
                localized: false,
                qualifier: 'modifiedtime',
                editable: false
            }],
            category: 'PAGE'
        };
    }
});
