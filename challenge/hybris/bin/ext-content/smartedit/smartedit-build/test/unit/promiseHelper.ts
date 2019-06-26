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
import 'jasmine';
import * as angular from 'angular';

import IPromise = angular.IPromise;
import SpyObj = jasmine.SpyObj;

export enum PromiseType {
	RESOLVES, REJECTS
}
/**
 * @ngdoc interface
 * @name testHelpers.interface:IExtensiblePromise<T>
 * @description
 * extended spied on angular promise that stores its promiseType (REJECTS or RESOLVES) and its value
 */
export interface IExtensiblePromise<T> extends SpyObj<IPromise<T>> {
	value: T;
	promiseType: PromiseType;
	name: string;
}
/**
 * @ngdoc service
 * @name testHelpers.service:PromiseHelper
 * @description
 * Helper to easily make use of immediately resolving/rejecting promises in unit tests
 */
class PromiseHelper {

	/**
	 * @ngdoc method
	 * @name testHelpers.service:PromiseHelper#buildPromise
	 * @methodOf testHelpers.service:PromiseHelper
	 * @description
	 * Builds an immediately resolving or rejecting promise
	 * @param {string} name the name of the promise
	 * @param {PromiseType} promiseType enum of values RESOLVES and REJECTS to indicate the behaviour of the promise
	 * @param {object} value  the object to which the promise resolves or rejects, it will be passed as argument to its resolveFunction or rejectFunction passed as arguments of .then method
	 */
	buildPromise<T>(name: string, promiseType: PromiseType = PromiseType.RESOLVES, value?: T): IExtensiblePromise<T> {

		const promise = jasmine.createSpyObj<IPromise<T>>(name, ['then', 'finally', 'catch']) as IExtensiblePromise<T>;

		promise.promiseType = promiseType;
		promise.value = value;
		promise.name = name;
		promise.then.and.callFake((resolveFunction: (response: any) => any, rejectFunction: (response: any) => any) => {
			let newValue: any = null;
			if (promise.promiseType === PromiseType.RESOLVES) {
				if (resolveFunction) {
					newValue = resolveFunction(promise.value);
				} else {
					newValue = promise.value;
				}
				if (newValue && newValue.then) {
					return newValue;
				} else {
					return this.buildPromise(name + "_chained", PromiseType.RESOLVES, newValue);
				}
			} else {
				if (rejectFunction) {
					newValue = rejectFunction(promise.value);
				} else {
					newValue = promise.value;
				}
				if (newValue && newValue.then) {
					return newValue;
				} else {
					return this.buildPromise(name + "_chained", PromiseType.REJECTS, newValue);
				}
			}
		});

		promise.finally.and.callFake((finallyFunction: (response: any) => any) => {
			let newValue: any = finallyFunction(promise.value);
			return this.buildPromise(name + "_chained", promise.promiseType, newValue);
		});

		promise.catch.and.callFake((rejectFunction: (response: any) => any) => {
			if (promise.promiseType === PromiseType.REJECTS) {
				let newValue: any = rejectFunction(promise.value);
				return this.buildPromise(name + "_chained", PromiseType.REJECTS, newValue);
			}

			return promise;
		});

		return promise;
	}

	/**
	 * @ngdoc method
	 * @name testHelpers.service:PromiseHelper#$q
	 * @methodOf testHelpers.service:PromiseHelper
	 * @description
	 * Builds an immediately resolving or rejecting mock of angular $q
	 */
	$q(): SpyObj<angular.IQService> {

		const $q: SpyObj<angular.IQService> = jasmine.createSpyObj<angular.IQService>('$q', ['all', 'defer', 'when', 'resolve', 'reject']);

		$q.all.and.callFake((promises: IExtensiblePromise<any>[]) => {

			const collector: any[] = [];

			promises
				.filter((promise) => promise.promiseType === PromiseType.RESOLVES)
				.forEach((promise) => {
					promise.then((response) => {
						collector.push(response);
					});
				});

			const oneRejected = promises.find((promise) => promise.promiseType === PromiseType.REJECTS);

			if (!oneRejected) {
				return this.buildPromise("arrayPromise", PromiseType.RESOLVES, collector);
			} else {
				return this.buildPromise("arrayPromise", PromiseType.REJECTS, oneRejected.value);
			}
		});

		$q.when.and.callFake((value: any) => {
			if (value && value.then) {
				return value;
			} else {
				return this.buildPromise("whenPromise", PromiseType.RESOLVES, value);
			}
		});

		$q.defer.and.callFake(() => {

			const deferred: angular.IDeferred<any> = {

				promise: this.buildPromise<any>("deferredPromise", PromiseType.REJECTS),

				resolve(value: any) {
					this.promise.promiseType = PromiseType.RESOLVES;
					this.promise.value = value;
				},

				reject(value: any) {
					this.promise.promiseType = PromiseType.REJECTS;
					this.promise.value = value;
				},

				notify(state?: any) {angular.noop();}
			};
			return deferred;
		});

		$q.resolve.and.callFake((value: any) => {
			if (value && value.then) {
				return value;
			} else {
				return this.buildPromise("resolvePromise", PromiseType.RESOLVES, value);
			}
		});

		$q.reject.and.callFake((value: any) => {
			if (value && value.then) {
				return value;
			} else {
				return this.buildPromise("rejectPromise", PromiseType.REJECTS, value);
			}
		});

		return $q;
	}
}

export const promiseHelper = new PromiseHelper();