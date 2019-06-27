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
import {GatewayProxiedAnnotationFactory} from './gatewayProxiedAnnotation';
import {SeModule} from './dependencyInjection/di';
import {GatewayFactory, GatewayProxy} from './gateway';
import {OperationContextService} from './httpErrorInterceptor/default/retryInterceptor/OperationContextService';
import {OperationContextAnnotationFactory} from './httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation';

/**
 * @name smarteditRootModule
 *
 * @description
 * Module acts as a root module of smartedit commons module.
 */
@SeModule({
	imports: [
		'resourceLocationsModule',
		'functionsModule',
		'seConstantsModule',
		'yjqueryModule',
		'yLoDashModule'
	],
	providers: [
		GatewayFactory,
		GatewayProxy,
		GatewayProxiedAnnotationFactory,
		OperationContextService,
		OperationContextAnnotationFactory
	],
	initialize: (gatewayProxiedAnnotationFactory: any, operationContextAnnotationFactory: any) => {
		'ngInject';
	}
})
/** @internal */
export class SmarteditRootModule {}
