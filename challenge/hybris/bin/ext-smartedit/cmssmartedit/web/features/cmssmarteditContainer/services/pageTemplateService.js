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
     * @name pageTemplateServiceModule
     * @description
     * # The pageTemplateServiceModule
     *
     * The page template service module provides a service that allows the retrieval of page templates associated to a page type.
     *
     */
    angular.module('pageTemplateServiceModule', ['resourceLocationsModule', 'yLoDashModule'])
        .constant('NON_SUPPORTED_TEMPLATES', [
            "layout/landingLayout1Page",
            "layout/landingLayout3Page",
            "layout/landingLayout4Page",
            "layout/landingLayout5Page",
            "layout/landingLayout6Page",
            "layout/landingLayoutPage",
            "account/accountRegisterPage",
            "checkout/checkoutRegisterPage"
        ])
        /**
         * @ngdoc service
         * @name pageTemplateServiceModule.service:pageTemplateService
         *
         * @description
         * This service allows the retrieval of page templates associated to a page type.
         *
         */
        .factory('pageTemplateService', function(restServiceFactory, PAGE_TEMPLATES_URI, NON_SUPPORTED_TEMPLATES, lodash) {
            var pageTemplateRestService = restServiceFactory.get(PAGE_TEMPLATES_URI);

            return {
                /**
                 * @ngdoc method
                 * @name pageTemplateServiceModule.service:pageTemplateService#getPageTemplatesForType
                 * @methodOf pageTemplateServiceModule.service:pageTemplateService
                 *
                 * @description
                 * When called, this method retrieves the page templates associated to the provided page type.
                 *
                 * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
                 * @param {String} pageType The page type for which to retrieve its associated page templates.
                 *
                 * @returns {Promise} A promise that will resolve with the page templates retrieved for the provided page type.
                 *
                 */
                getPageTemplatesForType: function(uriContext, pageType) {

                    var params = lodash.assign({
                            pageTypeCode: pageType,
                            active: true
                        },
                        uriContext);

                    return pageTemplateRestService.get(params).then(function(pageTemplates) {
                        return {
                            templates: pageTemplates.templates.filter(function(pageTemplate) {
                                return NON_SUPPORTED_TEMPLATES.indexOf(pageTemplate.frontEndName) === -1;
                            })
                        };
                    });
                }
            };
        });
})();
