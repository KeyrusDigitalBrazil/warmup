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
import {WebStorage} from "./WebStorage";
import {WebStorageBridge} from "./WebStorageBridge";

import {
	IStorage,
	IStorageController,
	IStorageOptions
} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export abstract class AbstractWebStorageController implements IStorageController {

	abstract readonly storageType: string;

	protected constructor(protected $q: angular.IQService) {
	}

	abstract getStorageApi(): Storage;

	abstract getStorageRootKey(): string;

	getStorage(configuration: IStorageOptions): angular.IPromise<IStorage<any, any>> {

		const bridge = new WebStorageBridge(this, configuration);
		const store: any = new WebStorage<any, any>(this.$q, bridge, configuration);
		const oldDispose = store.dispose;
		store.dispose = () => {
			return this.deleteStorage(configuration.storageId).then(() => {
				return oldDispose();
			});
		};
		return this.$q.when(store);
	}

	deleteStorage(storageId: string): angular.IPromise<boolean> {
		const container = this.getWebStorageContainer();
		delete container[storageId];
		this.setWebStorageContainer(container);
		return this.$q.when(true);
	}

	getStorageIds(): angular.IPromise<string[]> {
		const keys = Object.keys(this.getWebStorageContainer());
		return this.$q.when(keys);
	}

	saveStorageData(storageId: string, data: any): angular.IPromise<boolean> {
		const root = this.getWebStorageContainer();
		root[storageId] = data;
		this.setWebStorageContainer(root);
		return this.$q.when(true);
	}

	getStorageData(storageId: string): angular.IPromise<any> {
		const root = this.getWebStorageContainer();
		if (root[storageId]) {
			return this.$q.when(root[storageId]);
		}
		return this.$q.when({});
	}

	private setWebStorageContainer(data: any): void {
		this.getStorageApi().setItem(this.getStorageRootKey(), JSON.stringify(data));
	}

	private getWebStorageContainer(): any {
		const container = this.getStorageApi().getItem(this.getStorageRootKey());
		if (!container) {
			return {};
		}
		return JSON.parse(container);
	}


}