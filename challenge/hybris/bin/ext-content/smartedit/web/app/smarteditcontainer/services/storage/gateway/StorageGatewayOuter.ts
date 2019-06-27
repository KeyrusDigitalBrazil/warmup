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
import {
	Cloneable,
	GatewayProxied,
	IStorage,
	IStorageGateway,
	IStorageManager,
	IStorageOptions,
	SeInjectable
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
@GatewayProxied()
@SeInjectable()
export class StorageGateway implements IStorageGateway {

	constructor(private $q: angular.IQService, private storageManager: IStorageManager) {
	}

	handleStorageRequest(storageConfiguration: IStorageOptions, method: keyof IStorage<Cloneable, Cloneable>, args: Cloneable[]): angular.IPromise<any> {
		const def = this.$q.defer();
		this.storageManager.getStorage(storageConfiguration).then(
			(storage: any) => def.resolve(storage[method](...args)),
			(reason: any) => def.reject(reason));
		return def.promise;
	}

}


