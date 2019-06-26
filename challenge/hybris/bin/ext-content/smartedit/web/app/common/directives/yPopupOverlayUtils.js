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
 * @name yPopupOverlayUtilsModule
 * @description
 * This module provides utility services for the {@link yPopupOverlayModule}
 */
angular.module('yPopupOverlayUtilsModule', [])

    /**
     * @ngdoc service
     * @name yPopupOverlayUtilsModule.service:yPopupOverlayUtilsClickOrderService
     *
     * @description
     * A service that manages the click handlers for all {@link yPopupOverlayModule.directive:yPopupOverlay yPopupOverlay} overlay DOM elements.
     * When the user clicks outside of an overlay, the overlay should close, but in the case of multiple overlays
     * the click handlers are registered in the reverse order that want to execute them.<br />
     * <br />
     * So this service keeps a stack of all displayed popup overlays, and delegates clicks only to the top of the stack.
     */
    .service('yPopupOverlayUtilsClickOrderService', function($document, $log) {

        var controllerRegistry = [];

        function clickHandler($event) {
            if (controllerRegistry.length > 0) {
                controllerRegistry[0].onBodyElementClicked($event);
            }
        }

        this.register = function(instance) {
            var index = controllerRegistry.indexOf(instance);
            if (index === -1) {
                if (controllerRegistry.length === 0) {
                    angular.element($document[0].body).on('click', clickHandler);
                }
                controllerRegistry.unshift(instance);
            } else {
                $log.warn('yPopupOverlayUtilsClickOrderService.onHide() - instance already registered');
            }
        };

        this.unregister = function(instance) {
            var index = controllerRegistry.indexOf(instance);
            if (index !== -1) {
                controllerRegistry.splice(index, 1);
            }
            if (controllerRegistry.length === 0) {
                angular.element($document[0].body).off('click', clickHandler);
            }
        };


    })

    /**
     * @ngdoc service
     * @name yPopupOverlayUtilsModule.service:yPopupOverlayUtilsDOMCalculations
     *
     * @description
     * Contains some {@link yPopupOverlayModule.directive:yPopupOverlay yPopupOverlay} helper functions for
     * calculating positions and sizes on the DOM
     */
    .service('yPopupOverlayUtilsDOMCalculations', function($window, $document) {

        function getScrollBarWidth() {
            if ($document[0].body.scrollHeight > $document[0].body.clientHeight) {
                var inner = $document[0].createElement('p');
                inner.style.width = "100%";
                inner.style.height = "200px";

                var outer = $document[0].createElement('div');
                outer.style.position = "absolute";
                outer.style.top = "0px";
                outer.style.left = "0px";
                outer.style.visibility = "hidden";
                outer.style.width = "200px";
                outer.style.height = "150px";
                outer.style.overflow = "hidden";
                outer.appendChild(inner);

                $document[0].body.appendChild(outer);
                var w1 = inner.offsetWidth;
                outer.style.overflow = 'scroll';
                var w2 = inner.offsetWidth;
                if (w1 === w2) {
                    w2 = outer.clientWidth;
                }
                $document[0].body.removeChild(outer);
                return (w1 - w2);
            } else {
                return 0;
            }
        }

        /**
         * @ngdoc method
         * @name yPopupOverlayUtilsModule.service:yPopupOverlayUtilsDOMCalculations#calculatePreferredPosition
         * @methodOf yPopupOverlayUtilsModule.service:yPopupOverlayUtilsDOMCalculations
         *
         * @description
         * Calculates the preferred position of the overlay, based on the size and position of the anchor
         * and the size of the overlay element
         *
         * @param {Object} anchorBoundingClientRect A bounding rectangle representing the overlay's anchor
         * @param {number} anchorBoundingClientRect.top The top of the anchor, absolutely positioned
         * @param {number} anchorBoundingClientRect.right The right of the anchor, absolutely positioned
         * @param {number} anchorBoundingClientRect.bottom The bottom of the anchor, absolutely positioned
         * @param {number} anchorBoundingClientRect.left The left of the anchor, absolutely positioned
         * @param {number} targetWidth The width of the overlay element
         * @param {number} targetHeight The height of the overlay element
         * @param {string =} [targetValign='bottom'] The preferred vertical alignment, either 'top' or 'bottom'
         * @param {string =} [targetHalign='right'] The preferred horizontal alignment, either 'left' or 'right'
         *
         * @returns {Object} A new size and position for the overlay
         */
        this.calculatePreferredPosition = function(anchorBoundingClientRect, targetWidth, targetHeight, targetValign, targetHalign) {

            var scrollX = $window.pageXOffset;
            var scrollY = $window.pageYOffset;

            var position = {
                width: targetWidth,
                height: targetHeight
            };

            switch (targetValign) {
                case 'top':
                    position.top = anchorBoundingClientRect.top + scrollY - targetHeight;
                    break;

                case 'bottom':
                    /* falls through */
                default:
                    position.top = anchorBoundingClientRect.bottom + scrollY;
            }

            switch (targetHalign) {
                case 'left':
                    position.left = anchorBoundingClientRect.right + scrollX - targetWidth;
                    break;

                case 'right':
                    /* falls through */
                default:
                    position.left = anchorBoundingClientRect.left + scrollX;
            }
            return position;
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayUtilsModule.service:yPopupOverlayUtilsDOMCalculations#adjustHorizontalToBeInViewport
         * @methodOf yPopupOverlayUtilsModule.service:yPopupOverlayUtilsDOMCalculations
         *
         * @description
         * Modifies the input rectangle to be absolutely positioned horizontally in the viewport.<br />
         * Does not modify vertical positioning.
         *
         * @param {Object} absPosition A rectangle object representing the size and absolutely positioned location of the overlay
         * @param {number} absPosition.left The left side of the overlay element
         * @param {number} absPosition.width The width of the overlay element
         */
        this.adjustHorizontalToBeInViewport = function(absPosition) {

            // HORIZONTAL POSITION / SIZE
            // if width of popup is wider then viewport, set it full width
            if (absPosition.width >= $window.innerWidth) {
                absPosition.left = 0;
                absPosition.width = $window.innerWidth;
            } else {
                var scrollWidth = getScrollBarWidth(); // maybe replace this with proper calculated value but im not sure if its worth the cpu cost
                // var scrollWidth = getScrollBarWidth();
                // if right edge of popup would be off the viewport on the right, then
                // move it left until right edge of popup is on right side of viewport
                if (absPosition.left - $window.pageXOffset + absPosition.width >= $window.innerWidth - scrollWidth) {
                    absPosition.left = $window.innerWidth - absPosition.width - scrollWidth;
                }
                // if left edge is off the viewport to left, move to left edge
                if (absPosition.left - $window.pageXOffset <= 0) {
                    absPosition.left = $window.pageXOffset;
                }
            }

        };

    });
