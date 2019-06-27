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
import {IStorage} from "./IStorage";
import {IStorageOptions} from "./IStorageOptions";

import * as angular from "angular";

/**
 * @ngdoc interface
 * @name storage.interface:IStorageFactory
 *
 * @description
 *
 * IStorageFactory represents a typical factory of {@link storage.interface:IStorage IStorage}(s).
 *
 * See {@link smarteditServicesModule.service:storageManagerFactory storageManagerFactory}
 */
export interface IStorageFactory {

    /**
     * @ngdoc method
     * @name storage.interface:IStorageFactory#getStorage
     * @methodOf storage.interface:IStorageFactory
     *
     * @param {IStorageOptions} options - {@link storage.interface:IStorageOptions IStorageOptions}
     *
     * @return {angular.IPromise<IStorage<any, any>>} A storage instance
     */
	getStorage(configuration: IStorageOptions): angular.IPromise<IStorage<any, any>>;

}