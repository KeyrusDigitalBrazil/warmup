angular.module('personalizationsmarteditContextMenu', [
        'modalServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'ui.select',
        'genericEditorModule',
        'editorModalServiceModule',
        'smarteditRootModule',
        'personalizationsmarteditServicesModule',
        'renderServiceModule',
        'personalizationsmarteditDataFactory',
        'slotRestrictionsServiceModule',
        'catalogVersionFilterDropdownModule',
        'hasMulticatalogModule'
    ])
    .factory('personalizationsmarteditContextModal', function($controller, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, $filter, personalizationsmarteditMessageHandler, gatewayProxy, personalizationsmarteditContextService, renderService, personalizationsmarteditRestService, editorModalService) {

        var PersonalizationsmarteditContextModal = function() { //NOSONAR
            this.gatewayId = "personalizationsmarteditContextModal";
            gatewayProxy.initForService(this);
        };

        var modalButtons = [{
            id: 'cancel',
            label: "personalization.modal.addeditaction.button.cancel",
            style: MODAL_BUTTON_STYLES.SECONDARY,
            action: MODAL_BUTTON_ACTIONS.DISMISS
        }, {
            id: 'submit',
            label: "personalization.modal.addeditaction.button.submit",
            action: MODAL_BUTTON_ACTIONS.CLOSE
        }];

        var confirmModalButtons = [{
            id: 'confirmCancel',
            label: 'personalization.modal.deleteaction.button.cancel',
            style: MODAL_BUTTON_STYLES.SECONDARY,
            action: MODAL_BUTTON_ACTIONS.DISMISS
        }, {
            id: 'confirmOk',
            label: 'personalization.modal.deleteaction.button.ok',
            action: MODAL_BUTTON_ACTIONS.CLOSE
        }];

        PersonalizationsmarteditContextModal.prototype.openDeleteAction = function(config) {
            modalService.open({
                size: 'md',
                title: 'personalization.modal.deleteaction.title',
                templateInline: '<div id="confirmationModalDescription">{{ "' + "personalization.modal.deleteaction.content" + '" | translate }}</div>',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    angular.merge($scope, config);
                    $scope.modalManager = modalManager;
                    angular.extend(this, $controller('modalDeleteActionController', {
                        $scope: $scope
                    }));
                }],
                cssClasses: 'yFrontModal',
                buttons: confirmModalButtons
            }).then(function() {
                if (personalizationsmarteditContextService.getCombinedView().enabled) {
                    personalizationsmarteditRestService.getActions(config.selectedCustomizationCode, config.selectedVariationCode, config)
                        .then(function successCallback(response) {
                            var combinedView = personalizationsmarteditContextService.getCombinedView();
                            if (combinedView.customize.selectedComponents) {
                                combinedView.customize.selectedComponents.splice(combinedView.customize.selectedComponents.indexOf(config.containerSourceId), 1);
                            }
                            angular.forEach(combinedView.selectedItems, function(value) {
                                if (value.customization.code === config.selectedCustomizationCode && value.variation.code === config.selectedVariationCode) {
                                    value.variation.actions = response.actions;
                                }
                            });
                            personalizationsmarteditContextService.setCombinedView(combinedView);
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingactions'));
                        });
                } else {
                    var customize = personalizationsmarteditContextService.getCustomize();
                    customize.selectedComponents.splice(customize.selectedComponents.indexOf(config.containerSourceId), 1);
                    personalizationsmarteditContextService.setCustomize(customize);
                }
                renderService.renderSlots(config.slotsToRefresh);
            });
        };

        PersonalizationsmarteditContextModal.prototype.openAddAction = function(config) {
            modalService.open({
                title: "personalization.modal.addaction.title",
                templateUrl: 'personalizationsmarteditAddEditActionTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    angular.merge($scope, config);
                    $scope.defaultComponentId = config.componentId;
                    $scope.modalManager = modalManager;
                    $scope.editEnabled = false;
                    angular.extend(this, $controller('modalAddEditActionController', {
                        $scope: $scope
                    }));
                    angular.extend(this, $controller('modalAddActionController', {
                        $scope: $scope
                    }));
                }],
                cssClasses: 'yPersonalizationContextModal',
                buttons: modalButtons
            }).then(function(resultContainer) {
                if (personalizationsmarteditContextService.getCombinedView().enabled) {
                    var combinedView = personalizationsmarteditContextService.getCombinedView();
                    combinedView.customize.selectedComponents.push(resultContainer);
                    personalizationsmarteditContextService.setCombinedView(combinedView);
                } else {
                    var customize = personalizationsmarteditContextService.getCustomize();
                    customize.selectedComponents.push(resultContainer);
                    personalizationsmarteditContextService.setCustomize(customize);
                }
                renderService.renderSlots(config.slotsToRefresh);
            }, function() {});
        };

        PersonalizationsmarteditContextModal.prototype.openEditAction = function(config) {
            modalService.open({
                title: "personalization.modal.editaction.title",
                templateUrl: 'personalizationsmarteditAddEditActionTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    angular.merge($scope, config);
                    $scope.modalManager = modalManager;
                    $scope.editEnabled = true;
                    angular.extend(this, $controller('modalAddEditActionController', {
                        $scope: $scope
                    }));
                    angular.extend(this, $controller('modalEditActionController', {
                        $scope: $scope
                    }));
                }],
                cssClasses: 'yPersonalizationContextModal',
                buttons: modalButtons
            }).then(function() {
                renderService.renderSlots(config.slotsToRefresh);
            });
        };

        PersonalizationsmarteditContextModal.prototype.openEditComponentAction = function(config) {
            editorModalService.open(config);
        };

        return new PersonalizationsmarteditContextModal();

    })
    .controller('modalAddEditActionController', function($scope, $filter, $q, $timeout, editorModalService, personalizationsmarteditContextService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, PaginationHelper, slotRestrictionsService, PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING) {

        $scope.actions = [{
            id: "create",
            name: $filter('translate')("personalization.modal.addeditaction.createnewcomponent")
        }, {
            id: "use",
            name: $filter('translate')("personalization.modal.addeditaction.usecomponent")
        }];

        $scope.newComponent = {};
        $scope.component = {};
        $scope.components = [];
        $scope.newComponentTypes = [];

        var initNewComponentTypes = function() {
            slotRestrictionsService.getSlotRestrictions($scope.slotId).then(function(restrictions) {
                personalizationsmarteditRestService.getNewComponentTypes().then(function success(resp) {
                    $scope.newComponentTypes = resp.componentTypes.filter(function(elem) {
                        return restrictions.indexOf(elem.code) > -1;
                    });
                }, function error() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponentstypes'));
                });
            }, function error() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingslotrestrictions'));
            });
        };

        var getAndSetComponentById = function(componentUuid) {
            personalizationsmarteditRestService.getComponent(componentUuid).then(function successCallback(resp) {
                $scope.component.selected = resp;
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponents'));
            });
        };

        $scope.newComponentTypeSelectedEvent = function() {
            var componentAttributes = {
                smarteditComponentType: $scope.newComponent.selected.code,
                catalogVersionUuid: personalizationsmarteditContextService.getSeData().seExperienceData.pageContext.catalogVersionUuid
            };
            editorModalService.open(componentAttributes).then(
                function(response) {
                    $scope.action = {
                        selected: $filter('filter')($scope.actions, {
                            id: "use"
                        }, true)[0]
                    };
                    $scope.componentUuid = response.uuid;
                    getAndSetComponentById($scope.componentUuid);
                },
                function() {
                    $scope.newComponent = {};
                });
        };

        var editAction = function(customizationId, variationId, actionId, componentId, componentCatalog, filter) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.editAction(customizationId, variationId, actionId, componentId, componentCatalog, filter).then(
                function successCallback() {
                    personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.updatingaction'));
                    deferred.resolve();
                },
                function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingaction'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var addActionToContainer = function(componentId, catalogId, containerSourceId, customizationId, variationId, filter) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.addActionToContainer(componentId, catalogId, containerSourceId, customizationId, variationId, filter).then(
                function successCallback() {
                    personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.creatingaction'));
                    deferred.resolve(containerSourceId);
                },
                function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.creatingaction'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var buttonHandlerFn = function(buttonId) {
            if (buttonId === 'submit') {
                $scope.modalManager.disableButton("submit");
                var componentCatalogId = $scope.component.selected.catalogVersion.substring(0, $scope.component.selected.catalogVersion.indexOf('\/'));
                var filter = {
                    catalog: $scope.selectedCustomization.catalog,
                    catalogVersion: $scope.selectedCustomization.catalogVersion
                };
                var extraCatalogFilter = {
                    slotCatalog: $scope.slotCatalog,
                    oldComponentCatalog: $scope.componentCatalog
                };
                angular.extend(extraCatalogFilter, filter);

                if ($scope.editEnabled) {
                    return editAction($scope.selectedCustomization.code, $scope.selectedVariation.code, $scope.actionId, $scope.component.selected.uid, componentCatalogId, filter);
                } else {
                    return personalizationsmarteditRestService.replaceComponentWithContainer($scope.defaultComponentId, $scope.slotId, extraCatalogFilter).then(
                        function successCallback(result) {
                            return addActionToContainer($scope.component.selected.uid, componentCatalogId, result.sourceId, $scope.selectedCustomization.code, $scope.selectedVariation.code, filter);
                        },
                        function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.replacingcomponent'));
                            return $q.defer().reject();
                        });
                }
            }
            return $q.defer().reject();
        };
        $scope.modalManager.setButtonHandler(buttonHandlerFn);

        $scope.componentFilter = {
            name: ''
        };
        var getComponentFilterObject = function() {
            var typeCodes = $scope.newComponentTypes.map(function(elem) {
                return elem.code;
            }).join(",");

            return {
                currentPage: $scope.componentPagination.page + 1,
                mask: $scope.componentFilter.name,
                pageSize: 50,
                sort: 'name',
                catalog: $scope.catalogFilter,
                catalogVersion: $scope.catalogVersionFilter,
                typeCodes: typeCodes
            };
        };

        $scope.componentPagination = new PaginationHelper();
        $scope.componentPagination.reset();
        $scope.moreComponentRequestProcessing = false;

        $scope.addMoreComponentItems = function() {
            if ($scope.componentPagination.page < $scope.componentPagination.totalPages - 1 && !$scope.moreComponentRequestProcessing && $scope.newComponentTypes.length > 0) {
                $scope.moreComponentRequestProcessing = true;
                personalizationsmarteditRestService.getComponents(getComponentFilterObject()).then(function successCallback(resp) {
                    var filteredComponents = resp.response.filter(function(elem) {
                        return !elem.restricted;
                    });
                    Array.prototype.push.apply($scope.components, filteredComponents);
                    $scope.componentPagination = new PaginationHelper(resp.pagination);
                    $scope.moreComponentRequestProcessing = false;
                    if ($scope.components.length < 20) { //not enough components on list to enable scroll
                        $timeout((function() {
                            $scope.addMoreComponentItems();
                        }), 0);
                    }
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponents'));
                    $scope.moreComponentRequestProcessing = false;
                });
            }
        };

        $scope.componentSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            $scope.componentPagination.reset();
            $scope.componentFilter.name = searchObj;
            $scope.components.length = 0;
            $scope.addMoreComponentItems();
        };

        var getAndSetColorAndLetter = function() {
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            if (combinedView.enabled) {
                (combinedView.selectedItems || []).forEach(function(element, index) {
                    var state = $scope.selectedCustomizationCode === element.customization.code;
                    state = state && $scope.selectedVariationCode === element.variation.code;
                    var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
                    if (state) {
                        $scope.letterIndicatorForElement = String.fromCharCode('a'.charCodeAt() + wrappedIndex).toUpperCase();
                        $scope.colorIndicatorForElement = PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
                    }
                });
            }
        };

        $scope.catalogVersionFilerChange = function(value) {
            var arr = value.split("\/");
            $scope.catalogFilter = arr[0];
            $scope.catalogVersionFilter = arr[1];

            $scope.componentPagination.reset();
            $scope.components.length = 0;
            $scope.addMoreComponentItems();
        };

        $scope.$watch('action.selected', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                $scope.component.selected = undefined;
                if ($scope.editEnabled) {
                    getAndSetComponentById($scope.componentUuid);
                }
            }
        }, true);

        $scope.$watch('component.selected', function(newValue) {
            $scope.modalManager.disableButton("submit");
            if (newValue !== undefined) {
                $scope.modalManager.enableButton("submit");
            }
        }, true);

        $scope.$watch('newComponentTypes', function(newValue) {
            if (newValue !== undefined) {
                $scope.componentPagination = new PaginationHelper();
                $scope.componentPagination.reset();

                $scope.moreComponentRequestProcessing = false;

                $scope.addMoreComponentItems();
            }
        }, true);

        //init
        (function() {
            personalizationsmarteditRestService.getCustomization({
                    code: $scope.selectedCustomizationCode
                })
                .then(function successCallback(response) {
                    $scope.selectedCustomization = response;
                    $scope.selectedVariation = response.variations.filter(function(elem) {
                        return elem.code === $scope.selectedVariationCode;
                    })[0];
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                });

            if ($scope.editEnabled) {
                getAndSetComponentById($scope.componentUuid);
            }

            initNewComponentTypes();
            getAndSetColorAndLetter();
        })();

    })
    .controller('modalAddActionController', function($scope) {
        $scope.action = {};
    })
    .controller('modalEditActionController', function($scope, $filter) {
        $scope.action = {
            selected: $filter('filter')($scope.actions, {
                id: "use"
            }, true)[0]
        };
    })
    .controller('modalDeleteActionController', function($scope, $q, personalizationsmarteditRestService) {
        var buttonHandlerFn = function(buttonId) {
            if (buttonId === 'confirmOk') {
                var filter = {
                    catalog: $scope.catalog,
                    catalogVersion: $scope.catalogVersion
                };
                return personalizationsmarteditRestService.deleteAction($scope.selectedCustomizationCode, $scope.selectedVariationCode, $scope.actionId, filter);
            }
            return $q.defer().reject();
        };
        $scope.modalManager.setButtonHandler(buttonHandlerFn);
    });
