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
import {SeRichTextFieldLocalizationService} from "smarteditcommons/components/genericEditor";
import {promiseHelper} from 'testhelpers';
import {LanguageService} from "smarteditcommons";

describe('seRichTextFieldLocalizationService', () => {
	const $q = promiseHelper.$q();

	let seRichTextFieldLocalizationService: SeRichTextFieldLocalizationService;
	let languageService: jasmine.SpyObj<LanguageService>;

	let resolvedLocaleToCKEDITORLocaleMap: any;

	let originalCKEDITOR: any;

	beforeAll(() => {
		originalCKEDITOR = (window as any).CKEDITOR;
	});

	afterAll(() => {
		(window as any).CKEDITOR = originalCKEDITOR;
	});

	beforeEach(() => {
		(window as any).CKEDITOR = {
			config: {}
		};

		languageService = jasmine.createSpyObj<LanguageService>('languageService', ['getResolveLocale']);
		resolvedLocaleToCKEDITORLocaleMap = {
			en: 'xx'
		};

		seRichTextFieldLocalizationService = new SeRichTextFieldLocalizationService(
			languageService,
			resolvedLocaleToCKEDITORLocaleMap
		);
	});

	describe('localizeCKEditor', () => {
		it('should set global variable CKEDITOR\'s language to the current locale\'s equivalent in CKEDITOR when the conversion exists', function() {
			const existingLocale = 'en';
			expect(resolvedLocaleToCKEDITORLocaleMap[existingLocale]).not.toBeUndefined();

			languageService.getResolveLocale.and.returnValue($q.when(existingLocale));
			seRichTextFieldLocalizationService.localizeCKEditor();

			expect(languageService.getResolveLocale).toHaveBeenCalled();
			expect(CKEDITOR.config.language).toEqual('xx');
		});

		it('should set global variable CKEDITOR\'s language to the current locale when the conversion does not exist', function() {
			const nonexistingLocale = 'zz';
			expect(resolvedLocaleToCKEDITORLocaleMap[nonexistingLocale]).toBeUndefined();

			languageService.getResolveLocale.and.returnValue($q.when(nonexistingLocale));
			seRichTextFieldLocalizationService.localizeCKEditor();

			expect(languageService.getResolveLocale).toHaveBeenCalled();
			expect(CKEDITOR.config.language).toEqual(nonexistingLocale);
		});
	});
});
