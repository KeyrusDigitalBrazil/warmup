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
import * as angular from "angular";
import {
	Cloneable,
	GatewayProxied,
	IStorage,
	IStorageGateway,
	IStorageOptions
} from "smarteditcommons";

/** @internal */
@GatewayProxied("handleStorageRequest")
export class StorageGateway implements IStorageGateway {

	handleStorageRequest(storageConfiguration: IStorageOptions, method: keyof IStorage<Cloneable, Cloneable>, args: Cloneable[]): angular.IPromise<any> {
		'proxyFunction';
		return null;
	}

}
