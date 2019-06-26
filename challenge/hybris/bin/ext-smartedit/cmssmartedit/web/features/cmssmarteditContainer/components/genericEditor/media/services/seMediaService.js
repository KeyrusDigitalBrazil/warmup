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
 * @name seMediaServiceModule
 * @description
 * The media service module provides a service to create an image file for a catalog through AJAX calls. This module
 * uses a dedicated transformed $resource that posts a multipart form data in the request.
 */
angular.module('seMediaServiceModule', ['resourceLocationsModule', 'ngResource'])

    /**
     * @ngdoc service
     * @name seMediaServiceModule.seMediaResource
     * @description
     * A {@link https://docs.angularjs.org/api/ngResource/service/$resource $resource} that makes REST calls to the default
     * CMS catalog media API. It supports HTTP GET and POST methods against this API. The GET method is used to retrieve a collection of media.
     *
     * The POST methods transform the POJO into a {@link https://developer.mozilla.org/en/docs/Web/API/FormData FormData}
     * object before the request is made to the API. This transformation is required for file uploads that use the
     * Content-Type 'multipart/form-data'.
     * @deprecated since 6.4, use mediaService
     */
    .factory('seMediaResource', function($resource, MEDIA_RESOURCE_URI) {
        return $resource(MEDIA_RESOURCE_URI, {}, {
            get: {
                method: 'GET',
                cache: true,
                headers: {}
            },
            save: {
                method: 'POST',
                headers: {
                    'Content-Type': undefined,
                    enctype: 'multipart/form-data',
                    'x-requested-with': 'Angular'
                },
                transformRequest: function(data) {
                    var formData = new FormData();
                    angular.forEach(data, function(value, key) {
                        formData.append(key, value);
                    });
                    return formData;
                }
            }
        });
    })

    /**
     * @ngdoc service
     * @name seMediaServiceModule.seMediaResourceService
     * @description
     * This service provides an interface to the {@link https://docs.angularjs.org/api/ngResource/service/$resource $resource} that makes REST 
     * calls to the default CMS catalog media API. It supports HTTP GET method returning against this API. 
     * The GET method is used to retrieve a single media.
     * @deprecated since 6.4, use mediaService
     */
    .factory('seMediaResourceService', function($resource, MEDIA_RESOURCE_URI) {

        var getMediaByCode = function(mediaCode) {
            return $resource(MEDIA_RESOURCE_URI + "/" + mediaCode, {}, {
                get: {
                    method: 'GET',
                    cache: true,
                    headers: {}
                }
            });
        };

        return {
            getMediaByCode: getMediaByCode
        };
    })

    /**
     * @ngdoc service
     * @name seMediaServiceModule.seMediaService
     * @description
     * This service provides an interface to the {@link https://docs.angularjs.org/api/ngResource/service/$resource
     * $resource} provided by the {@link seMediaServiceModule.seMediaResource seMediaResource} service and 
     * the {@link seMediaServiceModule.seMediaResourceService seMediaResourceService} service. It provides the
     * functionality to upload images and to fetch images by code for a specific catalog-catalog version combination.
     */
    .factory('seMediaService', function(seMediaResource, seMediaResourceService) {
        /**
         * @ngdoc method
         * @name seMediaServiceModule.seMediaService.uploadMedia
         * @methodOf seMediaServiceModule.seMediaService
         *
         * @description
         * Uploads the media to the catalog.
         *
         * @param {Object} media The media to be uploaded
         * @param {String} media.code A unique code identifier for the media
         * @param {String} media.description A description of the media
         * @param {String} media.altText An alternate text to be shown for the media
         * @param {File} media.file The {@link https://developer.mozilla.org/en/docs/Web/API/File File} object to be
         * uploaded.
         *
         * @returns {Promise} If request is successful, it returns a promise that resolves with the media POJO. If the
         * request fails, it resolves with errors from the backend.
         */
        var uploadMedia = function(media) {
            return seMediaResource.save(media).$promise;
        };

        /**
         * @ngdoc method
         * @name seMediaServiceModule.seMediaService.getMediaByCode
         * @methodOf seMediaServiceModule.seMediaService
         *
         * @description
         * Fetches the media for the selected catalog corresponding to the specified code.
         * Deprecated since 6.4, use mediaService.getMedia
         *
         * @param {String} code A unique code identifier that corresponds to the media as it exists in the backend.
         *
         * @returns {Promise} If request is successful, it returns a promise that resolves with the media POJO. If the
         * request fails, it resolves with errors from the backend.
         * 
         * @deprecated since 6.4, use mediaService.getMedia
         */
        var getMediaByCode = function(code) {
            return seMediaResourceService.getMediaByCode(code).get().$promise;
        };

        return {
            uploadMedia: uploadMedia,
            getMediaByCode: getMediaByCode
        };
    });
