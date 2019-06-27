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
import 'jasmine';
import {
	CachedAnnotationFactory,
	CacheConfigAnnotationFactory,
	CacheService,
	ClassAnnotationFactory,
	GatewayProxiedAnnotationFactory,
	GatewayProxy,
	InvalidateCacheAnnotationFactory,
	MethodAnnotationFactory,
	OperationContextAnnotationFactory,
	OperationContextService
} from "smarteditcommons";

export class CoreAnnotationsHelperMocks {

	cacheService: jasmine.SpyObj<CacheService>;
	gatewayProxy: jasmine.SpyObj<GatewayProxy>;
	operationContextService: jasmine.SpyObj<OperationContextService>;
}

// at the time a factory is added to the smarteditcommons namespace, it may have already been ngInjected hence changed into an array
function getFactory(factory: ClassAnnotationFactory | MethodAnnotationFactory): any {
	return (window as any).smarteditLodash.isArray(factory) ? (factory as any)[factory.length - 1] : factory;
}

// tslint:disable-next-line:max-classes-per-file
class CoreAnnotationsHelper {

	initCached(): jasmine.SpyObj<CacheService> {

		///////////////////////////////////////////////////////

		const $log = jasmine.createSpyObj('$log', ['debug']);

		getFactory(CacheConfigAnnotationFactory)($log);

		///////////////////////////////////////////////////////

		const cacheService: jasmine.SpyObj<CacheService> = jasmine.createSpyObj<CacheService>('cacheService', ['handle', 'evict']);
		cacheService.handle.and.callFake((target: any, methdName: string, method: (...args: any[]) => any, invocationArguments: IArguments) => {
			return method.apply(undefined, invocationArguments);
		});
		getFactory(CachedAnnotationFactory)(cacheService);

		getFactory(InvalidateCacheAnnotationFactory)(cacheService);

		return cacheService;

	}


	initGatewayProxied(): jasmine.SpyObj<any> {

		const gatewayProxy: GatewayProxy = jasmine.createSpyObj<any>('gatewayProxy', ['initForService']);
		const $log = jasmine.createSpyObj('$log', ['debug']);

		getFactory(GatewayProxiedAnnotationFactory)(gatewayProxy, $log);
		return gatewayProxy;
	}

	initOperationContextService(): jasmine.SpyObj<OperationContextService> {
		const $injector = jasmine.createSpyObj<any>('$injector', ['has', 'get']);
		const operationContextService: jasmine.SpyObj<OperationContextService> = jasmine.createSpyObj<OperationContextService>('operationContextService', ['register']);
		const OPERATION_CONTEXT = jasmine.createSpy();

		getFactory(OperationContextAnnotationFactory)($injector, operationContextService, OPERATION_CONTEXT);
		return operationContextService;
	}

	init(): CoreAnnotationsHelperMocks {

		return {
			cacheService: this.initCached(),
			gatewayProxy: this.initGatewayProxied(),
			operationContextService: this.initOperationContextService()
		};

	}
}

export const coreAnnotationsHelper = new CoreAnnotationsHelper();
