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
 * @name pageServiceModule
 * @description
 * # The pageServiceModule
 *
 * The pageServiceModule provides services for dealing with CMS page objects
 *
 */
angular.module('pageServiceModule', [
        'cmsitemsRestServiceModule',
        'pagesFallbacksRestServiceModule',
        'pagesRestServiceModule',
        'pagesVariationsRestServiceModule',
        'yLoDashModule'
    ])

    /**
     * @ngdoc service
     * @name pageServiceModule.service:pageService
     *
     * @description
     * The pageService provides low level functionality for CMS pages
     */
    .service('pageService', function(
        cmsitemsRestService,
        pageInfoService,
        pagesFallbacksRestService,
        pagesRestService,
        pagesVariationsRestService,
        experienceService,
        lodash,
        $q,
        $routeParams,
        CONTEXT_SITE_ID,
        CONTEXT_CATALOG,
        CONTEXT_CATALOG_VERSION
    ) {

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#getPrimaryPagesForPageType
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Fetches a list of pages for for a given site+catalog+catalogversion
         *
         * @param {String} pageTypeCode A page type code to filter pages by
         * @param {Object} uriParams A {@link resourceLocationsModule.object:UriContext UriContext}
         *
         * @returns {Object} A promise that resolves to an array of page objects, or empty array
         */
        this.getPrimaryPagesForPageType = function(pageTypeCode, uriParams) {
            var extendedParams = lodash.assign({}, uriParams || {}, {
                typeCode: pageTypeCode,
                itemSearchParams: "defaultPage:true,pageStatus:ACTIVE",
                currentPage: 0,
                pageSize: 1000
            });
            return cmsitemsRestService.get(extendedParams).then(function(primaryPages) {
                return primaryPages.response;
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#isPagePrimary
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Determines if a page belonging to the current contextual site+catalog+catalogversion is primary.
         *
         * @param {String} pageId The UID of the page.
         *
         * @returns {Promise} A promise that resolves to true if the page is primary, false otherwise.
         */
        this.isPagePrimary = function(pageId) {
            return pagesFallbacksRestService.getFallbacksForPageId(pageId).then(function(fallbacks) {
                return fallbacks.length === 0;
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#isPagePrimary
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Determines if a page belonging to the provided contextual site+catalog+catalogversion is primary.
         *
         * @param {String} pageId The UID of the page.
         * @param {IUriContext} uriContext The uriContext for the pageId
         *
         * @returns {Promise} A promise that resolves to true if the page is primary, false otherwise.
         */
        this.isPagePrimaryWithContext = function(pageId, uriContext) {
            return pagesFallbacksRestService.getFallbacksForPageIdAndContext(pageId, uriContext).then(function(fallbacks) {
                return fallbacks.length === 0;
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#isPagePrimary
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Retrieves the primary page for the current site+catalog+catalogversion.
         *
         * @param {String} variationPageId The UID of the page.
         *
         * @returns {Promise} A promise that resolves to the page object or undefined if no primary page was found.
         */
        this.getPrimaryPage = function(variationPageId) {
            return pagesFallbacksRestService.getFallbacksForPageId(variationPageId).then(function(fallbacks) {
                return fallbacks[0] ? pagesRestService.getById(fallbacks[0]) : $q.when();
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#isPagePrimary
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Retrieves the variation pages for the current site+catalog+catalogversion.
         *
         * @param {String} primaryPageId The UID of the primary page.
         *
         * @returns {Promise} A promise that resolves an array of variation pages or an empty list if none are found.
         */
        this.getVariationPages = function(primaryPageId) {
            return pagesVariationsRestService.getVariationsForPrimaryPageId(primaryPageId).then(function(variationPageIds) {
                return variationPageIds.length > 0 ? pagesRestService.get({
                    uids: variationPageIds
                }) : $q.when([]);
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#updatePageById
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Updates the page corresponding to the given page UID with the payload provided for the current site+catalog+catalogversion.
         *
         * @param {String} pageUid The UID of the page
         * @param {String} payload The JSON object to PUT to the pages API
         *
         * @returns {Promise} A promise that resolves to the JSON page object as it now exists in the backend
         */
        this.updatePageById = function(pageUid, payload) {
            return pagesRestService.getById(pageUid).then(function(originalPage) {
                delete originalPage.$promise;
                delete originalPage.$resolved;

                payload = lodash.assign({}, originalPage, payload);
                return pagesRestService.update(pageUid, payload);
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#updatePageById
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Retrieves the page corresponding to the given page UID.
         *
         * @param {String} pageUid The UID of the page
         *
         * @returns {Promise} A promise that resolves to the JSON page object
         * corresponding to this page UID
         */
        this.getPageById = function(pageUid) {
            return pagesRestService.getById(pageUid);
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#getCurrentPageInfo
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Retrieves the page information of the page that is currently loaded.
         *
         * @returns {Promise} A promise that resolves to a CMS Item object containing
         * information related to the current page
         */
        this.getCurrentPageInfo = function() {
            return pageInfoService.getPageUUID().then(function(pageUUID) {
                return cmsitemsRestService.getById(pageUUID);
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#getCurrentPageInfo
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Retrieves a version, as identified by the provided version id, of the page information that is currently loaded.
         *
         * @param {String} versionId The ID of the page version to load. 
         * 
         * @returns {Promise} A promise that resolves to a CMS Item object containing
         * information related to the version selected of the current page
         */
        this.getCurrentPageInfoByVersion = function(versionId) {
            return pageInfoService.getPageUUID().then(function(pageUUID) {
                return cmsitemsRestService.getByIdAndVersion(pageUUID, versionId);
            });
        };

        /**
         * @ngdoc method
         * @name pageServiceModule.service:pageService#buildUriContextForCurrentPage
         * @methodOf pageServiceModule.service:pageService
         *
         * @description
         * Returns the uriContext populated with the siteId, catalogId and catalogVersion taken from $routeParams and fallback to the currentExperience
         * Note: From the page list, $routeParams are defined. From the storefront, $routeParams are undefined.
         * 
         * @return {angular.IPromise<IUriContext>} promise resolve to the uri context
         */
        this.buildUriContextForCurrentPage = function() {
            var uriContext = {};

            if ($routeParams.siteId && $routeParams.catalogId && $routeParams.catalogVersion) {
                uriContext[CONTEXT_SITE_ID] = $routeParams.siteId;
                uriContext[CONTEXT_CATALOG] = $routeParams.catalogId;
                uriContext[CONTEXT_CATALOG_VERSION] = $routeParams.catalogVersion;
            }

            if (!lodash.isEmpty(uriContext)) {
                return $q.when(uriContext);
            } else {
                return experienceService.getCurrentExperience().then(function(experience) {
                    uriContext[CONTEXT_SITE_ID] = experience.pageContext.siteId;
                    uriContext[CONTEXT_CATALOG] = experience.pageContext.catalogId;
                    uriContext[CONTEXT_CATALOG_VERSION] = experience.pageContext.catalogVersion;
                    return uriContext;
                });
            }
        };

    });
