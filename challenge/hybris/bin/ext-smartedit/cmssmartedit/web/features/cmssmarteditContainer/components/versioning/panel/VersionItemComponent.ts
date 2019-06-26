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
import {ISeComponent, SeComponent} from 'smarteditcommons';
import {CMSModesService} from 'cmscommons';
import {IPageVersion} from 'cmssmarteditcontainer/services';
import {PageVersionSelectionService} from "../services/PageVersionSelectionService";

/**
 * This component displays a page version entry in the list of versions. 
 */
@SeComponent({
	templateUrl: 'versionItemTemplate.html',
	inputs: ['pageVersion']
})
export class VersionItemComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public textLimit: number;
	private pageVersion: IPageVersion;
	private VERSIONING_MODE_KEY: string = CMSModesService.VERSIONING_PERSPECTIVE_KEY;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private pageVersionSelectionService: PageVersionSelectionService,
		private perspectiveService: any,
		private cMSModesService: CMSModesService
	) {}

	$onInit(): void {
		this.textLimit = 40;
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	public selectVersion(): void {
		this.cMSModesService.isVersioningPerspectiveActive().then((isVersioningModeActive: boolean) => {
			if (!isVersioningModeActive) {
				this.perspectiveService.switchTo(this.VERSIONING_MODE_KEY);
			}
			this.pageVersionSelectionService.selectPageVersion(this.pageVersion);
		});
	}

	public isSelectedVersion(): boolean {
		const selectedVersion = this.pageVersionSelectionService.getSelectedPageVersion();
		return selectedVersion && selectedVersion.uid === this.pageVersion.uid;
	}

	public isVersionMenuEnabled(): boolean {
		const activePerspective = this.perspectiveService.getActivePerspective();
		return activePerspective && activePerspective.key === this.VERSIONING_MODE_KEY;
	}
}