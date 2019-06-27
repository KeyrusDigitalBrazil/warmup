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
import {annotationService, GatewayProxied} from 'smarteditcommons';
import {SessionService} from 'smartedit/services';
import {coreAnnotationsHelper} from 'testhelpers';

describe('inner sessionService', () => {

	let sessionService: SessionService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		sessionService = new SessionService();
	});

	it('initializes and invokes gatewayProxy', function() {
		expect(annotationService.getClassAnnotation(SessionService, GatewayProxied)).toEqual([]);
	});

	it('leaves all interface functions unimplemented', function() {
		expect(sessionService.getCurrentUsername).toBeEmptyFunction();
		expect(sessionService.getCurrentUserDisplayName).toBeEmptyFunction();
		expect(sessionService.hasUserChanged).toBeEmptyFunction();
		expect(sessionService.resetCurrentUserData).toBeEmptyFunction();
		expect(sessionService.setCurrentUsername).toBeEmptyFunction();
	});
});
