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

import {IRestService} from './../../common/services/rest/IRestService';
import {IRestServiceFactory} from './../../common/services/rest/IRestServiceFactory';
import {DelegateRestService} from './DelegateRestServiceInner';
import {RestService} from './RestService';
import {SeInjectable} from 'smarteditcommons';

/** @internal */
@SeInjectable()
export class RestServiceFactory implements IRestServiceFactory {

	constructor(private delegateRestService: DelegateRestService) {
	}

	get<T>(uri: string, identifier?: string): IRestService<T> {

		return new RestService<T>(this.delegateRestService, uri, identifier);
	}

}
