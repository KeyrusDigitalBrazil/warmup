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
angular.module('yPopoverModule', [
        'coretemplates',
        'yPopupOverlayModule',
        'smarteditCommonsModule',
        'functionsModule',
        'yjqueryModule',
        'seConstantsModule'
    ])
    .controller('yPopoverController', function($log, $scope, $element, yjQuery, isBlank, $templateCache, yPopupEngineService, isIframe, OVERLAY_ID, $transclude) {
        this.engine = null;
        this.config = null;

        this.transcludedContent = null;
        this.transclusionScope = null;

        $transclude(function(clone, scope) {
            $element.append(clone);
            this.transcludedContent = clone;
            this.transclusionScope = scope;
        }.bind(this));

        this.getTemplate = function() {
            return '<y-popover-popup class="se-popover-popup" data-placement="ypop.placement" data-template="ypop.template" data-title="ypop.title"></y-popover-popup>';
        };

        this.$onInit = function() {
            var anchor = $element[0];
            var overlay = isIframe() ? yjQuery('#' + OVERLAY_ID) : null;

            this.config = {
                placement: this.placement || 'top',
                trigger: this.trigger || 'click',
                container: (overlay && overlay.length) ? overlay[0] : 'body',
                onShow: function() {
                    this.isOpen = true;
                }.bind(this),
                onHide: function() {
                    this.isOpen = false;
                }.bind(this),
                onChanges: function(element, data) {
                    if (this.placement !== data.placement) {
                        this.placement = data.placement;
                        $scope.$apply();
                    }
                }.bind(this)
            };

            this.engine = new yPopupEngineService(anchor, this.getTemplate(), $scope, this.config);
            this.isOpen = !isBlank(this.isOpen) ? this.isOpen : this.engine.isOpen;
        }.bind(this);

        this.$doCheck = function() {
            if (this.previousIsOpen !== this.isOpen) {

                if (this.isOpen) {
                    this.engine.show();
                } else {
                    this.engine.hide();
                }
                this.previousIsOpen = this.isOpen;
            }
        };

        this.$onChanges = function() {
            if (this.templateUrl) {
                this.template = $templateCache.get(this.templateUrl);
                delete this.templateUrl;
            }
            if (this.engine) {
                this.config.placement = this.placement || 'top';
                this.config.trigger = this.trigger || 'click';
                this.engine.configure(this.config);
            }
        };

        this.$onDestroy = function() {
            this.engine.dispose();
            this.transcludedContent.remove();
            this.transclusionScope.$destroy();
        };
    })

    /**
     * @ngdoc directive
     * @name yPopoverModule.directive:yPopover
     * @scope
     * @restrict A
     *
     * @description
     * This directive attaches a customizable popover on a DOM element.
     * @param {<String=} template the HTML body to be used in the popover body, it will automatically be trusted by the directive. Optional but exactly one of either template or templateUrl must be defined.
     * @param {<String=} templateUrl the location of the HTML template to be used in the popover body. Optional but exactly one of either template or templateUrl must be defined.
     * @param {<String=} title the title to be used in the popover title section. Optional.
     * @param {<String=} placement the placement of the popover around the target element. Possible values are <b>top, left, right, bottom</b>, as well as any
     * concatenation of them with the following format: placement1-placement2 such as bottom-right. Optional, default value is top.
     * @param {=String=} trigger the event type that will trigger the popover. Possibles values are <b>hover, click, outsideClick, none</b>. Optional, default value is 'click'.
     */
    .directive('yPopover', function() {
        return {
            restrict: 'A',
            transclude: true,
            replace: false,
            controller: 'yPopoverController',
            controllerAs: 'ypop',
            scope: {},
            bindToController: {
                template: '<?',
                templateUrl: '<?',
                title: '<?',
                placement: "<?",
                trigger: "<?",
                isOpen: "=?"
            }
        };
    })
    /**
     * Internal component for the yPopover to hold the bindings for the template, placement, and title.
     */
    .component('yPopoverPopup', {
        templateUrl: 'yPopoverPopupTemplate.html',
        bindings: {
            placement: '<?',
            title: '<?',
            template: '<?'
        }
    });
