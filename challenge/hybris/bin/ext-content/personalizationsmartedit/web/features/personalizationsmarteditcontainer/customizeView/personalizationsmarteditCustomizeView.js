angular.module('personalizationsmarteditCustomizeViewModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'personalizationsmarteditServicesModule',
        'personalizationsmarteditDataFactory',
        'customizationsListModule',
        'catalogFilterDropdownModule',
        'hasMulticatalogModule',
        'statusFilterDropdownModule',
        'pageFilterDropdownModule'
    ])
    .controller('personalizationsmarteditCustomizeViewController',
        function(
            $filter,
            customizationDataFactory,
            PaginationHelper,
            personalizationsmarteditContextService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditUtils,
            PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER) {

            var self = this;

            //Private methods
            var errorCallback = function() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
                self.moreCustomizationsRequestProcessing = false;
            };

            var successCallback = function(response) {
                self.pagination = new PaginationHelper(response.pagination);
                self.moreCustomizationsRequestProcessing = false;
            };

            var getStatus = function() {
                if (self.statusFilter === undefined) {
                    return personalizationsmarteditUtils.getStatusesMapping()[0]; // all elements
                }
                return personalizationsmarteditUtils.getStatusesMapping().filter(function(elem) {
                    return elem.code === self.statusFilter;
                })[0];
            };

            var getCustomizations = function(categoryFilter) {
                var params = {
                    filter: categoryFilter,
                    dataArrayName: 'customizations'
                };
                customizationDataFactory.updateData(params, successCallback, errorCallback);
            };

            var getCustomizationsFilterObject = function() {
                var ret = {
                    currentSize: self.pagination.count,
                    currentPage: self.pagination.page + 1,
                    name: self.nameFilter,
                    statuses: getStatus().modelStatuses,
                    catalogs: self.catalogFilter
                };
                if (self.pageFilter === PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE) {
                    ret.pageId = personalizationsmarteditContextService.getSeData().pageId;
                    ret.pageCatalogId = (personalizationsmarteditContextService.getSeData().seExperienceData.pageContext || {}).catalogId;
                }
                return ret;
            };

            var refreshList = function() {
                if (!self.moreCustomizationsRequestProcessing) {
                    self.moreCustomizationsRequestProcessing = true;
                    self.pagination.reset();
                    customizationDataFactory.resetData();
                    getCustomizations(getCustomizationsFilterObject());
                }
            };

            //Properties
            this.catalogFilerChange = function(itemId) {
                self.catalogFilter = itemId;
                refreshList();
            };

            this.pageFilerChange = function(itemId) {
                self.pageFilter = itemId;
                refreshList();
            };

            this.statusFilerChange = function(itemId) {
                self.statusFilter = itemId;
                refreshList();
            };

            this.nameInputKeypress = function(keyEvent) {
                if (keyEvent.which === 13 || self.nameFilter.length > 2 || self.nameFilter.length === 0) {
                    refreshList();
                }
            };

            this.addMoreCustomizationItems = function() {
                if (self.pagination.page < self.pagination.totalPages - 1 && !self.moreCustomizationsRequestProcessing) {
                    self.moreCustomizationsRequestProcessing = true;
                    getCustomizations(getCustomizationsFilterObject());
                }
            };

            //Lifecycle methods
            this.$onInit = function() {
                personalizationsmarteditContextService.refreshExperienceData();
                self.pagination = new PaginationHelper();
                self.pagination.reset();
                self.moreCustomizationsRequestProcessing = false;
                self.customizationsList = customizationDataFactory.items;
                customizationDataFactory.resetData();

                var filters = personalizationsmarteditContextService.getCustomizeFiltersState();
                self.catalogFilter = filters.catalogFilter;
                self.pageFilter = filters.pageFilter;
                self.statusFilter = filters.statusFilter;
                self.nameFilter = filters.nameFilter;
            };

            this.$onDestroy = function() {
                var filters = personalizationsmarteditContextService.getCustomizeFiltersState();
                filters.catalogFilter = self.catalogFilter;
                filters.pageFilter = self.pageFilter;
                filters.statusFilter = self.statusFilter;
                filters.nameFilter = self.nameFilter;
                personalizationsmarteditContextService.setCustomizeFiltersState(filters);
            };

            this.$onChanges = function(changes) {
                if (changes.isMenuOpen && changes.isMenuOpen.currentValue) {
                    refreshList();
                }
            };

        })
    .component('personalizationsmarteditCustomizeView', {
        templateUrl: 'personalizationsmarteditCustomizeViewTemplate.html',
        controller: 'personalizationsmarteditCustomizeViewController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            isMenuOpen: '<'
        }
    });
