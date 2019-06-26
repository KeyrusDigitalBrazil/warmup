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
 * @ngdoc overview
 * @name pagesRestServiceModule
 * @description
 * # The pagesRestServiceModule
 *
 * The pagesRestServiceModule provides REST services for the CMS pages rest endpoint
 *
 */
angular.module('pagesRestServiceModule', ['yLoDashModule', 'resourceLocationsModule', 'functionsModule'])

    /**
     * @ngdoc service
     * @name pagesRestServiceModule.service:pagesRestService
     *
     * @description
     * The pagesRestService provides core REST functionality for the CMS pages rest endpoint
     */
    .service('pagesRestService', function(restServiceFactory, lodash, URIBuilder, PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {
        var URI = '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pages/:pageUid';

        /**
         * @ngdoc method
         * @name pagesRestServiceModule.service:pagesRestService#get
         * @methodOf pagesRestServiceModule.service:pagesRestService
         *
         * @description
         * Fetches a list of pages for a given site, catalog, and catalog version. If the site, catalog, or catalog version
         * is not defined, those used contextually in the session will be used instead.
         *
         * @param {Object} params A JSON object containing catalog context and/or any additional request parameters
         * @param {String} params.siteUID A side ID
         * @param {String} params.catalogId A catalog ID
         * @param {String} params.catalogVersion A catalog version ID
         *
         * Example:
         * ```
         * {
         *      siteUID: 'supershoes',
         *      catalogId: 'shoes',
         *      catalogVersion: 'online',
         *      anOptionalQueryParamName: 'paramValue'
         * }
         * ```
         *
         * @returns {Promise<Array>} A promise resolving to a list of pages, or an empty list.
         */
        this.get = function(params) {
            var _params = lodash.cloneDeep(params);
            var uri = new URIBuilder(URI).replaceParams(_params).build();
            delete _params[CONTEXT_SITE_ID];
            delete _params[CONTEXT_CATALOG];
            delete _params[CONTEXT_CATALOG_VERSION];
            delete _params[PAGE_CONTEXT_SITE_ID];
            delete _params[PAGE_CONTEXT_CATALOG];
            delete _params[PAGE_CONTEXT_CATALOG_VERSION];
            return restServiceFactory.get(uri, 'pageUid').get(_params).then(function(response) {
                return response.pages;
            });
        };

        /**
         * @ngdoc method
         * @name pagesRestServiceModule.service:pagesRestService#getById
         * @methodOf pagesRestServiceModule.service:pagesRestService
         *
         * @description
         * Fetches a page for a given site, catalog, and catalog version. If the site, catalog, or catalog version is not
         * defined, those used contextually in the session will be used instead.
         *
         * @param {String} pageUid A page UID of the page to fetch
         * @param {Object} params A JSON object containing catalog context and/or any additional request parameters
         * @param {String} params.siteUID A side ID
         * @param {String} params.catalogId A catalog ID
         * @param {String} params.catalogVersion A catalog version ID
         *
         * Example:
         * ```
         * {
         *      siteUID: 'supershoes',
         *      catalogId: 'shoes',
         *      catalogVersion: 'online',
         *      anOptionalQueryParamName: 'paramValue'
         * }
         * ```
         *
         * @returns {Promise<Object>} A promise that resolves to a JSON object representing the page.
         */
        this.getById = function(pageUid, params) {
            var uri = new URIBuilder(URI).replaceParams(params).build();
            var extendedParams = lodash.assign({
                pageUid: pageUid
            }, params || {});
            delete extendedParams[PAGE_CONTEXT_SITE_ID];
            delete extendedParams[PAGE_CONTEXT_CATALOG];
            delete extendedParams[PAGE_CONTEXT_CATALOG_VERSION];
            return restServiceFactory.get(uri, 'pageUid').get(extendedParams);
        };

        /**
         * @ngdoc method
         * @name pagesRestServiceModule.service:pagesRestService#update
         * @methodOf pagesRestServiceModule.service:pagesRestService
         *
         * @description
         * Updates a page for a given site, catalog, and catalog version. If the site, catalog, or catalog version is not
         * defined, those used contextually in the session will be used instead.
         *
         * @param {String} pageUid The page UID of the page to update
         * @param {Object} payload The page object to be applied to the page resource as it exists on the backend
         *
         * @returns {Promise<Object>} A promise that resolves to a JSON object representing the updated page.
         */
        this.update = function(pageUid, payload) {
            var uri = new URIBuilder(URI).replaceParams(payload).build();
            var extendedParams = lodash.assign({
                pageUid: pageUid
            }, payload || {});
            delete extendedParams[PAGE_CONTEXT_SITE_ID];
            delete extendedParams[PAGE_CONTEXT_CATALOG];
            delete extendedParams[PAGE_CONTEXT_CATALOG_VERSION];
            return restServiceFactory.get(uri, 'pageUid').update(extendedParams);
        };

    });
