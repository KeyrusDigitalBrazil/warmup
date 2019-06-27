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
 * @name yCollapsibleContainerModule
 * @requires ui.bootstrap
 * @requires yLoDashModule
 * @description
 * This module defines the collapsible container Angular component and its associated constants and controller.
 *
 * ## Basic Implementation
 *
 * To define a new collapsible container, you must make some basic modifications to your Angular module and controller, as well
 * as to your HTML template. You can also customize the rendering of your collapsible container in your controller.
 *
 * - ### Angular Module
 *
 * You must add the yCollapsibleContainerModule as a dependency to your Angular module.
 *
 * <pre>
 angular.module('yourApp', ['yCollapsibleContainerModule']) { ... }
 * </pre>
 *
 * - ### HTML template
 *
 * To include HTML content in the collapsible panel, you must embed it within a `<y-collapsible-container> </y-collapsible-container>` tag.<br />
 *
 * <pre>
 <y-collapsible-container>
    <header>
        Your title here
    </header>
    <content>
        Your content here
    </content>
 </y-collapsible-container>
 * </pre>
 *
 * - ### Angular Controller
 *
 * Within your Angular controller, you can define configurations which will get applied on the collapsible container.
 * <pre>
 angular.module('yourApp', ['sliderPanelModule'])
 .controller('yourController', function() {
     ...
     this.configuration = { ... };
     ...
 });
 * </pre>
 *
 * The configurations are passed and applied to the collapsible container through the binded variable 'configuration'
 *
 * <pre>
 <y-collapsible-container data-configuration="$yourCtrl.configuration">
    ...
 </y-collapsible-container>

 * </pre>
 *
 * For information about the available settings, see the {@link yCollapsibleContainerModule.directive:yCollapsibleContainer yCollapsibleContainer} Angular component.
 */
angular.module('yCollapsibleContainerModule', [
        'yLoDashModule',
        'ui.bootstrap'
    ])

    /**
     * @ngdoc object
     * @name yCollapsibleContainerModule.object:COLLAPSIBLE_CONTAINER_CONSTANTS
     * @description
     * This object defines injectable Angular constants that store the default configuration and CSS class names used in the controller to define the rendering and animation of the collapsible container.
     */
    .constant("COLLAPSIBLE_CONTAINER_CONSTANTS", {

        /**
         * @ngdoc property
         * @name DEFAULT_CONFIGURATION {Object}
         * @propertyOf yCollapsibleContainerModule.object:COLLAPSIBLE_CONTAINER_CONSTANTS
         * @param {Boolean} expandedByDefault Specifies if the collapsible container is expanded by default.
         * @param {String} iconAlignment Specifies if the expand-collapse icon is to be displayed to the left or to the right of the container header.
         * @param {Boolean} iconVisible Specifies if the expand-collapse icon is to be rendered.
         * @description
         * A JSON object defining the configuration applied by default to each collapsible container.
         **/
        DEFAULT_CONFIGURATION: {
            expandedByDefault: false,
            iconAlignment: "right",
            iconVisible: true
        },

        /**
         * @ngdoc property
         * @name ICON_LEFT {String}
         * @propertyOf yCollapsibleContainerModule.object:COLLAPSIBLE_CONTAINER_CONSTANTS
         *
         * @description
         * A classname allowing for the display of a CSS-based icon positioned to the left of the collapsible container's header
         **/
        ICON_LEFT: "icon-left",

        /**
         * @ngdoc property
         * @name ICON_RIGHT {String}
         * @propertyOf yCollapsibleContainerModule.object:COLLAPSIBLE_CONTAINER_CONSTANTS
         *
         * @description
         * A classname allowing for the display of a CSS-based icon positioned to the right of the collapsible container's header
         **/
        ICON_RIGHT: "icon-right"

    })

    .controller('yCollapsibleContainerController', function(
        lodash,
        COLLAPSIBLE_CONTAINER_CONSTANTS
    ) {

        this.getIconRelatedClassname = function() {
            if (this.configuration.iconVisible) {
                return COLLAPSIBLE_CONTAINER_CONSTANTS["ICON_" + this.configuration.iconAlignment.toUpperCase()];
            }
        }.bind(this);

        this.$onInit = function() {

            this.configuration = lodash.defaultsDeep(
                this.configuration,
                COLLAPSIBLE_CONTAINER_CONSTANTS.DEFAULT_CONFIGURATION
            );

        };

    })

    /**
     * @ngdoc directive
     * @name yCollapsibleContainerModule.directive:yCollapsibleContainer
     * @restrict E
     * @param {<Object=} configuration JSON object containing the configuration to be applied on a collapsible container.
     * @param {Boolean} configuration.expandedByDefault Specifies if the collapsible container is expanded by default.
     * @param {String} configuration.iconAlignment Specifies if the expand-collapse icon is to be displayed to the *left* or to the _right_ of the container header.
     * @param {Boolean} configuration.iconVisible Specifies if the expand-collapse icon is to be rendered.
     * @description
     * The yCollapsibleContainer Angular component allows for the dynamic display of any HTML content on a collapsible container.
     */
    .component('yCollapsibleContainer', {
        templateUrl: 'yCollapsibleContainer.html',
        controller: 'yCollapsibleContainerController',
        controllerAs: '$yCollapsibleContainerCtrl',
        transclude: {
            "collapsible-container-content": "content",
            "collapsible-container-title": "?header"
        },
        bindings: {
            configuration: '<?'
        }
    });
