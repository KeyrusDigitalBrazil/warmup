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
import {IGatewayPostMessageData, MessageGateway} from './MessageGateway';
import {SystemEventService} from './../SystemEventService';
import {CloneableUtils, TypedMap, WindowUtils} from 'smarteditcommons';
import {SeInjectable} from './../dependencyInjection/di';

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:GatewayFactory
 *
 * @description
 * The Gateway Factory controls the creation of and access to {@link smarteditCommonsModule.service:MessageGateway MessageGateway}
 * instances.
 *
 * To construct and access a gateway, you must use the GatewayFactory's createGateway method and provide the channel
 * ID as an argument. If you try to create the same gateway twice, the second call will return a null.
 */
@SeInjectable()
export class GatewayFactory {

	private messageGatewayMap: TypedMap<MessageGateway> = {};

	/** @internal */
	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private $window: angular.IWindowService,
		private $injector: angular.auto.IInjectorService,
		private $timeout: angular.ITimeoutService,
		private systemEventService: SystemEventService,
		private cloneableUtils: CloneableUtils,
		private getOrigin: any,
		private regExpFactory: any,
		private isIframe: any,
		private windowUtils: WindowUtils,
		private WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY: string,
		private TIMEOUT_TO_RETRY_PUBLISHING: number
	) {}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:GatewayFactory#initListener
     * @methodOf smarteditCommonsModule.service:GatewayFactory
     *
     * @description
     * Initializes a postMessage event handler that dispatches the handling of an event to the specified gateway.
     * If the corresponding gateway does not exist, an error is logged.
     */

	initListener() {

		const processedPrimaryKeys: string[] = [];

		// Listen to message from child window
		this.$window.addEventListener('message', (e: MessageEvent) => {

			if (this._isAllowed(e.origin)) {
				// add control on e.origin
				const event: IGatewayPostMessageData = e.data;

				if (processedPrimaryKeys.indexOf(event.pk) > -1) {
					return;
				}
				processedPrimaryKeys.push(event.pk);
				this.$log.debug('message event handler called', event.eventId);

				const gatewayId: string = event.gatewayId;
				const gateway: MessageGateway = this.messageGatewayMap[gatewayId];
				if (!gateway) {
					this.$log.debug('Incoming message on gateway ' + gatewayId + ', but no destination exists.');
					return;
				}

				gateway.processEvent(event);
			} else {
				this.$log.error("disallowed storefront is trying to communicate with smarteditcontainer");
			}

		}, false);
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:GatewayFactory#createGateway
     * @methodOf smarteditCommonsModule.service:GatewayFactory
     *
     * @description
     * Creates a gateway for the specified gateway identifier and caches it in order to handle postMessage events
     * later in the application lifecycle. This method will fail on subsequent calls in order to prevent two
     * clients from using the same gateway.
     *
     * @param {String} gatewayId The identifier of the gateway.
     * @returns {MessageGateway} Returns the newly created Message Gateway or null.
     */
	createGateway(gatewayId: string): MessageGateway {
		if (this.messageGatewayMap[gatewayId]) {
			this.$log.error('Message Gateway for ' + gatewayId + ' already reserved');
			return null;
		}

		this.messageGatewayMap[gatewayId] = new MessageGateway(this.$q, this.$log, this.$timeout,
			this.systemEventService, this.cloneableUtils, this.windowUtils,
			this.TIMEOUT_TO_RETRY_PUBLISHING, gatewayId);

		return this.messageGatewayMap[gatewayId];
	}

    /**
     * allowed if receiving end is frame or [container + (white listed storefront or same origin)]
     */
	private _isAllowed(origin: string): boolean {
		const whiteListedStorefronts: string[] = this.$injector.has(this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY) ?
			this.$injector.get(this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY) : [];
		return this.isIframe() || this.getOrigin() === origin || (whiteListedStorefronts.some((allowedURI: string) => {
			return this.regExpFactory(allowedURI).test(origin);
		}));
	}
}