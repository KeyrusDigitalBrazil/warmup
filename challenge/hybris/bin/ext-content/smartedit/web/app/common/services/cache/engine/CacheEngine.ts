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
import {ICacheItem, ICacheTiming, IMetadata} from "./interfaces";

/** @internal */
export interface ICacheItemRegistry {
	item: ICacheItem<any>;
	cacheTiming: ICacheTiming;
	completed: boolean;
	processing: boolean;
	defer: any;
	refresh<T>(): angular.IPromise<T>;
}

/** @internal */
/** @ngInject */
export class CacheEngine {
	private static readonly BACKGROUND_REFRESH_INTERVAL: number = 10000;
	private cachedItemsRegistry: ICacheItemRegistry[] = [];

	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService) {
		this.startBackgroundMonitoringJob();
	}

	public addItem(item: ICacheItem<any>, cacheTiming: ICacheTiming, refresh: <T>() => angular.IPromise<T>): void {
		if (this.getItemIndex(item) === -1) {
			this.cachedItemsRegistry.push({
				item,
				cacheTiming,
				refresh,
				completed: false,
				processing: false,
				defer: this.$q.defer()
			});
		} else {
			this.$log.warn(`CacheEngine - item already exist for id: ${item.id}`);
		}
	}

	public getItemById(id: string): ICacheItem<any> {
		const match = this.cachedItemsRegistry.find((obj) => obj.item.id === id);
		return match ? match.item : null;
	}

	public handle<T>(item: ICacheItem<any>): angular.IPromise<T> {
		const obj = this.cachedItemsRegistry[this.getItemIndex(item)];
		if (obj.completed && !this.hasExpired(item)) {
			obj.defer.resolve(item.cache);
		} else if (!obj.processing) {
			obj.processing = true;
			this.refreshCache(obj);
		}
		return obj.defer.promise;
	}

	public evict(...tags: string[]): void {
		tags.forEach((tag) => {
			this.cachedItemsRegistry
				.filter((obj) => obj.item.evictionTags.indexOf(tag) > -1)
				.forEach((obj) => this.cachedItemsRegistry.splice(this.getItemIndex(obj.item), 1));
		});
	}

	// regularly go though cache data and call prebound methods to refresh data when needed.
	protected startBackgroundMonitoringJob(): void {
		setInterval(() => {
			this.cachedItemsRegistry
				.filter((obj) => this.needRefresh(obj.item))
				.forEach((obj) => this.refreshCache(obj));
		}, CacheEngine.BACKGROUND_REFRESH_INTERVAL);
	}

	protected refreshCache<T>(obj: ICacheItemRegistry): void {
		obj.refresh().then((value: IMetadata) => {
			// TODO: read value.metadata to refresh expiry/refresh ages.
			obj.cacheTiming.setAge(obj.item);
			obj.item.cache = value;
			obj.item.timestamp = new Date().getTime();
			obj.completed = true;
			obj.processing = false;
			obj.defer.resolve(value);
		}, (e: any) => {
			this.$log.error(`CacheEngine - unable to refresh cache for id: ${obj.item.id}`, e);
			delete obj.item.cache;
			obj.defer.reject(e);
		});
	}

	private hasExpired(item: ICacheItem<any>): boolean {
		return (item.timestamp + item.expirationAge) <= new Date().getTime();
	}

	private needRefresh(item: ICacheItem<any>): boolean {
		return (item.timestamp + item.refreshAge) <= new Date().getTime();
	}

	private getItemIndex(item: ICacheItem<any>): number {
		return this.cachedItemsRegistry.findIndex((o) => o.item.id === item.id);
	}
}
