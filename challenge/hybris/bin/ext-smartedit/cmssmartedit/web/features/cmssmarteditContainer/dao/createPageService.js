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
 * @name createPageServiceModule
 *
 * @description
 * deprecated since 6.7 - use cmsItemsRestService directly for page creation
 * @deprecated since 6.7 - use cmsItemsRestService directly for page creation
 */
angular.module('createPageServiceModule', ['resourceLocationsModule'])

    /**
     * @ngdoc service
     * @name createPageServiceModule.service:createPageService
     *
     * @description
     * deprecated since 6.7 - use cmsItemsRestService directly for page creation
     * @deprecated since 6.7 - use cmsItemsRestService directly for page creation
     */
    .factory('createPageService', function(restServiceFactory, PAGES_LIST_RESOURCE_URI) {
        var pageRestService = restServiceFactory.get(PAGES_LIST_RESOURCE_URI);

        return {
            /**
             * @ngdoc method
             * @name createPageServiceModule.service:createPageService#createPage
             * @methodOf createPageServiceModule.service:createPageService
             *
             * @description
             * deprecated since 6.7 - use cmsItemsRestService directly for page creation
             * @deprecated since 6.7 - use cmsItemsRestService directly for page creation
             */
            createPage: function(uriContext, page) {
                var payload = angular.extend({}, page, uriContext);
                return pageRestService.save(payload);
            }
        };
    });
