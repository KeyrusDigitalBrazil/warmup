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
import {UrlService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper} from 'testhelpers';

describe('test urlService ', () => {

	let urlService: UrlService;

	let $location: jasmine.SpyObj<angular.ILocationService>;
	let $window: jasmine.SpyObj<angular.IWindowService>;
	let popupWindow: jasmine.SpyObj<Window>;

	const CONTEXT_SITE_ID = 'some site id';
	const CONTEXT_CATALOG = 'some context catalog';
	const CONTEXT_CATALOG_VERSION = 'some catalog version';
	const PAGE_CONTEXT_SITE_ID = 'some page context site ID';
	const PAGE_CONTEXT_CATALOG = 'some page context catalog';
	const PAGE_CONTEXT_CATALOG_VERSION = 'some page context catalog version';

	beforeEach(() => {

		coreAnnotationsHelper.init();

		$location = jasmine.createSpyObj<angular.ILocationService>('$location', ['path']);

		$window = jasmine.createSpyObj<angular.IWindowService>('$window', ['open', 'focus']);
		popupWindow = jasmine.createSpyObj<Window>('popupWindow', ['open', 'focus']);

		$window.open.and.returnValue(popupWindow);

		urlService = new UrlService(
			$location,
			$window,
			PAGE_CONTEXT_SITE_ID,
			PAGE_CONTEXT_CATALOG,
			PAGE_CONTEXT_CATALOG_VERSION,
			CONTEXT_SITE_ID,
			CONTEXT_CATALOG,
			CONTEXT_CATALOG_VERSION
		);
	});

	it('WHEN openUrlInPopup is called THEM it should open a the url in a popup', function() {
		const ANY_URL = 'http://a.com/';
		urlService.openUrlInPopup(ANY_URL);
		expect($window.open).toHaveBeenCalledWith(ANY_URL, '_blank', 'toolbar=no, scrollbars=yes, resizable=yes');
		expect(popupWindow.focus).toHaveBeenCalled();
	});

	it('url service inits a private gateway', function() {
		expect(annotationService.getClassAnnotation(UrlService, GatewayProxied)).toEqual(['openUrlInPopup', 'path']);
	});

});
