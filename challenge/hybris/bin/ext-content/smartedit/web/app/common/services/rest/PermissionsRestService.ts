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
import {IRestServiceFactory} from "smarteditcommons/services/rest/IRestServiceFactory";
import {SeInjectable} from "smarteditcommons/services/dependencyInjection/di";
import {IRestService} from "smarteditcommons/services/rest/IRestService";
import {IPermissionsRestServiceQueryData, IPermissionsRestServiceResult} from "smarteditcommons/dtos/IPermissionsDto";

@SeInjectable()
export class PermissionsRestService {

	private readonly URI = "/permissionswebservices/v1/permissions/principals/:user/global";

	private readonly resource: IRestService<IPermissionsRestServiceResult>;

	constructor(restServiceFactory: IRestServiceFactory) {
		this.resource = restServiceFactory.get<IPermissionsRestServiceResult>(this.URI);
	}

	get(queryData: IPermissionsRestServiceQueryData): angular.IPromise<IPermissionsRestServiceResult> {
		return this.resource.get(queryData as any).then((data: IPermissionsRestServiceResult) => {
			return {
				permissions: data.permissions
			};
		});
	}

}