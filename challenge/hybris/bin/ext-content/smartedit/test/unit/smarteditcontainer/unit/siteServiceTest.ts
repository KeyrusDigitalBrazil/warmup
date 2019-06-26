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
import 'jasmine';
import * as lo from 'lodash';
import {annotationService, authorizationEvictionTag, rarelyChangingContent, Cached, IRestService, ISite, OperationContextRegistered} from 'smarteditcommons';
import {RestServiceFactory, SiteService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise, PromiseType} from 'testhelpers';

describe('siteService', () => {

	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

	let siteService: SiteService;
	const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', ['get']);
	const siteRestService: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>('siteRestService', ['get']);

	const SITES_RESOURCE_URI: string = 'some uri';

	const sitesDTO = {
		sites: [{
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-uk',
			uid: 'electronics-uk'
		}]
	};

	const sitesDTOByCatalogs = {
		sites: [{
			contentCatalogs: ['electronicsContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics',
			uid: 'electronics'
		}, {
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-uk',
			uid: 'electronics-uk'
		}, {
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-eu',
			uid: 'electronics-eu'
		}]
	};

	const sitesDTOPromise = promiseHelper.buildPromise<any>('sitesDTOPromise', PromiseType.RESOLVES, sitesDTO) as IExtensiblePromise<ISite[]>;
	const sitesDTOByCatalogsPromise = promiseHelper.buildPromise<any>('sitesDTOByCatalogsPromise', PromiseType.RESOLVES, sitesDTOByCatalogs) as IExtensiblePromise<ISite[]>;

	beforeEach(() => {
		siteRestService.get.calls.reset();

		coreAnnotationsHelper.init();

		siteRestService.get.and.callFake((arg: any) => {
			if (lodash.isEmpty(arg)) {
				return sitesDTOPromise;
			} else if (arg && arg.catalogIds && lodash.isEqual(arg.catalogIds, ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'].join(','))) {
				return sitesDTOByCatalogsPromise;
			}
			throw new Error("unexpected argument for siteRestService.get method: " + arg);
		});

		restServiceFactory.get.and.returnValue(siteRestService);

		siteService = new SiteService(restServiceFactory, SITES_RESOURCE_URI);
	});

	it('is initialized', () => {
		expect(restServiceFactory.get).toHaveBeenCalledWith(SITES_RESOURCE_URI);

		const decoratorObj = annotationService.getClassAnnotation(SiteService, OperationContextRegistered as (args?: any) => ClassDecorator);
		expect(decoratorObj).toEqual(['SITES_RESOURCE_URI', 'CMS']);
	});

	it('is calling getAccessibleSites method', () => {
		const promise = siteService.getAccessibleSites() as IExtensiblePromise<ISite[]>;
		expect(promise.value).toEqual(sitesDTO.sites);
		expect(siteRestService.get).toHaveBeenCalledWith({});
	});

	it('is calling getSites method', () => {
		const promise = siteService.getSites() as IExtensiblePromise<ISite[]>;
		expect(promise.value).toEqual(sitesDTOByCatalogs.sites);
		expect(siteRestService.get).toHaveBeenCalledWith({});
		expect(siteRestService.get).toHaveBeenCalledWith({catalogIds: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'].join(',')});
		expect(siteRestService.get.calls.count()).toEqual(2);
	});

	it('checks Cached annotation on getSites() method ', () => {
		const decoratorObj = annotationService.getMethodAnnotation(SiteService, 'getSites', Cached);
		expect(decoratorObj).toEqual(jasmine.objectContaining([{
			actions: [rarelyChangingContent],
			tags: [authorizationEvictionTag]
		}]));
	});

	it('is calling getSiteById method', () => {
		const uid = 'electronics';
		siteRestService.get.calls.reset();
		const promise = siteService.getSiteById(uid) as IExtensiblePromise<ISite>;
		expect(promise.value).toEqual(sitesDTOByCatalogs.sites.find((site) => site.uid === uid));
		expect(siteRestService.get).toHaveBeenCalled();
		expect(siteRestService.get.calls.count()).toEqual(2);
	});
});
