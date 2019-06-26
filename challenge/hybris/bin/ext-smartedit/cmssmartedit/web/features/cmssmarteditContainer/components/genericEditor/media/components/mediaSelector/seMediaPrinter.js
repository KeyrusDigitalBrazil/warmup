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
angular.module('seMediaPrinterModule', ['seMediaPreviewModule'])
    .factory('seMediaPrinterConstants', function(assetsService) {
        var assetsRoot = assetsService.getAssetsRoot();

        return {
            DELETE_ICON_URL: assetsRoot + '/images/remove_image_small.png',
            REPLACE_ICON_URL: assetsRoot + '/images/replace_image_small.png',
            ADV_INFO_ICON_URL: assetsRoot + '/images/info_image_small.png'
        };
    })
    .controller('seMediaPrinterController', function($scope, seMediaPrinterConstants) {
        this.deleteIcon = seMediaPrinterConstants.DELETE_ICON_URL;
        this.replaceIcon = seMediaPrinterConstants.REPLACE_ICON_URL;
        this.advInfoIcon = seMediaPrinterConstants.ADV_INFO_ICON_URL;

        this.$onChanges = function() {
            $scope.selected = this.selected;
            $scope.item = this.item;
            $scope.ySelect = this.ySelect;
        };

    })
    .component('seMediaPrinter', {
        templateUrl: 'seMediaPrinterTemplate.html',
        controller: 'seMediaPrinterController',
        controllerAs: 'printer',
        require: {
            ySelect: '^ySelect'
        },
        bindings: {
            selected: '<',
            item: '<'
        }
    });
