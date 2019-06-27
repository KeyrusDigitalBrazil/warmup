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
import {IContextualMenuConfiguration, IFeature, IPrioritized, TypedMap} from 'smarteditcommons';

/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IContextualMenuButton
 * @description
 * payload passed to ContextualMenuService#addItems, to describe a contextual menu button
 */
export interface IContextualMenuButton extends IFeature, IPrioritized {
	/**
	 * @ngdoc object
	 * @name smarteditServicesModule.object:action
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {Object} action The action to be performed when clicking the menu item.
	 * Action is an object that must contain exactly one of callback | callbacks | template | templateUrl<br />
	 */
	action?: {
		/**
		 * @ngdoc property
		 * @name smarteditServicesModule.property:template
		 * @propertyOf smarteditServicesModule.object:action
		 * @description {string} template is an html string that will displayed below the menu item when the item is clicked.
		 */
		template?: string,
		/**
		 * @ngdoc property
		 * @name smarteditServicesModule.property:templateUrl
		 * @propertyOf smarteditServicesModule.object:action
		 * @description {string} templateUrl is the same as template but instead of passing a string, pass a url to an html file.
		 */
		templateUrl?: string;
		/**
		 * @ngdoc property
		 * @name smarteditServicesModule.property:callbacks
		 * @propertyOf smarteditServicesModule.object:action
		 * @description {object} map of DOM events occuring on the contextual menu button and callbacks to be invoked when they occur
		 */
		callbacks?: TypedMap<(configuration?: IContextualMenuConfiguration, $event?: Event) => void>;
		/**
		 * @ngdoc method
		 * @name callback
		 * @methodOf smarteditServicesModule.object:action
		 * @description {Function} callback A function executed on clicking of the menu item. It is invoked with the component specific {@link IContextualMenuConfiguration IContextualMenuConfiguration}
		 * @param {IContextualMenuConfiguration=} configuration the smartedit component specific {@link smarteditServicesModule.interface:IContextualMenuConfiguration IContextualMenuConfiguration}
		 * @param {$event=} $event the yjQuery event triggering the callback
		 */
		callback?(configuration?: IContextualMenuConfiguration, $event?: Event): void;
	};
	/**
	 * @ngdoc property
	 * @name i18nKey
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} i18nKey i18nKey Is the message key of the contextual menu item to be translated.
	 */
	i18nKey?: string;
	/**
	 * @ngdoc property
	 * @name displayClass
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} displayClass Contains the CSS classes used to style the contextual menu item
	 */
	displayClass?: string;
	/**
	 * @ngdoc property
	 * @name displayIconClass
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} displayIconClass Contains the CSS classes used to style the non-idle icon of the contextual menu item to be displayed.
	 */
	displayIconClass?: string;
	/**
	 * @ngdoc property
	 * @name displaySmallIconClass
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} displaySmallIconClass displaySmallIconClass Contains the location of the smaller version of the icon to be displayed when the menu item is part of the More... menu options.
	 */
	displaySmallIconClass?: string;
	/**
	 * @ngdoc property
	 * @name iconIdle
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} iconIdle iconIdle Contains the location of the idle icon of the contextual menu item to be displayed.
	 */
	iconIdle?: string;
	/**
	 * @ngdoc property
	 * @name iconNonIdle
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String=} iconNonIdle iconNonIdle Contains the location of the non-idle icon of the contextual menu item to be displayed.
	 */
	iconNonIdle?: string;
	/**
	 * @ngdoc property
	 * @name templateUrl
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description
	 * deprecated since 6.5, see {@link smarteditServicesModule.object:action action}
	 * @deprecated since 6.5, see {@link smarteditServicesModule.object:action action}
	 */
	templateUrl?: string;
	/**
	 * @ngdoc property
	 * @name regexpKeys
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {String} regexpKeys array of regular expressions matching component types eligible for this button
	 */
	regexpKeys: string[];
	/**
	 * @ngdoc property
	 * @name callbacks
	 * @propertyOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description
	 * deprecated since 6.7, see {@link smarteditServicesModule.object:action action}
	 * @deprecated since 6.7, see {@link smarteditServicesModule.object:action action}
	 */
	callbacks?: TypedMap<(configuration?: IContextualMenuConfiguration, $event?: Event) => void>;
	/**
	 * @ngdoc method
	 * @name condition
	 * @methodOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description {Function} contextualMenuItemsMap.condition condition Is an optional entry that holds the condition function required to activate the menu item. It is invoked with a {@link smarteditServicesModule.interface:IContextualMenuConfiguration IContextualMenuConfiguration} payload
	 */
	condition?(configuration: IContextualMenuConfiguration): boolean | angular.IPromise<boolean>;
	/**
	 * @ngdoc method
	 * @name callback
	 * @methodOf smarteditServicesModule.interface:IContextualMenuButton
	 * @description
	 * deprecated since 6.7, see {@link smarteditServicesModule.object:action action}
	 * @deprecated since 6.7, see {@link smarteditServicesModule.object:action action}
	 */
	callback?(configuration?: IContextualMenuConfiguration, $event?: Event): void;
}
