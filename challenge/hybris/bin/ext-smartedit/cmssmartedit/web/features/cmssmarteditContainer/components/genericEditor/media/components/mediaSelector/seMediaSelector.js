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
angular.module('seMediaSelectorModule', ['seMediaAdvancedPropertiesModule', 'seMediaPrinterModule', 'mediaServiceModule'])
    .controller('seMediaSelectorController', function(mediaService) {

        this.mediaTemplate = 'seMediaPrinterWrapperTemplate.html';

        this.fetchStrategy = {
            fetchEntity: function(uuid) {
                return mediaService.getMedia(uuid);
            },
            fetchPage: function(mask, pageSize, currentPage) {
                return mediaService.getPage(mask, pageSize, currentPage);
            }
        };

    })
    .component('seMediaSelector', {
        templateUrl: 'seMediaSelectorTemplate.html',
        require: {
            geField: '^^genericEditorField'
        },
        bindings: {
            field: '<',
            model: '<',
            editor: '<',
            qualifier: '<',
            deleteIcon: '<',
            replaceIcon: '<',
            advInfoIcon: '<'
        },
        controller: 'seMediaSelectorController',
        controllerAs: 'ctrl'
    });
