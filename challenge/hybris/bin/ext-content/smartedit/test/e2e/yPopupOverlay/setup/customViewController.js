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
angular.module("customViewModule", [
        'yjqueryModule',
        "yPopupOverlayModule"
    ])

    .constant("PATH_TO_CUSTOM_VIEW", "setup/customView.html")


    .component('yPopupOverlayTestComponent', {
        controller: function($timeout) {

            this.$onInit = function() {
                this.show = false;
                $timeout(function() {
                    this.show = true;
                }.bind(this), 2000);
            };
        },
        template: '<div id="popup-template" style="background-color: #caddf0; padding: 10px; border: solid black 1px">' +
            '<div>Some static content</div>' +
            '<div style="border: solid 1px" data-ng-if="$ctrl.show">Some asynchronous content</div>' +
            '</div>'
    })

    .controller("customViewController", function(yjQuery) {

        this.setup = function() {
            this.onShowCount = 0;
            this.onHideCount = 0;
            this.popupConfig = {
                template: '<y-popup-overlay-test-component></y-popup-overlay-test-component>',
                halign: 'right',
                valign: 'bottom'
            };
            this.popupTrigger = 'click';
            this.anchorCssData = {
                'position': 'absolute',
                'background-color': '#32a0ec',
                'top': 200,
                'left': 400,
                'min-height': 50,
                'min-width': 50,
                'height': 50,
                'width': 50
            };
            this.anchor = yjQuery('anchor');
            this.updateAnchor();
        }.bind(this);

        this.updateAnchor = function() {
            var css = {
                'position': this.anchorCssData.position,
                'background-color': this.anchorCssData['background-color'],
                'top': this.anchorCssData.top + 'px',
                'left': this.anchorCssData.left + 'px',
                'min-height': this.anchorCssData['min-height'] + 'px',
                'min-width': this.anchorCssData['min-width'] + 'px',
                'height': this.anchorCssData.height + 'px',
                'width': this.anchorCssData.width + 'px'
            };
            this.anchor.css(css);
        }.bind(this);

        this.setup();


    });
