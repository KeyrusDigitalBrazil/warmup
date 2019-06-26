import * as angular from 'angular';
import {doImport as doImport1} from './forcedImports';
doImport1();

import {IContextualMenuButton, IFeatureService, SeModule} from 'smarteditcommons';
import {BodyDirective} from './BodyDirective';
import {PersonalizationsmarteditCommonsModule} from 'personalizationcommons';
import {PersonalizationsmarteditServicesModule} from "personalizationsmartedit/service/PersonalizationsmarteditServicesModule";
import {PersonalizationsmarteditShowComponentInfoListModule} from "personalizationsmartedit/contextMenu/PersonalizationsmarteditShowComponentInfoListModule";
import {PersonalizationsmarteditComponentHandlerService} from "personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService";
import {PersonalizationsmarteditContextServiceProxy} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerProxy";
import {PersonalizationsmarteditShowActionListModule} from "personalizationsmartedit/contextMenu/PersonalizationsmarteditShowActionListModule";

@SeModule({
	imports: [
		PersonalizationsmarteditShowActionListModule,
		PersonalizationsmarteditCommonsModule,
		PersonalizationsmarteditServicesModule,
		PersonalizationsmarteditShowComponentInfoListModule,
		'personalizationsmarteditTemplates',
		'decoratorServiceModule',
		'personalizationsmarteditComponentLightUpDecorator',
		'personalizationsmarteditCombinedViewComponentLightUpDecorator',
		'personalizationsmarteditContextMenu',
		'personalizationsmarteditCommons',
		'personalizationsmarteditSharedSlotDecorator',
		'featureServiceModule',
		'yjqueryModule',
		'personalizationsmarteditContextualMenuServiceModule',
		'externalComponentDecoratorModule',
		'externalComponentButtonModule',
		'personalizationsmarteditCustomizeViewServiceModule',
		'personalizationsmarteditExternalComponentDecoratorModule'
	],
	declarations: [BodyDirective],
	initialize: (
		yjQuery: any,
		domain: any,
		$q: angular.IQService,
		personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
		personalizationsmarteditContextualMenuService: any,
		personalizationsmarteditContextServiceProxy: PersonalizationsmarteditContextServiceProxy, // dont remove
		personalizationsmarteditContextModalHelper: any,
		personalizationsmarteditCustomizeViewProxy: any,
		bodyDirective: BodyDirective,
		decoratorService: any,
		featureService: IFeatureService
	) => {
		'ngInject';

		const loadCSS = (href: string) => {
			const cssLink = yjQuery("<link rel='stylesheet' type='text/css' href='" + href + "'>");
			yjQuery("head").append(cssLink);
		};
		loadCSS(domain + "/personalizationsmartedit/css/style.css");

		decoratorService.addMappings({
			'^.*Slot$': ['personalizationsmarteditSharedSlot']
		});

		decoratorService.addMappings({
			'^.*Component$': ['personalizationsmarteditComponentLightUp', 'personalizationsmarteditCombinedViewComponentLightUp']
		});

		decoratorService.addMappings({
			'^((?!Slot).)*$': ['personalizationsmarteditExternalComponentDecorator']
		});

		featureService.addDecorator({
			key: 'personalizationsmarteditExternalComponentDecorator',
			nameI18nKey: 'personalizationsmarteditExternalComponentDecorator',
			displayCondition: (componentType: string, componentId: string) => {
				const component: JQuery = personalizationsmarteditComponentHandlerService.getOriginalComponent(componentId, componentType);
				const container: any = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(component);
				if (container.length > 0 && container[0].attributes["data-smartedit-personalization-action-id"]) {
					return $q.when(false);
				}
				return $q.when(personalizationsmarteditComponentHandlerService.isExternalComponent(componentId, componentType));
			}
		});

		featureService.addDecorator({
			key: 'personalizationsmarteditComponentLightUp',
			nameI18nKey: 'personalizationsmarteditComponentLightUp'
		});

		featureService.addDecorator({
			key: 'personalizationsmarteditCombinedViewComponentLightUp',
			nameI18nKey: 'personalizationsmarteditCombinedViewComponentLightUp'
		});

		featureService.addDecorator({
			key: 'personalizationsmarteditSharedSlot',
			nameI18nKey: 'personalizationsmarteditSharedSlot'
		});

		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.show.action.list",
			i18nKey: 'personalization.context.action.list.show',
			nameI18nKey: 'personalization.context.action.list.show',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuShowActionListEnabled(config);
			},
			action: {
				template: '<personalizationsmartedit-show-action-list data-component="componentDetails"></personalizationsmartedit-show-action-list>'
			},
			displayClass: "showactionlistbutton",
			displayIconClass: "hyicon hyicon-combinedview cmsx-ctx__icon personalization-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-combinedview cmsx-ctx__icon--small",
			permissions: ['se.read.page'],
			priority: 500
		} as IContextualMenuButton);

		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.info.action",
			i18nKey: 'personalization.context.action.info',
			nameI18nKey: 'personalization.context.action.info',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuInfoItemEnabled(config.element);
			},
			action: {
				template: '<personalizationsmartedit-show-component-info-list data-component="componentDetails"></personalizationsmartedit-show-component-info-list>'
			},
			displayClass: "infoactionbutton",
			displayIconClass: "hyicon hyicon-msginfo cmsx-ctx__icon personalization-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-msginfo cmsx-ctx__icon--small",
			permissions: ['se.edit.page'],
			priority: 510
		} as IContextualMenuButton);

		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.add.action",
			i18nKey: 'personalization.context.action.add',
			nameI18nKey: 'personalization.context.action.add',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuAddItemEnabled(config);
			},
			callback: (config: any, $event: any) => {
				personalizationsmarteditContextModalHelper.openAddAction(config);
			},
			displayClass: "addactionbutton",
			displayIconClass: "hyicon hyicon-addlg cmsx-ctx__icon personalization-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-addlg cmsx-ctx__icon--small",
			permissions: ['se.edit.page'],
			priority: 520
		} as IContextualMenuButton);

		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.component.edit.action",
			i18nKey: 'personalization.context.component.action.edit',
			nameI18nKey: 'personalization.context.component.action.edit',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuEditComponentItemEnabled(config);
			},
			callback: (config: any, $event: any) => {
				personalizationsmarteditContextModalHelper.openEditComponentAction(config);
			},
			displayClass: "editbutton",
			displayIconClass: "hyicon hyicon-edit cmsx-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-edit cmsx-ctx__icon--small",
			permissions: ['se.edit.page'],
			priority: 530
		} as IContextualMenuButton);

		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.edit.action",
			i18nKey: 'personalization.context.action.edit',
			nameI18nKey: 'personalization.context.action.edit',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuEditItemEnabled(config);
			},
			callback: (config: any, $event: any) => {
				personalizationsmarteditContextModalHelper.openEditAction(config);
			},
			displayClass: "replaceactionbutton",
			displayIconClass: "hyicon hyicon-change cmsx-ctx__icon personalization-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-change cmsx-ctx__icon--small",
			permissions: ['se.edit.page'],
			priority: 540
		} as IContextualMenuButton);


		featureService.addContextualMenuButton({
			key: "personalizationsmartedit.context.delete.action",
			i18nKey: 'personalization.context.action.delete',
			nameI18nKey: 'personalization.context.action.delete',
			regexpKeys: ['^.*Component$'],
			condition: (config: any) => {
				return personalizationsmarteditContextualMenuService.isContextualMenuDeleteItemEnabled(config);
			},
			callback: (config: any, $event: any) => {
				personalizationsmarteditContextModalHelper.openDeleteAction(config);
			},
			displayClass: "removeactionbutton",
			displayIconClass: "hyicon hyicon-removelg cmsx-ctx__icon personalization-ctx__icon",
			displaySmallIconClass: "hyicon hyicon-removelg cmsx-ctx__icon--small",
			permissions: ['se.edit.page'],
			priority: 550
		} as IContextualMenuButton);

	}
})
export class Personalizationsmarteditmodule {}
