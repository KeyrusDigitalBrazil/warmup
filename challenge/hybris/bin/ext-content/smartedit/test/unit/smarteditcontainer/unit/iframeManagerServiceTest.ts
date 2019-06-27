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
import {IframeManagerService} from 'smarteditcontainer/services';

import {jQueryHelper, promiseHelper, LogHelper, PromiseType} from 'testhelpers';

describe('iframeManagerService', () => {
	const $q = promiseHelper.$q();
	const $http: jasmine.SpyObj<angular.IHttpService> = jasmine.createSpyObj('http', ['get']);
	let yjQuery: JQueryStatic;
	const heartBeatService: jasmine.SpyObj<any> = jasmine.createSpyObj('heartBeatService', ['resetTimer']);

	const DEVICE_SUPPORTS: any[] = [];
	const DEVICE_ORIENTATIONS: any[] = [];
	const SMARTEDIT_IFRAME_ID: string = '';

	const getOrigin = jasmine.createSpy('getOrigin');
	const getURI = jasmine.createSpy('getURI');
	const parseQuery = jasmine.createSpy('parseQuery');

	const previewTicket: string = 'previewTicket1';

	let iframeMock: jasmine.SpyObj<JQuery>;

	let iframeManagerService: IframeManagerService;

	beforeEach(() => {
		yjQuery = jQueryHelper.jQuery();

		getURI.and.callFake((url: string) => {
			return url && url.indexOf('?') > -1 ? url.split('?')[0] : url;
		});
		parseQuery.and.callFake((url: any) => {
			const objURL = {} as any;
			url.replace(new RegExp('([^?=&]+)(=([^&]*))?', 'g'), ($0: any, $1: any, $2: any, $3: any) => {
				objURL[$1] = $3;
			});
			return objURL;
		});

		iframeManagerService = new IframeManagerService(
			$q,
			new LogHelper(),
			$http,
			heartBeatService,
			DEVICE_SUPPORTS,
			DEVICE_ORIENTATIONS,
			getOrigin,
			getURI,
			parseQuery,
			SMARTEDIT_IFRAME_ID,
			yjQuery
		);

		iframeMock = jasmine.createSpyObj('iframeMock', ['removeClass', 'addClass', 'css', 'attr']);
		iframeMock.removeClass.and.returnValue(iframeMock);
		iframeMock.addClass.and.returnValue(iframeMock);

		spyOn(iframeManagerService, 'getIframe').and.returnValue(iframeMock);

		$http.get.calls.reset();
		heartBeatService.resetTimer.calls.reset();
	});

	describe('_mustLoadAsSuch', () => {
		it('will return true if not currentLocation is set', () => {
			iframeManagerService.setCurrentLocation(undefined);
			expect((iframeManagerService as any)._mustLoadAsSuch('/myurl')).toBe(true);
		});

		it('will return true if the currentLocation is the homePageOrPageFromPageList', () => {
			iframeManagerService.setCurrentLocation('/profilepage');
			expect((iframeManagerService as any)._mustLoadAsSuch('/profilepage')).toBe(true);
		});

		it('will return true if the currentLocation has a cmsTicketId', () => {
			iframeManagerService.setCurrentLocation('/profilepage?cmsTicketId=myticketID');
			expect((iframeManagerService as any)._mustLoadAsSuch('/otherpage')).toBe(true);
		});

		it("will return false if we have a currentLocation that is not the home page or a page from the page list, and doesn't have a cmsTicketID", () => {
			iframeManagerService.setCurrentLocation('/randomURL');
			expect((iframeManagerService as any)._mustLoadAsSuch('/homePageOrPageFromPageList')).toBe(false);
		});
	});

	it('GIVEN that _mustLoadHasSuch has returned false WHEN I request to load a preview THEN the page will be first loaded in preview mode, then we will load the currentLocation', () => {
		// Arrange
		(iframeManagerService as any)._mustLoadAsSuch = jasmine.createSpy().and.returnValue(false);
		spyOn(iframeManagerService, 'load');
		$http.get.and.callFake((uri: string) => promiseHelper.buildPromise('success', PromiseType.RESOLVES));

		// Act
		iframeManagerService.setCurrentLocation('aLocation');
		iframeManagerService.loadPreview('myurl', previewTicket);

		// Assert
		expect($http.get).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
		expect(iframeManagerService.load).toHaveBeenCalledWith(
			'aLocation',
			true,
			'myurl/previewServlet?cmsTicketId=previewTicket1'
		);
	});

	it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is not failing THEN iframe will display the requested URL', () => {
		// Arrange/Act
		$http.get.and.callFake((uri: string) => promiseHelper.buildPromise('success', PromiseType.RESOLVES));
		iframeManagerService.load('/notfailinghtml', true, '/myhomepage');

		// Assert
		expect(iframeMock.attr).toHaveBeenCalledWith('src', '/notfailinghtml');
		expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
	});

	it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is failing THEN iframe will display the homepage', () => {
		// Arrange/Act
		$http.get.and.callFake((uri: string) =>
			promiseHelper.buildPromise('error', PromiseType.REJECTS, {
				status: 404
			})
		);
		iframeManagerService.load('/failinghtml', true, '/myhomepage');

		// Assert
		expect(iframeMock.attr).toHaveBeenCalledWith('src', '/myhomepage');
		expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
	});

	it('iframeManagerService load the expected url into the iframe', () => {
		// Arrange/Act
		iframeManagerService.load('myurl');

		// Assert
		expect(iframeMock.attr).toHaveBeenCalledWith('src', 'myurl');
		expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
		expect($http.get).not.toHaveBeenCalled();
	});

	it('iframeManagerService loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 1', () => {
		// Arrange
		spyOn(iframeManagerService, 'load');

		// Act
		iframeManagerService.loadPreview('myurl', previewTicket);

		// Assert
		expect(iframeManagerService.load).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
	});
	it('iframeManagerService loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 2', () => {
		// Arrange
		spyOn(iframeManagerService, 'load');

		// Act
		iframeManagerService.loadPreview('myurl/', previewTicket);

		// Assert
		expect(iframeManagerService.load).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
	});
	it('iframeManagerService loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 3', () => {
		// Arrange
		spyOn(iframeManagerService, 'load');

		// Act
		iframeManagerService.loadPreview('myurl?param1=value1', previewTicket);

		// Assert
		expect(iframeManagerService.load).toHaveBeenCalledWith(
			'myurl/previewServlet?param1=value1&cmsTicketId=previewTicket1'
		);
	});
	it('iframeManagerService loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 4', () => {
		// Arrange
		spyOn(iframeManagerService, 'load');

		// Act
		iframeManagerService.loadPreview('myurl/?param1=value1', previewTicket);

		// Assert
		expect(iframeManagerService.load).toHaveBeenCalledWith(
			'myurl/previewServlet?param1=value1&cmsTicketId=previewTicket1'
		);
	});

	it('apply on no arguments gives a full frame', () => {
		iframeManagerService.apply();
		expect(iframeMock.removeClass).toHaveBeenCalled();
		expect(iframeMock.addClass).not.toHaveBeenCalled();
		expect(iframeMock.css).toHaveBeenCalledWith({
			width: '100%',
			height: '100%',
			display: 'block',
			margin: 'auto'
		});
	});

	it('apply device support with no orientation sets it to vertical', () => {
		iframeManagerService.apply({
			width: 600,
			height: '100%',
			icon: 'icon.png',
			selectedIcon: 'icon-selected.png',
			blueIcon: 'icon-selected.png',
			type: 'newType'
		});
		expect(iframeMock.removeClass).toHaveBeenCalled();
		expect(iframeMock.addClass).toHaveBeenCalledWith('device-vertical device-default');
		expect(iframeMock.css).toHaveBeenCalledWith({
			width: 600,
			height: '100%',
			display: 'block',
			margin: 'auto'
		});
	});

	it('apply device support with orientation applies this orientation', () => {
		iframeManagerService.apply(
			{
				height: 600,
				width: '100%',
				icon: 'icon.png',
				selectedIcon: 'icon-selected.png',
				blueIcon: 'icon-selected.png',
				type: 'newType'
			},
			{
				orientation: 'horizontal',
				key: 'se.deviceorientation.horizontal.label'
			}
		);
		expect(iframeMock.removeClass).toHaveBeenCalled();
		expect(iframeMock.addClass).toHaveBeenCalledWith('device-horizontal device-default');
		expect(iframeMock.css).toHaveBeenCalledWith({
			width: 600,
			height: '100%',
			display: 'block',
			margin: 'auto'
		});
	});
});
