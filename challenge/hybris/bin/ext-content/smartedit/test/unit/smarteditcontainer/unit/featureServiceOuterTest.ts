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
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';
import {annotationService, GatewayProxied, IContextualMenuButton, IDecorator, IFeature, IToolbarItem} from 'smarteditcommons';
import {FeatureService} from 'smarteditcontainer/services';
describe('outer featureService', () => {

	const toolbarServiceFactory = jasmine.createSpyObj('toolbarServiceFactory', ['getToolbarService']);
	const toolbarService = jasmine.createSpyObj('toolbarService', ['addItems', 'removeItemByKey']);

	const cloneableUtils = jasmine.createSpyObj('cloneableUtils', ['makeCloneable']);
	cloneableUtils.makeCloneable.and.callFake((arg: any) => arg);

	const $q = promiseHelper.$q();
	const lodash = (window as any).smarteditLodash;
	let featureService: FeatureService;

	beforeEach(() => {
		coreAnnotationsHelper.init();
		featureService = new FeatureService(toolbarServiceFactory, lodash, cloneableUtils, $q);
	});

	beforeEach(() => {
		toolbarServiceFactory.getToolbarService.and.returnValue(toolbarService);
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(FeatureService, GatewayProxied);
		expect(decoratorObj).toEqual(['_registerAliases', 'addToolbarItem', 'register', 'enable', 'disable', '_remoteEnablingFromInner', '_remoteDisablingFromInner', 'addDecorator', 'getFeatureProperty', 'addContextualMenuButton']);
	});

	it('initializes', () => {
		expect(featureService.getFeatureKeys()).toEqual([]);
	});

	it('leaves addDecorator unimplemented', () => {
		expect(featureService.addDecorator).toBeEmptyFunction();
	});

	it('leaves addContextualMenuButton unimplemented', () => {
		expect(featureService.addContextualMenuButton).toBeEmptyFunction();
	});

	it('register throws exception if key is not provided', () => {

		expect(() => {
			featureService.register({
				nameI18nKey: 'nameI18nKey',
				enablingCallback: angular.noop,
				disablingCallback: angular.noop
			} as IFeature);
		}).toThrowError("featureService.configuration.key.error.required");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register throws exception if nameI18nKey is not provided', () => {

		expect(() => {
			featureService.register({
				key: 'somekey',
				enablingCallback: angular.noop,
				disablingCallback: angular.noop
			} as IFeature);
		}).toThrowError("featureService.configuration.nameI18nKey.error.required");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register throws exception if enablingCallback is not provided', function() {

		const configuration: IFeature = {
			key: 'somekey',
			nameI18nKey: 'nameI18nKey',
			disablingCallback: jasmine.createSpy("disablingCallback")
		};

		expect(function() {
			featureService.register(configuration);
		}).toThrowError("featureService.configuration.enablingCallback.error.not.function");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register throws exception if enablingCallback is not a function', function() {

		const configuration: IFeature = {
			key: 'somekey',
			nameI18nKey: 'nameI18nKey',
			enablingCallback: 'somestringinsteadofafunction' as any,
			disablingCallback: jasmine.createSpy("disablingCallback")
		};

		expect(function() {
			featureService.register(configuration);
		}).toThrowError("featureService.configuration.enablingCallback.error.not.function");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register throws exception if disablingCallback is not provided', function() {


		const configuration: IFeature = {
			key: 'somekey',
			nameI18nKey: 'nameI18nKey',
			enablingCallback: jasmine.createSpy("enablingCallback")
		};

		expect(function() {
			featureService.register(configuration);
		}).toThrowError("featureService.configuration.disablingCallback.error.not.function");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register throws exception if disablingCallback is not a function', function() {

		const configuration: IFeature = {
			key: 'somekey',
			nameI18nKey: 'nameI18nKey',
			enablingCallback: jasmine.createSpy("enablingCallback"),
			disablingCallback: 'somestringinsteadofafunction' as any
		};

		expect(function() {
			featureService.register(configuration);
		}).toThrowError("featureService.configuration.disablingCallback.error.not.function");

		expect((featureService as any)._featuresToAlias).toBeUndefined();
	});

	it('register delegates to _registerAliases everything but the callbacks that are kept along with the key', function() {

		const enablingCallback: jasmine.Spy = jasmine.createSpy("enablingCallback");
		const disablingCallback: jasmine.Spy = jasmine.createSpy("disablingCallback");

		const configuration: IFeature = {
			key: 'somekey',
			nameI18nKey: 'somenameI18nKey',
			enablingCallback,
			disablingCallback
		};

		expect((featureService as any)._featuresToAlias).toBeUndefined();

		featureService.register(configuration);

		expect((featureService as any)._featuresToAlias).toEqual({
			somekey: {
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function)
			}
		});

		expect(cloneableUtils.makeCloneable).toHaveBeenCalledWith(configuration);
	});

	it('GIVEN that feature alias is found in the same frame, THEN enable will call the enabling callback is called and no request is sent across the gateway', function() {

		const enablingCallback = jasmine.createSpy('enablingCallback');
		const disablingCallback = jasmine.createSpy('disablingCallback');
		const key = 'key1';
		spyOn(featureService as any, '_remoteEnablingFromInner').and.returnValue($q.when());
		spyOn(featureService as any, '_remoteDisablingFromInner').and.returnValue($q.when());

		(featureService as any)._featuresToAlias = {};
		(featureService as any)._featuresToAlias[key] = {
			enablingCallback,
			disablingCallback
		};

		featureService.enable('key1');

		expect(enablingCallback).toHaveBeenCalled();
		expect(disablingCallback).not.toHaveBeenCalled();
		expect((featureService as any)._remoteEnablingFromInner).not.toHaveBeenCalled();
		expect((featureService as any)._remoteDisablingFromInner).not.toHaveBeenCalled();
	});

	it('GIVEN that feature alias is not found in the same frame, THEN enable will send a request across the gateway to call remote enablingCallback', function() {

		const enablingCallback = jasmine.createSpy('enablingCallback');
		const disablingCallback = jasmine.createSpy('disablingCallback');
		spyOn(featureService as any, '_remoteEnablingFromInner').and.returnValue($q.when());
		spyOn(featureService as any, '_remoteDisablingFromInner').and.returnValue($q.when());
		const key = 'key2';

		(featureService as any)._featuresToAlias = {};
		(featureService as any)._featuresToAlias[key] = {
			enablingCallback,
			disablingCallback
		};

		featureService.enable('key1');

		expect(enablingCallback).not.toHaveBeenCalled();
		expect(disablingCallback).not.toHaveBeenCalled();
		expect((featureService as any)._remoteEnablingFromInner).toHaveBeenCalledWith('key1');
		expect((featureService as any)._remoteDisablingFromInner).not.toHaveBeenCalled();
	});

	it('GIVEN that feature alias is found in the same frame, THEN disable will call the disabling callback is called and no request is sent across the gateway', function() {

		const enablingCallback = jasmine.createSpy('enablingCallback');
		const disablingCallback = jasmine.createSpy('disablingCallback');
		spyOn(featureService as any, '_remoteEnablingFromInner').and.returnValue($q.when());
		spyOn(featureService as any, '_remoteDisablingFromInner').and.returnValue($q.when());
		const key = 'key1';

		(featureService as any)._featuresToAlias = {};
		(featureService as any)._featuresToAlias[key] = {
			enablingCallback,
			disablingCallback
		};
		featureService.disable('key1');

		expect(enablingCallback).not.toHaveBeenCalled();
		expect(disablingCallback).toHaveBeenCalled();
		expect((featureService as any)._remoteEnablingFromInner).not.toHaveBeenCalled();
		expect((featureService as any)._remoteDisablingFromInner).not.toHaveBeenCalled();
	});

	it('GIVEN that feature alias is not found in the same frame, THEN disable will send a request across the gateway to call remote disablingCallback', function() {

		const enablingCallback = jasmine.createSpy('enablingCallback');
		const disablingCallback = jasmine.createSpy('disablingCallback');
		spyOn(featureService as any, '_remoteEnablingFromInner').and.returnValue($q.when());
		spyOn(featureService as any, '_remoteDisablingFromInner').and.returnValue($q.when());
		const key = 'key2';

		(featureService as any)._featuresToAlias = {};
		(featureService as any)._featuresToAlias[key] = {
			enablingCallback,
			disablingCallback
		};

		featureService.disable('key1');

		expect(enablingCallback).not.toHaveBeenCalled();
		expect(disablingCallback).not.toHaveBeenCalled();
		expect((featureService as any)._remoteEnablingFromInner).not.toHaveBeenCalled();
		expect((featureService as any)._remoteDisablingFromInner).toHaveBeenCalledWith('key1');
	});

	it('_registerAliases pushes to the features list and assigns an id', () => {
		(featureService as any)._registerAliases({
			key: 'somekey',
			nameI18nKey: 'somenameI18nKey',
			descriptionI18nKey: 'somedescriptionI18nKey'
		});
		expect((featureService as any).features).toEqual([{
			id: 'c29tZWtleQ==',
			key: 'somekey',
			nameI18nKey: 'somenameI18nKey',
			descriptionI18nKey: 'somedescriptionI18nKey'
		}]);
	});

	it('_registerAliases does not push a feature it already contains', () => {
		(featureService as any)._registerAliases({
			key: 'somekey',
			nameI18nKey: 'somenameI18nKey',
			descriptionI18nKey: 'somedescriptionI18nKey'
		});
		(featureService as any)._registerAliases({
			key: 'someOtherKey',
			nameI18nKey: 'someOthernameI18nKey',
			descriptionI18nKey: 'someOthertdescriptionI18nKey'
		});
		(featureService as any)._registerAliases({
			key: 'somekey',
			nameI18nKey: 'fsgdfgwrteg',
			descriptionI18nKey: 'sdfgstgrwwwwr'
		});

		expect((featureService as any).features).toEqual([{
			id: 'c29tZWtleQ==',
			key: 'somekey',
			nameI18nKey: 'somenameI18nKey',
			descriptionI18nKey: 'somedescriptionI18nKey'

		}, {
			id: 'c29tZU90aGVyS2V5',
			key: 'someOtherKey',
			nameI18nKey: 'someOthernameI18nKey',
			descriptionI18nKey: 'someOthertdescriptionI18nKey'
		}]);
	});

	describe('addToolbarItem will call register', () => {
		let configuration: IToolbarItem;
		let expectedRegisterConfiguration: IToolbarItem;
		let featureServiceRegisterSpy;
		let subconfig: IToolbarItem;
		beforeEach(() => {
			featureServiceRegisterSpy = spyOn(featureService, 'register').and.returnValue($q.when());
			featureServiceRegisterSpy.calls.reset();

			configuration = {
				toolbarId: 'sometoolbarId',
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				icons: ['url1', 'url2'],
				type: 'HYBRID_ACTION',
				include: 'sometemplate.html',
				callback: jasmine.createSpy('callback')
			};

			expectedRegisterConfiguration = {
				toolbarId: 'sometoolbarId',
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				icons: ['url1', 'url2'],
				type: 'HYBRID_ACTION',
				include: 'sometemplate.html',
				callback: configuration.callback,
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function)
			};

			featureService.addToolbarItem(configuration);
			expect(featureServiceRegisterSpy.calls.count()).toBe(1);
			expect(featureService.register).toHaveBeenCalledWith(expectedRegisterConfiguration);
			expect(toolbarServiceFactory.getToolbarService).toHaveBeenCalledWith('sometoolbarId');
			subconfig = featureServiceRegisterSpy.calls.argsFor(0)[0];

		});
		it('toolbarService.addItems callback', () => {
			expect(toolbarService.addItems).not.toHaveBeenCalled();
			subconfig.enablingCallback();
			expect(toolbarService.addItems).toHaveBeenCalledWith([{
				toolbarId: 'sometoolbarId',
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				icons: ['url1', 'url2'],
				type: 'HYBRID_ACTION',
				include: 'sometemplate.html',
				callback: configuration.callback,
				enablingCallback: jasmine.any(Function),
				disablingCallback: jasmine.any(Function)
			}]);
			expect(toolbarService.addItems.calls.count()).toBe(1);
		});
		it('toolbarService.removeItemByKey callback', () => {
			expect(toolbarService.removeItemByKey).not.toHaveBeenCalled();
			subconfig.disablingCallback();
			expect(toolbarService.removeItemByKey).toHaveBeenCalledWith('somekey');
			expect(toolbarService.removeItemByKey.calls.count()).toBe(1);
		});
	});

	it('getFeatureKeys fetches the list of features keys', () => {
		(featureService as any).features = [{
			key: 'key1',
			name: 'dfgsdfg'
		}, {
			key: 'key2',
			name: 'fgsfd'
		}, {
			key: 'key3',
			name: 'afgfdgsdf'
		}];
		expect(featureService.getFeatureKeys()).toEqual(['key1', 'key2', 'key3']);
	});

	describe('permissions', () => {

		let configuration: IContextualMenuButton | IDecorator | IToolbarItem;
		const FEATURE_KEY = 'key';
		const PERMISSION_NAME = 'se.fake.permission';
		beforeEach(() => {
			configuration = {
				key: FEATURE_KEY,
				nameI18nKey: 'nameI18nKey',
				enablingCallback: () => { /* */},
				disablingCallback: () => { /* */},
				permissions: [PERMISSION_NAME]
			};
		});

		it('featureService registers with permissions returns permissions for feature by calling getFeatureProperty', () => {
			// GIVEN
			featureService.register(configuration);

			// WHEN
			const permissions = (featureService.getFeatureProperty(FEATURE_KEY, "permissions") as IExtensiblePromise<string>).value;

			// THEN
			expect(permissions).toEqual([PERMISSION_NAME]);
		});
	});
});
