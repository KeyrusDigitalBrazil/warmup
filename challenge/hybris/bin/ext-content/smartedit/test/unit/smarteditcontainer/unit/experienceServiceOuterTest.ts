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
import * as angular from 'angular';
import * as lo from 'lodash';
import {
	annotationService,
	GatewayProxied,
	IBaseCatalog,
	IBaseCatalogs,
	ICatalogService,
	IDefaultExperienceParams,
	IExperience,
	ILanguage,
	ISharedDataService,
	ISite,
	IStorage,
	IStorageManager,
	LanguageService,
} from 'smarteditcommons';
import {ExperienceService, IframeManagerService, PreviewService, SiteService, StoragePropertiesService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';

describe('experienceService', function() {

	const PRODUCT_CATALOGS = [{
		catalogId: 'catalog1',
		versions: [{
			active: true,
			uuid: 'catalog1Version/Online',
			version: 'Online'
		}, {
			active: false,
			uuid: 'catalog1Version/Staged',
			version: 'Staged'
		}]
	}, {
		catalogId: 'catalog2',
		versions: [{
			active: true,
			uuid: 'catalog2Version/Online',
			version: 'Online'
		}, {
			active: false,
			uuid: 'catalog2Version/Staged',
			version: 'Staged'
		}]
	}];

	const ACTIVE_PRODUCT_CATALOG_VERSIONS = [{
		catalog: 'catalog1',
		catalogName: undefined as {},
		catalogVersion: 'Online',
		active: true,
		uuid: 'catalog1Version/Online'
	}, {
		catalog: 'catalog2',
		catalogName: undefined as {},
		catalogVersion: 'Online',
		active: true,
		uuid: 'catalog2Version/Online'
	}];

	const MOCK_STOREFRONT_PREVIEW_URL = 'someMockPreviewStorefronUrl';
	const MOCK_RESOURCE_PATH = 'https://somedomain/storefronturl';
	const MOCK_PREVIEW_TICKET_ID = 1234;

	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const $location = jasmine.createSpyObj<angular.ILocationService>('$location', ['path', 'url']);
	const $log = jasmine.createSpyObj('$log', ['error']);
	const $route = jasmine.createSpyObj('$route', ['reload']);
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	const crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);
	const siteService = jasmine.createSpyObj<SiteService>('siteService', ['getSiteById']);
	const catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', ['getContentCatalogsForSite', 'getProductCatalogsForSite']);
	const languageService = jasmine.createSpyObj<LanguageService>('languageService', ['getLanguagesForSite']);
	const previewService = jasmine.createSpyObj<PreviewService>('previewService', ['getResourcePathFromPreviewUrl', 'createPreview', 'updateUrlWithNewPreviewTicketId']);
	const sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get', 'set']);
	const seStorageManager = jasmine.createSpyObj<IStorageManager>('seStorageManager', ['getStorage']);
	const experienceStorage = jasmine.createSpyObj<IStorage<string, IExperience>>('experienceStorage', ['get', 'put']);

	const iframeManagerService = jasmine.createSpyObj<IframeManagerService>('iframeManagerService', ['loadPreview', 'setCurrentLocation']);

	const EVENTS = {
		PAGE_CHANGE: 'PAGE_CHANGE'
	};
	const LANDING_PAGE_PATH: string = '/asDasdASDa';
	const STORE_FRONT_CONTEXT: string = '/STORE_FRONT_CONTEXT';

	let experienceService: jasmine.SpyObj<ExperienceService>;

	let siteDescriptor: ISite;
	let catalogVersionDescriptor: IBaseCatalogs;
	let languageDescriptor: ILanguage;

	const oldPageId = 'oldPageId';

	let previousExperience: IExperience;

	let locationPath: angular.ILocationService;

	beforeEach(() => {

		locationPath = jasmine.createSpyObj<angular.ILocationService>("locationPath", ["replace"]);

		previousExperience = {
			siteDescriptor: {
				previewUrl: '/someURI/?someSite=site'
			},
			catalogDescriptor: {
				catalogId: 'someCatalog',
				catalogVersion: 'someVersion'
			},
			languageDescriptor: {
				isocode: 'someLanguage'
			},
			pageId: oldPageId
		} as IExperience;


		$location.path.and.returnValue(locationPath);

		seStorageManager.getStorage.and.returnValue($q.when(experienceStorage));

		experienceStorage.put.and.returnValue($q.when({}));

		siteDescriptor = {
			contentCatalogs: ['myCatalogId'],
			name: {
				en: 'mySiteName'
			},
			previewUrl: '/yacceleratorstorefront/?site=mySiteUid',
			uid: 'mySiteUid'
		};

		catalogVersionDescriptor = {
			catalogs: [{
				catalogId: 'myCatalogId',
				name: {
					en: 'myCatalogName'
				},
				versions: [{
					homepage: null,
					pageDisplayConditions: null,
					version: 'myCatalogVersion',
					active: true,
					uuid: 'myCatalogId/myCatalogVersion',
					thumbnailUrl: '/url1'
				}]
			}],
		};

		languageDescriptor = {
			active: true,
			isocode: 'en',
			name: 'English',
			nativeName: 'English-en',
			required: true
		};

		catalogService.getProductCatalogsForSite.and.returnValue($q.when(PRODUCT_CATALOGS));

		previewService.getResourcePathFromPreviewUrl.and.callFake(function() {
			return $q.when(MOCK_RESOURCE_PATH);
		});

		previewService.createPreview.and.callFake(function() {
			return $q.when({
				resourcePath: MOCK_RESOURCE_PATH,
				previewTicketId: MOCK_PREVIEW_TICKET_ID
			});
		});

		previewService.updateUrlWithNewPreviewTicketId.and.callFake(function() {
			return $q.when(MOCK_STOREFRONT_PREVIEW_URL);
		});

		iframeManagerService.loadPreview.and.callFake(function() {
			return $q.when();
		});

		siteService.getSiteById.calls.reset();
		catalogService.getContentCatalogsForSite.calls.reset();
		catalogService.getProductCatalogsForSite.calls.reset();
		languageService.getLanguagesForSite.calls.reset();
		sharedDataService.set.calls.reset();
		sharedDataService.get.calls.reset();
	});

	beforeEach(() => {

		coreAnnotationsHelper.init();

		const propsService = new StoragePropertiesService([], lodash);

		experienceService = new ExperienceService($q, $location, $log, $route, lodash, crossFrameEventService, siteService, catalogService, languageService,
			previewService, sharedDataService, seStorageManager, iframeManagerService, EVENTS, LANDING_PAGE_PATH, STORE_FRONT_CONTEXT, propsService) as jasmine.SpyObj<ExperienceService>;

		spyOn(experienceService, 'updateExperience').and.callThrough();
		spyOn(experienceService, 'getCurrentExperience').and.returnValue($q.when(previousExperience));
		spyOn(experienceService, 'updateExperiencePageContext');
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(ExperienceService, GatewayProxied);
		expect(decoratorObj).toEqual(['loadExperience', 'updateExperiencePageContext', 'getCurrentExperience', 'hasCatalogVersionChanged', 'buildRefreshedPreviewUrl']);
	});

	it('GIVEN a pageId has been passed to the params WHEN I call buildAndSetExperience THEN it will return an experience with a pageId', function() {

		// GIVEN
		siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'myCatalogId',
			name: {
				en: 'myCatalogName',
			},
			versions: [{
				homepage: null,
				pageDisplayConditions: null,
				version: 'myCatalogVersion',
				active: true,
				thumbnailUrl: '/url',
				uuid: 'myCatalogId/myCatalogVersion'
			}]
		} as IBaseCatalog]));

		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'myCatalogId',
			catalogVersion: 'myCatalogVersion',
			pageId: 'myPageId'
		}) as IExtensiblePromise<IExperience>;

		const expectedValue: IExperience = {
			pageId: 'myPageId',
			siteDescriptor,
			catalogDescriptor: {
				catalogId: 'myCatalogId',
				catalogVersion: 'myCatalogVersion',
				catalogVersionUuid: 'myCatalogId/myCatalogVersion',
				name: {
					en: 'myCatalogName',
				},
				siteId: 'mySiteId',
				active: true
			},
			languageDescriptor,
			time: null,
			productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
		};

		// THEN
		expect(promise.value).toEqual(expectedValue);
	});

	it('GIVEN pageId has not been passed to the params WHEN I call buildAndSetExperience THEN it will return an experience without a pageId', function() {

		// GIVEN
		siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'myCatalogId',
			name: {
				en: 'myCatalogName'
			},
			versions: [{
				homepage: null,
				pageDisplayConditions: null,
				version: 'myCatalogVersion',
				active: true,
				thumbnailUrl: '/url',
				uuid: 'uuid2'
			}]
		} as IBaseCatalog]));
		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'myCatalogId',
			catalogVersion: 'myCatalogVersion'
		}) as IExtensiblePromise<IExperience>;

		// THEN
		expect(promise.value).toEqual({
			siteDescriptor,
			catalogDescriptor: {
				catalogId: 'myCatalogId',
				catalogVersion: 'myCatalogVersion',
				catalogVersionUuid: 'uuid2',
				name: {
					en: 'myCatalogName'
				},
				siteId: 'mySiteId',
				active: true
			},
			languageDescriptor,
			time: null,
			productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
		});
	});

	it('GIVEN a siteId, catalogId and catalogVersion, buildAndSetExperience will reconstruct an experience', function() {

		// GIVEN
		siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'myCatalogId',
			name: {
				en: 'myCatalogName'
			},
			versions: [{
				homepage: null,
				pageDisplayConditions: null,
				version: 'myCatalogVersion',
				active: true,
				thumbnailUrl: '/url',
				uuid: 'uuid2'
			}]
		} as IBaseCatalog]));
		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'myCatalogId',
			catalogVersion: 'myCatalogVersion'
		}) as IExtensiblePromise<IExperience>;

		// THEN
		expect(promise.value).toEqual({
			siteDescriptor,
			catalogDescriptor: {
				catalogId: 'myCatalogId',
				catalogVersion: 'myCatalogVersion',
				catalogVersionUuid: 'uuid2',
				name: {
					en: 'myCatalogName'
				},
				siteId: 'mySiteId',
				active: true
			},
			languageDescriptor,
			time: null,
			productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
		} as IExperience);

		expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
		expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
		expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('mySiteId');
	});

	it('GIVEN a siteId, catalogId and unknown catalogVersion, buildAndSetExperience will return a rejected promise', function() {

		// GIVEN
		siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'someValue',
			catalogVersion: 'someCatalogVersion'
		}, catalogVersionDescriptor]));
		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'myCatalogId',
			catalogVersion: 'unknownVersion'
		}) as IExtensiblePromise<string>;

		// THEN
		expect(promise.value).toEqual('no catalogVersionDescriptor found for myCatalogId catalogId and unknownVersion catalogVersion');

		expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
		expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
	});

	it('GIVEN a siteId, unknown catalogId and right catalogVersion, buildAndSetExperience will return a rejected promise', function() {

		// GIVEN
		siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'someValue',
			catalogVersion: 'someCatalogVersion'
		}, catalogVersionDescriptor]));
		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'unknownCatalogId',
			catalogVersion: 'myCatalogVersion'
		}) as IExtensiblePromise<string>;

		// THEN
		expect(promise.value).toEqual('no catalogVersionDescriptor found for unknownCatalogId catalogId and myCatalogVersion catalogVersion');

		expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
		expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
	});

	it('GIVEN a wrong siteId, buildAndSetExperience will return a rejected promise', function() {
		// GIVEN
		siteService.getSiteById.and.returnValue($q.reject(siteDescriptor));
		catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
			catalogId: 'someValue',
			catalogVersion: 'someCatalogVersion'
		}, catalogVersionDescriptor]));
		languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

		// WHEN
		const promise = experienceService.buildAndSetExperience({
			siteId: 'mySiteId',
			catalogId: 'myCatalogId',
			catalogVersion: 'myCatalogVersion'
		});

		// THEN
		expect(promise).toBeRejected();

		expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
	});

	it('WHEN updateExperiencePageID is called THEN it retrieves the current experience, changes it and re-initializes the catalog', function() {
		// Arrange
		const newPageId = 'newPageId';

		// Act
		experienceService.updateExperiencePageId(newPageId);

		expect(previousExperience.pageId).toEqual(newPageId);
		// Assert
		expect(experienceStorage.put).toHaveBeenCalledWith(previousExperience, 'experience');
		expect(sharedDataService.set).toHaveBeenCalledWith('experience', previousExperience);

		expect($location.path).toHaveBeenCalledWith(STORE_FRONT_CONTEXT);
		expect(locationPath.replace).toHaveBeenCalled();
	});

	it('WHEN loadExperience is not called from the storefront view THEN it creates an experience, redirects to the storefront view and reloads the view', function() {
		// GIVEN 
		const siteId = 'someSite';
		const catalogId = 'someCatalog';
		const catalogVersion = 'someVersion';
		const pageId = 'somePageId';

		spyOn<ExperienceService>(experienceService, 'buildAndSetExperience').and.returnValue($q.when());
		// WHEN 
		experienceService.loadExperience({
			siteId,
			catalogId,
			catalogVersion,
			pageId
		} as IDefaultExperienceParams);

		// THEN
		expect(experienceService.buildAndSetExperience).toHaveBeenCalledWith({
			siteId,
			catalogId,
			catalogVersion,
			pageId
		});
		expect($location.path).toHaveBeenCalledWith(STORE_FRONT_CONTEXT);
		expect(locationPath.replace).toHaveBeenCalled();
	});

	it('WHEN loadExperience is called from the storefront view THEN it creates an experience and reloads the storefront view', function() {
		// GIVEN 
		const siteId = 'someSite';
		const catalogId = 'someCatalog';
		const catalogVersion = 'someVersion';
		const pageId = 'somePageId';

		spyOn<ExperienceService>(experienceService, 'buildAndSetExperience').and.returnValue($q.when());
		$location.path.and.returnValue(STORE_FRONT_CONTEXT);

		// WHEN 
		experienceService.loadExperience({
			siteId,
			catalogId,
			catalogVersion,
			pageId
		} as IDefaultExperienceParams);

		// THEN
		expect(experienceService.buildAndSetExperience).toHaveBeenCalledWith({
			siteId,
			catalogId,
			catalogVersion,
			pageId
		});
		expect($route.reload).toHaveBeenCalled();
	});

	it('GIVEN that an experience has been set WHEN I request to load a storefront THEN initializeExperience will call updateExperience', function() {
		// Arrange
		sharedDataService.get.and.returnValue($q.when(previousExperience));

		experienceService.updateExperience.and.returnValue($q.when(previousExperience));
		// Act
		experienceService.initializeExperience();

		// Assert
		expect(experienceService.updateExperience).toHaveBeenCalled();
		expect(iframeManagerService.setCurrentLocation).toHaveBeenCalled();
	});

	it('GIVEN that no experience has been set THEN initializeExperience will redirect to landing page', function() {
		// Arrange
		experienceService.getCurrentExperience.and.returnValue($q.when(null));
		// Act
		experienceService.initializeExperience();

		// Assert
		expect($location.url).toHaveBeenCalledWith(LANDING_PAGE_PATH);
		expect(experienceService.updateExperience).not.toHaveBeenCalled();
		expect(iframeManagerService.setCurrentLocation).toHaveBeenCalled();
	});

	it('WHEN updateExperience is called THEN it retrieves the current experience, merges it with the new experience, creates a preview ticket and reloads the iFrame', function() {

		const newExperience = {
			pageId: 'someOtherPageId'
		} as IExperience;

		const temp = lodash.merge(lodash.cloneDeep(previousExperience), newExperience);

		const previewData = experienceService._convertExperienceToPreviewData(temp, MOCK_RESOURCE_PATH);

		// Act
		experienceService.updateExperience(newExperience);

		// Assert

		expect(sharedDataService.set.calls.count()).toBe(1);
		expect(sharedDataService.set.calls.argsFor(0)[0]).toEqual('experience');
		expect(previewService.getResourcePathFromPreviewUrl).toHaveBeenCalledWith(previousExperience.siteDescriptor.previewUrl);
		expect(previewService.createPreview).toHaveBeenCalledWith(previewData);
		expect(iframeManagerService.loadPreview).toHaveBeenCalledWith(MOCK_RESOURCE_PATH, MOCK_PREVIEW_TICKET_ID);
	});

	it('WHEN updateExperience is called with no experience THEN it retrieves the current experience, creates a preview ticket and reloads the iFrame', function() {

		const previewData = experienceService._convertExperienceToPreviewData(previousExperience, MOCK_RESOURCE_PATH);
		// Act
		experienceService.updateExperience();

		// Assert
		expect(experienceService.getCurrentExperience).toHaveBeenCalled();
		expect(previewService.getResourcePathFromPreviewUrl).toHaveBeenCalledWith(previousExperience.siteDescriptor.previewUrl);
		expect(previewService.createPreview).toHaveBeenCalledWith(previewData);
		expect(iframeManagerService.loadPreview).toHaveBeenCalledWith(MOCK_RESOURCE_PATH, MOCK_PREVIEW_TICKET_ID);
	});
});
