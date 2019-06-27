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
import {GatewayProxied, SeInjectable} from 'smarteditcommons';
import {PersonalizationsmarteditContextService} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter";

@GatewayProxied('applySynchronization', 'setPageId')
@SeInjectable()
export class PersonalizationsmarteditContextServiceReverseProxy {

	constructor(
		protected personalizationsmarteditContextService: PersonalizationsmarteditContextService) {
	}

	applySynchronization(): void {
		this.personalizationsmarteditContextService.applySynchronization();
	}

	setPageId(newPageId: any): any {
		const seData = this.personalizationsmarteditContextService.getSeData();
		seData.pageId = newPageId;
		this.personalizationsmarteditContextService.setSeData(seData);
	}
}
