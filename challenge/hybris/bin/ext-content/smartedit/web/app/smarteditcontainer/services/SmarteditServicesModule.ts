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

import {
	diNameUtils,
	CachedAnnotationFactory,
	CacheConfigAnnotationFactory,
	CacheEngine,
	CacheService,
	ContentCatalogRestService,
	InvalidateCacheAnnotationFactory,
	InViewElementObserver,
	PolyfillService,
	PriorityService,
	ProductCatalogRestService,
	SeModule,
	SmarteditBootstrapGateway,
	SmarteditCommonsModule,
	TestModeService
} from 'smarteditcommons';

import {
	BootstrapService,
	CatalogService,
	CatalogVersionPermissionRestService,
	ConfigurationExtractorService,
	CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT,
	DelegateRestService,
	DragAndDropCrossOrigin,
	DEFAULT_DEFAULT_RULE_NAME,
	DEVICE_ORIENTATIONS,
	DEVICE_SUPPORTS,
	ExperienceService,
	FeatureService,
	IframeManagerService,
	NotificationMouseLeaveDetectionService,
	NotificationService,
	PageInfoService,
	PermissionsRegistrationService,
	PermissionService,
	PerspectiveService,
	PreviewService,
	ProductService,
	RestServiceFactory,
	SessionService,
	SharedDataService,
	SiteService,
	StorageService,
	UrlService,
	WaitDialogService
} from 'smarteditcontainer/services';

import {StorageModule} from "./storage/StorageModuleOuter";
import {ExperienceSelectorComponent, SitesLinkComponent, UserAccountComponent} from 'smarteditcontainer/components';

/**
 * @ngdoc overview
 * @name smarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the smartedit container application
 */
@SeModule({
	declarations: [
		ExperienceSelectorComponent,
		UserAccountComponent,
		SitesLinkComponent
	],
	imports: [
		'seConstantsModule',
		'ngResource',
		SmarteditCommonsModule,
		'browserServiceModule',
		'ngCookies',
		'functionsModule',
		'toolbarModule',
		'resourceLocationsModule',
		'yLoDashModule',
		'modalServiceModule',
		StorageModule.configure(),
		'previewDataDropdownPopulatorModule',
		'heartBeatServiceModule',
		'loadConfigModule'
	],
	providers: [
		SmarteditBootstrapGateway,
		CatalogService,
		CatalogVersionPermissionRestService,
		ConfigurationExtractorService,
		ContentCatalogRestService,
		BootstrapService,
		UrlService,
		SharedDataService,
		PageInfoService,
		ProductCatalogRestService,
		TestModeService,
		PolyfillService,
		WaitDialogService,
		DelegateRestService,
		FeatureService,
		{
			provide: 'DEVICE_ORIENTATIONS',
			useValue: DEVICE_ORIENTATIONS
		},
		{
			provide: "DEVICE_SUPPORTS",
			useValue: DEVICE_SUPPORTS
		},
		IframeManagerService,
		RestServiceFactory,
		PerspectiveService,
		PreviewService,
		PriorityService,
		ProductService,
		SiteService,
		NotificationService,
		NotificationMouseLeaveDetectionService,
		DragAndDropCrossOrigin,
		InViewElementObserver,
		StorageService,
		SessionService,
		PermissionsRegistrationService,
		CacheService,
		CacheEngine,
		CachedAnnotationFactory,
		CacheConfigAnnotationFactory,
		InvalidateCacheAnnotationFactory,
		ExperienceService,
		PermissionService,
		diNameUtils.makeValueProvider({DEFAULT_DEFAULT_RULE_NAME}),
		diNameUtils.makeValueProvider({
			CATALOG_VERSION_PERMISSIONS_RESOURCE_URI: CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT
		}),
	],
	initialize: (
		cachedAnnotationFactory: any,
		cacheConfigAnnotationFactory: any,
		invalidateCacheAnnotationFactory: any,
		previewService: any) => {
		'ngInject';
	}
})
export class SmarteditServicesModule {
}