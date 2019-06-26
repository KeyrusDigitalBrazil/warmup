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
import {IRestService} from './IRestService';

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:restServiceFactory
 *
 * @description
 * A factory used to generate a REST service wrapper for a given resource URL, providing a means to perform HTTP
 * operations (GET, POST, etc) for the given resource.
 */
export interface IRestServiceFactory {

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:restServiceFactory#get<T>
	 * @methodOf smarteditServicesModule.interface:restServiceFactory
	 *
	 * @description
	 * A factory method used to create a REST service of type {@link smarteditServicesModule.interface:IRestService IRestService<T>}
	 * that points to the given resource URI. 
	 * The returned service wraps a $resource object. As opposed to a $resource, the REST services retrieved from the
	 * restServiceFactory can only take one object argument. The object argument will automatically be split
	 * into a parameter object and a payload object before they are delegated to the wrapped $resource object.
	 * If the domain is set, the domain is prepended to the given URI.
	 * 
	 * @param {String} uri The URI of the REST service to be retrieved.
	 * @param {String=} identifier An optional parameter. The name of the placeholder that is appended to the end
	 * of the URI if the name is not already provided as part of the URI. The default value is "identifier".
	 * <pre>
	 * 	if identifier is "resourceId" and uri is "resource/:resourceId/someValue", the target URI will remain the same.
	 * 	if identifier is "resourceId" and uri is "resource", the target URI will be "resource/:resourceId".
	 * </pre>
	 *
	 * @returns {IResourceService} A {@link smarteditServicesModule.interface:IResourceService IRestService} around a {@link https://docs.angularjs.org/api/ngResource/service/$resource $resource}
	 */
	get<T>(uri: string, identifier?: string): IRestService<T>;

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:restServiceFactory#setDomain
	 * @methodOf smarteditServicesModule.interface:restServiceFactory
	 *
	 * @description
	 * When working with multiple services that reference the same domain, it is best to only specify relative
	 * paths for the services and specify the context-wide domain in a separate location. The {@link
	 * smarteditServicesModule.interface:restServiceFactory#get get} method of the {@link
	 * smarteditServicesModule.interface:restServiceFactory restServiceFactory} will then prefix the specified service
	 * URIs with the domain and a forward slash.
	 *
	 * @param {String} domain The context-wide domain that all URIs will be prefixed with when services are
	 * created/when a service is created
	 *
	 * @deprecated since 6.7
	 */
	setDomain?(domain: string): void;


}