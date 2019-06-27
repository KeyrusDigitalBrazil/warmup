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
/**
 * @ngdoc directive
 * @name SmarteditCommonsModule.component:LanguageDropdownSelectorComponent
 * @element language-dropdown-selector
 * @description
 * An icon language dropdown selector which allows the user to select a language.
 *
 * Use the {@link smarteditCommonsModule.service:LanguageService languageService}
 * to call backend API in order to get the list of supported languages
 */
import {CrossFrameEventService, IToolingLanguage, LanguageService, SeComponent} from "../../../services";
import {LanguageSelectorController} from "../LanguageSelectorController";

@SeComponent({
	templateUrl: 'languageDropdownSelectorTemplate.html'
})
export class LanguageDropdownSelectorComponent extends LanguageSelectorController {

	constructor(
		SWITCH_LANGUAGE_EVENT: string,
		languageService: LanguageService,
		crossFrameEventService: CrossFrameEventService,
		$q: angular.IQService
	) {
		super(SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q);
	}

	protected orderLanguagesWithSelectedLanguage(selectedLanguage: IToolingLanguage, languages: IToolingLanguage[]) {
		return this.languages;
	}

}
