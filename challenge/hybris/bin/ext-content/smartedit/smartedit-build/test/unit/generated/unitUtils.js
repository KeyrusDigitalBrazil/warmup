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
 * @name seConstantsModule
 * @description
 * The seConstantsModule module contains Smartedit's global constants.
 */
angular.module('seConstantsModule', [])
    /**
     * @ngdoc object
     * @name seConstantsModule.OVERLAY_ID
     * @description
     * the identifier of the overlay placed in front of the storefront to where all smartEdit component decorated clones are copied.
     */
    .constant('OVERLAY_ID', 'smarteditoverlay')
    /**
     * @ngdoc object
     * @name seConstantsModule.COMPONENT_CLASS
     * @description
     * the css class of the smartEdit components as per contract with the storefront
     */
    .constant('COMPONENT_CLASS', 'smartEditComponent')
    /**
     * @ngdoc object
     * @name seConstantsModule.OVERLAY_COMPONENT_CLASS
     * @description
     * the css class of the smartEdit component clones copied to the storefront overlay
     */
    .constant('OVERLAY_COMPONENT_CLASS', 'smartEditComponentX')
    /**
     * @ngdoc object
     * @name seConstantsModule.SMARTEDIT_ATTRIBUTE_PREFIX
     * @description
     * If the storefront needs to expose more attributes than the minimal contract, these attributes must be prefixed with this constant value
     */
    .constant('SMARTEDIT_ATTRIBUTE_PREFIX', 'data-smartedit-')
    /**
     * @ngdoc object
     * @name seConstantsModule.ID_ATTRIBUTE
     * @description
     * the id attribute of the smartEdit components as per contract with the storefront
     */
    .constant('ID_ATTRIBUTE', 'data-smartedit-component-id')
    /**
     * @ngdoc object
     * @name seConstantsModule.UUID_ATTRIBUTE
     * @description
     * the uuid attribute of the smartEdit components as per contract with the storefront
     */
    .constant('UUID_ATTRIBUTE', 'data-smartedit-component-uuid')
    /**
     * @description
     * the front-end randomly generated uuid of the smartEdit components and their equivalent in the overlay
     */
    .constant('ELEMENT_UUID_ATTRIBUTE', 'data-smartedit-element-uuid')
    /**
     * @ngdoc object
     * @name seConstantsModule.UUID_ATTRIBUTE
     * @description
     * the uuid attribute of the smartEdit components as per contract with the storefront
     */
    .constant('CATALOG_VERSION_UUID_ATTRIBUTE', 'data-smartedit-catalog-version-uuid')
    /**
     * @ngdoc object
     * @name seConstantsModule.TYPE_ATTRIBUTE
     * @description
     * the type attribute of the smartEdit components as per contract with the storefront
     */
    .constant('TYPE_ATTRIBUTE', 'data-smartedit-component-type')
    /**
     * @ngdoc object
     * @name seConstantsModule.CONTAINER_ID_ATTRIBUTE
     * @description
     * the id attribute of the smartEdit container, when applicable, as per contract with the storefront
     */
    .constant('CONTAINER_ID_ATTRIBUTE', 'data-smartedit-container-id')
    /**
     * @ngdoc object
     * @name seConstantsModule.CONTAINER_TYPE_ATTRIBUTE
     * @description
     * the type attribute of the smartEdit container, when applicable, as per contract with the storefront
     */
    .constant('CONTAINER_TYPE_ATTRIBUTE', 'data-smartedit-container-type')
    /**
     * @ngdoc object
     * @name seConstantsModule.CONTENT_SLOT_TYPE
     * @description
     * the type value of the smartEdit slots as per contract with the storefront
     */
    .constant('CONTENT_SLOT_TYPE', 'ContentSlot')
    /**
     * @ngdoc object
     * @name seConstantsModule.SMARTEDIT_IFRAME_ID
     * @description
     * the id of the iframe which contains storefront
     */
    .constant('SMARTEDIT_IFRAME_ID', 'ySmartEditFrame')
    .constant('SMARTEDIT_IFRAME_WRAPPER_ID', '#js_iFrameWrapper')
    .constant('SMARTEDIT_IFRAME_DRAG_AREA', 'ySmartEditFrameDragArea')
    .constant('EVENT_TOGGLE_SMARTEDIT_IFRAME_DRAG_AREA', 'EVENT_TOGGLE_SMARTEDIT_IFRAME_DRAG_AREA')
    .constant('SCROLL_AREA_CLASS', 'ySECmsScrollArea')
    .constant('SMARTEDIT_ELEMENT_HOVERED', 'smartedit-element-hovered')
    .constant('SMARTEDIT_DRAG_AND_DROP_EVENTS', {
        DRAG_DROP_CROSS_ORIGIN_START: 'DRAG_DROP_CROSS_ORIGIN_START',
        DRAG_DROP_START: 'EVENT_DRAG_DROP_START',
        DRAG_DROP_END: 'EVENT_DRAG_DROP_END',
        TRACK_MOUSE_POSITION: 'EVENT_TRACK_MOUSE_POSITION',
        DROP_ELEMENT: 'EVENT_DROP_ELEMENT'
    })

    .constant('DATE_CONSTANTS', {
        ANGULAR_FORMAT: 'short',
        MOMENT_FORMAT: 'M/D/YY h:mm A',
        MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
        ISO: 'yyyy-MM-ddTHH:mm:00Z'
    })
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_CONTENT_CATALOG_UPDATE
     * @description
     * The ID of the event that is triggered when the content of a catalog is
     * updated (by page edit or page deletion).
     */
    .constant('EVENT_CONTENT_CATALOG_UPDATE', 'EVENT_CONTENT_CATALOG_UPDATE')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_PERSPECTIVE_CHANGED
     * @description
     * The ID of the event that is triggered when the perspective (known as mode for users) is changed.
     */
    .constant('EVENT_PERSPECTIVE_CHANGED', 'EVENT_PERSPECTIVE_CHANGED')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_PERSPECTIVE_ADDED
     * @description
     * The ID of the event that is triggered when a new perspective (known as mode for users) is registered.
     */
    .constant('EVENT_PERSPECTIVE_ADDED', 'EVENT_PERSPECTIVE_ADDED')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_PERSPECTIVE_UNLOADING
     * @description
     * The ID of the event that is triggered when a perspective is about to be unloaded.
     * This event is triggered immediately before the features are disabled.
     */
    .constant('EVENT_PERSPECTIVE_UNLOADING', 'EVENT_PERSPECTIVE_UNLOADING')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_PERSPECTIVE_REFRESHED
     * @description
     * The ID of the event that is triggered when the perspective (known as mode for users) is refreshed.
     */
    .constant('EVENT_PERSPECTIVE_REFRESHED', 'EVENT_PERSPECTIVE_REFRESHED')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:ALL_PERSPECTIVE
     * @description
     * The key of the default All Perspective.
     */
    .constant('ALL_PERSPECTIVE', 'se.all')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:NONE_PERSPECTIVE
     * @description
     * The key of the default None Perspective.
     */
    .constant('NONE_PERSPECTIVE', 'se.none')
    /**
     * @ngdoc object
     * @name seConstantsModule.object:VALIDATION_MESSAGE_TYPES
     * @description
     * Validation message types
     */
    .constant('VALIDATION_MESSAGE_TYPES', {
        /**
         * @ngdoc property
         * @name seConstantsModule.object:VALIDATION_MESSAGE_TYPES#VALIDATION_ERROR
         * @propertyOf seConstantsModule.object:VALIDATION_MESSAGE_TYPES
         * @description
         * Validation error type.
         */
        VALIDATION_ERROR: 'ValidationError',
        /**
         * @ngdoc property
         * @name seConstantsModule.object:VALIDATION_MESSAGE_TYPES#WARNING
         * @propertyOf seConstantsModule.object:VALIDATION_MESSAGE_TYPES
         * @description
         * Validation warning type.
         */
        WARNING: 'Warning'
    })
    .constant("CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS", {
        PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
        RESTART_PROCESS: 'contractChangeListenerRestartProcess'
    })
    .constant("SMARTEDIT_COMPONENT_PROCESS_STATUS", "smartEditComponentProcessStatus")
    .constant("CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS", {
        PROCESS: "processComponent",
        REMOVE: "removeComponent",
        KEEP_VISIBLE: "keepComponentVisible"
    })
    /**
     * @ngdoc object
     * @name seConstantsModule.object:SORT_DIRECTIONS
     * @description
     * Sort directions
     */
    .constant('SORT_DIRECTIONS', {
        /**
         * @ngdoc property
         * @name seConstantsModule.object:SORT_DIRECTIONS#ASC
         * @propertyOf seConstantsModule.object:SORT_DIRECTIONS
         * @description
         * Sort direction - Ascending
         */
        ASC: 'asc',
        /**
         * @ngdoc property
         * @name seConstantsModule.object:SORT_DIRECTIONS#DESC
         * @propertyOf seConstantsModule.object:SORT_DIRECTIONS
         * @description
         * Sort direction - Descending
         */
        DESC: 'desc'
    })
    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_OUTER_FRAME_CLICKED
     * @description
     * The event that triggeres when user clicks on the outer frame.
     */
    .constant('EVENT_OUTER_FRAME_CLICKED', 'EVENT_OUTER_FRAME_CLICKED')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT
     * @description
     * Name of the event triggered whenever SmartEdit decides to update items in contextual menus.
     */
    .constant('REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT', 'REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:SHOW_TOOLBAR_ITEM_CONTEXT
     * @description
     * The event that is used to show the toolbar item context.
     */
    .constant('SHOW_TOOLBAR_ITEM_CONTEXT', 'SHOW_TOOLBAR_ITEM_CONTEXT')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:HIDE_TOOLBAR_ITEM_CONTEXT
     * @description
     * The event that is used to hide the toolbar item context.
     */
    .constant('HIDE_TOOLBAR_ITEM_CONTEXT', 'HIDE_TOOLBAR_ITEM_CONTEXT')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENT_NOTIFICATION_CHANGED
     * 
     * @description
     * The ID of the event that is triggered when a notification is pushed or removed.
     */
    .constant('EVENT_NOTIFICATION_CHANGED', 'EVENT_NOTIFICATION_CHANGED')
    .constant('OVERLAY_RERENDERED_EVENT', 'overlayRerendered')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:WHOAMI_RESOURCE_URI
     *
     * @description
     * Resource URI of the WhoAmI REST service used to retrieve information on the
     * current logged-in user.
     */
    .constant('WHO_AM_I_RESOURCE_URI', '/authorizationserver/oauth/whoami')
    .constant('PREVIOUS_USERNAME_HASH', 'previousUsername')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:EVENTS
     *
     * @description
     * Events that are fired/handled in the SmartEdit application
     */
    .constant('EVENTS', {
        AUTHORIZATION_SUCCESS: 'AUTHORIZATION_SUCCESS',
        USER_HAS_CHANGED: 'USER_HAS_CHANGED',
        LOGOUT: 'SE_LOGOUT_EVENT',
        CLEAR_PERSPECTIVE_FEATURES: 'CLEAR_PERSPECTIVE_FEATURES',
        EXPERIENCE_UPDATE: 'experienceUpdate',
        PERMISSION_CACHE_CLEANED: 'PERMISSION_CACHE_CLEANED',
        PAGE_CHANGE: 'PAGE_CHANGE',
        PAGE_CREATED: 'PAGE_CREATED_EVENT',
        PAGE_DELETED: 'PAGE_DELETED_EVENT',
        PAGE_SELECTED: 'PAGE_SELECTED_EVENT',
        PAGE_RESTORED: 'PAGE_RESTORED_EVENT'
    })

    .constant('CROSS_FRAME_EVENT', 'CROSS_FRAME_EVENT')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:SELECTED_LANGUAGE
     *
     * @description
     * A constant that is used as key to store the selected language in the storageService
     */
    .constant('SELECTED_LANGUAGE', 'SELECTED_LANGUAGE')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:SWITCH_LANGUAGE_EVENT
     *
     * @description
     * A constant that is used as key to publish and receive events when a language is changed.
     */
    .constant('SWITCH_LANGUAGE_EVENT', 'SWITCH_LANGUAGE_EVENT')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY
     *
     * @description
     * the name of the configuration key containing the list of white listed storefront domain names
     */
    .constant('WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY', 'whiteListedStorefronts')

    /**
     * @ngdoc object
     * @name seConstantsModule.object:TIMEOUT_TO_RETRY_PUBLISHING
     *
     * @description
     * Period between two retries of a {@link smarteditCommonsModule.service:MessageGateway} to publish an event
     * this value must be greater than the time needed by the browser to process a postMessage back and forth across two frames.
     * Internet Explorer is now known to need more than 100ms.
     */
    .constant('TIMEOUT_TO_RETRY_PUBLISHING', 500)

    /**
     * @ngdoc object
     * @name seConstantsModule.object:OPERATION_CONTEXT
     *
     * @description
     * Injectable angular constant<br/>
     * This object provides an enumeration of operation context for the application.
     */
    .constant('OPERATION_CONTEXT', {
        BACKGROUND_TASKS: 'Background Tasks',
        INTERACTIVE: 'Interactive',
        NON_INTERACTIVE: 'Non-Interactive',
        BATCH_OPERATIONS: 'Batch Operations',
        TOOLING: 'Tooling',
        CMS: 'CMS'
    });

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
 * @name yjqueryModule
 * @description
 * This module manages the use of the jQuery library in SmartEdit.
 * It enables smartEdit to work with a "noConflict" version of jQuery in a storefront that may contain another version
 */
angular.module('yjqueryModule', [])
    /**
     * As a configuration step for this module, add the getCssPath method to jquery selectors. This method will return
     * the CSS path of the wrapped JQuery element.
     */
    .run(function(yjQuery) {
        yjQuery.fn.extend({        
            getCssPath: function() {            
                var path;
                var node = this;            
                while (node.length) {                
                    var realNode = node[0];                    
                    var name = realNode.className;                
                    if (realNode.tagName === 'BODY') {                    
                        break;                
                    }                
                    node = node.parent();                
                    path = name + (path ? '>' + path : '');            
                }            
                return path;        
            }    
        });
    })
    /**
     * @ngdoc object
     * @name yjqueryModule.yjQuery
     * @description
     * 
     * Expose a jQuery wrapping factory all the while preserving potentially pre-existing jQuery in storefront and smartEditContainer
     */
    /* forbiddenNameSpaces window.$:false */
    .factory('yjQuery', function() {

        var namespace = "smarteditJQuery";

        if (!window[namespace]) {
            if (window.$ && window.$.noConflict) {
                window[namespace] = window.$.noConflict();
            } else {
                window[namespace] = window.$;
            }
        }
        return window[namespace];
    });

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
     * @name resourceLocationsModule.object:PageUriContext
     *
     * @description
     * A map that contains the necessary site and catalog information for CMS services and directives for a given page.
     * It contains the following keys:
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_SITE_ID PAGE_CONTEXT_SITE_ID} for the site uid,
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_CATALOG PAGE_CONTEXT_CATALOG} for the catalog uid,
     * {@link resourceLocationsModule.object:PAGE_CONTEXT_CATALOG_VERSION PAGE_CONTEXT_CATALOG_VERSION} for the catalog version.
     */

    angular.module('resourceLocationsModule', [])

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
         * @name resourceLocationsModule.object:SMARTEDIT_ROOT
         *
         * @description
         * the name of the webapp root context
         */
        .constant('SMARTEDIT_ROOT', 'smartedit')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SMARTEDIT_RESOURCE_URI_REGEXP
         *
         * @description
         * to calculate platform domain URI, this regular expression will be used
         */
        .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/smartedit/)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONFIGURATION_URI
         *
         * @description
         * the name of the SmartEdit configuration API root
         */
        .constant('CONFIGURATION_URI', '/smartedit/configuration/:key')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CONFIGURATION_COLLECTION_URI
         *
         * @description
         * The SmartEdit configuration collection API root
         */
        .constant('CONFIGURATION_COLLECTION_URI', '/smartedit/configuration')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CMSWEBSERVICES_RESOURCE_URI
         *
         * @description
         * Constant for the cmswebservices API root
         */
        .constant('CMSWEBSERVICES_RESOURCE_URI', '/cmswebservices')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT
         *
         * @description
         * When configuration is not available yet to provide authenticationMap, one needs a default authentication entry point to access configuration API itself
         */
        .constant('DEFAULT_AUTHENTICATION_ENTRY_POINT', '/authorizationserver/oauth/token')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:DEFAULT_AUTHENTICATION_CLIENT_ID
         *
         * @description
         * The default OAuth 2 client id to use during authentication.
         */
        .constant('DEFAULT_AUTHENTICATION_CLIENT_ID', 'smartedit')
        /**
         * Root resource URI of i18n API 
         */
        .constant('I18N_ROOT_RESOURCE_URI', '/smarteditwebservices/v1/i18n')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:I18N_RESOURCE_URI
         *
         * @description
         * Resource URI to fetch the i18n initialization map for a given locale.
         */
        .constant('I18N_RESOURCE_URI', '/smarteditwebservices/v1/i18n/translations')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:I18N_LANGUAGE_RESOURCE_URI
         *
         * @description
         * Resource URI to fetch the supported i18n languages.
         */
        .constant('I18N_LANGUAGES_RESOURCE_URI', '/smarteditwebservices/v1/i18n/languages')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:LANGUAGE_RESOURCE_URI
         *
         * @description
         * Resource URI of the languages REST service.
         */
        .constant('LANGUAGE_RESOURCE_URI', '/cmswebservices/v1/sites/:siteUID/languages')
        .constant('PRODUCT_RESOURCE_API', '/cmssmarteditwebservices/v1/sites/:siteUID/products/:productUID')
        .constant('PRODUCT_LIST_RESOURCE_API', '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products')
        .constant('PRODUCT_CATEGORY_RESOURCE_URI', '/cmssmarteditwebservices/v1/sites/:siteUID/categories/:categoryUID')
        .constant('PRODUCT_CATEGORY_SEARCH_RESOURCE_URI', '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/categories')

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SITES_RESOURCE_URI
         *
         * @description
         * Resource URI of the sites REST service.
         */
        .constant('SITES_RESOURCE_URI', '/cmswebservices/v1/sites')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:LANDING_PAGE_PATH
         *
         * @description
         * Path of the landing page
         */
        .constant('LANDING_PAGE_PATH', '/')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:STORE_FRONT_CONTEXT
         *
         * @description
         * to fetch the store front context for inflection points.
         */
        .constant('STORE_FRONT_CONTEXT', '/storefront')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CATALOGS_PATH
         *
         * @description
         * Path of the catalogs
         */
        .constant('CATALOGS_PATH', '/cmswebservices/v1/catalogs/')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:MEDIA_PATH
         *
         * @description
         * Path of the media
         */
        .constant('MEDIA_PATH', '/cmswebservices/v1/media')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:CMSWEBSERVICES_PATH
         *
         * @description
         * Regular expression identifying CMS related URIs
         */
        .constant('CMSWEBSERVICES_PATH', /\/cmssmarteditwebservices|\/cmswebservices/)
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PREVIEW_RESOURCE_URI
         *
         * @description
         * Path of the preview ticket API
         */
        .constant('PREVIEW_RESOURCE_URI', '/previewwebservices/v1/preview')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:ENUM_RESOURCE_URI
         *
         * @description
         * Path to fetch list of values of a given enum type
         */
        .constant('ENUM_RESOURCE_URI', "/cmswebservices/v1/enums")

        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:PERMISSIONSWEBSERVICES_RESOURCE_URI
         *
         * @description
         * Path to fetch permissions of a given type
         * 
         * @deprecated since 1811
         */
        .constant('USER_GLOBAL_PERMISSIONS_RESOURCE_URI', "/permissionswebservices/v1/permissions/principals/:user/global")
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:SYNC_PATH
         *
         * @description
         * Path of the synchronization service
         */
        .constant('SYNC_PATH', '/cmswebservices/v1/catalogs/:catalog/versions/Staged/synchronizations/versions/Online')
        /**
         * @ngdoc object
         * @name resourceLocationsModule.object:MEDIA_RESOURCE_URI
         *
         * @description
         * Resource URI of the media REST service.
         */
        .constant('MEDIA_RESOURCE_URI', '/cmswebservices/v1/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/media')
        /**
         * @name resourceLocationsModule.object:TYPES_RESOURCE_URI
         *
         * @description
         * Resource URI of the component types REST service.
         */
        .constant('TYPES_RESOURCE_URI', '/cmswebservices/v1/types')

        /**
         * @ngdoc service
         * @name resourceLocationsModule.resourceLocationToRegex
         *
         * @description
         * Generates a regular expresssion matcher from a given resource location URL, replacing predefined keys by wildcard
         * matchers.
         *
         * Example:
         * <pre>
         *     // Get a regex matcher for the someResource endpoint, ie: /\/smarteditwebservices\/someResource\/.*$/g
         *     var endpointRegex = resourceLocationToRegex('/smarteditwebservices/someResource/:id');
         *
         *     // Use the regex to match hits to the mocked HTTP backend. This regex will match for any ID passed in to the
         *     // someResource endpoint.
         *     $httpBackend.whenGET(endpointRegex).respond({someKey: 'someValue'});
         * </pre>
         */
        .factory('resourceLocationToRegex', function() {
            return function(str) {
                return new RegExp(str.replace(/\/:[^\/]*/g, '/.*'));
            };
        });
})();

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
     * @name yLoDashModule
     * @description
     * This module manages the use of the lodash library in SmartEdit. It makes sure the library is introduced
     * in the Angular lifecycle and makes it easy to mock for unit tests.
     */
    angular.module('yLoDashModule', [])
        /**
         * @ngdoc object
         * @name yLoDashModule.lodash
         * @description
         * 
         * Makes the underscore library available to SmartEdit.
         *
         * Note: original _ namespace is removed from window in order not to clash with other libraries especially in the storefront AND to enforce proper dependency injection.
         */
        /* forbiddenNameSpaces window._:false */
        .factory('lodash', function() {
            if (!window.smarteditLodash) {
                if (window._ && window._.noConflict) {
                    window.smarteditLodash = window._.noConflict();
                } else {
                    throw "could not find lodash library under window._ namespace";
                }
            }
            return window.smarteditLodash;
        });
})();

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
angular.module('templateCacheDecoratorModule', [])
    .config(function($provide) {

        var pathRegExp = /web.+\/(\w+)\.html/;
        var namePathMap = {};

        $provide.decorator('$templateCache', ['$delegate', function($delegate) {

            var originalPut = $delegate.put;

            $delegate.put = function() {
                var path = arguments[0];
                var template = arguments[1];
                if (pathRegExp.test(path)) {
                    var fileName = pathRegExp.exec(path)[1] + ".html";
                    if (!namePathMap[fileName]) {

                        originalPut.apply($delegate, [fileName, template]);
                        namePathMap[fileName] = path;
                    } else {
                        throw "[templateCacheDecorator] html templates '" + namePathMap[fileName] + "' and '" + path + "' are conflicting, you must give them different filenames";
                    }
                }
                return originalPut.apply($delegate, arguments);
            };

            // ============== UNCOMMENT THIS TO DEBUG TEMPLATECACHE ==============
            // ========================== DO NOT COMMIT ==========================
            // var originalGet = $delegate.get;
            //
            // $delegate.get = function() {
            //     var path = arguments[0];
            //     var $log = angular.injector(['ng']).get('$log');
            //
            //     $log.debug("$templateCache GET: " + path);
            //     return originalGet.apply($delegate, arguments);
            // };

            return $delegate;
        }]);
    });
