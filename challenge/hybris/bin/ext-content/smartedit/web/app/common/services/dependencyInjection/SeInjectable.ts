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
 * @name smarteditServicesModule.object:@SeInjectable()
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit injectable service from a Dependency injection standpoint.
 * When multiple class annotations are used, {@link smarteditServicesModule.object:@SeInjectable() @SeInjectable()} must be closest to the class declaration.
 */
'se:smarteditcommons';
export const SeInjectable = function() {
	return function(providerConstructor: any) {
		return providerConstructor;
	};
};