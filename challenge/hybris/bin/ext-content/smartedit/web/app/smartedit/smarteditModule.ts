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
import {doImport as forceImport} from './forcedImports';
forceImport();
import {deprecate} from "smartedit/deprecate";
deprecate();

import * as angular from 'angular';
import * as lo from 'lodash';
import {
	instrument,
	Cloneable,
	CrossFrameEventService,
	GatewayFactory,
	IDragAndDropEvents,
	IExperienceService,
	IFeatureService,
	IPageInfoService,
	IPermissionService,
	IPerspectiveService,
	ISharedDataService,
	LanguageService,
	PolyfillService,
	SeModule,
	SystemEventService,
	TestModeService,
	TypedMap
} from 'smarteditcommons';

import {SystemModule} from 'smartedit/modules';
import {
	DelegateRestService,
	RestServiceFactory,
	SeNamespaceService,
	SmarteditServicesModule
} from 'smartedit/services';
import {HtmlDirective} from 'smartedit/directives/HtmlDirective';
import {SmartEditContractChangeListener} from "smartedit/services/SmartEditContractChangeListener";

@SeModule({
	imports: [
		SmarteditServicesModule,
		'yjqueryModule',
		'templateCacheDecoratorModule',
		'ui.bootstrap',
		'ngResource',
		'decoratorServiceModule',
		'alertsBoxModule',
		'ui.select',
		'httpAuthInterceptorModule',
		'httpErrorInterceptorServiceModule',
		'unauthorizedErrorInterceptorModule',
		'retryInterceptorModule',
		'resourceNotFoundErrorInterceptorModule',
		'experienceInterceptorModule',
		'renderServiceModule',
		'iframeClickDetectionServiceModule',
		'sanitizeHtmlInputModule',
		'resizeComponentServiceModule',
		'slotContextualMenuDecoratorModule',
		'contextualMenuDecoratorModule',
		'pageSensitiveDirectiveModule',
		'browserServiceModule',
		SystemModule
	],
	declarations: [HtmlDirective],
	config: ($provide: angular.auto.IProvideService, readObjectStructureFactory: () => (arg: Cloneable) => Cloneable, $logProvider: angular.ILogProvider) => {
		'ngInject';

		instrument($provide, readObjectStructureFactory(), 'smartedit');

		$logProvider.debugEnabled(false);
	},
	initialize: (
		systemEventService: SystemEventService,
		EVENTS: any,
		ID_ATTRIBUTE: string,
		OVERLAY_RERENDERED_EVENT: string,
		SMARTEDIT_DRAG_AND_DROP_EVENTS: IDragAndDropEvents,
		smartEditContractChangeListener: SmartEditContractChangeListener,
		crossFrameEventService: CrossFrameEventService,
		perspectiveService: IPerspectiveService,
		languageService: LanguageService,
		restServiceFactory: RestServiceFactory,
		gatewayFactory: GatewayFactory,
		renderService: any,
		decoratorService: any,
		featureService: IFeatureService,
		permissionService: IPermissionService,
		resizeComponentService: any,
		seNamespaceService: SeNamespaceService,
		experienceService: IExperienceService,
		httpErrorInterceptorService: any,
		retryInterceptor: any,
		unauthorizedErrorInterceptor: any,
		resourceNotFoundErrorInterceptor: any,
		lodash: lo.LoDashStatic,
		delegateRestService: DelegateRestService,
		pageInfoService: IPageInfoService,
		browserService: any,
		polyfillService: PolyfillService,
		testModeService: TestModeService,
		sharedDataService: ISharedDataService
	) => {
		'ngInject';
		gatewayFactory.initListener();

		httpErrorInterceptorService.addInterceptor(retryInterceptor);
		httpErrorInterceptorService.addInterceptor(unauthorizedErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(resourceNotFoundErrorInterceptor);

		smartEditContractChangeListener.onComponentsAdded((components: HTMLElement[], isEconomyMode: boolean) => {
			if (!isEconomyMode) {
				seNamespaceService.reprocessPage();
				resizeComponentService._resizeComponents(true);
				renderService._resizeSlots();
			}
			components.forEach((component) => renderService._createComponent(component));
			systemEventService.publishAsync(OVERLAY_RERENDERED_EVENT, {addedComponents: components});
		});

		smartEditContractChangeListener.onComponentsRemoved((components: {component: HTMLElement, parent: HTMLElement}[], isEconomyMode: boolean) => {
			if (!isEconomyMode) {
				seNamespaceService.reprocessPage();
				renderService._resizeSlots();
			}
			components.forEach((entry) => renderService._destroyComponent(entry.component, entry.parent));
			systemEventService.publishAsync(OVERLAY_RERENDERED_EVENT, {removedComponents: lodash.map(components, 'component')});
		});

		smartEditContractChangeListener.onComponentResized((component: HTMLElement) => {
			seNamespaceService.reprocessPage();
			renderService._resizeSlots();
			renderService._updateComponentSizeAndPosition(component);
		});

		smartEditContractChangeListener.onComponentRepositioned((component: HTMLElement) => {
			renderService._updateComponentSizeAndPosition(component);
		});

		smartEditContractChangeListener.onComponentChanged((component: any, oldAttributes: TypedMap<string>) => {
			seNamespaceService.reprocessPage();
			renderService._resizeSlots();

			renderService._destroyComponent(component, component.parent, oldAttributes);
			renderService._createComponent(component);
		});

		smartEditContractChangeListener.onPageChanged((pageUUID: string) => {
			pageInfoService.getCatalogVersionUUIDFromPage().then((catalogVersionUUID: string) => {
				pageInfoService.getPageUID().then((pageUID: string) => {
					experienceService.updateExperiencePageContext(catalogVersionUUID, pageUID);
				});
			});
		});

		if (polyfillService.isEligibleForEconomyMode()) {
			systemEventService.subscribe(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, function() {
				smartEditContractChangeListener.setEconomyMode(true);
			});

			systemEventService.subscribe(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, function() {
				seNamespaceService.reprocessPage();
				resizeComponentService._resizeComponents(true);
				renderService._resizeSlots();
				smartEditContractChangeListener.setEconomyMode(false);
			});
		}

		crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
			perspectiveService.refreshPerspective();
			languageService.registerSwitchLanguage();
		});

		smartEditContractChangeListener.initListener();

		// Feature registration
		featureService.register({
			key: 'se.emptySlotFix',
			nameI18nKey: 'se.emptyslotfix',
			enablingCallback: () => {
				resizeComponentService._resizeComponents(true);
			},
			disablingCallback: () => {
				resizeComponentService._resizeComponents(false);
			}
		});

		featureService.addDecorator({
			key: 'se.contextualMenu',
			nameI18nKey: 'contextualMenu'
		});

		featureService.addDecorator({
			key: 'se.slotContextualMenu',
			nameI18nKey: 'se.slot.contextual.menu'
		});

	}
})
export class Smartedit {}