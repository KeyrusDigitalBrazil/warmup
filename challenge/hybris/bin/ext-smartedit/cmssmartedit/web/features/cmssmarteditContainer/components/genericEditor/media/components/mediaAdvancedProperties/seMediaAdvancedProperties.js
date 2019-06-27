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
angular.module('seMediaAdvancedPropertiesModule', ['ui.bootstrap'])
    .constant('seMediaAdvancedPropertiesConstants', {
        CONTENT_URL: 'seMediaAdvancedPropertiesContentTemplate.html',
        I18N_KEYS: {
            DESCRIPTION: 'se.media.advanced.information.description',
            CODE: 'se.media.advanced.information.code',
            ALT_TEXT: 'se.media.advanced.information.alt.text',
            ADVANCED_INFORMATION: 'se.media.advanced.information',
            INFORMATION: 'se.media.information'
        }
    })
    .controller('seMediaAdvancedPropertiesController', function(seMediaAdvancedPropertiesConstants) {

        this.$onInit = function() {
            this.i18nKeys = seMediaAdvancedPropertiesConstants.I18N_KEYS;
            this.contentUrl = seMediaAdvancedPropertiesConstants.CONTENT_URL;
        };

    })
    .component('seMediaAdvancedProperties', {
        bindings: {
            code: '=',
            advInfoIcon: '=',
            description: '=',
            altText: '='
        },
        controller: 'seMediaAdvancedPropertiesController',
        controllerAs: 'ctrl',
        templateUrl: 'seMediaAdvancedPropertiesTemplate.html'
    })
    .component('seMediaAdvancedPropertiesCondensed', {
        bindings: {
            code: '<',
            advInfoIcon: '<',
            description: '<',
            altText: '<'
        },
        controller: 'seMediaAdvancedPropertiesController',
        controllerAs: 'ctrl',
        templateUrl: 'seMediaAdvancedPropertiesCondensedTemplate.html'
    });
