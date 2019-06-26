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

import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';
import {FeatureService} from 'smartedit/services';
import {annotationService, GatewayProxied, IContextualMenuButton, IDecorator} from 'smarteditcommons';
describe('inner featureService', () => {

	const $log = jasmine.createSpyObj('$log', ['warn', 'debug']);
	const decoratorService = jasmine.createSpyObj('decoratorService', ['enable', 'disable']);
	const contextualMenuService = jasmine.createSpyObj('contextualMenuService', ['addItems', 'removeItemByKey']);
	const $q = promiseHelper.$q();
	const lodash = (window as any).smarteditLodash;
	const cloneableUtils = jasmine.createSpyObj('cloneableUtils', ['makeCloneable']);
	cloneableUtils.makeCloneable.and.callFake((arg: any) => arg);

	let featureService: FeatureService;
	let featureServiceRegisterSpy: jasmine.Spy;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		featureService = new FeatureService($log, decoratorService, lodash, cloneableUtils, $q, contextualMenuService);
		featureServiceRegisterSpy = spyOn(featureService, 'register').and.returnValue($q.when());
		$log.warn.calls.reset();
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(FeatureService, GatewayProxied);
		expect(decoratorObj).toEqual(['_registerAliases', 'addToolbarItem', 'register', 'enable', 'disable', '_remoteEnablingFromInner', '_remoteDisablingFromInner', 'addDecorator', 'getFeatureProperty', 'addContextualMenuButton']);
	});

	it('leaves _registerAliases unimplemented', function() {
		expect((featureService as any)._registerAliases).toBeEmptyFunction();
	});

	it('leaves addToolbarItem unimplemented', function() {
		expect(featureService.addToolbarItem).toBeEmptyFunction();
	});

	it('leaves getFeatureProperty unimplemented', function() {
		expect(featureService.getFeatureProperty).toBeEmptyFunction();
	});

	describe('addDecorator', () => {
		let config;
		let subconfig: IDecorator;
		let promise: angular.IPromise<void>;

		beforeEach(() => {

			config = {
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				displayFunction: undefined
			} as IDecorator;
			promise = featureService.addDecorator(config);

			expect(featureServiceRegisterSpy.calls.count()).toBe(1);
			expect(featureService.register).toHaveBeenCalledWith({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function),
				displayFunction: undefined
			});
			subconfig = featureServiceRegisterSpy.calls.argsFor(0)[0];
		});

		it('resolves promise', function(done) {
			promise.then(function(value: any) {
				expect(value).toBeUndefined();
				done();
			});
		});

		it('addDecorator will delegate to decoratorService and prepare callback with decoratorService.enable function', function() {
			expect(decoratorService.enable).not.toHaveBeenCalled();
			subconfig.enablingCallback();
			expect(decoratorService.enable).toHaveBeenCalledWith('somekey', undefined);
			expect(decoratorService.enable.calls.count()).toBe(1);
		});
		it('addDecorator will delegate to decoratorService and prepare callback with decoratorService.disable function', function() {
			expect(decoratorService.disable).not.toHaveBeenCalled();
			subconfig.disablingCallback();
			expect(decoratorService.disable).toHaveBeenCalledWith('somekey');
			expect(decoratorService.disable.calls.count()).toBe(1);
		});
	});

	describe('addContextualMenuButton will call register', () => {
		let button: IContextualMenuButton;
		let registeredButton: IContextualMenuButton;
		let subconfig: IContextualMenuButton;
		let promise: angular.IPromise<void>;
		beforeEach(() => {
			contextualMenuService.addItems.calls.reset();
			contextualMenuService.removeItemByKey.calls.reset();
			button = {
				key: 'somekey',
				regexpKeys: ['someregexpKey', 'strictType'],
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				i18nKey: 'somei18nKey',
				condition: jasmine.any(Function),
				callback: jasmine.any(Function),
				displayClass: 'displayClass1 displayClass2',
				iconIdle: 'pathToIconIdle',
				iconNonIdle: 'pathToIconNonIdle',
				displaySmallIconClass: 'pathToSmallIcon'
			};

			registeredButton = {
				key: 'somekey',
				regexpKeys: ['someregexpKey', 'strictType'],
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				i18nKey: 'somei18nKey',
				condition: jasmine.any(Function),
				callback: jasmine.any(Function),
				displayClass: 'displayClass1 displayClass2',
				iconIdle: 'pathToIconIdle',
				iconNonIdle: 'pathToIconNonIdle',
				displaySmallIconClass: 'pathToSmallIcon',
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function)
			};
			promise = featureService.addContextualMenuButton(button);
			expect(featureServiceRegisterSpy.calls.count()).toBe(1);
			expect(featureService.register).toHaveBeenCalledWith(registeredButton);
			subconfig = featureServiceRegisterSpy.calls.argsFor(0)[0];
		});
		it('resolves promise', function(done) {
			promise.then(function(value: any) {
				expect(value).toBeUndefined();
				done();
			});
		});
		it('add contextualMenuService.addItems into the callbacks', function() {

			expect(contextualMenuService.addItems).not.toHaveBeenCalled();

			subconfig.enablingCallback();

			expect(contextualMenuService.addItems).toHaveBeenCalledWith({
				someregexpKey: [{
					key: 'somekey',
					i18nKey: 'somei18nKey',
					condition: button.condition,
					callback: button.callback,
					displayClass: 'displayClass1 displayClass2',
					iconIdle: 'pathToIconIdle',
					iconNonIdle: 'pathToIconNonIdle',
					displaySmallIconClass: 'pathToSmallIcon'
				}],
				strictType: [{
					key: 'somekey',
					i18nKey: 'somei18nKey',
					condition: button.condition,
					callback: button.callback,
					displayClass: 'displayClass1 displayClass2',
					iconIdle: 'pathToIconIdle',
					iconNonIdle: 'pathToIconNonIdle',
					displaySmallIconClass: 'pathToSmallIcon'
				}]
			});

			expect(contextualMenuService.addItems.calls.count()).toBe(1);
		});
		it('add contextualMenuService.removeItemByKey into the callbacks', function() {

			expect(contextualMenuService.removeItemByKey).not.toHaveBeenCalled();

			subconfig.disablingCallback();

			expect(contextualMenuService.removeItemByKey).toHaveBeenCalledWith('somekey');
			expect(contextualMenuService.removeItemByKey.calls.count()).toBe(1);
		});
	});

	describe('addSlotContextualMenuButton', function() {

		let button: IContextualMenuButton;
		let expectedFeatureCall: IContextualMenuButton;
		let expectedContextualMenuServiceCall: any;

		beforeEach(function() {
			contextualMenuService.addItems.calls.reset();
			contextualMenuService.removeItemByKey.calls.reset();
			button = {
				key: 'somekey',
				regexpKeys: ['someregexpKey', 'strictType'],
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				i18nKey: 'somei18nKey',
				condition: jasmine.any(Function),
				callback: jasmine.any(Function),
				displayClass: 'displayClass1 displayClass2',
				iconIdle: 'pathToIconIdle',
				iconNonIdle: 'pathToIconNonIdle',
				displaySmallIconClass: 'pathToSmallIcon'
			};

			expectedFeatureCall = {
				key: 'somekey',
				regexpKeys: ['someregexpKey', 'strictType'],
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				i18nKey: 'somei18nKey',
				condition: jasmine.any(Function),
				callback: jasmine.any(Function),
				displayClass: 'displayClass1 displayClass2',
				iconIdle: 'pathToIconIdle',
				iconNonIdle: 'pathToIconNonIdle',
				displaySmallIconClass: 'pathToSmallIcon',
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function)
			};

			expectedContextualMenuServiceCall = {
				someregexpKey: [{
					key: 'somekey',
					i18nKey: 'somei18nKey',
					condition: jasmine.any(Function),
					callback: jasmine.any(Function),
					displayClass: 'displayClass1 displayClass2',
					iconIdle: 'pathToIconIdle',
					iconNonIdle: 'pathToIconNonIdle',
					displaySmallIconClass: 'pathToSmallIcon'
				}],
				strictType: [{
					key: 'somekey',
					i18nKey: 'somei18nKey',
					condition: jasmine.any(Function),
					callback: jasmine.any(Function),
					displayClass: 'displayClass1 displayClass2',
					iconIdle: 'pathToIconIdle',
					iconNonIdle: 'pathToIconNonIdle',
					displaySmallIconClass: 'pathToSmallIcon'
				}]
			};

			featureService.addContextualMenuButton(button);
		});

		it('should call register', function() {

			expect(featureServiceRegisterSpy.calls.count()).toBe(1);
			expect(featureService.register).toHaveBeenCalledWith(expectedFeatureCall);

			expect(contextualMenuService.addItems).not.toHaveBeenCalled();
			expect(contextualMenuService.removeItemByKey).not.toHaveBeenCalled();
		});

		it('should add template by enabling callback', function() {
			const subconfig = featureServiceRegisterSpy.calls.argsFor(0)[0];

			subconfig.enablingCallback();

			expect(contextualMenuService.addItems).toHaveBeenCalledWith(expectedContextualMenuServiceCall);
			expect(contextualMenuService.removeItemByKey).not.toHaveBeenCalled();
		});

		it('should remove template by disabling callback', function() {
			const subconfig = featureServiceRegisterSpy.calls.argsFor(0)[0];
			subconfig.enablingCallback();
			subconfig.disablingCallback();

			expect(contextualMenuService.addItems.calls.count()).toBe(1);
			expect(contextualMenuService.removeItemByKey).toHaveBeenCalledWith('somekey');
		});

	});

});
