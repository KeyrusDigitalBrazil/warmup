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
import {CrossFrameEventService, ISeComponent, IToolingLanguage, LanguageService} from "../../services";

/* @internal */
export class LanguageSelectorController implements ISeComponent {

	public selectedLanguage: IToolingLanguage = null;
	public languages: IToolingLanguage[] = [];

	private unRegisterEventService: () => void;

	constructor(
		private SWITCH_LANGUAGE_EVENT: string,
		private languageService: LanguageService,
		private crossFrameEventService: CrossFrameEventService,
		private $q: angular.IQService
	) {}

	$onInit(): void {
		this.$q.all([
			this.languageService.getResolveLocale(),
			this.languageService.getToolingLanguages()
		]).then(([isoCode, languages]: [string, IToolingLanguage[]]) => {
			this.languages = [...languages];
			this.setSelectedLanguage(isoCode);
		});

		this.unRegisterEventService = this.crossFrameEventService.subscribe(this.SWITCH_LANGUAGE_EVENT, this.handleLanguageChange.bind(this));
	}

	$onDestroy(): void {
		this.unRegisterEventService();
	}

	/**
	 * Triggered when an user selects a language.
	 * @param {IToolingLanguage} language
	 */
	onSelectedLanguage(language: IToolingLanguage) {
		this.languageService.setSelectedToolingLanguage(language);
	}

	/**
	 * Returns an ordered language array by name and sets the selected language at the beginning.
	 *
	 * @param {IToolingLanguage} selectedLanguage
	 * @param {IToolingLanguage[]} languages
	 * @returns {IToolingLanguage[]}
	 */
	protected orderLanguagesWithSelectedLanguage(selectedLanguage: IToolingLanguage, languages: IToolingLanguage[]) {
		const orderedLanguages = this.languages.filter((language) => language !== selectedLanguage).sort((a, b) => {
			return a.isoCode.localeCompare(b.isoCode);
		});
		orderedLanguages.unshift(selectedLanguage);

		return orderedLanguages;
	}

	/**
	 * Triggered onInit and when language service sets a new language.
	 *
	 * @param {IToolingLanguage[]} languages
	 * @param {string} isoCode
	 */
	private setSelectedLanguage(isoCode: string): void {
		this.selectedLanguage = this.findLanguageWithIsoCode(isoCode);

		if (this.selectedLanguage) {
			this.languages = this.orderLanguagesWithSelectedLanguage(this.selectedLanguage, this.languages);
			return;
		}

		// In case the iso code is too specific, it will use the more generic iso code to set the language.
		this.languageService.getResolveLocaleIsoCode().then((code: string) => {
			this.selectedLanguage = this.findLanguageWithIsoCode(code);
			this.languages = this.orderLanguagesWithSelectedLanguage(this.selectedLanguage, this.languages);
		});
	}

	/**
	 * Finds the language with a specified isoCode.
	 *
	 * @param {string} isoCode
	 * @returns {IToolingLanguage}
	 */
	private findLanguageWithIsoCode(isoCode: string): IToolingLanguage {
		return this.languages.find((language) => language.isoCode === isoCode);
	}

	/**
	 * Callback for setting the selected language.
	 */
	private handleLanguageChange(): void {
		this.languageService.getResolveLocale().then((isoCode: string) => {
			this.setSelectedLanguage(isoCode);
		});
	}

}
