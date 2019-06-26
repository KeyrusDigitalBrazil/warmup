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
import {Cloneable, GatewayFactory, GatewayProxy, MessageGateway} from 'smarteditcommons';
import {promiseHelper, IExtensiblePromise} from 'testhelpers';
import {FunctionsUtils} from 'smarteditcommons/utils/FunctionsUtils';

describe('test gatewayProxy', function() {

	const gatewayId = 'toolbar';
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj('$log', ['error']);
	const $q = promiseHelper.$q();
	const toPromise: jasmine.Spy = jasmine.createSpy('toPromise');
	const isBlank: jasmine.Spy = jasmine.createSpy('isBlank');
	const functionsUtils: FunctionsUtils = new FunctionsUtils();
	let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
	let gateway: jasmine.SpyObj<MessageGateway>;
	let gatewayProxy: GatewayProxy;

	beforeEach(() => {
		gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway', 'initListener']);
		gateway = jasmine.createSpyObj('gateway', ['publish', 'subscribe']);
		gatewayFactory.createGateway.and.returnValue(gateway);

		toPromise.and.callFake(function(method: ((...args: Cloneable[]) => Cloneable), context: any) {
			return function() {
				return $q.when(method.apply(context, arguments));
			};
		});

		isBlank.and.callFake(function(value: string) {
			return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
		});
		gatewayProxy = new GatewayProxy($log, $q, toPromise, isBlank, functionsUtils, gatewayFactory);
	});

	it('gatewayProxy will proxy empty functions and subscribe listeners for non empty functions now returning promises resolving to the return value of the method', function(done) {

		const service = {
			gatewayId,
			methodToBeProxied: angular.noop,
			method2ToBeProxied: angular.noop,
			methodToBeRemotelyInvokable(arg: string) {
				return arg + 'Suffix';
			},
			method2ToBeRemotelyInvokable(arg: string) {
				return arg + 'Suffix2';
			}
		};
		toPromise.calls.reset();

		gateway.publish.and.returnValue($q.defer().promise);

		gatewayProxy.initForService(service);

		expect(gatewayFactory.createGateway).toHaveBeenCalledWith(gatewayId);

		expect(toPromise.calls.count()).toBe(2);

		expect(gateway.subscribe).toHaveBeenCalledWith('methodToBeRemotelyInvokable', jasmine.any(Function));
		expect(gateway.subscribe).toHaveBeenCalledWith('method2ToBeRemotelyInvokable', jasmine.any(Function));

		(service.methodToBeRemotelyInvokable('anything') as any).then(function(data: string) {
			expect(data).toBe('anythingSuffix');
			done();
		}, function() {
			fail();
		});

		expect(gateway.publish).not.toHaveBeenCalled();

		service.methodToBeProxied('arg1', 'arg2');

		expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
			arguments: ['arg1', 'arg2']
		});

		service.method2ToBeProxied('arg1', 'arg2');

		expect(gateway.publish).toHaveBeenCalledWith('method2ToBeProxied', {
			arguments: ['arg1', 'arg2']
		});
	});

	it('gatewayProxy will proxy empty functions and subscribe listeners for a subset of methods', function(done) {

		const service = {
			gatewayId,
			methodToBeProxied: angular.noop,
			method2ToBeProxied: angular.noop,
			methodToBeRemotelyInvokable(arg: string) {
				return arg + 'Suffix';
			},
			method2ToBeRemotelyInvokable(arg: string) {
				return arg + 'Suffix2';
			}
		};

		gateway.publish.and.returnValue($q.defer().promise);
		gatewayProxy.initForService(service, ['methodToBeProxied', 'methodToBeRemotelyInvokable']);

		expect(gatewayFactory.createGateway).toHaveBeenCalledWith(gatewayId);
		expect(gateway.subscribe).toHaveBeenCalledWith('methodToBeRemotelyInvokable', jasmine.any(Function));
		expect(gateway.subscribe).not.toHaveBeenCalledWith('method2ToBeRemotelyInvokable', jasmine.any(Function));

		(service.methodToBeRemotelyInvokable('anything') as any).then(function(data: string) {
			expect(data).toBe('anythingSuffix');
			done();
		}, function() {
			fail();
		});

		expect(gateway.publish).not.toHaveBeenCalled();

		service.methodToBeProxied('arg1', 'arg2');

		expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
			arguments: ['arg1', 'arg2']
		});

		service.method2ToBeProxied('arg1', 'arg2');

		expect(gateway.publish).not.toHaveBeenCalledWith('method2ToBeProxied', {
			arguments: ['arg1', 'arg2']
		});

	});

	it('gatewayProxy will proxy empty functions and will wrap the value returned by the proxy in a promise', function() {

		const expectedReturnValue = 'This is a return value';
		const service = {
			gatewayId,
			methodToBeProxied(str1: string, str2: string): any {
				'proxyFunction';
				return null;
			}
		};

		gateway.publish.and.returnValue($q.when(expectedReturnValue));
		gatewayProxy.initForService(service);

		// Act
		const promise = service.methodToBeProxied('arg1', 'arg2') as IExtensiblePromise<string>;

		expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
			arguments: ['arg1', 'arg2']
		});

		expect(promise.value).toEqual(expectedReturnValue);
	});

	it('gatewayProxy will proxy empty functions and will wrap an undefined value in a promise if the remote method returns void', function() {
		// Arrange
		const service = {
			gatewayId,
			methodToBeProxied(str1: string, str2: string): any {
				'proxyFunction';
				return null;
			}
		};

		gateway.publish.and.returnValue($q.when());
		gatewayProxy.initForService(service);

		// Act
		const promise = service.methodToBeProxied('arg1', 'arg2') as IExtensiblePromise<string>;

		expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
			arguments: ['arg1', 'arg2']
		});
		expect(promise.value).toEqual(undefined);
	});

	it('gatewayProxy will wrap the result of non-empty functions in a promise', function() {
		// Arrange
		const providedArg = 'some argument';
		const expectedResult = 'some result';
		const service = {
			gatewayId,
			methodToBeRemotelyInvokable(arg: string) {
				return expectedResult + arg;
			}
		};

		gatewayProxy.initForService(service);
		const eventID = gateway.subscribe.calls.argsFor(0)[0];
		const onGatewayEvent = gateway.subscribe.calls.argsFor(0)[1];

		// Act
		const promise = onGatewayEvent(eventID, {
			arguments: [providedArg]
		}) as IExtensiblePromise<string>;

		expect(promise.value).toEqual(expectedResult + providedArg);
	});
});
