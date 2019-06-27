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
import * as lo from 'lodash';
import {annotationService, GatewayProxied, IPageInfoService, IRestService, IRestServiceFactory, TypedMap} from 'smarteditcommons';
import {PageContentSlotsComponentsRestService} from 'cmssmartedit/dao/PageContentSlotsComponentsRestServiceInner';
import {ICMSComponent} from 'cmscommons/services/ICMSComponent';
import {promiseHelper, IExtensiblePromise} from 'testhelpers';

describe('pageContentSlotsComponentsRestService', () => {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	const PAGE_UID = 'homepage';
	const PAGE_CONTEXT_SITE_ID = 'some site ID';
	const PAGE_CONTEXT_CATALOG = 'some catalog';
	const PAGE_CONTEXT_CATALOG_VERSION = 'some catalog version';
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	const $q = promiseHelper.$q();

	const COMPONENT1 = {
		name: "component1",
		typeCode: "component1",
		itemtype: "component1",
		uid: "component1",
		uuid: "component1",
		visible: true,
		cloneable: true
	};

	const COMPONENT2 = {
		name: "component2",
		typeCode: "component2",
		itemtype: "component2",
		uid: "component2",
		uuid: "component2",
		visible: true,
		cloneable: true
	};

	const COMPONENT3 = {
		name: "component3",
		typeCode: "component3",
		itemtype: "component3",
		uid: "component3",
		uuid: "component3",
		visible: true,
		cloneable: true
	};

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	let pageContentSlotsComponentsRestService: PageContentSlotsComponentsRestService;
	let sampleContentSlotsComponentsInfo: any[];
	let sampleComponentsInfo: any[];

	const restServiceFactory: jasmine.SpyObj<IRestServiceFactory> = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', ['get']);
	const pagesContentSlotsRestService: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>('pagesContentSlotsRestService', ['get']);
	const cmsitemsRestService: any = jasmine.createSpyObj('cmsitemsRestService', ['getByIds']);

	const pageInfoService: jasmine.SpyObj<IPageInfoService> = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

	beforeEach(() => {
		sampleContentSlotsComponentsInfo = [{
			pageId: PAGE_UID,
			slotId: "topHeaderSlot",
			componentId: "component1",
			componentUuid: "component1",
			position: 0
		}, {
			pageId: PAGE_UID,
			slotId: "topHeaderSlot",
			componentId: "component2",
			componentUuid: "component2",
			position: 1
		}, {
			pageId: PAGE_UID,
			slotId: "bottomHeaderSlot",
			componentId: "component3",
			componentUuid: "component3",
			position: 2
		}];

		sampleComponentsInfo = [COMPONENT1, COMPONENT2, COMPONENT3];

		pagesContentSlotsRestService.get.and.returnValue($q.when({
			pageContentSlotComponentList: sampleContentSlotsComponentsInfo
		}));

		cmsitemsRestService.getByIds.and.returnValue($q.when({
			response: sampleComponentsInfo
		}));

		restServiceFactory.get.and.returnValue(pagesContentSlotsRestService);

		pageInfoService.getPageUID.and.returnValue($q.when(PAGE_UID));

		pagesContentSlotsRestService.get.calls.reset();
		cmsitemsRestService.getByIds.calls.reset();

		// call service
		pageContentSlotsComponentsRestService = new PageContentSlotsComponentsRestService(restServiceFactory, pageInfoService, cmsitemsRestService, lodash,
			PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION);
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(PageContentSlotsComponentsRestService, GatewayProxied);
		expect(decoratorObj).toEqual(['clearCache', 'getSlotsToComponentsMapForPageUid']);
	});

	it(`When getSlotsToComponentsMapForPageUid is called
        THEN it fetcehes the pagecontentslotscomponents and converts them into solt- components[] map`, () => {

			// WHEN
			const promise = pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(PAGE_UID) as IExtensiblePromise<TypedMap<ICMSComponent[]>>;

			// THEN
			expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
				pageId: PAGE_UID
			});

			expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith([COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid]);

			expect(promise.value).toEqual({
				topHeaderSlot: [COMPONENT1, COMPONENT2],
				bottomHeaderSlot: [COMPONENT3]
			});
		});

	it(`When getComponentsForSlot is called
        THEN it fetcehes the components[] for the slot`, () => {

			// WHEN
			const promise = pageContentSlotsComponentsRestService.getComponentsForSlot("topHeaderSlot") as IExtensiblePromise<ICMSComponent[]>;

			// THEN
			expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
				pageId: PAGE_UID
			});

			expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith([COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid]);
			expect(pageInfoService.getPageUID).toHaveBeenCalled();

			expect(promise.value).toEqual([COMPONENT1, COMPONENT2]);
		});

	it(`When getComponentsForSlot is called
        THEN it returns empty array if not slot info is found`, () => {

			// WHEN
			const promise = pageContentSlotsComponentsRestService.getComponentsForSlot("someUnknownSlot") as IExtensiblePromise<ICMSComponent[]>;

			// THEN
			expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
				pageId: PAGE_UID
			});

			expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith([COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid]);
			expect(pageInfoService.getPageUID).toHaveBeenCalled();

			expect(promise.value).toEqual([]);
		});

});