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
 * @name landingPageModule
 * @description
 *
 * This module contains the component responsible of displaying the SmartEdit landing page.
 *
 */
angular.module('landingPageModule', ['loadConfigModule', 'smarteditServicesModule', 'smarteditServicesModule', 'resourceLocationsModule', 'seDropdownModule'])
    .service('selectedSiteCookieService', function($cookies) {
        var SELECTED_SITE_COOKIE_NAME = "seselectedsite";

        this.setSelectedSite = function(siteId) {
            $cookies.putObject(SELECTED_SITE_COOKIE_NAME, siteId);
        };

        this.getSelectedSite = function() {
            var selectedSite = $cookies.getObject(SELECTED_SITE_COOKIE_NAME);
            return (typeof selectedSite === "string") ? selectedSite : null;
        };
    })

    .controller(
        'landingPageController',
        function(loadConfigManagerService, siteService, catalogService, SITES_RESOURCE_URI, permissionService, selectedSiteCookieService, systemEventService, EVENTS, LINKED_DROPDOWN) {

            this.sites_id = 'sites-id';

            this.$onInit = function() {
                this.catalogs = [];
                this.qualifier = 'site';
                this.field = {
                    uri: SITES_RESOURCE_URI,
                    idAttribute: "uid",
                    labelAttributes: ["name"],
                    editable: true,
                    paged: false
                };

                this.getCurrentSiteId().then(function(siteId) {
                    this.model = {
                        site: siteId
                    };
                }.bind(this));

                this.removeStorefrontCssClass();
            };

            this.getCurrentSiteId = function() {
                var siteIdFromCookie = selectedSiteCookieService.getSelectedSite();
                return siteService.getAccessibleSites().then(function(sites) {
                    var isSelectedSiteAvailable = sites.some(function(site) {
                        return site.uid === siteIdFromCookie;
                    }.bind(this));
                    if (!isSelectedSiteAvailable) {
                        var firstSiteId = sites.length > 0 ? sites[0].uid : null;
                        return firstSiteId;
                    } else {
                        return siteIdFromCookie;
                    }
                }.bind(this));
            }.bind(this);

            this.removeStorefrontCssClass = function() {
                var bodyTag = angular.element(document.querySelector('body'));
                if (bodyTag.hasClass('is-storefront')) {
                    bodyTag.removeClass('is-storefront');
                }
            };

            this.loadCatalogsBySite = function(siteId) {
                catalogService.getContentCatalogsForSite(siteId).then(function(catalogs) {
                    if (!this.displayedSite || siteId !== this.displayedSite) {
                        this.displayedSite = siteId;
                        this.catalogs = catalogs;
                    }
                }.bind(this));
            }.bind(this);

            this.selectedSiteDropdownEvent = function(eventId, handle) {
                if (handle.optionObject) {
                    var siteId = handle.optionObject.id;
                    selectedSiteCookieService.setSelectedSite(siteId);
                    this.loadCatalogsBySite(siteId);
                } else {
                    this.catalogs = [];
                }
            };

            var unregisterSitesDropdownEvent = systemEventService.subscribe(this.sites_id + LINKED_DROPDOWN, this.selectedSiteDropdownEvent.bind(this));

            this.$onDestroy = function() {
                this.displayedSite = null;
                unregisterSitesDropdownEvent();
            };
        })
    /**
     * @ngdoc directive
     * @name landingPageModule.component:landingPage
     * @scope
     * @restrict E
     * @element landing-page
     *
     * @description
     *
     * Component responsible of displaying the SmartEdit landing page. It retrieves a list of sites, which can be filtered.
     * Once a site is selected, the page will display the catalog(s) associated with it. From there, a user can navigate
     * into the storefront and other areas of the SmartEdit application.
     *
     * */
    .component('landingPage', {
        templateUrl: 'landingPage.html',
        controller: 'landingPageController',
        controllerAs: 'landingCtl',
        bindings: {}
    });
