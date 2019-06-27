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
import {FunctionsModule, SeModule, TranslationServiceModule} from 'smarteditcommons';
import {GeneralConfigurationComponent} from "smarteditcontainer/components/generalConfiguration/GeneralConfigurationComponent";
import {GenericEditorModule} from "smarteditcommons/components/genericEditor/GenericEditorModule";

/**
 * @ngdoc overview
 * @name administration
 *
 * @description
 * # The administration module
 *
 * The administration module provides services to display and manage configurations
 * that point to web service and the value property contains the URI of the web service or data.
 *
 */
/** @internal */
@SeModule({
	imports: [
		FunctionsModule,
		TranslationServiceModule,
		'ngResource',
		'loadConfigModule',
		'modalServiceModule',
		'confirmationModalServiceModule',
		GenericEditorModule,
		'seProductCatalogVersionsSelectorModule'
	],
	declarations: [
		GeneralConfigurationComponent
	],
	initialize: (editorFieldMappingService: any) => {
		'ngInject';

		editorFieldMappingService.addFieldMapping('ProductCatalogVersionsSelector', null, null, {
			template: 'productCatalogVersionsSelectorWrapperTemplate.html'
		});
	}
})
export class AdministrationModule {}