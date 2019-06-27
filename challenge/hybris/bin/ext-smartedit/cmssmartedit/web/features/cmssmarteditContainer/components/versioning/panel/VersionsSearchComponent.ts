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

/**
 * This component renders controls that can allow the user to search for a specific page version or 
 * manage them. 
 */
@SeComponent({
	templateUrl: 'versionsSearchTemplate.html',
	inputs: ['onSearchTermChanged: &', 'versionsFound', 'showSearchControls']
})
export class VersionsSearchComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public showResetButton: boolean;
	public showSearchControls: boolean;
	public versionsFound: number;
	public onSearchTermChanged: (param: any) => void;

	private previousSearchTerm: string;
	private searchTerm: string;
	private VERSIONING_MODE_KEY: string = CMSModesService.VERSIONING_PERSPECTIVE_KEY;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private perspectiveService: any
	) {}

	$onInit(): void {
		this.previousSearchTerm = '';
		this.showResetButton = false;
		this.resetSearchBox();
	}

	$doCheck(): void {
		if (this.searchTerm !== this.previousSearchTerm) {
			this.previousSearchTerm = this.searchTerm;
			this.showResetButton = (this.searchTerm !== '');
			this.onSearchTermChanged({
				newSearchTerm: this.searchTerm
			});
		}
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	public resetSearchBox(): void {
		this.searchTerm = '';
	}

	public getVersionsFoundTranslationData(): any {
		return {
			versionsFound: this.versionsFound
		};
	}

	public showManageVersionsButton(): boolean {
		const activePerspective = this.perspectiveService.getActivePerspective();
		return activePerspective && activePerspective.key !== this.VERSIONING_MODE_KEY;
	}

	public switchToVersioningMode(): void {
		this.perspectiveService.switchTo(this.VERSIONING_MODE_KEY);
	}
}