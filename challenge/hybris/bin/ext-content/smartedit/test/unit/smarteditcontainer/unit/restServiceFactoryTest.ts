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
import 'jasmine';
import * as angular from 'angular';
import * as lo from 'lodash';
import {IExtensibleResourceClass, IRestService, Page, Pageable, Payload} from 'smarteditcommons';
import {RestServiceFactory} from 'smarteditcontainer/services';
import {promiseHelper} from 'testhelpers';

describe('test RestServiceFactory ', () => {

	class DTO {
	}

	let restServiceFactory: RestServiceFactory;
	const uri: string = "theuri";
	const identifier = "customidentifier";

	let extensibleResourceClass: IExtensibleResourceClass<DTO>;

	let $q: jasmine.SpyObj<angular.IQService>;
	let $resource: jasmine.Spy;
	let lodash: lo.LoDashStatic;

	const voidPromise = promiseHelper.buildPromise<void>('voidPromise');
	const singleInstancePromise = promiseHelper.buildPromise<DTO>('singInstancePromise');
	const arrayPromise = promiseHelper.buildPromise<DTO[]>('arrayPromise');
	const pagePromise = promiseHelper.buildPromise<Page<DTO>>('pagePromise');
	const errorPromise = promiseHelper.buildPromise<any>('errorPromise');

	beforeEach(() => {

		lodash = (window as any).smarteditLodash;

		$q = jasmine.createSpyObj<angular.IQService>('$q', ['reject']);
		$q.reject.and.returnValue(errorPromise);

		$resource = jasmine.createSpy('$resource');

		extensibleResourceClass = jasmine.createSpyObj<IExtensibleResourceClass<DTO>>('extensibleResourceClass', ['getById', 'get', 'query', 'page', 'update', 'save', 'remove']);
		(extensibleResourceClass.getById as any).and.returnValue({$promise: singleInstancePromise});
		(extensibleResourceClass.get as any).and.returnValue({$promise: singleInstancePromise});
		(extensibleResourceClass.query as any).and.returnValue({$promise: arrayPromise});
		(extensibleResourceClass.page as any).and.returnValue({$promise: pagePromise});
		(extensibleResourceClass.update as any).and.returnValue({$promise: singleInstancePromise});
		(extensibleResourceClass.save as any).and.returnValue({$promise: singleInstancePromise});
		(extensibleResourceClass.remove as any).and.returnValue({$promise: voidPromise});

		$resource.and.returnValue(extensibleResourceClass);

		restServiceFactory = new RestServiceFactory($q, $resource, lodash);

	});

	function assertOnCallTo$Resource(finalUri: string) {
		expect($resource).toHaveBeenCalledWith(finalUri, {}, jasmine.any(Object));
	}
	function getMethodMap(): any {
		return $resource.calls.argsFor(0)[2];
	}
	function assertOnMethodConfiguration(methodName: string, actionDescriptor: angular.resource.IActionDescriptor) {

		expect(getMethodMap()[methodName]).toEqual(actionDescriptor);
	}

	it('returns singleton for a given API key', () => {

		expect(restServiceFactory.get<DTO>(uri, identifier)).toBe(restServiceFactory.get<DTO>(uri, identifier));
		expect(restServiceFactory.get<DTO>(uri, identifier)).not.toBe(restServiceFactory.get<DTO>(uri));
	});


	it('calls $resource and adds getById, get, query, page, save, update and remove custom methods', () => {

		restServiceFactory.get<DTO>(uri, identifier);
		const methodMap = $resource.calls.argsFor(0)[2];
		expect(Object.keys(methodMap)).toEqual(['getById', 'get', 'query', 'page', 'update', 'save', 'remove']);
	});

	it('calls $resource for the given API suffixed with default identifier placeholder', () => {

		restServiceFactory.get<DTO>(uri);

		assertOnCallTo$Resource(uri + '/:identifier');
	});

	it('calls $resource for the given API suffixed with custom identifier placeholder', () => {

		restServiceFactory.get<DTO>(uri, identifier);

		assertOnCallTo$Resource(uri + '/:' + identifier);
	});

	it('calls $resource for the given API prefixed with domain when set', () => {

		restServiceFactory.setDomain("someDomain");

		restServiceFactory.get<DTO>(uri, "customidentifier");

		assertOnCallTo$Resource('someDomain/' + uri + '/:' + identifier);
	});


	it('$resource is initialized with a custom getById method', () => {

		const restService = restServiceFactory.get<DTO>(uri);

		assertOnMethodConfiguration("getById", {
			method: 'GET',
			params: {},
			isArray: false,
			cache: false,
			headers: {
				'x-requested-with': 'Angular',
				'Pragma': 'no-cache'
			},
			transformResponse: jasmine.any(Function)
		});

		checkTransformResponseDoesntAddHeaders("getById");
		restService.activateMetadata();
		checkTransformResponseAddsHeaders("getById");
	});

	it('$resource is initialized with a custom get method', () => {

		const restService = restServiceFactory.get<DTO>(uri);

		assertOnMethodConfiguration("get", {
			method: 'GET',
			params: {},
			isArray: false,
			cache: false,
			headers: {
				'x-requested-with': 'Angular',
				'Pragma': 'no-cache'
			},
			transformResponse: jasmine.any(Function)
		});

		checkTransformResponseDoesntAddHeaders("get");
		restService.activateMetadata();
		checkTransformResponseAddsHeaders("get");

	});

	it('$resource is initialized with a custom query method', () => {

		const restService = restServiceFactory.get<DTO>(uri);

		assertOnMethodConfiguration("query", {
			method: 'GET',
			params: {},
			isArray: true,
			cache: false,
			headers: {
				'x-requested-with': 'Angular',
				'Pragma': 'no-cache'
			},
			transformResponse: jasmine.any(Function)
		});

		checkTransformResponseDoesntAddHeaders("query");
		restService.activateMetadata();
		checkTransformResponseAddsHeaders("query");

	});

	it('$resource is initialized with a custom update method', () => {

		restServiceFactory.get<DTO>(uri);

		assertOnMethodConfiguration("update", {
			method: 'PUT',
			cache: false,
			headers: {
				'x-requested-with': 'Angular'
			}
		});

	});

	it('$resource is initialized with a custom page method', () => {

		const restService = restServiceFactory.get<DTO>(uri);

		assertOnMethodConfiguration("page", {
			method: 'GET',
			params: {},
			isArray: false,
			cache: false,
			headers: {
				'x-requested-with': 'Angular',
				'Pragma': 'no-cache'
			},
			transformResponse: jasmine.any(Function)
		});

		checkTransformResponseDoesntAddHeaders("page");
		restService.activateMetadata();
		checkTransformResponseAddsHeaders("page");

	});

	it('getById delegates to $resource.getById and passes identifier in params object with expected key', () => {

		const restService = restServiceFactory.get<DTO>(uri, identifier);

		expect(restService.getById("someId")).toBe(singleInstancePromise);

		expect(extensibleResourceClass.getById).toHaveBeenCalledWith({
			customidentifier: 'someId'
		}, {});
	});

	it('get delegates to $resource.get and passes passes copy of payload as params', () => {

		const restService = restServiceFactory.get<DTO>(uri, identifier);

		const search = {
			key1: 'key1Value',
			key2: 'key2Value'
		} as Payload;

		expect(restService.get(search)).toBe(singleInstancePromise);

		expect(extensibleResourceClass.get).toHaveBeenCalledWith(search, {});
	});

	it('query delegates to $resource.query and passes passes copy of payload as params', () => {

		const restService = restServiceFactory.get<DTO>(uri, identifier);

		const search = {
			key1: 'key1Value',
			key2: 'key2Value'
		} as Payload;

		expect(restService.query(search)).toBe(arrayPromise);

		expect(extensibleResourceClass.query).toHaveBeenCalledWith(search, {});
	});

	it('page delegates to $resource.page and passes passes copy of payload as params', () => {

		const restService = restServiceFactory.get<DTO>(uri, identifier);

		const search = {
			key1: 'key1Value',
			key2: 'key2Value',
			currentPage: 5
		} as Pageable;

		expect(restService.page(search)).toBe(pagePromise);

		expect(extensibleResourceClass.page).toHaveBeenCalledWith(search, {});
	});

	it('save delegates to $resource.save and extract from payload identifier and placeholders to feed params', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/:ph2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		} as Payload;

		expect(restService.save(payload)).toBe(singleInstancePromise);

		expect(extensibleResourceClass.save).toHaveBeenCalledWith({
			ph1: 'ph1Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		}, {
				key1: 'key1Value',
				ph1: 'ph1Value',
				key2: 'key2Value',
				ph2: 'ph1Value',
				customidentifier: 'myId'
			});
	});

	it('update is rejected if identifier is missing from payload', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/:ph2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value'
		} as Payload;

		expect(restService.update(payload)).toBe(errorPromise);

		expect(extensibleResourceClass.update).not.toHaveBeenCalled();
	});

	it('update delegates to $resource.update and extract from payload identifier and placeholders to feed params', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/:ph2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		} as Payload;

		expect(restService.update(payload)).toBe(singleInstancePromise);

		expect(extensibleResourceClass.update).toHaveBeenCalledWith({
			ph1: 'ph1Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		}, {
				key1: 'key1Value',
				ph1: 'ph1Value',
				key2: 'key2Value',
				ph2: 'ph1Value',
				customidentifier: 'myId'
			});
	});

	it('update delegates to $resource.update and extract from payload identifier, query params and placeholders to feed params', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/someResource?key1=:key1&key2=:key2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		} as Payload;

		expect(restService.update(payload)).toBe(singleInstancePromise);

		expect(extensibleResourceClass.update).toHaveBeenCalledWith({
			ph1: 'ph1Value',
			key1: 'key1Value',
			key2: 'key2Value',
			customidentifier: 'myId'
		}, {
				key1: 'key1Value',
				ph1: 'ph1Value',
				key2: 'key2Value',
				ph2: 'ph1Value',
				customidentifier: 'myId'
			});
	});

	it('remove is rejected if identifier is missing from payload', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/:ph2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value'
		} as Payload;

		expect(restService.update(payload)).toBe(errorPromise);

		expect(extensibleResourceClass.remove).not.toHaveBeenCalled();
	});

	it('remove delegates to $resource.remove and extract from payload identifier and placeholders to feed params and empties payload', () => {

		const restService = restServiceFactory.get<DTO>("someAPI/:ph1/:ph2", identifier);

		const payload = {
			key1: 'key1Value',
			ph1: 'ph1Value',
			key2: 'key2Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		} as Payload;

		expect(restService.remove(payload)).toBe(voidPromise);

		expect(extensibleResourceClass.remove).toHaveBeenCalledWith({
			ph1: 'ph1Value',
			ph2: 'ph1Value',
			customidentifier: 'myId'
		}, {});
	});


	function checkTransformResponseDoesntAddHeaders(method: keyof IRestService<any>): void {

		const data = {
			a: 1,
			b: 'abc'
		};
		const transformedResponse = getMethodMap()[method].transformResponse(JSON.stringify(data), () => {
			return {
				header1: 'header1Value'
			};
		});

		expect(transformedResponse).toEqual({
			a: 1,
			b: 'abc'
		});

	}

	function checkTransformResponseAddsHeaders(method: keyof IRestService<any>): void {

		const data = {
			a: 1,
			b: 'abc'
		};
		const transformedResponse = getMethodMap()[method].transformResponse(JSON.stringify(data), () => {
			return {
				header1: 'header1Value'
			};
		});

		expect(transformedResponse).toEqual({
			a: 1,
			b: 'abc',
			headers: {
				header1: 'header1Value'
			}
		});

	}
});
