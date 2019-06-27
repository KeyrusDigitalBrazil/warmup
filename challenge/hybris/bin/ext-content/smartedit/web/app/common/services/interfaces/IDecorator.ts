
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

import {IFeature} from "./IFeature";
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IDecorator
 * 
 * @description
 * Interface for IDecorator and it acts as a payload passed to addDecorator method of featureService method to register a decorator as a feature.
 */
export interface IDecorator extends IFeature {
	displayCondition?(componentType: string, componentId: string): ng.IPromise<boolean>;
}
