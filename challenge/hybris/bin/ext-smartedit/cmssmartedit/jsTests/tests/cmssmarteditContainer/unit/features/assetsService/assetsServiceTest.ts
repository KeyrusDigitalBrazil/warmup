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
import {AssetsService} from 'cmscommons';

describe('Assets Service ', function() {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	const TEST_ASSETS_ROOT: string = '/web/webroot';
	const PROD_ASSETS_ROOT: string = '/cmssmartedit';

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	let assetsService: AssetsService;
	let $injectorMock: jasmine.SpyObj<angular.auto.IInjectorService>;

	// --------------------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------------------
	beforeEach(() => {
		const fixture = AngularUnitTestHelper.prepareModule('cmsSmarteditServicesModule')
			.service('assetsService');
		assetsService = fixture.service;

		$injectorMock = jasmine.createSpyObj('$injector', ['has', 'get']);
		spyOn<AssetsService>(assetsService, 'getInjector').and.returnValue($injectorMock);
	});

	it('GIVEN in test mode WHEN getAssetsRoot is called THEN it returns test assets root', () => {
		// GIVEN
		$injectorMock.has.and.returnValue(true);
		$injectorMock.get.and.returnValue(true);

		// WHEN
		const result = assetsService.getAssetsRoot();

		// THEN
		expect(result).toBe(TEST_ASSETS_ROOT);
	});

	it('GIVEN in production mode WHEN getAssetsRoot is called THEN it returns production assets root', () => {
		// GIVEN
		$injectorMock.has.and.returnValue(false);

		// WHEN
		const result = assetsService.getAssetsRoot();

		// THEN
		expect(result).toBe(PROD_ASSETS_ROOT);
	});

});
