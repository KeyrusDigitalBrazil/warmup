angular.module('customizationsListModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditServicesModule',
        'personalizationsmarteditPreviewServiceModule',
        'personalizationsmarteditManageCustomizationViewModule',
        'personalizationsmarteditCustomizeViewServiceModule',
        'smarteditCommonsModule',
        'personalizationsmarteditCommonsModule'
    ])
    .controller(
        'customizationsListController',
        function(
            $q,
            $filter,
            personalizationsmarteditContextService,
            personalizationsmarteditRestService,
            personalizationsmarteditCommerceCustomizationService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditUtils,
            personalizationsmarteditDateUtils,
            personalizationsmarteditContextUtils,
            personalizationsmarteditPreviewService,
            personalizationsmarteditManager,
            personalizationsmarteditCustomizeViewProxy,
            systemEventService) {

            var self = this;

            //Private methods
            var matchActionForVariation = function(action, variation) {
                return ((action.variationCode === variation.code) &&
                    (action.actionCatalog === variation.catalog) &&
                    (action.actionCatalogVersion === variation.catalogVersion));
            };

            var numberOfAffectedComponentsForActions = function(actionsForVariation) {
                var result = 0;
                actionsForVariation.forEach(function(action) {
                    result += parseInt(self.sourceContainersComponentsInfo[action.containerId]) || 0;
                });
                return result;
            };

            var initSourceContainersComponentsInfo = function() {
                var deferred = $q.defer();
                personalizationsmarteditCustomizeViewProxy.getSourceContainersInfo().then(
                    function successCallback(response) {
                        self.sourceContainersComponentsInfo = response;
                        deferred.resolve();
                    },
                    function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingnumberofaffectedcomponentsforvariation'));
                        deferred.reject();
                    });

                return deferred.promise;
            };

            var paginatedGetAndSetNumberOfAffectedComponentsForVariations = function(customization, currentPage) {

                personalizationsmarteditRestService.getCxCmsActionsOnPageForCustomization(customization, currentPage).then(
                    function successCallback(response) {
                        customization.variations.forEach(function(variation) {
                            var actionsForVariation = response.actions.filter(function(action) {
                                return matchActionForVariation(action, variation);
                            });
                            variation.numberOfAffectedComponents = (currentPage === 0) ? 0 : variation.numberOfAffectedComponents;
                            variation.numberOfAffectedComponents += numberOfAffectedComponentsForActions(actionsForVariation);
                        });

                        var nextPage = currentPage + 1;
                        if (nextPage < response.pagination.totalPages) {
                            paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, nextPage);
                        }
                    },
                    function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingnumberofaffectedcomponentsforvariation'));
                    });
            };

            var getAndSetNumberOfAffectedComponentsForVariations = function(customization) {
                var customize = personalizationsmarteditContextService.getCustomize();
                var isUpToDate = (customize.selectedComponents || []).every(function(componentId) {
                    return self.sourceContainersComponentsInfo[componentId] !== undefined;
                });
                if (!isUpToDate || customize.selectedComponents === null || angular.equals(self.sourceContainersComponentsInfo, {})) {
                    initSourceContainersComponentsInfo().finally(function() {
                        paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
                    });
                } else if (isUpToDate) {
                    paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
                }
            };

            var getNumberOfAffectedComponentsForCorrespondingVariation = function(variationsArray, variationCode) {
                var foundVariation = variationsArray.filter(function(elem) {
                    return elem.code === variationCode;
                });
                return (foundVariation[0] || {}).numberOfAffectedComponents;
            };

            var updateCustomizationData = function(customization) {
                personalizationsmarteditRestService.getVariationsForCustomization(customization.code, customization).then(
                    function successCallback(response) {
                        response.variations.forEach(function(variation) {
                            variation.numberOfAffectedComponents = getNumberOfAffectedComponentsForCorrespondingVariation(customization.variations, variation.code);
                        });
                        customization.variations = response.variations || [];
                        customization.variations.forEach(function(variation) {
                            variation.numberOfCommerceActions = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                            variation.commerceCustomizations = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                        });
                        getAndSetNumberOfAffectedComponentsForVariations(customization);
                    },
                    function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                    });
            };

            var getVisibleVariations = function(customization) {
                return personalizationsmarteditUtils.getVisibleItems(customization.variations);
            };

            var getAndSetComponentsForVariation = function(customizationId, variationId, catalog, catalogVersion) {
                personalizationsmarteditRestService.getComponenentsIdsForVariation(customizationId, variationId, catalog, catalogVersion).then(function successCallback(response) {
                    var customize = personalizationsmarteditContextService.getCustomize();
                    customize.selectedComponents = response.components;
                    personalizationsmarteditContextService.setCustomize(customize);
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponentsforvariation'));
                });
            };

            var updatePreviewTicket = function(customizationId, variationArray) {
                var variationKeys = personalizationsmarteditUtils.getVariationKey(customizationId, variationArray);
                personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(variationKeys);
            };

            var refreshCustomizeContext = function() {
                var customize = angular.copy(personalizationsmarteditContextService.getCustomize());
                if (customize.selectedCustomization) {
                    personalizationsmarteditRestService.getCustomization(customize.selectedCustomization)
                        .then(function successCallback(response) {
                            customize.selectedCustomization = response;
                            if (customize.selectedVariations && !angular.isArray(customize.selectedVariations)) {
                                response.variations.filter(function(item) {
                                    return customize.selectedVariations.code === item.code;
                                }).forEach(function(variation) {
                                    customize.selectedVariations = variation;
                                    if (!personalizationsmarteditUtils.isItemVisible(variation)) {
                                        customize.selectedCustomization = null;
                                        customize.selectedVariations = null;
                                        personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
                                    }
                                });
                            }
                            personalizationsmarteditContextService.setCustomize(customize);
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                        });
                }
            };

            //Methods
            this.initCustomization = function(customization) {
                customization.collapsed = true;
                if ((personalizationsmarteditContextService.getCustomize().selectedCustomization || {}).code === customization.code) {
                    customization.collapsed = false;
                    updateCustomizationData(customization);
                }
                personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(customization);
            };

            this.editCustomizationAction = function(customization) {
                personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
                personalizationsmarteditManager.openEditCustomizationModal(customization.code);
            };

            this.customizationRowClick = function(customization, select) {
                this.clearAllSubMenu();
                customization.collapsed = !customization.collapsed;

                if (!customization.collapsed) {
                    updateCustomizationData(customization);
                }
                if (select) {
                    this.customizationClick(customization);
                }

                self.customizationsList.filter(function(cust) {
                    return customization.code !== cust.code;
                }).forEach(function(cust) {
                    cust.collapsed = true;
                });
            };

            this.customizationClick = function(customization) {
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                var currentVariations = personalizationsmarteditContextService.getCustomize().selectedVariations;
                var visibleVariations = getVisibleVariations(customization);
                var customize = personalizationsmarteditContextService.getCustomize();
                customize.selectedCustomization = customization;
                customize.selectedVariations = visibleVariations;
                personalizationsmarteditContextService.setCustomize(customize);
                if (visibleVariations.length > 0) {
                    var allVariations = personalizationsmarteditUtils.getVariationCodes(visibleVariations).join(",");
                    getAndSetComponentsForVariation(customization.code, allVariations, customization.catalog, customization.catalogVersion);
                }

                if ((angular.isObject(currentVariations) && !angular.isArray(currentVariations)) || combinedView.enabled) {
                    updatePreviewTicket();
                }

                combinedView.enabled = false;
                personalizationsmarteditContextService.setCombinedView(combinedView);
            };

            this.getSelectedVariationClass = function(variation) {
                if (angular.equals(variation.code, (personalizationsmarteditContextService.getCustomize().selectedVariations || {}).code)) {
                    return "selectedVariation";
                }
            };

            this.getSelectedCustomizationClass = function(customization) {
                if (angular.equals(customization.code, (personalizationsmarteditContextService.getCustomize().selectedCustomization || {}).code) &&
                    angular.isArray(personalizationsmarteditContextService.getCustomize().selectedVariations)) {
                    return "selectedCustomization";
                }
            };

            this.variationClick = function(customization, variation) {
                var customize = personalizationsmarteditContextService.getCustomize();
                customize.selectedCustomization = customization;
                customize.selectedVariations = variation;
                personalizationsmarteditContextService.setCustomize(customize);
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                combinedView.enabled = false;
                personalizationsmarteditContextService.setCombinedView(combinedView);
                getAndSetComponentsForVariation(customization.code, variation.code, customization.catalog, customization.catalogVersion);
                updatePreviewTicket(customization.code, [variation]);
            };

            this.hasCommerceActions = function(variation) {
                return personalizationsmarteditUtils.hasCommerceActions(variation);
            };

            this.getCommerceCustomizationTooltip = function(variation, prefix, suffix) {
                return personalizationsmarteditUtils.getCommerceCustomizationTooltipHTML(variation, prefix, suffix);
            };

            this.getActivityStateForCustomization = function(customization) {
                return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
            };

            this.getActivityStateForVariation = function(customization, variation) {
                return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
            };

            this.clearAllSubMenu = function() {
                angular.forEach(self.customizationsList, function(customization) {
                    customization.subMenu = false;
                });
            };

            this.getEnablementTextForCustomization = function(customization) {
                return personalizationsmarteditUtils.getEnablementTextForCustomization(customization, 'personalization.toolbar.pagecustomizations');
            };

            this.getEnablementTextForVariation = function(variation) {
                return personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.toolbar.pagecustomizations');
            };

            this.isEnabled = function(item) {
                return personalizationsmarteditUtils.isPersonalizationItemEnabled(item);
            };

            this.getDatesForCustomization = function(customization) {
                var activityStr = "";
                var startDateStr = "";
                var endDateStr = "";

                if (customization.enabledStartDate || customization.enabledEndDate) {
                    startDateStr = personalizationsmarteditDateUtils.formatDateWithMessage(customization.enabledStartDate);
                    endDateStr = personalizationsmarteditDateUtils.formatDateWithMessage(customization.enabledEndDate);
                    if (!customization.enabledStartDate) {
                        startDateStr = " ...";
                    }
                    if (!customization.enabledEndDate) {
                        endDateStr = "... ";
                    }
                    activityStr += " (" + startDateStr + " - " + endDateStr + ") ";
                }
                return activityStr;
            };

            this.customizationSubMenuAction = function(customization) {
                if (!customization.subMenu) {
                    self.clearAllSubMenu();
                }
                customization.subMenu = !customization.subMenu;
            };

            this.isCustomizationFromCurrentCatalog = function(customization) {
                return personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, personalizationsmarteditContextService.getSeData());
            };

            this.$onInit = function() {
                self.sourceContainersComponentsInfo = {};
                systemEventService.registerEventHandler('CUSTOMIZATIONS_MODIFIED', function() {
                    refreshCustomizeContext();
                    return $q.when();
                });
            };

        })
    .component('customizationsList', {
        templateUrl: 'customizationsListTemplate.html',
        controller: 'customizationsListController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            customizationsList: '<',
            requestProcessing: '<'
        }
    });
