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
import * as lo from 'lodash';
import {GatewayProxied, INotificationConfiguration, INotificationService, SeInjectable, SystemEventService} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:NotificationService
 * 
 * @description
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
@GatewayProxied('pushNotification', 'removeNotification', 'removeAllNotifications')
@SeInjectable()
export class NotificationService implements INotificationService {

	private notifications: INotificationConfiguration[];

	constructor(
		private lodash: lo.LoDashStatic,
		private systemEventService: SystemEventService,
		private EVENT_NOTIFICATION_CHANGED: string,
		private $q: angular.IQService) {
		this.notifications = [];
	}

	pushNotification(configuration: INotificationConfiguration): angular.IPromise<void> {
		this._validate(configuration);

		if (this.getNotification(configuration.id)) {
			throw new Error('notificationService.pushNotification: Notification already exists with ID "' + configuration.id + '"');
		}

		this.notifications.push(this.lodash.clone(configuration));

		this.systemEventService.publishAsync(this.EVENT_NOTIFICATION_CHANGED);

		return this.$q.when();
	}
	removeNotification(notificationId: string): angular.IPromise<void> {
		this.lodash.remove(this.notifications, (notification: INotificationConfiguration) => notification.id === notificationId);
		this.systemEventService.publishAsync(this.EVENT_NOTIFICATION_CHANGED);
		return this.$q.when();
	}
	removeAllNotifications(): angular.IPromise<void> {
		this.notifications = [];
		this.systemEventService.publishAsync(this.EVENT_NOTIFICATION_CHANGED);
		return this.$q.when();
	}

	isNotificationDisplayed(notificationId: string): boolean {
		return !!this.getNotification(notificationId);
	}

	getNotification(notificationId: string): INotificationConfiguration {
		return this.lodash.find(this.notifications, ['id', notificationId]);
	}

	getNotifications(): INotificationConfiguration[] {
		const clonedNotifications = this.lodash.clone(this.notifications);
		return this.lodash.reverse(clonedNotifications);
	}

	private _validate(configuration: INotificationConfiguration) {
		if (!configuration) {
			throw new Error('notificationService.pushNotification: Configuration is required');
		}

		if (this.lodash.isEmpty(configuration.id)) {
			throw new Error('notificationService.pushNotification: Notification ID cannot be undefined or null or empty');
		}

		if (!configuration.hasOwnProperty('template') && !configuration.hasOwnProperty('templateUrl')) {
			throw new Error('notificationService.pushNotification: Configuration must contain a template or template URL');
		}

		if (configuration.hasOwnProperty('template') && configuration.hasOwnProperty('templateUrl')) {
			throw new Error('notificationService.pushNotification: Configuration cannot contain both a template and template URL; use one or the other');
		}
	}
}