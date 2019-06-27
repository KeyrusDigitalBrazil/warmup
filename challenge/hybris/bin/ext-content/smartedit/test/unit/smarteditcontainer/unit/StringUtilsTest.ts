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

import {StringUtils} from 'smarteditcommons';

describe('StringUtilsTests', () => {

	const stringUtils: StringUtils = new StringUtils();

	it('regexp test positively with alphanumerical pattern', function() {
		const regexp = stringUtils.regExpFactory("someAlpha1");

		expect(regexp.test("someAlpha1")).toBe(true);
	});

	it('regexp test negatively with alphanumerical pattern', function() {
		const regexp = stringUtils.regExpFactory("someAlpha1");

		expect(regexp.test("someurl")).toBe(false);
	});

	it('regexp test positively with wildcards pattern', function() {
		const regexp = stringUtils.regExpFactory("*some*Alpha1*");

		expect(regexp.test("bla_some_bla_Alpha1_bla")).toBe(true);
	});

	it('regexp test negatively with wildcards pattern', function() {
		const regexp = stringUtils.regExpFactory("*some*Alpha1*");

		expect(regexp.test("bla_some_bla_Alpha2_bla")).toBe(false);
	});

	it('regexp test positively with ready to use regexp string pattern', function() {
		const regexp = stringUtils.regExpFactory("^[a-z]+$");

		expect(regexp.test("somelowercase")).toBe(true);
	});

	it('regexp test negatively with ready to use regexp string pattern', function() {
		const regexp = stringUtils.regExpFactory("^[a-z]+$");

		expect(regexp.test("someUppercase")).toBe(false);
	});

}); 