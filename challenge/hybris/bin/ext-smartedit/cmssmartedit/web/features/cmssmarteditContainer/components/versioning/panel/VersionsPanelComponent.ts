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

import {ISeComponent, Page, SeComponent} from 'smarteditcommons';
import {IPageVersion, PageVersioningService, PageVersionSearchPayload} from 'cmssmarteditcontainer/services';
import {VersionsSearchComponent} from './VersionsSearchComponent';

/**
 * This component renders a panel where versions are displayed and selected. The users can also 
 * query for specific versions. 
 */
@SeComponent({
	entryComponents: [VersionsSearchComponent],
	templateUrl: 'versionsPanelTemplate.html',
	inputs: ['pageUuid']
})
export class VersionsPanelComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public searchTerm: string;
	public pageSize: number;
	public versionsFound: number;

	private totalPageVersions: number;
	private isLoading: boolean;
	private pageUuid: string;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private pageVersioningService: PageVersioningService,
		private $log: angular.ILogService) {}

	$onInit(): void {
		this.pageSize = 10;
		this.totalPageVersions = 0;
		this.isLoading = true;

		// Infinite scrolling
		this.searchTerm = '';
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	public onSearchTermChanged(newSearchTerm: string): void {
		this.searchTerm = newSearchTerm;
	}

	public loadVersions = (mask: string, pageSize: number, currentPage: number): angular.IPromise<Page<IPageVersion>> => {
		const payload: PageVersionSearchPayload = {
			pageUuid: this.pageUuid,
			currentPage,
			mask,
			pageSize
		};

		return this.pageVersioningService.findPageVersions(payload).then((page: Page<IPageVersion>) => {
			if (this.isLoading) {
				this.totalPageVersions = page.pagination.totalCount;
				this.isLoading = false;
			}
			this.versionsFound = page.pagination.totalCount;

			return page;
		}, (error: string): Page<IPageVersion> => {
			this.$log.error(`Cannot find page versions for page ${this.pageUuid}.`);
			return null;
		});
	}

	public pageHasVersions(): boolean {
		return this.totalPageVersions > 0;
	}

	public pageHasVersionsOrIsLoading(): boolean {
		return this.pageUuid && (this.pageHasVersions() || this.isLoading);
	}
}