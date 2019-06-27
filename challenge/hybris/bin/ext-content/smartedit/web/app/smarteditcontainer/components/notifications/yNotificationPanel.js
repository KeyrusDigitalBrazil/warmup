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
angular.module('yNotificationPanelModule', [
        'smarteditServicesModule',
        'yNotificationModule',
        'yHotkeyNotificationModule',
        'ngAnimate'
    ])
    /*
     * The yNotificationPanel controller is responsible for getting the list of
     * notifications to display and handling showing and hiding the list when the mouse
     * pointer enters and leaves the portion of the screen occupied by the list.
     */
    .controller('YNotificationPanelController', function(notificationService, EVENT_NOTIFICATION_CHANGED, notificationMouseLeaveDetectionService, systemEventService, iframeManagerService, $element, $window, $scope, $timeout) {
        var notificationPanelBounds = null;
        var iFrameNotificationPanelBounds = null;
        var addMouseMoveEventListenerTimeout = null;
        var unRegisterNotificationChangedEventHandler;

        var getIFrame = function() {
            return iframeManagerService.getIframe()[0];
        };

        var getNotificationPanel = function() {
            return $element.find('.y-notification-panel');
        };

        var calculateNotificationPanelBounds = function() {
            var notificationPanel = getNotificationPanel();
            var notificationPanelPosition = notificationPanel.position();

            notificationPanelBounds = {
                x: Math.floor(notificationPanelPosition.left),
                y: Math.floor(notificationPanelPosition.top),
                width: Math.floor(notificationPanel.width()),
                height: Math.floor(notificationPanel.height())
            };
        };

        var calculateIFrameNotificationPanelBounds = function() {
            var iFrame = getIFrame();

            if (iFrame) {
                iFrameNotificationPanelBounds = {
                    x: notificationPanelBounds.x - iFrame.offsetLeft,
                    y: notificationPanelBounds.y - iFrame.offsetTop,
                    width: notificationPanelBounds.width,
                    height: notificationPanelBounds.height
                };
            }
        };

        var calculateBounds = function() {
            calculateNotificationPanelBounds();
            calculateIFrameNotificationPanelBounds();
        };

        var invalidateBounds = function() {
            notificationPanelBounds = null;
            iFrameNotificationPanelBounds = null;
        };

        var hasBounds = function() {
            var hasBounds = !!notificationPanelBounds;
            var hasIFrameBounds = getIFrame() ? !!iFrameNotificationPanelBounds : true;

            return hasBounds && hasIFrameBounds;
        };

        /*
         * Due to the fact that this function is called from a timeout, no digest cycle is
         * triggered. It has to be triggered for the template to be refreshed to make the
         * notification panel re-appear.
         */
        var onMouseLeave = function() {
            $timeout(function() {
                this.isMouseOver = false;
            }.bind(this), 0);
        }.bind(this);

        /*
         * This method stops mouse leave detection across frames and and invalidates the
         * notification panel bounds. If the area was hidden, it is forced to re-appear.
         */
        var cancelDetection = function() {
            invalidateBounds();
            notificationMouseLeaveDetectionService.stopDetection();

            if (this.isMouseOver) {
                onMouseLeave();
            }
        }.bind(this);

        /*
         * This method will clear the coordinates and size of the notification panel previously
         * stored so that they can be re-calculated the next time it is hovered over. If the panel
         * is hidden, it will re-appear.
         */
        var onResize = function() {
            cancelDetection();
        };

        /*
         * This method is triggered when a notification is added, removed from the notification panel.
         * It makes the panel re-appear so that the bounds can be re-calculated to ensure proper mouse detection.
         */
        var onNotificationChanged = function() {
            cancelDetection();
        };

        var addMouseMoveEventListener = function() {
            addMouseMoveEventListenerTimeout = null;

            notificationMouseLeaveDetectionService.startDetection(
                notificationPanelBounds, iFrameNotificationPanelBounds, onMouseLeave);
        };

        this.$onInit = function() {
            this.isMouseOver = false;

            $window.addEventListener('resize', onResize);

            unRegisterNotificationChangedEventHandler = systemEventService.subscribe(
                EVENT_NOTIFICATION_CHANGED, onNotificationChanged);
        };

        this.$onDestroy = function() {
            $window.removeEventListener('resize', onResize);

            notificationMouseLeaveDetectionService.stopDetection();

            unRegisterNotificationChangedEventHandler();
        };

        this.onMouseEnter = function() {
            this.isMouseOver = true;

            if (!hasBounds()) {
                calculateBounds();
            }

            /*
             * We use a small timeout to delay the activation of mouse tracking. If the listener
             * is immediately added, it triggers an event with mouse coordiantes that are outside
             * of the notification panel, thus making it re-appear.
             */
            addMouseMoveEventListenerTimeout = addMouseMoveEventListenerTimeout || $timeout(addMouseMoveEventListener, 10);
        };

        this.getNotifications = function() {
            return notificationService.getNotifications();
        };
    })

    /*
     * The yNotificationPanel component renders the list of notifications that are pushed
     * to the notificationService.
     * 
     * It disappears when the mouse pointer enters it to make it possible to access any
     * elements on the page that may be hidden by the notifications. Once the pointer leaves,
     * the list will re-appear.
     */
    .component('yNotificationPanel', {
        templateUrl: 'yNotificationPanelTemplate.html',
        controller: 'YNotificationPanelController'
    });
