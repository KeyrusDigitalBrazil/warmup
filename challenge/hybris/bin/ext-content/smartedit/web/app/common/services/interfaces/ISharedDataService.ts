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
import {Cloneable} from 'smarteditcommons';
import * as angular from 'angular';
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISharedDataService
 *
 * @description
 * Provides an abstract extensible shared data service. Used to store any data to be used either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export abstract class ISharedDataService {

    /** 
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#get
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Get the data for the given key.
     *
     * @param {String} key The key of the data to fetch
     */
	get(key: string): angular.IPromise<Cloneable> {
		'proxyFunction';
		return null;
	}

    /** 
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#set
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Set data for the given key.
     *
     * @param {String} key The key of the data to set
     * @param {object} value The value of the data to set
     */
	set(key: string, value: Cloneable): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#update
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Convenience method to retrieve and modify on the fly the content stored under a given key
     *
     * @param {String} key The key of the data to store
     * @param {Function} modifyingCallback callback fed with the value stored under the given key. The callback must return the new value of the object to update.
     */
	update(key: string, modifyingCallback: (oldValue: any) => any): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}
}
