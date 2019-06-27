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
/* jshint unused:false, undef:false */
angular.module('customViewModule', ['editorModalServiceModule', 'backendMocksUtilsModule'])
    .constant('PATH_TO_CUSTOM_VIEW', 'genericEditor/carouselComponent/customView.html')
    .controller('customViewController', function(editorModalService, backendMocksUtils) {

        var carouselComponent = null;

        var saveOrUpdateComponent = function(method, url, data, headers) {
            var payload = JSON.parse(data);
            payload.uid = 'carouselComponent_1';
            carouselComponent = payload;
            carouselComponent.onlyOneRestrictionMustApply = false;
            carouselComponent.restrictions = [];

            return [201];
        };

        backendMocksUtils.getBackendMock('componentPUTMock').respond(saveOrUpdateComponent);
        backendMocksUtils.getBackendMock('componentPOSTMock').respond(saveOrUpdateComponent);


        var componentGETMock = backendMocksUtils.getBackendMock('componentGETMock');
        componentGETMock.respond(function(method, url, data, headers) {
            var uid = /items\/(.*)/.exec(url)[1];

            if (uid !== carouselComponent.uid) {
                return [400];
            } else {
                return [200, carouselComponent];
            }
        });

        // Information about the component in the generic editor. 
        this.componentConfiguration = {
            componentType: "ProductCarouselComponent",
            componentId: null
        };

        this.openEditor = function() {
            var componentId = (carouselComponent) ? carouselComponent.uid : null;
            var componentAttributes = {
                smarteditComponentType: this.componentConfiguration.componentType,
                smarteditComponentId: componentId,
                smarteditComponentUuid: componentId,
                catalogVersionUuid: 'somecatalogId/someCatalogVersion'
            };
            editorModalService.open(componentAttributes);
        };
    });

try {
    angular.module('smarteditcontainer').requires.push('customViewModule');
} catch (e) {}
