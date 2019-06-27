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
     * @name renderServiceInterfaceModule
     * @description
     * # The renderServiceInterfaceModule
     *
     * The render service interface module provides an abstract extensible
     * {@link renderServiceInterfaceModule.service:RenderServiceInterface renderService} . It is designed to
     * re-render components after an update component data operation has been performed, according to how
     * the Accelerator displays the component.
     *
     */
    angular.module('renderServiceInterfaceModule', ['smarteditServicesModule', 'seConstantsModule', 'functionsModule', 'yjqueryModule'])
        .constant("KEY_CODES", {
            ESC: 27
        })
        .constant("MOUSE_AND_KEYBOARD_EVENTS", {
            KEY_DOWN: 'keydown',
            KEY_UP: 'keyup',
            CLICK: 'click'
        })
        .constant('OVERLAY_DISABLED', 'OVERLAY_DISABLED')
        .constant('MODAL_DIALOG_ID', 'y-modal-dialog')
        /**
         * @ngdoc service
         * @name renderServiceInterfaceModule.service:RenderServiceInterface
         * @description
         * Designed to re-render components after an update component data operation has been performed, according to
         * how the Accelerator displays the component.
         *
         * This class serves as an interface and should be extended, not instantiated.
         *
         */
        .factory('RenderServiceInterface', createRenderServiceInterface);

    function createRenderServiceInterface($document, $window, $q, yjQuery, KEY_CODES, MOUSE_AND_KEYBOARD_EVENTS, OVERLAY_DISABLED, MODAL_DIALOG_ID, NONE_PERSPECTIVE, EVENT_OUTER_FRAME_CLICKED, systemEventService, notificationService, pageInfoService, perspectiveService, crossFrameEventService, isIframe) {

        var HOTKEY_NOTIFICATION_ID = 'HOTKEY_NOTIFICATION_ID';
        var HOTKEY_NOTIFICATION_TEMPLATE_URL = 'perspectiveSelectorHotkeyNotificationTemplate.html';

        var HOTKEY_NOTIFICATION_CONFIGURATION = {
            id: HOTKEY_NOTIFICATION_ID,
            templateUrl: HOTKEY_NOTIFICATION_TEMPLATE_URL
        };

        function RenderServiceInterface() {
            // Bind to document events
            this._bindEvents();
        }

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#renderSlots
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * Re-renders a slot in the page.
         *
         * @param {Promise} promise a promise
         */
        RenderServiceInterface.prototype.renderSlots = function() {};

        // Proxied Functions
        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#renderComponent
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * Re-renders a component in the page.
         *
         * @param {String} componentId The ID of the component.
         * @param {String} componentType The type of the component.
         * @param {String} customContent The custom content to replace the component content with. If specified, the
         * component content will be rendered with it, instead of the accelerator's. Optional.
         *
         * @returns {Promise} Promise that will resolve on render success or reject if there's an error. When rejected,
         * the promise returns an Object{message, stack}.
         */
        RenderServiceInterface.prototype.renderComponent = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#renderRemoval
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * This method removes a component from a slot in the current page. Note that the component is only removed
         * on the frontend; the operation does not propagate to the backend.
         *
         * @param {String} componentId The ID of the component to remove.
         * @param {String} componentType The type of the component.
         *
         * @returns {Object} Object wrapping the removed component.
         */
        RenderServiceInterface.prototype.renderRemoval = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#renderPage
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * Re-renders all components in the page.
         * this method first resets the HTML content all of components to the values saved by {@link decoratorServiceModule.service:decoratorService#methods_storePrecompiledComponent} at the last $compile time
         * then requires a new compilation.
         */
        RenderServiceInterface.prototype.renderPage = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#toggleOverlay
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * Toggles on/off the visibility of the page overlay (containing the decorators).
         *
         * @param {Boolean} showOverlay Flag that indicates if the overlay must be displayed.
         */
        RenderServiceInterface.prototype.toggleOverlay = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#refreshOverlayDimensions
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * This method updates the position of the decorators in the overlay. Normally, this method must be executed every
         * time the original storefront content is updated to keep the decorators correctly positioned.
         *
         */
        RenderServiceInterface.prototype.refreshOverlayDimensions = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#blockRendering
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * Toggles the rendering to be blocked or not which determines whether the overlay should be rendered or not.
         *
         *@param {Boolean} isBlocked Flag that indicates if the rendering should be blocked or not.
         */
        RenderServiceInterface.prototype.blockRendering = function() {};

        /**
         * @ngdoc method
         * @name renderServiceInterfaceModule.service:RenderServiceInterface#isRenderingBlocked
         * @methodOf renderServiceInterfaceModule.service:RenderServiceInterface
         *
         * @description
         * This method returns a boolean that determines whether the rendering is blocked or not.
         *
         *@param {Boolean} An indicator if the rendering is blocked or not.
         */
        RenderServiceInterface.prototype.isRenderingBlocked = function() {};

        RenderServiceInterface.prototype._bindEvents = function() {

            $document.on(MOUSE_AND_KEYBOARD_EVENTS.KEY_UP, function(event) {
                this._shouldEnableKeyPressEvent(event).then(function(shouldEnableKeyPressEvent) {
                    if (shouldEnableKeyPressEvent) {
                        this._keyPressEvent();
                    }
                }.bind(this));
            }.bind(this));

            $document.on(MOUSE_AND_KEYBOARD_EVENTS.CLICK, function() {
                this._clickEvent();
            }.bind(this));

        };

        RenderServiceInterface.prototype._keyPressEvent = function() {
            this.isRenderingBlocked().then(function(isBlocked) {
                if (this._areAllModalWindowsClosed()) {
                    if (!isBlocked) {
                        this.blockRendering(true);
                        this.renderPage(false);
                        notificationService.pushNotification(HOTKEY_NOTIFICATION_CONFIGURATION);
                        systemEventService.publishAsync(OVERLAY_DISABLED);
                    } else {
                        this.blockRendering(false);
                        this.renderPage(true);
                        notificationService.removeNotification(HOTKEY_NOTIFICATION_ID);
                    }
                }
            }.bind(this));
        };

        RenderServiceInterface.prototype._clickEvent = function() {
            if (!isIframe()) {
                crossFrameEventService.publish(EVENT_OUTER_FRAME_CLICKED);
            }

            this.isRenderingBlocked().then(function(isBlocked) {
                if (isBlocked && !isIframe()) {
                    this.blockRendering(false);
                    this.renderPage(true);
                    notificationService.removeNotification(HOTKEY_NOTIFICATION_ID);
                }
            }.bind(this));
        };

        RenderServiceInterface.prototype._areAllModalWindowsClosed = function() {
            return yjQuery('[id="' + MODAL_DIALOG_ID + '"]').length === 0;
        };

        RenderServiceInterface.prototype._shouldEnableKeyPressEvent = function(event) {
            try {
                return pageInfoService.getPageUUID().then(function(pageUUID) {
                    if (pageUUID) {
                        return perspectiveService.isHotkeyEnabledForActivePerspective().then(function(isHotkeyEnabled) {
                            return ((event.which === KEY_CODES.ESC) && isHotkeyEnabled);
                        });
                    }
                });
            } catch (e) {
                return $q.when(false);
            }
            return $q.when(false);
        };

        return RenderServiceInterface;
    }
})();
