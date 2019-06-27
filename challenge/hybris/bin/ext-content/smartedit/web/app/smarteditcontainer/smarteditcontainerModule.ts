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
import {doImport as forcedImport} from './forcedImports';
forcedImport();
import {deprecate} from "smarteditcontainer/deprecate";
deprecate();

import * as angular from 'angular';
import {
	instrument,
	AuthorizationService,
	Cloneable,
	GatewayFactory,
	IFeatureService,
	IPermissionService,
	IRestServiceFactory,
	IUrlService,
	IWaitDialogService,
	PermissionContext,
	SeModule
} from 'smarteditcommons';
import {
	BootstrapService,
	CatalogAwareRouteResolverModule,
	DelegateRestService,
	ExperienceService,
	IframeManagerService,
	PermissionsRegistrationService,
	PerspectiveService,
	SharedDataService,
	SmarteditServicesModule,
	StorageService,
} from 'smarteditcontainer/services';
import {SmarteditDefaultController} from 'smarteditcontainer/components/SmarteditDefaultController';
import {ToolbarItemContextComponent} from 'smarteditcontainer/components/topToolbars/toolbarItemContext/ToolbarItemContextComponent';
import {AdministrationModule} from 'smarteditcontainer/modules';
import {GenericEditorModule} from "smarteditcommons/components/genericEditor/GenericEditorModule";
import {TreeModule} from "smarteditcommons/components/tree/TreeModule";

const TOP_LEVEL_MODULE_NAME = 'smarteditcontainer';

@SeModule({
	declarations: [ToolbarItemContextComponent],
	imports: [
		SmarteditServicesModule,
		AdministrationModule,
		'landingPageModule',
		'templateCacheDecoratorModule',
		'ngRoute',
		'ngResource',
		'ui.bootstrap',
		'coretemplates',
		'loadConfigModule',
		'alertsBoxModule',
		'httpAuthInterceptorModule',
		'experienceInterceptorModule',
		'toolbarModule',
		'modalServiceModule',
		'catalogDetailsModule',
		'experienceSelectorButtonModule',
		'inflectionPointSelectorModule',
		'paginationFilterModule',
		'resourceLocationsModule',
		'perspectiveSelectorModule',
		'hasOperationPermissionModule',
		'l10nModule',
		TreeModule,
		'ySelectModule',
		'yHelpModule',
		'renderServiceModule',
		'systemAlertsModule',
		'yCollapsibleContainerModule',
		'yNotificationPanelModule',
		CatalogAwareRouteResolverModule,
		'catalogVersionPermissionModule',
		'httpErrorInterceptorServiceModule',
		'unauthorizedErrorInterceptorModule',
		'resourceNotFoundErrorInterceptorModule',
		'nonvalidationErrorInterceptorModule',
		'permissionErrorInterceptorModule',
		'previewErrorInterceptorModule',
		'retryInterceptorModule',
		'seConstantsModule',
		'pageSensitiveDirectiveModule',
		'yjqueryModule',
		GenericEditorModule,
		'recompileDomModule'
	],
	config: ($provide: angular.auto.IProvideService, readObjectStructureFactory: () => (arg: Cloneable) => Cloneable, LANDING_PAGE_PATH: string, STORE_FRONT_CONTEXT: string, $routeProvider: angular.route.IRouteProvider, $logProvider: angular.ILogProvider, catalogAwareRouteResolverFunctions: any) => {
		'ngInject';

		instrument($provide, readObjectStructureFactory(), TOP_LEVEL_MODULE_NAME);

		$routeProvider.when(LANDING_PAGE_PATH, {
			template: '<landing-page></landing-page>'
		})
			.when(STORE_FRONT_CONTEXT, {
				templateUrl: 'mainview.html',
				controller: SmarteditDefaultController,
				resolve: {
					setExperience: catalogAwareRouteResolverFunctions.storefrontResolve
				}
			})
			.otherwise({
				redirectTo: LANDING_PAGE_PATH
			});

		$logProvider.debugEnabled(false);
	},
	initialize: (
		$rootScope: angular.IRootScopeService,
		$log: angular.ILogService,
		$q: angular.IQService,
		DEFAULT_RULE_NAME: string,
		EVENTS: any,
		smarteditBootstrapGateway: any,
		toolbarServiceFactory: any,
		perspectiveService: PerspectiveService,
		gatewayFactory: GatewayFactory,
		loadConfigManagerService: any,
		bootstrapService: BootstrapService,
		iframeManagerService: IframeManagerService,
		waitDialogService: IWaitDialogService,
		experienceService: ExperienceService,
		restServiceFactory: IRestServiceFactory,
		delegateRestService: DelegateRestService,
		sharedDataService: SharedDataService,
		urlService: IUrlService,
		featureService: IFeatureService,
		storageService: StorageService,
		renderService: any,
		closeOpenModalsOnBrowserBack: any,
		authorizationService: AuthorizationService,
		permissionService: IPermissionService,
		httpErrorInterceptorService: any,
		unauthorizedErrorInterceptor: any,
		resourceNotFoundErrorInterceptor: any,
		nonValidationErrorInterceptor: any,
		previewErrorInterceptor: any,
		permissionErrorInterceptor: any,
		retryInterceptor: any,
		yjQuery: any,
		SMARTEDIT_IFRAME_WRAPPER_ID: string,
		permissionsRegistrationService: PermissionsRegistrationService
	) => {
		'ngInject';
		gatewayFactory.initListener();
		httpErrorInterceptorService.addInterceptor(retryInterceptor);
		httpErrorInterceptorService.addInterceptor(unauthorizedErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(resourceNotFoundErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(nonValidationErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(previewErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(permissionErrorInterceptor);

		loadConfigManagerService.loadAsObject().then((configurations: any) => {
			sharedDataService.set('defaultToolingLanguage', configurations.defaultToolingLanguage);
		});

		const smartEditHeaderToolbarService = toolbarServiceFactory.getToolbarService("smartEditHeaderToolbar");

		smartEditHeaderToolbarService.addItems([{
			key: 'headerToolbar.logoTemplate',
			type: 'TEMPLATE',
			include: 'headerToolbarLogoTemplate.html',
			priority: 1,
			section: 'left'
		}, {
			key: 'headerToolbar.userAccountTemplate',
			type: 'TEMPLATE',
			include: 'headerToolbarUserAccountTemplate.html',
			priority: 1,
			section: 'right'
		}, {
			key: 'headerToolbar.languageSelectorTemplate',
			type: 'TEMPLATE',
			include: 'headerToolbarLanguageSelectorTemplate.html',
			priority: 2,
			section: 'right'
		}, {
			key: 'headerToolbar.configurationTemplate',
			type: 'TEMPLATE',
			include: 'headerToolbarConfigurationTemplate.html',
			priority: 3,
			section: 'right'
		}]);

		const smartEditExperienceToolbarService = toolbarServiceFactory.getToolbarService("smartEditExperienceToolbar");

		smartEditExperienceToolbarService.addItems([{
			key: 'experienceToolbar.sitesLinkTemplate',
			type: 'TEMPLATE',
			include: 'sitesLinkWrapperTemplate.html',
			priority: 1,
			section: 'left'
		}, {
			key: 'experienceToolbar.deviceSupportTemplate',
			type: 'TEMPLATE',
			include: 'deviceSupportTemplate.html',
			priority: 1,
			section: 'right'
		}, {
			type: 'TEMPLATE',
			key: 'experienceToolbar.experienceSelectorTemplate',
			className: 'ySEPreviewSelector',
			include: 'experienceSelectorWrapperTemplate.html',
			priority: 1, // first in the middle
			section: 'middle'
		}]);

		const smartEditPerspectiveToolbarService = toolbarServiceFactory.getToolbarService("smartEditPerspectiveToolbar");

		smartEditPerspectiveToolbarService.addItems([{
			key: "perspectiveToolbar.perspectiveSelectorTemplate",
			type: 'TEMPLATE',
			section: 'right',
			priority: 1,
			include: 'perspectiveSelectorWrapperTemplate.html'
		}]);

		function offSetStorefront() {
			// Set the storefront offset
			yjQuery(SMARTEDIT_IFRAME_WRAPPER_ID).css('padding-top', (yjQuery('.ySmartEditToolbars') as JQuery).height() + 'px');
		}

		smarteditBootstrapGateway.subscribe("loading", (eventId: string, data: any) => {
			const deferred = $q.defer();

			iframeManagerService.setCurrentLocation(data.location);
			waitDialogService.showWaitModal();

			const smartEditBootstrapped = getBootstrapNamespace();
			delete smartEditBootstrapped[data.location];

			return deferred.promise;
		});

		smarteditBootstrapGateway.subscribe("unloading", (eventId: string, data: any) => {
			const deferred = $q.defer();

			waitDialogService.showWaitModal();

			return deferred.promise;
		});

		smarteditBootstrapGateway.subscribe("bootstrapSmartEdit", (eventId: string, data: any) => {
			offSetStorefront();
			const deferred = $q.defer();
			const smartEditBootstrapped = getBootstrapNamespace();

			if (!smartEditBootstrapped[data.location]) {
				smartEditBootstrapped[data.location] = true;
				loadConfigManagerService.loadAsObject().then((configurations: any) => {
					bootstrapService.bootstrapSEApp(configurations);
					deferred.resolve();
				});
			} else {
				deferred.resolve();
			}
			return deferred.promise;
		});

		smarteditBootstrapGateway.subscribe("smartEditReady", function() {
			const deferred = $q.defer();
			deferred.resolve();

			waitDialogService.hideWaitModal();
			return deferred.promise;
		});

		$rootScope.$on('$routeChangeSuccess', function() {
			closeOpenModalsOnBrowserBack();
		});

		gatewayFactory.createGateway('accessTokens').subscribe("get", function() {
			return $q.when(storageService.getAuthTokens());
		});

		permissionService.registerDefaultRule({
			names: [DEFAULT_RULE_NAME],
			verify: (permissionNameObjs: PermissionContext[]) => {
				const permissionNames = permissionNameObjs.map((permissionName: PermissionContext) => {
					return permissionName.name;
				});
				return authorizationService.hasGlobalPermissions(permissionNames);
			}
		});

		// storefront actually loads twice all the JS files, including webApplicationInjector.js, smartEdit must be protected against receiving twice a smartEditBootstrap event
		function getBootstrapNamespace(): any {
			const smarteditWindow = window as any;
			if (smarteditWindow.smartEditBootstrapped) {
				smarteditWindow.smartEditBootstrapped = {};
			}
			return smarteditWindow.smartEditBootstrapped;
		}

		permissionsRegistrationService.registerRulesAndPermissions();
	}
})
/** @internal */
export class Smarteditcontainer {}