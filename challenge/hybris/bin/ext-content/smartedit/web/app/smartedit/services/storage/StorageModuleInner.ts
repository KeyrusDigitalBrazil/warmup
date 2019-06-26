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
import {StorageGateway} from "./StorageGatewayInner";
import {StorageManagerGateway} from "./StorageManagerGatewayInner";

import {
	IStorageGateway,
	IStorageManager,
	IStorageManagerFactory,
	IStorageManagerGateway,
	SeModule,
	StorageManagerFactory
} from "smarteditcommons";

@SeModule({
	providers: [
		StorageGateway,
		StorageManagerGateway,
        /**
         * @ngdoc service
         * @name smarteditServicesModule.service:storageManagerFactory
         *
         * @description
         * The StorageManagerFactory implements the IStorageManagerFactory interface, and produces
         * StorageManager instances. Typically you would only create one StorageManager instance, and expose it through a
         * service for the rest of your application. StorageManagers produced from this factory will take care of
         * name-spacing storage ids, preventing clashes between extensions, or other storages with the same ID.
         * All StorageManagers produced by the storageManagerFactory delegate to the same single root StorageManager.
         *
         * Example:
         * ```
         * @SeModule(
         * 		providers:[
         * 		{
         * 			provide: "yourStorageManager",
         *    			useFactory: (storageManagerFactory: IStorageManagerFactory) => {
         * 	    				'ngInject';
         * 	    				return storageManagerFactory.getStorageManager("your_namespace");
         * 			}
         *         }
         *     ]
         * )
         * export class YourModule {}
         * ```
         */
		{
			provide: "storageManagerFactory",
			useFactory: (storageManagerGateway: IStorageManagerGateway) => {
				// 'ngInject';
				return new StorageManagerFactory(storageManagerGateway);
			},
			deps: ["storageManagerGateway"]
		},
		{
			provide: "seStorageManager",
			useFactory: (storageManagerFactory: IStorageManagerFactory) => {
				// 'ngInject';
				return storageManagerFactory.getStorageManager("se.nsp");
			},
			deps: ["storageManagerFactory"]

		}
	],
	initialize: (
		storageManagerFactory: IStorageManagerFactory,
		storageGateway: IStorageGateway,
		seStorageManager: IStorageManager
	) => {
		'ngInject';
		// instantiate proxied services
	}
})
export class StorageModule {}