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
import * as lo from "lodash";
import {IRestServiceFactory, LanguageService, SeInjectable, TypedMap} from "smarteditcommons";
import {
	DropdownPopulatorInterface,
} from "./DropdownPopulatorInterface";
import {
	DropdownPopulatorFetchPageResponse,
	DropdownPopulatorItemPayload,
	DropdownPopulatorPagePayload,
	DropdownPopulatorPayload,
	GenericEditorOption
} from "smarteditcommons/components/genericEditor";

/**
 * @ngdoc service
 * @name dropdownPopulatorModule.service:uriDropdownPopulator
 * @description
 * implementation of {@link dropdownPopulatorModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
 * containing uri attribute.
 */
@SeInjectable()
export class UriDropdownPopulator extends DropdownPopulatorInterface {

	constructor(
		public lodash: lo.LoDashStatic,
		private $q: angular.IQService,
		private restServiceFactory: IRestServiceFactory,
		private getDataFromResponse: any,
		private getKeyHoldingDataFromResponse: any,
		public languageService: LanguageService
	) {
		super(lodash, languageService);
	}

	_buildQueryParams(dependsOn: string, model: any) {
		const queryParams = dependsOn.split(",").reduce((obj: TypedMap<any>, current: string) => {
			obj[current] = model[current];
			return obj;
		}, {});

		return queryParams;
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.service:uriDropdownPopulator#fetchAll
	 * @methodOf dropdownPopulatorModule.service:uriDropdownPopulator
	 *
	 * @description
	 * Implementation of the {@link dropdownPopulatorModule.DropdownPopulatorInterface#fetchAll DropdownPopulatorInterface.fetchAll} method
	 */
	fetchAll(payload: DropdownPopulatorPayload): angular.IPromise<GenericEditorOption[]> {
		let params;

		if (payload.field.dependsOn) {
			params = this._buildQueryParams(payload.field.dependsOn, payload.model);
		}

		return this.restServiceFactory.get<DropdownPopulatorPayload>(payload.field.uri).get(params).then((response: DropdownPopulatorPayload) => {
			const dataFromResponse = this.getDataFromResponse(response);
			const options = this.populateAttributes(dataFromResponse, payload.field.idAttribute, payload.field.labelAttributes);

			if (payload.search) {
				return this.search(options, payload.search);
			}

			return this.$q.when(options);
		});
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.service:uriDropdownPopulator#fetchPage
	 * @methodOf dropdownPopulatorModule.service:uriDropdownPopulator
	 *
	 * @description
	 * Implementation of the {@link dropdownPopulatorModule.DropdownPopulatorInterface#fetchPage DropdownPopulatorInterface.fetchPage} method
	 */
	fetchPage(payload: DropdownPopulatorPagePayload): angular.IPromise<DropdownPopulatorFetchPageResponse> {
		let params: TypedMap<any> = {};

		if (payload.field.dependsOn) {
			params = this._buildQueryParams(payload.field.dependsOn, payload.model);
		}

		params.pageSize = payload.pageSize;
		params.currentPage = payload.currentPage;
		params.mask = payload.search;

		if (payload.field.params) {
			this.lodash.extend(params, payload.field.params);
		}

		return this.restServiceFactory.get<DropdownPopulatorFetchPageResponse>(payload.field.uri).get(params).then((response: DropdownPopulatorFetchPageResponse) => {
			const key = this.getKeyHoldingDataFromResponse(response);
			response[key] = this.populateAttributes(response[key], payload.field.idAttribute, payload.field.labelAttributes);

			return this.$q.when(response);
		});
	}

	/**
	 * @ngdoc method
	 * @name dropdownPopulatorModule.service:uriDropdownPopulator#getItem
	 * @methodOf dropdownPopulatorModule.service:uriDropdownPopulator
	 *
	 * @description
	 * Implementation of the {@link dropdownPopulatorModule.DropdownPopulatorInterface#getItem DropdownPopulatorInterface.getItem} method
	 *
	 * @param {Object} payload The payload object containing the uri and other options
	 * @param {String} payload.id The id of the item to fetch
	 * @param {String} payload.field.uri The uri used to make a rest call to fetch data
	 * @param {String} [payload.field.dependsOn=null] A comma separated list of attributes to include from the model when building the request params
	 * @param {String} [payload.field.idAttribute=id] The name of the attribute to use when setting the id attribute
	 * @param {String} [payload.field.labelAttributes=label] A list of attributes to use when setting the label attribute
	 * @param {String} [payload.model=null] The model used when building query params on attributes defined in payload.field.dependsOn
	 *
	 * @returns {Promise} A promise that resolves to the option that was fetched
	 */
	getItem(payload: DropdownPopulatorItemPayload): angular.IPromise<GenericEditorOption> {
		return this.restServiceFactory.get<GenericEditorOption>(payload.field.uri).getById(payload.id).then((item) => {
			item = this.populateAttributes([item], payload.field.idAttribute, payload.field.labelAttributes)[0];

			return this.$q.when(item);
		});
	}
}
