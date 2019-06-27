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
import {UrlUtils} from 'smarteditcommons';

describe('UrlUtils', () => {

	const urlUtils = new UrlUtils();

	const newQueryKey = 'newQueryKey';
	const newQueryValue = 'newQueryValue';

	it('Will add the missing query param to url when url has no existing query params', () => {

		const sourceUrl = "https://domain";
		const expectUrl = "https://domain?newQueryKey=newQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will add the missing query param to a string when url has other query params', () => {

		const sourceUrl = "https://domain?otherQueryKey=otherQueryValue";
		const expectUrl = "https://domain?otherQueryKey=otherQueryValue&newQueryKey=newQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will update value of an existing query param with no value', () => {

		const sourceUrl = "https://domain?newQueryKey=";
		const expectUrl = "https://domain?newQueryKey=newQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will update value of an existing query param with a value', () => {

		const sourceUrl = "https://domain?newQueryKey=oldQueryValue";
		const expectUrl = "https://domain?newQueryKey=newQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will update value of an existing query param with a value in middle of other query params', () => {

		const sourceUrl = "https://domain?newQueryKey=oldQueryValue&otherQueryKey=otherQueryValue";
		const expectUrl = "https://domain?newQueryKey=newQueryValue&otherQueryKey=otherQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will update value of an existing query param with a value in middle of other query params', () => {

		const sourceUrl = "https://domain?newQueryKey=oldQueryValue&otherQueryKey=otherQueryValue";
		const expectUrl = "https://domain?newQueryKey=newQueryValue&otherQueryKey=otherQueryValue";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will keep any anchors at the end of the url', () => {

		const sourceUrl = "https://domain#blablalba";
		const expectUrl = "https://domain?newQueryKey=newQueryValue#blablalba";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

	it('Will keep any anchors at the end of the url with existing query params before the anchor', () => {

		const sourceUrl = "https://domain?otherQueryKey=otherQueryValue#blablalba";
		const expectUrl = "https://domain?otherQueryKey=otherQueryValue&newQueryKey=newQueryValue#blablalba";

		expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(expectUrl);
	});

});