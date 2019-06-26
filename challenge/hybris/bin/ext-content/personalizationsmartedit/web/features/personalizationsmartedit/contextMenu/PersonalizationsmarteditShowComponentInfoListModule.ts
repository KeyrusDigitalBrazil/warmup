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
import {PersonalizationsmarteditShowComponentInfoListComponent} from "./ShowComponentInfoList/PersonalizationsmarteditShowComponentInfoList";

@SeModule({
	imports: [
		'personalizationsmarteditCommons',
		'personalizationsmarteditCommonsModule'
	],

	declarations: [PersonalizationsmarteditShowComponentInfoListComponent]

})
export class PersonalizationsmarteditShowComponentInfoListModule {}
