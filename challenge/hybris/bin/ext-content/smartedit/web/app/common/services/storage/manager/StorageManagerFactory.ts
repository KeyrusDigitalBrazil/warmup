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
import {IStorageManager} from "../IStorageManager";
import {IStorageManagerFactory} from "../IStorageManagerFactory";
import {NamespacedStorageManager} from "./NamespacedStorageManager";

/** @internal */
export class StorageManagerFactory implements IStorageManagerFactory {

	static ERR_INVALID_NAMESPACE(namespace: string): Error {
		return new Error(`StorageManagerFactory Error: invalid namespace [${namespace}]. Namespace must be a non-empty string`);
	}

	constructor(private theOneAndOnlyStorageManager: IStorageManager) {
	}

	getStorageManager(namespace: string) {
		this.validateNamespace(namespace);
		return new NamespacedStorageManager(this.theOneAndOnlyStorageManager, namespace);
	}

	private validateNamespace(namespace: string) {
		if (typeof namespace !== 'string' || namespace.length <= 0) {
			throw StorageManagerFactory.ERR_INVALID_NAMESPACE(namespace);
		}
	}
}