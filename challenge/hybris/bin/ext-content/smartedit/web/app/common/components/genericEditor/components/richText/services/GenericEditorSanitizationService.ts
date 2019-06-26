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
import {SeInjectable} from "smarteditcommons";

@SeInjectable()
export class GenericEditorSanitizationService {

	constructor(
		private $sanitize: angular.sanitize.ISanitizeService
	) {}

	isSanitized(content: any): boolean {
		let sanitizedContent = this.$sanitize(content);
		sanitizedContent = sanitizedContent.replace(/&#10;/g, '\n').replace(/&#160;/g, "\u00a0").replace(/<br>/g, '<br />');
		content = content.replace(/&#10;/g, '\n').replace(/&#160;/g, "\u00a0").replace(/<br>/g, '<br />');
		const sanitizedContentMatchesContent = sanitizedContent === content;
		return sanitizedContentMatchesContent;
	}

}
