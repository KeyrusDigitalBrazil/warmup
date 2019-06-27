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
angular.module('decoratorServiceModule', ['functionsModule'])
    .factory('DecoratorServiceClass', function($q, uniqueArray, regExpFactory) {

        // Constants

        var DecoratorServiceClass = function() {
            this._activeDecorators = {};
            this.componentDecoratorsMap = {};
        };

        /**
         * @ngdoc method
         * @name decoratorServiceModule.service:decoratorService#addMappings
         * @methodOf decoratorServiceModule.service:decoratorService
         * @description
         * This method enables a list of decorators for a group of component types.
         * The list to be {@link decoratorServiceModule.service:decoratorService#methods_enable enable} is identified by a matching pattern.
         * The list is enabled when a perspective or referenced perspective that it is bound to is activated/enabled.
         * @param {Object} map A key-map value; the key is the matching pattern and the value is an array of decorator keys. The key can be an exact type, an ant-like wild card, or a full regular expression:
         * <pre>
         * decoratorService.addMappings({
            '*Suffix': ['decorator1', 'decorator2'],
            '.*Suffix': ['decorator2', 'decorator3'],
            'MyExactType': ['decorator3', 'decorator4'],
            '^((?!Middle).)*$': ['decorator4', 'decorator5']
        	});
         * </pre>
         */
        DecoratorServiceClass.prototype.addMappings = function(map) {

            for (var regexpKey in map) {
                if (map.hasOwnProperty(regexpKey)) {
                    var decoratorsArray = map[regexpKey];
                    this.componentDecoratorsMap[regexpKey] = uniqueArray((this.componentDecoratorsMap[regexpKey] || []), decoratorsArray);
                }
            }

        };

        /**
         * @ngdoc method
         * @name decoratorServiceModule.service:decoratorService#enable
         * @methodOf decoratorServiceModule.service:decoratorService
         * @description
         * Enables a decorator
         * 
         * @param {String} decoratorKey The key that uniquely identifies the decorator.
         * @param {Function} displayCondition Returns a promise that will resolve to a boolean that determines whether the decorator will be displayed.
         */
        DecoratorServiceClass.prototype.enable = function(decoratorKey, displayCondition) {

            if (!(decoratorKey in this._activeDecorators)) {
                this._activeDecorators[decoratorKey] = {
                    'displayCondition': displayCondition
                };
            }
        };
        /**
         * @ngdoc method
         * @name decoratorServiceModule.service:decoratorService#disable
         * @methodOf decoratorServiceModule.service:decoratorService
         * @description
         * Disables a decorator
         * 
         * @param {String} decoratorKey the decorator key
         */
        DecoratorServiceClass.prototype.disable = function(decoratorKey) {
            if (this._activeDecorators[decoratorKey]) {
                delete this._activeDecorators[decoratorKey];
            }
        };

        /**
         * @ngdoc method
         * @name decoratorServiceModule.service:decoratorService#getDecoratorsForComponent
         * @methodOf decoratorServiceModule.service:decoratorService
         * @description
         * This method retrieves a list of decorator keys that is eligible for the specified component type.
         * The list retrieved depends on which perspective is active.
         *
         * This method uses the list of decorators enabled by the {@link decoratorServiceModule.service:decoratorService#methods_addMappings addMappings} method.
         *
         * @param {String} componentType The type of the component to be decorated.
         * @param {String} componentId The id of the component to be decorated.
         * @returns {Promise} A promise that resolves to a list of decorator keys.
         *
         */
        DecoratorServiceClass.prototype.getDecoratorsForComponent = function(componentType, componentId) {
            return this._getDecorators(componentType, componentId);
        };

        DecoratorServiceClass.prototype._getDecorators = function(componentType, componentId) {
            var decoratorArray = [];
            if (this.componentDecoratorsMap) {
                for (var regexpKey in this.componentDecoratorsMap) {
                    if (regExpFactory(regexpKey).test(componentType)) {
                        decoratorArray = uniqueArray(decoratorArray, this.componentDecoratorsMap[regexpKey]);
                    }
                }
            }

            var promisesToResolve = [];
            var displayedDecorators = [];
            decoratorArray.forEach(function(dec) {
                var activeDecorator = this._activeDecorators[dec];
                if (activeDecorator && activeDecorator.displayCondition) {
                    if (typeof activeDecorator.displayCondition !== 'function') {
                        throw new Error("The active decorator's displayCondition property must be a function and must return a boolean");
                    }

                    var deferred = $q.defer();
                    activeDecorator.displayCondition(componentType, componentId).then(function(display) {
                        if (display) {
                            deferred.resolve(dec);
                        } else {
                            deferred.resolve(null);
                        }
                    });

                    promisesToResolve.push(deferred.promise);
                } else if (activeDecorator) {
                    displayedDecorators.push(dec);
                }
            }.bind(this));

            return $q.all(promisesToResolve).then(function(decoratorsEnabled) {
                return displayedDecorators.concat(decoratorsEnabled.filter(function(dec) {
                    return dec;
                }));
            });
        };

        return DecoratorServiceClass;
    })
    /**
     * @ngdoc service
     * @name decoratorServiceModule.service:decoratorService
     *
     * @description
     * This service enables and disables decorators. It also maps decorators to SmartEdit component types–regardless if they are enabled or disabled.
     * 
     */
    .factory('decoratorService', function(DecoratorServiceClass) {
        return new DecoratorServiceClass();
    });
