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
angular.module('seFileSelectorModule', [])
    .controller('seFileSelectorController', function() {

        this.$onInit = function() {
            this.imageRoot = this.imageRoot || "";
            this.disabled = this.disabled || false;
            this.customClass = this.customClass || "";
        };

        this.buildAcceptedFileTypesList = function() {
            return this.acceptedFileTypes.map(function(fileType) {
                return '.' + fileType;
            }).join(',');
        };
    })
    .directive('seFileSelector', function() {
        return {
            templateUrl: 'seFileSelectorTemplate.html',
            restrict: 'E',
            scope: {},
            controller: 'seFileSelectorController',
            controllerAs: 'ctrl',
            bindToController: {
                imageRoot: '<?',
                uploadIcon: '<',
                labelI18nKey: '<',
                acceptedFileTypes: '<',
                customClass: '<?',
                disabled: '<?',
                onFileSelect: '&'
            },
            link: function($scope, $element) {
                $element.find('input').on('change', function(event) {
                    $scope.$apply(function() {
                        $scope.ctrl.onFileSelect({
                            files: event.target.files
                        });
                        var input = $element.find('input');
                        input.replaceWith(input.clone(true));
                    });
                });
            }
        };
    });
