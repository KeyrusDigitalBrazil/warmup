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
import {SeInjectable, SeValueProvider} from 'smarteditcommons';
import {PersonalizationsmarteditContextService} from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import * as angular from 'angular';

export const ACTIONS_DETAILS_PROVIDER: SeValueProvider = {
	provide: 'ACTIONS_DETAILS',
	useValue: "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions"

};

@SeInjectable()
export class PersonalizationsmarteditRestService {

	constructor(
		protected restServiceFactory: any,
		protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
		protected ACTIONS_DETAILS: string) {
	}

	extendRequestParamObjWithCatalogAwarePathVariables(requestParam: any, catalogAware: any): any {
		catalogAware = catalogAware || {};
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const catalogAwareParams = {
			catalogId: catalogAware.catalog || experienceData.catalogDescriptor.catalogId,
			catalogVersion: catalogAware.catalogVersion || experienceData.catalogDescriptor.catalogVersion
		};
		requestParam = angular.extend(requestParam, catalogAwareParams);
		return requestParam;
	}

	getCxCmsAllActionsForContainer(containerId: string, filter: any): any {
		let requestParams: any;
		filter = filter || {};

		const restService = this.restServiceFactory.get(this.ACTIONS_DETAILS);
		requestParams = {
			type: "CXCMSACTION",
			customizationStatus: "ENABLED",
			variationStatus: "ENABLED",
			catalogs: "ALL",
			needsTotal: true,
			containerId,
			pageSize: filter.currentSize || 25,
			currentPage: filter.currentPage || 0
		};

		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, undefined);

		return restService.get(requestParams);
	}

}
