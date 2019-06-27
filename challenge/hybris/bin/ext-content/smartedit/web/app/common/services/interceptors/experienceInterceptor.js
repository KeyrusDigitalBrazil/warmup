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
angular.module('experienceInterceptorModule', ['interceptorHelperModule', 'functionsModule', 'smarteditServicesModule', 'smarteditServicesModule', 'resourceLocationsModule', 'yLoDashModule'])
    /**
     * @ngdoc service
     * @name ExperienceInterceptorModule.experienceInterceptor
     *
     * @description
     * A HTTP request interceptor which intercepts all 'cmswebservices/catalogs' requests and adds the current catalog and version
     * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
     *
     *
     * Note: The interceptors are service factories that are registered with the $httpProvider by adding them to the $httpProvider.interceptors array.
     * The factory is called and injected with dependencies and returns the interceptor object with contains the interceptor methods.
     */
    .factory('experienceInterceptor', function(lodash, sharedDataService, interceptorHelper, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, CMSWEBSERVICES_PATH, CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, PAGE_CONTEXT_SITE_ID) {

        /**
         * @ngdoc method
         * @name ExperienceInterceptorModule.experienceInterceptor#request
         * @methodOf ExperienceInterceptorModule.experienceInterceptor
         *
         * @description
         * Interceptor method which gets called with a http config object, intercepts any 'cmswebservices/catalogs' requests and adds
         * the current catalog and version
         * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
         * If the request URI contains any of 'PAGE_CONTEXT_SITE_ID', 'PAGE_CONTEXT_CATALOG' or 'PAGE_CONTEXT_CATALOG_VERSION', 
         * then it is replaced by the siteId/catalogId/catalogVersion of the current page in context.
         *
         * The catalog name and catalog versions of the current experience and the page loaded are stored in the shared data service object called 'experience' during preview initialization
         * and here we retrieve those details and set it to headers.
         *
         * @param {Object} config the http config object that holds the configuration information.
         *
         * @returns {Promise} Returns a {@link https://docs.angularjs.org/api/ng/service/$q promise} of the passed config object.
         */
        var request = function request(config) {
            return interceptorHelper.handleRequest(config, function() {
                if (CMSWEBSERVICES_PATH.test(config.url)) {
                    return sharedDataService.get('experience').then(function(data) {
                        if (data) {

                            var keys = {};
                            keys.CONTEXT_SITE_ID_WITH_COLON = data.siteDescriptor.uid;
                            keys.CONTEXT_CATALOG_VERSION_WITH_COLON = data.catalogDescriptor.catalogVersion;
                            keys.CONTEXT_CATALOG_WITH_COLON = data.catalogDescriptor.catalogId;
                            keys[CONTEXT_SITE_ID] = data.siteDescriptor.uid;
                            keys[CONTEXT_CATALOG_VERSION] = data.catalogDescriptor.catalogVersion;
                            keys[CONTEXT_CATALOG] = data.catalogDescriptor.catalogId;

                            keys[PAGE_CONTEXT_SITE_ID] = data.pageContext ? data.pageContext.siteId : data.siteDescriptor.uid;
                            keys[PAGE_CONTEXT_CATALOG_VERSION] = data.pageContext ? data.pageContext.catalogVersion : data.catalogDescriptor.catalogVersion;
                            keys[PAGE_CONTEXT_CATALOG] = data.pageContext ? data.pageContext.catalogId : data.catalogDescriptor.catalogId;

                            config.url = replaceAll(config.url, keys);

                            if (config.params && typeof config.params === 'object') {
                                config.params = JSON.parse(replaceAll(JSON.stringify(config.params), keys));
                            }
                        }
                        return config;
                    });
                } else {
                    return config;
                }
            });
        };

        var replaceAll = function(str, mapObj) {
            var regex = new RegExp(Object.keys(mapObj).join("|"), "g");
            return str.replace(regex, function(matched) {
                return mapObj[matched];
            });
        };

        var interceptor = {};
        interceptor.request = request.bind(interceptor);
        return interceptor;
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('experienceInterceptor');
    });
