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
angular.module('dragAndDropAndRemoveMocks', ['ngMockE2EAsync', 'ngMockE2E', 'smarteditServicesModule'])
    /*
     * is mirrored with another componentMocksUpdater in rerenderMocks.js
     */
    .service('componentMocksUpdater', function(gatewayProxy) {

        this.gatewayId = 'componentMocksUpdater';

        this.update = function() {};

        gatewayProxy.initForService(this);
    })
    .run(function($q, $timeout, $httpBackend, $window, lodash, SMARTEDIT_IFRAME_ID, componentMocksUpdater) {

        this.idCounter = 10;
        this.slots = [{
            id: 'topHeaderSlot',
            components: ['component1', 'component2', 'component3']
        }, {
            id: 'otherSlot',
            components: []
        }, {
            id: 'bottomHeaderSlot',
            components: ['component4', 'component10'].concat(Array.apply(null, {
                length: 20
            }).map(function(element, index) {
                return 'component-0' + (index + 1);
            }))
        }, {
            id: 'searchBoxSlot',
            components: ['component8']
        }, {
            id: 'footerSlot',
            components: ['component5']
        }, {
            id: 'staticDummySlot',
            components: ['staticDummyComponent']
        }, {
            id: 'emptyDummySlot',
            components: []
        }];


        this.types = {
            'component1': 'componentType1',
            'component2': 'componentType2',
            'component3': 'componentType3',
            'component4': 'componentType4',
            'component5': 'componentType5',
            'component10': 'componentType10',
            'staticDummyComponent': 'componentType1'
        };

        componentMocksUpdater.update(this.slots, this.types);

        $httpBackend.whenAsync('PUT', /cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents\/pages\/homepage\/contentslots\/.*/).respond(function(method, url, data, headers) {
            var deferred = $q.defer();
            var parsedData = JSON.parse(data);
            this.moveComponentToSlot(parsedData.currentSlotId, parsedData.componentId, parsedData.slotId, parsedData.position).then(function() {
                deferred.resolve([200, data]);
            });
            return deferred.promise;
        }.bind(this));

        //cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/pagescontentslotscomponents
        $httpBackend.whenAsync('POST', /cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents/).respond(function(method, url, data, headers) {
            var deferred = $q.defer();
            var parsedData = JSON.parse(data);
            // this.addComponentToSlot(parsedData.slotId, parsedData.componentId, parsedData.position);
            this.addComponentToSlot(parsedData.slotId, parsedData.componentId, parsedData.position).then(function() {
                deferred.resolve([200, data]);
            });
            return deferred.promise;
        }.bind(this));

        //Delete component
        $httpBackend.whenAsync('DELETE', /cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pagescontentslotscomponents\/contentslots\/.*\/components/).respond(function(method, url, data, headers) {
            var deferred = $q.defer();
            var slotId = /cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pagescontentslotscomponents\/contentslots\/(.*)/.exec(url)[1].split('/')[0];
            var componentId = /cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pagescontentslotscomponents\/contentslots\/.*\/components\/(.*)/.exec(url)[1];

            this.slots.forEach(function(slot) {
                if (slot.id === slotId) {
                    slot.components.splice(slot.components.indexOf(componentId), 1);
                }
            });
            this.removeComponent(slotId, componentId).then(function() {
                deferred.resolve([200, {}]);
            });

            return deferred.promise;
        }.bind(this));

        var component1_data = {
            'creationtime': '2016-08-17T16:05:47+0000',
            'modifiedtime': '2016-08-17T16:05:47+0000',
            'name': 'Component 1',
            'pk': '1',
            'typeCode': 'CMSParagraphComponent',
            'uid': 'component1',
            'visible': true
        };

        $httpBackend.whenGET('/cmswebservices/v1/sites/apparel-uk/cmsitems/component1').respond(component1_data);

        this.removeComponent = function(slotId, componentId) {
            var slot = this.getSlot(slotId);
            var componentIndex = slot.components.indexOf(componentId);
            if (componentIndex !== -1) {
                slot.components.splice(componentIndex, 1);
            }
            return componentMocksUpdater.update({
                updates: {
                    slots: this.slots,
                    types: this.types
                },
                operation: {
                    method: "queueRemoveComponent",
                    args: [componentId, slotId]
                }
            });
        };

        this.addComponentToSlot = function(slotId, componentId, position) {
            var slot = this.getSlot(slotId);
            slot.components.splice(position, 0, componentId);

            return componentMocksUpdater.update({
                updates: {
                    slots: this.slots,
                    types: this.types
                },
                operation: {
                    method: "queueAddComponent",
                    args: [componentId, slotId, position]
                }
            });
        };

        this.moveComponentToSlot = function(originalSlot, componentId, targetSlot, position) {
            return $q.all([this.removeComponent(originalSlot, componentId), this.addComponentToSlot(targetSlot, componentId, position)]);
        };

        this.getSlot = function(slotId) {
            var resultSlot = null;
            lodash.each(this.slots, function(slot) {
                if (slot.id === slotId) {
                    resultSlot = slot;
                    return false;
                }
            }.bind(this));

            return resultSlot;
        };
    });
try {
    angular.module('smarteditloader').requires.push('dragAndDropAndRemoveMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('dragAndDropAndRemoveMocks');
} catch (e) {}
