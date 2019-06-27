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
import * as lo from 'lodash';
import {GatewayProxied, IAuthToken, ISessionService, IStorageService, SeInjectable} from 'smarteditcommons';

export interface ISessionAuth {
	[index: string]: IAuthToken | any;
}

/** @internal */
@GatewayProxied('isInitialized', 'storeAuthToken', 'getAuthToken', 'removeAuthToken', 'removeAllAuthTokens', 'storePrincipalIdentifier', 'getPrincipalIdentifier', 'removePrincipalIdentifier', 'getValueFromCookie')
@SeInjectable()
export class StorageService extends IStorageService {
	private STORAGE_COOKIE_NAME = 'smartedit-sessions';
	private CUSTOM_PROPERTIES = 'custom_properties';
	constructor(
		private lodash: lo.LoDashStatic,
		private $q: angular.IQService,
		private $injector: angular.auto.IInjectorService,
		private $cookies: angular.cookies.ICookiesService,
		private $window: ng.IWindowService,
		private $log: angular.ILogService
	) {
		super();
	}

	getSessionService(): ISessionService {
		return this.$injector.get('sessionService');
	}

	isInitialized(): angular.IPromise<boolean> {
		const sessions: ISessionAuth = this.getAuthTokens();
		return this.$q.when(this.lodash.values(this.lodash.omit(sessions, [this.CUSTOM_PROPERTIES])).length > 0);
	}

	storePrincipalIdentifier(principalUID: string): angular.IPromise<void> {
		return this.$q.when();
	}

	removePrincipalIdentifier(): angular.IPromise<void> {
		return this.$q.when();
	}

	getPrincipalIdentifier(): angular.IPromise<string> {
		return this.getSessionService().getCurrentUsername();
	}

	storeAuthToken(authURI: string, auth: IAuthToken): angular.IPromise<void> {
		const sessions: ISessionAuth = this.getAuthTokens();
		sessions[authURI] = auth;
		this._setToAuthCookie(sessions);
		return this.$q.when();
	}

	getAuthToken(authURI: string): angular.IPromise<IAuthToken> {
		const sessions: ISessionAuth = this.getAuthTokens();
		return this.$q.when(sessions[authURI]);
	}

	removeAuthToken(authURI: string): angular.IPromise<void> {
		const sessions: ISessionAuth = this.getAuthTokens();
		delete sessions[authURI];
		this._setToAuthCookie(sessions);
		return this.$q.when();
	}

	removeAllAuthTokens(): angular.IPromise<void> {
		this._removeAllAuthTokens();
		return this.$q.when();
	}

	getValueFromCookie(cookieName: string, isEncoded: boolean): angular.IPromise<any> {
		return this.$q.when(this._getValueFromCookie(cookieName, isEncoded));
	}

	getAuthTokens(): ISessionAuth {
		return this._getValueFromCookie(this.STORAGE_COOKIE_NAME, true) || {};
	}

	putValueInCookie(cookieName: string, value: any, encode: boolean) {
		this._putValueInCookie(cookieName, value, encode);
	}

	setItem(key: string, value: any) {
		const sessions = this.getAuthTokens();
		sessions[this.CUSTOM_PROPERTIES] = sessions[this.CUSTOM_PROPERTIES] || {};
		sessions[this.CUSTOM_PROPERTIES][key] = value;
		this._setToAuthCookie(sessions);
		return this.$q.when();
	}

	getItem(key: string) {
		const sessions = this.getAuthTokens();
		sessions[this.CUSTOM_PROPERTIES] = sessions[this.CUSTOM_PROPERTIES] || {};
		return this.$q.when(sessions[this.CUSTOM_PROPERTIES][key]);
	}

	private _removeAllAuthTokens() {
		const sessions: ISessionAuth = this.getAuthTokens();
		const newSessions = this.lodash.pick(sessions, [this.CUSTOM_PROPERTIES]);
		this._putValueInCookie(this.STORAGE_COOKIE_NAME, newSessions, true);
	}

	private _getValueFromCookie(cookieName: string, isEncoded: boolean): any {
		const rawValue: string = this.$cookies.get(cookieName);
		let value = null;
		if (rawValue) {
			try {
				value = JSON.parse((isEncoded) ? decodeURIComponent(escape(window.atob(rawValue))) : rawValue);
			} catch (e) {
				// protecting against deserialization issue
				this.$log.error('Failed during deserialization ', e);
			}
		}
		return value;
	}

	private _setToAuthCookie(sessions: ISessionAuth) {
		this._putValueInCookie(this.STORAGE_COOKIE_NAME, sessions, true);
	}

	private _putValueInCookie(cookieName: string, value: any, encode: boolean) {
		let processedValue: string = JSON.stringify(value);
		processedValue = (encode) ? btoa(unescape(encodeURIComponent(processedValue))) : processedValue;
		this.$cookies.put(cookieName, processedValue, {
			secure: this.$window.location.protocol.indexOf("https") >= 0
		});
	}
}