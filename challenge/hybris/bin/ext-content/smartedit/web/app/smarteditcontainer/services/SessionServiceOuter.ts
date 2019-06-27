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

import {rarelyChangingContent, userEvictionTag, Cached, CryptographicUtils, GatewayProxied, InvalidateCache, IRestService, ISessionService, IStorageService, SeInjectable, User} from 'smarteditcommons';
import {RestServiceFactory} from 'smarteditcontainer/services';


/* @internal */
interface IWhoAmIData {
	displayName: string;
	uid: string;
}

/* @internal */
interface IUserData {
	uid: string;
	readableLanguages: string[];
	writeableLanguages: string[];
}
/** @internal */
@GatewayProxied('getCurrentUsername', 'getCurrentUserDisplayName', 'hasUserChanged', 'setCurrentUsername', 'getCurrentUser')
@SeInjectable()
export class SessionService extends ISessionService {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------ 
	private USER_DATA_URI = "/cmswebservices/v1/users/:userUid";

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------ 
	private cachedUserHash: string;
	private whoAmIService: IRestService<IWhoAmIData>;
	private userRestService: IRestService<IUserData>;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------ 
	constructor(
		private $log: angular.ILogService,
		private $q: angular.IQService,
		restServiceFactory: RestServiceFactory,
		private WHO_AM_I_RESOURCE_URI: string,
		private PREVIOUS_USERNAME_HASH: string,
		private storageService: IStorageService,
		private cryptographicUtils: CryptographicUtils
	) {
		super();
		this.whoAmIService = restServiceFactory.get<IWhoAmIData>(this.WHO_AM_I_RESOURCE_URI);
		this.userRestService = restServiceFactory.get<IUserData>(this.USER_DATA_URI);
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------ 
	public getCurrentUserDisplayName(): angular.IPromise<string> {
		return this.getCurrentUserData().then((currentUserData) => currentUserData.displayName);
	}

	public getCurrentUsername(): angular.IPromise<string> {
		return this.getCurrentUserData().then((currentUserData) => currentUserData.uid);
	}

	public getCurrentUser(): angular.IPromise<User> {
		return this.getCurrentUserData();
	}

	public hasUserChanged(): angular.IPromise<boolean> {
		const prevHashPromise = (this.cachedUserHash) ?
			this.$q.when(this.cachedUserHash) : this.storageService.getItem(this.PREVIOUS_USERNAME_HASH);
		return prevHashPromise.then((prevHash: string) => {
			return this.whoAmIService.get({}).then((currentUserData: IWhoAmIData) => {
				return (!!prevHash && prevHash !== this.cryptographicUtils.sha1Hash(currentUserData.uid));
			});
		});
	}

	@InvalidateCache(userEvictionTag)
	public setCurrentUsername(): angular.IPromise<void> {
		return this.whoAmIService.get({}).then((currentUserData: IWhoAmIData) => {
			// NOTE: For most of SmartEdit operation, it is enough to store the previous user hash in the cache. 
			// However, if the page is refreshed the cache is cleaned. Therefore, it's necessary to also store it in 
			// a cookie through the storageService. 
			this.cachedUserHash = this.cryptographicUtils.sha1Hash(currentUserData.uid);
			this.storageService.setItem(this.PREVIOUS_USERNAME_HASH, this.cachedUserHash);
		});
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------ 
	@Cached({actions: [rarelyChangingContent], tags: [userEvictionTag]})
	private getCurrentUserData(): angular.IPromise<User> {
		return this.whoAmIService.get({}).then((whoAmIData: IWhoAmIData) => {
			return this.userRestService.get({
				userUid: whoAmIData.uid
			}).then((userData: IUserData) => {
				return {
					uid: userData.uid,
					displayName: whoAmIData.displayName,
					readableLanguages: userData.readableLanguages,
					writeableLanguages: userData.writeableLanguages
				};
			});
		}).catch((reason: any) => {
			this.$log.warn("[SessionService]: Can't load session information", reason);
			return null;
		});
	}

}