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

import {ISeComponent, SeComponent} from 'smarteditcommons/services/dependencyInjection/di';
import {IframeManagerService} from 'smarteditcontainer/services';

@SeComponent({
	templateUrl: 'sitesLinkTemplate.html',
	inputs: [
		'cssClass:@?',
		'iconCssClass:@?'
	]
})
export class SitesLinkComponent implements ISeComponent {
	// inputs
	public cssClass: string;
	public iconCssClass: string;

	constructor(
		private $location: angular.ILocationService,
		private iframeManagerService: IframeManagerService,
		private LANDING_PAGE_PATH: string) {}

	goToSites() {
		this.iframeManagerService.setCurrentLocation(null);
		this.$location.url(this.LANDING_PAGE_PATH);
	}
}