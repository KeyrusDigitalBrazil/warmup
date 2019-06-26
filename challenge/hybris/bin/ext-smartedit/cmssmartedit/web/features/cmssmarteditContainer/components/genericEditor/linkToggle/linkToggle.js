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
angular.module('linkToggleModule', [])
    .controller('linkToggleController', function() {
        this.emptyUrlLink = function() {
            this.model.linkToggle.urlLink = '';
        };
        this.$onInit = function() {
            if (this.model.linkToggle === undefined) {
                this.model.linkToggle = {};
                this.editor.pristine.linkToggle = {};
            }

            if (this.model.linkToggle.external === undefined) {
                this.model.linkToggle.external = true;
                this.editor.pristine.linkToggle.external = true;
            }
        };
    })
    .component('linkToggle', {
        templateUrl: 'linkToggleTemplate.html',
        transclude: true,
        controller: 'linkToggleController',
        bindings: {
            field: '<',
            model: '<',
            editor: '<'
        }
    });
