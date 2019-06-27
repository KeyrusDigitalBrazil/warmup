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
import {Personalization} from "personalizationcommons/dtos/Personalization";
import {Customize} from "personalizationcommons/dtos/Customize";
import {CombinedView} from "personalizationcommons/dtos/CombinedView";
import {SeData} from "personalizationcommons/dtos/SeData";

@GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData')
@SeInjectable()
export class PersonalizationsmarteditContextServiceProxy {

	setPersonalization(personalization: Personalization): void {
		'proxyFunction';
		return undefined;
	}

	setCustomize(customize: Customize): void {
		'proxyFunction';
		return undefined;
	}

	setCombinedView(combinedView: CombinedView): void {
		'proxyFunction';
		return undefined;
	}

	setSeData(seData: SeData): void {
		'proxyFunction';
		return undefined;
	}
}
