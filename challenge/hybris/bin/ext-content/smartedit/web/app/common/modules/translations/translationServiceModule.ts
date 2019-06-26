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
import {SeModule} from 'smarteditcommons/services/dependencyInjection/di';
import {$translateStaticFilesLoader} from './translateStaticFilesLoader';

/**
 * @ngdoc service
 * @name translationServiceModule
 *
 * @description
 * 
 * This module is used to configure the translate service, the filter, and the directives from the 'pascalprecht.translate' package. The configuration consists of:
 * 
 * <br/>- Initializing the translation map from the {@link i18nInterceptorModule.object:I18NAPIROOT I18NAPIROOT} constant.
 * <br/>- Setting the preferredLanguage to the {@link i18nInterceptorModule.object:UNDEFINED_LOCALE UNDEFINED_LOCALE} so that the {@link i18nInterceptorModule.service:i18nInterceptor#methods_request i18nInterceptor request} can replace it with the appropriate URI combined with the runtime browser locale retrieved from browserService.getBrowserLocale, which is unaccessible at configuration time.
 */

@SeModule({
	imports: ['pascalprecht.translate', 'i18nInterceptorModule', 'smarteditCommonsModule'],
	providers: [$translateStaticFilesLoader],
	config: ($translateProvider: any, I18NAPIROOT: string, UNDEFINED_LOCALE: string) => {
		'ngInject';
        /*
         * hard coded url that is always intercepted by i18nInterceptor so as to replace by value from configuration REST call
         */
		$translateProvider.useStaticFilesLoader({
			prefix: '/' + I18NAPIROOT + '/',
			suffix: ''
		});

		// Tell the module what language to use by default
		$translateProvider.preferredLanguage(UNDEFINED_LOCALE);

		// Using 'escapeParameters' strategy. 'sanitize' not supported in current version.
		// see https://angular-translate.github.io/docs/#/guide/19_security
		// Note that this is the only option that should be used for now.
		// The options 'sanitizeParameters' and 'escape' are causing issues (& replaced by &amp; and interpolation parameters values are not displayed correctly).
		$translateProvider.useSanitizeValueStrategy('escapeParameters');
	},
	initialize: (operationContextService: any, I18N_RESOURCE_URI: string, OPERATION_CONTEXT: any) => {
		'ngInject';
		operationContextService.register(I18N_RESOURCE_URI, OPERATION_CONTEXT.TOOLING);
	}
})
export class TranslationServiceModule {}