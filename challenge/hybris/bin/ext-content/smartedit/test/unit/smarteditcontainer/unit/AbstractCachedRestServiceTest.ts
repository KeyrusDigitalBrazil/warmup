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
import {
	Page,
	Payload
} from 'smarteditcommons/dtos';
import {
	AbstractCachedRestService,
	CacheAction,
	CacheConfig,
	EvictionTag,
	IRestService,
	IRestServiceFactory,
	SeInjectable
} from 'smarteditcommons/services';
import {
	coreAnnotationsHelper,
	promiseHelper,
	IExtensiblePromise,
	PromiseType
} from 'testhelpers';

describe("AbstractCachedRestServiceTest ", () => {

	const cacheAction = new CacheAction('ANY_NAME');

	const evictionTag = {
		event: 'event0'
	} as EvictionTag;

	interface DTO {
		someKey: string;
	}

	@CacheConfig({actions: [cacheAction], tags: [evictionTag]})
	@SeInjectable()
	class SomeRestClass extends AbstractCachedRestService<DTO> {

		constructor(rsf: IRestServiceFactory) {
			super(rsf, '/someURI', 'identifier');
		}
	}

	let service: SomeRestClass;
	let innerRestService: jasmine.SpyObj<IRestService<DTO>>;
	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;

	beforeEach(() => {

		coreAnnotationsHelper.init();

		innerRestService = jasmine.createSpyObj<IRestService<DTO>>("innerRestService", ["activateMetadata", "getById", "get", "query", "page", "update", "save", "remove"]);
		restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>("restServiceFactory", ["get"]);
		restServiceFactory.get.and.callFake((uri: string, identifier: string) => {
			if (uri === '/someURI' && identifier === 'identifier') {
				return innerRestService;
			} else {
				throw new Error("unexpected arguments");
			}
		});
		service = new SomeRestClass(restServiceFactory);
	});

	it("the underlying restService has response metadata retrieval activated", () => {

		expect(innerRestService.activateMetadata).toHaveBeenCalled();
	});

	it("getById delegates to underlying restService#getById", () => {

		const response: DTO = {someKey: "someValue"};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.getById.and.returnValue(promise);

		expect((service.getById("someId") as IExtensiblePromise<DTO>).value).toBe(response);

		expect(innerRestService.getById).toHaveBeenCalledWith("someId");

	});

	it("get delegates to underlying restService#get", () => {

		const searchParam: Payload = {searchKey: "searchValue"};
		const response: DTO = {someKey: "someValue"};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.get.and.returnValue(promise);

		expect((service.get(searchParam) as IExtensiblePromise<DTO>).value).toBe(response);

		expect(innerRestService.get).toHaveBeenCalledWith(searchParam);

	});

	it("query delegates to underlying restService#query", () => {

		const searchParam: Payload = {searchKey: "searchValue"};
		const response: DTO[] = [{someKey: "someValue"}];

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.query.and.returnValue(promise);

		expect((service.query(searchParam) as IExtensiblePromise<DTO[]>).value).toBe(response);

		expect(innerRestService.query).toHaveBeenCalledWith(searchParam);

	});

	it("page delegates to underlying restService#page", () => {

		const pageable = {currentPage: 5};

		const response: Page<DTO> = {pagination: {totalCount: 10}, results: [{someKey: "someValue"}]};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.page.and.returnValue(promise);

		expect((service.page(pageable) as IExtensiblePromise<Page<DTO>>).value).toBe(response);

		expect(innerRestService.page).toHaveBeenCalledWith(pageable);

	});

	it("update delegates to underlying restService#update", () => {

		const payload: Payload = {someKey: "someValue"};
		const response: DTO = {someKey: "someValue"};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.update.and.returnValue(promise);

		expect((service.update(payload) as IExtensiblePromise<DTO>).value).toBe(response);

		expect(innerRestService.update).toHaveBeenCalledWith(payload);

	});

	it("save delegates to underlying restService#save", () => {

		const payload: Payload = {someKey: "someValue"};
		const response: DTO = {someKey: "someValue"};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES, response);
		innerRestService.save.and.returnValue(promise);

		expect((service.save(payload) as IExtensiblePromise<DTO>).value).toBe(response);

		expect(innerRestService.save).toHaveBeenCalledWith(payload);

	});

	it("remove delegates to underlying restService#remove", () => {

		const payload: Payload = {someKey: "someValue"};

		const promise = promiseHelper.buildPromise("promise", PromiseType.RESOLVES);
		innerRestService.remove.and.returnValue(promise);

		expect((service.remove(payload) as IExtensiblePromise<void>).value).toBeUndefined();

		expect(innerRestService.remove).toHaveBeenCalledWith(payload);

	});

});