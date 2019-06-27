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
import {MultipleTriggersComponent} from './manageCustomizationView/multipleTriggersComponent/MultipleTriggersComponent';
import {TriggerTabService} from './manageCustomizationView/multipleTriggersComponent/TriggerTabService';

@SeModule({
	imports: [
		'smarteditServicesModule'
	],
	providers: [
		TriggerTabService
	],
	declarations: [
		MultipleTriggersComponent
	]
})
export class PersonalizationsmarteditManagementModule {}
