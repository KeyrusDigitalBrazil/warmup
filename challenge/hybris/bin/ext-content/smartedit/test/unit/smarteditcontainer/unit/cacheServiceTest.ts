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
import * as lo from 'lodash';
import {
	Cached,
	CachedAnnotationFactory,
	CacheAction,
	CacheConfig,
	CacheConfigAnnotationFactory,
	CacheEngine,
	CacheService,
	CrossFrameEventService,
	EvictionTag,
	FunctionsUtils,
	InvalidateCache,
	InvalidateCacheAnnotationFactory,
	ICacheItem,
	RarelyChangingContentName,
	TypedMap
} from 'smarteditcommons';
import {promiseHelper, PromiseType} from 'testhelpers';

describe('cacheService', () => {

	const cacheAction = new CacheAction(RarelyChangingContentName);

	let cacheService: CacheService;

	let $q: angular.IQService;
	let $log: jasmine.SpyObj<angular.ILogService>;
	let lodash: lo.LoDashStatic;
	let encode: any;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
	let cacheEngine: jasmine.SpyObj<CacheEngine>;
	let service: any;
	let methodImplems: any;

	let cacheConfigEvictionTag: EvictionTag;
	let evictionTag0: EvictionTag;
	let evictionTag1: EvictionTag;
	let evictionTag2: EvictionTag;
	let groupedevictionTag: EvictionTag;
	let eventHandles: TypedMap<(eventId: string) => void>;

	const RARELY_CHANGING_CONTENT_EXPIRATION_AGE: number = 24 * 60 * 60 * 1000;
	const RARELY_CHANGING_CONTENT_REFRESH_AGE: number = 12 * 60 * 60 * 1000;

	beforeEach(function() {

		$q = promiseHelper.$q();
		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['debug']);
		$log.debug.and.callFake((message: any) => {
			// tslint:disable-next-line
			// console.info(message);
		});

		lodash = (window as any).smarteditLodash;
		encode = function(object: any) {
			return JSON.stringify(object);
		};

		crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['publish', 'subscribe']);
		eventHandles = {};
		crossFrameEventService.subscribe.and.callFake((eventId: string, handle: (eventId: string) => void) => {
			eventHandles[eventId] = handle;
		});

		cacheEngine = jasmine.createSpyObj<CacheEngine>('cacheEngine', ['addItem', 'getItemById', 'handle', 'evict']);
		cacheEngine.getItemById.and.callFake((id: string): ICacheItem<any> => {
			return null;
		});
		cacheEngine.handle.and.callFake((item: ICacheItem<any>) => {
			return $q.when(item.cache);
		});

		const functionsUtils = jasmine.createSpyObj<FunctionsUtils>('functionsUtils', ['getInstanceConstructorName']);
		functionsUtils.getInstanceConstructorName.and.callFake((instance: any) => instance.constructor.name);

		cacheService = new CacheService($q, $log, lodash, encode, functionsUtils, crossFrameEventService, cacheEngine);

		CacheConfigAnnotationFactory($log);
		CachedAnnotationFactory(cacheService);
		InvalidateCacheAnnotationFactory(cacheService);

		cacheConfigEvictionTag = {
			event: 'eventY'
		};

		evictionTag0 = {
			event: 'event0'
		};

		evictionTag1 = {
			event: 'event1',
			relatedTags: [evictionTag0]
		};

		evictionTag2 = {
			event: 'event2'
		};

		groupedevictionTag = {
			event: 'eventX',
			relatedTags: [evictionTag1, evictionTag2]
		};

		methodImplems = {
			method1(arg1: string, arg2: number): angular.IPromise<string> {
				return promiseHelper.buildPromise('method1Promise', PromiseType.RESOLVES, arg1 + arg2);
			},

			method2(arg1: string[]): angular.IPromise<string> {
				return promiseHelper.buildPromise('method2Promise', PromiseType.RESOLVES, arg1.join(" and "));
			}
		};

		@CacheConfig({tags: [cacheConfigEvictionTag]})
		class Service {

			@Cached({actions: [cacheAction], tags: [groupedevictionTag]})
			method1(arg1: string, arg2: number): angular.IPromise<string> {
				return methodImplems.method1(arg1, arg2);
			}

			@Cached({actions: [cacheAction], tags: [evictionTag2]})
			method2(arg1: string[]): angular.IPromise<string> {
				return methodImplems.method2(arg1);
			}
		}

		service = new Service();

		spyOn(methodImplems, 'method1').and.callThrough();
		spyOn(methodImplems, 'method2').and.callThrough();

	});

	let baseTime: Date;

	beforeEach(function() {
		baseTime = new Date(2222, 0, 1);
		jasmine.clock().uninstall();
		jasmine.clock().install();
		jasmine.clock().mockDate(baseTime);

		service.method1('fantastic', 4);
		service.method2(['apple', 'pears']);
	});

	afterEach(() => {
		jasmine.clock().uninstall();
	});

	it('Empty @Cached on a method of a non annotated class will throw exception', () => {
		expect(function() {

			// tslint:disable-next-line
			class SomeWronglyAnnotatedClass {

				@Cached()
				wronglyAnnotatedMethod() {
					return promiseHelper.buildPromise('wronglyAnnotatedMethod', PromiseType.RESOLVES);
				}

			}

			(new SomeWronglyAnnotatedClass()).wronglyAnnotatedMethod();

		}).toThrow(new Error("method wronglyAnnotatedMethod of SomeWronglyAnnotatedClass is @Cached annotated but no CacheAction is specified either through @Cached or through class level @CacheConfig annotation"));
	});

	it('Empty @InvalidateCache on a method of a non annotated class will throw exception', () => {
		expect(function() {

			// tslint:disable-next-line
			class SomeWronglyAnnotatedClass {

				@InvalidateCache()
				wronglyAnnotatedMethod() {
					return promiseHelper.buildPromise('wronglyAnnotatedMethod', PromiseType.RESOLVES);
				}

			}

			(new SomeWronglyAnnotatedClass()).wronglyAnnotatedMethod();

		}).toThrow(new Error("method wronglyAnnotatedMethod of SomeWronglyAnnotatedClass is @InvalidateCache annotated but no EvictionTag is specified either through @InvalidateCache or through class level @CacheConfig annotation"));
	});

	it('service method call should call cacheEngine with the expected itemId', () => {
		const expectedMethod1ItemId: string = window.btoa(service.constructor.name + 'method1') + encode(['fantastic', 4]);
		expect(cacheEngine.getItemById.calls.argsFor(0)[0]).toEqual(expectedMethod1ItemId);

		const expectedMethod2ItemId = window.btoa(service.constructor.name + 'method2') + encode([['apple', 'pears']]);
		expect(cacheEngine.getItemById.calls.argsFor(1)[0]).toEqual(expectedMethod2ItemId);
	});

	it('service method call should call cacheEngine.handle with the expected item object', () => {
		const id: string = window.btoa(service.constructor.name + 'method1') + encode(['fantastic', 4]);

		const expectedItem: ICacheItem<any> = {
			id,
			timestamp: baseTime.getTime(),
			evictionTags: ['eventX', 'event1', 'event0', 'event2', 'eventY'],
			cache: null,
			expirationAge: RARELY_CHANGING_CONTENT_EXPIRATION_AGE,
			refreshAge: RARELY_CHANGING_CONTENT_REFRESH_AGE,
		};

		const item: ICacheItem<any> = cacheEngine.handle.calls.argsFor(0)[0];

		expect(item).toEqual(jasmine.objectContaining(expectedItem));
	});

});