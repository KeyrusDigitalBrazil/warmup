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
import {authorizationEvictionTag, rarelyChangingContent, Cached, IPermissionsRestServicePair, IPermissionsRestServiceResult, IRestService, IRestServiceFactory, ISessionService, SeInjectable, TypedMap} from "smarteditcommons";

/**
 * @ngdoc object
 * @name cmsSmarteditServicesModule.object:TypePermissionNames
 * @description
 * An enum type representing available type permission names for a given item
 */
export enum TypePermissionNames {
	CREATE = 'create',
	READ = 'read',
	CHANGE = 'change',
	REMOVE = 'remove'
}

/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService
 * 
 * @description
 * Rest Service to retrieve the type permissions.
 */
@SeInjectable()
export class TypePermissionsRestService {

	private readonly URI = "/permissionswebservices/v1/permissions/principals/:user/types";

	private resource: IRestService<IPermissionsRestServiceResult>;

	constructor(
		private $log: angular.ILogService,
		private $q: angular.IQService,
		private sessionService: ISessionService,
		restServiceFactory: IRestServiceFactory) {
		this.resource = restServiceFactory.get<IPermissionsRestServiceResult>(this.URI);
	}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService#hasCreatePermissionForTypes
	 * @methodOf cmsSmarteditServicesModule.service:TypePermissionsRestService
	 *
	 * @description
	 * Determines if the current user has CREATE access to the given types.
	 * 
	 * @param {String[]} types The codes of all types.
	 * @returns {angular.IPromise<TypedMap<boolean>>} A promise that resolves to a TypedMap object with key (the code) and 
	 * value (true if the user has CREATE access to the type or false otherwise).
	 */
	hasCreatePermissionForTypes(types: string[]): angular.IPromise<TypedMap<boolean>> {
		return this._getPermissionsForTypesAndName(types, TypePermissionNames.CREATE);
	}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService#hasReadPermissionForTypes
	 * @methodOf cmsSmarteditServicesModule.service:TypePermissionsRestService
	 *
	 * @description
	 * Determines if the current user has READ access to the given types.
	 * 
	 * @param {String[]} types The codes of all types.
	 * @returns {angular.IPromise<TypedMap<boolean>>} A promise that resolves to a TypedMap object with key (the code) and 
	 * value (true if the user has READ access to the type or false otherwise).
	 */
	hasReadPermissionForTypes(types: string[]): angular.IPromise<TypedMap<boolean>> {
		return this._getPermissionsForTypesAndName(types, TypePermissionNames.READ);
	}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService#hasUpdatePermissionForTypes
	 * @methodOf cmsSmarteditServicesModule.service:TypePermissionsRestService
	 *
	 * @description
	 * Determines if the current user has CHANGE access to the given types.
	 * 
	 * @param {String[]} types The codes of all types.
	 * @returns {angular.IPromise<TypedMap<boolean>>} A promise that resolves to a TypedMap object with key (the code) and 
	 * value (true if the user has CHANGE access to the type or false otherwise).
	 */
	hasUpdatePermissionForTypes(types: string[]): angular.IPromise<TypedMap<boolean>> {
		return this._getPermissionsForTypesAndName(types, TypePermissionNames.CHANGE);
	}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService#hasDeletePermissionForTypes
	 * @methodOf cmsSmarteditServicesModule.service:TypePermissionsRestService
	 *
	 * @description
	 * Determines if the current user has REMOVE access to the given types.
	 * 
	 * @param {String[]} types The codes of all types.
	 * @returns {angular.IPromise<TypedMap<boolean>>} A promise that resolves to a TypedMap object with key (the code) and 
	 * value (true if the user has REMOVE access to the type or false otherwise).
	 */
	hasDeletePermissionForTypes(types: string[]): angular.IPromise<TypedMap<boolean>> {
		return this._getPermissionsForTypesAndName(types, TypePermissionNames.REMOVE);
	}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TypePermissionsRestService#hasAllPermissionsForTypes
	 * @methodOf cmsSmarteditServicesModule.service:TypePermissionsRestService
	 *
	 * @description
	 * Determines if the current user has READ, CREATE, CHANGE, REMOVE access to the given types.
	 * 
	 * @param {String[]} types The codes of all types.
	 * @returns {angular.IPromise<TypedMap<TypedMap<boolean>>>} A promise that resolves to a TypedMap of TypedMap object with key (the code) and 
	 * value (true if the user has corresponding access to the type or false otherwise).
	 * {
	 *  "typeA": {"read": true, "change": false, "create": true, "remove": true},
	 *  "typeB": {"read": true, "change": false, "create": true, "remove": false}
	 * }
	 */
	hasAllPermissionsForTypes(types: string[]): angular.IPromise<TypedMap<TypedMap<boolean>>> {
		const initialMap: TypedMap<TypedMap<boolean>> = {};

		return this._getAllPermissionsForTypes(types).then((response: IPermissionsRestServiceResult[]) => {
			return response.reduce((map: TypedMap<TypedMap<boolean>>, permissionsResult: IPermissionsRestServiceResult) => {
				if (permissionsResult.permissions) {
					map[permissionsResult.id] = {};
					map[permissionsResult.id][TypePermissionNames.READ] = this._getPermissionByNameAndResult(permissionsResult, TypePermissionNames.READ);
					map[permissionsResult.id][TypePermissionNames.CHANGE] = this._getPermissionByNameAndResult(permissionsResult, TypePermissionNames.CHANGE);
					map[permissionsResult.id][TypePermissionNames.CREATE] = this._getPermissionByNameAndResult(permissionsResult, TypePermissionNames.CREATE);
					map[permissionsResult.id][TypePermissionNames.REMOVE] = this._getPermissionByNameAndResult(permissionsResult, TypePermissionNames.REMOVE);
				}
				return map;
			}, initialMap);
		});
	}

	private _getPermissionByNameAndResult(permissionsResult: IPermissionsRestServiceResult, permissionName: string): boolean {
		return JSON.parse(permissionsResult.permissions.find((permission: IPermissionsRestServicePair) => permission.key === permissionName).value);
	}

	private _getPermissionsForTypesAndName(types: string[], permissionName: string): angular.IPromise<TypedMap<boolean>> {
		return this._getAllPermissionsForTypes(types).then((response: IPermissionsRestServiceResult[]) => {
			return response.reduce((map: TypedMap<boolean>, permissionsResult: IPermissionsRestServiceResult) => {
				if (permissionsResult.permissions) {
					map[permissionsResult.id] = this._getPermissionByNameAndResult(permissionsResult, permissionName);
				}
				return map;
			}, {} as TypedMap<boolean>);
		});
	}

	@Cached({actions: [rarelyChangingContent], tags: [authorizationEvictionTag]})
	private _getAllPermissionsForTypes(types: string[]): angular.IPromise<IPermissionsRestServiceResult[]> {
		if (types.length <= 0) {
			return this.$q.when([]);
		}
		return this.sessionService.getCurrentUsername().then((user: string) => {
			if (!user) {
				return [];
			}
			return this.resource.get({
				user,
				types: types.join(','),
				permissionNames: TypePermissionNames.CREATE + ',' + TypePermissionNames.CHANGE + ',' + TypePermissionNames.READ + ',' + TypePermissionNames.REMOVE
			}).then((response: any) => {
				return response.permissionsList || [];
			}, (error: any) => {
				if (error) {
					this.$log.error(`TypePermissionsRestService - no composed types ${types} exist`);
				}
				return this.$q.reject(error);
			});
		});
	}
}