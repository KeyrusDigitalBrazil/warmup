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
import {Page, Pageable, Payload} from 'smarteditcommons';
import {DelegateRestService, RestService} from 'smartedit/services';
import {promiseHelper} from 'testhelpers';

describe('test RestService ', () => {

	class DTO {
	}
	const payload = {} as Payload;
	const pageable: Pageable = {} as Pageable;

	let restService: RestService<DTO>;
	let delegateRestServiceMock: jasmine.SpyObj<DelegateRestService>;
	const uri: string = "theuri";
	const identifier = "theidentifier";

	const singleInstancePromise = promiseHelper.buildPromise<DTO>('singInstancePromise');
	const arrayPromise = promiseHelper.buildPromise<DTO[]>('arrayPromise');
	const pagePromise = promiseHelper.buildPromise<Page<DTO>>('pagePromise');
	const voidPromise = promiseHelper.buildPromise<any>('voidpromise');

	beforeEach(() => {

		delegateRestServiceMock = jasmine.createSpyObj<DelegateRestService>('delegateRestService', ['delegateForSingleInstance', 'delegateForArray', 'delegateForPage', 'delegateForVoid']);
		delegateRestServiceMock.delegateForSingleInstance.and.returnValue(singleInstancePromise);
		delegateRestServiceMock.delegateForArray.and.returnValue(arrayPromise);
		delegateRestServiceMock.delegateForPage.and.returnValue(pagePromise);
		delegateRestServiceMock.delegateForVoid.and.returnValue(voidPromise);
		restService = new RestService<DTO>(delegateRestServiceMock, uri, identifier);
	});

	it('getById delegates to delegateForSingleInstance', function() {
		expect(restService.getById("myid")).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("getById", "myid", uri, identifier, false);
		restService.activateMetadata();
		expect(restService.getById("myid")).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("getById", "myid", uri, identifier, true);
	});

	it('get delegates to delegateForSingleInstance', function() {
		expect(restService.get(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("get", payload, uri, identifier, false);
		restService.activateMetadata();
		expect(restService.get(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("get", payload, uri, identifier, true);
	});

	it('update delegates to delegateForSingleInstance', function() {
		expect(restService.update(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("update", payload, uri, identifier, false);
		restService.activateMetadata();
		expect(restService.update(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("update", payload, uri, identifier, true);
	});

	it('save delegates to delegateForSingleInstance', function() {
		expect(restService.save(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("save", payload, uri, identifier, false);
		restService.activateMetadata();
		expect(restService.save(payload)).toBe(singleInstancePromise);
		expect(delegateRestServiceMock.delegateForSingleInstance).toHaveBeenCalledWith("save", payload, uri, identifier, true);
	});

	it('query delegates to delegateForSingleInstance', function() {
		expect(restService.query(payload)).toBe(arrayPromise);
		expect(delegateRestServiceMock.delegateForArray).toHaveBeenCalledWith("query", payload, uri, identifier, false);
		restService.activateMetadata();
	});

	it('page delegates to delegateForPage', function() {
		expect(restService.page(pageable)).toBe(pagePromise);
		expect(delegateRestServiceMock.delegateForPage).toHaveBeenCalledWith(pageable, uri, identifier, false);
		restService.activateMetadata();
		expect(restService.page(pageable)).toBe(pagePromise);
		expect(delegateRestServiceMock.delegateForPage).toHaveBeenCalledWith(pageable, uri, identifier, true);
	});

	it('remove delegates to delegateForSingleInstance', function() {
		expect(restService.remove(payload)).toBe(voidPromise);
		expect(delegateRestServiceMock.delegateForVoid).toHaveBeenCalledWith("remove", payload, uri, identifier, false);
		restService.activateMetadata();
		expect(restService.remove(payload)).toBe(voidPromise);
		expect(delegateRestServiceMock.delegateForVoid).toHaveBeenCalledWith("remove", payload, uri, identifier, true);
	});

});
