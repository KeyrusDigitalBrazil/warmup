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
import {LanguageService, SeInjectable} from "smarteditcommons";
import "ckeditor";

@SeInjectable()
export class SeRichTextFieldLocalizationService {

	constructor(
		private languageService: LanguageService,
		private resolvedLocaleToCKEDITORLocaleMap: any
	) {}

	localizeCKEditor(): void {
		this.languageService.getResolveLocale().then((locale) => {
			CKEDITOR.config.language = this.convertResolvedToCKEditorLocale(locale);
		});
	}

	private convertResolvedToCKEditorLocale(resolvedLocale: string): string {
		const conversion = this.resolvedLocaleToCKEDITORLocaleMap[resolvedLocale];
		if (conversion) {
			return conversion;
		}
		return resolvedLocale;
	}

}
