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
 * @name slotVisibilityButtonModule
 * @description
 *
 * The slot visibility button module provides a directive and controller to manage the button within the slot contextual menu 
 * and the hidden component list, which is also part of the dropdown menu associated with the directive's template.     
 */
angular.module('slotVisibilityButtonModule', ['cmssmarteditTemplates', 'slotVisibilityServiceModule', 'slotVisibilityComponentModule', 'seConstantsModule'])

    /**
     * @ngdoc controller
     * @name slotVisibilityButtonModule.controller:slotVisibilityButtonController
     *
     * @description
     * The slot visibility button controller is responsible for enabling and disabling the hidden components button, 
     * as well as displaying the hidden components list. It also provides functions to open and close the hidden component list.
     *
     * @param {Object} slotVisibilityService slot visibility service instance
     * @param {Object} $scope current scope instance
     */
    .controller('slotVisibilityButtonController', function(slotVisibilityService, $scope, sharedDataService, $translate, crossFrameEventService, EVENT_OUTER_FRAME_CLICKED) {
        this.buttonName = 'slotVisibilityButton';
        this.eyeOnImageUrl = '/cmssmartedit/images/visibility_slot_menu_on.png';
        this.eyeOffImageUrl = '/cmssmartedit/images/visibility_slot_menu_off.png';
        this.eyeImageUrl = this.eyeOffImageUrl;
        this.closeImageUrl = '/cmssmartedit/images/close_button.png';
        this.buttonVisible = false;
        this.hiddenComponents = [];
        this.isComponentListOpened = false;

        $scope.$watch('ctrl.isComponentListOpened', function(newValue, oldValue) {
            this.eyeImageUrl = (newValue ? this.eyeOnImageUrl : this.eyeOffImageUrl);
            if (newValue !== oldValue) {
                this.setRemainOpen({
                    button: this.buttonName,
                    remainOpen: this.isComponentListOpened
                });
            }
        }.bind(this));

        this.markExternalComponents = function(experience, hiddenComponents) {
            hiddenComponents.forEach(function(hiddenComponent) {
                hiddenComponent.isExternal = hiddenComponent.catalogVersion !== experience.pageContext.catalogVersionUuid;
            });
        };

        this.getTemplateInfoForExternalComponent = function() {
            return "<div>" + $translate.instant('se.cms.slotvisibility.external.component') + "</div>";
        };

        this.onInsideClick = function($event) {
            $event.stopPropagation();
        };

        this.$onInit = function() {
            slotVisibilityService.getHiddenComponents(this.slotId).then(function(hiddenComponents) {
                sharedDataService.get('experience').then(function(experience) {
                    this.hiddenComponents = hiddenComponents;
                    this.markExternalComponents(experience, this.hiddenComponents);
                    this.hiddenComponentCount = hiddenComponents.length;
                    if (this.hiddenComponentCount > 0) {
                        this.buttonVisible = true;
                    }
                }.bind(this));
            }.bind(this));

            this.unregFn = crossFrameEventService.subscribe(EVENT_OUTER_FRAME_CLICKED, function() {
                this.isComponentListOpened = false;
            }.bind(this));
        };

        this.$onDestroy = function() {
            this.unregFn();
        };

    })
    /**
     * @ngdoc directive
     * @name slotVisibilityButtonModule.directive:slotVisibilityButton
     *
     * @description
     * The slot visibility button component is used inside the slot contextual menu and provides a button 
     * image that displays the number of hidden components, as well as a dropdown menu of hidden component.
     *
     * The directive expects that the parent, the slot contextual menu, has a setRemainOpen function and a 
     * slotId value on the parent's scope. setRemainOpen is used to send a command to the parent to leave 
     * the slot contextual menu open.
     */
    .component('slotVisibilityButton', {
        templateUrl: 'slotVisibilityButtonTemplate.html',
        transclude: true,
        controller: 'slotVisibilityButtonController',
        controllerAs: 'ctrl',
        bindings: {
            setRemainOpen: '&',
            slotId: '@',
            initButton: '@'
        }
    });
