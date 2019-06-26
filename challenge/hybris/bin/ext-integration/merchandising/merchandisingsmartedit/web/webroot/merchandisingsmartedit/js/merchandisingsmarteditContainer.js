/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditcontainer.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditContainer_bundle.js":
/*!*********************************************************************************************************!*\
  !*** ./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditContainer_bundle.js ***!
  \*********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {




/***/ }),

/***/ "./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditcontainer.ts":
/*!**************************************************************************************************!*\
  !*** ./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditcontainer.ts ***!
  \**************************************************************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var merchandisingsmarteditContainer_merchandisingsmarteditContainer_bundle_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! merchandisingsmarteditContainer/merchandisingsmarteditContainer_bundle.js */ "./jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditContainer_bundle.js");
/* harmony import */ var merchandisingsmarteditContainer_merchandisingsmarteditContainer_bundle_js__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(merchandisingsmarteditContainer_merchandisingsmarteditContainer_bundle_js__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _merchandisingsmarteditcommons_merchandisingExperienceInterceptor_ts__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./../merchandisingsmarteditcommons/merchandisingExperienceInterceptor.ts */ "./jsTarget/web/features/merchandisingsmarteditcommons/merchandisingExperienceInterceptor.ts");
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



angular__WEBPACK_IMPORTED_MODULE_0__["module"]('merchandisingsmarteditContainer', [
    'merchandisingExperienceInterceptorModule',
    'loadConfigModule',
    'smarteditServicesModule'
]).run(["loadConfigManagerService", "sharedDataService", function (loadConfigManagerService, sharedDataService) {
    'ngInject';
    loadConfigManagerService.loadAsObject().then(function (configurations) {
        sharedDataService.set('contextDrivenServicesMerchandisingUrl', configurations.contextDrivenServicesMerchandisingUrl);
    });
}]);


/***/ }),

/***/ "./jsTarget/web/features/merchandisingsmarteditcommons/merchandisingExperienceInterceptor.ts":
/*!***************************************************************************************************!*\
  !*** ./jsTarget/web/features/merchandisingsmarteditcommons/merchandisingExperienceInterceptor.ts ***!
  \***************************************************************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

angular__WEBPACK_IMPORTED_MODULE_0__["module"]('merchandisingExperienceInterceptorModule', ['interceptorHelperModule', 'functionsModule', 'smarteditServicesModule', 'resourceLocationsModule', 'yLoDashModule'])
    /**
     * @ngdoc service
     * @name merchandisingExperienceInterceptorModule.merchandisingExperienceInterceptor
     *
     * @description
     * A HTTP request interceptor which intercepts all 'merchandisingcmswebservices' requests and adds the current base site ID
     * from any URI which define the variables 'CURRENT_CONTEXT_SITE_ID' and 'CONTEXT_SITE_ID' in the URL.
     *
     * Note: The interceptors are service factories that are registered with the $httpProvider by adding them to the $httpProvider.interceptors array.
     * The factory is called and injected with dependencies and returns the interceptor object with contains the interceptor methods.
     */
    .factory('merchandisingExperienceInterceptor', ["hitch", "lodash", "sharedDataService", "interceptorHelper", "CONTEXT_CATALOG", "CONTEXT_CATALOG_VERSION", "MEDIA_PATH", "CONTEXT_SITE_ID", function (hitch, lodash, sharedDataService, interceptorHelper, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, MEDIA_PATH, CONTEXT_SITE_ID) {
    'ngInject';
    var MERCHCMSWEBSERVICES_PATH = /\/merchandisingcmswebservices/;
    /**
     * @ngdoc method
     * @name merchandisingExperienceInterceptorModule.merchandisingExperienceInterceptor#request
     * @methodOf merchandisingExperienceInterceptorModule.merchandisingExperienceInterceptor
     *
     * @description
     * Interceptor method which gets called with a http config object, intercepts any 'merchandisingcmswebservices' requests and
     * adds the current base site ID from any URI which define the variables 'CURRENT_CONTEXT_SITE_ID' in the URL.
     *
     * The base site is stored in the shared data service object called 'experience' during preview initialization
     * and here we retrieve that detail and set it to headers.
     *
     * @param {Object} config the http config object that holds the configuration information.
     *
     * @returns {Promise} Returns a {@link https://docs.angularjs.org/api/ng/service/$q promise} of the passed config object.
     */
    var request = function (config) {
        'ngInject';
        return interceptorHelper.handleRequest(config, function () {
            'ngInject';
            if (MERCHCMSWEBSERVICES_PATH.test(config.url)) {
                return sharedDataService.get('experience').then(function (data) {
                    if (data) {
                        if (config.url.indexOf(CONTEXT_SITE_ID) > -1) {
                            if (config.params) {
                                if (config.params.catalogId) {
                                    delete config.params.catalogId;
                                }
                                if (config.params.catalogVersion) {
                                    delete config.params.catalogVersion;
                                }
                                if (config.params.mask) {
                                    delete config.params.mask;
                                }
                            }
                            // Injecting the current value for the site, when there is a search query.
                            config.url = config.url.replace(CONTEXT_SITE_ID, data.siteDescriptor.uid);
                        }
                    }
                    return config;
                });
            }
            else {
                return config;
            }
        });
    };
    request.$inject = ["config"];
    var interceptor = {};
    interceptor.request = hitch(interceptor, request);
    return interceptor;
}])
    .config(["$httpProvider", function ($httpProvider) {
    'ngInject';
    $httpProvider.interceptors.push('merchandisingExperienceInterceptor');
}]);


/***/ }),

/***/ "angular":
/*!**************************!*\
  !*** external "angular" ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = angular;

/***/ })

/******/ });
//# sourceMappingURL=merchandisingsmarteditContainer.js.map