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
import {
	Cloneable,
	IStorage,
	TypedMap
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class MemoryStorage<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {

	private data: TypedMap<D> = {};

	constructor(private $q: angular.IQService) {
	}

	clear(): angular.IPromise<boolean> {
		this.data = {};
		return this.$q.when(true);
	}

	dispose(): angular.IPromise<boolean> {
		return this.$q.when(true);
	}

	find(queryObject?: Q): angular.IPromise<D[]> {
		return this.get(queryObject).then((result) => [result]);
	}

	get(queryObject?: Q): angular.IPromise<D> {
		return this.$q.when(this.data[this.getKey(queryObject)]);
	}

	getLength(): angular.IPromise<number> {
		return this.$q.when(Object.keys(this.data).length);
	}

	put(obj: D, queryObject?: Q): angular.IPromise<boolean> {
		this.data[this.getKey(queryObject)] = obj;
		return this.$q.when(true);
	}

	remove(queryObject?: Q): angular.IPromise<D> {
		const originalData = this.data[this.getKey(queryObject)];
		delete this.data[this.getKey(queryObject)];
		return this.$q.when(originalData);
	}

	entries(): angular.IPromise<any[]> {
		const entries: any[] = [];
		Object.keys(this.data).forEach((key) => {
			entries.push([JSON.parse(key), this.data[key]]);
		});
		return this.$q.when(entries);
	}

	private getKey(queryObject: Q): string {
		return JSON.stringify(queryObject);
	}

}