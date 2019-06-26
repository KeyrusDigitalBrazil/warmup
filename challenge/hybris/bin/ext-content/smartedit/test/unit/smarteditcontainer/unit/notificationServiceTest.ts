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
import * as lo from 'lodash';
import {NotificationService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';
import {annotationService, GatewayProxied} from 'smarteditcommons';

describe('notificationService', () => {
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	const $q = promiseHelper.$q();

	const DUMMY_ID = 'dummy.id';
	const DUMMY_ID1 = DUMMY_ID + '1';
	const DUMMY_ID2 = DUMMY_ID + '2';
	const DUMMY_ID3 = DUMMY_ID + '3';
	const DUMMY_INVALID_ID = 'dummy.invalid.id';

	const DUMMY_TEMPLATE = '<div>this is a dummy template</div>';
	const DUMMY_TEMPLATE_URL = 'dummyTemplateUrl.html';

	const DUMMY_CONFIGURATION = {
		id: DUMMY_ID,
		template: DUMMY_TEMPLATE
	};

	const systemEventService: any = jasmine.createSpyObj('systemEventService', ['publishAsync']);
	const EVENT_NOTIFICATION_CHANGED: string = 'EVENT_NOTIFICATION_CHANGED';

	let notificationService: NotificationService;

	beforeEach(() => {
		systemEventService.publishAsync.and.returnValue(null);
		coreAnnotationsHelper.init();
		notificationService = new NotificationService(lodash, systemEventService, EVENT_NOTIFICATION_CHANGED, $q);
	});

	describe('initialization', () => {
		it('invokes the gatway proxy with the proper parameter values', () => {
			const decoratorObj = annotationService.getClassAnnotation(NotificationService, GatewayProxied);
			expect(decoratorObj).toEqual(['pushNotification', 'removeNotification', 'removeAllNotifications']);
		});
	});

	describe('pushNotification', () => {
		it('throws an error if no configuration is given', () => {
			expect(function() {
				notificationService.pushNotification(null);
			}).toThrowError('notificationService.pushNotification: Configuration is required');
		});

		it('throws an error if the configuration contains a unique identifier that is undefined or null or empty string', () => {
			expect(function() {
				notificationService.pushNotification({
					id: '',
					template: DUMMY_TEMPLATE
				});
			}).toThrowError('notificationService.pushNotification: Notification ID cannot be undefined or null or empty');
		});

		it('throws an error if the configuration does not contain a template or template URL', () => {
			expect(function() {
				notificationService.pushNotification({
					id: DUMMY_ID
				});
			}).toThrowError('notificationService.pushNotification: Configuration must contain a template or template URL');
		});

		it('throws an error if the configuration contains both a template and a template URL', () => {
			expect(function() {
				notificationService.pushNotification({
					id: DUMMY_ID,
					template: DUMMY_TEMPLATE,
					templateUrl: DUMMY_TEMPLATE_URL
				});
			}).toThrowError('notificationService.pushNotification: Configuration cannot contain both a template and template URL; use one or the other');
		});

		it('throws an error when the configuration contains a unique identifier that already exists', () => {
			// Given
			notificationService.pushNotification(DUMMY_CONFIGURATION);

			// When/Then
			expect(function() {
				notificationService.pushNotification(DUMMY_CONFIGURATION);
			}).toThrowError('notificationService.pushNotification: Notification already exists with ID "' + DUMMY_ID + '"');
		});

		it('creates an notification with the proper ID and template', () => {
			// Given
			const configuration = {
				id: DUMMY_ID,
				template: DUMMY_TEMPLATE
			};

			// When
			notificationService.pushNotification(configuration);

			// Then
			expect(notificationService.getNotification(DUMMY_ID)).toEqual(jasmine.objectContaining(configuration));
		});

		it('creates an notification with the proper ID and template URL', () => {
			// Given
			const configuration = {
				id: DUMMY_ID,
				templateUrl: DUMMY_TEMPLATE_URL
			};

			// When
			notificationService.pushNotification(configuration);

			// Then
			expect(notificationService.getNotification(DUMMY_ID)).toEqual(jasmine.objectContaining(configuration));
		});

		it('sends an "EVENT_NOTIFICATION_CHANGED" event when an notification is added to the list', () => {
			// When
			notificationService.pushNotification(DUMMY_CONFIGURATION);

			// Then
			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_NOTIFICATION_CHANGED);
		});
	});

	describe('removeNotification', () => {
		it('removes the notification with the given ID from the list', () => {
			// Given
			notificationService.pushNotification(DUMMY_CONFIGURATION);

			// When
			notificationService.removeNotification(DUMMY_ID);

			// Then
			expect(notificationService.getNotification(DUMMY_ID)).toBeFalsy();
		});

		it('removes nothing when no notification with the given ID exists', () => {
			// Given
			notificationService.pushNotification({
				id: DUMMY_ID1,
				template: DUMMY_TEMPLATE
			});

			notificationService.pushNotification({
				id: DUMMY_ID2,
				template: DUMMY_TEMPLATE
			});

			// When
			notificationService.removeNotification(DUMMY_INVALID_ID);

			// Then
			expect(notificationService.getNotifications().length).toEqual(2);
		});

		it('sends an "EVENT_NOTIFICATION_CHANGED" event when an notification is removed from the list', () => {
			// Given
			notificationService.pushNotification({
				id: DUMMY_ID,
				template: DUMMY_TEMPLATE
			});

			systemEventService.publishAsync.calls.reset();

			// When
			notificationService.removeNotification(DUMMY_ID);

			// Then
			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_NOTIFICATION_CHANGED);
		});
	});

	describe('removeAllNotifications', () => {
		it('removes all the notificationes from the list', () => {
			// Given
			notificationService.pushNotification({
				id: DUMMY_ID1,
				template: DUMMY_TEMPLATE
			});

			notificationService.pushNotification({
				id: DUMMY_ID2,
				template: DUMMY_TEMPLATE
			});

			notificationService.pushNotification({
				id: DUMMY_ID3,
				template: DUMMY_TEMPLATE
			});

			// When
			notificationService.removeAllNotifications();

			// Then
			expect(notificationService.getNotification(DUMMY_ID1)).toBeFalsy();
			expect(notificationService.getNotification(DUMMY_ID2)).toBeFalsy();
			expect(notificationService.getNotification(DUMMY_ID3)).toBeFalsy();
		});

		it('sends an "EVENT_NOTIFICATION_CHANGED" event when the notificationes are removed from the list', () => {
			// Given
			notificationService.pushNotification({
				id: DUMMY_ID1,
				template: DUMMY_TEMPLATE
			});

			notificationService.pushNotification({
				id: DUMMY_ID2,
				template: DUMMY_TEMPLATE
			});

			notificationService.pushNotification({
				id: DUMMY_ID3,
				template: DUMMY_TEMPLATE
			});

			systemEventService.publishAsync.calls.reset();

			// When
			notificationService.removeAllNotifications();

			// Then
			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_NOTIFICATION_CHANGED);
		});
	});

	describe('getNotification', () => {
		it('returns the notification with the given ID', () => {
			// Given
			notificationService.pushNotification(DUMMY_CONFIGURATION);

			// When
			const result = notificationService.getNotification(DUMMY_ID);

			// Then
			expect(result).toEqual(jasmine.objectContaining(DUMMY_CONFIGURATION));
		});
	});

	describe('getNotifications', () => {
		it('returns the list of notificationes', () => {
			// Given
			const configuration1 = {
				id: DUMMY_ID1,
				template: DUMMY_TEMPLATE
			};

			const configuration2 = {
				id: DUMMY_ID2,
				template: DUMMY_TEMPLATE
			};

			const configuration3 = {
				id: DUMMY_ID3,
				template: DUMMY_TEMPLATE
			};

			notificationService.pushNotification(configuration1);
			notificationService.pushNotification(configuration2);
			notificationService.pushNotification(configuration3);

			// When
			const notifications = notificationService.getNotifications();

			// Then
            /*
             * NOTE: The notifications in the list should be in reverse order, since we return the
             * reversed list.
             */
			expect(notifications.length).toBe(3);
			expect(notifications[0]).toEqual(jasmine.objectContaining(configuration3));
			expect(notifications[1]).toEqual(jasmine.objectContaining(configuration2));
			expect(notifications[2]).toEqual(jasmine.objectContaining(configuration1));
		});
	});
});
