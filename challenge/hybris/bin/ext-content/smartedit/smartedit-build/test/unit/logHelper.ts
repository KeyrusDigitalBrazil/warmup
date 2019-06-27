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
import * as angular from "angular";

export class LogHelper implements angular.ILogService {

	debug: angular.ILogCall = function() {
		const f: any = (...args: any[]) => {
			return console.debug.call(this, args);
		};
		f.logs = [];
		return f;
	}();


	info: angular.ILogCall = function() {
		const f: any = (...args: any[]) => {
			return console.info.call(this, args);
		};
		f.logs = [];
		return f;
	}();

	log: angular.ILogCall = function() {
		const f: any = (...args: any[]) => {
			return console.log.call(this, args);
		};
		f.logs = [];
		return f;
	}();

	error: angular.ILogCall = function() {
		const f: any = (...args: any[]) => {
			return console.error.call(this, args);
		};
		f.logs = [];
		return f;
	}();

	warn: angular.ILogCall = function() {
		const f: any = (...args: any[]) => {
			return console.warn.call(this, args);
		};
		f.logs = [];
		return f;
	}();

	constructor() {
		spyOn(this, 'debug');
		spyOn(this, 'info');
		spyOn(this, 'log');
		spyOn(this, 'error');
		spyOn(this, 'warn');
	}

	// from angular-mocks
	assertEmpty(): void {
		throw new Error(`LogHelper.assertEmpty() was not implemented`);
	}

	// from angular-mocks
	reset(): void {
		throw new Error(`LogHelper.reset() was not implemented`);
	}

}
