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
describe('clonePageBuilderFactory', function() {

    var $rootScope;
    var $q;

    var ClonePageBuilderFactory;
    var pageInfo, newPageInfo, pageTemplate;

    var mockPageListService;
    var mockCmsitemsRestServiceModule;
    var mockpageInfoService;
    var mockContextAwarePageStructureService;
    var mockTypeStructureRestService;
    var mocks;
    var PageStructureMocks = require('../../common/pageStructureMocks');

    var restrictionsStepHandler = {
        hideStep: function() {},
        showStep: function() {}
    };

    //data
    var primaryPageDisplayConditionData = {
        isPrimary: true
    };

    var variationPageDisplayConditionData = {
        isPrimary: false,
        primaryPage: {
            label: 'newPrimaryLabel'
        }
    };

    var restrictionsData = [{
        uid: 'restrictionId1',
        uuid: 'restrictionUuid1'
    }, {
        uid: 'restrictionId2',
        uuid: 'restrictionUuid2'
    }];

    beforeEach(function() {

        pageInfo = {
            creationtime: "pageCreationTime",
            modifiedtime: "pageModifiedTime",
            pk: "some pk",
            masterTemplate: "PageTemplateUuid",
            name: "pageName",
            label: 'pageLabel',
            typeCode: "pageTypeCode",
            uid: "pageUid",
            uuid: "somePageUUID",
            itemtype: 'pageType'
        };

        newPageInfo = {
            creationtime: "pageCreationTime",
            modifiedtime: "pageModifiedTime",
            pk: "some pk",
            masterTemplate: "PageTemplateUuid",
            name: "pageName",
            label: 'pageLabel',
            typeCode: "pageTypeCode",
            uid: "pageUid",
            pageUuid: "somePageUUID",
            itemtype: 'pageType',
            template: 'PageTemplate',
            type: 'somePageType',
            catalogVersion: 'someCatalogVersionUUID'
        };

        pageTemplate = {
            uuid: 'PageTemplateUuid',
            uid: 'PageTemplate'
        };

        var harness = AngularUnitTestHelper.prepareModule('clonePageWizardServiceModule')
            .mock('pageListService', 'getPageById')
            .mock('pageInfoService', 'getPageUUID')
            .mock('contextAwarePageStructureService', 'getPageStructureForNewPage')
            .mock('typeStructureRestService', 'getStructureByTypeAndMode')
            .mock('restrictionsStepHandlerFactory', 'createRestrictionsStepHandler').and.returnValue(restrictionsStepHandler)
            .mock('cmsitemsRestService', 'getById')
            .mock('catalogService', 'getCatalogVersionUUid').and.returnValue('someCatalogVersionUUID')
            .service('ClonePageBuilderFactory');

        ClonePageBuilderFactory = harness.service;
        mocks = harness.mocks;
        mockPageListService = mocks.pageListService;
        mockCmsitemsRestServiceModule = mocks.cmsitemsRestService;
        mockpageInfoService = mocks.pageInfoService;
        mockContextAwarePageStructureService = mocks.contextAwarePageStructureService;
        mockTypeStructureRestService = mocks.typeStructureRestService;
        $rootScope = harness.injected.$rootScope;
        $q = harness.injected.$q;

        mockpageInfoService.getPageUUID.and.returnValue($q.when('somePageUUID'));

        mockPageListService.getPageById.and.returnValue($q.when(pageInfo));
        mockContextAwarePageStructureService.getPageStructureForNewPage.and.returnValue($q.when(PageStructureMocks.getFields()));
        mockTypeStructureRestService.getStructureByTypeAndMode.and.returnValue($q.when({
            type: 'somePageType'
        }));

        mockCmsitemsRestServiceModule.getById.and.callFake(function(itemId) {
            if (itemId === 'somePageUUID') {
                return $q.when(pageInfo);
            } else if (itemId === 'PageTemplateUuid') {
                return $q.when(pageTemplate);
            }
            return $q.when({});
        });
    });

    it('WHEN basePageUid is not passed THEN clonePageBuilder will call pageInfoService to fetch pageUid and then fetch page details', function() {

        var clonePageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();
        expect(mockCmsitemsRestServiceModule.getById).toHaveBeenCalledWith('somePageUUID');
    });

    it('WHEN basePageUid is passed THEN clonePageBuilder will call fetch page details by passing the basePageUid to PageListService', function() {

        var clonePageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler, 'someOtherPageUid');
        $rootScope.$digest();
        expect(mockCmsitemsRestServiceModule.getById).toHaveBeenCalledWith('someOtherPageUid');

    });

    it('WHEN clonePageBuilder is called THEN basic page information is fetch and set to the page object', function() {

        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        expect(pageBuilder.getPageInfo()).toEqual(newPageInfo);
        expect(pageBuilder.getPageTypeCode()).toEqual('pageTypeCode');
        expect(pageBuilder.getPageTemplate()).toEqual('PageTemplate');

    });

    it('WHEN displayConditionSelected for primary is called THEN page structure is fetched based on the type code', function() {

        spyOn(restrictionsStepHandler, 'hideStep');
        spyOn(restrictionsStepHandler, 'showStep');
        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.displayConditionSelected(primaryPageDisplayConditionData);
        $rootScope.$digest();

        expect(restrictionsStepHandler.hideStep).toHaveBeenCalled();
        expect(restrictionsStepHandler.showStep).not.toHaveBeenCalled();
        expect(mockContextAwarePageStructureService.getPageStructureForNewPage).toHaveBeenCalledWith('pageTypeCode', true);
        expect(pageBuilder.getPageInfoStructure()).toEqual(PageStructureMocks.getFields());

    });

    it('WHEN displayConditionSelected for variation is called THEN clone page label is set to the selected primaryPage and the page structure is fetched based on the type code', function() {

        spyOn(restrictionsStepHandler, 'hideStep');
        spyOn(restrictionsStepHandler, 'showStep');
        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.displayConditionSelected(variationPageDisplayConditionData);
        $rootScope.$digest();

        expect(pageBuilder.getPageInfo().label).toEqual('newPrimaryLabel');
        expect(restrictionsStepHandler.hideStep).not.toHaveBeenCalled();
        expect(restrictionsStepHandler.showStep).toHaveBeenCalled();
        expect(mockContextAwarePageStructureService.getPageStructureForNewPage).toHaveBeenCalledWith('pageTypeCode', false);
        expect(pageBuilder.getPageInfoStructure()).toEqual(PageStructureMocks.getFields());

    });

    it('WHEN displayConditionSelected for primary/variation is called and if page has no typeCode THEN page structure is set to empty', function() {

        delete pageInfo.typeCode;
        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.displayConditionSelected({
            isPrimary: true
        });
        $rootScope.$digest();

        expect(mockContextAwarePageStructureService.getPageStructureForNewPage).not.toHaveBeenCalled();
        expect(pageBuilder.getPageInfoStructure()).toEqual([]);

    });

    it('WHEN componentCloneOptionSelected is called with "clone" option THEN getComponentCloneOption will return "clone" ', function() {

        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.componentCloneOptionSelected('clone');

        expect(pageBuilder.getComponentCloneOption()).toEqual('clone');

    });

    it('WHEN componentCloneOptionSelected is called with "reference" option THEN getComponentCloneOption will return "reference" ', function() {

        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.componentCloneOptionSelected('reference');

        expect(pageBuilder.getComponentCloneOption()).toEqual('reference');

    });

    it('WHEN restrictionsSelected is called with onlyOneRestrictionMustApply and list of restrictions THEN corresponding values are set to the page object ', function() {

        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        pageBuilder.restrictionsSelected(true, restrictionsData);

        expect(pageBuilder.getPageRestrictions()).toEqual([{
            uid: 'restrictionId1',
            uuid: 'restrictionUuid1'
        }, {
            uid: 'restrictionId2',
            uuid: 'restrictionUuid2'
        }]);

        expect(pageBuilder.getPageInfo()).toEqual(window.smarteditJQuery.extend({}, newPageInfo, {
            onlyOneRestrictionMustApply: true,
            restrictions: [{
                uid: 'restrictionId1',
                uuid: 'restrictionUuid1'
            }, {
                uid: 'restrictionId2',
                uuid: 'restrictionUuid2'
            }]
        }));

    });

    it('WHEN getPageProperties is called THEN it return basic page info such as type, typeCode, template and onlyOneRestrictionMustApply', function() {

        pageInfo.onlyOneRestrictionMustApply = false;
        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        expect(pageBuilder.getPageProperties()).toEqual({
            type: 'somePageType',
            typeCode: 'pageTypeCode',
            template: 'PageTemplate',
            catalogVersion: 'someCatalogVersionUUID',
            onlyOneRestrictionMustApply: false
        });

    });

    it('WHEN targetCatalogVersionSelected is called with selected catalog version THEN catalogVersion value is set in the page object', function() {

        var pageBuilder = new ClonePageBuilderFactory(restrictionsStepHandler);
        $rootScope.$digest();

        var catalogVersion = {
            uuid: 'someCatalogVersionUUID'
        };

        pageBuilder.onTargetCatalogVersionSelected(catalogVersion);

        expect(pageBuilder.getTargetCatalogVersion()).toEqual(catalogVersion);
        expect(pageBuilder.getPageInfo().catalogVersion).toEqual(catalogVersion.uuid);
    });
});
