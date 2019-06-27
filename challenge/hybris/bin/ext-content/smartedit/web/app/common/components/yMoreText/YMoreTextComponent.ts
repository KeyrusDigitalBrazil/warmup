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
import {ISeComponent, SeComponent} from "smarteditcommons/services/dependencyInjection/di";

import {TruncatedText} from "smarteditcommons/dtos/TruncatedText";
import {TextTruncateService} from "smarteditcommons/services/text/textTruncateService";

/**
 * @ngdoc directive
 * @name SmarteditCommonsModule.component:yMoreTextComponent
 * @element more-text
 * @description
 * The component for truncating strings and adding an ellipsis. 
 * If the limit is less then the string length then the string is truncated and 'more'/'less' buttons
 * are displayed to expand or collapse the string.
 * 
 * @param {< String} text the text to be displayed
 * @param {< String =} limit index in text to truncate to. Default value is 100.
 * @param {< String =} moreLabelI18nKey the label property value for a more button. Default value is 'more'.
 * @param {< String =} lessLabelI18nKey the label property value for a less button. Default value is 'less'.
 * @param {< String =} ellipsis the ellipsis for a truncated text. Default value is an empty string.
 */
@SeComponent({
	templateUrl: 'moreTextTemplate.html',
	inputs: ['text', 'limit:?', 'moreLabelI18nKey:?', 'lessLabelI18nKey:?', 'ellipsis:?'],
	providers: [TextTruncateService]
})
export class YMoreTextComponent implements ISeComponent {
	public text: string;
	public linkLabel: string;
	public isTruncated: boolean = false;

	private limit: number;
	private ellipsis: string;
	private showingMore: boolean = false;
	private moreLabelI18nKey: string;
	private lessLabelI18nKey: string;
	private moreLabel: string;
	private lessLabel: string;
	private truncatedText: TruncatedText;

	constructor(
		private textTruncateService: TextTruncateService,
		private $translate: angular.translate.ITranslateService,
		private $q: angular.IQService) {}

	$onInit(): void {
		this.limit = this.limit || 100;
		this.moreLabelI18nKey = this.moreLabelI18nKey || 'se.moretext.more.link';
		this.lessLabelI18nKey = this.lessLabelI18nKey || 'se.moretext.less.link';
		this.truncatedText = this.textTruncateService.truncateToNearestWord(this.limit, this.text, this.ellipsis);
		this.isTruncated = this.truncatedText.isTruncated();
		this.translateLabels().then(() => {
			this.showHideMoreText();
		});
	}

	showHideMoreText(): void {
		if (this.isTruncated) {
			this.text = this.showingMore ? this.truncatedText.getUntruncatedText() : this.truncatedText.getTruncatedText();
			this.linkLabel = this.showingMore ? this.lessLabel : this.moreLabel;
			this.showingMore = !this.showingMore;
		}
	}

	translateLabels(): angular.IPromise<any> {
		const promisesToResolve: angular.IPromise<any>[] = [];
		const moreLink = this.$translate(this.moreLabelI18nKey).then((label: string) => {
			this.moreLabel = this.moreLabel || label;
		});
		const lessLink = this.$translate(this.lessLabelI18nKey).then((label: string) => {
			this.lessLabel = this.lessLabel || label;
		});
		promisesToResolve.push(moreLink, lessLink);
		return this.$q.all(promisesToResolve);
	}
}
