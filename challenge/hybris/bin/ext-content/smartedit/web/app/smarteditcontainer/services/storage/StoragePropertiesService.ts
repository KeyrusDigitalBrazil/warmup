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
import * as lo from 'lodash';
import {IStorageProperties, IStoragePropertiesService} from "smarteditcommons/services/storage";
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';
import {defaultStorageProperties} from "./defaultStorageProperties";

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:storagePropertiesService
 *
 * @description
 * The storagePropertiesService is a provider that implements the IStoragePropertiesService
 * interface and exposes the default storage properties. These properties are used to bootstrap various
 * pieces of the storage system.
 * By Means of StorageModule.configure() you would might change the default localStorage key names, or storage types.
 */
/** @internal */
@SeInjectable()
export class StoragePropertiesService implements IStoragePropertiesService {

	private readonly properties: IStorageProperties;

	constructor(
		storageProperties: IStorageProperties[],
		lodash: lo.LoDashStatic) {


		this.properties = lodash.cloneDeep(defaultStorageProperties);

		storageProperties.forEach((properties) => {
			lodash.merge(this.properties, properties);
		});
	}

	getProperty(propertyName: keyof IStorageProperties): any {
		return this.properties[propertyName];
	}
}
