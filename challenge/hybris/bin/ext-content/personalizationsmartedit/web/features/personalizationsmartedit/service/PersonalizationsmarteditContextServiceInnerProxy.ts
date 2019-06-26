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
import {PersonalizationsmarteditContextService} from "personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner";
import {Customize} from "personalizationcommons/dtos/Customize";
import {CombinedView} from "personalizationcommons/dtos/CombinedView";
import {SeData} from "personalizationcommons/dtos/SeData";
import {Personalization} from "personalizationcommons/dtos/Personalization";

@GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData')
@SeInjectable()
export class PersonalizationsmarteditContextServiceProxy {

	constructor(
		protected personalizationsmarteditContextService: PersonalizationsmarteditContextService) {
	}

	setPersonalization(newPersonalization: Personalization): void {
		this.personalizationsmarteditContextService.setPersonalization(newPersonalization);
	}

	setCustomize(newCustomize: Customize): void {
		this.personalizationsmarteditContextService.setCustomize(newCustomize);
	}

	setCombinedView(newCombinedView: CombinedView): void {
		this.personalizationsmarteditContextService.setCombinedView(newCombinedView);
	}

	setSeData(newSeData: SeData): void {
		this.personalizationsmarteditContextService.setSeData(newSeData);
	}

}
