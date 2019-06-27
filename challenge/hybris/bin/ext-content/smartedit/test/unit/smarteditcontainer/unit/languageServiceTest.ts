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
import 'jasmine';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise, PromiseType} from 'testhelpers';
import {annotationService, CrossFrameEventService, GatewayFactory, ILanguage, IRestService, IStorageService, IToolingLanguage, LanguageService, OperationContextRegistered} from "smarteditcommons";
import {RestServiceFactory} from 'smarteditcontainer/services';

describe('languageService', () => {
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj('log', ['error', 'debug', 'warn']);
	const $q = promiseHelper.$q();
	const $translate: angular.translate.ITranslateService = jasmine.createSpyObj<angular.translate.ITranslateService>('$translate', ['use']);
	let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
	let gateway: any;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
	let browserService: any;
	const storageService: jasmine.SpyObj<IStorageService> = jasmine.createSpyObj('storageService', ['getValueFromCookie', 'putValueInCookie']);
	const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', ['get']);
	const languageRestService: jasmine.SpyObj<IRestService<{languages: ILanguage[]}>> = jasmine.createSpyObj<IRestService<{languages: ILanguage[]}>>('languageRestService', ['get']);
	const i18nLanguageRestService: jasmine.SpyObj<IRestService<{languages: IToolingLanguage[]}>> = jasmine.createSpyObj<IRestService<{languages: IToolingLanguage[]}>>('languageRestService', ['get']);

	const SWITCH_LANGUAGE_EVENT: string = 'SWITCH_LANGUAGE_EVENT';
	const SELECTED_LANGUAGE: string = 'SELECTED_LANGUAGE';
	const LANGUAGE_RESOURCE_URI: string = 'LANGUAGE_RESOURCE_URI';
	const I18N_LANGUAGES_RESOURCE_URI: string = 'I18N_LANGUAGES_RESOURCE_URI';

	const MOCK_TOOLING_LANGUAGES: any = {
		languages: [{
			isoCode: "en",
			name: "English"
		}, {
			isoCode: "de",
			name: "German"
		}, {
			isoCode: "pt_BR",
			name: "Portuguese"
		}]
	};
	const DEFAULT_BROWSER_LOCALE = 'pt_BR';

	let languageService: LanguageService;

	beforeEach(() => {
		gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
		gateway = jasmine.createSpyObj('gateway', ['publish', 'subscribe']);
		gatewayFactory.createGateway.and.returnValue(gateway);

		crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish', 'subscribe']);
		browserService = jasmine.createSpyObj('browserService', ['getBrowserLocale']);
		browserService.getBrowserLocale.and.returnValue(DEFAULT_BROWSER_LOCALE);

		restServiceFactory.get.and.returnValue(i18nLanguageRestService);
		($translate.use as any).calls.reset();

		coreAnnotationsHelper.init();

		languageService = new LanguageService(
			$log,
			$translate,
			$q,
			gateway,
			crossFrameEventService,
			browserService,
			storageService,
			SWITCH_LANGUAGE_EVENT,
			SELECTED_LANGUAGE,
			LANGUAGE_RESOURCE_URI,
			I18N_LANGUAGES_RESOURCE_URI,
			restServiceFactory);
	});

	it('OperationContextRegistered annotation', () => {
		const decoratorObj = annotationService.getClassAnnotation(LanguageService, OperationContextRegistered as (args?: any) => ClassDecorator);
		expect(decoratorObj).toEqual(['LANGUAGE_RESOURCE_URI', 'TOOLING']);
	});

	it('GIVEN i18n REST call succeed WHEN requesting tooling languages THEN it receives a promise which contains a list of languages ', () => {
		i18nLanguageRestService.get.and.returnValue($q.when(MOCK_TOOLING_LANGUAGES));

		const promise = languageService.getToolingLanguages() as IExtensiblePromise<IToolingLanguage[]>;

		expect(promise.promiseType).toEqual(PromiseType.RESOLVES);
		expect(promise.value).toEqual(MOCK_TOOLING_LANGUAGES.languages);
	});

	it('GIVEN i18n REST call fails WHEN requesting tooling languages THEN it receives a rejected promise', () => {
		i18nLanguageRestService.get.and.returnValue($q.reject());

		const promise = languageService.getToolingLanguages() as IExtensiblePromise<IToolingLanguage[]>;

		expect(promise.promiseType).toEqual(PromiseType.REJECTS);
	});

	it('GIVEN I have previously selected a locale (de), THEN I expect to get that locale (de)', () => {
		storageService.getValueFromCookie.and.returnValue($q.when({
			name: 'German',
			isoCode: 'de'
		}));

		i18nLanguageRestService.get.and.returnValue($q.when(MOCK_TOOLING_LANGUAGES));

		const promise = languageService.getResolveLocale() as IExtensiblePromise<string>;

		expect(promise.promiseType).toEqual(PromiseType.RESOLVES);
		expect(promise.value).toEqual('de');
	});

	it('GIVEN I have previously selected a locale (de), THEN I expect to get the iso code for that locale', () => {
		storageService.getValueFromCookie.and.returnValue($q.when({
			name: 'German',
			isoCode: 'de'
		}));
		i18nLanguageRestService.get.and.returnValue($q.when(MOCK_TOOLING_LANGUAGES));

		const promise = languageService.getResolveLocale() as IExtensiblePromise<string>;

		expect(promise.promiseType).toEqual(PromiseType.RESOLVES);
		expect(promise.value).toEqual('de');
	});

	it('GIVEN I have not previously selected a locale, THEN I expect to get the browser locale', () => {
		storageService.getValueFromCookie.and.returnValue($q.when());
		i18nLanguageRestService.get.and.returnValue($q.when(MOCK_TOOLING_LANGUAGES));

		const promise = languageService.getResolveLocale() as IExtensiblePromise<string>;

		expect(promise.promiseType).toEqual(PromiseType.RESOLVES);
		expect(promise.value).toEqual(DEFAULT_BROWSER_LOCALE);
	});

	it('GIVEN I have not previously selected a locale, THEN I expect to be able to resolve the browser locale iso code', () => {
		storageService.getValueFromCookie.and.returnValue($q.when());
		i18nLanguageRestService.get.and.returnValue($q.when(MOCK_TOOLING_LANGUAGES));

		const promise = languageService.getResolveLocaleIsoCode() as IExtensiblePromise<string>;

		expect(promise.promiseType).toEqual(PromiseType.RESOLVES);
		expect(promise.value).toEqual(DEFAULT_BROWSER_LOCALE.split('_')[0]);
	});

	it('GIVEN I register for switching the language THEN it should subscribe to the gateway', () => {
		languageService.registerSwitchLanguage();
		expect(gateway.subscribe).toHaveBeenCalledWith(SWITCH_LANGUAGE_EVENT, jasmine.any(Function));

		expect($translate.use).not.toHaveBeenCalled();

		const callback = gateway.subscribe.calls.argsFor(0)[1];

		callback("eventId", {
			isoCode: 'kl',
			name: 'ANY_NAME'
		} as IToolingLanguage);

		expect($translate.use).toHaveBeenCalledWith('kl');
	});

	it('GIVEN I select a language THEN it should save the language in the cookie AND switch the language AND publish an event to the gateway', () => {
		const language = {
			name: 'German',
			isoCode: 'de'
		};

		languageService.setSelectedToolingLanguage(language);

		expect(storageService.putValueInCookie).toHaveBeenCalledWith(SELECTED_LANGUAGE, language, false);
		expect($translate.use).toHaveBeenCalledWith('de');

		expect(crossFrameEventService.publish).toHaveBeenCalledWith(SWITCH_LANGUAGE_EVENT);
		expect(gateway.publish).toHaveBeenCalledWith(SWITCH_LANGUAGE_EVENT, {
			isoCode: 'de'
		});
	});

	it('GIVEN tag in BCP47 format WHEN convertBCP47TagToJavaTag is used THEN it is converted to java tag', () => {
		const bcp47Tag = 'en-US';

		const javaTag = languageService.convertBCP47TagToJavaTag(bcp47Tag);

		expect(javaTag).toEqual('en_US');
	});

	it('GIVEN tag in java format WHEN convertJavaTagToBCP47Tag is used THEN it is converted to BCP47 tag', () => {
		const javaTag = 'en_US';

		const bcp47Tag = languageService.convertJavaTagToBCP47Tag(javaTag);

		expect(bcp47Tag).toEqual('en-US');
	});

	describe('site languages', () => {
		const SITE_UID = 'apparel-de';
		const MOCK_LANGUAGES: any = {
			languages: [{
				nativeName: "English",
				isocode: "en",
				name: "English",
				active: true,
				required: true
			}, {
				nativeName: "Deutsch",
				isocode: "de",
				name: "German",
				active: true,
				required: false
			}]
		};

		beforeEach(() => {
			restServiceFactory.get.and.returnValue(languageRestService);

			languageService = new LanguageService(
				$log,
				$translate,
				$q,
				gateway,
				crossFrameEventService,
				browserService,
				storageService,
				SWITCH_LANGUAGE_EVENT,
				SELECTED_LANGUAGE,
				LANGUAGE_RESOURCE_URI,
				I18N_LANGUAGES_RESOURCE_URI,
				restServiceFactory);
		});

		it('GIVEN languages REST call fails WHEN I request all languages for a given site THEN I will receive a rejected promise', () => {
			languageRestService.get.and.returnValue($q.reject());

			const promise = languageService.getLanguagesForSite(SITE_UID) as IExtensiblePromise<ILanguage[]>;

			expect(promise.value).toBeFalsy();
		});

		it('GIVEN languages REST call succeeds WHEN I request all languages for a given site THEN I will receive a promise that resolves to the list of language objects', () => {
			languageRestService.get.and.returnValue($q.when(MOCK_LANGUAGES));

			const promise = languageService.getLanguagesForSite(SITE_UID) as IExtensiblePromise<ILanguage[]>;

			expect(promise.value).toEqual(MOCK_LANGUAGES.languages);
		});
	});
});
