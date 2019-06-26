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
describe('pageListController for a non active catalogVersion', function() {

    var $rootScope, $q;
    var controller, mocks;
    var uriContext = "uriContext";
    var pageUriContext = "pageUriContext";

    var MOCKED_EVENT_NAME = "EVENT_CONTENT_CATALOG_UPDATE";

    beforeEach(angular.mock.module(function($provide) {
        $provide.value("TRASHED_PAGE_LIST_PATH", "TRASHED_PAGE_LIST_PATH");
    }));

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageListControllerModule')
            .mock('urlService', 'buildUriContext').and.returnValue(uriContext)
            .mock('urlService', 'buildPageUriContext').and.returnValue(pageUriContext)
            .mock('catalogService', 'getContentCatalogsForSite').and.returnResolvedPromise([{
                'catalogId': 'someCatalogId',
                'name': 'Some Catalog Name'
            }, {
                'catalogId': 'someOtherCatalogId',
                'name': 'Some Other Catalog Name'
            }])
            .mock('catalogService', 'isContentCatalogVersionNonActive').and.returnResolvedPromise(true)
            .mock('syncPageModalService', 'open').and.returnResolvedPromise('')
            .mock('clonePageWizardService', 'openClonePageWizard').and.returnResolvedPromise('')
            .mock('pageEditorModalService', 'open').and.returnResolvedPromise('')
            .mock('sharedDataService', 'set').and.returnResolvedPromise('')
            .mock('addPageWizardService', 'openAddPageWizard')
            .mock('syncPollingService', 'initSyncPolling')
            .mock('systemEventService', 'subscribe')
            .mock('cmsitemsUri', '$get').and.returnValue('testUrl')
            .mock('managePageService', 'getSoftDeletedPagesCount').and.returnResolvedPromise(2)
            .mock('experienceService', 'loadExperience')
            .mock('catalogVersionPermissionService', 'hasSyncPermissionToActiveCatalogVersion').and.returnResolvedPromise(true)
            .controller('pageListController', {
                $routeParams: {
                    siteId: 'someSiteId',
                    catalogId: 'someCatalogId',
                    catalogVersion: 'someCatalogVersion'
                },
                EVENT_CONTENT_CATALOG_UPDATE: MOCKED_EVENT_NAME
            });

        controller = harness.controller;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

    });

    describe('init', function() {
        it('should initialize with default pageListConfig properties', function() {
            expect(controller.pageListConfig.sortBy).toEqual('name');
            expect(controller.pageListConfig.reversed).toEqual(false);
            expect(controller.pageListConfig.itemsPerPage).toEqual(10);
            expect(controller.pageListConfig.displayCount).toEqual(true);
        });

        it('should initialize a list of dropdown items and injected context', function() {
            expect(controller.pageListConfig.dropdownItems).toEqual([{
                template: "<edit-page-item data-page-info='$ctrl.selectedItem' />"
            }, {
                template: "<sync-page-item data-page-info='$ctrl.selectedItem' />"
            }, {
                template: "<clone-page-item data-page-info='$ctrl.selectedItem' />"
            }, {
                template: "<delete-page-item data-page-info='$ctrl.selectedItem' />"
            }]);

            expect(controller.pageListConfig.injectedContext).toEqual({
                onLink: jasmine.any(Function),
                uriContext: uriContext,
                dropdownItems: controller.pageListConfig.dropdownItems,
                permissionForDropdownItems: 'se.edit.page'
            });
        });

        it('should initialize with the page list uri and page list query params', function() {
            expect(controller.pageListConfig.uri).toEqual(mocks.cmsitemsUri);
            expect(controller.pageListConfig.queryParams).toEqual({
                catalogId: 'someCatalogId',
                catalogVersion: 'someCatalogVersion',
                typeCode: 'AbstractPage',
                itemSearchParams: 'pageStatus:active'
            });

        });

        it('should initialize with a catalog name to display and other site related params', function() {
            expect(controller.siteUID).toEqual('someSiteId');
            expect(controller.catalogId).toEqual('someCatalogId');
            expect(controller.catalogVersion).toEqual('someCatalogVersion');
            expect(controller.catalogName).toEqual('Some Catalog Name');
        });

        it('should initialize a list of dropdown items', function() {
            expect(controller.pageListConfig.keys).toEqual([{
                property: 'name',
                i18n: 'se.cms.pagelist.headerpagename',
                sortable: true
            }, {
                property: 'uid',
                i18n: 'se.cms.pagelist.headerpageid',
                sortable: true
            }, {
                property: 'itemtype',
                i18n: 'se.cms.pagelist.headerpagetype',
                sortable: true
            }, {
                property: 'template',
                i18n: 'se.cms.pagelist.headerpagetemplate'
            }, {
                property: 'numberOfRestrictions',
                i18n: 'se.cms.pagelist.headerrestrictions'
            }, {
                property: 'syncStatus',
                i18n: 'se.cms.actionitem.page.sync'
            }, {
                property: 'dropdownitems',
                i18n: ''
            }]);
        });

        it('should initialize event subscriptions', function() {
            expect(mocks.systemEventService.subscribe).toHaveBeenCalledWith(MOCKED_EVENT_NAME, jasmine.any(Function));
        });

    });
});
