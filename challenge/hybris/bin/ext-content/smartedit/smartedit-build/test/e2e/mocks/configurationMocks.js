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
/**
 * Extension requirement:
 * The angular module "configurationMocksModule" must be created and must define the constant {Array} "CONFIGURATION_MOCKS"
 * 
 * The 'generateSmarteditIndex.html' must include the configuration mocks angular modules in the property conf.e2eSetup.headerContent (base smartedit files):
 * 
 * @example
 * file: customExtension/smartedit-custom-build/config/generateSmarteditIndexHtml.js
 * <script src="/jsTests/tests/customExtension/e2e/extensionConfigurationMocks.js"></script>
 * <script src="/smartedit-build/test/e2e/mocks/configurationMocks.js"></script>
 * 
 * @example
 * file: /jsTests/tests/customExtension/e2e/extensionConfigurationMocks.js
  angular
    .module('configurationMocksModule', [])
    .constant('CONFIGURATION_MOCKS', [{
        value: "\"/cmswebservices/v1/i18n/languages\"",
        key: "i18nAPIRoot"
    }, {
        value: "{\"smartEditLocation\":\"/jsTests/tests/extensionContainer/e2e/customMock.js.js\"}",
        key: "applications.extensionCustomMock"
    }, {
        value: "{\"smartEditContainerLocation\":\"/web/webroot/customExtension/js/customExtensionContainer.js\"}",
        key: "applications.customExtensionContainer"
    }, {
        value: "{\"smartEditLocation\":\"/web/webroot/customExtension/js/customExtension.js\"}",
        key: "applications.customExtension"
    }]);
 */

/**
 * Since we use the injector to see if CONFIGURATION_MOCKS is defined, we need to make sure first that the
 * configurationMocksModule exists. This wil just test if it exists, or create it if not. So it won't break
 * the angular bootstrapping
 */
angular.module('configurationMocksModule') || angular.module('configurationMocksModule', []);

angular
    .module('configurationMocks', ['ngMockE2E', 'configurationMocksModule'])
    .constant('SMARTEDIT_ROOT', 'smartedit-build/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    // for latency testing:
    // .config(function($provide) {
    //     $provide.decorator('$httpBackend', function($delegate) {
    //         var proxy = function(method, url, data, callback, headers) {
    //             var interceptor = function() {
    //                 var _this = this,
    //                     _arguments = arguments;
    //                 setTimeout(function() {
    //                     callback.apply(_this, _arguments);
    //                 }, 700);
    //             };
    //             return $delegate.call(this, method, url, data, interceptor, headers);
    //         };
    //         for (var key in $delegate) {
    //             proxy[key] = $delegate[key];
    //         }
    //         return proxy;
    //     });
    // })
    .run(
        function($httpBackend, $injector) {
            $httpBackend.whenGET(/customView\.html/).passThrough();
            $httpBackend.whenGET(/view/).passThrough();
            $httpBackend.whenGET(/\.js/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();
            $httpBackend.whenGET(/web\/webroot/).passThrough();
            $httpBackend.whenGET(/smartedit-build/).passThrough();

            var configurationMocks = [];
            if (!$injector.has('CONFIGURATION_MOCKS')) {
                console.error("angular.constant 'CONFIGURATION_MOCKS' not found. Please create a 'configurationMocksModule' angular module and define a 'CONFIGURATION_MOCKS' constant {Array} with your extension specific resources.");
            } else {
                configurationMocks = $injector.get('CONFIGURATION_MOCKS');
            }

            var map = [{
                value: "\"thepreviewTicketURI\"",
                key: "previewTicketURI"
            }, {
                value: "{\"smartEditLocation\":\"/smartedit-build/test/e2e/smartedit/clickThroughOverlay.js\"}",
                key: "applications.clickThroughOverlayModule"
            }, {
                value: "{\"smartEditContainerLocation\":\"/smartedit-build/test/e2e/smarteditContainer/clickThroughOverlay.js\"}",
                key: "applications.clickThroughOverlayTriggerModule"
            }, {
                value: "[\"*\"]",
                key: "whiteListedStorefronts"
            }].concat(configurationMocks);

            var stringifiedArray = sessionStorage.getItem("additionalTestJSFiles");
            if (stringifiedArray) {
                var additionalTestJSFiles = JSON.parse(stringifiedArray);
                Array.prototype.push.apply(map, additionalTestJSFiles);
            }

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            // $httpBackend.whenPUT(/configuration/).respond(404);
        });

angular.module('smarteditloader').requires.push('configurationMocks');
angular.module('smarteditcontainer').requires.push('configurationMocks');
angular.module('configurationMocks').requires.push('configurationMocksModule');
angular.module('smarteditcontainer').constant('isE2eTestingActive', true);
angular.module('smarteditcontainer').constant('testAssets', true);
angular.module('smarteditcontainer').constant('e2eMode', true);
