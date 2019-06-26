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
export interface ITriggerTab {
	id: string;
	title: string;
	templateUrl: string;
	isTriggerDefined(): boolean; // Function returns 'true' if trigger is created and correct, otherwise 'false'
	isValidOrEmpty(): boolean; // Function returns 'true' if trigger is correct or is empty, otherwise 'false'
	// In multiple trigger environment all trigger tabs must be valid or empty, and at least one must have defined trigger
}
