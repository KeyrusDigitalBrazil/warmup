/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
angular.module('FakeModule', [
        'decoratorServiceModule',
        'smarteditServicesModule',
        'functionsModule'
    ]) .component('pageChangeTest', {
        template: "<span>{{$ctrl.getPageChangeMessage()}}</span>",
                controller: function($rootScope) {
            this.getPageChangeMessage = function() {
                return $rootScope.pageChangeMessage;
            };
        }
    })
    .directive('deco1', function() {
        return {
            template: "<div style='background: rgba(0, 0, 255, .4)'><div ng-transclude></div></div>",
            restrict: 'C',
            controller: function() {},
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '='
            }
        };
    })
    .directive('deco2', function() {
        return {
            template: "<div style='background: rgba(255, 0, 0, .4)'><div ng-transclude></div></div>",
            restrict: 'C',
            controller: function() {},
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '='
            }
        };
    })
    .directive('deco3', function() {
        return {
            template: "<div style='background: rgba(0, 255, 0, .4)'><div ng-transclude></div></div>",
            restrict: 'C',
            controller: function() {},
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '='
            }
        };
    })
    .directive('deco4', function() {
        return {
            template: "<div style='background: rgba(0, 255, 255, .8)'><div ng-transclude></div>page changed</div>",
            restrict: 'C',
            controller: function() {},
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '='
            }
        };
    })
    .run(function($q, $rootScope, decoratorService, crossFrameEventService, featureService) {
        crossFrameEventService.subscribe("PAGE_CHANGE", function(eventId, experience) {
            $rootScope.pageChangeMessage = "paged changed to " + experience.pageId;
            $rootScope.currentPageId = experience.pageId;
            return $q.when();
        });

        decoratorService.addMappings({
            'componentType1': ['deco1', 'deco4'],
            'componentType2': ['deco2'],
            'ContentSlot': ['deco3']
        });

        decoratorService.enable('deco1');
        decoratorService.enable('deco2');
        decoratorService.enable('deco3');

        featureService.addDecorator({
            key: 'deco4',
            nameI18nKey: 'deco4',
            displayCondition: function() {
                return $q.when($rootScope.currentPageId === 'demo_storefront_page_id');
            }
        });
    })
    .controller('sakExecutorDebugController', function($interval, sakExecutorService, yjQuery, COMPONENT_CLASS, ID_ATTRIBUTE, resizeListener, positionRegistry, smartEditContractChangeListener, isInExtendedViewPort, CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS) {

        this.visibleElements = [];
        var intersectionObserver;
        $interval(function() {
            if (intersectionObserver) {
                intersectionObserver.disconnect();
            }

            var inPageElements = [];

            intersectionObserver = new IntersectionObserver(function(entries) {
                entries.filter(function(entry) {
                    var index = this.visibleElements.indexOf(entry.target);
                    if (this._isInExtendedViewPort(entry.target, entry.isIntersecting)) {
                        if (index === -1) {
                            this.visibleElements.push(entry.target);
                        }
                    } else {
                        if (index !== -1) {
                            this.visibleElements.splice(index, 1);
                        }
                    }
                }.bind(this));

                if (smartEditContractChangeListener.isExtendedViewEnabled()) {
                    this.visibleElements.filter(function(element) {
                        return !isInExtendedViewPort(element);
                    }).forEach(function(element, index) {
                        this.visibleElements.splice(index, 1);
                    }.bind(this));
                }

            }.bind(this), CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS);

            inPageElements = Array.prototype.slice.apply(yjQuery("." + COMPONENT_CLASS));
            inPageElements.forEach(function(element) {
                intersectionObserver.unobserve(element);
                intersectionObserver.observe(element);
            });
        }.bind(this), 500);

        this._isInExtendedViewPort = function(target, isIntersecting) {
            return smartEditContractChangeListener.isExtendedViewEnabled() ? isInExtendedViewPort(target) : isIntersecting;
        };

        this.$onInit = function() {
            this.showDebug = true;
            this.toggleDebug = function() {
                this.showDebug = !this.showDebug;
            };

            this.getTotalSmartEditComponents = function() {
                return yjQuery("." + COMPONENT_CLASS).length;
            };

            this.getTotalVisibleSmartEditComponents = function() {
                return this.visibleElements.length;
            };

            this.getVisibleSmartEditComponents = function() {
                return this.visibleElements.map(function(element) {
                    return yjQuery(element).attr(ID_ATTRIBUTE);
                }).sort();
            };

            this.getTotalSakExecutorElements = function() {
                return sakExecutorService.getScopes().length;
            };

            this.getSakExecutorElements = function() {
                return sakExecutorService.getScopes().map(function(scope) {
                    return yjQuery(scope.element).attr(ID_ATTRIBUTE);
                }).sort();
            };


            this.getTotalResizeEventListeners = function() {
                return resizeListener._listenerCount();
            };

            this.getTotalPositionEventListeners = function() {
                return positionRegistry._listenerCount();
            };

            this.getTotalComponentsQueue = function() {
                return smartEditContractChangeListener._componentsQueueLength();
            };

            this.assertAllGood = function() {
                var totalVisibleElements = this.getTotalVisibleSmartEditComponents();

                return totalVisibleElements === this.getTotalSakExecutorElements() &&
                    totalVisibleElements <= this.getTotalPositionEventListeners() &&
                    totalVisibleElements <= this.getTotalResizeEventListeners() &&
                    this.getTotalPositionEventListeners() === this.getTotalResizeEventListeners() &&
                    this.getTotalComponentsQueue() <= this.getTotalSmartEditComponents();
            };
        };
    })
    .component('e2eDebugger', {
        template: '<button class="btn btn-info btn-sm toggleDebug" ng-click="ctrl.toggleDebug();"><span ng-if="!ctrl.showDebug">open</span><span ng-if="ctrl.showDebug">close</span> debug</button>' +
            '<div ng-show="ctrl.showDebug">' +
            '<pre>Total SmartEdit components in page: <div id="total-store-front-components"><strong>{{ctrl.getTotalSmartEditComponents()}}</strong></div></pre>' +
            '<pre>Total visible SmartEdit components: <div id="total-visible-store-front-components"><strong>{{ctrl.getTotalVisibleSmartEditComponents()}}</strong></div><span data-ng-if="false">{{ctrl.getVisibleSmartEditComponents() | json}}</span></pre>' +
            '<pre>Total sakExecutor stored elements:<div id="total-sak-executor-elements"><strong>{{ctrl.getTotalSakExecutorElements()}}</strong></div><span data-ng-if="false">{{ctrl.getSakExecutorElements() | json}}</span></pre>' +
            '<pre>Total resize eventlisteners in DOM:<div id="total-resize-listeners"><strong>{{ctrl.getTotalResizeEventListeners()}}</strong></div></pre>' +
            '<pre>Total position eventlisteners in DOM:<div id="total-reposition-listeners"><strong>{{ctrl.getTotalPositionEventListeners()}}</strong></div></pre>' +
            '<pre>Total components in queue:<div id="total-components-queue"><strong>{{ctrl.getTotalComponentsQueue()}}</strong></div></pre>' +
            '<pre><span id="healthStatus">{{ctrl.assertAllGood() ? "OK" : "ERROR (possible leak)"}}</span></pre>' +
            '</div>',
        controller: 'sakExecutorDebugController',
        controllerAs: 'ctrl'
    });
