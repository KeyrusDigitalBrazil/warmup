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
angular.module('synchronizationMocksModule', ['ngMockE2E', 'yLoDashModule'])
    .run(function($httpBackend, $window, lodash) {

        var topHeaderSlotSyncStatus = {
            itemId: 'topHeaderSlot',
            itemType: 'topHeaderSlotContentSlot',
            name: 'topHeaderSlot',
            lastSyncStatus: new Date().getTime(),
            status: 'NOT_SYNC',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            selectedDependencies: [{
                itemId: 'component1',
                itemType: 'ContentSlot',
                name: 'component1',
                lastSyncStatus: new Date().getTime(),
                status: 'NOT_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged",
                dependentItemTypesOutOfSync: [{
                    type: 'Navigation',
                    i18nKey: 'some.key.for.Navigation'
                }, {
                    type: 'Customization',
                    i18nKey: 'some.key.for.Customization'
                }]
            }, {
                itemId: 'component2',
                itemType: 'SimpleBannerComponent',
                name: 'component2',
                lastSyncStatus: new Date().getTime(),
                status: 'IN_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }],
            dependentItemTypesOutOfSync: [{
                type: 'ContentSlot',
                i18nKey: 'some.key.for.component1'
            }],
            unavailableDependencies: []
        };

        var bottomHeaderSlotSyncStatus = {
            itemId: 'bottomHeaderSlot',
            itemType: 'bottomHeaderSlotContentSlot',
            name: 'bottomHeaderSlot',
            lastSyncStatus: new Date().getTime(),
            status: 'NOT_SYNC',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            selectedDependencies: [{
                itemId: 'component3',
                itemType: 'ContentSlot',
                name: 'component3',
                lastSyncStatus: new Date().getTime(),
                status: 'IN_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }, {
                itemId: 'component4',
                itemType: 'ContentSlot',
                name: 'component4',
                lastSyncStatus: new Date().getTime(),
                status: 'NOT_SYNC',
                dependentItemTypesOutOfSync: [{
                    type: 'Component',
                    i18nKey: 'some.key.for.Component'
                }],
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }],
            dependentItemTypesOutOfSync: [{
                type: 'ContentSlot',
                i18nKey: 'some.key.for.component4'
            }],
            unavailableDependencies: []
        };

        var footerSlotSyncStatus = {
            itemId: 'footerSlot',
            itemType: 'footerSlotContentSlot',
            name: 'footerSlot',
            lastSyncStatus: new Date().getTime(),
            status: 'NOT_SYNC',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            selectedDependencies: [{
                itemId: 'component5',
                itemType: 'ContentSlot',
                name: 'component5',
                lastSyncStatus: new Date().getTime(),
                status: 'IN_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }],
            dependentItemTypesOutOfSync: [{
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }],
            unavailableDependencies: []
        };

        var otherSlotSyncStatus = {
            itemId: 'otherSlot',
            itemType: 'otherSlotContentSlot',
            name: 'otherSlot',
            lastSyncStatus: new Date().getTime(),
            status: 'IN_SYNC',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            selectedDependencies: [{
                itemId: 'component6',
                itemType: 'ContentSlot',
                name: 'component6',
                lastSyncStatus: new Date().getTime(),
                status: 'IN_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }],
            unavailableDependencies: []
        };

        var syncStatus = {
            itemId: 'homepage',
            itemType: 'AbstractPage',
            name: 'homepage',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            lastSyncStatus: new Date(2016, 10, 10, 13, 10, 0).getTime(),
            status: 'NOT_SYNC',
            dependentItemTypesOutOfSync: [{
                type: 'MetaData',
                i18nKey: 'some.key.for.MetaData'
            }, {
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }, {
                type: 'Slot',
                i18nKey: 'some.key.for.Slot'
            }, {
                type: 'Component',
                i18nKey: 'some.key.for.Component'
            }, {
                type: 'Navigation',
                i18nKey: 'some.key.for.Navigation'
            }, {
                type: 'Customization',
                i18nKey: 'some.key.for.Customization'
            }],
            selectedDependencies: [topHeaderSlotSyncStatus, bottomHeaderSlotSyncStatus, footerSlotSyncStatus],
            sharedDependencies: [otherSlotSyncStatus],
            unavailableDependencies: []
        };

        var newlyCreatedPageSyncStatus = {
            itemId: 'secondpage',
            itemType: 'AbstractPage',
            name: 'secondpage',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            status: 'NOT_SYNC',
            dependentItemTypesOutOfSync: [{
                type: 'MetaData',
                i18nKey: 'some.key.for.MetaData'
            }, {
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }, {
                type: 'Slot',
                i18nKey: 'some.key.for.Slot'
            }, {
                type: 'Component',
                i18nKey: 'some.key.for.Component'
            }, {
                type: 'Navigation',
                i18nKey: 'some.key.for.Navigation'
            }, {
                type: 'Customization',
                i18nKey: 'some.key.for.Customization'
            }],
            selectedDependencies: [topHeaderSlotSyncStatus, bottomHeaderSlotSyncStatus, footerSlotSyncStatus],
            sharedDependencies: [otherSlotSyncStatus],
            unavailableDependencies: []
        };

        var otherPageSyncStatus = {
            itemId: 'secondpage',
            itemType: 'AbstractPage',
            name: 'secondpage',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            status: 'NOT_SYNC',
            dependentItemTypesOutOfSync: [{
                type: 'MetaData',
                i18nKey: 'some.key.for.MetaData'
            }, {
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }, {
                type: 'Slot',
                i18nKey: 'some.key.for.Slot'
            }, {
                type: 'Component',
                i18nKey: 'some.key.for.Component'
            }, {
                type: 'Navigation',
                i18nKey: 'some.key.for.Navigation'
            }, {
                type: 'Customization',
                i18nKey: 'some.key.for.Customization'
            }],
            selectedDependencies: [topHeaderSlotSyncStatus, bottomHeaderSlotSyncStatus, footerSlotSyncStatus],
            sharedDependencies: [otherSlotSyncStatus],
            unavailableDependencies: [{
                itemId: 'secondPage',
                itemType: 'ContentPage',
                name: 'secondPage',
                status: 'NOT_SYNC',
                catalogVersionUuid: "apparel-ukContentCatalog/Staged"
            }]
        };

        var trashedCategoryPageSyncStatus = {
            itemId: 'trashedCategoryPage',
            itemType: 'AbstractPage',
            name: 'trashedCategoryPage',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            lastSyncStatus: new Date(2016, 10, 10, 13, 10, 0).getTime(),
            status: 'NOT_SYNC',
            dependentItemTypesOutOfSync: [{
                type: 'MetaData',
                i18nKey: 'some.key.for.MetaData'
            }, {
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }, {
                type: 'Slot',
                i18nKey: 'some.key.for.Slot'
            }, {
                type: 'Component',
                i18nKey: 'some.key.for.Component'
            }, {
                type: 'Navigation',
                i18nKey: 'some.key.for.Navigation'
            }, {
                type: 'Customization',
                i18nKey: 'some.key.for.Customization'
            }],
            selectedDependencies: [topHeaderSlotSyncStatus, bottomHeaderSlotSyncStatus, footerSlotSyncStatus],
            sharedDependencies: [otherSlotSyncStatus],
            unavailableDependencies: []
        };

        var trashedContentPageSyncStatus = {
            itemId: 'trashedContentPage',
            itemType: 'AbstractPage',
            name: 'trashedContentPage',
            catalogVersionUuid: "apparel-ukContentCatalog/Staged",
            lastSyncStatus: new Date(2016, 10, 10, 13, 10, 0).getTime(),
            status: 'IN_SYNC',
            dependentItemTypesOutOfSync: [{
                type: 'MetaData',
                i18nKey: 'some.key.for.MetaData'
            }, {
                type: 'Restrictions',
                i18nKey: 'some.key.for.Restrictions'
            }, {
                type: 'Slot',
                i18nKey: 'some.key.for.Slot'
            }, {
                type: 'Component',
                i18nKey: 'some.key.for.Component'
            }, {
                type: 'Navigation',
                i18nKey: 'some.key.for.Navigation'
            }, {
                type: 'Customization',
                i18nKey: 'some.key.for.Customization'
            }],
            selectedDependencies: [topHeaderSlotSyncStatus, bottomHeaderSlotSyncStatus, footerSlotSyncStatus],
            sharedDependencies: [otherSlotSyncStatus],
            unavailableDependencies: []
        };

        sessionStorage.setItem("syncStatus", JSON.stringify(syncStatus));
        sessionStorage.setItem("trashedCategoryPageSyncStatus", JSON.stringify(trashedCategoryPageSyncStatus));
        sessionStorage.setItem("trashedContentPageSyncStatus", JSON.stringify(trashedContentPageSyncStatus));

        var counter = 0;

        function makeStatusInSync(status) {
            status.status = 'IN_SYNC';
            status.dependentItemTypesOutOfSync = [];
            status.selectedDependencies.forEach(function(item) {
                item.status = 'IN_SYNC';
                item.dependentItemTypesOutOfSync = [];

                item.selectedDependencies.forEach(function(subItem) {
                    subItem.status = 'IN_SYNC';
                    subItem.dependentItemTypesOutOfSync = [];
                });
            });
        }

        $httpBackend.whenGET(/\/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/synchronizations\/versions\/Online\/pages/).respond(function(method, url, data, headers) {
            counter++;
            var status = JSON.parse(sessionStorage.getItem("syncStatus"));

            if (counter === 3) {
                if (status.selectedDependencies[1].status === 'IN_PROGRESS') {
                    status.selectedDependencies[1].status = 'IN_SYNC';
                    status.selectedDependencies[1].dependentItemTypesOutOfSync = [];
                    sessionStorage.setItem("syncStatus", JSON.stringify(status));
                    counter = 1;
                }
            }

            var id = /pages\/(.*)/.exec(url)[1];

            // set in_sync status for one page and all its dependencies
            if (id === "syncedpageuid" || id === "synchedPage") {
                var syncedPageStatus = lodash.cloneDeep(status);
                syncedPageStatus.status = "IN_SYNC";
                syncedPageStatus.dependentItemTypesOutOfSync = [];
                syncedPageStatus.selectedDependencies.forEach(function(selectedDependency) {
                    selectedDependency.status = "IN_SYNC";
                });
                return [200, syncedPageStatus];
            } else if (id === "secondpage") {
                return [200, lodash.cloneDeep(newlyCreatedPageSyncStatus)];
            } else if (id === "otherpage") {
                return [200, lodash.cloneDeep(otherPageSyncStatus)];
            } else if (id === "trashedCategoryPage") {
                return [200, lodash.cloneDeep(JSON.parse(sessionStorage.getItem("trashedCategoryPageSyncStatus")))];
            } else if (id === "trashedContentPage") {
                return [200, lodash.cloneDeep(JSON.parse(sessionStorage.getItem("trashedContentPageSyncStatus")))];
            } else if (id === "trashedProductPage") {
                return [200, lodash.cloneDeep(newlyCreatedPageSyncStatus)];
            } else {
                return [200, status];
            }
        });


        $httpBackend.whenPOST(/\/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/synchronizations\/versions\/Online/).respond(function(method, url, data, headers) {

            var items = JSON.parse(data).items.map(function(item) {
                return item.itemId;
            });

            var currentSyncStatus = items.indexOf('trashedCategoryPage') > -1 ? trashedCategoryPageSyncStatus : syncStatus;
            var status = items.indexOf('trashedCategoryPage') > -1 ? JSON.parse(sessionStorage.getItem("trashedCategoryPageSyncStatus")) : JSON.parse(sessionStorage.getItem("syncStatus"));

            if (items.indexOf(currentSyncStatus.itemId) > -1 && currentSyncStatus.status !== 'IN_SYNC') {
                makeStatusInSync(status);
            } else if (items.indexOf(newlyCreatedPageSyncStatus.itemId) > -1 && newlyCreatedPageSyncStatus.status !== 'IN_SYNC') {
                newlyCreatedPageSyncStatus.lastSyncStatus = new Date(2016, 10, 10, 13, 10, 0).getTime();
                makeStatusInSync(newlyCreatedPageSyncStatus);
            } else {
                status.selectedDependencies.forEach(function(item) {
                    if (items.indexOf(item.itemId) > -1) {
                        if (item.itemId === 'footerSlot') {
                            item.status = 'SYNC_FAILED';
                            item.dependentItemTypesOutOfSync = [{
                                type: 'Component',
                                i18nKey: 'component 5'
                            }];

                            item.selectedDependencies.forEach(function(subItem) {
                                if (items.indexOf(subItem.itemId) > -1) {
                                    if (subItem.itemId === 'component5') {
                                        subItem.status = 'SYNC_FAILED';
                                        subItem.dependentItemTypesOutOfSync = [{
                                            type: 'Other',
                                            i18nKey: 'other'
                                        }];
                                    }
                                }
                            });

                        } else if (item.itemId === 'bottomHeaderSlot') {
                            item.status = 'IN_PROGRESS';
                        } else {
                            item.status = 'IN_SYNC';
                            item.dependentItemTypesOutOfSync = [];

                            item.selectedDependencies.forEach(function(subItem) {
                                subItem.status = 'IN_SYNC';
                                subItem.dependentItemTypesOutOfSync = [];
                            });
                        }
                    }

                });
            }

            if (items.indexOf('trashedCategoryPage') > -1) {
                sessionStorage.setItem("trashedCategoryPageSyncStatus", JSON.stringify(status));
            } else {
                sessionStorage.setItem("syncStatus", JSON.stringify(status));
            }

            return [200, status];
        });

        //////////////////////////////////PAGE SYNC///////////////////////////////////////////////////////////
        var currentSyncJob = {
            creationDate: '2016-01-29T16:25:28',
            syncStatus: 'FINISHED',
            endDate: '2016-01-29T16:25:28',
            lastModifiedDate: '2016-01-29T16:25:28',
            syncResult: 'SUCCESS',
            startDate: '2016-01-29T16:25:28',
            sourceCatalogVersion: "Staged",
            targetCatalogVersion: "Online"
        };

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/synchronizations\/targetversions\/Online/).
        respond(function() {
            var syncResult = $window.localStorage.syncResult;
            if (syncResult) {
                if (syncResult === 'Failed') {
                    currentSyncJob.syncStatus = 'FAILURE';
                    currentSyncJob.syncResult = 'FAILURE';
                } else if (syncResult === 'Finished') {
                    currentSyncJob.syncStatus = 'FINISHED';
                    currentSyncJob.syncResult = 'SUCCESS';
                }

                $window.localStorage.syncResult = "ACK";
            }

            return [200, currentSyncJob];
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/synchronizations\/versions\/Online/).
        respond(function() {
            return [200, currentSyncJob];
        });

        $httpBackend.whenPUT(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/synchronizations\/versions\/Online/).
        respond(function() {
            currentSyncJob.syncStatus = 'RUNNING';
            currentSyncJob.syncResult = 'UNKNOWN';
            return [200, currentSyncJob];
        });

    });

try {
    angular.module('smarteditloader').requires.push('synchronizationMocksModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('synchronizationMocksModule');
} catch (e) {}
