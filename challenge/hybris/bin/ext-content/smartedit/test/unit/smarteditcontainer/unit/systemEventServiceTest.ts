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
import {promiseHelper, IExtensiblePromise} from 'testhelpers';
import {SystemEventService} from 'smarteditcommons';

describe('systemEventService', function() {

	let systemEventService: SystemEventService;
	let handlerHolderMock: any;
	const $q = promiseHelper.$q();
	const $timeout = jasmine.createSpy('$timeout');
	const $log = jasmine.createSpyObj('$log', ['error', 'warn']);
	const toPromise = jasmine.createSpy('toPromise');

	const id: string = 'myId';
	const data: string = 'myData';
	let deferred: any;
	let deferred2: any;
	let deferred3: any;

	beforeEach(() => {

		deferred = $q.defer();
		deferred2 = $q.defer();
		deferred3 = $q.defer();

		handlerHolderMock = {
			handler: angular.noop,
			handler2: angular.noop,
			handler3: angular.noop
		};
		spyOn(handlerHolderMock, 'handler').and.returnValue(deferred.promise);
		spyOn(handlerHolderMock, 'handler2').and.returnValue(deferred2.promise);
		spyOn(handlerHolderMock, 'handler3').and.returnValue(deferred3.promise);
	});

	beforeEach(() => {

		toPromise.and.callFake(function(method: ((...args: any[]) => any), context: any) {
			return function() {
				return $q.when(method.apply(context, arguments));
			};
		});

		systemEventService = new SystemEventService($timeout as any, $q, $log, toPromise);
	});

	describe('subscribe', () => {
		it('subscribe event handler with incorrect eventId', () => {
			systemEventService.subscribe(null, handlerHolderMock.handler);
			expect($log.error).toHaveBeenCalledWith('Failed to subscribe event handler for event: null');
			expect(handlerHolderMock.handler).not.toHaveBeenCalledWith(id, data);
		});
		it('subscribe event handler with incorrect eventId', () => {
			systemEventService.subscribe(id, null);
			expect($log.error).toHaveBeenCalledWith('Failed to subscribe event handler for event: ' + id);
			expect(handlerHolderMock.handler).not.toHaveBeenCalledWith(id, data);
		});
		it('unsubscribe function', () => {
			const unsubscribeSpy = spyOn(systemEventService as any, '_unsubscribe').and.callThrough();
			const unsubscribeFn = systemEventService.subscribe(id, handlerHolderMock.handler);
			unsubscribeFn();
			expect(unsubscribeSpy).toHaveBeenCalledWith(id, handlerHolderMock.handler);
		});
	});

	describe('unsubscribe', () => {
		it('unsubscribe event handler with incorrect eventId', () => {
			(systemEventService as any)._unsubscribe(null, handlerHolderMock.handler);
			expect($log.warn).toHaveBeenCalledWith('Attempting to remove event handler for null but handler not found.');
		});
	});

	describe('publishAsync', () => {
		beforeEach(() => {
			jasmine.clock().uninstall();
			jasmine.clock().install();
			$timeout.and.callFake(function(fn: () => void, delay: number) {
				setTimeout(fn, delay);
			});
		});
		afterEach(() => {
			jasmine.clock().uninstall();
		});
		it('When first handler out of 2 is successful Will call both my event handlers subscribed to the same eventId - with correct id and data - and resolves the promise chain to the resolved data of the last subscriber', (done) => {
			const publishSpy = spyOn(systemEventService, 'publish').and.callThrough();
			deferred.resolve('firstValue');
			deferred2.resolve('secondValue');

			systemEventService.subscribe(id, handlerHolderMock.handler);
			systemEventService.subscribe(id, handlerHolderMock.handler2);

			const promise = systemEventService.publishAsync(id, data) as IExtensiblePromise<any>;

			jasmine.clock().tick(0);

			promise.then(function(resolvedData: string) {
				expect(resolvedData).toBe('secondValue');
				done();
			}, function(rejectedData) {
				fail();
			});

			expect(publishSpy).toHaveBeenCalledWith(id, data);
			expect(handlerHolderMock.handler).toHaveBeenCalledWith(id, data);
		});
	});

	// ----------------------------------------------------------
	// All the rest of the tests are using the Synchronized mode
	// ----------------------------------------------------------

	it('Will call my event handler with correct id and data', function() {

		systemEventService.subscribe(id, handlerHolderMock.handler);
		systemEventService.publish(id, data);
		expect(handlerHolderMock.handler).toHaveBeenCalledWith(id, data);
	});

	it('When first handler out of 2 is successful Will call both my event handlers subscribed to the same eventId - with correct id and data - and resolves the promise chain to the resolved data of the last subscriber', function(done) {

		deferred.resolve('firstValue');
		deferred2.resolve('secondValue');

		systemEventService.subscribe(id, handlerHolderMock.handler);
		systemEventService.subscribe(id, handlerHolderMock.handler2);
		const promise = systemEventService.publish(id, data) as IExtensiblePromise<any>;

		promise.then(function(resolvedData: string) {
			expect(resolvedData).toBe('secondValue');
			done();
		}, function(rejectedData) {
			fail();
		});

		expect(handlerHolderMock.handler).toHaveBeenCalledWith(id, data);
		expect(handlerHolderMock.handler2).toHaveBeenCalledWith(id, data);

	});

	it('When more than 2 handlers and all are successful , resolves the promise chain to the resolved data of the last subscriber', function(done) {

		deferred.resolve('firstValue');
		deferred2.resolve('secondValue');
		deferred3.resolve('thirdValue');

		systemEventService.subscribe(id, handlerHolderMock.handler);
		systemEventService.subscribe(id, handlerHolderMock.handler2);
		systemEventService.subscribe(id, handlerHolderMock.handler3);

		systemEventService.publish(id, data).then(function(resolvedData: string) {
			expect(resolvedData).toBe('thirdValue');
			done();
		}, function() {
			fail();
		});

		expect(handlerHolderMock.handler).toHaveBeenCalledWith(id, data);
		expect(handlerHolderMock.handler2).toHaveBeenCalledWith(id, data);
		expect(handlerHolderMock.handler3).toHaveBeenCalledWith(id, data);

	});

	it('When either of first or second handler is not successful Will send method promise rejects', function(done) {

		deferred.resolve('firstValue');
		deferred2.reject('error reason');

		systemEventService.subscribe(id, handlerHolderMock.handler);
		systemEventService.subscribe(id, handlerHolderMock.handler2);
		systemEventService.publish(id, data).then(function(resolvedData: string) {
			fail();
		}, function(reason: string) {
			expect(reason).toBe('error reason');
			done();
		});

		expect(handlerHolderMock.handler).toHaveBeenCalledWith(id, data);
		expect(handlerHolderMock.handler2).toHaveBeenCalledWith(id, data);

	});

	it('Will NOT call my handler after it has been unsubscribed for the specific eventId and send method promise resolves', function() {

		const unsubscribeFn = systemEventService.subscribe(id, handlerHolderMock.handler);
		unsubscribeFn();
		systemEventService.publish(id, data).then(angular.noop, function() {
			fail();
		});
		expect(handlerHolderMock.handler).not.toHaveBeenCalled();
	});


});
