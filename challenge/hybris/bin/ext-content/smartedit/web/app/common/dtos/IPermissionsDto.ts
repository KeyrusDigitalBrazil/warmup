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

/**
 * Key value pairs where the key is the permission and the value is a boolean string
 * Used in IPermissionsRestServiceResult
 */
export interface IPermissionsRestServicePair {
	key: string;
	value: string;
}

/**
 * Result of getting permissions form the PermissionsRestService.get
 */
export interface IPermissionsRestServiceResult {
	id?: string;
	permissions: IPermissionsRestServicePair[];
}

/**
 * The input param type for PermissionsRestService.get
 */
export interface IPermissionsRestServiceQueryData {
	user: string;
	permissionNames: string;
}

