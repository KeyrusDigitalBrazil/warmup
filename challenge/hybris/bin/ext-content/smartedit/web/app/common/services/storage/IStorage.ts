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
import {Cloneable} from "smarteditcommons";

import * as angular from "angular";

/**
 * @ngdoc interface
 * @name storage.interface:IStorage
 * @description
 *  ```IStorage<Q extends Cloneable, D extends Cloneable>```
 *
 *  The IStorage interface represents a data access point, but depending on the implementation doesn't
 *  necessarily contain the stored data directly. For example a cloud storage implementation might access the remote
 *  storage via REST on a get(), or it might fetch the whole storage and keep it in memory.
 *
 *  This interface is kept purposely un-opinionated to support a variety of storage types.
 *
 *  # Generics
 *  The interface requires two generics types be provided,
 *
 *  ### Q extends Cloneable
 *  This is the query type for the storage. In a typical key-value storage this is often a string key type, but
 *  in a more complex storage implementation, for instance indexedDb, this could be a query object.
 *  ### D extends Cloneable
 *  This represents the type of data being stored.
 *
 *  # Cloneable
 *  The IStorage interface does not allow any data or query types, they must extend the Cloneable interface.
 *  This is because the data must be able to be serialized, both for remote storages, and for storages that live in
 *  the inner smartedit application, as this data is passed over the w3c postMessage() between frames.
 */
export interface IStorage<Q extends Cloneable, D extends Cloneable> {

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#get
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Get a data record from the storage.
     *
     * @param {Q extends Cloneable} queryObject - The query type. In a key-value storage this would be the key
     */
	get: (queryObject?: Q) => angular.IPromise<D>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#put
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Put a data record into the storage, or replace an existing record
     *
     * @param {D extends Cloneable} obj - The data to store.
     * @param {Q extends Cloneable} queryObject - The query object. In a key-value storage this would be the key
     */
	put: (obj: D, queryObject?: Q) => angular.IPromise<boolean>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#remove
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Remove a data record from the storage.
     *
     * @param {Q extends Cloneable} queryObject - The query object. In a key-value storage this would be the key
     */
	remove: (queryObject?: Q) => angular.IPromise<D>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#find
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Find multiple records of data in the storage.
     *
     * @param {Q extends Cloneable} queryObject - The query type. In a key-value storage this would be the key
     */
	find: (queryObject?: Q) => angular.IPromise<D[]>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#clear
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Remove all data records from the storage.
     */
	clear: () => angular.IPromise<boolean>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#getLength
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Get the number of data records in the storage
     */
	getLength: () => angular.IPromise<number>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#dispose
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Remove all storage records and remove the storage itself and all metadata from the storage manager.
     */
	dispose: () => angular.IPromise<boolean>;

    /**
     * @ngdoc method
     * @name storage.interface:IStorage#entries
     * @methodOf storage.interface:IStorage
     *
     * @description
     * Get all query/data entries from the storage.
     *
     * For key-value storages this will be an array of key-value 2-tuples.
     *
     * ```[key, value][]```
     *
     */
	entries: () => angular.IPromise<any[]>;

}

