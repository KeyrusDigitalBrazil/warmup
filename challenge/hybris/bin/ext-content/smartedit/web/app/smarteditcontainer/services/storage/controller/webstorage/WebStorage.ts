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
import {WebStorageBridge} from "./WebStorageBridge";

import {
	Cloneable,
	IStorage,
	IStorageOptions,
	TypedMap
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class WebStorage<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {

	public static ERR_INVALID_QUERY_OBJECT(queryObjec: any, storageId: string): Error {
		return new Error(`WebStorage exception for storage [${storageId}]. Invalid key [${queryObjec}]`);
	}

	constructor(
		private readonly $q: angular.IQService,
		private readonly controller: WebStorageBridge,
		private readonly storageConfiguration: IStorageOptions) {
	}

	clear(): angular.IPromise<boolean> {
		this.controller.saveStorageData({});
		return this.$q.when(true);
	}

	find(queryObject?: Q): angular.IPromise<D[]> {
		if (queryObject === undefined) {
			throw WebStorage.ERR_INVALID_QUERY_OBJECT(queryObject, this.storageConfiguration.storageId);
		}
		return this.get(queryObject).then((result) => {
			return [result];
		});
	}

	get(queryObject?: Q): angular.IPromise<D> {
		return this.controller.getStorageData().then((data) => {
			const key: string = this.getKeyFromQueryObj(queryObject);
			return this.$q.when(data[key] as D);
		});
	}

	put(obj: D, queryObject?: Q): angular.IPromise<boolean> {
		return this.controller.getStorageData().then((data) => {
			data[this.getKeyFromQueryObj(queryObject)] = obj;
			this.controller.saveStorageData(data);
			return this.$q.when(true);
		});
	}

	remove(queryObject?: Q): angular.IPromise<D> {
		if (queryObject === undefined) {
			throw WebStorage.ERR_INVALID_QUERY_OBJECT(queryObject, this.storageConfiguration.storageId);
		}
		const getPromise = this.get(queryObject);
		return this.controller.getStorageData().then((data) => {
			delete data[this.getKeyFromQueryObj(queryObject)];
			this.controller.saveStorageData(data);
			return getPromise;
		});
	}

	getLength(): angular.IPromise<number> {
		return this.controller.getStorageData().then((data) => {
			return this.$q.when(Object.keys(data).length);
		});
	}

	dispose(): angular.IPromise<boolean> {
		return this.$q.when(true);
	}

	entries(): angular.IPromise<any[]> {
		const entries: any[] = [];
		const def = this.$q.defer<any[]>();
		this.controller.getStorageData().then((data: TypedMap<D>) => {
			Object.keys(data).forEach((key) => {
				entries.push([JSON.parse(key), data[key]]);
			});
			def.resolve(entries);
		});
		return def.promise;
	}

	private getKeyFromQueryObj(queryObj: Q): string {
		return JSON.stringify(queryObj);
	}


}