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
import {doImport as doImport1} from './forcedImports';
doImport1();

import {IFeatureService, SeModule} from 'smarteditcommons';
import {personalizationsmarteditManageCustomizationViewModule} from "personalizationsmarteditcontainer/management/manageCustomizationView/manageCustomizationViewModule";
import {
	PersonalizationsmarteditCommonsModule,
	PersonalizationsmarteditContextUtils,
	PersonalizationsmarteditDateUtils
} from "personalizationcommons";
import {PersonalizationsmarteditManagementModule} from "personalizationsmarteditcontainer/management/PersonalizationsmarteditManagementModule";
import {PersonalizationsmarteditServicesModule} from "personalizationsmarteditcontainer";
import {PersonalizationsmarteditContextService} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter";
import {PersonalizationsmarteditContextServiceReverseProxy} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterReverseProxy";
import {VersioningModule} from "personalizationsmarteditcontainer/versioning/VersioningModule";
import {VersionCheckerService} from "personalizationsmarteditcontainer/versioning/VersionCheckerService";
import {PersonalizationsmarteditRestService} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService";

@SeModule({
	imports: [
		'personalizationsmarteditcontainerTemplates',
		'ui.bootstrap',
		'personalizationsmarteditCommons',
		'functionsModule',
		'personalizationsmarteditPreviewServiceModule',
		'personalizationsmarteditManagerViewModule',
		'personalizationsmarteditContextMenu',
		'featureServiceModule',
		'perspectiveServiceModule',
		'personalizationsmarteditCombinedViewModule',
		'personalizationsmarteditSegmentViewModule',
		'personalizationsmarteditToolbarContextModule',
		'seConstantsModule',
		'personalizationsmarteditRulesAndPermissionsRegistrationModule',
		'yjqueryModule',
		'smarteditCommonsModule',
		'personalizationsmarteditCustomizeViewModule',
		'smarteditRootModule',
		'experienceServiceModule',
		personalizationsmarteditManageCustomizationViewModule.name,
		PersonalizationsmarteditCommonsModule,
		PersonalizationsmarteditManagementModule,
		PersonalizationsmarteditServicesModule,
		VersioningModule
	],
	config: ($provide: angular.auto.IProvideService) => {
		'ngInject';

		$provide.decorator('rollbackPageVersionService', (
			$delegate: any,
			$log: angular.ILogService,
			versionCheckerService: VersionCheckerService
		) => {

			const rollbackCallback = $delegate.rollbackPageVersion;
			function rollbackWrapper() {
				versionCheckerService.setVersion(arguments[0]);
				return rollbackCallback.apply($delegate, arguments);
			}
			$delegate.rollbackPageVersion = rollbackWrapper;

			const modalCallback = $delegate.showConfirmationModal;
			function modalWrapper() {
				const targetArguments = arguments;

				return versionCheckerService.provideTranlationKey(targetArguments[1]).then(
					(text: string) => {
						targetArguments[1] = text;
						return modalCallback.apply($delegate, targetArguments);
					});
			}
			$delegate.showConfirmationModal = modalWrapper;

			return $delegate;
		});

	},
	initialize: (
		yjQuery: any,
		domain: any,
		$q: angular.IQService,
		personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy,
		personalizationsmarteditContextService: PersonalizationsmarteditContextService, // dont remove
		personalizationsmarteditContextModal: any, // dont remove
		personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
		personalizationsmarteditPreviewService: any,
		personalizationsmarteditMessageHandler: any,
		personalizationsmarteditRestService: PersonalizationsmarteditRestService,
		personalizationsmarteditUtils: any,
		EVENTS: any,
		SWITCH_LANGUAGE_EVENT: any,
		EVENT_PERSPECTIVE_UNLOADING: any,
		EVENT_PERSPECTIVE_ADDED: any,
		crossFrameEventService: any,
		featureService: IFeatureService,
		perspectiveService: any,
		smarteditBootstrapGateway: any,
		systemEventService: any,
		experienceService: any,
		personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils
	) => {
		'ngInject';

		const PERSONALIZATION_PERSPECTIVE_KEY: string = 'personalizationsmartedit.perspective';

		const loadCSS = (href: string) => {
			const cssLink = yjQuery("<link rel='stylesheet' type='text/css' href='" + href + "'>");
			yjQuery("head").append(cssLink);
		};
		loadCSS(domain + "/personalizationsmartedit/css/style.css");

		featureService.addToolbarItem({
			toolbarId: 'smartEditPerspectiveToolbar',
			key: 'personalizationsmartedit.container.pagecustomizations.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.pagecustomizations',
			priority: 4,
			section: 'left',
			include: 'personalizationsmarteditCustomizeViewWrapperTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.edit.page']
		});
		featureService.addToolbarItem({
			toolbarId: 'smartEditPerspectiveToolbar',
			key: 'personalizationsmartedit.container.combinedview.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.combinedview.name',
			priority: 6,
			section: 'left',
			include: 'personalizationsmarteditCombinedViewMenuTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.read.page']
		});
		featureService.addToolbarItem({
			toolbarId: 'smartEditPerspectiveToolbar',
			key: 'personalizationsmartedit.container.manager.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.library.name',
			priority: 8,
			section: 'left',
			include: 'manageCustomizationViewMenuTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.edit.page']
		});
		featureService.register({
			key: 'personalizationsmartedit.context.service',
			nameI18nKey: 'personalization.context.service.name',
			descriptionI18nKey: 'personalization.context.service.description',
			enablingCallback: () => {
				const personalization = personalizationsmarteditContextService.getPersonalization();
				personalization.enabled = true;
				personalizationsmarteditContextService.setPersonalization(personalization);
			},
			disablingCallback: () => {
				const personalization = personalizationsmarteditContextService.getPersonalization();
				personalization.enabled = false;
				personalizationsmarteditContextService.setPersonalization(personalization);
			},
			permissions: ['se.edit.page']
		});

		perspectiveService.register({
			key: PERSONALIZATION_PERSPECTIVE_KEY,
			nameI18nKey: 'personalization.perspective.name',
			features: ['personalizationsmartedit.context.service',
				'personalizationsmartedit.container.pagecustomizations.toolbar',
				'personalizationsmartedit.container.manager.toolbar',
				'personalizationsmartedit.container.combinedview.toolbar',
				'personalizationsmarteditSharedSlot',
				'personalizationsmarteditComponentLightUp',
				'personalizationsmarteditCombinedViewComponentLightUp',
				'personalizationsmartedit.context.add.action',
				'personalizationsmartedit.context.edit.action',
				'personalizationsmartedit.context.delete.action',
				'personalizationsmartedit.context.info.action',
				'personalizationsmartedit.context.component.edit.action',
				'personalizationsmartedit.context.show.action.list',
				'se.contextualMenu',
				'se.emptySlotFix',
				'externalcomponentbutton',
				'personalizationsmarteditExternalComponentDecorator'
			],
			perspectives: [],
			permissions: ['se.personalization.open']
		});

		const clearAllContextsAndReloadPreview = () => {
			personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
		};

		crossFrameEventService.subscribe(EVENT_PERSPECTIVE_UNLOADING, function(eventId: any, unloadingPerspective: string) {
			if (unloadingPerspective === PERSONALIZATION_PERSPECTIVE_KEY) {
				clearAllContextsAndReloadPreview();
			}
		});

		const clearAllContexts = () => {
			personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCombinedViewContext(personalizationsmarteditContextService);
		};

		systemEventService.registerEventHandler(EVENTS.EXPERIENCE_UPDATE, () => {
			clearAllContexts();
			return $q.when();
		});

		systemEventService.registerEventHandler(EVENT_PERSPECTIVE_ADDED, () => {
			personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
			return $q.when();
		});

		systemEventService.registerEventHandler(SWITCH_LANGUAGE_EVENT, () => {
			const combinedView = personalizationsmarteditContextService.getCombinedView();
			angular.forEach(combinedView.selectedItems, function(item: any) {
				personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(item.variation);
			});
			personalizationsmarteditContextService.setCombinedView(combinedView);
			return $q.when();
		});

		smarteditBootstrapGateway.subscribe("smartEditReady", (eventId: any, data: any) => {

			const customize = personalizationsmarteditContextService.getCustomize().selectedCustomization;
			const combinedView = personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization;
			const combinedViewCustomize = personalizationsmarteditContextService.getCombinedView().selectedItems;
			experienceService.getCurrentExperience().then((experience: any) => {
				if (!experience.variations && (customize || combinedView || combinedViewCustomize)) {
					clearAllContexts();
				}
			});

			personalizationsmarteditContextService.refreshExperienceData().then(() => {
				const experience = personalizationsmarteditContextService.getSeData().seExperienceData;
				const activePerspective = perspectiveService.getActivePerspective() || {};
				if (activePerspective.key === PERSONALIZATION_PERSPECTIVE_KEY && experience.pageContext.catalogVersionUuid !== experience.catalogDescriptor.catalogVersionUuid) {
					const warningConf = {
						message: 'personalization.warning.pagefromparent',
						timeout: -1
					};
					personalizationsmarteditMessageHandler.sendWarning(warningConf);
				}
			}).finally(() => {
				personalizationsmarteditContextService.applySynchronization();
			});
		});

	}
})
export class Personalizationsmarteditcontainermodule {}
