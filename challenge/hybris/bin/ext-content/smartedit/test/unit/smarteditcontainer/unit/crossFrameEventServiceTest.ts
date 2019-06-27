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
import {promiseHelper} from 'testhelpers';
import {
	CrossFrameEventService,
	GatewayFactory,
	MessageGateway,
	SystemEventService,
	WindowUtils
} from 'smarteditcommons';

describe('crossFrameEventService', function() {
	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let crossFrameEventService: CrossFrameEventService;
	let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
	let gateway: jasmine.SpyObj<MessageGateway>;
	const $q = promiseHelper.$q();
	const eventId = 'eventId';
	const data = 'some data';
	const handler = angular.noop;
	let windowUtils: jasmine.SpyObj<WindowUtils>;
	let targetIFrameValue = true;

	beforeEach(() => {
		systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync', 'subscribe']);
		gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
		gateway = jasmine.createSpyObj('gateway', ['publish', 'subscribe']);
		gatewayFactory.createGateway.and.returnValue(gateway);

		windowUtils = jasmine.createSpyObj<WindowUtils>('windowUtils', ['getTargetIFrame']);
		windowUtils.getTargetIFrame.and.callFake(() => {
			return targetIFrameValue;
		});

		crossFrameEventService = new CrossFrameEventService($q, systemEventService, gateway, windowUtils);
	});

	it('publish will publish to the gatewayFactory and then send an event for a given event id and data', function() {
		targetIFrameValue = true;

		// GIVEN
		systemEventService.publishAsync.and.returnValue($q.when("systemEventService"));
		gateway.publish.and.returnValue($q.when("gateway"));

		// WHEN
		const promise = crossFrameEventService.publish(eventId, data);

		// THEN
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(eventId, data);
		expect(gateway.publish).toHaveBeenCalledWith(eventId, data);
		(expect(promise) as any).toBeResolvedWithData(['systemEventService', 'gateway']);
	});

	it('publish with no targetIframe will send only resolve systemEventService', () => {
		targetIFrameValue = false;

		// GIVEN
		systemEventService.publishAsync.and.returnValue($q.when("systemEventService"));
		gateway.publish.and.returnValue($q.when("gateway"));

		// WHEN
		const promise = crossFrameEventService.publish(eventId, data);

		// THEN
		expect(systemEventService.publishAsync).toHaveBeenCalledWith(eventId, data);
		expect(gateway.publish).not.toHaveBeenCalledWith(eventId, data);
		(expect(promise) as any).toBeResolvedWithData(['systemEventService']);
	});

	it('subscribe will subscribe to the gatewayFactory and then register an event for the given event id and the provided handler', function() {
		targetIFrameValue = true;

		const unRegisterFnSpy = jasmine.createSpy('unRegisterFn');
		const gatewayUnsubscribeFn = jasmine.createSpy('gatewayUnsubscribeFn');

		// GIVEN
		systemEventService.subscribe.and.returnValue(unRegisterFnSpy);
		gateway.subscribe.and.returnValue(gatewayUnsubscribeFn);

		// WHEN
		const unSubscribeFn = crossFrameEventService.subscribe(eventId, handler);

		unSubscribeFn();

		// THEN
		expect(systemEventService.subscribe).toHaveBeenCalledWith(eventId, handler);
		expect(gateway.subscribe).toHaveBeenCalledWith(eventId, handler);
		expect(unRegisterFnSpy).toHaveBeenCalled();
		expect(gatewayUnsubscribeFn).toHaveBeenCalled();
	});
});
