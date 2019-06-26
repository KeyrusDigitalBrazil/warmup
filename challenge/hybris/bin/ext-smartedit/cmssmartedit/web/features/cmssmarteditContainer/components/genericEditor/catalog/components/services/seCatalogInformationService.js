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
 * @name catalogInformationServiceModule
 * @description
 *
 * The catalogInformationServiceModule contains a service with helper methods used by catalog aware selector components.
 *
 */
angular.module('catalogInformationServiceModule', ['smarteditServicesModule', 'productCategoryServiceModule'])
    /**
     * @ngdoc service
     * @name catalogInformationServiceModule.service:seCatalogInformationService
     *
     * @description
     * This service contains helper methods used by catalog aware components.
     *
     */
    .service('seCatalogInformationService', function($q, lodash, catalogService, productService, productCategoryService, sharedDataService) {

        /**
         * @ngdoc method
         * @name catalogInformationServiceModule.service:seCatalogInformationService#getProductCatalogsInformation
         * @methodOf catalogInformationServiceModule.service:seCatalogInformationService
         *
         * @description
         *
         * This method retrieves the information of the product catalogs available in the current site.
         *
         * @returns {Promise} A promise that resolves to an array containing the information of all the product catalogs available in the current site.
         *
         */
        this.getProductCatalogsInformation = function() {
            return this._getSiteUID().then(function(siteUID) {
                if (this._cachedSiteUID === siteUID && this._parsedCatalogs) {
                    // Return the cached catalogs only if the site hasn't changed
                    // otherwise it's necessary to reload them. 
                    return this._parsedCatalogs;
                } else {
                    this._cachedSiteUID = siteUID;
                    return catalogService.getProductCatalogsForSite(siteUID).then(function(catalogs) {
                        var result = {};

                        catalogs.map(function(catalog) {
                            result[catalog.catalogId] = {
                                id: catalog.catalogId,
                                name: catalog.name,
                                versions: catalog.versions.map(function(catalogVersion) {
                                    return {
                                        id: catalogVersion.version,
                                        label: catalogVersion.version
                                    };
                                })
                            };
                        });
                        this._parsedCatalogs = lodash.values(result);

                        return this._parsedCatalogs;
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this);

        /**
         * @ngdoc object
         * @name catalogInformationServiceModule.object:productsFetchStrategy
         * @description
         * This object contains the strategy necessary to display products in a paged way; it contains a method to retrieve pages of products and another method to
         * retrieve individual products. Such a strategy is necessary to work with products in a ySelect .
         */
        this.productsFetchStrategy = {
            fetchPage: function(catalogInfo, _mask, _pageSize, _currentPage) {
                return this._getSiteUID().then(function(siteUID) {
                    catalogInfo.siteUID = siteUID;
                    return productService.findProducts(catalogInfo, {
                        mask: _mask,
                        pageSize: _pageSize,
                        currentPage: _currentPage
                    });
                }).then(function(result) {
                    result.products.map(function(rawProduct) {
                        rawProduct.id = rawProduct.uid;
                    }.bind(this));

                    return {
                        pagination: result.pagination,
                        results: result.products
                    };
                }.bind(this));
            }.bind(this),
            fetchEntity: function(productUID) {
                return this._getSiteUID().then(function(siteUID) {
                    return productService.getProductById(siteUID, productUID);
                }.bind(this)).then(function(product) {
                    // ySelect requires an id, while other parts of the selector rely on the UID. 
                    product.id = product.uid;

                    return product;
                }.bind(this));
            }.bind(this)
        };

        /**
         * @ngdoc object
         * @name catalogInformationServiceModule.object:categoriesFetchStrategy
         * @description
         * This object contains the strategy necessary to display categories in a paged way; it contains a method to retrieve pages of categories and another method to
         * retrieve individual categories. Such a strategy is necessary to work with categories in a ySelect.
         */
        this.categoriesFetchStrategy = {
            fetchPage: function(catalogInfo, mask, pageSize, currentPage) {
                return this._getSiteUID().then(function(siteUID) {
                    catalogInfo.siteUID = siteUID;
                    return productCategoryService.getCategories(catalogInfo, mask, pageSize, currentPage);
                }).then(function(result) {
                    result.productCategories.map(function(rawCategory) {
                        // ySelect requires an id, while other parts of the selector rely on the UID. 
                        rawCategory.id = rawCategory.uid;
                    }.bind(this));

                    return {
                        pagination: result.pagination,
                        results: result.productCategories
                    };
                });
            }.bind(this),
            fetchEntity: function(categoryUID) {
                return this._getSiteUID().then(function(siteUID) {
                    return productCategoryService.getCategoryById(siteUID, categoryUID);
                }).then(function(category) {
                    category.id = category.uid;
                    return category;
                });
            }.bind(this)
        };

        // Helper Methods
        this._getSiteUID = function() {
            return sharedDataService.get('experience').then(function(currentExperience) {
                return currentExperience.siteDescriptor.uid;
            });
        };
    });
