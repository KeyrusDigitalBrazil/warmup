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
(function() {

    var CONTEXT_CATALOG = 'CURRENT_CONTEXT_CATALOG';
    var CONTEXT_CATALOG_VERSION = 'CURRENT_CONTEXT_CATALOG_VERSION';
    var CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';

    var PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
    var PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
    var PAGE_CONTEXT_SITE_ID = 'CURRENT_PAGE_CONTEXT_SITE_ID';

    /**
     * @ngdoc object
     * @name resourceLocationsModule.object:UriContext
     *
     * @description
     * A map that contains the necessary site and catalog information for CMS services and directives.
     * It contains the following keys:
     * {@link resourceLocationsModule.object:CONTEXT_SITE_ID CONTEXT_SITE_ID} for the site uid,
     * {@link resourceLocationsModule.object:CONTEXT_CATALOG CONTEXT_CATALOG} for the catalog uid,
     * {@link resourceLocationsModule.object:CONTEXT_CATALOG_VERSION CONTEXT_CATALOG_VERSION} for the catalog version.
     */

    /**
     * @ngdoc object
     * @name resourceLocationsModule.object:PageUriContext
     *
     * @description
     * A map that contains the necessary site and catalog information for CMS services and directives for a given page.
     * It contains the following keys:
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_SITE_ID PAGE_CONTEXT_SITE_ID} for the site uid,
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_CATALOG PAGE_CONTEXT_CATALOG} for the catalog uid,
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_CATALOG_VERSION PAGE_CONTEXT_CATALOG_VERSION} for the catalog version.
     */

    angular.module('resourceLocationsModule', [])

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_SITE_ID
         *
         * @description
         * Constant containing the name of the site uid placeholder in URLs
         */
        .constant('CONTEXT_SITE_ID', CONTEXT_SITE_ID)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_CATALOG
         *
         * @description
         * Constant containing the name of the catalog uid placeholder in URLs
         */
        .constant('CONTEXT_CATALOG', CONTEXT_CATALOG)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_CATALOG_VERSION
         *
         * @description
         * Constant containing the name of the catalog version placeholder in URLs
         */
        .constant('CONTEXT_CATALOG_VERSION', CONTEXT_CATALOG_VERSION)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_SITE_ID
         *
         * @description
         * Constant containing the name of the current page site uid placeholder in URLs
         */
        .constant('PAGE_CONTEXT_SITE_ID', PAGE_CONTEXT_SITE_ID)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG
         *
         * @description
         * Constant containing the name of the current page catalog uid placeholder in URLs
         */
        .constant('PAGE_CONTEXT_CATALOG', PAGE_CONTEXT_CATALOG)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG_VERSION
         *
         * @description
         * Constant containing the name of the current page catalog version placeholder in URLs
         */
        .constant('PAGE_CONTEXT_CATALOG_VERSION', PAGE_CONTEXT_CATALOG_VERSION)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SMARTEDIT_ROOT
         *
         * @description
         * the name of the webapp root context
         */
        .constant('SMARTEDIT_ROOT', 'smartedit')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SMARTEDIT_RESOURCE_URI_REGEXP
         *
         * @description
         * to calculate platform domain URI, this regular expression will be used
         */
        .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/smartedit/)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONFIGURATION_URI
         *
         * @description
         * the name of the SmartEdit configuration API root
         */
        .constant('CONFIGURATION_URI', '/smartedit/configuration/:key')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONFIGURATION_COLLECTION_URI
         *
         * @description
         * The SmartEdit configuration collection API root
         */
        .constant('CONFIGURATION_COLLECTION_URI', '/smartedit/configuration')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CMSWEBSERVICES_RESOURCE_URI
         *
         * @description
         * Constant for the cmswebservices API root
         */
        .constant('CMSWEBSERVICES_RESOURCE_URI', '/cmswebservices')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT
         *
         * @description
         * When configuration is not available yet to provide authenticationMap, one needs a default authentication entry point to access configuration API itself
         */
        .constant('DEFAULT_AUTHENTICATION_ENTRY_POINT', '/authorizationserver/oauth/token')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:DEFAULT_AUTHENTICATION_CLIENT_ID
         *
         * @description
         * The default OAuth 2 client id to use during authentication.
         */
        .constant('DEFAULT_AUTHENTICATION_CLIENT_ID', 'smartedit')
        /**
         * Root resource URI of i18n API 
         */
        .constant('I18N_ROOT_RESOURCE_URI', '/smarteditwebservices/v1/i18n')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:I18N_RESOURCE_URI
         *
         * @description
         * Resource URI to fetch the i18n initialization map for a given locale.
         */
        .constant('I18N_RESOURCE_URI', '/smarteditwebservices/v1/i18n/translations')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:I18N_LANGUAGE_RESOURCE_URI
         *
         * @description
         * Resource URI to fetch the supported i18n languages.
         */
        .constant('I18N_LANGUAGES_RESOURCE_URI', '/smarteditwebservices/v1/i18n/languages')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:LANGUAGE_RESOURCE_URI
         *
         * @description
         * Resource URI of the languages REST service.
         */
        .constant('LANGUAGE_RESOURCE_URI', '/cmswebservices/v1/sites/:siteUID/languages')
        .constant('PRODUCT_RESOURCE_API', '/cmssmarteditwebservices/v1/sites/:siteUID/products/:productUID')
        .constant('PRODUCT_LIST_RESOURCE_API', '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products')
        .constant('PRODUCT_CATEGORY_RESOURCE_URI', '/cmssmarteditwebservices/v1/sites/:siteUID/categories/:categoryUID')
        .constant('PRODUCT_CATEGORY_SEARCH_RESOURCE_URI', '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/categories')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SITES_RESOURCE_URI
         *
         * @description
         * Resource URI of the sites REST service.
         */
        .constant('SITES_RESOURCE_URI', '/cmswebservices/v1/sites')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:LANDING_PAGE_PATH
         *
         * @description
         * Path of the landing page
         */
        .constant('LANDING_PAGE_PATH', '/')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:STORE_FRONT_CONTEXT
         *
         * @description
         * to fetch the store front context for inflection points.
         */
        .constant('STORE_FRONT_CONTEXT', '/storefront')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CATALOGS_PATH
         *
         * @description
         * Path of the catalogs
         */
        .constant('CATALOGS_PATH', '/cmswebservices/v1/catalogs/')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:MEDIA_PATH
         *
         * @description
         * Path of the media
         */
        .constant('MEDIA_PATH', '/cmswebservices/v1/media')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CMSWEBSERVICES_PATH
         *
         * @description
         * Regular expression identifying CMS related URIs
         */
        .constant('CMSWEBSERVICES_PATH', /\/cmssmarteditwebservices|\/cmswebservices/)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PREVIEW_RESOURCE_URI
         *
         * @description
         * Path of the preview ticket API
         */
        .constant('PREVIEW_RESOURCE_URI', '/previewwebservices/v1/preview')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:ENUM_RESOURCE_URI
         *
         * @description
         * Path to fetch list of values of a given enum type
         */
        .constant('ENUM_RESOURCE_URI', "/cmswebservices/v1/enums")

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PERMISSIONSWEBSERVICES_RESOURCE_URI
         *
         * @description
         * Path to fetch permissions of a given type
         * 
         * @deprecated since 1811
         */
        .constant('USER_GLOBAL_PERMISSIONS_RESOURCE_URI', "/permissionswebservices/v1/permissions/principals/:user/global")
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SYNC_PATH
         *
         * @description
         * Path of the synchronization service
         */
        .constant('SYNC_PATH', '/cmswebservices/v1/catalogs/:catalog/versions/Staged/synchronizations/versions/Online')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:MEDIA_RESOURCE_URI
         *
         * @description
         * Resource URI of the media REST service.
         */
        .constant('MEDIA_RESOURCE_URI', '/cmswebservices/v1/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/media')
        /**
         * @name resourceLocationsModule.object:TYPES_RESOURCE_URI
         *
         * @description
         * Resource URI of the component types REST service.
         */
        .constant('TYPES_RESOURCE_URI', '/cmswebservices/v1/types')

        /**
         * @ngdoc service
         * @name resourceLocationsModule.resourceLocationToRegex
         *
         * @description
         * Generates a regular expresssion matcher from a given resource location URL, replacing predefined keys by wildcard
         * matchers.
         *
         * Example:
         * <pre>
         *     // Get a regex matcher for the someResource endpoint, ie: /\/smarteditwebservices\/someResource\/.*$/g
         *     var endpointRegex = resourceLocationToRegex('/smarteditwebservices/someResource/:id');
         *
         *     // Use the regex to match hits to the mocked HTTP backend. This regex will match for any ID passed in to the
         *     // someResource endpoint.
         *     $httpBackend.whenGET(endpointRegex).respond({someKey: 'someValue'});
         * </pre>
         */
        .factory('resourceLocationToRegex', function() {
            return function(str) {
                return new RegExp(str.replace(/\/:[^\/]*/g, '/.*'));
            };
        });
})();
