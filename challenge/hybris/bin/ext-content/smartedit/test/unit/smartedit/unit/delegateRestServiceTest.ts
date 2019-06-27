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
import {DelegateRestService} from 'smartedit/services';
import {coreAnnotationsHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('test DelegateRestService ', () => {

	let delegateRestService: DelegateRestService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		delegateRestService = new DelegateRestService();
	});

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(DelegateRestService, GatewayProxied)).toEqual([]);
	});

	it('delegateForVoid is left unimplemented', function() {
		expect(delegateRestService.delegateForVoid).toBeEmptyFunction();
	});

	it('delegateForSingleInstance is left unimplemented', function() {
		expect(delegateRestService.delegateForSingleInstance).toBeEmptyFunction();
	});

	it('delegateForArray is left unimplemented', function() {
		expect(delegateRestService.delegateForArray).toBeEmptyFunction();
	});

	it('delegateForPage is left unimplemented', function() {
		expect(delegateRestService.delegateForPage).toBeEmptyFunction();
	});

});
