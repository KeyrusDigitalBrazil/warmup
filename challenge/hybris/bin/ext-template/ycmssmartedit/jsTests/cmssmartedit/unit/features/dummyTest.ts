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

describe('cmssmartedit - some test suite with TypeScript', () => {
	let ycmsSampleService: any;

	beforeEach(() => {
		angular.mock.module("ycmssmarteditModule");
		angular.mock.inject((_ycmsSampleService_: any) => {
			ycmsSampleService = _ycmsSampleService_;
		});
	});

	it('will assert that ycmsSampleService is defined', () => {
		expect(ycmsSampleService).toBeDefined();
	});
});
