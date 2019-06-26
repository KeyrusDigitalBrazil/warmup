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
 * @name clonePageAlertServiceModule
 *
 * @description
 * This module defines the clonePageAlertService Angular service used
 * in a multi-country context to display an actionable alert whenever a page got
 * successfully cloned to a catalog different than the one of the page being
 * displayed. This alert contains an hyperlink allowing for the user to navigate
 * to the newly cloned page.
 */
angular.module("clonePageAlertServiceModule", [
        "actionableAlertModule",
        "smarteditServicesModule",
        "functionsModule",
        "l10nModule"
    ])

    /**
     * @ngdoc service
     * @name clonePageAlertServiceModule.service:clonePageAlertService
     *
     * @description
     * The clonePageAlertService is used by external modules to display of an
     * actionable alert anytime a page got cloned to a catalog different than the
     * one of the page being displayed. This alert contains an hyperlink allowing
     * for the user to navigate to the newly cloned page.
     */
    .service('clonePageAlertService', function(
        $translate,
        actionableAlertService,
        experienceService,
        catalogService,
        l10nFilter,
        isBlank
    ) {

        /**
         * @ngdoc method
         * @name clonePageAlertServiceModule.service:clonePageAlertService#displayClonePageAlert
         * @methodOf clonePageAlertServiceModule.service:clonePageAlertService
         *
         * @description
         * Method triggering the 'actionableAlertService.displayActionableAlert()'
         * method to display an alert containing an hyperlink allowing for the user
         * to navigate to the newly cloned page.
         *
         * @param {Object} clonedPageInfo A JSON object containing the uid of the
         * newly cloned page.
         * @param {String} clonedPageInfo.uid Uid of the newly cloned page.
         */
        this.displayClonePageAlert = function(clonedPageInfo) {
            return catalogService.getCatalogVersionByUuid(clonedPageInfo.catalogVersion).then(function(catalogVersion) {
                return actionableAlertService.displayActionableAlert({
                    controller: ['experienceService', 'l10nFilter', function(experienceService) {
                        this.description = "se.cms.clonepage.alert.info.description";
                        this.descriptionDetails = {
                            catalogName: l10nFilter(catalogVersion.catalogName),
                            catalogVersion: catalogVersion.version
                        };
                        this.hyperlinkLabel = "se.cms.clonepage.alert.info.hyperlink";

                        this.onClick = function() {
                            if (isBlank(clonedPageInfo.uid)) {
                                throw "clonePageAlertService.checkAndAlertOnClonePage - missing required parameter 'uid'";
                            }

                            experienceService.loadExperience({
                                siteId: catalogVersion.siteId,
                                catalogId: catalogVersion.catalogId,
                                catalogVersion: catalogVersion.version,
                                pageId: clonedPageInfo.uid
                            });
                        };
                    }]
                });
            }.bind(this));
        };
    });
