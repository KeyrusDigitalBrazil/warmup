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
import {PerspectiveService} from 'smartedit/services';
import {coreAnnotationsHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('inner perspectiveService', () => {

	let perspectiveService: PerspectiveService;
	beforeEach(() => {
		coreAnnotationsHelper.init();
		perspectiveService = new PerspectiveService();
	});

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(PerspectiveService, GatewayProxied)).toEqual([]);
	});

	it('register is left unimplemented', () => {
		expect(perspectiveService.register).toBeEmptyFunction();
	});

	it('isEmptyPerspectiveActive is left unimplemented', () => {
		expect(perspectiveService.isEmptyPerspectiveActive).toBeEmptyFunction();
	});
});
