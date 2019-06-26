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
import {AbstractWebStorageController} from "./AbstractWebStorageController";

import {IStorageOptions} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class WebStorageBridge {

	constructor(private controller: AbstractWebStorageController, private configuration: IStorageOptions) {
	}

	saveStorageData(data: any): angular.IPromise<boolean> {
		return this.controller.saveStorageData(this.configuration.storageId, data);
	}

	getStorageData(): angular.IPromise<any> {
		return this.controller.getStorageData(this.configuration.storageId);
	}

}