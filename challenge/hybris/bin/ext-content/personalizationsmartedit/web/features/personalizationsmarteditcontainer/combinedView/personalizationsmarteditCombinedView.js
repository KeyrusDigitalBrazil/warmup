angular.module('personalizationsmarteditCombinedViewModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'ui.select',
        'personalizationsmarteditServicesModule',
        'renderServiceModule',
        'personalizationsmarteditDataFactory',
        'modalServiceModule',
        'personalizationsmarteditPreviewServiceModule',
        'pageFilterDropdownModule',
        'catalogFilterDropdownModule',
        'hasMulticatalogModule',
        'componentMenuServiceModule',
        'permissionServiceModule'
    ])
    .factory('personalizationsmarteditCombinedView',
        function(
            $controller,
            modalService,
            MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES,
            personalizationsmarteditContextService,
            personalizationsmarteditCombinedViewCommons,
            personalizationsmarteditContextUtils) {

            var manager = {};
            manager.openManagerAction = function() {
                modalService.open({
                    title: "personalization.modal.combinedview.title",
                    templateUrl: 'personalizationsmarteditCombinedViewConfigureTemplate.html',
                    controller: ['$scope', 'modalManager', function($scope, modalManager) {
                        $scope.modalManager = modalManager;
                        angular.extend(this, $controller('personalizationsmarteditCombinedViewController', {
                            $scope: $scope
                        }));
                    }],
                    buttons: [{
                        id: 'confirmCancel',
                        label: 'personalization.modal.combinedview.button.cancel',
                        style: MODAL_BUTTON_STYLES.SECONDARY,
                        action: MODAL_BUTTON_ACTIONS.DISMISS
                    }, {
                        id: 'confirmOk',
                        label: 'personalization.modal.combinedview.button.ok',
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }]
                }).then(function() {
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
                    if (personalizationsmarteditContextService.getCombinedView().enabled) {
                        personalizationsmarteditCombinedViewCommons.updatePreview(personalizationsmarteditCombinedViewCommons.getVariationsForPreviewTicket());
                    }
                }, function() {});
            };

            return manager;
        })
    .factory('personalizationsmarteditCombinedViewCommons',
        function(
            $q,
            $filter,
            personalizationsmarteditContextService,
            personalizationsmarteditPreviewService,
            personalizationsmarteditRestService,
            personalizationsmarteditUtils) {

            var service = {};

            var updateActionsOnSelectedVariations = function() {
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                var promissesArray = [];
                (combinedView.selectedItems || []).forEach(function(item) {
                    promissesArray.push(personalizationsmarteditRestService.getActions(item.customization.code, item.variation.code, item.variation).then(function successCallback(response) {
                        item.variation.actions = response.actions;
                    }));
                });
                $q.all(promissesArray).then(function() {
                    personalizationsmarteditContextService.setCombinedView(combinedView);
                });
            };

            service.updatePreview = function(previewTicketVariations) {
                personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicketVariations);
                updateActionsOnSelectedVariations();
            };

            service.getVariationsForPreviewTicket = function() {
                var previewTicketVariations = [];
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                (combinedView.selectedItems || []).forEach(function(item) {
                    previewTicketVariations.push({
                        customizationCode: item.customization.code,
                        variationCode: item.variation.code,
                        catalog: item.variation.catalog,
                        catalogVersion: item.variation.catalogVersion
                    });
                });
                return previewTicketVariations;
            };

            service.combinedViewEnabledEvent = function(isEnabled) {
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                combinedView.enabled = isEnabled;
                personalizationsmarteditContextService.setCombinedView(combinedView);
                var customize = personalizationsmarteditContextService.getCustomize();
                customize.selectedCustomization = null;
                customize.selectedVariations = null;
                customize.selectedComponents = null;
                personalizationsmarteditContextService.setCustomize(customize);
                if (isEnabled) {
                    service.updatePreview(service.getVariationsForPreviewTicket());
                } else {
                    service.updatePreview([]);
                }
            };

            service.isItemFromCurrentCatalog = function(item) {
                return personalizationsmarteditUtils.isItemFromCurrentCatalog(item, personalizationsmarteditContextService.getSeData());
            };

            return service;
        })
    .controller('personalizationsmarteditCombinedViewMenuController',
        function(
            $scope,
            $filter,
            personalizationsmarteditUtils,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditRestService,
            personalizationsmarteditContextService,
            personalizationsmarteditCombinedViewCommons,
            permissionService) {

            $scope.combinedView = personalizationsmarteditContextService.getCombinedView();
            $scope.selectedItems = $scope.combinedView.selectedItems || [];

            var getAndSetComponentsForElement = function(customizationId, variationId, catalog, catalogVersion) {
                personalizationsmarteditRestService.getComponenentsIdsForVariation(customizationId, variationId, catalog, catalogVersion).then(function successCallback(response) {
                    var combinedView = personalizationsmarteditContextService.getCombinedView();
                    combinedView.customize.selectedComponents = response.components;
                    personalizationsmarteditContextService.setCombinedView(combinedView);
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponentsforvariation'));
                });
            };

            $scope.itemClick = function(item) {
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                if (!combinedView.enabled) {
                    return;
                }

                $scope.selectedItems.forEach(function(elem) {
                    elem.highlighted = false;
                });
                item.highlighted = true;

                combinedView.customize.selectedCustomization = item.customization;
                combinedView.customize.selectedVariations = item.variation;
                personalizationsmarteditContextService.setCombinedView(combinedView);
                permissionService.isPermitted([{
                    names: ['se.edit.page']
                }]).then(function(roleGranted) {
                    if (roleGranted) {
                        getAndSetComponentsForElement(item.customization.code, item.variation.code, item.customization.catalog, item.customization.catalogVersion);
                    }
                });
                personalizationsmarteditCombinedViewCommons.updatePreview(personalizationsmarteditUtils.getVariationKey(item.customization.code, [item.variation]));
            };

            $scope.getClassForElement = function(index) {
                return personalizationsmarteditUtils.getClassForElement(index);
            };
            $scope.getLetterForElement = function(index) {
                return personalizationsmarteditUtils.getLetterForElement(index);
            };

            $scope.combinedViewEnabledChangeEvent = function() {
                personalizationsmarteditCombinedViewCommons.combinedViewEnabledEvent($scope.combinedView.enabled);
            };

            $scope.isItemFromCurrentCatalog = function(item) {
                return personalizationsmarteditCombinedViewCommons.isItemFromCurrentCatalog(item);
            };

            $scope.$watch('combinedView.selectedItems', function(newValue, oldValue) {
                if (newValue !== oldValue) {
                    $scope.selectedItems = $scope.combinedView.selectedItems || [];
                }
            }, true);

        })
    .controller('personalizationsmarteditCombinedViewController',
        function(
            $q,
            $scope,
            $filter,
            customizationDataFactory,
            PaginationHelper,
            personalizationsmarteditCombinedViewCommons,
            personalizationsmarteditContextService,
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditUtils,
            PERSONALIZATION_VIEW_STATUS_MAPPING_CODES,
            PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER,
            componentMenuService) {

            customizationDataFactory.resetData();

            var successCallback = function(response) {
                $scope.pagination = new PaginationHelper(response.pagination);
                $scope.selectionArray.length = 0;
                customizationDataFactory.items.map(function(customization) {
                    customization.variations.filter(function(variation) {
                        return personalizationsmarteditUtils.isItemVisible(variation);
                    }).forEach(function(variation) {
                        $scope.selectionArray.push({
                            customization: {
                                code: customization.code,
                                name: customization.name,
                                rank: customization.rank,
                                catalog: customization.catalog,
                                catalogVersion: customization.catalogVersion
                            },
                            variation: {
                                code: variation.code,
                                name: variation.name,
                                catalog: variation.catalog,
                                catalogVersion: variation.catalogVersion
                            }
                        });
                    });
                });
                $scope.moreCustomizationsRequestProcessing = false;
            };

            var errorCallback = function() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
                $scope.moreCustomizationsRequestProcessing = false;
            };

            var getDefaultStatus = function() {
                return personalizationsmarteditUtils.getStatusesMapping().filter(function(elem) {
                    return elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL;
                })[0];
            };

            var getCustomizationsFilterObject = function() {
                var ret = {
                    currentSize: $scope.pagination.count,
                    currentPage: $scope.pagination.page + 1,
                    name: $scope.customizationFilter.name,
                    statuses: getDefaultStatus().modelStatuses,
                    catalogs: $scope.catalogFilter
                };
                if ($scope.customizationPageFilter === PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE) {
                    ret.pageId = personalizationsmarteditContextService.getSeData().pageId;
                    ret.pageCatalogId = personalizationsmarteditContextService.getSeData().seExperienceData.pageContext.catalogId;
                }
                return ret;
            };

            var getCustomizations = function(categoryFilter) {
                var params = {
                    filter: categoryFilter,
                    dataArrayName: 'customizations'
                };
                customizationDataFactory.updateData(params, successCallback, errorCallback);
            };

            $scope.pagination = new PaginationHelper();
            $scope.pagination.reset();

            $scope.combinedView = personalizationsmarteditContextService.getCombinedView();
            $scope.selectedItems = [];
            angular.copy($scope.combinedView.selectedItems || [], $scope.selectedItems);
            $scope.selectedElement = {};
            $scope.selectionArray = [];

            $scope.moreCustomizationsRequestProcessing = false;
            $scope.addMoreItems = function() {
                if ($scope.pagination.page < $scope.pagination.totalPages - 1 && !$scope.moreCustomizationsRequestProcessing) {
                    $scope.moreCustomizationsRequestProcessing = true;
                    getCustomizations(getCustomizationsFilterObject());
                }
            };

            $scope.selectElement = function(item) {
                $scope.selectedItems.push(item);

                componentMenuService.getValidContentCatalogVersions().then(function(catalogVersions) {
                    var catalogsUuids = catalogVersions.map(function(elem) {
                        return elem.id;
                    });
                    $scope.selectedItems.sort(function(a, b) {
                        var aCatalogUuid = a.customization.catalog + "/" + a.customization.catalogVersion;
                        var bCatalogUuid = b.customization.catalog + "/" + b.customization.catalogVersion;
                        if (aCatalogUuid === bCatalogUuid) {
                            return a.customization.rank - b.customization.rank;
                        }
                        return catalogsUuids.indexOf(bCatalogUuid) - catalogsUuids.indexOf(aCatalogUuid);
                    });
                });

                $scope.selectedElement = null;
                $scope.searchInputKeypress(null, '');
            };

            $scope.initUiSelect = function(uiSelectController) {
                uiSelectController.isActive = function() {
                    return false;
                };
            };

            $scope.removeSelectedItem = function(item) {
                $scope.selectedItems.splice($scope.selectedItems.indexOf(item), 1);
                $scope.selectedElement = null;
                $scope.searchInputKeypress(null, '');
            };

            $scope.getClassForElement = function(index) {
                return personalizationsmarteditUtils.getClassForElement(index);
            };
            $scope.getLetterForElement = function(index) {
                return personalizationsmarteditUtils.getLetterForElement(index);
            };

            $scope.isItemInSelectDisabled = function(item) {
                return $scope.selectedItems.find(function(currentItem) {
                    return currentItem.customization.code === item.customization.code;
                });
            };

            $scope.isItemSelected = function(item) {
                return $scope.selectedItems.find(function(currentItem) {
                    return currentItem.customization.code === item.customization.code && currentItem.variation.code === item.variation.code;
                });
            };

            $scope.customizationFilter = {
                name: ''
            };

            $scope.searchInputKeypress = function(keyEvent, searchObj) {
                if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                    return;
                }
                $scope.pagination.reset();
                $scope.customizationFilter.name = searchObj;
                customizationDataFactory.resetData();
                $scope.addMoreItems();
            };

            var isCombinedViewContextPersRemoved = function(combinedView) {
                return combinedView.selectedItems.filter(function(item) {
                    return item.customization.code === combinedView.customize.selectedCustomization.code && item.variation.code === combinedView.customize.selectedVariations.code;
                }).length === 0;
            };

            var buttonHandlerFn = function(buttonId) {
                var deferred = $q.defer();
                if (buttonId === 'confirmOk') {
                    var combinedView = personalizationsmarteditContextService.getCombinedView();
                    combinedView.selectedItems = $scope.selectedItems;

                    if (combinedView.enabled && combinedView.customize.selectedVariations !== null && isCombinedViewContextPersRemoved(combinedView)) {
                        combinedView.customize.selectedCustomization = null;
                        combinedView.customize.selectedVariations = null;
                        combinedView.customize.selectedComponents = null;
                    }

                    personalizationsmarteditContextService.setCombinedView(combinedView);
                    return deferred.resolve();
                }
                return deferred.reject();
            };

            $scope.modalManager.setButtonHandler(buttonHandlerFn);

            $scope.$watch('selectedItems', function(newValue, oldValue) {
                $scope.modalManager.disableButton("confirmOk");
                if (newValue !== oldValue) {
                    var combinedView = personalizationsmarteditContextService.getCombinedView();
                    var arrayEquals = (combinedView.selectedItems || []).length === 0 && $scope.selectedItems.length === 0;
                    arrayEquals = arrayEquals || angular.equals(combinedView.selectedItems, $scope.selectedItems);
                    if (!arrayEquals) {
                        $scope.modalManager.enableButton("confirmOk");
                    }
                }
            }, true);

            $scope.pageFilerChange = function(itemId) {
                $scope.customizationPageFilter = itemId;
                $scope.pagination.reset();
                customizationDataFactory.resetData();
                $scope.addMoreItems();
            };

            $scope.catalogFilerChange = function(itemId) {
                $scope.catalogFilter = itemId;
                $scope.pagination.reset();
                customizationDataFactory.resetData();
                $scope.addMoreItems();
            };

            $scope.isItemFromCurrentCatalog = function(item) {
                return personalizationsmarteditCombinedViewCommons.isItemFromCurrentCatalog(item);
            };

            $scope.getAndSetCatalogVersionNameL10N = function(customization) {
                return personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(customization);
            };

        });
