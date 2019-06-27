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
import {GatewayProxied} from 'smarteditcommons/services';
import {Page, Pageable, Payload} from 'smarteditcommons/dtos';
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';
/*
 * internal service to proxy calls from inner RESTService to the outer restServiceFactory and the 'real' IRestService
 */

/** @internal */
@GatewayProxied()
@SeInjectable()
export class DelegateRestService {

	delegateForVoid(methodName: string, params: string | Payload, uri: string, identifier: string, metadataActivated: boolean): angular.IPromise<void> {
		'proxyFunction';
		return null;
	}

	delegateForSingleInstance<T>(methodName: string, params: string | Payload, uri: string, identifier: string, metadataActivated: boolean): angular.IPromise<T> {
		'proxyFunction';
		return null;
	}

	delegateForArray<T>(methodName: string, params: string | Payload, uri: string, identifier: string, metadataActivated: boolean): angular.IPromise<T[]> {
		'proxyFunction';
		return null;
	}

	delegateForPage<T>(pageable: Pageable, uri: string, identifier: string, metadataActivated: boolean): angular.IPromise<Page<T>> {
		'proxyFunction';
		return null;
	}

}
