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
import {CrossFrameEventService, IDragAndDropEvents, IMousePosition, SeInjectable} from "smarteditcommons";
import {IframeManagerService} from 'smarteditcontainer/services';

/**
 * Polyfill for HTML5 Drag and Drop in a cross-origin setup.
 * Most browsers (except Firefox) do not allow on-page drag-and-drop from non-same-origin frames.
 * This service is a polyfill to allow it, by listening the 'dragover' event over a sibling <div> of the iframe and sending the mouse position to the inner frame.
 * The inner frame 'DragAndDropCrossOriginInner' will use document.elementFromPoint (or isPointOverElement helper function for IE only) to determine the current hovered element and then dispatch drag events onto elligible droppable elements.
 * 
 * More information about security restrictions:
 * https://bugs.chromium.org/p/chromium/issues/detail?id=251718
 * https://bugs.chromium.org/p/chromium/issues/detail?id=59081
 * https://www.infosecurity-magazine.com/news/new-google-chrome-clickjacking-vulnerability/
 * https://bugzilla.mozilla.org/show_bug.cgi?id=605991
 */

/** @internal */
@SeInjectable()
export class DragAndDropCrossOrigin {
	private throttledSendMousePosition: (mousePosition: IMousePosition) => void;

	constructor(
		private yjQuery: JQueryStatic,
		private lodash: lo.LoDashStatic,
		private crossFrameEventService: CrossFrameEventService,
		private iframeManagerService: IframeManagerService,
		private SEND_MOUSE_POSITION_THROTTLE: number,
		private SMARTEDIT_DRAG_AND_DROP_EVENTS: IDragAndDropEvents,
		private SMARTEDIT_IFRAME_DRAG_AREA: string) {}

	initialize(): void {
		this.throttledSendMousePosition = this.lodash.throttle(this.sendMousePosition, this.SEND_MOUSE_POSITION_THROTTLE);
		this.crossFrameEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, this.onDragStart);
		this.crossFrameEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, this.onDragEnd);
	}

	private isEnabled() {
		return this.iframeManagerService.isCrossOrigin();
	}

	private onDragStart = () => {
		if (!this.isEnabled()) {
			return;
		}

		this.crossFrameEventService.publish(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_CROSS_ORIGIN_START);

		this.syncIframeDragArea()
			.show()
			.off("dragover") // `off()` is necessary since dragEnd event is not always fired.
			.on('dragover', (e: JQuery.Event) => {
				e.preventDefault(); // `preventDefault()` is necessary for the 'drop' event callback to be fired.
				const mousePosition: IMousePosition = this.getPositionRelativeToIframe(e.pageX, e.pageY);
				this.throttledSendMousePosition(mousePosition);
				return false;
			})
			.off("drop")
			.on('drop', (e: JQuery.Event) => {
				e.preventDefault();
				e.stopPropagation();
				const mousePosition: IMousePosition = this.getPositionRelativeToIframe(e.pageX, e.pageY);
				this.crossFrameEventService.publish(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DROP_ELEMENT, mousePosition);
				return false;
			});
	}

	private onDragEnd = () => {
		if (!this.isEnabled()) {
			return;
		}

		this.getIframeDragArea()
			.off("dragover")
			.off("drop")
			.hide();
	}

	private sendMousePosition = (mousePosition: IMousePosition) => {
		this.crossFrameEventService.publish(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.TRACK_MOUSE_POSITION, mousePosition);
	}

	private getIframeDragArea(): JQuery {
		return this.yjQuery("#" + this.SMARTEDIT_IFRAME_DRAG_AREA);
	}

	private getPositionRelativeToIframe(posX: number, posY: number): IMousePosition {
		const iframeOffset: JQuery.Coordinates = this.getIframeDragArea().offset();
		return {
			x: posX - iframeOffset.left,
			y: posY - iframeOffset.top
		};
	}

	private syncIframeDragArea(): JQuery {
		this.getIframeDragArea().width(this.iframeManagerService.getIframe().width());
		this.getIframeDragArea().height(this.iframeManagerService.getIframe().height());

		const iframeOffset: JQuery.Coordinates = this.iframeManagerService.getIframe().offset();
		this.getIframeDragArea().css({
			top: iframeOffset.top,
			left: iframeOffset.left
		});

		return this.getIframeDragArea();
	}
}
