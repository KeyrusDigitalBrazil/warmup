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
(function() {

    angular.module('browserServiceModule', [])
        .constant('SUPPORTED_BROWSERS', {
            IE: 'IE',
            CHROME: 'Chrome',
            FIREFOX: 'Firefox',
            EDGE: 'Edge',
            SAFARI: 'Safari',
            UNKNOWN: 'Uknown'
        })
        .service('browserService', function($window, SUPPORTED_BROWSERS) {

            this.getCurrentBrowser = function() {
                var browser = SUPPORTED_BROWSERS.UNKNOWN;
                if (typeof InstallTrigger !== 'undefined') {
                    browser = SUPPORTED_BROWSERS.FIREFOX;
                } else if ( /*@cc_on!@*/ false || !!document.documentMode) {
                    browser = SUPPORTED_BROWSERS.IE;
                } else if (!!window.StyleMedia) {
                    browser = SUPPORTED_BROWSERS.EDGE;
                } else if (!!window.chrome && !!window.chrome.webstore) {
                    browser = SUPPORTED_BROWSERS.CHROME;
                } else if (this._isSafari()) {
                    browser = SUPPORTED_BROWSERS.SAFARI;
                }

                return browser;
            }.bind(this);

            /*
                It is always better to detect a browser via features. Unfortunately, it's becoming really hard to identify 
                Safari, since newer versions do not match the previous ones. Thus, we have to rely on User Agent as the last
                option. 
            */
            this._isSafari = function() {
                // return this.getCurrentBrowser() === SUPPORTED_BROWSERS.SAFARI;
                var userAgent = $window.navigator.userAgent;
                var vendor = $window.navigator.vendor;

                var testFeature = /constructor/i.test(function HTMLElementConstructor() {});
                var testUserAgent = vendor && vendor.indexOf('Apple') > -1 && userAgent && !userAgent.match('CriOS');

                return testFeature || testUserAgent;
            };

            this.isIE = function() {
                return this.getCurrentBrowser() === SUPPORTED_BROWSERS.IE;
            };

            this.isFF = function() {
                return this.getCurrentBrowser() === SUPPORTED_BROWSERS.FIREFOX;
            };

            this.isSafari = function() {
                return this.getCurrentBrowser() === SUPPORTED_BROWSERS.SAFARI;
            };

            this.getBrowserLocale = function() {
                var locale = window.navigator.language.split("-");
                return locale.length === 1 ? locale[0] : (locale[0] + "_" + locale[1].toUpperCase());
            };

        });

})();
