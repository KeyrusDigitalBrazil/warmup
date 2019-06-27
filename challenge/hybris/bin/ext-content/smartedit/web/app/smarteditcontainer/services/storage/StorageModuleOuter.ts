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
import {MemoryStorageController} from "./controller/memorystorage/MemoryStorageController";
import {LocalStorageController} from "./controller/webstorage/LocalStorageController";
import {SessionStorageController} from "./controller/webstorage/SessionStorageController";
import {StorageGateway} from "./gateway/StorageGatewayOuter";
import {StorageManagerGateway} from "./gateway/StorageManagerGatewayOuter";
import {StorageManager} from "./manager/StorageManager";
import {StoragePropertiesService} from "./StoragePropertiesService";

import {
	IStorageGateway,
	IStorageManager,
	IStorageManagerFactory,
	IStorageManagerGateway,
	SeModule,
	SeModuleWithProviders,
	StorageManagerFactory,
	TypedMap
} from "smarteditcommons";

import * as angular from "angular";

/**
 * Storage API
 * @type {angular.IModule}
 */

@SeModule({
	providers: [
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
		StoragePropertiesService,
		{
			provide: "doNotUseStorageManager",
			useClass: StorageManager
		},
		{
			provide: "storageManagerFactory",
			useFactory: ($q: angular.IQService, $log: angular.ILogService, doNotUseStorageManager: IStorageManager) => {
				// 'ngInject';
				return new StorageManagerFactory(doNotUseStorageManager);
			},
			deps: ["$q", "$log", "doNotUseStorageManager"]
		},
		{
			provide: "seStorageManager",
			useFactory: (storageManagerFactory: IStorageManagerFactory) => {
				// 'ngInject';
				return storageManagerFactory.getStorageManager("se.nsp");
			},
			deps: ["storageManagerFactory"]
		},
		{
			provide: "storageGateway",
			useFactory: ($q: angular.IQService, doNotUseStorageManager: IStorageManager) => {
				// 'ngInject';
				return new StorageGateway($q, doNotUseStorageManager);
			},
			deps: ["$q", "doNotUseStorageManager"]
		},
		{
			provide: "storageManagerGateway",
			useFactory: (doNotUseStorageManager: IStorageManager) => {
				// 'ngInject';
				return new StorageManagerGateway(doNotUseStorageManager);
			},
			deps: ["doNotUseStorageManager"]
		}

	],
	initialize: (
		$q: angular.IQService,
		storagePropertiesService: StoragePropertiesService,
		seStorageManager: IStorageManager,
		storageGateway: IStorageGateway,
		storageManagerGateway: IStorageManagerGateway) => {

		'ngInject';
		seStorageManager.registerStorageController(new LocalStorageController($q, storagePropertiesService));
		seStorageManager.registerStorageController(new SessionStorageController($q, storagePropertiesService));
		seStorageManager.registerStorageController(new MemoryStorageController($q, storagePropertiesService));
	}
})
export class StorageModule {
	static configure(properties: TypedMap<any> = {}): SeModuleWithProviders {
		return {
			seModule: StorageModule,
			providers: [{
				provide: "storageProperties",
				multi: true,
				useValue: properties
			}]
		};
	}
}