angular.module('personalizationsmarteditToolbarContextModule', [])
    .controller('topToolbarMenuController', function(
        $scope,
        personalizationsmarteditManager,
        personalizationsmarteditManagerView,
        personalizationsmarteditPreviewService,
        personalizationsmarteditContextService,
        personalizationsmarteditContextUtils,
        personalizationsmarteditCombinedView) {

        $scope.status = {
            isopen: false
        };

        $scope.preventDefault = function(oEvent) {
            oEvent.stopPropagation();
        };

        $scope.createCustomizationClick = function() {
            personalizationsmarteditManager.openCreateCustomizationModal();
        };

        $scope.managerViewClick = function() {
            personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
            personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
            personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
            personalizationsmarteditManagerView.openManagerAction();
        };

        $scope.combinedViewClick = function() {
            personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
            personalizationsmarteditCombinedView.openManagerAction();
        };
        $scope.isCustomizeCustomizationSelected = function() {
            return personalizationsmarteditContextService.getCustomize().selectedCustomization;
        };
        $scope.isCombinedViewCustomizationSelected = function() {
            return personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization;
        };

    })
    .controller('personalizationsmarteditCustomizeToolbarContextController', function($scope, $timeout, personalizationsmarteditContextService, personalizationsmarteditPreviewService, personalizationsmarteditContextUtils) {
        var self = this;

        $scope.$watch(function() {
            return personalizationsmarteditContextService.getCustomize().selectedCustomization;
        }, function(newValue, oldValue) {
            if (newValue && newValue !== oldValue) {
                self.title = newValue.name;
                self.visible = true;
            } else if (!newValue) {
                self.visible = false;
            }
        });

        $scope.$watch(function() {
            return personalizationsmarteditContextService.getCustomize().selectedVariations;
        }, function(newValue, oldValue) {
            if (newValue && newValue !== oldValue) {
                self.subtitle = newValue.name;
            }
        });

        self.clear = function() {
            personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditPreviewService, personalizationsmarteditContextService);
            $timeout((function() {
                angular.element(".personalizationsmarteditTopToolbarCustomizeButton[aria-expanded='true']").click();
            }), 0);
        };

        self.$onInit = function() {
            self.visible = false;
            if (personalizationsmarteditContextService.getCustomize().selectedCustomization) {
                self.title = personalizationsmarteditContextService.getCustomize().selectedCustomization.name;
                self.visible = true;
                if (!angular.isArray(personalizationsmarteditContextService.getCustomize().selectedVariations)) {
                    self.subtitle = personalizationsmarteditContextService.getCustomize().selectedVariations.name;
                }
            }
        };

    })
    .controller('personalizationsmarteditCombinedViewToolbarContextController', function($scope, personalizationsmarteditCombinedViewCommons, $timeout, personalizationsmarteditContextService, personalizationsmarteditContextUtils) {
        var self = this;

        $scope.$watch(function() {
            return personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization;
        }, function(newValue, oldValue) {
            if (newValue && newValue !== oldValue) {
                self.title = newValue.name;
                self.subtitle = personalizationsmarteditContextService.getCombinedView().customize.selectedVariations.name;
                self.visible = true;
            } else if (!newValue) {
                self.visible = false;
            }
        });

        $scope.$watch(function() {
            return personalizationsmarteditContextService.getCombinedView().enabled;
        }, function(newValue, oldValue) {
            if (newValue === false && newValue !== oldValue) {
                personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
            }
        });

        self.clear = function() {
            personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            var variations = [];
            (combinedView.selectedItems || []).forEach(function(item) {
                variations.push({
                    customizationCode: item.customization.code,
                    variationCode: item.variation.code,
                    catalog: item.variation.catalog,
                    catalogVersion: item.variation.catalogVersion
                });
            });
            personalizationsmarteditCombinedViewCommons.updatePreview(variations);
        };

        self.$onInit = function() {
            self.visible = false;
            if (personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization) {
                self.title = personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization.name;
                self.subtitle = personalizationsmarteditContextService.getCombinedView().customize.selectedVariations.name;
                self.visible = true;
            }
        };

    })
    .component('personalizationsmarteditCustomizeToolbarContext', {
        templateUrl: 'personalizationsmarteditToolbarContextTemplate.html',
        controller: 'personalizationsmarteditCustomizeToolbarContextController',
        controllerAs: 'ctrl',
        transclude: true
    })
    .component('personalizationsmarteditCombinedViewToolbarContext', {
        templateUrl: 'personalizationsmarteditToolbarContextTemplate.html',
        controller: 'personalizationsmarteditCombinedViewToolbarContextController',
        controllerAs: 'ctrl',
        transclude: true
    });
