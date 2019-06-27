angular.module('catalogFilterDropdownModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'ySelectModule'
    ])
    .constant('PERSONALIZATION_CATALOG_FILTER', {
        ALL: 'all',
        CURRENT: 'current',
        PARENTS: 'parents'
    })
    .controller('catalogFilterDropdownController', function($q, personalizationsmarteditUtils, PERSONALIZATION_CATALOG_FILTER) {
        var self = this;

        //Lifecycle methods
        this.$onInit = function() {
            self.items = [{
                id: PERSONALIZATION_CATALOG_FILTER.ALL,
                label: "personalization.filter.catalog.all"
            }, {
                id: PERSONALIZATION_CATALOG_FILTER.CURRENT,
                label: "personalization.filter.catalog.current"
            }, {
                id: PERSONALIZATION_CATALOG_FILTER.PARENTS,
                label: "personalization.filter.catalog.parents"
            }];
            self.selectedId = self.initialValue || self.items[1].id;
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
    .component('catalogFilterDropdown', {
        templateUrl: 'pageFilterDropdownTemplate.html',
        controller: 'catalogFilterDropdownController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            onSelectCallback: '&',
            initialValue: '<?'
        }
    });
