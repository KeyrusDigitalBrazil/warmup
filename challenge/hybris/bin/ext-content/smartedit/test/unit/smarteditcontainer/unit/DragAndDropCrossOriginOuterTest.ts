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
import 'jasmine';
import {CrossFrameEventService, IDragAndDropEvents, IMousePosition} from 'smarteditcommons';
import {DragAndDropCrossOrigin, IframeManagerService} from 'smarteditcontainer/services';
import {domHelper, jQueryHelper, ElementForJQuery} from 'testhelpers';

describe('DragAndDropCrossOriginOuter', () => {

	let iframeManagerService: jasmine.SpyObj<IframeManagerService>;
	let yjQuery: JQueryStatic;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

	const SMARTEDIT_DRAG_AND_DROP_EVENTS = {
		TRACK_MOUSE_POSITION: 'tmp',
		DROP_ELEMENT: 'de',
		DRAG_DROP_START: 'dds',
		DRAG_DROP_END: 'dde',
		DRAG_DROP_CROSS_ORIGIN_START: 'ddcos'
	} as IDragAndDropEvents;

	const SEND_MOUSE_POSITION_THROTTLE = 5;
	const SMARTEDIT_IFRAME_DRAG_AREA = 'sida';

	let service: DragAndDropCrossOrigin;

	let onDragStart: (eventId: string) => angular.IPromise<any>;
	let onDragEnd: (eventId: string) => angular.IPromise<any>;
	let dragoverCallback: (event: JQuery.Event) => void;
	let dropCallback: (event: JQuery.Event) => void;
	let throttledSendMousePosition: (mousePosition: IMousePosition) => void;

	let event: jasmine.SpyObj<JQuery.Event>;

	const lodash = (window as any).smarteditLodash;

	let dragArea: jasmine.SpyObj<JQuery<ElementForJQuery>>;

	let iframe: jasmine.SpyObj<ElementForJQuery>;
	let iframeWrapper: jasmine.SpyObj<JQuery<Element>>;

	const width = 1800;
	const height = 1000;
	const iframeOffset = {top: 5, left: 3};
	const dragAreaOffset = {top: 50, left: 30} as JQuery.Coordinates;
	const pageX = 300;
	const pageY = 500;

	beforeEach(() => {

		event = domHelper.event();
		(event as any).pageX = pageX;
		(event as any).pageY = pageY;

		throttledSendMousePosition = jasmine.createSpy('throttledSendMousePosition');

		spyOn(lodash, 'throttle').and.returnValue(throttledSendMousePosition);

		iframe = domHelper.element('iframe', {width, height, offset: iframeOffset});
		iframeWrapper = jQueryHelper.wrap('iframeWrapper', iframe);

		dragArea = jQueryHelper.wrap('dragArea');
		dragArea.offset.and.returnValue(dragAreaOffset);

		yjQuery = jQueryHelper.jQuery((selector) => {
			if (selector === '#' + SMARTEDIT_IFRAME_DRAG_AREA) {
				return dragArea;
			}
			throw new Error(`unexpected string selector: ${selector}`);
		});

		iframeManagerService = jasmine.createSpyObj('iframeManagerService', ['getIframe', 'isCrossOrigin']);
		iframeManagerService.getIframe.and.returnValue(iframeWrapper);

		crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish', 'subscribe']);

		service = new DragAndDropCrossOrigin(
			yjQuery,
			lodash,
			crossFrameEventService,
			iframeManagerService,
			SEND_MOUSE_POSITION_THROTTLE,
			SMARTEDIT_DRAG_AND_DROP_EVENTS,
			SMARTEDIT_IFRAME_DRAG_AREA
		);

		service.initialize();

	});

	it('initialize will subscribe to 2 events', () => {

		expect(crossFrameEventService.subscribe).toHaveBeenCalledTimes(2);
		expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, jasmine.any(Function));
		expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, jasmine.any(Function));
	});

	describe('callbacks', () => {

		beforeEach(() => {
			onDragStart = crossFrameEventService.subscribe.calls.argsFor(0)[1];
			onDragEnd = crossFrameEventService.subscribe.calls.argsFor(1)[1];

			crossFrameEventService.subscribe.calls.reset();

			expect(onDragStart).toBeDefined();
			expect(onDragEnd).toBeDefined();
		});

		it('onDragEnd will stop if not cross origin', () => {

			onDragEnd('eventId');

			expect(dragArea.off).not.toHaveBeenCalled();

		});

		it('onDragEnd in cross origin, dragover and drop listeners are removed from the drag area', () => {

			iframeManagerService.isCrossOrigin.and.returnValue(true);

			onDragEnd('eventId');

			expect(dragArea.off).toHaveBeenCalledWith('dragover');
			expect(dragArea.off).toHaveBeenCalledWith('drop');
			expect(dragArea.hide).toHaveBeenCalled();
		});


		it('onDragStart will stop if not cross origin', () => {

			iframeManagerService.isCrossOrigin.and.returnValue(false);

			onDragStart('eventId');

			expect(crossFrameEventService.publish).not.toHaveBeenCalled();

		});

		describe('onDragStart in cross origin', () => {


			beforeEach(() => {
				iframeManagerService.isCrossOrigin.and.returnValue(true);

				onDragStart('eventId');

			});

			it('dragover and drop listeners are removed from the drag area', () => {

				expect(dragArea.off).toHaveBeenCalledWith('dragover');
				expect(dragArea.off).toHaveBeenCalledWith('drop');

			});

			it('dragover and drop listeners are added to the drag area', () => {

				expect(dragArea.on).toHaveBeenCalledWith('dragover', jasmine.any(Function));
				expect(dragArea.on).toHaveBeenCalledWith('drop', jasmine.any(Function));

			});

			it('the drag area will acquire same dimensions and position as the storefront iframe', () => {

				expect(dragArea.width).toHaveBeenCalledWith(width);
				expect(dragArea.height).toHaveBeenCalledWith(height);
				expect(dragArea.css).toHaveBeenCalledWith(iframeOffset);

			});

			describe('dragoverCallback and dropCallback', () => {

				beforeEach(() => {

					dragoverCallback = dragArea.on.calls.argsFor(0)[1];
					dropCallback = dragArea.on.calls.argsFor(1)[1];

					expect(dragoverCallback).toBeDefined();
					expect(dropCallback).toBeDefined();
				});

				it('dragoverCallback will sendMousePosition', () => {
					dragoverCallback(event);

					expect(throttledSendMousePosition).toHaveBeenCalledWith({x: 270, y: 450});
				});

				it('dropCallback will send event with mouse position', () => {
					dropCallback(event);

					expect(crossFrameEventService.publish).toHaveBeenCalledWith(SMARTEDIT_DRAG_AND_DROP_EVENTS.DROP_ELEMENT, {x: 270, y: 450});
				});
			});

		});


	});
});