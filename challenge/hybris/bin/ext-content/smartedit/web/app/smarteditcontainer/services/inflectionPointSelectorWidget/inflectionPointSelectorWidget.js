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
angular.module('inflectionPointSelectorModule', ['yjqueryModule', 'smarteditServicesModule', 'resourceLocationsModule', 'iframeClickDetectionServiceModule'])
    .directive('inflectionPointSelector', function(DEVICE_SUPPORTS, iframeManagerService, iframeClickDetectionService, systemEventService, $document, yjQuery) {
        return {
            templateUrl: 'inflectionPointSelectorWidgetTemplate.html',
            restrict: 'E',
            transclude: true,
            $scope: {},
            link: function($scope) {
                $scope.selectPoint = function(choice) {

                    $scope.currentPointSelected = choice;
                    $scope.status.isopen = !$scope.status.isopen;

                    if (choice !== undefined) {
                        iframeManagerService.apply(choice, $scope.deviceOrientation);
                    }


                };

                $scope.setHoverState = function(hoverState) {
                    $scope.isHovered = hoverState;
                };

                $scope.getButtonImageSrc = function() {
                    var source = ($scope.status.isopen || $scope.isHovered) ? $scope.currentPointSelected.icon : $scope.currentPointSelected.blueIcon;

                    return $scope.imageRoot + source;
                };

                $scope.currentPointSelected = DEVICE_SUPPORTS.find(function(deviceSupport) {
                    return deviceSupport.default;
                });

                $scope.points = DEVICE_SUPPORTS;

                $scope.status = {
                    isopen: false
                };

                $scope.toggleDropdown = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $scope.status.isopen = !$scope.status.isopen;
                };

                iframeClickDetectionService.registerCallback('inflectionPointClose', function() {
                    $scope.status.isopen = false;
                });

                $document.on('click', function(event) {

                    if (yjQuery(event.target).parents('inflection-point-selector').length <= 0 && $scope.status.isopen) {
                        $scope.status.isopen = false;
                        $scope.$apply();
                    }
                });

                var unRegFn = systemEventService.subscribe('OVERLAY_DISABLED', function() {
                    $scope.status.isopen = false;
                });

                $scope.$on('$destroy', function() {
                    unRegFn();
                });

            }
        };
    });
