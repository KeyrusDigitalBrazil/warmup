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
describe('ySelectModule - ', function() {

    var VALIDATION_MESSAGE_TYPES,
        $templateCache,
        controller,
        $template,
        $element,
        $rootScope,
        $compile,
        lodash,
        $scope,
        spy,
        $q;

    var languages = [{
        id: 'en',
        label: 'English'
    }, {
        id: 'de',
        label: 'German'
    }, {
        id: 'ru',
        label: 'Russian'
    }];

    var products = [{
        id: 'product1',
        label: 'Test Product 1',
        image: '',
        price: 123
    }, {
        id: 'product2',
        label: 'Test Product 2',
        image: '',
        price: 234
    }, {
        id: 'product3',
        label: 'Test Product 3',
        image: '',
        price: 567
    }];

    var testScopeSingle = {
        id: 'example',
        model: "en",
        reset: true,
        source: languages,
        fetchStrategy: {
            fetchAll: function() {
                return $q.when(lodash.clone(languages));
            }
        },
        getSelectorApi: function(api) {
            this._api = api;
        }
    };

    var testScopeMulti = {
        id: 'example',
        model: ["product2"],
        reset: true,
        source: products,
        multiSelect: true,
        fetchStrategy: {
            fetchAll: function() {
                return $q.when(lodash.clone(products));
            }
        },
        getSelectorApi: function(api) {
            this._api = api;
        }
    };

    function initialize(templateScope, merge) {
        $scope = $rootScope.$new(true);
        $scope.selector = lodash.merge(lodash.clone(templateScope), merge);

        spy = jasmine.createSpyObj('spy', ['onChange']);
        $scope.spy = spy;

        $template = '<y-select id="{{ selector.id }}" ' +
            'ng-model="selector.model" ' +
            'reset="selector.reset" ' +
            'keep-model-on-reset="!selector.forceReset" ' +
            'fetch-strategy="selector.fetchStrategy" ' +
            'multi-select="selector.multiSelect" ' +
            'get-api="selector.getSelectorApi($api)" ' +
            'item-template="selector.itemTemplate" ' +
            'results-header-template-url="selector.resultsHeaderTemplateUrl" ' +
            'results-header-template="selector.resultsHeaderTemplate" ' +
            'results-header-label="selector.resultsHeaderLabel" ' +
            'search-enabled="selector.searchEnabled" ' +
            'is-read-only="selector.isReadOnly" ' +
            'placeholder="selector.placeholder" ' +
            'disable-choice-fn="selector.disableChoiceFn" ' +
            'reset="selector.reset" ' +
            'on-change="spy.onChange" ' +
            'controls="selector.controls" ></y-select>';

        $element = $compile($template)($scope);
        $rootScope.$digest();

        controller = $element.controller("ySelect");
    }

    beforeEach(angular.mock.module('yLoDashModule'));
    beforeEach(angular.mock.module('seConstantsModule'));
    beforeEach(angular.mock.module('coretemplates'));
    beforeEach(angular.mock.module('ui.select'));
    beforeEach(angular.mock.module('ngSanitize'));

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        $provide.value('l10nFilter', function(name) {
            return name;
        });
        var holder = jasmine.createSpyObj('holder', ['$translate']);
        $provide.value('$translate', holder.$translate);
        $provide.value('translateFilter', function(data) {
            return data;
        });
    }));

    beforeEach(angular.mock.module('ySelectModule'));

    beforeEach(inject(function(_$q_, _lodash_, _$compile_, _$rootScope_, _VALIDATION_MESSAGE_TYPES_, _$templateCache_) {
        $q = _$q_;
        lodash = _lodash_;
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $templateCache = _$templateCache_;
        VALIDATION_MESSAGE_TYPES = _VALIDATION_MESSAGE_TYPES_;
    }));

    it('should initialize with the expected id', function() {
        initialize(testScopeSingle);

        expect(controller.id).toBe('example');
    });

    it('should initialize items with the fetch all strategy and expect the items', function() {
        initialize(testScopeSingle);

        var items = controller.items.map(function(item) {
            delete item.$$hashKey;
            return item;
        });

        expect(items).toEqual(languages);
    });

    it('setting the validation state to error through the api should have the same constant VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR', function() {
        initialize(testScopeSingle, {
            getApi: function(api) {
                api.setValidationState(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
                expect(controller.validationState).toEqual(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);

                api.resetValidationState();
                expect(controller.validationState).toBeUndefined();
            }
        });
    });

    it('using a custom label template should repeat a total 4 of uses, in the preview and 3 list items for single select', function() {
        $templateCache.put('kitten.html', '<div class="custom">{{ item.label }}</div>');

        initialize(testScopeSingle, {
            itemTemplate: 'kitten.html'
        });

        var uiSelect = $element.find('#example-list').controller('uiSelect');
        uiSelect.open = true;

        $scope.$digest();

        var numberOfLabels = $element.find('.custom').length;
        expect(numberOfLabels).toBe(4);
    });

    it('using a custom template should repeat a total of 3 uses', function() {
        $templateCache.put('kitten.html', '<div class="custom">{{ item.label }}</div>');

        initialize(testScopeMulti, {
            itemTemplate: 'kitten.html'
        });

        var uiSelect = $element.find('#example-list').controller('uiSelect');
        uiSelect.open = true;

        $scope.$digest();

        var numberOfLabels = $element.find('.custom').length;
        expect(numberOfLabels).toBe(3);
    });

    it('disable the search input', function() {
        initialize(testScopeSingle, {
            searchEnabled: false
        });

        expect($element.find('.search-container').hasClass('ui-select-search-hidden')).toBe(true);
    });

    it('should display the results header label', function() {
        var header = 'ilikemexicanfood';

        initialize(testScopeSingle, {
            resultsHeaderLabel: header
        });

        expect($element.find('.y-infinite-scrolling__listbox-header').text()).toBe(header);
    });

    it('should display inline template header', function() {
        var header = '<span class="custom-header">kitten</span>';

        initialize(testScopeSingle, {
            resultsHeaderTemplate: header
        });

        expect($element.find('.custom-header').text()).toEqual('kitten');
    });

    it('should display template url header', function() {
        $templateCache.put('kitten.html', '<span class="custom-header">kitten</span>');

        initialize(testScopeSingle, {
            resultsHeaderTemplateUrl: 'kitten.html'
        });

        expect($element.find('.custom-header').text()).toEqual('kitten');
    });

    it('should be read only', function() {
        initialize(testScopeSingle, {
            isReadOnly: true
        });

        var isDisabled = $element.find('#example-selector').hasClass('select2-container-disabled');

        expect(isDisabled).toBeTruthy();
    });

    it('should contain placeholder', function() {
        initialize(testScopeSingle, {
            model: '',
            placeholder: 'kitten'
        });
        var placeholder = $element.find('.select2-choice > .select2-chosen:first-child').text().trim();
        expect(placeholder).toEqual('kitten');

        initialize(testScopeMulti, {
            model: '',
            placeholder: 'kitten'
        });

        placeholder = $element.find('.ui-select-search').attr('placeholder');
        expect(placeholder).toEqual('kitten');
    });

    // This whole test have to be refactored. It has too much knowledge about ui-select. We should be testing
    // our implementation, not someone else's implementation. Or at least, encapsulate in clear methods what could 
    // change and ensure it's clear what is/should be happening. 
    // it('should have disabled selected item for single select', function() {
    //     initialize(testScopeSingle, {
    //         disableChoiceFn: function(item) {
    //             return item.id === 'de';
    //         }
    //     });

    //     var uiSelect = $element.find('#example-list').controller('uiSelect');
    //     uiSelect.open = true;

    //     $scope.$digest();

    //     var language = $element.find('.select2-disabled').text().trim();
    //     expect(language).toEqual('German');
    // });

    it('should call changes function when there is a change inside the component', function() {
        initialize(testScopeSingle);
        expect(spy.onChange).toHaveBeenCalled();
    });

    it('should display control buttons such as magnifiers and clear.', function() {
        initialize(testScopeSingle, {
            controls: true
        });

        var icons = $element.find('.glyphicon');
        expect(icons.length).toEqual(2);
    });

});
