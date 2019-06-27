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
import {SharedDataService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('test sharedDataService', function() {

	let sharedDataService: SharedDataService;
	const $q = promiseHelper.$q();

	beforeEach(() => {
		coreAnnotationsHelper.init();
		sharedDataService = new SharedDataService($q);
	});

	it('shared data service should validate get and set method', function() {
		sharedDataService.set('catalogVersion', '1.4');
		(expect(sharedDataService.get('catalogVersion')) as any).toBeResolvedWithData('1.4');
	});

	it('shared data service should override the value for a given key', function() {
		sharedDataService.set('catalogVersion', '1.4');
		sharedDataService.set('catalogVersion', '1.6');
		(expect(sharedDataService.get('catalogVersion')) as any).toBeResolvedWithData('1.6');
	});


	it('shared data service should check the object saved for a given key', function() {
		const obj = {
			catalog: 'apparel-ukContentCatalog',
			catalogVersion: '1.4'
		};

		sharedDataService.set('obj', obj);
		(expect(sharedDataService.get('obj')) as any).toBeResolvedWithData(obj);
	});


	it('shared data service should set the value to null for a given key', function() {
		sharedDataService.set('catalogVersion', '1.4');
		sharedDataService.set('catalogVersion', null);
		(expect(sharedDataService.get('catalogVersion')) as any).toBeResolvedWithData(null);
	});

	it('checks GatewayProxied', function() {
		expect(annotationService.getClassAnnotation(SharedDataService, GatewayProxied)).toEqual([]);
	});
});
