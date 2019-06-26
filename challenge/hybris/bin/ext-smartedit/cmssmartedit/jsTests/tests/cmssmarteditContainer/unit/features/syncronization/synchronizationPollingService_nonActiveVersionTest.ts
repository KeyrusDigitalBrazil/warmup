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
import {
	CrossFrameEventService,
	ICatalogService,
	IExperienceService,
	IPageInfoService,
	IRestService,
	IUriContext,
	SystemEventService,
} from 'smarteditcommons';
import {SyncPollingService} from 'cmssmarteditcontainer/services/syncPollingService/SyncPollingServiceOuter';
import {promiseHelper} from 'testhelpers';

describe("Synchronization polling service with content catalog non active version - ", () => {

	let syncPollingService: SyncPollingService;
	let $q: jasmine.SpyObj<angular.IQService>;
	let experienceService: jasmine.SpyObj<IExperienceService>;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let pageInfoService: jasmine.SpyObj<IPageInfoService>;
	let catalogService: jasmine.SpyObj<ICatalogService>;
	let timerService: jasmine.SpyObj<any>;
	let timer: jasmine.SpyObj<any>;
	let synchronizationResource: jasmine.SpyObj<any>;
	let pageSynchronizationGetRestService: jasmine.SpyObj<IRestService<any>>;
	let pageSynchronizationPostRestService: jasmine.SpyObj<IRestService<any>>;
	const isBlank: jasmine.Spy = jasmine.createSpy('isBlank');
	let $log: jasmine.SpyObj<angular.ILogService>;
	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;

	const SYNCHRONIZATION_POLLING = {
		SLOW_POLLING_TIME: 20000,
		FAST_POLLING_TIME: 2000,
		SPEED_UP: "syncPollingSpeedUp",
		SLOW_DOWN: "syncPollingSlowDown",
		FAST_FETCH: "syncFastFetch",
		FETCH_SYNC_STATUS_ONCE: "fetchSyncStatusOnce"
	};

	const EVENTS = {
		EXPERIENCE_UPDATE: 'EXPERIENCE_UPDATE',
		PAGE_CHANGE: 'PAGE_CHANGE'
	};

	const SYNCHRONIZATION_EVENT = {
		CATALOG_SYNCHRONIZED: "CATALOG_SYNCHRONIZED_EVENT"
	};

	const OVERLAY_RERENDERED_EVENT = "mockedOverlayRerenderedEvent";

	const SynchronizationMockData = (window as any).test.unit.mockData.synchronization;
	const pageId1SyncStatus = new SynchronizationMockData().PAGE_ID1_SYNC_STATUS;
	const pageId2SyncStatus = new SynchronizationMockData().PAGE_ID2_SYNC_STATUS;

	const SYNCHRONIZATION_SLOW_POLLING_TIME = 20000;
	const SYNCHRONIZATION_FAST_POLLING_TIME = 2000;
	const SYNC_POLLING_SPEED_UP = 'syncPollingSpeedUp';
	const SYNC_POLLING_SLOW_DOWN = 'syncPollingSlowDown';

	beforeEach(() => {

		isBlank.and.callFake((value: string) => {
			return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
		});

		$q = promiseHelper.$q();

		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['info', 'error']);

		experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', ['getCurrentExperience']);
		experienceService.getCurrentExperience.and.returnValue($q.when({
			pageContext: {
				active: false
			}
		}));
		crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['publish', 'subscribe']);
		systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', ['publish', 'subscribe']);

		pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUUID']);

		catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', ['getContentCatalogActiveVersion']);
		catalogService.getContentCatalogActiveVersion.and.returnValue($q.when('Online'));

		timerService = jasmine.createSpyObj('timerService', ['createTimer']);
		timer = jasmine.createSpyObj('Timer', ['start', 'restart', 'stop', 'isActive']);

		timerService.createTimer.and.returnValue(timer);

		synchronizationResource = jasmine.createSpyObj('synchronizationResource', ['getPageSynchronizationGetRestService', 'getPageSynchronizationPostRestService']);
		pageSynchronizationGetRestService = jasmine.createSpyObj<IRestService<any>>('pageSynchronizationGetRestService', ['get']);
		pageSynchronizationPostRestService = jasmine.createSpyObj<IRestService<any>>('pageSynchronizationPostRestService', ['save']);
		synchronizationResource.getPageSynchronizationGetRestService.and.returnValue(pageSynchronizationGetRestService);
		synchronizationResource.getPageSynchronizationPostRestService.and.returnValue(pageSynchronizationPostRestService);

		syncPollingService = new SyncPollingService(
			$q,
			$log,
			isBlank,
			pageInfoService,
			experienceService,
			catalogService,
			synchronizationResource,
			crossFrameEventService,
			SYNCHRONIZATION_POLLING,
			systemEventService,
			OVERLAY_RERENDERED_EVENT,
			lodash,
			EVENTS,
			SYNCHRONIZATION_EVENT,
			timerService
		);

		syncPollingService.initSyncPolling();
	});

	it('initSyncPolling will be called on service initialization and will set default values, register event handlers and start timer', () => {

		// GIVEN
		systemEventService.subscribe.and.returnValue({});

		// THEN
		expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
		expect((syncPollingService as any).triggers).toEqual([]);
		expect((syncPollingService as any).syncStatus).toEqual({});

		expect(systemEventService.subscribe.calls.count()).toEqual(2);
		expect(systemEventService.subscribe.calls.argsFor(0)[0]).toEqual(SYNC_POLLING_SPEED_UP);
		expect(systemEventService.subscribe.calls.argsFor(1)[0]).toEqual(SYNC_POLLING_SLOW_DOWN);

	});

	it('when syncStatus in the scope is empty then getSyncStatus will fetch the sync status by making a rest call and set it to the scope object ', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		// WHEN
		const promise = syncPollingService.getSyncStatus('pageId1');

		// THEN
		expect(promise).toBeResolvedWithData(pageId1SyncStatus);
		expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);

	});

	it('when syncStatus object is not empty syncStatus but has an unmatched name, then getSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));
		(syncPollingService as any).syncStatus.pageId2 = pageId2SyncStatus;

		// WHEN
		const promise = syncPollingService.getSyncStatus('pageId1');

		// THEN
		expect(promise).toBeResolvedWithData(pageId1SyncStatus);

		expect(pageSynchronizationGetRestService.get.calls.count()).toBe(1);
		expect(pageSynchronizationGetRestService.get).toHaveBeenCalledWith({
			target: 'Online',
			pageUid: 'pageId1'
		});
		expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);
	});

	it('when syncStatus object is not empty syncStatus and matches the name then getSyncStatus with directly return the promise of the syncStatus object', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));
		(syncPollingService as any).syncStatus.pageId1 = pageId1SyncStatus;

		// WHEN
		const promise = syncPollingService.getSyncStatus('pageId1');

		// THEN
		expect(promise).toBeResolvedWithData(pageId1SyncStatus);
		expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();

	});

	it('fetchSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		// WHEN
		const promise = syncPollingService._fetchSyncStatus();

		// THEN
		expect(promise).toBeResolvedWithData(pageId1SyncStatus);
		expect(pageSynchronizationGetRestService.get).toHaveBeenCalled();
		expect((syncPollingService as any).syncStatus.pageId1).toEqual(pageId1SyncStatus);

	});

	it('when no page id is available then fetchSyncStatus will return an empty object', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when(null));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		// WHEN
		const promise = syncPollingService._fetchSyncStatus();

		// THEN
		expect(promise).toBeResolvedWithData({});
		expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();
		expect((syncPollingService as any).syncStatus).toEqual({});

	});

	it('when changePollingSpeed is called with syncPollingSpeedUp then the item is added to the triggers array and refreshInterval is set to speed up interval', () => {

		// GIVEN
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		// WHEN
		syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');

		// THEN
		expect((syncPollingService as any).triggers).toEqual(['slot1']);
		expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

		// WHEN
		syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');

		// THEN
		expect((syncPollingService as any).triggers).toEqual(['slot1', 'slot2']);
		expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

	});

	it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is set to slow down interval if the array is empty', () => {

		// GIVEN
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
		expect((syncPollingService as any).triggers).toEqual(['slot1']);

		// WHEN
		syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

		// THEN
		expect((syncPollingService as any).triggers).toEqual([]);
		expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_SLOW_POLLING_TIME);

	});

	it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is unaltered if the array is not empty', () => {

		// GIVEN
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));

		syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
		syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');
		expect((syncPollingService as any).triggers).toEqual(['slot1', 'slot2']);

		// WHEN
		syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

		// THEN
		expect((syncPollingService as any).triggers).toEqual(['slot2']);
		expect((syncPollingService as any).refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

	});


	it('will listen to OVERLAY_RERENDERED_EVENT events and proceed to one fetch', () => {
		const status = {
			a: 'b'
		};
		spyOn(syncPollingService, '_fetchSyncStatus').and.returnValue($q.when(status));

		expect(crossFrameEventService.subscribe).toHaveBeenCalledWith('mockedOverlayRerenderedEvent', jasmine.any(Function));

		const callback = crossFrameEventService.subscribe.calls.argsFor(0)[1];

		callback();
		expect(syncPollingService._fetchSyncStatus).toHaveBeenCalled();

	});


	it('performSync will use activeVersion in REST call', () => {

		// GIVEN
		const uriContext = {} as IUriContext;
		const array = [{
			a: 'b'
		}];
		catalogService.getContentCatalogActiveVersion.and.returnValue($q.when('mockedOnline'));

		// WHEN
		const promise = syncPollingService.performSync(array, uriContext);

		// THEN
		expect(promise).toBeResolved();

		expect(pageSynchronizationPostRestService.save).toHaveBeenCalledWith({
			target: 'mockedOnline',
			items: array
		});
		expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith(uriContext);
	});

	it('will listen to EVENTS.PAGE_CHANGE events and stops sync polling', () => {

		// GIVEN
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		pageSynchronizationGetRestService.get.and.returnValue($q.when(pageId1SyncStatus));
		(syncPollingService as any).syncStatus.pageId2 = pageId2SyncStatus;

		timer.isActive.and.returnValue(true);

		// WHEN
		syncPollingService.getSyncStatus('pageId1');

		expect(crossFrameEventService.subscribe).toHaveBeenCalledWith('PAGE_CHANGE', jasmine.any(Function));

		const callback = crossFrameEventService.subscribe.calls.argsFor(2)[1];

		callback();
		expect(timer.isActive).toHaveBeenCalled();
		expect(timer.stop).toHaveBeenCalled();

	});

	it('will listen to SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED events and clear the syncStatus cache.', () => {
		// GIVEN
		const callback = crossFrameEventService.subscribe.calls.argsFor(3)[1];
		spyOn(syncPollingService, '_fetchSyncStatus');

		// WHEN
		callback();

		// THEN
		expect((syncPollingService as any).syncStatus).toEqual({});
		expect(syncPollingService._fetchSyncStatus).toHaveBeenCalled();
	});

});
