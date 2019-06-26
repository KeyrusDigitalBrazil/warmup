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
angular.module('slotContextualMenuDecoratorModule', [
        'smarteditServicesModule',
        'contextualMenuDecoratorModule',
        'ui.bootstrap',
    ])
    .constant('SHOW_SLOT_MENU', '_SHOW_SLOT_MENU')
    .constant('HIDE_SLOT_MENU', 'HIDE_SLOT_MENU')
    .constant('SHOW_SLOT_PADDING', 'SHOW_SLOT_PADDING')
    .constant('HIDE_SLOT_PADDING', 'HIDE_SLOT_PADDING')
    .controller('slotContextualMenuController', function(
        $controller,
        $scope,
        $element,
        SHOW_SLOT_MENU,
        HIDE_SLOT_MENU,
        SHOW_SLOT_PADDING,
        HIDE_SLOT_PADDING,
        REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT,
        smarteditroot,
        systemEventService,
        contextualMenuService,
        permissionService,
        yjQuery
    ) {
        angular.extend(this, $controller('baseContextualMenuController', {
            $scope: $scope
        }));

        this.$onInit = function() {
            this.updateItems();
            this.showItems = false;

            this.permissionsObject = [{
                names: ["se.slot.not.external"],
                context: {
                    slotCatalogVersionUuid: this.componentAttributes.smarteditCatalogVersionUuid
                }
            }];

            permissionService.isPermitted(this.permissionsObject).then(function(isAllowed) {
                this.showItems = isAllowed;

                var showSlotMenuId = this.smarteditComponentId + SHOW_SLOT_MENU;
                this.showSlotMenuUnregFn = systemEventService.subscribe(showSlotMenuId, this._showSlotMenu);
                this.hideSlotMenuUnregFn = systemEventService.subscribe(HIDE_SLOT_MENU, this._hideSlotMenu);
                this.refreshContextualMenuUnregFn = systemEventService.subscribe(REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT, this.updateItems);

            }.bind(this));

        };

        this.triggerMenuItemAction = function(item, $event) {
            item.callback({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                componentAttributes: this.componentAttributes
            }, $event);
        };

        this._showSlotMenu = function(eventId, slotId) {
            if (this.smarteditComponentId === slotId) {
                this.remainOpenMap.slotMenuButton = true;
                systemEventService.publishAsync(SHOW_SLOT_PADDING);
            }
        }.bind(this);

        this._hideSlotMenu = function() {
            if (this.remainOpenMap.slotMenuButton) {
                delete this.remainOpenMap.slotMenuButton;
            }
            systemEventService.publishAsync(HIDE_SLOT_PADDING);
        }.bind(this);

        this.maxContextualMenuItems = 3;

        this.updateItems = function() {
            contextualMenuService.getContextualMenuItems({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                iLeftBtns: this.maxContextualMenuItems,
                element: $element
            }).then(function(newItems) {
                this.items = newItems;
            }.bind(this));
        }.bind(this);

        this.triggerMenuItemAction = function(item, $event) {
            item.callback({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                slotId: this.smarteditSlotId,
                slotUuid: this.smarteditSlotUuid,
                element: $element,
                //@deprecated since 6.4
                properties: JSON.stringify(this.componentAttributes)
            }, $event);
        }.bind(this);

        this.getItems = function() {
            return this.items;
        };

        this.$onDestroy = function() {
            if (this.showSlotMenuUnregFn) {
                this.showSlotMenuUnregFn();
            }
            if (this.hideSlotMenuUnregFn) {
                this.hideSlotMenuUnregFn();
            }
            if (this.refreshContextualMenuUnregFn) {
                this.refreshContextualMenuUnregFn();
            }
        };

        this.positionPanelHorizontally = function() {
            var $decorativePanel = $element.find('.decorative-panel-area');
            var rightMostOffsetFromPage = $element.offset().left + $decorativePanel.width();

            // Calculate if the slot is overflowing the body width.
            var isOnLeft = rightMostOffsetFromPage >= yjQuery('body').width();

            if (isOnLeft) {
                var offset = $decorativePanel.outerWidth() - $element.find('.yWrapperData').width();
                $decorativePanel.css('margin-left', -offset);
                $element.find('.decorator-padding-left').css('margin-left', -offset);
            }

            // Hide all paddings and show the left or right one.
            this.hidePadding();
            $element.find(isOnLeft ? '.decorator-padding-left' : '.decorator-padding-right').css('display', 'flex');
        };

        this.$doCheck = function() {
            if (this.active) {
                this.positionPanelHorizontally();
            }
        };

        this.hidePadding = function() {
            $element.find('.decorator-padding-left').css('display', 'none');
            $element.find('.decorator-padding-right').css('display', 'none');
        };

    })
    .directive('slotContextualMenu', function($timeout, SHOW_SLOT_PADDING, HIDE_SLOT_PADDING, systemEventService) {
        return {
            templateUrl: 'slotContextualMenuDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'slotContextualMenuController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                componentAttributes: '<',
                active: '='
            },
            link: function($scope, $element) {
                $scope.ctrl.positionPanelVertically = function() {
                    var decorativePanelArea = $element.find('.decorative-panel-area');
                    var decoratorPaddingContainer = $element.find('.decorator-padding-container');
                    var marginTop;
                    if ($element.offset().top <= decorativePanelArea.height()) {
                        marginTop = decoratorPaddingContainer.height();
                        decoratorPaddingContainer.css('margin-top', -(marginTop + decorativePanelArea.height()));
                    } else {
                        marginTop = -42;
                    }
                    decorativePanelArea.css('margin-top', marginTop);
                };

                $scope.ctrl.positionPanel = function() {
                    $scope.ctrl.positionPanelVertically();
                    $scope.ctrl.positionPanelHorizontally();
                };

                $scope.$watch('ctrl.active', function(isActive) {
                    $timeout(function() {
                        $scope.ctrl.hidePadding();
                        if (!!isActive) {
                            $scope.ctrl.positionPanel();
                            systemEventService.publishAsync('SLOT_CONTEXTUAL_MENU_ACTIVE');
                        }
                    });
                });

                var hideSlotUnSubscribeFn = systemEventService.subscribe(HIDE_SLOT_PADDING, $scope.ctrl.hidePadding);
                var showSlotUnSubscribeFn = systemEventService.subscribe(SHOW_SLOT_PADDING, $scope.ctrl.positionPanel);

                $scope.$on("$destroy", function() {
                    hideSlotUnSubscribeFn();
                    showSlotUnSubscribeFn();
                });
            }
        };
    });
