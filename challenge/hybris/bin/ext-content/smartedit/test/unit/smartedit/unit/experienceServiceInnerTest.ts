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

import * as lo from 'lodash';

import {ExperienceService} from 'smartedit/services';
import {annotationService, GatewayProxied, IExperience, IPreviewData} from 'smarteditcommons';
import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';

describe('experienceService', function() {

	const MOCK_STOREFRONT_PREVIEW_URL = 'someMockPreviewStorefrontUrl';
	const MOCK_RESOURCE_PATH = "https://somedomain/storefronturl";

	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const $location = jasmine.createSpyObj('$location', ['absUrl']);
	const $log = jasmine.createSpyObj('$log', ['error']);
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	const previewService = jasmine.createSpyObj('previewService', ['getResourcePathFromPreviewUrl', 'updateUrlWithNewPreviewTicketId']);

	let experienceService: ExperienceService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		experienceService = new ExperienceService($q, $location, $log, lodash, previewService);
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(ExperienceService, GatewayProxied);
		expect(decoratorObj).toEqual(['loadExperience', 'updateExperiencePageContext', 'getCurrentExperience', 'setCurrentExperience', 'hasCatalogVersionChanged', 'buildRefreshedPreviewUrl']);
	});

	it('WHEN buildRefreshedPreviewUrl is called THEN it retrieves the current experience and returns the updated url with new preview ticket id', function() {
		const experience: any = {
			siteDescriptor: {
				previewUrl: '/someURI/?someSite=site'
			},
			catalogDescriptor: {
				catalogId: 'someCatalog',
				catalogVersion: 'someVersion'
			},
			languageDescriptor: {
				isocode: 'someLanguage'
			}
		} as IExperience;
		const url = 'http://server/';

		spyOn(experienceService, 'getCurrentExperience').and.returnValue($q.when(experience));
		$location.absUrl.and.returnValue(url);
		previewService.getResourcePathFromPreviewUrl.and.returnValue($q.when(MOCK_RESOURCE_PATH));
		previewService.updateUrlWithNewPreviewTicketId.and.returnValue($q.when(MOCK_STOREFRONT_PREVIEW_URL));

		experience.catalogVersions = [{
			catalog: experience.catalogDescriptor.catalogId,
			catalogVersion: experience.catalogDescriptor.catalogVersion
		}];
		experience.language = experience.languageDescriptor.isocode;
		experience.resourcePath = MOCK_RESOURCE_PATH;
		// Act
		experienceService.buildRefreshedPreviewUrl();

		// Assert
		expect(previewService.getResourcePathFromPreviewUrl).toHaveBeenCalledWith(experience.siteDescriptor.previewUrl);
		expect(previewService.updateUrlWithNewPreviewTicketId).toHaveBeenCalledWith(
			url,
			{
				catalogVersions: [{catalog: 'someCatalog', catalogVersion: 'someVersion'}],
				language: 'someLanguage',
				resourcePath: 'https://somedomain/storefronturl'
			} as IPreviewData);
	});
});
