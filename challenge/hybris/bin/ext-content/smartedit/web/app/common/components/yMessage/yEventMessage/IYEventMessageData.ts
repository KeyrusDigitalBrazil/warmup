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
 * @name smarteditCommonsModule.interface:IYEventMessageData
 *
 * @description
 * IYEventMessageData represents the data that can optionaly be passed to the event service
 * when firing an event to show a {@link smarteditCommonsModule.directive:YEventMessage YEventMessage} 
 */
export interface IYEventMessageData {

	/**
	 * @ngdoc property
	 * @name smarteditCommonsModule.interface:IYEventMessageData.property:description
	 * @propertyOf smarteditCommonsModule.interface:IYEventMessageData
	 *
	 * @description
	 * ```description?: string```
	 *
	 * The new description to be displayed in the yMessage
	 */
	description?: string;

	/**
	 * @ngdoc property
	 * @name smarteditCommonsModule.interface:IYEventMessageData.property:title
	 * @propertyOf smarteditCommonsModule.interface:IYEventMessageData
	 *
	 * @description
	 * ```title?: string```
	 *
	 * The new title to be displayed in the yMessage
	 */
	title?: string;
}
