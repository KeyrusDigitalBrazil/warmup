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
/**
 * @ngdoc object
 * @name cmsConstantsModule.object:SYNCHRONIZATION_STATUSES
 *
 * @description
 * Constant containing the different sync statuses
 * * UNAVAILABLE
 * * IN_SYNC
 * * NOT_SYNC
 * * IN_PROGRESS
 * * SYNC_FAILED
 * 
 */
export const DEFAULT_SYNCHRONIZATION_STATUSES = {
	UNAVAILABLE: "UNAVAILABLE",
	IN_SYNC: "IN_SYNC",
	NOT_SYNC: "NOT_SYNC",
	IN_PROGRESS: "IN_PROGRESS",
	SYNC_FAILED: "SYNC_FAILED"
};

/**
 * @ngdoc object
 * @name cmsConstantsModule.object:SYNCHRONIZATION_POLLING
 *
 * @description
 * Constant containing polling related values
 * * `SLOW_POLLING_TIME` : the slow polling time in milliseconds (60000ms)
 * * `FAST_POLLING_TIME` : the slow polling time in milliseconds (60000ms)
 * * `SPEED_UP` : event used to speed up polling (`syncPollingSpeedUp`)
 * * `SLOW_DOWN` : event used to slow down polling (`syncPollingSlowDown`)
 * * `FAST_FETCH` : event used to trigger a sync fetch (`syncFastFetch`)
 * * `FETCH_SYNC_STATUS_ONCE`: event used to trigger a one time sync (`fetchSyncStatusOnce`)
 *
 */
export const DEFAULT_SYNCHRONIZATION_POLLING = {
	SLOW_POLLING_TIME: 20000,
	FAST_POLLING_TIME: 2000,
	SPEED_UP: "syncPollingSpeedUp",
	SLOW_DOWN: "syncPollingSlowDown",
	FAST_FETCH: "syncFastFetch",
	FETCH_SYNC_STATUS_ONCE: "fetchSyncStatusOnce"
};

/**
 * @ngdoc object
 * @name cmsConstantsModule.object:SYNCHRONIZATION_EVENT
 * 
 * @description
 * Constant containing synchronization events
 * * CATALOG_SYNCHRONIZED
 */
export const DEFAULT_SYNCHRONIZATION_EVENT = {
	CATALOG_SYNCHRONIZED: "CATALOG_SYNCHRONIZED_EVENT"
};