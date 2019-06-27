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
import {SeModule} from "smarteditcommons";
import {SEDropdownServiceFactory} from "./SEDropdownServiceFactory";
import {SeDropdownComponent} from "./SeDropdownComponent";
import {DropdownPopulatorModule} from "../../populators/DropdownPopulatorModule";

/**
 * @ngdoc overview
 * @name seDropdownModule
 */
@SeModule({
	imports: [
		'smarteditServicesModule',
		'functionsModule',
		'seConstantsModule',
		DropdownPopulatorModule
	],
	providers: [
		{
			provide: 'DROPDOWN_IMPLEMENTATION_SUFFIX',
			useValue: 'DropdownPopulator'
		},
		{
			provide: 'LINKED_DROPDOWN',
			useValue: 'LinkedDropdown'
		},
		{
			provide: 'CLICK_DROPDOWN',
			useValue: 'ClickDropdown',
		},
		{
			provide: 'SEDropdownService',
			useFactory: SEDropdownServiceFactory
		}
	],
	declarations: [
		SeDropdownComponent
	]
})
export class SeDropdownModule {}