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
import {SeModule} from 'smarteditcommons';
import {CmsConstantsModule} from 'cmscommons/cmsConstantsModule';
import {SyncPollingService} from './SyncPollingServiceOuter';

@SeModule({
	imports: [
		'functionsModule',
		'resourceModule',
		CmsConstantsModule
	],
	providers: [
		SyncPollingService
	],
	initialize: (syncPollingService: SyncPollingService) => {
		'ngInject';
		syncPollingService.initSyncPolling();
	}
})

export class SynchronizationPollingServiceModule {}