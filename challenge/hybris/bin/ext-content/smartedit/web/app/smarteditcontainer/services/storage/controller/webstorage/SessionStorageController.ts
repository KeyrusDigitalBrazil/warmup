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
import {IStoragePropertiesService} from "smarteditcommons";

import * as angular from "angular";

/** @internal */
export class SessionStorageController extends AbstractWebStorageController {

	readonly storageType: string;

	constructor($q: angular.IQService, private storagePropertiesService: IStoragePropertiesService) {
		super($q);
		this.storageType = this.storagePropertiesService.getProperty("STORAGE_TYPE_SESSION_STORAGE");
	}

	getStorageApi(): Storage {
		return window.sessionStorage;
	}

	getStorageRootKey(): string {
		return this.storagePropertiesService.getProperty("SESSION_STORAGE_ROOT_KEY");
	}


}