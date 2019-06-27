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
import {ConfigurationObject} from 'smarteditcontainer/services/bootstrap/Configuration';
import {BootstrapService, SharedDataService} from 'smarteditcontainer/services';
import {promiseHelper, PromiseType} from 'testhelpers';

describe('bootstrapService', () => {

	const WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY = "whiteListedStorefronts";
	let bootstrapService: BootstrapService;
	let configurationExtractorService: any;
	let sharedDataService: SharedDataService;
	let injectJS: any;
	let $log: jasmine.SpyObj<angular.ILogService>;
	let $http: jasmine.SpyObj<angular.IHttpService>;
	let $q: jasmine.SpyObj<angular.IQService>;
	let smarteditBootstrapGateway: jasmine.SpyObj<any>;

	let addDependencyToSmartEditContainerSpy: jasmine.Spy;
	let successPromise: angular.IPromise<void>;
	let failurePromise: angular.IPromise<void>;

	const configurations = {
		'smarteditroot': 'smarteditroot1',
		'domain': 'domain1',
		'whiteListedStorefronts': ['a', 'b'],
		'authentication.credentials': {
			key2: 'value2'
		}
	} as ConfigurationObject;

	const lodash = (window as any).smarteditLodash;

	beforeEach(() => {

		$q = promiseHelper.$q();

		successPromise = promiseHelper.buildPromise("success");
		failurePromise = promiseHelper.buildPromise("failure", PromiseType.REJECTS);

		$log = jasmine.createSpyObj('$log', ['debug', 'error']);

		$http = jasmine.createSpyObj('$http', ['get']);
		$http.get.and.callFake((uri: string) => {
			if (uri === 'SELocationForAppX' || uri === 'SELocationForAppZ') {
				return successPromise;
			} else if (uri === 'SELocationForAppY') {
				return failurePromise;
			} else if (uri === 'SEForAppA' || uri === 'SEForAppB' || uri === 'SEForAppC' || uri === 'SEForAppD') {
				return successPromise;
			} else {
				throw new Error(`unexpected call to $http.get: ${uri}`);
			}
		});

		sharedDataService = jasmine.createSpyObj('sharedDataService', ['set', 'get']);

		configurationExtractorService = jasmine.createSpyObj('configurationExtractorService', ['extractSEContainerModules', 'extractSEModules']);
		configurationExtractorService.extractSEContainerModules.and.returnValue({
			applications: [{name: 'AppA', location: 'SEContainerLocationForAppA'}, {name: 'AppB', location: 'SEContainerLocationForAppB'}, {name: 'AppC', location: 'SEContainerLocationForAppC'}],
			authenticationMap: {
				key1: 'value1'
			}
		});

		configurationExtractorService.extractSEModules.and.returnValue({
			applications: [{name: 'AppX', location: 'SELocationForAppX'}, {name: 'AppY', location: 'SELocationForAppY'}, {name: 'AppZ', location: 'SELocationForAppZ'}],
			authenticationMap: {
				key1: 'value1'
			}
		});

		injectJS = jasmine.createSpyObj('injectJS', ['execute']);

		smarteditBootstrapGateway = jasmine.createSpyObj('smarteditBootstrapGateway', ['publish']);

		bootstrapService = new BootstrapService(
			configurationExtractorService,
			sharedDataService,
			injectJS,
			$log,
			$http,
			$q,
			WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY,
			smarteditBootstrapGateway,
			lodash
		);

		addDependencyToSmartEditContainerSpy = spyOn(bootstrapService, 'addDependencyToSmartEditContainer');
		addDependencyToSmartEditContainerSpy.and.callThrough();

		spyOn(bootstrapService, 'bootstrapSmartEditContainer').and.returnValue(null);


	});

	it('addDependencyToSmartEditContainer will attach module as a SmartEdit container dependency if it exists in angular', () => {
		// GIVEN
		angular.module('ExistentApp', []);
		bootstrapService.addDependencyToSmartEditContainer('ExistentApp');
		expect($log.debug).toHaveBeenCalled();

		expect(angular.module('smarteditcontainer').requires).toContain('ExistentApp');
	});

	it('addDependencyToSmartEditContainer will log an error if the module does not exist in angular', () => {
		// WHEN
		bootstrapService.addDependencyToSmartEditContainer('NonExistentApp');
		expect($log.error).toHaveBeenCalledWith("Failed to load outer module NonExistentApp; SmartEdit functionality may be compromised.");
		expect(angular.module('smarteditcontainer').requires).not.toContain('NonExistentApp');
	});

	it('calling bootstrapContainerModules will invoke extractSEContainerModules and inject the javascript sources,' +
		' push the modules to smarteditcontainer module and re-bootstrap smarteditcontainer', () => {

			addDependencyToSmartEditContainerSpy.and.returnValue(null);

			bootstrapService.bootstrapContainerModules(configurations);

			expect(injectJS.execute).toHaveBeenCalledWith(jasmine.objectContaining({
				srcs: ['SEContainerLocationForAppA', 'SEContainerLocationForAppB', 'SEContainerLocationForAppC']
			}));

			expect(Object.keys(injectJS.execute.calls.argsFor(0)[0]).length).toBe(2);

			const callback = injectJS.execute.calls.argsFor(0)[0].callback;

			expect(bootstrapService.addDependencyToSmartEditContainer).not.toHaveBeenCalled();
			expect(bootstrapService.bootstrapSmartEditContainer).not.toHaveBeenCalled();

			callback();

			expect(bootstrapService.addDependencyToSmartEditContainer).toHaveBeenCalledWith('AppA');
			expect(bootstrapService.addDependencyToSmartEditContainer).toHaveBeenCalledWith('AppB');
			expect(bootstrapService.bootstrapSmartEditContainer).toHaveBeenCalled();

			expect(sharedDataService.set).toHaveBeenCalledWith('authenticationMap', {
				key1: 'value1'
			});
			expect(sharedDataService.set).toHaveBeenCalledWith('credentialsMap', {
				key2: 'value2'
			});

		});

	it('outer applications will be sorted by means of extends keyword and applications extending unknown apps will be ignored', () => {

		configurationExtractorService.extractSEContainerModules.and.returnValue({
			applications: [
				{name: 'AppD', location: 'SEContainerLocationForAppD', extends: 'AppC'},
				{name: 'AppA', location: 'SEContainerLocationForAppA'},
				{name: 'AppE', location: 'SEContainerLocationForAppE', extends: 'unknownApp'},
				{name: 'AppC', location: 'SEContainerLocationForAppC', extends: 'AppB'},
				{name: 'AppB', location: 'SEContainerLocationForAppB'}
			]
		});

		bootstrapService.bootstrapContainerModules(configurations);

		expect(injectJS.execute).toHaveBeenCalledWith(jasmine.objectContaining({
			srcs: [
				'SEContainerLocationForAppA',
				'SEContainerLocationForAppB',
				'SEContainerLocationForAppC',
				'SEContainerLocationForAppD'
			]
		}));

		expect($log.error).toHaveBeenCalledWith("Application AppE located at SEContainerLocationForAppE is ignored because it extends an unknown application 'unknownApp'; SmartEdit functionality may be compromised.");

	});

	it('calling bootstrapSEApp will invoke extractSEModules and inject the javascript sources by means of postMessage and if the module fails to load it will not be injected as module AppY fails because of URL not found', () => {

		bootstrapService.bootstrapSEApp(configurations);

		expect(configurationExtractorService.extractSEModules).toHaveBeenCalledWith(configurations);

		expect(sharedDataService.set).toHaveBeenCalledWith('authenticationMap', {
			key1: 'value1'
		});
		expect(sharedDataService.set).toHaveBeenCalledWith('credentialsMap', {
			key2: 'value2'
		});

		expect(smarteditBootstrapGateway.publish).toHaveBeenCalledWith("bundle", {
			resources: {
				properties: {
					domain: 'domain1',
					smarteditroot: 'smarteditroot1',
					applications: ['AppX', 'AppZ'],
					whiteListedStorefronts: ['a', 'b']
				},
				js: ['smarteditroot1/static-resources/dist/smartedit/js/prelibraries.js',
					'smarteditroot1/static-resources/thirdparties/ckeditor/ckeditor.js',
					'smarteditroot1/static-resources/dist/smartedit/js/smartedit.js',
					'SELocationForAppX',
					'SELocationForAppZ',
					'smarteditroot1/static-resources/dist/smartedit/js/smarteditbootstrap.js'],
				css: ['smarteditroot1/static-resources/dist/smartedit/css/inner-styling.css']
			}
		});

		expect($log.error).toHaveBeenCalledWith("Failed to load application 'AppY' from location SELocationForAppY; SmartEdit functionality may be compromised.");

	});

	it('inner applications will be sorted by means of extends keyword and applications extending unknown apps will be ignored', () => {

		configurationExtractorService.extractSEModules.and.returnValue({
			applications: [
				{name: 'AppD', location: 'SEForAppD', extends: 'AppC'},
				{name: 'AppA', location: 'SEForAppA'},
				{name: 'AppE', location: 'SEForAppE', extends: 'unknownApp'},
				{name: 'AppC', location: 'SEForAppC', extends: 'AppB'},
				{name: 'AppB', location: 'SEForAppB'}
			]
		});

		bootstrapService.bootstrapSEApp(configurations);

		expect(smarteditBootstrapGateway.publish.calls.argsFor(0)[1].resources.js).toEqual(
			['smarteditroot1/static-resources/dist/smartedit/js/prelibraries.js',
				'smarteditroot1/static-resources/thirdparties/ckeditor/ckeditor.js',
				'smarteditroot1/static-resources/dist/smartedit/js/smartedit.js',
				'SEForAppA',
				'SEForAppB',
				'SEForAppC',
				'SEForAppD',
				'smarteditroot1/static-resources/dist/smartedit/js/smarteditbootstrap.js']
		);

		expect($log.error).toHaveBeenCalledWith("Application AppE located at SEForAppE is ignored because it extends an unknown application 'unknownApp'; SmartEdit functionality may be compromised.");

	});

});
