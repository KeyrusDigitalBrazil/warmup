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
import {IHomepageVersions, ISite, Payload, TypedMap} from 'smarteditcommons';

/** from Backend */
export interface IBaseCatalogVersion extends Payload {
	active: boolean;
	pageDisplayConditions: {
		options: {
			id: string;
			label: string;
		}[];
		typecode: string;
	}[];
	uuid: string;
	version: string;
	thumbnailUrl?: string;
}

export interface ICatalogVersion extends IBaseCatalogVersion {
	name?: {[index: string]: string};
	catalogId?: string;
	catalogName?: TypedMap<string>;
	siteDescriptor?: ISite;
	homepage?: IHomepageVersions;
}

export interface IBaseCatalog {
	catalogId: string;
	versions: IBaseCatalogVersion[];
	name?: TypedMap<string>;
}

export interface ICatalog {
	catalogId: string;
	versions: ICatalogVersion[];
	name?: TypedMap<string>;
}
export interface IBaseCatalogs {
	catalogs: IBaseCatalog[];
}

export interface ICatalogs {
	catalogs: ICatalog[];
}
