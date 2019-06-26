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
 * @description
 * Interface for cms-component information
 */
export interface ICMSComponent {
	name: string;
	typeCode: string;
	itemtype: string;
	uid: string;
	uuid: string;
	visible: boolean;
	cloneable: boolean;
}
