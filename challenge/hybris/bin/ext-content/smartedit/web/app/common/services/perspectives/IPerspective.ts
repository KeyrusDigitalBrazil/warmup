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
 * @name smarteditServicesModule.interface:IPerspective
 * 
 * @description
 * Interface for IPerspective
 */
export interface IPerspective {
    /**
     * @ngdoc property
     * @name key
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description The key that uniquely identifies the perspective in the registry.
     */
	key: string;

    /**
     * @ngdoc property
     * @name nameI18nKey
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description The i18n key that stores the perspective name to be translated.
     */
	nameI18nKey: string;

    /**
     * @ngdoc property
     * @name descriptionI18nKey
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description The i18n key that stores the perspective description to be translated. The description is used as a tooltip in the web application. This is an optional parameter.
     */
	descriptionI18nKey?: string;

    /**
     * @ngdoc property
     * @name features
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description A list of feature keys to be bound to the perspective.
     */
	features: string[];

    /**
     * @ngdoc property
     * @name perspectives
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description A list of referenced perspectives to be extended to this system perspective. This list is optional.
     */
	perspectives?: string[];

    /**
     * @ngdoc property
     * @name permissions
     * @propertyOf smarteditServicesModule.interface:IPerspective
     * @description A list of permission names to be bound to the perspective to determine if the user is allowed to see it and use it. This list is optional.
     */
	permissions?: string[];

	/**
	 * @ngdoc property
	 * @name isHotkeyDisabled
	 * @propertyOf smarteditServicesModule.interface:IPerspective
	 * @description used to prevent the default enablement of the 'hot key' mechanism. Default is false.
	 */
	isHotkeyDisabled?: boolean;
}