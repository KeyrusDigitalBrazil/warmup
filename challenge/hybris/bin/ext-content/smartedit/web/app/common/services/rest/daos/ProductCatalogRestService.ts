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
import {IBaseCatalogs} from 'smarteditcommons/dtos/ICatalog';
import {rarelyChangingContent, userEvictionTag, CacheConfig} from "smarteditcommons/services/cache";
import {IRestServiceFactory} from "smarteditcommons/services/rest/IRestServiceFactory";
import {AbstractCachedRestService} from "smarteditcommons/services/rest/AbstractCachedRestService";
import {SeInjectable} from "smarteditcommons/services/dependencyInjection/di";

@CacheConfig({actions: [rarelyChangingContent], tags: [userEvictionTag]})
@SeInjectable()
export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {

	constructor(restServiceFactory: IRestServiceFactory) {
		super(restServiceFactory, '/cmssmarteditwebservices/v1/sites/:siteUID/productcatalogs');
	}
}