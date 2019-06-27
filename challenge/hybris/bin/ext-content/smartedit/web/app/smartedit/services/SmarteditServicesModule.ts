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
	InvalidateCacheAnnotationFactory,
	InViewElementObserver,
	PolyfillService,
	PriorityService,
	SeModule,
	SmarteditCommonsModule,
	TestModeService
} from 'smarteditcommons/services';

import {
	CatalogService,
	ComponentHandlerService,
	ContextualMenuService,
	DelegateRestService,
	DragAndDropCrossOrigin,
	ExperienceService,
	FeatureService,
	NotificationMouseLeaveDetectionService,
	NotificationService,
	PageInfoService,
	PermissionService,
	PerspectiveService,
	PreviewService,
	RestServiceFactory,
	SessionService,
	SeNamespaceService,
	SharedDataService,
	StorageService,
	UrlService,
	WaitDialogService
} from 'smartedit/services';

import {StorageModule} from "./storage/StorageModuleInner";
import {
	DEFAULT_CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS,
	DEFAULT_CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE,
	DEFAULT_PROCESS_QUEUE_POLYFILL_INTERVAL,
	DEFAULT_REPROCESS_TIMEOUT,
	SmartEditContractChangeListener
} from "./SmartEditContractChangeListener";
import {SakExecutorService} from "smartedit/services/sakExecutor/SakExecutorService";
import {SmarteditElementComponent} from "smartedit/services/sakExecutor/SmarteditElementComponent";

/**
 * @ngdoc overview
 * @name smarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the smartedit application
 */
@SeModule({
	imports: [
		'coretemplates',
		'seConstantsModule',
		'ngResource',
		'functionsModule',
		'decoratorServiceModule',
		'yLoDashModule',
		'timerModule',
		'resizeListenerModule',
		'positionRegistryModule',
		SmarteditCommonsModule,
		StorageModule
	],
	providers: [
		UrlService,
		SharedDataService,
		CatalogService,
		ComponentHandlerService,
		PageInfoService,
		TestModeService,
		PolyfillService,
		WaitDialogService,
		DelegateRestService,
		RestServiceFactory,
		PreviewService,
		PriorityService,
		PerspectiveService,
		FeatureService,
		NotificationService,
		NotificationMouseLeaveDetectionService,
		StorageService,
		ContextualMenuService,
		DragAndDropCrossOrigin,
		InViewElementObserver,
		CacheService,
		CacheEngine,
		CachedAnnotationFactory,
		CacheConfigAnnotationFactory,
		InvalidateCacheAnnotationFactory,
		ExperienceService,
		SeNamespaceService,
		PermissionService,
		SessionService,
		diNameUtils.makeValueProvider({DEFAULT_REPROCESS_TIMEOUT}),
		diNameUtils.makeValueProvider({DEFAULT_PROCESS_QUEUE_POLYFILL_INTERVAL}),
		diNameUtils.makeValueProvider({DEFAULT_CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS}),
		diNameUtils.makeValueProvider({DEFAULT_CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE}),
		SmartEditContractChangeListener,
		SakExecutorService
	],
	declarations: [
		SmarteditElementComponent
	],
	initialize: (
		cachedAnnotationFactory: any,
		cacheConfigAnnotationFactory: any,
		invalidateCacheAnnotationFactory: any,
		notificationMouseLeaveDetectionService: NotificationMouseLeaveDetectionService) => {
		'ngInject';
	}
})
export class SmarteditServicesModule {}
