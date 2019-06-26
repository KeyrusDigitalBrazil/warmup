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
import {OperationContextService} from './OperationContextService';

const operationContextName = 'OperationContextRegistered';

/**
 * @ngdoc object
 * @name smarteditCommonsModule.object:@OperationContextRegistered
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} is delegated to  
 * {@link smarteditCommonsModule.service:OperationContextService OperationContextService.register} and it provides the functionality 
 * to register an url with {@link seConstantsModule.object:OPERATION_CONTEXT operation context(s)}.
 * 
 * For example: 
 * 1. @OperationContextRegistered('apiUrl', ['CMS', 'INTERACTIVE'])
 * 2. @OperationContextRegistered('apiUrl', 'TOOLING')
 * 
 * @param {string} url
 * @param {string | string[]} operationContext
 */
'se:smarteditcommons';
export const OperationContextRegistered = annotationService.getClassAnnotationFactory(operationContextName) as (url: string, operationContext: string | string[]) => ClassDecorator;

'se:smarteditcommons';
export function OperationContextAnnotationFactory($injector: angular.auto.IInjectorService, operationContextService: OperationContextService, OPERATION_CONTEXT: any) {
	'ngInject';
	return annotationService.setClassAnnotationFactory(operationContextName, function(factoryArguments: any[]) {
		return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {

			originalConstructor.call(instance, ...invocationArguments);

			const url: string = $injector.has(factoryArguments[0]) ? $injector.get(factoryArguments[0]) : factoryArguments[0];

			if (typeof factoryArguments[1] === 'string') {
				const operationContext: string = OPERATION_CONTEXT[factoryArguments[1]];
				operationContextService.register(url, operationContext);
			} else if (Array.isArray(factoryArguments[1]) && factoryArguments[1].length > 0) {
				factoryArguments[1].forEach((element: string) => {
					operationContextService.register(url, OPERATION_CONTEXT[element]);
				});
			}
		};
	});
}