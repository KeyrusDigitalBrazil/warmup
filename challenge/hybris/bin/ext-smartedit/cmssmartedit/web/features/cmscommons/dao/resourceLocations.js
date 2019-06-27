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

    var CONTEXT_CATALOG = 'CURRENT_CONTEXT_CATALOG';
    var CONTEXT_CATALOG_VERSION = 'CURRENT_CONTEXT_CATALOG_VERSION';
    var CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';

    var PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
    var PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
    var PAGE_CONTEXT_SITE_ID = 'CURRENT_PAGE_CONTEXT_SITE_ID';

    angular.module('cmsResourceLocationsModule', [])

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_SITE_ID
         *
         * @description
         * Constant containing the name of the site uid placeholder in URLs
         */
        .constant('CONTEXT_SITE_ID', CONTEXT_SITE_ID)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_CATALOG
         *
         * @description
         * Constant containing the name of the catalog uid placeholder in URLs
         */
        .constant('CONTEXT_CATALOG', CONTEXT_CATALOG)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTEXT_CATALOG_VERSION
         *
         * @description
         * Constant containing the name of the catalog version placeholder in URLs
         */
        .constant('CONTEXT_CATALOG_VERSION', CONTEXT_CATALOG_VERSION)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_SITE_ID
         *
         * @description
         * Constant containing the name of the current page site uid placeholder in URLs
         */
        .constant('PAGE_CONTEXT_SITE_ID', PAGE_CONTEXT_SITE_ID)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG
         *
         * @description
         * Constant containing the name of the current page catalog uid placeholder in URLs
         */
        .constant('PAGE_CONTEXT_CATALOG', PAGE_CONTEXT_CATALOG)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG_VERSION
         *
         * @description
         * Constant containing the name of the current page catalog version placeholder in URLs
         */
        .constant('PAGE_CONTEXT_CATALOG_VERSION', PAGE_CONTEXT_CATALOG_VERSION)

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:UriContext
         *
         * @description
         * A map that contains the necessary site and catalog information for CMS services and directives.
         * It contains the following keys:
         * {@link resourceLocationsModule.object:CONTEXT_SITE_ID CONTEXT_SITE_ID} for the site uid,
         * {@link resourceLocationsModule.object:CONTEXT_CATALOG CONTEXT_CATALOG} for the catalog uid,
         * {@link resourceLocationsModule.object:CONTEXT_CATALOG_VERSION CONTEXT_CATALOG_VERSION} for the catalog version.
         */

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:TYPES_RESOURCE_URI
         *
         * @description
         * Resource URI of the component types REST service.
         */
        .constant('TYPES_RESOURCE_URI', '/cmswebservices/v1/types')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:ITEMS_RESOURCE_URI
         *
         * @description
         * Resource URI of the custom components REST service.
         */
        .constant('ITEMS_RESOURCE_URI', '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI
         *
         * @description
         * Resource URI of the pages content slot component REST service.
         */
        .constant('PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI', '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pagescontentslotscomponents')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI
         *
         * @description
         * Resource URI of the content slot type restrictions REST service.
         */
        .constant('CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI', '/cmswebservices/v1/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pages/:pageUid/contentslots/:slotUid/typerestrictions')

        /**
         * @ngdoc object
         * @name resourceLocationsMod`ule.object:PAGES_LIST_RESOURCE_URI
         *
         * @description
         * Resource URI of the pages REST service.
         */
        .constant('PAGES_LIST_RESOURCE_URI', '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/pages')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_LIST_PATH
         *
         * @description
         * Path of the page list
         */
        .constant('PAGE_LIST_PATH', '/pages/:siteId/:catalogId/:catalogVersion')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:TRASHED_PAGE_LIST_PATH
         *
         * @description
         * Path of the page list
         */
        .constant('TRASHED_PAGE_LIST_PATH', '/trashedpages/:siteId/:catalogId/:catalogVersion')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGES_CONTENT_SLOT_RESOURCE_URI
         *
         * @description
         * Resource URI of the page content slots REST service
         */
        .constant('PAGES_CONTENT_SLOT_RESOURCE_URI', '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pagescontentslots')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PAGE_TEMPLATES_URI
         *
         * @description
         * Resource URI of the page templates REST service
         */
        .constant('PAGE_TEMPLATES_URI', '/cmswebservices/v1/sites/:' + CONTEXT_SITE_ID + '/catalogs/:' + CONTEXT_CATALOG + '/versions/:' + CONTEXT_CATALOG_VERSION + '/pagetemplates')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_PAGE_PATH
         *
         * @description
         * Path to the Navigation Management
         */
        .constant('NAVIGATION_MANAGEMENT_PAGE_PATH', '/navigations/:siteId/:catalogId/:catalogVersion')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_RESOURCE_URI
         *
         * @description
         * Resource URI of the navigations REST service.
         */
        .constant('NAVIGATION_MANAGEMENT_RESOURCE_URI', '/cmswebservices/v1/sites/:' + CONTEXT_SITE_ID + '/catalogs/:' + CONTEXT_CATALOG + '/versions/:' + CONTEXT_CATALOG_VERSION + '/navigations')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_ENTRIES_RESOURCE_URI
         *
         * @description
         * Resource URI of the navigations REST service.
         */
        .constant('NAVIGATION_MANAGEMENT_ENTRIES_RESOURCE_URI', '/cmswebservices/v1/sites/:' + CONTEXT_SITE_ID + '/catalogs/:' + CONTEXT_CATALOG + '/versions/:' + CONTEXT_CATALOG_VERSION + '/navigations/:navigationUid/entries')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_ENTRY_TYPES_RESOURCE_URI
         *
         * @description
         * Resource URI of the navigation entry types REST service.
         */
        .constant('NAVIGATION_MANAGEMENT_ENTRY_TYPES_RESOURCE_URI', '/cmswebservices/v1/navigationentrytypes')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.CONTEXTUAL_PAGES_RESOURCE_URI
         * @deprecated since 6.5
         *
         * @description
         * Resource URI of the pages REST service, with placeholders to be replaced by the currently selected catalog version.
         */
        .constant('CONTEXTUAL_PAGES_RESOURCE_URI', '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/pages')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI
         *
         * @description
         * Resource URI of the pages restrictions REST service, with placeholders to be replaced by the currently selected catalog version.
         */
        .constant('CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI', '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pagesrestrictions')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.PAGES_RESTRICTIONS_RESOURCE_URI
         *
         * @description
         * Resource URI of the pages restrictions REST service, with placeholders to be replaced by the currently selected catalog version.
         */
        .constant('PAGES_RESTRICTIONS_RESOURCE_URI', '/cmswebservices/v1/sites/:siteUID/catalogs/:catalogId/versions/:catalogVersion/pagesrestrictions')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.UPDATE_PAGES_RESTRICTIONS_RESOURCE_URI
         * @deprecated since 6.5
         *
         * @description
         * Resource URI of the pages restrictions REST service, with placeholders to be replaced by the currently selected catalog version.
         */
        .constant('UPDATE_PAGES_RESTRICTIONS_RESOURCE_URI', '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/pagesrestrictions/pages')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.RESTRICTION_TYPES_URI
         *
         * @description
         * Resource URI of the restriction types REST service.
         */
        .constant('RESTRICTION_TYPES_URI', '/cmswebservices/v1/restrictiontypes')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.RESTRICTION_TYPES_URI
         *
         * @description
         * Resource URI of the pageTypes-restrictionTypes relationship REST service.
         */
        .constant('PAGE_TYPES_RESTRICTION_TYPES_URI', '/cmswebservices/v1/pagetypesrestrictiontypes')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.PAGEINFO_RESOURCE_URI
         * @deprecated since 6.5
         *
         * @description
         * Resource URI of the page info REST service.
         */
        .constant('PAGEINFO_RESOURCE_URI', '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/pages/:pageUid')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.PAGE_TYPES_URI
         *
         * @description
         * Resource URI of the page types REST service.
         */
        .constant('PAGE_TYPES_URI', '/cmswebservices/v1/pagetypes')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:GET_PAGE_SYNCHRONIZATION_RESOURCE_URI
         *
         * @description
         * Resource URI to retrieve the full synchronization status of page related items
         */
        .constant('GET_PAGE_SYNCHRONIZATION_RESOURCE_URI', '/cmssmarteditwebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/synchronizations/versions/:target/pages/:pageUid')


        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:POST_PAGE_SYNCHRONIZATION_RESOURCE_URI
         *
         * @description
         * Resource URI to perform synchronization of page related items
         */
        .constant('POST_PAGE_SYNCHRONIZATION_RESOURCE_URI', '/cmssmarteditwebservices/v1/sites/' + CONTEXT_SITE_ID + '/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/synchronizations/versions/:target');
})();
