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
import {annotationService, GatewayProxied, IRestService, Page, Pageable, Payload} from 'smarteditcommons';
import {DelegateRestService, RestServiceFactory} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';

describe('test DelegateRestService ', () => {

	class DTO {

	}
	const payload = {} as Payload;
	const pageable = {} as Pageable;

	let delegateRestService: DelegateRestService;

	const uri: string = "theuri";
	const identifier: string = "theidentifier";
	const methodname: string = "methodname";
	const metadataActivated = true;

	const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', ['get']);
	const restService: jasmine.SpyObj<IRestService<DTO>> = jasmine.createSpyObj<IRestService<DTO>>('iRestService', ['activateMetadata', 'getMethodForVoid', 'getMethodForSingleInstance', 'getMethodForArray', 'page']);

	const voidMethod: jasmine.Spy = jasmine.createSpy('voidMethod');
	const singleInstanceMethod: jasmine.Spy = jasmine.createSpy('singleInstanceMethod');
	const arrayMethod: jasmine.Spy = jasmine.createSpy('arrayMethod');

	const singleInstancePromise = promiseHelper.buildPromise<DTO>('singInstancePromise');
	const arrayPromise = promiseHelper.buildPromise<DTO[]>('arrayPromise');
	const pagePromise = promiseHelper.buildPromise<Page<DTO>>('pagePromise');
	const voidPromise = promiseHelper.buildPromise<any>('voidpromise');

	beforeEach(() => {

		voidMethod.and.returnValue(voidPromise);
		singleInstanceMethod.and.returnValue(singleInstancePromise);
		arrayMethod.and.returnValue(arrayPromise);

		restService.getMethodForVoid.and.returnValue(voidMethod);
		restService.getMethodForSingleInstance.and.returnValue(singleInstanceMethod);
		restService.getMethodForArray.and.returnValue(arrayMethod);
		restService.page.and.returnValue(pagePromise);

		restServiceFactory.get.and.returnValue(restService);

		coreAnnotationsHelper.init();
		delegateRestService = new DelegateRestService(restServiceFactory);
	});

	function assertOnCallToRestServiceFactory() {
		expect(restServiceFactory.get).toHaveBeenCalledWith(uri, identifier);
	}

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(DelegateRestService, GatewayProxied)).toEqual([]);
	});

	it('delegateForVoid delegates to getMethodForVoid of the appropriate RestService', function() {
		expect(delegateRestService.delegateForVoid(methodname, payload, uri, identifier, metadataActivated)).toBe(voidPromise);
		assertOnCallToRestServiceFactory();
	});

	it('delegateForSingleInstance delegates to getMethodForSingleInstance of the appropriate RestService', function() {
		expect(delegateRestService.delegateForSingleInstance(methodname, payload, uri, identifier, metadataActivated)).toBe(singleInstancePromise);
		assertOnCallToRestServiceFactory();
	});

	it('delegateForArray delegates to getMethodForVoid of the appropriate RestService', function() {
		expect(delegateRestService.delegateForArray(methodname, payload, uri, identifier, metadataActivated)).toBe(arrayPromise);
		assertOnCallToRestServiceFactory();
	});

	it('delegateForPage delegates to page of the appropriate RestService', function() {
		expect(delegateRestService.delegateForPage(pageable, uri, identifier, metadataActivated)).toBe(pagePromise);
		assertOnCallToRestServiceFactory();
	});

});
