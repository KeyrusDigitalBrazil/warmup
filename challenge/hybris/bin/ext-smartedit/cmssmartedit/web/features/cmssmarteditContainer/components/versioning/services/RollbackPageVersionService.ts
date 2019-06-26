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
import * as angular from "angular";
import {IAlertService, IExperienceService, IPageInfoService, SeInjectable, SystemEventService} from 'smarteditcommons';
import {PageVersioningService} from 'cmssmarteditcontainer/services/pageVersioning/PageVersioningService';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';
import {IPageVersion} from '../../../services/pageVersioning/PageVersioningService';

/**
 * This service is used to rollback a page version from the toolbar context.
 */
@SeInjectable()
export class RollbackPageVersionService {

	constructor(
		private $log: angular.ILogService,
		private alertService: IAlertService,
		private confirmationModalService: any,
		private experienceService: IExperienceService,
		private pageInfoService: IPageInfoService,
		private pageVersioningService: PageVersioningService,
		private pageVersionSelectionService: PageVersionSelectionService,
		private systemEventService: SystemEventService,
		private EVENT_CONTENT_CATALOG_UPDATE: string) {}

	rollbackPageVersion(version?: IPageVersion): void {
		const pageVersion = version || this.pageVersionSelectionService.getSelectedPageVersion();
		if (!!pageVersion) {
			const TRANSLATE_NS: string = 'se.cms.actionitem.page.version.rollback.confirmation';
			this.pageInfoService.getPageUUID().then((pageUuid: string) => {
				this.showConfirmationModal(pageVersion.label, TRANSLATE_NS)
					.then(this.performRollback.bind(this, pageUuid, pageVersion));
			});
		}
	}

	// Warning! This method is patched in personalization module, be careful when modifying it.
	private showConfirmationModal(versionLabel: string, translateNs: string): angular.IPromise<void> {
		return this.confirmationModalService.confirm({
			title: `${translateNs}.title`,
			description: `${translateNs}.description`,
			descriptionPlaceholders: {
				versionLabel
			}
		});
	}

	private performRollback(pageUuid: string, pageVersion: IPageVersion): void {
		this.pageVersioningService.rollbackPageVersion(pageUuid, pageVersion.uid).then(() => {
			// invalidate the content catalog cache: a rollback of a page could replace the existing homepage.
			this.systemEventService.publishAsync(this.EVENT_CONTENT_CATALOG_UPDATE);
			this.alertService.showSuccess('se.cms.versions.rollback.alert.success');
			// reload experience
			this.experienceService.updateExperience({}).then(() => {
				this.pageVersionSelectionService.deselectPageVersion(false);
			});
		}, () => {
			this.$log.error('RollbackPageVersionService::performRollback - unable to perform page rollback');
		});
	}
}
