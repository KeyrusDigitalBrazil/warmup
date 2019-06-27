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

var DEFAULT_IMPLICIT_WAIT = 5000; // the default timeout that the driver waits before every element() call
var NO_IMPLICIT_WAIT = 0; // the wait time to be used when the driver is checking for non presence/display of an element 
var EXPLICIT_WAIT = 30000; // the timeout to be used for specific conditions to be fulfilled (e.g., the page is ready)
var OVERALL_TEST_TIMEOUT = 50000; // the overall timeout for any test to pass or fail
var NEGATIVE_WAIT = 5000; // the timeout to be used when waiting for an element to become not present/not displayed
var ASYNC_WAIT = 5000; // the timedout value waiting for asynchronous Angular tasks to finish  

exports.config = {
    allScriptsTimeout: ASYNC_WAIT,

    specs: [
        'e2e/**/*Test.js'
    ],

    seleniumServerJar: '../node_modules/protractor/selenium/selenium-server-standalone-3.0.1.jar',

    capabilities: {
        'browserName': 'chrome',
        'shardTestFiles': false,
        'maxInstances': 10,
        'chromeOptions': {
            args: ['lang=en-US']
        }
    },
    directConnect: true,

    troubleshoot: false,

    baseUrl: 'http://127.0.0.1:7000',

    framework: 'jasmine2',

    jasmineNodeOpts: {
        defaultTimeoutInterval: OVERALL_TEST_TIMEOUT
    },

    //method found in node_modules/protractor/docs/referenceConf.js :
    onPrepare: function() {

        global.e2e = {};
        var dependencyLoader = null;
        try {
            dependencyLoader = require('./testDependencyLoader.js');
        } catch (e) {} //do not throw any error here to avoid failing on CI, because an extension could not have the file testDependencyLoader.js
        if (dependencyLoader) {
            dependencyLoader.loadE2eDependencies(browser, global.e2e);
        }

        global.EC = protractor.ExpectedConditions;

        var jasmineReporters = require('jasmine-reporters');
        jasmine.getEnv().addReporter(new jasmineReporters.JUnitXmlReporter({
            consolidateAll: false,
            savePath: 'jsTarget/test/smarteditContainer/junit/protractor'
        }));

        //this is protractor but both protractor and browser instances are available in this closure

        browser.setSize = function() {
            return browser.driver.manage().window().setSize(2500, 1000);
        };
        browser.setSize();

        // Wait up to 5 seconds for trying to find an element before failing
        browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT);

        //If you are outputting logs into the shell after running a protractor test.
        //i.e. grunt connect:test protractor:run --specs='cmsxuiJSTests/e2e/apiAuthentication/apiAuthenticationTest.js'
        //Setting this value to anything other than 0 will wait in milliseconds between each log statement
        global.waitForSprintDemoLogTime = 0;
        //Set log levels to display in shell
        global.sprintDemoLogLevels = ["WARNING", "INFO"];
        //Show any log parsing errors - by default they are not shown
        global.sprintDemoShowLogParsingErrors = false;

        browser.dumpLogs = function() {
            return browser.manage().logs().get('browser').then(function(browserLogs) {
                browserLogs
                    .filter(function(log) {
                        return log.message.indexOf(".png") === -1 && log.message.indexOf("favicon.ico") === -1;
                    })
                    .filter(function(log) {
                        return log.level.name === 'SEVERE';
                    })
                    .forEach(function(log) {
                        console.log("Brower log:", log.message);
                    });
                return true;
            });
        };


        browser.getInnerHTML = function(source) {
            var ele = browser._getElementFromSource(source);
            return browser.executeScript("return arguments[0].innerHTML;", ele);
        };

        browser.waitForContainerToBeReady = function() {
            //click on load preview button
            return browser.wait(protractor.ExpectedConditions.elementToBeClickable(element(by.id('nav-expander'))), EXPLICIT_WAIT, "could not find burger menu/toolbar when first loading app").then(function() {
                return browser.waitForAngular().then(function() {
                    // wait for any modal overlay to disappear
                    return browser.waitUntilNoModal();
                });
            });
        };

        browser.waitUntilNoModal = function() {
            return browser.waitForAbsence(by.css("body.modal-open"));
        };
        browser.waitUntilModalAppears = function() {
            return browser.waitForPresence(by.css("body.modal-open"));
        };

        browser.waitForFrameToBeReady = function() {
            /*
             * protractor cannot nicely use browser api until angular app is bootstrapped.
             * to do so it needs to see ng-app attribute.
             * But in our smartEdit setup, app is bootstrapped programmatically, not through ng-app
             * workaround consists then in waiting arbitrary amount fo time
             */
            return browser.wait(function() {
                var initialWaitForAngularEnabled = browser.waitForAngularEnabled();
                browser.waitForAngularEnabled(false);
                browser.waitUntilNoModal();
                return element.all(by.css('body')).then(function(bodyArray) {
                    var hasBody = bodyArray.length === 1;
                    if (hasBody && initialWaitForAngularEnabled) {
                        browser.waitForAngularEnabled(true);
                    }
                    return hasBody;
                });
            }, EXPLICIT_WAIT, "could not find data-smartedit-ready='true' attribute on the iframe body tag");
        };

        browser.switchToIFrame = function(waitForFrameToBeReady) {
            return browser.switchToParent().then(function() {
                return browser.driver.switchTo().frame(element(by.tagName('iframe')).getWebElement('')).then(function() {
                    if (waitForFrameToBeReady !== false) {
                        return browser.waitForFrameToBeReady();
                    } else {
                        return;
                    }
                });
            });
        };

        browser.waitForWholeAppToBeReady = function() {
            return browser.waitForContainerToBeReady().then(function() {
                return browser.switchToIFrame().then(function() {
                    return browser.waitForFrameToBeReady().then(function() {
                        return browser.switchToParent().then(function() {
                            //console.info("whole app is ready");
                            return;
                        });
                    });
                });
            });
        };

        browser.linkAndBackToParent = function(bySelector) {
            return browser.switchToIFrame().then(function() {
                return browser.click(bySelector).then(function() {
                    return browser.switchToParent().then(function() {
                        return browser.waitForWholeAppToBeReady();
                    });
                });
            });
        };

        browser.clickLoadPreview = function() {
            //click on load preview button
            return browser.waitForContainerToBeReady().then(function() {
                return element(by.id('loadPreview')).click();
            });
        };

        browser.switchToParent = function() {
            return browser.driver.switchTo().defaultContent();
        };

        browser.waitForUrlToMatch = function(regex) {
            browser.wait(function() {
                return browser.getCurrentUrl().then(function(url) {
                    return regex.test(url);
                });
            }, EXPLICIT_WAIT, 'URL did not change');
        };

        var disableNgAnimate = function() {
            angular.module('disableNgAnimate', []).run(['$animate', function($animate) {
                $animate.enabled(false);
            }]);
        };
        browser.addMockModule('disableNgAnimate', disableNgAnimate);

        var disableCssAnimate = function() {
            angular
                .module('disableCssAnimate', [])
                .run(function() {
                    var style = document.createElement('style');
                    style.type = 'text/css';
                    style.innerHTML = '* {' +
                        /*CSS transitions*/
                        '-o-transition:none !important; ' +
                        '-moz-transition:none !important; ' +
                        '-ms-transition:none !important; ' +
                        '-webkit-transition:none !important; ' +
                        'transition:none !important; ' +
                        '}';
                    document.getElementsByTagName('head')[0].appendChild(style);
                });
        };

        browser.addMockModule('disableCssAnimate', disableCssAnimate);

        browser._getElementFromSource = function(source) {
            if (typeof source === 'string') {
                return element(by.css(source));
            } else if (source.hasOwnProperty('then')) {
                return source;
            } else {
                return element(source);
            }
        };

        browser.moveTo = function(selector) {
            return browser.executeScript("window.smarteditJQuery(arguments[0]).mouseover();", selector);
        };

        browser.click = function(source, errorMessage) {
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not click on element " + source;
            return browser.waitUntil(protractor.ExpectedConditions.elementToBeClickable(ele), message).then(function() {
                return this._getElementFromSource(source).click();
            }.bind(this));
        };

        browser.clear = function(source, errorMessage) {
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not find element " + source;
            return browser.waitForPresence(source, message).then(function() {
                return ele.clear();
            });
        };

        browser.sendKeys = function(source, text, errorMessage) {
            text = text || "";
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not find element " + source;
            return browser.waitForPresence(source, message).then(function() {
                return ele.sendKeys(text);
            });
        };

        browser.clearAndSendKeys = function(source, text, errorMessage) {
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not click on element " + source;
            return browser.waitUntil(protractor.ExpectedConditions.elementToBeClickable(ele), message).then(function() {
                return this._getElementFromSource(source).clear().sendKeys(text);
            }.bind(this));
        };

        browser.waitUntil = function(assertionFunction, errorMessage, specificWait) {
            specificWait = specificWait || EXPLICIT_WAIT;
            var message = errorMessage ? errorMessage : "could not match condition";
            return browser.wait(assertionFunction, specificWait, message);
        };

        browser.scrollToBottom = function(scrollElm) {
            return browser.executeScript('arguments[0].scrollTop = arguments[0].scrollHeight;', scrollElm.getWebElement());
        };

        browser.scrollToTop = function(scrollElm) {
            return browser.executeScript('arguments[0].scrollTop = 0;', scrollElm.getWebElement());
        };

        browser.waitForPresence = function(source, errorMessage) {
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not find element " + source;
            return browser.waitUntil(EC.presenceOf(ele), message);
        };

        var waitForNonPresence = function(source, errorMessage) {
            var ele = browser._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "should not have found element " + source;
            return browser.waitUntil(EC.stalenessOf(ele), message, NEGATIVE_WAIT);
        };

        browser.waitForAbsence = function(source, errorMessage) {
            return browser.driver.manage().timeouts().implicitlyWait(NO_IMPLICIT_WAIT).then(function() {
                return waitForNonPresence(source, errorMessage).then(function(result) {
                    return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                        return result;
                    });
                }, function(reason) {
                    return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                        return reason;
                    });
                });
            });
        };

        browser.isAbsent = function(source) {
            return browser.waitForAbsence(source).then(function() {
                return true;
            }, function() {
                return false;
            });

        };

        browser.isPresent = function(source) {
            return browser.waitForPresence(source).then(function() {
                return true;
            }, function() {
                return false;
            });

        };

        browser.waitToBeDisplayed = function(source, errorMessage) {
            var message = errorMessage ? errorMessage : "could not find element " + source;
            return browser.waitUntil(function() {
                return this._getElementFromSource(source).isDisplayed().then(function() {
                    return true;
                });
            }.bind(this), message);
        };

        var waitNotToBeDisplayed = function(source, errorMessage) {
            var message = errorMessage ? errorMessage : "could not find element " + source;
            return browser.waitUntil(function() {
                return browser._getElementFromSource(source).isDisplayed().then(function() {
                    return false;
                });
            }.bind(this), message);
        };


        browser.waitNotToBeDisplayed = function(source, errorMessage) {
            return browser.driver.manage().timeouts().implicitlyWait(NO_IMPLICIT_WAIT).then(function() {
                return waitNotToBeDisplayed(source, errorMessage).then(function(result) {
                    return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                        return result;
                    });
                }, function(reason) {
                    return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                        return reason;
                    });
                });
            });
        };

        browser.waitForVisibility = function(source) {
            return browser.waitFor(source, "visibilityOf", "Could not verify visibility of " + source);
        };

        browser.waitFor = function(source, expectedConditions, errorMessage, specificWait) {
            specificWait = specificWait || EXPLICIT_WAIT;
            return browser.wait(EC[expectedConditions](browser._getElementFromSource(source)), specificWait, (errorMessage ? errorMessage : "Could not verify " + expectedConditions + " for " + source));
        };

        // [ find element helper ]

        browser.findElement = function(source, searchOption, errorMessage) {

            if (!errorMessage) {
                errorMessage = "Could not find element " + source;
            }

            switch (typeof searchOption) {
                case "boolean":
                    searchOption = (searchOption) ? "visibilityOf" : "presenceOf";
                    break;
                case "string":
                    break;
                default:
                    searchOption = "presenceOf";
            }

            browser.waitFor(source, searchOption, errorMessage);
            return browser._getElementFromSource(source);

        };

        // [ scrolling helper ]
        browser.testThatOverflowingContentIsHidden = function(source) {
            var element = browser._getElementFromSource(source);
            return element.getCssValue("height").then(function(height) {
                expect(element.getAttribute("scrollHeight")).toBeGreaterThan(height.replace("px", ""));
            });
        };

        //---------------------------------------------------------------------------------
        //-----------------------------------ACTIONS---------------------------------------
        //---------------------------------------------------------------------------------

        /* all keys of protractor.Key :
         *[ 'NULL', 'CANCEL', 'HELP', 'BACK_SPACE', 'TAB', 'CLEAR', 'RETURN', 'ENTER', 'SHIFT', 'CONTROL',
         *  'ALT', 'PAUSE', 'ESCAPE', 'SPACE', 'PAGE_UP', 'PAGE_DOWN', 'END', 'HOME', 'ARROW_LEFT', 'LEFT',
         *  'ARROW_UP', 'UP', 'ARROW_RIGHT', 'RIGHT', 'ARROW_DOWN', 'DOWN', 'INSERT', 'DELETE', 'SEMICOLON',
         *  'EQUALS', 'NUMPAD0', 'NUMPAD1', 'NUMPAD2', 'NUMPAD3', 'NUMPAD4', 'NUMPAD5', 'NUMPAD6', 'NUMPAD7',
         *  'NUMPAD8', 'NUMPAD9', 'MULTIPLY', 'ADD', 'SEPARATOR', 'SUBTRACT', 'DECIMAL', 'DIVIDE', 'F1',
         *  'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9', 'F10', 'F11', 'F12', 'COMMAND', 'META', 'chord' ]
         */

        browser.press = function(protractorKey) {
            browser.actions().sendKeys(protractorKey).perform();
        };

        browser.isDelayed = false;

        var currentDelayedState;

        var controlFlow = browser.driver.controlFlow();
        var originalExecute = browser.driver.controlFlow().execute.bind(controlFlow);
        browser.driver.controlFlow().execute = function() {

            if (currentDelayedState !== browser.isDelayed) {
                console.info("switched to isDelayed ", browser.isDelayed);
            }
            currentDelayedState = browser.isDelayed;

            // queue 10ms wait
            var args = arguments;
            if (browser.isDelayed) {
                return originalExecute(function() {
                    return protractor.promise.delayed(10).then(function() {
                        return originalExecute.apply(null, args);
                    });
                });
            } else {
                return originalExecute.apply(null, args);
            }
        };

        //---------------------------------------------------------------------------------
        //---------------------------------ASSERTIONS--------------------------------------
        //---------------------------------------------------------------------------------

        beforeEach(function() {

            // TODO - extract from some build process variable from ant
            if (browser.setStorefrontDelayConfigInSessionStorage) {
                var delayConfig = "DELAY_NONE";
                browser.setStorefrontDelayConfigInSessionStorage(delayConfig);
            }

            browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {

                /**
                 * Given a normal compare function and matcherName, this creates a new matcher
                 * that catches exceptions being thrown by the compare, and returns a failed
                 * result with a failure messager containing the matcher name and exception details
                 */
                function getSafeMatcher(matcherName, compareFunction) {
                    return {
                        compare: function(actual, expected) {
                            try {
                                return compareFunction(actual, expected);
                            } catch (e) {
                                return {
                                    pass: false,
                                    message: "Error executing matcher '" + matcherName + "': " + e
                                };
                            }
                        }
                    };
                }

                jasmine.addMatchers({
                    toEqualData: function() {
                        return {
                            compare: function(actual, expected) {
                                var passed = (JSON.stringify(actual) === JSON.stringify(expected));
                                return {
                                    pass: passed,
                                    message: 'Expected ' + actual + (passed ? '' : ' not') + ' to equal ' + expected
                                };
                            }
                        };
                    },
                    toEqualText: function() {
                        return getSafeMatcher('toEqualText', function(actualLocatorOrElement, expectedText) {
                            var actual = "";
                            return {
                                pass: element(actualLocatorOrElement).getText().then(function(actualText) {
                                    actual = actualText;
                                    return actualText.trim() === expectedText.trim();
                                }),
                                message: 'Expected source text ' + actual + ' to equal text ' + expectedText + ". Note: both will be trimmed."
                            };
                        });
                    },
                    toContainClass: function() {
                        return getSafeMatcher('toContainClass', function(actualLocatorOrElement, expectedCssClass) {
                            var actualClasses = "";
                            return {
                                pass: element(actualLocatorOrElement).getAttribute('class').then(function(classes) {
                                    actualClasses = classes;
                                    return (classes.split(' ').indexOf(expectedCssClass)) !== -1;
                                }),
                                message: 'Expected source classes ' + actualClasses + ' to contain the css class ' + expectedCssClass
                            };
                        });
                    },
                    toBeEmptyString: function() {
                        return {
                            compare: function(actual) {
                                return {
                                    pass: actual === ''
                                };
                            }
                        };
                    },
                    toContain: function() {
                        return {
                            compare: function(actual, expected) {
                                return {
                                    pass: actual.indexOf(expected) > -1
                                };
                            }
                        };
                    },
                    toBeDisplayed: function() {
                        return {
                            compare: function(actual) {
                                return {
                                    pass: actual.isDisplayed()
                                };
                            }
                        };
                    },
                    toBeWithinRange: function() {
                        return {
                            compare: function(actual, expected, range) {
                                range = range || 1;
                                return {
                                    pass: Math.abs(expected - actual) < range
                                };
                            }
                        };
                    },
                    toBeAbsent: function() {
                        return {
                            compare: function(locator) {
                                var message = 'Expected element with locator ' + locator + ' to be present in DOM';
                                return {
                                    pass: browser.driver.manage().timeouts().implicitlyWait(NO_IMPLICIT_WAIT).then(function() {
                                        return browser.wait(function() {
                                            return element(locator).isPresent().then(function(isPresent) {
                                                return !isPresent;
                                            });
                                        }, EXPLICIT_WAIT, message).then(function(result) {
                                            return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                                                return result;
                                            });
                                        });
                                    }),
                                    message: message
                                };
                            }
                        };
                    }
                });
            });
        });

        afterEach(function(done) {
            browser.waitForAngularEnabled(true);
            //browser.dumpLogs();
            done();
        });

    },
    params: {
        implicitWait: DEFAULT_IMPLICIT_WAIT,
        explicitWait: EXPLICIT_WAIT
    }
};
