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
import {LanguageService, SeInjectable, TypedMap} from "smarteditcommons";

/**
 * @ngdoc service
 * @name dateTimePickerModule.service:dateTimePickerLocalizationService
 *
 * @description
 * The dateTimePickerLocalizationService is responsible for both localizing the date time picker as well as the tooltips
 */
@SeInjectable()
export class DateTimePickerLocalizationService {

	constructor(
		private $translate: angular.translate.ITranslateService,
		private resolvedLocaleToMomentLocaleMap: any,
		private tooltipsMap: TypedMap<string>,
		private languageService: LanguageService
	) {}

	localizeDateTimePicker(datetimepicker: any) {
		this.localizeDateTimePickerUI(datetimepicker);
		this.localizeDateTimePickerTooltips(datetimepicker);
	}

	private convertResolvedToMomentLocale(resolvedLocale: string): string {
		const conversion = this.resolvedLocaleToMomentLocaleMap[resolvedLocale];
		if (conversion) {
			return conversion;
		} else {
			return resolvedLocale;
		}
	}

	private getLocalizedTooltips(): TypedMap<string> {
		const localizedTooltips: TypedMap<string> = {};

		for (const index in this.tooltipsMap) {
			if (this.tooltipsMap.hasOwnProperty(index)) {
				localizedTooltips[index] = this.$translate.instant(this.tooltipsMap[index]);
			}
		}

		return localizedTooltips;

	}

	private compareTooltips(tooltips1: TypedMap<string>, tooltips2: TypedMap<string>): boolean {
		for (const index in this.tooltipsMap) {
			if (tooltips1[index] !== tooltips2[index]) {
				return false;
			}
		}
		return true;
	}

	private localizeDateTimePickerUI(datetimepicker: any): void {
		this.languageService.getResolveLocale().then((language: string) => {

			const momentLocale = this.convertResolvedToMomentLocale(language);

			// This if statement was added to prevent infinite recursion, at the moment it triggers twice
			// due to what seems like datetimepicker.locale(<string>) broadcasting dp.show
			if (datetimepicker.locale() !== momentLocale) {
				datetimepicker.locale(momentLocale);
			}

		});
	}

	private localizeDateTimePickerTooltips(datetimepicker: any): void {
		const currentTooltips = datetimepicker.tooltips();
		const translatedTooltips = this.getLocalizedTooltips();

		// This if statement was added to prevent infinite recursion, at the moment it triggers twice
		// due to what seems like datetimepicker.tooltips(<tooltips obj>) broadcasting dp.show
		if (!this.compareTooltips(currentTooltips, translatedTooltips)) {
			datetimepicker.tooltips(translatedTooltips);
		}

	}

}
