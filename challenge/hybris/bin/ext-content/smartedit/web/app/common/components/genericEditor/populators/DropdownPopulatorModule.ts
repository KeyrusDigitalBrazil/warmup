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
import {FunctionsModule, SeModule} from "smarteditcommons";
import {DropdownPopulatorInterface} from "./DropdownPopulatorInterface";
import {OptionsDropdownPopulator} from "./OptionsDropdownPopulator";
import {UriDropdownPopulator} from "./UriDropdownPopulator";

/**
 * @ngdoc overview
 * @name dropdownPopulatorModule
 */
@SeModule({
	imports: [
		'yLoDashModule',
		'smarteditServicesModule',
		FunctionsModule
	],
	providers: [
		{
			provide: 'DropdownPopulatorInterface',
			useFactory: () => DropdownPopulatorInterface
		},
		OptionsDropdownPopulator,
		UriDropdownPopulator,
	]
})
export class DropdownPopulatorModule {}