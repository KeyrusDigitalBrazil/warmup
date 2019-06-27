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
const baseUrl: string = "static-resources/images";

/** @internal */
export interface DeviceSupport {
	icon: string;
	selectedIcon: string;
	blueIcon: string;
	type: string;
	width: number | string;
	height?: number | string;
	default?: boolean;
}

/** @internal */
export const DEVICE_SUPPORTS: DeviceSupport[] = [{
	icon: `${baseUrl}/icon_res_phone.png`,
	selectedIcon: `${baseUrl}/icon_res_phone_s.png`,
	blueIcon: `${baseUrl}/icon_res_phone_b.png`,
	type: "phone",
	width: 480
}, {
	icon: `${baseUrl}/icon_res_wphone.png`,
	selectedIcon: `${baseUrl}/icon_res_wphone_s.png`,
	blueIcon: `${baseUrl}/icon_res_wphone_b.png`,
	type: "wide-phone",
	width: 600
}, {
	icon: `${baseUrl}/icon_res_tablet.png`,
	selectedIcon: `${baseUrl}/icon_res_tablet_s.png`,
	blueIcon: `${baseUrl}/icon_res_tablet_b.png`,
	type: "tablet",
	width: 700
}, {
	icon: `${baseUrl}/icon_res_wtablet.png`,
	selectedIcon: `${baseUrl}/icon_res_wtablet_s.png`,
	blueIcon: `${baseUrl}/icon_res_wtablet_b.png`,
	type: "wide-tablet",
	width: 1024
}, {
	icon: `${baseUrl}/icon_res_desktop.png`,
	selectedIcon: `${baseUrl}/icon_res_desktop_s.png`,
	blueIcon: `${baseUrl}/icon_res_desktop_b.png`,
	type: "desktop",
	width: 1200
}, {
	type: "wide-desktop",
	icon: `${baseUrl}/icon_res_wdesktop.png`,
	selectedIcon: `${baseUrl}/icon_res_wdesktop_s.png`,
	blueIcon: `${baseUrl}/icon_res_wdesktop_b.png`,
	width: "100%",
	default: true
}];
