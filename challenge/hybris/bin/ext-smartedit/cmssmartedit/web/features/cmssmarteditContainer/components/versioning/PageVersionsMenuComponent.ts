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
import {IPageInfoService, ISeComponent, SeComponent} from 'smarteditcommons';
import {VersionsPanelComponent} from './panel/VersionsPanelComponent';
import {PageVersionSelectionService} from "./services/PageVersionSelectionService";

@SeComponent({
	entryComponents: [VersionsPanelComponent],
	templateUrl: 'pageVersionsMenuTemplate.html',
	inputs: ['actionItem']
})
export class PageVersionsMenuComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public pageUuid: string;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private pageInfoService: IPageInfoService,
		private pageVersionSelectionService: PageVersionSelectionService,
	) {}

	$onInit(): void {
		this.pageVersionSelectionService.showToolbarContextIfNeeded();
		this.pageInfoService.getPageUUID().then((pageUUID: string) => {
			this.pageUuid = pageUUID;
		});
	}
}