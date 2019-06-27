angular.module('personalizationsmarteditSegmentViewModule', [
        'modalServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommonsModule',
        'personalizationsmarteditServicesModule',
        'confirmationModalServiceModule',
        'personalizationsmarteditManageCustomizationViewModule',
        'personalizationsmarteditCommerceCustomizationModule',
        'personalizationsmarteditDataFactory',
        'ui.tree',
        'personalizationsmarteditSegmentExpressionAsHtml',
        'personalizationsmarteditManagementModule'
    ])
    .controller('personalizationsmarteditSegmentViewController', function(personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditTriggerService, PaginationHelper, $timeout, $filter, confirmationModalService, triggerTabService) {
        var self = this;

        this.actions = personalizationsmarteditTriggerService.actions;

        //Properties
        var elementToScrollHeight = 0;
        Object.defineProperty(this, 'elementToScrollHeight', {
            get: function() {
                return elementToScrollHeight;
            },
            set: function(newVal) {
                elementToScrollHeight = newVal;
            }
        });

        var getSegmentsFilterObject = function() {
            return {
                code: self.segmentFilter.code,
                pageSize: self.segmentPagination.count,
                currentPage: self.segmentPagination.page + 1
            };
        };

        var findElementAndDuplicate = function(element, index, array) {
            var elementToDuplicate = this; //'this' is additional argument passed to function so in this case it is 'elementToDuplicate'
            if (elementToDuplicate === element) {
                array.splice(index, 0, angular.copy(elementToDuplicate));
                return true;
            }
            if (self.isContainer(element)) {
                element.nodes.some(findElementAndDuplicate, elementToDuplicate); //recursive call to check all sub containers
            }
            return false;
        };

        var dropzoneItem = {
            type: 'dropzone'
        };

        var initExpression = [{
            'type': 'container',
            'operation': self.actions[0],
            'nodes': [dropzoneItem]
        }];

        var removeDropzoneItem = function(nodes) {
            nodes.forEach(function(element, index, array) {
                if (self.isDropzone(element)) {
                    array.splice(index, 1);
                }
            });
        };

        var fixEmptyContainer = function(nodes) {
            nodes.forEach(function(element) {
                if (self.isEmptyContainer(element)) {
                    element.nodes.push(dropzoneItem);
                }
                if (self.isContainer(element)) {
                    fixEmptyContainer(element.nodes);
                }
            });
        };

        this.treeOptions = {
            dragStart: function() {
                self.scrollZoneVisible = self.isScrollZoneVisible();
            },
            dropped: function(e) {
                self.scrollZoneVisible = false;
                removeDropzoneItem(e.dest.nodesScope.$modelValue);
                $timeout(function() {
                    fixEmptyContainer(self.expression);
                }, 0);
            },
            dragMove: function(e) {
                self.highlightedContainer = e.dest.nodesScope.$nodeScope.$modelValue.$$hashKey;
                if (self.isScrollZoneVisible() !== self.scrollZoneVisible) {
                    self.scrollZoneVisible = self.isScrollZoneVisible();
                } else if (Math.abs(self.elementToScrollHeight - self.elementToScroll.get(0).scrollHeight) > 10) {
                    self.elementToScrollHeight = self.elementToScroll.get(0).scrollHeight;
                    self.scrollZoneVisible = false;
                }
            }
        };

        this.isScrollZoneVisible = function() {
            return self.elementToScroll.get(0).scrollHeight > self.elementToScroll.get(0).clientHeight;
        };

        this.getElementToScroll = function() {
            return self.elementToScroll;
        };

        this.removeItem = function(scope) {
            if (personalizationsmarteditTriggerService.isNotEmptyContainer(scope.$modelValue) && !self.isContainerWithDropzone(scope.$modelValue)) {
                confirmationModalService.confirm({
                    description: $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.segments.removecontainerconfirmation')
                }).then(function() {
                    scope.remove();
                    $timeout(function() {
                        fixEmptyContainer(self.expression);
                    }, 0);
                });
            } else {
                scope.remove();
                $timeout(function() {
                    fixEmptyContainer(self.expression);
                }, 0);
            }
        };

        this.duplicateItem = function(elementToDuplicate) {
            self.expression[0].nodes.some(findElementAndDuplicate, elementToDuplicate);
        };

        this.toggle = function(scope) {
            scope.toggle();
        };

        this.newSubItem = function(scope, type) {
            var nodeData = scope.$modelValue;
            removeDropzoneItem(nodeData.nodes);
            nodeData.nodes.unshift({
                type: type,
                operation: (type === 'item' ? '' : self.actions[0]),
                nodes: [dropzoneItem]
            });
            scope.expand();
            $timeout(function() {
                var childArray = scope.childNodes();
                childArray[0].expand();
            }, 0);
        };

        this.segments = [];
        this.segmentPagination = new PaginationHelper();
        this.segmentPagination.reset();
        this.segmentFilter = {
            code: ''
        };

        this.segmentSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            self.segmentPagination.reset();
            self.segmentFilter.code = searchObj;
            self.segments.length = 0;
            self.addMoreSegmentItems();
        };

        this.segmentSelectedEvent = function(item) {
            self.expression[0].nodes.unshift({
                type: 'item',
                operation: '',
                selectedSegment: item,
                nodes: []
            });
            self.singleSegment = null;
            self.highlightedContainer = self.expression[0].$$hashKey;
            removeDropzoneItem(self.expression[0].nodes);
        };

        this.moreSegmentRequestProcessing = false;
        this.addMoreSegmentItems = function() {
            if (self.segmentPagination.page < self.segmentPagination.totalPages - 1 && !self.moreSegmentRequestProcessing) {
                self.moreSegmentRequestProcessing = true;
                personalizationsmarteditRestService.getSegments(getSegmentsFilterObject()).then(function successCallback(response) {
                    Array.prototype.push.apply(self.segments, response.segments);
                    self.segmentPagination = new PaginationHelper(response.pagination);
                    self.moreSegmentRequestProcessing = false;
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingsegments'));
                    self.moreSegmentRequestProcessing = false;
                });
            }
        };

        this.isTopContainer = function(element) {
            return angular.equals(self.expression[0], element.node);
        };

        this.isEmptyContainer = function(element) {
            return self.isContainer(element) && element.nodes.length === 0;
        };

        this.isContainerWithDropzone = function(element) {
            return self.isContainer(element) && element.nodes.length === 1 && self.isDropzone(element.nodes[0]);
        };

        this.isItem = function(element) {
            return personalizationsmarteditTriggerService.isItem(element);
        };

        this.isDropzone = function(element) {
            return personalizationsmarteditTriggerService.isDropzone(element);
        };

        this.isContainer = function(element) {
            return personalizationsmarteditTriggerService.isContainer(element);
        };

        this.isHighlightedContainer = function(node) {
            return node.$$hashKey === self.highlightedContainer;
        };

        //Lifecycle methods
        this.$onInit = function() {
            self.triggers = self.triggers || (triggerTabService.getTriggerDataState().selectedVariation || {}).triggers;
            self.expression = self.expression || triggerTabService.getTriggerDataState().expression;

            if (self.triggers && self.triggers.length > 0) {
                self.expression = personalizationsmarteditTriggerService.buildData(self.triggers);
            } else {
                self.expression = angular.copy(initExpression);
            }
            triggerTabService.getTriggerDataState().expression = self.expression;
            self.elementToScroll = angular.element(".sliderpanel-body");
        };

    })
    .component('multiSegmentView', {
        templateUrl: 'personalizationsmarteditSegmentViewTemplate.html',
        controller: 'personalizationsmarteditSegmentViewController',
        controllerAs: 'ctrl',
        bindings: {
            triggers: '<?',
            expression: '=?'
        }
    });
