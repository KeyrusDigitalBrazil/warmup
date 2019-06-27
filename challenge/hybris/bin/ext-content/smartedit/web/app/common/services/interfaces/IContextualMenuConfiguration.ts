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
 * @name smarteditServicesModule.interface:IContextualMenuConfiguration
 * @description
 * The smartedit component specific configuration being passed to contextualMenuService
 * to retrieve the appropriate list of {@link smarteditServicesModule.interface:IContextualMenuButton IContextualMenuButton}
 * and being passed to condition and callbak function of {@link smarteditServicesModule.interface:IContextualMenuButton IContextualMenuButton}
 */
export interface ComponentAttributes {
	[index: string]: string;
	smarteditCatalogVersionUuid: string;
	smarteditComponentId: string;
	smarteditComponentType: string;
	smarteditComponentUuid: string;
	smarteditElementUuid: string;
}

export interface IContextualMenuConfiguration {

    /** 
     * @param {Map<ComponentAttributes>} componentAttributes the map of all attributes prefixed with smartedit- collected on the DOM element
     */
	componentAttributes: ComponentAttributes;
    /** 
     * @param {String} componentType The type code of the selected component.
     */
	componentType: string;
    /**
     * @param {String} componentId The ID of the selected component.
     */
	componentId: string;
    /**
     * containerType The type code of the container of the component if applicable, this is optional.
     */
	containerType?: string;
    /**
     * @param {String} containerId The ID of the container of the component if applicable, this is optional.
     */
	containerId?: string;
    /**
     * @param {String} slotId the smarteditComponent id of the slot containing the component, null if a slot itself
     */
	slotId: string;
    /**
     * @param {String} slotUuid the UUID id of the slot containing the component, null if a slot itself
     */
	slotUuid: string;

    /**
     * @param {Number} iLeftBtns The number of visible contextual menu items for a specified component.
     */
	iLeftBtns: number;
    /**
     * @param {number} element The DOM element of selected component
     */
	element: HTMLElement;

    /**
     * @param {boolean} isComponentHidden hiiden state of the component
     */
	isComponentHidden: boolean;

}