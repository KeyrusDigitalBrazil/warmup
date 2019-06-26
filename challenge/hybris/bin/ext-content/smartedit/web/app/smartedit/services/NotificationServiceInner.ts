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

import {GatewayProxied, INotificationService} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:NotificationService
 * 
 * @description
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
@GatewayProxied('pushNotification', 'removeNotification', 'removeAllNotifications')
export class NotificationService extends INotificationService {

	constructor() {
		super();
	}
}