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

import {SharedDataService} from 'smartedit/services';
import {coreAnnotationsHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('test sharedDataService', function() {

	let sharedDataService: SharedDataService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		sharedDataService = new SharedDataService();
	});

	it('set function is left empty to enable proxying', () => {
		expect(sharedDataService.set).toBeEmptyFunction();
	});

	it('get function is left empty to enable proxying', () => {
		expect(sharedDataService.get).toBeEmptyFunction();
	});

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(SharedDataService, GatewayProxied)).toEqual([]);
	});
});
