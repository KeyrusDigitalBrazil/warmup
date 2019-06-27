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
import {DiscardablePromiseUtils} from 'smarteditcommons/utils/DiscardablePromiseUtils';

describe("DiscardablePromiseUtilsTest", () => {

	const identifier1 = "identifier1";
	const identifier2 = "identifier2";
	class DTO {
		someKey: string;
	}

	let $log: jasmine.SpyObj<angular.ILogService>;
	let discardablePromiseUtils: DiscardablePromiseUtils;

	let values: string[];
	let errors: string[];

	let successcallback: (response: DTO) => void;
	let failurecallback: (response: Error) => void;

	let firstPromiseResolve: () => void;
	let firstPromiseReject: () => void;
	let secondPromiseResolve: () => void;
	let secondPromiseReject: () => void;

	beforeEach(() => {

		values = [];
		errors = [];

		successcallback = (response: DTO) => {
			values.push(response.someKey);
		};
		failurecallback = (response: Error) => {
			errors.push(response.message);
		};

		$log = jasmine.createSpyObj<angular.ILogService>("$log", ["debug"]);

		discardablePromiseUtils = new DiscardablePromiseUtils($log);
	});

	describe("Both promises resolve", () => {

		it("GIVEN first promise is faster than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseResolve = () => resolve({someKey: "value1"});
			});
			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseResolve = () => resolve({someKey: "value2"});
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await firstPromiseResolve();
			await secondPromiseResolve();

			expect($log.debug.calls.count()).toBe(2);
			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted successCallback for promise identified by ${identifier1}`);

			expect(values).toEqual(["value2"]);
			expect(errors).toEqual([]);
		});

		it("GIVEN first promise is slower than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseResolve = () => resolve({someKey: "value1"});
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseResolve = () => resolve({someKey: "value2"});
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await secondPromiseResolve();
			await firstPromiseResolve();

			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted successCallback for promise identified by ${identifier1}`);

			expect(values).toEqual(["value2"]);
			expect(errors).toEqual([]);
		});

		it("GIVEN 2 promises on different identifiers, they do not interfere", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseResolve = () => resolve({someKey: "value1"});
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseResolve = () => resolve({someKey: "value2"});
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier2, secondPromise, successcallback, failurecallback);

			await firstPromiseResolve();
			await secondPromiseResolve();

			expect($log.debug).not.toHaveBeenCalled();

			expect(values).toEqual(["value1", "value2"]);
			expect(errors).toEqual([]);
		});

	});


	describe("Both promises reject", () => {

		it("GIVEN first promise is faster than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseReject = () => reject(new Error("value1"));
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseReject = () => reject(new Error("value2"));
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await firstPromiseReject();
			await secondPromiseReject();

			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted failureCallback for promise identified by ${identifier1}`);

			expect(values).toEqual([]);
			expect(errors).toEqual(["value2"]);
		});

		it("GIVEN first promise is slower than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseReject = () => reject(new Error("value1"));
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseReject = () => reject(new Error("value2"));
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await secondPromiseReject();
			await firstPromiseReject();

			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted failureCallback for promise identified by ${identifier1}`);

			expect(values).toEqual([]);
			expect(errors).toEqual(["value2"]);
		});

		it("GIVEN 2 promises on different identifiers, they do not interfere", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseReject = () => reject(new Error("value1"));
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseReject = () => reject(new Error("value2"));
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier2, secondPromise, successcallback, failurecallback);

			await firstPromiseReject();
			await secondPromiseReject();

			expect($log.debug).not.toHaveBeenCalled();

			expect(values).toEqual([]);
			expect(errors).toEqual(["value1", "value2"]);

		});

	});

	describe("First promise resolves and second rejects", () => {

		it("GIVEN first promise is faster than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseResolve = () => resolve({someKey: "value1"});
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseReject = () => reject(new Error("value2"));
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await firstPromiseResolve();
			await secondPromiseReject();

			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted successCallback for promise identified by ${identifier1}`);

			expect(values).toEqual([]);
			expect(errors).toEqual(["value2"]);
		});

		it("GIVEN first promise is slower than second, the second still takes precedence", async () => {

			const firstPromise = new Promise(function(resolve, reject) {
				firstPromiseResolve = () => resolve({someKey: "value1"});
			});

			const secondPromise = new Promise(function(resolve, reject) {
				secondPromiseReject = () => reject(new Error("value2"));
			});

			discardablePromiseUtils.apply(identifier1, firstPromise, successcallback, failurecallback);
			discardablePromiseUtils.apply(identifier1, secondPromise, successcallback, failurecallback);

			await secondPromiseReject();
			await firstPromiseResolve();

			expect($log.debug).toHaveBeenCalledWith(`competing promise for key ${identifier1}`);
			expect($log.debug).toHaveBeenCalledWith(`aborted successCallback for promise identified by ${identifier1}`);

			expect(values).toEqual([]);
			expect(errors).toEqual(["value2"]);
		});

	});
});
