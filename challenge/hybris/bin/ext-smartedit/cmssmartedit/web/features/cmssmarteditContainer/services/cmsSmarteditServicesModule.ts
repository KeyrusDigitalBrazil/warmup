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
import {diNameUtils, SeModule} from 'smarteditcommons';
import {AssetsService, CMSModesService, TypePermissionsRestService} from 'cmscommons';
import {PageContentSlotsComponentsRestService} from 'cmssmarteditcontainer/dao/PageContentSlotsComponentsRestServiceOuter';
import {PageSynchronizationService} from 'cmssmarteditcontainer/dao/PageSynchronizationService';
import {PageVersioningService} from 'cmssmarteditcontainer/services/pageVersioning/PageVersioningService';
import {TrashedPageService} from 'cmssmarteditcontainer/services/pages/TrashedPageService';
import {ManagePageService} from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {DeletePageService} from 'cmssmarteditcontainer/services/deletePage/DeletePageService';
import {PageServicesModule} from './pages';
import {PageRestoredAlertService} from './actionableAlert';
import {SynchronizationPollingServiceModule} from 'cmssmarteditcontainer/services/syncPollingService/SynchronizationPollingServiceModule';

import {
	DEFAULT_CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO,
	DEFAULT_CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO,
	HomepageService
} from 'cmssmarteditcontainer/services/pageDisplayConditions/HomepageService';

/**
 * @ngdoc overview
 * @name cmsSmarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the CmsSmartEdit application.
 */
@SeModule({
	imports: [
		SynchronizationPollingServiceModule,
		'smarteditServicesModule',
		PageServicesModule
	],
	providers: [
		AssetsService,
		CMSModesService,
		DeletePageService,
		HomepageService,
		TrashedPageService,
		TypePermissionsRestService,
		ManagePageService,
		PageVersioningService,
		PageContentSlotsComponentsRestService,
		PageSynchronizationService,
		PageRestoredAlertService,
		diNameUtils.makeValueProvider({DEFAULT_CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO}),
		diNameUtils.makeValueProvider({DEFAULT_CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO})
	]
})
export class CmsSmarteditServicesModule {}
