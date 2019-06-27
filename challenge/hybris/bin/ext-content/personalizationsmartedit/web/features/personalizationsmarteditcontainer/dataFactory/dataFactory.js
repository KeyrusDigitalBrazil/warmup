angular.module('personalizationsmarteditDataFactory', [
        'personalizationsmarteditServicesModule'
    ])
    .factory('customizationDataFactory', function(personalizationsmarteditRestService) {
        var factory = {};
        var defaultFilter = {};
        var defaultDataArrayName = "customizations";
        var defaultSuccessCallbackFunction = function() {};
        var defaultErrorCallbackFunction = function() {};

        var getCustomizations = function(filter) {
            personalizationsmarteditRestService.getCustomizations(filter).then(function(response) {
                Array.prototype.push.apply(factory.items, response[defaultDataArrayName]);
                defaultSuccessCallbackFunction(response);
            }, function(response) {
                defaultErrorCallbackFunction(response);
            });
        };

        factory.items = [];

        factory.updateData = function(params, successCallbackFunction, errorCallbackFunction) {
            params = params || {};
            defaultFilter = params.filter || defaultFilter;
            defaultDataArrayName = params.dataArrayName || defaultDataArrayName;
            if (successCallbackFunction && typeof(successCallbackFunction) === "function") {
                defaultSuccessCallbackFunction = successCallbackFunction;
            }
            if (errorCallbackFunction && typeof(errorCallbackFunction) === "function") {
                defaultErrorCallbackFunction = errorCallbackFunction;
            }

            getCustomizations(defaultFilter);
        };

        factory.refreshData = function() {
            if (angular.equals({}, defaultFilter)) {
                return;
            }
            var tempFilter = {};
            angular.copy(defaultFilter, tempFilter);
            tempFilter.currentSize = factory.items.length;
            tempFilter.currentPage = 0;
            factory.resetData();
            getCustomizations(tempFilter);
        };

        factory.resetData = function() {
            factory.items.length = 0;
        };

        factory.pushData = function(newData) {
            if (angular.isObject(newData)) {
                if (angular.isArray(newData)) {
                    Array.prototype.push.apply(factory.items, newData);
                } else {
                    factory.items.push(newData);
                }
            }
        };

        return factory;
    });
