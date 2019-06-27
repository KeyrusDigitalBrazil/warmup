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
import {CacheEngine, DefaultCacheTiming, ICacheItem} from "smarteditcommons";
import {promiseHelper, IExtensiblePromise} from 'testhelpers';

describe('cacheEngine', () => {
	let cacheEngine: CacheEngine;
	const BACKGROUND_REFRESH_INTERVAL: number = 10000;

	let MOCK_ITEM: ICacheItem<{items: any[]}>;
	const MOCK_DEFAULT_CACHE_TIMING = new DefaultCacheTiming(30000, 15000);
	const MOCK_TAGS: string[] = ['TAG1', 'TAG2'];
	let baseTime: Date;

	let $q: angular.IQService;
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj<angular.ILogService>('$log', ['error', 'warn']);

	beforeEach(angular.mock.inject(() => {
		baseTime = new Date(2000, 0, 1);

		jasmine.clock().uninstall();
		jasmine.clock().install();
		jasmine.clock().mockDate(baseTime);

		$q = promiseHelper.$q();

		cacheEngine = new CacheEngine($q, $log);

		MOCK_ITEM = {
			cache: {
				items: [
					1234, 'any_value', {id: 1}
				]
			},
			expirationAge: 60000,
			evictionTags: MOCK_TAGS,
			id: '==itemId1==',
			refreshAge: 30000,
			timestamp: baseTime.getTime(),
		};
	}));

	afterEach(() => {
		jasmine.clock().uninstall();
	});

	it('should be able to store and get an item', () => {
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));

		expect(cacheEngine.getItemById(MOCK_ITEM.id)).toEqual(MOCK_ITEM);
	});

	it('should not store the same item twice', () => {
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));

		expect($log.warn).toHaveBeenCalledWith(`CacheEngine - item already exist for id: ${MOCK_ITEM.id}`);
	});

	it('GIVEN an item is handled twice and it\'s cache is not expired, THEN it should return the cache without refreshing it', () => {
		const refresh: jasmine.Spy = jasmine.createSpy('refresh').and.returnValue($q.when(null));

		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

		// first handle, will cache the value
		const promise1 = cacheEngine.handle(MOCK_ITEM) as IExtensiblePromise<any>;

		const promise2 = cacheEngine.handle(MOCK_ITEM) as IExtensiblePromise<any>;

		expect(promise1.value).toEqual(MOCK_ITEM.cache);
		expect(promise2.value).toEqual(MOCK_ITEM.cache);
		expect(refresh).toHaveBeenCalledTimes(1);
	});

	it('GIVEN a item does not have a cache and is handled twice, THEN it should refresh the item only once AND return the expected cache', () => {
		const expectedReturnCache: string[] = [
			'item 1',
			'item 2',
		];
		const MOCK_ITEM_NO_CACHE: ICacheItem<any> = {
			cache: null,
			expirationAge: 60000,
			evictionTags: [],
			id: '~~itemId~~',
			refreshAge: 30000,
			timestamp: baseTime.getTime(),
		};

		const refresh: jasmine.Spy = jasmine.createSpy('refresh').and.returnValue($q.when(expectedReturnCache));

		cacheEngine.addItem(MOCK_ITEM_NO_CACHE, MOCK_DEFAULT_CACHE_TIMING, refresh);

		const promise1 = cacheEngine.handle(MOCK_ITEM_NO_CACHE) as IExtensiblePromise<any>;
		const promise2 = cacheEngine.handle(MOCK_ITEM_NO_CACHE) as IExtensiblePromise<any>;

		expect(promise1.value).toEqual(expectedReturnCache);
		expect(promise2.value).toEqual(expectedReturnCache);
		expect(refresh).toHaveBeenCalledTimes(1);
		expect(MOCK_ITEM_NO_CACHE.cache).toEqual(expectedReturnCache);
	});

	it('GIVEN an item has an expired cache, THEN it should refresh the item AND return the expected cache', () => {
		const EXPIRATION_AGE: number = 60000;

		jasmine.clock().tick(EXPIRATION_AGE);

		const expectedReturnCache: string[] = [
			'item 1',
			'item 2',
		];
		const ANY_MOCK_ITEM: ICacheItem<{randomValue: string}> = {
			cache: {
				randomValue: '4 is a guaranteed random value'
			},
			expirationAge: EXPIRATION_AGE,
			evictionTags: [],
			id: 'skidimarink ❤',
			refreshAge: 30000,
			timestamp: baseTime.getTime(),
		};

		const refresh: jasmine.Spy = jasmine.createSpy('refresh').and.returnValue($q.when(expectedReturnCache));

		cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

		const promise = cacheEngine.handle(ANY_MOCK_ITEM) as IExtensiblePromise<any>;

		expect(promise.value).toEqual(expectedReturnCache);
		expect(refresh).toHaveBeenCalled();
	});

	it('should be able to evict an item that match a tag', () => {
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));

		cacheEngine.evict(...[MOCK_TAGS[0]]);

		expect(cacheEngine.getItemById(MOCK_ITEM.id)).toBeNull();
	});

	it('should be able to evict an item by tags', () => {
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));

		cacheEngine.evict(...MOCK_TAGS);

		expect(cacheEngine.getItemById(MOCK_ITEM.id)).toBeNull();
	});

	it('should not evict an item when tags do not match', () => {
		cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => $q.when({} as any));

		cacheEngine.evict(...['ANY_TAG', 'ANOTHER_TAG']);

		expect(cacheEngine.getItemById(MOCK_ITEM.id)).toEqual(MOCK_ITEM);
	});

	it('should automatically refresh an item when required', () => {
		const REFRESH_AGE = 10000;
		const expectedReturnCache: {key: string} = {
			key: 'ANY_REFRESHED_DATA'
		};

		jasmine.clock().tick(REFRESH_AGE);

		const ANY_MOCK_ITEM: ICacheItem<{randomValue: string}> = {
			cache: {
				randomValue: 'random value...'
			},
			expirationAge: REFRESH_AGE * 2,
			evictionTags: [],
			id: 'ANY_ID',
			refreshAge: REFRESH_AGE,
			timestamp: baseTime.getTime(),
		};

		const refresh: jasmine.Spy = jasmine.createSpy('refresh').and.returnValue($q.when(expectedReturnCache));
		spyOn(MOCK_DEFAULT_CACHE_TIMING, 'setAge');

		cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

		jasmine.clock().tick(BACKGROUND_REFRESH_INTERVAL);

		expect(refresh).toHaveBeenCalled();
		expect(MOCK_DEFAULT_CACHE_TIMING.setAge).toHaveBeenCalledWith(ANY_MOCK_ITEM);
		expect(cacheEngine.getItemById(ANY_MOCK_ITEM.id).cache).toEqual(expectedReturnCache);
		expect(ANY_MOCK_ITEM.timestamp).toEqual(new Date().getTime());
	});

	it('GIVEN an item is automatically refreshed, THEN it should log an Error if the refresh action fail', () => {
		const REFRESH_AGE = 10000;

		jasmine.clock().tick(REFRESH_AGE);

		const error = 'error message here';
		const ANY_MOCK_ITEM: ICacheItem<{randomValue: string}> = {
			cache: {
				randomValue: 'random value...'
			},
			expirationAge: REFRESH_AGE * 2,
			evictionTags: [],
			id: 'ANY_ID',
			refreshAge: REFRESH_AGE,
			timestamp: baseTime.getTime(),
		};

		const refresh: jasmine.Spy = jasmine.createSpy('refresh').and.returnValue($q.reject(error));

		cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

		jasmine.clock().tick(BACKGROUND_REFRESH_INTERVAL);

		expect(refresh).toHaveBeenCalled();
		expect($log.error).toHaveBeenCalledWith(`CacheEngine - unable to refresh cache for id: ${ANY_MOCK_ITEM.id}`, `${error}`);
		expect(ANY_MOCK_ITEM.cache).toBeUndefined();
	});

});