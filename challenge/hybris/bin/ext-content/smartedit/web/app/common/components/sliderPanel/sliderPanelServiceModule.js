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
 * @name sliderPanelServiceModule
 * @description
 * This module provides a service to initialize and control the rendering of the {@link sliderPanelModule.directive:ySliderPanel ySliderPanel} Angular component.
 */
angular.module("sliderPanelServiceModule", ['yLoDashModule'])

    .factory("sliderPanelServiceFactory", function(lodash) {

        /**
         * @ngdoc service
         * @name sliderPanelServiceModule.service:sliderPanelService
         * @description
         * The sliderPanelService handles the initialization and the rendering of the {@link sliderPanelModule.directive:ySliderPanel ySliderPanel} Angular component.
         */
        function sliderPanelService(element, window, configuration) {

            /* ----------
             [ variables ]
             ---------- */

            var sliderPanelDefaultConfiguration = {
                slideFrom: "right",
                overlayDimension: "80%"
            };

            var returningHigherZIndex = function() {

                var highestZIndex = 0;
                var zIndex = 0;

                var browseNodeChildren = function(node) {
                    angular.forEach(angular.element(node).children(),
                        function(child) {
                            processBranchOrLeaf(child);
                        }
                    );
                };

                var checkZIndexValue = function(node) {
                    zIndex = angular.element(node).css("z-index");
                    if (!isNaN(zIndex) && zIndex > highestZIndex) {
                        highestZIndex = zIndex;
                    }
                };

                var processBranchOrLeaf = function(node) {
                    // we are only processing the UI tags
                    if (["SCRIPT", "lINK", "BASE"].indexOf(angular.element(node)[0].nodeName) === -1) {
                        checkZIndexValue(node);
                        if (angular.element(node).children().length > 0) {
                            browseNodeChildren(node);
                        }
                    }

                };

                processBranchOrLeaf(angular.element("body"));

                return parseInt(highestZIndex);

            };

            /**
             * @ngdoc method
             * @name sliderPanelServiceModule.service:sliderPanelService#updateContainerInlineStyling
             * @methodOf sliderPanelServiceModule.service:sliderPanelService
             * @param {Boolean} screenResized Sets whether the update is triggered by a screen resize
             * @description
             * This method sets the inline styling applied to the slider panel container according to the dimension and position values
             * of the parent element.
             */
            this.updateContainerInlineStyling = function(screenResized) {

                var parentClientRect = parent[0].getBoundingClientRect();
                var borders = {
                    left: (parent.css("border-left-width") ? parseInt(parent.css("border-left-width").replace("px", "")) : 0),
                    top: (parent.css("border-top-width") ? parseInt(parent.css("border-top-width").replace("px", "")) : 0)
                };

                this.inlineStyling.container.height = parent[0].clientHeight + "px";
                this.inlineStyling.container.width = parent[0].clientWidth + "px";
                this.inlineStyling.container.left = ((appendChildTarget.nodeName === "BODY") ? Math.round(parentClientRect.left + window.pageXOffset + borders.left) : 0) + "px";
                this.inlineStyling.container.top = ((appendChildTarget.nodeName === "BODY") ? Math.round(parentClientRect.top + window.pageYOffset + borders.top) : 0) + "px";

                // z-index value is not set during screen resize
                if (!screenResized) {
                    this.inlineStyling.container.zIndex = (this.sliderPanelConfiguration.zIndex) ? parseInt(this.sliderPanelConfiguration.zIndex) : (returningHigherZIndex() + 1);
                }

            }.bind(this);

            /* ---------------
            [ initialization ]
             -------------- */

            // defining the configuration set on the processed slider panel by merging the JSON object provided as parameter
            // with the default configuration
            this.sliderPanelConfiguration = lodash.defaultsDeep(
                configuration,
                sliderPanelDefaultConfiguration
            );

            // instantiating "parent" local variable
            var parentRawElement = (this.sliderPanelConfiguration.cssSelector) ? document.querySelector(this.sliderPanelConfiguration.cssSelector) : null;
            if (!parentRawElement) {
                parentRawElement = element.parent();
            }

            var parent = angular.element(parentRawElement);

            // instantiating "appendChildTarget" local variable
            for (var testedElement = parent, modalFound = false, i = 0; testedElement[0].nodeName !== "BODY"; testedElement = angular.element(testedElement.parent()), i++) {
                if (testedElement[0].getAttribute("id") === "y-modal-dialog") {
                    modalFound = true;
                    break;
                }
            }
            var appendChildTarget = (modalFound) ? testedElement[0] : document.body;

            // storing parent dimension and position within session variable
            this.inlineStyling = {
                container: {},
                content: {}
            };

            // setting the inline styling applied on the slider panel content according to its configuration.
            this.inlineStyling.content[(["top", "bottom"].indexOf(this.sliderPanelConfiguration.slideFrom) === -1) ? "width" : "height"] = this.sliderPanelConfiguration.overlayDimension;

            // appending the slider panel HTML tag as last child of the HTML body tag.
            angular.element(appendChildTarget).append(element[0]);

        }

        return {

            /**
             * @ngdoc method
             * @name sliderPanelServiceModule.service:sliderPanelService#getNewServiceInstance
             * @methodOf sliderPanelServiceModule.service:sliderPanelService
             * @description
             * Set and returns a new instance of the slider panel.
             * @param {Object} element A pointer to the slider panel HTML tag provided by the Angular framework.
             * @param {Object} window A pointer to the Javascript window element provided by the Angular framework.
             * @param {Object =} configuration A JSON object containing the specific configuration to be applied on the slider panel.
             * @param {String} configuration.cssSelector CSS pattern used to select the element covered by the slider panel.
             * @param {Object} configuration.modal Renders the slider panel as a SmartEdit modal.
             * ````
             *{
             *   showDismissButton: {Boolean},
             *   title: {String},
             *   cancel: {
             *       onClick: {Function},
             *       label: {String}
             *   }
             *   save: {
             *       disabled: {String} (optional)
             *       onClick: {Function},
             *       label: {String}
             *   },
             *   dismiss: {
             *       onClick: {Function}
             *   }
             *}
             * ````
             * @param {Boolean} configuration.noGreyedOutOverlay Used to indicate if a greyed-out overlay is to be displayed or not.
             * @param {String} configuration.overlayDimension Indicates the dimension of the container slider panel once it is displayed.
             * @param {String} configuration.slideFrom Specifies from which side of its container the slider panel slides out.
             * @param {Boolean} configuration.displayedByDefault Specifies whether the slider panel is to be shown by default.
             * @param {String} configuration.zIndex Indicates the z-index value to be applied on the slider panel.
             */
            getNewServiceInstance: function(element, window, configuration) {
                return new sliderPanelService(element, window, configuration);
            }

        };

    });
