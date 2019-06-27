angular.module('personalizationsmarteditSharedSlotDecorator', [
        'slotSharedServiceModule'
    ])
    .controller('personalizationsmarteditSharedSlotController', function(slotSharedService, $element) {
        var self = this;

        var positionPanel = function() {
            if ($element.offset().top <= $element.find('.decorative-panel-area').height()) {
                $element.find('.decorative-panel-area').css('margin-top', $element.find('.decorator-padding-container').height());
            }
        };

        this.$onChanges = function(changes) {
            if (changes.active && changes.active.currentValue) {
                positionPanel();
                self.isPopupOpened = false;
            }
        };

        this.$onInit = function() {
            self.slotSharedFlag = false;
            slotSharedService.isSlotShared(self.smarteditComponentId).then(function(result) {
                self.slotSharedFlag = result;
            });
        };

    })
    .directive('personalizationsmarteditSharedSlot', function() {
        return {
            templateUrl: 'personalizationsmarteditSharedSlotDecoratorTemplate.html',
            restrict: 'C',
            controller: 'personalizationsmarteditSharedSlotController',
            controllerAs: 'ctrl',
            scope: {},
            transclude: true,
            bindToController: {
                smarteditComponentId: '@',
                active: '<'
            }
        };
    });
