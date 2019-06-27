angular.module('personalizationsmarteditManagerViewModule', [
        'modalServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'personalizationsmarteditServicesModule',
        'confirmationModalServiceModule',
        'personalizationsmarteditManageCustomizationViewModule',
        'personalizationsmarteditCommerceCustomizationModule',
        'smarteditCommonsModule',
        'personalizationsmarteditDataFactory',
        'personalizationsmarteditCommonsModule',
        'waitDialogServiceModule',
        'ui.tree'
    ])
    .factory('personalizationsmarteditManagerView', function(modalService) {
        var manager = {};
        manager.openManagerAction = function() {
            modalService.open({
                title: "personalization.modal.manager.title",
                templateUrl: 'personalizationsmarteditManagerViewTemplate.html',
                controller: 'personalizationsmarteditManagerViewController',
                size: 'fullscreen',
                cssClasses: 'perso-library'
            }).then(function() {

            }, function() {});
        };

        return manager;
    })
    .controller('personalizationsmarteditManagerViewController', function($q, $scope, $filter, $timeout, confirmationModalService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditContextService, personalizationsmarteditManager, systemEventService, personalizationsmarteditUtils, personalizationsmarteditDateUtils, personalizationsmarteditCommerceCustomizationView, personalizationsmarteditCommerceCustomizationService, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES, PERSONALIZATION_MODEL_STATUS_CODES, PaginationHelper, waitDialogService) { //NOSONAR

        var getCustomizations = function(filter) {
            personalizationsmarteditRestService.getCustomizations(filter)
                .then(function successCallback(response) {
                    Array.prototype.push.apply($scope.customizations, response.customizations);
                    $scope.filteredCustomizationsCount = response.pagination.totalCount;
                    $scope.pagination = new PaginationHelper(response.pagination);
                    $scope.moreCustomizationsRequestProcessing = false;
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
                    $scope.moreCustomizationsRequestProcessing = false;
                });
        };

        var getCustomizationsFilterObject = function() {
            return {
                active: "all",
                name: $scope.search.name,
                currentSize: $scope.pagination.count,
                currentPage: $scope.pagination.page + 1,
                statuses: $scope.search.status.modelStatuses
            };
        };

        var refreshGrid = function() {
            $scope.pagination.reset();
            $scope.customizations.length = 0;
            $scope.addMoreItems();
        };

        var getDefaultStatus = function() {
            return $scope.statuses.filter(function(elem) {
                return elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL;
            })[0];
        };

        var updateCustomizationRank = function(customizationCode, increaseValue) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.updateCustomizationRank(customizationCode, increaseValue)
                .then(function successCallback() {
                    deferred.resolve();
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingcustomization'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var updateVariationRank = function(customization, variationCode, increaseValue) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.getVariation(customization.code, variationCode)
                .then(function successCallback(responseVariation) {
                    responseVariation.rank = responseVariation.rank + increaseValue;
                    personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                        .then(function successCallback() {
                            deferred.resolve();
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.editingvariation'));
                            deferred.reject();
                        });
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingvariation'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var updateRanks = function(item, nextItem, itemsArray, event, increaseValue) {
            var startIndex = (increaseValue > 0) ? event.source.index : event.dest.index;
            var endIndex = (increaseValue > 0) ? event.dest.index : event.source.index;
            itemsArray[event.dest.index].rank = nextItem.rank;
            for (var i = startIndex; i <= endIndex; i++) {
                if (i !== event.dest.index) {
                    itemsArray[i].rank += (increaseValue > 0) ? (-1) : 1;
                }
            }
        };

        var valideRanks = function(itemsArray) {
            for (var j = 0; j < itemsArray.length - 1; j++) {
                if (itemsArray[j].rank > itemsArray[j + 1].rank) {
                    refreshGrid();
                    break;
                }
            }
        };

        var getActionsForVariation = function(variationCode, variationsArray) {
            variationsArray = variationsArray || [];
            var variation = variationsArray.filter(function(elem) {
                return variationCode === elem.code;
            })[0];
            return variation ? variation.actions : [];
        };

        var droppedCustomization = function(item, e) {
            var nextItem = $scope.customizations[e.dest.index];
            var increaseValue = nextItem.rank - item.rank;
            if (increaseValue !== 0) {
                waitDialogService.showWaitModal();
                updateCustomizationRank(item.code, increaseValue)
                    .then(function successCallback() {
                        updateRanks(item, nextItem, $scope.customizations, e, increaseValue);
                        $timeout(function() {
                            valideRanks($scope.customizations);
                        }, 100);
                    }).finally(function() {
                        waitDialogService.hideWaitModal();
                    });
            }
        };

        var droppedTargetGroup = function(item, e) {
            var variationsArray = e.source.nodesScope.$modelValue;
            var nextItem = variationsArray[e.dest.index];
            var increaseValue = nextItem.rank - item.rank;
            var customization = e.source.nodesScope.$parent.$modelValue;
            if (increaseValue !== 0) {
                waitDialogService.showWaitModal();
                updateVariationRank(customization, item.code, increaseValue)
                    .then(function successCallback() {
                        updateRanks(item, nextItem, customization.variations, e, increaseValue);
                        $timeout(function() {
                            valideRanks(customization.variations);
                        }, 100);
                    }).finally(function() {
                        waitDialogService.hideWaitModal();
                    });
            }
        };

        var seExperienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
        var currentLanguageIsocode = seExperienceData.languageDescriptor.isocode;
        $scope.catalogName = seExperienceData.catalogDescriptor.name[currentLanguageIsocode];
        $scope.catalogName += " - " + seExperienceData.catalogDescriptor.catalogVersion;
        $scope.customizations = [];
        $scope.filteredCustomizationsCount = 0;
        $scope.scrollZoneElement = undefined;

        $scope.statuses = personalizationsmarteditUtils.getStatusesMapping();

        $scope.search = {
            name: '',
            status: getDefaultStatus()
        };

        $scope.moreCustomizationsRequestProcessing = false;
        $scope.addMoreItems = function() {
            if ($scope.pagination.page < $scope.pagination.totalPages - 1 && !$scope.moreCustomizationsRequestProcessing) {
                $scope.moreCustomizationsRequestProcessing = true;
                getCustomizations(getCustomizationsFilterObject());
            }
        };

        $scope.pagination = new PaginationHelper();
        $scope.pagination.reset();

        $scope.searchInputKeypress = function(keyEvent) {
            if (keyEvent.which === 13 || $scope.search.name.length > 2 || $scope.search.name.length === 0) {
                refreshGrid();
            }
        };

        $scope.resetSearchInput = function() {
            $scope.search.name = "";
            $scope.search.status = getDefaultStatus();
            refreshGrid();
        };

        $scope.editCustomizationAction = function(customization) {
            personalizationsmarteditManager.openEditCustomizationModal(customization.code);
        };
        $scope.deleteCustomizationAction = function(customization) {
            confirmationModalService.confirm({
                description: 'personalization.modal.manager.deletecustomization.content'
            }).then(function() {
                personalizationsmarteditRestService.getCustomization(customization)
                    .then(function successCallback(responseCustomization) {
                        responseCustomization.status = "DELETED";
                        personalizationsmarteditRestService.updateCustomization(responseCustomization)
                            .then(function successCallback() {
                                $scope.customizations.splice($scope.customizations.indexOf(customization), 1);
                            }, function errorCallback() {
                                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingcustomization'));
                            });
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingcustomization'));
                    });
            });
        };
        $scope.editVariationAction = function(customization, variation) {
            personalizationsmarteditManager.openEditCustomizationModal(customization.code, variation.code);
        };

        $scope.isCommerceCustomizationEnabled = function() {
            return personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(personalizationsmarteditContextService.getSeData().seConfigurationData);
        };

        $scope.manageCommerceCustomization = function(customization, variation) {
            personalizationsmarteditCommerceCustomizationView.openCommerceCustomizationAction(customization, variation);
        };

        $scope.isDeleteVariationEnabled = function(customization) {
            return personalizationsmarteditUtils.getVisibleItems(customization.variations).length > 1;
        };

        $scope.deleteVariationAction = function(customization, variation, $event) {
            if ($scope.isDeleteVariationEnabled(customization)) {
                confirmationModalService.confirm({
                    description: 'personalization.modal.manager.deletevariation.content'
                }).then(function() {
                    personalizationsmarteditRestService.getVariation(customization.code, variation.code)
                        .then(function successCallback(responseVariation) {
                            responseVariation.status = "DELETED";
                            personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                                .then(function successCallback(response) {
                                    variation.status = response.status;
                                }, function errorCallback() {
                                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingvariation'));
                                });
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingvariation'));
                        });
                });
            } else {
                $event.stopPropagation();
            }
        };

        $scope.openNewModal = function() {
            personalizationsmarteditManager.openCreateCustomizationModal();
        };

        $scope.toogleVariationActive = function(customization, variation) {
            personalizationsmarteditRestService.getVariation(customization.code, variation.code)
                .then(function successCallback(responseVariation) {
                    responseVariation.enabled = !responseVariation.enabled;
                    responseVariation.status = responseVariation.enabled ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
                    personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                        .then(function successCallback(response) {
                            variation.enabled = response.enabled;
                            variation.status = response.status;
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.editingvariation'));
                        });
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingvariation'));
                });
        };

        $scope.isUndefined = function(value) {
            return value === undefined;
        };

        $scope.customizationClickAction = function(customization) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.getVariationsForCustomization(customization.code, customization).then(
                function successCallback(response) {
                    customization.variations.forEach(function(variation) {
                        variation.actions = getActionsForVariation(variation.code, response.variations);
                        variation.numberOfComponents = personalizationsmarteditCommerceCustomizationService.getNonCommerceActionsCount(variation);
                        variation.commerceCustomizations = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                        variation.numberOfCommerceActions = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                    });
                    deferred.resolve();
                },
                function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        $scope.hasCommerceActions = function(variation) {
            return personalizationsmarteditUtils.hasCommerceActions(variation);
        };

        $scope.getCommerceCustomizationTooltip = function(variation) {
            return personalizationsmarteditUtils.getCommerceCustomizationTooltipHTML(variation);
        };

        $scope.getFormattedDate = function(myDate) {
            if (myDate) {
                return personalizationsmarteditDateUtils.formatDate(myDate);
            } else {
                return "";
            }
        };

        $scope.getEnablementTextForCustomization = function(customization) {
            return personalizationsmarteditUtils.getEnablementTextForCustomization(customization, 'personalization.modal.manager');
        };

        $scope.getEnablementTextForVariation = function(variation) {
            return personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.modal.manager');
        };

        $scope.getEnablementActionTextForVariation = function(variation) {
            return personalizationsmarteditUtils.getEnablementActionTextForVariation(variation, 'personalization.modal.manager');
        };

        $scope.getActivityStateForCustomization = function(customization) {
            return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
        };

        $scope.getActivityStateForVariation = function(customization, variation) {
            return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
        };

        $scope.allCustomizationsCollapsed = function() {
            return $scope.customizations.map(function(elem) {
                return elem.isCollapsed;
            }).reduce(function(previousValue, currentValue) {
                return previousValue && currentValue;
            }, true);
        };

        $scope.isReturnToTopButtonVisible = function() {
            return $scope.scrollZoneElement.scrollTop() > 50;
        };

        $scope.scrollZoneReturnToTop = function() {
            $scope.scrollZoneElement.animate({
                scrollTop: 0
            }, 500);
        };

        $scope.treeOptions = {
            dragStart: function(e) {
                $scope.scrollZoneVisible = $scope.isScrollZoneVisible();
                e.source.nodeScope.$modelValue.isDragging = true;
            },
            dragStop: function(e) {
                e.source.nodeScope.$modelValue.isDragging = undefined;
            },
            dropped: function(e) {
                $scope.scrollZoneVisible = false;
                var item = e.source.nodeScope.$modelValue;

                if (e.source.index === e.dest.index) {
                    return; // Element didn't change position
                } else if (item.variations) { //Customization
                    droppedCustomization(item, e);
                } else { //Target group
                    droppedTargetGroup(item, e);
                }
            },
            accept: function(sourceNodeScope, destNodesScope) {
                if (angular.isArray(destNodesScope.$modelValue) && destNodesScope.$modelValue.indexOf(sourceNodeScope.$modelValue) > -1) {
                    return true;
                }
                return false;
            }
        };

        $scope.isScrollZoneVisible = function() {
            return $scope.scrollZoneElement.get(0).scrollHeight > $scope.scrollZoneElement.get(0).clientHeight;
        };

        $scope.isSearchResultHidden = function() {
            return $scope.scrollZoneElement.scrollTop() >= 125;
        };

        $scope.isSearchGridHeaderHidden = function() {
            return $scope.scrollZoneElement.scrollTop() >= 220;
        };

        $scope.setScrollZoneElement = function(element) {
            $scope.scrollZoneElement = element;
        };

        $scope.getElementToScroll = function() {
            return $scope.scrollZoneElement;
        };

        $scope.statusNotDeleted = function(variation) {
            return personalizationsmarteditUtils.isItemVisible(variation);
        };

        $scope.isFilterEnabled = function() {
            return $scope.search.name !== '' || $scope.search.status !== getDefaultStatus();
        };

        $scope.setCustomizationRank = function(customization, increaseValue) {
            var nextItem = $scope.customizations[$scope.customizations.indexOf(customization) + increaseValue];
            waitDialogService.showWaitModal();
            updateCustomizationRank(customization.code, nextItem.rank - customization.rank)
                .then(function successCallback() {
                    customization.rank += increaseValue;
                    nextItem.rank -= increaseValue;
                    var index = $scope.customizations.indexOf(customization);
                    var tempItem = $scope.customizations.splice(index, 1);
                    $scope.customizations.splice(index + increaseValue, 0, tempItem[0]);
                }).finally(function() {
                    waitDialogService.hideWaitModal();
                });
        };

        $scope.setVariationRank = function(customization, variation, increaseValue) {
            var nextItem = customization.variations[customization.variations.indexOf(variation) + increaseValue];
            waitDialogService.showWaitModal();
            updateVariationRank(customization, variation.code, increaseValue)
                .then(function successCallback() {
                    variation.rank += increaseValue;
                    nextItem.rank -= increaseValue;
                    var index = customization.variations.indexOf(variation);
                    var tempItem = customization.variations.splice(index, 1);
                    customization.variations.splice(index + increaseValue, 0, tempItem[0]);
                }).finally(function() {
                    waitDialogService.hideWaitModal();
                });
        };

        $scope.$watch('search.status', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                refreshGrid();
            }
        }, true);

        //init
        (function() {
            systemEventService.registerEventHandler('CUSTOMIZATIONS_MODIFIED', function() {
                refreshGrid();
                return $q.when();
            });
        })();

    });
