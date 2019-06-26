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
import {
	IExtensibleResourceClass,
	IRestService,
	IRestServiceFactory,
	Payload,
	SeInjectable,
	TypedMap
} from 'smarteditcommons';
import IResourceArray = angular.resource.IResourceArray;
import IResource = angular.resource.IResource;
import IPromise = angular.IPromise;

/** @internal */
@SeInjectable()
export class RestServiceFactory implements IRestServiceFactory {

	private DOMAIN: string = null;
	private IDENTIFIER: string = "identifier";
	private serviceMap: TypedMap<IRestService<any>> = {};
	private methodMapFactory: () => angular.resource.IActionHash;

	constructor(
		private $q: angular.IQService,
		private $resource: angular.resource.IResourceService,
		private lodash: lo.LoDashStatic
	) {

		const headers: Payload = {
			'x-requested-with': 'Angular'
		};
		const nocacheheader: Payload = lodash.cloneDeep(headers);
		nocacheheader.Pragma = 'no-cache';


		function addMetadataToResponse(data: string, headersGetter: angular.IHttpHeadersGetter): JSON {
			let deserialized: any;

			if (typeof data === 'string') {
				try {
					deserialized = !lodash.isEmpty(data) ? JSON.parse(data) : {};
				} catch (e) {
					deserialized = {response: data};
				}
			} else {
				deserialized = data;
			}

			// here "this" represents the service wrapper
			if ((this as any).metadataActivated) {
				deserialized.headers = headersGetter();
			}
			return deserialized;
		}

		this.methodMapFactory = function() {
			return {
				getById: {
					method: 'GET',
					params: {},
					isArray: false,
					cache: false,
					headers: nocacheheader,
					transformResponse: addMetadataToResponse.bind(this)
				},
				get: {
					method: 'GET',
					params: {},
					isArray: false,
					cache: false,
					headers: nocacheheader,
					transformResponse: addMetadataToResponse.bind(this)
				},
				query: {
					method: 'GET',
					params: {},
					isArray: true,
					cache: false,
					headers: nocacheheader,
					transformResponse: addMetadataToResponse.bind(this)
				},
				page: {
					method: 'GET',
					params: {},
					isArray: false, // due to spring Page
					cache: false,
					headers: nocacheheader,
					transformResponse: addMetadataToResponse.bind(this)
				},
				update: {
					method: 'PUT',
					cache: false,
					headers
				},
				save: {
					method: 'POST',
					cache: false,
					headers
				},
				remove: {
					method: 'DELETE',
					cache: false,
					headers
				}
			} as angular.resource.IActionHash;
		};
	}

	setDomain(DOMAIN: string): void {

		this.DOMAIN = DOMAIN;

	}

	get<T>(uri: string, identifier?: string): IRestService<T> {

		if (!identifier) {
			identifier = this.IDENTIFIER;
		}
		const wrappedService: IRestService<T> = this.serviceMap[uri + identifier];

		if (typeof wrappedService !== 'undefined') {
			return wrappedService;
		} else {
			let initialURI = '';
			if (/^https?\:\/\//.test(uri) || /^\//.test(uri)) {
				initialURI = uri;
			} else {
				initialURI = this.lodash.isEmpty(this.DOMAIN) ? uri : this.DOMAIN + '/' + uri;
			}

			let finalURI = initialURI;
			if (finalURI.indexOf(':' + identifier) === -1) {
				finalURI += '/:' + identifier;
			}

			const wrapper = {

				getMethodForVoid(name: string): (...params: any[]) => IPromise<void> {
					return this[name];
				},
				getMethodForSingleInstance(name: string): (...params: any[]) => IPromise<T> {
					return this[name];
				},
				getMethodForArray(name: string): (...params: any[]) => IPromise<T[]> {
					return this[name];
				},
				activateMetadata(): void {
					// will activate response headers appending
					this.metadataActivated = true;
				}
			} as any;

			const methodMap = this.methodMapFactory.bind(wrapper)();

			const service = this.$resource<T>(finalURI, {}, methodMap) as IExtensibleResourceClass<T>;

			this.lodash.forEach(methodMap, (actionDescriptor: angular.resource.IActionDescriptor, methodName: string) => {
				if (methodName === 'getById') {
					wrapper.getById = this.wrapGetByIdMethod.bind(undefined, service, identifier);
				} else {
					wrapper[methodName] = this.wrapMethodHavingPayload.bind(this, service, methodName, actionDescriptor, finalURI, identifier);
				}
			});

			this.serviceMap[uri + identifier] = wrapper;

			return (wrapper as IRestService<T>);
		}
	}

	private wrapGetByIdMethod<T>(service: IExtensibleResourceClass<IResource<T>>, identifier: string, identifierValue: string): IPromise<T> | IPromise<IResourceArray<IResource<T>>> {
		// payload is the actual identifier value
		const params = {} as Payload;
		params[identifier] = identifierValue;
		const payload = {} as Payload;
		return service.getById(params, payload).$promise;
	}

	private wrapMethodHavingPayload<T>(service: IExtensibleResourceClass<IResource<T>>, methodName: string, actionDescriptor: angular.resource.IActionDescriptor, finalURI: string, identifier: string, payload: Payload = {}): IPromise<T> | IPromise<IResourceArray<IResource<T>>> {

		// only keep params to be found in the URI or query params
		let params: {[index: string]: any} = typeof payload === 'object' ? Object.keys(payload).reduce(function(prev: Payload, next: string) {
			if (new RegExp(":" + next + "\/").test(finalURI) || new RegExp(":" + next + "$").test(finalURI) || new RegExp(":" + next + "&").test(finalURI)) {
				prev[next] = payload[next];
			}
			return prev;
		}, {}) : {};

		if (actionDescriptor.method === 'PUT' || actionDescriptor.method === 'DELETE') {
			if (!payload[identifier]) {
				return this.$q.reject("no data was found under the " + identifier + " field of object " + JSON.stringify(payload) + ", it is necessary for update and remove operations");
			}
			params[identifier] = payload[identifier];
			if (actionDescriptor.method === 'DELETE') {
				payload = {} as Payload;
			}
		}
		if (actionDescriptor.method === 'GET') {
			// params and payload are inverted
			params = this.lodash.cloneDeep(payload);
			payload = {} as Payload;
		}
		return service[methodName](params, payload).$promise;
	}
}
