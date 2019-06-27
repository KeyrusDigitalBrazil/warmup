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
 * @name catalogVersionRestServiceModule
 * @description
 * # The catalogVersionRestServiceModule
 *
 * The catalogVersionRestServiceModule provides REST services for the CMS catalog version endpoint
 *
 */
angular.module('catalogVersionRestServiceModule', ['resourceLocationsModule'])

    /**
     * @ngdoc service
     * @name catalogVersionRestServiceModule.service:catalogVersionRestService
     *
     * @description
     * The catalogVersionRestService provides core REST functionality for the CMS catalog version endpoint
     */
    .factory('catalogVersionRestService', function(restServiceFactory, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var URI = '/cmswebservices/v1/sites/:' + CONTEXT_SITE_ID + '/catalogs/:' + CONTEXT_CATALOG + '/versions/:' + CONTEXT_CATALOG_VERSION;

        return {

            /**
             * @ngdoc method
             * @name catalogVersionRestServiceModule.service:catalogVersionRestService#get
             * @methodOf catalogVersionRestServiceModule.service:catalogVersionRestService
             *
             * @description
             * Fetches catalog information for a given site+catalog+catalogversion
             * deprecated since 6.7, use catalogService.getContentCatalogVersion
             * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
             *
             * @returns {Object} A JSON object representing the current catalog version
             * @deprecated since 6.7
             */
            get: function(uriContext) {
                var rest = restServiceFactory.get(URI);
                return rest.get(uriContext);
            },

            /**
             * @ngdoc method
             * @name catalogVersionRestServiceModule.service:catalogVersionRestService#getCloneableTargets
             * @methodOf catalogVersionRestServiceModule.service:catalogVersionRestService
             *
             * @description
             * Fetches all cloneable target catalog versions for a given site+catalog+catalogversion
             *
             * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
             *
             * @returns {Object} A JSON object with a single field 'versions' containing a list of catalog versions, or an empty list.
             */
            getCloneableTargets: function(uriContext) {
                var rest = restServiceFactory.get(URI + '/targets?mode=cloneableTo');
                return rest.get(uriContext);
            }
        };

    });
