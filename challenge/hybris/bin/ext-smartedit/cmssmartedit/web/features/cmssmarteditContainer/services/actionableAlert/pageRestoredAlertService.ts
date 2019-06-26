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
import {ICatalogService, SeInjectable} from 'smarteditcommons';
import {ICMSPage} from 'cmscommons';

@SeInjectable()
export class PageRestoredAlertService {
	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------

	// ---------------------------------------------------------------------------
	// Constructor
	// ---------------------------------------------------------------------------
	constructor(
		private catalogService: ICatalogService,
		private actionableAlertService: any,
		private actionableAlertConstants: any
	) {}

	// ---------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------
	public displayPageRestoredSuccessAlert(pageInfo: ICMSPage): angular.IPromise<void> {
		if (!pageInfo) {
			throw new Error("[pageRestoredAlertService] - page info not provided.");
		}

		return this.catalogService.getCatalogVersionByUuid(pageInfo.catalogVersion).then((catalogVersion: any) => {
			const alertConfig = {
				controller: ['experienceService', function(experienceService: any) {
					this.description = 'se.cms.page.restored.alert.info.description';
					this.hyperlinkLabel = 'se.cms.page.restored.alert.info.hyperlink';
					this.hyperlinkDetails = {
						pageName: pageInfo.name
					};

					this.onClick = () => {
						experienceService.loadExperience({
							siteId: catalogVersion.siteId,
							catalogId: catalogVersion.catalogId,
							catalogVersion: catalogVersion.version,
							pageId: pageInfo.uid
						});
					};
				}]
			};

			return this.actionableAlertService.displayActionableAlert(alertConfig, this.actionableAlertConstants.ALERT_TYPES.SUCCESS);
		});

	}
}