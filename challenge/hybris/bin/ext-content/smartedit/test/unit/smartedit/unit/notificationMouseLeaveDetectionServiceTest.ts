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
import {annotationService, GatewayProxied, IBound} from 'smarteditcommons';
import {NotificationMouseLeaveDetectionService} from 'smartedit/services';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';

describe('notificationMouseLeaveDetectionService', () => {

	const DUMMY_X = 0;
	const DUMMY_Y = 0;
	const DUMMY_WIDTH = 100;
	const DUMMY_HEIGHT = 100;
	const DUMMY_BOUNDS = {
		x: DUMMY_X,
		y: DUMMY_Y,
		width: DUMMY_WIDTH,
		height: DUMMY_HEIGHT
	};
	const MOUSE_MOVE_EVENT = 'mousemove';

	const $q = promiseHelper.$q();
	const $document: any = jasmine.createSpyObj<any>('$document', ['on', 'off']);

	let notificationMouseLeaveDetectionService: NotificationMouseLeaveDetectionService;

    /*
     * This method prepares a mock for the Gateway Proxy. It is used to test that
     * the service properly initializes itself for proxying across the gateay.
     */
	beforeEach(() => {
		coreAnnotationsHelper.init();
		notificationMouseLeaveDetectionService = new NotificationMouseLeaveDetectionService($document, $q);
	});

	describe('initialization', () => {
		it('extends the INotificationMouseLeaveDetectionService', () => {
			expect(notificationMouseLeaveDetectionService.startDetection).toBeEmptyFunction();
			expect(notificationMouseLeaveDetectionService.stopDetection).toBeEmptyFunction();
			expect((notificationMouseLeaveDetectionService as any)._callCallback).toBeEmptyFunction();
		});

		it('checks GatewayProxied', () => {
			const decoratorObj = annotationService.getClassAnnotation(NotificationMouseLeaveDetectionService, GatewayProxied);
			expect(decoratorObj).toEqual(['stopDetection', '_remoteStartDetection', '_remoteStopDetection', '_callCallback']);
		});
	});

	describe('_remoteStartDetection', () => {
		it('registers a mouse move event listener on the local frame', () => {

			// When
			(notificationMouseLeaveDetectionService as any)._remoteStartDetection(DUMMY_BOUNDS);

			// Then
			expect($document.on).toHaveBeenCalledWith(MOUSE_MOVE_EVENT, (notificationMouseLeaveDetectionService as any)._onMouseMove);
		});
	});

	describe('_remoteStopDetection', () => {
		it('un-registers the mouse move event listener on the local frame', () => {

			// When
			(notificationMouseLeaveDetectionService as any)._remoteStopDetection();

			// Then
			expect($document.off).toHaveBeenCalledWith(MOUSE_MOVE_EVENT, (notificationMouseLeaveDetectionService as any)._onMouseMove);
		});

		it('resets the notification panel bounds that were stored', function() {
			// Given
			notificationMouseLeaveDetectionService.startDetection(DUMMY_BOUNDS, null, function() { /**/});

			// When
			notificationMouseLeaveDetectionService.stopDetection();

			// Then
			expect(((notificationMouseLeaveDetectionService as any)._getBounds() as IExtensiblePromise<IBound>).value).toBeFalsy();
		});
	});

	describe('_getBounds', () => {
		it('returns the bounds that were given when detection was started', () => {
			// Given
			(notificationMouseLeaveDetectionService as any)._remoteStartDetection(DUMMY_BOUNDS);

			// When
			const bounds = ((notificationMouseLeaveDetectionService as any)._getBounds() as IExtensiblePromise<IBound>).value;

			// Then
			expect(bounds).toEqual(DUMMY_BOUNDS);
		});
	});

	describe('_getCallback', () => {
		it('always returns null', () => {
			// Given
			(notificationMouseLeaveDetectionService as any)._remoteStartDetection(DUMMY_BOUNDS);

			// When
			const callback = ((notificationMouseLeaveDetectionService as any)._getCallback() as IExtensiblePromise<IBound>).value;

			// Then
			expect(callback).toBeFalsy();
		});
	});
});
