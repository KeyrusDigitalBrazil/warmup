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
angular.module("mediaServiceModule", ['resourceModule', 'resourceLocationsModule', 'functionsModule'])
    /**
     * @ngdoc service
     * @name mediaServiceModule.service:mediaService
     * @description
     * Service to deal with media related CRUD operations
     */
    .service("mediaService", function(restServiceFactory, MEDIA_PATH, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, isBlank) {

        this.uriParameters = {};


        /*
         * @ngdoc method
         * @name mediaServiceModule.service:mediaService#getPage
         * @methodOf mediaServiceModule.service:mediaService
         *
         * Fetch paged search results by making a REST call to the appropriate item end point.
         * Must return a Page of type Page as per SmartEdit documentation
         * @param {String} mask for filtering the search
         * @param {String} pageSize number of items in the page
         * @param {String} currentPage current page number
         * @param {Object} parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        this.getPage = function(mask, pageSize, currentPage, parameters) {

            this.uriParameters = parameters || {};

            var payload = {
                catalogId: this.uriParameters[CONTEXT_CATALOG] || CONTEXT_CATALOG,
                catalogVersion: this.uriParameters[CONTEXT_CATALOG_VERSION] || CONTEXT_CATALOG_VERSION
            };
            if (!isBlank(mask)) {
                payload.code = mask;
            }

            var subParams = Object.keys(payload).reduce(function(accumulator, next) {
                accumulator += "," + next + ":" + payload[next];
                return accumulator;
            }, "").substring(1);

            var params = {
                namedQuery: "namedQueryMediaSearchByCodeCatalogVersion",
                params: subParams,
                pageSize: pageSize,
                currentPage: currentPage
            };
            return restServiceFactory.get(MEDIA_PATH).get(params).then(function(response) {
                response.results = response.media.map(function(media) {
                    return {
                        id: media.uuid,
                        code: media.code,
                        description: media.description,
                        altText: media.altText,
                        url: media.url,
                        downloadUrl: media.downloadUrl
                    };
                });
                delete response.media;
                return response;
            });

        };

        /*
         * @ngdoc method
         * @name mediaServiceModule.service:mediaService#getMedia
         * @methodOf mediaServiceModule.service:mediaService
         *
         * @description
         * This method fetches a Media by its UUID
         * @param {String} uuid the universal uid the unique identifier of a media (contains catalog information)
         */
        this.getMedia = function(uuid, parameters) {
            this.uriParameters = parameters;
            //identifier is added to URI and not getByid argument because it contains slashes
            return restServiceFactory.get(MEDIA_PATH + "/" + uuid).get().then(function(media) {
                return {
                    id: media.uuid,
                    code: media.code,
                    description: media.description,
                    altText: media.altText,
                    url: media.url,
                    downloadUrl: media.downloadUrl
                };
            });
        };
    });
