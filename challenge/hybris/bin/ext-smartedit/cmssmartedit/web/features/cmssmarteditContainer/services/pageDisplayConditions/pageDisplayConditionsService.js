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
 * @name pageDisplayConditionsServiceModule
 * @description
 * # The pageDisplayConditionsServiceModule
 *
 * The pageDisplayConditionsServiceModule provides services for working with page Display Conditions
 *
 */
angular.module('pageDisplayConditionsServiceModule', [])

    /**
     * @ngdoc service
     * @name pageDisplayConditionsServiceModule.service:pageDisplayConditionsService
     *
     * @description
     * The pageDisplayConditionsService provides an abstraction layer for the business logic of
     * primary/variant display conditions of a page
     */
    .service('pageDisplayConditionsService', function(catalogService) {

        /**
         * @ngdoc object
         * @name pageDisplayConditionsServiceModule.object:CONDITION
         *
         * @description
         * An object representing a page display condition<br/>
         * Structure:<br/>
         * ```
         * {
            label: [string] key to be localized to render this condition on a webpage
            description: [string] key to be localized to render this condition description on a webpage
            isPrimary: [boolean]
         * }
         * ```
         */

        function fetchDisplayConditionsForPageType(pageType, uriContext) {
            return catalogService.getContentCatalogVersion(uriContext).then(function(catalogVersion) {
                return catalogVersion.pageDisplayConditions.find(function(condition) {
                    return condition.typecode === pageType;
                });
            });
        }

        function getPageDisplayConditionsByPageType(pageType, uriContext) {
            var conditions = [];

            return fetchDisplayConditionsForPageType(pageType, uriContext).then(function(obj) {
                if (!obj || !obj.options) {
                    return [];
                }
                obj.options.forEach(function(option) {
                    var displayCondition = {
                        label: option.label,
                        description: option.label + '.description',
                        isPrimary: option.id === 'PRIMARY'
                    };

                    conditions.push(displayCondition);
                });

                return conditions;
            });
        }


        /**
         * @ngdoc method
         * @name pageDisplayConditionsServiceModule.service:pageDisplayConditionsService#getNewPageConditions
         * @methodOf pageDisplayConditionsServiceModule.service:pageDisplayConditionsService
         *
         * @param {String} pageTypeCode The page typeCode of a potential new page
         * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
         *
         * @returns {Array} An array of {@link pageDisplayConditionsServiceModule.object:CONDITION page conditions} that are the
         * possible conditions if you wish to create a new page of the given pagetype that has the given possible primary
         * pages
         */
        this.getNewPageConditions = function(pageTypeCode, uriContext) {
            return getPageDisplayConditionsByPageType(pageTypeCode, uriContext);
        };

    });
