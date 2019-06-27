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
/* forbiddenNameSpaces angular.module:false */
import * as angular from 'angular';

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to cmsSmarteditServicesModule
 *
 * IMPORANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 6.7
 */
/* @internal */
const deprecatedSince67 = () => {
	angular.module('AssetsServiceModule', ['cmsSmarteditServicesModule']);
	angular.module('trashedPageModule', ['cmsSmarteditServicesModule']);
};

export const deprecate = () => {
	deprecatedSince67();
};