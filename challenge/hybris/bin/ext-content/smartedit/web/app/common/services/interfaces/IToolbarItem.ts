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
import {IFeature, IPrioritized} from 'smarteditcommons';

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IToolbarItem
 * 
 * @description
 * Interface for ToolbarItem
 */

export interface IToolbarItem extends IFeature, IPrioritized {

    /**
     * @ngdoc property
     * @name iconClassName
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description List of classes used to display icons from fonts
     */
	iconClassName?: string;

    /**
     * @ngdoc property
     * @name icons
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description A list of image URLs for the icon images to be displayed in the toolbar for the items. The images are only available for ACTION and HYBRID_ACTION toolbar items.
     */
	icons?: string[];

    /**
     * @ngdoc property
     * @name include
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The URL to the HTML template. By default, templates are available for TEMPLATE and HYBRID_ACTION toolbar items.
     */
	include?: string;

    /**
     * @ngdoc property
     * @name contextTemplate
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The template of the context to be displayed. An optional parameter.
     */
	contextTemplate?: string;

    /**
     * @ngdoc property
     * @name contextTemplateUrl
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The templateUrl that prints the context associated to the toolbar item. An optional parameter.
     */
	contextTemplateUrl?: string;

    /**
     * @ngdoc property
     * @name keepAliveOnClose
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description keepAliveOnClose keeps the dropdown content in the DOM on close. This is an optional parameter.
     */
	keepAliveOnClose?: boolean;

    /**
     * @ngdoc property
     * @name section
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description Determines the sections(left, middle or right) of the item in the toolbar.
     */
	section?: string;

    /**
     * @ngdoc property
     * @name toolbarId
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The key that uniquely identifies the toolbar that the feature is added to.
     */
	toolbarId: string;

    /**
     * @ngdoc property
     * @name type
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The type of toolbar item. The possible value are: TEMPLATE, ACTION, and HYBRID_ACTION.
     */
	type: string;

    /**
     * @ngdoc property
     * @name callback
     * @propertyOf smarteditServicesModule.interface:IToolbarItem
     * @description The callback that is triggered when the toolbar action item is clicked.
     */
	callback?: () => void;
}