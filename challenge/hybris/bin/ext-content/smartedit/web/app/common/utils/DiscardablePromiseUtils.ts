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
import {SeInjectable} from '../services/dependencyInjection/di';
import {TypedMap} from '../dtos';

export interface DiscardablePromise<T> {
	promise: angular.IPromise<T> | Promise<T>;
	successCallback: (...args: any[]) => any;
	failureCallback: (...args: any[]) => any;
	discardableHolder?: {successCallback: (...args: any[]) => any, failureCallback: (...args: any[]) => any};
}
/**
 * @ngdoc service
 * @name functionsModule.service:DiscardablePromiseUtils
 * @description
 * helper to handle competing promises
 */
@SeInjectable()
export class DiscardablePromiseUtils {

	private _map = {} as TypedMap<DiscardablePromise<any>>;

	constructor(private $log: angular.ILogService) {
	}

	/**
	 * @ngdoc method
	 * @methodOf DiscardablePromiseUtils
	 * @name functionsModule.service:DiscardablePromiseUtils#apply
	 * @methodOf functionsModule.service:DiscardablePromiseUtils
	 * @description
	 * selects a new promise as candidate for invoking a given callback
	 * each invocation of this method for a given key discards the previously selected promise
	 * @param {string} key the string key identifying the discardable promise
	 * @param {Promise} promise the discardable promise instance once a new candidate is called with this method
	 * @param {Function} successCallback the success callback to ultimately apply on the last promise not discarded
	 * @param {Function=} failureCallback the failure callback to ultimately apply on the last promise not discarded. Optional.
	 */
	apply<T>(key: string, promise: angular.IPromise<T> | Promise<T>, successCallback: (arg: T) => any, failureCallback?: (arg: Error) => any) {

		if (!this._map[key]) {
			this._map[key] = {
				promise,
				successCallback,
				failureCallback
			};
		} else {
			this.$log.debug(`competing promise for key ${key}`);
			delete this._map[key].discardableHolder.successCallback;
			delete this._map[key].discardableHolder.failureCallback;
			this._map[key].promise = promise;
		}

		this._map[key].discardableHolder = {
			successCallback: this._map[key].successCallback,
			failureCallback: this._map[key].failureCallback
		};

		const self = this;
		const p = this._map[key].promise;
		(p as Promise<T>).then(
			function(response: T) {

				if (this.successCallback) {
					delete self._map[key];
					this.successCallback.apply(undefined, arguments);
				} else {
					self.$log.debug(`aborted successCallback for promise identified by ${key}`);
				}
			}.bind(this._map[key].discardableHolder),
			function(error: Error) {
				if (this.failureCallback) {
					delete self._map[key];
					this.failureCallback.apply(undefined, arguments);
				} else {
					self.$log.debug(`aborted failureCallback for promise identified by ${key}`);
				}
			}.bind(this._map[key].discardableHolder)
		);
	}

}
