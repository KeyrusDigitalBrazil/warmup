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
import {Cloneable, GatewayProxied, ISharedDataService, SeInjectable, TypedMap} from 'smarteditcommons';

/** @internal */
@GatewayProxied()
@SeInjectable()
export class SharedDataService extends ISharedDataService {

	private storage: TypedMap<Cloneable> = {};

	constructor(private $q: angular.IQService) {
		super();
	}

	get(name: string): angular.IPromise<Cloneable> {
		return this.$q.when(this.storage[name]);
	}
	set(name: string, value: Cloneable): angular.IPromise<void> {
		this.storage[name] = value;
		return this.$q.when();
	}
	update(name: string, modifyingCallback: (oldValue: any) => any): angular.IPromise<void> {
		return this.get(name).then((oldValue: any) => {
			return this.$q.when(modifyingCallback(oldValue)).then((newValue: any) => {
				return this.set(name, newValue);
			});
		});
	}
}

