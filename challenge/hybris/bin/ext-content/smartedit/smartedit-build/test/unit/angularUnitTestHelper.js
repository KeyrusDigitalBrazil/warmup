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
/* jshint unused:false, undef:false */ //new file which can be used to add helper methods for testing

//injects rootscope and q for global use
var $rootScope, $q;
var directiveScope, response, element, componentService;
var setupBackendResponse, templateSetup;

var testSetup = inject(function(_$rootScope_, _$q_) {
    $rootScope = _$rootScope_;
    $q = _$q_;
});


// temp - to be replaced with proper loading as in testDependencyLoader.js in future
var unit = {};
unit.mockData = {};
unit.mockServices = {};
unit.mockServices.dao = {};
unit.mockServices.services = {};

window.test = window.test || {};
window.test.unit = unit;

var AngularUnitTestHelper = (function() {

    function AngularUnitTestBuilder(moduleName) {

        var mockServices = {};
        var mockConstants = {};
        var injected = [];
        var translations = {};
        var injectedMap;
        var lastMock;

        function moduleExists(moduleName) {
            try {
                return !!angular.module(moduleName);
            } catch (ex) {
                return false;
            }
        }

        function createModulesInChainIfNecessary(moduleNames, alreadyIn) {
            alreadyIn = alreadyIn || [];
            moduleNames
                .filter(function(moduleName) {
                    return alreadyIn.indexOf(moduleName) === -1;
                })
                .forEach(function(moduleName) {
                    alreadyIn.push(moduleName);
                    if (!moduleExists(moduleName)) {
                        angular.module(moduleName, []);
                    } else {
                        createModulesInChainIfNecessary(angular.module(moduleName).requires, alreadyIn);
                    }
                });
        }

        function loadDependencyModules(moduleName) {
            var directDependencies = angular.module(moduleName).requires;
            var defaultDependencies = ['yjqueryModule', 'configModule', 'templateCacheDecoratorModule', 'ui.select', 'ui.bootstrap'];
            var allDependencies = directDependencies.concat(defaultDependencies);
            createModulesInChainIfNecessary(allDependencies);
            allDependencies.forEach(function(moduleName) {
                try {
                    module(moduleName);
                } catch (e) {
                    //may have already been loadeds
                }
            });
        }

        function addDefaultMocks() {
            mockConstants.l10nFilter = function(localizedMap) {
                return localizedMap.en;
            };
        }

        function loadModule() {
            if (!moduleExists(moduleName)) {
                throw Error('Could not find module ' + moduleName + ', should this test exist in smartedit or smarteditContainer?');
            }

            // Add default mocks
            addDefaultMocks();
            // Create/load the dependency modules as required
            loadDependencyModules(moduleName);
            // Load Translations
            module('pascalprecht.translate', function($translateProvider) {
                $translateProvider.translations('en', translations);
                $translateProvider.preferredLanguage('en');
            });

            // Prepare required module and provide the mocks
            module(moduleName, function($provide) {
                // Load mock services
                Object.keys(mockServices).forEach(function(mockServiceName) {
                    var mockService = mockServices[mockServiceName];
                    $provide.value(mockServiceName, mockService);
                });

                // Load mock constants
                Object.keys(mockConstants).forEach(function(mockConstant) {
                    var mockValue = mockConstants[mockConstant];
                    $provide.constant(mockConstant, mockValue);
                });
            });
        }

        function loadInjected() {
            loadModule();
            var injectedMap = {};
            var defaultInjected = ['$controller', '$q', '$rootScope', '$compile', '$timeout'];
            var allInjected = injected.concat(defaultInjected);
            inject(allInjected.concat([function() {
                var injectedServices = Array.prototype.slice.call(arguments);
                allInjected.forEach(function(injectedName, i) {
                    injectedMap[injectedName] = injectedServices[i];
                });
            }]));
            return injectedMap;
        }

        function build() {
            injectedMap = loadInjected();
            //extendMockBehaviour(injectedMap.$q);
            return {
                mocks: mockServices,
                injected: injectedMap,
                detectChanges: function() {
                    injectedMap.$rootScope.$digest();
                }
            };
        }

        this.mock = function(serviceName, functionName) {
            mockServices[serviceName] = mockServices[serviceName] || {};
            if (functionName) {
                var spy = jasmine.createSpy(functionName);
                lastMock = spy;

                spy.and.returnResolvedPromise = function(data) {
                    spy.and.callFake(function() {
                        return injectedMap.$q.when(data);
                    });
                };

                spy.and.returnRejectedPromise = function(data) {
                    spy.and.callFake(function() {
                        return injectedMap.$q.reject(data);
                    });
                };

                mockServices[serviceName][functionName] = spy;
            }
            return this;
        };

        this.and = {};

        this.and.returnValue = function(data) {
            if (!lastMock) {
                return;
            }
            lastMock.and.returnValue(data);
            return this;
        }.bind(this);

        this.and.callFake = function(callback) {
            if (!lastMock) {
                return;
            }
            lastMock.and.callFake(callback);
            return this;
        }.bind(this);

        this.and.returnResolvedPromise = function(data) {
            if (!lastMock) {
                return;
            }
            lastMock.and.returnResolvedPromise(data);
            return this;
        }.bind(this);

        this.and.returnRejectedPromise = function(data) {
            if (!lastMock) {
                return;
            }
            lastMock.and.returnRejectedPromise(data);
            return this;
        }.bind(this);

        this.mockConstant = function(constant, value) {
            mockConstants[constant] = value;
            return this;
        };

        this.inject = function(serviceName) {
            injected.push(serviceName);
            return this;
        };

        this.withTranslations = function(newTranslations) {
            translations = newTranslations;
            return this;
        };

        this.controller = function(controllerName, locals) {
            locals = locals || {};
            locals.$scope = locals.$scope || {};
            locals.$routeParams = locals.$routeParams || {};
            var fixture = build();
            fixture.controller = fixture.injected.$controller(controllerName, locals);
            fixture.detectChanges();
            return fixture;
        };

        this.directive = function(template, locals) {
            var fixture = build();

            directiveScope = fixture.injected.$rootScope.$new();
            window.smarteditJQuery.extend(directiveScope, locals || {});
            element = angular.element(template);
            fixture.injected.$compile(element)(directiveScope);
            fixture.detectChanges();

            window.smarteditJQuery('body').append(element);
            fixture.element = element;
            fixture.scope = directiveScope;
            return fixture;
        };

        this.component = function(template, locals) {
            return this.directive(template, locals);
        };

        this.service = function(serviceName) {
            this.inject(serviceName);
            var fixture = build();
            fixture.service = fixture.injected[serviceName];
            return fixture;
        };
    }

    return {
        prepareModule: function(moduleName) {
            return new AngularUnitTestBuilder(moduleName);
        }
    };

}());
