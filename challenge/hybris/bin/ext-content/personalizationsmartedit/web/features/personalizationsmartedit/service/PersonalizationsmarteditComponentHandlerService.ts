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
import {SeInjectable, SeValueProvider} from 'smarteditcommons';
import {ComponentHandlerService} from 'smartedit';


export const COMPONENT_CONTAINER_TYPE_PROVIDER: SeValueProvider = {
	provide: 'COMPONENT_CONTAINER_TYPE',
	useValue: 'CxCmsComponentContainer'

};

export const CONTAINER_SOURCE_ID_ATTR_PROVIDER: SeValueProvider = {
	provide: 'CONTAINER_SOURCE_ID_ATTR',
	useValue: 'data-smartedit-container-source-id'

};


@SeInjectable()
export class PersonalizationsmarteditComponentHandlerService {

	constructor(
		protected componentHandlerService: ComponentHandlerService,
		protected yjQuery: any,
		protected CONTAINER_TYPE_ATTRIBUTE: string,
		protected CONTAINER_ID_ATTRIBUTE: string,
		protected TYPE_ATTRIBUTE: string,
		protected CONTENT_SLOT_TYPE: string,
		protected COMPONENT_CONTAINER_TYPE: string,
		protected CONTAINER_SOURCE_ID_ATTR: string) {
	}

	getParentContainerForComponent(component: JQuery<HTMLElement> | JQuery): JQuery {
		const parent: JQuery = component.closest('[' + this.CONTAINER_TYPE_ATTRIBUTE + '=' + this.COMPONENT_CONTAINER_TYPE + ']');
		return parent;
	}

	getParentContainerIdForComponent(component: JQuery<HTMLElement> | JQuery): string {
		const parent = component.closest('[' + this.CONTAINER_TYPE_ATTRIBUTE + '=' + this.COMPONENT_CONTAINER_TYPE + ']');
		return parent.attr(this.CONTAINER_ID_ATTRIBUTE);
	}

	getParentContainerSourceIdForComponent(component: JQuery<HTMLElement> | JQuery): string {
		const parent = component.closest('[' + this.CONTAINER_TYPE_ATTRIBUTE + '=' + this.COMPONENT_CONTAINER_TYPE + ']');
		return parent.attr(this.CONTAINER_SOURCE_ID_ATTR);
	}

	getParentSlotForComponent(component: JQuery<HTMLElement> | JQuery): JQuery {
		const parent: JQuery = component.closest('[' + this.TYPE_ATTRIBUTE + '=' + this.CONTENT_SLOT_TYPE + ']');
		return parent;
	}

	getParentSlotIdForComponent(component: HTMLElement | JQuery): string {
		return this.componentHandlerService.getParentSlotForComponent(component);
	}

	getOriginalComponent(componentId: string, componentType: string): JQuery {
		return this.componentHandlerService.getOriginalComponent(componentId, componentType);
	}

	isExternalComponent(componentId: string, componentType: string): boolean {
		return this.componentHandlerService.isExternalComponent(componentId, componentType);
	}

	getCatalogVersionUuid(component: HTMLElement | JQuery): string {
		return this.componentHandlerService.getCatalogVersionUuid(component);
	}

	getAllSlotsSelector(): string {
		return this.componentHandlerService.getAllSlotsSelector();
	}

	getFromSelector(selector: string | HTMLElement | JQuery): JQuery {
		return this.yjQuery(selector);
	}

	getContainerSourceIdForContainerId(containerId: string) {
		let containerSelector: string = this.getAllSlotsSelector();
		containerSelector += ' [' + this.CONTAINER_ID_ATTRIBUTE + '="' + containerId + '"]'; // space at beginning is important
		const container: any = this.getFromSelector(containerSelector);
		return container[0] ? container[0].getAttribute(this.CONTAINER_SOURCE_ID_ATTR) : "";
	}
}
