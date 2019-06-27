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
    /**
     * @ngdoc overview
     * @name resourceModule
     *
     * @description
     * The resource module provides $resource factories.
     */
    angular.module('resourceModule', ['smarteditServicesModule', 'resourceLocationsModule', 'cmsResourceLocationsModule', 'functionsModule'])
        /**
         * @ngdoc service
         * @name resourceModule.service:itemsResource
         *
         * @description
         * This service is used to retrieve the $resource factor for retrieving component items.
         */
        .factory('itemsResource', function(restServiceFactory, ITEMS_RESOURCE_URI, URIBuilder) {

            return {
                /**
                 * @ngdoc method
                 * @name resourceModule.service:itemsResource#getItemResourceByContext
                 * @methodOf resourceModule.service:itemsResource
                 * 
                 * @description
                 * Returns  the resource of the custom components REST service by replacing the placeholders with the currently selected catalog version.
                 */
                getItemResource: function() {
                    return restServiceFactory.get(ITEMS_RESOURCE_URI);
                },

                /**
                 * @ngdoc method
                 * @name resourceModule.service:itemsResource#getItemResourceByContext
                 * @methodOf resourceModule.service:itemsResource
                 * 
                 * @description
                 * Returns  the resource of the custom components REST service by providing the current uri context as the input object.
                 * 
                 * The input object contains the necessary site and catalog information to retrieve the items.
                 * 
                 * @param {Object} uriContext A  {@link resourceLocationsModule.object:UriContext uriContext}
                 */
                getItemResourceByContext: function(context) {
                    return restServiceFactory.get(new URIBuilder(ITEMS_RESOURCE_URI).replaceParams(context).build());
                }

            };

        })
        .factory('pagesContentSlotsComponentsResource', function(restServiceFactory, PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI) {
            return restServiceFactory.get(PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI);
        })
        .factory('navigationNodeRestService', function(restServiceFactory, NAVIGATION_MANAGEMENT_RESOURCE_URI) {
            return restServiceFactory.get(NAVIGATION_MANAGEMENT_RESOURCE_URI);
        })
        .factory('synchronizationResource', function(restServiceFactory, URIBuilder, GET_PAGE_SYNCHRONIZATION_RESOURCE_URI, POST_PAGE_SYNCHRONIZATION_RESOURCE_URI) {

            return {

                getPageSynchronizationGetRestService: function(uriContext) {
                    var getURI = new URIBuilder(GET_PAGE_SYNCHRONIZATION_RESOURCE_URI).replaceParams(uriContext).build();
                    return restServiceFactory.get(getURI);
                },

                getPageSynchronizationPostRestService: function(uriContext) {
                    var postURI = new URIBuilder(POST_PAGE_SYNCHRONIZATION_RESOURCE_URI).replaceParams(uriContext).build();
                    return restServiceFactory.get(postURI);
                }
            };

        });
})();
