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
import {MemoryStorage} from "../memorystorage/MemoryStorage";

import {
	IStorage,
	IStorageController,
	IStorageOptions,
	IStoragePropertiesService
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class MemoryStorageController implements IStorageController {

	readonly storageType: string;

	private readonly storages: any = {};

	constructor(private readonly $q: angular.IQService, storagePropertiesService: IStoragePropertiesService) {
		this.storageType = storagePropertiesService.getProperty("STORAGE_TYPE_IN_MEMORY");

	}

	getStorage(options: IStorageOptions): angular.IPromise<IStorage<any, any>> {
		let storage = this.storages[options.storageId];
		if (!storage) {
			storage = new MemoryStorage(this.$q);
		}
		this.storages[options.storageId] = storage;
		return this.$q.when(storage);
	}

	deleteStorage(storageId: string): angular.IPromise<boolean> {
		delete this.storages[storageId];
		return this.$q.when(true);
	}

	getStorageIds(): angular.IPromise<string[]> {
		return this.$q.when(Object.keys(this.storages));
	}


}