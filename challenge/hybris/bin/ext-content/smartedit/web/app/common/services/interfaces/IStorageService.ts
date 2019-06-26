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
 * @name smarteditServicesModule.interface:IAuthToken
 * @description
 * Interface for Auth token
 */
export interface IAuthToken {
	access_token: string;
	expires_in: number;
	refresh_token: string;
	scope: string;
	token_type: string;
}

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IStorageService
 * @description
 * Interface for StorageService
 */
export abstract class IStorageService {

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#isInitialized
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to determine if the storage service has been initialized properly. It
	 * makes sure that the smartedit-sessions cookie is available in the browser.
	 *
	 * @returns {Boolean} Indicates if the storage service was properly initialized.
	 */
	isInitialized(): angular.IPromise<boolean> {
		'proxyFunction';
		return null;
	}

	/**
	 * @deprecated since 6.6
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#storePrincipalIdentifier
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to store the principal's login name in storage service. Deprecated since 6.6.
	 *
	 * @param {String} principalUID Value associated to store principal's login.
	 */
	storePrincipalIdentifier(principalUID: string): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @deprecated since 6.6
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#removePrincipalIdentifier
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to remove the principal's UID from storage service. Deprecated since 6.6.
	 *
	 */
	removePrincipalIdentifier(): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @deprecated since 6.6
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#getPrincipalIdentifier
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to retrieve the principal's login name from storage service. Deprecated since 6.6.
	 *
	 * @returns {String} principalNameValue principal's name associated with the key.
	 */
	getPrincipalIdentifier(): angular.IPromise<string> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#storeAuthToken
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method creates and stores a new key/value entry. It associates an authentication token with a
	 * URI.
	 *
	 * @param {String} authURI The URI that identifies the resource(s) to be authenticated with the authToken. Will be used as a key.
	 * @param {String} auth The token to be used to authenticate the user in the provided URI.
	 */
	storeAuthToken(authURI: string, auth: IAuthToken): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#getAuthToken
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to retrieve the authToken associated with the provided URI.
	 *
	 * @param {String} authURI The URI for which the associated authToken is to be retrieved.
	 * @returns {String} The authToken used to authenticate the current user in the provided URI.
	 */
	getAuthToken(authURI: string): angular.IPromise<IAuthToken> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#removeAuthToken
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * Removes the authToken associated with the provided URI.
	 *
	 * @param {String} authURI The URI for which its authToken is to be removed.
	 */
	removeAuthToken(authURI: string): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#removeAllAuthTokens
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method removes all authURI/authToken key/pairs from the storage service.
	 */
	removeAllAuthTokens(): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#getValueFromCookie
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * Retrieves the value stored in the cookie identified by the provided name.
	 */
	getValueFromCookie(cookieName: string, isEncoded: boolean): angular.IPromise<any> {
		'proxyFunction';
		return null;
	}

	putValueInCookie(cookieName: string, value: any, encode: boolean): void {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#setItem
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * This method is used to store the item.
	 * 
	 * @param {String} key The key of the item.
	 * @param {any} value The value of the item.
	 */
	setItem(key: string, value: any): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IStorageService#getItem
	 * @methodOf smarteditServicesModule.interface:IStorageService
	 *
	 * @description
	 * Retrieves the value for a given key.
	 * 
	 * @param {String} key The key of the item.
	 * 
	 * @returns {Promise<any>} A promise that resolves to the item value.
	 */
	getItem(key: string): angular.IPromise<any> {
		'proxyFunction';
		return null;
	}
}
