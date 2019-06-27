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
import {ICatalogService, IUriContext, SeComponent, SystemEventService} from "smarteditcommons";
import {ICMSPage} from "cmscommons/dtos/ICMSPage";

/**
 * @ngdoc directive
 * @name pageComponentsModule.directive:syncPageItem
 * @scope
 * @restrict E
 *
 * @description
 * syncPageItem builds a drop-down item allowing for the
 * edition of a given CMS page .
 *
 * @param {<Object} pageInfo An object defining the context of the
 * CMS page associated to the editPage item.
 */
@SeComponent({
	templateUrl: 'syncPageItemTemplate.html',
	inputs: ['pageInfo']
})
export class SyncPageItemComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public pageInfo: ICMSPage;

	constructor(
		private syncPageModalService: any,
		private catalogService: ICatalogService,
		private EVENT_CONTENT_CATALOG_UPDATE: string,
		private systemEventService: SystemEventService,
	) {}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	onClickOnSync() {
		this.catalogService.retrieveUriContext().then((uriContext: IUriContext) => {
			this.syncPageModalService.open(this.pageInfo, uriContext).then((response: any) => {
				this.systemEventService.publishAsync(this.EVENT_CONTENT_CATALOG_UPDATE, response);
			});
		});
	}

}