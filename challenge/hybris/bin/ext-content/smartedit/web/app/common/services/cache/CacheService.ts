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
import * as angular from 'angular';
import * as lo from 'lodash';
import {CacheAction} from './CacheAction';
import {EvictionTag} from './EvictionTag';
import {IMetadata} from './engine/interfaces';
import {
	CacheEngine,
	CrossFrameEventService,
	DefaultCacheTiming,
	ICacheItem,
	ICacheTiming,
	RarelyChangingContentName,
	SeInjectable
} from 'smarteditcommons/services';
import {Cloneable} from 'smarteditcommons/dtos';
import {FunctionsUtils} from "smarteditcommons";

export type ICachePredicate = (cacheActions: CacheAction[]) => boolean;

/** @internal */
interface IPredicateRegistry {
	test: ICachePredicate;
	cacheTiming: ICacheTiming;
}

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:CacheService
 * @description
 * Service to which the {@link smarteditServicesModule.object:@Cached @Cached} and {@link smarteditServicesModule.object:@InvalidateCache @InvalidateCache} annotations delegate to perform service method level caching.
 * It is not handled explicitly except for its evict method.
 */
@SeInjectable()
export class CacheService {

	private predicatesRegistry: IPredicateRegistry[] = [];
	private eventListeners: string[] = [];

	/** @internal */
	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private lodash: lo.LoDashStatic,
		private encode: (obj: any) => string,
		private functionsUtils: FunctionsUtils,
		private crossFrameEventService: CrossFrameEventService,
		private cacheEngine: CacheEngine) {
		this.registerDefaultCacheTimings();
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:CacheService#register
	 * @methodOf smarteditServicesModule.service:CacheService
	 * 
	 * @description
	 * Register a new predicate with it's associated cacheTiming.
	 * Each time the @Cache annotation is handled, the CacheService try to find a matching cacheTiming for the given cacheActions.
	 * 
	 * @param {ICachePredicate} test This function takes the cacheActions {@link smarteditServicesModule.object:CacheAction CacheAction} argument, and must return a Boolean that is true if the given cacheActions match the predicate.
	 * @param {ICacheTiming} cacheTiming This function is used to call setAge(item: ICacheItem<any>) on the cached item.
	 * 
	 * @return {CacheService} CacheService The CacheService instance.
	 * 
	 * @example
	 * ```ts
	 * export class CustomCacheTiming implements ICacheTiming {
	 * 	private expirationAge: number;
	 * 	private refreshAge: number;
	 *  constructor(expirationAge: number, refreshAge: number) {
	 * 		// The cached response is discarded if it is older than the expiration age.
	 * 		this.expirationAge = expirationAge;
	 * 		// maximum age for the cached response to be considered "fresh."
	 * 		this.refreshAge = refreshAge;
	 * 	}
	 * 	setAge(item: ICacheItem<any>): void {
	 * 		item.expirationAge = this.expirationAge;
	 * 		item.refreshAge = this.refreshAge;
	 * 	}
	 * 	};
	 * 	const customCacheTiming = new CustomCacheTiming(30 * 60000, 15 * 60000);
	 * 	const customContentPredicate: ICachePredicate = (cacheActions: CacheAction[]) => {
	 * 		return cacheActions.find((cacheAction) => cacheAction.name === 'CUSTOM_TAG') !== null;
	 * 	};
	 * this.register(customContentPredicate, customCacheTiming);
	 * ```
	 */
	public register(test: ICachePredicate, cacheTiming: ICacheTiming): CacheService {
		this.predicatesRegistry.unshift({
			test,
			cacheTiming
		});
		return this;
	}

	/**
	 * public method but only meant to be used by @Cache annotation
	 */
	public handle<T extends IMetadata>(service: any, methodName: string, preboundMethod: (...args: any[]) => T, invocationArguments: any[], cacheActions: CacheAction[], tags: EvictionTag[]): angular.IPromise<T> {

		const constructorName = this.functionsUtils.getInstanceConstructorName(service);
		const cachedItemId: string = window.btoa(constructorName + methodName) + this.encode(invocationArguments);

		const _item: ICacheItem<any> = this.cacheEngine.getItemById(cachedItemId);
		const item: ICacheItem<any> = _item || {
			id: cachedItemId,
			timestamp: new Date().getTime(),
			evictionTags: this.collectEventNamesFromTags(tags),
			cache: null,
			expirationAge: null,
			refreshAge: null
		};

		if (!_item) {
			const cacheTiming: ICacheTiming = this.findCacheTimingByCacheActions(cacheActions);
			if (!cacheTiming) {
				throw new Error('CacheService::handle - No predicate match.');
			}
			cacheTiming.setAge(item);

			this.cacheEngine.addItem(item, cacheTiming, preboundMethod.bind(undefined, ...invocationArguments));

			this.listenForEvictionByTags(tags);
		}

		return this.cacheEngine.handle(item);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:CacheService#evict
	 * @methodOf  smarteditServicesModule.service:CacheService
	 * @description
	 * Will evict the entire cache of all methods of all services referencing either directly or indirectly the given {@link smarteditServicesModule.object:EvictionTag EvictionTags}
	 * @param {...EvictionTag[]} evictionTags the {@link smarteditServicesModule.object:EvictionTag EvictionTags}
	 */
	public evict(...evictionTags: EvictionTag[]): void {
		const tags: string[] = this.collectEventNamesFromTags(evictionTags);
		this.cacheEngine.evict(...tags);
	}

	protected listenForEvictionByTags(tags: EvictionTag[]): void {
		this.collectEventNamesFromTags(tags).filter((eventId) => {
			return this.eventListeners.indexOf(eventId) === -1;
		}).forEach((eventId) => {
			this.$log.debug(`registering event listener ${eventId}`);
			this.eventListeners.push(eventId);
			this.crossFrameEventService.subscribe(eventId, (evt: string, data: Cloneable) => {
				this.$log.debug(`cleaning cache on event ${eventId}`);
				this.cacheEngine.evict(eventId);
				return this.$q.when<Cloneable>({});
			});
		});
	}

	protected collectEventNamesFromTags(tags: EvictionTag[]): string[] {
		if (tags && tags.length) {
			return this.lodash.union(...tags.map((t) => this.collectEventNamesFromTag(t)));
		} else {
			return [];
		}
	}

	protected collectEventNamesFromTag(tag: EvictionTag): string[] {
		return this.lodash.union([tag.event], ...(tag.relatedTags ? tag.relatedTags.map((t) => this.collectEventNamesFromTag(t)) : []));
	}

	protected findCacheTimingByCacheActions(cacheActions: CacheAction[]): ICacheTiming {
		const predicate: IPredicateRegistry = this.predicatesRegistry.find((cacheTimingPredicate) => cacheTimingPredicate.test(cacheActions));
		return predicate ? predicate.cacheTiming : null;
	}

	protected registerDefaultCacheTimings(): void {
		const defaultCacheTiming = new DefaultCacheTiming(24 * 60 * 60 * 1000, 12 * 60 * 60 * 1000);
		const rarelyChangingContentPredicate: ICachePredicate = (cacheActions: CacheAction[]) => {
			return cacheActions.find((cacheAction) => cacheAction.name === RarelyChangingContentName) !== null;
		};
		this.register(rarelyChangingContentPredicate, defaultCacheTiming);
	}
}
