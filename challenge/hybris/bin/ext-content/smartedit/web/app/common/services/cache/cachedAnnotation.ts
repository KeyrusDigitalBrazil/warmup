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
import {annotationService} from '../annotationService';
import {CacheAction} from './CacheAction';
import {EvictionTag} from './EvictionTag';
import {CacheService} from './CacheService';
import * as lo from 'lodash';
import {functionsUtils} from "../../utils/FunctionsUtils";

const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////// CACHE CONFIG ////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

const cacheConfigAnnotationName = "CacheConfig";

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@CacheConfig
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for setting
 *  class level cache configuration to be merged into method specific {@link smarteditServicesModule.object:@Cached @Cached} and 
 *  {@link smarteditServicesModule.object:@InvalidateCache @InvalidateCache} configurations.
 * @param {object} cacheConfig the configuration fo this cache
 * @param {cacheAction} cacheConfig.actions the list of {@link smarteditServicesModule.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link smarteditServicesModule.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
'se:smarteditcommons';
export const CacheConfig = annotationService.getClassAnnotationFactory(cacheConfigAnnotationName) as (args: {actions?: CacheAction[], tags?: EvictionTag[]}) => ClassDecorator;

'se:smarteditcommons';
export function CacheConfigAnnotationFactory($log: angular.ILogService) {
	'ngInject';
	return annotationService.setClassAnnotationFactory(cacheConfigAnnotationName, (factoryArguments: [{actions: CacheAction[], tags?: EvictionTag[]}]) => {
		return function(instance: any, originalConstructor: (...x: any[]) => any, invocationArguments: any[]) {
			originalConstructor.call(instance, ...invocationArguments);

			instance.cacheConfig = factoryArguments[0];

			$log.debug(`adding cache config ${JSON.stringify(instance.cacheConfig)} to class ${functionsUtils.getInstanceConstructorName(instance)}`, instance);
		};
	});
}

///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// CACHE ////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

const CachedAnnotationName = 'Cached';

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@Cached
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for performing
 * invocation arguments sensitive method caching.
 * <br/> This annotation must only be used on methods returning promises.
 * @param {object} cacheConfig the configuration fo this cache
 * <br/> This configuration will be merged with a class level {@link smarteditServicesModule.object:@CacheConfig @acheConfig} if any.
 * @throws if no {@link smarteditServicesModule.object:CacheAction CacheAction} is found in the resulting merge
 * @param {cacheAction} cacheConfig.actions the list of {@link smarteditServicesModule.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link smarteditServicesModule.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
'se:smarteditcommons';
export const Cached = annotationService.getMethodAnnotationFactory(CachedAnnotationName) as (args?: {actions: CacheAction[], tags?: EvictionTag[]}) => MethodDecorator;

'se:smarteditcommons';
export function CachedAnnotationFactory(cacheService: CacheService) {
	'ngInject';
	return annotationService.setMethodAnnotationFactory(CachedAnnotationName, (factoryArguments: [{actions: CacheAction[], tags?: EvictionTag[]}]) => {

		return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, invocationArguments: IArguments) {

			let actions: CacheAction[] = [];
			let tags: EvictionTag[] = [];

			if (factoryArguments[0]) {
				actions = factoryArguments[0].actions;
				tags = factoryArguments[0].tags;
			}

			if (target.cacheConfig) {
				if (target.cacheConfig.actions) {
					actions = lodash.uniq(actions.concat(target.cacheConfig.actions));
				}
				if (target.cacheConfig.tags) {
					tags = lodash.uniq(tags.concat(target.cacheConfig.tags));
				}
			}

			if (!actions.length) {
				const constructorName = functionsUtils.getInstanceConstructorName(target);
				throw new Error(`method ${propertyName} of ${constructorName} is @Cached annotated but no CacheAction is specified either through @Cached or through class level @CacheConfig annotation`);
			}
			return cacheService.handle(target, propertyName, originalMethod, Array.prototype.slice.apply(invocationArguments), actions, tags);
		};
	});
}
///////////////////////////////////////////////////////////////////////////////
////////////////////////////// INVALIDATE CACHE ///////////////////////////////
///////////////////////////////////////////////////////////////////////////////

const InvalidateCacheName = 'InvalidateCache';
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@InvalidateCache
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for
 * invalidating all caches either directly or indirectly declaring the {@link smarteditServicesModule.object:EvictionTag eviction tag} passed as argument.
 * if no eviction tag is passed as argument, defaults to the optional eviction tags passed to the class through {@link smarteditServicesModule.object:@CacheConfig @CacheConfig}.
 * 
 * @param {EvictionTag} evictionTag the {@link smarteditServicesModule.object:EvictionTag eviction tag}.
 */
'se:smarteditcommons';
export const InvalidateCache = function(tag?: EvictionTag) {
	return annotationService.getMethodAnnotationFactory(InvalidateCacheName)(tag);
};

'se:smarteditcommons';
export function InvalidateCacheAnnotationFactory(cacheService: CacheService) {
	'ngInject';
	return annotationService.setMethodAnnotationFactory(InvalidateCacheName, (factoryArguments: [EvictionTag]) => {

		return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, invocationArguments: IArguments) {

			let tags: EvictionTag[] = [];

			const tag: EvictionTag = factoryArguments[0];
			if (!tag) {
				if (target.cacheConfig && target.cacheConfig.tags) {
					tags = target.cacheConfig.tags;
				}
			} else {
				tags = [tag];
			}

			if (!tags.length) {
				throw new Error(`method ${propertyName} of ${target.constructor.name} is @InvalidateCache annotated but no EvictionTag is specified either through @InvalidateCache or through class level @CacheConfig annotation`);
			}

			const returnedObject = originalMethod.apply(undefined, invocationArguments);
			if (returnedObject && returnedObject.then) {
				return returnedObject.then((value: any) => {
					cacheService.evict(...tags);
					return value;
				});
			} else {
				cacheService.evict(...tags);
				return returnedObject;
			}

		};
	});
}
