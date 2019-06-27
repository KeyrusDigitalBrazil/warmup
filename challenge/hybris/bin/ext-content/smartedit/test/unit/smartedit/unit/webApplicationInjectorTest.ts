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

describe('Testing webApplicationInjector', function() {

	let SmarteditWAI: any;

	const disallowedConfigs = [
		'*',
		'foo.*',
		'foo.com*',
		'foo.n*t',
		'bar.foo.o*g',
		'*.com',
		'f*o.net',
		'bar.*.org',
		'bar.*foo.io',
		'.',
		'f*o.c*m',
		'bar..',
		'foo.bar.*',
		'*:9000',
		'*.com:9000',
		'bar.*.com:9000',
	];

	const allowedConfigs = [
		'127.0.0.1',
		'*.some.domain:9000',
		'some.domain:900',
		'some.domain',
		'*.my-shop.cx',
		'*-hybris.my-site.io',
		'*.hybirs.my-site.net'
	];

	const allowedOriginURLs: string[] = [
		'http://localhost:9876',
		'http://127.0.0.1',
		'http://hmm.some.domain:9000',
		'http://some.domain:900',
		'http://some.domain',
		'http://subdomain.my-shop.cx',
		'http://prefix-hybris.my-site.io',
		'http://subdomain.hybirs.my-site.net'
	];

	const disallowedOriginURLs: any[] = [
		null,
		undefined,
		42,
		{object: 'foo'},
		['array'],
		'null',
		'file://',
		'file:',
		'javascript://',
		'data:',
		'http://127.0.0.2:9876',
		'http://*.some.*:9000',
		'http://fewafwa.some.domain:900',
		'http://some.domainn',
		'http://fewaea...my-shop.cx',
		'http://subsubdomain.subdomain.hybirs.my-site.net'
	];

	function createLocationWithURL(url: string) {
		const location = document.createElement('a');
		location.href = url;
		return location;
	}

	beforeAll((done) => {
		const script = document.createElement('script');
		script.async = true;
		script.src = '/base/web/webApplicationInjector.js?allow-origin=' + allowedConfigs.join(',');
		script.addEventListener('load', () => {
			SmarteditWAI = (window as any).SmarteditWAI;
			done();
		});
		script.addEventListener('onerror', () => {
			fail('Failed to load webApplicationInjector.js');
		});
		document.body.appendChild(script);
	});

	it('should disallows invalid white-listing config', () => {
		const allowOriginPatternRegex = SmarteditWAI.createAllowOriginRegex();

		disallowedConfigs.forEach((invalidConfg) => {
			if (allowOriginPatternRegex.test(invalidConfg)) {
				fail(`Failed to block disallowed config [${invalidConfg}] for whitelisting`);
			}
		});
	});

	it('should allow valid configs', () => {
		const allowOriginPatternRegex = SmarteditWAI.createAllowOriginRegex();

		allowedConfigs.forEach((validConfg) => {
			if (!allowOriginPatternRegex.test(validConfg)) {
				fail(`Blocked a valid config [${validConfg}] for whitelisting`);
			}
		});
	});

	it('should test for allowed urls origins base on the allowed configuration.', () => {
		const {isAllowed} = SmarteditWAI;

		allowedOriginURLs.forEach((allowedUrl) => {
			if (!isAllowed(allowedUrl, document.location)) {
				fail(`Blocked a valid url [${allowedUrl}] from whitelisting`);
			}
		});
	});

	it('should test for blocked urls origins base on the allowed configuration.', () => {
		const {isAllowed} = SmarteditWAI;

		disallowedOriginURLs.forEach((disallowedUrl) => {
			if (isAllowed(disallowedUrl, document.location)) {
				fail(`Failed to block disallowed url [${disallowedUrl}] from whitelisting`);
			}
		});
	});

	it('should not allow non-TLS origins on a TLS storefront', () => {
		const {isAllowed} = SmarteditWAI;

		const storeFrontDomain = createLocationWithURL('https://storefront.com');
		const originDomain = 'http://localhost:9876';

		expect(isAllowed(originDomain, storeFrontDomain)).toBeFalsy();
	});

	it('should allow TLS origins on a TLS storefront', () => {
		const {isAllowed} = SmarteditWAI;

		const storeFrontDomain = createLocationWithURL('https://storefront.com');
		const originDomain = 'https://localhost:9876';

		expect(isAllowed(originDomain, storeFrontDomain)).toBeTruthy();
	});

});