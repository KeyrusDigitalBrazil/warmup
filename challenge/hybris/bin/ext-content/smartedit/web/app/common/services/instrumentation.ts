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
import {Cloneable} from 'smarteditcommons';
import {FunctionsUtils} from 'smarteditcommons/utils/FunctionsUtils';
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';
interface ServiceConfig {
	name: string;
	exclude: string[];
	include: string[];
}

interface DelegateFunction<T> {
	name: keyof T;
	fn: (...args: any[]) => any;
}

interface FunctionExecutionData {
	serviceName: string;
	functionName: string;
	arguments: Cloneable;
	result: any;
}

/** @internal */
interface InstrumentationConfig {
	globalExcludedRecipes?: string[];
	globalIncludedRecipes?: string[];
	globalExcludedFunctions?: string[];
	globalIncludedFunctions?: string[];
	defaultExcludeModules?: string[];
	modules: string[];
	$provide: angular.auto.IProvideService;
}

/** @internal */
@SeInjectable()
class Instrumentation {

	private _FUNCTION_EXECUTION_DATA: {[key: string]: FunctionExecutionData} = {};
	private _DECORATED_MODULES: any[] = [];
	private _DEFAULT_LEVEL = 10;
	private alreadyBrowsed: string[] = [];
	private functionsUtils: FunctionsUtils = new FunctionsUtils();

	constructor(private readObjectStructure: (arg: Cloneable) => Cloneable) {}

	execute(config: InstrumentationConfig, _LEVEL: number, isRoot: boolean): void {
		const LEVEL = _LEVEL || this._DEFAULT_LEVEL;

		if (config.modules === undefined) {
			return;
		}

		this._logFunctionArgumentsAndReturnValue = this._logFunctionArgumentsAndReturnValue.bind(this);
		this._getServiceConfig = this._getServiceConfig.bind(this);
		this._getIterableDelegate = this._getIterableDelegate.bind(this);
		this._getDelegateFunctions = this._getDelegateFunctions.bind(this);
		this._extractComponentBindings = this._extractComponentBindings.bind(this);
		this._extractDirectiveBindings = this._extractDirectiveBindings.bind(this);
		const $provide = config.$provide;

		config.modules.forEach((moduleName: string) => {

			this.alreadyBrowsed.push(moduleName);

			if (!isRoot) {
				if (this._DECORATED_MODULES.indexOf(moduleName) > -1 || !moduleName.endsWith('Module')) {
					return;
				}
			}

			this._DECORATED_MODULES.push(moduleName);

			const moduleConfig = this._getServiceConfig(moduleName);
			// FIXME: necessary to concat?
			const serviceExcludeFunctions = moduleConfig.exclude.concat(config.globalExcludedFunctions || []);
			const serviceIncludeFunctions = moduleConfig.include.concat(config.globalIncludedFunctions || []);

			/* forbiddenNameSpaces angular.module:false */
			const module = angular.module(moduleName);

			(module as any)._invokeQueue.forEach((invoke: [string, string, [string]]) => {
				const fn = invoke[1];
				if (['factory', 'component', 'service', 'directive'].indexOf(fn) > -1) {
					const args = invoke[2];
					const recipeName = args[0];
					if (fn === 'component') {
						this._extractComponentBindings(args);
					} else if (fn === 'directive') {
						this._extractDirectiveBindings(args);
					} else {
						try {
							if (this._isEligible(recipeName, config.globalExcludedRecipes, config.globalIncludedRecipes)) {

								$provide.decorator(recipeName, ($delegate: (...param: any[]) => any) => {

									const iterableDelegate = this._getIterableDelegate($delegate);
									const originals: DelegateFunction<(...param: any[]) => any>[] = this._getDelegateFunctions(iterableDelegate, serviceExcludeFunctions, serviceIncludeFunctions);

									originals.forEach((original) => {
										if (!iterableDelegate[original.name].__DECORATED__) {
											const self = this;
											const isEmpty = self.functionsUtils.isEmpty(original.fn);
											if (isEmpty) {
												iterableDelegate[original.name] = function() {
													'proxyFunction';
													const result = original.fn.apply(this, arguments);
													self._logFunctionArgumentsAndReturnValue(recipeName, original.name, arguments, result);
													return result;
												};
											} else {
												iterableDelegate[original.name] = function() {
													const result = original.fn.apply(this, arguments);
													self._logFunctionArgumentsAndReturnValue(recipeName, original.name, arguments, result);
													return result;
												};
											}
											iterableDelegate[original.name].__DECORATED__ = true;
										}
									});
									return $delegate;
								});
							} else {
								this.warn("not eligible recipe : " + recipeName);
							}
						} catch (e) {
							this.error(e);
							this.warn("Warning-No-Service-Exists: " + recipeName + " of type " + fn + ", moduleName: " + moduleName);
						}
					}
				}
			});

			if (LEVEL > 0) {
				const NEW_LEVEL = LEVEL - 1;
				module.requires.forEach((_moduleName: string) => {
					if (this.alreadyBrowsed.indexOf(_moduleName) === -1
						&& config.defaultExcludeModules.indexOf(_moduleName) === -1
						&& _moduleName.indexOf("Mock") === -1) {
						this.execute({
							globalExcludedRecipes: config.globalExcludedRecipes,
							globalIncludedRecipes: config.globalIncludedRecipes,
							globalExcludedFunctions: config.globalExcludedFunctions,
							globalIncludedFunctions: config.globalIncludedFunctions,
							modules: [_moduleName],
							$provide: config.$provide,
							defaultExcludeModules: config.defaultExcludeModules
						}, NEW_LEVEL, false);
					}
				});
			}

		});
	}

	private _getServiceConfig(service: string): ServiceConfig {
		return typeof service === "string" ? {
			name: service,
			exclude: [],
			include: []
		} : service;
	}

	private _getIterableDelegate<T extends (...args: any[]) => any>($delegate: T) {
		return $delegate.prototype ? $delegate.prototype : $delegate;
	}

	private _matches(name: string, nameRegex: string): boolean {
		return (new RegExp(nameRegex, 'gi')).test(name);
	}

	private _getDelegateFunctions<T extends (...args: any[]) => any>($delegate: T, serviceExcludeFunctions: string[], serviceIncludeFunctions: string[]): DelegateFunction<T>[] {
		const result = [];

		for (const fnName in $delegate) {

			if (typeof $delegate[fnName] === "function") {
				if (this._isEligible(fnName, serviceExcludeFunctions, serviceIncludeFunctions)) {
					result.push({
						name: fnName,
						fn: $delegate[fnName] as any as (...args: any[]) => any
					});
				}
			}
		}
		return result;
	}

	private _isEligible(recipeName: string, excludes: string[], includes: string[]): boolean {
		return (!includes.length || !!includes.find(this._matches.bind(this, recipeName))) && (!excludes.length || !excludes.find(this._matches.bind(this, recipeName)));
	}

	private _resultIsPromise(result: angular.IPromise<any>) {
		return !!result && (result as any).$$state !== undefined;
	}

	private _keyExists(key: string) {
		if (this._FUNCTION_EXECUTION_DATA[key] !== undefined) {
			return true;
		}
		return false;
	}

	private _extractComponentBindings(args: any[]) {
		const componentName = args[0];
		const bindVariables = args[1].bindings;
		this._logDirectiveResult(componentName, bindVariables);
	}

	private _extractDirectiveBindings(args: any[]) {
		const directiveName = args[0];
		try {
			const secondAttribute = args[1];
			let directiveConfig = null;
			if (typeof secondAttribute === 'function') {
				directiveConfig = secondAttribute();
			} else if (secondAttribute instanceof Array) {
				directiveConfig = secondAttribute[secondAttribute.length - 1]();
			}
			if (directiveConfig) {
				const scope = directiveConfig.scope;
				const bindToController = directiveConfig.bindToController;
				this._logDirectiveResult(directiveName, undefined, scope, bindToController);
			}
		} catch (e) {
			this._logDirectiveResult(directiveName);
		}
	}

	private _logFunctionArgumentsAndReturnValue(serviceName: string, functionName: string, _args: IArguments, result: any | angular.IPromise<any>) {
		const args = this.readObjectStructure(Array.prototype.slice.call(_args));

		let key: string = null;
		try {
			key = serviceName + "~" + functionName + "~" + angular.toJson(args);
		} catch (e) {
			this.error('COULD NOT GENERATE KEY');
			return result;
		}
		if (this._keyExists(key)) {
			return result;
		}
		this._FUNCTION_EXECUTION_DATA[key] = {} as FunctionExecutionData;

		if (this._resultIsPromise(result)) {
			return result.then((res: any) => {
				try {
					this._FUNCTION_EXECUTION_DATA[key] = {
						serviceName,
						functionName,
						arguments: args,
						result: {
							promiseValue: this.readObjectStructure(res)
						}
					};
					this.warn(angular.toJson(this._FUNCTION_EXECUTION_DATA[key]));
				} catch (e) {
					this.error('COULD NOT STRINGIFY');
				}
				return Promise.resolve(res);
			}, (reason: Error) => {
				this.error('ERROR WHILE RESOLVING RESULT: ' + reason);
				this._FUNCTION_EXECUTION_DATA[key] = {
					serviceName,
					functionName,
					arguments: args,
					result: {promiseValue: reason}
				};
				this.warn(angular.toJson(this._FUNCTION_EXECUTION_DATA[key]));
				return Promise.reject(reason);
			});
		} else {

			this._FUNCTION_EXECUTION_DATA[key] = {
				serviceName,
				functionName,
				arguments: args,
				result: this.readObjectStructure(result)
			};
			try {
				this.warn(angular.toJson(this._FUNCTION_EXECUTION_DATA[key]));
			} catch (e) {
				this.error('COULD NOT STRINGIFY');
			}
			return result;
		}
	}

	private _logDirectiveResult(directiveName: string, bindings?: string[], scope?: angular.IScope, bindToController?: {[boundProperty: string]: string}) {
		this.warn(angular.toJson({
			directiveName,
			bindings,
			scope,
			bindToController
		}));
	}

	private warn(message: string): void {
		// tslint:disable-next-line
		console.warn(message)
	}

	private error(message: string): void {
		// tslint:disable-next-line
		console.error(message)
	}

}

function getItemFromSessionStorage(name: string): Cloneable {
	try {
		return window.sessionStorage.getItem(name);
	} catch (e) {
		/*
		 * would fail if:
		 * - sessionStorage is not implemented
		 * - accessing sessionStorage is forbidden in CORS because of default "Block third-party cookies" settings in chrome
		 */
		return null;
	}
}

/** @internal */
export const instrument = ($provide: any, readObjectStructure: (arg: Cloneable) => Cloneable, TOP_LEVEL_MODULE_NAME: string) => {
	'ngInject';

	if (getItemFromSessionStorage("isInstrumented") === "true") {
		new Instrumentation(readObjectStructure).execute({
			globalExcludedRecipes: [
				"assetsService",
				"configurationExtractorService",
				"experienceService"
			],
			globalIncludedRecipes: [
				"^.*Interface$",
				"^.*Service$",
				"^.*Helper$",
				"^.*Hanlder$",
				"^.*Editor$",
				"^I.*$",
				"^.*Decorator$",
				"^.*Directive$",
				"^.*Registry$",
				"^.*Listener$",
				"^.*Resource$",
				"^.*Populator$",
				"^.*Constants$",
				"^.*Factory$",
				"^.*Facade$",
				"^.*Interceptor$",
				"^.*Manager$",
				"^.*Class$",
				"^.*Strategy",
				"^.*Predicate",
				"^.*Retry",
				"^.*Gateway"
			],
			globalExcludedFunctions: ["^_.*$", "^\\$", "lodash", "yjQuery"],
			globalIncludedFunctions: ["^.*$"], // this regex helps to exclude private function prefixed with "_"
			defaultExcludeModules: [
				"yjqueryModule",
				"functionsModule",
				"ycmssmarteditModule",
				"timerModule",
				"ui.bootstrap",
				"ngResource",
				"ui.select",
				"yjQuery",
				"instrumentModule",
				"interceptorHelperModule",
				"i18nInterceptorModule",
				'loadConfigModule',
				'ui.tree',
				'treeModule' // contains fetchChildren function that extracts dom objects that contains circular structure
			],

			modules: [TOP_LEVEL_MODULE_NAME],
			$provide
		}, 15, true);
	}
};