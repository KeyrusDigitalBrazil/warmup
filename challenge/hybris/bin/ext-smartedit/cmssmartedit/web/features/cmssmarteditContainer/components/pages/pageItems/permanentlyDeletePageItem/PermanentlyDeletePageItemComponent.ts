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
import {CrossFrameEventService, ISeComponent, IUriContext, MultiNamePermissionContext, SeComponent, TypedMap} from 'smarteditcommons';
import {ManagePageService} from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {PageSynchronizationService} from 'cmssmarteditcontainer/dao/PageSynchronizationService';
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';
import {ISyncStatus} from 'cmscommons/dtos/ISyncStatus';

@SeComponent({
	templateUrl: 'permanentlyDeletePageItemTemplate.html',
	inputs: ['pageInfo', 'uriContext']
})
export class PermanentlyDeletePageItemComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public pageInfo: ICMSPage;
	public uriContext: IUriContext;
	public permanentlyDeletePagePermission: MultiNamePermissionContext[];
	private unregFetchSyncStatus: () => void;
	private isDeletable: boolean = false;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private managePageService: ManagePageService,
		private crossFrameEventService: CrossFrameEventService,
		private SYNCHRONIZATION_POLLING: TypedMap<any>,
		private SYNCHRONIZATION_STATUSES: TypedMap<string>,
		private pageSynchronizationService: PageSynchronizationService
	) {}

	$onInit(): void {
		this.unregFetchSyncStatus = this.crossFrameEventService.subscribe(this.SYNCHRONIZATION_POLLING.FAST_FETCH, this.triggerSyncFetch.bind(this));
		this.triggerSyncFetch();

		this.permanentlyDeletePagePermission = [{
			names: ['se.permanently.delete.page.type'],
			context: {
				typeCode: this.pageInfo.typeCode
			}
		}];
	}

	$onDestroy(): void {
		this.unregFetchSyncStatus();
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	triggerSyncFetch(): void {
		this.pageSynchronizationService.getSyncStatus(this.pageInfo.uuid, this.uriContext).then((response: ISyncStatus) => {
			const pageHasSyncStatus = !!(response.lastSyncStatus);
			const pageIsSynced = response.status === this.SYNCHRONIZATION_STATUSES.IN_SYNC;

			this.isDeletable = !pageHasSyncStatus || pageIsSynced;
		}, () => {
			this.isDeletable = false;
		});
	}

	permanentlyDelete(): void {
		this.managePageService.hardDeletePage(this.pageInfo);
	}

	isDeleteButtonDisabled(): boolean {
		return !this.isDeletable;
	}
}