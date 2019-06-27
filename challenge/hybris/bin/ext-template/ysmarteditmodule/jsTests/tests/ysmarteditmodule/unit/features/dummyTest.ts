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

describe('ysmarteditmodule - some test suite with TypeScript', () => {
	beforeEach(() => {
		angular.mock.module("ysmarteditmodule");
	});

	it('will assert that true equals true', () => {
		expect(true).toBe(true);
	});
});
