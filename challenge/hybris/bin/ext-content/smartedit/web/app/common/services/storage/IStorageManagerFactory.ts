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
import {IStorageManager} from "./IStorageManager";

/**
 * @ngdoc interface
 * @name storage.interface:IStorageManagerFactory
 *
 * @description
 *
 * IStorageManagerFactory represents a typical factory of {@link storage.interface:IStorageManager IStorageManager}(s).
 * There should typically only be 1 StorageManager in the system, which make this factory seem redundant, but it's used
 * to create wrapper around the single real StorageManager.
 *
 * The main use-case is for namespacing. A namespaced storagemanager will take care to prevent storageID clashes
 * between extensions or teams.
 */
export interface IStorageManagerFactory {

    /**
     * @ngdoc method
     * @name storage.interface:IStorageManagerFactory#getStorageManager
     * @methodOf storage.interface:IStorageManagerFactory
     *
     * @param {string} namespace - A unique namespace for all your storage ids
     *
     * @return {IStorageManager} A StorageManager instance
     */
	getStorageManager(namespace: string): IStorageManager;

}