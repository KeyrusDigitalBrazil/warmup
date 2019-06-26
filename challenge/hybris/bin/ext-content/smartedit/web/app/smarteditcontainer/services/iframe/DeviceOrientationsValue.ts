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
/** @internal */
export interface DeviceOrientation {
	orientation: 'vertical' | 'horizontal';
	key: string;
	default?: boolean;
}

/** @internal */
export const DEVICE_ORIENTATIONS: DeviceOrientation[] = [{
	orientation: 'vertical',
	key: 'se.deviceorientation.vertical.label',
	default: true
}, {
	orientation: 'horizontal',
	key: 'se.deviceorientation.horizontal.label'
}];
