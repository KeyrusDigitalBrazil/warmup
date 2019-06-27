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

import {rarelyChangingContent, Cached} from '../cache';
import {
	CrossFrameEventService,
	IBrowserService,
	IRestService,
	IRestServiceFactory,
	IStorageService
} from "smarteditcommons";

import {SeInjectable} from '../dependencyInjection/di';
import {OperationContextRegistered} from '../httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation';
import {Payload} from 'smarteditcommons/dtos';

/**
 * @ngdoc interface
 * @name smarteditCommonsModule.interface:ILanguage
 * @description
 * Interface for language information
 */
export interface ILanguage extends Payload {
	active: boolean;
	isocode: string;
	name: string;
	nativeName: string;
	required: boolean;
}

export interface IToolingLanguage {
	isoCode: string;
	name: string;
}

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:LanguageService
 */
@OperationContextRegistered('LANGUAGE_RESOURCE_URI', 'TOOLING')
@SeInjectable()
export class LanguageService {
	private languageRestService: IRestService<{languages: ILanguage[]}>;
	private i18nLanguageRestService: IRestService<{languages: IToolingLanguage[]}>;
	private initDeferred: angular.IDeferred<void> = this.$q.defer();

	/** @internal */
	constructor(
		private $log: angular.ILogService,
		private $translate: angular.translate.ITranslateService,
		private $q: angular.IQService,
		private languageServiceGateway: any,
		private crossFrameEventService: CrossFrameEventService,
		private browserService: IBrowserService,
		private storageService: IStorageService,
		private SWITCH_LANGUAGE_EVENT: string,
		private SELECTED_LANGUAGE: string,
		LANGUAGE_RESOURCE_URI: string,
		I18N_LANGUAGES_RESOURCE_URI: string,
		restServiceFactory: IRestServiceFactory
	) {
		this.languageRestService = restServiceFactory.get(LANGUAGE_RESOURCE_URI);
		this.i18nLanguageRestService = restServiceFactory.get(I18N_LANGUAGES_RESOURCE_URI);
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getBrowserLanguageIsoCode
     * @methodOf smarteditCommonsModule.service:LanguageService
     * 
     * @deprecated since 1808
     * 
     * @description
     * Uses the browser's current locale to determine the selected language ISO code.
     *
     * @returns {String} The language ISO code of the browser's currently selected locale.
     */
	getBrowserLanguageIsoCode(): string {
		return window.navigator.language.split('-')[0];
	}

	setInitialized(initialized: boolean): void {
		initialized ? this.initDeferred.resolve() : this.initDeferred.reject();
	}

	isInitialized(): angular.IPromise<void> {
		return this.initDeferred.promise;
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getBrowserLocale
     * @methodOf smarteditCommonsModule.service:LanguageService
     * 
     * @deprecated since 1808 - use browserService instead.
     *
     * @description
     * determines the browser locale in the format en_US
     *
     * @returns {string} the browser locale
     */
	getBrowserLocale(): string {
		return this.browserService.getBrowserLocale();
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getResolveLocale
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale. It determines in the
     * following order:
     *
     * 1. Check if the user has previously selected the language
     * 2. Check if the user browser locale is supported in the system
     * 
     * @returns {angular.IPromise<string>} the locale
     */
	getResolveLocale(): angular.IPromise<string> {
		return this.$q.when(this._getDefaultLanguage());
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getResolveLocaleIsoCode
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale ISO code. i.e.: If the selected tooling language is 'en_US',
     * the resolved value will be 'en'.
     *
     * @returns {angular.IPromise<string>} A promise that resolves to the isocode of the tooling language.
     */
	getResolveLocaleIsoCode(): angular.IPromise<string> {
		return this.getResolveLocale().then((resolveLocale: string) => {
			return this.convertBCP47TagToJavaTag(resolveLocale).split('_')[0];
		});
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getLanguagesForSite
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Fetches a list of language descriptors for the specified storefront site UID.
     * The object containing the list of sites is fetched using REST calls to the cmswebservices languages API.
     * 
     * @param {string} siteUID the site unique identifier.
     * 
     * @returns {angular.IPromise<ILanguage[]>} A promise that resolves to an array of ILanguage.
     */
	@Cached({actions: [rarelyChangingContent]})
	getLanguagesForSite(siteUID: string): angular.IPromise<ILanguage[]> {
		return this.languageRestService.get({
			siteUID
		}).then((languagesList) => {
			return languagesList.languages;
		}, (error: any) => {
			this.$log.error('LanguageService.getLanguagesForSite() - Error loading languages');
			return this.$q.reject(error);
		});
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getToolingLanguages
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Retrieves a list of language descriptors using REST calls to the smarteditwebservices i18n API.
     *
     * @returns {angular.IPromise<IToolingLanguage[]>} A promise that resolves to an array of IToolingLanguage.
     */
	@Cached({actions: [rarelyChangingContent]})
	getToolingLanguages(): angular.IPromise<IToolingLanguage[]> {
		return this.i18nLanguageRestService.get({}).then((response) => {
			return this.$q.when(response.languages);
		}, (error) => {
			this.$log.error('LanguageService.getToolingLanguages() - Error loading tooling languages');
			return this.$q.reject(error);
		});
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#setSelectedToolingLanguage
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Set the user preference language in the storage service
     *
     * @param {IToolingLanguage} language the language object to be saved.
     */
	setSelectedToolingLanguage(language: IToolingLanguage): void {
		this.storageService.putValueInCookie(this.SELECTED_LANGUAGE, language, false);
		this.$translate.use(language.isoCode);
		this.languageServiceGateway.publish(this.SWITCH_LANGUAGE_EVENT, {
			isoCode: language.isoCode
		});
		this.crossFrameEventService.publish(this.SWITCH_LANGUAGE_EVENT);
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#registerSwitchLanguage
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Register a callback function to the gateway in order to switch the tooling language
     */
	registerSwitchLanguage(): void {
		this.languageServiceGateway.subscribe(this.SWITCH_LANGUAGE_EVENT, (eventId: string, language: IToolingLanguage) => this.$translate.use(language.isoCode));
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#convertBCP47TagToJavaTag
     * @methodOf smarteditCommonsModule.service:LanguageService
     * 
     * @description
     * Method converts the BCP47 language tag representing the locale to the default java representation.
     * For example, method converts "en-US" to "en_US".
     * 
     * @param {string} languageTag the language tag to be converted.
     * 
     * @returns {string} the languageTag in java representation
     */
	convertBCP47TagToJavaTag(languageTag: string): string {
		return !!languageTag ? languageTag.replace(/-/g, '_') : languageTag;
	}

    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#convertJavaTagToBCP47Tag
     * @methodOf smarteditCommonsModule.service:LanguageService
     * 
     * @description
     * Method converts the default java language tag representing the locale to the BCP47 representation.
     * For example, method converts "en_US" to "en-US".
     * 
     * @param {string} languageTag the language tag to be converted.
     * 
     * @returns {string} the languageTag in BCP47 representation
     */
	convertJavaTagToBCP47Tag(languageTag: string): string {
		return !!languageTag ? languageTag.replace(/_/g, '-') : languageTag;
	}

	private _getDefaultLanguage(): angular.IPromise<string> {
		return this.storageService.getValueFromCookie(this.SELECTED_LANGUAGE, false).then((lang: {name: string, isoCode: string}) => {
			return lang ? lang.isoCode : this.browserService.getBrowserLocale();
		}, () => {
			return this.browserService.getBrowserLocale();
		}
		);
	}
}
