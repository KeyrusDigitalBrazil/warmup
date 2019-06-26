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

	startDetection(outerBounds: IBound, innerBounds: IBound, callback: () => any): angular.IPromise<void> {
		this.validateBounds(outerBounds);

		if (!callback) {
			throw new Error('Callback function is required');
		}

		this.notificationPanelBounds = outerBounds;
		this.mouseLeaveCallback = callback;

		this.$document.on('mousemove', this._onMouseMove);

		if (innerBounds) {
			this.validateBounds(innerBounds);

			this._remoteStartDetection(innerBounds);
		}

		return this.$q.when();
	}

	stopDetection(): angular.IPromise<void> {
		this.$document.off('mousemove', this._onMouseMove);

		this.notificationPanelBounds = null;
		this.mouseLeaveCallback = null;

		this._remoteStopDetection();

		return this.$q.when();
	}

	protected _callCallback(): angular.IPromise<void> {

		this._getCallback().then((callback: (() => void)) => {
			if (callback) {
				callback();
			}
		});

		return this.$q.when();
	}

	protected _getBounds(): angular.IPromise<IBound> {
		return this.$q.when(this.notificationPanelBounds);
	}

	protected _getCallback(): angular.IPromise<(() => void)> {
		return this.$q.when(this.mouseLeaveCallback);
	}

	private validateBounds(bounds: IBound) {
		if (!bounds) {
			throw new Error('Bounds are required for mouse leave detection');
		}

		if (!bounds.hasOwnProperty('x')) {
			throw new Error('Bounds must contain the x coordinate');
		}

		if (!bounds.hasOwnProperty('y')) {
			throw new Error('Bounds must contain the y coordinate');
		}

		if (!bounds.hasOwnProperty('width')) {
			throw new Error('Bounds must contain the width dimension');
		}

		if (!bounds.hasOwnProperty('height')) {
			throw new Error('Bounds must contain the height dimension');
		}
	}
}