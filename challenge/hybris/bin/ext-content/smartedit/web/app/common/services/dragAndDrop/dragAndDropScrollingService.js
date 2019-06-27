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

    angular.module('_dragAndDropScrollingModule', ['yjqueryModule', 'seConstantsModule', 'functionsModule'])
        .constant('THROTTLE_SCROLLING_DELAY', 75)
        .service('_dragAndDropScrollingService', function($window, $translate, yjQuery, lodash, isPointOverElement, inViewElementObserver, SCROLL_AREA_CLASS, THROTTLE_SCROLLING_DELAY) {

            // Constants
            this._SCROLLING_AREA_HEIGHT = 50;
            this._FAST_SCROLLING_AREA_HEIGHT = 25;

            this._SCROLLING_STEP = 5;
            this._FAST_SCROLLING_STEP = 15;

            var TOP_SCROLL_AREA_ID = 'top_scroll_page';
            var BOTTOM_SCROLL_AREA_ID = 'bottom_scroll_page';

            inViewElementObserver.addSelector('#' + TOP_SCROLL_AREA_ID);
            inViewElementObserver.addSelector('#' + BOTTOM_SCROLL_AREA_ID);

            // Variables
            this._topScrollArea = null;
            this._bottomScrollArea = null;

            this._throttleScrollingEnabled = false;

            this._initialize = function() {
                this._scrollable = this._getSelector(document.scrollingElement || document.documentElement);

                this._addScrollAreas();
                this._addEventListeners();

                this._scrollDelta = 0;
                this._initialized = true;


            };

            this._deactivate = function() {
                this._removeEventListeners();

                this._scrollDelta = 0;
                this._initialized = false;
            };

            this._enable = function() {
                if (this._initialized) {
                    // Calculate limits based on current state.
                    this._scrollLimitY = this._scrollable.get(0).scrollHeight - $window.innerHeight;
                    this._showScrollAreas();
                }
            };

            this._disable = function() {
                if (this._initialized) {
                    var scrollAreas = this._getScrollAreas();
                    // following trigger necessary to remove scrollable areas when loosing track of the mouse from the outer layer
                    scrollAreas.trigger('dragleave');
                    scrollAreas.hide();
                }
            };

            this._addScrollAreas = function() {
                this._topScrollArea = this._getSelector('<div id="' + TOP_SCROLL_AREA_ID + '" class="' + SCROLL_AREA_CLASS + '"></div>').appendTo('body');
                this._bottomScrollArea = this._getSelector('<div id="' + BOTTOM_SCROLL_AREA_ID + '" class="' + SCROLL_AREA_CLASS + '"></div>').appendTo('body');

                var scrollAreas = this._getScrollAreas();
                scrollAreas.height(this._SCROLLING_AREA_HEIGHT);

                this._topScrollArea.css({
                    top: 0
                });
                this._bottomScrollArea.css({
                    bottom: 0
                });

                scrollAreas.hide();

                var topMessage;
                var bottomMessage;
                $translate('se.draganddrop.uihint.top').then(function(localizedTopMessage) {
                    topMessage = localizedTopMessage;
                    return $translate('se.draganddrop.uihint.bottom');
                }).then(function(localizedBottomMsg) {
                    bottomMessage = localizedBottomMsg;

                    this._topScrollArea.text(topMessage);
                    this._bottomScrollArea.text(bottomMessage);
                }.bind(this));
            };

            this._addEventListeners = function() {
                var scrollAreas = this._getScrollAreas();

                scrollAreas.on('dragenter', this._onDragEnter.bind(this));
                scrollAreas.on('dragover', this._onDragOver.bind(this));
                scrollAreas.on('dragleave', this._onDragLeave.bind(this));
            };

            this._removeEventListeners = function() {
                var scrollAreas = this._getScrollAreas();

                scrollAreas.off('dragenter');
                scrollAreas.off('dragover');
                scrollAreas.off('dragleave');

                scrollAreas.remove();
            };

            // Event Listeners
            this._onDragEnter = function(event) {
                var scrollDelta = this._SCROLLING_STEP;
                var scrollArea = this._getSelector(event.target);
                var scrollAreaId = scrollArea.attr('id');
                if (scrollAreaId === TOP_SCROLL_AREA_ID) {
                    scrollDelta *= -1;
                }

                this._scrollDelta = scrollDelta;

                this._animationFrameId = $window.requestAnimationFrame(this._scrollPage.bind(this));
            };

            this._onDragOver = function(evt) {
                var event = evt.originalEvent;
                var scrollArea = this._getSelector(event.target);
                var scrollAreaId = scrollArea.attr('id');

                if (scrollAreaId === TOP_SCROLL_AREA_ID) {
                    if (event.clientY <= this._FAST_SCROLLING_AREA_HEIGHT) {
                        this._scrollDelta = -this._FAST_SCROLLING_STEP;
                    } else {
                        this._scrollDelta = -this._SCROLLING_STEP;
                    }
                } else {
                    var windowHeight = this._getSelector($window).height();

                    if (event.clientY >= windowHeight - this._FAST_SCROLLING_AREA_HEIGHT) {
                        this._scrollDelta = this._FAST_SCROLLING_STEP;
                    } else {
                        this._scrollDelta = this._SCROLLING_STEP;
                    }
                }

            };

            this._onDragLeave = function() {
                this._scrollDelta = 0;
                $window.cancelAnimationFrame(this._animationFrameId);
            };

            this._scrollPage = function() {
                if (this._scrollDelta) {
                    var scrollTop = this._scrollable.scrollTop();
                    var continueScrolling = false;

                    if (this._scrollDelta > 0 && scrollTop < this._scrollLimitY) {
                        continueScrolling = true;
                    } else if (this._scrollDelta < 0 && scrollTop > 0) {
                        continueScrolling = true;
                    }


                    if (continueScrolling) {
                        var current = this._scrollable.scrollTop();
                        var next = current + this._scrollDelta;
                        this._scrollable.scrollTop(next);
                        this._animationFrameId = $window.requestAnimationFrame(this._throttleScrollingEnabled ? this._throttledScrollPage : this._scrollPage.bind(this));
                    }

                    this._showScrollAreas();
                }
            };
            this._throttledScrollPage = lodash.throttle(this._scrollPage.bind(this), THROTTLE_SCROLLING_DELAY);

            this._getSelector = function(selector) {
                return yjQuery(selector);
            };

            this._getScrollAreas = function() {
                return yjQuery(['#' + TOP_SCROLL_AREA_ID, '#' + BOTTOM_SCROLL_AREA_ID].join(','));
            };

            this._showScrollAreas = function() {
                var scrollTop = this._scrollable.scrollTop();

                if (scrollTop === 0) {
                    this._topScrollArea.hide();
                } else {
                    this._topScrollArea.show();
                }

                if (scrollTop >= this._scrollLimitY) {
                    this._bottomScrollArea.hide();
                } else {
                    this._bottomScrollArea.show();
                }
            };


            this.toggleThrottling = function(isEnabled) {
                this._throttleScrollingEnabled = isEnabled;
            };
        });

})();
