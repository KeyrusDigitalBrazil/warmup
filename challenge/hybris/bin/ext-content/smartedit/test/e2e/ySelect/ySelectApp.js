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
angular.module('ySelectApp', ['smarteditServicesModule', 'ySelectModule', 'templateCacheDecoratorModule', 'seConstantsModule'])
    .run(function($templateCache) {
        $templateCache.put('test.html', "<div><b>Label:</b> {{item.label}} <b>Price:</b> {{item.price}}</div>");
    })
    .controller('defaultController', function($q, lodash, VALIDATION_MESSAGE_TYPES) {
        // Source data
        var languagesV1 = [{
            id: 'en',
            label: 'English'
        }, {
            id: 'de',
            label: 'German'
        }, {
            id: 'ru',
            label: 'Russian'
        }];
        var languagesV2 = [{
            id: 'fr',
            label: 'French'
        }, {
            id: 'en',
            label: 'English'
        }, {
            id: 'es',
            label: 'Spanish'
        }, {
            id: 'it',
            label: 'Italian'
        }];

        var productsV1 = [{
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

        var productsV2 = [{
            id: 'product1',
            label: 'Test Product 1',
            image: '',
            price: 123
        }, {
            id: 'product3',
            label: 'Test Product 3',
            image: '',
            price: 789
        }, {
            id: 'product4',
            label: 'Test Product 4',
            image: '',
            price: 234
        }, {
            id: 'product5',
            label: 'Test Product 5',
            image: '',
            price: 234
        }];

        // First Language dropdown
        this.example1 = {
            id: 'example1',
            model: "en",
            forceReset: true,
            fetchStrategy: {
                fetchAll: _fetchAll.bind(null, "example1")
            },
            source: languagesV1,
            setSelectorApi: function(api) {
                this.api = api;
            }
        };

        // First multi-select dropdown
        this.exampleMulti1 = {
            id: 'exampleMulti1',
            model: ["product2", "product3"],
            fetchStrategy: {
                fetchAll: _fetchAllClone.bind(null, "exampleMulti1")
            },
            forceReset: true,
            source: productsV1
        };

        // Second multi-select dropdown
        this.exampleMulti2 = {
            id: 'exampleMulti2',
            model: ["product2"],
            template: "test.html",
            forceReset: true,
            source: productsV1,
            fetchStrategy: {
                fetchPage: _fetchPage.bind(null, "exampleMulti2"),
                fetchEntity: _fetchEntity.bind(null, "exampleMulti2")
            },
            setSelectorApi: function(api) {
                this.api = api;
            }
        };

        this.changeSource = function(componentName) {
            var component = this[componentName];
            component.reset();

            if (componentName === "exampleMulti1") {
                sources[componentName] = (sources[componentName] === productsV1) ? productsV2 : productsV1;
            } else {
                sources[componentName] = (sources[componentName] === languagesV1) ? languagesV2 : languagesV1;
            }
        };

        this.changeValidationState = function(id, state) {
            if (state === 'error') {
                this[id].api.setValidationState(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
            } else if (state === 'warning') {
                this[id].api.setValidationState(VALIDATION_MESSAGE_TYPES.WARNING);
            } else {
                this[id].api.resetValidationState();
            }
        };

        var sources = {
            "exampleMulti1": productsV1,
            "exampleMulti2": productsV1,
            "example1": languagesV1
        };

        // Helper functions
        function _fetchAll(ySelect) {
            console.log("Calling fetchAll", ySelect);
            var list = sources[ySelect];
            return $q.when(list);
        }

        // Helper function for multi selector. See ySelectTest.js line 144.
        function _fetchAllClone(ySelect) {
            console.log("Calling fetchAll Clone", ySelect);
            var list = sources[ySelect];
            return $q.when(lodash.clone(list));
        }

        function _fetchPage(ySelect) {
            var list = sources[ySelect];
            var result = {
                pagination: {
                    totalCount: list.length
                },
                results: list
            };

            return $q.when(result);
        }

        function _fetchEntity(ySelect, itemId) {
            var list = sources[ySelect];
            var result = list.filter(function(elem) {
                return elem.id === itemId;
            })[0];

            return $q.when(lodash.cloneDeep(result));
        }

    });
