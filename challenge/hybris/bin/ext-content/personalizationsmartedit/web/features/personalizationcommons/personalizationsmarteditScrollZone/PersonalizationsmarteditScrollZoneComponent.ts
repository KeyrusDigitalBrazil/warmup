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
import {SeComponent} from 'smarteditcommons';

@SeComponent({
	inputs: [
		'scrollZoneVisible',
		'getElementToScroll: &',
		'isTransparent',
		'scrollZoneId'
	]
})
export class PersonalizationsmarteditScrollZoneComponent {
	scrollZoneTop: boolean = true;
	scrollZoneBottom: boolean = true;
	start: boolean = false;
	scrollZoneVisible: boolean = false;
	isTransparent: boolean = false;
	elementToScroll: any = {};
	getElementToScroll: any;
	scrollZoneId: string = "";

	constructor(
		private $scope: any,
		private $timeout: any,
		private $compile: any,
		private yjQuery: JQueryStatic) {
	}

	// Methods
	stopScroll(): void {
		this.start = false;
	}

	scrollTop(): void {
		if (!this.start) {
			return;
		}
		this.scrollZoneTop = this.elementToScroll.scrollTop() <= 2 ? false : true;
		this.scrollZoneBottom = true;

		this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() - 15);
		this.$timeout(() => {
			this.scrollTop();
		}, 100);
	}

	scrollBottom(): void {
		if (!this.start) {
			return;
		}
		this.scrollZoneTop = true;
		const heightVisibleFromTop = this.elementToScroll.get(0).scrollHeight - this.elementToScroll.scrollTop();
		this.scrollZoneBottom = Math.abs(heightVisibleFromTop - this.elementToScroll.outerHeight()) < 2 ? false : true;

		this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() + 15);
		this.$timeout(() => {
			this.scrollBottom();
		}, 100);
	}

	// Lifecycle methods
	$onChanges(changes: any): void {
		if (changes.scrollZoneVisible) {
			this.start = changes.scrollZoneVisible.currentValue;
			this.scrollZoneTop = true;
			this.scrollZoneBottom = true;
		}
	}

	$onInit(): void {
		const topScrollZone = this.$compile("<div id=\"sliderTopScrollZone" + this.scrollZoneId + "\" data-ng-include=\"'personalizationsmarteditScrollZoneTopTemplate.html'\"></div>")(this.$scope);
		angular.element("body").append(topScrollZone);
		const bottomScrollZone = this.$compile("<div id=\"sliderBottomScrollZone" + this.scrollZoneId + "\" data-ng-include=\"'personalizationsmarteditScrollZoneBottomTemplate.html'\"></div>")(this.$scope);
		angular.element("body").append(bottomScrollZone);
		this.elementToScroll = this.getElementToScroll();
	}

	$onDestroy(): void {
		angular.element("#sliderTopScrollZone" + this.scrollZoneId).scope().$destroy();
		angular.element("#sliderTopScrollZone" + this.scrollZoneId).remove();
		angular.element("#sliderBottomScrollZone" + this.scrollZoneId).scope().$destroy();
		angular.element("#sliderBottomScrollZone" + this.scrollZoneId).remove();
		angular.element("body").contents().each((val: any) => {
			if (val.nodeType === Node.COMMENT_NODE && val.data.indexOf('personalizationsmarteditScrollZone') > -1) {
				this.yjQuery(val).remove();
			}
		});
	}
}




