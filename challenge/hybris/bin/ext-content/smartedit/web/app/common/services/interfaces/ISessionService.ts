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

import * as angular from 'angular';
import {User} from 'smarteditcommons';

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISessionService
 * @description
 * The ISessionService provides information related to the current session
 * and the authenticated user (including a user readable and writeable languages). 
 */
export abstract class ISessionService {

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the username, previously mentioned as "principalUID",
     * associated to the authenticated user.
     *
     * @returns {angular.IPromise<string>} A promise resolving to the username,
     * previously mentioned as "principalUID", associated to the
     * authenticated user.
     */
	getCurrentUsername(): angular.IPromise<string> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUserDisplayName
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the displayed name associated to the authenticated user.
     *
     * @returns {angular.IPromise<string>} A promise resolving to the displayed name
     * associated to the authenticated user.
     */
	getCurrentUserDisplayName(): angular.IPromise<string> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUser
     * @methodOf smarteditServicesModule.interface:ISessionService 
     * 
     * @description
     * Returns the data of the current authenticated user. 
     * Also note that as part of the User object returned by this method contains 
     * the list of readable and writeable languages available to the user. 
     * 
     * @returns {angular.IPromise<User>} A promise resolving to the data of the current 
     * authenticated user. 
     */
	getCurrentUser(): angular.IPromise<User> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#hasUserChanged
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns boolean indicating whether the current user is different from
     * the last authenticated one.
     *
     * @returns {angular.IPromise<boolean>} Boolean indicating whether the current user is
     * different from the last authenticated one.
     */
	hasUserChanged(): angular.IPromise<boolean> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#resetCurrentUserData
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Reset all data associated to the authenticated user.
     * to the authenticated user.
     * 
     * @return {angular.IPromise<void>} returns an empty promise.
     */
	resetCurrentUserData(): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#setCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Set the username, previously mentioned as "principalUID", associated
     * to the authenticated user.
     *
     * @param {String} currentUsername Username, previously mentioned as
     * "principalUID", associated to the authenticated user.
     * 
     * @return {angular.IPromise<void>} returns an empty promise.
     */
	setCurrentUsername(username: string): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}
}