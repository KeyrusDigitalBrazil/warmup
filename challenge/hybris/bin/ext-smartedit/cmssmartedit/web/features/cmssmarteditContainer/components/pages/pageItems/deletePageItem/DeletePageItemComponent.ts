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
import {ICatalogService, ISeComponent, IUriContext, MultiNamePermissionContext, SeComponent, SystemEventService} from "smarteditcommons";
import {ICMSPage} from "cmscommons";
import {ManagePageService} from "cmssmarteditcontainer/services/pages/ManagePageService";

/**
 * @ngdoc directive
 * @name pageComponentsModule.directive:deletePageItem
 * @scope
 * @restrict E
 *
 * @description
 * deletePageItem builds a dropdown item allowing for the soft
 * deletion of a given CMS page .
 *
 * @param {< Object} pageInfo An object defining the context of the
 * CMS page associated to the deletePage item.
 */
@SeComponent({
	templateUrl: 'deletePageItemTemplate.html',
	inputs: ['pageInfo']
})
export class DeletePageItemComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public pageInfo: ICMSPage;
	public isDeletePageEnabled = false;
	public tooltipMessage = 'se.cms.tooltip.movetotrash';
	public deletePagePermission: MultiNamePermissionContext[];

	constructor(
		private managePageService: ManagePageService,
		private systemEventService: SystemEventService,
		private catalogService: ICatalogService,
		private EVENT_CONTENT_CATALOG_UPDATE: string,
	) {}

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	$onInit() {
		this.catalogService.retrieveUriContext().then((uriContext: IUriContext) => {
			this.managePageService.isPageTrashable(this.pageInfo, uriContext).then((isEnabled: boolean) => {
				this.isDeletePageEnabled = isEnabled;

				if (this.isDeletePageEnabled) {
					this.tooltipMessage = null;
				} else {
					this.managePageService.getDisabledTrashTooltipMessage(this.pageInfo, uriContext).then((tooltipMessage: string) => {
						this.tooltipMessage = tooltipMessage;
					});
				}
			});
		});

		this.deletePagePermission = [{
			names: ['se.delete.page.type'],
			context: {
				typeCode: this.pageInfo.typeCode
			}
		}];
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	onClickOnDeletePage() {
		this.catalogService.retrieveUriContext().then((uriContext: IUriContext) => {
			return this.managePageService.softDeletePage(this.pageInfo, uriContext).then((response: any) => {
				this.systemEventService.publishAsync(this.EVENT_CONTENT_CATALOG_UPDATE, response);
			});
		});
	}

	getTooltipTemplate() {
		return '<div class="popover-tooltip"><span data-translate="' + this.tooltipMessage + '" /></div>';
	}

}