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
import {annotationService} from 'smarteditcommons/services/annotationService';
import {GatewayProxy} from 'smarteditcommons/services/gateway';
import {diNameUtils} from 'smarteditcommons/services/dependencyInjection/di';

const GatewayProxiedName = 'GatewayProxied';

'se:smarteditcommons';
export const GatewayProxied = annotationService.getClassAnnotationFactory(GatewayProxiedName) as (...args: string[]) => ClassDecorator;

'se:smarteditcommons';
export function GatewayProxiedAnnotationFactory(gatewayProxy: GatewayProxy, $log: angular.ILogService) {
	'ngInject';
	return annotationService.setClassAnnotationFactory(GatewayProxiedName, function(factoryArguments?: string[]) {

		return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {

			originalConstructor.call(instance, ...invocationArguments);
			instance.gatewayId = diNameUtils.buildServiceName(originalConstructor);

			gatewayProxy.initForService(instance, factoryArguments.length > 0 ? factoryArguments : null);
			// $log.debug(`${instance.gatewayId} is mutated into a proxied service with the arguments (${factoryArguments})`);
		};
	});
}
