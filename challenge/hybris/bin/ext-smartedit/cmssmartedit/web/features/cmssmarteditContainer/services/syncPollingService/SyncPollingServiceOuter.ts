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
	CrossFrameEventService,
	GatewayProxied,
	ICatalogService,
	IExperienceService,
	IPageInfoService,
	IUriContext,
	SeInjectable,
	SystemEventService,
	TypedMap
} from "smarteditcommons";
import {ISyncStatus} from 'cmscommons/dtos/ISyncStatus';
import * as lo from 'lodash';

@GatewayProxied('getSyncStatus', '_fetchSyncStatus', 'changePollingSpeed', 'registerSyncPollingEvents', 'performSync')
@SeInjectable()
export class SyncPollingService {

	public SYNC_POLLING_THROTTLE: number = 500;

	private syncStatus: any = {};
	private triggers: any[] = [];
	private syncPollingTimer: any = null;
	private refreshInterval: any = null;



	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private isBlank: any,
		private pageInfoService: IPageInfoService,
		private experienceService: IExperienceService,
		private catalogService: ICatalogService,
		private synchronizationResource: any,
		private crossFrameEventService: CrossFrameEventService,
		private SYNCHRONIZATION_POLLING: TypedMap<any>,
		private systemEventService: SystemEventService,
		private OVERLAY_RERENDERED_EVENT: string,
		private lodash: lo.LoDashStatic,
		private EVENTS: TypedMap<string>,
		private SYNCHRONIZATION_EVENT: TypedMap<string>,
		private timerService: any
	) {}

	getSyncStatus(pageUUID?: string, uriContext?: IUriContext, forceGetSynchronization?: boolean): angular.IPromise<ISyncStatus> {
		forceGetSynchronization = forceGetSynchronization === true ? forceGetSynchronization : false;
		if (this.syncStatus[pageUUID] && pageUUID === this.syncStatus[pageUUID].itemId && !forceGetSynchronization) {
			return this.$q.when(this.syncStatus[pageUUID]);
		} else {
			return this.getPageUUID(pageUUID).then((_pageUUID: string) => {
				this.syncPollingTimer.restart(this.refreshInterval);
				return this._fetchSyncStatus(_pageUUID, uriContext).then((syncStatus: ISyncStatus) => {
					return syncStatus;
				}, () => {
					this.$log.error('syncPollingService::getSyncStatus - failed call to _fetchSyncStatus');
					return this.$q.reject();
				});
			}, (e: any) => {
				this.$log.error('syncPollingService::getSyncStatus - failed call to getPageUUID');
				this.syncPollingTimer.stop();
				return this._fetchSyncStatus(pageUUID, uriContext);
			});
		}
	}

	_fetchSyncStatus(_pageUUID?: string, uriContext?: IUriContext): angular.IPromise<ISyncStatus> {
		return this.getPageUUID(_pageUUID).then((pageUUID: string) => {
			if (pageUUID) {
				return this._isCurrentPageFromActiveCatalog().then((currentPageFromActiveCatalog) => {
					if (!currentPageFromActiveCatalog) {

						return this.catalogService.getContentCatalogActiveVersion(uriContext).then((activeVersion: string) => {
							return this.synchronizationResource.getPageSynchronizationGetRestService(uriContext).get({
								pageUid: pageUUID,
								target: activeVersion
							}).then((syncStatus: ISyncStatus) => {
								if (JSON.stringify(syncStatus) !== JSON.stringify(this.syncStatus[syncStatus.itemId])) {
									this.crossFrameEventService.publish(this.SYNCHRONIZATION_POLLING.FAST_FETCH, syncStatus);
								}
								this.syncStatus[syncStatus.itemId] = syncStatus;
								return syncStatus;
							});
						});

					} else {
						return this.$q.reject();
					}
				});
			} else {
				return this.$q.when({});
			}
		}, () => {
			this.syncPollingTimer.stop();
			return this.$q.reject();
		});
	}

	changePollingSpeed(eventId: string, itemId?: string): void {
		if (eventId === this.SYNCHRONIZATION_POLLING.SPEED_UP) {
			this.syncStatus = {};
			if (itemId && this.triggers.indexOf(itemId) === -1) {
				this.triggers.push(itemId);
			}

			this.refreshInterval = this.SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
		} else {
			if (itemId) {
				this.triggers.splice(this.triggers.indexOf(itemId), 1);
			}
			if (this.triggers.length === 0) {
				this.refreshInterval = this.SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
			}
		}

		this.syncPollingTimer.restart(this.refreshInterval);

	}

	initSyncPolling() {
		this.refreshInterval = this.SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
		this.triggers = [];
		this.syncStatus = {};

		const changePolling = this.changePollingSpeed.bind(this);

		this.systemEventService.subscribe(this.SYNCHRONIZATION_POLLING.SPEED_UP, changePolling);
		this.systemEventService.subscribe(this.SYNCHRONIZATION_POLLING.SLOW_DOWN, changePolling);

		this.crossFrameEventService.subscribe(this.SYNCHRONIZATION_POLLING.FETCH_SYNC_STATUS_ONCE, (eventId: string, pageUUID: string) => {
			this._fetchSyncStatus.bind(this)(pageUUID);
		});

		this.crossFrameEventService.subscribe(this.OVERLAY_RERENDERED_EVENT, this.lodash.throttle(() => {
			if (this.syncPollingTimer.isActive()) {
				this._fetchSyncStatus.bind(this)();
			}
		}, this.SYNC_POLLING_THROTTLE));

		this.crossFrameEventService.subscribe(this.EVENTS.PAGE_CHANGE, this.stopSync.bind(this));

		this.crossFrameEventService.subscribe(this.SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED, () => {
			this.syncStatus = {};
			this._fetchSyncStatus.bind(this)();
		});
		this.syncPollingTimer = this.timerService.createTimer(this._fetchSyncStatus.bind(this), this.refreshInterval);
	}

	performSync(array: any[], uriContext: IUriContext): angular.IPromise<any> {
		return this._isCurrentPageFromActiveCatalog().then((currentPageFromActiveCatalog) => {
			if (!currentPageFromActiveCatalog) {
				return this.catalogService.getContentCatalogActiveVersion(uriContext).then((activeVersion: string) => {
					return this.synchronizationResource.getPageSynchronizationPostRestService(uriContext).save({
						target: activeVersion,
						items: array
					});
				});
			} else {
				return this.$q.reject();
			}
		});
	}

	stopSync(): void {
		if (this.syncPollingTimer.isActive()) {
			this.syncPollingTimer.stop();
		}
	}

	startSync(pollingType?: any) {
		if (!this.syncPollingTimer.isActive()) {
			this.changePollingSpeed(pollingType || this.SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME);
		}
	}

	private getPageUUID(_pageUUID: string): angular.IPromise<string> {
		return !this.isBlank(_pageUUID) ? this.$q.when(_pageUUID) : this.pageInfoService.getPageUUID();
	}

	private _isCurrentPageFromActiveCatalog() {
		return this.experienceService.getCurrentExperience().then((currentExperience) => {
			return currentExperience.pageContext ? currentExperience.pageContext.active : currentExperience.catalogDescriptor.active;
		});
	}
}
