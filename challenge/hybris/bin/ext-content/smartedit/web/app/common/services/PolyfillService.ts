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
import {TestModeService} from 'smarteditcommons';
import {SeInjectable} from './dependencyInjection/di';

/* @internal */
@SeInjectable()
export class PolyfillService {

	constructor(
		private browserService: any,
		private testModeService: TestModeService
	) {}

	isEligibleForEconomyMode(): boolean {
		return this.browserService.isIE() || this.testModeService.isE2EMode();
	}

	isEligibleForExtendedView(): boolean {
		return (this.browserService.isIE() || this.browserService.isFF()) || this.testModeService.isE2EMode();
	}

	isEligibleForThrottledScrolling(): boolean {
		return this.browserService.isIE();
	}

}