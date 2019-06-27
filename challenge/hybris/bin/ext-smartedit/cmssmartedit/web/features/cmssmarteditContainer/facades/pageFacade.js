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
 * @name pageFacadeModule
 * @description
 * 
 * This module provides a facade module for pages
 */
angular.module('pageFacadeModule', [
        'yLoDashModule',
        'cmsitemsRestServiceModule',
        'resourceLocationsModule'
    ])

    /**
     * @ngdoc service
     * @name pageFacadeModule.service:pageFacade
     * @requires cmsitemsRestService
     * @description
     * A facade that exposes only the business logic necessary for features that need to work with pages
     */
    .service('pageFacade', function(
        lodash,
        cmsitemsRestService,
        cmsitemsUri,
        crossFrameEventService,
        sharedDataService,
        urlService,
        restServiceFactory,
        catalogService,
        EVENTS,
        CONTEXT_SITE_ID) {

        /**
         * @ngdoc method
         * @name pageFacadeModule.service:pageFacade#contentPageWithLabelExists
         * @methodOf pageFacadeModule.service:pageFacade
         *
         * @description
         * Determines if a ContentPage with a given label exists in the given catalog and catalog version
         *
         * @param {String} label The label to search for
         * @param {String} catalogId The catalog ID to search in for the ContentPage
         * @param {String} catalogVersion The catalog version to search in for the ContentPage
         * @return {Promise} Promise resolving to a boolean determining if the ContentPage exists
         */
        this.contentPageWithLabelExists = function(label, catalogId, catalogVersion) {
            var requestParams = {
                pageSize: 10,
                currentPage: 0,
                typeCode: 'ContentPage',
                itemSearchParams: 'label:' + label,
                catalogId: catalogId,
                catalogVersion: catalogVersion
            };

            return cmsitemsRestService.get(requestParams).then(function(result) {
                return result && !lodash.isEmpty(result.response);
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name pageFacadeModule.service:pageFacade#retrievePageUriContext
         * @methodOf pageFacadeModule.service:pageFacade
         *
         * @description
         * Retrieves the experience and builds a uri context based on its page context
         *
         * @return {Object} the page uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
         */
        this.retrievePageUriContext = function() {
            return sharedDataService.get('experience').then(function(experience) {
                if (!experience) {
                    throw new Error("pageFacade - could not retrieve an experience from sharedDataService");
                }
                if (!experience.pageContext) {
                    return null;
                }
                return urlService.buildUriContext(experience.pageContext.siteId, experience.pageContext.catalogId, experience.pageContext.catalogVersion);
            });
        };

        /**
         * @ngdoc method
         * @name pageFacadeModule.service:pageFacade#createPage
         * @methodOf pageFacadeModule.service:pageFacade
         *
         * @description
         * Creates a new CMS page item
         *
         * @param {Object} page) The object representing the CMS page item to create
         * @return {Promise} If request is successful, it returns a promise that resolves with the CMS page item object. If
         * the request fails, it resolves with errors from the backend.
         */
        this.createPage = function(page) {
            return catalogService.getCatalogVersionUUid().then(function(catalogVersionUUid) {
                page.catalogVersion = page.catalogVersion || catalogVersionUUid;
                if (page.onlyOneRestrictionMustApply === undefined) {
                    page.onlyOneRestrictionMustApply = false;
                }
                return cmsitemsRestService.create(page).then(function(newlyCreatedPage) {
                    crossFrameEventService.publish(EVENTS.PAGE_CREATED, page);
                    return newlyCreatedPage;
                });
            });
        };

        /**
         * @ngdoc method
         * @name pageFacadeModule.service:pageFacade#createPageForSite
         * @methodOf pageFacadeModule.service:pageFacade
         *
         * @description
         * Creates a new CMS page item for a given site.
         *
         * @param {Object} page The object representing the CMS page item to create
         * @param {string} siteUid The uid of the target site.
         * @return {Promise} If request is successful, it returns a promise that resolves with the CMS page item object. If
         * the request fails, it resolves with errors from the backend.
         */
        this.createPageForSite = function(page, siteUid) {
            return catalogService.getCatalogVersionUUid().then(function(catalogVersionUUid) {
                page.catalogVersion = page.catalogVersion || catalogVersionUUid;
                if (page.onlyOneRestrictionMustApply === undefined) {
                    page.onlyOneRestrictionMustApply = false;
                }

                var resource = restServiceFactory.get(cmsitemsUri.replace(CONTEXT_SITE_ID, siteUid));
                return resource.save(page).then(function(newlyCreatedPage) {
                    crossFrameEventService.publish(EVENTS.PAGE_CREATED, page);
                    return newlyCreatedPage;
                });
            });
        };
    });
