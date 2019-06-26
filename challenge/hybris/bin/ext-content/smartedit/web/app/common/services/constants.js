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
