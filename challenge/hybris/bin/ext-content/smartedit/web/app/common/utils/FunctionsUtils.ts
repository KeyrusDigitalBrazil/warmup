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
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';

/**
 * @ngdoc service
 * @name functionsModule.service:FunctionsUtils
 *
 * @description
 * utility service around Functions.
 */
@SeInjectable()
export class FunctionsUtils {

	/*
	 * regexp matching function(a, $b){} and function MyFunction(a, $b){}
	 */
	private signatureArgsRegexp: RegExp = /function[\s\w]*\(([\w\s\$,]*)\)[\s]*{/;

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#isEmpty
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Will determine whether a function body is empty or should be considered empty for proxying purposes
     *
     * @param {Function} func, the function to evaluate
     * @returns {Boolean} a boolean.
     */
	isEmpty(func: (...args: any[]) => any) {
		return func.toString().match(/\{([\s\S]*)\}/m)[1].trim() === '' || /(proxyFunction)/g.test(func.toString().replace(/\s/g, ""));
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getArguments
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the array of string arguments of the given function signature
     * 
     * @param {Function} func the function to analyze
     * @returns {string[]} an array of string arguments
     */
	getArguments(func: (...args: any[]) => any) {
		try {
			return this.signatureArgsRegexp.exec(func.toString())[1].replace(/\s/g, "").split(",");
		} catch (e) {
			throw new Error(`failed to retrieve arguments list of ${func}`);
		}
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#hasArguments
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Determines whether a given function (anonymous or not) has arguments in it signature
     * 
     * @param {Function} func the function to analyze
     * @returns {boolean} true if the function has signature arguments
     */
	hasArguments(func: (...args: any[]) => any) {
		try {
			return !this.getLodash().isEmpty(this.signatureArgsRegexp.exec(func.toString())[1]);
		} catch (e) {
			throw new Error(`failed to retrieve arguments list of ${func}`);
		}
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getConstructorName
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion
     * 
     * @param {Function} func the function to analyze
     * @returns {string} the constructor name
     */
	getConstructorName(func: new (...args: any[]) => any) {
		try {
			// IE does not support constructor.name
			return func.name || /function (\$?\w+)\s*\(/.exec(func.toString())[1];
		} catch {
			throw new Error("[FunctionsUtils] - Cannot get name from invalid constructor.");
		}
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getInstanceConstructorName
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion of a class instance
     *
     * @param {Object} instance instance class to analyze
     * @returns {string} the constructor name of the instance
     */
	getInstanceConstructorName(instance: object): string {
		return this.getConstructorName(Object.getPrototypeOf(instance).constructor);
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#extendsConstructor
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Overrides a given constructor with a new constructor body. The resulting constructor will share the same prototype as the original one.
     * 
     * @param {(...args:any[]) => T} originalConstructor the original constructor to override
     * @returns {(...args:any[]) => T} newConstructorBody the new constructor body to execute in the override. It may or may not return an instance. Should it return an instance, the latter will be returned by the override.
     */
	extendsConstructor<T>(originalConstructor: (...args: any[]) => T, newConstructorBody: (...args: any[]) => T): any {
		// the new constructor behaviour
		const newConstructor: any = function(...args: any[]) {
			const result = newConstructorBody.apply(this, args);
			if (result) {
				return result;
			}
		};
		// copy prototype so intanceof operator still works
		newConstructor.prototype = originalConstructor.prototype;

		return newConstructor;
	}

    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getLodash
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns lodash service instance in a portable way
     * This is onyl to be used in unit testing and runtime code where angular DI is not available
     * @returns {lodash.LoDashStatic} the instance of the lodash service
     */
	public getLodash(): lo.LoDashStatic {
		return (window as any).smarteditLodash;
	}

	/** @internal */
	isUnitTestMode(): boolean {
		return typeof (window as any).__karma__ !== 'undefined';
	}

}

export const functionsUtils = new FunctionsUtils();
