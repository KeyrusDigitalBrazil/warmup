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
import {
	CrossFrameEventService,
	InViewElementObserver,
	IDragAndDropEvents,
	IDragAndDropScrollingService,
	IDragEventType,
	IMousePosition,
	PolyfillService,
	SeInjectable
} from "smarteditcommons";

/** @internal */
@SeInjectable()
export class DragAndDropCrossOrigin {
	private currentElementHovered: JQuery;
	private lastElementHovered: JQuery;
	private isSearchingElement: boolean;

	constructor(
		private $document: angular.IDocumentService,
		private yjQuery: JQueryStatic,
		private crossFrameEventService: CrossFrameEventService,
		private inViewElementObserver: InViewElementObserver,
		private _dragAndDropScrollingService: IDragAndDropScrollingService,
		private SMARTEDIT_DRAG_AND_DROP_EVENTS: IDragAndDropEvents,
		private SMARTEDIT_ELEMENT_HOVERED: string,
		private polyfillService: PolyfillService) {}

	initialize(): void {
		this.crossFrameEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.TRACK_MOUSE_POSITION, this.onTrackMouseInner);
		this.crossFrameEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DROP_ELEMENT, this.onDropElementInner);
		this.crossFrameEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_CROSS_ORIGIN_START, this.onDnDCrossOriginStart);
	}

	private onDnDCrossOriginStart = (eventId: string) => {
		this._dragAndDropScrollingService.toggleThrottling(this.polyfillService.isEligibleForThrottledScrolling());
	}

	private onTrackMouseInner = (eventId: string, eventData: IMousePosition) => {
		if (this.isSearchingElement) {
			return;
		}
		this.isSearchingElement = true;

		/**
		 * Get the element from mouse position.
		 * In IE11, document.elementFromPoint returns null because of the #ySmartEditFrameDragArea positioned over the iframe and has pointer-events (necessary to listen on 'dragover' to track the mouse position).
		 * To polyfill document.elementFromPoint in IE11 in this scenario, we call isPointOverElement() on each elligible droppable element.
		 * 
		 * Note: in IE11, a switch of pointer-events value to none for the #ySmartEditFrameDragArea will return a value when calling $document.elementFromPoint, BUT it is causing cursor flickering and too much latency. The 'isPointOverElement' approach give better results.
		 */
		this.currentElementHovered = this.yjQuery(this.inViewElementObserver.elementFromPoint(eventData)) as JQuery<HTMLElement>;
		const mousePositionInPage: IMousePosition = this.getMousePositionInPage(eventData);

		if (this.lastElementHovered && this.lastElementHovered.length) {
			if ((this.currentElementHovered.length && this.lastElementHovered[0] !== this.currentElementHovered[0]) || !this.currentElementHovered.length) {
				this.dispatchDragEvent(this.lastElementHovered[0], IDragEventType.DRAG_LEAVE, mousePositionInPage);
				this.lastElementHovered.data(this.SMARTEDIT_ELEMENT_HOVERED, false);
			}
		}

		if (this.currentElementHovered.length) {
			if (!this.currentElementHovered.data(this.SMARTEDIT_ELEMENT_HOVERED)) {
				this.dispatchDragEvent(this.currentElementHovered[0], IDragEventType.DRAG_ENTER, mousePositionInPage);
				this.currentElementHovered.data(this.SMARTEDIT_ELEMENT_HOVERED, true);
			}

			this.dispatchDragEvent(this.currentElementHovered[0], IDragEventType.DRAG_OVER, mousePositionInPage);
		}

		this.lastElementHovered = this.currentElementHovered;
		this.isSearchingElement = false;
	}

	private onDropElementInner = (eventId: string, mousePosition: IMousePosition) => {

		if (this.currentElementHovered.length) {
			this.currentElementHovered.data(this.SMARTEDIT_ELEMENT_HOVERED, false);
			this.dispatchDragEvent(this.currentElementHovered[0], IDragEventType.DROP, mousePosition);
			this.dispatchDragEvent(this.currentElementHovered[0], IDragEventType.DRAG_LEAVE, mousePosition);
		}
	}

	private dispatchDragEvent(element: Element, type: IDragEventType, mousePosition: IMousePosition): void {
		const evt: CustomEvent = this.$document[0].createEvent('CustomEvent');
		evt.initCustomEvent(type, true, true, null);
		(evt as any).dataTransfer = {
			data: {},
			setData(_type: string, val: object) {
				this.data[_type] = val;
			},
			getData(_type: string) {
				return this.data[_type];
			}
		};
		(evt as any).pageX = mousePosition.x;
		(evt as any).pageY = mousePosition.y;
		element.dispatchEvent(evt);
	}

	private getMousePositionInPage(mousePosition: IMousePosition): IMousePosition {
		const scrollingElement: JQuery = this.yjQuery(this.$document[0].scrollingElement || this.$document[0].documentElement) as JQuery<HTMLElement>;
		return {
			x: mousePosition.x + scrollingElement.scrollLeft(),
			y: mousePosition.y + scrollingElement.scrollTop()
		};
	}

}
