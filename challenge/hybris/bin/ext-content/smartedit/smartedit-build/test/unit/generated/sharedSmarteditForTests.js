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
/******/ 	return __webpack_require__(__webpack_require__.s = "./smartedit-build/test/unit/sharedSmarteditForTests.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./jsTarget/web/app/common/components/index.ts":
/*!*****************************************************!*\
  !*** ./jsTarget/web/app/common/components/index.ts ***!
  \*****************************************************/
/*! exports provided: YMoreTextComponent, YTreeDndEvent, YInfiniteScrollingComponent, YEventMessageComponent, LanguageSelectorComponent, LanguageDropdownSelectorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _yMessage_yEventMessage_IYEventMessageData__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./yMessage/yEventMessage/IYEventMessageData */ "./jsTarget/web/app/common/components/yMessage/yEventMessage/IYEventMessageData.ts");
/* harmony import */ var _yMessage_yEventMessage_IYEventMessageData__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_yMessage_yEventMessage_IYEventMessageData__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _tree_types__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./tree/types */ "./jsTarget/web/app/common/components/tree/types.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YTreeDndEvent", function() { return _tree_types__WEBPACK_IMPORTED_MODULE_1__["YTreeDndEvent"]; });

/* harmony import */ var _yMoreText_YMoreTextComponent__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./yMoreText/YMoreTextComponent */ "./jsTarget/web/app/common/components/yMoreText/YMoreTextComponent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YMoreTextComponent", function() { return _yMoreText_YMoreTextComponent__WEBPACK_IMPORTED_MODULE_2__["YMoreTextComponent"]; });

/* harmony import */ var _infiniteScrolling_YInfiniteScrollingComponent__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./infiniteScrolling/YInfiniteScrollingComponent */ "./jsTarget/web/app/common/components/infiniteScrolling/YInfiniteScrollingComponent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YInfiniteScrollingComponent", function() { return _infiniteScrolling_YInfiniteScrollingComponent__WEBPACK_IMPORTED_MODULE_3__["YInfiniteScrollingComponent"]; });

/* harmony import */ var _yMessage_yEventMessage_yEventMessage__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./yMessage/yEventMessage/yEventMessage */ "./jsTarget/web/app/common/components/yMessage/yEventMessage/yEventMessage.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YEventMessageComponent", function() { return _yMessage_yEventMessage_yEventMessage__WEBPACK_IMPORTED_MODULE_4__["YEventMessageComponent"]; });

/* harmony import */ var _languageSelector_selector_LanguageSelectorComponent__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./languageSelector/selector/LanguageSelectorComponent */ "./jsTarget/web/app/common/components/languageSelector/selector/LanguageSelectorComponent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageSelectorComponent", function() { return _languageSelector_selector_LanguageSelectorComponent__WEBPACK_IMPORTED_MODULE_5__["LanguageSelectorComponent"]; });

/* harmony import */ var _languageSelector_dropdown_LanguageDropdownSelectorComponent__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./languageSelector/dropdown/LanguageDropdownSelectorComponent */ "./jsTarget/web/app/common/components/languageSelector/dropdown/LanguageDropdownSelectorComponent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageDropdownSelectorComponent", function() { return _languageSelector_dropdown_LanguageDropdownSelectorComponent__WEBPACK_IMPORTED_MODULE_6__["LanguageDropdownSelectorComponent"]; });

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









/***/ }),

/***/ "./jsTarget/web/app/common/components/infiniteScrolling/YInfiniteScrollingComponent.ts":
/*!*********************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/infiniteScrolling/YInfiniteScrollingComponent.ts ***!
  \*********************************************************************************************/
/*! exports provided: YInfiniteScrollingComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YInfiniteScrollingComponent", function() { return YInfiniteScrollingComponent; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc directive
 * @name yInfiniteScrollingModule.directive:yInfiniteScrolling
 * @scope
 * @restrict E
 *
 * @description
 * A component that you can use to implement infinite scrolling for an expanding content (typically with a ng-repeat) nested in it.
 * It is meant to handle paginated requests from a backend when data is expected to be large.
 * Since the expanding content is a <b>transcluded</b> element, we must specify the context to which the items will be attached:
 * If context is myContext, each pagination will push its new items to myContext.items.
 * @param {String} pageSize The maximum size of each page requested from the backend.
 * @param {String} mask A string value sent to the server upon fetching a page to further restrict the search, it is sent as query string "mask".
 * <br>The directive listens for change to mask and will reset the scroll and re-fetch data.
 * <br/>It it left to the implementers to decide what it filters on
 * @param {String} distance A number representing how close the bottom of the element must be to the bottom of the container before the expression specified by fetchPage function is triggered. Measured in multiples of the container height; for example, if the container is 1000 pixels tall and distance is set to 2, the infinite scroll expression will be evaluated when the bottom of the element is within 2000 pixels of the bottom of the container. Defaults to 0 (e.g. the expression will be evaluated when the bottom of the element crosses the bottom of the container).
 * @param {Object} context The container object to which the items of the fetched {@link Page.object:Page Page} will be added
 * @param {Function} fetchPage function to fetch the next page when the bottom of the element approaches the bottom of the container.
 *        fetchPage will be invoked with 3 arguments : <b>mask, pageSize, currentPage</b>. The currentPage is determined by the scrolling and starts with 0. The function must return a page of type {@link Page.object:Page Page}.
 * @param {String} dropDownContainerClass An optional CSS class to be added to the container of the dropdown. It would typically be used to override the default height. <b>The resolved CSS must set a height (or max-height) and overflow-y:scroll.</b>
 * @param {String} dropDownClass An optional CSS class to be added to the dropdown. <b>Neither height nor overflow should be set on the dropdown, it must be free to fill up the space and reach the container size. Failure to do so will cause the directive to call nextPage as many times as the number of available pages on the server.</b>
 */
var YInfiniteScrollingComponent = /** @class */ (function () {
    /** @internal */
    function YInfiniteScrollingComponent($timeout, encode, lodash, discardablePromiseUtils, $element, generateIdentifier, throttle, testModeService) {
        this.$timeout = $timeout;
        this.encode = encode;
        this.lodash = lodash;
        this.discardablePromiseUtils = discardablePromiseUtils;
        this.$element = $element;
        /** @internal */
        this.CONTAINER_CLASS = "ySEInfiniteScrolling-container";
        /** @internal */
        this.initiated = false;
        this.THROTTLE_MILLISECONDS = 250;
        this.containerId = generateIdentifier();
        // needs to be bound for usage by underlying infinite-scroll
        this.nextPage = this.nextPage.bind(this);
        if (!testModeService.isE2EMode()) {
            this.$onChanges = throttle(this.$onChanges.bind(this), this.THROTTLE_MILLISECONDS);
        }
    }
    /** @internal */
    YInfiniteScrollingComponent.prototype.$onChanges = function () {
        this.context = this.context || this;
        this.$postLink();
    };
    /** @internal */
    YInfiniteScrollingComponent.prototype.nextPage = function () {
        var _this = this;
        if (this.pagingDisabled) {
            return;
        }
        this.pagingDisabled = true;
        this.currentPage++;
        this.mask = this.mask || "";
        this.discardablePromiseUtils.apply(this.containerId, this.fetchPage(this.mask, this.pageSize, this.currentPage), function (page) {
            page.results.forEach(function (element) {
                element.technicalUniqueId = _this.encode(element);
            });
            var uniqueResults = _this.lodash.differenceBy(page.results, _this.context.items, "technicalUniqueId");
            if (_this.lodash.size(uniqueResults) > 0) {
                Array.prototype.push.apply(_this.context.items, uniqueResults);
            }
            /*
             * pagingDisabled controls the disablement of the native infinite-scroll directive therefore its
             * re-evaluation must happen on the next digest cycle, after the HTML real estate has been modified
             * by the new data set. Doing it on the same digest cycle would cause the non throttled infinite-scroll directive
             * to fetch more pages than required
             */
            _this.$timeout(function () {
                _this.pagingDisabled = page.results.length === 0 || (page.pagination && _this.context.items.length === page.pagination.totalCount);
            });
        });
    };
    /** @internal */
    YInfiniteScrollingComponent.prototype.$postLink = function () {
        var wasInitiated = this.initiated;
        this.distance = this.distance || 0;
        this.context.items = [];
        this.currentPage = -1;
        this.pagingDisabled = false;
        if (!this.container && this.$element) {
            this.container = this.$element.find("." + this.CONTAINER_CLASS).get(0);
            this.initiated = true;
        }
        else {
            this.container.scrollTop = 0;
        }
        if (wasInitiated) {
            // not needed the first time since data-infinite-scroll-immediate-check="true"
            this.nextPage();
        }
    };
    YInfiniteScrollingComponent = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeComponent"])({
            templateUrl: 'yInfiniteScrollingTemplate.html',
            inputs: [
                'pageSize',
                'mask:?',
                'fetchPage',
                'distance:?',
                'context:?',
                'dropDownContainerClass: @?',
                'dropDownClass: @?'
            ]
        })
        /* @ngInject */
    ], YInfiniteScrollingComponent);
    return YInfiniteScrollingComponent;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/components/languageSelector/LanguageSelectorController.ts":
/*!*******************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/languageSelector/LanguageSelectorController.ts ***!
  \*******************************************************************************************/
/*! exports provided: LanguageSelectorController */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LanguageSelectorController", function() { return LanguageSelectorController; });
/* @internal */
var LanguageSelectorController = /** @class */ (function () {
    function LanguageSelectorController(SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q) {
        this.SWITCH_LANGUAGE_EVENT = SWITCH_LANGUAGE_EVENT;
        this.languageService = languageService;
        this.crossFrameEventService = crossFrameEventService;
        this.$q = $q;
        this.selectedLanguage = null;
        this.languages = [];
    }
    LanguageSelectorController.prototype.$onInit = function () {
        var _this = this;
        this.$q.all([
            this.languageService.getResolveLocale(),
            this.languageService.getToolingLanguages()
        ]).then(function (_a) {
            var isoCode = _a[0], languages = _a[1];
            _this.languages = languages.slice();
            _this.setSelectedLanguage(isoCode);
        });
        this.unRegisterEventService = this.crossFrameEventService.subscribe(this.SWITCH_LANGUAGE_EVENT, this.handleLanguageChange.bind(this));
    };
    LanguageSelectorController.prototype.$onDestroy = function () {
        this.unRegisterEventService();
    };
    /**
     * Triggered when an user selects a language.
     * @param {IToolingLanguage} language
     */
    LanguageSelectorController.prototype.onSelectedLanguage = function (language) {
        this.languageService.setSelectedToolingLanguage(language);
    };
    /**
     * Returns an ordered language array by name and sets the selected language at the beginning.
     *
     * @param {IToolingLanguage} selectedLanguage
     * @param {IToolingLanguage[]} languages
     * @returns {IToolingLanguage[]}
     */
    LanguageSelectorController.prototype.orderLanguagesWithSelectedLanguage = function (selectedLanguage, languages) {
        var orderedLanguages = this.languages.filter(function (language) { return language !== selectedLanguage; }).sort(function (a, b) {
            return a.isoCode.localeCompare(b.isoCode);
        });
        orderedLanguages.unshift(selectedLanguage);
        return orderedLanguages;
    };
    /**
     * Triggered onInit and when language service sets a new language.
     *
     * @param {IToolingLanguage[]} languages
     * @param {string} isoCode
     */
    LanguageSelectorController.prototype.setSelectedLanguage = function (isoCode) {
        var _this = this;
        this.selectedLanguage = this.findLanguageWithIsoCode(isoCode);
        if (this.selectedLanguage) {
            this.languages = this.orderLanguagesWithSelectedLanguage(this.selectedLanguage, this.languages);
            return;
        }
        // In case the iso code is too specific, it will use the more generic iso code to set the language.
        this.languageService.getResolveLocaleIsoCode().then(function (code) {
            _this.selectedLanguage = _this.findLanguageWithIsoCode(code);
            _this.languages = _this.orderLanguagesWithSelectedLanguage(_this.selectedLanguage, _this.languages);
        });
    };
    /**
     * Finds the language with a specified isoCode.
     *
     * @param {string} isoCode
     * @returns {IToolingLanguage}
     */
    LanguageSelectorController.prototype.findLanguageWithIsoCode = function (isoCode) {
        return this.languages.find(function (language) { return language.isoCode === isoCode; });
    };
    /**
     * Callback for setting the selected language.
     */
    LanguageSelectorController.prototype.handleLanguageChange = function () {
        var _this = this;
        this.languageService.getResolveLocale().then(function (isoCode) {
            _this.setSelectedLanguage(isoCode);
        });
    };
    return LanguageSelectorController;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/components/languageSelector/dropdown/LanguageDropdownSelectorComponent.ts":
/*!***********************************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/languageSelector/dropdown/LanguageDropdownSelectorComponent.ts ***!
  \***********************************************************************************************************/
/*! exports provided: LanguageDropdownSelectorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LanguageDropdownSelectorComponent", function() { return LanguageDropdownSelectorComponent; });
/* harmony import */ var _services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../services */ "./jsTarget/web/app/common/services/index.ts");
/* harmony import */ var _LanguageSelectorController__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../LanguageSelectorController */ "./jsTarget/web/app/common/components/languageSelector/LanguageSelectorController.ts");
var __extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc directive
 * @name SmarteditCommonsModule.component:LanguageDropdownSelectorComponent
 * @element language-dropdown-selector
 * @description
 * An icon language dropdown selector which allows the user to select a language.
 *
 * Use the {@link smarteditCommonsModule.service:LanguageService languageService}
 * to call backend API in order to get the list of supported languages
 */


var LanguageDropdownSelectorComponent = /** @class */ (function (_super) {
    __extends(LanguageDropdownSelectorComponent, _super);
    function LanguageDropdownSelectorComponent(SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q) {
        return _super.call(this, SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q) || this;
    }
    LanguageDropdownSelectorComponent.prototype.orderLanguagesWithSelectedLanguage = function (selectedLanguage, languages) {
        return this.languages;
    };
    LanguageDropdownSelectorComponent = __decorate([
        Object(_services__WEBPACK_IMPORTED_MODULE_0__["SeComponent"])({
            templateUrl: 'languageDropdownSelectorTemplate.html'
        })
        /* @ngInject */
    ], LanguageDropdownSelectorComponent);
    return LanguageDropdownSelectorComponent;
}(_LanguageSelectorController__WEBPACK_IMPORTED_MODULE_1__["LanguageSelectorController"]));



/***/ }),

/***/ "./jsTarget/web/app/common/components/languageSelector/selector/LanguageSelectorComponent.ts":
/*!***************************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/languageSelector/selector/LanguageSelectorComponent.ts ***!
  \***************************************************************************************************/
/*! exports provided: LanguageSelectorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LanguageSelectorComponent", function() { return LanguageSelectorComponent; });
/* harmony import */ var _services___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../services/ */ "./jsTarget/web/app/common/services/index.ts");
/* harmony import */ var _LanguageSelectorController__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../LanguageSelectorController */ "./jsTarget/web/app/common/components/languageSelector/LanguageSelectorController.ts");
var __extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc directive
 * @name SmarteditCommonsModule.component:LanguageSelectorComponent
 * @element language-selector
 * @description
 * A language selector which allows the user to select a language while showing the currently displayed language.
 *
 * Use the {@link smarteditCommonsModule.service:LanguageService languageService}
 * to call backend API in order to get the list of supported languages
 */


var LanguageSelectorComponent = /** @class */ (function (_super) {
    __extends(LanguageSelectorComponent, _super);
    function LanguageSelectorComponent(SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q) {
        return _super.call(this, SWITCH_LANGUAGE_EVENT, languageService, crossFrameEventService, $q) || this;
    }
    LanguageSelectorComponent = __decorate([
        Object(_services___WEBPACK_IMPORTED_MODULE_0__["SeComponent"])({
            templateUrl: 'languageSelectorTemplate.html'
        })
        /* @ngInject */
    ], LanguageSelectorComponent);
    return LanguageSelectorComponent;
}(_LanguageSelectorController__WEBPACK_IMPORTED_MODULE_1__["LanguageSelectorController"]));



/***/ }),

/***/ "./jsTarget/web/app/common/components/tree/types.ts":
/*!**********************************************************!*\
  !*** ./jsTarget/web/app/common/components/tree/types.ts ***!
  \**********************************************************/
/*! exports provided: YTreeDndEvent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YTreeDndEvent", function() { return YTreeDndEvent; });
/**
 * @ngdoc object
 * @name treeModule.object:YTreeDndEvent
 * @description
 * A plain JSON object, representing the event triggered when dragging and dropping nodes in the {@link treeModule.directive:ytree ytree} directive.
 *
 * @param {Object} sourceNode is the {@link treeModule.object:Node node} that is being dragged.
 * @param {Object} destinationNodes is the set of the destination's parent's children {@link treeModule.object:Node nodes}.
 * @param {Number} position is the index at which the {@link treeModule.object:Node node} was dropped.
 *
 */
var YTreeDndEvent = /** @class */ (function () {
    function YTreeDndEvent(
    /**
     * @ngdoc property
     * @name sourceNode
     * @propertyOf treeModule.object:YTreeDndEvent
     * @description
     * the {@link treeModule.object:Node node} being dragged
     */
    sourceNode, 
    /**
     * @ngdoc property
     * @name destinationNodes
     * @propertyOf treeModule.object:YTreeDndEvent
     * @description
     * array of siblings {@link treeModule.object:Node nodes} to the location drop location
     */
    destinationNodes, 
    /**
     * @ngdoc property
     * @name position
     * @propertyOf treeModule.object:YTreeDndEvent
     * @description
     * the index at which {@link treeModule.object:Node node} was dropped amongst its siblings
     */
    position, 
    /**
     * @ngdoc property
     * @name sourceParentHandle
     * @propertyOf treeModule.object:YTreeDndEvent
     * @description
     * the  UI handle of the parent node of the source element
     */
    sourceParentHandle, 
    /**
     * @ngdoc property
     * @name targetParentHandle
     * @propertyOf treeModule.object:YTreeDndEvent
     * @description
     * the UI handle of the targeted parent element
     */
    targetParentHandle) {
        this.sourceNode = sourceNode;
        this.destinationNodes = destinationNodes;
        this.position = position;
        this.sourceParentHandle = sourceParentHandle;
        this.targetParentHandle = targetParentHandle;
    }
    return YTreeDndEvent;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/components/yDropdown/yDropDownMenu/IYDropdownMenuItem.ts":
/*!******************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/yDropdown/yDropDownMenu/IYDropdownMenuItem.ts ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/components/yMessage/yEventMessage/IYEventMessageData.ts":
/*!*****************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/yMessage/yEventMessage/IYEventMessageData.ts ***!
  \*****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/components/yMessage/yEventMessage/yEventMessage.ts":
/*!************************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/yMessage/yEventMessage/yEventMessage.ts ***!
  \************************************************************************************/
/*! exports provided: YEventMessageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YEventMessageComponent", function() { return YEventMessageComponent; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_SeComponent__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/SeComponent */ "./jsTarget/web/app/common/services/dependencyInjection/SeComponent.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc directive
 * @name smarteditCommonsModule.directive:YEventMessage
 * @scope
 * @restrict E
 *
 * @description
 * The YEventMessage is a wrapper around YMessage, used to display or hide the message based on events sent through the systemEventService.
 *
 * @param {< string =} type The YMessage type
 * @param {< string =} title The YMessage title
 * @param {< string =} description The YMessage description
 * @param {< string =} showEvent The event id where the YMessage should be shown. You can update the message or title at this time,
 * by passing a {@link smarteditCommonsModule.interface:IYEventMessageData IYEventMessageData} as argument to the event service.
 * @param {< string =} hideEvent The event id where the YMessage should be hidden
 * @param {< string =} showToStart Controls whether the component is shown right away after compiling the dom
 */
var YEventMessageComponent = /** @class */ (function () {
    /** @internal */
    function YEventMessageComponent(systemEventService) {
        this.systemEventService = systemEventService;
        this.type = 'info';
        this.show = false;
    }
    YEventMessageComponent.prototype.$onChanges = function (changesObj) {
        var _this = this;
        if (changesObj.showEvent) {
            this.removeShowEventHandler();
            this.unregisterShowEventHandler = this.systemEventService.subscribe(changesObj.showEvent.currentValue, function (eventId, eventData) { return _this.showEventHandler(eventId, eventData); });
        }
        if (changesObj.hideEvent) {
            this.removeHideEventHandler();
            this.unregisterHideEventHandler = this.systemEventService.subscribe(changesObj.hideEvent.currentValue, function () { return _this.show = false; });
        }
        if (this.recompile) {
            this.recompile();
        }
    };
    YEventMessageComponent.prototype.$onInit = function () {
        this.show = this.showToStart === 'true' || this.showToStart === true;
    };
    YEventMessageComponent.prototype.$onDestroy = function () {
        this.removeShowEventHandler();
        this.removeHideEventHandler();
    };
    YEventMessageComponent.prototype.showDescription = function () {
        return typeof this.description === 'string' && this.description.length > 0;
    };
    YEventMessageComponent.prototype.showTitle = function () {
        return typeof this.title === 'string' && this.title.length > 0;
    };
    YEventMessageComponent.prototype.showEventHandler = function (eventId, eventData) {
        if (eventData.description && eventData.description.length) {
            this.description = eventData.description;
        }
        if (eventData.title && eventData.title.length) {
            this.title = eventData.title;
        }
        this.show = true;
        if (this.recompile) {
            this.recompile();
        }
    };
    YEventMessageComponent.prototype.removeHideEventHandler = function () {
        if (this.unregisterHideEventHandler) {
            this.unregisterHideEventHandler();
        }
    };
    YEventMessageComponent.prototype.removeShowEventHandler = function () {
        if (this.unregisterShowEventHandler) {
            this.unregisterShowEventHandler();
        }
    };
    YEventMessageComponent = __decorate([
        Object(smarteditcommons_services_dependencyInjection_SeComponent__WEBPACK_IMPORTED_MODULE_0__["SeComponent"])({
            template: "\n\t\t<div data-recompile-dom=\"$ctrl.recompile\">\n\t\t\t<y-message data-type=\"$ctrl.type\"\n\t\t\t\tdata-ng-if=\"$ctrl.show\">\n\t\t\t\t<message-title data-ng-if=\"$ctrl.title.length\">\n\t\t\t\t\t{{ $ctrl.title | translate }}\n\t\t\t\t</message-title>\n\t\t\t\t<message-description data-ng-if=\"$ctrl.description.length\">\n\t\t\t\t\t{{ $ctrl.description | translate }}\n\t\t\t\t</message-description>\n\t\t\t</y-message>\n\t\t</div>\n    ",
            inputs: [
                'type: ?',
                'title: ?',
                'description: ?',
                'showEvent: ?',
                'hideEvent: ?',
                'showToStart: ?'
            ]
        })
        /* @ngInject */
    ], YEventMessageComponent);
    return YEventMessageComponent;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/components/yMoreText/YMoreTextComponent.ts":
/*!****************************************************************************!*\
  !*** ./jsTarget/web/app/common/components/yMoreText/YMoreTextComponent.ts ***!
  \****************************************************************************/
/*! exports provided: YMoreTextComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YMoreTextComponent", function() { return YMoreTextComponent; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var smarteditcommons_services_text_textTruncateService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/text/textTruncateService */ "./jsTarget/web/app/common/services/text/textTruncateService.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};


/**
 * @ngdoc directive
 * @name SmarteditCommonsModule.component:yMoreTextComponent
 * @element more-text
 * @description
 * The component for truncating strings and adding an ellipsis.
 * If the limit is less then the string length then the string is truncated and 'more'/'less' buttons
 * are displayed to expand or collapse the string.
 *
 * @param {< String} text the text to be displayed
 * @param {< String =} limit index in text to truncate to. Default value is 100.
 * @param {< String =} moreLabelI18nKey the label property value for a more button. Default value is 'more'.
 * @param {< String =} lessLabelI18nKey the label property value for a less button. Default value is 'less'.
 * @param {< String =} ellipsis the ellipsis for a truncated text. Default value is an empty string.
 */
var YMoreTextComponent = /** @class */ (function () {
    function YMoreTextComponent(textTruncateService, $translate, $q) {
        this.textTruncateService = textTruncateService;
        this.$translate = $translate;
        this.$q = $q;
        this.isTruncated = false;
        this.showingMore = false;
    }
    YMoreTextComponent.prototype.$onInit = function () {
        var _this = this;
        this.limit = this.limit || 100;
        this.moreLabelI18nKey = this.moreLabelI18nKey || 'se.moretext.more.link';
        this.lessLabelI18nKey = this.lessLabelI18nKey || 'se.moretext.less.link';
        this.truncatedText = this.textTruncateService.truncateToNearestWord(this.limit, this.text, this.ellipsis);
        this.isTruncated = this.truncatedText.isTruncated();
        this.translateLabels().then(function () {
            _this.showHideMoreText();
        });
    };
    YMoreTextComponent.prototype.showHideMoreText = function () {
        if (this.isTruncated) {
            this.text = this.showingMore ? this.truncatedText.getUntruncatedText() : this.truncatedText.getTruncatedText();
            this.linkLabel = this.showingMore ? this.lessLabel : this.moreLabel;
            this.showingMore = !this.showingMore;
        }
    };
    YMoreTextComponent.prototype.translateLabels = function () {
        var _this = this;
        var promisesToResolve = [];
        var moreLink = this.$translate(this.moreLabelI18nKey).then(function (label) {
            _this.moreLabel = _this.moreLabel || label;
        });
        var lessLink = this.$translate(this.lessLabelI18nKey).then(function (label) {
            _this.lessLabel = _this.lessLabel || label;
        });
        promisesToResolve.push(moreLink, lessLink);
        return this.$q.all(promisesToResolve);
    };
    YMoreTextComponent = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeComponent"])({
            templateUrl: 'moreTextTemplate.html',
            inputs: ['text', 'limit:?', 'moreLabelI18nKey:?', 'lessLabelI18nKey:?', 'ellipsis:?'],
            providers: [smarteditcommons_services_text_textTruncateService__WEBPACK_IMPORTED_MODULE_1__["TextTruncateService"]]
        })
        /* @ngInject */
    ], YMoreTextComponent);
    return YMoreTextComponent;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/directives/CompileHtmlDirective.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/directives/CompileHtmlDirective.ts ***!
  \********************************************************************/
/*! exports provided: CompileHtmlDirective */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CompileHtmlDirective", function() { return CompileHtmlDirective; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_SeDirective__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/SeDirective */ "./jsTarget/web/app/common/services/dependencyInjection/SeDirective.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

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
 * @ngdoc directive
 * @name smarteditCommonsModule.directive:compileHtml
 * @scope
 * @restrict A
 * @attribute compile-html
 *
 * @description
 * Directive responsible for evaluating and compiling HTML markup.
 *
 * @param {String} String HTML string to be evaluated and compiled in the parent scope.
 * @example
 * <pre>
 *      <div compile-html="<a data-ng-click=\"injectedContext.onLink( item.path )\">{{ item[key.property] }}</a>"></div>
 * </pre>
 */
var CompileHtmlDirective = /** @class */ (function () {
    function CompileHtmlDirective($compile, $scope, $element, $attrs) {
        this.$compile = $compile;
        this.$scope = $scope;
        this.$element = $element;
        this.$attrs = $attrs;
    }
    CompileHtmlDirective.prototype.$postLink = function () {
        var _this = this;
        this.$scope.$parent.$watch(function (scope) { return scope.$eval(_this.$attrs.compileHtml); }, function (value) {
            _this.$element.html(value);
            _this.$compile(_this.$element.contents())(_this.$scope.$parent);
        });
    };
    CompileHtmlDirective = __decorate([
        Object(smarteditcommons_services_dependencyInjection_SeDirective__WEBPACK_IMPORTED_MODULE_0__["SeDirective"])({
            selector: '[compile-html]'
        })
        /* @ngInject */
    ], CompileHtmlDirective);
    return CompileHtmlDirective;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/directives/index.ts":
/*!*****************************************************!*\
  !*** ./jsTarget/web/app/common/directives/index.ts ***!
  \*****************************************************/
/*! exports provided: CompileHtmlDirective */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _CompileHtmlDirective__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./CompileHtmlDirective */ "./jsTarget/web/app/common/directives/CompileHtmlDirective.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CompileHtmlDirective", function() { return _CompileHtmlDirective__WEBPACK_IMPORTED_MODULE_0__["CompileHtmlDirective"]; });

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



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Cloneable.ts":
/*!***************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Cloneable.ts ***!
  \***************************************************/
/*! exports provided: CloneableUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CloneableUtils", function() { return CloneableUtils; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name functionsModule.service:CloneableUtils
 *
 * @description
 * utility service around Cloneable objects
 */
var CloneableUtils = /** @class */ (function () {
    function CloneableUtils(lodash) {
        this.lodash = lodash;
    }
    /**
     * @ngdoc method
     * @name functionsModule.service:CloneableUtils#makeCloneable
     * @methodOf functionsModule.service:CloneableUtils
     * @description
     * returns a "cloneable" version of an object.
     * Something is cloneable when it can be sent through W3C postMessage.
     * To this purpose, functions must be removed from the cloneable candidate.
     * @param {Object} json the object to be made cloneable
     * @returns {Cloneable} the cloneable copy of the object
     */
    CloneableUtils.prototype.makeCloneable = function (_json) {
        var _this = this;
        var json = this.lodash.cloneDeepWith(_json, function (value) {
            if (value !== undefined && value !== null && !_this.isPrimitive(json)) {
                // is a promise
                if (value.then) {
                    return null;
                }
                else if (typeof value === 'function') {
                    return null;
                }
                else if (_this.lodash.isElement(value)) {
                    return null;
                    // is yjQuery
                }
                else if (typeof value !== 'string' && value.hasOwnProperty('length') && !value.forEach) {
                    return null;
                }
                else {
                    return value;
                }
            }
            else {
                return value;
            }
        });
        if (json === undefined || json === null || this.isPrimitive(json)) {
            return json;
        }
        else if (json.hasOwnProperty('length') || json.forEach) { // Array, already taken care of yjQuery
            return json.map(function (arrayElement) { return _this.makeCloneable(arrayElement); });
        }
        else { // JSON
            return Object.keys(json).reduce(function (clone, directKey) {
                if (directKey.indexOf("$") !== 0) {
                    clone[directKey] = _this.makeCloneable(json[directKey]);
                }
                return clone;
            }, {});
        }
    };
    CloneableUtils.prototype.isPrimitive = function (value) {
        return typeof value === 'number' || typeof value === 'string' || typeof value === 'boolean';
    };
    CloneableUtils = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], CloneableUtils);
    return CloneableUtils;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/ICatalog.ts":
/*!**************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/ICatalog.ts ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/IHomepage.ts":
/*!***************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/IHomepage.ts ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/IPermissionsDto.ts":
/*!*********************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/IPermissionsDto.ts ***!
  \*********************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Page.ts":
/*!**********************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Page.ts ***!
  \**********************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Pageable.ts":
/*!**************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Pageable.ts ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Pagination.ts":
/*!****************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Pagination.ts ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Payload.ts":
/*!*************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Payload.ts ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/Primitive.ts":
/*!***************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/Primitive.ts ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/TruncatedText.ts":
/*!*******************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/TruncatedText.ts ***!
  \*******************************************************/
/*! exports provided: TruncatedText */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TruncatedText", function() { return TruncatedText; });
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
 * @internal
 *
 * @name TruncatedText
 *
 * @description
 * Model containing truncated text properties.
 */
var TruncatedText = /** @class */ (function () {
    function TruncatedText(text, truncatedText, truncated, ellipsis) {
        if (text === void 0) { text = ""; }
        if (truncatedText === void 0) { truncatedText = ""; }
        if (ellipsis === void 0) { ellipsis = ""; }
        this.text = text;
        this.truncatedText = truncatedText;
        this.truncated = truncated;
        this.ellipsis = ellipsis;
        // if text/truncatedText is null, then set its value to ""
        this.text = this.text || "";
        this.truncatedText = this.truncatedText || "";
    }
    TruncatedText.prototype.getUntruncatedText = function () {
        return this.text;
    };
    TruncatedText.prototype.getTruncatedText = function () {
        return this.truncatedText + this.ellipsis;
    };
    TruncatedText.prototype.isTruncated = function () {
        return this.truncated;
    };
    return TruncatedText;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/TypedMap.ts":
/*!**************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/TypedMap.ts ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/dtos/ValidationError.ts":
/*!*********************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/ValidationError.ts ***!
  \*********************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/dtos/forcedImport.ts":
/*!******************************************************!*\
  !*** ./jsTarget/web/app/common/dtos/forcedImport.ts ***!
  \******************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _Cloneable__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Cloneable */ "./jsTarget/web/app/common/dtos/Cloneable.ts");
/* harmony import */ var _ICatalog__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./ICatalog */ "./jsTarget/web/app/common/dtos/ICatalog.ts");
/* harmony import */ var _ICatalog__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(_ICatalog__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _IHomepage__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./IHomepage */ "./jsTarget/web/app/common/dtos/IHomepage.ts");
/* harmony import */ var _IHomepage__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(_IHomepage__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var _IPermissionsDto__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./IPermissionsDto */ "./jsTarget/web/app/common/dtos/IPermissionsDto.ts");
/* harmony import */ var _IPermissionsDto__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(_IPermissionsDto__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _Page__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Page */ "./jsTarget/web/app/common/dtos/Page.ts");
/* harmony import */ var _Page__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(_Page__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _Pageable__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./Pageable */ "./jsTarget/web/app/common/dtos/Pageable.ts");
/* harmony import */ var _Pageable__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(_Pageable__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var _Pagination__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./Pagination */ "./jsTarget/web/app/common/dtos/Pagination.ts");
/* harmony import */ var _Pagination__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(_Pagination__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _Payload__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./Payload */ "./jsTarget/web/app/common/dtos/Payload.ts");
/* harmony import */ var _Payload__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(_Payload__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var _Primitive__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./Primitive */ "./jsTarget/web/app/common/dtos/Primitive.ts");
/* harmony import */ var _Primitive__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(_Primitive__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var _TypedMap__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./TypedMap */ "./jsTarget/web/app/common/dtos/TypedMap.ts");
/* harmony import */ var _TypedMap__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(_TypedMap__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var _ValidationError__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./ValidationError */ "./jsTarget/web/app/common/dtos/ValidationError.ts");
/* harmony import */ var _ValidationError__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(_ValidationError__WEBPACK_IMPORTED_MODULE_10__);
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
 * We are doing forced imports in order to generate the types (d.ts) of below interfaces or classes correctly.
 * If we don't include the below imports, as a part of webpack tree shaking, the types will not be generated.
 * There is an open issue in typescript github regarding forced imports
 * https://github.com/Microsoft/TypeScript/issues/9191
 * https://github.com/Microsoft/TypeScript/wiki/FAQ#why-are-imports-being-elided-in-my-emit
 *
 * If an interface X extends an interface Y, make sure X has all types it needs from Y by checking index.d.ts, if not, do force import of X and Y.
 */













/***/ }),

/***/ "./jsTarget/web/app/common/dtos/index.ts":
/*!***********************************************!*\
  !*** ./jsTarget/web/app/common/dtos/index.ts ***!
  \***********************************************/
/*! exports provided: CloneableUtils, TruncatedText */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _forcedImport__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./forcedImport */ "./jsTarget/web/app/common/dtos/forcedImport.ts");
/* harmony import */ var _Cloneable__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Cloneable */ "./jsTarget/web/app/common/dtos/Cloneable.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CloneableUtils", function() { return _Cloneable__WEBPACK_IMPORTED_MODULE_1__["CloneableUtils"]; });

/* harmony import */ var _TruncatedText__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./TruncatedText */ "./jsTarget/web/app/common/dtos/TruncatedText.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TruncatedText", function() { return _TruncatedText__WEBPACK_IMPORTED_MODULE_2__["TruncatedText"]; });

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





/***/ }),

/***/ "./jsTarget/web/app/common/index.ts":
/*!******************************************!*\
  !*** ./jsTarget/web/app/common/index.ts ***!
  \******************************************/
/*! exports provided: CryptographicUtils, FunctionsUtils, StringUtils, WindowUtils, FunctionsModule, CloneableUtils, TruncatedText, AnnotationService, annotationService, AuthorizationService, CrossFrameEventService, CrossFrameEventServiceGateway, GatewayProxied, GatewayProxiedAnnotationFactory, instrument, LanguageService, LanguageServiceGateway, IPerspectiveService, OperationContextService, OperationContextAnnotationFactory, OperationContextRegistered, PolyfillService, PriorityService, SmarteditBootstrapGateway, SystemEventService, TestModeService, SmarteditCommonsModule, YMoreTextComponent, UrlUtils, SeInjectable, SeComponent, parseDirectiveBindings, parseDirectiveName, SeDirective, SeModule, DINameUtils, diNameUtils, CacheConfig, CacheConfigAnnotationFactory, Cached, CachedAnnotationFactory, InvalidateCache, InvalidateCacheAnnotationFactory, SeAlertServiceType, IExperienceService, IFeatureService, INotificationMouseLeaveDetectionService, INotificationService, IPageInfoService, IPreviewService, ISessionService, ISharedDataService, IStorageService, IUrlService, IWaitDialogService, YTreeDndEvent, YInfiniteScrollingComponent, YEventMessageComponent, LanguageSelectorComponent, LanguageDropdownSelectorComponent, CompileHtmlDirective, TranslationServiceModule, CacheAction, CacheService, EvictionTag, GatewayFactory, GatewayProxy, MessageGateway, ICatalogService, IPermissionService, AbstractCachedRestService, ContentCatalogRestService, ProductCatalogRestService, PermissionsRestService, IDragEventType, InViewElementObserver, NamespacedStorageManager, StorageManagerFactory, StorageNamespaceConverter, FrequentlyChangingContentName, frequentlyChangingContent, RarelyChangingContentName, rarelyChangingContent, CacheEngine, DefaultCacheTiming, authorizationEvictionTag, catalogSyncedEvictionTag, catalogEvictionTag, pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag, pageRestoredEvictionTag, pageChangeEvictionTag, pageEvictionTag, userEvictionTag, contentCatalogUpdateEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _dtos__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dtos */ "./jsTarget/web/app/common/dtos/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CloneableUtils", function() { return _dtos__WEBPACK_IMPORTED_MODULE_0__["CloneableUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TruncatedText", function() { return _dtos__WEBPACK_IMPORTED_MODULE_0__["TruncatedText"]; });

/* harmony import */ var _services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./services */ "./jsTarget/web/app/common/services/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnnotationService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["AnnotationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "annotationService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["annotationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AuthorizationService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["AuthorizationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CrossFrameEventService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventServiceGateway", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CrossFrameEventServiceGateway"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxied", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["GatewayProxied"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxiedAnnotationFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["GatewayProxiedAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "instrument", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["instrument"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["LanguageService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageServiceGateway", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["LanguageServiceGateway"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPerspectiveService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IPerspectiveService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["OperationContextService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextAnnotationFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["OperationContextAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextRegistered", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["OperationContextRegistered"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PolyfillService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["PolyfillService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PriorityService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["PriorityService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SmarteditBootstrapGateway", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SmarteditBootstrapGateway"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SystemEventService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SystemEventService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TestModeService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["TestModeService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SmarteditCommonsModule", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SmarteditCommonsModule"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeInjectable", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeComponent", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SeComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveBindings", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["parseDirectiveBindings"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveName", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["parseDirectiveName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeDirective", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SeDirective"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeModule", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SeModule"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DINameUtils", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["DINameUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "diNameUtils", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["diNameUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfig", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CacheConfig"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfigAnnotationFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CacheConfigAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "Cached", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["Cached"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CachedAnnotationFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CachedAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCache", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["InvalidateCache"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCacheAnnotationFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["InvalidateCacheAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeAlertServiceType", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["SeAlertServiceType"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IExperienceService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IExperienceService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IFeatureService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IFeatureService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationMouseLeaveDetectionService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["INotificationMouseLeaveDetectionService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["INotificationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPageInfoService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IPageInfoService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPreviewService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IPreviewService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISessionService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["ISessionService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISharedDataService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["ISharedDataService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IStorageService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IStorageService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IUrlService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IUrlService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IWaitDialogService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IWaitDialogService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheAction", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CacheAction"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CacheService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "EvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["EvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["GatewayFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxy", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["GatewayProxy"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "MessageGateway", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["MessageGateway"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ICatalogService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["ICatalogService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPermissionService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IPermissionService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AbstractCachedRestService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["AbstractCachedRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ContentCatalogRestService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["ContentCatalogRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ProductCatalogRestService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["ProductCatalogRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PermissionsRestService", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["PermissionsRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IDragEventType", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["IDragEventType"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InViewElementObserver", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["InViewElementObserver"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "NamespacedStorageManager", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["NamespacedStorageManager"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageManagerFactory", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["StorageManagerFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageNamespaceConverter", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["StorageNamespaceConverter"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FrequentlyChangingContentName", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["FrequentlyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "frequentlyChangingContent", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["frequentlyChangingContent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RarelyChangingContentName", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["RarelyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "rarelyChangingContent", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["rarelyChangingContent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheEngine", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["CacheEngine"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultCacheTiming", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["DefaultCacheTiming"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "authorizationEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["authorizationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogSyncedEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["catalogSyncedEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["catalogEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageCreationEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageCreationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageDeletionEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageDeletionEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageUpdateEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageUpdateEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageRestoredEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageRestoredEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageChangeEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageChangeEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["pageEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "userEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["userEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "contentCatalogUpdateEvictionTag", function() { return _services__WEBPACK_IMPORTED_MODULE_1__["contentCatalogUpdateEvictionTag"]; });

/* harmony import */ var _components__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./components */ "./jsTarget/web/app/common/components/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YMoreTextComponent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["YMoreTextComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YTreeDndEvent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["YTreeDndEvent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YInfiniteScrollingComponent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["YInfiniteScrollingComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "YEventMessageComponent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["YEventMessageComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageSelectorComponent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["LanguageSelectorComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageDropdownSelectorComponent", function() { return _components__WEBPACK_IMPORTED_MODULE_2__["LanguageDropdownSelectorComponent"]; });

/* harmony import */ var _directives__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./directives */ "./jsTarget/web/app/common/directives/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CompileHtmlDirective", function() { return _directives__WEBPACK_IMPORTED_MODULE_3__["CompileHtmlDirective"]; });

/* harmony import */ var _modules__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./modules */ "./jsTarget/web/app/common/modules/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TranslationServiceModule", function() { return _modules__WEBPACK_IMPORTED_MODULE_4__["TranslationServiceModule"]; });

/* harmony import */ var _utils__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./utils */ "./jsTarget/web/app/common/utils/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CryptographicUtils", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["CryptographicUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FunctionsUtils", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["FunctionsUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StringUtils", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["StringUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "WindowUtils", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["WindowUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FunctionsModule", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["FunctionsModule"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UrlUtils", function() { return _utils__WEBPACK_IMPORTED_MODULE_5__["UrlUtils"]; });

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
// barrel








/***/ }),

/***/ "./jsTarget/web/app/common/modules/index.ts":
/*!**************************************************!*\
  !*** ./jsTarget/web/app/common/modules/index.ts ***!
  \**************************************************/
/*! exports provided: TranslationServiceModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _translations_translationServiceModule__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./translations/translationServiceModule */ "./jsTarget/web/app/common/modules/translations/translationServiceModule.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TranslationServiceModule", function() { return _translations_translationServiceModule__WEBPACK_IMPORTED_MODULE_0__["TranslationServiceModule"]; });

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



/***/ }),

/***/ "./jsTarget/web/app/common/modules/translations/TranslationFile.ts":
/*!*************************************************************************!*\
  !*** ./jsTarget/web/app/common/modules/translations/TranslationFile.ts ***!
  \*************************************************************************/
/*! exports provided: TranslationFile */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TranslationFile", function() { return TranslationFile; });
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
/** @internal */
var TranslationFile = /** @class */ (function () {
    function TranslationFile(prefix, suffix, key) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.key = key;
    }
    return TranslationFile;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/modules/translations/translateStaticFilesLoader.ts":
/*!************************************************************************************!*\
  !*** ./jsTarget/web/app/common/modules/translations/translateStaticFilesLoader.ts ***!
  \************************************************************************************/
/*! exports provided: $translateStaticFilesLoader */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "$translateStaticFilesLoader", function() { return $translateStaticFilesLoader; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _TranslationFile__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./TranslationFile */ "./jsTarget/web/app/common/modules/translations/TranslationFile.ts");
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


/** @internal */
function $translateStaticFilesLoader($q, lodash, restServiceFactory) {
    'ngInject';
    return initializer.bind(undefined, $q, lodash, restServiceFactory);
}
/*
 * this custom implementations of $translateStaticFilesLoader needed by 'pascalprecht.translate' package leverages
 * our restServiceFactory as opposed to $http in order to proxy the i18n loading to the container.
 * This is required for our cross-origin compliancy
 */
function initializer($q, lodash, restServiceFactory, options) {
    if (!options || (!angular__WEBPACK_IMPORTED_MODULE_0__["isArray"](options.files) && (!angular__WEBPACK_IMPORTED_MODULE_0__["isString"](options.prefix) || !angular__WEBPACK_IMPORTED_MODULE_0__["isString"](options.suffix)))) {
        throw new Error("Couldn't load translation static files, no files and prefix or suffix specified!");
    }
    options.files = options.files || [new _TranslationFile__WEBPACK_IMPORTED_MODULE_1__["TranslationFile"](options.prefix, options.suffix)];
    var load = function (file) {
        if (!file || (!angular__WEBPACK_IMPORTED_MODULE_0__["isString"](file.prefix) || !angular__WEBPACK_IMPORTED_MODULE_0__["isString"](file.suffix))) {
            throw new Error("Couldn't load translation static files, no files and prefix or suffix specified!");
        }
        var fileUrl = [
            file.prefix,
            options.key,
            file.suffix
        ].join('');
        if (angular__WEBPACK_IMPORTED_MODULE_0__["isObject"](options.fileMap) && options.fileMap[fileUrl]) {
            fileUrl = options.fileMap[fileUrl];
        }
        return restServiceFactory.get(fileUrl).get(options.$http);
    };
    var promises = options.files.map(function (file) {
        return load(new _TranslationFile__WEBPACK_IMPORTED_MODULE_1__["TranslationFile"](file.prefix, file.suffix, options.key));
    });
    return $q.all(promises).then(function (data) {
        var mergedData = {};
        data.forEach(function (datum) {
            delete datum.$resolved;
            delete datum.$promise;
            lodash.merge(mergedData, datum);
        });
        return mergedData;
    });
}


/***/ }),

/***/ "./jsTarget/web/app/common/modules/translations/translationServiceModule.ts":
/*!**********************************************************************************!*\
  !*** ./jsTarget/web/app/common/modules/translations/translationServiceModule.ts ***!
  \**********************************************************************************/
/*! exports provided: TranslationServiceModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TranslationServiceModule", function() { return TranslationServiceModule; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var _translateStaticFilesLoader__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translateStaticFilesLoader */ "./jsTarget/web/app/common/modules/translations/translateStaticFilesLoader.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name translationServiceModule
 *
 * @description
 *
 * This module is used to configure the translate service, the filter, and the directives from the 'pascalprecht.translate' package. The configuration consists of:
 *
 * <br/>- Initializing the translation map from the {@link i18nInterceptorModule.object:I18NAPIROOT I18NAPIROOT} constant.
 * <br/>- Setting the preferredLanguage to the {@link i18nInterceptorModule.object:UNDEFINED_LOCALE UNDEFINED_LOCALE} so that the {@link i18nInterceptorModule.service:i18nInterceptor#methods_request i18nInterceptor request} can replace it with the appropriate URI combined with the runtime browser locale retrieved from browserService.getBrowserLocale, which is unaccessible at configuration time.
 */
var TranslationServiceModule = /** @class */ (function () {
    function TranslationServiceModule() {
    }
    TranslationServiceModule = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeModule"])({
            imports: ['pascalprecht.translate', 'i18nInterceptorModule', 'smarteditCommonsModule'],
            providers: [_translateStaticFilesLoader__WEBPACK_IMPORTED_MODULE_1__["$translateStaticFilesLoader"]],
            config: function ($translateProvider, I18NAPIROOT, UNDEFINED_LOCALE) {
                'ngInject';
                /*
                 * hard coded url that is always intercepted by i18nInterceptor so as to replace by value from configuration REST call
                 */
                $translateProvider.useStaticFilesLoader({
                    prefix: '/' + I18NAPIROOT + '/',
                    suffix: ''
                });
                // Tell the module what language to use by default
                $translateProvider.preferredLanguage(UNDEFINED_LOCALE);
                // Using 'escapeParameters' strategy. 'sanitize' not supported in current version.
                // see https://angular-translate.github.io/docs/#/guide/19_security
                // Note that this is the only option that should be used for now.
                // The options 'sanitizeParameters' and 'escape' are causing issues (& replaced by &amp; and interpolation parameters values are not displayed correctly).
                $translateProvider.useSanitizeValueStrategy('escapeParameters');
            },
            initialize: function (operationContextService, I18N_RESOURCE_URI, OPERATION_CONTEXT) {
                'ngInject';
                operationContextService.register(I18N_RESOURCE_URI, OPERATION_CONTEXT.TOOLING);
            }
        })
    ], TranslationServiceModule);
    return TranslationServiceModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/ConfigModule.ts":
/*!**********************************************************!*\
  !*** ./jsTarget/web/app/common/services/ConfigModule.ts ***!
  \**********************************************************/
/*! exports provided: ConfigModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ConfigModule", function() { return ConfigModule; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/** @internal */
function $exceptionHandler($log) {
    'ngInject';
    var ignorePatterns = [
        /^Possibly unhandled rejection/
    ];
    var patternsForE2EErrorLogs = [
        /Unexpected request/,
        /No more request expected/ // missing http mock patterns in e2e
    ];
    return function exceptionHandler(exception, cause) {
        /*
         * original exception occuring in a promise based API won't show here
         * the catch set in decoration is necessary to log them
         */
        if (ignorePatterns.some(function (pattern) { return pattern.test(exception); })) {
            return;
        }
        if (patternsForE2EErrorLogs.some(function (pattern) { return pattern.test(exception); })) {
            $log.error("E2E mock issue: " + exception);
            return;
        }
        $log.error(exception);
    };
}
function isAjaxError(error) {
    return error.hasOwnProperty("headers");
}
/*
 * Helper function used on all known promise based Angular 1.6 APIs
 * to handle promise rejection in an AOP fashion through Angular decorators
 */
function handlePromiseRejections($q, $log, lodash, promise) {
    var defaultFailureCallback = function (error) {
        if (undefined !== error && "canceled" !== error) {
            if (lodash.isPlainObject(error)) {
                if (!isAjaxError(error)) {
                    $log.error("exception caught in promise: " + JSON.stringify(error));
                }
            }
            else if (!lodash.isBoolean(error)) {
                $log.error(error);
            }
        }
        return $q.reject(error);
    };
    var oldThen = promise.then;
    promise.then = function (successCallback, _failureCallback, notifyCallback) {
        var failureCallback = _failureCallback ? _failureCallback : defaultFailureCallback;
        return oldThen.call(this, successCallback, failureCallback, notifyCallback);
    };
    return promise;
}
/** @internal */
var ConfigModule = /** @class */ (function () {
    function ConfigModule() {
    }
    ConfigModule = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeModule"])({
            providers: [
                $exceptionHandler
            ],
            /*
             * Decoration all known promise based Angular 1.6 APIs
             * to handle promise rejection in an AOP fashion
             */
            config: function ($qProvider, $provide) {
                'ngInject';
                $qProvider.errorOnUnhandledRejections(true);
                $provide.decorator('$q', function ($delegate, $log, lodash) {
                    'ngInject';
                    var originalWhen = $delegate.when;
                    $delegate.when = function () {
                        if (arguments[0] && !arguments[0].then) {
                            return handlePromiseRejections($delegate, $log, lodash, originalWhen.apply(this, arguments));
                        }
                        else {
                            return originalWhen.apply(this, arguments);
                        }
                    };
                    var originalAll = $delegate.all;
                    $delegate.all = function () {
                        return handlePromiseRejections($delegate, $log, lodash, originalAll.apply($delegate, arguments));
                    };
                    var originalDefer = $delegate.defer;
                    $delegate.defer = function () {
                        var deferred = originalDefer.bind($delegate)();
                        handlePromiseRejections($delegate, $log, lodash, deferred.promise);
                        return deferred;
                    };
                    return $delegate;
                });
                $provide.decorator('$timeout', function ($delegate, $q, $log, lodash) {
                    'ngInject';
                    var originalTimeout = $delegate;
                    function wrappedTimeout() {
                        return handlePromiseRejections($q, $log, lodash, originalTimeout.apply($delegate, arguments));
                    }
                    lodash.merge(wrappedTimeout, originalTimeout);
                    return wrappedTimeout;
                });
            }
        })
    ], ConfigModule);
    return ConfigModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/PolyfillService.ts":
/*!*************************************************************!*\
  !*** ./jsTarget/web/app/common/services/PolyfillService.ts ***!
  \*************************************************************/
/*! exports provided: PolyfillService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PolyfillService", function() { return PolyfillService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/* @internal */
var PolyfillService = /** @class */ (function () {
    function PolyfillService(browserService, testModeService) {
        this.browserService = browserService;
        this.testModeService = testModeService;
    }
    PolyfillService.prototype.isEligibleForEconomyMode = function () {
        return this.browserService.isIE() || this.testModeService.isE2EMode();
    };
    PolyfillService.prototype.isEligibleForExtendedView = function () {
        return (this.browserService.isIE() || this.browserService.isFF()) || this.testModeService.isE2EMode();
    };
    PolyfillService.prototype.isEligibleForThrottledScrolling = function () {
        return this.browserService.isIE();
    };
    PolyfillService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], PolyfillService);
    return PolyfillService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/PriorityService.ts":
/*!*************************************************************!*\
  !*** ./jsTarget/web/app/common/services/PriorityService.ts ***!
  \*************************************************************/
/*! exports provided: PriorityService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PriorityService", function() { return PriorityService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:PriorityService
 * @description
 * The PriorityService handles arrays of {@link smarteditServicesModule.interface:IPrioritized IPrioritized} elements
 */
var PriorityService = /** @class */ (function () {
    function PriorityService(encode) {
        this.encode = encode;
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:PriorityService#sort<T>
     * @methodOf smarteditServicesModule.service:PriorityService
     *
     * @description
     * Will sort the candidate array by ascendign or descending priority.
     * Even if the priority is not defined for a number of elements, the sorting will still be consistent over invocations
     * @param {T[]} candidate the array of @link smarteditServicesModule.interface:IPrioritized IPrioritized} elements to be sorted
     * @param {boolean=} [ascending=true] if true, candidate will be sorted by ascending priority.
     * @returns {T[]} A promise resolving to the username,
     * previously mentioned as "principalUID", associated to the
     * authenticated user.
     */
    PriorityService.prototype.sort = function (candidate, ascending) {
        var _this = this;
        if (ascending === void 0) { ascending = true; }
        return candidate.sort(function (item1, item2) {
            var output = item1.priority - item2.priority;
            if (output === 0) {
                output = _this.encode(item1).localCompare(_this.encode(item2));
            }
            return output;
        });
    };
    PriorityService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], PriorityService);
    return PriorityService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/SmarteditBootstrapGateway.ts":
/*!***********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/SmarteditBootstrapGateway.ts ***!
  \***********************************************************************/
/*! exports provided: SmarteditBootstrapGateway */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SmarteditBootstrapGateway", function() { return SmarteditBootstrapGateway; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var SmarteditBootstrapGateway = /** @class */ (function () {
    function SmarteditBootstrapGateway(gatewayFactory) {
        return gatewayFactory.createGateway('smartEditBootstrap');
    }
    SmarteditBootstrapGateway = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], SmarteditBootstrapGateway);
    return SmarteditBootstrapGateway;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/SmarteditCommonsModule.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/SmarteditCommonsModule.ts ***!
  \********************************************************************/
/*! exports provided: SmarteditCommonsModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SmarteditCommonsModule", function() { return SmarteditCommonsModule; });
/* harmony import */ var _deprecate__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./deprecate */ "./jsTarget/web/app/common/services/deprecate.ts");
/* harmony import */ var smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/components */ "./jsTarget/web/app/common/components/index.ts");
/* harmony import */ var smarteditcommons_directives__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/directives */ "./jsTarget/web/app/common/directives/index.ts");
/* harmony import */ var _crossFrame_CrossFrameEventService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./crossFrame/CrossFrameEventService */ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventService.ts");
/* harmony import */ var _crossFrame_CrossFrameEventServiceGateway__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./crossFrame/CrossFrameEventServiceGateway */ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventServiceGateway.ts");
/* harmony import */ var _SystemEventService__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./SystemEventService */ "./jsTarget/web/app/common/services/SystemEventService.ts");
/* harmony import */ var _utils_functionsModule__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../utils/functionsModule */ "./jsTarget/web/app/common/utils/functionsModule.ts");
/* harmony import */ var _language_LanguageService__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./language/LanguageService */ "./jsTarget/web/app/common/services/language/LanguageService.ts");
/* harmony import */ var _language_LanguageServiceGateway__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./language/LanguageServiceGateway */ "./jsTarget/web/app/common/services/language/LanguageServiceGateway.ts");
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var _SmarteditRootModule__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./SmarteditRootModule */ "./jsTarget/web/app/common/services/SmarteditRootModule.ts");
/* harmony import */ var _modules_translations_translationServiceModule__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../modules/translations/translationServiceModule */ "./jsTarget/web/app/common/modules/translations/translationServiceModule.ts");
/* harmony import */ var _flaws_flawInjectionInterceptorModule__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./flaws/flawInjectionInterceptorModule */ "./jsTarget/web/app/common/services/flaws/flawInjectionInterceptorModule.ts");
/* harmony import */ var _ConfigModule__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./ConfigModule */ "./jsTarget/web/app/common/services/ConfigModule.ts");
/* harmony import */ var smarteditcommons_services_auth_AuthorizationService__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! smarteditcommons/services/auth/AuthorizationService */ "./jsTarget/web/app/common/services/auth/AuthorizationService.ts");
/* harmony import */ var smarteditcommons_services_rest_CommonsRestServiceModule__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! smarteditcommons/services/rest/CommonsRestServiceModule */ "./jsTarget/web/app/common/services/rest/CommonsRestServiceModule.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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

Object(_deprecate__WEBPACK_IMPORTED_MODULE_0__["deprecate"])();















/**
 * @ngdoc overview
 * @name smarteditCommonsModule
 *
 * @description
 * Module containing all the services shared within the smartedit commons.
 */
var SmarteditCommonsModule = /** @class */ (function () {
    function SmarteditCommonsModule() {
    }
    SmarteditCommonsModule = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_9__["SeModule"])({
            imports: [
                _SmarteditRootModule__WEBPACK_IMPORTED_MODULE_10__["SmarteditRootModule"],
                smarteditcommons_services_rest_CommonsRestServiceModule__WEBPACK_IMPORTED_MODULE_15__["CommonsRestServiceModule"],
                _utils_functionsModule__WEBPACK_IMPORTED_MODULE_6__["FunctionsModule"],
                _flaws_flawInjectionInterceptorModule__WEBPACK_IMPORTED_MODULE_12__["FlawInjectionInterceptorModule"],
                'infinite-scroll',
                'resourceLocationsModule',
                'seConstantsModule',
                'yjqueryModule',
                'yLoDashModule',
                _modules_translations_translationServiceModule__WEBPACK_IMPORTED_MODULE_11__["TranslationServiceModule"],
                _ConfigModule__WEBPACK_IMPORTED_MODULE_13__["ConfigModule"],
                'ui.select',
                'ngSanitize'
            ],
            providers: [
                smarteditcommons_services_auth_AuthorizationService__WEBPACK_IMPORTED_MODULE_14__["AuthorizationService"],
                _SystemEventService__WEBPACK_IMPORTED_MODULE_5__["SystemEventService"],
                _crossFrame_CrossFrameEventServiceGateway__WEBPACK_IMPORTED_MODULE_4__["CrossFrameEventServiceGateway"],
                _crossFrame_CrossFrameEventService__WEBPACK_IMPORTED_MODULE_3__["CrossFrameEventService"],
                _language_LanguageServiceGateway__WEBPACK_IMPORTED_MODULE_8__["LanguageServiceGateway"],
                _language_LanguageService__WEBPACK_IMPORTED_MODULE_7__["LanguageService"]
            ],
            declarations: [
                smarteditcommons_directives__WEBPACK_IMPORTED_MODULE_2__["CompileHtmlDirective"],
                smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__["YInfiniteScrollingComponent"],
                smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__["YEventMessageComponent"],
                smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__["YMoreTextComponent"],
                smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__["LanguageDropdownSelectorComponent"],
                smarteditcommons_components__WEBPACK_IMPORTED_MODULE_1__["LanguageSelectorComponent"]
            ]
        })
    ], SmarteditCommonsModule);
    return SmarteditCommonsModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/SmarteditRootModule.ts":
/*!*****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/SmarteditRootModule.ts ***!
  \*****************************************************************/
/*! exports provided: SmarteditRootModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SmarteditRootModule", function() { return SmarteditRootModule; });
/* harmony import */ var _gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./gatewayProxiedAnnotation */ "./jsTarget/web/app/common/services/gatewayProxiedAnnotation.ts");
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var _gateway__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./gateway */ "./jsTarget/web/app/common/services/gateway/index.ts");
/* harmony import */ var _httpErrorInterceptor_default_retryInterceptor_OperationContextService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./httpErrorInterceptor/default/retryInterceptor/OperationContextService */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/OperationContextService.ts");
/* harmony import */ var _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @name smarteditRootModule
 *
 * @description
 * Module acts as a root module of smartedit commons module.
 */
var SmarteditRootModule = /** @class */ (function () {
    /** @internal */
    function SmarteditRootModule() {
    }
    SmarteditRootModule = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeModule"])({
            imports: [
                'resourceLocationsModule',
                'functionsModule',
                'seConstantsModule',
                'yjqueryModule',
                'yLoDashModule'
            ],
            providers: [
                _gateway__WEBPACK_IMPORTED_MODULE_2__["GatewayFactory"],
                _gateway__WEBPACK_IMPORTED_MODULE_2__["GatewayProxy"],
                _gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_0__["GatewayProxiedAnnotationFactory"],
                _httpErrorInterceptor_default_retryInterceptor_OperationContextService__WEBPACK_IMPORTED_MODULE_3__["OperationContextService"],
                _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_4__["OperationContextAnnotationFactory"]
            ],
            initialize: function (gatewayProxiedAnnotationFactory, operationContextAnnotationFactory) {
                'ngInject';
            }
        })
        /** @internal */
    ], SmarteditRootModule);
    return SmarteditRootModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/SystemEventService.ts":
/*!****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/SystemEventService.ts ***!
  \****************************************************************/
/*! exports provided: SystemEventService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SystemEventService", function() { return SystemEventService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:SystemEventService
 * @description
 *
 * The SystemEventService is used to transmit events synchronously or asynchronously. It is supported by the SmartEdit {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory} to propagate events between SmartEditContainer and SmartEdit.
 * It also contains options to publish events, as well as subscribe the event handlers.
 *
 */
var SystemEventService = /** @class */ (function () {
    /** @internal */
    function SystemEventService($timeout, $q, $log, toPromise) {
        this.$timeout = $timeout;
        this.$q = $q;
        this.$log = $log;
        this.toPromise = toPromise;
        this._eventHandlers = {};
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#publish
     * @methodOf smarteditCommonsModule.service:SystemEventService
     *
     * @description
     * send the event with data synchronously.
     *
     * @param {String} eventId The identifier of the event.
     * @param {any=} data The event payload. It is optional parameter.
     *
     * @return {angular.IPromise<any>} A promise with resolved data of last subscriber or with the rejected error reason
     */
    SystemEventService.prototype.publish = function (eventId, data) {
        if (!eventId) {
            this.$log.error('Failed to send event. No event ID provided for data: ' + data);
        }
        else {
            if (this._eventHandlers[eventId] && this._eventHandlers[eventId].length > 0) {
                return this._invokeEventHandlers(eventId, data);
            }
        }
        return this.$q.when();
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#sendSynchEvent
     * @methodOf smarteditCommonsModule.service:SystemEventService
     * @deprecated since 1808
     * @description
     * send the event with data synchronously.
     *
     * @param {String} eventId The identifier of the event.
     * @param {any=} data The event payload. It is optional parameter.
     *
     * @return {angular.IPromise<any>} A promise with resolved data of last subscriber or with the rejected error reason
     */
    SystemEventService.prototype.sendSynchEvent = function (eventId, data) {
        return this.publish(eventId, data);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#publishAsync
     * @methodOf smarteditCommonsModule.service:SystemEventService
     *
     * @description
     * send the event with data asynchronously.
     *
     * @param {String} eventId The identifier of the event.
     * @param {any=} data The event payload. It is an optional parameter.
     *
     * @return {angular.IPromise<any>} A deferred promise
     */
    SystemEventService.prototype.publishAsync = function (eventId, data) {
        var deferred = this.$q.defer();
        this.$timeout(function () {
            this.publish(eventId, data).then(function (resolvedData) {
                deferred.resolve(resolvedData);
            }, function (reason) {
                deferred.reject(reason);
            });
        }.bind(this), 0);
        return deferred.promise;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#sendAsynchEvent
     * @methodOf smarteditCommonsModule.service:SystemEventService
     * @deprecated since 1808
     * @description
     * send the event with data asynchronously.
     *
     * @param {String} eventId The identifier of the event.
     * @param {any=} data The event payload. It is an optional parameter.
     *
     * @return {angular.IPromise<any>} A deferred promise
     */
    SystemEventService.prototype.sendAsynchEvent = function (eventId, data) {
        return this.publishAsync(eventId, data);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#subscribe
     * @methodOf smarteditCommonsModule.service:SystemEventService
     *
     * @description
     * method to subscribe the event handler given the eventId and handler
     *
     * @param {String} eventId The identifier of the event.
     * @param {EventHandler} handler The event handler, a callback function which can either return a promise or directly a value.
     *
     * @return {() => void} unsubscribeFn Function to unsubscribe the event handler
     */
    SystemEventService.prototype.subscribe = function (eventId, handler) {
        var _this = this;
        var unsubscribeFn;
        if (!eventId || !handler) {
            this.$log.error('Failed to subscribe event handler for event: ' + eventId);
        }
        else {
            // create handlers array for this event if not already created
            if (this._eventHandlers[eventId] === undefined) {
                this._eventHandlers[eventId] = [];
            }
            this._eventHandlers[eventId].push(handler);
            unsubscribeFn = function () {
                _this._unsubscribe(eventId, handler);
            };
        }
        return unsubscribeFn;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:SystemEventService#registerEventHandler
     * @methodOf smarteditCommonsModule.service:SystemEventService
     * @deprecated since 1808
     * @description
     * method to subscribe the event handler given the eventId and handler
     *
     * @param {String} eventId The identifier of the event.
     * @param {EventHandler} handler The event handler, a callback function which can either return a promise or directly a value.
     *
     * @return {() => void} unsubscribeFn Function to unsubscribe the event handler
     */
    SystemEventService.prototype.registerEventHandler = function (eventId, handler) {
        return this.subscribe(eventId, handler);
    };
    SystemEventService.prototype._unsubscribe = function (eventId, handler) {
        var handlersArray = this._eventHandlers[eventId];
        var index = handlersArray ? this._eventHandlers[eventId].indexOf(handler) : -1;
        if (index >= 0) {
            this._eventHandlers[eventId].splice(index, 1);
        }
        else {
            this.$log.warn('Attempting to remove event handler for ' + eventId + ' but handler not found.');
        }
    };
    SystemEventService.prototype._invokeEventHandlers = function (eventId, data) {
        var _this = this;
        return this.$q.all(this._eventHandlers[eventId].map(function (eventHandler) {
            var promiseClosure = _this.toPromise(eventHandler);
            return promiseClosure(eventId, data);
        })).then(function (results) {
            return _this.$q.when(results.pop());
        }, function (reason) {
            return _this.$q.reject(reason);
        });
    };
    SystemEventService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], SystemEventService);
    return SystemEventService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/annotationService.ts":
/*!***************************************************************!*\
  !*** ./jsTarget/web/app/common/services/annotationService.ts ***!
  \***************************************************************/
/*! exports provided: AnnotationService, annotationService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnnotationService", function() { return AnnotationService; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "annotationService", function() { return annotationService; });
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
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

/** @internal */
var annotationType;
(function (annotationType) {
    annotationType["Class"] = "classAnnotation";
    annotationType["Method"] = "MethodAnnotation";
})(annotationType || (annotationType = {}));
var lodash = window.smarteditLodash;
/**
 * @ngdoc service
 * @name NoModule.service:AnnotationService
 *
 * @description
 * Utility service to declare and consume method level and class level {@link https://www.typescriptlang.org/docs/handbook/decorators.html Typescript decorator factories}.
 * <br/>Since Decorator is a reserved word in Smartedit, Typescript Decorators are called as Annotations.
 */
var AnnotationService = /** @class */ (function () {
    function AnnotationService() {
        this.INJECTABLE_NAME_KEY = "getInjectableName";
        this.ORIGINAL_CONSTRUCTOR_KEY = "originalConstructor";
        /**
         * @ngdoc method
         * @name NoModule.service:AnnotationService#getClassAnnotations
         * @methodOf NoModule.service:AnnotationService
         *
         * @description
         * Retrieves an object with all the string-indexed annotations defined on the given class target
         * @param {any} target The typescript class on which class annotations are defined
         * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
         */
        this.getClassAnnotations = lodash.memoize(this.getClassAnnotationsLogic);
        /**
         * @ngdoc method
         * @name NoModule.service:AnnotationService#getMethodAnnotations
         * @methodOf NoModule.service:AnnotationService
         *
         * @description
         * Retrieves an object with all the string indexed annotations defined on the given class method
         * @param {any} target The typescript class to the inspected
         * @param {string} propertyName The name of the method on which annotations are defined
         * @returns {[index: string]: any} an object contains string-indexed annotation name and payload
         */
        this.getMethodAnnotations = lodash.memoize(this.getMethodAnnotationsLogic, function (target, propertyName) {
            return JSON.stringify(target.prototype) + propertyName;
        });
        this.functionsUtils = new smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__["FunctionsUtils"]();
        this.annotationFactoryMap = {};
    }
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#getClassAnnotation
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Retrieves arguments of class annotation under a given annotation name
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {any} the payload passed to the annotation
     */
    AnnotationService.prototype.getClassAnnotation = function (target, annotation) {
        var annotationMap = this.getClassAnnotations(target);
        var annotationName = annotation.annotationName;
        if (annotationMap) {
            if (annotationName in annotationMap) {
                return annotationMap[annotationName];
            }
        }
        else {
            return null;
        }
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#getMethodAnnotation
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Retrieves arguments of method annotation for a given typescript class
     * @param {any} target The typescript class
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator)} annotation The type of the method annotation
     * @returns {any} the payload passed to the annotation
     */
    AnnotationService.prototype.getMethodAnnotation = function (target, propertyName, annotation) {
        var annotationMap = this.getMethodAnnotations(target, propertyName);
        var annotationName = annotation.annotationName;
        if (annotationMap) {
            if (annotationName in annotationMap) {
                return annotationMap[annotationName];
            }
        }
        else {
            return null;
        }
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#hasClassAnnotation
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Determines whether a given class target has given annotation name defined or not
     * @param {any} target The typescript class on which class annotation is defined
     * @param {(args?: any) => ClassDecorator} annotation The type of the class annotation
     * @returns {boolean} true if a given target has given annotation name. Otherwise false.
     */
    AnnotationService.prototype.hasClassAnnotation = function (target, annotation) {
        var annotationMap = this.getClassAnnotations(target);
        return (annotation.annotationName in annotationMap) ? true : false;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#hasMethodAnnotation
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Determines whether a given method name has given annotation name defined or not under a given typescript class
     * @param {any} target The typescript class object
     * @param {string} propertyName The name of the method on which annotation is defined
     * @param {(args?: any) => MethodDecorator} annotation The type of the method annotation
     * @returns {boolean} true if a given method name has given annotation name. Otherwise false.
     */
    AnnotationService.prototype.hasMethodAnnotation = function (target, propertyName, annotation) {
        var annotationMap = this.getMethodAnnotations(target, propertyName);
        return (annotation.annotationName in annotationMap) ? true : false;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#setClassAnnotationFactory
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Registers a {@link NoModule.object:ClassAnnotationFactory ClassAnnotationFactory} under a given name.
     * <br/>Typically, in order for the ClassAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name the name of the factory.
     * @returns {ClassAnnotationFactory} a {@link NoModule.object:ClassAnnotationFactory ClassAnnotationFactory}
     */
    AnnotationService.prototype.setClassAnnotationFactory = function (name, annotationFactory) {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#getClassAnnotationFactory
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Retrieves a {@link NoModule.object:ClassAnnotationFactory ClassAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const GatewayProxied = annotationService.getClassAnnotationFactory('GatewayProxied');
     * </pre>
     *
     * @param {string} name The name of the factory
     * @returns {ClassAnnotationFactory} a {@link NoModule.object:ClassAnnotationFactory ClassAnnotationFactory}
     */
    AnnotationService.prototype.getClassAnnotationFactory = function (name) {
        var instance = this;
        var classAnnotationFactory = function () {
            var factoryArgument = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                factoryArgument[_i] = arguments[_i];
            }
            return function (originalConstructor) {
                var newConstructor = instance.functionsUtils.extendsConstructor(originalConstructor, function () {
                    var args = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        args[_i] = arguments[_i];
                    }
                    var annotationFactory = instance.annotationFactoryMap[name];
                    if (annotationFactory) {
                        // Note: Before we used to bind originalConstructor.bind(this). However, it had to be left up to the caller 
                        // since that causes problems in IE; when a function is bound in IE, the browser wraps it in a function with 
                        // native code, making it impossible to retrieve its name. 
                        var result = annotationFactory(factoryArgument)(this, originalConstructor, args);
                        if (result) {
                            return result;
                        }
                    }
                    else {
                        throw new Error("annotation '" + name + "' is used on '" + originalConstructor.name + "' but its ClassAnnotationFactory may not have been added to the dependency injection");
                    }
                });
                if (instance.functionsUtils.hasArguments(originalConstructor) && !originalConstructor.$inject && !instance.functionsUtils.isUnitTestMode()) {
                    throw new Error(originalConstructor.name + " class was decorated with annotation " + name + " but has probably not been annotated with @SeInjectable() or @SeComponent");
                }
                /*
                 * enable angular to inject this new constructor even though it has an empty signature
                 * by copying $inject property
                 * For idempotency purposes we copy all properties anyways
                 */
                lodash.merge(newConstructor, originalConstructor);
                var rootOriginalConstructor = instance.getOriginalConstructor(originalConstructor);
                Reflect.defineMetadata(instance.ORIGINAL_CONSTRUCTOR_KEY, rootOriginalConstructor, newConstructor);
                Reflect.defineMetadata(annotationType.Class + ':' + name, factoryArgument, rootOriginalConstructor);
                // override original constructor
                return newConstructor;
            };
        };
        classAnnotationFactory.annotationName = name;
        return classAnnotationFactory;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#setMethodAnnotationFactory
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Registers a {@link NoModule.object:MethodAnnotationFactory MethodAnnotationFactory} under a given name.
     * <br/>Typically, in order for the MethodAnnotationFactory to benefit from Angular dependency injection, this method will be called within an Angular factory.
     * @param {string} name The name of the factory.
     * @returns {MethodAnnotationFactory} a {@link NoModule.object:MethodAnnotationFactory MethodAnnotationFactory}
     */
    AnnotationService.prototype.setMethodAnnotationFactory = function (name, annotationFactory) {
        this.annotationFactoryMap[name] = annotationFactory;
        return annotationFactory;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#getMethodAnnotationFactory
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Retrieves a method level {@link NoModule.object:MethodAnnotationFactory MethodAnnotationFactory}
     * previously registered under the given name:
     *
     * <pre>
     *   export const Cached = annotationService.getMethodAnnotationFactory('Cached');
     * </pre>
     *
     * @param {string} name the name of the factory.
     * @returns {MethodAnnotationFactory} a {@link NoModule.object:MethodAnnotationFactory MethodAnnotationFactory}.
     */
    AnnotationService.prototype.getMethodAnnotationFactory = function (name) {
        var instance = this;
        var methodAnnotationFactory = function () {
            var factoryArgument = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                factoryArgument[_i] = arguments[_i];
            }
            /*
             * when decorating an abstract class, strangely enough target is an instance of the abstract class
             * we need pass "this" instead to the annotationFactory invocation
             */
            return function (target, propertyName, descriptor) {
                var originalMethod = descriptor.value;
                descriptor.value = function () {
                    var annotationFactory = instance.annotationFactoryMap[name];
                    if (annotationFactory) {
                        return annotationFactory(factoryArgument)(this, propertyName, originalMethod.bind(this), arguments);
                    }
                    else {
                        throw new Error("annotation '" + name + "' is used but its MethodAnnotationFactory may not have been added to the dependency injection");
                    }
                };
                Reflect.defineMetadata(annotationType.Method + ':' + name, factoryArgument, target, propertyName);
            };
        };
        methodAnnotationFactory.annotationName = name;
        return methodAnnotationFactory;
    };
    /**
     * @ngdoc method
     * @name NoModule.service:AnnotationService#getOriginalConstructor
     * @methodOf NoModule.service:AnnotationService
     *
     * @description
     * Given a class constructor, returns the original constructor of it prior to any class level
     * proxying by annotations declared through {@link NoModule.service:AnnotationService AnnotationService}
     *
     * @param {SeConstructor} target the constructor
     */
    AnnotationService.prototype.getOriginalConstructor = function (target) {
        return Reflect.getMetadata(this.ORIGINAL_CONSTRUCTOR_KEY, target) || target;
    };
    AnnotationService.prototype.getClassAnnotationsLogic = function (target) {
        var originalConstructor = this.getOriginalConstructor(target);
        var annotationMap = {};
        Reflect.getMetadataKeys(originalConstructor)
            .filter(function (key) { return key.toString().startsWith(annotationType.Class); })
            .map(function (key) {
            annotationMap[key.split(':')[1]] = Reflect.getMetadata(key, originalConstructor);
        });
        return annotationMap;
    };
    AnnotationService.prototype.getMethodAnnotationsLogic = function (target, propertyName) {
        var annotationMap = {};
        Reflect.getMetadataKeys(target.prototype, propertyName)
            .filter(function (key) { return key.toString().startsWith(annotationType.Method); })
            .map(function (key) {
            annotationMap[key.split(':')[1]] = Reflect.getMetadata(key, target.prototype, propertyName);
        });
        return annotationMap;
    };
    return AnnotationService;
}());

'se:smarteditcommons';
var annotationService = new AnnotationService();
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.annotationService = annotationService;


/***/ }),

/***/ "./jsTarget/web/app/common/services/auth/AuthorizationService.ts":
/*!***********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/auth/AuthorizationService.ts ***!
  \***********************************************************************/
/*! exports provided: AuthorizationService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthorizationService", function() { return AuthorizationService; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name smarteditCommonsModule.service.AuthorizationService
 *
 * @description
 * This service makes calls to the Global Permissions REST API to check if the current user was
 * granted certain permissions.
 */
var AuthorizationService = /** @class */ (function () {
    function AuthorizationService($log, sessionService, permissionsRestService) {
        this.$log = $log;
        this.sessionService = sessionService;
        this.permissionsRestService = permissionsRestService;
    }
    AuthorizationService_1 = AuthorizationService;
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service.AuthorizationService#hasGlobalPermissions
     * @methodOf smarteditCommonsModule.service.AuthorizationService
     *
     * @description
     * This method checks if the current user is granted the given global permissions.
     *
     * @param {String[]} permissionNames The list of global permissions to check.
     *
     * @return {Boolean} true if the user is granted all of the given permissions, false otherwise
     *
     * @throws Will throw an error if the permissionNames array is empty
     */
    AuthorizationService.prototype.hasGlobalPermissions = function (permissionNames) {
        var _this = this;
        if (!permissionNames.length || permissionNames.length < 1) {
            throw AuthorizationService_1.ERR_INVALID_PERMISSION_NAMES;
        }
        var onSuccess = function (permissions) {
            return _this.mergePermissionResults(permissions, permissionNames);
        };
        var onError = function () {
            _this.$log.error('AuthorizationService - Failed to determine authorization for the following permissions: ' + permissionNames.toString());
            return false;
        };
        return this.getPermissions(permissionNames).then(onSuccess, onError);
    };
    /*
     * This method will look for the result for the given permission name. If found, it is
     * verified that it has been granted. Otherwise, the method will return false.
     */
    AuthorizationService.prototype.getPermissionResult = function (permissionResults, permissionName) {
        var permission = permissionResults.permissions.find(function (result) { return result.key.toLowerCase() === permissionName.toLowerCase(); });
        return !!permission && permission.value === 'true';
    };
    /*
     * This method merges permission results. It iterates through the list of permission names that
     * were checked and evaluates if the permission is granted. It immediately returns false when
     * it encounters a permission that is denied.
     */
    AuthorizationService.prototype.mergePermissionResults = function (permissionResults, permissionNames) {
        var hasPermission = !!permissionNames && permissionNames.length > 0;
        var index = 0;
        while (hasPermission && index < permissionNames.length) {
            hasPermission = hasPermission && this.getPermissionResult(permissionResults, permissionNames[index++]);
        }
        return hasPermission;
    };
    /*
     * This method makes a call to the Global Permissions API with the given permission names
     * and returns the list of results.
     */
    AuthorizationService.prototype.getPermissions = function (permissionNames) {
        var _this = this;
        return this.sessionService.getCurrentUsername().then(function (user) {
            if (!user) {
                return { permissions: [] };
            }
            return _this.permissionsRestService.get({
                user: user,
                permissionNames: permissionNames.join(',')
            }).then(function (response) {
                return response;
            });
        });
    };
    var AuthorizationService_1;
    AuthorizationService.ERR_INVALID_PERMISSION_NAMES = new Error('permissionNames must be a non-empty array');
    AuthorizationService = AuthorizationService_1 = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], AuthorizationService);
    return AuthorizationService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/CacheAction.ts":
/*!***************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/CacheAction.ts ***!
  \***************************************************************/
/*! exports provided: CacheAction */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CacheAction", function() { return CacheAction; });
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
 * @ngdoc object
 * @name smarteditServicesModule.object:CacheAction
 * @description
 * A {@link smarteditServicesModule.object:@Cached @Cached} annotation is associated to a CacheAction.
 */
var CacheAction = /** @class */ (function () {
    function CacheAction(name) {
        this.name = name;
    }
    return CacheAction;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/CacheService.ts":
/*!****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/CacheService.ts ***!
  \****************************************************************/
/*! exports provided: CacheService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CacheService", function() { return CacheService; });
/* harmony import */ var smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services */ "./jsTarget/web/app/common/services/index.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:CacheService
 * @description
 * Service to which the {@link smarteditServicesModule.object:@Cached @Cached} and {@link smarteditServicesModule.object:@InvalidateCache @InvalidateCache} annotations delegate to perform service method level caching.
 * It is not handled explicitly except for its evict method.
 */
var CacheService = /** @class */ (function () {
    /** @internal */
    function CacheService($q, $log, lodash, encode, functionsUtils, crossFrameEventService, cacheEngine) {
        this.$q = $q;
        this.$log = $log;
        this.lodash = lodash;
        this.encode = encode;
        this.functionsUtils = functionsUtils;
        this.crossFrameEventService = crossFrameEventService;
        this.cacheEngine = cacheEngine;
        this.predicatesRegistry = [];
        this.eventListeners = [];
        this.registerDefaultCacheTimings();
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:CacheService#register
     * @methodOf smarteditServicesModule.service:CacheService
     *
     * @description
     * Register a new predicate with it's associated cacheTiming.
     * Each time the @Cache annotation is handled, the CacheService try to find a matching cacheTiming for the given cacheActions.
     *
     * @param {ICachePredicate} test This function takes the cacheActions {@link smarteditServicesModule.object:CacheAction CacheAction} argument, and must return a Boolean that is true if the given cacheActions match the predicate.
     * @param {ICacheTiming} cacheTiming This function is used to call setAge(item: ICacheItem<any>) on the cached item.
     *
     * @return {CacheService} CacheService The CacheService instance.
     *
     * @example
     * ```ts
     * export class CustomCacheTiming implements ICacheTiming {
     * 	private expirationAge: number;
     * 	private refreshAge: number;
     *  constructor(expirationAge: number, refreshAge: number) {
     * 		// The cached response is discarded if it is older than the expiration age.
     * 		this.expirationAge = expirationAge;
     * 		// maximum age for the cached response to be considered "fresh."
     * 		this.refreshAge = refreshAge;
     * 	}
     * 	setAge(item: ICacheItem<any>): void {
     * 		item.expirationAge = this.expirationAge;
     * 		item.refreshAge = this.refreshAge;
     * 	}
     * 	};
     * 	const customCacheTiming = new CustomCacheTiming(30 * 60000, 15 * 60000);
     * 	const customContentPredicate: ICachePredicate = (cacheActions: CacheAction[]) => {
     * 		return cacheActions.find((cacheAction) => cacheAction.name === 'CUSTOM_TAG') !== null;
     * 	};
     * this.register(customContentPredicate, customCacheTiming);
     * ```
     */
    CacheService.prototype.register = function (test, cacheTiming) {
        this.predicatesRegistry.unshift({
            test: test,
            cacheTiming: cacheTiming
        });
        return this;
    };
    /**
     * public method but only meant to be used by @Cache annotation
     */
    CacheService.prototype.handle = function (service, methodName, preboundMethod, invocationArguments, cacheActions, tags) {
        var constructorName = this.functionsUtils.getInstanceConstructorName(service);
        var cachedItemId = window.btoa(constructorName + methodName) + this.encode(invocationArguments);
        var _item = this.cacheEngine.getItemById(cachedItemId);
        var item = _item || {
            id: cachedItemId,
            timestamp: new Date().getTime(),
            evictionTags: this.collectEventNamesFromTags(tags),
            cache: null,
            expirationAge: null,
            refreshAge: null
        };
        if (!_item) {
            var cacheTiming = this.findCacheTimingByCacheActions(cacheActions);
            if (!cacheTiming) {
                throw new Error('CacheService::handle - No predicate match.');
            }
            cacheTiming.setAge(item);
            this.cacheEngine.addItem(item, cacheTiming, preboundMethod.bind.apply(preboundMethod, [undefined].concat(invocationArguments)));
            this.listenForEvictionByTags(tags);
        }
        return this.cacheEngine.handle(item);
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:CacheService#evict
     * @methodOf  smarteditServicesModule.service:CacheService
     * @description
     * Will evict the entire cache of all methods of all services referencing either directly or indirectly the given {@link smarteditServicesModule.object:EvictionTag EvictionTags}
     * @param {...EvictionTag[]} evictionTags the {@link smarteditServicesModule.object:EvictionTag EvictionTags}
     */
    CacheService.prototype.evict = function () {
        var evictionTags = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            evictionTags[_i] = arguments[_i];
        }
        var _a;
        var tags = this.collectEventNamesFromTags(evictionTags);
        (_a = this.cacheEngine).evict.apply(_a, tags);
    };
    CacheService.prototype.listenForEvictionByTags = function (tags) {
        var _this = this;
        this.collectEventNamesFromTags(tags).filter(function (eventId) {
            return _this.eventListeners.indexOf(eventId) === -1;
        }).forEach(function (eventId) {
            _this.$log.debug("registering event listener " + eventId);
            _this.eventListeners.push(eventId);
            _this.crossFrameEventService.subscribe(eventId, function (evt, data) {
                _this.$log.debug("cleaning cache on event " + eventId);
                _this.cacheEngine.evict(eventId);
                return _this.$q.when({});
            });
        });
    };
    CacheService.prototype.collectEventNamesFromTags = function (tags) {
        var _this = this;
        var _a;
        if (tags && tags.length) {
            return (_a = this.lodash).union.apply(_a, tags.map(function (t) { return _this.collectEventNamesFromTag(t); }));
        }
        else {
            return [];
        }
    };
    CacheService.prototype.collectEventNamesFromTag = function (tag) {
        var _this = this;
        var _a;
        return (_a = this.lodash).union.apply(_a, [[tag.event]].concat((tag.relatedTags ? tag.relatedTags.map(function (t) { return _this.collectEventNamesFromTag(t); }) : [])));
    };
    CacheService.prototype.findCacheTimingByCacheActions = function (cacheActions) {
        var predicate = this.predicatesRegistry.find(function (cacheTimingPredicate) { return cacheTimingPredicate.test(cacheActions); });
        return predicate ? predicate.cacheTiming : null;
    };
    CacheService.prototype.registerDefaultCacheTimings = function () {
        var defaultCacheTiming = new smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__["DefaultCacheTiming"](24 * 60 * 60 * 1000, 12 * 60 * 60 * 1000);
        var rarelyChangingContentPredicate = function (cacheActions) {
            return cacheActions.find(function (cacheAction) { return cacheAction.name === smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__["RarelyChangingContentName"]; }) !== null;
        };
        this.register(rarelyChangingContentPredicate, defaultCacheTiming);
    };
    CacheService = __decorate([
        Object(smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], CacheService);
    return CacheService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/EvictionTag.ts":
/*!***************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/EvictionTag.ts ***!
  \***************************************************************/
/*! exports provided: EvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "EvictionTag", function() { return EvictionTag; });
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
 * @ngdoc object
 * @name smarteditServicesModule.object:EvictionTag
 * @description
 * A {@link smarteditServicesModule.object:@Cached @Cached} annotation is tagged with 0 to n EvictionTag, each EvictionTag possibly referencing other evictionTags.
 * <br/>An EvictionTag enables a method cache to be evicted 2 different ways:
 * <ul>
 * <li> An event with the same name as the tag is raised.</li>
 * <li> {@link smarteditServicesModule.service:CacheService#methods_evict evict} method of {@link smarteditServicesModule.service:CacheService cacheService} is invoked with the tag.</li>
 * </ul>
 */
var EvictionTag = /** @class */ (function () {
    function EvictionTag(args) {
        this.event = args.event;
        this.relatedTags = args.relatedTags;
    }
    return EvictionTag;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/actions/frequentlyChangingContent.ts":
/*!*************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/actions/frequentlyChangingContent.ts ***!
  \*************************************************************************************/
/*! exports provided: FrequentlyChangingContentName, frequentlyChangingContent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FrequentlyChangingContentName", function() { return FrequentlyChangingContentName; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "frequentlyChangingContent", function() { return frequentlyChangingContent; });
/* harmony import */ var _CacheAction__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../CacheAction */ "./jsTarget/web/app/common/services/cache/CacheAction.ts");
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

'se:smarteditcommons';
var FrequentlyChangingContentName = 'FrequentlyChangingContent';
'se:smarteditcommons';
var frequentlyChangingContent = new _CacheAction__WEBPACK_IMPORTED_MODULE_0__["CacheAction"](FrequentlyChangingContentName);
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.FrequentlyChangingContentName = FrequentlyChangingContentName;
window.smarteditcommons.frequentlyChangingContent = frequentlyChangingContent;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/actions/index.ts":
/*!*****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/actions/index.ts ***!
  \*****************************************************************/
/*! exports provided: FrequentlyChangingContentName, frequentlyChangingContent, RarelyChangingContentName, rarelyChangingContent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _frequentlyChangingContent__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./frequentlyChangingContent */ "./jsTarget/web/app/common/services/cache/actions/frequentlyChangingContent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FrequentlyChangingContentName", function() { return _frequentlyChangingContent__WEBPACK_IMPORTED_MODULE_0__["FrequentlyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "frequentlyChangingContent", function() { return _frequentlyChangingContent__WEBPACK_IMPORTED_MODULE_0__["frequentlyChangingContent"]; });

/* harmony import */ var _rarelyChangingContent__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./rarelyChangingContent */ "./jsTarget/web/app/common/services/cache/actions/rarelyChangingContent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RarelyChangingContentName", function() { return _rarelyChangingContent__WEBPACK_IMPORTED_MODULE_1__["RarelyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "rarelyChangingContent", function() { return _rarelyChangingContent__WEBPACK_IMPORTED_MODULE_1__["rarelyChangingContent"]; });

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




/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/actions/rarelyChangingContent.ts":
/*!*********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/actions/rarelyChangingContent.ts ***!
  \*********************************************************************************/
/*! exports provided: RarelyChangingContentName, rarelyChangingContent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RarelyChangingContentName", function() { return RarelyChangingContentName; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "rarelyChangingContent", function() { return rarelyChangingContent; });
/* harmony import */ var _CacheAction__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../CacheAction */ "./jsTarget/web/app/common/services/cache/CacheAction.ts");
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

'se:smarteditcommons';
/** @internal */
var RarelyChangingContentName = 'RarelyChangingContent';
'se:smarteditcommons';
var rarelyChangingContent = new _CacheAction__WEBPACK_IMPORTED_MODULE_0__["CacheAction"](RarelyChangingContentName);
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.RarelyChangingContentName = RarelyChangingContentName;
window.smarteditcommons.rarelyChangingContent = rarelyChangingContent;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/cachedAnnotation.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/cachedAnnotation.ts ***!
  \********************************************************************/
/*! exports provided: CacheConfig, CacheConfigAnnotationFactory, Cached, CachedAnnotationFactory, InvalidateCache, InvalidateCacheAnnotationFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CacheConfig", function() { return CacheConfig; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CacheConfigAnnotationFactory", function() { return CacheConfigAnnotationFactory; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Cached", function() { return Cached; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CachedAnnotationFactory", function() { return CachedAnnotationFactory; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "InvalidateCache", function() { return InvalidateCache; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "InvalidateCacheAnnotationFactory", function() { return InvalidateCacheAnnotationFactory; });
/* harmony import */ var _annotationService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
/* harmony import */ var _utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
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


var lodash = window.smarteditLodash;
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////// CACHE CONFIG ////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
var cacheConfigAnnotationName = "CacheConfig";
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@CacheConfig
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for setting
 *  class level cache configuration to be merged into method specific {@link smarteditServicesModule.object:@Cached @Cached} and
 *  {@link smarteditServicesModule.object:@InvalidateCache @InvalidateCache} configurations.
 * @param {object} cacheConfig the configuration fo this cache
 * @param {cacheAction} cacheConfig.actions the list of {@link smarteditServicesModule.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link smarteditServicesModule.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
'se:smarteditcommons';
var CacheConfig = _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].getClassAnnotationFactory(cacheConfigAnnotationName);
'se:smarteditcommons';
function CacheConfigAnnotationFactory($log) {
    'ngInject';
    return _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].setClassAnnotationFactory(cacheConfigAnnotationName, function (factoryArguments) {
        return function (instance, originalConstructor, invocationArguments) {
            originalConstructor.call.apply(originalConstructor, [instance].concat(invocationArguments));
            instance.cacheConfig = factoryArguments[0];
            $log.debug("adding cache config " + JSON.stringify(instance.cacheConfig) + " to class " + _utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["functionsUtils"].getInstanceConstructorName(instance), instance);
        };
    });
}
///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// CACHE ////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
var CachedAnnotationName = 'Cached';
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@Cached
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for performing
 * invocation arguments sensitive method caching.
 * <br/> This annotation must only be used on methods returning promises.
 * @param {object} cacheConfig the configuration fo this cache
 * <br/> This configuration will be merged with a class level {@link smarteditServicesModule.object:@CacheConfig @acheConfig} if any.
 * @throws if no {@link smarteditServicesModule.object:CacheAction CacheAction} is found in the resulting merge
 * @param {cacheAction} cacheConfig.actions the list of {@link smarteditServicesModule.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link smarteditServicesModule.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
'se:smarteditcommons';
var Cached = _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].getMethodAnnotationFactory(CachedAnnotationName);
'se:smarteditcommons';
function CachedAnnotationFactory(cacheService) {
    'ngInject';
    return _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].setMethodAnnotationFactory(CachedAnnotationName, function (factoryArguments) {
        return function (target, propertyName, originalMethod, invocationArguments) {
            var actions = [];
            var tags = [];
            if (factoryArguments[0]) {
                actions = factoryArguments[0].actions;
                tags = factoryArguments[0].tags;
            }
            if (target.cacheConfig) {
                if (target.cacheConfig.actions) {
                    actions = lodash.uniq(actions.concat(target.cacheConfig.actions));
                }
                if (target.cacheConfig.tags) {
                    tags = lodash.uniq(tags.concat(target.cacheConfig.tags));
                }
            }
            if (!actions.length) {
                var constructorName = _utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["functionsUtils"].getInstanceConstructorName(target);
                throw new Error("method " + propertyName + " of " + constructorName + " is @Cached annotated but no CacheAction is specified either through @Cached or through class level @CacheConfig annotation");
            }
            return cacheService.handle(target, propertyName, originalMethod, Array.prototype.slice.apply(invocationArguments), actions, tags);
        };
    });
}
///////////////////////////////////////////////////////////////////////////////
////////////////////////////// INVALIDATE CACHE ///////////////////////////////
///////////////////////////////////////////////////////////////////////////////
var InvalidateCacheName = 'InvalidateCache';
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@InvalidateCache
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for
 * invalidating all caches either directly or indirectly declaring the {@link smarteditServicesModule.object:EvictionTag eviction tag} passed as argument.
 * if no eviction tag is passed as argument, defaults to the optional eviction tags passed to the class through {@link smarteditServicesModule.object:@CacheConfig @CacheConfig}.
 *
 * @param {EvictionTag} evictionTag the {@link smarteditServicesModule.object:EvictionTag eviction tag}.
 */
'se:smarteditcommons';
var InvalidateCache = function (tag) {
    return _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].getMethodAnnotationFactory(InvalidateCacheName)(tag);
};
'se:smarteditcommons';
function InvalidateCacheAnnotationFactory(cacheService) {
    'ngInject';
    return _annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].setMethodAnnotationFactory(InvalidateCacheName, function (factoryArguments) {
        return function (target, propertyName, originalMethod, invocationArguments) {
            var tags = [];
            var tag = factoryArguments[0];
            if (!tag) {
                if (target.cacheConfig && target.cacheConfig.tags) {
                    tags = target.cacheConfig.tags;
                }
            }
            else {
                tags = [tag];
            }
            if (!tags.length) {
                throw new Error("method " + propertyName + " of " + target.constructor.name + " is @InvalidateCache annotated but no EvictionTag is specified either through @InvalidateCache or through class level @CacheConfig annotation");
            }
            var returnedObject = originalMethod.apply(undefined, invocationArguments);
            if (returnedObject && returnedObject.then) {
                return returnedObject.then(function (value) {
                    cacheService.evict.apply(cacheService, tags);
                    return value;
                });
            }
            else {
                cacheService.evict.apply(cacheService, tags);
                return returnedObject;
            }
        };
    });
}
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.CacheConfig = CacheConfig;
window.smarteditcommons.CacheConfigAnnotationFactory = CacheConfigAnnotationFactory;
window.smarteditcommons.Cached = Cached;
window.smarteditcommons.CachedAnnotationFactory = CachedAnnotationFactory;
window.smarteditcommons.InvalidateCache = InvalidateCache;
window.smarteditcommons.InvalidateCacheAnnotationFactory = InvalidateCacheAnnotationFactory;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/engine/CacheEngine.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/engine/CacheEngine.ts ***!
  \**********************************************************************/
/*! exports provided: CacheEngine */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CacheEngine", function() { return CacheEngine; });
/** @internal */
/** @ngInject */
var CacheEngine = /** @class */ (function () {
    function CacheEngine($q, $log) {
        this.$q = $q;
        this.$log = $log;
        this.cachedItemsRegistry = [];
        this.startBackgroundMonitoringJob();
    }
    CacheEngine.prototype.addItem = function (item, cacheTiming, refresh) {
        if (this.getItemIndex(item) === -1) {
            this.cachedItemsRegistry.push({
                item: item,
                cacheTiming: cacheTiming,
                refresh: refresh,
                completed: false,
                processing: false,
                defer: this.$q.defer()
            });
        }
        else {
            this.$log.warn("CacheEngine - item already exist for id: " + item.id);
        }
    };
    CacheEngine.prototype.getItemById = function (id) {
        var match = this.cachedItemsRegistry.find(function (obj) { return obj.item.id === id; });
        return match ? match.item : null;
    };
    CacheEngine.prototype.handle = function (item) {
        var obj = this.cachedItemsRegistry[this.getItemIndex(item)];
        if (obj.completed && !this.hasExpired(item)) {
            obj.defer.resolve(item.cache);
        }
        else if (!obj.processing) {
            obj.processing = true;
            this.refreshCache(obj);
        }
        return obj.defer.promise;
    };
    CacheEngine.prototype.evict = function () {
        var _this = this;
        var tags = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            tags[_i] = arguments[_i];
        }
        tags.forEach(function (tag) {
            _this.cachedItemsRegistry
                .filter(function (obj) { return obj.item.evictionTags.indexOf(tag) > -1; })
                .forEach(function (obj) { return _this.cachedItemsRegistry.splice(_this.getItemIndex(obj.item), 1); });
        });
    };
    // regularly go though cache data and call prebound methods to refresh data when needed.
    CacheEngine.prototype.startBackgroundMonitoringJob = function () {
        var _this = this;
        setInterval(function () {
            _this.cachedItemsRegistry
                .filter(function (obj) { return _this.needRefresh(obj.item); })
                .forEach(function (obj) { return _this.refreshCache(obj); });
        }, CacheEngine.BACKGROUND_REFRESH_INTERVAL);
    };
    CacheEngine.prototype.refreshCache = function (obj) {
        var _this = this;
        obj.refresh().then(function (value) {
            // TODO: read value.metadata to refresh expiry/refresh ages.
            obj.cacheTiming.setAge(obj.item);
            obj.item.cache = value;
            obj.item.timestamp = new Date().getTime();
            obj.completed = true;
            obj.processing = false;
            obj.defer.resolve(value);
        }, function (e) {
            _this.$log.error("CacheEngine - unable to refresh cache for id: " + obj.item.id, e);
            delete obj.item.cache;
            obj.defer.reject(e);
        });
    };
    CacheEngine.prototype.hasExpired = function (item) {
        return (item.timestamp + item.expirationAge) <= new Date().getTime();
    };
    CacheEngine.prototype.needRefresh = function (item) {
        return (item.timestamp + item.refreshAge) <= new Date().getTime();
    };
    CacheEngine.prototype.getItemIndex = function (item) {
        return this.cachedItemsRegistry.findIndex(function (o) { return o.item.id === item.id; });
    };
    CacheEngine.BACKGROUND_REFRESH_INTERVAL = 10000;
    return CacheEngine;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/engine/DefaultCacheTiming.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/engine/DefaultCacheTiming.ts ***!
  \*****************************************************************************/
/*! exports provided: DefaultCacheTiming */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DefaultCacheTiming", function() { return DefaultCacheTiming; });
var DefaultCacheTiming = /** @class */ (function () {
    function DefaultCacheTiming(expirationAge, refreshAge) {
        // The cached response is discarded if it is older than the expiration age.
        this.expirationAge = expirationAge;
        // maximum age for the cached response to be considered "fresh."
        this.refreshAge = refreshAge;
    }
    DefaultCacheTiming.prototype.setAge = function (item) {
        item.expirationAge = this.expirationAge;
        item.refreshAge = this.refreshAge;
    };
    return DefaultCacheTiming;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/engine/index.ts":
/*!****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/engine/index.ts ***!
  \****************************************************************/
/*! exports provided: CacheEngine, DefaultCacheTiming */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _CacheEngine__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./CacheEngine */ "./jsTarget/web/app/common/services/cache/engine/CacheEngine.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheEngine", function() { return _CacheEngine__WEBPACK_IMPORTED_MODULE_0__["CacheEngine"]; });

/* harmony import */ var _DefaultCacheTiming__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./DefaultCacheTiming */ "./jsTarget/web/app/common/services/cache/engine/DefaultCacheTiming.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultCacheTiming", function() { return _DefaultCacheTiming__WEBPACK_IMPORTED_MODULE_1__["DefaultCacheTiming"]; });

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




/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/index.ts":
/*!*********************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/index.ts ***!
  \*********************************************************/
/*! exports provided: CacheConfig, CacheConfigAnnotationFactory, Cached, CachedAnnotationFactory, InvalidateCache, InvalidateCacheAnnotationFactory, CacheAction, CacheService, EvictionTag, FrequentlyChangingContentName, frequentlyChangingContent, RarelyChangingContentName, rarelyChangingContent, CacheEngine, DefaultCacheTiming, authorizationEvictionTag, catalogSyncedEvictionTag, catalogEvictionTag, pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag, pageRestoredEvictionTag, pageChangeEvictionTag, pageEvictionTag, userEvictionTag, contentCatalogUpdateEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./cachedAnnotation */ "./jsTarget/web/app/common/services/cache/cachedAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfig", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["CacheConfig"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfigAnnotationFactory", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["CacheConfigAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "Cached", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["Cached"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CachedAnnotationFactory", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["CachedAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCache", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["InvalidateCache"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCacheAnnotationFactory", function() { return _cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["InvalidateCacheAnnotationFactory"]; });

/* harmony import */ var _CacheAction__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./CacheAction */ "./jsTarget/web/app/common/services/cache/CacheAction.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheAction", function() { return _CacheAction__WEBPACK_IMPORTED_MODULE_1__["CacheAction"]; });

/* harmony import */ var _CacheService__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./CacheService */ "./jsTarget/web/app/common/services/cache/CacheService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheService", function() { return _CacheService__WEBPACK_IMPORTED_MODULE_2__["CacheService"]; });

/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "EvictionTag", function() { return _EvictionTag__WEBPACK_IMPORTED_MODULE_3__["EvictionTag"]; });

/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./actions */ "./jsTarget/web/app/common/services/cache/actions/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FrequentlyChangingContentName", function() { return _actions__WEBPACK_IMPORTED_MODULE_4__["FrequentlyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "frequentlyChangingContent", function() { return _actions__WEBPACK_IMPORTED_MODULE_4__["frequentlyChangingContent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RarelyChangingContentName", function() { return _actions__WEBPACK_IMPORTED_MODULE_4__["RarelyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "rarelyChangingContent", function() { return _actions__WEBPACK_IMPORTED_MODULE_4__["rarelyChangingContent"]; });

/* harmony import */ var _engine__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./engine */ "./jsTarget/web/app/common/services/cache/engine/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheEngine", function() { return _engine__WEBPACK_IMPORTED_MODULE_5__["CacheEngine"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultCacheTiming", function() { return _engine__WEBPACK_IMPORTED_MODULE_5__["DefaultCacheTiming"]; });

/* harmony import */ var _tags__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./tags */ "./jsTarget/web/app/common/services/cache/tags/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "authorizationEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["authorizationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogSyncedEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["catalogSyncedEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["catalogEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageCreationEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageCreationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageDeletionEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageDeletionEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageUpdateEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageUpdateEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageRestoredEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageRestoredEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageChangeEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageChangeEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["pageEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "userEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["userEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "contentCatalogUpdateEvictionTag", function() { return _tags__WEBPACK_IMPORTED_MODULE_6__["contentCatalogUpdateEvictionTag"]; });

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









/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/authorizationEvictionTag.ts":
/*!*********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/authorizationEvictionTag.ts ***!
  \*********************************************************************************/
/*! exports provided: authorizationEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "authorizationEvictionTag", function() { return authorizationEvictionTag; });
/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
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

// TODO : merge the EVENT strings and the tag ones
'se:smarteditcommons';
var authorizationEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "AUTHORIZATION_SUCCESS" });
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.authorizationEvictionTag = authorizationEvictionTag;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/catalogEvictionTag.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/catalogEvictionTag.ts ***!
  \***************************************************************************/
/*! exports provided: catalogSyncedEvictionTag, catalogEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "catalogSyncedEvictionTag", function() { return catalogSyncedEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "catalogEvictionTag", function() { return catalogEvictionTag; });
/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
/* harmony import */ var _userEvictionTag__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./userEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/userEvictionTag.ts");
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


'se:smarteditcommons';
var catalogSyncedEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "CATALOG_SYNCHRONIZED_EVENT" });
'se:smarteditcommons';
var catalogEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "CATALOG_EVENT", relatedTags: [catalogSyncedEvictionTag, _userEvictionTag__WEBPACK_IMPORTED_MODULE_1__["userEvictionTag"]] });
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.catalogSyncedEvictionTag = catalogSyncedEvictionTag;
window.smarteditcommons.catalogEvictionTag = catalogEvictionTag;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/contentCatalogUpdateEvictionTag.ts":
/*!****************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/contentCatalogUpdateEvictionTag.ts ***!
  \****************************************************************************************/
/*! exports provided: contentCatalogUpdateEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "contentCatalogUpdateEvictionTag", function() { return contentCatalogUpdateEvictionTag; });
/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
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

'se:smarteditcommons';
var contentCatalogUpdateEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "EVENT_CONTENT_CATALOG_UPDATE" });
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.contentCatalogUpdateEvictionTag = contentCatalogUpdateEvictionTag;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/index.ts":
/*!**************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/index.ts ***!
  \**************************************************************/
/*! exports provided: authorizationEvictionTag, catalogSyncedEvictionTag, catalogEvictionTag, pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag, pageRestoredEvictionTag, pageChangeEvictionTag, pageEvictionTag, userEvictionTag, contentCatalogUpdateEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _authorizationEvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./authorizationEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/authorizationEvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "authorizationEvictionTag", function() { return _authorizationEvictionTag__WEBPACK_IMPORTED_MODULE_0__["authorizationEvictionTag"]; });

/* harmony import */ var _catalogEvictionTag__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./catalogEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/catalogEvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogSyncedEvictionTag", function() { return _catalogEvictionTag__WEBPACK_IMPORTED_MODULE_1__["catalogSyncedEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogEvictionTag", function() { return _catalogEvictionTag__WEBPACK_IMPORTED_MODULE_1__["catalogEvictionTag"]; });

/* harmony import */ var _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./pageEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/pageEvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageCreationEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageCreationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageDeletionEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageDeletionEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageUpdateEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageUpdateEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageRestoredEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageRestoredEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageChangeEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageChangeEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageEvictionTag", function() { return _pageEvictionTag__WEBPACK_IMPORTED_MODULE_2__["pageEvictionTag"]; });

/* harmony import */ var _userEvictionTag__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./userEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/userEvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "userEvictionTag", function() { return _userEvictionTag__WEBPACK_IMPORTED_MODULE_3__["userEvictionTag"]; });

/* harmony import */ var _contentCatalogUpdateEvictionTag__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./contentCatalogUpdateEvictionTag */ "./jsTarget/web/app/common/services/cache/tags/contentCatalogUpdateEvictionTag.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "contentCatalogUpdateEvictionTag", function() { return _contentCatalogUpdateEvictionTag__WEBPACK_IMPORTED_MODULE_4__["contentCatalogUpdateEvictionTag"]; });

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







/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/pageEvictionTag.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/pageEvictionTag.ts ***!
  \************************************************************************/
/*! exports provided: pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag, pageRestoredEvictionTag, pageChangeEvictionTag, pageEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageCreationEvictionTag", function() { return pageCreationEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageDeletionEvictionTag", function() { return pageDeletionEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageUpdateEvictionTag", function() { return pageUpdateEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageRestoredEvictionTag", function() { return pageRestoredEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageChangeEvictionTag", function() { return pageChangeEvictionTag; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "pageEvictionTag", function() { return pageEvictionTag; });
/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
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

'se:smarteditcommons';
var pageCreationEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "PAGE_CREATED_EVENT" });
'se:smarteditcommons';
var pageDeletionEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "PAGE_DELETED_EVENT" });
'se:smarteditcommons';
var pageUpdateEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "PAGE_UPDATED_EVENT" });
'se:smarteditcommons';
var pageRestoredEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "PAGE_RESTORED_EVENT" });
'se:smarteditcommons';
var pageChangeEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "PAGE_CHANGE" });
'se:smarteditcommons';
var pageEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "pageEvictionTag", relatedTags: [pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag] });
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.pageCreationEvictionTag = pageCreationEvictionTag;
window.smarteditcommons.pageDeletionEvictionTag = pageDeletionEvictionTag;
window.smarteditcommons.pageUpdateEvictionTag = pageUpdateEvictionTag;
window.smarteditcommons.pageRestoredEvictionTag = pageRestoredEvictionTag;
window.smarteditcommons.pageChangeEvictionTag = pageChangeEvictionTag;
window.smarteditcommons.pageEvictionTag = pageEvictionTag;


/***/ }),

/***/ "./jsTarget/web/app/common/services/cache/tags/userEvictionTag.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/cache/tags/userEvictionTag.ts ***!
  \************************************************************************/
/*! exports provided: userEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "userEvictionTag", function() { return userEvictionTag; });
/* harmony import */ var _EvictionTag__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../EvictionTag */ "./jsTarget/web/app/common/services/cache/EvictionTag.ts");
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

// TODO : merge the EVENT strings and the tag ones
var userEvictionTag = new _EvictionTag__WEBPACK_IMPORTED_MODULE_0__["EvictionTag"]({ event: "USER_HAS_CHANGED" });


/***/ }),

/***/ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventService.ts":
/*!*******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/crossFrame/CrossFrameEventService.ts ***!
  \*******************************************************************************/
/*! exports provided: CrossFrameEventService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventService", function() { return CrossFrameEventService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:CrossFrameEventService
 *
 * @description
 * The Cross Frame Event Service is responsible for publishing and subscribing events within and between frames.
 * It uses {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory} and {@link smarteditCommonsModule.service:SystemEventService EventService} to transmit events.
 *
 */
var CrossFrameEventService = /** @class */ (function () {
    /** @internal */
    function CrossFrameEventService($q, systemEventService, crossFrameEventServiceGateway, windowUtils) {
        this.$q = $q;
        this.systemEventService = systemEventService;
        this.crossFrameEventServiceGateway = crossFrameEventServiceGateway;
        this.windowUtils = windowUtils;
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:CrossFrameEventService#publish
     * @methodOf smarteditCommonsModule.service:CrossFrameEventService
     *
     * @description
     * Publishes an event within and across the gateway.
     *
     * The publish method is used to send events using {@link smarteditCommonsModule.SystemEventService#publishAsync publishAsync} of
     * {@link smarteditCommonsModule.SystemEventService SystemEventService} and as well send the message across the gateway by using
     * {@link smarteditCommonsModule.service:MessageGateway#publish publish} of the {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory}.
     *
     * @param {String} eventId Event identifier
     * @param {any=} data The event payload. It is an optional paramter.
     * @returns {angular.IPromise<[any, any]>} Promise to resolve
     */
    CrossFrameEventService.prototype.publish = function (eventId, data) {
        var promises = [this.systemEventService.publishAsync(eventId, data)];
        if (this.windowUtils.getTargetIFrame()) {
            promises.push(this.crossFrameEventServiceGateway.publish(eventId, data));
        }
        return this.$q.all(promises);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:CrossFrameEventService#subscribe
     * @methodOf smarteditCommonsModule.service:CrossFrameEventService
     *
     * @description
     * Subscribe to an event across both frames.
     *
     * The subscribe method is used to register for listening to events using subscribe method of
     * {@link smarteditCommonsModule.SystemEventService SystemEventService} and as well send the registration message across the gateway by using
     * {@link smarteditCommonsModule.service:MessageGateway#subscribe subscribe} of the {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory}.
     *
     * @param {String} eventId Event identifier
     * @param {CloneableEventHandler} handler Callback function to be invoked
     * @returns {() => void} The function to call in order to unsubscribe the event listening; this will unsubscribe both from the systemEventService and the crossFrameEventServiceGatway
     */
    CrossFrameEventService.prototype.subscribe = function (eventId, handler) {
        var systemEventServiceUnsubscribeFn = this.systemEventService.subscribe(eventId, handler);
        var crossFrameEventServiceGatewayUnsubscribeFn = this.crossFrameEventServiceGateway.subscribe(eventId, handler);
        var unsubscribeFn = function () {
            systemEventServiceUnsubscribeFn();
            crossFrameEventServiceGatewayUnsubscribeFn();
        };
        return unsubscribeFn;
    };
    CrossFrameEventService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], CrossFrameEventService);
    return CrossFrameEventService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventServiceGateway.ts":
/*!**************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/crossFrame/CrossFrameEventServiceGateway.ts ***!
  \**************************************************************************************/
/*! exports provided: CrossFrameEventServiceGateway */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventServiceGateway", function() { return CrossFrameEventServiceGateway; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/** @internal */
var CrossFrameEventServiceGateway = /** @class */ (function () {
    function CrossFrameEventServiceGateway(CROSS_FRAME_EVENT, gatewayFactory) {
        return gatewayFactory.createGateway(CROSS_FRAME_EVENT);
    }
    CrossFrameEventServiceGateway = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], CrossFrameEventServiceGateway);
    return CrossFrameEventServiceGateway;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/DINameUtils.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/DINameUtils.ts ***!
  \*****************************************************************************/
/*! exports provided: DINameUtils, diNameUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DINameUtils", function() { return DINameUtils; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "diNameUtils", function() { return diNameUtils; });
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony import */ var _annotationService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
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


/** @internal */
var DINameUtils = /** @class */ (function () {
    function DINameUtils() {
    }
    DINameUtils.prototype.buildComponentName = function (componentConstructor) {
        return this.buildName(componentConstructor).replace(/Component$/, '').replace(/Directive$/, '');
    };
    DINameUtils.prototype.buildServiceName = function (serviceConstructor) {
        return this.buildName(serviceConstructor);
    };
    // builds the DI recipe name for a given construtor
    DINameUtils.prototype.buildName = function (constructor) {
        var originalConstructor = _annotationService__WEBPACK_IMPORTED_MODULE_1__["annotationService"].getOriginalConstructor(constructor);
        var originalName = smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__["functionsUtils"].getConstructorName(originalConstructor);
        return this.convertNameCasing(originalName);
    };
    // converts the first character to lower case
    DINameUtils.prototype.convertNameCasing = function (originalName) {
        var builtName = originalName.substring(0, 1).toLowerCase() + originalName.substring(1);
        return builtName;
    };
    /*
     * This method will generate a SeValueProvider from a shortHand map built off a variable:
     * if a variable x (or DEFAULT_x) equals 5, then the method will return
     * { provide : 'x', useValue: 5} when it is passed {x}
     */
    /* forbiddenNameSpaces useValue:false */
    DINameUtils.prototype.makeValueProvider = function (variableShortHand) {
        var fullKey = Object.keys(variableShortHand)[0];
        var key = fullKey.replace(/^DEFAULT_/, "");
        return {
            provide: key,
            useValue: variableShortHand[fullKey]
        };
    };
    return DINameUtils;
}());

'se:smarteditcommons';
var diNameUtils = new DINameUtils();
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.diNameUtils = diNameUtils;


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/ISeComponent.ts":
/*!******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/ISeComponent.ts ***!
  \******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/SeComponent.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/SeComponent.ts ***!
  \*****************************************************************************/
/*! exports provided: SeComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SeComponent", function() { return SeComponent; });
/* harmony import */ var _SeDirective__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./SeDirective */ "./jsTarget/web/app/common/services/dependencyInjection/SeDirective.ts");
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");


/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeComponent
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit web component from a Depencency injection standpoint.
 * The controller alias will be $ctrl.
 * inherits properties from {@link smarteditServicesModule.object:@SeDirective}
 * @param {object} definition the component definition
 * @param {string?} definition.templateUrl the HTML file location for this component
 * @param {string?} definition.template the inline HTML template for this component
 * @param {object?} definition.entryComponents the array of {@link smarteditServicesModule.object:@SeComponent @SeComponent} that this new one requires.
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into the component.
 */
'se:smarteditcommons';
var SeComponent = function (definition) {
    return function (componentConstructor) {
        var component = {
            controller: componentConstructor,
            controllerAs: '$ctrl',
            transclude: true,
            bindings: Object(_SeDirective__WEBPACK_IMPORTED_MODULE_0__["parseDirectiveBindings"])(definition.inputs),
            require: definition.require
        };
        if (definition.templateUrl) {
            component.templateUrl = definition.templateUrl;
        }
        else if (definition.template) {
            component.template = definition.template;
        }
        var nameSet = Object(_SeDirective__WEBPACK_IMPORTED_MODULE_0__["parseDirectiveName"])(definition.selector, componentConstructor);
        if (nameSet.restrict !== "E") {
            var componentName = smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["functionsUtils"].getConstructorName(componentConstructor);
            throw new Error("component " + componentName + " declared a selector on class or attribute. version 1808 of Smartedit DI limits SeComponents to element selectors");
        }
        componentConstructor.componentName = nameSet.name;
        componentConstructor.definition = component;
        // will be browsed by owning @SeModule
        componentConstructor.entryComponents = definition.entryComponents;
        componentConstructor.providers = definition.providers;
        return componentConstructor;
    };
};
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.SeComponent = SeComponent;


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/SeDirective.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/SeDirective.ts ***!
  \*****************************************************************************/
/*! exports provided: parseDirectiveBindings, parseDirectiveName, SeDirective */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveBindings", function() { return parseDirectiveBindings; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveName", function() { return parseDirectiveName; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SeDirective", function() { return SeDirective; });
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony import */ var _DINameUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./DINameUtils */ "./jsTarget/web/app/common/services/dependencyInjection/DINameUtils.ts");


/** @internal */
var parseDirectiveBindings = function (inputs) {
    var bindings;
    if (inputs && inputs.length) {
        bindings = inputs.reduce(function (seed, element) {
            var values = element.replace(/\s/g, "").split(":");
            var bindingProperty = values[values.length - 1];
            if (!bindingProperty.startsWith("@")
                && !bindingProperty.startsWith("&")
                && !bindingProperty.startsWith("=")) {
                bindingProperty = '<' + bindingProperty;
            }
            seed[values[0]] = bindingProperty;
            return seed;
        }, {});
    }
    return bindings;
};
/** @internal */
var parseDirectiveName = function (selector, seContructor) {
    var attributeDirectiveNamePattern = /^\[([-\w]+)\]$/;
    var elementDirectiveNamePattern = /^([-\w]+)$/;
    var lodash = window.smarteditLodash;
    if (!selector) {
        return { name: _DINameUtils__WEBPACK_IMPORTED_MODULE_1__["diNameUtils"].buildComponentName(seContructor), restrict: "E" };
    }
    else if (selector.startsWith(".")) {
        return { name: lodash.camelCase(selector.substring(1)), restrict: "C" };
    }
    else if (attributeDirectiveNamePattern.test(selector)) {
        return { name: lodash.camelCase(attributeDirectiveNamePattern.exec(selector)[1]), restrict: "A" };
    }
    else if (elementDirectiveNamePattern.test(selector)) {
        return { name: lodash.camelCase(elementDirectiveNamePattern.exec(selector)[1]), restrict: "E" };
    }
    else {
        var directiveClassName = smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_0__["functionsUtils"].getConstructorName(seContructor);
        throw new Error("SeDirective " + directiveClassName + " declared an unexpected selector (" + selector + "). \n\t\tMake sure to use an element name or class (.class) or attribute ([attribute])");
    }
};
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeDirective
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit web directive from a Depencency injection standpoint.
 * This directive will have an isolated scope and will bind its properties to its controller
 * @param {object} definition the component definition
 * @param {string?} definition.selector The CSS selector that triggers the instantiation of a directive.
 * selector may be declared as one of the following:
 * <ul>
 * <li>element-name: select by element name.</li>
 * <li>.class: select by class name.</li>
 * <li>[attribute]: select by attribute name.</li>
 * </ul>
 * If no selector is set, will default to an element named as the lower camel case of the component class.
 * @param {string[]?} definition.inputs the array of input data binding
 * The inputs property defines a set of directiveProperty to bindingProperty configuration:
 * <ul>
 * <li>directiveProperty specifies the component property where the value is written.</li>
 * <li>bindingProperty specifies the binding type and/or the DOM property where the value is read from.</li>
 * binding type is legacy support for "@", "&" and "=" of Angular 1.x
 * </ul>
 * example: inputs: ['bankName', 'id: account-id']
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into the component.
 */
'se:smarteditcommons';
var SeDirective = function (definition) {
    return function (directiveConstructor) {
        var directive = {
            controller: directiveConstructor,
            scope: {},
            bindToController: parseDirectiveBindings(definition.inputs) || true,
            require: definition.require
        };
        var nameSet = parseDirectiveName(definition.selector, directiveConstructor);
        directive.restrict = nameSet.restrict;
        directiveConstructor.directiveName = nameSet.name;
        directiveConstructor.definition = directive;
        // will be browsed by owning @SeModule
        directiveConstructor.providers = definition.providers;
        return directiveConstructor;
    };
};
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.parseDirectiveBindings = parseDirectiveBindings;
window.smarteditcommons.parseDirectiveName = parseDirectiveName;
window.smarteditcommons.SeDirective = SeDirective;


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts":
/*!******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts ***!
  \******************************************************************************/
/*! exports provided: SeInjectable */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SeInjectable", function() { return SeInjectable; });
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
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeInjectable()
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit injectable service from a Dependency injection standpoint.
 * When multiple class annotations are used, {@link smarteditServicesModule.object:@SeInjectable() @SeInjectable()} must be closest to the class declaration.
 */
'se:smarteditcommons';
var SeInjectable = function () {
    return function (providerConstructor) {
        return providerConstructor;
    };
};
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.SeInjectable = SeInjectable;


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/SeModule.ts":
/*!**************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/SeModule.ts ***!
  \**************************************************************************/
/*! exports provided: SeModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SeModule", function() { return SeModule; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony import */ var _DINameUtils__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./DINameUtils */ "./jsTarget/web/app/common/services/dependencyInjection/DINameUtils.ts");
var __assign = (undefined && undefined.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
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
/* forbiddenNameSpaces angular.module:false */



var MultiProviderMap = {};
/**
 * @ngdoc object
 * @name smarteditServicesModule.object:@SeModule
 *
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit module from a Dependency injection standpoint.
 *
 * To create a configurable module, create a static method returning an SeModuleWithProvider object. The module
 * can then be imported by a parent module returning the SeModuleWithProvider object from the static method.
 *
 * @param {object} definition the module definition
 * @param {object} definition.declarations the array of {@link smarteditServicesModule.object:@SeDirective @SeDirective} and {@link smarteditServicesModule.object:@SeComponent @SeComponent} on which this new {@link smarteditServicesModule.object:@SeModule @SeModule} depends.
 * @param {object} definition.imports the array of modules on which this new module depends.
 * <br/> This is a mixed array of string (legacy approach) and {@link smarteditServicesModule.object:@SeModule @SeModule} annotated classes (recommended approach).
 * @param {object} definition.providers the list of {@link smarteditServicesModule.interface:SeClassProvider service classes},
 * {@link smarteditServicesModule.interface:SeFactoryProvider service factories}, {@link smarteditServicesModule.interface:SeValueProvider value},
 * or multi providers to be injected into this new module.
 * @param {Array} definition.config the injectable callback to be executed at configuration time
 * @param {Array} definition.initialize the injectable callback to be executed at startup time
 */
'se:smarteditcommons';
var SeModule = function (definition) {
    return function (moduleConstructor) {
        var seModuleName = _DINameUtils__WEBPACK_IMPORTED_MODULE_2__["diNameUtils"].buildName(moduleConstructor);
        var angularInstance = getAngular();
        var allImports = [];
        if (definition.imports) {
            definition.imports.forEach(function (importStatement, index) {
                var throwUnAnnotatedModuleError = function (seModule) {
                    var importedModule = _DINameUtils__WEBPACK_IMPORTED_MODULE_2__["diNameUtils"].buildName(seModule);
                    throw new Error(importedModule + " module was imported into " + seModuleName + " module but doesn't seem to have been @SeModule annotated");
                };
                var moduleName;
                if (typeof importStatement === 'string') {
                    moduleName = importStatement;
                }
                else if (typeof importStatement === 'function') {
                    moduleName = importStatement.moduleName;
                    if (!moduleName) {
                        throwUnAnnotatedModuleError(importStatement);
                    }
                }
                else if (importStatement && importStatement.seModule) {
                    if (!importStatement.seModule.moduleName) {
                        throwUnAnnotatedModuleError(importStatement.seModule);
                    }
                    var moduleWithProvidersName = _DINameUtils__WEBPACK_IMPORTED_MODULE_2__["diNameUtils"].buildName(importStatement.seModule);
                    var moduleWithProviders = angularInstance.module(moduleWithProvidersName);
                    moduleName = moduleWithProviders.name;
                    if (importStatement.providers) {
                        addArrayOfProvidersToModule(moduleWithProviders, importStatement.providers);
                    }
                }
                else {
                    throw new Error("the import statement " + importStatement + " at index " + index + " added to " + seModuleName + " is neither a legacy string nor an SeModuleConstructor");
                }
                if (allImports.indexOf(moduleName) > -1) {
                    throw new Error("module " + moduleName + " is imported more than once into " + seModuleName);
                }
                allImports.push(moduleName);
            });
        }
        var module = angularInstance.module(seModuleName, allImports);
        if (definition.providers) {
            addArrayOfProvidersToModule(module, definition.providers);
        }
        if (definition.declarations) {
            definition.declarations.forEach(function (comp) {
                addFullComponentGraphToModule(module, comp);
            });
        }
        if (definition.config) {
            module.config(definition.config);
        }
        if (definition.initialize) {
            module.run(definition.initialize);
        }
        moduleConstructor.moduleName = module.name;
        return moduleConstructor;
    };
};
function addArrayOfProvidersToModule(module, providers) {
    providers.forEach(function (provider, index) {
        var moduleName = module.name;
        if (!provider) {
            throw new Error("At the time a provider at index " + index + " was added to module " + moduleName + ",\n\t\t\t\tit was undefined, this is probably due to the path in your typescript import statement\n\t\t\t\treferencing a barrel file of an alias defined in a higher layer, consider using a relative path instead.");
        }
        if (provider.provide && !provider.useValue &&
            !provider.useClass && !provider.useFactory) {
            throw new Error("At the time a provider named " + provider.provide + " was added to module " + moduleName + "\n\t\t        did not provide an instance of SeValueProvider, SeClassProvider, or FactoryProvider.");
        }
        if (provider.multi) {
            provider = provider;
            addMultiProviderToModule(module, provider);
        }
        else {
            addProviderToModule(module, provider);
        }
    });
}
function addProviderToModule(module, provider) {
    if (provider.useValue) {
        provider = provider;
        module.constant(provider.provide, provider.useValue);
    }
    else if (provider.useClass) {
        provider = provider;
        module.service(provider.provide, provider.useClass);
    }
    else if (provider.useFactory) {
        provider = provider;
        var isNgAnnotated = Array.isArray(provider.useFactory) || provider.useFactory.$inject;
        if (isNgAnnotated && provider.deps) {
            throw Error("At the time a provider " + provider.provide + " uses ngInject annotations and \n\t\t\tSeFactoryProvider.deps at the same time. Please use one or the other.");
        }
        var dependencies = provider.deps ? provider.deps.map(function (dependency) {
            return typeof dependency === 'string' ? dependency : _DINameUtils__WEBPACK_IMPORTED_MODULE_2__["diNameUtils"].buildServiceName(dependency);
        }) : [];
        // In current framework, this is only needed for case of multi and for uglify ready di
        module.factory(provider.provide, isNgAnnotated ? provider.useFactory : dependencies.concat([provider.useFactory]));
    }
    else {
        provider = provider;
        var serviceName = _DINameUtils__WEBPACK_IMPORTED_MODULE_2__["diNameUtils"].buildServiceName(provider);
        module.service(serviceName, provider);
    }
}
function addMultiProviderToModule(module, provider) {
    var multiProviderMapName = module.name + provider.provide;
    var dependencies = MultiProviderMap[multiProviderMapName];
    if (!dependencies) {
        dependencies = [];
    }
    var multiProviderInstance = multiProviderMapName + smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["functionsUtils"].getLodash().uniqueId();
    dependencies.push(multiProviderInstance);
    MultiProviderMap[multiProviderMapName] = dependencies;
    var useFactory = function () {
        return [].slice.call(arguments);
    };
    addProviderToModule(module, __assign({}, provider, { provide: multiProviderInstance }));
    addProviderToModule(module, {
        provide: provider.provide,
        useFactory: useFactory,
        deps: dependencies
    });
}
function addFullComponentGraphToModule(module, component) {
    var definition = component.definition;
    if (!definition) {
        var componentConstructorName = smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["functionsUtils"].getConstructorName(component);
        throw new Error(componentConstructorName + " component was imported into " + module.name + " module but doesn't seem to have been @SeComponent or @SeDirective annotated");
    }
    if (component.providers) {
        addArrayOfProvidersToModule(module, component.providers);
    }
    var componentName = component.componentName;
    var directivename = component.directiveName;
    if (componentName) {
        module.component(componentName, definition);
        delete component.definition;
        var entryComponents = component.entryComponents;
        if (entryComponents) {
            entryComponents.forEach(function (entryComponent) {
                addFullComponentGraphToModule(module, entryComponent);
            });
        }
        delete component.entryComponents;
    }
    else if (directivename) {
        module.directive(directivename, function () { return definition; });
    }
}
// For testing purposes.
SeModule.getAngular = function () {
    return angular__WEBPACK_IMPORTED_MODULE_0__;
};
function getAngular() {
    return SeModule.getAngular();
}
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.SeModule = SeModule;
window.smarteditcommons.addArrayOfProvidersToModule = addArrayOfProvidersToModule;
window.smarteditcommons.addProviderToModule = addProviderToModule;
window.smarteditcommons.addMultiProviderToModule = addMultiProviderToModule;
window.smarteditcommons.addFullComponentGraphToModule = addFullComponentGraphToModule;
window.smarteditcommons.getAngular = getAngular;


/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/di.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/di.ts ***!
  \********************************************************************/
/*! exports provided: SeInjectable, SeComponent, parseDirectiveBindings, parseDirectiveName, SeDirective, SeModule, DINameUtils, diNameUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _SeInjectable__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./SeInjectable */ "./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeInjectable", function() { return _SeInjectable__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"]; });

/* harmony import */ var _SeComponent__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./SeComponent */ "./jsTarget/web/app/common/services/dependencyInjection/SeComponent.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeComponent", function() { return _SeComponent__WEBPACK_IMPORTED_MODULE_1__["SeComponent"]; });

/* harmony import */ var _SeDirective__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./SeDirective */ "./jsTarget/web/app/common/services/dependencyInjection/SeDirective.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveBindings", function() { return _SeDirective__WEBPACK_IMPORTED_MODULE_2__["parseDirectiveBindings"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveName", function() { return _SeDirective__WEBPACK_IMPORTED_MODULE_2__["parseDirectiveName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeDirective", function() { return _SeDirective__WEBPACK_IMPORTED_MODULE_2__["SeDirective"]; });

/* harmony import */ var _SeModule__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./SeModule */ "./jsTarget/web/app/common/services/dependencyInjection/SeModule.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeModule", function() { return _SeModule__WEBPACK_IMPORTED_MODULE_3__["SeModule"]; });

/* harmony import */ var _DINameUtils__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./DINameUtils */ "./jsTarget/web/app/common/services/dependencyInjection/DINameUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DINameUtils", function() { return _DINameUtils__WEBPACK_IMPORTED_MODULE_4__["DINameUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "diNameUtils", function() { return _DINameUtils__WEBPACK_IMPORTED_MODULE_4__["diNameUtils"]; });

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







/***/ }),

/***/ "./jsTarget/web/app/common/services/dependencyInjection/types.ts":
/*!***********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dependencyInjection/types.ts ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/services/deprecate.ts":
/*!*******************************************************!*\
  !*** ./jsTarget/web/app/common/services/deprecate.ts ***!
  \*******************************************************/
/*! exports provided: deprecatedSince1811, deprecate */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "deprecatedSince1811", function() { return deprecatedSince1811; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "deprecate", function() { return deprecate; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
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
/* forbiddenNameSpaces angular.module:false */

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditCommonsModule
 *
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 1808
 */
/* @internal */
var deprecatedSince1808 = function () {
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('eventServiceModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('crossFrameEventServiceModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('languageServiceModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('catalogServiceModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('gatewayFactoryModule', ['smarteditRootModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('gatewayProxyModule', ['smarteditRootModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('operationContextServiceModule', ['smarteditRootModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('compileHtmlModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('yMoreTextModule', ['smarteditCommonsModule']);
};
/*
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditCommonsModule
 *
 * IMPORTANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 1811
 */
/* @internal */
function deprecatedSince1811() {
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('permissionServiceInterfaceModule', ['smarteditCommonsModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('FetchDataHandlerInterfaceModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('fetchEnumDataHandlerModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('dateFormatterModule', ['dateTimePickerModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('DropdownPopulatorInterface', ['dropdownPopulatorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('optionsDropdownPopulatorModule', ['dropdownPopulatorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('uriDropdownPopulatorModule', ['dropdownPopulatorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('editorFieldMappingServiceModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('genericEditorStackServiceModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('genericEditorTabServiceModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('seValidationErrorParserModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('seValidationMessageParserModule', ['genericEditorServicesModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('seGenericEditorFieldMessagesModule', ['genericEditorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('genericEditorTabModule', ['genericEditorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('genericEditorFieldModule', ['genericEditorModule']);
    angular__WEBPACK_IMPORTED_MODULE_0__["module"]('authorizationModule', ['smarteditCommonsModule']);
}
var deprecate = function () {
    deprecatedSince1808();
    deprecatedSince1811();
};


/***/ }),

/***/ "./jsTarget/web/app/common/services/dragAndDrop/IDragAndDropScrollingService.ts":
/*!**************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dragAndDrop/IDragAndDropScrollingService.ts ***!
  \**************************************************************************************/
/*! exports provided: IDragEventType */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IDragEventType", function() { return IDragEventType; });
var IDragEventType = {
    DROP: 'drop',
    DRAG_ENTER: 'dragenter',
    DRAG_OVER: 'dragover',
    DRAG_LEAVE: 'dragleave'
};


/***/ }),

/***/ "./jsTarget/web/app/common/services/dragAndDrop/InViewElementObserver.ts":
/*!*******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dragAndDrop/InViewElementObserver.ts ***!
  \*******************************************************************************/
/*! exports provided: InViewElementObserver */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "InViewElementObserver", function() { return InViewElementObserver; });
/* harmony import */ var smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services */ "./jsTarget/web/app/common/services/index.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var IN_VIEW_ELEMENTS_INTERSECTION_OBSERVER_OPTIONS = {
    // The root to use for intersection.
    // If not provided, use the top-level document’s viewport.
    root: null,
    // Threshold(s) at which to trigger callback, specified as a ratio, or list of
    // ratios, of (visible area / total area) of the observed element (hence all
    // entries must be in the range [0, 1]). Callback will be invoked when the visible
    // ratio of the observed element crosses a threshold in the list.
    threshold: 0
};
/*
 * Of all the DOM node types we only care for 1 and 2 (Element and Attributes)
 */
var NODE_TYPES = {
    ELEMENT: 1,
    ATTRIBUTE: 2,
    TEXT: 3
};
/*
* This is the configuration passed to the MutationObserver instance
*/
var IN_VIEW_ELEMENTS_MUTATION_OBSERVER_OPTIONS = {
    /*
        * diables observation of attribute mutations
        */
    attributes: false,
    /*
        * instruct the observer not to keep in store the former values of the mutated attributes
        */
    attributeOldValue: false,
    /*
        * enables observation of addition and removal of nodes
        */
    childList: true,
    characterData: false,
    /*
        * enables recursive lookup without which only addition and removal of DIRECT children of the observed DOM root would be collected
        */
    subtree: true
};
/**
 * @ngdoc service
 * @name smarteditServicesModule.service:InViewElementObserver
 * @description
 * InViewElementObserver maintains a collection of eligible DOM elements considered "in view".
 * <br/>An element is considered eligible if matches at least one of the selectors passed to the service.
 * <br/>An eligible element is in view when and only when it intersects with the view port of the window frame.
 * <br/>This services provides as well convenience methods around "in view" components:
 */
var InViewElementObserver = /** @class */ (function () {
    /** @internal */
    function InViewElementObserver($log, $document, compareHTMLElementsPosition, lodash, yjQuery, isBlank, isPointOverElement) {
        this.$log = $log;
        this.$document = $document;
        this.compareHTMLElementsPosition = compareHTMLElementsPosition;
        this.lodash = lodash;
        this.yjQuery = yjQuery;
        this.isBlank = isBlank;
        this.isPointOverElement = isPointOverElement;
        /*
         * Queue used to process components when intersecting the viewport
         * {Array.<{isIntersecting: Boolean, parent: DOMElement, processed: COMPONENT_STATE}>}
         */
        this.componentsQueue = [];
        this.selectors = [];
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:InViewElementObserver#elementFromPoint
     * @methodOf smarteditServicesModule.service:InViewElementObserver
     * @description
     * Retrieves the element targeted by the given mousePosition.
     * <br/>On some browsers, the native Javascript API will not work when targeting
     * an element inside an iframe from the container if a container overlay blocks it.
     * <br/>In such case we resort to returning the targeted element amongst the list of "in view" elements
     * @param {IMousePosition} mousePosition the fixed {@link smarteditServicesModule.object:IMousePosition coordinates} of the pointer
     */
    InViewElementObserver.prototype.elementFromPoint = function (mousePosition) {
        var _this = this;
        var elementFromPointThroughNativeAPI = document.elementFromPoint(mousePosition.x, mousePosition.y);
        // we might potentially have an issue here if a browser has an intersection observer and document.elementFromPoint returns null.
        // Chrome version 66 when running in isE2EMode has the issue. But this is not likely to happen in reality because Chrome has an intersectionObserver
        // and hence it should just use the result from document.elementFromPoint.
        return elementFromPointThroughNativeAPI || this.getInViewElements()
            .find(function (component) {
            return _this.isPointOverElement(mousePosition, component);
        });
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:InViewElementObserver#addSelector
     * @methodOf smarteditServicesModule.service:InViewElementObserver
     * @description
     * Declares a new yjQuery selector in order to observe more elements.
     * @param {string[]} selector a {@link https://jquery.com jquery} selector
     */
    InViewElementObserver.prototype.addSelector = function (selector) {
        if (!this.isBlank(selector) && this.selectors.indexOf(selector) === -1) {
            this.selectors.push(selector);
            this.restart();
        }
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:InViewElementObserver#getAllElements
     * @methodOf smarteditServicesModule.service:InViewElementObserver
     * @description
     * Retrieves the full list of eligible DOM elements even if they are not "in view".
     * @return {Element[]} An array of DOM elements
     */
    InViewElementObserver.prototype.getAllElements = function () {
        return this.componentsQueue
            .map(function (element) { return element.component; });
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:InViewElementObserver#getInViewElements
     * @methodOf smarteditServicesModule.service:InViewElementObserver
     * @description
     * Retrieves the list of currently "in view" DOM elements.
     * @return {Element[]} An array of DOM elements
     */
    InViewElementObserver.prototype.getInViewElements = function () {
        return this.componentsQueue
            .filter(function (element) { return element.isIntersecting; })
            .map(function (element) { return element.component; });
    };
    InViewElementObserver.prototype.restart = function () {
        this.stopListener();
        this.initListener();
    };
    /*
     * stops and clean up all listeners
     */
    InViewElementObserver.prototype.stopListener = function () {
        // Stop listening for DOM mutations
        if (this.mutationObserver) {
            this.mutationObserver.disconnect();
            delete this.mutationObserver;
        }
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
            delete this.intersectionObserver;
        }
        this.componentsQueue = [];
    };
    /*
     * initializes and starts all Intersection/DOM listeners:
     * - Intersection of eligible components with the viewport
     * - DOM mutations on eligible components (by Means of native MutationObserver)
     */
    InViewElementObserver.prototype.initListener = function () {
        var _this = this;
        if (!this.mutationObserver) {
            this.mutationObserver = this._newMutationObserver(this._mutationObserverCallback.bind(this));
            if (!this.intersectionObserver) {
                // Intersection Observer is used to observe intersection of components with the viewport.
                // each time the 'isIntersecting' property of an entry changes, the Intersection Callback is called.
                // we are using the componentsQueue to hold the components references and their isIntersecting value.
                this.intersectionObserver = this._newIntersectionObserver(function (entries) {
                    entries
                        .filter(function (entry) { return _this._isEligibleComponent(entry.target); })
                        .forEach(function (entry) {
                        _this._updateQueue(entry);
                    });
                });
            }
            // Observing all eligible components that are already in the page.
            // Note that when an element visible in the viewport is removed, the Intersection Callback is called so we don't need to use the Mutation Observe to oberser removal of Nodes.
            this._getEligibleElements().forEach(function (component) {
                _this.intersectionObserver.observe(component);
            });
        }
    };
    /*
     * Method used in mutationObserverCallback that extracts from mutations the list of added and removed nodes
     */
    InViewElementObserver.prototype._aggregateAddedOrRemovedNodes = function (mutations, addedOnes) {
        var _this = this;
        var entries = this.lodash.flatten(mutations.filter(function (mutation) {
            // only keep mutations of type childList and addedNodes
            return mutation.type === "childList"
                && ((!!addedOnes && mutation.addedNodes && mutation.addedNodes.length)
                    || (!addedOnes && mutation.removedNodes && mutation.removedNodes.length));
        }).map(function (mutation) {
            var children = _this.lodash.flatten(Array.prototype.slice.call(addedOnes ? mutation.addedNodes : mutation.removedNodes)
                .filter(function (node) {
                return node.nodeType === NODE_TYPES.ELEMENT;
            })
                .filter(function (node) { return _this._isEligibleComponent(node); })
                .map(function (child) {
                return [child].concat(_this._getAllEligibleChildren(child));
            }))
                .sort(_this.compareHTMLElementsPosition())
                // so that in case of nested eligible components the deeper element is picked
                .reverse();
            return children;
        }));
        /*
         * Despite MutationObserver specifications it so happens that sometimes,
         * depending on the very way a parent node is added with its children,
         * parent AND children will appear in a same mutation. We then must only keep the parent
         * Since the parent will appear first, the filtering lodash.uniqWith will always return the parent as opposed to the child which is what we need
         */
        return this.lodash.uniqWith(entries, function (entry1, entry2) {
            return entry1.contains(entry2) || entry2.contains(entry1);
        });
    };
    /*
     * callback executed by the mutation observer every time mutations occur.
     * repositioning and resizing are not part of this except that every time a eligible component is added,
     * it is registered within the positionRegistry and the resizeListener
     */
    InViewElementObserver.prototype._mutationObserverCallback = function (mutations) {
        var _this = this;
        this.$log.debug(mutations);
        this._aggregateAddedOrRemovedNodes(mutations, true).forEach(function (node) {
            _this.intersectionObserver.observe(node);
        });
        this._aggregateAddedOrRemovedNodes(mutations, false).forEach(function (node) {
            var componentIndex = _this._getComponentIndexInQueue(node);
            if (componentIndex !== -1) {
                _this.componentsQueue.splice(componentIndex, 1);
            }
        });
    };
    /*
     * Add the given entry to the componentsQueue
     * The components in the queue are sorted according to their position in the DOM
     * so that the adding of components is done to have parents before children
     */
    InViewElementObserver.prototype._updateQueue = function (entry) {
        var componentIndex = this._getComponentIndexInQueue(entry.target);
        if (componentIndex !== -1) {
            if (!entry.intersectionRatio && !this._isInDOM(entry.target)) {
                this.componentsQueue.splice(componentIndex, 1);
            }
            else {
                this.componentsQueue[componentIndex].isIntersecting = !!entry.intersectionRatio;
            }
        }
        else if (this._isInDOM(entry.target)) { // may have been removed by competing MutationObserver hence showign here but not intersecting
            this.componentsQueue.push({
                component: entry.target,
                isIntersecting: !!entry.intersectionRatio
            });
        }
    };
    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// HELPER METHODS //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    /*
     * wrapping for test purposes
     */
    InViewElementObserver.prototype._newMutationObserver = function (callback) {
        var mutationObserver = new MutationObserver(callback);
        mutationObserver.observe(document.body, IN_VIEW_ELEMENTS_MUTATION_OBSERVER_OPTIONS);
        return mutationObserver;
    };
    /*
     * wrapping for test purposes
     */
    InViewElementObserver.prototype._newIntersectionObserver = function (callback) {
        return new IntersectionObserver(callback, IN_VIEW_ELEMENTS_INTERSECTION_OBSERVER_OPTIONS);
    };
    InViewElementObserver.prototype._getJQuerySelector = function () {
        return this.selectors.join(",");
    };
    InViewElementObserver.prototype._isEligibleComponent = function (component) {
        return this.yjQuery(component).is(this._getJQuerySelector());
    };
    InViewElementObserver.prototype._getEligibleElements = function () {
        return Array.prototype.slice.call(this.yjQuery(this._getJQuerySelector()));
    };
    InViewElementObserver.prototype._getAllEligibleChildren = function (component) {
        return Array.prototype.slice.call(this.yjQuery(component).find(this._getJQuerySelector()));
    };
    InViewElementObserver.prototype._getComponentIndexInQueue = function (component) {
        return this.componentsQueue.findIndex(function (obj) {
            return component === obj.component;
        });
    };
    InViewElementObserver.prototype._isInDOM = function (component) {
        return this.yjQuery.contains(this.$document[0], component);
    };
    InViewElementObserver = __decorate([
        Object(smarteditcommons_services__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], InViewElementObserver);
    return InViewElementObserver;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/dragAndDrop/index.ts":
/*!***************************************************************!*\
  !*** ./jsTarget/web/app/common/services/dragAndDrop/index.ts ***!
  \***************************************************************/
/*! exports provided: IDragEventType, InViewElementObserver */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _IDragAndDropScrollingService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./IDragAndDropScrollingService */ "./jsTarget/web/app/common/services/dragAndDrop/IDragAndDropScrollingService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IDragEventType", function() { return _IDragAndDropScrollingService__WEBPACK_IMPORTED_MODULE_0__["IDragEventType"]; });

/* harmony import */ var _InViewElementObserver__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./InViewElementObserver */ "./jsTarget/web/app/common/services/dragAndDrop/InViewElementObserver.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InViewElementObserver", function() { return _InViewElementObserver__WEBPACK_IMPORTED_MODULE_1__["InViewElementObserver"]; });

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




/***/ }),

/***/ "./jsTarget/web/app/common/services/flaws/FlawInjectionInterceptor.ts":
/*!****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/flaws/FlawInjectionInterceptor.ts ***!
  \****************************************************************************/
/*! exports provided: FlawInjectionInterceptor */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlawInjectionInterceptor", function() { return FlawInjectionInterceptor; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/*
 * interceptor that will inject flaw into outbound and inbound http calls.
 * It is mainly used to validate reliability and consitency of test frameworks
 */
/** @internal */
var FlawInjectionInterceptor = /** @class */ (function () {
    function FlawInjectionInterceptor($log, interceptorHelper) {
        this.$log = $log;
        this.interceptorHelper = interceptorHelper;
        this.requestMutations = [];
        this.responseMutations = [];
        this.flawWindow = window;
        this.flawWindow.allRequests = 0;
        this.flawWindow.flawedRequests = 0;
        this.flawWindow.allResponses = 0;
        this.flawWindow.flawedResponses = 0;
        this.request = this.request.bind(this);
        this.response = this.response.bind(this);
    }
    FlawInjectionInterceptor_1 = FlawInjectionInterceptor;
    FlawInjectionInterceptor.prototype.registerRequestFlaw = function (mutation) {
        this.requestMutations.push(mutation);
    };
    FlawInjectionInterceptor.prototype.registerResponseFlaw = function (mutation) {
        this.responseMutations.push(mutation);
    };
    FlawInjectionInterceptor.prototype.request = function (config) {
        var _this = this;
        if (FlawInjectionInterceptor_1.PROBABILITY !== 0 && this._isCRUDRequest(config) && !this._isGET(config)) {
            return this.interceptorHelper.handleRequest(config, function () {
                _this.flawWindow.allRequests++;
                if (_this._activateWithProbability(FlawInjectionInterceptor_1.PROBABILITY)) {
                    _this.flawWindow.flawedRequests++;
                    var requestMutation = _this.requestMutations.find(function (mutation) { return mutation.test(config); });
                    if (requestMutation) {
                        config = requestMutation.mutate(config);
                        _this.$log.error("FLAWED REQUEST-\"" + config.url);
                    }
                }
                return config;
            });
        }
        else {
            return config;
        }
    };
    FlawInjectionInterceptor.prototype.response = function (response) {
        if (FlawInjectionInterceptor_1.PROBABILITY !== 0 && this._isCRUDResponse(response) && !this._isGET(response.config)) {
            this.flawWindow.allResponses++;
            if (this._activateWithProbability(FlawInjectionInterceptor_1.PROBABILITY)) {
                this.flawWindow.flawedResponses++;
                var responseMutation = this.responseMutations.find(function (mutation) { return mutation.test(response.config); });
                if (responseMutation) {
                    response = responseMutation.mutate(response);
                    this.$log.error("FLAWED RESPONSE-\"" + response.config.url);
                }
            }
            return response;
        }
        else {
            return response;
        }
    };
    FlawInjectionInterceptor.prototype._isCRUDRequest = function (config) {
        return config.url
            && (config.url.indexOf(".html") === -1
                && config.url.indexOf(".js") === -1);
    };
    FlawInjectionInterceptor.prototype._isCRUDResponse = function (response) {
        return response.config && response.config.url
            && response.config.url.indexOf(".html") === -1
            && response.headers('Content-Type')
            && response.headers('Content-Type').indexOf("json") > -1;
    };
    FlawInjectionInterceptor.prototype._isGET = function (config) {
        return config.method === "GET";
    };
    FlawInjectionInterceptor.prototype._activateWithProbability = function (probabilityTrue) {
        return Math.random() >= 1.0 - probabilityTrue;
    };
    var FlawInjectionInterceptor_1;
    /*
     * probability of flaw occurrence ranging from 0 to 1
     */
    FlawInjectionInterceptor.PROBABILITY = 0;
    FlawInjectionInterceptor = FlawInjectionInterceptor_1 = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], FlawInjectionInterceptor);
    return FlawInjectionInterceptor;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/flaws/flawInjectionInterceptorModule.ts":
/*!**********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/flaws/flawInjectionInterceptorModule.ts ***!
  \**********************************************************************************/
/*! exports provided: FlawInjectionInterceptorModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlawInjectionInterceptorModule", function() { return FlawInjectionInterceptorModule; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var _FlawInjectionInterceptor__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./FlawInjectionInterceptor */ "./jsTarget/web/app/common/services/flaws/FlawInjectionInterceptor.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};


/** @internal */
var FlawInjectionInterceptorModule = /** @class */ (function () {
    function FlawInjectionInterceptorModule() {
    }
    FlawInjectionInterceptorModule = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeModule"])({
            imports: [
                'interceptorHelperModule'
            ],
            providers: [_FlawInjectionInterceptor__WEBPACK_IMPORTED_MODULE_1__["FlawInjectionInterceptor"]],
            config: function ($httpProvider) {
                'ngInject';
                $httpProvider.interceptors.push(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["diNameUtils"].buildServiceName(_FlawInjectionInterceptor__WEBPACK_IMPORTED_MODULE_1__["FlawInjectionInterceptor"]));
            },
            initialize: function (flawInjectionInterceptor) {
                'ngInject';
                // mutates sites id
                flawInjectionInterceptor.registerRequestFlaw({
                    test: function (config) { return /sites\/[\w-]+\//.test(config.url); },
                    mutate: function (config) {
                        config.url = config.url.replace(/sites\/([\w-]+)\//, "sites/" + Math.random() + "/");
                        return config;
                    }
                });
            }
        })
    ], FlawInjectionInterceptorModule);
    return FlawInjectionInterceptorModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/forcedImport.ts":
/*!**********************************************************!*\
  !*** ./jsTarget/web/app/common/services/forcedImport.ts ***!
  \**********************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var smarteditcommons_services_dependencyInjection_types__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/types */ "./jsTarget/web/app/common/services/dependencyInjection/types.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_types__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_dependencyInjection_types__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var smarteditcommons_services_dependencyInjection_ISeComponent__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/ISeComponent */ "./jsTarget/web/app/common/services/dependencyInjection/ISeComponent.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_ISeComponent__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_dependencyInjection_ISeComponent__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var smarteditcommons_services_crossFrame_CrossFrameEventService__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/services/crossFrame/CrossFrameEventService */ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IAlertService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IAlertService */ "./jsTarget/web/app/common/services/interfaces/IAlertService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IBrowserService__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IBrowserService */ "./jsTarget/web/app/common/services/interfaces/IBrowserService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IBrowserService__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IBrowserService__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var smarteditcommons_services_interfaces_ICatalogService__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! smarteditcommons/services/interfaces/ICatalogService */ "./jsTarget/web/app/common/services/interfaces/ICatalogService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IContextualMenuButton__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IContextualMenuButton */ "./jsTarget/web/app/common/services/interfaces/IContextualMenuButton.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IContextualMenuButton__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IContextualMenuButton__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var smarteditcommons_services_interfaces_IContextualMenuConfiguration__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IContextualMenuConfiguration */ "./jsTarget/web/app/common/services/interfaces/IContextualMenuConfiguration.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IContextualMenuConfiguration__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IContextualMenuConfiguration__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var smarteditcommons_services_interfaces_IDecorator__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IDecorator */ "./jsTarget/web/app/common/services/interfaces/IDecorator.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IDecorator__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IDecorator__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var smarteditcommons_services_interfaces_IExperience__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IExperience */ "./jsTarget/web/app/common/services/interfaces/IExperience.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IExperience__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IExperience__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var smarteditcommons_services_interfaces_IFeature__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IFeature */ "./jsTarget/web/app/common/services/interfaces/IFeature.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IFeature__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IFeature__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var smarteditcommons_services_interfaces_IFeatureService__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IFeatureService */ "./jsTarget/web/app/common/services/interfaces/IFeatureService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IModalService__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IModalService */ "./jsTarget/web/app/common/services/interfaces/IModalService.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IModalService__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IModalService__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var smarteditcommons_services_interfaces_IPrioritized__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IPrioritized */ "./jsTarget/web/app/common/services/interfaces/IPrioritized.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IPrioritized__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IPrioritized__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var smarteditcommons_services_interfaces_IReflectable__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IReflectable */ "./jsTarget/web/app/common/services/interfaces/IReflectable.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IReflectable__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IReflectable__WEBPACK_IMPORTED_MODULE_14__);
/* harmony import */ var smarteditcommons_services_rest_IRestService__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! smarteditcommons/services/rest/IRestService */ "./jsTarget/web/app/common/services/rest/IRestService.ts");
/* harmony import */ var smarteditcommons_services_rest_IRestService__WEBPACK_IMPORTED_MODULE_15___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_rest_IRestService__WEBPACK_IMPORTED_MODULE_15__);
/* harmony import */ var smarteditcommons_services_rest_IRestServiceFactory__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! smarteditcommons/services/rest/IRestServiceFactory */ "./jsTarget/web/app/common/services/rest/IRestServiceFactory.ts");
/* harmony import */ var smarteditcommons_services_rest_IRestServiceFactory__WEBPACK_IMPORTED_MODULE_16___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_rest_IRestServiceFactory__WEBPACK_IMPORTED_MODULE_16__);
/* harmony import */ var smarteditcommons_services_interfaces_IToolbarItem__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IToolbarItem */ "./jsTarget/web/app/common/services/interfaces/IToolbarItem.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IToolbarItem__WEBPACK_IMPORTED_MODULE_17___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IToolbarItem__WEBPACK_IMPORTED_MODULE_17__);
/* harmony import */ var smarteditcommons_services_interfaces_IUriContext__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IUriContext */ "./jsTarget/web/app/common/services/interfaces/IUriContext.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IUriContext__WEBPACK_IMPORTED_MODULE_18___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IUriContext__WEBPACK_IMPORTED_MODULE_18__);
/* harmony import */ var smarteditcommons_services_interfaces_IURIBuilder__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! smarteditcommons/services/interfaces/IURIBuilder */ "./jsTarget/web/app/common/services/interfaces/IURIBuilder.ts");
/* harmony import */ var smarteditcommons_services_interfaces_IURIBuilder__WEBPACK_IMPORTED_MODULE_19___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_services_interfaces_IURIBuilder__WEBPACK_IMPORTED_MODULE_19__);
/* harmony import */ var smarteditcommons_services_SystemEventService__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! smarteditcommons/services/SystemEventService */ "./jsTarget/web/app/common/services/SystemEventService.ts");
/* harmony import */ var smarteditcommons_services_wizard_WizardServiceModule__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! smarteditcommons/services/wizard/WizardServiceModule */ "./jsTarget/web/app/common/services/wizard/WizardServiceModule.ts");
/* harmony import */ var smarteditcommons_modules_translations_translationServiceModule__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! smarteditcommons/modules/translations/translationServiceModule */ "./jsTarget/web/app/common/modules/translations/translationServiceModule.ts");
/* harmony import */ var smarteditcommons_components_yDropdown_yDropDownMenu_IYDropdownMenuItem__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! smarteditcommons/components/yDropdown/yDropDownMenu/IYDropdownMenuItem */ "./jsTarget/web/app/common/components/yDropdown/yDropDownMenu/IYDropdownMenuItem.ts");
/* harmony import */ var smarteditcommons_components_yDropdown_yDropDownMenu_IYDropdownMenuItem__WEBPACK_IMPORTED_MODULE_23___default = /*#__PURE__*/__webpack_require__.n(smarteditcommons_components_yDropdown_yDropDownMenu_IYDropdownMenuItem__WEBPACK_IMPORTED_MODULE_23__);
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
 * We are doing forced imports in order to generate the types (d.ts) of below interfaces or classes correctly.
 * If we don't include the below imports, as a part of webpack tree shaking, the types will not be generated.
 * There is an open issue in typescript github regarding forced imports
 * https://github.com/Microsoft/TypeScript/issues/9191
 * https://github.com/Microsoft/TypeScript/wiki/FAQ#why-are-imports-being-elided-in-my-emit
 *
 * If an interface X extends an interface Y, make sure X has all types it needs from Y by checking index.d.ts, if not, do force import of X and Y.
 */


























/***/ }),

/***/ "./jsTarget/web/app/common/services/gateway/GatewayFactory.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/gateway/GatewayFactory.ts ***!
  \********************************************************************/
/*! exports provided: GatewayFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GatewayFactory", function() { return GatewayFactory; });
/* harmony import */ var _MessageGateway__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./MessageGateway */ "./jsTarget/web/app/common/services/gateway/MessageGateway.ts");
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};


/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:GatewayFactory
 *
 * @description
 * The Gateway Factory controls the creation of and access to {@link smarteditCommonsModule.service:MessageGateway MessageGateway}
 * instances.
 *
 * To construct and access a gateway, you must use the GatewayFactory's createGateway method and provide the channel
 * ID as an argument. If you try to create the same gateway twice, the second call will return a null.
 */
var GatewayFactory = /** @class */ (function () {
    /** @internal */
    function GatewayFactory($q, $log, $window, $injector, $timeout, systemEventService, cloneableUtils, getOrigin, regExpFactory, isIframe, windowUtils, WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY, TIMEOUT_TO_RETRY_PUBLISHING) {
        this.$q = $q;
        this.$log = $log;
        this.$window = $window;
        this.$injector = $injector;
        this.$timeout = $timeout;
        this.systemEventService = systemEventService;
        this.cloneableUtils = cloneableUtils;
        this.getOrigin = getOrigin;
        this.regExpFactory = regExpFactory;
        this.isIframe = isIframe;
        this.windowUtils = windowUtils;
        this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY = WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY;
        this.TIMEOUT_TO_RETRY_PUBLISHING = TIMEOUT_TO_RETRY_PUBLISHING;
        this.messageGatewayMap = {};
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:GatewayFactory#initListener
     * @methodOf smarteditCommonsModule.service:GatewayFactory
     *
     * @description
     * Initializes a postMessage event handler that dispatches the handling of an event to the specified gateway.
     * If the corresponding gateway does not exist, an error is logged.
     */
    GatewayFactory.prototype.initListener = function () {
        var _this = this;
        var processedPrimaryKeys = [];
        // Listen to message from child window
        this.$window.addEventListener('message', function (e) {
            if (_this._isAllowed(e.origin)) {
                // add control on e.origin
                var event_1 = e.data;
                if (processedPrimaryKeys.indexOf(event_1.pk) > -1) {
                    return;
                }
                processedPrimaryKeys.push(event_1.pk);
                _this.$log.debug('message event handler called', event_1.eventId);
                var gatewayId = event_1.gatewayId;
                var gateway = _this.messageGatewayMap[gatewayId];
                if (!gateway) {
                    _this.$log.debug('Incoming message on gateway ' + gatewayId + ', but no destination exists.');
                    return;
                }
                gateway.processEvent(event_1);
            }
            else {
                _this.$log.error("disallowed storefront is trying to communicate with smarteditcontainer");
            }
        }, false);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:GatewayFactory#createGateway
     * @methodOf smarteditCommonsModule.service:GatewayFactory
     *
     * @description
     * Creates a gateway for the specified gateway identifier and caches it in order to handle postMessage events
     * later in the application lifecycle. This method will fail on subsequent calls in order to prevent two
     * clients from using the same gateway.
     *
     * @param {String} gatewayId The identifier of the gateway.
     * @returns {MessageGateway} Returns the newly created Message Gateway or null.
     */
    GatewayFactory.prototype.createGateway = function (gatewayId) {
        if (this.messageGatewayMap[gatewayId]) {
            this.$log.error('Message Gateway for ' + gatewayId + ' already reserved');
            return null;
        }
        this.messageGatewayMap[gatewayId] = new _MessageGateway__WEBPACK_IMPORTED_MODULE_0__["MessageGateway"](this.$q, this.$log, this.$timeout, this.systemEventService, this.cloneableUtils, this.windowUtils, this.TIMEOUT_TO_RETRY_PUBLISHING, gatewayId);
        return this.messageGatewayMap[gatewayId];
    };
    /**
     * allowed if receiving end is frame or [container + (white listed storefront or same origin)]
     */
    GatewayFactory.prototype._isAllowed = function (origin) {
        var _this = this;
        var whiteListedStorefronts = this.$injector.has(this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY) ?
            this.$injector.get(this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY) : [];
        return this.isIframe() || this.getOrigin() === origin || (whiteListedStorefronts.some(function (allowedURI) {
            return _this.regExpFactory(allowedURI).test(origin);
        }));
    };
    GatewayFactory = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"])()
        /* @ngInject */
    ], GatewayFactory);
    return GatewayFactory;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/gateway/GatewayProxy.ts":
/*!******************************************************************!*\
  !*** ./jsTarget/web/app/common/services/gateway/GatewayProxy.ts ***!
  \******************************************************************/
/*! exports provided: GatewayProxy */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GatewayProxy", function() { return GatewayProxy; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:GatewayProxy
 *
 * @description
 * To seamlessly integrate the gateway factory between two services on different frames, you can use a gateway
 * proxy. The gateway proxy service simplifies using the gateway module by providing an API that registers an
 * instance of a service that requires a gateway for communication.
 *
 * This registration process automatically attaches listeners to each of the service's functions (turned into promises), allowing stub
 * instances to forward calls to these functions using an instance of a gateway from {@link
 * smarteditCommonsModule.service:GatewayFactory gatewayFactory}. Any function that has an empty body declared on the service is used
 * as a proxy function. It delegates a publish call to the gateway under the same function name, and wraps the result
 * of the call in a Promise.
 */
var GatewayProxy = /** @class */ (function () {
    function GatewayProxy($log, $q, toPromise, isBlank, functionsUtils, gatewayFactory) {
        this.$log = $log;
        this.$q = $q;
        this.toPromise = toPromise;
        this.isBlank = isBlank;
        this.functionsUtils = functionsUtils;
        this.gatewayFactory = gatewayFactory;
        this.nonProxiableMethods = ['getMethodForVoid', 'getMethodForSingleInstance', 'getMethodForArray'];
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:GatewayProxy#initForService
     * @methodOf smarteditCommonsModule.service:GatewayProxy
     *
     * @description Mutates the given service into a proxied service.
     * You must provide a unique string gatewayId, in one of 2 ways.<br />
     * 1) Having a gatewayId property on the service provided<br />
     * OR<br />
     * 2) providing a gatewayId as 3rd param of this function<br />
     *
     * @param {any} service Service to mutate into a proxied service.
     * @param {String[]=} methodsSubset An explicit set of methods on which the gatewayProxy will trigger. Otherwise, by default all functions will be proxied. This is particularly useful to avoid inner methods being unnecessarily turned into promises.
     * @param {String=} gatewayId The gateway ID to use internaly for the proxy. If not provided, the service <strong>must<strong> have a gatewayId property.
     */
    GatewayProxy.prototype.initForService = function (service, methodsSubset, gatewayId) {
        var _this = this;
        var gwId = gatewayId || service.gatewayId;
        if (!gwId) {
            this.$log.error("initForService() - service expected to have an associated gatewayId - methodsSubset: " + (methodsSubset && methodsSubset.length ? methodsSubset.join(',') : []));
            return null;
        }
        var gateway = this.gatewayFactory.createGateway(gwId);
        var loopedOver = methodsSubset;
        if (!loopedOver) {
            loopedOver = [];
            for (var key in service) {
                if (typeof service[key] === 'function' && !this._isNonProxiableMethod(key)) {
                    loopedOver.push(key);
                }
            }
        }
        loopedOver.forEach(function (fnName) {
            if (typeof service[fnName] === 'function') {
                if (_this.functionsUtils.isEmpty(service[fnName])) {
                    _this._turnToProxy(fnName, service, gateway);
                }
                else {
                    service[fnName] = _this.toPromise(service[fnName], service);
                    gateway.subscribe(fnName, _this._onGatewayEvent.bind(null, fnName, service));
                }
            }
        });
    };
    GatewayProxy.prototype._isNonProxiableMethod = function (key) {
        return this.nonProxiableMethods.indexOf(key) > -1 || key.startsWith('$') || key === 'lodash' || key === 'jQuery';
    };
    GatewayProxy.prototype._onGatewayEvent = function (fnName, service, eventId, data) {
        return service[fnName].apply(service, data.arguments);
    };
    GatewayProxy.prototype._turnToProxy = function (fnName, service, gateway) {
        var _this = this;
        delete service[fnName];
        service[fnName] = (function () {
            var args = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                args[_i] = arguments[_i];
            }
            return gateway.publish(fnName, {
                arguments: args
            }).then(function (resolvedData) {
                if (!_this.isBlank(resolvedData)) {
                    delete resolvedData.$resolved;
                    delete resolvedData.$promise;
                }
                return resolvedData;
            }, function (error) {
                if (error) {
                    _this.$log.error("gatewayProxy - publish failed for gateway " + gateway.gatewayId + " method " + fnName + " and arguments " + args);
                }
                return _this.$q.reject(error);
            });
        });
    };
    GatewayProxy = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], GatewayProxy);
    return GatewayProxy;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/gateway/MessageGateway.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/gateway/MessageGateway.ts ***!
  \********************************************************************/
/*! exports provided: MessageGateway */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MessageGateway", function() { return MessageGateway; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name smarteditCommonsModule.service:MessageGateway
 *
 * @description
 * The Message Gateway is a private channel that is used to publish and subscribe to events across iFrame
 * boundaries. The gateway uses the W3C-compliant postMessage as its underlying technology. The benefits of
 * the postMessage are that:
 * <ul>
 *     <li>It works in cross-origin scenarios.</li>
 *     <li>The receiving end can reject messages based on their origins.</li>
 * </ul>
 *
 * The creation of instances is controlled by the {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory}. Only one
 * instance can exist for each gateway ID.
 *
 * @param {String} gatewayId The channel identifier
 * @constructor
 */
var MessageGateway = /** @class */ (function () {
    /** @internal */
    function MessageGateway($q, $log, $timeout, systemEventService, cloneableUtils, windowUtils, TIMEOUT_TO_RETRY_PUBLISHING, gatewayId) {
        this.$q = $q;
        this.$log = $log;
        this.$timeout = $timeout;
        this.systemEventService = systemEventService;
        this.cloneableUtils = cloneableUtils;
        this.windowUtils = windowUtils;
        this.TIMEOUT_TO_RETRY_PUBLISHING = TIMEOUT_TO_RETRY_PUBLISHING;
        this.gatewayId = gatewayId;
        this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID = 'promiseAcknowledgement';
        this.PROMISE_RETURN_EVENT_ID = 'promiseReturn';
        this.SUCCESS = 'success';
        this.FAILURE = 'failure';
        this.MAX_RETRIES = 5;
        this.promisesToResolve = {};
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:MessageGateway#publish
     * @methodOf smarteditCommonsModule.service:MessageGateway
     *
     * @description
     * Publishes a message across the gateway using the postMessage.
     *
     * The gateway's publish method implements promises, which are an AngularJS implementation. To resolve a
     * publish promise, all listener promises on the side of the channel must resolve. If a failure occurs in the
     * chain, the chain is interrupted and the publish promise is rejected.
     *
     * @param {String} eventId Event identifier
     * @param {Object} data Message payload
     * @param {Number} retries The current number of attempts to publish a message. By default it is 0.
     * @param {String=} pk An optional parameter. It is a primary key for the event, which is generated after
     * the first attempt to send a message.
     * @returns {Promise} Promise to resolve
     */
    MessageGateway.prototype.publish = function (eventId, _data, retries, pk) {
        var _this = this;
        if (retries === void 0) { retries = 0; }
        if (!eventId) {
            this.$log.error("MessageGateway: Failed to send event. No event ID provided for _data: " + _data);
            return this.$q.when({});
        }
        var data = this.cloneableUtils.makeCloneable(_data);
        if (!angular__WEBPACK_IMPORTED_MODULE_0__["equals"](data, _data)) {
            this.$log.warn("gatewayFactory.publish - Non cloneable payload has been sanitized for gateway " + this.gatewayId + ", event " + eventId + ":", data);
        }
        var deferred = this.promisesToResolve[pk] || this.$q.defer();
        var target;
        try {
            target = this.windowUtils.getTargetIFrame();
            if (!target) {
                throw new Error('It is standalone. There is no iframe');
            }
            pk = pk || this._generateIdentifier();
            try {
                target.postMessage({
                    pk: pk,
                    gatewayId: this.gatewayId,
                    eventId: eventId,
                    data: data
                }, '*');
            }
            catch (e) {
                this.$log.error(e);
                this.$log.error("gatewayFactory.publish - postMessage has failed for gateway " + this.gatewayId + " event " + eventId + " and data ", data);
            }
            this.promisesToResolve[pk] = deferred;
            // in case promise does not return because, say, a non ready frame
            this.$timeout(function () {
                if (!deferred.acknowledged && eventId !== _this.PROMISE_RETURN_EVENT_ID && eventId !== _this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID) { // still pending
                    if (retries < _this.MAX_RETRIES) {
                        _this.$log.debug(document.location.href, "is retrying to publish event", eventId);
                        _this.publish(eventId, data, ++retries, pk);
                    }
                    else {
                        deferred.reject();
                    }
                }
            }, this.TIMEOUT_TO_RETRY_PUBLISHING);
        }
        catch (e) {
            deferred.reject();
        }
        return deferred.promise;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:MessageGateway#subscribe
     * @methodOf smarteditCommonsModule.service:MessageGateway
     *
     * @description
     * Registers a given callback function to the given event ID.
     *
     * @param {String} eventId Event identifier
     * @param {CloneableEventHandler} callback Callback function to be invoked
     * @returns {() => void)} The function to call in order to unsubscribe the event listening
     */
    MessageGateway.prototype.subscribe = function (eventId, callback) {
        var unsubscribeFn;
        if (!eventId) {
            this.$log.error('MessageGateway: Failed to subscribe event handler for event: ' + eventId);
        }
        else {
            var systemEventId = this._getSystemEventId(eventId);
            unsubscribeFn = this.systemEventService.subscribe(systemEventId, callback);
        }
        return unsubscribeFn;
    };
    MessageGateway.prototype.processEvent = function (event) {
        var _this = this;
        var eventData = event.data;
        if (event.eventId !== this.PROMISE_RETURN_EVENT_ID && event.eventId !== this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID) {
            this.$log.debug(document.location.href, "sending acknowledgement for", event);
            this.publish(this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID, {
                pk: event.pk
            });
            var systemEventId = this._getSystemEventId(event.eventId);
            this.systemEventService.publishAsync(systemEventId, event.data).then(function (resolvedDataOfLastSubscriber) {
                _this.$log.debug(document.location.href, "sending promise resolve", event);
                _this.publish(_this.PROMISE_RETURN_EVENT_ID, {
                    pk: event.pk,
                    type: _this.SUCCESS,
                    resolvedDataOfLastSubscriber: resolvedDataOfLastSubscriber
                });
            }, function () {
                _this.$log.debug(document.location.href, "sending promise reject", event);
                _this.publish(_this.PROMISE_RETURN_EVENT_ID, {
                    pk: event.pk,
                    type: _this.FAILURE
                });
            });
        }
        else if (event.eventId === this.PROMISE_RETURN_EVENT_ID) {
            if (this.promisesToResolve[eventData.pk]) {
                if (eventData.type === this.SUCCESS) {
                    this.$log.debug(document.location.href, "received promise resolve", event);
                    this.promisesToResolve[eventData.pk].resolve(eventData.resolvedDataOfLastSubscriber);
                }
                else if (eventData.type === this.FAILURE) {
                    this.$log.debug(document.location.href, "received promise reject", event);
                    this.promisesToResolve[eventData.pk].reject();
                }
                delete this.promisesToResolve[eventData.pk];
            }
        }
        else if (event.eventId === this.PROMISE_ACKNOWLEDGEMENT_EVENT_ID) {
            if (this.promisesToResolve[eventData.pk]) {
                this.$log.debug(document.location.href, "received acknowledgement", event);
                this.promisesToResolve[eventData.pk].acknowledged = true;
            }
        }
    };
    MessageGateway.prototype._generateIdentifier = function () {
        return new Date().getTime() + Math.random().toString();
    };
    MessageGateway.prototype._getSystemEventId = function (eventId) {
        return this.gatewayId + ':' + eventId;
    };
    MessageGateway = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"])()
        /* @ngInject */
    ], MessageGateway);
    return MessageGateway;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/gateway/index.ts":
/*!***********************************************************!*\
  !*** ./jsTarget/web/app/common/services/gateway/index.ts ***!
  \***********************************************************/
/*! exports provided: GatewayFactory, GatewayProxy, MessageGateway */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _GatewayFactory__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./GatewayFactory */ "./jsTarget/web/app/common/services/gateway/GatewayFactory.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayFactory", function() { return _GatewayFactory__WEBPACK_IMPORTED_MODULE_0__["GatewayFactory"]; });

/* harmony import */ var _GatewayProxy__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./GatewayProxy */ "./jsTarget/web/app/common/services/gateway/GatewayProxy.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxy", function() { return _GatewayProxy__WEBPACK_IMPORTED_MODULE_1__["GatewayProxy"]; });

/* harmony import */ var _MessageGateway__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./MessageGateway */ "./jsTarget/web/app/common/services/gateway/MessageGateway.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "MessageGateway", function() { return _MessageGateway__WEBPACK_IMPORTED_MODULE_2__["MessageGateway"]; });

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





/***/ }),

/***/ "./jsTarget/web/app/common/services/gatewayProxiedAnnotation.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/gatewayProxiedAnnotation.ts ***!
  \**********************************************************************/
/*! exports provided: GatewayProxied, GatewayProxiedAnnotationFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GatewayProxied", function() { return GatewayProxied; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GatewayProxiedAnnotationFactory", function() { return GatewayProxiedAnnotationFactory; });
/* harmony import */ var smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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


var GatewayProxiedName = 'GatewayProxied';
'se:smarteditcommons';
var GatewayProxied = smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].getClassAnnotationFactory(GatewayProxiedName);
'se:smarteditcommons';
function GatewayProxiedAnnotationFactory(gatewayProxy, $log) {
    'ngInject';
    return smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].setClassAnnotationFactory(GatewayProxiedName, function (factoryArguments) {
        return function (instance, originalConstructor, invocationArguments) {
            originalConstructor.call.apply(originalConstructor, [instance].concat(invocationArguments));
            instance.gatewayId = smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["diNameUtils"].buildServiceName(originalConstructor);
            gatewayProxy.initForService(instance, factoryArguments.length > 0 ? factoryArguments : null);
            // $log.debug(`${instance.gatewayId} is mutated into a proxied service with the arguments (${factoryArguments})`);
        };
    });
}
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.GatewayProxied = GatewayProxied;
window.smarteditcommons.GatewayProxiedAnnotationFactory = GatewayProxiedAnnotationFactory;


/***/ }),

/***/ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/OperationContextService.ts":
/*!*******************************************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/OperationContextService.ts ***!
  \*******************************************************************************************************************/
/*! exports provided: OperationContextService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "OperationContextService", function() { return OperationContextService; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:OperationContextService
 * @description
 * This service provides the functionality to register a url with its associated operation contexts and also finds operation context given an url.
 */
var OperationContextService = /** @class */ (function () {
    /** @internal */
    function OperationContextService(lodash) {
        this.lodash = lodash;
        this.store = [];
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:OperationContextService#register
     * @methodOf smarteditCommonsModule.service:OperationContextService
     *
     * @description
     * Register a new url with it's associated operationContext.
     *
     * @param {String} url The url that is associated to the operation context.
     * @param {String} operationContext The operation context name that is associated to the given url.
     *
     * @return {Object} operationContextService The operationContextService service
     */
    OperationContextService.prototype.register = function (url, operationContext) {
        if (typeof url !== 'string' || this.lodash.isEmpty(url)) {
            throw new Error('operationContextService.register error: url is invalid');
        }
        if (typeof operationContext !== 'string' || this.lodash.isEmpty(operationContext)) {
            throw new Error('operationContextService.register error: operationContext is invalid');
        }
        var regexIndex = this.store.findIndex(function (store) { return store.urlRegex.test(url) === true && store.operationContext === operationContext; });
        if (regexIndex !== -1) {
            return null;
        }
        var urlRegex = new RegExp(url.replace(/\/:[^\/]*/g, '/.*'));
        this.store.push({
            urlRegex: urlRegex,
            operationContext: operationContext
        });
        return this;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:OperationContextService#findOperationContext
     * @methodOf smarteditCommonsModule.service:OperationContextService
     *
     * @description
     * Find the first matching operation context for the given url.
     *
     * @param {String} url The request url.
     *
     * @return {String} operationContext
     */
    OperationContextService.prototype.findOperationContext = function (url) {
        var regexIndex = this.store.findIndex(function (store) { return store.urlRegex.test(url) === true; });
        return ~regexIndex ? this.store[regexIndex].operationContext : null;
    };
    OperationContextService = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], OperationContextService);
    return OperationContextService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts":
/*!**********************************************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts ***!
  \**********************************************************************************************************************/
/*! exports provided: OperationContextRegistered, OperationContextAnnotationFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "OperationContextRegistered", function() { return OperationContextRegistered; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "OperationContextAnnotationFactory", function() { return OperationContextAnnotationFactory; });
/* harmony import */ var smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
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

var operationContextName = 'OperationContextRegistered';
/**
 * @ngdoc object
 * @name smarteditCommonsModule.object:@OperationContextRegistered
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} is delegated to
 * {@link smarteditCommonsModule.service:OperationContextService OperationContextService.register} and it provides the functionality
 * to register an url with {@link seConstantsModule.object:OPERATION_CONTEXT operation context(s)}.
 *
 * For example:
 * 1. @OperationContextRegistered('apiUrl', ['CMS', 'INTERACTIVE'])
 * 2. @OperationContextRegistered('apiUrl', 'TOOLING')
 *
 * @param {string} url
 * @param {string | string[]} operationContext
 */
'se:smarteditcommons';
var OperationContextRegistered = smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].getClassAnnotationFactory(operationContextName);
'se:smarteditcommons';
function OperationContextAnnotationFactory($injector, operationContextService, OPERATION_CONTEXT) {
    'ngInject';
    return smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_0__["annotationService"].setClassAnnotationFactory(operationContextName, function (factoryArguments) {
        return function (instance, originalConstructor, invocationArguments) {
            originalConstructor.call.apply(originalConstructor, [instance].concat(invocationArguments));
            var url = $injector.has(factoryArguments[0]) ? $injector.get(factoryArguments[0]) : factoryArguments[0];
            if (typeof factoryArguments[1] === 'string') {
                var operationContext = OPERATION_CONTEXT[factoryArguments[1]];
                operationContextService.register(url, operationContext);
            }
            else if (Array.isArray(factoryArguments[1]) && factoryArguments[1].length > 0) {
                factoryArguments[1].forEach(function (element) {
                    operationContextService.register(url, OPERATION_CONTEXT[element]);
                });
            }
        };
    });
}
window.smarteditcommons = window.smarteditcommons ? window.smarteditcommons : {};
window.smarteditcommons.OperationContextRegistered = OperationContextRegistered;
window.smarteditcommons.OperationContextAnnotationFactory = OperationContextAnnotationFactory;


/***/ }),

/***/ "./jsTarget/web/app/common/services/index.ts":
/*!***************************************************!*\
  !*** ./jsTarget/web/app/common/services/index.ts ***!
  \***************************************************/
/*! exports provided: AnnotationService, annotationService, AuthorizationService, CrossFrameEventService, CrossFrameEventServiceGateway, GatewayProxied, GatewayProxiedAnnotationFactory, instrument, LanguageService, LanguageServiceGateway, IPerspectiveService, OperationContextService, OperationContextAnnotationFactory, OperationContextRegistered, PolyfillService, PriorityService, SmarteditBootstrapGateway, SystemEventService, TestModeService, SmarteditCommonsModule, SeInjectable, SeComponent, parseDirectiveBindings, parseDirectiveName, SeDirective, SeModule, DINameUtils, diNameUtils, CacheConfig, CacheConfigAnnotationFactory, Cached, CachedAnnotationFactory, InvalidateCache, InvalidateCacheAnnotationFactory, SeAlertServiceType, IExperienceService, IFeatureService, INotificationMouseLeaveDetectionService, INotificationService, IPageInfoService, IPreviewService, ISessionService, ISharedDataService, IStorageService, IUrlService, IWaitDialogService, CacheAction, CacheService, EvictionTag, GatewayFactory, GatewayProxy, MessageGateway, ICatalogService, IPermissionService, AbstractCachedRestService, ContentCatalogRestService, ProductCatalogRestService, PermissionsRestService, IDragEventType, InViewElementObserver, NamespacedStorageManager, StorageManagerFactory, StorageNamespaceConverter, FrequentlyChangingContentName, frequentlyChangingContent, RarelyChangingContentName, rarelyChangingContent, CacheEngine, DefaultCacheTiming, authorizationEvictionTag, catalogSyncedEvictionTag, catalogEvictionTag, pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag, pageRestoredEvictionTag, pageChangeEvictionTag, pageEvictionTag, userEvictionTag, contentCatalogUpdateEvictionTag */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _forcedImport__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./forcedImport */ "./jsTarget/web/app/common/services/forcedImport.ts");
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeInjectable", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeComponent", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveBindings", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["parseDirectiveBindings"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "parseDirectiveName", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["parseDirectiveName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeDirective", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeDirective"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeModule", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeModule"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DINameUtils", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["DINameUtils"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "diNameUtils", function() { return _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["diNameUtils"]; });

/* harmony import */ var _annotationService__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnnotationService", function() { return _annotationService__WEBPACK_IMPORTED_MODULE_2__["AnnotationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "annotationService", function() { return _annotationService__WEBPACK_IMPORTED_MODULE_2__["annotationService"]; });

/* harmony import */ var _cache__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./cache */ "./jsTarget/web/app/common/services/cache/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfig", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CacheConfig"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfigAnnotationFactory", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CacheConfigAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "Cached", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["Cached"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CachedAnnotationFactory", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CachedAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCache", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["InvalidateCache"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCacheAnnotationFactory", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["InvalidateCacheAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheAction", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CacheAction"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheService", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CacheService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "EvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["EvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FrequentlyChangingContentName", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["FrequentlyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "frequentlyChangingContent", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["frequentlyChangingContent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RarelyChangingContentName", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["RarelyChangingContentName"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "rarelyChangingContent", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["rarelyChangingContent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheEngine", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["CacheEngine"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultCacheTiming", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["DefaultCacheTiming"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "authorizationEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["authorizationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogSyncedEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["catalogSyncedEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "catalogEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["catalogEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageCreationEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageCreationEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageDeletionEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageDeletionEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageUpdateEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageUpdateEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageRestoredEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageRestoredEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageChangeEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageChangeEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "pageEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["pageEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "userEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["userEvictionTag"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "contentCatalogUpdateEvictionTag", function() { return _cache__WEBPACK_IMPORTED_MODULE_3__["contentCatalogUpdateEvictionTag"]; });

/* harmony import */ var _gateway__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./gateway */ "./jsTarget/web/app/common/services/gateway/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayFactory", function() { return _gateway__WEBPACK_IMPORTED_MODULE_4__["GatewayFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxy", function() { return _gateway__WEBPACK_IMPORTED_MODULE_4__["GatewayProxy"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "MessageGateway", function() { return _gateway__WEBPACK_IMPORTED_MODULE_4__["MessageGateway"]; });

/* harmony import */ var _interfaces__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./interfaces */ "./jsTarget/web/app/common/services/interfaces/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeAlertServiceType", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["SeAlertServiceType"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IExperienceService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IExperienceService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IFeatureService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IFeatureService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationMouseLeaveDetectionService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["INotificationMouseLeaveDetectionService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["INotificationService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPageInfoService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IPageInfoService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPreviewService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IPreviewService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISessionService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["ISessionService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISharedDataService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["ISharedDataService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IStorageService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IStorageService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IUrlService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IUrlService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IWaitDialogService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IWaitDialogService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ICatalogService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["ICatalogService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPermissionService", function() { return _interfaces__WEBPACK_IMPORTED_MODULE_5__["IPermissionService"]; });

/* harmony import */ var _auth_AuthorizationService__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./auth/AuthorizationService */ "./jsTarget/web/app/common/services/auth/AuthorizationService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AuthorizationService", function() { return _auth_AuthorizationService__WEBPACK_IMPORTED_MODULE_6__["AuthorizationService"]; });

/* harmony import */ var _crossFrame_CrossFrameEventService__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./crossFrame/CrossFrameEventService */ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventService", function() { return _crossFrame_CrossFrameEventService__WEBPACK_IMPORTED_MODULE_7__["CrossFrameEventService"]; });

/* harmony import */ var _crossFrame_CrossFrameEventServiceGateway__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./crossFrame/CrossFrameEventServiceGateway */ "./jsTarget/web/app/common/services/crossFrame/CrossFrameEventServiceGateway.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CrossFrameEventServiceGateway", function() { return _crossFrame_CrossFrameEventServiceGateway__WEBPACK_IMPORTED_MODULE_8__["CrossFrameEventServiceGateway"]; });

/* harmony import */ var _gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./gatewayProxiedAnnotation */ "./jsTarget/web/app/common/services/gatewayProxiedAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxied", function() { return _gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_9__["GatewayProxied"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxiedAnnotationFactory", function() { return _gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_9__["GatewayProxiedAnnotationFactory"]; });

/* harmony import */ var _instrumentation__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./instrumentation */ "./jsTarget/web/app/common/services/instrumentation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "instrument", function() { return _instrumentation__WEBPACK_IMPORTED_MODULE_10__["instrument"]; });

/* harmony import */ var _language_LanguageService__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./language/LanguageService */ "./jsTarget/web/app/common/services/language/LanguageService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageService", function() { return _language_LanguageService__WEBPACK_IMPORTED_MODULE_11__["LanguageService"]; });

/* harmony import */ var _language_LanguageServiceGateway__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./language/LanguageServiceGateway */ "./jsTarget/web/app/common/services/language/LanguageServiceGateway.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LanguageServiceGateway", function() { return _language_LanguageServiceGateway__WEBPACK_IMPORTED_MODULE_12__["LanguageServiceGateway"]; });

/* harmony import */ var _perspectives_IPerspectiveService__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./perspectives/IPerspectiveService */ "./jsTarget/web/app/common/services/perspectives/IPerspectiveService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPerspectiveService", function() { return _perspectives_IPerspectiveService__WEBPACK_IMPORTED_MODULE_13__["IPerspectiveService"]; });

/* harmony import */ var _rest_rest__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./rest/rest */ "./jsTarget/web/app/common/services/rest/rest.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AbstractCachedRestService", function() { return _rest_rest__WEBPACK_IMPORTED_MODULE_14__["AbstractCachedRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ContentCatalogRestService", function() { return _rest_rest__WEBPACK_IMPORTED_MODULE_14__["ContentCatalogRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ProductCatalogRestService", function() { return _rest_rest__WEBPACK_IMPORTED_MODULE_14__["ProductCatalogRestService"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PermissionsRestService", function() { return _rest_rest__WEBPACK_IMPORTED_MODULE_14__["PermissionsRestService"]; });

/* harmony import */ var _httpErrorInterceptor_default_retryInterceptor_OperationContextService__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./httpErrorInterceptor/default/retryInterceptor/OperationContextService */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/OperationContextService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextService", function() { return _httpErrorInterceptor_default_retryInterceptor_OperationContextService__WEBPACK_IMPORTED_MODULE_15__["OperationContextService"]; });

/* harmony import */ var _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextAnnotationFactory", function() { return _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_16__["OperationContextAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextRegistered", function() { return _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_16__["OperationContextRegistered"]; });

/* harmony import */ var _PolyfillService__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ./PolyfillService */ "./jsTarget/web/app/common/services/PolyfillService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PolyfillService", function() { return _PolyfillService__WEBPACK_IMPORTED_MODULE_17__["PolyfillService"]; });

/* harmony import */ var _PriorityService__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ./PriorityService */ "./jsTarget/web/app/common/services/PriorityService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PriorityService", function() { return _PriorityService__WEBPACK_IMPORTED_MODULE_18__["PriorityService"]; });

/* harmony import */ var _SmarteditBootstrapGateway__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./SmarteditBootstrapGateway */ "./jsTarget/web/app/common/services/SmarteditBootstrapGateway.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SmarteditBootstrapGateway", function() { return _SmarteditBootstrapGateway__WEBPACK_IMPORTED_MODULE_19__["SmarteditBootstrapGateway"]; });

/* harmony import */ var _SystemEventService__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ./SystemEventService */ "./jsTarget/web/app/common/services/SystemEventService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SystemEventService", function() { return _SystemEventService__WEBPACK_IMPORTED_MODULE_20__["SystemEventService"]; });

/* harmony import */ var _testModeService__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! ./testModeService */ "./jsTarget/web/app/common/services/testModeService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TestModeService", function() { return _testModeService__WEBPACK_IMPORTED_MODULE_21__["TestModeService"]; });

/* harmony import */ var _dragAndDrop__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! ./dragAndDrop */ "./jsTarget/web/app/common/services/dragAndDrop/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IDragEventType", function() { return _dragAndDrop__WEBPACK_IMPORTED_MODULE_22__["IDragEventType"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InViewElementObserver", function() { return _dragAndDrop__WEBPACK_IMPORTED_MODULE_22__["InViewElementObserver"]; });

/* harmony import */ var _storage__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! ./storage */ "./jsTarget/web/app/common/services/storage/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "NamespacedStorageManager", function() { return _storage__WEBPACK_IMPORTED_MODULE_23__["NamespacedStorageManager"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageManagerFactory", function() { return _storage__WEBPACK_IMPORTED_MODULE_23__["StorageManagerFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageNamespaceConverter", function() { return _storage__WEBPACK_IMPORTED_MODULE_23__["StorageNamespaceConverter"]; });

/* harmony import */ var _SmarteditCommonsModule__WEBPACK_IMPORTED_MODULE_24__ = __webpack_require__(/*! ./SmarteditCommonsModule */ "./jsTarget/web/app/common/services/SmarteditCommonsModule.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SmarteditCommonsModule", function() { return _SmarteditCommonsModule__WEBPACK_IMPORTED_MODULE_24__["SmarteditCommonsModule"]; });

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
// forced import to make sure d.ts are generated for the interfaces below



























/***/ }),

/***/ "./jsTarget/web/app/common/services/instrumentation.ts":
/*!*************************************************************!*\
  !*** ./jsTarget/web/app/common/services/instrumentation.ts ***!
  \*************************************************************/
/*! exports provided: instrument */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "instrument", function() { return instrument; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/utils/FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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



/** @internal */
var Instrumentation = /** @class */ (function () {
    function Instrumentation(readObjectStructure) {
        this.readObjectStructure = readObjectStructure;
        this._FUNCTION_EXECUTION_DATA = {};
        this._DECORATED_MODULES = [];
        this._DEFAULT_LEVEL = 10;
        this.alreadyBrowsed = [];
        this.functionsUtils = new smarteditcommons_utils_FunctionsUtils__WEBPACK_IMPORTED_MODULE_1__["FunctionsUtils"]();
    }
    Instrumentation.prototype.execute = function (config, _LEVEL, isRoot) {
        var _this = this;
        var LEVEL = _LEVEL || this._DEFAULT_LEVEL;
        if (config.modules === undefined) {
            return;
        }
        this._logFunctionArgumentsAndReturnValue = this._logFunctionArgumentsAndReturnValue.bind(this);
        this._getServiceConfig = this._getServiceConfig.bind(this);
        this._getIterableDelegate = this._getIterableDelegate.bind(this);
        this._getDelegateFunctions = this._getDelegateFunctions.bind(this);
        this._extractComponentBindings = this._extractComponentBindings.bind(this);
        this._extractDirectiveBindings = this._extractDirectiveBindings.bind(this);
        var $provide = config.$provide;
        config.modules.forEach(function (moduleName) {
            _this.alreadyBrowsed.push(moduleName);
            if (!isRoot) {
                if (_this._DECORATED_MODULES.indexOf(moduleName) > -1 || !moduleName.endsWith('Module')) {
                    return;
                }
            }
            _this._DECORATED_MODULES.push(moduleName);
            var moduleConfig = _this._getServiceConfig(moduleName);
            // FIXME: necessary to concat?
            var serviceExcludeFunctions = moduleConfig.exclude.concat(config.globalExcludedFunctions || []);
            var serviceIncludeFunctions = moduleConfig.include.concat(config.globalIncludedFunctions || []);
            /* forbiddenNameSpaces angular.module:false */
            var module = angular__WEBPACK_IMPORTED_MODULE_0__["module"](moduleName);
            module._invokeQueue.forEach(function (invoke) {
                var fn = invoke[1];
                if (['factory', 'component', 'service', 'directive'].indexOf(fn) > -1) {
                    var args = invoke[2];
                    var recipeName_1 = args[0];
                    if (fn === 'component') {
                        _this._extractComponentBindings(args);
                    }
                    else if (fn === 'directive') {
                        _this._extractDirectiveBindings(args);
                    }
                    else {
                        try {
                            if (_this._isEligible(recipeName_1, config.globalExcludedRecipes, config.globalIncludedRecipes)) {
                                $provide.decorator(recipeName_1, function ($delegate) {
                                    var iterableDelegate = _this._getIterableDelegate($delegate);
                                    var originals = _this._getDelegateFunctions(iterableDelegate, serviceExcludeFunctions, serviceIncludeFunctions);
                                    originals.forEach(function (original) {
                                        if (!iterableDelegate[original.name].__DECORATED__) {
                                            var self_1 = _this;
                                            var isEmpty = self_1.functionsUtils.isEmpty(original.fn);
                                            if (isEmpty) {
                                                iterableDelegate[original.name] = function () {
                                                    'proxyFunction';
                                                    var result = original.fn.apply(this, arguments);
                                                    self_1._logFunctionArgumentsAndReturnValue(recipeName_1, original.name, arguments, result);
                                                    return result;
                                                };
                                            }
                                            else {
                                                iterableDelegate[original.name] = function () {
                                                    var result = original.fn.apply(this, arguments);
                                                    self_1._logFunctionArgumentsAndReturnValue(recipeName_1, original.name, arguments, result);
                                                    return result;
                                                };
                                            }
                                            iterableDelegate[original.name].__DECORATED__ = true;
                                        }
                                    });
                                    return $delegate;
                                });
                            }
                            else {
                                _this.warn("not eligible recipe : " + recipeName_1);
                            }
                        }
                        catch (e) {
                            _this.error(e);
                            _this.warn("Warning-No-Service-Exists: " + recipeName_1 + " of type " + fn + ", moduleName: " + moduleName);
                        }
                    }
                }
            });
            if (LEVEL > 0) {
                var NEW_LEVEL_1 = LEVEL - 1;
                module.requires.forEach(function (_moduleName) {
                    if (_this.alreadyBrowsed.indexOf(_moduleName) === -1
                        && config.defaultExcludeModules.indexOf(_moduleName) === -1
                        && _moduleName.indexOf("Mock") === -1) {
                        _this.execute({
                            globalExcludedRecipes: config.globalExcludedRecipes,
                            globalIncludedRecipes: config.globalIncludedRecipes,
                            globalExcludedFunctions: config.globalExcludedFunctions,
                            globalIncludedFunctions: config.globalIncludedFunctions,
                            modules: [_moduleName],
                            $provide: config.$provide,
                            defaultExcludeModules: config.defaultExcludeModules
                        }, NEW_LEVEL_1, false);
                    }
                });
            }
        });
    };
    Instrumentation.prototype._getServiceConfig = function (service) {
        return typeof service === "string" ? {
            name: service,
            exclude: [],
            include: []
        } : service;
    };
    Instrumentation.prototype._getIterableDelegate = function ($delegate) {
        return $delegate.prototype ? $delegate.prototype : $delegate;
    };
    Instrumentation.prototype._matches = function (name, nameRegex) {
        return (new RegExp(nameRegex, 'gi')).test(name);
    };
    Instrumentation.prototype._getDelegateFunctions = function ($delegate, serviceExcludeFunctions, serviceIncludeFunctions) {
        var result = [];
        for (var fnName in $delegate) {
            if (typeof $delegate[fnName] === "function") {
                if (this._isEligible(fnName, serviceExcludeFunctions, serviceIncludeFunctions)) {
                    result.push({
                        name: fnName,
                        fn: $delegate[fnName]
                    });
                }
            }
        }
        return result;
    };
    Instrumentation.prototype._isEligible = function (recipeName, excludes, includes) {
        return (!includes.length || !!includes.find(this._matches.bind(this, recipeName))) && (!excludes.length || !excludes.find(this._matches.bind(this, recipeName)));
    };
    Instrumentation.prototype._resultIsPromise = function (result) {
        return !!result && result.$$state !== undefined;
    };
    Instrumentation.prototype._keyExists = function (key) {
        if (this._FUNCTION_EXECUTION_DATA[key] !== undefined) {
            return true;
        }
        return false;
    };
    Instrumentation.prototype._extractComponentBindings = function (args) {
        var componentName = args[0];
        var bindVariables = args[1].bindings;
        this._logDirectiveResult(componentName, bindVariables);
    };
    Instrumentation.prototype._extractDirectiveBindings = function (args) {
        var directiveName = args[0];
        try {
            var secondAttribute = args[1];
            var directiveConfig = null;
            if (typeof secondAttribute === 'function') {
                directiveConfig = secondAttribute();
            }
            else if (secondAttribute instanceof Array) {
                directiveConfig = secondAttribute[secondAttribute.length - 1]();
            }
            if (directiveConfig) {
                var scope = directiveConfig.scope;
                var bindToController = directiveConfig.bindToController;
                this._logDirectiveResult(directiveName, undefined, scope, bindToController);
            }
        }
        catch (e) {
            this._logDirectiveResult(directiveName);
        }
    };
    Instrumentation.prototype._logFunctionArgumentsAndReturnValue = function (serviceName, functionName, _args, result) {
        var _this = this;
        var args = this.readObjectStructure(Array.prototype.slice.call(_args));
        var key = null;
        try {
            key = serviceName + "~" + functionName + "~" + angular__WEBPACK_IMPORTED_MODULE_0__["toJson"](args);
        }
        catch (e) {
            this.error('COULD NOT GENERATE KEY');
            return result;
        }
        if (this._keyExists(key)) {
            return result;
        }
        this._FUNCTION_EXECUTION_DATA[key] = {};
        if (this._resultIsPromise(result)) {
            return result.then(function (res) {
                try {
                    _this._FUNCTION_EXECUTION_DATA[key] = {
                        serviceName: serviceName,
                        functionName: functionName,
                        arguments: args,
                        result: {
                            promiseValue: _this.readObjectStructure(res)
                        }
                    };
                    _this.warn(angular__WEBPACK_IMPORTED_MODULE_0__["toJson"](_this._FUNCTION_EXECUTION_DATA[key]));
                }
                catch (e) {
                    _this.error('COULD NOT STRINGIFY');
                }
                return Promise.resolve(res);
            }, function (reason) {
                _this.error('ERROR WHILE RESOLVING RESULT: ' + reason);
                _this._FUNCTION_EXECUTION_DATA[key] = {
                    serviceName: serviceName,
                    functionName: functionName,
                    arguments: args,
                    result: { promiseValue: reason }
                };
                _this.warn(angular__WEBPACK_IMPORTED_MODULE_0__["toJson"](_this._FUNCTION_EXECUTION_DATA[key]));
                return Promise.reject(reason);
            });
        }
        else {
            this._FUNCTION_EXECUTION_DATA[key] = {
                serviceName: serviceName,
                functionName: functionName,
                arguments: args,
                result: this.readObjectStructure(result)
            };
            try {
                this.warn(angular__WEBPACK_IMPORTED_MODULE_0__["toJson"](this._FUNCTION_EXECUTION_DATA[key]));
            }
            catch (e) {
                this.error('COULD NOT STRINGIFY');
            }
            return result;
        }
    };
    Instrumentation.prototype._logDirectiveResult = function (directiveName, bindings, scope, bindToController) {
        this.warn(angular__WEBPACK_IMPORTED_MODULE_0__["toJson"]({
            directiveName: directiveName,
            bindings: bindings,
            scope: scope,
            bindToController: bindToController
        }));
    };
    Instrumentation.prototype.warn = function (message) {
        // tslint:disable-next-line
        console.warn(message);
    };
    Instrumentation.prototype.error = function (message) {
        // tslint:disable-next-line
        console.error(message);
    };
    Instrumentation = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__["SeInjectable"])()
    ], Instrumentation);
    return Instrumentation;
}());
function getItemFromSessionStorage(name) {
    try {
        return window.sessionStorage.getItem(name);
    }
    catch (e) {
        /*
         * would fail if:
         * - sessionStorage is not implemented
         * - accessing sessionStorage is forbidden in CORS because of default "Block third-party cookies" settings in chrome
         */
        return null;
    }
}
/** @internal */
var instrument = function ($provide, readObjectStructure, TOP_LEVEL_MODULE_NAME) {
    'ngInject';
    if (getItemFromSessionStorage("isInstrumented") === "true") {
        new Instrumentation(readObjectStructure).execute({
            globalExcludedRecipes: [
                "assetsService",
                "configurationExtractorService",
                "experienceService"
            ],
            globalIncludedRecipes: [
                "^.*Interface$",
                "^.*Service$",
                "^.*Helper$",
                "^.*Hanlder$",
                "^.*Editor$",
                "^I.*$",
                "^.*Decorator$",
                "^.*Directive$",
                "^.*Registry$",
                "^.*Listener$",
                "^.*Resource$",
                "^.*Populator$",
                "^.*Constants$",
                "^.*Factory$",
                "^.*Facade$",
                "^.*Interceptor$",
                "^.*Manager$",
                "^.*Class$",
                "^.*Strategy",
                "^.*Predicate",
                "^.*Retry",
                "^.*Gateway"
            ],
            globalExcludedFunctions: ["^_.*$", "^\\$", "lodash", "yjQuery"],
            globalIncludedFunctions: ["^.*$"],
            defaultExcludeModules: [
                "yjqueryModule",
                "functionsModule",
                "ycmssmarteditModule",
                "timerModule",
                "ui.bootstrap",
                "ngResource",
                "ui.select",
                "yjQuery",
                "instrumentModule",
                "interceptorHelperModule",
                "i18nInterceptorModule",
                'loadConfigModule',
                'ui.tree',
                'treeModule' // contains fetchChildren function that extracts dom objects that contains circular structure
            ],
            modules: [TOP_LEVEL_MODULE_NAME],
            $provide: $provide
        }, 15, true);
    }
};


/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IAlertService.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IAlertService.ts ***!
  \**********************************************************************/
/*! exports provided: SeAlertServiceType */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SeAlertServiceType", function() { return SeAlertServiceType; });
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
var SeAlertServiceType;
(function (SeAlertServiceType) {
    SeAlertServiceType["Info"] = "INFO";
    SeAlertServiceType["Success"] = "SUCCESS";
    SeAlertServiceType["Warning"] = "WARNING";
    SeAlertServiceType["Danger"] = "DANGER";
})(SeAlertServiceType || (SeAlertServiceType = {}));


/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IBrowserService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IBrowserService.ts ***!
  \************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/ICatalogService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/ICatalogService.ts ***!
  \************************************************************************/
/*! exports provided: ICatalogService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ICatalogService", function() { return ICatalogService; });
/**
 * @ngdoc service
 * @name smarteditServicesModule.service:catalogService
 *
 * @description
 * The Catalog Service fetches catalogs for a specified site or for all sites registered on the hybris platform using
 * REST calls to the cmswebservices Catalog Version Details API.
 */
var ICatalogService = /** @class */ (function () {
    function ICatalogService() {
    }
    // ------------------------------------------------------------------------------------------------------------------------
    //  Deprecated
    // ------------------------------------------------------------------------------------------------------------------------
    /**
     * @deprecated since 6.4
     *
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getAllCatalogsGroupedById
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches a list of content catalog groupings for all sites.
     *
     * @returns {Array} An array of catalog groupings sorted by catalog ID, each of which has a name, a catalog ID, and a list of
     * catalog version descriptors.
     */
    ICatalogService.prototype.getAllCatalogsGroupedById = function () {
        'proxyFunction';
        return null;
    };
    // ------------------------------------------------------------------------------------------------------------------------
    //  Active
    // ------------------------------------------------------------------------------------------------------------------------
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#retrieveUriContext
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Convenience method to return a full {@link resourceLocationsModule.object:UriContext uriContext} to the invoker through a promise.
     * <br/>if uriContext is provided, it will be returned as such.
     * <br/>if uriContext is not provided, A uriContext will be built from the experience present in {@link  smarteditServicesModule.sharedDataService sharedDataService}.
     * if we fail to find a uriContext in sharedDataService, an exception will be thrown.
     * @param {=Object=} uriContext An optional uriContext that, if provided, is simply returned wrapped in a promise
     *
     * @returns {Object} a {@link resourceLocationsModule.object:UriContext uriContext}
     */
    ICatalogService.prototype.retrieveUriContext = function (_uriContext) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getContentCatalogsForSite
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches a list of content catalogs for the site that corresponds to the specified site UID.
     *
     * @param {String} siteUID The UID of the site that the catalog versions are to be fetched.
     *
     * @returns {Array} An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    ICatalogService.prototype.getContentCatalogsForSite = function (siteUID) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getAllContentCatalogsGroupedById
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches a list of content catalog groupings for all sites.
     *
     * @returns {Array} An array of catalog groupings sorted by catalog ID, each of which has a name, a catalog ID, and a list of
     * catalog version descriptors.
     */
    ICatalogService.prototype.getAllContentCatalogsGroupedById = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getCatalogByVersion
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches a list of catalogs for the given site UID and a given catalog version.
     *
     * @param {String} siteUID The UID of the site that the catalog versions are to be fetched.
     * @param {String} catalogVersion The version of the catalog that is to be fetched.
     *
     * @returns {Array} An array containing the catalog descriptor (if any). Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalogVersion.
     */
    // FIXME : this method does not seem to be safe for same catalogversion version name across multiple catalogs
    ICatalogService.prototype.getCatalogByVersion = function (siteUID, catalogVersionName) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#isContentCatalogVersionNonActive
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Determines whether the catalog version identified by the given uriContext is a non active one
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link smarteditServicesModule.sharedDataService sharedDataService}
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext}. Optional
     * @returns {Boolean} true if the given catalog version is non active
     */
    ICatalogService.prototype.isContentCatalogVersionNonActive = function (_uriContext) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getContentCatalogActiveVersion
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * find the version that is flagged as active for the given uriContext
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link smarteditServicesModule.sharedDataService sharedDataService}
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext}. Optional
     * @returns {String} the version name
     */
    ICatalogService.prototype.getContentCatalogActiveVersion = function (_uriContext) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getActiveContentCatalogVersionByCatalogId
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the version name that is flagged as active for the given content catalog.
     *
     * @param {String} contentCatalogId The UID of content catalog for which to retrieve its active catalog version name.
     * @returns {String} the version name
     */
    ICatalogService.prototype.getActiveContentCatalogVersionByCatalogId = function (contentCatalogId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getDefaultSiteForContentCatalog
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the ID of the default site configured for the provided content catalog.
     *
     * @param {String} contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns {String} the ID of the default site found.
     */
    ICatalogService.prototype.getDefaultSiteForContentCatalog = function (contentCatalogId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getContentCatalogVersion
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the catalog version given an uriContext object.
     *
     * @param {Object} uriContext  An object that represents the current context, containing information about the site.
     * @returns {angular.IPromise<IBaseCatalogVersion>} A promise that resolves to the catalog version descriptor found.
     */
    ICatalogService.prototype.getContentCatalogVersion = function (uriContext) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getCatalogVersionByUuid
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the catalog version descriptor identified by the provided UUID. An exception is thrown if no
     * match is found.
     *
     * @param {String} catalogVersionUuid The UID of the catalog version descriptor to find.
     * @param {String=} siteId the ID of the site where to perform the search. If no ID is provided, the search will
     * be performed on all permitted sites.
     * @returns {Promise} A promise that resolves to the catalog version descriptor found.
     *
     */
    ICatalogService.prototype.getCatalogVersionByUuid = function (catalogVersionUuid, siteId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getCatalogVersionUUid
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the catalog version UUID given an optional urlContext object. The current catalog version UUID from the active experience selector is returned, if the URL is not present in the call.
     *
     * @param {Object} urlContext An object that represents the current context, containing information about the site.
     * @returns {Promise<String>} A promise that resolves to the catalog version uuid.
     *
     */
    ICatalogService.prototype.getCatalogVersionUUid = function (_uriContext) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getProductCatalogsForSite
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches a list of product catalogs for the site that corresponds to the specified site UID.
     *
     * @param {String} siteUID The UID of the site that the catalog versions are to be fetched.
     *
     * @returns {Array} An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    ICatalogService.prototype.getProductCatalogsForSite = function (siteUID) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#getActiveProductCatalogVersionByCatalogId
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Finds the version name that is flagged as active for the given product catalog.
     *
     * @param {String} productCatalogId The UID of product catalog for which to retrieve its active catalog version name.
     * @returns {String} the version name
     */
    ICatalogService.prototype.getActiveProductCatalogVersionByCatalogId = function (productCatalogId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:catalogService#returnActiveCatalogVersionUIDs
     * @methodOf smarteditServicesModule.service:catalogService
     *
     * @description
     * Fetches all the active catalog version uuid's for a provided array of catalogs.
     *
     * @param {Array} An array of catalogs objects. Each catalog object must have a versions array.
     * @returns {Array} An array of catalog version uuid's
     */
    ICatalogService.prototype.returnActiveCatalogVersionUIDs = function (catalogs) {
        'proxyFunction';
        return null;
    };
    return ICatalogService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IContextualMenuButton.ts":
/*!******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IContextualMenuButton.ts ***!
  \******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IContextualMenuConfiguration.ts":
/*!*************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IContextualMenuConfiguration.ts ***!
  \*************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IDecorator.ts":
/*!*******************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IDecorator.ts ***!
  \*******************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IExperience.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IExperience.ts ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IExperienceService.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IExperienceService.ts ***!
  \***************************************************************************/
/*! exports provided: IExperienceService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IExperienceService", function() { return IExperienceService; });
/**
 * @ngdoc service
 * @name smarteditServicesModule.service:ExperienceService
 *
 * @description
 * ExperienceService deals with building experience objects given a context.
 */
var IExperienceService = /** @class */ (function () {
    /** @internal */
    function IExperienceService(lodash) {
        this.lodash = lodash;
    }
    /* @internal */
    IExperienceService.prototype.updateExperiencePageContext = function (pageCatalogVersionUuid, pageId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#getCurrentExperience
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Retrieves the active experience.
     *
     * @returns {IExperience} an {@link smarteditServicesModule.interface:IExperience experience}
     */
    IExperienceService.prototype.getCurrentExperience = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#setCurrentExperience
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Stores a given experience as current experience.
     * Invoking this method ensures that a hard refresh of the application will preserve the experience.
     *
     * @returns {angular.IPromise<IExperience>} a promise returning the experience
     */
    IExperienceService.prototype.setCurrentExperience = function (experience) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#hasCatalogVersionChanged
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Determines whether the catalog version has changed between the previous and current experience
     *
     * @returns {angular.IPromise<boolean>} a promise returning whether thta catalog version has changed
     */
    IExperienceService.prototype.hasCatalogVersionChanged = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#buildRefreshedPreviewUrl
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Retrieves the active experience, creates a new preview ticket and returns a new preview url with an updated
     * previewTicketId query param
     *
     * @returns {angular.IPromise<string>} an url containing the new previewTicketId
     */
    IExperienceService.prototype.buildRefreshedPreviewUrl = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#updateExperience
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Retrieves the active experience, merges it with a new experience, creates a new preview ticket and reloads the
     * preview within the iframeManagerService
     *
     * @param {Payload=} newExperience The object containing new attributes to be merged with the current experience
     *
     * @returns {angular.IPromise<IExperience>} An promise of the updated experience
     */
    IExperienceService.prototype.updateExperience = function (newExperience) {
        'proxyFunction';
        return null;
    };
    /** @internal */
    IExperienceService.prototype._convertExperienceToPreviewData = function (experience, resourcePath) {
        var previewData = this.lodash.cloneDeep(experience);
        var catalogVersions = [];
        delete previewData.catalogDescriptor;
        delete previewData.siteDescriptor;
        delete previewData.languageDescriptor;
        delete previewData.pageContext;
        delete previewData.productCatalogVersions;
        if (experience.productCatalogVersions && experience.productCatalogVersions.length) {
            experience.productCatalogVersions.forEach(function (productCatalogVersion) {
                catalogVersions.push({
                    catalog: productCatalogVersion.catalog,
                    catalogVersion: productCatalogVersion.catalogVersion
                });
            });
        }
        catalogVersions.push({
            catalog: experience.catalogDescriptor.catalogId,
            catalogVersion: experience.catalogDescriptor.catalogVersion
        });
        previewData.catalogVersions = catalogVersions;
        previewData.language = experience.languageDescriptor.isocode;
        previewData.resourcePath = resourcePath;
        return previewData;
    };
    return IExperienceService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IFeature.ts":
/*!*****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IFeature.ts ***!
  \*****************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

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


/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IFeatureService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IFeatureService.ts ***!
  \************************************************************************/
/*! exports provided: IFeatureService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IFeatureService", function() { return IFeatureService; });
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IFeatureService
 *
 * @description
 * The interface stipulates how to register features in the SmartEdit application and the SmartEdit container.
 * The SmartEdit implementation stores two instances of the interface across the {@link smarteditCommonsModule.service:GatewayFactory gateway}: one for the SmartEdit application and one for the SmartEdit container.
 */
var IFeatureService = /** @class */ (function () {
    /** @internal */
    function IFeatureService(lodash, cloneableUtils) {
        this.lodash = lodash;
        this.cloneableUtils = cloneableUtils;
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IFeatureService#register
     * @methodOf smarteditServicesModule.interface:IFeatureService
     * @description
     * This method registers a feature.
     * When an end user selects a perspective, all the features that are bound to the perspective
     * will be enabled when their respective enablingCallback functions are invoked
     * and all the features that are not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     * The SmartEdit application and the SmartEdit container hold/store an instance of the implementation because callbacks cannot cross the gateway as they are functions.
     *
     * this method is meant to register a feature (identified by a key).
     * When a perspective (registered through {@link smarteditServicesModule.interface:IPerspectiveService#methods_register IPerspectiveService.register}) is selected, all its bound features will be enabled by invocation of their respective enablingCallback functions
     * and any feature not bound to it will be disabled by invocation of its disablingCallback function.
     * Both SmartEdit and SmartEditContainer will hold a concrete implementation since Callbacks, being functions, cannot cross the gateway.
     * The function will keep a frame bound reference on a full feature in order to be able to invoke its callbacks when needed.
     *
     * @param {IContextualMenuButton | IDecorator | IToolbarItem} configuration of a {@link smarteditServicesModule.interface:IContextualMenuButton IContextualMenuButton} or
     * {@link smarteditServicesModule.interface:IDecorator IDecorator} or {@link smarteditServicesModule.interface:IToolbarItem IToolbarItem}
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IFeatureService.prototype.register = function (configuration) {
        this._validate(configuration);
        this._featuresToAlias = this._featuresToAlias || {};
        this._featuresToAlias[configuration.key] = {
            enablingCallback: configuration.enablingCallback,
            disablingCallback: configuration.disablingCallback
        };
        delete configuration.enablingCallback;
        delete configuration.disablingCallback;
        return this._registerAliases(this.cloneableUtils.makeCloneable(configuration));
    };
    IFeatureService.prototype.enable = function (key) {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].enablingCallback();
        }
        else {
            this._remoteEnablingFromInner(key);
        }
    };
    IFeatureService.prototype.disable = function (key) {
        if (this._featuresToAlias && this._featuresToAlias[key]) {
            this._featuresToAlias[key].disablingCallback();
        }
        else {
            this._remoteDisablingFromInner(key);
        }
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IFeatureService#getFeatureProperty
     * @methodOf smarteditServicesModule.interface:IFeatureService
     * @description
     * Returns a feature property
     *
     * @param {String} featureKey the key property value of the feature
     * @param {String} propertyName name of the property
     *
     * @return {angular.IPromise<string | string[] | (() => void)>} returns promise of property value or null if property does not exist
     */
    IFeatureService.prototype.getFeatureProperty = function (featureKey, propertyName) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IFeatureService#addToolbarItem
     * @methodOf smarteditServicesModule.interface:IFeatureService
     *
     * @description
     * This method registers toolbar items as features. It is a wrapper around {@link smarteditServicesModule.interface:IFeatureService#methods_register register}.
     *
     * @param {IToolbarItem} configuration The {@link smarteditServicesModule.interface:IToolbarItem configuration} that represents the toolbar action item to be registered.
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IFeatureService.prototype.addToolbarItem = function (toolbar) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IFeatureService#addDecorator
     * @methodOf smarteditServicesModule.interface:IFeatureService
     *
     * @description
     * this method registers decorator and delegates to the
     *  {@link decoratorServiceModule.service:decoratorService#methods_enable enable}
     *  {@link decoratorServiceModule.service:decoratorService#methods_disable disable} methods of
     *  {@link decoratorServiceModule.service:decoratorService decoratorService}.
     * This method is not a wrapper around {@link decoratorServiceModule.service:decoratorService#addMappings decoratorService.addMappings}:
     * From a feature stand point, we deal with decorators, not their mappings to SmartEdit components.
     * We still need to have a separate invocation of {@link decoratorServiceModule.service:decoratorService#addMappings decoratorService.addMappings}
     * @param {IDecorator} configuration The {@link smarteditServicesModule.interface:IDecorator configuration} that represents the decorator to be registered.
     * @return {angular.IPromise<void>} An empty promise
     */
    IFeatureService.prototype.addDecorator = function (decorator) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IFeatureService#addContextualMenuButton
     * @methodOf smarteditServicesModule.interface:IFeatureService
     *
     * @description
     * This method registers contextual menu buttons. It is a wrapper around {@link smarteditServicesModule.ContextualMenuService#methods_addItems contextualMenuService.addItems}.
     *
     * @param {IContextualMenuButton} btn The {@link smarteditServicesModule.interface:IContextualMenuButton btn} that represents the feature to be registered.
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IFeatureService.prototype.addContextualMenuButton = function (btn) {
        'proxyFunction';
        return null;
    };
    IFeatureService.prototype._remoteEnablingFromInner = function (key) {
        'proxyFunction';
        return null;
    };
    IFeatureService.prototype._remoteDisablingFromInner = function (key) {
        'proxyFunction';
        return null;
    };
    /**
     * This method registers a feature, identified by a unique key, across the {@link smarteditCommonsModule.service:GatewayFactory gateway}.
     * It is a simplified version of the register method, from which callbacks have been removed.
     */
    IFeatureService.prototype._registerAliases = function (configuration) {
        'proxyFunction';
        return null;
    };
    IFeatureService.prototype._validate = function (configuration) {
        if (this.lodash.isEmpty(configuration.key)) {
            throw new Error("featureService.configuration.key.error.required");
        }
        if (this.lodash.isEmpty(configuration.nameI18nKey)) {
            throw new Error("featureService.configuration.nameI18nKey.error.required");
        }
        if (!this.lodash.isFunction(configuration.enablingCallback)) {
            throw new Error("featureService.configuration.enablingCallback.error.not.function");
        }
        if (!this.lodash.isFunction(configuration.disablingCallback)) {
            throw new Error("featureService.configuration.disablingCallback.error.not.function");
        }
    };
    return IFeatureService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IModalService.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IModalService.ts ***!
  \**********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/INotificationMouseLeaveDetectionService.ts":
/*!************************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/INotificationMouseLeaveDetectionService.ts ***!
  \************************************************************************************************/
/*! exports provided: INotificationMouseLeaveDetectionService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "INotificationMouseLeaveDetectionService", function() { return INotificationMouseLeaveDetectionService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:INotificationMouseLeaveDetectionService
 *
 * @description
 * The interface defines the methods required to detect when the mouse leaves the notification panel
 * in the SmartEdit application and in the SmartEdit container.
 *
 * It is solely meant to be used with the notificationService.
 */
var INotificationMouseLeaveDetectionService = /** @class */ (function () {
    function INotificationMouseLeaveDetectionService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:INotificationMouseLeaveDetectionService#startDetection
     * @methodOf smarteditServicesModule.interface:INotificationMouseLeaveDetectionService
     *
     * @description
     * This method starts tracking the movement of the mouse pointer in order to detect when it
     * leaves the notification panel.
     *
     * The innerBounds parameter is considered optional. If it is not provided, it will not be
     * validated and detection will only be started in the SmartEdit container.
     *
     * Here is an example of a bounds object:
     *
     * {
     *     x: 100,
     *     y: 100,
     *     width: 200,
     *     height: 50
     * }
     *
     * This method will throw an error if:
     *     - the bounds parameter is not provided
     *     - a bounds object does not contain the X coordinate
     *     - a bounds object does not contain the Y coordinate
     *     - a bounds object does not contain the width dimension
     *     - a bounds object does not contain the height dimension
     */
    INotificationMouseLeaveDetectionService.prototype.startDetection = function (outerBounds, innerBounds, callback) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:INotificationMouseLeaveDetectionService#stopDetection
     * @methodOf smarteditServicesModule.interface:INotificationMouseLeaveDetectionService
     *
     * @description
     * This method stops tracking the movement of the mouse pointer.
     */
    INotificationMouseLeaveDetectionService.prototype.stopDetection = function () {
        'proxyFunction';
        return null;
    };
    /**
     * This method is used to start tracking the movement of the mouse pointer within the iFrame.
     */
    INotificationMouseLeaveDetectionService.prototype._remoteStartDetection = function (bound) {
        'proxyFunction';
        return null;
    };
    /**
     * This method is used to stop tracking the movement of the mouse pointer within the iFrame.
     */
    INotificationMouseLeaveDetectionService.prototype._remoteStopDetection = function () {
        'proxyFunction';
        return null;
    };
    /**
     * This method is used to call the callback function when it is detected from within the iFrame that
     * the mouse left the notification panel
     */
    INotificationMouseLeaveDetectionService.prototype._callCallback = function () {
        'proxyFunction';
        return null;
    };
    /**
     * This method is called for each mouse movement. It evaluates whether or not the
     * mouse pointer is in the notification panel. If it isn't, it calls the onMouseLeave.
     */
    INotificationMouseLeaveDetectionService.prototype._onMouseMove = function (event) {
        var _this = this;
        this._getBounds().then(function (bounds) {
            var isOutsideX = bounds && event && (event.clientX < bounds.x || event.clientX > bounds.x + bounds.width);
            var isOutsideY = bounds && event && (event.clientY < bounds.y || event.clientY > bounds.y + bounds.height);
            if (isOutsideX || isOutsideY) {
                _this._onMouseLeave();
            }
        });
    };
    /**
     * This method gets bounds
     */
    INotificationMouseLeaveDetectionService.prototype._getBounds = function () {
        'proxyFunction';
        return null;
    };
    /**
     * This method gets callback
     */
    INotificationMouseLeaveDetectionService.prototype._getCallback = function () {
        'proxyFunction';
        return null;
    };
    /**
     * This method is triggered when the service has detected that the mouse left the
     * notification panel. It will execute the callback function and stop detection.
     */
    INotificationMouseLeaveDetectionService.prototype._onMouseLeave = function () {
        var _this = this;
        this._getCallback().then(function (callback) {
            if (callback) {
                callback();
                _this.stopDetection();
            }
            else {
                _this._callCallback().then(function () {
                    _this.stopDetection();
                });
            }
        });
    };
    return INotificationMouseLeaveDetectionService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/INotificationService.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/INotificationService.ts ***!
  \*****************************************************************************/
/*! exports provided: INotificationService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "INotificationService", function() { return INotificationService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:INotificationService
 *
 * @description
 * INotificationService provides a service to display visual cues to inform
 * the user of the state of the application in the container or the iFramed application.
 * The interface defines the methods required to manage notifications that are to be displayed to the user.
 */
var INotificationService = /** @class */ (function () {
    function INotificationService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:INotificationService#pushNotification
     * @methodOf smarteditServicesModule.interface:INotificationService
     *
     * @description
     * This method creates a new notification based on the given configuration and
     * adds it to the top of the list.
     *
     * The configuration must contain either a template or template URL, but not both.
     *
     * @param {Object} configuration The notification's configuration {@link smarteditServicesModule.interface:INotificationConfiguration INotificationConfiguration}
     *
     * @throws An error if no configuration is given.
     * @throws An error if the configuration does not contain a unique identifier.
     * @throws An error if the configuration's unique identifier is an empty string.
     * @throws An error if the configuration does not contain a template or templateUrl.
     * @throws An error if the configuration contains both a template and template Url.
     */
    INotificationService.prototype.pushNotification = function (configuration) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:INotificationService#removeNotification
     * @methodOf smarteditServicesModule.interface:INotificationService
     *
     * @description
     * This method removes the notification with the given ID from the list.
     *
     * @param {String} notificationId The notification's unique identifier.
     */
    INotificationService.prototype.removeNotification = function (notificationId) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:INotificationService#removeAllNotifications
     * @methodOf smarteditServicesModule.interface:INotificationService
     *
     * @description
     * This method removes all notifications.
     */
    INotificationService.prototype.removeAllNotifications = function () {
        'proxyFunction';
        return null;
    };
    return INotificationService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IPageInfoService.ts":
/*!*************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IPageInfoService.ts ***!
  \*************************************************************************/
/*! exports provided: IPageInfoService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IPageInfoService", function() { return IPageInfoService; });
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IPageInfoService
 *
 * @description
 * The IPageInfoService provides information about the storefront page currently loaded in the iFrame.
 */
var IPageInfoService = /** @class */ (function () {
    function IPageInfoService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPageInfoService#getPageUID
     * @methodOf smarteditServicesModule.interface:IPageInfoService
     *
     * @description
     * This extracts the pageUID of the storefront page loaded in the smartedit iframe.
     *
     * @return {angular.IPromise<string>} A promise resolving to a string matching the page's ID
     */
    IPageInfoService.prototype.getPageUID = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPageInfoService#getPageUUID
     * @methodOf smarteditServicesModule.interface:IPageInfoService
     *
     * @description
     * This extracts the pageUUID of the storefront page loaded in the smartedit iframe.
     * The UUID is different from the UID in that it is an encoding of uid and catalog version combined
     *
     * @return {angular.IPromise<string>} A promise resolving to the page's UUID
     */
    IPageInfoService.prototype.getPageUUID = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPageInfoService#getCatalogVersionUUIDFromPage
     * @methodOf smarteditServicesModule.interface:IPageInfoService
     *
     * @description
     * This extracts the catalogVersionUUID of the storefront page loaded in the smartedit iframe.
     * The UUID is different from the UID in that it is an encoding of uid and catalog version combined
     *
     * @return {angular.IPromise<string>} A promise resolving to the page's UUID
     */
    IPageInfoService.prototype.getCatalogVersionUUIDFromPage = function () {
        'proxyFunction';
        return null;
    };
    return IPageInfoService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IPermissionService.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IPermissionService.ts ***!
  \***************************************************************************/
/*! exports provided: IPermissionService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IPermissionService", function() { return IPermissionService; });
var prepareRuleConfiguration = function (ruleConfiguration) {
    this.ruleVerifyFunctions = this.ruleVerifyFunctions || {};
    this.ruleVerifyFunctions[ruleConfiguration.names.join("-")] = {
        verify: ruleConfiguration.verify
    };
    delete ruleConfiguration.verify;
    return ruleConfiguration;
};
var validateRule = function (rule) {
    if (!(rule.names instanceof Array)) {
        throw Error("Rule names must be array");
    }
    if (rule.names.length < 1) {
        throw Error("Rule requires at least one name");
    }
    if (!rule.verify) {
        throw Error("Rule requires a verify function");
    }
    if (typeof rule.verify !== 'function') {
        throw Error("Rule verify must be a function");
    }
};
/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:PermissionServiceInterface
 *
 * @description
 * The permission service is used to check if a user has been granted certain permissions.
 *
 * It is configured with rules and permissions. A rule is used to execute some logic to determine whether or not
 * the permission should be granted. A permission references a list of rules. In order for a permission to be
 * granted, each rule must be executed successfully and return true.
 */
var IPermissionService = /** @class */ (function () {
    function IPermissionService() {
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#clearCache
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method clears all cached results in the rules' caches.
     */
    IPermissionService.prototype.clearCache = function () {
        'proxyFunction';
        return;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#getPermission
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method returns the registered permission that contains the given name in its
     * array of names.
     *
     * @param {String} permissionName The name of the permission to lookup.
     *
     * @returns {Object} rule The permission with the given name, undefined otherwise.
     */
    IPermissionService.prototype.getPermission = function (permission) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#isPermitted
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method checks if a user has been granted certain permissions.
     *
     * It takes an array of permission objects structured as follows:
     *
     * {
     *     names: ["permission.aliases"],
     *     context: {
     *         data: "required to check a permission"
     *     }
     * }
     *
     * @param {Object[]} permissions A list of permission objects.
     *
     * @returns {IPromise} A promise that resolves to true if permission is granted, rejects to false if it isn't and rejects on error.
     */
    IPermissionService.prototype.isPermitted = function (permissions) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#registerPermission
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method registers a permission.
     *
     * A permission is defined by a set of aliases and rules. It is verified by its set of rules.
     * The set of aliases is there for convenience, as there may be different permissions
     * that use the same set of rules to be verified. The permission aliases property
     * will resolve if any one alias is in the aliases' array. Calling {@link smarteditCommonsModule.service:PermissionServiceInterface#isPermitted}
     * with any of these aliases will use the same permission object, therefore the same
     * combination of rules to check if the user has the appropriate clearance. This reduces the
     * number of permissions you need to register.
     *
     * @param {Object} configuration The configuration of the permission to register.
     * @param {String[]} configuration.aliases The list of aliases associated to the permission. A permission alias must be prefixed by at least one
     * namespace followed by a "." character to be valid. i.e. "se.fake.permission"
     * @param {String[]} configuration.rules The list of the names of the rules used to verify.
     *
     * @throws Will throw an error if the permission has no aliases array
     * @throws Will throw an error if the permission's aliases array is empty
     * @throws Will throw an error if the permission has no rules array
     * @throws Will throw an error if the permission's rule aliases array is empty
     * @throws Will throw an error if a permission is already registered with a common entry in its array of aliases
     * @throws Will throw an error if one of the permission's aliases is not name spaced
     * @throws Will throw an error if no rule is registered with on of the permission's rule names
     */
    IPermissionService.prototype.registerPermission = function (permission) {
        'proxyFunction';
        return;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#registerRule
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method registers a rule. These rules can be used by registering permissions that
     * use them to verify if a user has the appropriate clearance.
     *
     * To avoid accidentally overriding the default rule, an error is thrown when attempting
     * to register a rule with the {@link smarteditServicesModule.object:DEFAULT_RULE_NAME
     * default rule name}.
     *
     * To register the default rule, see {@link smarteditCommonsModule.service:PermissionServiceInterface#registerDefaultRule}.
     *
     * @param {Object} ruleConfiguration The configuration of the rule to register.
     * @param {String[]} ruleConfiguration.names The list of names associated to the rule.
     * @param {Function} ruleConfiguration.verify The verification function of the rule. It must return a promise that responds with true, false, or an error.
     *
     * @throws Will throw an error if the list of rule names contains the reserved {@link smarteditServicesModule.object:DEFAULT_RULE_NAME default rule name}.
     * @throws Will throw an error if the rule has no names array.
     * @throws Will throw an error if the rule's names array is empty.
     * @throws Will throw an error if the rule has no verify function.
     * @throws Will throw an error if the rule's verify parameter is not a function.
     * @throws Will throw an error if a rule is already registered with a common entry in its names array
     */
    IPermissionService.prototype.registerRule = function (ruleConfiguration) {
        validateRule(ruleConfiguration);
        ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
        this._registerRule(ruleConfiguration);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#registerDefaultRule
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method registers the default rule.
     *
     * The default rule is used when no permission is found for a given permission name when
     * {@link smarteditCommonsModule.service:PermissionServiceInterface#isPermitted} is called.
     *
     * @param {Object} ruleConfiguration The configuration of the default rule.
     * @param {String[]} ruleConfiguration.names The list of names associated to the default rule (must contain {@link smarteditServicesModule.object:DEFAULT_RULE_NAME}).
     * @param {Function} ruleConfiguration.verify The verification function of the default rule.
     *
     * @throws Will throw an error if the default rule's names does not contain {@link smarteditServicesModule.object:DEFAULT_RULE_NAME}
     * @throws Will throw an error if the default rule has no names array.
     * @throws Will throw an error if the default rule's names array is empty.
     * @throws Will throw an error if the default rule has no verify function.
     * @throws Will throw an error if the default rule's verify parameter is not a function.
     * @throws Will throw an error if a rule is already registered with a common entry in its names array
     */
    IPermissionService.prototype.registerDefaultRule = function (ruleConfiguration) {
        ruleConfiguration = prepareRuleConfiguration.bind(this)(ruleConfiguration);
        this._registerDefaultRule(ruleConfiguration);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:PermissionServiceInterface#unregisterDefaultRule
     * @methodOf smarteditCommonsModule.service:PermissionServiceInterface
     *
     * @description
     * This method un-registers the default rule.
     */
    IPermissionService.prototype.unregisterDefaultRule = function () {
        'proxyFunction';
        return;
    };
    IPermissionService.prototype._registerRule = function (ruleConfiguration) {
        'proxyFunction';
        return;
    };
    IPermissionService.prototype._registerDefaultRule = function (ruleConfiguration) {
        'proxyFunction';
        return;
    };
    return IPermissionService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IPreviewService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IPreviewService.ts ***!
  \************************************************************************/
/*! exports provided: IPreviewService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IPreviewService", function() { return IPreviewService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @name smarteditServicesModule.interface:IPreviewService
 *
 * @description
 *
 * Interface for previewService.
 *
 * This service is for managing the storefront preview ticket and is proxied across the gateway. (implements)
 *
 */
var IPreviewService = /** @class */ (function () {
    function IPreviewService(urlUtils) {
        this.urlUtils = urlUtils;
    }
    /**
     * @name smarteditServicesModule.interface:IPreviewService#createPreview
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will create a new previewTicket for the given experience, using the preview API
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {Object} previewData Data representing storefront preview
     *
     * @returns {Object} A {@link smarteditServicesModule.interface:IPreview IPreviewResponse} object
     */
    IPreviewService.prototype.createPreview = function (previewData) {
        'proxyFunction';
        return null;
    };
    /**
     * @name smarteditServicesModule.interface:IPreviewService#getResourcePathFromPreviewUrl
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will preduce a resourcePath from a given preview url
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {Object} previewUrl A URL for a storefornt with preview
     *
     * @returns {Object} A {@link smarteditServicesModule.interface:IPreview IPreviewResponse} object
     */
    IPreviewService.prototype.getResourcePathFromPreviewUrl = function (previewUrl) {
        'proxyFunction';
        return null;
    };
    /**
     * @name smarteditServicesModule.interface:IPreviewService#updateUrlWithNewPreviewTicketId
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will create a new preview ticket, and return the given url with an updated previewTicketId query param
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {string} storefrontUrl Existing storefront url
     * @param {Object} previewData JSON representing storefront previewData (catalog, catalaog vesion, etc...)
     *
     * @returns {string} A new string with storefrontUrl having the new ticket ID inside
     */
    IPreviewService.prototype.updateUrlWithNewPreviewTicketId = function (storefrontUrl, previewData) {
        var _this = this;
        return this.createPreview(previewData).then(function (preview) {
            return _this.urlUtils.updateUrlParameter(storefrontUrl, 'cmsTicketId', preview.previewTicketId);
        });
    };
    IPreviewService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
    ], IPreviewService);
    return IPreviewService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IPrioritized.ts":
/*!*********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IPrioritized.ts ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IReflectable.ts":
/*!*********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IReflectable.ts ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/ISessionService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/ISessionService.ts ***!
  \************************************************************************/
/*! exports provided: ISessionService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ISessionService", function() { return ISessionService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISessionService
 * @description
 * The ISessionService provides information related to the current session
 * and the authenticated user (including a user readable and writeable languages).
 */
var ISessionService = /** @class */ (function () {
    function ISessionService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the username, previously mentioned as "principalUID",
     * associated to the authenticated user.
     *
     * @returns {angular.IPromise<string>} A promise resolving to the username,
     * previously mentioned as "principalUID", associated to the
     * authenticated user.
     */
    ISessionService.prototype.getCurrentUsername = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUserDisplayName
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the displayed name associated to the authenticated user.
     *
     * @returns {angular.IPromise<string>} A promise resolving to the displayed name
     * associated to the authenticated user.
     */
    ISessionService.prototype.getCurrentUserDisplayName = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUser
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the data of the current authenticated user.
     * Also note that as part of the User object returned by this method contains
     * the list of readable and writeable languages available to the user.
     *
     * @returns {angular.IPromise<User>} A promise resolving to the data of the current
     * authenticated user.
     */
    ISessionService.prototype.getCurrentUser = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#hasUserChanged
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns boolean indicating whether the current user is different from
     * the last authenticated one.
     *
     * @returns {angular.IPromise<boolean>} Boolean indicating whether the current user is
     * different from the last authenticated one.
     */
    ISessionService.prototype.hasUserChanged = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#resetCurrentUserData
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Reset all data associated to the authenticated user.
     * to the authenticated user.
     *
     * @return {angular.IPromise<void>} returns an empty promise.
     */
    ISessionService.prototype.resetCurrentUserData = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#setCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Set the username, previously mentioned as "principalUID", associated
     * to the authenticated user.
     *
     * @param {String} currentUsername Username, previously mentioned as
     * "principalUID", associated to the authenticated user.
     *
     * @return {angular.IPromise<void>} returns an empty promise.
     */
    ISessionService.prototype.setCurrentUsername = function (username) {
        'proxyFunction';
        return null;
    };
    return ISessionService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/ISharedDataService.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/ISharedDataService.ts ***!
  \***************************************************************************/
/*! exports provided: ISharedDataService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ISharedDataService", function() { return ISharedDataService; });
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISharedDataService
 *
 * @description
 * Provides an abstract extensible shared data service. Used to store any data to be used either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
var ISharedDataService = /** @class */ (function () {
    function ISharedDataService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#get
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Get the data for the given key.
     *
     * @param {String} key The key of the data to fetch
     */
    ISharedDataService.prototype.get = function (key) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#set
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Set data for the given key.
     *
     * @param {String} key The key of the data to set
     * @param {object} value The value of the data to set
     */
    ISharedDataService.prototype.set = function (key, value) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISharedDataService#update
     * @methodOf smarteditServicesModule.interface:ISharedDataService
     *
     * @description
     * Convenience method to retrieve and modify on the fly the content stored under a given key
     *
     * @param {String} key The key of the data to store
     * @param {Function} modifyingCallback callback fed with the value stored under the given key. The callback must return the new value of the object to update.
     */
    ISharedDataService.prototype.update = function (key, modifyingCallback) {
        'proxyFunction';
        return null;
    };
    return ISharedDataService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IStorageService.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IStorageService.ts ***!
  \************************************************************************/
/*! exports provided: IStorageService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IStorageService", function() { return IStorageService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IStorageService
 * @description
 * Interface for StorageService
 */
var IStorageService = /** @class */ (function () {
    function IStorageService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#isInitialized
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to determine if the storage service has been initialized properly. It
     * makes sure that the smartedit-sessions cookie is available in the browser.
     *
     * @returns {Boolean} Indicates if the storage service was properly initialized.
     */
    IStorageService.prototype.isInitialized = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @deprecated since 6.6
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#storePrincipalIdentifier
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to store the principal's login name in storage service. Deprecated since 6.6.
     *
     * @param {String} principalUID Value associated to store principal's login.
     */
    IStorageService.prototype.storePrincipalIdentifier = function (principalUID) {
        'proxyFunction';
        return null;
    };
    /**
     * @deprecated since 6.6
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#removePrincipalIdentifier
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to remove the principal's UID from storage service. Deprecated since 6.6.
     *
     */
    IStorageService.prototype.removePrincipalIdentifier = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @deprecated since 6.6
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#getPrincipalIdentifier
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to retrieve the principal's login name from storage service. Deprecated since 6.6.
     *
     * @returns {String} principalNameValue principal's name associated with the key.
     */
    IStorageService.prototype.getPrincipalIdentifier = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#storeAuthToken
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method creates and stores a new key/value entry. It associates an authentication token with a
     * URI.
     *
     * @param {String} authURI The URI that identifies the resource(s) to be authenticated with the authToken. Will be used as a key.
     * @param {String} auth The token to be used to authenticate the user in the provided URI.
     */
    IStorageService.prototype.storeAuthToken = function (authURI, auth) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#getAuthToken
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to retrieve the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which the associated authToken is to be retrieved.
     * @returns {String} The authToken used to authenticate the current user in the provided URI.
     */
    IStorageService.prototype.getAuthToken = function (authURI) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#removeAuthToken
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * Removes the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which its authToken is to be removed.
     */
    IStorageService.prototype.removeAuthToken = function (authURI) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#removeAllAuthTokens
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method removes all authURI/authToken key/pairs from the storage service.
     */
    IStorageService.prototype.removeAllAuthTokens = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#getValueFromCookie
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * Retrieves the value stored in the cookie identified by the provided name.
     */
    IStorageService.prototype.getValueFromCookie = function (cookieName, isEncoded) {
        'proxyFunction';
        return null;
    };
    IStorageService.prototype.putValueInCookie = function (cookieName, value, encode) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#setItem
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * This method is used to store the item.
     *
     * @param {String} key The key of the item.
     * @param {any} value The value of the item.
     */
    IStorageService.prototype.setItem = function (key, value) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IStorageService#getItem
     * @methodOf smarteditServicesModule.interface:IStorageService
     *
     * @description
     * Retrieves the value for a given key.
     *
     * @param {String} key The key of the item.
     *
     * @returns {Promise<any>} A promise that resolves to the item value.
     */
    IStorageService.prototype.getItem = function (key) {
        'proxyFunction';
        return null;
    };
    return IStorageService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IToolbarItem.ts":
/*!*********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IToolbarItem.ts ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IURIBuilder.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IURIBuilder.ts ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IUriContext.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IUriContext.ts ***!
  \********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IUrlService.ts":
/*!********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IUrlService.ts ***!
  \********************************************************************/
/*! exports provided: IUrlService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IUrlService", function() { return IUrlService; });
var IUrlService = /** @class */ (function () {
    function IUrlService(PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {
        this.PAGE_CONTEXT_SITE_ID = PAGE_CONTEXT_SITE_ID;
        this.PAGE_CONTEXT_CATALOG = PAGE_CONTEXT_CATALOG;
        this.PAGE_CONTEXT_CATALOG_VERSION = PAGE_CONTEXT_CATALOG_VERSION;
        this.CONTEXT_SITE_ID = CONTEXT_SITE_ID;
        this.CONTEXT_CATALOG = CONTEXT_CATALOG;
        this.CONTEXT_CATALOG_VERSION = CONTEXT_CATALOG_VERSION;
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IUrlService#openUrlInPopup
     * @methodOf smarteditServicesModule.interface:IUrlService
     *
     * @description
     * Opens a given URL in a new browser pop up without authentication.
     *
     * @param {String} url - the URL we wish to open.
     */
    IUrlService.prototype.openUrlInPopup = function (url) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IUrlService#path
     * @methodOf smarteditServicesModule.interface:IUrlService
     *
     * @description
     * Navigates to the given path in the same browser tab.
     *
     * @param {String} path - the path we wish to navigate to.
     */
    IUrlService.prototype.path = function (path) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IUrlService#buildUriContext
     * @methodOf smarteditServicesModule.interface:IUrlService
     *
     * @description
     * Returns a uri context array populated with the given siteId, catalogId and catalogVersion information
     *
     * @param {String} siteId - site Id
     * @param {String} catalogId - catalog Id
     * @param {String} catalogVersion - catalog version
     *
     * @return {IUriContext} uri context array
     */
    IUrlService.prototype.buildUriContext = function (siteId, catalogId, catalogVersion) {
        var uriContext = {};
        uriContext[this.CONTEXT_SITE_ID] = siteId;
        uriContext[this.CONTEXT_CATALOG] = catalogId;
        uriContext[this.CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IUrlService#buildPageUriContext
     * @methodOf smarteditServicesModule.interface:IUrlService
     *
     * @description
     * Returns a page uri context array populated with the given siteId, catalogId and catalogVersion information
     *
     * @param {String} siteId - site Id
     * @param {String} catalogId - catalog Id
     * @param {String} catalogVersion - catalog version
     *
     * @return {IUriContext} uri context array
     */
    IUrlService.prototype.buildPageUriContext = function (siteId, catalogId, catalogVersion) {
        var uriContext = {};
        uriContext[this.PAGE_CONTEXT_SITE_ID] = siteId;
        uriContext[this.PAGE_CONTEXT_CATALOG] = catalogId;
        uriContext[this.PAGE_CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    };
    return IUrlService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/IWaitDialogService.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/IWaitDialogService.ts ***!
  \***************************************************************************/
/*! exports provided: IWaitDialogService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IWaitDialogService", function() { return IWaitDialogService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IWaitDialogService
 *
 * @description
 * This service be used in order to display (or hide) a 'loading' overlay. The overlay should display on top of everything, preventing
 * the user from doing any action until the overlay gets hidden.
 */
var IWaitDialogService = /** @class */ (function () {
    function IWaitDialogService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IWaitDialogService#showWaitModal
     * @methodOf smarteditServicesModule.interface:IWaitDialogService
     *
     * @description
     * This method can be called to display the loading overlay.
     *
     * @param {String} [customLoadingMessageLocalizedKey="se.wait.dialog.message"] The i18n key that corresponds to the message to be displayed.
     */
    IWaitDialogService.prototype.showWaitModal = function (customLoadingMessageLocalizedKey) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IWaitDialogService#hideWaitModal
     * @methodOf smarteditServicesModule.interface:IWaitDialogService
     *
     * @description
     * Removes the loading overlay.
     */
    IWaitDialogService.prototype.hideWaitModal = function () {
        'proxyFunction';
        return null;
    };
    return IWaitDialogService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/interfaces/index.ts":
/*!**************************************************************!*\
  !*** ./jsTarget/web/app/common/services/interfaces/index.ts ***!
  \**************************************************************/
/*! exports provided: SeAlertServiceType, IExperienceService, IFeatureService, INotificationMouseLeaveDetectionService, INotificationService, IPageInfoService, IPreviewService, ISessionService, ISharedDataService, IStorageService, IUrlService, IWaitDialogService, ICatalogService, IPermissionService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _IAlertService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./IAlertService */ "./jsTarget/web/app/common/services/interfaces/IAlertService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeAlertServiceType", function() { return _IAlertService__WEBPACK_IMPORTED_MODULE_0__["SeAlertServiceType"]; });

/* harmony import */ var _ICatalogService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./ICatalogService */ "./jsTarget/web/app/common/services/interfaces/ICatalogService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ICatalogService", function() { return _ICatalogService__WEBPACK_IMPORTED_MODULE_1__["ICatalogService"]; });

/* harmony import */ var _IExperienceService__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./IExperienceService */ "./jsTarget/web/app/common/services/interfaces/IExperienceService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IExperienceService", function() { return _IExperienceService__WEBPACK_IMPORTED_MODULE_2__["IExperienceService"]; });

/* harmony import */ var _IFeatureService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./IFeatureService */ "./jsTarget/web/app/common/services/interfaces/IFeatureService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IFeatureService", function() { return _IFeatureService__WEBPACK_IMPORTED_MODULE_3__["IFeatureService"]; });

/* harmony import */ var _INotificationMouseLeaveDetectionService__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./INotificationMouseLeaveDetectionService */ "./jsTarget/web/app/common/services/interfaces/INotificationMouseLeaveDetectionService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationMouseLeaveDetectionService", function() { return _INotificationMouseLeaveDetectionService__WEBPACK_IMPORTED_MODULE_4__["INotificationMouseLeaveDetectionService"]; });

/* harmony import */ var _INotificationService__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./INotificationService */ "./jsTarget/web/app/common/services/interfaces/INotificationService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "INotificationService", function() { return _INotificationService__WEBPACK_IMPORTED_MODULE_5__["INotificationService"]; });

/* harmony import */ var _IPageInfoService__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./IPageInfoService */ "./jsTarget/web/app/common/services/interfaces/IPageInfoService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPageInfoService", function() { return _IPageInfoService__WEBPACK_IMPORTED_MODULE_6__["IPageInfoService"]; });

/* harmony import */ var _IPreviewService__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./IPreviewService */ "./jsTarget/web/app/common/services/interfaces/IPreviewService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPreviewService", function() { return _IPreviewService__WEBPACK_IMPORTED_MODULE_7__["IPreviewService"]; });

/* harmony import */ var _ISessionService__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./ISessionService */ "./jsTarget/web/app/common/services/interfaces/ISessionService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISessionService", function() { return _ISessionService__WEBPACK_IMPORTED_MODULE_8__["ISessionService"]; });

/* harmony import */ var _ISharedDataService__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./ISharedDataService */ "./jsTarget/web/app/common/services/interfaces/ISharedDataService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ISharedDataService", function() { return _ISharedDataService__WEBPACK_IMPORTED_MODULE_9__["ISharedDataService"]; });

/* harmony import */ var _IStorageService__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./IStorageService */ "./jsTarget/web/app/common/services/interfaces/IStorageService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IStorageService", function() { return _IStorageService__WEBPACK_IMPORTED_MODULE_10__["IStorageService"]; });

/* harmony import */ var _IUrlService__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./IUrlService */ "./jsTarget/web/app/common/services/interfaces/IUrlService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IUrlService", function() { return _IUrlService__WEBPACK_IMPORTED_MODULE_11__["IUrlService"]; });

/* harmony import */ var _IWaitDialogService__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./IWaitDialogService */ "./jsTarget/web/app/common/services/interfaces/IWaitDialogService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IWaitDialogService", function() { return _IWaitDialogService__WEBPACK_IMPORTED_MODULE_12__["IWaitDialogService"]; });

/* harmony import */ var _IPermissionService__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./IPermissionService */ "./jsTarget/web/app/common/services/interfaces/IPermissionService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IPermissionService", function() { return _IPermissionService__WEBPACK_IMPORTED_MODULE_13__["IPermissionService"]; });

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
















/***/ }),

/***/ "./jsTarget/web/app/common/services/language/LanguageService.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/language/LanguageService.ts ***!
  \**********************************************************************/
/*! exports provided: LanguageService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LanguageService", function() { return LanguageService; });
/* harmony import */ var _cache__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../cache */ "./jsTarget/web/app/common/services/cache/index.ts");
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var _httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};



/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:LanguageService
 */
var LanguageService = /** @class */ (function () {
    /** @internal */
    function LanguageService($log, $translate, $q, languageServiceGateway, crossFrameEventService, browserService, storageService, SWITCH_LANGUAGE_EVENT, SELECTED_LANGUAGE, LANGUAGE_RESOURCE_URI, I18N_LANGUAGES_RESOURCE_URI, restServiceFactory) {
        this.$log = $log;
        this.$translate = $translate;
        this.$q = $q;
        this.languageServiceGateway = languageServiceGateway;
        this.crossFrameEventService = crossFrameEventService;
        this.browserService = browserService;
        this.storageService = storageService;
        this.SWITCH_LANGUAGE_EVENT = SWITCH_LANGUAGE_EVENT;
        this.SELECTED_LANGUAGE = SELECTED_LANGUAGE;
        this.initDeferred = this.$q.defer();
        this.languageRestService = restServiceFactory.get(LANGUAGE_RESOURCE_URI);
        this.i18nLanguageRestService = restServiceFactory.get(I18N_LANGUAGES_RESOURCE_URI);
    }
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getBrowserLanguageIsoCode
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @deprecated since 1808
     *
     * @description
     * Uses the browser's current locale to determine the selected language ISO code.
     *
     * @returns {String} The language ISO code of the browser's currently selected locale.
     */
    LanguageService.prototype.getBrowserLanguageIsoCode = function () {
        return window.navigator.language.split('-')[0];
    };
    LanguageService.prototype.setInitialized = function (initialized) {
        initialized ? this.initDeferred.resolve() : this.initDeferred.reject();
    };
    LanguageService.prototype.isInitialized = function () {
        return this.initDeferred.promise;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getBrowserLocale
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @deprecated since 1808 - use browserService instead.
     *
     * @description
     * determines the browser locale in the format en_US
     *
     * @returns {string} the browser locale
     */
    LanguageService.prototype.getBrowserLocale = function () {
        return this.browserService.getBrowserLocale();
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getResolveLocale
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale. It determines in the
     * following order:
     *
     * 1. Check if the user has previously selected the language
     * 2. Check if the user browser locale is supported in the system
     *
     * @returns {angular.IPromise<string>} the locale
     */
    LanguageService.prototype.getResolveLocale = function () {
        return this.$q.when(this._getDefaultLanguage());
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getResolveLocaleIsoCode
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale ISO code. i.e.: If the selected tooling language is 'en_US',
     * the resolved value will be 'en'.
     *
     * @returns {angular.IPromise<string>} A promise that resolves to the isocode of the tooling language.
     */
    LanguageService.prototype.getResolveLocaleIsoCode = function () {
        var _this = this;
        return this.getResolveLocale().then(function (resolveLocale) {
            return _this.convertBCP47TagToJavaTag(resolveLocale).split('_')[0];
        });
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getLanguagesForSite
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Fetches a list of language descriptors for the specified storefront site UID.
     * The object containing the list of sites is fetched using REST calls to the cmswebservices languages API.
     *
     * @param {string} siteUID the site unique identifier.
     *
     * @returns {angular.IPromise<ILanguage[]>} A promise that resolves to an array of ILanguage.
     */
    LanguageService.prototype.getLanguagesForSite = function (siteUID) {
        var _this = this;
        return this.languageRestService.get({
            siteUID: siteUID
        }).then(function (languagesList) {
            return languagesList.languages;
        }, function (error) {
            _this.$log.error('LanguageService.getLanguagesForSite() - Error loading languages');
            return _this.$q.reject(error);
        });
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#getToolingLanguages
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Retrieves a list of language descriptors using REST calls to the smarteditwebservices i18n API.
     *
     * @returns {angular.IPromise<IToolingLanguage[]>} A promise that resolves to an array of IToolingLanguage.
     */
    LanguageService.prototype.getToolingLanguages = function () {
        var _this = this;
        return this.i18nLanguageRestService.get({}).then(function (response) {
            return _this.$q.when(response.languages);
        }, function (error) {
            _this.$log.error('LanguageService.getToolingLanguages() - Error loading tooling languages');
            return _this.$q.reject(error);
        });
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#setSelectedToolingLanguage
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Set the user preference language in the storage service
     *
     * @param {IToolingLanguage} language the language object to be saved.
     */
    LanguageService.prototype.setSelectedToolingLanguage = function (language) {
        this.storageService.putValueInCookie(this.SELECTED_LANGUAGE, language, false);
        this.$translate.use(language.isoCode);
        this.languageServiceGateway.publish(this.SWITCH_LANGUAGE_EVENT, {
            isoCode: language.isoCode
        });
        this.crossFrameEventService.publish(this.SWITCH_LANGUAGE_EVENT);
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#registerSwitchLanguage
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Register a callback function to the gateway in order to switch the tooling language
     */
    LanguageService.prototype.registerSwitchLanguage = function () {
        var _this = this;
        this.languageServiceGateway.subscribe(this.SWITCH_LANGUAGE_EVENT, function (eventId, language) { return _this.$translate.use(language.isoCode); });
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#convertBCP47TagToJavaTag
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Method converts the BCP47 language tag representing the locale to the default java representation.
     * For example, method converts "en-US" to "en_US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in java representation
     */
    LanguageService.prototype.convertBCP47TagToJavaTag = function (languageTag) {
        return !!languageTag ? languageTag.replace(/-/g, '_') : languageTag;
    };
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:LanguageService#convertJavaTagToBCP47Tag
     * @methodOf smarteditCommonsModule.service:LanguageService
     *
     * @description
     * Method converts the default java language tag representing the locale to the BCP47 representation.
     * For example, method converts "en_US" to "en-US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in BCP47 representation
     */
    LanguageService.prototype.convertJavaTagToBCP47Tag = function (languageTag) {
        return !!languageTag ? languageTag.replace(/_/g, '-') : languageTag;
    };
    LanguageService.prototype._getDefaultLanguage = function () {
        var _this = this;
        return this.storageService.getValueFromCookie(this.SELECTED_LANGUAGE, false).then(function (lang) {
            return lang ? lang.isoCode : _this.browserService.getBrowserLocale();
        }, function () {
            return _this.browserService.getBrowserLocale();
        });
    };
    __decorate([
        Object(_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])({ actions: [_cache__WEBPACK_IMPORTED_MODULE_0__["rarelyChangingContent"]] })
    ], LanguageService.prototype, "getLanguagesForSite", null);
    __decorate([
        Object(_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])({ actions: [_cache__WEBPACK_IMPORTED_MODULE_0__["rarelyChangingContent"]] })
    ], LanguageService.prototype, "getToolingLanguages", null);
    LanguageService = __decorate([
        Object(_httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_2__["OperationContextRegistered"])('LANGUAGE_RESOURCE_URI', 'TOOLING'),
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"])()
        /* @ngInject */
    ], LanguageService);
    return LanguageService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/language/LanguageServiceGateway.ts":
/*!*****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/language/LanguageServiceGateway.ts ***!
  \*****************************************************************************/
/*! exports provided: LanguageServiceGateway */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LanguageServiceGateway", function() { return LanguageServiceGateway; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/** @internal */
var LanguageServiceGateway = /** @class */ (function () {
    function LanguageServiceGateway(gatewayFactory) {
        return gatewayFactory.createGateway('languageSwitch');
    }
    LanguageServiceGateway = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], LanguageServiceGateway);
    return LanguageServiceGateway;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/perspectives/IPerspectiveService.ts":
/*!******************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/perspectives/IPerspectiveService.ts ***!
  \******************************************************************************/
/*! exports provided: IPerspectiveService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IPerspectiveService", function() { return IPerspectiveService; });
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
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IPerspectiveService
 *
 * @description
 * Interface for Perspective Service
 */
var IPerspectiveService = /** @class */ (function () {
    function IPerspectiveService() {
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#register
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method registers a perspective.
     * When an end user selects a perspective in the SmartEdit web application,
     * all features bound to the perspective will be enabled when their respective enablingCallback functions are invoked
     * and all features not bound to the perspective will be disabled when their respective disablingCallback functions are invoked.
     *
     * @param {Object} configuration The perspective's configuration {@link smarteditServicesModule.interface:IPerspective IPerspective}
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IPerspectiveService.prototype.register = function (configuration) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#switchTo
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method activates a perspective identified by its key and deactivates the currently active perspective.
     * Activating a perspective consists in activating any feature that is bound to the perspective
     * or any feature that is bound to the perspective's referenced perspectives and deactivating any features
     * that are not bound to the perspective or to its referenced perspectives.
     * After the perspective is changed, the {@link seConstantsModule.object:EVENT_PERSPECTIVE_CHANGED
     * EVENT_PERSPECTIVE_CHANGED} event is published on the {@link smarteditCommonsModule.service:CrossFrameEventService
     * crossFrameEventService}, with no data.
     *
     * @param {String} key The key that uniquely identifies the perspective to be activated. This is the same key as the key used in the {@link smarteditServicesModule.interface:IPerspectiveService#methods_register register} method.
     * @return {angular.IPromise<void>} An empty promise
     */
    IPerspectiveService.prototype.switchTo = function (key) {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#hasActivePerspective
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method returns true if a perspective is selected.
     *
     * @returns {angular.IPromise<boolean>} A promise with the value of the key of the active perspective.
     */
    IPerspectiveService.prototype.hasActivePerspective = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#selectDefault
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method switches the currently-selected perspective to the default perspective.
     * It will also disable all features for the default perspective before enabling them all back.
     * If no value has been stored in the smartedit-perspectives cookie, the value of the default perspective is se.none.
     * If a value is stored in the cookie, that value is used as the default perspective.
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IPerspectiveService.prototype.selectDefault = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#isEmptyPerspectiveActive
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method returns true if the current active perspective is the Preview mode (No active overlay).
     *
     * @returns {angular.IPromise<boolean>} A promise with the boolean flag that indicates if the current perspective is the Preview mode.
     */
    IPerspectiveService.prototype.isEmptyPerspectiveActive = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#refreshPerspective
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method is used to refresh the prespective.
     * If there is an exising perspective set then it is refreshed by replaying all the features associated to the current perspective.
     * If there is no perspective set or if the perspective is not permitted then we set the default perspective.
     *
     * @return {angular.IPromise<void>} An empty promise
     */
    IPerspectiveService.prototype.refreshPerspective = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#getActivePerspectiveKey
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * 	This method returns the key of the perspective that is currently loaded.
     *
     * @returns {angular.IPromise<string>} A promise that resolves to the key of the current perspective loaded in the storefront, null otherwise.
     */
    IPerspectiveService.prototype.getActivePerspectiveKey = function () {
        'proxyFunction';
        return null;
    };
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:IPerspectiveService#isHotkeyEnabledForActivePerspective
     * @methodOf smarteditServicesModule.interface:IPerspectiveService
     *
     * @description
     * This method returns true if the active perspective has the hotkey enabled
     *
     * @returns {angular.IPromise<boolean>} A promise with the boolean flag that indicates if the current perspective has the hotkey enabled.
     */
    IPerspectiveService.prototype.isHotkeyEnabledForActivePerspective = function () {
        'proxyFunction';
        return null;
    };
    return IPerspectiveService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/AbstractCachedRestService.ts":
/*!****************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/AbstractCachedRestService.ts ***!
  \****************************************************************************/
/*! exports provided: AbstractCachedRestService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AbstractCachedRestService", function() { return AbstractCachedRestService; });
/* harmony import */ var smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/cache */ "./jsTarget/web/app/common/services/cache/index.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:AbstractCachedRestService
 *
 * @description
 * Base class to implement Cache enabled {@link smarteditServicesModule.interface:IRestService IRestServices}.
 * <br/>Implementing classes just need declare a class level {@link smarteditServicesModule.object:@CacheConfig @CacheConfig} annotation
 * with at least one {@link smarteditServicesModule.object:CacheAction CacheAction} and one {@link smarteditServicesModule.object:EvictionTag EvictionTag}.
 * <br/>Cache policies called by the set of {@link smarteditServicesModule.object:CacheAction CacheActions} will have access to
 * REST call response headers being added to the response under "headers" property.
 * <br/>Those headers are then stripped from the response.
 *
 * <h2>Usage</h2>
 * <pre>
 * &#64;CacheConfig({actions: [rarelyChangingContent], tags: [userEvictionTag]})
 * &#64;SeInjectable()
 * export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
 * 	constructor(restServiceFactory: IRestServiceFactory) {
 * 		super(restServiceFactory, '/productcatalogs');
 * 	}
 * }
 * </pre>
 */
var AbstractCachedRestService = /** @class */ (function () {
    function AbstractCachedRestService(restServiceFactory, uri, identifier) {
        this.innerRestService = restServiceFactory.get(uri, identifier);
        this.innerRestService.activateMetadata();
    }
    AbstractCachedRestService.prototype.getById = function (identifier) {
        return this.innerRestService.getById(identifier);
    };
    AbstractCachedRestService.prototype.get = function (searchParams) {
        return this.innerRestService.get(searchParams);
    };
    AbstractCachedRestService.prototype.query = function (searchParams) {
        return this.innerRestService.query(searchParams);
    };
    AbstractCachedRestService.prototype.page = function (searchParams) {
        return this.innerRestService.page(searchParams);
    };
    AbstractCachedRestService.prototype.update = function (payload) {
        return this.innerRestService.update(payload);
    };
    AbstractCachedRestService.prototype.remove = function (payload) {
        return this.innerRestService.remove(payload);
    };
    AbstractCachedRestService.prototype.save = function (payload) {
        return this.innerRestService.save(payload);
    };
    __decorate([
        StripResponseHeaders,
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])()
    ], AbstractCachedRestService.prototype, "getById", null);
    __decorate([
        StripResponseHeaders,
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])()
    ], AbstractCachedRestService.prototype, "get", null);
    __decorate([
        StripResponseHeaders,
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])()
    ], AbstractCachedRestService.prototype, "query", null);
    __decorate([
        StripResponseHeaders,
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["Cached"])()
    ], AbstractCachedRestService.prototype, "page", null);
    __decorate([
        StripResponseHeaders,
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["InvalidateCache"])()
    ], AbstractCachedRestService.prototype, "update", null);
    __decorate([
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["InvalidateCache"])()
    ], AbstractCachedRestService.prototype, "remove", null);
    __decorate([
        StripResponseHeaders
    ], AbstractCachedRestService.prototype, "save", null);
    return AbstractCachedRestService;
}());

function StripResponseHeaders(target, propertyName, descriptor) {
    var originalMethod = descriptor.value;
    descriptor.value = function () {
        return originalMethod.apply(this, arguments).then(function (response) {
            delete response.headers;
            return response;
        });
    };
}


/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/CommonsRestServiceModule.ts":
/*!***************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/CommonsRestServiceModule.ts ***!
  \***************************************************************************/
/*! exports provided: CommonsRestServiceModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CommonsRestServiceModule", function() { return CommonsRestServiceModule; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var smarteditcommons_services_rest_PermissionsRestService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/rest/PermissionsRestService */ "./jsTarget/web/app/common/services/rest/PermissionsRestService.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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


var CommonsRestServiceModule = /** @class */ (function () {
    function CommonsRestServiceModule() {
    }
    CommonsRestServiceModule = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeModule"])({
            providers: [
                smarteditcommons_services_rest_PermissionsRestService__WEBPACK_IMPORTED_MODULE_1__["PermissionsRestService"]
            ]
        })
    ], CommonsRestServiceModule);
    return CommonsRestServiceModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/IRestService.ts":
/*!***************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/IRestService.ts ***!
  \***************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/IRestServiceFactory.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/IRestServiceFactory.ts ***!
  \**********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/PermissionsRestService.ts":
/*!*************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/PermissionsRestService.ts ***!
  \*************************************************************************/
/*! exports provided: PermissionsRestService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PermissionsRestService", function() { return PermissionsRestService; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var PermissionsRestService = /** @class */ (function () {
    function PermissionsRestService(restServiceFactory) {
        this.URI = "/permissionswebservices/v1/permissions/principals/:user/global";
        this.resource = restServiceFactory.get(this.URI);
    }
    PermissionsRestService.prototype.get = function (queryData) {
        return this.resource.get(queryData).then(function (data) {
            return {
                permissions: data.permissions
            };
        });
    };
    PermissionsRestService = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], PermissionsRestService);
    return PermissionsRestService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/daos/ContentCatalogRestService.ts":
/*!*********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/daos/ContentCatalogRestService.ts ***!
  \*********************************************************************************/
/*! exports provided: ContentCatalogRestService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ContentCatalogRestService", function() { return ContentCatalogRestService; });
/* harmony import */ var smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/cache */ "./jsTarget/web/app/common/services/cache/index.ts");
/* harmony import */ var smarteditcommons_services_rest_AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/rest/AbstractCachedRestService */ "./jsTarget/web/app/common/services/rest/AbstractCachedRestService.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony import */ var smarteditcommons_services_httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! smarteditcommons/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts");
var __extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};




var CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API = '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs';
var ContentCatalogRestService = /** @class */ (function (_super) {
    __extends(ContentCatalogRestService, _super);
    function ContentCatalogRestService(restServiceFactory) {
        return _super.call(this, restServiceFactory, CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API) || this;
    }
    ContentCatalogRestService = __decorate([
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["CacheConfig"])({ actions: [smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["rarelyChangingContent"]], tags: [smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["userEvictionTag"], smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["pageEvictionTag"], smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["contentCatalogUpdateEvictionTag"]] }),
        Object(smarteditcommons_services_httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_3__["OperationContextRegistered"])(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API, ['CMS', 'INTERACTIVE']),
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__["SeInjectable"])()
        /* @ngInject */
    ], ContentCatalogRestService);
    return ContentCatalogRestService;
}(smarteditcommons_services_rest_AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_1__["AbstractCachedRestService"]));



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/daos/ProductCatalogRestService.ts":
/*!*********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/daos/ProductCatalogRestService.ts ***!
  \*********************************************************************************/
/*! exports provided: ProductCatalogRestService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProductCatalogRestService", function() { return ProductCatalogRestService; });
/* harmony import */ var smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/cache */ "./jsTarget/web/app/common/services/cache/index.ts");
/* harmony import */ var smarteditcommons_services_rest_AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/rest/AbstractCachedRestService */ "./jsTarget/web/app/common/services/rest/AbstractCachedRestService.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __extends = (undefined && undefined.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};



var ProductCatalogRestService = /** @class */ (function (_super) {
    __extends(ProductCatalogRestService, _super);
    function ProductCatalogRestService(restServiceFactory) {
        return _super.call(this, restServiceFactory, '/cmssmarteditwebservices/v1/sites/:siteUID/productcatalogs') || this;
    }
    ProductCatalogRestService = __decorate([
        Object(smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["CacheConfig"])({ actions: [smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["rarelyChangingContent"]], tags: [smarteditcommons_services_cache__WEBPACK_IMPORTED_MODULE_0__["userEvictionTag"]] }),
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_2__["SeInjectable"])()
        /* @ngInject */
    ], ProductCatalogRestService);
    return ProductCatalogRestService;
}(smarteditcommons_services_rest_AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_1__["AbstractCachedRestService"]));



/***/ }),

/***/ "./jsTarget/web/app/common/services/rest/rest.ts":
/*!*******************************************************!*\
  !*** ./jsTarget/web/app/common/services/rest/rest.ts ***!
  \*******************************************************/
/*! exports provided: AbstractCachedRestService, ContentCatalogRestService, ProductCatalogRestService, PermissionsRestService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./AbstractCachedRestService */ "./jsTarget/web/app/common/services/rest/AbstractCachedRestService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AbstractCachedRestService", function() { return _AbstractCachedRestService__WEBPACK_IMPORTED_MODULE_0__["AbstractCachedRestService"]; });

/* harmony import */ var _daos_ContentCatalogRestService__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./daos/ContentCatalogRestService */ "./jsTarget/web/app/common/services/rest/daos/ContentCatalogRestService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ContentCatalogRestService", function() { return _daos_ContentCatalogRestService__WEBPACK_IMPORTED_MODULE_1__["ContentCatalogRestService"]; });

/* harmony import */ var _daos_ProductCatalogRestService__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./daos/ProductCatalogRestService */ "./jsTarget/web/app/common/services/rest/daos/ProductCatalogRestService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ProductCatalogRestService", function() { return _daos_ProductCatalogRestService__WEBPACK_IMPORTED_MODULE_2__["ProductCatalogRestService"]; });

/* harmony import */ var _PermissionsRestService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./PermissionsRestService */ "./jsTarget/web/app/common/services/rest/PermissionsRestService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PermissionsRestService", function() { return _PermissionsRestService__WEBPACK_IMPORTED_MODULE_3__["PermissionsRestService"]; });

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






/***/ }),

/***/ "./jsTarget/web/app/common/services/storage/index.ts":
/*!***********************************************************!*\
  !*** ./jsTarget/web/app/common/services/storage/index.ts ***!
  \***********************************************************/
/*! exports provided: NamespacedStorageManager, StorageManagerFactory, StorageNamespaceConverter */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _manager_NamespacedStorageManager__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./manager/NamespacedStorageManager */ "./jsTarget/web/app/common/services/storage/manager/NamespacedStorageManager.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "NamespacedStorageManager", function() { return _manager_NamespacedStorageManager__WEBPACK_IMPORTED_MODULE_0__["NamespacedStorageManager"]; });

/* harmony import */ var _manager_StorageManagerFactory__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./manager/StorageManagerFactory */ "./jsTarget/web/app/common/services/storage/manager/StorageManagerFactory.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageManagerFactory", function() { return _manager_StorageManagerFactory__WEBPACK_IMPORTED_MODULE_1__["StorageManagerFactory"]; });

/* harmony import */ var _manager_StorageNamespaceConverter__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./manager/StorageNamespaceConverter */ "./jsTarget/web/app/common/services/storage/manager/StorageNamespaceConverter.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StorageNamespaceConverter", function() { return _manager_StorageNamespaceConverter__WEBPACK_IMPORTED_MODULE_2__["StorageNamespaceConverter"]; });

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
// Manager





/***/ }),

/***/ "./jsTarget/web/app/common/services/storage/manager/NamespacedStorageManager.ts":
/*!**************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/storage/manager/NamespacedStorageManager.ts ***!
  \**************************************************************************************/
/*! exports provided: NamespacedStorageManager */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NamespacedStorageManager", function() { return NamespacedStorageManager; });
/* harmony import */ var _StorageNamespaceConverter__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./StorageNamespaceConverter */ "./jsTarget/web/app/common/services/storage/manager/StorageNamespaceConverter.ts");

/** @internal */
var NamespacedStorageManager = /** @class */ (function () {
    function NamespacedStorageManager(storageManager, namespace) {
        this.storageManager = storageManager;
        this.namespace = namespace;
    }
    NamespacedStorageManager.prototype.getStorage = function (storageConfiguration) {
        storageConfiguration.storageId = this.getNamespaceStorageId(storageConfiguration.storageId);
        return this.storageManager.getStorage(storageConfiguration);
    };
    NamespacedStorageManager.prototype.deleteStorage = function (storageId, force) {
        if (force === void 0) { force = false; }
        return this.storageManager.deleteStorage(this.getNamespaceStorageId(storageId), force);
    };
    NamespacedStorageManager.prototype.deleteExpiredStorages = function (force) {
        if (force === void 0) { force = false; }
        return this.storageManager.deleteExpiredStorages(force);
    };
    NamespacedStorageManager.prototype.hasStorage = function (storageId) {
        return this.storageManager.hasStorage(this.getNamespaceStorageId(storageId));
    };
    NamespacedStorageManager.prototype.registerStorageController = function (controller) {
        return this.storageManager.registerStorageController(controller);
    };
    NamespacedStorageManager.prototype.getNamespaceStorageId = function (storageId) {
        return _StorageNamespaceConverter__WEBPACK_IMPORTED_MODULE_0__["StorageNamespaceConverter"].getNamespacedStorageId(this.namespace, storageId);
    };
    NamespacedStorageManager.prototype.getStorageManager = function () {
        return this.storageManager;
    };
    return NamespacedStorageManager;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/storage/manager/StorageManagerFactory.ts":
/*!***********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/storage/manager/StorageManagerFactory.ts ***!
  \***********************************************************************************/
/*! exports provided: StorageManagerFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StorageManagerFactory", function() { return StorageManagerFactory; });
/* harmony import */ var _NamespacedStorageManager__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./NamespacedStorageManager */ "./jsTarget/web/app/common/services/storage/manager/NamespacedStorageManager.ts");

/** @internal */
var StorageManagerFactory = /** @class */ (function () {
    function StorageManagerFactory(theOneAndOnlyStorageManager) {
        this.theOneAndOnlyStorageManager = theOneAndOnlyStorageManager;
    }
    StorageManagerFactory.ERR_INVALID_NAMESPACE = function (namespace) {
        return new Error("StorageManagerFactory Error: invalid namespace [" + namespace + "]. Namespace must be a non-empty string");
    };
    StorageManagerFactory.prototype.getStorageManager = function (namespace) {
        this.validateNamespace(namespace);
        return new _NamespacedStorageManager__WEBPACK_IMPORTED_MODULE_0__["NamespacedStorageManager"](this.theOneAndOnlyStorageManager, namespace);
    };
    StorageManagerFactory.prototype.validateNamespace = function (namespace) {
        if (typeof namespace !== 'string' || namespace.length <= 0) {
            throw StorageManagerFactory.ERR_INVALID_NAMESPACE(namespace);
        }
    };
    return StorageManagerFactory;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/storage/manager/StorageNamespaceConverter.ts":
/*!***************************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/storage/manager/StorageNamespaceConverter.ts ***!
  \***************************************************************************************/
/*! exports provided: StorageNamespaceConverter */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StorageNamespaceConverter", function() { return StorageNamespaceConverter; });
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
/** @internal */
var StorageNamespaceConverter = /** @class */ (function () {
    function StorageNamespaceConverter() {
    }
    /**
     * Given:
     *  namespace = nmsp
     *  storageId = stoid
     *
     * Produces:
     *  newStorageId = nmsp<ns:id>stoid
     *
     * Fastest implementation I could think of that (most likely) will not clash with weird storageIds
     *
     * This algorithm is a bit overly simple, and assumes that neither storageId nor namespace contains "<ns:id>"
     * I think this is a fairly safe assumption, but if we have time in the future, we should escape any existing
     * matches of the string.
     */
    StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID = function (id) {
        return new Error("StorageNamespaceConverter - Invalid namespaced id [" + id + "]");
    };
    StorageNamespaceConverter.getNamespacedStorageId = function (namespace, storageId) {
        return "" + namespace + this.separator + storageId;
    };
    StorageNamespaceConverter.getStorageIdFromNamespacedId = function (namespacedId) {
        var matches = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
        if (matches && matches[2].length > 0) {
            return matches[2];
        }
        throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
    };
    StorageNamespaceConverter.getNamespaceFromNamespacedId = function (namespacedId) {
        var matches = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
        if (matches && matches[1].length > 0) {
            return matches[1];
        }
        throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
    };
    StorageNamespaceConverter.separator = '<ns:id>';
    StorageNamespaceConverter.namespaceDecoderRegexStr = "(.*)" + StorageNamespaceConverter.separator + "(.*)";
    return StorageNamespaceConverter;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/testModeService.ts":
/*!*************************************************************!*\
  !*** ./jsTarget/web/app/common/services/testModeService.ts ***!
  \*************************************************************/
/*! exports provided: TestModeService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TestModeService", function() { return TestModeService; });
/* harmony import */ var _dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:TestModeService
 *
 * @description
 * Used to determine whether smartedit is running in a e2e (test) mode
 */
/** @internal */
var TestModeService = /** @class */ (function () {
    function TestModeService($injector) {
        this.$injector = $injector;
        // Constants
        this.TEST_KEY = 'e2eMode';
    }
    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:TestModeService#isE2EMode
     * @methodOf smarteditServicesModule.service:TestModeService
     *
     * @description
     * returns true if smartedit is running in e2e (test) mode
     *
     * @returns {Boolean} true/false
     */
    TestModeService.prototype.isE2EMode = function () {
        return this.$injector.has(this.TEST_KEY) && this.$injector.get(this.TEST_KEY);
    };
    TestModeService = __decorate([
        Object(_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], TestModeService);
    return TestModeService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/text/textTruncateService.ts":
/*!**********************************************************************!*\
  !*** ./jsTarget/web/app/common/services/text/textTruncateService.ts ***!
  \**********************************************************************/
/*! exports provided: TextTruncateService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TextTruncateService", function() { return TextTruncateService; });
/* harmony import */ var smarteditcommons_dtos_TruncatedText__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/dtos/TruncatedText */ "./jsTarget/web/app/common/dtos/TruncatedText.ts");
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};


/**
 * @internal
 *
 * @name TextTruncateService
 *
 * @description
 * Service containing truncate string functions.
 */
var TextTruncateService = /** @class */ (function () {
    function TextTruncateService(lodash) {
        this.lodash = lodash;
    }
    /**
     * @name TextTruncateService#truncateToNearestWord
     * @methodOf TextTruncateService
     *
     * @description
     * Truncates text to the nearest word depending on character length. Truncates below character length.
     *
     * @param {number} limit index in text to truncate to
     * @param {string} text text to be truncated
     * @return {TruncatedText}
     */
    TextTruncateService.prototype.truncateToNearestWord = function (limit, text, ellipsis) {
        if (ellipsis === void 0) { ellipsis = ""; }
        if (this.lodash.isNil(text) || limit > text.length) {
            return new smarteditcommons_dtos_TruncatedText__WEBPACK_IMPORTED_MODULE_0__["TruncatedText"](text, text, false);
        }
        var regexp = /(\s)/g;
        var truncatedGroups = text.match(regexp);
        var truncateIndex = 0;
        if (!truncatedGroups) {
            truncateIndex = limit;
        }
        else {
            for (var i = 0; i < truncatedGroups.length; i++) {
                var nextPosition = this.getPositionOfCharacters(text, truncatedGroups[i], i + 1);
                if (nextPosition > limit) {
                    break;
                }
                truncateIndex = nextPosition;
            }
        }
        var truncated = text.substr(0, truncateIndex);
        return new smarteditcommons_dtos_TruncatedText__WEBPACK_IMPORTED_MODULE_0__["TruncatedText"](text, truncated, true, ellipsis);
    };
    TextTruncateService.prototype.getPositionOfCharacters = function (searchString, characters, index) {
        return searchString.split(characters, index).join(characters).length;
    };
    TextTruncateService = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"])()
        /* @ngInject */
    ], TextTruncateService);
    return TextTruncateService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/DefaultWizardActionStrategy.ts":
/*!********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/DefaultWizardActionStrategy.ts ***!
  \********************************************************************************/
/*! exports provided: DefaultWizardActionStrategy */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DefaultWizardActionStrategy", function() { return DefaultWizardActionStrategy; });
/* harmony import */ var _dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/SeInjectable */ "./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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

/* @internal */
var DefaultWizardActionStrategy = /** @class */ (function () {
    function DefaultWizardActionStrategy(wizardActions) {
        this.wizardActions = wizardActions;
    }
    DefaultWizardActionStrategy.prototype.applyStrategy = function (wizardService, conf) {
        var nextAction = this.applyOverrides(wizardService, this.wizardActions.next(), conf.nextLabel, conf.onNext, conf.isFormValid);
        var doneAction = this.applyOverrides(wizardService, this.wizardActions.done(), conf.doneLabel, conf.onDone, conf.isFormValid);
        var backConf = conf.backLabel ? {
            i18n: conf.backLabel
        } : null;
        var backAction = this.wizardActions.back(backConf);
        conf.steps.forEach(function (step, index) {
            step.actions = [];
            if (index > 0) {
                step.actions.push(backAction);
            }
            if (index === (conf.steps.length - 1)) {
                step.actions.push(doneAction);
            }
            else {
                step.actions.push(nextAction);
            }
        });
        conf.cancelAction = this.applyOverrides(wizardService, this.wizardActions.cancel(), conf.cancelLabel, conf.onCancel, null);
        conf.templateOverride = 'modalWizardNavBarTemplate.html';
    };
    DefaultWizardActionStrategy.prototype.applyOverrides = function (wizardService, action, label, executeCondition, enableCondition) {
        if (label) {
            action.i18n = label;
        }
        if (executeCondition) {
            action.executeIfCondition = function () {
                return executeCondition(wizardService.getCurrentStepId());
            };
        }
        if (enableCondition) {
            action.enableIfCondition = function () {
                return enableCondition(wizardService.getCurrentStepId());
            };
        }
        return action;
    };
    DefaultWizardActionStrategy = __decorate([
        Object(_dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], DefaultWizardActionStrategy);
    return DefaultWizardActionStrategy;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/ModalWizard.ts":
/*!****************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/ModalWizard.ts ***!
  \****************************************************************/
/*! exports provided: ModalWizard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ModalWizard", function() { return ModalWizard; });
/* harmony import */ var _dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/SeInjectable */ "./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts");
/* harmony import */ var _ModalWizardControllerFactory__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./ModalWizardControllerFactory */ "./jsTarget/web/app/common/services/wizard/ModalWizardControllerFactory.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};


/**
 * @ngdoc service
 * @name wizardServiceModule.modalWizard
 *
 * @description
 * The modalWizard service is used to create wizards that are embedded into the {@link modalServiceModule modalService}
 */
var ModalWizard = /** @class */ (function () {
    function ModalWizard(modalService) {
        this.modalService = modalService;
    }
    /**
     * @ngdoc method
     * @name wizardServiceModule.modalWizard#open
     * @methodOf wizardServiceModule.modalWizard
     *
     * @description
     * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you, such as look
     * and feel, and wizard navigation.
     *
     * @param {WizardAction} conf configuration
     * @param {String|function|Array} conf.controller An angular controller which will be the underlying controller
     * for all of the wizard. This controller MUST implement the function <strong>getWizardConfig()</strong> which
     * returns a {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig}.<br />
     * If you need to do any manual wizard manipulation, 'wizardManager' can be injected into your controller.
     * See {@link wizardServiceModule.WizardManager WizardManager}
     * @param {String} conf.controllerAs (OPTIONAL) An alternate controller name that can be used in your wizard step
     * @param {=String=} conf.properties A map of properties to initialize the wizardManager with. They are accessible under wizardManager.properties.
     * templates. By default the controller name is wizardController.
     *
     * @returns {function} {@link https://docs.angularjs.org/api/ng/service/$q promise} that will either be resolved (wizard finished) or
     * rejected (wizard cancelled).
     */
    ModalWizard.prototype.open = function (config) {
        this.validateConfig(config);
        return this.modalService.open({
            templateUrl: 'modalWizardTemplate.html',
            controller: Object(_ModalWizardControllerFactory__WEBPACK_IMPORTED_MODULE_1__["ModalWizardControllerFactory"])(config)
        });
    };
    ModalWizard.prototype.validateConfig = function (config) {
        if (!config.controller) {
            throw new Error("WizardService - initialization exception. No controller provided");
        }
    };
    ModalWizard = __decorate([
        Object(_dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], ModalWizard);
    return ModalWizard;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/ModalWizardControllerFactory.ts":
/*!*********************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/ModalWizardControllerFactory.ts ***!
  \*********************************************************************************/
/*! exports provided: ModalWizardControllerFactory */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ModalWizardControllerFactory", function() { return ModalWizardControllerFactory; });
/* harmony import */ var _WizardService__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./WizardService */ "./jsTarget/web/app/common/services/wizard/WizardService.ts");

/* @internal */
var ModalWizardControllerFactory = function (config) {
    /* @ngInject */
    var ModalWizardController = /** @class */ (function () {
        function ModalWizardController(lodash, $scope, $rootScope, modalManager, $controller, wizardActions, MODAL_BUTTON_STYLES, $q, defaultWizardActionStrategy, generateIdentifier) {
            var _this = this;
            this.lodash = lodash;
            this.$scope = $scope;
            this.$rootScope = $rootScope;
            this.modalManager = modalManager;
            this.wizardActions = wizardActions;
            this.MODAL_BUTTON_STYLES = MODAL_BUTTON_STYLES;
            this.$q = $q;
            this.wizardService = new _WizardService__WEBPACK_IMPORTED_MODULE_0__["WizardService"](this.$q, defaultWizardActionStrategy, generateIdentifier);
            this.wizardService.properties = config.properties;
            this.lodash.assign(this, $controller(config.controller, {
                $scope: $scope,
                wizardManager: this.wizardService
            }));
            if (config.controllerAs) {
                this.$scope[config.controllerAs] = this;
            }
            if (typeof this.getWizardConfig !== 'function') {
                throw new Error("The provided controller must provide a getWizardConfig() function.");
            }
            var modalConfig = this.getWizardConfig();
            this._wizardContext = {
                _steps: modalConfig.steps
            };
            this.executeAction = function (action) {
                _this.wizardService.executeAction(action);
            };
            var unregisterWatch;
            this.wizardService.onLoadStep = function (stepIndex, step) {
                _this.modalManager.title = step.title;
                _this._wizardContext.templateUrl = step.templateUrl;
                _this.modalManager.removeAllButtons();
                (step.actions || []).forEach(function (action) {
                    if (typeof action.enableIfCondition === 'function') {
                        unregisterWatch = _this.$rootScope.$watch(action.enableIfCondition, function (newVal) {
                            if (newVal) {
                                _this.modalManager.enableButton(action.id);
                            }
                            else {
                                _this.modalManager.disableButton(action.id);
                            }
                        });
                    }
                    _this.modalManager.addButton(_this.convertActionToButtonConf(action));
                });
            };
            this.wizardService.onClose = function (result) {
                _this.modalManager.close(result);
                unregisterWatch();
            };
            this.wizardService.onCancel = function () {
                _this.modalManager.dismiss();
                unregisterWatch();
            };
            this.wizardService.onStepsUpdated = function (steps) {
                _this.setupNavBar(steps);
                _this._wizardContext._steps = steps;
            };
            this.wizardService.initialize(modalConfig);
            this.setupModal(modalConfig);
        }
        ModalWizardController.prototype.setupNavBar = function (steps) {
            var _this = this;
            this._wizardContext.navActions = steps.map(function (step, index) {
                var action = _this.wizardActions.navBarAction({
                    id: 'NAV-' + step.id,
                    stepIndex: index,
                    wizardService: _this.wizardService,
                    destinationIndex: index,
                    i18n: step.name,
                    isCurrentStep: function () {
                        return action.stepIndex === _this.wizardService.getCurrentStepIndex();
                    }
                });
                return action;
            });
        };
        ModalWizardController.prototype.setupModal = function (setupConfig) {
            var _this = this;
            this._wizardContext.templateOverride = setupConfig.templateOverride;
            if (setupConfig.cancelAction) {
                this.modalManager.setDismissCallback(function () {
                    _this.wizardService.executeAction(setupConfig.cancelAction);
                    return _this.$q.reject();
                });
            }
            this.setupNavBar(setupConfig.steps);
        };
        ModalWizardController.prototype.convertActionToButtonConf = function (action) {
            var _this = this;
            return {
                id: action.id,
                style: action.isMainAction ? this.MODAL_BUTTON_STYLES.PRIMARY : this.MODAL_BUTTON_STYLES.SECONDARY,
                label: action.i18n,
                callback: function () {
                    _this.wizardService.executeAction(action);
                }
            };
        };
        return ModalWizardController;
    }());
    return ModalWizardController;
};


/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/WizardActions.ts":
/*!******************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/WizardActions.ts ***!
  \******************************************************************/
/*! exports provided: WizardActions */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WizardActions", function() { return WizardActions; });
/* harmony import */ var _dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/SeInjectable */ "./jsTarget/web/app/common/services/dependencyInjection/SeInjectable.ts");
var __assign = (undefined && undefined.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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

var DEFAULT_WIZARD_ACTION = {
    id: "wizard_action_id",
    i18n: 'wizard_action_label',
    isMainAction: true,
    enableIfCondition: function () {
        return true;
    },
    executeIfCondition: function () {
        return true;
    },
    execute: function (wizardService) {
        return;
    }
};
/* @internal */
var WizardActions = /** @class */ (function () {
    /* @ngInject */
    function WizardActions() {
    }
    WizardActions.prototype.customAction = function (configuration) {
        return this.createNewAction(configuration);
    };
    WizardActions.prototype.done = function (configuration) {
        var custom = {
            id: 'ACTION_DONE',
            i18n: 'se.action.done',
            execute: function (wizardService) {
                wizardService.close();
            }
        };
        return this.createNewAction(configuration, custom);
    };
    WizardActions.prototype.next = function (configuration) {
        var custom = {
            id: 'ACTION_NEXT',
            i18n: 'se.action.next',
            execute: function (wizardService) {
                wizardService.goToStepWithIndex(wizardService.getCurrentStepIndex() + 1);
            }
        };
        return this.createNewAction(configuration, custom);
    };
    WizardActions.prototype.navBarAction = function (configuration) {
        if (!configuration.wizardService || configuration.destinationIndex === null) {
            throw new Error("Error initializating navBarAction, must provide the wizardService and destinationIndex fields");
        }
        var custom = {
            id: 'ACTION_GOTO',
            i18n: 'action.goto',
            enableIfCondition: function () {
                return configuration.wizardService.getCurrentStepIndex() >= configuration.destinationIndex;
            },
            execute: function (wizardService) {
                wizardService.goToStepWithIndex(configuration.destinationIndex);
            }
        };
        return this.createNewAction(configuration, custom);
    };
    WizardActions.prototype.back = function (configuration) {
        var custom = {
            id: 'ACTION_BACK',
            i18n: 'se.action.back',
            isMainAction: false,
            execute: function (wizardService) {
                var currentIndex = wizardService.getCurrentStepIndex();
                if (currentIndex <= 0) {
                    throw new Error("Failure to execute BACK action, no previous index exists!");
                }
                wizardService.goToStepWithIndex(currentIndex - 1);
            }
        };
        return this.createNewAction(configuration, custom);
    };
    WizardActions.prototype.cancel = function () {
        return this.createNewAction({
            id: 'ACTION_CANCEL',
            i18n: 'se.action.cancel',
            isMainAction: false,
            execute: function (wizardService) {
                wizardService.cancel();
            }
        });
    };
    WizardActions.prototype.createNewAction = function (configuration, customConfiguration) {
        if (configuration === void 0) { configuration = null; }
        if (customConfiguration === void 0) { customConfiguration = null; }
        return __assign({}, DEFAULT_WIZARD_ACTION, customConfiguration, configuration);
    };
    WizardActions = __decorate([
        Object(_dependencyInjection_SeInjectable__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], WizardActions);
    return WizardActions;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/WizardService.ts":
/*!******************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/WizardService.ts ***!
  \******************************************************************/
/*! exports provided: WizardService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WizardService", function() { return WizardService; });
var __assign = (undefined && undefined.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
/**
 * @ngdoc service
 * @name wizardServiceModule.WizardManager
 *
 * @description
 * The Wizard Manager is a wizard management service that can be injected into your wizard controller.
 *
 */
var WizardService = /** @class */ (function () {
    function WizardService($q, defaultWizardActionStrategy, generateIdentifier) {
        this.$q = $q;
        this.defaultWizardActionStrategy = defaultWizardActionStrategy;
        this.generateIdentifier = generateIdentifier;
        // the overridable callbacks
        this.onLoadStep = function (index, nextStep) {
            return;
        };
        this.onClose = function (result) {
            return;
        };
        this.onCancel = function () {
            return;
        };
        this.onStepsUpdated = function (steps) {
            return;
        };
    }
    /* @internal */
    WizardService.prototype.initialize = function (conf) {
        this.validateConfig(conf);
        this._actionStrategy = conf.actionStrategy || this.defaultWizardActionStrategy;
        this._actionStrategy.applyStrategy(this, conf);
        this._currentIndex = 0;
        this._conf = __assign({}, conf);
        this._steps = this._conf.steps;
        this._getResult = conf.resultFn;
        this.validateStepUids(this._steps);
        this.goToStepWithIndex(0);
    };
    /* @internal */
    WizardService.prototype.executeAction = function (action) {
        var _this = this;
        if (action.executeIfCondition) {
            return this.$q.resolve(action.executeIfCondition()).then(function () {
                return action.execute(_this);
            });
        }
        return this.$q.resolve(action.execute(this));
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#goToStepWithIndex
     * @methodOf wizardServiceModule.WizardManager
     * @description Navigates the wizard to the given step
     * @param {Number} index The 0-based index from the steps array returned by the wizard controllers getWizardConfig() function
     */
    WizardService.prototype.goToStepWithIndex = function (index) {
        var nextStep = this.getStepWithIndex(index);
        if (nextStep) {
            this.onLoadStep(index, nextStep);
            this._currentIndex = index;
        }
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#goToStepWithId
     * @methodOf wizardServiceModule.WizardManager
     * @description Navigates the wizard to the given step
     * @param {String} id The ID of a step returned by the wizard controllers getWizardConfig() function. Note that if
     * no id was provided for a given step, then one is automatically generated.
     */
    WizardService.prototype.goToStepWithId = function (id) {
        this.goToStepWithIndex(this.getStepIndexFromId(id));
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#addStep
     * @methodOf wizardServiceModule.WizardManager
     * @description Adds an additional step to the wizard at runtime
     * @param {Object} newStep A {@link wizardServiceModule.object:WizardStepConfig WizardStepConfig}
     * @param {Number} index (OPTIONAL) A 0-based index position in the steps array. Default is 0.
     */
    WizardService.prototype.addStep = function (newStep, index) {
        if (parseInt(newStep.id, 10) !== 0 && !newStep.id) {
            newStep.id = this.generateIdentifier();
        }
        if (!index) {
            index = 0;
        }
        if (this._currentIndex >= index) {
            this._currentIndex++;
        }
        this._steps.splice(index, 0, newStep);
        this.validateStepUids(this._steps);
        this._actionStrategy.applyStrategy(this, this._conf);
        this.onStepsUpdated(this._steps);
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#removeStepById
     * @methodOf wizardServiceModule.WizardManager
     * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     * @param {String} id The id of the step you wish to remove
     */
    WizardService.prototype.removeStepById = function (id) {
        this.removeStepByIndex(this.getStepIndexFromId(id));
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#removeStepByIndex
     * @methodOf wizardServiceModule.WizardManager
     * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     * @param {Number} index The 0-based index of the step you wish to remove.
     */
    WizardService.prototype.removeStepByIndex = function (index) {
        if (index >= 0 && index < this.getStepsCount()) {
            this._steps.splice(index, 1);
            if (index === this._currentIndex) {
                this.goToStepWithIndex(0);
            }
            this._actionStrategy.applyStrategy(this, this._conf);
            this.onStepsUpdated(this._steps);
        }
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#close
     * @methodOf wizardServiceModule.WizardManager
     * @description Close the wizard. This will return a resolved promise to the creator of the wizard, and if any
     * resultFn was provided in the {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig} the returned
     * value of this function will be passed as the result.
     */
    WizardService.prototype.close = function () {
        var result;
        if (typeof this._getResult === 'function') {
            result = this._getResult();
        }
        this.onClose(result);
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#cancel
     * @methodOf wizardServiceModule.WizardManager
     * @description Cancel the wizard. This will return a rejected promise to the creator of the wizard.
     */
    WizardService.prototype.cancel = function () {
        this.onCancel();
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getSteps
     * @methodOf wizardServiceModule.WizardManager
     * @returns {Array} An array of all the steps in the wizard
     */
    WizardService.prototype.getSteps = function () {
        return this._steps;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getStepIndexFromId
     * @methodOf wizardServiceModule.WizardManager
     * @param {String} id A step ID
     * @returns {Number} The index of the step with the provided ID
     */
    WizardService.prototype.getStepIndexFromId = function (id) {
        var index = this._steps.findIndex(function (step) {
            return step.id === id;
        });
        return index;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#containsStep
     * @methodOf wizardServiceModule.WizardManager
     * @param {String} id A step ID
     * @returns {Boolean} True if the ID exists in one of the steps
     */
    WizardService.prototype.containsStep = function (stepId) {
        return this.getStepIndexFromId(stepId) >= 0;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getCurrentStepId
     * @methodOf wizardServiceModule.WizardManager
     * @returns {String} The ID of the currently displayed step
     */
    WizardService.prototype.getCurrentStepId = function () {
        return this.getCurrentStep().id;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getCurrentStepIndex
     * @methodOf wizardServiceModule.WizardManager
     * @returns {Number} The index of the currently displayed step
     */
    WizardService.prototype.getCurrentStepIndex = function () {
        return this._currentIndex;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getCurrentStep
     * @methodOf wizardServiceModule.WizardManager
     * @returns {Object} The currently displayed step
     */
    WizardService.prototype.getCurrentStep = function () {
        return this.getStepWithIndex(this._currentIndex);
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getStepsCount
     * @methodOf wizardServiceModule.WizardManager
     * @returns {Number} The number of steps in the wizard. This should always be equal to the size of the array
     * returned by {@link wizardServiceModule.WizardManager#methods_getSteps getSteps()}
     */
    WizardService.prototype.getStepsCount = function () {
        return this._steps.length;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getStepWithId
     * @methodOf wizardServiceModule.WizardManager
     * @param {String} id The ID of a step
     * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given ID
     */
    WizardService.prototype.getStepWithId = function (id) {
        var index = this.getStepIndexFromId(id);
        if (index >= 0) {
            return this.getStepWithIndex(index);
        }
        return null;
    };
    /**
     * @ngdoc method
     * @name wizardServiceModule.WizardManager#getStepWithIndex
     * @methodOf wizardServiceModule.WizardManager
     * @param {Number} index The ID of a step
     * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given index
     */
    WizardService.prototype.getStepWithIndex = function (index) {
        if (index >= 0 && index < this.getStepsCount()) {
            return this._steps[index];
        }
        throw new Error(("wizardService.getStepForIndex - Index out of bounds: " + index));
    };
    WizardService.prototype.validateConfig = function (config) {
        if (!config.steps || config.steps.length <= 0) {
            throw new Error("Invalid WizardService configuration - no steps provided");
        }
        config.steps.forEach(function (step) {
            if (!step.templateUrl) {
                throw new Error("Invalid WizardService configuration - Step missing a url: " + step);
            }
        });
    };
    WizardService.prototype.validateStepUids = function (steps) {
        var _this = this;
        var stepIds = {};
        steps.forEach(function (step) {
            if (!step.id) {
                step.id = _this.generateIdentifier();
            }
            else if (stepIds[step.id]) {
                throw new Error("Invalid (Duplicate) step id: " + step.id);
            }
            else {
                stepIds[step.id] = step.id;
            }
        });
    };
    return WizardService;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/services/wizard/WizardServiceModule.ts":
/*!************************************************************************!*\
  !*** ./jsTarget/web/app/common/services/wizard/WizardServiceModule.ts ***!
  \************************************************************************/
/*! exports provided: WizardServiceModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WizardServiceModule", function() { return WizardServiceModule; });
/* harmony import */ var _dependencyInjection_SeModule__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../dependencyInjection/SeModule */ "./jsTarget/web/app/common/services/dependencyInjection/SeModule.ts");
/* harmony import */ var _DefaultWizardActionStrategy__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./DefaultWizardActionStrategy */ "./jsTarget/web/app/common/services/wizard/DefaultWizardActionStrategy.ts");
/* harmony import */ var _ModalWizard__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./ModalWizard */ "./jsTarget/web/app/common/services/wizard/ModalWizard.ts");
/* harmony import */ var _WizardActions__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./WizardActions */ "./jsTarget/web/app/common/services/wizard/WizardActions.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc overview
 * @name wizardServiceModule
 *
 * @description
 * # The wizardServiceModule
 * The wizardServiceModule is a module containing all wizard related services
 * # Creating a modal wizard in a few simple steps
 * 1. Add the wizardServiceModule to your module dependencies
 * 2. Inject {@link wizardServiceModule.modalWizard modalWizard} where you want to use the wizard.
 * 3. Create a new controller for your wizard. This controller will be used for all steps of the wizard.
 * 4. Implement a function in your new controller called <strong>getWizardConfig</strong> that returns a {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig}
 * 5. Use {@link wizardServiceModule.modalWizard#methods_open modalWizard.open()} passing in your new controller
 *
 * <pre>
 * @SeInjectable()
 * export class MyWizardService {
 * 		constructor(private modalWizard) {}
 * 		open() {
 * 			this.modalWizard.open({
 * 				controller: (wizardManager: any) => {
 * 					'ngInject';
 * 					return {
 * 						steps: [{
 * 							id: 'step1',
 * 							name: 'i18n.step1.name',
 * 							title: 'i18n.step1.title',
 * 							templateUrl: 'some/template1.html'
 * 						}, {
 * 							id: 'step2',
 * 							name: 'i18n.step2.name',
 * 							title: 'i18n.step2.title',
 * 							templateUrl: 'some/template2.html'
 * 						}]
 * 					};
 * 				}
 * 			});
 * 		}
 * }
 * </pre>
 */
var WizardServiceModule = /** @class */ (function () {
    function WizardServiceModule() {
    }
    WizardServiceModule = __decorate([
        Object(_dependencyInjection_SeModule__WEBPACK_IMPORTED_MODULE_0__["SeModule"])({
            imports: [
                'ui.bootstrap',
                'translationServiceModule',
                'functionsModule',
                'coretemplates',
                'modalServiceModule'
            ],
            providers: [
                _DefaultWizardActionStrategy__WEBPACK_IMPORTED_MODULE_1__["DefaultWizardActionStrategy"],
                _ModalWizard__WEBPACK_IMPORTED_MODULE_2__["ModalWizard"],
                _WizardActions__WEBPACK_IMPORTED_MODULE_3__["WizardActions"],
            ]
        })
    ], WizardServiceModule);
    return WizardServiceModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/CryptographicUtils.ts":
/*!*************************************************************!*\
  !*** ./jsTarget/web/app/common/utils/CryptographicUtils.ts ***!
  \*************************************************************/
/*! exports provided: CryptographicUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CryptographicUtils", function() { return CryptographicUtils; });
/* harmony import */ var crypto_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! crypto-js */ "crypto-js");
/* harmony import */ var crypto_js__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(crypto_js__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name functionsModule.service:CryptographicUtils
 *
 * @description
 * utility service around Cryptographic operations.
 */
var CryptographicUtils = /** @class */ (function () {
    /* @ngInject */
    function CryptographicUtils() {
    }
    /**
     * @ngdoc method
     * @name functionsModule.service:CryptographicUtils#sha1Hash
     * @methodOf functionsModule.service:CryptographicUtils
     *
     * @description
     * A utility function that takes an input string and provides a cryptographic SHA1 hash value.
     *
     * @param {String} data The input string to be encrypted.
     * @returns {String} the encrypted hashed result.
     */
    CryptographicUtils.prototype.sha1Hash = function (data) {
        return crypto_js__WEBPACK_IMPORTED_MODULE_0__["SHA1"](data).toString();
    };
    CryptographicUtils = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_1__["SeInjectable"])()
        /* @ngInject */
    ], CryptographicUtils);
    return CryptographicUtils;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/DiscardablePromiseUtils.ts":
/*!******************************************************************!*\
  !*** ./jsTarget/web/app/common/utils/DiscardablePromiseUtils.ts ***!
  \******************************************************************/
/*! exports provided: DiscardablePromiseUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DiscardablePromiseUtils", function() { return DiscardablePromiseUtils; });
/* harmony import */ var _services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name functionsModule.service:DiscardablePromiseUtils
 * @description
 * helper to handle competing promises
 */
var DiscardablePromiseUtils = /** @class */ (function () {
    function DiscardablePromiseUtils($log) {
        this.$log = $log;
        this._map = {};
    }
    /**
     * @ngdoc method
     * @methodOf DiscardablePromiseUtils
     * @name functionsModule.service:DiscardablePromiseUtils#apply
     * @methodOf functionsModule.service:DiscardablePromiseUtils
     * @description
     * selects a new promise as candidate for invoking a given callback
     * each invocation of this method for a given key discards the previously selected promise
     * @param {string} key the string key identifying the discardable promise
     * @param {Promise} promise the discardable promise instance once a new candidate is called with this method
     * @param {Function} successCallback the success callback to ultimately apply on the last promise not discarded
     * @param {Function=} failureCallback the failure callback to ultimately apply on the last promise not discarded. Optional.
     */
    DiscardablePromiseUtils.prototype.apply = function (key, promise, successCallback, failureCallback) {
        if (!this._map[key]) {
            this._map[key] = {
                promise: promise,
                successCallback: successCallback,
                failureCallback: failureCallback
            };
        }
        else {
            this.$log.debug("competing promise for key " + key);
            delete this._map[key].discardableHolder.successCallback;
            delete this._map[key].discardableHolder.failureCallback;
            this._map[key].promise = promise;
        }
        this._map[key].discardableHolder = {
            successCallback: this._map[key].successCallback,
            failureCallback: this._map[key].failureCallback
        };
        var self = this;
        var p = this._map[key].promise;
        p.then(function (response) {
            if (this.successCallback) {
                delete self._map[key];
                this.successCallback.apply(undefined, arguments);
            }
            else {
                self.$log.debug("aborted successCallback for promise identified by " + key);
            }
        }.bind(this._map[key].discardableHolder), function (error) {
            if (this.failureCallback) {
                delete self._map[key];
                this.failureCallback.apply(undefined, arguments);
            }
            else {
                self.$log.debug("aborted failureCallback for promise identified by " + key);
            }
        }.bind(this._map[key].discardableHolder));
    };
    DiscardablePromiseUtils = __decorate([
        Object(_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], DiscardablePromiseUtils);
    return DiscardablePromiseUtils;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/FunctionsUtils.ts":
/*!*********************************************************!*\
  !*** ./jsTarget/web/app/common/utils/FunctionsUtils.ts ***!
  \*********************************************************/
/*! exports provided: FunctionsUtils, functionsUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FunctionsUtils", function() { return FunctionsUtils; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "functionsUtils", function() { return functionsUtils; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

/**
 * @ngdoc service
 * @name functionsModule.service:FunctionsUtils
 *
 * @description
 * utility service around Functions.
 */
var FunctionsUtils = /** @class */ (function () {
    function FunctionsUtils() {
        /*
         * regexp matching function(a, $b){} and function MyFunction(a, $b){}
         */
        this.signatureArgsRegexp = /function[\s\w]*\(([\w\s\$,]*)\)[\s]*{/;
    }
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#isEmpty
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Will determine whether a function body is empty or should be considered empty for proxying purposes
     *
     * @param {Function} func, the function to evaluate
     * @returns {Boolean} a boolean.
     */
    FunctionsUtils.prototype.isEmpty = function (func) {
        return func.toString().match(/\{([\s\S]*)\}/m)[1].trim() === '' || /(proxyFunction)/g.test(func.toString().replace(/\s/g, ""));
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getArguments
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the array of string arguments of the given function signature
     *
     * @param {Function} func the function to analyze
     * @returns {string[]} an array of string arguments
     */
    FunctionsUtils.prototype.getArguments = function (func) {
        try {
            return this.signatureArgsRegexp.exec(func.toString())[1].replace(/\s/g, "").split(",");
        }
        catch (e) {
            throw new Error("failed to retrieve arguments list of " + func);
        }
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#hasArguments
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Determines whether a given function (anonymous or not) has arguments in it signature
     *
     * @param {Function} func the function to analyze
     * @returns {boolean} true if the function has signature arguments
     */
    FunctionsUtils.prototype.hasArguments = function (func) {
        try {
            return !this.getLodash().isEmpty(this.signatureArgsRegexp.exec(func.toString())[1]);
        }
        catch (e) {
            throw new Error("failed to retrieve arguments list of " + func);
        }
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getConstructorName
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion
     *
     * @param {Function} func the function to analyze
     * @returns {string} the constructor name
     */
    FunctionsUtils.prototype.getConstructorName = function (func) {
        try {
            // IE does not support constructor.name
            return func.name || /function (\$?\w+)\s*\(/.exec(func.toString())[1];
        }
        catch (_a) {
            throw new Error("[FunctionsUtils] - Cannot get name from invalid constructor.");
        }
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getInstanceConstructorName
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns the constructor name in a cross browser fashion of a class instance
     *
     * @param {Object} instance instance class to analyze
     * @returns {string} the constructor name of the instance
     */
    FunctionsUtils.prototype.getInstanceConstructorName = function (instance) {
        return this.getConstructorName(Object.getPrototypeOf(instance).constructor);
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#extendsConstructor
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Overrides a given constructor with a new constructor body. The resulting constructor will share the same prototype as the original one.
     *
     * @param {(...args:any[]) => T} originalConstructor the original constructor to override
     * @returns {(...args:any[]) => T} newConstructorBody the new constructor body to execute in the override. It may or may not return an instance. Should it return an instance, the latter will be returned by the override.
     */
    FunctionsUtils.prototype.extendsConstructor = function (originalConstructor, newConstructorBody) {
        // the new constructor behaviour
        var newConstructor = function () {
            var args = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                args[_i] = arguments[_i];
            }
            var result = newConstructorBody.apply(this, args);
            if (result) {
                return result;
            }
        };
        // copy prototype so intanceof operator still works
        newConstructor.prototype = originalConstructor.prototype;
        return newConstructor;
    };
    /**
     * @ngdoc method
     * @name functionsModule.service:FunctionsUtils#getLodash
     * @methodOf functionsModule.service:FunctionsUtils
     *
     * @description
     * Returns lodash service instance in a portable way
     * This is onyl to be used in unit testing and runtime code where angular DI is not available
     * @returns {lodash.LoDashStatic} the instance of the lodash service
     */
    FunctionsUtils.prototype.getLodash = function () {
        return window.smarteditLodash;
    };
    /** @internal */
    FunctionsUtils.prototype.isUnitTestMode = function () {
        return typeof window.__karma__ !== 'undefined';
    };
    FunctionsUtils = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], FunctionsUtils);
    return FunctionsUtils;
}());

var functionsUtils = new FunctionsUtils();


/***/ }),

/***/ "./jsTarget/web/app/common/utils/StringUtils.ts":
/*!******************************************************!*\
  !*** ./jsTarget/web/app/common/utils/StringUtils.ts ***!
  \******************************************************/
/*! exports provided: StringUtils, stringUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StringUtils", function() { return StringUtils; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "stringUtils", function() { return stringUtils; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name functionsModule.service:StringUtils
 *
 * @description
 * utility service around Strings.
 */
var StringUtils = /** @class */ (function () {
    /* @ngInject */
    function StringUtils() {
    }
    /**
     * @ngdoc method
     * @name functionsModule.regExpFactory
     * @name functionsModule.service:StringUtils#regExpFactory
     * @methodOf functionsModule.service:StringUtils
     *
     * @description
     * <b>regExpFactory</b> will convert a given pattern into a regular expression.
     * This method will prepend and append a string with ^ and $ respectively replaces
     * and wildcards (*) by proper regex wildcards.
     *
     * @param {String} pattern any string that needs to be converted to a regular expression.
     *
     * @returns {RegExp} a regular expression generated from the given string.
     *
     */
    StringUtils.prototype.regExpFactory = function (pattern) {
        var onlyAlphanumericsRegex = new RegExp(/^[a-zA-Z\d]+$/i);
        var antRegex = new RegExp(/^[a-zA-Z\d\*]+$/i);
        var regexpKey;
        if (onlyAlphanumericsRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern);
        }
        else if (antRegex.test(pattern)) {
            regexpKey = ['^', '$'].join(pattern.replace(/\*/g, '.*'));
        }
        else {
            regexpKey = pattern;
        }
        return new RegExp(regexpKey, 'g');
    };
    StringUtils = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], StringUtils);
    return StringUtils;
}());

var stringUtils = new StringUtils();


/***/ }),

/***/ "./jsTarget/web/app/common/utils/UrlUtils.ts":
/*!***************************************************!*\
  !*** ./jsTarget/web/app/common/utils/UrlUtils.ts ***!
  \***************************************************/
/*! exports provided: UrlUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UrlUtils", function() { return UrlUtils; });
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
 * @ngdoc service
 * @name functionsModule.service:UrlUtils
 *
 * @description
 * A collection of utility methods for manipulating URLs
 */
var UrlUtils = /** @class */ (function () {
    function UrlUtils() {
    }
    /**
     * @ngdoc method
     * @name functionsModule.service:UrlUtils#updateUrlParameter
     * @methodOf functionsModule.service:UrlUtils
     *
     * @description
     * Updates a URL to contain the query param and value provided. If already exists then it is updated,
     * if it did not previously exist, then it will be added.
     *
     * @param {String} url The url to be updated (this param will not be modified)
     * @param {String} key The query param key
     * @param {String} value The query param value
     *
     * @returns {String} The url with updated key/value
     */
    UrlUtils.prototype.updateUrlParameter = function (url, key, value) {
        var i = url.indexOf('#');
        var hash = i === -1 ? '' : url.substr(i);
        url = i === -1 ? url : url.substr(0, i);
        var regex = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
        var separator = url.indexOf('?') !== -1 ? "&" : "?";
        if (url.match(regex)) {
            url = url.replace(regex, '$1' + key + "=" + value + '$2');
        }
        else {
            url = url + separator + key + "=" + value;
        }
        return url + hash;
    };
    return UrlUtils;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/WindowUtils.ts":
/*!******************************************************!*\
  !*** ./jsTarget/web/app/common/utils/WindowUtils.ts ***!
  \******************************************************/
/*! exports provided: WindowUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WindowUtils", function() { return WindowUtils; });
/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
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
 * @ngdoc service
 * @name functionsModule.service:WindowUtils
 *
 * @description
 * A collection of utility methods for windows.
 */
var WindowUtils = /** @class */ (function () {
    function WindowUtils(isIframe, SMARTEDIT_IFRAME_ID, $window) {
        this.isIframe = isIframe;
        this.SMARTEDIT_IFRAME_ID = SMARTEDIT_IFRAME_ID;
        this.$window = $window;
    }
    /**
     * @ngdoc method
     * @name functionsModule.service:WindowUtils#getTargetIFrame
     * @methodOf functionsModule.service:WindowUtils
     *
     * @description
     * Retrieves the iframe from the inner or outer app.
     *
     * @returns {Window} The content window or null if it does not exists.
     */
    WindowUtils.prototype.getTargetIFrame = function () {
        if (this.isIframe()) {
            return this.$window.parent;
        }
        else if (this.$window.document.getElementById(this.SMARTEDIT_IFRAME_ID)) {
            return this.$window.document.getElementById(this.SMARTEDIT_IFRAME_ID).contentWindow;
        }
        return null;
    };
    WindowUtils = __decorate([
        Object(smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_0__["SeInjectable"])()
        /* @ngInject */
    ], WindowUtils);
    return WindowUtils;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/functionsModule.ts":
/*!**********************************************************!*\
  !*** ./jsTarget/web/app/common/utils/functionsModule.ts ***!
  \**********************************************************/
/*! exports provided: FunctionsModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FunctionsModule", function() { return FunctionsModule; });
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var _dtos_Cloneable__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../dtos/Cloneable */ "./jsTarget/web/app/common/dtos/Cloneable.ts");
/* harmony import */ var _CryptographicUtils__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./CryptographicUtils */ "./jsTarget/web/app/common/utils/CryptographicUtils.ts");
/* harmony import */ var _UrlUtils__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./UrlUtils */ "./jsTarget/web/app/common/utils/UrlUtils.ts");
/* harmony import */ var _FunctionsUtils__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony import */ var _DiscardablePromiseUtils__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./DiscardablePromiseUtils */ "./jsTarget/web/app/common/utils/DiscardablePromiseUtils.ts");
/* harmony import */ var _StringUtils__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./StringUtils */ "./jsTarget/web/app/common/utils/StringUtils.ts");
/* harmony import */ var _WindowUtils__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./WindowUtils */ "./jsTarget/web/app/common/utils/WindowUtils.ts");
/* harmony import */ var _services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
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
var __decorate = (undefined && undefined.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};









/**
 * @ngdoc service
 * @name functionsModule
 *
 * @description
 * provides a list of useful functions that can be used as part of the SmartEdit framework.
 */
var FunctionsModule = /** @class */ (function () {
    function FunctionsModule() {
    }
    FunctionsModule = __decorate([
        Object(_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_8__["SeModule"])({
            imports: [
                'yjqueryModule',
                'yLoDashModule',
                'ngSanitize'
            ],
            providers: [
                _dtos_Cloneable__WEBPACK_IMPORTED_MODULE_1__["CloneableUtils"],
                _UrlUtils__WEBPACK_IMPORTED_MODULE_3__["UrlUtils"],
                _CryptographicUtils__WEBPACK_IMPORTED_MODULE_2__["CryptographicUtils"],
                _FunctionsUtils__WEBPACK_IMPORTED_MODULE_4__["FunctionsUtils"],
                _StringUtils__WEBPACK_IMPORTED_MODULE_6__["StringUtils"],
                _WindowUtils__WEBPACK_IMPORTED_MODULE_7__["WindowUtils"],
                _DiscardablePromiseUtils__WEBPACK_IMPORTED_MODULE_5__["DiscardablePromiseUtils"],
                {
                    provide: "ParseError",
                    useFactory: function () {
                        return function (value) {
                            this.value = value;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.getResourcePath
                 *
                 * @description
                 * makes url absolute (with provided domain) if not yet
                 *
                 * @param {String} domain the domain with witch to prepend the url if not absolute
                 * @param {String} url the url to tests
                 */
                {
                    provide: "getAbsoluteURL",
                    useFactory: function () {
                        return function (domain, url) {
                            // url regex
                            // scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
                            var re = new RegExp("([a-zA-Z0-9]+://)" + // scheme
                                "([a-zA-Z0-9_]+:[a-zA-Z0-9_]+@)?" + // user:password
                                "([a-zA-Z0-9.-]+)" + // hostname
                                "|([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)" + // or ip
                                "(:[0-9]+)?" + // port
                                "(/.*)?" // everything else
                            );
                            if (re.exec(url)) {
                                return url;
                            }
                            else {
                                return domain + url;
                            }
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.getOrigin
                 *
                 * @description
                 * returns document location origin
                 * Some browsers still do not support W3C document.location.origin, this function caters for gap.
                 *
                 * @param {String =} url optional any url
                 */
                {
                    provide: 'getOrigin',
                    useFactory: function () {
                        return function (url) {
                            if (url) {
                                var link = document.createElement('a');
                                link.setAttribute('href', url);
                                var origin = link.protocol + "//" + link.hostname + (link.port ? ':' + link.port : '');
                                link = null; // GC
                                return origin;
                            }
                            else {
                                return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
                            }
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.isBlank
                 *
                 * @description
                 * <b>isBlank</b> will check if a given string is undefined or null or empty.
                 * - returns TRUE for undefined / null/ empty string
                 * - returns FALSE otherwise
                 *
                 * @param {String} inputString any input string.
                 *
                 * @returns {boolean} true if the string is null else false
                 */
                {
                    provide: 'isBlank',
                    useFactory: function () {
                        return function (value) {
                            return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.extend
                 *
                 * @description
                 * <b>extend</b> provides a convenience to either default a new child or "extend" an existing child with the prototype of the parent
                 *
                 * @param {Class} ParentClass which has a prototype you wish to extend.
                 * @param {Class} ChildClass will have its prototype set.
                 *
                 * @returns {Class} ChildClass which has been extended
                 */
                {
                    provide: 'extend',
                    useFactory: function () {
                        return function (ParentClass, ChildClass) {
                            if (!ChildClass) {
                                // tslint:disable-next-line:no-empty
                                ChildClass = function () { };
                            }
                            ChildClass.prototype = Object.create(ParentClass.prototype);
                            return ChildClass;
                        };
                    }
                },
                /**
                 * @deprecated since 6.6, use {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind bind} instead
                 * @ngdoc service
                 * @name functionsModule.hitch
                 *
                 * @description
                 * <b>hitch</b> will create a new function that will pass our desired context (scope) to the given function.
                 * This method will also pre-bind the given parameters.
                 *
                 * @param {Object} scope scope that is to be assigned.
                 * @param {Function} method the method that needs binding.
                 *
                 * @returns {Function} a new function thats binded to the given scope
                 */
                {
                    provide: 'hitch',
                    useFactory: function () {
                        return function (scope, method) {
                            var argumentArray = Array.prototype.slice.call(arguments); // arguments is not an array
                            // (from  http://www.sitepoint.com/arguments-a-javascript-oddity/)
                            var preboundArguments = argumentArray.slice(2);
                            return function lockedMethod() {
                                // from here, "arguments" are the arguments passed to lockedMethod
                                var postBoundArguments = Array.prototype.slice.call(arguments);
                                return method.apply(scope, preboundArguments.concat(postBoundArguments));
                            };
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.customTimeout
                 *
                 * @description
                 * <b>customTimeout</b> will call the javascrit's native setTimeout method to execute a given function after a specified period of time.
                 * This method is better than using $timeout since it is difficult to assert on $timeout during end-to-end testing.
                 *
                 * @param {Function} func function that needs to be executed after the specified duration.
                 * @param {Number} duration time in milliseconds.
                 */
                {
                    provide: 'customTimeout',
                    useFactory: function ($rootScope) {
                        return function (func, duration) {
                            setTimeout(function () {
                                func();
                                $rootScope.$digest();
                            }, duration);
                        };
                    },
                    deps: ['$rootScope']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.copy
                 *
                 * @description
                 * <b>copy</b> will do a deep copy of the given input object.
                 *
                 * @param {Object} candidate the javaScript value that needs to be deep copied.
                 *
                 * @returns {Object} A deep copy of the input
                 */
                {
                    provide: 'copy',
                    useFactory: function () {
                        return function (candidate) {
                            return JSON.parse(JSON.stringify(candidate));
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.merge
                 *
                 * @description
                 * <b>merge</b> will merge the contents of two objects together into the first object.
                 *
                 * @param {Object} target any JavaScript object.
                 * @param {Object} source any JavaScript object.
                 *
                 * @returns {Object} a new object as a result of merge
                 */
                {
                    provide: 'merge',
                    useFactory: function (yjQuery) {
                        return function (source, target) {
                            yjQuery.extend(source, target);
                            return source;
                        };
                    },
                    deps: ['yjQuery']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.getQueryString
                 *
                 * @description
                 * <b>getQueryString</b> will convert a given object into a query string.
                 *
                 * Below is the code snippet for sample input and sample output:
                 *
                 * <pre>
                 * var params = {
                 *  key1 : 'value1',
                 *  key2 : 'value2',
                 *  key3 : 'value3'
                 *  }
                 *
                 *  var output = getQueryString(params);
                 *
                 *  // The output is '?&key1=value1&key2=value2&key3=value3'
                 *
                 * </pre>
                 *
                 * @param {Object} params Object containing a list of params.
                 *
                 * @returns {String} a query string
                 */
                {
                    provide: 'getQueryString',
                    useFactory: function () {
                        return function (params) {
                            var queryString = "";
                            if (params) {
                                for (var param in params) {
                                    if (params.hasOwnProperty(param)) {
                                        queryString += '&' + param + "=" + params[param];
                                    }
                                }
                            }
                            return "?" + queryString;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.getURI
                 *
                 * @description
                 * Will return the URI part of a URL
                 * @param {String} url the URL the URI of which is to be returned
                 */
                {
                    provide: 'getURI',
                    useFactory: function () {
                        return function (url) {
                            return url && url.indexOf("?") > -1 ? url.split("?")[0] : url;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.parseQuery
                 *
                 * @description
                 * <b>parseQuery</b> will convert a given query string to an object.
                 *
                 * Below is the code snippet for sample input and sample output:
                 *
                 * <pre>
                 * var query = '?key1=value1&key2=value2&key3=value3';
                 *
                 * var output = parseQuery(query);
                 *
                 * // The output is { key1 : 'value1', key2 : 'value2', key3 : 'value3' }
                 *
                 * </pre>
                 *
                 * @param {String} query String that needs to be parsed.
                 *
                 * @returns {Object} an object containing all params of the given query
                 */
                {
                    provide: 'parseQuery',
                    useFactory: function () {
                        return function (str) {
                            var objURL = {};
                            str.replace(new RegExp("([^?=&]+)(=([^&]*))?", "g"), function ($0, $1, $2, $3) {
                                objURL[$1] = $3;
                            });
                            return objURL;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.trim
                 *
                 * @description
                 * <b>trim</b> will remove spaces at the beginning and end of a given string.
                 *
                 * @param {String} inputString any input string.
                 *
                 * @returns {String} the newly modified string without spaces at the beginning and the end
                 */
                {
                    provide: 'trim',
                    useFactory: function () {
                        return function (aString) {
                            var regExpBeginning = /^\s+/;
                            var regExpEnd = /\s+$/;
                            return aString.replace(regExpBeginning, "").replace(regExpEnd, "");
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.convertToArray
                 *
                 * @description
                 * <b>convertToArray</b> will convert the given object to array.
                 * The output array elements are an object that has a key and value,
                 * where key is the original key and value is the original object.
                 *
                 * @param {Object} inputObject any input object.
                 *
                 * @returns {Array} the array created from the input object
                 */
                {
                    provide: 'convertToArray',
                    useFactory: function () {
                        return function (object) {
                            var configuration = [];
                            for (var key in object) {
                                if (key.indexOf('$') !== 0 && key.indexOf('toJSON') !== 0) {
                                    configuration.push({
                                        key: key,
                                        value: object[key]
                                    });
                                }
                            }
                            return configuration;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.injectJS
                 *
                 * @description
                 * <b>injectJS</b> will inject script tags into html for a given set of sources.
                 *
                 */
                {
                    provide: 'injectJS',
                    useFactory: function () {
                        function getInjector() {
                            return $script;
                        }
                        return {
                            getInjector: getInjector,
                            execute: function (conf) {
                                var _this = this;
                                var srcs = conf.srcs;
                                var index = conf.index;
                                var callback = conf.callback;
                                if (!srcs.length) {
                                    callback();
                                    return;
                                }
                                if (index === undefined) {
                                    index = 0;
                                }
                                if (srcs[index] !== undefined) {
                                    this.getInjector()(srcs[index], function () {
                                        if (index + 1 < srcs.length) {
                                            _this.execute({
                                                srcs: srcs,
                                                index: index + 1,
                                                callback: callback
                                            });
                                        }
                                        else if (typeof callback === 'function') {
                                            callback();
                                        }
                                    });
                                }
                            }
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.uniqueArray
                 *
                 * @description
                 * <b>uniqueArray</b> will return the first Array argument supplemented with new entries from the second Array argument.
                 *
                 * @param {Array} array1 any JavaScript array.
                 * @param {Array} array2 any JavaScript array.
                 */
                {
                    provide: 'uniqueArray',
                    useFactory: function () {
                        return function (array1, array2) {
                            array2.forEach(function (instance) {
                                if (array1.indexOf(instance) === -1) {
                                    array1.push(instance);
                                }
                            });
                            return array1;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.regExpFactory
                 *
                 * @description
                 * <b>regExpFactory</b> will convert a given pattern into a regular expression.
                 * This method will prepend and append a string with ^ and $ respectively replaces
                 * and wildcards (*) by proper regex wildcards.
                 *
                 * @param {String} pattern any string that needs to be converted to a regular expression.
                 *
                 * @returns {RegExp} a regular expression generated from the given string.
                 *
                 * @deprecated since 1811, use {@link functionsModule.service:StringUtils#regExpFactory StringUtils#regExpFactory}
                 *
                 */
                {
                    provide: 'regExpFactory',
                    useFactory: function (stringUtils) {
                        return stringUtils.regExpFactory;
                    },
                    deps: ['stringUtils']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.generateIdentifier
                 *
                 * @description
                 * <b>generateIdentifier</b> will generate a unique string based on system time and a random generator.
                 *
                 * @returns {String} a unique identifier.
                 *
                 */
                {
                    provide: 'generateIdentifier',
                    useFactory: function () {
                        return function () {
                            var d = new Date().getTime();
                            if (window.performance && typeof window.performance.now === "function") {
                                d += window.performance.now(); // use high-precision timer if available
                            }
                            var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                                var r = (d + Math.random() * 16) % 16 | 0;
                                d = Math.floor(d / 16);
                                return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
                            });
                            return uuid;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.escapeHtml
                 *
                 * @description
                 * <b>escapeHtml</b> will escape &, <, >, " and ' characters .
                 *
                 * @param {String} a string that needs to be escaped.
                 *
                 * @returns {String} the escaped string.
                 *
                 */
                {
                    provide: 'escapeHtml',
                    useFactory: function () {
                        return function (str) {
                            if (typeof str === 'string') {
                                return str.replace(/&/g, '&amp;')
                                    .replace(/>/g, '&gt;')
                                    .replace(/</g, '&lt;')
                                    .replace(/"/g, '&quot;')
                                    .replace(/'/g, '&apos;');
                            }
                            else {
                                return str;
                            }
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.sanitize
                 *
                 * @description
                 * <b>escapes any harmful scripting from a string, leaves innocuous HTML untouched/b>
                 *
                 * @param {String} a string that needs to be sanitized.
                 *
                 * @returns {String} the sanitized string.
                 *
                 */
                {
                    provide: 'sanitize',
                    useFactory: function (isBlank) {
                        return function (str) {
                            /* The correct solution for this is to use Negative Lookbehind Regex expression which is available as part of ES2018. // str.replace(/(?:(?<!\\)([()]))/g, '\\$1')
                            But in order to support cross browser compatibility, the string is reversed and negative lookahead is used instead. */
                            return !isBlank(str) ? str.split('').reverse().join('').replace(/(?:(([()])(?!\\)))/g, '$1\\').split('').reverse().join('') : str;
                        };
                    },
                    deps: ['isBlank']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.sanitizeHTML
                 *
                 * @description
                 * <b>sanitizeHTML</b> will remove breaks and space .
                 *
                 * @param {String} a string that needs to be escaped.
                 *
                 * @returns {String} the sanitized HTML.
                 *
                 */
                {
                    provide: 'sanitizeHTML',
                    useFactory: function (isBlank) {
                        return function (obj) {
                            var result = angular__WEBPACK_IMPORTED_MODULE_0__["copy"](obj);
                            if (!isBlank(result)) {
                                result = result.replace(/(\r\n|\n|\r)/gm, '').replace(/>\s+</g, '><').replace(/<\/br\>/g, '');
                            }
                            return result;
                        };
                    },
                    deps: ['isBlank']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.toPromise
                 *
                 * @description
                 * <b>toPromise</> transforms a function into a function that is guaranteed to return a Promise that resolves to the
                 * original return value of the function, rejects with the rejected return value and rejects with an exception object when the invocation fails
                 */
                {
                    provide: 'toPromise',
                    useFactory: function ($q, $log) {
                        return function (method, context) {
                            return function () {
                                try {
                                    return $q.when(method.apply(context, arguments));
                                }
                                catch (e) {
                                    $log.error('execution of a method that was turned into a promise failed');
                                    $log.error(e);
                                    return $q.reject(e);
                                }
                            };
                        };
                    },
                    deps: ['$q', '$log']
                },
                /**
                 * Checks if `value` is a function.
                 *
                 * @static
                 * @category Objects
                 * @param {*} value The value to check.
                 * @returns {boolean} Returns `true` if the `value` is a function, else `false`.
                 */
                {
                    provide: 'isFunction',
                    useFactory: function () {
                        return function (value) {
                            return typeof value === 'function';
                        };
                    }
                },
                // check if the value is the ECMAScript language type of Object
                {
                    provide: 'isObject',
                    useFactory: function () {
                        /** Used to determine if values are of the language type Object */
                        var objectTypes = {
                            boolean: false,
                            function: true,
                            object: true,
                            number: false,
                            string: false,
                            undefined: false
                        };
                        return function (value) {
                            return !!(value && objectTypes[typeof value]);
                        };
                    }
                },
                {
                    provide: 'debounce',
                    useFactory: function (isFunction, isObject) {
                        // tslint:disable-next-line
                        var TypeError = /** @class */ (function () {
                            function TypeError() {
                            }
                            return TypeError;
                        }());
                        return function (func, wait, options) {
                            var args;
                            var maxTimeoutId;
                            var result;
                            var stamp;
                            var thisArg;
                            var timeoutId;
                            var trailingCall;
                            var leading;
                            var lastCalled = 0;
                            var maxWait = false;
                            var trailing = true;
                            var isCalled;
                            if (!isFunction(func)) {
                                throw new TypeError();
                            }
                            wait = Math.max(0, wait) || 0;
                            if (options === true) {
                                leading = true;
                                trailing = false;
                            }
                            else if (isObject(options)) {
                                leading = options.leading;
                                maxWait = 'maxWait' in options && (Math.max(wait, options.maxWait) || 0);
                                trailing = 'trailing' in options ? options.trailing : trailing;
                            }
                            var delayed = function () {
                                var remaining = wait - (Date.now() - stamp);
                                if (remaining <= 0) {
                                    if (maxTimeoutId) {
                                        clearTimeout(maxTimeoutId);
                                    }
                                    isCalled = trailingCall;
                                    maxTimeoutId = timeoutId = trailingCall = undefined;
                                    if (isCalled) {
                                        lastCalled = Date.now();
                                        result = func.apply(thisArg, args);
                                        if (!timeoutId && !maxTimeoutId) {
                                            args = thisArg = null;
                                        }
                                    }
                                }
                                else {
                                    timeoutId = setTimeout(delayed, remaining);
                                }
                            };
                            var maxDelayed = function () {
                                if (timeoutId) {
                                    clearTimeout(timeoutId);
                                }
                                maxTimeoutId = timeoutId = trailingCall = undefined;
                                if (trailing || (maxWait !== wait)) {
                                    lastCalled = Date.now();
                                    result = func.apply(thisArg, args);
                                    if (!timeoutId && !maxTimeoutId) {
                                        args = thisArg = null;
                                    }
                                }
                            };
                            return function () {
                                args = arguments;
                                stamp = Date.now();
                                thisArg = this;
                                trailingCall = trailing && (timeoutId || !leading);
                                var leadingCall;
                                if (maxWait === false) {
                                    leadingCall = leading && !timeoutId;
                                }
                                else {
                                    if (!maxTimeoutId && !leading) {
                                        lastCalled = stamp;
                                    }
                                    var remaining = maxWait - (stamp - lastCalled);
                                    isCalled = remaining <= 0;
                                    if (isCalled) {
                                        if (maxTimeoutId) {
                                            maxTimeoutId = clearTimeout(maxTimeoutId);
                                        }
                                        lastCalled = stamp;
                                        result = func.apply(thisArg, args);
                                    }
                                    else if (!maxTimeoutId) {
                                        maxTimeoutId = setTimeout(maxDelayed, remaining);
                                    }
                                }
                                if (isCalled && timeoutId) {
                                    timeoutId = clearTimeout(timeoutId);
                                }
                                else if (!timeoutId && wait !== maxWait) {
                                    timeoutId = setTimeout(delayed, wait);
                                }
                                if (leadingCall) {
                                    isCalled = true;
                                    result = func.apply(thisArg, args);
                                }
                                if (isCalled && !timeoutId && !maxTimeoutId) {
                                    args = thisArg = null;
                                }
                                return result;
                            };
                        };
                    },
                    deps: ['isFunction', 'isObject']
                },
                {
                    provide: 'throttle',
                    useFactory: function (debounce, isFunction, isObject) {
                        return function (func, wait, options) {
                            var leading = true;
                            var trailing = true;
                            if (!isFunction(func)) {
                                throw new TypeError();
                            }
                            if (options === false) {
                                leading = false;
                            }
                            else if (isObject(options)) {
                                leading = 'leading' in options ? options.leading : leading;
                                trailing = 'trailing' in options ? options.trailing : trailing;
                            }
                            options = {};
                            options.leading = leading;
                            options.maxWait = wait;
                            options.trailing = trailing;
                            return debounce(func, wait, options);
                        };
                    },
                    deps: ['debounce', 'isFunction', 'isObject']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.parseHTML
                 *
                 * @description
                 * parses a string HTML into a queriable DOM object, stripping any JavaScript from the HTML.
                 *
                 * @param {String} stringHTML, the string representation of the HTML to parse
                 */
                {
                    provide: 'parseHTML',
                    useFactory: function (yjQuery) {
                        return function (stringHTML) {
                            return yjQuery.parseHTML(stringHTML);
                        };
                    },
                    deps: ['yjQuery']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.unsafeParseHTML
                 *
                 * @description
                 * parses a string HTML into a queriable DOM object, preserving any JavaScript present in the HTML.
                 * Note - as this preserves the JavaScript present it must only be used on HTML strings originating
                 * from a known safe location. Failure to do so may result in an XSS vulnerability.
                 *
                 * @param {String} stringHTML, the string representation of the HTML to parse
                 */
                {
                    provide: 'unsafeParseHTML',
                    useFactory: function (yjQuery) {
                        return function (stringHTML) {
                            return yjQuery.parseHTML(stringHTML, null, true);
                        };
                    },
                    deps: ['yjQuery']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.extractFromElement
                 *
                 * @description
                 * parses a string HTML into a queriable DOM object
                 *
                 * @param {Object} parent, the DOM element from which we want to extract matching selectors
                 * @param {String} extractionSelector, the yjQuery selector identifying the elements to be extracted
                 */
                {
                    provide: 'extractFromElement',
                    useFactory: function (yjQuery) {
                        return function (parent, extractionSelector) {
                            parent = yjQuery(parent);
                            return parent.filter(extractionSelector).add(parent.find(extractionSelector));
                        };
                    },
                    deps: ['yjQuery']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.closeOpenModalsOnBrowserBack
                 *
                 * @description
                 * close any open modal window when a user clicks browser back button
                 *
                 * @param {Object} modalStack, the $modalStack service of angular-ui.
                 */
                {
                    provide: 'closeOpenModalsOnBrowserBack',
                    useFactory: function ($uibModalStack) {
                        return function () {
                            if ($uibModalStack.getTop()) {
                                $uibModalStack.dismissAll();
                            }
                        };
                    },
                    deps: ['$uibModalStack']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:URIBuilder
                 *
                 * @description
                 * builder or URIs, build() method must be invoked to actually retrieve a URI
                 *
                 * @param {Object} modalStack, the $modalStack service of angular-ui.
                 */
                {
                    provide: 'URIBuilder',
                    useFactory: function (lodash) {
                        return function URIBuilder(uri) {
                            this.uri = uri;
                            this.build = function () {
                                return this.uri;
                            };
                            /**
                             * @ngdoc method
                             * @name functionsModule.service:URIBuilder#replaceParams
                             * @methodOf functionsModule.service:URIBuilder
                             *
                             * @description
                             * Substitute all placeholders in the URI with the matching values in the given params
                             *
                             * @param {Object} params a map of placeholder names / values
                             */
                            this.replaceParams = function (params) {
                                var clone = lodash.cloneDeep(this);
                                if (params) {
                                    // order the keys by descending length
                                    var keys = Object.keys(params).sort(function (a, b) {
                                        return b.length - a.length;
                                    });
                                    keys.forEach(function (key) {
                                        var re = new RegExp('\\b' + key + '\\b');
                                        clone.uri = clone.uri.replace(':' + key, params[key]).replace(re, params[key]);
                                    });
                                }
                                return clone;
                            };
                        };
                    },
                    deps: ["lodash"]
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:getDataFromResponse
                 *
                 * @description
                 * when provided with a response returned from a backend call, will filter the response
                 * to retrieve the data of interest.
                 *
                 * @param {Object} response, response returned from a backend call.
                 * @returns {Array} Returns the array from the response.
                 */
                {
                    provide: 'getDataFromResponse',
                    useFactory: function () {
                        return function (response) {
                            var dataKey = Object.keys(response).filter(function (key) {
                                return response[key] instanceof Array;
                            })[0];
                            return response[dataKey];
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:getKeyHoldingDataFromResponse
                 *
                 * @description
                 * when provided with a response returned from a backend call, will filter the response
                 * to retrieve the key holding the data of interest.
                 *
                 * @param {Object} response, response returned from a backend call.
                 * @returns {String} Returns the name of the key holding the array from the response.
                 */
                {
                    provide: 'getKeyHoldingDataFromResponse',
                    useFactory: function () {
                        return function (response) {
                            var dataKey = Object.keys(response).filter(function (key) {
                                return response[key] instanceof Array;
                            })[0];
                            return dataKey;
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:resetObject
                 *
                 * @description
                 * Resets a given object's properties' values
                 *
                 * @param {Object} targetObject, the object to reset
                 * @param {Object} modelObject, an object that contains the structure that targetObject should have after a reset
                 * @returns {Object} Returns the object that has been reset
                 */
                {
                    provide: 'resetObject',
                    useFactory: function (copy) {
                        return function (targetObject, modelObject) {
                            if (!targetObject) {
                                targetObject = copy(modelObject);
                            }
                            else {
                                for (var i in targetObject) {
                                    if (targetObject.hasOwnProperty(i)) {
                                        delete targetObject[i];
                                    }
                                }
                                angular__WEBPACK_IMPORTED_MODULE_0__["extend"](targetObject, copy(modelObject));
                            }
                            return targetObject;
                        };
                    },
                    deps: ['copy']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:isFunctionEmpty
                 *
                 * @description
                 * Will determine whether a function body is empty or should be considered empty for proxying purposes
                 * @deprecated since 1808, use {@link functionsModule.service:FunctionsUtils#isEmpty FunctionsUtils#isEmpty}
                 * @param {Function} fn, the function to evaluate
                 * @returns {Boolean} a boolean.
                 * @deprecated since 1808, use {@link functionsModule.service:FunctionsUtils#isEmpty FunctionsUtils#isEmpty}
                 */
                {
                    provide: 'isFunctionEmpty',
                    useFactory: function (functionsUtils) {
                        return function (fn) {
                            return functionsUtils.isEmpty(fn);
                        };
                    },
                    deps: ['functionsUtils']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:isObjectEmptyDeep
                 *
                 * @description
                 * Will check if the object is empty and will return true if each and every property of the object is empty
                 *
                 * @param {Object} value, the value to evaluate
                 * @returns {Boolean} a boolean.
                 */
                {
                    provide: 'isObjectEmptyDeep',
                    useFactory: function (lodash) {
                        return function (value) {
                            if (lodash.isObject(value)) {
                                for (var key in value) {
                                    if (value.hasOwnProperty(key)) {
                                        if (!lodash.isEmpty(value[key])) {
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            }
                            return lodash.isString(value) ? lodash.isEmpty(value) : lodash.isNil(value);
                        };
                    },
                    deps: ['lodash']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:isAllTruthy
                 *
                 * @description
                 * Iterate on the given array of Functions, return true if each function returns true
                 *
                 * @param {Array} arguments the functions
                 *
                 * @return {Boolean} true if every function returns true
                 */
                {
                    provide: 'isAllTruthy',
                    useFactory: function () {
                        return function () {
                            var fns = Array.prototype.slice.call(arguments);
                            return function () {
                                var args = arguments;
                                return fns.every(function (f) {
                                    return f.apply(f, args);
                                });
                            };
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:isAnyTruthy
                 *
                 * @description
                 * Iterate on the given array of Functions, return true if at least one function returns true
                 *
                 * @param {Array} arguments the functions
                 *
                 * @return {Boolean} true if at least one function returns true
                 */
                {
                    provide: 'isAnyTruthy',
                    useFactory: function () {
                        return function () {
                            var fns = Array.prototype.slice.call(arguments);
                            return function () {
                                var args = arguments;
                                return fns.some(function (f) {
                                    return f.apply(f, args);
                                });
                            };
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:formatDateAsUtc
                 *
                 * @description
                 * Formats provided dateTime as utc.
                 *
                 * @param {Object|String} dateTime DateTime to format in utc.
                 *
                 * @return {String} formatted string.
                 */
                {
                    provide: 'formatDateAsUtc',
                    useFactory: function (DATE_CONSTANTS) {
                        return function (dateTime) {
                            return moment(dateTime).utc().format(DATE_CONSTANTS.MOMENT_ISO);
                        };
                    },
                    deps: ['DATE_CONSTANTS']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.getEncodedString
                 *
                 * @description
                 * <b>getEncodedString</b> will creates a base-64 encoded ASCII string
                 * from the String passed as input
                 *
                 * @returns {String} a base-64 encoded ASCII string.
                 *
                 */
                {
                    provide: 'getEncodedString',
                    useFactory: function () {
                        return function (passedString) {
                            if (typeof passedString === "string") {
                                return btoa(passedString);
                            }
                            else {
                                throw new Error('getEncodedString called with input of type "' + typeof passedString + '" when only "string" is accepted.');
                            }
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.encode
                 *
                 * @description
                 * will return a encoded value for any JSON object passed as argument
                 * @param {object} JSON object to be encoded
                 */
                {
                    provide: 'encode',
                    useFactory: function () {
                        return function (object) {
                            /* first we use encodeURIComponent to get percent-encoded UTF-8,
                             * then we convert the percent encodings into raw bytes which
                             * can be fed into btoa.
                             * from https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
                             */
                            return btoa(encodeURIComponent(JSON.stringify(object)).replace(/%([0-9A-F]{2})/g, function toSolidBytes(match, p1) {
                                // return String.fromCharCode('0x' + p1);
                                return String.fromCharCode(parseInt(p1, 16));
                            }));
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.service:compareHTMLElementsPosition
                 *
                 * @description
                 * A function to sort an array containing DOM elements according to their position in the DOM
                 *
                 * @param {key =} key Optional key value to get the
                 *
                 * @return {Function} the compare function to use with array.sort(compareFunction) to order DOM elements as they would appear in the DOM
                 */
                {
                    provide: 'compareHTMLElementsPosition',
                    useFactory: function () {
                        return function (key) {
                            return function (a, b) {
                                if (key) {
                                    a = a[key];
                                    b = b[key];
                                }
                                if (a === b) {
                                    return 0;
                                }
                                if (!a.compareDocumentPosition) {
                                    // support for IE8 and below
                                    return a.sourceIndex - b.sourceIndex;
                                }
                                if (a.compareDocumentPosition(b) & 2) {
                                    // Note: CompareDocumentPosition returns the compared value as a bitmask that can take several values. 
                                    // 2 represents DOCUMENT_POSITION_PRECEDING, which means that b comes before a. 
                                    return 1;
                                }
                                return -1;
                            };
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.isIframe
                 *
                 * @description
                 * <b>isIframe</b> will check if the current document is in an iFrame.
                 *
                 * @returns {boolean} true if the current document is in an iFrame.
                 */
                {
                    provide: 'isIframe',
                    useFactory: function ($window) {
                        return function () {
                            return $window.top !== $window;
                        };
                    },
                    deps: ['$window']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.isPointOverElement
                 *
                 * @description
                 * <b>isPointOverElement</b> will check if the given point is over the htmlElement
                 *
                 * @param {Object} point point coordinates
                 * @param {Number} point.x mouse x position
                 * @param {Number} point.y mouse y position
                 * @param {HTMLElement} htmlElement htmlElement to test
                 *
                 * @returns {boolean} true if the given point is over the htmlElement
                 */
                {
                    provide: 'isPointOverElement',
                    useFactory: function () {
                        return function (point, htmlElement) {
                            var domRect = htmlElement.getBoundingClientRect();
                            return (point.x >= domRect.left && point.x <= domRect.right && point.y >= domRect.top && point.y <= domRect.bottom);
                        };
                    }
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.areIntersecting
                 *
                 * @description
                 * determines whether 2 BoundingClientRect are intersecting even partially
                 *
                 * @param {Object} boundingClientRect1 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
                 * @param {Object} boundingClientRect2 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
                 * @returns {boolean} true if there is a partial or total intersection
                 */
                {
                    provide: 'areIntersecting',
                    useFactory: function () {
                        return function (boundingClientRect1, boundingClientRect2) {
                            return !(boundingClientRect2.left > (boundingClientRect1.left + boundingClientRect1.width) ||
                                (boundingClientRect2.left + boundingClientRect2.width) < boundingClientRect1.left ||
                                boundingClientRect2.top > (boundingClientRect1.top + boundingClientRect1.height) ||
                                (boundingClientRect2.top + boundingClientRect2.height) < boundingClientRect1.top);
                        };
                    }
                },
                {
                    provide: 'EXTENDED_VIEW_PORT_MARGIN',
                    useValue: 1000
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.isInExtendedViewPort
                 *
                 * @description
                 * determines whether a DOM element is partially or totally intersecting with the "extended" viewPort
                 * the "extended" viewPort is the real viewPort that extends up and down by a margin, in pixels, given by the overridable constant EXTENDED_VIEW_PORT_MARGIN
                 * @param {HtmlElement} element a DOM element
                 * @returns {boolean} true if the given element is in the extended view port
                 */
                {
                    provide: 'isInExtendedViewPort',
                    useFactory: function ($document, yjQuery, areIntersecting, EXTENDED_VIEW_PORT_MARGIN) {
                        return function (element) {
                            if (!yjQuery.contains($document[0], element)) {
                                return false;
                            }
                            var bounds = yjQuery(element).offset();
                            bounds.width = yjQuery(element).outerWidth();
                            bounds.height = yjQuery(element).outerHeight();
                            var doc = document.scrollingElement || document.documentElement;
                            return areIntersecting({
                                left: -EXTENDED_VIEW_PORT_MARGIN + doc.scrollLeft,
                                width: window.innerWidth + 2 * EXTENDED_VIEW_PORT_MARGIN,
                                top: -EXTENDED_VIEW_PORT_MARGIN + doc.scrollTop,
                                height: window.innerHeight + 2 * EXTENDED_VIEW_PORT_MARGIN
                            }, bounds);
                        };
                    },
                    deps: ['$document', 'yjQuery', 'areIntersecting', 'EXTENDED_VIEW_PORT_MARGIN']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.deepIterateOverObjectWith
                 *
                 * @description
                 * Iterates over object and allows to modify a value using callback function.
                 * @param {Object} obj an object to iterate.
                 * @param {Function} callback callback to apply to each object value.
                 * @returns {Object} the object with modified values.
                 */
                {
                    provide: 'deepIterateOverObjectWith',
                    useFactory: function (lodash) {
                        return function deepIterateOverObjectWith(obj, callback) {
                            return lodash.reduce(obj, function (result, value, key) {
                                if (lodash.isPlainObject(value)) {
                                    result[key] = deepIterateOverObjectWith(value, callback);
                                }
                                else {
                                    result[key] = callback(value);
                                }
                                return result;
                            }, {});
                        };
                    },
                    deps: ['lodash']
                },
                /**
                 * @ngdoc service
                 * @name functionsModule.deepObjectPropertyDiff
                 *
                 * @description
                 * Returns an object that contains list of fields and for each field it has a boolean value
                 * which is true when the property was modified, added or removed, false otherwise.
                 * @param {Object} object The first object to inspect.
                 * @param {Object} source The second object to inspect.
                 * @returns {Object} the diff object.
                 */
                {
                    provide: 'deepObjectPropertyDiff',
                    useFactory: function (lodash, deepIterateOverObjectWith) {
                        return function (firstObject, secondObject) {
                            // tslint:disable-next-line:no-empty
                            function CHANGED_PROPERTY() { }
                            // tslint:disable-next-line:no-empty
                            function NON_CHANGED_PROPERTY() { }
                            var mergedObj = lodash.mergeWith(lodash.cloneDeep(firstObject), secondObject, function (prValue, cpValue) {
                                if (!lodash.isPlainObject(prValue)) {
                                    return !lodash.isEqual(prValue, cpValue) ? CHANGED_PROPERTY : NON_CHANGED_PROPERTY;
                                }
                                // Note: Previous versions of lodash could work with null, but the latest version of lodash requires 
                                // undefined to be returned. 
                                return undefined;
                            });
                            // If the field is not CHANGED_PROPERTY/NON_CHANGED_PROPERTY then it was removed or added.
                            var sanitizedObj = deepIterateOverObjectWith(mergedObj, function (value) {
                                if (value !== CHANGED_PROPERTY && value !== NON_CHANGED_PROPERTY) {
                                    return CHANGED_PROPERTY;
                                }
                                else {
                                    return value;
                                }
                            });
                            // If it's CHANGED_PROPERTY return true otherwise false.
                            return deepIterateOverObjectWith(sanitizedObj, function (value) {
                                return value === CHANGED_PROPERTY ? true : false;
                            });
                        };
                    },
                    deps: ['lodash', 'deepIterateOverObjectWith']
                },
                {
                    provide: 'isInDOM',
                    useFactory: function ($document, yjQuery) {
                        return function (component) {
                            return yjQuery.contains($document[0], component);
                        };
                    },
                    deps: ['$document', 'yjQuery']
                },
                {
                    provide: 'readObjectStructureFactory',
                    useValue: function () {
                        var readObjectStructure = function (json) {
                            var currentWindow = window;
                            if (json === undefined || json === null || json.then) {
                                return json;
                            }
                            if (typeof json === 'function') {
                                return "FUNCTION";
                            }
                            else if (typeof json === 'number') {
                                return "NUMBER";
                            }
                            else if (typeof json === 'string') {
                                return "STRING";
                            }
                            else if (typeof json === 'boolean') {
                                return "BOOLEAN";
                            }
                            else if (currentWindow.smarteditLodash.isElement(json)) {
                                return "ELEMENT";
                            }
                            else if (json.hasOwnProperty && json.hasOwnProperty('length')) { // jquery or Array
                                if (json.forEach) {
                                    var arr_1 = [];
                                    json.forEach(function (arrayElement) {
                                        arr_1.push(readObjectStructure(arrayElement));
                                    });
                                    return arr_1;
                                }
                                else {
                                    return "JQUERY";
                                }
                            }
                            else { // JSON
                                var clone_1 = {};
                                Object.keys(json).forEach(function (directKey) {
                                    if (directKey.indexOf("$") !== 0) {
                                        clone_1[directKey] = readObjectStructure(json[directKey]);
                                    }
                                });
                                return clone_1;
                            }
                        };
                        return readObjectStructure;
                    }
                }
            ]
        })
    ], FunctionsModule);
    return FunctionsModule;
}());



/***/ }),

/***/ "./jsTarget/web/app/common/utils/index.ts":
/*!************************************************!*\
  !*** ./jsTarget/web/app/common/utils/index.ts ***!
  \************************************************/
/*! exports provided: CryptographicUtils, FunctionsUtils, StringUtils, WindowUtils, FunctionsModule, UrlUtils */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _UrlUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./UrlUtils */ "./jsTarget/web/app/common/utils/UrlUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UrlUtils", function() { return _UrlUtils__WEBPACK_IMPORTED_MODULE_0__["UrlUtils"]; });

/* harmony import */ var _CryptographicUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./CryptographicUtils */ "./jsTarget/web/app/common/utils/CryptographicUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CryptographicUtils", function() { return _CryptographicUtils__WEBPACK_IMPORTED_MODULE_1__["CryptographicUtils"]; });

/* harmony import */ var _FunctionsUtils__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./FunctionsUtils */ "./jsTarget/web/app/common/utils/FunctionsUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FunctionsUtils", function() { return _FunctionsUtils__WEBPACK_IMPORTED_MODULE_2__["FunctionsUtils"]; });

/* harmony import */ var _StringUtils__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./StringUtils */ "./jsTarget/web/app/common/utils/StringUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "StringUtils", function() { return _StringUtils__WEBPACK_IMPORTED_MODULE_3__["StringUtils"]; });

/* harmony import */ var _WindowUtils__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./WindowUtils */ "./jsTarget/web/app/common/utils/WindowUtils.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "WindowUtils", function() { return _WindowUtils__WEBPACK_IMPORTED_MODULE_4__["WindowUtils"]; });

/* harmony import */ var _functionsModule__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./functionsModule */ "./jsTarget/web/app/common/utils/functionsModule.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FunctionsModule", function() { return _functionsModule__WEBPACK_IMPORTED_MODULE_5__["FunctionsModule"]; });

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

// barrel







/***/ }),

/***/ "./smartedit-build/test/unit/coreAnnotationsHelper.ts":
/*!************************************************************!*\
  !*** ./smartedit-build/test/unit/coreAnnotationsHelper.ts ***!
  \************************************************************/
/*! exports provided: CoreAnnotationsHelperMocks, coreAnnotationsHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CoreAnnotationsHelperMocks", function() { return CoreAnnotationsHelperMocks; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "coreAnnotationsHelper", function() { return coreAnnotationsHelper; });
/* harmony import */ var jasmine__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jasmine */ "jasmine");
/* harmony import */ var jasmine__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(jasmine__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var smarteditcommons__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons */ "./jsTarget/web/app/common/index.ts");
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


var CoreAnnotationsHelperMocks = /** @class */ (function () {
    function CoreAnnotationsHelperMocks() {
    }
    return CoreAnnotationsHelperMocks;
}());

// at the time a factory is added to the smarteditcommons namespace, it may have already been ngInjected hence changed into an array
function getFactory(factory) {
    return window.smarteditLodash.isArray(factory) ? factory[factory.length - 1] : factory;
}
// tslint:disable-next-line:max-classes-per-file
var CoreAnnotationsHelper = /** @class */ (function () {
    function CoreAnnotationsHelper() {
    }
    CoreAnnotationsHelper.prototype.initCached = function () {
        ///////////////////////////////////////////////////////
        var $log = jasmine.createSpyObj('$log', ['debug']);
        getFactory(smarteditcommons__WEBPACK_IMPORTED_MODULE_1__["CacheConfigAnnotationFactory"])($log);
        ///////////////////////////////////////////////////////
        var cacheService = jasmine.createSpyObj('cacheService', ['handle', 'evict']);
        cacheService.handle.and.callFake(function (target, methdName, method, invocationArguments) {
            return method.apply(undefined, invocationArguments);
        });
        getFactory(smarteditcommons__WEBPACK_IMPORTED_MODULE_1__["CachedAnnotationFactory"])(cacheService);
        getFactory(smarteditcommons__WEBPACK_IMPORTED_MODULE_1__["InvalidateCacheAnnotationFactory"])(cacheService);
        return cacheService;
    };
    CoreAnnotationsHelper.prototype.initGatewayProxied = function () {
        var gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        var $log = jasmine.createSpyObj('$log', ['debug']);
        getFactory(smarteditcommons__WEBPACK_IMPORTED_MODULE_1__["GatewayProxiedAnnotationFactory"])(gatewayProxy, $log);
        return gatewayProxy;
    };
    CoreAnnotationsHelper.prototype.initOperationContextService = function () {
        var $injector = jasmine.createSpyObj('$injector', ['has', 'get']);
        var operationContextService = jasmine.createSpyObj('operationContextService', ['register']);
        var OPERATION_CONTEXT = jasmine.createSpy();
        getFactory(smarteditcommons__WEBPACK_IMPORTED_MODULE_1__["OperationContextAnnotationFactory"])($injector, operationContextService, OPERATION_CONTEXT);
        return operationContextService;
    };
    CoreAnnotationsHelper.prototype.init = function () {
        return {
            cacheService: this.initCached(),
            gatewayProxy: this.initGatewayProxied(),
            operationContextService: this.initOperationContextService()
        };
    };
    return CoreAnnotationsHelper;
}());
var coreAnnotationsHelper = new CoreAnnotationsHelper();


/***/ }),

/***/ "./smartedit-build/test/unit/domHelper.ts":
/*!************************************************!*\
  !*** ./smartedit-build/test/unit/domHelper.ts ***!
  \************************************************/
/*! exports provided: domHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "domHelper", function() { return domHelper; });
var DomHelper = /** @class */ (function () {
    function DomHelper() {
    }
    DomHelper.prototype.element = function (name, mockedMethodsOfJQueryWrapper) {
        name = name || "element_" + Math.random();
        var prototype = jasmine.createSpyObj(name, ['dispatchEvent', 'getBoundingClientRect']);
        /*
         * trick for lodash to be able to consider as an Element:
         * - not be a plain object (achieved by beign born off a constructor)
         * - have nodeType 1
         */
        var Clazz = /** @class */ (function () {
            function Clazz() {
            }
            return Clazz;
        }());
        Clazz.prototype = prototype;
        var mock = new Clazz();
        mock.nodeType = 1;
        mock.mockedMethodsOfJQueryWrapper = mockedMethodsOfJQueryWrapper;
        return mock;
    };
    DomHelper.prototype.customEvent = function (name) {
        name = name || "CustomEvent_" + Math.random();
        return jasmine.createSpyObj(name, ['initCustomEvent']);
        ;
    };
    DomHelper.prototype.event = function (name) {
        name = name || "JQueryEvent" + Math.random();
        return jasmine.createSpyObj(name, ['preventDefault', 'stopPropagation']);
    };
    DomHelper.prototype.$document = function () {
        var $document = {
            getDocument: function () {
                return this[0];
            }
        };
        var doc = jasmine.createSpyObj("document", ['createEvent']);
        doc.mockScrollingElement = function (scrollingElement) {
            this.scrollingElement = scrollingElement;
        };
        $document[0] = doc;
        return $document;
    };
    return DomHelper;
}());
var domHelper = new DomHelper();


/***/ }),

/***/ "./smartedit-build/test/unit/index.ts":
/*!********************************************!*\
  !*** ./smartedit-build/test/unit/index.ts ***!
  \********************************************/
/*! exports provided: coreAnnotationsHelper, domHelper, jQueryHelper, PromiseType, promiseHelper, LogHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _domHelper__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./domHelper */ "./smartedit-build/test/unit/domHelper.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "domHelper", function() { return _domHelper__WEBPACK_IMPORTED_MODULE_0__["domHelper"]; });

/* harmony import */ var _jQueryHelper__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./jQueryHelper */ "./smartedit-build/test/unit/jQueryHelper.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "jQueryHelper", function() { return _jQueryHelper__WEBPACK_IMPORTED_MODULE_1__["jQueryHelper"]; });

/* harmony import */ var _promiseHelper__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./promiseHelper */ "./smartedit-build/test/unit/promiseHelper.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PromiseType", function() { return _promiseHelper__WEBPACK_IMPORTED_MODULE_2__["PromiseType"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "promiseHelper", function() { return _promiseHelper__WEBPACK_IMPORTED_MODULE_2__["promiseHelper"]; });

/* harmony import */ var _logHelper__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./logHelper */ "./smartedit-build/test/unit/logHelper.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LogHelper", function() { return _logHelper__WEBPACK_IMPORTED_MODULE_3__["LogHelper"]; });

/* harmony import */ var _coreAnnotationsHelper__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./coreAnnotationsHelper */ "./smartedit-build/test/unit/coreAnnotationsHelper.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "coreAnnotationsHelper", function() { return _coreAnnotationsHelper__WEBPACK_IMPORTED_MODULE_4__["coreAnnotationsHelper"]; });

/* harmony import */ var _setupUnitTestEnv__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./setupUnitTestEnv */ "./smartedit-build/test/unit/setupUnitTestEnv.ts");
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








/***/ }),

/***/ "./smartedit-build/test/unit/jQueryHelper.ts":
/*!***************************************************!*\
  !*** ./smartedit-build/test/unit/jQueryHelper.ts ***!
  \***************************************************/
/*! exports provided: jQueryHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "jQueryHelper", function() { return jQueryHelper; });
/* harmony import */ var testhelpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! testhelpers */ "./smartedit-build/test/unit/index.ts");

var lodash = window.smarteditLodash;
var JQueryHelper = /** @class */ (function () {
    function JQueryHelper() {
        this.IDEMPOTENT_METHODS = ['show', 'hide', 'on', 'off'];
        this.DEFAULT_MOCKED_JQUERY_FUNCTIONS = ['get', 'data', 'css', 'width', 'height', 'offset'];
    }
    JQueryHelper.prototype.jQuery = function (selectorTransform) {
        var _this = this;
        var jq = window.$ || window.smarteditJQuery;
        var jqSpy = jasmine.createSpy("jQueryMock", jq);
        jqSpy.and.callFake(function (element) {
            if (typeof element === 'string') {
                if (selectorTransform) {
                    return selectorTransform(element);
                }
                else {
                    return _this.wrap("jqSpyForElement", testhelpers__WEBPACK_IMPORTED_MODULE_0__["domHelper"].element("Element Mock for selector " + element));
                }
            }
            else {
                return _this.wrap("jqSpyForElement", element);
            }
        });
        return jqSpy;
    };
    JQueryHelper.prototype.wrap = function (name) {
        var elementsArray = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            elementsArray[_i - 1] = arguments[_i];
        }
        var filteredElementsArray = elementsArray.filter(function (element) { return !!element; });
        var elementWithMocks = filteredElementsArray.find(function (element) { return !!element.mockedMethodsOfJQueryWrapper; });
        var mockedMethodsOfJQueryWrapperNames = !!elementWithMocks ? lodash.uniq(lodash.cloneDeep(this.DEFAULT_MOCKED_JQUERY_FUNCTIONS).concat(Object.keys(elementWithMocks.mockedMethodsOfJQueryWrapper))) : this.DEFAULT_MOCKED_JQUERY_FUNCTIONS;
        var elementsWrapper = jasmine.createSpyObj(name, this.IDEMPOTENT_METHODS.concat(mockedMethodsOfJQueryWrapperNames));
        filteredElementsArray.forEach(function (element, index) {
            var wrapper = elementsWrapper;
            wrapper[index] = element;
        });
        elementsWrapper.length = filteredElementsArray.length;
        this.DEFAULT_MOCKED_JQUERY_FUNCTIONS.forEach(function (methodName) {
            elementsWrapper[methodName].and.returnValue({});
        });
        if (elementWithMocks) {
            lodash.forEach(elementWithMocks.mockedMethodsOfJQueryWrapper, function (value, methodName) {
                elementsWrapper[methodName].and.returnValue(value);
            });
        }
        this.IDEMPOTENT_METHODS.forEach(function (methodName) {
            elementsWrapper[methodName].and.returnValue(elementsWrapper);
        });
        return elementsWrapper;
    };
    return JQueryHelper;
}());
var jQueryHelper = new JQueryHelper();


/***/ }),

/***/ "./smartedit-build/test/unit/logHelper.ts":
/*!************************************************!*\
  !*** ./smartedit-build/test/unit/logHelper.ts ***!
  \************************************************/
/*! exports provided: LogHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LogHelper", function() { return LogHelper; });
var LogHelper = /** @class */ (function () {
    function LogHelper() {
        this.debug = function () {
            var _this = this;
            var f = function () {
                var args = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    args[_i] = arguments[_i];
                }
                return console.debug.call(_this, args);
            };
            f.logs = [];
            return f;
        }();
        this.info = function () {
            var _this = this;
            var f = function () {
                var args = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    args[_i] = arguments[_i];
                }
                return console.info.call(_this, args);
            };
            f.logs = [];
            return f;
        }();
        this.log = function () {
            var _this = this;
            var f = function () {
                var args = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    args[_i] = arguments[_i];
                }
                return console.log.call(_this, args);
            };
            f.logs = [];
            return f;
        }();
        this.error = function () {
            var _this = this;
            var f = function () {
                var args = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    args[_i] = arguments[_i];
                }
                return console.error.call(_this, args);
            };
            f.logs = [];
            return f;
        }();
        this.warn = function () {
            var _this = this;
            var f = function () {
                var args = [];
                for (var _i = 0; _i < arguments.length; _i++) {
                    args[_i] = arguments[_i];
                }
                return console.warn.call(_this, args);
            };
            f.logs = [];
            return f;
        }();
        spyOn(this, 'debug');
        spyOn(this, 'info');
        spyOn(this, 'log');
        spyOn(this, 'error');
        spyOn(this, 'warn');
    }
    // from angular-mocks
    LogHelper.prototype.assertEmpty = function () {
        throw new Error("LogHelper.assertEmpty() was not implemented");
    };
    // from angular-mocks
    LogHelper.prototype.reset = function () {
        throw new Error("LogHelper.reset() was not implemented");
    };
    return LogHelper;
}());



/***/ }),

/***/ "./smartedit-build/test/unit/promiseHelper.ts":
/*!****************************************************!*\
  !*** ./smartedit-build/test/unit/promiseHelper.ts ***!
  \****************************************************/
/*! exports provided: PromiseType, promiseHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PromiseType", function() { return PromiseType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "promiseHelper", function() { return promiseHelper; });
/* harmony import */ var jasmine__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jasmine */ "jasmine");
/* harmony import */ var jasmine__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(jasmine__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_1__);
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


var PromiseType;
(function (PromiseType) {
    PromiseType[PromiseType["RESOLVES"] = 0] = "RESOLVES";
    PromiseType[PromiseType["REJECTS"] = 1] = "REJECTS";
})(PromiseType || (PromiseType = {}));
/**
 * @ngdoc service
 * @name testHelpers.service:PromiseHelper
 * @description
 * Helper to easily make use of immediately resolving/rejecting promises in unit tests
 */
var PromiseHelper = /** @class */ (function () {
    function PromiseHelper() {
    }
    /**
     * @ngdoc method
     * @name testHelpers.service:PromiseHelper#buildPromise
     * @methodOf testHelpers.service:PromiseHelper
     * @description
     * Builds an immediately resolving or rejecting promise
     * @param {string} name the name of the promise
     * @param {PromiseType} promiseType enum of values RESOLVES and REJECTS to indicate the behaviour of the promise
     * @param {object} value  the object to which the promise resolves or rejects, it will be passed as argument to its resolveFunction or rejectFunction passed as arguments of .then method
     */
    PromiseHelper.prototype.buildPromise = function (name, promiseType, value) {
        var _this = this;
        if (promiseType === void 0) { promiseType = PromiseType.RESOLVES; }
        var promise = jasmine.createSpyObj(name, ['then', 'finally', 'catch']);
        promise.promiseType = promiseType;
        promise.value = value;
        promise.name = name;
        promise.then.and.callFake(function (resolveFunction, rejectFunction) {
            var newValue = null;
            if (promise.promiseType === PromiseType.RESOLVES) {
                if (resolveFunction) {
                    newValue = resolveFunction(promise.value);
                }
                else {
                    newValue = promise.value;
                }
                if (newValue && newValue.then) {
                    return newValue;
                }
                else {
                    return _this.buildPromise(name + "_chained", PromiseType.RESOLVES, newValue);
                }
            }
            else {
                if (rejectFunction) {
                    newValue = rejectFunction(promise.value);
                }
                else {
                    newValue = promise.value;
                }
                if (newValue && newValue.then) {
                    return newValue;
                }
                else {
                    return _this.buildPromise(name + "_chained", PromiseType.REJECTS, newValue);
                }
            }
        });
        promise.finally.and.callFake(function (finallyFunction) {
            var newValue = finallyFunction(promise.value);
            return _this.buildPromise(name + "_chained", promise.promiseType, newValue);
        });
        promise.catch.and.callFake(function (rejectFunction) {
            if (promise.promiseType === PromiseType.REJECTS) {
                var newValue = rejectFunction(promise.value);
                return _this.buildPromise(name + "_chained", PromiseType.REJECTS, newValue);
            }
            return promise;
        });
        return promise;
    };
    /**
     * @ngdoc method
     * @name testHelpers.service:PromiseHelper#$q
     * @methodOf testHelpers.service:PromiseHelper
     * @description
     * Builds an immediately resolving or rejecting mock of angular $q
     */
    PromiseHelper.prototype.$q = function () {
        var _this = this;
        var $q = jasmine.createSpyObj('$q', ['all', 'defer', 'when', 'resolve', 'reject']);
        $q.all.and.callFake(function (promises) {
            var collector = [];
            promises
                .filter(function (promise) { return promise.promiseType === PromiseType.RESOLVES; })
                .forEach(function (promise) {
                promise.then(function (response) {
                    collector.push(response);
                });
            });
            var oneRejected = promises.find(function (promise) { return promise.promiseType === PromiseType.REJECTS; });
            if (!oneRejected) {
                return _this.buildPromise("arrayPromise", PromiseType.RESOLVES, collector);
            }
            else {
                return _this.buildPromise("arrayPromise", PromiseType.REJECTS, oneRejected.value);
            }
        });
        $q.when.and.callFake(function (value) {
            if (value && value.then) {
                return value;
            }
            else {
                return _this.buildPromise("whenPromise", PromiseType.RESOLVES, value);
            }
        });
        $q.defer.and.callFake(function () {
            var deferred = {
                promise: _this.buildPromise("deferredPromise", PromiseType.REJECTS),
                resolve: function (value) {
                    this.promise.promiseType = PromiseType.RESOLVES;
                    this.promise.value = value;
                },
                reject: function (value) {
                    this.promise.promiseType = PromiseType.REJECTS;
                    this.promise.value = value;
                },
                notify: function (state) { angular__WEBPACK_IMPORTED_MODULE_1__["noop"](); }
            };
            return deferred;
        });
        $q.resolve.and.callFake(function (value) {
            if (value && value.then) {
                return value;
            }
            else {
                return _this.buildPromise("resolvePromise", PromiseType.RESOLVES, value);
            }
        });
        $q.reject.and.callFake(function (value) {
            if (value && value.then) {
                return value;
            }
            else {
                return _this.buildPromise("rejectPromise", PromiseType.REJECTS, value);
            }
        });
        return $q;
    };
    return PromiseHelper;
}());
var promiseHelper = new PromiseHelper();


/***/ }),

/***/ "./smartedit-build/test/unit/setupUnitTestEnv.ts":
/*!*******************************************************!*\
  !*** ./smartedit-build/test/unit/setupUnitTestEnv.ts ***!
  \*******************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! angular */ "angular");
/* harmony import */ var angular__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(angular__WEBPACK_IMPORTED_MODULE_0__);
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
// necessary for registration of decorators and hence required by downstream teams that flag smarteditcommons as external
// without smarteditcommons flagged as external, import of functions, like decorators, in downstream extensions fail

beforeEach(angular__WEBPACK_IMPORTED_MODULE_0__["mock"].module(function ($provide) {
    $provide.value('$log', console);
}));


/***/ }),

/***/ "./smartedit-build/test/unit/sharedSmarteditForTests.ts":
/*!**************************************************************!*\
  !*** ./smartedit-build/test/unit/sharedSmarteditForTests.ts ***!
  \**************************************************************/
/*! exports provided: CachedAnnotationFactory, CacheConfigAnnotationFactory, InvalidateCacheAnnotationFactory, GatewayProxiedAnnotationFactory, OperationContextAnnotationFactory, annotationService, SeInjectable, SeComponent, SeModule, FunctionsModule, ConfigModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var smarteditcommons_services_cache_cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! smarteditcommons/services/cache/cachedAnnotation */ "./jsTarget/web/app/common/services/cache/cachedAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CachedAnnotationFactory", function() { return smarteditcommons_services_cache_cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["CachedAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CacheConfigAnnotationFactory", function() { return smarteditcommons_services_cache_cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["CacheConfigAnnotationFactory"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "InvalidateCacheAnnotationFactory", function() { return smarteditcommons_services_cache_cachedAnnotation__WEBPACK_IMPORTED_MODULE_0__["InvalidateCacheAnnotationFactory"]; });

/* harmony import */ var smarteditcommons_services_gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! smarteditcommons/services/gatewayProxiedAnnotation */ "./jsTarget/web/app/common/services/gatewayProxiedAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "GatewayProxiedAnnotationFactory", function() { return smarteditcommons_services_gatewayProxiedAnnotation__WEBPACK_IMPORTED_MODULE_1__["GatewayProxiedAnnotationFactory"]; });

/* harmony import */ var smarteditcommons_services_httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! smarteditcommons/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation */ "./jsTarget/web/app/common/services/httpErrorInterceptor/default/retryInterceptor/operationContextAnnotation.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "OperationContextAnnotationFactory", function() { return smarteditcommons_services_httpErrorInterceptor_default_retryInterceptor_operationContextAnnotation__WEBPACK_IMPORTED_MODULE_2__["OperationContextAnnotationFactory"]; });

/* harmony import */ var smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! smarteditcommons/services/annotationService */ "./jsTarget/web/app/common/services/annotationService.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "annotationService", function() { return smarteditcommons_services_annotationService__WEBPACK_IMPORTED_MODULE_3__["annotationService"]; });

/* harmony import */ var smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! smarteditcommons/services/dependencyInjection/di */ "./jsTarget/web/app/common/services/dependencyInjection/di.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeInjectable", function() { return smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_4__["SeInjectable"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeComponent", function() { return smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_4__["SeComponent"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SeModule", function() { return smarteditcommons_services_dependencyInjection_di__WEBPACK_IMPORTED_MODULE_4__["SeModule"]; });

/* harmony import */ var smarteditcommons_utils__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! smarteditcommons/utils */ "./jsTarget/web/app/common/utils/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FunctionsModule", function() { return smarteditcommons_utils__WEBPACK_IMPORTED_MODULE_5__["FunctionsModule"]; });

/* harmony import */ var smarteditcommons_services_ConfigModule__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! smarteditcommons/services/ConfigModule */ "./jsTarget/web/app/common/services/ConfigModule.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ConfigModule", function() { return smarteditcommons_services_ConfigModule__WEBPACK_IMPORTED_MODULE_6__["ConfigModule"]; });

/* harmony import */ var testhelpers__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! testhelpers */ "./smartedit-build/test/unit/index.ts");
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








testhelpers__WEBPACK_IMPORTED_MODULE_7__["coreAnnotationsHelper"].init();


/***/ }),

/***/ "angular":
/*!**************************!*\
  !*** external "angular" ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = angular;

/***/ }),

/***/ "crypto-js":
/*!***************************!*\
  !*** external "CryptoJS" ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = CryptoJS;

/***/ }),

/***/ "jasmine":
/*!**************************!*\
  !*** external "jasmine" ***!
  \**************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = jasmine;

/***/ })

/******/ });
//# sourceMappingURL=sharedSmarteditForTests.js.map