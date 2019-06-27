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

import {StorageService} from 'smartedit/services';
import {coreAnnotationsHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('inner storage service', () => {

	let storageService: StorageService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		storageService = new StorageService();
	});

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(StorageService, GatewayProxied)).toEqual([]);
	});

	it('all functions are left empty', function() {
		expect(storageService.isInitialized).toBeEmptyFunction();
		expect(storageService.storeAuthToken).toBeEmptyFunction();
		expect(storageService.getAuthToken).toBeEmptyFunction();
		expect(storageService.removeAuthToken).toBeEmptyFunction();
		expect(storageService.removeAllAuthTokens).toBeEmptyFunction();
	});
});
