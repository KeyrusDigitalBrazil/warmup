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
angular.module('contextualMenuDecoratorModule', [
        'smarteditServicesModule',
        'ui.bootstrap',
        'yPopupOverlayModule',
        'contextualMenuItemModule'
    ])

    .constant('CTX_MENU_DROPDOWN_IS_OPEN', 'CTX_MENU_DROPDOWN_IS_OPEN')

    .constant('CLOSE_CTX_MENU', 'CLOSE_CTX_MENU')

    .controller('baseContextualMenuController', function() {

        this.status = {
            isopen: false
        };

        this.isHybrisIcon = function(icon) {
            return icon && icon.indexOf("hyicon") >= 0;
        };

        this.remainOpenMap = {};

        /*
         setRemainOpen receives a key name and a boolean value
         the button name needs to be unique across all buttons so it won' t collide with other button actions.
         */
        this.setRemainOpen = function(key, remainOpen) {
            this.remainOpenMap[key] = remainOpen;
        };

        this.showOverlay = function() {
            if (this.active === true) {
                return true;
            }

            return Object.keys(this.remainOpenMap)
                .reduce(function(isOpen, key) {
                    return isOpen || this.remainOpenMap[key];
                }.bind(this), false);
        };

    })

    .controller('contextualMenuController', function(
        $scope,
        $element,
        $controller,
        $timeout,
        $log,
        REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT,
        CLOSE_CTX_MENU,
        smarteditroot,
        contextualMenuService,
        systemEventService
    ) {
        this.openItem = null;

        angular.extend(this, $controller('baseContextualMenuController', {
            $scope: $scope
        }));

        this.$postLink = function() {
            $scope.componentDetails = {
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                slotId: this.smarteditSlotId,
                slotUuid: this.smarteditSlotUuid
            };
        };

        /*
         * will only init when $element.width is not 0, which is what happens after a recompile of decorators called by sakExecutor after perspective change or refresh
         */
        this.$doCheck = function() {
            if (!this.initialized && $element.width() !== 0) {
                this.initialized = true;
                this.onInit();
            }
        };

        this.onInit = function() {
            this.maxContextualMenuItems = function() {
                var ctxSize = 50;
                var buttonMaxCapacity = Math.round($element.width() / ctxSize) - 1;
                var leftButtons = buttonMaxCapacity >= 4 ? 3 : buttonMaxCapacity - 1;
                leftButtons = (leftButtons < 0 ? 0 : leftButtons);
                return leftButtons;
            };

            this.moreMenuIsOpen = false;
            this.itemTemplateOverlayWrapper = {
                templateUrl: 'contextualMenuItemOverlayWrapper.html'
            };
            this.moreMenuPopupConfig = {
                templateUrl: 'moreItemsTemplate.html',
                halign: 'left'
            };
            this.updateItems();

            this.dndUnRegFn = systemEventService.subscribe(CLOSE_CTX_MENU, this.hideAllPopups);
            this.unregisterRefreshItems = systemEventService.subscribe(REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT, this.updateItems);

        };

        this.moreButton = {
            displayClass: 'hyicon hyicon-more cmsx-ctx__icon-more',
            i18nKey: 'se.cms.contextmenu.title.more'
        };

        this.updateItems = function() {
            contextualMenuService.getContextualMenuItems({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                iLeftBtns: this.maxContextualMenuItems(),
                element: $element
            }).then(function(newItems) {
                this.items = newItems;
            }.bind(this));
        }.bind(this);

        this.shouldShowTemplate = function(menuItem) {
            return this.displayedItem === menuItem;
        };

        this.onShowItemPopup = function(item) {
            this.setRemainOpen('someContextualPopupOverLay', true);

            this.openItem = item;
            this.openItem.isOpen = true;
        }.bind(this);

        this.onHideItemPopup = function(hideMoreMenu) {
            if (this.openItem) {
                this.openItem.isOpen = false;
                this.openItem = null;
            }

            this.displayedItem = null;
            this.setRemainOpen('someContextualPopupOverLay', false);
            if (hideMoreMenu) {
                this.onHideMoreMenuPopup();
            }
        }.bind(this);

        this.onShowMoreMenuPopup = function() {
            this.setRemainOpen('someContextualPopupOverLay', true);
        }.bind(this);

        this.onHideMoreMenuPopup = function() {
            this.moreMenuIsOpen = false;
            this.setRemainOpen('someContextualPopupOverLay', false);
        }.bind(this);

        this.hideAllPopups = function() {
            this.onHideMoreMenuPopup();
            this.onHideItemPopup();
        }.bind(this);

        this.triggerMenuItemAction = function(item, $event) {
            if (item.action.template || item.action.templateUrl) {
                if (this.displayedItem === item) {
                    this.displayedItem = null;
                } else {
                    this.displayedItem = item;
                }
            } else if (item.action.callback) {
                //close any popups
                this.hideAllPopups();
                item.action.callback({
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
            }
        }.bind(this);

        this.getItems = function() {
            return this.items;
        };

        this.$onDestroy = function() {
            if (this.dndUnRegFn) {
                this.dndUnRegFn();
            }
            if (this.unregisterRefreshItems) {
                this.unregisterRefreshItems();
            }
        };

        this.showContextualMenuBorders = function() {
            return this.active && this.items && this.items.leftMenuItems.length > 0;
        };
    })


    .directive('contextualMenu', function($q, $timeout, contextualMenuService, componentHandlerService) {
        return {
            templateUrl: 'contextualMenuDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'contextualMenuController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                smarteditContainerId: '@',
                smarteditContainerType: '@',
                componentAttributes: '<',
                active: '='
            },
            link: function($scope, $element) {

                $scope.ctrl.smarteditSlotId = componentHandlerService.getParentSlotForComponent($element);
                $scope.ctrl.smarteditSlotUuid = componentHandlerService.getParentSlotUuidForComponent($element);
                $scope.ctrl.slotAttributes = {
                    smarteditSlotId: $scope.ctrl.smarteditSlotId,
                    smarteditSlotUuid: $scope.ctrl.smarteditSlotUuid
                };
            }
        };
    });
