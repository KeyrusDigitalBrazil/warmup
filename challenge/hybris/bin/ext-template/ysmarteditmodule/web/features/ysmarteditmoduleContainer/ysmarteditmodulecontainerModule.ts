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
import {doImport} from './forcedImports';
doImport();
import {IFeatureService, SeModule} from 'smarteditcommons';
/**
 * @ngdoc overview
 * @name ysmarteditmoduleContainer
 * @description
 * Placeholder for documentation
 */
@SeModule({
	imports: [
		'smarteditServicesModule',
		'abAnalyticsToolbarItemModule'
	],
	initialize: (featureService: IFeatureService) => {
		'ngInject';
		////////////////////////////////////////////////////
		// Create Toolbar Item
		////////////////////////////////////////////////////
		// Create the toolbar item as a feature.
		featureService.addToolbarItem({
			toolbarId: 'smartEditPerspectiveToolbar',
			key: 'abAnalyticsToolbarItem',
			type: 'HYBRID_ACTION',
			nameI18nKey: 'ab.analytics.toolbar.item.name',
			priority: 2,
			section: 'left',
			iconClassName: 'hyicon hyicon-info se-toolbar-menu-ddlb--button__icon',
			include: 'abAnalyticsToolbarItemWrapperTemplate.html'
		});
	}
})
export class YSmarteditModuleContainer {}
