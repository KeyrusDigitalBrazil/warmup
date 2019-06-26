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
angular.module('pagesFallbacksRestServiceModule', ['resourceLocationsModule', 'yLoDashModule'])
    .service('pagesFallbacksRestService', function(
        restServiceFactory,
        lodash,
        CONTEXT_SITE_ID,
        CONTEXT_CATALOG,
        CONTEXT_CATALOG_VERSION,
        PAGE_CONTEXT_SITE_ID,
        PAGE_CONTEXT_CATALOG,
        PAGE_CONTEXT_CATALOG_VERSION) {

        var PAGE_FALLBACKS_URI = '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pages/:pageId/fallbacks';

        this.getFallbacksForPageId = function(pageId) {
            this._resource = this._resource || restServiceFactory.get(PAGE_FALLBACKS_URI);
            var extendedParams = {
                pageId: pageId
            };
            return this._resource.get(extendedParams).then(function(response) {
                return response.uids;
            });
        };

        this.getFallbacksForPageIdAndContext = function(pageId, uriContext) {
            var extendedParams = {
                pageId: pageId
            };
            var uri = '/cmswebservices/v1/sites/' + uriContext[CONTEXT_SITE_ID] + '/catalogs/' + uriContext[CONTEXT_CATALOG] + '/versions/' + uriContext[CONTEXT_CATALOG_VERSION] + '/pages/:pageId/fallbacks';
            var res = restServiceFactory.get(uri);
            return res.get(extendedParams).then(function(response) {
                return response.uids;
            });
        };
    });
