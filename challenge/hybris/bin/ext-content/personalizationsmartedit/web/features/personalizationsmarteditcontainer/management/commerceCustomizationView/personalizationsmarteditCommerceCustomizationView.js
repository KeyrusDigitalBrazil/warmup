angular.module('personalizationsmarteditCommerceCustomizationModule', [
        'modalServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'smarteditCommonsModule',
        'personalizationsmarteditServicesModule',
        'confirmationModalServiceModule'
    ])
    .constant('PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES', {
        OLD: 'old',
        NEW: 'new',
        DELETE: 'delete',
        UPDATE: 'update'
    })
    .factory(
        'personalizationsmarteditCommerceCustomizationView',
        function($controller, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES) {
            var manager = {};
            manager.openCommerceCustomizationAction = function(customization, variation) {
                modalService.open({
                    title: "personalization.modal.commercecustomization.title",
                    templateUrl: 'personalizationsmarteditCommerceCustomizationViewTemplate.html',
                    controller: ['$scope', 'modalManager', function($scope, modalManager) {
                        $scope.customization = customization;
                        $scope.variation = variation;
                        $scope.modalManager = modalManager;
                        angular.extend(this, $controller('personalizationsmarteditCommerceCustomizationViewController', {
                            $scope: $scope
                        }));
                    }],
                    buttons: [{
                        id: 'confirmCancel',
                        label: 'personalization.modal.commercecustomization.button.cancel',
                        style: MODAL_BUTTON_STYLES.SECONDARY,
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }, {
                        id: 'confirmSave',
                        label: 'personalization.modal.commercecustomization.button.submit',
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }]
                }).then(function() {

                }, function() {});
            };

            return manager;
        })
    .factory('actionsDataFactory', function(PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES) {
        var factory = {};

        factory.actions = [];
        factory.removedActions = [];

        factory.getActions = function() {
            return factory.actions;
        };

        factory.getRemovedActions = function() {
            return factory.removedActions;
        };

        factory.resetActions = function() {
            factory.actions.length = 0;
        };

        factory.resetRemovedActions = function() {
            factory.removedActions.length = 0;
        };

        // This function requires two parameters
        // action to be added
        // and comparer = function(action,action) for defining if two actions are identical
        // comparer is used
        factory.addAction = function(action, comparer) {

            var exist = false;
            factory.actions.forEach(function(wrapper) {
                exist = exist || comparer(action, wrapper.action);
            });
            if (!exist) {
                var status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW;
                var removedIndex = -1;
                factory.removedActions.forEach(function(wrapper, index) {
                    if (comparer(action, wrapper.action)) {
                        removedIndex = index;
                    }
                });
                if (removedIndex >= 0) { //we found or action in delete queue
                    status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD;
                    factory.removedActions.splice(removedIndex, 1);
                }
                factory.actions.push({
                    action: action,
                    status: status
                });
            }
        };

        factory.isItemInSelectedActions = function(action, comparer) {
            return factory.actions.find(function(wrapper) {
                return comparer(action, wrapper.action);
            });
        };

        return factory;
    })
    .controller(
        'personalizationsmarteditCommerceCustomizationViewController',
        function(
            $scope,
            $filter,
            $q,
            $log,
            actionsDataFactory,
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            systemEventService,
            personalizationsmarteditCommerceCustomizationService,
            personalizationsmarteditContextService,
            personalizationsmarteditUtils,
            confirmationModalService,
            PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES) {

            $scope.availableTypes = [];
            $scope.select = {};

            actionsDataFactory.resetActions();
            actionsDataFactory.resetRemovedActions();
            $scope.actions = actionsDataFactory.getActions();
            $scope.removedActions = actionsDataFactory.getRemovedActions();

            var populateActions = function() {
                personalizationsmarteditRestService.getActions($scope.customization.code, $scope.variation.code)
                    .then(function successCallback(response) {

                        var actions = response.actions.filter(function(elem) {
                            return elem.type !== 'cxCmsActionData';
                        }).map(function(item) {
                            return {
                                action: item,
                                status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD
                            };
                        });
                        actionsDataFactory.resetActions();
                        Array.prototype.push.apply(actionsDataFactory.getActions(), actions);
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingactions'));
                    });
            };

            var getType = function(type) {
                for (var i = 0; i < $scope.availableTypes.length; ++i) {
                    if ($scope.availableTypes[i].type === type) {
                        return $scope.availableTypes[i];
                    }
                }
                return {};
            };

            var sendRefreshEvent = function() {
                systemEventService.sendSynchEvent('CUSTOMIZATIONS_MODIFIED', {});
            };

            var dismissModalCallback = function() {
                if ($scope.isDirty()) {
                    return confirmationModalService.confirm({
                        description: 'personalization.modal.commercecustomization.cancelconfirmation'
                    }).then(function() {
                        return $q.resolve();
                    }, function() {
                        return $q.reject();
                    });
                } else {
                    return $q.resolve();
                }
            };

            $scope.getActionsToDisplay = function() {
                return actionsDataFactory.getActions();
            };

            $scope.isItemInSelectedActions = function(action, comparer) {
                return actionsDataFactory.isItemInSelectedActions(action, comparer);
            };

            $scope.displayAction = function(actionWrapper) {
                var action = actionWrapper.action;
                var type = getType(action.type);
                if (type.getName) {
                    return type.getName(action);
                } else {
                    return action.code;
                }
            };

            // This function requires two parameters
            // action to be added
            // and comparer = function(action,action) for defining if two actions are identical
            // comparer is used
            $scope.addAction = function(action, comparer) {
                actionsDataFactory.addAction(action, comparer);
            };

            $scope.removeSelectedAction = function(actionWrapper) {
                var index = $scope.actions.indexOf(actionWrapper);
                if (index < 0) {
                    return;
                }
                var removed = $scope.actions.splice(index, 1);
                //only old item should be added to delete queue
                //new items are just deleted
                if (removed[0].status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD ||
                    removed[0].status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE) {
                    removed[0].status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE;
                    $scope.removedActions.push(removed[0]);
                }
            };

            $scope.isDirty = function() {
                var dirty = false;
                //dirty if at least one new
                $scope.actions.forEach(function(wrapper) {
                    dirty = dirty || wrapper.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW ||
                        wrapper.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
                });
                //or one deleted
                dirty = dirty || $scope.removedActions.length > 0;
                return dirty;
            };

            // customization and variation status helper fucntions
            $scope.customizationStatusText = personalizationsmarteditUtils.getEnablementTextForCustomization($scope.customization, 'personalization.modal.commercecustomization');
            $scope.variationStatusText = personalizationsmarteditUtils.getEnablementTextForVariation($scope.variation, 'personalization.modal.commercecustomization');
            $scope.customizationStatus = personalizationsmarteditUtils.getActivityStateForCustomization($scope.customization);
            $scope.variationStatus = personalizationsmarteditUtils.getActivityStateForVariation($scope.customization, $scope.variation);

            var getActionTypesForActions = function(actions) {
                return actions.map(function(a) {
                    return a.type;
                }).filter(function(item, index, arr) {
                    //removes duplicates from mapped array
                    return arr.indexOf(item) === index;
                }).map(function(typeCode) {
                    return $scope.availableTypes.filter(function(availableType) {
                        return availableType.type === typeCode;
                    })[0];
                });
            };

            var createActions = function(customizationCode, variationCode, createData) {
                var deferred = $q.defer();
                personalizationsmarteditRestService.createActions(customizationCode, variationCode, createData)
                    .then(function successCallback(response) {
                        personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.creatingaction'));
                        sendRefreshEvent();
                        deferred.resolve(response);
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.creatingaction'));
                        deferred.reject();
                    });
                return deferred.promise;
            };

            var deleteActions = function(customizationCode, variationCode, deleteData) {
                var deferred = $q.defer();
                personalizationsmarteditRestService.deleteActions(customizationCode, variationCode, deleteData)
                    .then(function successCallback(response) {
                        personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.removingaction'));
                        sendRefreshEvent();
                        deferred.resolve(response);
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.removingaction'));
                        deferred.resolve();
                    });
                return deferred.promise;
            };

            var updateActions = function(customizationCode, variationCode, updateData, respCreate, respDelete) {
                var updateTypes = getActionTypesForActions(updateData.actions);

                updateTypes.forEach(function(type) {
                    if (type.updateActions) {
                        var actionsForType = updateData.actions.filter(function(a) {
                            return getType(a.type) === type;
                        });
                        type.updateActions(customizationCode, variationCode, actionsForType, respCreate, respDelete)
                            .then(function successCallback() {
                                personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.updatingactions'));
                                sendRefreshEvent();
                            }, function errorCallback() {
                                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingactions'));
                            });

                    } else {
                        $log.debug($filter('translate')('personalization.error.noupdatingactions'));
                    }
                });
            };

            // modal buttons
            $scope.onSave = function() {
                var createData = {
                    actions: $scope.actions.filter(function(item) {
                        return item.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW;
                    }).map(function(item) {
                        return item.action;
                    })
                };

                var deleteData = $scope.removedActions.filter(function(item) {
                    return item.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE;
                }).map(function(item) {
                    return item.action.code;
                });

                var updateData = {
                    actions: $scope.actions.filter(function(item) {
                        return item.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
                    }).map(function(item) {
                        return item.action;
                    })
                };

                var shouldCreate = createData.actions.length > 0;
                var shouldDelete = deleteData.length > 0;
                var shouldUpdate = updateData.actions.length > 0;

                (function() {
                    if (shouldCreate) {
                        return createActions($scope.customization.code, $scope.variation.code, createData);
                    } else {
                        return $q.resolve();
                    }
                })().then(function(respCreate) {
                    (function() {
                        if (shouldDelete) {
                            return deleteActions($scope.customization.code, $scope.variation.code, deleteData);
                        } else {
                            return $q.resolve();
                        }
                    })().then(function(respDelete) {
                        if (shouldUpdate) {
                            updateActions($scope.customization.code, $scope.variation.code, updateData, respCreate, respDelete);
                        }
                    });
                });

            };

            $scope.$watch('actions', function() {
                if ($scope.isDirty()) {
                    $scope.modalManager.enableButton("confirmSave");
                } else {
                    $scope.modalManager.disableButton("confirmSave");
                }
            }, true);

            $scope.modalManager.setButtonHandler(function(buttonId) {
                if (buttonId === 'confirmSave') {
                    $scope.onSave();
                } else if (buttonId === 'confirmCancel') {
                    return dismissModalCallback();
                }
            });

            $scope.modalManager.setDismissCallback(function() {
                return dismissModalCallback();
            });

            //init
            (function() {
                $scope.availableTypes = personalizationsmarteditCommerceCustomizationService.getAvailableTypes(personalizationsmarteditContextService.getSeData().seConfigurationData);
                $scope.select = {
                    type: $scope.availableTypes[0]
                };
                populateActions();
            })();
        });
