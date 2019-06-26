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

// Bundling app as legacy script

import {doImport as forImports} from './forcedImports';
forImports();
import {deprecate} from "./deprecate";
deprecate();

import {IFeatureService, IPerspectiveService, SeModule, SystemEventService} from 'smarteditcommons';

import {AssetsService, CMSModesService} from 'cmscommons';
import {VersionExperienceInterceptorModule} from 'cmscommons/services/interceptors/versionExperienceInterceptor/VersionExperienceInterceptorModule';
import {trashedPageListControllerModule, TrashedPageListController} from 'cmssmarteditcontainer/components/pages/trashedPageList/TrashedPageListController';
import {CmsSmarteditComponentsModule} from 'cmssmarteditcontainer/components/cmsSmarteditComponentsModule';
import {CmsSmarteditServicesModule} from 'cmssmarteditcontainer/services/cmsSmarteditServicesModule';
import {NavigationManagementPageController} from "cmssmarteditcontainer/components/navigation/NavigationManagementPageController";
import {ManagePageVersionService} from "./components/versioning/services/ManagePageVersionService";
import {RollbackPageVersionService} from "./components/versioning/services/RollbackPageVersionService";


@SeModule({
	imports: [
		CmsSmarteditServicesModule,
		CmsSmarteditComponentsModule,
		VersionExperienceInterceptorModule,
		'cmsResourceLocationsModule',
		'experienceInterceptorModule',
		'resourceLocationsModule',
		'cmssmarteditContainerTemplates',
		'componentMenuModule',
		'cmscommonsTemplates',
		'editorModalServiceModule',
		'genericEditorModule',
		'catalogDetailsModule',
		'synchronizeCatalogModule',
		'pageListLinkModule',
		'pageListControllerModule',
		'slotRestrictionsServiceModule',
		'cmsDragAndDropServiceModule',
		'seMediaFieldModule',
		'seMediaContainerFieldModule',
		'pageRestrictionsModule',
		'restrictionsEditorModule',
		'yActionableSearchItemModule',
		'seNavigationNodeSelector',
		'pageSyncMenuToolbarItemModule',
		'productSelectorModule',
		'categorySelectorModule',
		'clonePageWizardServiceModule',
		'cmsLinkToSelectModule',
		'rulesAndPermissionsRegistrationModule',
		'smarteditServicesModule',
		'singleActiveCatalogAwareItemSelectorModule',
		'productCatalogDropdownPopulatorModule',
		'productDropdownPopulatorModule',
		'categoryDropdownPopulatorModule',
		'cmsItemDropdownModule',
		'catalogAwareRouteResolverModule',
		'catalogVersionPermissionModule',
		'componentRestrictionsEditorModule',
		'pageRestrictionsEditorModule',
		'pageVersionsModule',
		'displayConditionsEditorModule',
		'linkToggleModule',
		'functionsModule',
		'componentVisibilityAlertServiceModule',
		'cmsGenericEditorConfigurationServiceModule',
		'deletePageToolbarItemModule',
		trashedPageListControllerModule.name
	],
	config: (PAGE_LIST_PATH: string, TRASHED_PAGE_LIST_PATH: string, NAVIGATION_MANAGEMENT_PAGE_PATH: string, $routeProvider: ng.route.IRouteProvider, catalogAwareRouteResolverFunctions: any) => {
		'ngInject';
		$routeProvider.when(PAGE_LIST_PATH, {
			templateUrl: 'pageListTemplate.html',
			controller: 'pageListController',
			controllerAs: 'pageListCtl',
			resolve: {
				experienceFromPathResolve: catalogAwareRouteResolverFunctions.experienceFromPathResolve
			}
		});
		$routeProvider.when(TRASHED_PAGE_LIST_PATH, {
			templateUrl: 'trashedpageListTemplate.html',
			controller: TrashedPageListController,
			controllerAs: 'trashedPageListCtl',
			resolve: {
				experienceFromPathResolve: catalogAwareRouteResolverFunctions.experienceFromPathResolve
			}
		});
		$routeProvider.when(NAVIGATION_MANAGEMENT_PAGE_PATH, {
			templateUrl: 'navigationManagementPageTemplate.html',
			controller: NavigationManagementPageController,
			controllerAs: 'nav',
			resolve: {
				experienceFromPathResolve: catalogAwareRouteResolverFunctions.experienceFromPathResolve
			}
		});
	},
	initialize:
		/* jshint -W098*/
		/*need to inject for gatewayProxy initialization of componentVisibilityAlertService*/
		($log: angular.ILogService,
			$rootScope: angular.IRootScopeService,
			$routeParams: any,
			NAVIGATION_MANAGEMENT_PAGE_PATH: string,
			ComponentService: any,
			systemEventService: SystemEventService,
			catalogDetailsService: any,
			featureService: IFeatureService,
			perspectiveService: IPerspectiveService,
			assetsService: AssetsService,
			editorFieldMappingService: any,
			genericEditorTabService: any,
			cmsDragAndDropService: any,
			editorModalService: any,
			clonePageWizardService: any,
			CATALOG_DETAILS_COLUMNS: any,
			componentVisibilityAlertService: any,
			cmsGenericEditorConfigurationService: any,
			managePageVersionService: ManagePageVersionService,
			rollbackPageVersionService: RollbackPageVersionService) => {
			'ngInject';
			// Configure generic editor 
			cmsGenericEditorConfigurationService.setDefaultEditorFieldMappings();
			cmsGenericEditorConfigurationService.setDefaultTabFieldMappings();
			cmsGenericEditorConfigurationService.setDefaultTabsConfiguration();

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.componentMenuTemplate',
				type: 'HYBRID_ACTION',
				nameI18nKey: 'se.cms.componentmenu.btn.label.addcomponent',
				descriptionI18nKey: 'cms.toolbaritem.componentmenutemplate.description',
				priority: 100,
				section: 'left',
				iconClassName: 'hyicon hyicon-addlg se-toolbar-menu-ddlb--button__icon',
				callback: () => {
					systemEventService.publish('ySEComponentMenuOpen', {});
				},
				include: 'componentMenuWrapperTemplate.html',
				permissions: ['se.add.component'],
				keepAliveOnClose: true
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.pageInfoMenu',
				type: 'TEMPLATE',
				nameI18nKey: 'se.cms.pageinfo.menu.btn.label',
				priority: 120,
				section: 'left',
				include: 'pageInfoMenuWrapperTemplate.html',
				permissions: ['se.read.page']
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.clonePageMenu',
				type: 'ACTION',
				nameI18nKey: 'se.cms.clonepage.menu.btn.label',
				iconClassName: 'hyicon hyicon-clone se-toolbar-menu-ddlb--button__icon',
				callback: () => {
					clonePageWizardService.openClonePageWizard();
				},
				priority: 130,
				section: 'left',
				permissions: ['se.clone.page']
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.pageSyncMenu',
				nameI18nKey: 'se.cms.toolbaritem.pagesyncmenu.name',
				type: 'TEMPLATE',
				include: 'pageSyncMenuToolbarItemWrapperTemplate.html',
				priority: 140,
				section: 'left',
				permissions: ['se.sync.page']
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'deletePageMenu',
				nameI18nKey: 'se.cms.actionitem.page.trash',
				type: 'TEMPLATE',
				include: 'deletePageToolbarItemWrapperTemplate.html',
				priority: 150,
				section: 'left',
				permissions: ['se.delete.page.menu']
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.pageVersionsMenu',
				type: 'HYBRID_ACTION',
				nameI18nKey: 'se.cms.actionitem.page.versions',
				priority: 102,
				section: 'left',
				iconClassName: 'hyicon hyicon-versions se-toolbar-menu-ddlb--button__icon',
				include: 'pageVersionsMenuWrapperTemplate.html',
				contextTemplateUrl: 'versionItemContextWrapperTemplate.html',
				permissions: ['se.version.page'],
				keepAliveOnClose: true
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.createVersionMenu',
				type: 'ACTION',
				nameI18nKey: 'se.cms.actionitem.page.versions.create',
				iconClassName: 'hyicon hyicon-addlg se-toolbar-menu-ddlb--button__icon',
				callback: () => {
					managePageVersionService.createPageVersion();
				},
				priority: 104,
				section: 'left',
				permissions: ['se.version.page', 'se.create.version.page']
			});

			featureService.addToolbarItem({
				toolbarId: 'smartEditPerspectiveToolbar',
				key: 'se.cms.rollbackVersionMenu',
				type: 'ACTION',
				nameI18nKey: 'se.cms.actionitem.page.versions.rollback',
				iconClassName: 'hyicon hyicon-rollback se-toolbar-menu-ddlb--button__icon',
				callback: () => {
					rollbackPageVersionService.rollbackPageVersion();
				},
				priority: 106,
				section: 'left',
				permissions: ['se.version.page', 'se.rollback.version.page']
			});

			catalogDetailsService.addItems([{
				include: 'pageListLinkTemplate.html'
			}, {
				include: 'navigationEditorLinkTemplate.html'
			}]);

			catalogDetailsService.addItems([{
				include: 'catalogDetailsSyncTemplate.html'
			}], CATALOG_DETAILS_COLUMNS.RIGHT);

			featureService.register({
				key: 'se.cms.html5DragAndDrop.outer',
				nameI18nKey: 'se.cms.dragAndDrop.name',
				descriptionI18nKey: 'se.cms.dragAndDrop.description',
				enablingCallback: () => {
					cmsDragAndDropService.register();
					cmsDragAndDropService.apply();
				},
				disablingCallback: () => {
					cmsDragAndDropService.unregister();
				}
			});

			perspectiveService.register({
				key: 'se.cms.perspective.basic',
				nameI18nKey: 'se.cms.perspective.basic.name',
				descriptionI18nKey: 'se.hotkey.tooltip',
				features: ['se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.clonePageMenu', 'se.cms.pageInfoMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'disableSharedSlotEditing', 'sharedSlotDisabledDecorator', 'se.cms.html5DragAndDrop.outer', 'externalComponentDecorator', 'externalcomponentbutton', 'externalSlotDisabledDecorator', 'clonecomponentbutton', 'deletePageMenu'],
				perspectives: []
			});

			/* Note: For advance edit mode, the ordering of the entries in the features list will determine the order the buttons will show in the slot contextual menu */
			perspectiveService.register({
				key: 'se.cms.perspective.advanced',
				nameI18nKey: 'se.cms.perspective.advanced.name',
				descriptionI18nKey: 'se.hotkey.tooltip',
				features: ['se.slotContextualMenu', 'se.slotSyncButton', 'se.slotSharedButton', 'se.slotContextualMenuVisibility', 'se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.clonePageMenu', 'se.cms.pageInfoMenu', 'se.cms.pageSyncMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'se.cms.html5DragAndDrop.outer', 'syncIndicator', 'externalSlotDisabledDecorator', 'externalComponentDecorator', 'externalcomponentbutton', 'clonecomponentbutton', 'slotUnsharedButton', 'deletePageMenu', 'se.cms.pageVersionsMenu'],
				perspectives: []
			});

			perspectiveService.register({
				key: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
				nameI18nKey: 'se.cms.perspective.versioning.name',
				descriptionI18nKey: 'se.cms.perspective.versioning.description',
				features: ['se.cms.pageVersionsMenu', 'se.cms.createVersionMenu', 'se.cms.rollbackVersionMenu', 'se.cms.restrictionsMenu', 'se.cms.pageInfoMenu', 'disableSharedSlotEditing', 'sharedSlotDisabledDecorator', 'externalSlotDisabledDecorator', 'externalComponentDecorator'],
				perspectives: [],
				permissions: ['se.version.page'],
				isHotkeyDisabled: true
			});

		}
})
export class CmssmarteditContainer {}
