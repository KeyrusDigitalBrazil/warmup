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
angular.module('i18nInterceptorModule', ['interceptorHelperModule', 'resourceLocationsModule', 'smarteditServicesModule']) //loose dependency on loadConfigModule since it exists in smartEditContainer but not smartEdit
    /**
     * @ngdoc object
     * @name i18nInterceptorModule.object:I18NAPIROOT
     *
     * @description
     * The I18NAPIroot is a hard-coded URI that is used to initialize the {@link translationServiceModule}.
     * The {@link i18nInterceptorModule.service:i18nInterceptor#methods_request i18nInterceptor.request} intercepts the URI and replaces it with the {@link resourceLocationsModule.object:I18N_RESOURCE_URI I18N_RESOURCE_URI}.
     */
    .constant('I18NAPIROOT', 'i18nAPIRoot')
    /**
     * @ngdoc object
     * @name i18nInterceptorModule.object:UNDEFINED_LOCALE
     *
     * @description
     * The undefined locale set as the preferred language of the {@link translationServiceModule} so that
     * an {@link i18nInterceptorModule.service:i18nInterceptor#methods_request i18nInterceptor.request} can intercept it and replace it with the browser locale.
     */
    .constant('UNDEFINED_LOCALE', 'UNDEFINED')
    /**
     * @ngdoc service
     * @name i18nInterceptorModule.service:i18nInterceptor
     *
     * @description
     * A HTTP request interceptor that intercepts all i18n calls and handles them as required in the {@link i18nInterceptorModule.service:i18nInterceptor#methods_request i18nInterceptor.request} method.
     *
     * The interceptors are service factories that are registered with the $httpProvider by adding them to the $httpProvider.interceptors array.
     * The factory is called and injected with dependencies and returns the interceptor object, which contains the interceptor methods.
     */
    .factory('i18nInterceptor', function($injector, interceptorHelper, I18N_RESOURCE_URI, I18NAPIROOT) {

        return {

            /**
             * @ngdoc method
             * @name i18nInterceptorModule.service:i18nInterceptor#request
             * @methodOf i18nInterceptorModule.service:i18nInterceptor
             *
             * @description
             * Interceptor method that is invoked with a HTTP configuration object.
             *  It intercepts all requests that are i18n calls, that is, it intercepts all requests that have an {@link i18nInterceptorModule.object:I18NAPIROOT I18NAPIROOT} in their calls.
             *  It replaces the URL provided in a request with the URL provided by the {@link resourceLocationsModule.object:I18N_RESOURCE_URI I18N_RESOURCE_URI}.
             *  If a locale has not already been defined, the interceptor method appends the locale retrieved using the {@link smarteditCommonsModule.service:languageService#methods_getResolveLocale languageService.getResolveLocale}.


             * @param {Object} config The HTTP configuration information that contains the configuration information.
             *
             * @returns {Promise} Returns a {@link https://docs.angularjs.org/api/ng/service/$q promise} of the passed configuration object.
             */
            request: function(config) {
                return interceptorHelper.handleRequest(config, function() {
                    /*
                     * always intercept i18n calls so as to replace URI by one from configuration (cannot be done at config time of $translateProvider)
                     * regex matching /i18nAPIRoot/<my_locale>
                     */
                    var regex = new RegExp(I18NAPIROOT + "\/([a-zA-Z_-]+)$");
                    if (regex.test(config.url)) {
                        return $injector.get('languageService').getResolveLocale().then(function(isoCode) {
                            config.url = [I18N_RESOURCE_URI, isoCode].join('/');
                            return config;
                        });
                    } else {
                        return config;
                    }
                });
            },
            response: function(response) {
                return interceptorHelper.handleResponse(response, function() {
                    /*
                     * if it intercepts a call to I18N_RESOURCE_URI the response body will be adapted to
                     * read the value from response.data.value instead.
                     */
                    var regex = new RegExp(I18N_RESOURCE_URI + "/([a-zA-Z_-]+)$");
                    if (response.config.url) {
                        var url = response.config.url;
                        if (regex.test(url) && response.data.value) {
                            response.data = response.data.value;
                        }
                    }
                    $injector.get('languageService').setInitialized(true);
                    return response;
                });
            }
        };
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('i18nInterceptor');
    });
