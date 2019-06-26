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
import {MultiNamePermissionContext, SeComponent} from "smarteditcommons";
import {ICMSPage} from "cmscommons";
import {PageInfoMenuService} from "../../pageInfoMenu/services/PageInfoMenuService";

/**
 * @ngdoc directive
 * @name pageComponentsModule.directive:editPageItem
 * @scope
 * @restrict E
 *
 * @description
 * editPageItem builds an action item allowing for the edition of a given
 * CMS page .
 *
 * @param {< Object} pageInfo An object defining the context of the
 * CMS page associated to the editPage item.
 */
@SeComponent({
	templateUrl: 'editPageItemTemplate.html',
	inputs: ['pageInfo']
})
export class EditPageItemComponent {

	public pageInfo: ICMSPage;
	public editPagePermission: MultiNamePermissionContext[];

	constructor(
		private pageInfoMenuService: PageInfoMenuService,
	) {
	}

	$onInit(): void {
		this.editPagePermission = [{
			names: ['se.edit.page.type'],
			context: {
				typeCode: this.pageInfo.typeCode
			}
		}];
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	onClickOnEdit() {
		return this.pageInfoMenuService.openPageEditor(this.pageInfo);
	}

}