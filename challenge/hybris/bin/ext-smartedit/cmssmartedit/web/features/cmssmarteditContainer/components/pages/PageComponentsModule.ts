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
import {SeModule} from 'smarteditcommons';
import {PermanentlyDeletePageItemComponent, RestorePageItemComponent} from './pageItems';
import {PageInfoMenuComponent} from './pageInfoMenu/PageInfoMenuComponent';
import {PageInfoMenuService} from './pageInfoMenu/services/PageInfoMenuService';
import {HomepageIconComponent} from "./homepageIcon/HomepageIconComponent";
import {ClonePageItemComponent} from "./pageItems/clonePageItem/ClonePageItemComponent";
import {DeletePageItemComponent} from "./pageItems/deletePageItem/DeletePageItemComponent";
import {EditPageItemComponent} from "./pageItems/editPageItem/EditPageItemComponent";
import {SyncPageItemComponent} from "./pageItems/syncPageItem/SyncPageItemComponent";
import {PageEditorModalService} from "./editPageModal/pageEditorModalService";
import {RestrictionsPageListIconComponent} from "./restrictionsPageListIcon/RestrictionsPageListIconComponent";

/**
 * @ngdoc overview
 * @name pageComponentsModule
 *
 * @description
 * Module containing all the components and services necessary to manage a page. 
 */
@SeModule({
	imports: [
		'typeStructureRestServiceModule',
		'pageServiceModule',
		'seConstantsModule',
		'cmsSmarteditServicesModule',
		'smarteditServicesModule',
		'clonePageWizardServiceModule',
		'syncPageModalServiceModule',
		'genericEditorModalServiceModule',
		'contextAwarePageStructureServiceModule',
		'cmsitemsRestServiceModule',
		'cmssmarteditContainerTemplates',
		'translationServiceModule'
	],
	declarations: [
		PageInfoMenuComponent,
		PermanentlyDeletePageItemComponent,
		RestorePageItemComponent,
		HomepageIconComponent,
		ClonePageItemComponent,
		DeletePageItemComponent,
		EditPageItemComponent,
		SyncPageItemComponent,
		RestrictionsPageListIconComponent
	],
	providers: [
		PageInfoMenuService,
		PageEditorModalService
	]
})
export class PageComponentsModule {}
