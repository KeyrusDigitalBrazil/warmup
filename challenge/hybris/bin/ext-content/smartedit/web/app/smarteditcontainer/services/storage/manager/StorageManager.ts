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
import {MetaDataMapStorage} from "../metadata/MetaDataMapStorage";
import {IStorageMetaData} from "../metadata/IStorageMetaData";

import {
	IStorage,
	IStorageController,
	IStorageManager,
	IStorageOptions,
	IStoragePropertiesService,
	SeInjectable,
	TypedMap
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
@SeInjectable()
export class StorageManager implements IStorageManager {

	static ERR_NO_STORAGE_TYPE_CONTROLLER(storageType: string): Error {
		return new Error(`StorageManager Error: Cannot create storage. No Controller available to handle type [${storageType}]`);
	}

	private readonly storageMetaDataMap: MetaDataMapStorage<IStorageMetaData>;
	private readonly storageControllers: TypedMap<IStorageController> = {};

	private readonly storages: TypedMap<IStorage<any, any>> = {};

	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private storagePropertiesService: IStoragePropertiesService) {

		this.storageMetaDataMap = new MetaDataMapStorage(this.storagePropertiesService.getProperty("LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA"));
	}

	registerStorageController(controller: IStorageController): void {
		this.storageControllers[controller.storageType] = controller;
	}

	getStorage(storageConfiguration: IStorageOptions): angular.IPromise<IStorage<any, any>> {

		this.setDefaultStorageOptions(storageConfiguration);
		const def = this.$q.defer<IStorage<any, any>>();
		const loadExistingStorage = this.hasStorage(storageConfiguration.storageId);
		let pendingValidation = this.$q.when(true);
		if (loadExistingStorage) {
			const metadata: IStorageMetaData = this.storageMetaDataMap.get(storageConfiguration.storageId);
			pendingValidation = this.verifyMetaData(metadata, storageConfiguration);
		}
		pendingValidation.then(() => {
			if (this.storages[storageConfiguration.storageId]) {
				this.updateStorageMetaData(storageConfiguration);
				def.resolve(this.storages[storageConfiguration.storageId]);
			} else {
				this.getStorageController(storageConfiguration.storageType)
					.getStorage(storageConfiguration)
					.then((newStorage: IStorage<any, any>) => {
						this.applyDisposeDecorator(storageConfiguration.storageId, newStorage);
						this.updateStorageMetaData(storageConfiguration);
						this.storages[storageConfiguration.storageId] = newStorage;
						def.resolve(newStorage);
					});
			}
		}, (reason: any) => def.reject(reason));
		return def.promise;
	}

	hasStorage(storageId: string): boolean {
		// true if we have metadata for it
		return !!this.storageMetaDataMap.get(storageId);
	}

	deleteStorage(storageId: string, force: boolean = false): angular.IPromise<boolean> {
		delete this.storages[storageId];
		if (!this.hasStorage(storageId)) {
			return this.$q.when(true);
		}
		const metaData = this.storageMetaDataMap.get(storageId);
		if (metaData) {
			let ctrl: IStorageController;
			try {
				ctrl = this.getStorageController(metaData.storageType);
			} catch (e) {
				// silently fail on no storage type handler
				if (force) {
					this.storageMetaDataMap.remove(storageId);
				}
				return this.$q.when(true);
			}
			return ctrl.deleteStorage(storageId).then((() => {
				this.storageMetaDataMap.remove(storageId);
				return this.$q.when(true);
			}));
		} else {
			return this.$q.when(true);
		}
	}

	deleteExpiredStorages(force: boolean = false): angular.IPromise<boolean> {
		const deletePromises: angular.IPromise<boolean>[] = [];
		const storageMetaDatas: IStorageMetaData[] = this.storageMetaDataMap.getAll();
		storageMetaDatas.forEach((metaData: IStorageMetaData) => {
			if (this.isStorageExpired(metaData)) {
				deletePromises.push(this.deleteStorage(metaData.storageId, force));
			}
		});
		return this.$q.all(deletePromises).then(() => true, () => false);
	}

	private updateStorageMetaData(storageConfiguration: IStorageOptions): void {
		this.storageMetaDataMap.put(storageConfiguration.storageId, {
			storageId: storageConfiguration.storageId,
			storageType: storageConfiguration.storageType,
			storageVersion: storageConfiguration.storageVersion,
			lastAccess: Date.now()
		});
	}

	private isStorageExpired(metaData: IStorageMetaData): boolean {
		const timeSinceLastAccess: number = Date.now() - metaData.lastAccess;
		let idleExpiryTime = metaData.expiresAfterIdle;
		if (idleExpiryTime === undefined) {
			idleExpiryTime = this.storagePropertiesService.getProperty("STORAGE_IDLE_EXPIRY");
		}
		return timeSinceLastAccess >= idleExpiryTime;
	}

	private applyDisposeDecorator(storageId: string, storage: IStorage<any, any>): void {
		const originalDispose = storage.dispose;
		storage.dispose = () => {
			return this.deleteStorage(storageId).then(() => {
				return originalDispose();
			});
		};
	}

	private getStorageController(storageType: string): IStorageController {
		const controller = this.storageControllers[storageType];
		if (!controller) {
			throw StorageManager.ERR_NO_STORAGE_TYPE_CONTROLLER(storageType);
		}
		return controller;
	}


	private verifyMetaData(metadata: IStorageMetaData, configuration: IStorageOptions): angular.IPromise<boolean> {
		if (metadata.storageVersion !== configuration.storageVersion) {
			this.$log.warn(`StorageManager - Removing old storage version for storage ${metadata.storageId}`);
			return this.deleteStorage(metadata.storageId);
		}
		if (metadata.storageType !== configuration.storageType) {
			this.$log.warn(`StorageManager - Detected a change in storage type for existing storage. Removing old storage with id ${configuration.storageId}`);
			return this.deleteStorage(metadata.storageId);
		}
		return this.$q.when(true);
	}

	private setDefaultStorageOptions(options: IStorageOptions): void {
		if (!options.storageVersion || options.storageVersion.length <= 0) {
			options.storageVersion = "0";
		}
	}


}

