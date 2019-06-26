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
import * as lo from 'lodash';
import * as angular from 'angular';
import {SeInjectable} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:ComponentHandlerService
 *
 * @description
 * Handles all get/set component related operations
 */
@SeInjectable()
export class ComponentHandlerService {


	private static PATTERN_SMARTEDIT_PAGE_UID: RegExp = /smartedit-page-uid\-(\S+)/;

	private static PATTERN_SMARTEDIT_PAGE_UUID: RegExp = /smartedit-page-uuid\-(\S+)/;

	private static PATTERN_SMARTEDIT_CATALOG_VERSION_UUID: RegExp = /smartedit-catalog-version-uuid\-(\S+)/;


	/* @internal */
	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private yjQuery: any,
		private isBlank: (value: string) => boolean,
		private lodash: lo.LoDashStatic,
		private OVERLAY_ID: string,
		private CONTENT_SLOT_TYPE: string,
		private ID_ATTRIBUTE: string,
		private UUID_ATTRIBUTE: string,
		private ELEMENT_UUID_ATTRIBUTE: string,
		private TYPE_ATTRIBUTE: string,
		private COMPONENT_CLASS: string,
		private OVERLAY_COMPONENT_CLASS: string,
		private CONTAINER_ID_ATTRIBUTE: string,
		private CONTAINER_TYPE_ATTRIBUTE: string,
		private CATALOG_VERSION_UUID_ATTRIBUTE: string) {
	}

	/** @internal */
	try(func: () => string): angular.IPromise<string> {
		try {
			return this.$q.when(func());
		} catch (e) {
			this.$log.warn('Missing SmartEdit attributes on body element of the storefront - SmartEdit will resume once the attributes are added');
			return this.$q.reject(e);
		}
	}

    /**
     * @deprecated since 6.7 - use PageInfoService
     */
	getPageUID(): angular.IPromise<string> {
		return this.try(this.getBodyClassAttributeByRegEx.bind(this, ComponentHandlerService.PATTERN_SMARTEDIT_PAGE_UID));
	}

    /**
     * @deprecated since 6.7 - use PageInfoService
     */
	getPageUUID(): angular.IPromise<string> {
		return this.try(this.getBodyClassAttributeByRegEx.bind(this, ComponentHandlerService.PATTERN_SMARTEDIT_PAGE_UUID));
	}

    /**
     * @deprecated since 6.7 - use PageInfoService
     */
	getCatalogVersionUUIDFromPage(): angular.IPromise<string> {
		return this.try(this.getBodyClassAttributeByRegEx.bind(this, ComponentHandlerService.PATTERN_SMARTEDIT_CATALOG_VERSION_UUID));
	}


	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getFromSelector
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Wrapper around yjQuery selector
	 *
	 * @param {String | HTMLElement | JQuery} selector selector as per yjQuery API or HTMLElement
	 * 
	 * @return {JQuery} a yjQuery object for the given selector
	 * 
	 * @deprecated since 6.7 - use yJquery service instead
	 */
	getFromSelector(selector: string | HTMLElement | JQuery): JQuery {
		return this.yjQuery(selector);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOverlay
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves a handler on the smartEdit overlay div
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @return {JQuery} The #smarteditoverlay JQuery Element
	 */
	getOverlay(): JQuery {
		return this.getFromSelector('#' + this.OVERLAY_ID);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#isOverlayOn
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * determines whether the overlay is visible
	 * This method can only be invoked from the smartEdit application and not the smartEdit iframe.
	 * 
	 * @return {boolean} true if the overlay is visible
	 */
	isOverlayOn(): boolean {
		return this.getOverlay().length && this.getOverlay().is(":visible");
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getComponentUnderSlot
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 *
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * @param {String} smarteditSlotId the slot id of the slot containing the component as per the smartEdit contract with the storefront
	 * @param {String =} cssClass the css Class to further restrict the search on. This parameter is optional.
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getComponentUnderSlot(smarteditComponentId: string, smarteditComponentType: string, smarteditSlotId: string, cssClass: string = null): JQuery {
		const slotQuery: string = this.buildComponentQuery(smarteditSlotId, this.CONTENT_SLOT_TYPE);
		const componentQuery: string = this.buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass);
		const selector: string = slotQuery + ' ' + componentQuery;

		return this.getFromSelector(selector);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 *
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * @param {String =} cssClass the css Class to further restrict the search on. This parameter is optional.
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getComponent(smarteditComponentId: string, smarteditComponentType: string, cssClass: string = null): JQuery {
		return this.getFromSelector(this.buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass));
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOriginalComponentWithinSlot
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type and slot ID
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * @param {String} slotId the ID of the slot within which the component resides
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getOriginalComponentWithinSlot(smarteditComponentId: string, smarteditComponentType: string, slotId: string): JQuery {
		return this.getComponentUnderSlot(smarteditComponentId, smarteditComponentType, slotId, this.COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOriginalComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 *
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getOriginalComponent(smarteditComponentId: string, smarteditComponentType: string): JQuery {
		return this.getComponent(smarteditComponentId, smarteditComponentType, this.COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOverlayComponentWithinSlot
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component of the overlay layer identified by its smartEdit id, smartEdit type and slot ID
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * @param {String} slotId the ID of the slot within which the component resides
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getOverlayComponentWithinSlot(smarteditComponentId: string, smarteditComponentType: string, slotId: string): JQuery {
		return this.getComponentUnderSlot(smarteditComponentId, smarteditComponentType, slotId, this.OVERLAY_COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOverlayComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around the smartEdit component of the overlay layer corresponding to the storefront layer component passed as argument
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {Object} originalComponent the DOM element in the storefront layer
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 */
	getOverlayComponent(originalComponent: JQuery): JQuery {
		const slotId: string = this.getParentSlotForComponent(originalComponent.parent());
		if (slotId) {
			return this.getComponentUnderSlot(originalComponent.attr(this.ID_ATTRIBUTE), originalComponent.attr(this.TYPE_ATTRIBUTE), slotId, this.OVERLAY_COMPONENT_CLASS);
		} else {
			return this.getComponent(originalComponent.attr(this.ID_ATTRIBUTE), originalComponent.attr(this.TYPE_ATTRIBUTE), this.OVERLAY_COMPONENT_CLASS);
		}
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getComponentInOverlay
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a smartEdit component of the overlay div identified by its smartEdit id, smartEdit type
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * 
	 * @return {JQuery} a yjQuery object wrapping the searched component
	 * 
	 * @deprecated since 6.5, use {@link smarteditServicesModule.componentHandlerService#methodsOf_getOverlayComponentWithinSlot getOverlayComponentWithinSlot} or {@link smarteditServicesModule.componentHandlerService#methodsOf_getOverlayComponent getOverlayComponent} 
	 */
	getComponentInOverlay(smarteditComponentId: string, smarteditComponentType: string): JQuery {
		return this.getComponent(smarteditComponentId, smarteditComponentType, this.OVERLAY_COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getParentSlotForComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the the slot ID for a given element
	 * 
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to search the parent
	 * 
	 * @return {String} the slot ID for that particular component
	 */
	getParentSlotForComponent(component: HTMLElement | JQuery): string {
		const parent: JQuery = this.getFromSelector(component).closest('[' + this.TYPE_ATTRIBUTE + '=' + this.CONTENT_SLOT_TYPE + ']');
		return parent.attr(this.ID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getComponentPositionInSlot
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the position of a component within a slot
	 *
	 * @param {String} slotId the slot id as per the smartEdit contract with the storefront
	 * @param {String} componentId the component id as per the smartEdit contract with the storefront
	 *
	 * @return {number} the position of the component within a slot
	 */
	getComponentPositionInSlot(slotId: string, componentId: string): number {
		const components: JQuery[] = this.getOriginalComponentsWithinSlot(slotId);

		return this.lodash.findIndex(components, (component: JQuery) => {
			return this.getId(component) === componentId;
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getOriginalComponentsWithinSlot
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the yjQuery wrapper around a list of smartEdit components contained in the slot identified by the given slotId.
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * 
	 * @param {String} slotId the ID of the slot within which the component resides
	 * 
	 * @return {Array<JQuery>} The list of searched components yjQuery objects
	 */
	getOriginalComponentsWithinSlot(slotId: string): JQuery[] {
		return this.yjQuery(this.buildComponentsInSlotQuery(slotId));
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getSlotOperationRelatedId
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the id that is relevant to be able to perform slot related operations for this components
	 * It typically is {@link seConstantsModule.CONTAINER_ID_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.ID_ATTRIBUTE}
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the id
	 * 
	 * @return {String} the slot operations related id
	 */
	getSlotOperationRelatedId(component: HTMLElement | JQuery): string {
		component = this.getFromSelector(component) as JQuery;
		const containerId: string = component.attr(this.CONTAINER_ID_ATTRIBUTE);
		return containerId && component.attr(this.CONTAINER_TYPE_ATTRIBUTE) ? containerId : component.attr(this.ID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getSlotOperationRelatedUuid
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the id that is relevant to be able to perform slot related operations for this components
	 * It typically is {@link seConstantsModule.CONTAINER_ID_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.ID_ATTRIBUTE}
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the Uuid
	 *
	 * @return {String} the slot operations related Uuid
	 */
	getSlotOperationRelatedUuid(component: HTMLElement | JQuery): string {
		const containerId: string = this.getFromSelector(component).attr(this.CONTAINER_ID_ATTRIBUTE);
		return containerId && this.getFromSelector(component).attr(this.CONTAINER_TYPE_ATTRIBUTE) ? containerId : this.getFromSelector(component).attr(this.UUID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getParent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the direct smartEdit component parent of a given component.
	 * The parent is fetched in the same layer (original storefront or smartEdit overlay) as the child 
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to search a parent
	 * 
	 * @return {JQuery} a yjQuery object wrapping the smae-layer parent component
	 */
	getParent(component: HTMLElement | JQuery): JQuery {
		component = this.getFromSelector(component) as JQuery;
		const parentClassToLookFor: string = component.hasClass(this.COMPONENT_CLASS) ? this.COMPONENT_CLASS : (component.hasClass(this.OVERLAY_COMPONENT_CLASS) ? this.OVERLAY_COMPONENT_CLASS : null);
		if (this.isBlank(parentClassToLookFor)) {
			throw new Error('componentHandlerService.getparent.error.component.from.unknown.layer');
		}
		return component.closest("." + parentClassToLookFor + "[" + this.ID_ATTRIBUTE + "]" + "[" + this.ID_ATTRIBUTE + "!='" + component.attr(this.ID_ATTRIBUTE) + "']");
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getClosestSmartEditComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Returns the closest parent (or self) being a smartEdit component
	 *
	 * @param {HTMLElement | JQuery} component the DOM/yjQuery element for which to search a parent
	 * 
	 * @return {JQuery} The closest closest parent (or self) being a smartEdit component
	 */
	getClosestSmartEditComponent(component: HTMLElement | JQuery): JQuery {
		return this.getFromSelector(component).closest("." + this.COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#isSmartEditComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Determines whether a DOM/yjQuery element is a smartEdit component
	 *
	 * @param {HTMLElement | JQuery} component the DOM/yjQuery element for which to check if it's a SmartEdit component
	 * 
	 * @return {boolean} true if DOM/yjQuery element is a smartEdit component
	 */
	isSmartEditComponent(component: HTMLElement | JQuery): boolean {
		return this.getFromSelector(component).hasClass(this.COMPONENT_CLASS);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#setId
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Sets the smartEdit component id of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to set the id
	 * @param {String} id the id to be set
	 * 
	 * @return {JQuery} component the yjQuery component
	 */
	setId(component: HTMLElement | JQuery, id: string): JQuery {
		return this.getFromSelector(component).attr(this.ID_ATTRIBUTE, id);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getId
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the smartEdit component id of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the id
	 * 
	 * @return {String} the component id
	 */
	getId(component: HTMLElement | JQuery): string {
		return this.getFromSelector(component).attr(this.ID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getUuid
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the smartEdit component id of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the id
	 * 
	 * @return {String} the component id
	 */
	getUuid(component: HTMLElement | JQuery): string {
		return this.getFromSelector(component).attr(this.UUID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getCatalogVersionUuid
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the smartEdit component id of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the id
	 * 
	 * @return {String} the component id
	 */
	getCatalogVersionUuid(component: HTMLElement | JQuery): string {
		return this.getFromSelector(component).attr(this.CATALOG_VERSION_UUID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#setType
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Sets the smartEdit component type of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to set the type
	 * @param {String} type the type to be set
	 * 
	 * @return {JQuery} component the yjQuery component
	 */
	setType(component: HTMLElement | JQuery, type: string): JQuery {
		return this.getFromSelector(component).attr(this.TYPE_ATTRIBUTE, type);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getType
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the smartEdit component type of a given component
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the type
	 * 
	 * @return {String} the component type
	 */
	getType(component: HTMLElement | JQuery): string {
		return this.getFromSelector(component).attr(this.TYPE_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getSlotOperationRelatedType
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Gets the type that is relevant to be able to perform slot related operations for this components
	 * It typically is {@link seConstantsModule.CONTAINER_TYPE_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.TYPE_ATTRIBUTE}
	 *
	 * @param {HTMLElement | JQuery} component the yjQuery component for which to get the type
	 * 
	 * @return {String} the slot operations related type
	 */
	getSlotOperationRelatedType(component: HTMLElement | JQuery): string {
		const containerType: string = this.getFromSelector(component).attr(this.CONTAINER_TYPE_ATTRIBUTE);
		return containerType && this.getFromSelector(component).attr(this.CONTAINER_ID_ATTRIBUTE) ? containerType : this.getFromSelector(component).attr(this.TYPE_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getAllComponentsSelector
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the DOM selector matching all smartEdit components that are not of type ContentSlot
	 * 
	 * @return {String} components selector
	 */
	getAllComponentsSelector(): string {
		return "." + this.COMPONENT_CLASS + "[" + this.TYPE_ATTRIBUTE + "!='ContentSlot']";
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getAllSlotsSelector
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the DOM selector matching all smartEdit components that are of type ContentSlot
	 * 
	 * @return {String} the slots selector
	 */
	getAllSlotsSelector(): string {
		return "." + this.COMPONENT_CLASS + "[" + this.TYPE_ATTRIBUTE + "='ContentSlot']";
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getParentSlotUuidForComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Retrieves the the slot Uuid for a given element
	 * 
	 * @param {JQuery} the DOM element which represents the component
	 * 
	 * @return {String} the slot Uuid for that particular component
	 */
	getParentSlotUuidForComponent(component: JQuery): string {
		return component.closest('[' + this.TYPE_ATTRIBUTE + '=' + this.CONTENT_SLOT_TYPE + ']').attr(this.UUID_ATTRIBUTE);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#isExternalComponent
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Determines whether the component identified by the provided smarteditComponentId and smarteditComponentType
	 * resides in a different catalog version to the one of the current page.  
	 * 
	 * @param {String} smarteditComponentId the component id as per the smartEdit contract with the storefront
	 * @param {String} smarteditComponentType the component type as per the smartEdit contract with the storefront
	 * 
	 * @return {Boolean} flag that evaluates to true if the component resides in a catalog version different to 
	 * the one of the current page.  False otherwise. 
	 */
	isExternalComponent(smarteditComponentId: string, smarteditComponentType: string): boolean {
		const component: JQuery = this.getOriginalComponent(smarteditComponentId, smarteditComponentType);
		const componentCatalogVersionUuid: string = this.getCatalogVersionUuid(component);
		return componentCatalogVersionUuid !== this.getBodyClassAttributeByRegEx(ComponentHandlerService.PATTERN_SMARTEDIT_CATALOG_VERSION_UUID);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getBodyClassAttributeByRegEx 
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @param {RegExp} pattern Pattern of class names to search for
	 *
	 * @return {String} Class attributes from the body element of the storefront
	 */
	getBodyClassAttributeByRegEx(pattern: RegExp): string {
		try {
			const bodyClass: string = (this.getFromSelector('body') as JQuery).attr('class');
			return pattern.exec(bodyClass)[1];
		} catch {
			throw {
				name: "InvalidStorefrontPageError",
				message: "Error: the page is not a valid storefront page."
			};
		}
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getFirstSmartEditComponentChildren
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * This method can only be invoked from the smartEdit application and not the smartEdit container.
	 * Get first level smartEdit component children for a given node, regardless how deep they are found.
	 * The returned children may have different depths relatively to the parent:
	 * Example: a call on the body would return 4 components with ids: 1,2,3,4
	 * <pre>
	 * 	<body>
	 * 		<div>
	 * 			<component smartedit-component-id="1">
	 * 				<component smartedit-component-id="1_1"></component>
	 * 			</component>
	 * 			<component smartedit-component-id="2">
	 * 				<component smartedit-component-id="2_1"></component>
	 * 			</component>
	 * 		</div>
	 * 		<component smartedit-component-id="3">
	 * 			<component smartedit-component-id="3_1"></component>
	 * 		</component>
	 * 		<div>
	 * 			<div>
	 * 				<component smartedit-component-id="4">
	 * 					<component smartedit-component-id="4_1"></component>
	 * 				</component>
	 * 			</div>
	 * 		</div>
	 * 	</body>
	 * </pre>
	 * 
	 * @param {HTMLElement | JQuery} node any HTML/yjQuery Element
	 *
	 * @return {Array<JQuery>} The list of first level smartEdit component children for a given node, regardless how deep they are found.
	 */
	getFirstSmartEditComponentChildren(htmlElement: HTMLElement | JQuery): JQuery[] {
		const stem: any = this.getFromSelector(htmlElement); // using any as getCssPath is a yJquery augmentation
		const parentCssPath: string = stem.getCssPath();
		const firstChildrenRegex: RegExp = new RegExp(this.COMPONENT_CLASS, 'g');
		return stem.find("." + this.COMPONENT_CLASS).filter((index: number, component: JQuery) => {
			const match: string = (this.getFromSelector(component) as any).getCssPath().replace(parentCssPath, "").match(firstChildrenRegex);
			return match && match.length === 1;
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ComponentHandlerService#getComponentCloneInOverlay
	 * @methodOf smarteditServicesModule.service:ComponentHandlerService
	 * 
	 * @description
	 * Get component clone in overlay
	 * 
	 * @param {JQuery} the DOM element which represents the component
	 * 
	 * @return {JQuery} The component clone in overlay
	 */
	getComponentCloneInOverlay(component: JQuery): JQuery {
		const elementUuid: string = component.attr(this.ELEMENT_UUID_ATTRIBUTE);
		return this.getFromSelector("." + this.OVERLAY_COMPONENT_CLASS + "[" + this.ELEMENT_UUID_ATTRIBUTE + "='" + elementUuid + "']");
	}

	private buildComponentQuery(smarteditComponentId: string, smarteditComponentType: string, cssClass: string = null): string {
		let query: string = '';
		query += cssClass ? ('.' + cssClass) : '';
		query += '[' + this.ID_ATTRIBUTE + '=\'' + smarteditComponentId + '\']';
		query += '[' + this.TYPE_ATTRIBUTE + '=\'' + smarteditComponentType + '\']';
		return query;
	}

	private buildComponentsInSlotQuery(slotId: string): string {
		let query: string = '';
		query += ('.' + this.COMPONENT_CLASS);
		query += '[' + this.ID_ATTRIBUTE + '=\'' + slotId + '\']';
		query += '[' + this.TYPE_ATTRIBUTE + '=\'' + this.CONTENT_SLOT_TYPE + '\']';
		query += ' > ';
		query += '[' + this.ID_ATTRIBUTE + '\]';
		return query;
	}

}

