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
 * @name yPopupOverlayModule
 * @description
 * This module provides the yPopupOverlay directive, and it's helper services
 */
angular.module('yPopupOverlayModule', [
        'yPopupOverlayUtilsModule',
        'yjqueryModule',
        'yLoDashModule',
        'smarteditCommonsModule',
        'functionsModule',
        'seConstantsModule'
    ])

    /**
     * A string, representing the prefix of the generated UUID for each yPopupOverlay.
     * This uuid is added as an attribute to the overlay DOM element.
     */
    .constant('yPopupOverlayUuidPrefix', 'ypo-uuid-_')

    /**
     * @ngdoc service
     * @name yPopupOverlayModule.service:yPopupEngine
     * @description
     * Service that positions a template relative to an anchor element.
     */
    .service('yPopupEngineService', function($document, $window, $compile, $timeout, lodash) {

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#contructor
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Initializes the popup engine with an anchor, template, and angular scope, and optionally a configuration object.
         *
         * @param {HTMLElement} anchor An anchor element that is used to position the template.
         * @param {String} template  A HTML template.
         * @param {Object} $scope The scope of the anchor.
         * @param {Object=} config The configuration object. See the {@link yPopupOverlayModule.service:yPopupEngine#configure configure}
         * method for more information.
         */
        function yPopupEngine(anchor, template, $scope, config) {
            this.anchorElement = anchor;
            this.template = template;

            this.scope = $scope;

            this.isOpen = false;
            this.oldTrigger = null;
            this.eventListeners = [];

            this.configure(config);
        }

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#configure
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Configures the popup engine.
         *
         * @param {Object=} config The configuration object.
         * @param {String=} [config.placement='bottom'] The placement of the popup, see {@link https://popper.js.org/popper-documentation.html#Popper.Defaults.placement options}.
         * @param {String=} [config.trigger='hover'] For the trigger, see {@link yPopupOverlayModule.service:yPopupEngine#setTrigger setTrigger} method for the available triggers.
         * @param {Function=} [config.onChanges] Called when a change occurs on the popup's position or creation.
         * @param {Function=} [config.onShow] Called when the popup is created.
         * @param {Function=} [config.onHide] Called when the popup is hidden.
         * @param {(String|HTMLElement)=} [config.container=document.body] The parent element that contains the popup. It can be a CSS selector or a HTMLElement.
         * @param {String=} [config.onClickOutside='close'] 'close'|'none'. setting to none will not affect the popup when the user clicks outside of the element.
         * @param {Object=} [config.modifiers] Modifiers provided by the popper library, see the {@link https://popper.js.org/popper-documentation.html#Popper.Defaults.modifiers popper} documentation.
         *
         */
        yPopupEngine.prototype.configure = function(config) {
            config = config || {};

            this.onChanges = config.onChanges;
            this.container = config.container || $document[0].body;
            this.onShow = config.onShow;
            this.onHide = config.onHide;
            this.disposing = false;

            var vm = this;

            this.config = {
                placement: config.placement || 'bottom',
                modifiers: config.modifiers,
                trigger: config.trigger || 'hover',
                onClickOutside: config.onClickOutside || 'close',
                onCreate: vm._onChanges.bind(vm),
                onUpdate: vm._onChanges.bind(vm)
            };

            this.setTrigger(this.config.trigger);
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#show
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Explicitly shows the popup.
         */
        yPopupEngine.prototype.show = function() {
            if (this.isOpen || this.disposing) {
                return;
            }

            this.isOpen = true;

            this.popupScope = this.scope.$new(false);
            this.popupElement = $compile(this.template)(this.popupScope)[0];

            // FIXME: CMSX-6084
            this.popupInstance = new $window.Popper(this.anchorElement,
                this.popupElement, lodash.merge(this.config, yPopupEngine.popperConfig));

            if (typeof this.container === 'string') {
                angular.element(this.container)[0].appendChild(this.popupElement);
            } else {
                this.container.appendChild(this.popupElement);
            }

            yPopupEngine.erd.listenTo(this.popupElement, function() {
                this.update();
            }.bind(this));

            if (this.onShow) {
                this.onShow();
            }

            this.update();
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#hide
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Explicitly hides the popup by removing it from the DOM.
         */
        yPopupEngine.prototype.hide = function() {
            if (!this.isOpen) {
                return;
            }

            yPopupEngine.erd.uninstall(this.popupElement);

            this.popupScope.$destroy();
            this.popupInstance.destroy();
            this.popupElement.parentNode.removeChild(this.popupElement);

            if (this.onHide) {
                this.onHide();
            }

            this.isOpen = false;
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#update
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Updates the position of the popup.
         */
        yPopupEngine.prototype.update = function() {
            if (this.isOpen) {
                this.popupInstance.scheduleUpdate();
            }
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#setTrigger
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Configures the anchor's trigger type.
         *
         * @param {String} newTrigger The trigger type: 'click', 'hover', or 'focus'.
         */
        yPopupEngine.prototype.setTrigger = function(newTrigger) {
            if (this.oldTrigger === newTrigger) {
                return;
            }
            this.config.trigger = newTrigger;
            this.oldTrigger = newTrigger;
            this._removeTriggers();

            var events = [];

            var handleShow = function($event) {
                $event.stopPropagation();
                $event.preventDefault();
                this.show();
            }.bind(this);
            var handleHide = function($event) {
                $event.stopPropagation();
                $event.preventDefault();
                this.hide();
            }.bind(this);
            switch (newTrigger) {
                case 'click':
                    events.push({
                        event: 'click',
                        handle: function() {
                            if (this.isOpen) {
                                this.hide();
                            } else {
                                this.show();
                            }
                        }.bind(this)
                    });
                    break;
                case 'hover':
                    events.push({
                        event: 'mouseenter',
                        handle: handleShow
                    });
                    events.push({
                        event: 'mouseleave',
                        handle: handleHide
                    });
                    break;
                case 'focus':
                    events.push({
                        event: 'focus',
                        handle: handleShow
                    });
                    events.push({
                        event: 'blur',
                        handle: handleHide
                    });
                    break;
                case 'show':
                case 'true':
                case true:
                    this.show();
                    return;
                case 'hide':
                case 'false':
                case false:
                    this.hide();
                    return;
            }

            events.forEach(function(event) {
                this.anchorElement.addEventListener(event.event, function($event) {
                    $timeout(function() {
                        event.handle($event);
                    });
                });
                this.eventListeners.push(function() {
                    this.anchorElement.removeEventListener(event.event, event.handle);
                }.bind(this));
            }.bind(this));

            if (this.config.onClickOutside === 'close' && this.config.trigger === 'click') {
                var bodyClick = function($event) {
                    if ($event.target !== this.anchorElement && !this.anchorElement.contains($event.target)) {
                        this.hide();
                    }
                }.bind(this);
                $document[0].body.addEventListener('click', function($event) {
                    $timeout(function() {
                        bodyClick($event);
                    });
                });
                this.eventListeners.push(function() {
                    $document[0].body.removeEventListener('click', bodyClick);
                });
            }
        };

        /**
         * @ngdoc method
         * @name yPopupOverlayModule.service:yPopupEngine#dispose
         * @methodOf yPopupOverlayModule.service:yPopupEngine
         *
         * @description
         * Removes the popup from the DOM and unregisters all events from the anchor.
         */
        yPopupEngine.prototype.dispose = function() {
            this.disposing = true;
            this.hide();
            this._removeTriggers();
        };

        /**
         * Removes event listeners from the anchor element.
         * @private
         */
        yPopupEngine.prototype._removeTriggers = function() {
            this.eventListeners.forEach(function(unRegisterEvent) {
                unRegisterEvent();
            });
            this.eventListeners = [];
        };

        /**
         * A function to be passed into the popper library and triggered when a popup is created or when there are any changes to an existing popup.
         * @param dataObject - see {@link https://popper.js.org/popper-documentation.html#dataObject dataObject} the popper documentation.
         * @private
         */
        yPopupEngine.prototype._onChanges = function(dataObject) {
            if (this.onChanges) {
                this.onChanges(this.popupElement, dataObject);
            }
        };

        // Default configurations for the popper library.
        yPopupEngine.popperConfig = {
            modifiers: {
                preventOverflow: {
                    padding: 0,
                    boundariesElement: 'viewport'
                }
            }
        };

        // Event resize detector maker.
        yPopupEngine.erd = $window.elementResizeDetectorMaker({
            strategy: "scroll"
        });

        return yPopupEngine;
    })


    /**
     *  @ngdoc directive
     *  @name yPopupOverlayModule.directive:yPopupOverlay
     *  @restrict A
     *
     *  @description
     *  The yPopupOverlay is meant to be a directive that allows popups/overlays to be displayed attached to any element.
     *  The element that the directive is applied to is called the anchor element. Once the popup is displayed, it is
     *  positioned relative to the anchor, depending on the configuration provided.<br />
     *  <br />
     *  <h3>Scrolling Limitation</h3>
     *  In this initial implementation, it appends the popup element to the body, and positions itself relative to body.
     *  This means that it can handle default window/body scrolling, but if the anchor is contained within an inner
     *  scrollable DOM element then the positions will not work correctly.
     *
     *  @param {< Object} yPopupOverlay A popup overlay configuration object that must contain either a template or a templateUrl
     *  @param {string} yPopupOverlay.template|templateUrl An html string template or a url to an html file
     *  @param {string =} [yPopupOverlay.halign='right'] Aligns the popup horizontally
     *      relative to the anchor (element). Accepts values: 'left' or 'right'.
     *  @param {string =} [yPopupOverlay.valign='bottom'] Aligns the popup vertically
     *      relative to the anchor (element). Accepts values: 'top' or 'bottom'.
     *  @param {@ string =} yPopupOverlayTrigger 'true'|'false'|'click' Controls when the overlay is displayed.
     *      If yPopupOverlayTrigger is true, the overlay is displayed, if false (or something other then true or click)
     *      then the overlay is hidden.
     *      If yPopupOverlayTrigger is 'click' then the overlay is displayed when the anchor (element) is clicked on
     *  @param {& expression =} yPopupOverlayOnShow An angular expression executed whenever this overlay is displayed
     *  @param {& expression =} yPopupOverlayOnHide An angular expression executed whenever this overlay is hidden
     */
    .directive('yPopupOverlay', function() {
        return {
            restrict: 'A',
            scope: false,
            controllerAs: '$yPopupCtrl',
            controller: 'yPopupOverlayController'
        };
    })

    .controller('yPopupOverlayController', function(
        $scope,
        $element,
        $document,
        $compile,
        $attrs,
        $timeout,
        $interpolate,
        yjQuery,
        lodash,
        isIframe,
        yPopupOverlayUuidPrefix,
        yPopupOverlayUtilsDOMCalculations,
        yPopupOverlayUtilsClickOrderService,
        OVERLAY_ID) {

        /**
         * Check if a yjQuery element contains a child element.
         * @param parentElement
         * @param childElement Click event target
         * @returns {boolean|*} True if parent contains child
         */
        function isChildOfElement(parentElement, childElement) {
            return parentElement[0] === childElement || yjQuery.contains(parentElement[0], childElement);
        }

        /**
         * Namespace to protect the non-isolated scope for this directive
         */
        function SafeController() {

            /**
             * Calculates the size of the popup content and stores it.
             * Returns true if the size has changed since the previous call.
             */
            this.checkPopupSizeChanged = function() {
                if (this.popupElement) {
                    var firstChildOfRootPopupElement = this.popupElement.children().first();
                    var popupBounds = firstChildOfRootPopupElement[0].getBoundingClientRect();
                    var changed = popupBounds.width !== this.popupSize.width || popupBounds.height !== this.popupSize.height;
                    this.popupSize = {
                        width: popupBounds.width,
                        height: popupBounds.height
                    };
                    if (changed) {
                        this.updatePopupElementPositionAndSize();
                    }
                }
                return false;
            }.bind(this);

            /**
             *
             */
            this.updatePopupElementPositionAndSize = function() {
                if (this.popupElement) {
                    try {
                        // Always calculate based on first child of popup, but apply css to root of popup
                        // otherwise any applied css may harm the content by enforcing size
                        var anchorBounds = $element[0].getBoundingClientRect();
                        var position = yPopupOverlayUtilsDOMCalculations.calculatePreferredPosition(anchorBounds,
                            this.popupSize.width, this.popupSize.height, this.config.valign, this.config.halign);
                        yPopupOverlayUtilsDOMCalculations.adjustHorizontalToBeInViewport(position);
                        this.popupElement.css(position);
                    } catch (e) {
                        // There are racing conditions where some of the elements are not ready yet...
                        // Since we're constantly recalculating, this is just an easy way to avoid all these conditions
                    }
                }
            }.bind(this);

            this.togglePoppup = function($event) {
                $event.stopPropagation();
                $event.preventDefault();
                if (this.popupDisplayed) {
                    this.hide();
                } else {
                    this.show();
                }
            }.bind(this);

            this.getTemplateString = function() {
                var outerElement = yjQuery('<div>');
                outerElement.attr('data-uuid', this.uuid);
                outerElement.addClass('y-popover-outer');

                var innerElement;
                if (this.config.template) {
                    innerElement = yjQuery('<div>');
                    innerElement.html(this.config.template);
                } else if (this.config.templateUrl) {
                    innerElement = yjQuery('<data-ng-include>');
                    innerElement.attr('src', "'" + this.config.templateUrl + "'");
                } else {
                    throw "yPositiongetTemplateString() - Missing template";
                }

                innerElement.addClass('y-popover-inner');
                outerElement.append(innerElement);

                return outerElement[0].outerHTML;
            }.bind(this);


            this.hide = function() {
                if (this.popupDisplayed) {
                    yPopupOverlayUtilsClickOrderService.unregister(this);
                    if (this.popupElementScope) {
                        this.popupElementScope.$destroy();
                        this.popupElementScope = null;
                    }
                    if (this.popupElement) {
                        this.popupElement.remove();
                        this.popupElement = null;
                    }
                    if ($attrs.yPopupOverlayOnHide) {
                        // We want to evaluate this angular expression inside of a digest cycle
                        $timeout(function() {
                            $scope.$eval($attrs.yPopupOverlayOnHide);
                        });
                    }
                    this.resetPopupSize();
                }
                this.popupDisplayed = false;
            }.bind(this);

            this.show = function() {
                if (!this.popupDisplayed) {
                    this.popupElement = this.getTemplateString();
                    this.popupElementScope = $scope.$new(false);
                    this.popupElement = $compile(this.popupElement)(this.popupElementScope);
                    var containerElement = isIframe() ? yjQuery('#' + OVERLAY_ID) : yjQuery('body');
                    this.updatePopupElementPositionAndSize();
                    this.popupElement.appendTo(containerElement);
                    angular.element(function() {
                        this.updatePopupElementPositionAndSize();
                        yPopupOverlayUtilsClickOrderService.register(this);
                        if ($attrs.yPopupOverlayOnShow) {
                            // We want to evaluate this angular expression inside of a digest cycle
                            $timeout(function() {
                                $scope.$eval($attrs.yPopupOverlayOnShow);
                            });
                        }

                    }.bind(this));
                }
                this.popupDisplayed = true;
            }.bind(this);

            this.updateTriggers = function(newValue) {
                if (typeof newValue === 'undefined') {
                    newValue = 'click';
                }
                if (this.oldTrigger === newValue) {
                    return;
                }
                this.oldTrigger = newValue;
                if (this.untrigger) {
                    this.untrigger();
                }
                if (newValue === 'click') {
                    angular.element($element).on('click', this.togglePoppup);
                    this.untrigger = function() {
                        angular.element($element).off('click', this.togglePoppup);
                    };
                    return;
                }
                if (newValue === "true" || newValue === true) {
                    this.show();
                } else {
                    this.hide();
                }
            }.bind(this);

            /**
             * Handles click event, triggered by the
             */
            this.onBodyElementClicked = function($event) {
                if (this.popupElement) {
                    var isPopupClicked = isChildOfElement(this.popupElement, $event.target);
                    var isAnchorClicked = isChildOfElement($element, $event.target);
                    if (!isPopupClicked && !isAnchorClicked) {
                        this.hide();
                        $event.stopPropagation();
                        $event.preventDefault();
                        return true;
                    }
                }
                return false;
            }.bind(this);

            this.resetPopupSize = function() {
                this.popupSize = {
                    width: 0,
                    height: 0
                };
            }.bind(this);

            this.$doCheck = function() {
                if (this.active) {
                    this.checkPopupSizeChanged();
                    var trigger = $interpolate($attrs.yPopupOverlayTrigger)($scope);
                    if (trigger !== this.doCheckTrigger) {
                        this.doCheckTrigger = trigger;
                        this.updateTriggers(trigger);
                    }
                }
            }.bind(this);

            this.$onInit = function() {
                this.uuid = lodash.uniqueId(yPopupOverlayUuidPrefix);
                this.popupDisplayed = false;
                this.config = $scope.$eval($attrs.yPopupOverlay);
                this.resetPopupSize();
                this.updateTriggers();

                // only activate
                this.active = this.config !== 'undefined';
            };

            this.$onDestroy = function() {
                if (this.untrigger) {
                    this.untrigger();
                }
                this.hide();
            }.bind(this);
        }


        // --------------------------------------------
        // --------------- PUBLIC API -----------------
        // --------------------------------------------

        var safeController = new SafeController();

        this.$doCheck = function() {
            safeController.$doCheck();
        };

        this.$onInit = function() {
            safeController.$onInit();
        };

        this.$onDestroy = function() {
            safeController.$onDestroy();
        };

        // EXPOSED Close API for users to programmatically close
        // the popup via their template or templateUrl
        $scope.closePopupOverlay = function() {
            safeController.hide();
        }.bind(this);

    });
