angular.module('pageFilterDropdownModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'ySelectModule'
    ])
    .constant('PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER', {
        ALL: 'all',
        ONLY_THIS_PAGE: 'onlythispage'
    })
    .controller('pageFilterDropdownController', function($q, personalizationsmarteditUtils, PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER) {
        var self = this;

        //Lifecycle methods
        this.$onInit = function() {
            self.items = [{
                id: PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ALL,
                label: "personalization.filter.page.all"
            }, {
                id: PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE,
                label: "personalization.filter.page.onlythispage"
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
    .component('pageFilterDropdown', {
        templateUrl: 'pageFilterDropdownTemplate.html',
        controller: 'pageFilterDropdownController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            onSelectCallback: '&',
            initialValue: '<?'
        }
    });
