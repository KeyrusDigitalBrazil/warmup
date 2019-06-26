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
 * @ngdoc object
 * @name smarteditServicesModule.object:CacheAction
 * @description
 * A {@link smarteditServicesModule.object:@Cached @Cached} annotation is associated to a CacheAction.
 */
export class CacheAction {

	public name: string;

	constructor(name: string) {
		this.name = name;
	}

}