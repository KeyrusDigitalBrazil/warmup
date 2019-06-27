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
import {IStorageOptions} from "./IStorageOptions";
import {IStorage} from "./IStorage";
import {IStorageController} from "./IStorageController";

import {Cloneable} from "smarteditcommons";

import * as angular from "angular";

/**
 * @ngdoc interface
 * @name storage.interface:IStorageManager
 *
 * @description
 * IStorageManager represents a manager of multiple {@link storage.interface:IStorage IStorage}(s).
 *
 * Typically there is 1 StorageManager in the system, and it is responsible accessing, creating and deleting storages,
 * usually by delegating to {@link storage.interface:IStorageController IStorageController}(s).
 *
 */
export interface IStorageManager {

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManager#registerStorageController
     * @methodOf storage.interface:IStorageManager
     *
     * @description
     * Register a new StorageController with the manager.
     *
     * @param {IStorageController} controller - An {@link storage.interface:IStorageController IStorageController} instance
     */
	registerStorageController(controller: IStorageController): void;

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManager#hasStorage
     * @methodOf storage.interface:IStorageManager
     *
     * @description
     * Check if a storage has been created.
     *
     * @param {string} storageId - The unique storage ID
     */
	hasStorage(storageId: string): boolean;

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManager#getStorage
     * @methodOf storage.interface:IStorageManager
     *
     * @description
     * Get an existing or new storage
     *
     * @param {IStorageOptions} storageConfiguration - A {@link storage.interface:IStorageOptions IStorageOptions}
     */
	getStorage(storageConfiguration: IStorageOptions): angular.IPromise<IStorage<Cloneable, Cloneable>>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManager#deleteStorage
     * @methodOf storage.interface:IStorageManager
     *
     * @description
     * Permanently delete a storage and all its data
     *
     * @param {string} storageId - The unique storage ID
     * @param {boolean =} force - If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
	deleteStorage(storageId: string, force?: boolean): angular.IPromise<boolean>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManager#deleteExpiredStorages
     * @methodOf storage.interface:IStorageManager
     *
     * @description
     * Delete all storages that have exceeded their idle timeout time.
     * See {@link storage.interface:IStorageOptions IStorageOptions} for more details
     * @param {boolean =} force - If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
	deleteExpiredStorages(force?: boolean): angular.IPromise<boolean>;

}

