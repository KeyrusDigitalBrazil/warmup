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
import {IStorage} from "../IStorage";
import {IStorageController} from "../IStorageController";
import {IStorageManager} from "../IStorageManager";
import {IStorageOptions} from "../IStorageOptions";
import {StorageNamespaceConverter} from "./StorageNamespaceConverter";


import * as angular from "angular";

/** @internal */
export class NamespacedStorageManager implements IStorageManager {

	constructor(private readonly storageManager: IStorageManager, private namespace: string) {
	}

	getStorage(storageConfiguration: IStorageOptions): angular.IPromise<IStorage<any, any>> {
		storageConfiguration.storageId = this.getNamespaceStorageId(storageConfiguration.storageId);
		return this.storageManager.getStorage(storageConfiguration);
	}

	deleteStorage(storageId: string, force: boolean = false): angular.IPromise<boolean> {
		return this.storageManager.deleteStorage(this.getNamespaceStorageId(storageId), force);
	}

	deleteExpiredStorages(force: boolean = false): angular.IPromise<boolean> {
		return this.storageManager.deleteExpiredStorages(force);
	}

	hasStorage(storageId: string): boolean {
		return this.storageManager.hasStorage(this.getNamespaceStorageId(storageId));
	}

	registerStorageController(controller: IStorageController): void {
		return this.storageManager.registerStorageController(controller);
	}

	getNamespaceStorageId(storageId: string): string {
		return StorageNamespaceConverter.getNamespacedStorageId(this.namespace, storageId);
	}

	getStorageManager(): IStorageManager {
		return this.storageManager;
	}

}