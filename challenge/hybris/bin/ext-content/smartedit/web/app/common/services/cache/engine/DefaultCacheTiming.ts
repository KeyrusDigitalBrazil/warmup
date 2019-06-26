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
import {ICacheItem, ICacheTiming} from "./interfaces";

export class DefaultCacheTiming implements ICacheTiming {
	private expirationAge: number;
	private refreshAge: number;

	constructor(expirationAge: number, refreshAge: number) {
		// The cached response is discarded if it is older than the expiration age.
		this.expirationAge = expirationAge;

		// maximum age for the cached response to be considered "fresh."
		this.refreshAge = refreshAge;
	}

	setAge(item: ICacheItem<any>): void {
		item.expirationAge = this.expirationAge;
		item.refreshAge = this.refreshAge;
	}
}
