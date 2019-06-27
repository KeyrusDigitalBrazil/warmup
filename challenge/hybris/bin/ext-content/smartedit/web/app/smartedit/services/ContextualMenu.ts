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
import {IContextualMenuButton} from 'smarteditcommons';
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ContextualMenu
 * @description
 * A full representation of a component contextual menu,
 * specifying the layout of {@link smarteditServicesModule.interface:IContextualMenuButton buttons}
 * to be displayed for a speicific smartedit component
 * 
 */
export interface ContextualMenu {

	/**
	 * @ngdoc property
	 * @name leftMenuItems
	 * @propertyOf smarteditServicesModule.interface:ContextualMenu
	 * @description leftMenuItems the ordered list of {@link smarteditServicesModule.interface:IContextualMenuButton IContextualMenuButton}
	 * to appear on the "left" side of the contextual menu
	 */
	leftMenuItems: IContextualMenuButton[];
	/**
	 * @ngdoc property
	 * @name moreMenuItems
	 * @propertyOf smarteditServicesModule.interface:ContextualMenu
	 * @description leftMenuItems the ordered list of {@link smarteditServicesModule.interface:IContextualMenuButton IContextualMenuButton}
	 * to appear under the "more" menu button
	 */
	moreMenuItems: IContextualMenuButton[];
}