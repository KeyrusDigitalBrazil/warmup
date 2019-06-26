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
 * @name sliderPanelModule
 * @requires sliderPanelServiceModule
 * @requires ngAnimate
 * @description
 * This module defines the slider panel Angular component and its associated constants and controller.
 *
 * ## Basic Implementation
 *
 * To define a new slider panel, you must make some basic modifications to your Angular module and controller, as well
 * as to your HTML template.
 *
 * - ### Angular Module
 *
 * You must add the sliderPanelModule as a dependency to your Angular module.
 *
 * <pre>
 angular.module('yourApp', ['sliderPanelModule']) { ... }
 * </pre>
 *
 * - ### Angular Controller
 *
 * Within the Angular controller, you must add a function to be instantiated so that the controller will trigger the
 * display of the slider panel.
 * <pre>
 angular.module('yourApp', ['sliderPanelModule'])
 .controller('yourController', function() {
     ...
     this.showSliderPanel = function() {};
     ...
 });
 * </pre>
 *
 * - ### HTML template
 *
 * To include HTML content in the slider panel, you must embed the HTML content in a `<y-slider-panel> </y-slider-panel>` tag.<br />
 * For more information, see the definition of {@link sliderPanelModule.directive:ySliderPanel ySliderPanel} Angular component.
 *
 * <pre>
 <y-slider-panel data-slider-panel-show="$ctrl.sliderPanelShow">
    <content>
        any HTML content
    </content>
 </y-slider-panel>
 * </pre>
 *
 * You can then make the slider panel visible by calling the "Show Slider Panel" function defined in the associated controller; for example:
 *
 * <pre>
 <button class="btn btn-default" ng-click="$ctrl.sliderPanelShow();">
    Show Slider Panel
 </button>
 * </pre>
 *
 * ## Advanced Configurations
 *
 * A default set of configurations is applied to all slider panels. You can overwrite and update the default configuration.
 *
 * To update the configuration of a specific slider panel, you must instantiate a JSON object that contains the expected
 * configuration in the Angular controller and provide it to the slider panel controller using the HTML template, for example:
 *
 * <pre>
 <y-slider-panel ... data-slider-panel-configuration="$ctrl.sliderPanelConfiguration">
 * </pre>
 *
 * If you define this type of configuration set, SmartEdit will automatically merge it with the slider panel's default configuration.
 * For information about the available settings, see the {@link sliderPanelServiceModule.service:sliderPanelService#methods_getNewServiceInstance getNewServiceInstance} method.
 */
angular.module('sliderPanelModule', [
        'ngAnimate',
        'sliderPanelServiceModule',
        'yjqueryModule',
        'yLoDashModule'
    ])

    /**
     * @ngdoc object
     * @name sliderPanelModule.object:CSS_CLASSNAMES
     * @description
     * This object defines injectable Angular constants that store the CSS class names used in the controller to define the
     * rendering and animation of the slider panel.
     */
    .constant("CSS_CLASSNAMES", {

        /**
         * @ngdoc property
         * @name SLIDERPANEL_ANIMATED {String}
         * @propertyOf sliderPanelModule.object:CSS_CLASSNAMES
         *
         * @description
         * The class name applied to the slide panel container to trigger the sliding action in the CSS animation.
         **/
        SLIDERPANEL_ANIMATED: "sliderpanel--animated",

        /**
         * @ngdoc property
         * @name SLIDERPANEL_SLIDEPREFIX {String}
         * @propertyOf sliderPanelModule.object:CSS_CLASSNAMES
         *
         * @description
         * A common prefix for the class names that defines how the content of the slider panel is to be rendered.
         **/
        SLIDERPANEL_SLIDEPREFIX: "sliderpanel--slidefrom"
    })

    .controller('sliderPanelController', function(
        $animate,
        $element,
        $scope,
        $window,
        CSS_CLASSNAMES,
        yjQuery,
        lodash,
        sliderPanelServiceFactory
    ) {

        var self = this;
        var sliderPanelService = {};

        var addScreenResizeEventHandler = function() {
            yjQuery(window).on("resize.sliderPanelRedraw_" + self.uniqueId, function() {

                if (self.isShown) {
                    $scope.$apply(function() {
                        sliderPanelService.updateContainerInlineStyling(true);
                        self.inlineStyling.container = sliderPanelService.inlineStyling.container;
                    });
                }
            });
        };

        this.isSaveDisabled = function() {
            if (this.sliderPanelConfiguration.modal &&
                this.sliderPanelConfiguration.modal.save &&
                this.sliderPanelConfiguration.modal.save.isDisabledFn) {
                return this.sliderPanelConfiguration.modal.save.isDisabledFn();
            }
            return false;
        }.bind(this);

        this.$onInit = function() {

            this.isShown = false;
            this.uniqueId = lodash.uniqueId();

            // setting new instance of slider panel service
            sliderPanelService = sliderPanelServiceFactory.getNewServiceInstance($element, $window, this.sliderPanelConfiguration);

            // variables made available on the html template
            this.sliderPanelConfiguration = sliderPanelService.sliderPanelConfiguration;

            this.slideClassName = CSS_CLASSNAMES.SLIDERPANEL_SLIDEPREFIX + this.sliderPanelConfiguration.slideFrom;
            this.inlineStyling = {
                container: sliderPanelService.inlineStyling.container,
                content: sliderPanelService.inlineStyling.content
            };
            this.sliderPanelShow = this.showSlider;
            this.sliderPanelHide = this.hideSlider;

            this.sliderPanelDismissAction = this.sliderPanelConfiguration.modal &&
                this.sliderPanelConfiguration.modal.dismiss &&
                this.sliderPanelConfiguration.modal.dismiss.onClick ? this.sliderPanelConfiguration.modal.dismiss.onClick : this.hideSlider;

            // applying event handler for screen resize
            addScreenResizeEventHandler();

            if (this.sliderPanelConfiguration.displayedByDefault) {
                this.showSlider();
            }

        };

        this.$onDestroy = function() {
            yjQuery(window).off("resize.doResize");
        };

        this.showSlider = function() {

            // container inline styling
            sliderPanelService.updateContainerInlineStyling(false);
            self.inlineStyling.container = sliderPanelService.inlineStyling.container;

            // container greyed out overlay
            var isSecondarySliderPanel = false;
            angular.forEach(angular.element("y-slider-panel.sliderpanel--animated .sliderpanel-container"), function(sliderPanelContainer) {
                sliderPanelContainer = angular.element(sliderPanelContainer);
                if (!isSecondarySliderPanel) {
                    if (sliderPanelContainer.css("height") === self.inlineStyling.container.height &&
                        sliderPanelContainer.css("width") === self.inlineStyling.container.width &&
                        sliderPanelContainer.css("left") === self.inlineStyling.container.left &&
                        sliderPanelContainer.css("top") === self.inlineStyling.container.top) {
                        isSecondarySliderPanel = true;
                    }
                }
            });

            // if no related configuration has been set, the no greyed out overlay is set to true for all secondary slider panels.
            self.sliderPanelConfiguration.noGreyedOutOverlay = (typeof self.sliderPanelConfiguration.noGreyedOutOverlay === "boolean") ?
                self.sliderPanelConfiguration.noGreyedOutOverlay :
                isSecondarySliderPanel;

            // triggering slider panel display
            self.isShown = true;
            return $animate.addClass($element, CSS_CLASSNAMES.SLIDERPANEL_ANIMATED);

        };

        this.hideSlider = function() {
            return $animate.removeClass($element, CSS_CLASSNAMES.SLIDERPANEL_ANIMATED).then(function() {
                self.isShown = false;
            });
        };

    })

    /**
     * @ngdoc directive
     * @name sliderPanelModule.directive:ySliderPanel
     * @restrict E
     * @param {Object} dataSliderPanelConfiguration (optional) A JSON object containing the configuration to be applied on slider panel.
     * @param {Function} dataSliderPanelHide (optional) A function shared in a two ways binding by the main controller and the slider panel and used to trigger the hiding of the slider panel.
     * @param {Function} dataSliderPanelShow A function shared in a two ways binding by the main controller and the slider panel and used to trigger the display of the slider panel.
     * @description
     * The ySliderPanel Angular component allows for the dynamic display of any HTML content on a sliding panel.
     */
    .component('ySliderPanel', {
        templateUrl: 'sliderPanelTemplate.html',
        controller: 'sliderPanelController',
        controllerAs: '$sliderPanelCtrl',
        transclude: true,
        bindings: {
            sliderPanelConfiguration: '<?',
            sliderPanelHide: '=?',
            sliderPanelShow: '='
        }
    });
