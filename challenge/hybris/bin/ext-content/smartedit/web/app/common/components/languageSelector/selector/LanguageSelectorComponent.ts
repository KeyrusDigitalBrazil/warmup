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
 * @name SmarteditCommonsModule.component:LanguageSelectorComponent
 * @element language-selector
 * @description
 * A language selector which allows the user to select a language while showing the currently displayed language.
 *
 * Use the {@link smarteditCommonsModule.service:LanguageService languageService}
 * to call backend API in order to get the list of supported languages
 */
import {CrossFrameEventService, LanguageService, SeComponent} from "../../../services/";
import {LanguageSelectorController} from "../LanguageSelectorController";

@SeComponent({
	templateUrl: 'languageSelectorTemplate.html'
})
export class LanguageSelectorComponent extends LanguageSelectorController {

	constructor(
		SWITCH_LANGUAGE_EVENT: string,
		languageService: LanguageService,
		crossFrameEventService: CrossFrameEventService,
		$q: angular.IQService
	) {
		super(SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q);
	}

}
