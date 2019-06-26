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
import {GatewayProxied, IBound, INotificationMouseLeaveDetectionService, SeInjectable} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:NotificationMouseLeaveDetectionService
 * @extends {smarteditServicesModule.interface:INotificationMouseLeaveDetectionService}
 * @description
 * This service makes it possible to track the mouse position to detect when it leaves the notification panel.
 * It is solely meant to be used with the notificationService.
 */
/** @internal */
@GatewayProxied('stopDetection', '_remoteStartDetection', '_remoteStopDetection', '_callCallback')
@SeInjectable()
export class NotificationMouseLeaveDetectionService extends INotificationMouseLeaveDetectionService {

	private notificationPanelBounds: IBound;
	private mouseLeaveCallback: () => void;

	constructor(private $document: any, private $q: angular.IQService) {
		super();
		this.notificationPanelBounds = null;
		this.mouseLeaveCallback = null;
        /*
        * We need to bind the function in order for it to execute within the service's
        * scope and store it to be able to un-register the listener.
        */
		this._onMouseMove = this._onMouseMove.bind(this);
	}

	protected _remoteStartDetection(innerBounds: IBound): angular.IPromise<void> {
		this.notificationPanelBounds = innerBounds;
		this.$document.on('mousemove', this._onMouseMove);
		return this.$q.when();
	}

	protected _remoteStopDetection(): angular.IPromise<void> {
		this.$document.off('mousemove', this._onMouseMove);
		this.notificationPanelBounds = null;
		return this.$q.when();
	}

	protected _getBounds(): angular.IPromise<IBound> {
		return this.$q.when(this.notificationPanelBounds);
	}

	protected _getCallback(): angular.IPromise<(() => void)> {
		return this.$q.when(this.mouseLeaveCallback);
	}
}