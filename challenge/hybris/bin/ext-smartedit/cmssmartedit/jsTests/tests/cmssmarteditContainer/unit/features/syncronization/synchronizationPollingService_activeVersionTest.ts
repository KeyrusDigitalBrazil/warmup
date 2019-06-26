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
	SystemEventService
} from 'smarteditcommons';
import {SyncPollingService} from 'cmssmarteditcontainer/services/syncPollingService/SyncPollingServiceOuter';
import {promiseHelper} from 'testhelpers';

describe("Synchronization polling service with content catalog active version - ", () => {

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
	let restService: jasmine.SpyObj<IRestService<any>>;
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

	beforeEach(() => {

		isBlank.and.callFake((value: string) => {
			return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
		});

		$q = promiseHelper.$q();

		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['info', 'error']);

		experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', ['getCurrentExperience']);
		experienceService.getCurrentExperience.and.returnValue($q.when({
			pageContext: {
				active: true
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

		synchronizationResource = jasmine.createSpyObj('synchronizationResource', ['getPageSynchronizationGetRestService']);
		restService = jasmine.createSpyObj<IRestService<any>>('restService', ['get']);
		synchronizationResource.getPageSynchronizationGetRestService.and.returnValue(restService);

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

	it('getSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', () => {

		// GIVEN
		(syncPollingService as any).syncStatus = pageId2SyncStatus;
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		restService.get.and.returnValue($q.when(pageId1SyncStatus));
		// WHEN
		const promise = syncPollingService.getSyncStatus('pageId1');

		// THEN
		expect(promise).toBeRejected();
		expect(restService.get).not.toHaveBeenCalled();
		expect((syncPollingService as any).syncStatus).toBe(pageId2SyncStatus);

	});

	it('fetchSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', () => {

		// GIVEN
		(syncPollingService as any).syncStatus = pageId2SyncStatus;
		pageInfoService.getPageUUID.and.returnValue($q.when('pageId1'));
		restService.get.and.returnValue($q.when(pageId1SyncStatus));

		// WHEN
		const promise = syncPollingService._fetchSyncStatus();

		// THEN
		expect(promise).toBeRejected();
		expect(restService.get).not.toHaveBeenCalled();
		expect((syncPollingService as any).syncStatus).toEqual(pageId2SyncStatus);

	});

	it('startSync call without pollingType should restart the timer with SLOW_POLLING_TIME by default', () => {
		timer.isActive.and.returnValue(false);

		syncPollingService.startSync();

		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME);
	});

	it('startSync call with SYNCHRONIZATION_POLLING.SPEED_UP should restart the timer with FAST_FETCH', () => {
		timer.isActive.and.returnValue(false);

		syncPollingService.startSync(SYNCHRONIZATION_POLLING.SPEED_UP);

		expect(timer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_POLLING.FAST_POLLING_TIME);
	});

	it('startSync call should not restart the timer if it\'s active', () => {
		timer.isActive.and.returnValue(true);

		syncPollingService.startSync();

		expect(timer.restart).not.toHaveBeenCalled();
	});

	it('stopSync should stop the timer when it\'s active', () => {
		timer.isActive.and.returnValue(true);

		syncPollingService.stopSync();

		expect(timer.stop).toHaveBeenCalled();
	});

	it('stopSync should not stop the timer when it\'s not active', () => {
		timer.isActive.and.returnValue(false);

		syncPollingService.stopSync();

		expect(timer.stop).not.toHaveBeenCalled();
	});

});
