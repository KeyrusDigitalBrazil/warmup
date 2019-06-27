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
import {ICMSPage} from 'cmscommons';

@SeComponent({
	templateUrl: 'missingPrimaryContentPageTemplate.html',
	inputs: ['model']
})
export class MissingPrimaryContentPageComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------
	public fetchStrategy = {
		fetchAll: this.fetchAll.bind(this)
	};

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	private CONTENT_PAGE_TYPE_CODE = "ContentPage";

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	constructor(
		private pageService: any,
		private $log: angular.ILogService) {}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	private fetchAll(): angular.IPromise<ICMSPage[]> {
		return this.pageService.getPrimaryPagesForPageType(this.CONTENT_PAGE_TYPE_CODE).then((rawPages: ICMSPage[]) => {
			return rawPages.map((pageInfo: ICMSPage) => {
				return {
					id: pageInfo.label,
					label: pageInfo.name
				};
			});
		}, (error: Error) => {
			this.$log.warn("[MissingPrimaryContentPageComponent] - Cannot retrieve list of alternative content primary pages. ", error);
		});
	}
}
