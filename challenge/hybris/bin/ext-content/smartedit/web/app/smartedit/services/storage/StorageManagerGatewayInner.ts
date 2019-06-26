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
import {StorageProxy} from "./StorageProxy";

import {
	Cloneable,
	GatewayProxied,
	IStorage,
	IStorageController,
	IStorageGateway,
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

	constructor(private $q: angular.IQService, private $log: angular.ILogService, private storageGateway: IStorageGateway) {
	}

    /**
     * Disabled for inner app, due not to being able to pass storage controller instances across the gateway
     * @param {IStorageController} controller
     */
	registerStorageController(controller: IStorageController): void {
		throw new Error(`registerStorageController() is not supported from the smartedit (inner) application, please register controllers from smarteditContainer`);
	}

	getStorage(storageConfiguration: IStorageOptions): angular.IPromise<IStorage<Cloneable, Cloneable>> {
		const errMsg = `Unable to get storage ${storageConfiguration.storageId}`;
		const def = this.$q.defer<IStorage<Cloneable, Cloneable>>();
		this.getStorageSanitityCheck(storageConfiguration).then((createdSuccessfully: boolean) => {
			if (createdSuccessfully) {
				def.resolve(new StorageProxy<Cloneable, Cloneable>(storageConfiguration, this.storageGateway));
			} else {
				this.$log.error(errMsg);
				def.reject(errMsg);
			}
		}, (result: any) => {
			this.$log.error(errMsg);
			this.$log.error(result);
			def.reject(errMsg);
		});
		return def.promise;
	}


	// =============================================
	// ============= PROXIED METHODS ===============
	// =============================================

	deleteExpiredStorages(force?: boolean): angular.IPromise<boolean> {
		'proxyFunction';
		return undefined;
	}

	deleteStorage(storageId: string, force?: boolean): angular.IPromise<boolean> {
		'proxyFunction';
		return undefined;
	}

	getStorageSanitityCheck(storageConfiguration: IStorageOptions): angular.IPromise<boolean> {
		'proxyFunction';
		return undefined;
	}

	hasStorage(storageId: string): boolean {
		'proxyFunction';
		return false;
	}

}
