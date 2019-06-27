angular.module('statusFilterDropdownModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'ySelectModule'
    ])
    .controller('statusFilterDropdownController', function($q, personalizationsmarteditUtils) {
        var self = this;

        //Lifecycle methods
        this.$onInit = function() {
            self.items = personalizationsmarteditUtils.getStatusesMapping().map(function(elem) {
                return {
                    id: elem.code,
                    label: elem.text,
                    modelStatuses: elem.modelStatuses
                };
            });
            self.selectedId = self.initialValue || self.items[0].id;
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
    .component('statusFilterDropdown', {
        templateUrl: 'pageFilterDropdownTemplate.html',
        controller: 'statusFilterDropdownController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            onSelectCallback: '&',
            initialValue: '<?'
        }
    });
