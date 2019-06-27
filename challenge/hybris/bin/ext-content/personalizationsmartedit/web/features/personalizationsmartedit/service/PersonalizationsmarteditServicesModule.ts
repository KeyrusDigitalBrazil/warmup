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
import {COMPONENT_CONTAINER_TYPE_PROVIDER, CONTAINER_SOURCE_ID_ATTR_PROVIDER, PersonalizationsmarteditComponentHandlerService} from "personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService";
import {PersonalizationsmarteditCustomizeViewHelper} from "personalizationsmartedit/service/PersonalizationsmarteditCustomizeViewHelper";
import {PersonalizationsmarteditContextService} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner";
import {PersonalizationsmarteditContextServiceProxy} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerProxy";
import {PersonalizationsmarteditContextServiceReverseProxy} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerReverseProxy";
import {
	ACTIONS_DETAILS_PROVIDER,
	PersonalizationsmarteditRestService
} from "personalizationsmartedit/service/PersonalizationsmarteditRestService";

@SeModule({
	imports: [
		'smarteditServicesModule',
		'yLoDashModule',
		'personalizationsmarteditCommons',
		'contextualMenuServiceModule',
		'personalizationsmarteditCommonsModule'
	],
	providers: [
		PersonalizationsmarteditComponentHandlerService,
		PersonalizationsmarteditCustomizeViewHelper,
		PersonalizationsmarteditContextService,
		PersonalizationsmarteditContextServiceProxy,
		PersonalizationsmarteditContextServiceReverseProxy,
		PersonalizationsmarteditRestService,
		COMPONENT_CONTAINER_TYPE_PROVIDER,
		CONTAINER_SOURCE_ID_ATTR_PROVIDER,
		ACTIONS_DETAILS_PROVIDER
	]
})
export class PersonalizationsmarteditServicesModule {}
