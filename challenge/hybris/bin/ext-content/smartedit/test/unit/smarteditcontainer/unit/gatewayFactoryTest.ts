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
	CloneableUtils,
	GatewayFactory,
	IGatewayPostMessageData,
	MessageGateway,
	SystemEventService,
	WindowUtils
} from 'smarteditcommons';

describe('test GatewayFactory and MessageGateway', function() {

	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let $log: jasmine.SpyObj<angular.ILogService>;
	let getOrigin: jasmine.Spy;
	let regExpFactory: jasmine.Spy;
	let isIframe: jasmine.Spy;
	let $injector: jasmine.SpyObj<angular.auto.IInjectorService>;
	const WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY: string = 'whiteListedStorefronts';
	let $q: jasmine.SpyObj<angular.IQService>;
	let cloneableUtils: jasmine.SpyObj<CloneableUtils>;
	let $timeout: jasmine.Spy;
	const TIMEOUT_TO_RETRY_PUBLISHING: number = 500;
	let $window: any;
	let gatewayFactory: GatewayFactory;
	let windowUtils: jasmine.SpyObj<WindowUtils>;
	let targetFrame: any;

	beforeEach(() => {
		systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync', 'subscribe']);
		$log = jasmine.createSpyObj('$log', ['warn', 'error', 'debug']);
		getOrigin = jasmine.createSpy('getOrigin');
		regExpFactory = jasmine.createSpy('regExpFactory');
		isIframe = jasmine.createSpy('isIframe');
		$injector = jasmine.createSpyObj('$injector', ['has', 'get', 'instantiate']);
		$q = promiseHelper.$q();
		cloneableUtils = jasmine.createSpyObj('cloneableUtils', ['makeCloneable']);
		$timeout = jasmine.createSpy('$timeout');
		$window = {
			addEventListener: jasmine.createSpy('addEventListener'),
			document: jasmine.createSpyObj('document', ['getElementById']),
			top: null
		};
		windowUtils = jasmine.createSpyObj<WindowUtils>('windowUtils', ['getTargetIFrame']);

		gatewayFactory = new GatewayFactory($q, $log, $window, $injector, $timeout as any, systemEventService, cloneableUtils,
			getOrigin, regExpFactory, isIframe, windowUtils, WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY, TIMEOUT_TO_RETRY_PUBLISHING);
	});

	beforeEach(() => {
		isIframe.and.callFake(() => {
			return $window.top !== $window;
		});
		windowUtils.getTargetIFrame.and.callFake(() => {
			targetFrame = jasmine.createSpyObj('targetFrame', ['postMessage']);
			return targetFrame;
		});
		regExpFactory.and.callFake((pattern: string) => {
			return new RegExp(pattern, 'g');
		});
		getOrigin.and.callFake(() => {
			return 'trusted';
		});
		cloneableUtils.makeCloneable.and.callFake((arg: any) => arg);
		setIframe(false);
	});
    /**
     * Set iframe mode on/off
     * @param {boolean} isIframeVal
     * if isIframeVal is false, isIframe() will return false
     * if isIframeVal is true, isIframe() will return true
     */
	const setIframe = function(isIframeVal: boolean) {
		$window.top = isIframeVal ? null : $window;
	};

	const instantiateMessageGateway = function(gatewayId: string) {
		const messageGateway = new MessageGateway($q, $log, $timeout as any, systemEventService, cloneableUtils, windowUtils, TIMEOUT_TO_RETRY_PUBLISHING, gatewayId);
		$injector.instantiate.and.returnValue(messageGateway);
	};

	it('should attach a W3C postMessage event when addEventListener exists on window', function() {

		gatewayFactory.initListener();

		expect($window.addEventListener).toHaveBeenCalledWith('message', jasmine.any(Function), false);
	});

	describe('GIVEN that the parent frame receives message', function() {

		let gateway: MessageGateway;
		let listener: (e: MessageEvent) => void;
		let processEventSpy: jasmine.Spy;
		const gatewayId = 'test';
		beforeEach(() => {

			$window.addEventListener.and.returnValue();

			instantiateMessageGateway(gatewayId);
			gateway = gatewayFactory.createGateway(gatewayId);

			$injector.has.and.returnValue(true);
			$injector.get.and.returnValue(['sometrusteddomain', 'someothertrusteddomain']);
			systemEventService.publishAsync.and.returnValue($q.when('systemEventService'));

			gatewayFactory.initListener();

			processEventSpy = spyOn<MessageGateway>(gateway, 'processEvent').and.returnValue('');

			listener = $window.addEventListener.calls.argsFor(0)[1];

			$log.error.calls.reset();
		});

		it('SHOULD have the listener\'s callback log error and not process event, GIVEN the domain is not white listed and the url is not same origin', function() {
			const e = {
				origin: 'untrusted'
			} as MessageEvent;
			listener(e);
			expect(gateway.processEvent).not.toHaveBeenCalled();
			expect($log.error).toHaveBeenCalledWith('disallowed storefront is trying to communicate with smarteditcontainer');
		});

		it('SHOULD have the listener\'s callback process event of gateway only once, GIVEN url is same origin and gatewayId is test', function() {
			const e = {
				data: {
					pk: 'somepk',
					gatewayId: 'test'
				},
				origin: getOrigin()
			} as MessageEvent;

			listener(e);

			expect(isIframe()).toBe(false);
			expect(getOrigin()).toBe(e.origin);

			expect(gateway.processEvent).toHaveBeenCalledWith(e.data);
			expect($log.error).not.toHaveBeenCalled();

			listener(e);
			expect(processEventSpy.calls.count()).toBe(1);
		});

		it('SHOULD have the listener\'s callback process the event of gateway only once, GIVEN url is not same origin but is white listed and gatewayId is test', function() {
			const e = {
				data: {
					pk: 'sometrusteddomain',
					gatewayId: 'test'
				},
				origin: getOrigin()
			} as MessageEvent;
			listener(e);
			expect(gateway.processEvent).toHaveBeenCalledWith(e.data);
			expect($log.error).not.toHaveBeenCalled();
			listener(e);
			expect(processEventSpy.calls.count()).toBe(1);
		});

		it('SHOULD have the listener callback\'s not process the event of the gateway, GIVEN url same origin and gatewayId is not test', function() {
			const e = {
				data: {
					pk: 'sometrusteddomain',
					gatewayId: 'nottest'
				},
				origin: getOrigin()
			} as MessageEvent;
			listener(e);
			expect(gateway.processEvent).not.toHaveBeenCalled();
			expect($log.error).not.toHaveBeenCalled();
		});
	});

	it('SHOULD return no gateway on subsequent calls to createGateway with the same gateway id', function() {
		instantiateMessageGateway('TestChannel1');
		const gateway = gatewayFactory.createGateway('TestChannel1');

		instantiateMessageGateway('TestChannel1');
		const duplicateGateway = gatewayFactory.createGateway('TestChannel1');

		expect(gateway).toBeDefined();
		expect(duplicateGateway).toBeNull();
	});

	it('SHOULD subscribe to the system event service with the event id <gateway_id>:<event_id>', function() {
		const CHANNEL_ID = 'TestChannel';
		const EVENT_ID = 'someEvent';
		const SYSTEM_EVENT_ID = CHANNEL_ID + ':' + EVENT_ID;

		const handler = angular.noop;
		instantiateMessageGateway(CHANNEL_ID);
		const gateway = gatewayFactory.createGateway(CHANNEL_ID);

		gateway.subscribe(EVENT_ID, handler);

		expect(systemEventService.subscribe).toHaveBeenCalledWith(SYSTEM_EVENT_ID, handler);
	});

	describe('publish', function() {

		let gatewayId: string;
		let eventId: string;
		let data: any;
		let gateway: MessageGateway;
		let pk: string;
		let successEvent: any;

		beforeEach(() => {
			jasmine.clock().uninstall();
			jasmine.clock().install();
			$timeout.and.callFake(function(fn: () => void, delay: number) {
				setTimeout(fn, delay);
			});
		});

		beforeEach(function() {
			$window.frameElement = {};
			$window.parent = jasmine.createSpyObj('parent', ['postMessage']);
			gatewayId = "TestChannel";
			eventId = "_testEvent";
			data = {
				arguments: [{
					key: "testKey"
				}]
			};

			instantiateMessageGateway(gatewayId);
			gateway = gatewayFactory.createGateway(gatewayId);

			pk = '1234567890';
			spyOn(gateway as any, '_generateIdentifier').and.returnValue(pk);
			successEvent = {
				eventId: 'promiseReturn',
				data: {
					pk,
					type: 'success',
					resolvedDataOfLastSubscriber: 'someData'
				}
			};
		});

		afterEach(() => {
			jasmine.clock().uninstall();
		});

		it('SHOULD post a W3C message to the target frame and return a hanging promise', function() {
			const promise = gateway.publish(eventId, data);

			expect(promise).not.toBeResolved();
			expect(targetFrame.postMessage).toHaveBeenCalledWith({
				pk,
				eventId,
				gatewayId,
				data
			}, '*');
		});

		it('SHOULD return a rejected promise even when there is no target frame', function() {
			windowUtils.getTargetIFrame.and.throwError('It is standalone. There is no iframe');
			const promise = gateway.publish(eventId, data);

			gateway.processEvent(successEvent);

			expect(promise).toBeRejected();
		});

		it('SHOULD return a promise from publish that is resolved to event.data.resolvedDataOfLastSubscriber when incoming success promiseReturn with same pk', function() {
			const promise = gateway.publish(eventId, data);

			gateway.processEvent(successEvent);

			expect(promise).toBeResolved();

		});

		it('SHOULD return a promise from publish that is rejected WHEN incoming failure promiseReturn with same pk', function() {
			const promise = gateway.publish(eventId, data);

			const failureEvent = {
				pk,
				gatewayId,
				eventId: 'promiseReturn',
				data: {
					pk,
					type: 'failure'
				}
			} as IGatewayPostMessageData;

			gateway.processEvent(failureEvent);

			expect(promise).toBeRejected();

		});

		it('SHOULD return a promise from publish that is still hanging WHEN incoming promiseReturn with different pk', function() {
			const promise = gateway.publish(eventId, data);
			const randomPk = 'fgsdfgssf';

			const differentEvent = {
				pk: randomPk,
				gatewayId,
				eventId: 'promiseReturn',
				data: {
					pk: randomPk,
					type: 'success',
					resolvedDataOfLastSubscriber: 'someData'
				}
			};
			gateway.processEvent(differentEvent);

			expect(promise).not.toBeResolved();
		});

		it('SHOULD reject a promise after retrying publish for 5 times', function() {
			const publishSpy = spyOn<MessageGateway>(gateway, 'publish').and.callThrough();

			const promise = gateway.publish(eventId, data);
			expect(publishSpy.calls.count()).toBe(1);

			jasmine.clock().tick(TIMEOUT_TO_RETRY_PUBLISHING);
			expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 1, pk);
			expect(publishSpy.calls.count()).toBe(2);
			expect(promise).not.toBeResolved();

			jasmine.clock().tick(TIMEOUT_TO_RETRY_PUBLISHING);
			expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 2, pk);
			expect(publishSpy.calls.count()).toBe(3);
			expect(promise).not.toBeResolved();

			jasmine.clock().tick(TIMEOUT_TO_RETRY_PUBLISHING);
			expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 3, pk);
			expect(publishSpy.calls.count()).toBe(4);
			expect(promise).not.toBeResolved();

			jasmine.clock().tick(TIMEOUT_TO_RETRY_PUBLISHING);
			expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 4, pk);
			expect(publishSpy.calls.count()).toBe(5);
			expect(promise).not.toBeResolved();

			jasmine.clock().tick(TIMEOUT_TO_RETRY_PUBLISHING);
			expect(promise).toBeRejected();
		});

	});

	describe('processEvent', function() {
		let gateway: MessageGateway;
		let event: any;

		beforeEach(function() {
			instantiateMessageGateway('TestChannel');
			gateway = gatewayFactory.createGateway('TestChannel');
			event = {
				pk: 'rlktqnvghsliutergwe',
				eventId: 'someEvent',
				data: {
					key1: 'abc'
				}
			};
		});

		it("SHOULD be different from 'promiseReturn' and 'promiseAcknowledgement' will call systemEventService.publishAsync and publish a success promiseReturn event with the last resolved data from subscribers", function() {
			const sendDeferred = $q.defer();
			sendDeferred.resolve('someResolvedData');
			systemEventService.publishAsync.and.returnValue(sendDeferred.promise);

			spyOn(gateway, 'publish').and.returnValue($q.defer().promise);

			gateway.processEvent(event);

			expect(systemEventService.publishAsync).toHaveBeenCalledWith('TestChannel:someEvent', {
				key1: 'abc'
			});
			expect(gateway.publish).toHaveBeenCalledWith("promiseReturn", {
				pk: 'rlktqnvghsliutergwe',
				type: 'success',
				resolvedDataOfLastSubscriber: 'someResolvedData'
			});
		});

		it("SHOULD be different from 'promiseReturn' and 'promiseAcknowldgement' will call systemEventService.publishAsync and publish a failure promiseReturn event", function() {

			const sendDeferred = $q.defer();
			sendDeferred.reject();
			systemEventService.publishAsync.and.returnValue(sendDeferred.promise);

			spyOn(gateway, 'publish').and.returnValue($q.defer().promise);

			gateway.processEvent(event);

			expect(systemEventService.publishAsync).toHaveBeenCalledWith('TestChannel:someEvent', {
				key1: 'abc'
			});
			expect(gateway.publish).toHaveBeenCalledWith("promiseReturn", {
				pk: 'rlktqnvghsliutergwe',
				type: 'failure'
			});
		});

	});

});
