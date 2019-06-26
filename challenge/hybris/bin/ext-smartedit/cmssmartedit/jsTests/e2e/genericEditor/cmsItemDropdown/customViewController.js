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
    .constant('PATH_TO_CUSTOM_VIEW', 'genericEditor/cmsItemDropdown/customView.html')
    .controller('customViewController', function(editorModalService, parseQuery, backendMocksUtils) {

        // -------------------------------------------------------------------------------
        // Constants
        // -------------------------------------------------------------------------------        
        var INVALID_TITLE = 'some invalid title';

        // -------------------------------------------------------------------------------
        // Backend Mocks
        // -------------------------------------------------------------------------------
        var componentsListGETMock = backendMocksUtils.getBackendMock('componentsListGETMock');

        // Banners
        var items = {
            componentItems: [
                // Banners 
                {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Banner 1',
                    'typeCode': 'ResponsiveBannerComponent',
                    'uid': 'banner1',
                    'uuid': 'banner1',
                    'visible': true,
                    'pk': '100',
                    'image': 'some image path',
                    'rotate': true
                },
                // Tabs
                {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Tab 1',
                    'typeCode': 'CmsTab',
                    'title': 'This is tab1',
                    'uid': 'tab1',
                    'uuid': 'tab1',
                    'visible': true,
                    'pk': '200'
                }
            ]
        };

        componentsListGETMock.respond(function(method, url, data, headers) {
            var params = parseQuery(url);
            var currentPage = params.currentPage;
            var mask = params.mask;
            var pageSize = params.pageSize;
            var typeCode = params.typeCode;
            var uuids = params.uuids && params.uuids.split(',');
            var itemSearchParams = params.itemSearchParams && params.itemSearchParams.split(',');

            var filteredItems = items.componentItems;

            if (uuids) {
                filteredItems = items.componentItems.filter(function(item) {
                    return uuids.indexOf(item.uuid) > -1;
                });

                return [200, {
                    response: filteredItems
                }];
            }

            if (typeCode) {
                var typeCodesMapping = {
                    'BannerComponent': 'Banner',
                    'CmsTab': 'Tab',
                    'CmsLinkComponent': 'Link'
                };

                var codeToUse = typeCodesMapping[typeCode];
                filteredItems = (!codeToUse) ? [] : filteredItems.filter(function(item) {
                    return item.typeCode.indexOf(codeToUse) > -1;
                });
            }

            if (itemSearchParams) {
                filteredItems = filteredItems.filter(function(item) {
                    var filtered = false;
                    itemSearchParams.forEach(function(param) {
                        var paramParsed = param.split(':');
                        if (paramParsed.length === 2 && item[paramParsed[0]] && item[paramParsed[0]].toUpperCase() === paramParsed[1].toUpperCase()) {
                            filtered = true;
                        }
                    });

                    return filtered;
                });
            }

            filteredItems = filteredItems.filter(function(item) {
                return mask ? ((item.name && typeof item.name === 'string' && item.name.toUpperCase().indexOf(mask.toUpperCase()) > -1) || item.uid.toUpperCase().indexOf(mask.toUpperCase()) > -1) : true;
            });

            var results = filteredItems.slice(currentPage * 10, currentPage * 10 + 10);

            var pagedResults = {
                pagination: {
                    count: 10,
                    page: currentPage,
                    totalCount: filteredItems.length,
                    totalPages: 3
                },
                response: results
            };

            return [200, pagedResults];
        });


        var componentGETMock = backendMocksUtils.getBackendMock('componentGETMock');
        componentGETMock.respond(function(method, url, data, headers) {
            var uuid = /cmsitems\/(.*)/.exec(url)[1];
            return [200, items.componentItems.find(function(item) {
                return item.uuid === uuid;
            })];
        });

        // Tabs
        var counter = 1;
        var DEFAULT_UID = 'COMP_';
        var componentPOSTMock = backendMocksUtils.getBackendMock('componentPOSTMock');

        componentPOSTMock.respond(function(method, url, data, headers) {
            var dataObject = angular.fromJson(data);
            if (dataObject.title === INVALID_TITLE) {
                return [400, {
                    'errors': [{
                        'message': 'Invalid title',
                        'reason': 'invalid',
                        'subject': 'title',
                        'subjectType': 'parameter',
                        'type': 'ValidationError'
                    }]
                }];
            } else {
                dataObject.uid = DEFAULT_UID + counter;
                dataObject.uuid = DEFAULT_UID + counter + '_UUID';
                counter++;

                items.componentItems.push(dataObject);
                return [200, dataObject];
            }
        });

        var componentPUTMock = backendMocksUtils.getBackendMock('componentPUTMock');
        componentPUTMock.respond(function(method, url, data, headers) {
            var uuid = /cmsitems\/(.*)/.exec(url)[1];
            var payload = JSON.parse(data);
            items.componentItems = items.componentItems.map(function(item) {
                if (item.uuid === payload.uuid) {
                    return payload;
                } else {
                    return item;
                }
            });

            return [204];
        });

        // Information about the component in the generic editor. 
        this.componentConfiguration = {
            componentType: "TabsetComponent",
            componentId: null
        };

        this.openEditor = function() {
            // var componentId = (carouselComponent) ? carouselComponent.uid : null;
            var componentId = null;
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
