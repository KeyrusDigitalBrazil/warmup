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
	IStorageGateway,
	IStorageOptions
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class StorageProxy<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {

	constructor(private configuration: IStorageOptions, private storageGateway: IStorageGateway) {
	}

	clear(): angular.IPromise<boolean> {
		return this.storageGateway.handleStorageRequest(this.configuration, "clear", this.arrayFromArguments(arguments));
	}

	dispose(): angular.IPromise<boolean> {
		return this.storageGateway.handleStorageRequest(this.configuration, "dispose", this.arrayFromArguments(arguments));
	}

	entries(): angular.IPromise<any[]> {
		return this.storageGateway.handleStorageRequest(this.configuration, "entries", this.arrayFromArguments(arguments));
	}

	find(queryObject?: Q): angular.IPromise<D[]> {
		return this.storageGateway.handleStorageRequest(this.configuration, "find", this.arrayFromArguments(arguments));
	}

	get(queryObject?: Q): angular.IPromise<D> {
		return this.storageGateway.handleStorageRequest(this.configuration, "get", this.arrayFromArguments(arguments));
	}

	getLength(): angular.IPromise<number> {
		return this.storageGateway.handleStorageRequest(this.configuration, "getLength", this.arrayFromArguments(arguments));
	}

	put(obj: D, queryObject?: Q): angular.IPromise<boolean> {
		return this.storageGateway.handleStorageRequest(this.configuration, "put", this.arrayFromArguments(arguments));
	}

	remove(queryObject?: Q): angular.IPromise<D> {
		return this.storageGateway.handleStorageRequest(this.configuration, "remove", this.arrayFromArguments(arguments));
	}

	private arrayFromArguments(args: IArguments): any[] {
		return Array.prototype.slice.call(args);
	}

}