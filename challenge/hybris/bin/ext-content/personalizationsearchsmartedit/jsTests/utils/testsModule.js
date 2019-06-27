/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/* jshint unused:false */
customMatchers = function() {
    var PromiseMatcherHelper = {
        states: {
            RESOLVED: 'resolved',
            REJECTED: 'rejected'
        },
        getPromiseInfo: function(promise) {
            var that = this;
            var rootScope;
            angular.mock.inject(function($injector) {
                rootScope = $injector.get('$rootScope');
            });

            var promiseInfo = {};
            promise.then(function(data) {
                promiseInfo.status = that.states.RESOLVED;
                promiseInfo.data = data;
            }, function(data) {
                promiseInfo.status = that.states.REJECTED;
                promiseInfo.data = data;
            });

            rootScope.$apply(); // Trigger promise resolution
            return promiseInfo;
        },
        getMessageForPromise: function(promiseInfo, expected) {
            return function() {
                var unresolvedMessage = 'Expected promise to be ' + promiseInfo.status;
                var badDataMessage = 'Expected promise resolved data ' + jasmine.pp(promiseInfo.data) + ' to be ' + jasmine.pp(expected);
                return promiseInfo.status !== PromiseMatcherHelper.states.RESOLVED ? unresolvedMessage : badDataMessage;
            };
        }
    };

    jasmine.addMatchers({
        toEqualData: function(util, customEqualityTesters) {
            return {
                compare: function(actual, expected) {
                    var passed = angular.equals(actual, expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + actual + (passed ? '' : ' not') + ' to equal ' + expected
                    };
                }
            };
        },
        toHaveClass: function(util, customEqualityTesters) {
            return {
                compare: function(element, className) {
                    var passed = element.hasClass(className);

                    return {
                        pass: passed,
                        message: 'Expected ' + element + (passed ? '' : ' not') + ' to have class ' + className
                    };
                }
            };
        },
        fail: function(util, customEqualityTesters) {
            return {
                compare: function(actual, errorMessage) {
                    return {
                        pass: false,
                        message: errorMessage
                    };
                }
            };
        },
        toHaveThatManyAlerts: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert span").length;
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + element + (passed ? '' : ' not') + ' to have ' + expected + ' alert(s)'
                    };
                }
            };
        },
        messageToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert-success span").text();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected message' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        alertToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert-danger span").text();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected alert' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        toContainChildElement: function(util, customEqualityTesters) {
            return {
                compare: function(element, childElement) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.find(childElement).length === 1);

                    return {
                        pass: passed,
                        message: 'Expected ' + html + (passed ? '' : ' not') + ' to have child element <' + childElement + '>'
                    };
                }
            };
        },
        toContainElementText: function(util, customEqualityTesters) {
            return {
                compare: function(element, text) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.text().indexOf(text) >= 0);

                    return {
                        pass: passed,
                        message: 'Expected ' + html + (passed ? '' : ' not') + ' to contain text "' + text + '"'
                    };
                }
            };
        },
        inputToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div input[type=text]").val();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected input' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        displayToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find('span').html();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + actual + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        flagToBeTrue: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/tick.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be true'
                    };
                }
            };
        },
        flagToBeFalse: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/no-tick.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be false'
                    };
                }
            };
        },
        flagToBeUndetermined: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/question.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be undetermined'
                    };
                }
            };
        },
        toBeInEditMode: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> div > input[type=text][data-ng-model='editor.temp']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected' + (passed ? '' : ' not') + ' to be in edit mode'
                    };
                }
            };
        },
        calendarToBeDisplayed: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("ul.dropdown-menu").css('display') === 'block');

                    return {
                        pass: passed,
                        message: 'Expected calendar' + (passed ? '' : ' not') + ' to be displayed'
                    };
                }
            };
        },
        toHaveAttribute: function(util, customEqualityTesters) {
            return {
                compare: function(element, attributeName, attributeValue) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.attr(attributeName) === attributeValue);

                    return {
                        pass: passed,
                        message: 'Expected ' + html + (passed ? '' : ' not') + ' to have attribute ' + attributeName + ' as ' + attributeValue
                    };
                }
            };
        },
        toBeRejected: function(util, customEqualityTesters) {
            return {
                compare: function(promise) {
                    var passed = (PromiseMatcherHelper.getPromiseInfo(promise).status === PromiseMatcherHelper.states.REJECTED);

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be rejected'
                    };
                }
            };
        },
        toBeResolved: function(util, customEqualityTesters) {
            return {
                compare: function(promise) {
                    var passed = (PromiseMatcherHelper.getPromiseInfo(promise).status === PromiseMatcherHelper.states.RESOLVED);

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be resolved'
                    };
                }
            };
        },
        toBeRejectedWithData: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var errorMessage = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.REJECTED && angular.equals(promiseInfo.data, expected));

                    return {
                        pass: passed,
                        message: errorMessage
                    };
                }
            };
        },
        toBeResolvedWithData: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var errorMessage = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.RESOLVED && angular.equals(promiseInfo.data, expected));

                    return {
                        pass: passed,
                        message: errorMessage
                    };
                }
            };
        },
        toExist: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (window.smarteditJQuery(element).length > 0);

                    return {
                        pass: passed,
                        message: 'Expected element' + (passed ? '' : ' not') + ' to exist'
                    };
                }
            };
        },
        toBeRejectedWithDataContaining: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.REJECTED && promiseInfo.data.some(function(actual) {
                        return angular.equals(actual, expected);
                    }));

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be rejected with data containing ' + expected
                    };
                }
            };
        },
        toBePromise: function(util, customEqualityTesters) {
            return {
                compare: function(object) {
                    var passed = !!object.then;

                    return {
                        pass: passed,
                        message: 'Expected ' + object + (passed ? '' : ' not') + ' to be promise'
                    };
                }
            };
        }
    });
};

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

        function createModulesInChainIfNecessary(moduleNames) {
            moduleNames.forEach(function(moduleName) {
                if (!moduleExists(moduleName)) {
                    angular.module(moduleName, []);
                }
                createModulesInChainIfNecessary(angular.module(moduleName).requires);
            });
        }

        function loadDependencyModules(moduleName) {
            var directDependencies = angular.module(moduleName).requires;
            var defaultDependencies = ['cmssmarteditContainerTemplates', 'cmssmarteditTemplates', 'ui.select', 'ui.bootstrap'];
            var allDependencies = directDependencies.concat(defaultDependencies);
            createModulesInChainIfNecessary(allDependencies);
            allDependencies.forEach(function(moduleName) {
                module(moduleName);
            });
        }

        function addDefaultMocks() {
            mockConstants.l10nFilter = function(localizedMap) {
                return localizedMap.en;
            };
        }

        function loadModule() {
            if (!moduleExists(moduleName)) {
                throw Error('Could not find module ' + moduleName + ', should this test exist in cmssmartedit or cmssmarteditContainer?');
            }

            // Add custom matchers
            customMatchers.bind(jasmine.getEnv().currentSpec)();

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
            var defaultInjected = ['$controller', '$q', '$rootScope', '$compile'];
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

        this.and.return = function(data) {
            if (!lastMock) {
                return;
            }
            lastMock.and.return(data);
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

            var directiveScope = fixture.injected.$rootScope.$new();
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
