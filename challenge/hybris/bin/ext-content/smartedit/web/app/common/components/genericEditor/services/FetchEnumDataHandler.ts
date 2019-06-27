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
import {IFetchDataHandler} from "./IFetchDataHandler";
import {IRestService, IRestServiceFactory, Payload, SeInjectable, TypedMap} from "smarteditcommons";
import {GenericEditorField} from "smarteditcommons/components/genericEditor";

/* @internal  */
@SeInjectable()
export class FetchEnumDataHandler implements IFetchDataHandler {

	public static resetForTests() {
		FetchEnumDataHandler.cache = {};
	}

	private static cache: TypedMap<any> = {};

	private restServiceForEnum: IRestService<Payload>;

	constructor(
		private $q: angular.IQService,
		private restServiceFactory: IRestServiceFactory,
		private isBlank: (value: any) => boolean,
		private ENUM_RESOURCE_URI: string
	) {
		this.restServiceForEnum = this.restServiceFactory.get<Payload>(this.ENUM_RESOURCE_URI);
	}

	findByMask(field: GenericEditorField, search?: string): angular.IPromise<string[]> {
		return (FetchEnumDataHandler.cache[field.cmsStructureEnumType] ? this.$q.when(FetchEnumDataHandler.cache[field.cmsStructureEnumType]) : this.restServiceForEnum.get({
			enumClass: field.cmsStructureEnumType
		})).then((response) => {
			FetchEnumDataHandler.cache[field.cmsStructureEnumType] = response;
			return FetchEnumDataHandler.cache[field.cmsStructureEnumType].enums.filter((element: Payload) => {
				return this.isBlank(search) || (element.label as string).toUpperCase().indexOf(search.toUpperCase()) > -1;
			});
		});
	}

	getById(field: GenericEditorField, identifier: string): angular.IPromise<string> {
		return null;
	}

}
