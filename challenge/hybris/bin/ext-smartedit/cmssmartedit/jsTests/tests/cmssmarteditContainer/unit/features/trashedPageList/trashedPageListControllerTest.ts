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
import 'jasmine';

import {ICatalogService, IUrlService, SystemEventService} from 'smarteditcommons';
import {TrashedPageListController} from 'cmssmarteditcontainer/components/pages/trashedPageList/TrashedPageListController';
import {promiseHelper} from 'testhelpers';

describe('TrashedPageListController - ', () => {

	// ======= Injected mocks =======
	const $routeParams: ng.route.IRouteParamsService = {
		siteId: 'someSiteId',
		catalogId: 'someCatalogId',
		catalogVersion: 'someCatalogVersion'
	};
	const $location: jasmine.SpyObj<angular.ILocationService> = jasmine.createSpyObj<angular.ILocationService>('$location', ['path']);
	const PAGE_LIST_PATH: string = '/abc::siteId:catalogId:catalogVersion';
	const urlService: jasmine.SpyObj<IUrlService> = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
	const catalogService: jasmine.SpyObj<ICatalogService> = jasmine.createSpyObj<ICatalogService>('catalogService', ['getContentCatalogsForSite']);
	const cmsitemsUri: any = jasmine.createSpyObj<any>('cmsitemsUri', ['$get']);
	const systemEventService: jasmine.SpyObj<SystemEventService> = jasmine.createSpyObj('systemEventService', ['publishAsync', 'subscribe']);
	const catalogVersionPermissionService: any = jasmine.createSpyObj<any>('catalogVersionPermissionService', ['hasSyncPermissionToActiveCatalogVersion']);
	const EVENT_CONTENT_CATALOG_UPDATE: string = 'EVENT_CONTENT_CATALOG_UPDATE';
	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const $scope: jasmine.SpyObj<angular.IScope> = jasmine.createSpyObj<angular.IScope>('$scope', ['$on']);

	// Service being tested
	let trashedPageListController: TrashedPageListController;

	// Mocked Data
	const uriContext = "uriContext";
	const dropdownItems = [{
		template: "<restore-page-item data-page-info='$ctrl.selectedItem' />"
	}, {
		template: "<sync-page-item data-page-info='$ctrl.selectedItem' />"
	}, {
		template: "<permanently-delete-page-item data-page-info='$ctrl.selectedItem' data-uri-context='$ctrl.config.injectedContext.uriContext'/>"
	}];

	beforeEach(() => {

		urlService.buildUriContext.and.returnValue(uriContext);
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'someCatalogId',
			name: {
				en: 'Some Catalog Name'
			}
		}, {
			catalogId: 'someOtherCatalogId',
			name: {
				en: 'Some Other Catalog Name'
			}
		}]));

		catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion.and.returnValue($q.when(true));

		trashedPageListController = new TrashedPageListController(
			$routeParams,
			$location,
			$q,
			$scope,
			PAGE_LIST_PATH,
			urlService,
			catalogService,
			cmsitemsUri,
			systemEventService,
			catalogVersionPermissionService,
			EVENT_CONTENT_CATALOG_UPDATE);

	});

	it('constructor should subscribe to EVENT_CONTENT_CATALOG_UPDATE', () => {
		expect(systemEventService.subscribe).toHaveBeenCalledWith(EVENT_CONTENT_CATALOG_UPDATE, jasmine.any(Function));
	});

	it('init should set trashedPageListConfig with the right attributes', () => {
		expect(trashedPageListController.trashedPageListConfig.sortBy).toEqual('name');
		expect(trashedPageListController.trashedPageListConfig.reversed).toEqual(false);
		expect(trashedPageListController.trashedPageListConfig.itemsPerPage).toEqual(10);
		expect(trashedPageListController.trashedPageListConfig.displayCount).toEqual(true);
		expect(trashedPageListController.trashedPageListConfig.injectedContext).toEqual({
			uriContext,
			dropdownItems,
			permissionForDropdownItems: 'se.edit.page'
		});
		expect(trashedPageListController.trashedPageListConfig.uri).toEqual(cmsitemsUri);
		expect(trashedPageListController.trashedPageListConfig.queryParams).toEqual({
			catalogId: 'someCatalogId',
			catalogVersion: 'someCatalogVersion',
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted'
		});
	});

	it('should initialize with a catalog name to display and other site related params', () => {
		expect(trashedPageListController.siteUID).toEqual('someSiteId');
		expect(trashedPageListController.catalogId).toEqual('someCatalogId');
		expect(trashedPageListController.catalogVersion).toEqual('someCatalogVersion');
		expect(trashedPageListController.catalogName).toEqual({en: 'Some Catalog Name'});
	});

	it('calling reset should clear the mask field', () => {
		trashedPageListController.mask = 'initial mask';
		trashedPageListController.reset();
		expect(trashedPageListController.mask).toEqual('');
	});

});
