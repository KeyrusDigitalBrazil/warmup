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
 * @name displayConditionsFacadeModule
 * @description
 * 
 * This module provides a facade module for page display conditions.
 * 
 */
angular.module('displayConditionsFacadeModule', ['pageServiceModule', 'pageRestrictionsServiceModule', 'pageDisplayConditionsServiceModule'])

    /**
     * @ngdoc service
     * @name displayConditionsFacadeModule.service:displayConditionsFacade
     * @description
     * 
     * Service defined to retrieve information related to the display conditions of a given page: 
     * - Whether the page is either of 'primary' or 'variation' display type.
     * - The name of the primary page associated to a variation one.
     * - The name of the display type of a given page ("primary" or "variant").
     * - The description of the display type of a given page ("primary" or "variant").
     * 
     */
    .service('displayConditionsFacade', function(pageService, pageRestrictionsService, pageDisplayConditionsService, $q) {

        this.getPageInfoForPageUid = function(pageId) {
            var pagePromise = pageService.getPageById(pageId);
            var displayConditionsPromise = pageService.isPagePrimary(pageId);

            var allPromises = $q.all([pagePromise, displayConditionsPromise]);
            return allPromises.then(function(values) {
                return {
                    pageName: values[0].name,
                    pageType: values[0].typeCode,
                    isPrimary: values[1]
                };
            });
        };

        this.getVariationsForPageUid = function(primaryPageId) {
            return pageService.getVariationPages(primaryPageId).then(function(variationPages) {
                if (variationPages.length === 0) {
                    return $q.when([]);
                }

                var restrictionsCountsPromise = $q.all(variationPages.map(function(variationPage) {
                    return pageRestrictionsService.getPageRestrictionsCountForPageUID(variationPage.uid);
                }));

                return restrictionsCountsPromise.then(function(restrictionCounts) {
                    return variationPages.map(function(variationPage, index) {
                        return {
                            pageName: variationPage.name,
                            creationDate: variationPage.creationtime,
                            restrictions: restrictionCounts[index]
                        };
                    });
                });
            });
        };

        this.getPrimaryPagesForVariationPageType = function(variationPageType) {
            return pageService.getPrimaryPagesForPageType(variationPageType).then(function(primaryPages) {
                return primaryPages.map(function(primaryPage) {
                    return {
                        uid: primaryPage.uid,
                        uuid: primaryPage.uuid,
                        name: primaryPage.name,
                        label: primaryPage.label
                    };
                });
            });
        };

        this.updatePage = function(pageId, pageData) {
            return pageService.updatePageById(pageId, pageData);
        };

        /**
         * @ngdoc method
         * @name displayConditionsFacadeModule.service:displayConditionsFacade#isPrimaryPage
         * @methodOf displayConditionsFacadeModule.service:displayConditionsFacade
         *
         * @description
         * Check whether the tested page is of type 'primary'.
         *
         * @param {String} The identifier of the tested page
         * @return {Promise} Promise resolving in a boolean indicated whether the tested page is of type 'primary'
         */
        this.isPagePrimary = function(pageId) {
            return pageService.isPagePrimary(pageId);
        };

        /**
         * @ngdoc method
         * @name displayConditionsFacadeModule.service:displayConditionsFacade#getPrimaryPageForVariationPage
         * @methodOf displayConditionsFacadeModule.service:displayConditionsFacade
         *
         * @description
         * Returns data related to the 'primary' page associated with the tested 'variation' page.
         *
         * @param {String} The identifier of the tested 'variation' page
         * @return {Promise} Promise resolving in an object containing uid, name and label of the associated primary page
         */
        this.getPrimaryPageForVariationPage = function(variationPageId) {
            return pageService.getPrimaryPage(variationPageId).then(function(primaryPage) {
                return {
                    uid: primaryPage.uid,
                    name: primaryPage.name,
                    label: primaryPage.label
                };
            });
        };

    });
