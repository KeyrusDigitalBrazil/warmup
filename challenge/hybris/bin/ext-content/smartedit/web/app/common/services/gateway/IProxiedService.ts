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
/**
 * @ngdoc interface
 * @name ProxiedService.interface:IProxiedService
 * @description
 * The IProxiedService interface represents a service that has one or more methods proxied over the
 * {@link smarteditCommonsModule.service:GatewayProxy gatewayProxy}
 */
export interface IProxiedService {

    /**
     * @ngdoc property
     * @name gatewayId - (readonly) string
     * @propertyOf ProxiedService.interface:IProxiedService
     * @description
     * gatewayId Is a unique string used to create the gateway communications channel between the smartedit and
     * smarteditContainer applications. This value should be unique to the application.
     *
     * A typical implementation would look as follows:
     *
     * Note: We use abstract class as a pseudo-interface for proxied services, due to technical constraints.
     *
     * ```js
     * // commons/.../IMyService.ts
     * abstract class IMyService implements IProxiedService {
     *
     *      get gatewayId(): string {
     *          return 'IMyService';
     *      };
     *
     *      myMethod(): string {
     *          'proxyFunction';
     *          return null;
     *      }
     * }
     * ```
     */
	readonly gatewayId?: string;

	[index: string]: any;
}
