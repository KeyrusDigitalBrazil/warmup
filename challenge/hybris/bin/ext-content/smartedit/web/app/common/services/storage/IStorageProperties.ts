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


/**
 * @ngdoc interface
 * @name storage.interface:IStorageProperties
 *
 * @description
 *
 * IStorageProperties defines the interface for the configurable properties used in various part of the storage system.
 *
 * See defaultStorageProperties for default values.
 *
 */
export interface IStorageProperties {

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:STORAGE_IDLE_EXPIRY
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```STORAGE_IDLE_EXPIRY: number```
     *
     * The default number of milliseconds before an idle storage becomes expired.
     */
	STORAGE_IDLE_EXPIRY: number;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: string```
     *
     * The localStorage key used by the StorageManager to store its stores metadata
     */
	LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:STORAGE_TYPE_LOCAL_STORAGE
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```STORAGE_TYPE_LOCAL_STORAGE: string```
     *
     * The storageType for a localStorage Storage.
     */
	STORAGE_TYPE_LOCAL_STORAGE: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:STORAGE_TYPE_SESSION_STORAGE
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```STORAGE_TYPE_SESSION_STORAGE: string```
     *
     * The storageType for a sessionStorage Storage.
     */
	STORAGE_TYPE_SESSION_STORAGE: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:STORAGE_TYPE_IN_MEMORY
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```STORAGE_TYPE_IN_MEMORY: string```
     *
     * The storageType for an in-memory Storage.
     */
	STORAGE_TYPE_IN_MEMORY: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:LOCAL_STORAGE_ROOT_KEY
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```LOCAL_STORAGE_ROOT_KEY: string```
     *
     * The root localStorage key where all storages are nested.
     */
	LOCAL_STORAGE_ROOT_KEY: string;

    /**
     * @ngdoc property
     * @name storage.interface:IStorageProperties.property:SESSION_STORAGE_ROOT_KEY
     * @propertyOf storage.interface:IStorageProperties
     *
     * @description
     * ```SESSION_STORAGE_ROOT_KEY: string```
     *
     * The root sessionStorage key where all storages are nested.
     */
	SESSION_STORAGE_ROOT_KEY: string;

}