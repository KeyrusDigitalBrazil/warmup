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
angular.module('toolbarModule', ['resourceLocationsModule', 'toolbarInterfaceModule', 'translationServiceModule', 'iframeClickDetectionServiceModule', 'smarteditServicesModule', 'seConstantsModule'])
    /**
     * @ngdoc object
     * @name toolbarModule.string:CLOSE_ALL_ACTION_ITEMS
     *
     * @description
     * Injectable angular constant<br/>
     * Constant identifying the closure of all action items.
     */
    .constant('CLOSE_ALL_ACTION_ITEMS', 'closeAllActionItems')
    /**
     * @ngdoc service
     * @name toolbarModule.toolbarServiceFactory
     *
     * @description
     * The toolbar service factory generates instances of the {@link toolbarModule.ToolbarService ToolbarService} based on
     * the gateway ID (toolbar-name) provided. Only one ToolbarService instance exists for each gateway ID, that is, the
     * instance is a singleton with respect to the gateway ID.
     */
    .factory('toolbarServiceFactory', function($q, $log, gatewayProxy, extend, ToolbarServiceInterface) {
        /////////////////////////////////////
        // OUTER ToolbarService Prototype
        /////////////////////////////////////

        /**
         * @ngdoc service
         * @name toolbarModule.ToolbarService
         *
         * @description
         * The SmartEdit container toolbar service is used to add toolbar items that can perform actions to either
         * the SmartEdit application or the SmartEdit container. Clients can pass items to this service using the
         * addItems() function. The items are then forwarded to the {@link toolbarModule.directive:toolbar toolbar}
         * directive as aliases (a key-name mapping that maps to a specific callback; the alias models the action as it
         * is meant to be displayed in the toolbar), which is responsible for mapping the actions to the view displayed
         * to the user.
         *
         * Uses {@link smarteditCommonsModule.service:GatewayProxy gatewayProxy} for cross iframe communication, using the toolbar
         * name as the gateway ID.
         *
         * <b>Inherited Methods from {@link toolbarInterfaceModule.ToolbarServiceInterface
         * ToolbarServiceInterface}</b>: {@link toolbarInterfaceModule.ToolbarServiceInterface#methods_addItems
         * addItems}
         *
         * @param {String} gatewayId Toolbar name used by the gateway proxy service.
         */
        var ToolbarService = function(gatewayId) {
            this.actions = {};
            this.aliases = [];
            this.onAliasesChange = null;
            this.gatewayId = gatewayId;

            gatewayProxy.initForService(this, ["addAliases", 'removeItemByKey', 'removeAliasByKey', "_removeItemOnInner", "triggerActionOnInner"]);
        };

        ToolbarService = extend(ToolbarServiceInterface, ToolbarService);

        function get(aliases, alias) {
            return aliases.filter(function(el) {
                return el.key === alias.key;
            })[0];
        }

        ToolbarService.prototype.addAliases = function(aliases) {

            this.aliases = this.aliases.map(function(alias) {
                var reference = get(aliases, alias);
                if (reference) {
                    return reference; // to force ngRepeat computation
                } else {
                    return alias;
                }
            });

            aliases.forEach(function(al) {
                if (!get(this.aliases, al)) {
                    this.aliases.push(al);
                }
            }.bind(this));

            var samePriority = false;
            var warning = "In " + this.gatewayId + " the items ";
            var _section = '';
            this.aliases.sort(function(a, b) {
                if (a.priority === b.priority && a.section === b.section) {
                    _section = a.section;
                    warning += a.key + " and " + b.key + " ";
                    samePriority = true;
                    return a.key > b.key;
                }
                return a.priority - b.priority;
            });

            if (samePriority) {
                $log.warn("WARNING: " + warning + "have the same priority withing section:" + _section);
            }

            if (this.onAliasesChange) {
                this.onAliasesChange(this.aliases);
            }
        };

        /**
         * @ngdoc method
         * @name toolbarInterfaceModule.ToolbarServiceInterface#removeItemByKey
         * @methodOf toolbarInterfaceModule.ToolbarServiceInterface
         *
         * @description
         * This method removes the action and the aliases of the toolbar item identified by
         * the provided key.
         *
         * @param {String} itemKey - Identifier of the toolbar item to remove.
         */
        ToolbarService.prototype.removeItemByKey = function(itemKey) {
            if (itemKey in this.actions) {
                delete this.actions[itemKey];
            } else {
                this._removeItemOnInner(itemKey);
            }

            this.removeAliasByKey(itemKey);
        };

        ToolbarService.prototype.removeAliasByKey = function(itemKey) {
            var aliasIndex = 0;
            for (; aliasIndex < this.aliases.length; aliasIndex++) {
                if (this.aliases[aliasIndex].key === itemKey) {
                    break;
                }
            }

            if (aliasIndex < this.aliases.length) {
                this.aliases.splice(aliasIndex, 1);
            }

            if (this.onAliasesChange) {
                this.onAliasesChange(this.aliases);
            }

        };

        ToolbarService.prototype.setOnAliasesChange = function(onAliasesChange) {
            this.onAliasesChange = onAliasesChange;
        };

        ToolbarService.prototype.triggerAction = function(action) {
            if (action && this.actions[action.key]) {
                this.actions[action.key].call(action);
                return;
            }

            this.triggerActionOnInner(action);
        };

        /**
         * @ngdoc method
         * @name toolbarModule.ToolbarService#addItemsStyling
         * @methodOf toolbarModule.ToolbarService
         *
         * @description
         * Adds CSS classes to the items on the toolbar.
         *
         * @param {String} classes Space-separated list of CSS classes
         */
        ToolbarService.prototype.addItemsStyling = function(classes) {
            this.actionsClasses = classes;
        };

        /////////////////////////////////////
        // Factory and Management
        /////////////////////////////////////
        var toolbarServicesByGatewayId = {};

        /**
         * @ngdoc method
         * @name toolbarModule.toolbarServiceFactory#getToolbarService
         * @methodOf toolbarModule.toolbarServiceFactory
         *
         * @description
         * Returns a single instance of the ToolbarService for the given gateway identifier. If one does not exist, an
         * instance is created and cached.
         *
         * @param {string} gatewayId The toolbar name used for cross iframe communication (see {@link
         * smarteditCommonsModule.service:GatewayProxy gatewayProxy})
         * @returns {ToolbarService} Corresponding ToolbarService instance for given gateway ID.
         */
        var getToolbarService = function(gatewayId) {
            if (!toolbarServicesByGatewayId[gatewayId]) {
                toolbarServicesByGatewayId[gatewayId] = new ToolbarService(gatewayId);
            }
            return toolbarServicesByGatewayId[gatewayId];
        };

        return {
            getToolbarService: getToolbarService
        };
    })
    /**
     * @ngdoc directive
     * @name toolbarModule.directive:toolbar
     * @scope
     * @restrict E
     * @element ANY
     *
     * @description
     * Toolbar HTML mark-up that compiles into a configurable toolbar with an assigned {@link
     * toolbarModule.ToolbarService ToolbarService} for functionality. The toolbar listens for 
     * {@link seConstantsModule.object:EVENTS PAGE_SELECTED} event and invokes the callback that closes all action items.
     *
     * @param {String} imageRoot Root folder path for images
     * @param {String} cssClass Space-separated string of CSS classes for toolbar item styling
     * @param {String} toolbarName Toolbar name used by the gateway proxy service
     */
    .directive('toolbar', function(toolbarServiceFactory, STORE_FRONT_CONTEXT, iframeClickDetectionService, CLOSE_ALL_ACTION_ITEMS, EVENTS, systemEventService, $location) {
        return {
            templateUrl: 'toolbarTemplate.html',
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                cssClass: '@',
                toolbarName: '@',
                imageRoot: '=?imageRoot'
            },
            link: function(scope) {
                if (!scope.imageRoot) {
                    scope.imageRoot = "";
                }

                if (!scope.btnStates) {
                    scope.btnStates = {
                        lang: false,
                        add: false,
                        edit: false,
                        pageInfo: false
                    };
                }

                var toolbarService = toolbarServiceFactory.getToolbarService(scope.toolbarName);
                toolbarService.setOnAliasesChange(function(actions) {
                    scope.actions = actions;
                    scope.actionsClasses = toolbarService.actionsClasses;
                });

                scope.actions = toolbarService.getAliases();
                scope.actionsClasses = toolbarService.actionsClasses;
                scope.triggerAction = function(action, $event) {
                    $event.preventDefault();
                    toolbarService.triggerAction(action);
                };

                scope.isOnStorefront = function() {
                    return $location.absUrl().indexOf(STORE_FRONT_CONTEXT) >= 0;
                };

                scope.getItemVisibility = function(item) {
                    return item.include && (item.isOpen || item.keepAliveOnClose);
                };

                scope.closeAllActionItems = function() {
                    scope.actions.forEach(function(action) {
                        action.isOpen = false;
                    });
                };

                this.unregCloseActions = iframeClickDetectionService.registerCallback(CLOSE_ALL_ACTION_ITEMS, function() {
                    scope.closeAllActionItems();
                }.bind(this));

                this.unregOverlay = systemEventService.subscribe('OVERLAY_DISABLED', function() {
                    scope.$apply();
                });

                this.unregCloseAll = systemEventService.subscribe(EVENTS.PAGE_SELECTED, function() {
                    scope.closeAllActionItems();
                }.bind(this));

                scope.$onDestroy = function() {
                    this.unregCloseActions();
                    this.unregOverlay();
                    this.unregCloseAll();
                }.bind(this);
            }
        };
    });
