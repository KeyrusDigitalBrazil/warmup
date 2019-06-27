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
import {Payload, Primitive} from "smarteditcommons";


/**
 * @ngdoc interface
 * @name storage.interface:IStorageOptions
 *
 * @description
 *
 * IStorageOptions defines the options when accessing or creating a storage.
 *
 */
export interface IStorageOptions extends Payload {

	[index: string]: Primitive | Primitive[] | Payload | Payload[];

    /**
     * @ngdoc property
     * @name storage.interface:IStorageOptions.property:storageId
     * @propertyOf storage.interface:IStorageOptions
     *
     * @description
     * ```storageId: string```
     *
     * A unique storage ID in the system. You can perform storage operations using only the storage ID using the
     * {@link storage.interface:IStorageManager IStorageManager}
     */
	storageId: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageOptions.property:storageType
     * @propertyOf storage.interface:IStorageOptions
     *
     * @description
     * ```storageType: string```
     *
     * A storage type that corresponds to the storageType of a {@link storage.interface:IStorageController IStorageController}
     * registered in the StorageManager.
     */
	storageType: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageOptions.property:storageVersion
     * @propertyOf storage.interface:IStorageOptions
     *
     * @description
     * ```storageVersion?: string```
     *
     * Optional, default value = "0"
     *
     * An optional storage version. The storageVersion provides an easy way to handle changing data models of persisted
     * storages. If you have a persisted storage (ex localStorage) and you change your data model of the items stored
     * in that storage, the next time the application runs you could be faced with your services receiving invalid data.
     *
     * In this case you can either handle it yourself in a 1-time cleanup or migration script, or simple change the
     * version for this storage. Whenever you access an existing storage through the StorageManager with a different
     * version then was previously set, the existing storage is emptied.
     */
	storageVersion?: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageOptions.property:expiresAfterIdle
     * @propertyOf storage.interface:IStorageOptions
     *
     * @description
     * ```expiresAfterIdle?: number```
     *
     * Optional
     *
     * Number of idle milliseconds after which the storage expires. A storage idle time is the time since the
     * StorageManager last receives a getStorage() for that storage id.
     *
     * There is no automatic cleaning of expired storages, an accessing an expired storage will simply reset the idle
     * time, so it will no longer be expired.
     *
     * If you want to cleanup expired storages then you must manual call {@link storage.interface:IStorageManager#deleteExpiredStorages deleteExpiredStorages}
     * on the StorageManager.
     */
	expiresAfterIdle?: number;

}
