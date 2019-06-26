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
	IStorageController,
	IStorageManager,
	IStorageManagerGateway,
	IStorageOptions,
	SeInjectable
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
@GatewayProxied(
	"getStorageSanitityCheck",
	"deleteExpiredStorages",
	"deleteStorage",
	"hasStorage"
)
@SeInjectable()
export class StorageManagerGateway implements IStorageManagerGateway {

	constructor(private storageManager: IStorageManager) {
	}

	getStorageSanitityCheck(storageConfiguration: IStorageOptions): angular.IPromise<boolean> {
		return this.storageManager.getStorage(storageConfiguration).then(() => true, () => false);
	}

	deleteExpiredStorages(force?: boolean): angular.IPromise<boolean> {
		return this.storageManager.deleteExpiredStorages(force);
	}

	deleteStorage(storageId: string, force?: boolean): angular.IPromise<boolean> {
		return this.storageManager.deleteStorage(storageId, force);
	}

	hasStorage(storageId: string): boolean {
		return this.storageManager.hasStorage(storageId);
	}

	getStorage(storageConfiguration: IStorageOptions): angular.IPromise<IStorage<Cloneable, Cloneable>> {
		throw new Error(`getStorage() is not supported from the StorageManagerGateway, please use the storage manager directly`);
	}

	registerStorageController(controller: IStorageController): void {
		throw new Error(`registerStorageController() is not supported from the StorageManagerGateway, please use the storage manager directly`);
	}

}
