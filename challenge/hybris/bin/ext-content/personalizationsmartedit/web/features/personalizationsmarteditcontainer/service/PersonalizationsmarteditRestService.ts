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
import {SeInjectable} from 'smarteditcommons';
import {PersonalizationsmarteditContextService} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter";
import * as angular from 'angular';

@SeInjectable()
export class PersonalizationsmarteditRestService {

	static readonly CUSTOMIZATIONS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizations";
	static readonly CUSTOMIZATION = PersonalizationsmarteditRestService.CUSTOMIZATIONS + "/:customizationCode";

	static readonly CUSTOMIZATION_PACKAGES = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizationpackages";
	static readonly CUSTOMIZATION_PACKAGE = PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGES + "/:customizationCode";

	static readonly ACTIONS_DETAILS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions";

	static readonly VARIATIONS = PersonalizationsmarteditRestService.CUSTOMIZATION + "/variations";
	static readonly VARIATION = PersonalizationsmarteditRestService.VARIATIONS + "/:variationCode";

	static readonly ACTIONS = PersonalizationsmarteditRestService.VARIATION + "/actions";
	static readonly ACTION = PersonalizationsmarteditRestService.ACTIONS + "/:actionId";

	static readonly CXCMSC_ACTIONS_FROM_VARIATIONS = "/personalizationwebservices/v1/query/cxcmscomponentsfromvariations";

	static readonly SEGMENTS = "/personalizationwebservices/v1/segments";

	static readonly CATALOGS = "/cmswebservices/v1/sites/:siteId/cmsitems";
	static readonly CATALOG = PersonalizationsmarteditRestService.CATALOGS + "/:itemUuid";

	static readonly ADD_CONTAINER = "/personalizationwebservices/v1/query/cxReplaceComponentWithContainer";

	static readonly COMPONENT_TYPES = '/cmswebservices/v1/types?category=COMPONENT';

	static readonly UPDATE_CUSTOMIZATION_RANK = "/personalizationwebservices/v1/query/cxUpdateCustomizationRank";
	static readonly CHECK_VERSION = "/personalizationwebservices/v1/query/cxCmsPageVersionCheck";

	static readonly VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS = "variations(active,actions,enabled,code,name,rank,status,catalog,catalogVersion)";

	static readonly FULL_FIELDS = "FULL";

	constructor(
		protected restServiceFactory: any,
		protected personalizationsmarteditUtils: any,
		protected $http: any,
		protected $q: any,
		protected yjQuery: any,
		protected personalizationsmarteditContextService: PersonalizationsmarteditContextService) {
	}

	extendRequestParamObjWithCatalogAwarePathVariables(requestParam: any, catalogAware?: any): any {
		catalogAware = catalogAware || {};
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const catalogAwareParams = {
			catalogId: catalogAware.catalog || experienceData.catalogDescriptor.catalogId,
			catalogVersion: catalogAware.catalogVersion || experienceData.catalogDescriptor.catalogVersion
		};
		requestParam = angular.extend(requestParam, catalogAwareParams);
		return requestParam;
	}

	extendRequestParamObjWithCustomizatonCode(requestParam: any, customizatiodCode: string): any {
		const customizationCodeParam = {
			customizationCode: customizatiodCode
		};
		requestParam = angular.extend(requestParam, customizationCodeParam);
		return requestParam;
	}

	extendRequestParamObjWithVariationCode(requestParam: any, variationCode: string): any {
		const param = {
			variationCode
		};
		requestParam = angular.extend(requestParam, param);
		return requestParam;
	}

	getParamsAction(oldComponentId: string, newComponentId: string, slotId: string, containerId: string, customizationId: string, variationId: string): any {
		const entries: any[] = [];
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "oldComponentId", oldComponentId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "newComponentId", newComponentId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "slotId", slotId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "containerId", containerId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variationId", variationId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customizationId", customizationId);
		return {
			params: {
				entry: entries
			}
		};
	}

	getPathVariablesObjForModifyingActionURI(customizationId: string, variationId: string, actionId: string, filter: any) {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		filter = filter || {};
		return {
			customizationCode: customizationId,
			variationCode: variationId,
			actionId,
			catalogId: filter.catalog || experienceData.catalogDescriptor.catalogId,
			catalogVersion: filter.catalogVersion || experienceData.catalogDescriptor.catalogVersion
		};
	}

	prepareURI(uri: string, pathVariables: any): any {
		return uri.replace(/((?:\:)(\w*)(?:\/))/g, function(match, p1, p2) {
			return pathVariables[p2] + "/";
		});
	}

	getParamsForCustomizations(filter: any): any {
		return {
			code: angular.isDefined(filter.code) ? filter.code : undefined,
			pageId: angular.isDefined(filter.pageId) ? filter.pageId : undefined,
			pageCatalogId: angular.isDefined(filter.pageCatalogId) ? filter.pageCatalogId : undefined,
			name: angular.isDefined(filter.name) ? filter.name : undefined,
			negatePageId: angular.isDefined(filter.negatePageId) ? filter.negatePageId : undefined,
			catalogs: angular.isDefined(filter.catalogs) ? filter.catalogs : undefined,
			statuses: angular.isDefined(filter.statuses) ? filter.statuses.join(',') : undefined
		};
	}

	getActionsDetails(filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ACTIONS_DETAILS);
		filter = this.extendRequestParamObjWithCatalogAwarePathVariables(filter);
		return restService.get(filter);
	}

	getCustomizations(filter: any): any {
		filter = filter || {};
		let requestParams: any = {};

		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATIONS);

		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);

		requestParams.pageSize = filter.currentSize || 10;
		requestParams.currentPage = filter.currentPage || 0;

		this.yjQuery.extend(requestParams, this.getParamsForCustomizations(filter));

		return restService.get(requestParams);
	}

	getComponenentsIdsForVariation(customizationId: string, variationId: string, catalog: string, catalogVersion: string): any {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;

		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CXCMSC_ACTIONS_FROM_VARIATIONS);
		const entries: any[] = [];
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", customizationId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variations", variationId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", catalog || experienceData.catalogDescriptor.catalogId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", catalogVersion || experienceData.catalogDescriptor.catalogVersion);
		const requestParams = {
			params: {
				entry: entries
			}
		};
		return restService.save(requestParams);
	}

	getCxCmsActionsOnPageForCustomization(customization: any, currentPage: number): any {
		const filter = {
			type: "CXCMSACTION",
			catalogs: "ALL",
			fields: "FULL",
			pageId: this.personalizationsmarteditContextService.getSeData().pageId,
			pageCatalogId: this.personalizationsmarteditContextService.getSeData().seExperienceData.pageContext.catalogId,
			customizationCode: customization.code || "",
			currentPage: currentPage || 0
		};

		return this.getActionsDetails(filter);
	}

	getSegments(filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.SEGMENTS);
		return restService.get(filter);
	}

	getCustomization(filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATION, "customizationCode");

		let requestParams = this.extendRequestParamObjWithCustomizatonCode({}, filter.code);
		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);

		return restService.get(requestParams);
	}

	createCustomization(customization: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGES);

		return restService.save(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
	}

	updateCustomization(customization: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATION, "customizationCode");
		customization.customizationCode = customization.code;
		return restService.update(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
	}

	updateCustomizationPackage(customization: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATION_PACKAGE, "customizationCode");
		customization.customizationCode = customization.code;
		return restService.update(this.extendRequestParamObjWithCatalogAwarePathVariables(customization));
	}

	deleteCustomization(customizationCode: string): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CUSTOMIZATION, "customizationCode");

		const requestParams = {
			customizationCode
		};

		return restService.remove(this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams));
	}

	getVariation(customizationCode: string, variationCode: string): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.VARIATION, "variationCode");

		let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
		requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

		return restService.get(requestParams);
	}

	editVariation(customizationCode: string, variation: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.VARIATION, "variationCode");

		variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
		variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);
		variation.variationCode = variation.code;
		return restService.update(variation);
	}

	deleteVariation(customizationCode: string, variationCode: string): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.VARIATION, "variationCode");

		let requestParams = this.extendRequestParamObjWithVariationCode({}, variationCode);
		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
		requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

		return restService.remove(requestParams);
	}

	createVariationForCustomization(customizationCode: string, variation: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.VARIATIONS);

		variation = this.extendRequestParamObjWithCatalogAwarePathVariables(variation);
		variation = this.extendRequestParamObjWithCustomizatonCode(variation, customizationCode);

		return restService.save(variation);
	}

	getVariationsForCustomization(customizationCode: string, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.VARIATIONS);

		let requestParams: any = {};
		const varForCustFilter = filter || {};

		requestParams = this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, varForCustFilter);
		requestParams = this.extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

		requestParams.fields = PersonalizationsmarteditRestService.VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS;

		const includeFullFields = typeof varForCustFilter.includeFullFields === "undefined" ? false : varForCustFilter.includeFullFields;

		if (includeFullFields) {
			requestParams.fields = PersonalizationsmarteditRestService.FULL_FIELDS;
		}

		return restService.get(requestParams);
	}

	replaceComponentWithContainer(componentId: string, slotId: string, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ADD_CONTAINER);
		const catalogParams = this.extendRequestParamObjWithCatalogAwarePathVariables({}, filter);
		const requestParams = this.getParamsAction(componentId, null, slotId, null, null, null);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "catalog", catalogParams.catalogId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "catalogVersion", catalogParams.catalogVersion);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "slotCatalog", filter.slotCatalog);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "oldComponentCatalog", filter.oldComponentCatalog);

		return restService.save(requestParams);
	}

	getActions(customizationId: string, variationId: string, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ACTIONS);
		const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);

		let requestParams = {
			fields: PersonalizationsmarteditRestService.FULL_FIELDS
		};
		requestParams = angular.extend(requestParams, pathVariables);

		return restService.get(requestParams);
	}

	createActions(customizationId: string, variationId: string, data: any, filter: any): any {

		const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
		const url = this.prepareURI(PersonalizationsmarteditRestService.ACTIONS, pathVariables);

		return this.$http({
			url,
			method: 'PATCH',
			data,
			headers: {
				"Content-Type": "application/json;charset=utf-8"
			}
		});
	}

	addActionToContainer(componentId: string, catalogId: string, containerId: string, customizationId: string, variationId: string, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ACTIONS);
		const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
		let requestParams = {
			type: "cxCmsActionData",
			containerId,
			componentId,
			componentCatalog: catalogId
		};
		requestParams = angular.extend(requestParams, pathVariables);
		return restService.save(requestParams);
	}

	editAction(customizationId: string, variationId: string, actionId: string, newComponentId: string, newComponentCatalog: any, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ACTION, "actionId");

		const requestParams = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);

		return restService.get(requestParams).then((actionInfo: any) => {
			actionInfo = angular.extend(actionInfo, requestParams);
			actionInfo.componentId = newComponentId;
			actionInfo.componentCatalog = newComponentCatalog;
			return restService.update(actionInfo);
		});
	}

	deleteAction(customizationId: string, variationId: string, actionId: string, filter: any): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.ACTION, "actionId");

		const requestParams = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);

		return restService.remove(requestParams);
	}

	deleteActions(customizationId: string, variationId: string, actionIds: string, filter: any): any {
		const pathVariables = this.getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
		const url = this.prepareURI(PersonalizationsmarteditRestService.ACTIONS, pathVariables);

		return this.$http({
			url,
			method: 'DELETE',
			data: actionIds,
			headers: {
				"Content-Type": "application/json;charset=utf-8"
			}
		});
	}

	getComponents(filter: any): any {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CATALOGS);
		let requestParams = {
			siteId: experienceData.siteDescriptor.uid
		};
		requestParams = angular.extend(requestParams, filter);

		return restService.get(this.extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter));
	}

	getComponent(itemUuid: string) {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CATALOG, "itemUuid");
		const requestParams = {
			itemUuid,
			siteId: experienceData.siteDescriptor.uid
		};

		return restService.get(requestParams);
	}

	getNewComponentTypes(): any {
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.COMPONENT_TYPES);
		return restService.get();
	}

	updateCustomizationRank(customizationId: string, icreaseValue: any) {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.UPDATE_CUSTOMIZATION_RANK);
		const entries: any[] = [];
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", customizationId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "increaseValue", icreaseValue);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", experienceData.catalogDescriptor.catalogId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", experienceData.catalogDescriptor.catalogVersion);
		const requestParams = {
			params: {
				entry: entries
			}
		};
		return restService.save(requestParams);
	}

	checkVersionConflict(versionId: string) {
		const experienceData = this.personalizationsmarteditContextService.getSeData().seExperienceData;
		const restService = this.restServiceFactory.get(PersonalizationsmarteditRestService.CHECK_VERSION);
		const entries: any[] = [];
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "versionId", versionId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", experienceData.catalogDescriptor.catalogId);
		this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", experienceData.catalogDescriptor.catalogVersion);
		const requestParams = {
			params: {
				entry: entries
			}
		};

		return restService.save(requestParams);
	}

}
