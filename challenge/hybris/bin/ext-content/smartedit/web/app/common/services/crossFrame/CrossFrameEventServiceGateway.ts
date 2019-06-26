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

import {GatewayFactory} from 'smarteditcommons';
import {SeInjectable} from '../dependencyInjection/di';

/** @internal */
@SeInjectable()
export class CrossFrameEventServiceGateway {
	constructor(
		CROSS_FRAME_EVENT: string,
		gatewayFactory: GatewayFactory
	) {
		return gatewayFactory.createGateway(CROSS_FRAME_EVENT);
	}
}
