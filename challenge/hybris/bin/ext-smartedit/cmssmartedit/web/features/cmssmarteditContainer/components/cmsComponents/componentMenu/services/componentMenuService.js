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
angular.module('componentMenuServiceModule', ['smarteditServicesModule', 'cmsDragAndDropServiceModule', 'yLoDashModule'])
    /**
     * This service provides functionality to the component menu; it's meant to be used internally. Thus, no ng-docs are added.
     */
    .service('componentMenuService', function($timeout, storageService, catalogService, experienceService, cmsDragAndDropService, lodash) {

        // --------------------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------------------
        var SELECTED_CATALOG_VERSION_COOKIE_NAME = "se_catalogmenu_catalogversion_cookie";

        // --------------------------------------------------------------------------------------------------
        // Methods
        // --------------------------------------------------------------------------------------------------
        this.hasMultipleContentCatalogs = function() {
            return this.getContentCatalogs().then(function(contentCatalogs) {
                return contentCatalogs.length > 1;
            });
        };

        /**
         * This method is used to retrieve the content catalogs of the site in the page context. 
         */
        this.getContentCatalogs = function() {
            return this._getPageContext().then(function(pageContext) {
                return pageContext ? catalogService.getContentCatalogsForSite(pageContext.siteId) : [];
            }.bind(this));
        }.bind(this);

        /**
         * Gets the list of catalog/catalog versions where components can be retrieved from for this page. 
         */
        this.getValidContentCatalogVersions = function() {
            return this._getPageContext().then(function(pageContext) {
                return this.getContentCatalogs().then(function(contentCatalogs) {
                    // Return 'active' catalog versions for content catalogs, except for the 
                    // catalog in the current experience. 
                    var result = contentCatalogs.map(function(catalog) {
                        return this._getActiveOrCurrentVersionForCatalog(pageContext, catalog);
                    }.bind(this));

                    return result;
                }.bind(this));
            }.bind(this));
        }.bind(this);

        /**
         * Gets the list of catalog/catalog versions where components can be retrieved from for this page. 
         */
        this._getActiveOrCurrentVersionForCatalog = function(pageContext, catalog) {
            var catalogVersion = catalog.versions.filter(function(catalogVersion) {
                if (pageContext.catalogId === catalog.catalogId) {
                    return pageContext.catalogVersion === catalogVersion.version;
                }
                return catalogVersion.active;
            })[0];

            return {
                isCurrentCatalog: pageContext.catalogVersion === catalogVersion.version,
                catalogName: catalog.name,
                catalogId: catalog.catalogId,
                catalogVersionId: catalogVersion.version,
                id: catalogVersion.uuid
            };
        };

        this._getPageContext = function() {
            return experienceService.getCurrentExperience().then(function(experience) {
                return experience.pageContext;
            });
        };

        this.refreshDragAndDrop = function() {
            $timeout(function() {
                cmsDragAndDropService.update();
            }, 0);
        };

        // --------------------------------------------------------------------------------------------------
        // Cookie Management Methods
        // --------------------------------------------------------------------------------------------------
        this.getInitialCatalogVersion = function(catalogVersions) {
            return storageService.getValueFromCookie(SELECTED_CATALOG_VERSION_COOKIE_NAME).then(function(rawValue) {
                var selectedCatalogVersionId = (typeof rawValue === 'string') ? rawValue : null;

                var selectedCatalogVersion = catalogVersions.filter(function(catalogVersion) {
                    return catalogVersion.id === selectedCatalogVersionId;
                }.bind(this))[0];

                return (selectedCatalogVersion) ? selectedCatalogVersion : lodash.last(catalogVersions);
            });
        };

        this.persistCatalogVersion = function(catalogVersionId) {
            storageService._putValueInCookie(SELECTED_CATALOG_VERSION_COOKIE_NAME, catalogVersionId);
        };
    });
