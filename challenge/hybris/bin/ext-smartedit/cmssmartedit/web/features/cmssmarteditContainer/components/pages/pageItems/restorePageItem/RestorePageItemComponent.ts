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
import {ISeComponent, MultiNamePermissionContext, SeComponent} from 'smarteditcommons';
import {ManagePageService} from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';

@SeComponent({
	templateUrl: 'restorePageItemTemplate.html',
	inputs: ['pageInfo']
})
export class RestorePageItemComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	public pageInfo: ICMSPage;
	public restorePagePermission: MultiNamePermissionContext[];

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	constructor(
		private managePageService: ManagePageService,
	) {}

	$onInit(): void {
		this.restorePagePermission = [{
			names: ['se.restore.page.type'],
			context: {
				typeCode: this.pageInfo.typeCode
			}
		}];
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	restorePage(): void {
		this.managePageService.restorePage(this.pageInfo);
	}
}