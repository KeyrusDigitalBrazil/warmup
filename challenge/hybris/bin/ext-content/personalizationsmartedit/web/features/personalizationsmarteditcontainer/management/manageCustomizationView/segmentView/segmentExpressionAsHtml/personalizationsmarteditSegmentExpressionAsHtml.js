angular.module('personalizationsmarteditSegmentExpressionAsHtml', [
        'personalizationsmarteditManageCustomizationViewModule'
    ])
    .controller('personalizationsmarteditSegmentExpressionAsHtmlController', function(personalizationsmarteditTriggerService) {
        var self = this;

        //Properties
        var segmentExpression = {};
        Object.defineProperty(this, 'segmentExpression', {
            get: function() {
                return segmentExpression;
            },
            set: function(newVal) {
                segmentExpression = newVal;
            }
        });

        var operators = ['AND', 'OR', 'NOT'];
        Object.defineProperty(this, 'operators', {
            get: function() {
                return operators;
            }
        });

        var emptyGroup = '[]';
        Object.defineProperty(this, 'emptyGroup', {
            get: function() {
                return emptyGroup;
            }
        });

        var emptyGroupAndOperators = self.operators.concat(emptyGroup);
        Object.defineProperty(this, 'emptyGroupAndOperators', {
            get: function() {
                return emptyGroupAndOperators;
            }
        });

        //Methods

        //A segmentExpression parameter can be 'variation.triggers' object or 'segmentExpression' object
        //If variations.triggers is passed it will converted to segmentExpression
        this.getExpressionAsArray = function() {
            if (angular.isDefined(self.segmentExpression) && !angular.isDefined(self.segmentExpression.operation)) {
                self.segmentExpression = personalizationsmarteditTriggerService.buildData(self.segmentExpression)[0];
            }
            return personalizationsmarteditTriggerService.getExpressionAsString(self.segmentExpression).split(" ");
        };

        this.getLocalizationKeyForOperator = function(operator) {
            return 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.' + operator.toLowerCase();
        };

    })
    .component('personalizationsmarteditSegmentExpressionAsHtml', {
        templateUrl: 'personalizationsmarteditSegmentExpressionAsHtmlTemplate.html',
        controller: 'personalizationsmarteditSegmentExpressionAsHtmlController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            segmentExpression: '<'
        }
    });
