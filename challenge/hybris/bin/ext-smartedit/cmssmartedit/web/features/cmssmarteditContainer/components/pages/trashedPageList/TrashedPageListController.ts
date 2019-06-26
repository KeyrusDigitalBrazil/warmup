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
import * as angular from 'angular';
import {
	IBaseCatalog,
	ICatalogService,
	IUrlService,
	IYDropdownMenuItem,
	SeInjectable,
	SystemEventService,
	TypedMap
} from 'smarteditcommons';

@SeInjectable()
export class TrashedPageListController {

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	// site params
	siteUID: string;
	catalogId: string;
	catalogVersion: string;
	catalogName: TypedMap<string>;
	uriContext: any;

	// dynamic paged list params
	trashedPageListConfig: any;
	mask: string;
	dropdownItems: IYDropdownMenuItem[] = [];

	// dynamic paged list API
	dynamicPagedListApi: any;

	eventSubscriptions: any[] = [];

	// --------------------------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------------------------
	constructor(
		private $routeParams: ng.route.IRouteParamsService,
		protected $location: ng.ILocationService,
		protected $q: ng.IQService,
		protected $scope: ng.IScope,
		protected PAGE_LIST_PATH: string,
		private urlService: IUrlService,
		private catalogService: ICatalogService,
		private cmsitemsUri: any,
		private systemEventService: SystemEventService,
		private catalogVersionPermissionService: any,
		EVENT_CONTENT_CATALOG_UPDATE: string
	) {
		this.initialize().then(() => {
			this.eventSubscriptions.push(this.systemEventService.subscribe(EVENT_CONTENT_CATALOG_UPDATE, this.onContentCatalogUpdate.bind(this)));
		});

		this.$scope.$on('$destroy', () => {
			this.eventSubscriptions.forEach((unsubscribeEvent) => {
				unsubscribeEvent();
			});
		});
	}

	public reset(): void {
		this.mask = '';
	}

	public backToPagelist(): void {
		this.$location.path(this.PAGE_LIST_PATH
			.replace(":siteId", this.siteUID)
			.replace(":catalogId", this.catalogId)
			.replace(":catalogVersion", this.catalogVersion));
	}

	public getApi($api: any): void {
		this.dynamicPagedListApi = $api;
	}

	private initialize(): angular.IPromise<void> {
		this.siteUID = this.$routeParams.siteId;
		this.catalogId = this.$routeParams.catalogId;
		this.catalogVersion = this.$routeParams.catalogVersion;
		this.uriContext = this.urlService.buildUriContext(this.siteUID, this.catalogId, this.catalogVersion);

		this.trashedPageListConfig = {
			sortBy: 'name',
			reversed: false,
			itemsPerPage: 10,
			displayCount: true
		};

		// set uri for retrieving trashed pages
		this.trashedPageListConfig.uri = this.cmsitemsUri;
		this.trashedPageListConfig.queryParams = {
			catalogId: this.catalogId,
			catalogVersion: this.catalogVersion,
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted'
		};

		this.trashedPageListConfig.keys = [{
			property: 'name',
			i18n: 'se.cms.pagelist.headerpagename',
			sortable: true
		}, {
			property: 'itemtype',
			i18n: 'se.cms.pagelist.headerpagetype',
			sortable: true
		}, {
			property: 'numberOfRestrictions',
			i18n: 'se.cms.pagelist.headerrestrictions'
		}, {
			property: 'modifiedtime',
			i18n: 'se.cms.trashedpagelist.trashed.date',
			sortable: true
		}, {
			property: 'syncStatus',
			i18n: 'se.cms.actionitem.page.sync'
		}, {
			property: 'dropdownitems',
			i18n: ''
		}];

		// set search params
		this.mask = "";

		this.catalogService.getContentCatalogsForSite(this.siteUID).then((catalogs: IBaseCatalog[]) => {
			this.catalogName = catalogs.filter((catalog: IBaseCatalog) => {
				return catalog.catalogId === this.catalogId;
			})[0].name;
		});

		// renderers Object that contains custom HTML renderers for a given key
		this.trashedPageListConfig.renderers = {
			numberOfRestrictions() {
				return '<restrictions-page-list-icon data-number-of-restrictions="item.restrictions.length"/>';
			},
			modifiedtime() {
				return '<div>{{item.modifiedtime | date:"short"}}</div>';
			},
			syncStatus() {
				return '<div data-recompile-dom="item.reloadSynchIcon"><page-list-sync-icon data-uri-context="$ctrl.config.injectedContext.uriContext" data-page-id="item.uuid" /></div>';
			},
			dropdownitems() {
				return '<div has-operation-permission="$ctrl.config.injectedContext.permissionForDropdownItems" class="paged-list-table__body__td paged-list-table__body__td-menu"><y-drop-down-menu dropdown-items="$ctrl.config.injectedContext.dropdownItems" selected-item="item" class="y-dropdown pull-right" /></div>';
			}
		};

		return this.catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion(this.catalogId, this.catalogVersion).then((canSynchronize: boolean) => {
			this.dropdownItems.push({
				template: "<restore-page-item data-page-info='$ctrl.selectedItem' />"
			});

			if (canSynchronize) {
				this.dropdownItems.push({
					template: "<sync-page-item data-page-info='$ctrl.selectedItem' />"
				});
			}
			this.dropdownItems.push({
				template: "<permanently-delete-page-item data-page-info='$ctrl.selectedItem' data-uri-context='$ctrl.config.injectedContext.uriContext'/>"
			});

			// injectedContext Object. This object is passed to the client-paged-list directive.
			this.trashedPageListConfig.injectedContext = {
				uriContext: this.uriContext,
				permissionForDropdownItems: 'se.edit.page',
				dropdownItems: this.dropdownItems
			};
			return this.$q.when();
		});
	}

	private onContentCatalogUpdate(): void {
		if (this.dynamicPagedListApi) {
			this.dynamicPagedListApi.reloadItems();
		}
	}

}

export const trashedPageListControllerModule = angular
	.module('trashedPageListControllerModule', ['pageListTemplatePrinterModule', 'resourceLocationsModule', 'catalogVersionPermissionModule'])
	.controller(TrashedPageListController.name, TrashedPageListController);
