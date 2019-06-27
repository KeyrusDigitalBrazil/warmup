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
export interface IModalService {
	open: (config: {
		title?: string,
		titleSuffix?: string,
		cssClasses?: string,
		buttons?: any[],
		size?: string,
		templateInline?: string,
		templateUrl?: string,
		template?: string,
		animation?: boolean,
		controller: angular.IControllerConstructor
	}) => angular.IPromise<any>;
	close: (data?: any) => void;
	dismiss: (data?: any) => void;
}
