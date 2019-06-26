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
import {doImport as doImport1} from './forcedImports';
doImport1();
import {doImport as doImport2} from 'cmssmartedit/services/deprecatedSince-6.7';
doImport2();

import * as angular from 'angular';
import {IContextualMenuButton, IContextualMenuConfiguration, IFeatureService, SeModule, TypedMap} from 'smarteditcommons';
import {ComponentHandlerService, ContextualMenuService} from 'smartedit';
import {AssetsService} from 'cmscommons';
import {CmsSmarteditServicesModule} from 'cmssmartedit/services';
import {TypePermissionsRestService} from 'cmscommons/services/TypePermissionsRestService';

@SeModule({
	imports: [
		CmsSmarteditServicesModule,
		'cmsResourceLocationsModule',
		'resourceLocationsModule',
		'decoratorServiceModule',
		'removeComponentServiceModule',
		'experienceInterceptorModule',
		'editorEnablerServiceModule',
		'alertServiceModule',
		'translationServiceModule',
		'slotVisibilityButtonModule',
		'slotVisibilityServiceModule',
		'cmssmarteditTemplates',
		'cmscommonsTemplates',
		'smarteditServicesModule',
		'slotSharedButtonModule',
		'cmsDragAndDropServiceModule',
		'syncIndicatorDecoratorModule',
		'slotSyncButtonModule',
		'confirmationModalServiceModule',
		'sharedSlotDisabledDecoratorModule',
		'externalSlotDisabledDecoratorModule',
		'slotRestrictionsServiceModule',
		'slotSharedServiceModule',
		'contextualMenuDropdownServiceModule',
		'externalComponentDecoratorModule',
		'externalComponentButtonModule',
		'componentEditingFacadeModule',
		'cmsitemsRestServiceModule',
		'slotUnsharedButtonModule',
		'componentInfoServiceModule'
	],
	initialize: (
		$rootScope: angular.IRootScopeService,
		$q: angular.IQService,
		$translate: angular.translate.ITranslateService,
		alertService: any,
		assetsService: AssetsService,
		cmsDragAndDropService: any,
		componentHandlerService: ComponentHandlerService,
		confirmationModalService: any,
		contextualMenuService: ContextualMenuService,
		decoratorService: any,
		editorEnablerService: any,
		featureService: IFeatureService,
		removeComponentService: any,
		slotRestrictionsService: any,
		slotSharedService: any,
		slotVisibilityService: any,
		componentEditingFacade: any,
		cmsitemsRestService: any,
		componentInfoService: any,
		typePermissionsRestService: TypePermissionsRestService) => {
		'ngInject';
		editorEnablerService.enableForComponents(['^((?!Slot).)*$']);

		decoratorService.addMappings({
			'^((?!Slot).)*$': ['se.contextualMenu', 'externalComponentDecorator'],
			'^.*Slot$': ['se.slotContextualMenu', 'se.basicSlotContextualMenu', 'syncIndicator', 'sharedSlotDisabledDecorator', 'externalSlotDisabledDecorator']
		});


		featureService.addContextualMenuButton({
			key: 'externalcomponentbutton',
			nameI18nKey: 'se.cms.contextmenu.title.externalcomponent',
			i18nKey: 'se.cms.contextmenu.title.externalcomponentbutton',
			regexpKeys: ['^((?!Slot).)*$'],
			condition: (configuration: IContextualMenuConfiguration) => {
				const slotId: string = componentHandlerService.getParentSlotForComponent(configuration.element);

				return slotRestrictionsService.isSlotEditable(slotId).then((isSlotEditable: boolean) => {
					if (!isSlotEditable) {
						return false;
					}

					const smarteditCatalogVersionUuid = configuration.componentAttributes && configuration.componentAttributes.smarteditCatalogVersionUuid;
					if (smarteditCatalogVersionUuid) {
						return componentHandlerService.getCatalogVersionUUIDFromPage().then((uuid: string) => {
							return smarteditCatalogVersionUuid !== uuid;
						});
					}

					return componentHandlerService.isExternalComponent(configuration.componentId, configuration.componentType);
				});
			},
			action: {
				template: '<external-component-button data-catalog-version-uuid="ctrl.componentAttributes.smarteditCatalogVersionUuid"></external-component-button>'
			},
			displayClass: 'externalcomponentbutton',
			displayIconClass: 'hyicon hyicon-globe',
			displaySmallIconClass: 'hyicon hyicon-globe'
		} as IContextualMenuButton);

		featureService.addContextualMenuButton({
			key: 'se.cms.dragandropbutton',
			nameI18nKey: 'se.cms.contextmenu.title.dragndrop',
			i18nKey: 'se.cms.contextmenu.title.dragndrop',
			regexpKeys: ['^((?!Slot).)*$'],
			condition: (configuration: IContextualMenuConfiguration) => {
				const slotId = componentHandlerService.getParentSlotForComponent(configuration.element);
				return slotRestrictionsService.isSlotEditable(slotId).then(function(slotEditable: boolean) {
					if (slotEditable) {
						return typePermissionsRestService.hasUpdatePermissionForTypes([configuration.componentType]).then(function(hasUpdatePermission: TypedMap<boolean>) {
							return hasUpdatePermission[configuration.componentType];
						});
					}
					return false;
				});
			},
			action: {
				callbacks: {
					mousedown: () => {
						cmsDragAndDropService.update();
					}
				}
			},
			displayClass: 'movebutton',
			displayIconClass: 'hyicon hyicon-dragdroplg',
			displaySmallIconClass: 'hyicon hyicon-dragdroplg',
			permissions: ['se.context.menu.drag.and.drop.component']
		});

		featureService.register({
			key: 'se.cms.html5DragAndDrop',
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

		featureService.addContextualMenuButton({
			key: 'se.cms.remove',
			i18nKey: 'se.cms.contextmenu.title.remove',
			nameI18nKey: 'se.cms.contextmenu.title.remove',
			regexpKeys: ['^((?!Slot).)*$'],
			condition: (configuration: IContextualMenuConfiguration) => {
				if (!configuration.isComponentHidden) {
					const slotId: string = componentHandlerService.getParentSlotForComponent(configuration.element);
					return slotRestrictionsService.isSlotEditable(slotId).then(function(slotEditable: boolean) {
						if (slotEditable) {
							return typePermissionsRestService.hasDeletePermissionForTypes([configuration.componentType]).then(function(hasDeletePermission: TypedMap<boolean>) {
								return hasDeletePermission[configuration.componentType];
							});
						}
						return false;
					});
				}

				return typePermissionsRestService.hasDeletePermissionForTypes([configuration.componentType]).then(function(hasDeletePermission: TypedMap<boolean>) {
					return hasDeletePermission[configuration.componentType];
				});
			},
			action: {
				callback: (configuration: IContextualMenuConfiguration, $event: Event) => {

					let slotOperationRelatedId: string;
					let slotOperationRelatedType: string;

					if (configuration.element) {
						slotOperationRelatedId = componentHandlerService.getSlotOperationRelatedId(configuration.element);
						slotOperationRelatedType = componentHandlerService.getSlotOperationRelatedType(configuration.element);
					} else {
						slotOperationRelatedId = (configuration.containerId) ? configuration.containerId : configuration.componentId;
						slotOperationRelatedType = (configuration.containerId && configuration.containerType) ? configuration.containerType : configuration.componentType;
					}

					const message: any = {};
					message.description = "se.cms.contextmenu.removecomponent.confirmation.message";
					message.title = "se.cms.contextmenu.removecomponent.confirmation.title";

					confirmationModalService.confirm(message).then(function() {
						removeComponentService.removeComponent({
							slotId: configuration.slotId,
							componentId: configuration.componentId,
							componentType: configuration.componentType,
							slotOperationRelatedId,
							slotOperationRelatedType
						}).then(
							function() {
								slotVisibilityService.reloadSlotsInfo();
								$translate('se.cms.alert.component.removed.from.slot', {
									componentID: slotOperationRelatedId,
									slotID: configuration.slotId
								}).then(function(translation: string) {
									alertService.showSuccess({
										message: translation
									});
									$event.preventDefault();
									$event.stopPropagation();
								});
							});
					});
				}
			},
			displayClass: 'removebutton',
			displayIconClass: 'hyicon hyicon-removelg',
			displaySmallIconClass: 'hyicon hyicon-removelg',
			permissions: ['se.context.menu.remove.component']
		});

		featureService.addContextualMenuButton({
			key: 'se.slotContextualMenuVisibility',
			nameI18nKey: 'slotcontextmenu.title.visibility',
			regexpKeys: ['^.*ContentSlot$'],
			action: {templateUrl: 'slotVisibilityWidgetTemplate.html'},
			permissions: ['se.slot.context.menu.visibility']
		});

		featureService.addContextualMenuButton({
			key: 'se.slotSharedButton',
			nameI18nKey: 'slotcontextmenu.title.shared.button',
			regexpKeys: ['^.*Slot$'],
			action: {templateUrl: 'slotSharedTemplate.html'},
			permissions: ['se.slot.context.menu.shared.icon']
		});

		featureService.addContextualMenuButton({
			key: 'slotUnsharedButton',
			nameI18nKey: 'slotcontextmenu.title.unshared.button',
			regexpKeys: ['^.*Slot$'],
			action: {templateUrl: 'slotUnsharedButtonWrapperTemplate.html'},
			permissions: ['se.slot.context.menu.unshared.icon']
		});

		featureService.addContextualMenuButton({
			key: 'se.slotSyncButton',
			nameI18nKey: 'slotcontextmenu.title.sync.button',
			regexpKeys: ['^.*Slot$'],
			action: {templateUrl: 'slotSyncTemplate.html'},
			permissions: ['se.sync.slot.context.menu']
		});

		featureService.addDecorator({
			key: 'syncIndicator',
			nameI18nKey: 'syncIndicator',
			permissions: ['se.sync.slot.indicator']
		});

		featureService.register({
			key: 'disableSharedSlotEditing',
			nameI18nKey: 'se.cms.disableSharedSlotEditing',
			descriptionI18nKey: 'se.cms.disableSharedSlotEditing.description',
			enablingCallback: () => {
				slotSharedService.setSharedSlotEnablementStatus(true);
			},
			disablingCallback: () => {
				slotSharedService.setSharedSlotEnablementStatus(false);
			}
		});

		featureService.addDecorator({
			key: 'sharedSlotDisabledDecorator',
			nameI18nKey: 'se.cms.shared.slot.disabled.decorator',
			// only show that the slot is shared if it is not already external
			displayCondition: (componentType: string, componentId: string) => {
				return $q.all([slotRestrictionsService.isSlotEditable(componentId), componentHandlerService.isExternalComponent(componentId, componentType)]).then(function(response: boolean[]) {
					return !response[0] && !response[1];
				});
			}
		});

		featureService.addDecorator({
			key: 'externalSlotDisabledDecorator',
			nameI18nKey: 'se.cms.external.slot.disabled.decorator',
			displayCondition: (componentType: string, componentId: string) => {
				return $q.when(componentHandlerService.isExternalComponent(componentId, componentType));
			}
		});

		featureService.addDecorator({
			key: 'externalComponentDecorator',
			nameI18nKey: 'se.cms.external.component.decorator',
			displayCondition: (componentType: string, componentId: string) => {
				return $q.when(componentHandlerService.isExternalComponent(componentId, componentType));
			}
		});

		featureService.addContextualMenuButton({
			key: 'clonecomponentbutton',
			nameI18nKey: 'se.cms.contextmenu.title.clone.component',
			i18nKey: 'se.cms.contextmenu.title.clone.component',
			regexpKeys: ['^((?!Slot).)*$'],
			condition: (configuration: IContextualMenuConfiguration) => {
				const componentUuid = configuration.componentAttributes.smarteditComponentUuid;
				if (!configuration.isComponentHidden) {
					const slotId: string = componentHandlerService.getParentSlotForComponent(configuration.element);
					return slotRestrictionsService.isSlotEditable(slotId).then(function(slotEditable: boolean) {
						if (slotEditable) {
							return typePermissionsRestService.hasCreatePermissionForTypes([configuration.componentType]).then(function(hasCreatePermission: TypedMap<boolean>) {
								if (hasCreatePermission[configuration.componentType]) {
									return componentInfoService.getById(componentUuid).then((component: any) => {
										return component.cloneable;
									});
								} else {
									return $q.when(false);
								}
							});
						}
						return false;
					});
				}
				return cmsitemsRestService.getById(componentUuid).then((component: any) => {
					return component.cloneable;
				});
			},
			action: {
				callback: (configuration: IContextualMenuConfiguration) => {
					const sourcePosition = componentHandlerService.getComponentPositionInSlot(configuration.slotId, configuration.componentAttributes.smarteditComponentId);
					componentEditingFacade.cloneExistingComponentToSlot({
						targetSlotId: configuration.slotId,
						dragInfo: {
							componentId: configuration.componentAttributes.smarteditComponentId,
							componentType: configuration.componentType,
							componentUuid: configuration.componentAttributes.smarteditComponentUuid
						},
						position: sourcePosition + 1
					});
				}
			},
			displayClass: 'clonebutton',
			displayIconClass: 'hyicon hyicon-clone',
			displaySmallIconClass: 'hyicon hyicon-clone',
			permissions: ['se.clone.component']
		});
	}
})
export class Cmssmartedit {}