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
import {Payload, TypedMap} from 'smarteditcommons/dtos';
import {ILanguage, ISite} from 'smarteditcommons/services';

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IExperience
 * @description
 * IExperience - Interface for experience information
 */
export interface IExperience extends Payload {
	catalogDescriptor: IExperienceCatalogDescriptor;
	siteDescriptor: ISite;
	productCatalogVersions: IExperienceCatalogVersion[];
	time: string;

	languageDescriptor?: ILanguage;
	pageId?: string;
	pageContext?: IExperiencePageContext;
}

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IExperienceCatalogDescriptor
 * @description
 * IExperienceCatalogDescriptor interface acts as a pointer to catalog and catalogVersion of the experience
 */
export interface IExperienceCatalogDescriptor extends Payload {
	active: boolean;
	catalogId: string;
	catalogVersion: string;
	catalogVersionUuid: string;
	name: TypedMap<string>;
	siteId: string;
}

export interface IExperienceCatalogVersion extends Payload {
	active: boolean;
	catalog: string;
	catalogName: TypedMap<string>;
	catalogVersion: string;
	uuid: string;
}

export interface IExperiencePageContext extends Payload {
	catalogId: string;
	catalogName: TypedMap<string>;
	catalogVersion: string;
	catalogVersionUuid: string;
	siteId: string;
	active: boolean;
}

export interface IDefaultExperienceParams {
	siteId: string;
	catalogId: string;
	catalogVersion: string;
	pageId?: string;
}

export interface IExperienceParams extends IDefaultExperienceParams {
	time?: string;
	productCatalogVersions?: string[];
	language?: string;
}
