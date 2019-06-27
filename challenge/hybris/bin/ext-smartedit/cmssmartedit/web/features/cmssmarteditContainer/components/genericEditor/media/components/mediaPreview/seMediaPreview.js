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
angular.module('seMediaPreviewModule', [])
    .constant('seMediaPreviewConstants', {
        CONTENT_URL: 'seMediaPreviewContentTemplate.html',
        I18N_KEYS: {
            PREVIEW: 'media.preview'
        }
    })
    .controller('seMediaPreviewController', function(seMediaPreviewConstants) {
        this.i18nKeys = seMediaPreviewConstants.I18N_KEYS;
        this.contentUrl = seMediaPreviewConstants.CONTENT_URL;
    })
    .directive('seMediaPreview', function() {
        return {
            restrict: 'E',
            scope: {},
            bindToController: {
                imageUrl: '<'
            },
            controller: 'seMediaPreviewController',
            controllerAs: 'ctrl',
            templateUrl: 'seMediaPreviewTemplate.html'
        };
    });
