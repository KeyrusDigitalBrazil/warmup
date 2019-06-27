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
import {ICatalogService, IOnChangesObject, ISeComponent, IUriContext, SeComponent} from 'smarteditcommons';
import {HomepageService, HomepageType} from 'cmssmarteditcontainer/services';
import {ICMSPage} from "cmscommons";

/**
 * @ngdoc directive
 * @name pageComponentsModule.directive:homepageIconComponent
 * @scope
 * @restrict E
 * @element homepage-icon
 *
 * @description
 * Component responsible for displaying a homepage icon with the passed cms page and uri context inputs.
 */
@SeComponent({
	templateUrl: 'HomepageIconTemplate.html',
	inputs: ['cmsPage', 'uriContext']
})
export class HomepageIconComponent implements ISeComponent {

	public type: HomepageType = null;

	private cmsPage: ICMSPage;
	private uriContext: IUriContext;

	constructor(
		public homepageService: HomepageService,
		public catalogService: ICatalogService
	) {}

	$onChanges(changes: IOnChangesObject): void {
		if (changes.cmsPage.previousValue === changes.cmsPage.currentValue &&
			changes.uriContext.previousValue === changes.uriContext.currentValue
		) {
			return;
		}

		if (this.cmsPage) {
			this.homepageService.getHomepageType(this.cmsPage, this.uriContext).then((homepageType: HomepageType) => {
				this.type = homepageType;
			});
		}
	}

	public isVisible() {
		return this.type !== null;
	}

	public getHomepageIcon() {
		return {
			'hyicon hyicon-home hyicon-home--current': HomepageType.CURRENT === this.type,
			'hyicon hyicon-home hyicon-home--old': HomepageType.OLD === this.type,
		};
	}

}