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
import {IRestService} from "./IRestService";
import {IRestServiceFactory} from "./IRestServiceFactory";
import {Cached, InvalidateCache} from 'smarteditcommons/services/cache';
import {Page, Pageable, Payload} from 'smarteditcommons/dtos';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:AbstractCachedRestService
 * 
 * @description
 * Base class to implement Cache enabled {@link smarteditServicesModule.interface:IRestService IRestServices}.
 * <br/>Implementing classes just need declare a class level {@link smarteditServicesModule.object:@CacheConfig @CacheConfig} annotation
 * with at least one {@link smarteditServicesModule.object:CacheAction CacheAction} and one {@link smarteditServicesModule.object:EvictionTag EvictionTag}.
 * <br/>Cache policies called by the set of {@link smarteditServicesModule.object:CacheAction CacheActions} will have access to
 * REST call response headers being added to the response under "headers" property.
 * <br/>Those headers are then stripped from the response.
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#64;CacheConfig({actions: [rarelyChangingContent], tags: [userEvictionTag]})
 * &#64;SeInjectable()
 * export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
 * 	constructor(restServiceFactory: IRestServiceFactory) {
 * 		super(restServiceFactory, '/productcatalogs');
 * 	}
 * } 
 * </pre>
 */
export abstract class AbstractCachedRestService<T> implements IRestService<T> {

	protected innerRestService: IRestService<T>;

	constructor(restServiceFactory: IRestServiceFactory, uri: string, identifier?: string) {

		this.innerRestService = restServiceFactory.get(uri, identifier);
		this.innerRestService.activateMetadata();

	}

	@StripResponseHeaders
	@Cached()
	getById(identifier: string): angular.IPromise<T> {
		return this.innerRestService.getById(identifier);
	}

	@StripResponseHeaders
	@Cached()
	get(searchParams: void | Payload): angular.IPromise<T> {
		return this.innerRestService.get(searchParams);
	}

	@StripResponseHeaders
	@Cached()
	query(searchParams: Payload): angular.IPromise<T[]> {
		return this.innerRestService.query(searchParams);
	}

	@StripResponseHeaders
	@Cached()
	page(searchParams: Pageable): angular.IPromise<Page<T>> {
		return this.innerRestService.page(searchParams);
	}

	@StripResponseHeaders
	@InvalidateCache()
	update(payload: Payload): angular.IPromise<T> {
		return this.innerRestService.update(payload);
	}

	@InvalidateCache()
	remove(payload: Payload): angular.IPromise<void> {
		return this.innerRestService.remove(payload);
	}

	@StripResponseHeaders
	save(payload: Payload): angular.IPromise<T> {
		return this.innerRestService.save(payload);
	}

}

function StripResponseHeaders(target: any, propertyName: string, descriptor: TypedPropertyDescriptor<(...x: any[]) => any>) {

	const originalMethod = descriptor.value;

	descriptor.value = function() {
		return originalMethod.apply(this, arguments).then((response: any) => {
			delete response.headers;
			return response;
		});
	};
}