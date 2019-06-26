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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IPrioritized
 * @description
 * Interface of entities aimed at being sorted by priority
 */
export interface IPrioritized {
	/**
	 * @ngdoc method
	 * @name number
	 * @methodOf smarteditServicesModule.interface:IPrioritized
	 * @description priority an optional number ranging from 0 to 1000 used for sorting
	 */
	priority?: number;
}
