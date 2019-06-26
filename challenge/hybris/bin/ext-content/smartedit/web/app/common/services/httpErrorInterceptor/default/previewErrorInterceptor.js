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
angular.module('previewErrorInterceptorModule', ['resourceLocationsModule', 'smarteditServicesModule', 'functionsModule'])
    /**
     * @ngdoc service
     * @name previewErrorInterceptorModule.service:previewErrorInterceptor
     * @description
     * Used for HTTP error code 400 from the Preview API when the pageId is not found in the context. The request will
     * be replayed without the pageId.
     *
     * This can happen in a few different scenarios. For instance, you are on electronics catalog, on some custom page called XYZ.
     * If you use the experience selector and switch to apparel catalog, it will try to create a new preview ticket
     * with apparel catalog and pageId of XYZ. Since XYZ doesn't exist in apparel, it will fail. So we remove the page ID
     * and create a preview for homepage as a default/fallback.
     */
    .factory('previewErrorInterceptor', function($injector, $q, $log, PREVIEW_RESOURCE_URI, sharedDataService, isBlank) {
        return {
            predicate: function(response) {
                return response.status === 400 && response.config.url.indexOf(PREVIEW_RESOURCE_URI) > -1 && !isBlank(response.config.data.pageId) && _hasUnknownIdentifierError(response.data.errors);
            },
            responseError: function(response) {

                $log.info("The error 400 above on preview is expected in some scenarios, typically when switching catalogs from experience selector.");
                $log.info("Removing the pageId [" + response.config.data.pageId + "] and creating a preview for homepage");

                delete response.config.data.pageId;
                sharedDataService.update("experience", function(experience) {
                    delete experience.pageId;
                    return experience;
                });
                $injector.get('iframeManagerService').setCurrentLocation(null);
                return $q.when($injector.get('$http')(response.config));
            }
        };

        function _hasUnknownIdentifierError(errors) {
            var unknownIdentifierErrors = errors.filter(function(error) {
                return error.type === 'UnknownIdentifierError';
            });
            return unknownIdentifierErrors.length ? true : false;
        }
    });
