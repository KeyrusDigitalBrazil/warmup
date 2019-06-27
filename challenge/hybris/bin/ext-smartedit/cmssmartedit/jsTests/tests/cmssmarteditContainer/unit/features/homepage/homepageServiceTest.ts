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
import {
	ICatalog,
	ICatalogService,
	ICatalogVersion,
	IUriContext,
	IYEventMessageData,
	SystemEventService
} from 'smarteditcommons';
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';
import {
	promiseHelper,
	// IExtensiblePromise
} from 'testhelpers';
import {
	CatalogHomepageDetailsStatus,
	HomepageService,
	HomepageType
} from 'cmssmarteditcontainer/services';

import {homepageServiceTestData} from './homepageServiceTestData';

import * as angular from 'angular';
import 'jasmine';

describe('homepageService', () => {

	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const eventService: jasmine.SpyObj<SystemEventService> = jasmine.createSpyObj('eventService', ['publish']);

	const MOCK_EVENT_ID_SHOW = 'MOCK_EVENT_ID_SHOW';
	const MOCK_EVENT_ID_HIDE = 'MOCK_EVENT_ID_HIDE';

	let catalogService: jasmine.SpyObj<ICatalogService>;
	let homepageService: HomepageService;

	const uriContexts = {
		electronicsStaged: {
			CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
			CURRENT_CONTEXT_CATALOG: 'electronicsContentCatalog',
			CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
		},
		euStaged: {
			CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
			CURRENT_CONTEXT_CATALOG: 'electronics-euContentCatalog',
			CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
		},
		ukStaged: {
			CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
			CURRENT_CONTEXT_CATALOG: 'electronics-ukContentCatalog',
			CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
		},
		frStaged: {
			CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
			CURRENT_CONTEXT_CATALOG: 'electronics-frContentCatalog',
			CURRENT_CONTEXT_CATALOG_VERSION: 'Staged'
		},
		noHomepageOnline: {
			CURRENT_CONTEXT_SITE_ID: 'electronicsSite',
			CURRENT_CONTEXT_CATALOG: 'electronics-noHomepage',
			CURRENT_CONTEXT_CATALOG_VERSION: 'Online'
		}
	};


	beforeEach(() => {
		catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', ['getContentCatalogVersion', 'getCatalogVersionByUuid']);
		catalogService.getContentCatalogVersion.and.callFake((urlContext: IUriContext) => {
			const matchingCatalog = homepageServiceTestData.catalogs.find((catalog: ICatalog) => catalog.catalogId === urlContext.CURRENT_CONTEXT_CATALOG);
			if (!matchingCatalog) {
				throw Error(`HomepageServiceTest - no catalog found with ID ${urlContext.CURRENT_CONTEXT_CATALOG}`);
			}
			const matchingCatalogVersion = matchingCatalog.versions.find((catalogVersion: ICatalogVersion) => catalogVersion.version === urlContext.CURRENT_CONTEXT_CATALOG_VERSION);
			if (matchingCatalogVersion) {
				return $q.when(matchingCatalogVersion);
			}
			throw Error(`HomepageServiceTest - version not found for getContentCatalogVersion(${urlContext})`);
		});
		catalogService.getCatalogVersionByUuid.and.callFake((uuid: string) => {
			let returnCatalogVersion;
			homepageServiceTestData.catalogs.forEach((catalog: ICatalog) => {
				const found = catalog.versions.find((catalogVersion: ICatalogVersion) => catalogVersion.uuid === uuid);
				if (found) {
					returnCatalogVersion = found;
				}
			});
			if (returnCatalogVersion) {
				return $q.when(returnCatalogVersion);
			}
			throw Error(`HomepageServiceTest - version not found for getCatalogVersionByUuid(${uuid})`);
		});
		homepageService = new HomepageService($q, catalogService, eventService, MOCK_EVENT_ID_HIDE, MOCK_EVENT_ID_SHOW, 'CURRENT_CONTEXT_CATALOG');
	});

	it('Send show replace parent homepage info event', () => {
		const data: IYEventMessageData = {
			description: "bla",
			title: "bla"
		};
		homepageService.sendEventShowReplaceParentHomePageInfo(data);
		expect(eventService.publish).toHaveBeenCalledWith(MOCK_EVENT_ID_SHOW, data);
	});

	it('Send hdie replace parent homepage info event', () => {
		const data: IYEventMessageData = {
			description: "bla",
			title: "bla"
		};
		homepageService.sendEventHideReplaceParentHomePageInfo(data);
		expect(eventService.publish).toHaveBeenCalledWith(MOCK_EVENT_ID_HIDE, data);
	});

	it('getHomepageType - CURRENT', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Staged',
			uid: 'cmsitem_00003001'
		} as ICMSPage;
		homepageService.getHomepageType(page, uriContexts.ukStaged).then((type: HomepageType) => {
			expect(type).toBe(HomepageType.CURRENT);
			done();
		});
	});

	it('getHomepageType - OLD', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Online',
			uid: 'homepage-uk'
		} as ICMSPage;
		homepageService.getHomepageType(page, uriContexts.ukStaged).then((type: HomepageType) => {
			expect(type).toBe(HomepageType.OLD);
			done();
		});
	});

	it('getHomepageType - FALLBACKS', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronicsContentCatalog/Online',
			uid: 'homepage'
		} as ICMSPage;
		homepageService.getHomepageType(page, uriContexts.euStaged).then((type: HomepageType) => {
			expect(type).toBe(HomepageType.FALLBACK);
			done();
		});
	});

	it('isCurrentHomepage - true', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Staged',
			uid: 'cmsitem_00003001'
		} as ICMSPage;
		spyOn(homepageService, 'getHomepageType').and.callThrough();
		homepageService.isCurrentHomepage(page, uriContexts.ukStaged).then((result: boolean) => {
			expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
			expect(result).toBe(true);
			done();
		});
	});

	it('isOldHomepage - false', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Staged',
			uid: 'homepage'
		} as ICMSPage;
		spyOn(homepageService, 'getHomepageType').and.callThrough();
		homepageService.isOldHomepage(page, uriContexts.ukStaged).then((result: boolean) => {
			expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
			expect(result).toBe(false);
			done();
		});
	});

	it('isOldHomepage - true', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Online',
			uid: 'homepage-uk'
		} as ICMSPage;
		spyOn(homepageService, 'getHomepageType').and.callThrough();
		homepageService.isOldHomepage(page, uriContexts.ukStaged).then((result: boolean) => {
			expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
			expect(result).toBe(true);
			done();
		});
	});

	it('isCurrentHomepage - false', (done) => {
		const page: ICMSPage = {
			catalogVersion: 'electronics-ukContentCatalog/Staged',
			uid: 'homepage'
		} as ICMSPage;
		spyOn(homepageService, 'getHomepageType').and.callThrough();
		homepageService.isCurrentHomepage(page, uriContexts.ukStaged).then((result: boolean) => {
			expect(homepageService.getHomepageType).toHaveBeenCalledWith(page, uriContexts.ukStaged);
			expect(result).toBe(false);
			done();
		});
	});

	it('hasFallbackHomePage - true', (done) => {
		homepageService.hasFallbackHomePage(uriContexts.ukStaged).then((result: boolean) => {
			expect(result).toBe(true);
			done();
		});
	});

	it('hasFallbackHomePage - false', (done) => {
		homepageService.hasFallbackHomePage(uriContexts.electronicsStaged).then((result: boolean) => {
			expect(result).toBe(false);
			done();
		});
	});

	describe('getHomepageDetailsForContext', () => {

		const errMsg = "some error message";

		it('Handles failure - getCatalogVersionByUuid', () => {
			catalogService.getCatalogVersionByUuid.and.returnValue($q.reject(errMsg));
			expect(homepageService.getHomepageDetailsForContext(uriContexts.ukStaged)).toBeRejectedWithData(errMsg);
		});

		it('Handles failure - getContentCatalogVersion', () => {
			catalogService.getContentCatalogVersion.and.returnValue($q.reject(errMsg));
			expect(homepageService.getHomepageDetailsForContext(uriContexts.ukStaged)).toBeRejectedWithData(errMsg);
		});

		it('NO homepage', () => {
			expect(homepageService.getHomepageDetailsForContext(uriContexts.noHomepageOnline)).toBeResolvedWithData({
				status: CatalogHomepageDetailsStatus.NO_HOMEPAGE
			});
		});

		it('Is a PARENT homepage', () => {
			// TARGET IS FR STAGED, PARENT SHOULD BE EU ONLINE
			expect(homepageService.getHomepageDetailsForContext(uriContexts.frStaged)).toBeResolvedWithData({
				status: 'PARENT',
				parentCatalogName: {
					en: 'Electronics Content Catalog EU',
					de: 'Elektronikkatalog EU',
					ja: 'エレクトロニクス コンテンツ カタログ EU',
					zh: '电子产品内容目录 EU'
				},
				parentCatalogVersion: 'Online',
				targetCatalogName: {
					en: 'Electronics Content Catalog FR'
				},
				targetCatalogVersion: 'Staged'
			});
		});

		it('Is a LOCAL homepage', () => {
			// TARGET IS FR STAGED, PARENT SHOULD BE EU ONLINE
			expect(homepageService.getHomepageDetailsForContext(uriContexts.ukStaged)).toBeResolvedWithData({
				status: 'LOCAL',
				currentHomepageName: 'n1',
				currentHomepageUid: 'cmsitem_00003001',
				oldHomepageUid: 'homepage-uk'
			});
		});

	});

});
