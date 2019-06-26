angular.module('catalogVersionFilterDropdownModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'personalizationsmarteditServicesModule',
        'ySelectModule',
        'componentMenuServiceModule',
        'l10nModule'
    ])
    .controller('catalogVersionFilterDropdownController', function($q, personalizationsmarteditUtils, componentMenuService, personalizationsmarteditContextService) {
        var self = this;

        //Lifecycle methods
        this.$onInit = function() {
            self.itemTemplate = 'catalogVersionFilterDropdownItemTemplate.html';
            componentMenuService.getValidContentCatalogVersions().then(function(catalogVersions) {
                self.items = catalogVersions;
                var experience = personalizationsmarteditContextService.getSeData().seExperienceData;
                self.items.forEach(function(item) {
                    item.isCurrentCatalog = item.id === experience.catalogDescriptor.catalogVersionUuid;
                });
                componentMenuService.getInitialCatalogVersion(self.items).then(function(selectedCatalogVersion) {
                    self.selectedId = self.initialValue || selectedCatalogVersion.id;
                });
            });
        };

        //ySelect config
        this.fetchStrategy = {
            fetchAll: function() {
                return $q.when(self.items);
            }
        };

        this.onChange = function() {
            self.onSelectCallback({
                value: self.selectedId
            });
        };

    })
    .component('catalogVersionFilterDropdown', {
        templateUrl: 'pageFilterDropdownTemplate.html',
        controller: 'catalogVersionFilterDropdownController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            onSelectCallback: '&',
            initialValue: '<?'
        }
    });
