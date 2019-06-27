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
	rarelyChangingContent,
	userEvictionTag,
	Cached,
	IRestServiceFactory,
	ISessionService,
	IURIBuilder,
	SeInjectable
} from 'smarteditcommons/services';

export interface CatalogVersionSyncPermission {
	canSynchronize: boolean;
	targetCatalogVersion: string;
}

export interface CatalogVersionPermissionMap {
	key: string;
	value: string;
}

export interface CatalogVersionPermission {
	catalogId: string;
	catalogVersion: string;
	permissions: CatalogVersionPermissionMap[];
	syncPermissions: CatalogVersionSyncPermission[];
}

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:CATALOG_VERSION_PERMISSIONS_RESOURCE_URI
 *
 * @description
 * Path to fetch permissions of a given catalog version.
 */
export const CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT = '/permissionswebservices/v1/permissions/principals/:principal/catalogs';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:catalogVersionPermissionRestService
 *
 * @description
 * The catalog version permission service is used to check if the current user has been granted certain permissions
 * on a given catalog ID and catalog Version.
 */
@SeInjectable()
export class CatalogVersionPermissionRestService {

	constructor(
		private restServiceFactory: IRestServiceFactory,
		private sessionService: ISessionService,
		private CATALOG_VERSION_PERMISSIONS_RESOURCE_URI: string,
		private URIBuilder: {new(uri: string): IURIBuilder}
	) {}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:catalogVersionPermissionRestService#getCatalogVersionPermissions
	 * @methodOf smarteditServicesModule.service:catalogVersionPermissionRestService
	 *
	 * @description
	 * This method returns permissions from the Catalog Version Permissions Service API.
	 *
	 * Sample Request:
	 * GET /permissionswebservices/v1/permissions/principals/{principal}/catalogs?catalogId=apparel-deContentCatalog&catalogVersion=Online
	 *
	 * Sample Response from API:
	 * {
	 * "permissionsList": [
	 *     {
	 *       "catalogId": "apparel-deContentCatalog",
	 *       "catalogVersion": "Online",
	 *       "permissions": [
	 *         {
	 *           "key": "read",
	 *           "value": "true"
	 *         },
	 *         {
	 *           "key": "write",
	 *           "value": "false"
	 *         }
	 *       ],
	 *      "syncPermissions": [
	 *        {
	 *          "canSynchronize": "true",
	 *          "targetCatalogVersion": "Online"
	 *        }
	 *     }
	 *    ]
	 * }
	 *
	 * Sample Response returned by the service:
	 * {
	 *   "catalogId": "apparel-deContentCatalog",
	 *   "catalogVersion": "Online",
	 *   "permissions": [
	 *      {
	 *        "key": "read",
	 *        "value": "true"
	 *      },
	 *      {
	 *        "key": "write",
	 *        "value": "false"
	 *      }
	 *     ],
	 *    "syncPermissions": [
	 *      {
	 *        "canSynchronize": "true",
	 *        "targetCatalogVersion": "Online"
	 *      }
	 *    ]
	 *  }
	 *
	 * @param {String} catalogId The Catalog ID
	 * @param {String} catalogVersion The Catalog Version name
	 *
	 * @returns {IPromise} A Promise which returns an object exposing a permissions array containing the catalog version permissions
	 */
	@Cached({actions: [rarelyChangingContent], tags: [userEvictionTag]})
	getCatalogVersionPermissions(catalogId: string, catalogVersion: string): angular.IPromise<CatalogVersionPermission | {}> {
		this.validateParams(catalogId, catalogVersion);

		return this.sessionService.getCurrentUsername().then((principal) => {
			const postURI = new this.URIBuilder(this.CATALOG_VERSION_PERMISSIONS_RESOURCE_URI)
				.replaceParams({principal}).build();

			const restService = this.restServiceFactory.get<{permissionsList: CatalogVersionPermission[]}>(postURI);

			return restService.get({
				catalogId,
				catalogVersion
			}).then(({permissionsList}) => {
				return permissionsList[0] || {};
			});
		});
	}

	// TODO: When everything has been migrated to typescript it is sufficient enough to remove this validation.
	private validateParams(catalogId: string, catalogVersion: string): void {
		if (!catalogId) {
			throw new Error('catalog.version.permission.service.catalogid.is.required');
		}

		if (!catalogVersion) {
			throw new Error('catalog.version.permission.service.catalogversion.is.required');
		}
	}

}
