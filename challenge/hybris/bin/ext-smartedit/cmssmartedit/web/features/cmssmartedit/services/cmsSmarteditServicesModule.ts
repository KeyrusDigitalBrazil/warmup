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
import {HiddenComponentMenuService} from 'cmssmartedit/services/HiddenComponentMenuService';
import {SlotContainerService} from 'cmssmartedit/services/slotContainerService';
import {AssetsService, CMSModesService, TypePermissionsRestService} from 'cmscommons';
import {PageContentSlotsComponentsRestService} from 'cmssmartedit/dao/PageContentSlotsComponentsRestServiceInner';
import {SynchronizationPollingServiceModule} from 'cmssmartedit/services/syncPollingService/SynchronizationPollingServiceModule';

/**
 * @ngdoc overview
 * @name cmsSmarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the CmsSmartEdit application.
 */
@SeModule({
	imports: [
		'yLoDashModule',
		SynchronizationPollingServiceModule,
		'smarteditServicesModule'
	],
	providers: [
		SlotContainerService,
		HiddenComponentMenuService,
		AssetsService,
		CMSModesService,
		PageContentSlotsComponentsRestService,
		TypePermissionsRestService
	]
})
export class CmsSmarteditServicesModule {}
