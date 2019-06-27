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
import {
	annotationService,
	contentCatalogUpdateEvictionTag,
	pageEvictionTag,
	rarelyChangingContent,
	userEvictionTag,
	CacheConfig,
	ContentCatalogRestService,
	IRestService,
	IRestServiceFactory,
	OperationContextRegistered
} from 'smarteditcommons/services';
import {coreAnnotationsHelper} from 'testhelpers';

describe("ContentCatalogRestService", () => {

	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
	let innerRestService: jasmine.SpyObj<IRestService<IBaseCatalogs>>;

	beforeEach(() => {

		coreAnnotationsHelper.init();

		innerRestService = jasmine.createSpyObj<IRestService<IBaseCatalogs>>('innerRestService', ['activateMetadata']);
		restServiceFactory = jasmine.createSpyObj<any>('restServiceFactory', ['get']);
		restServiceFactory.get.and.returnValue(innerRestService);

		new ContentCatalogRestService(restServiceFactory);

	});

	it("service has registered operation contexts for retry policies through OperationContextRegistered class annotation", () => {
		const decoratorObj = annotationService.getClassAnnotation(ContentCatalogRestService, OperationContextRegistered as (args?: any) => ClassDecorator);
		expect(decoratorObj).toEqual(['/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs', ['CMS', 'INTERACTIVE']]);
	});

	it("CacheConfig annotation", () => {
		const decoratorObj = annotationService.getClassAnnotation(ContentCatalogRestService, CacheConfig);
		expect(decoratorObj).toEqual([{actions: [rarelyChangingContent], tags: [userEvictionTag, pageEvictionTag, contentCatalogUpdateEvictionTag]}]);
	});
});