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
import {SeComponent} from 'smarteditcommons';
import * as angular from "angular";
import {AssetsService} from "cmscommons";

@SeComponent({
	inputs: ['numberOfRestrictions'],
	templateUrl: 'restrictionsPageListIconTemplate.html'
})
export class RestrictionsPageListIconComponent {

	public numberOfRestrictions: number;

	constructor(
		private assetsService: AssetsService,
		private $translate: angular.translate.ITranslateService
	) {}

	get imageSrc() {
		return this.assetsService.getAssetsRoot() + (this.numberOfRestrictions > 0 ? '/images/icon_restriction_small_blue.png' :
			'/images/icon_restriction_small_grey.png');
	}

	get title() {
		return this.$translate.instant('se.icon.tooltip.visibility', {
			numberOfRestrictions: this.numberOfRestrictions
		});
	}
}
