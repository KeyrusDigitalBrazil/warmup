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
angular.module('pageVersionMocks', ['ngMockE2E', 'backendMocksUtilsModule'])
    .run(function($httpBackend, parseQuery, backendMocksUtils) {
        var pageMocks = [{
            siteId: 'apparel-uk',
            pageUUID: 'homepage',
            versions: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    description: "Nullam non lorem ultricies, ultrices augue in, blandit quam. Aliquam dapibus urna id dapibus finibus. Integer nec sem in leo placerat fringilla sed sit amet sapien. Suspendisse potenti. Maecenas finibus suscipit lacus. Nulla auctor feugiat semper. Nunc posuere sem vel metus rutrum gravida. Vestibulum lectus mi, lacinia et pellentesque laoreet, luctus et sem. Quisque ultricies tincidunt augue, eu tristique dui ullamcorper at. Sed maximus interdum commodo. Nulla quis volutpat lorem, nec porttitor lorem.",
                    label: "Version 1",
                    itemUUID: "homepage",
                    uid: 'homepage_version1'
                },
                {
                    creationtime: "2016-04-02T21:16:41+0000",
                    description: "Sed maximus interdum commodo.",
                    label: "New - Version 2",
                    itemUUID: "homepage",
                    uid: 'homepage_version2'
                },
                {
                    creationtime: "2016-04-04T21:16:41+0000",
                    description: "",
                    label: "New - Version 3",
                    itemUUID: "homepage",
                    uid: 'homepage_version3'
                },
                {
                    creationtime: "2016-04-08T21:16:41+0000",
                    description: "Nullam non lorem ultricies, ultrices augue in, blandit quam. Aliquam dapibus urna id dapibus finibus. Integer nec sem in leo placerat fringilla sed sit amet sapien. Suspendisse potenti. Maecenas finibus suscipit lacus. Nulla auctor feugiat semper. Nunc posuere sem vel metus rutrum gravida. Vestibulum lectus mi, lacinia et pellentesque laoreet, luctus et sem. Quisque ultricies tincidunt augue, eu tristique dui ullamcorper at. Sed maximus interdum commodo. Nulla quis volutpat lorem, nec porttitor lorem.",
                    label: "Other - Version 4",
                    itemUUID: "homepage",
                    uid: 'homepage_version4'
                },
                {
                    creationtime: "2016-04-02T21:16:41+0000",
                    description: "Sed maximus interdum commodo.",
                    label: "Version 5 - This one has a super loooooong label which should be handled properly",
                    itemUUID: "homepage",
                    uid: 'homepage_version5'
                },
                {
                    creationtime: "2016-04-04T21:16:41+0000",
                    description: "",
                    label: "New - Version 6",
                    itemUUID: "homepage",
                    uid: 'homepage_version6'
                },
                {
                    creationtime: "2016-04-08T21:16:41+0000",
                    description: "Nullam non lorem ultricies, ultrices augue in, blandit quam. Aliquam dapibus urna id dapibus finibus. Integer nec sem in leo placerat fringilla sed sit amet sapien. Suspendisse potenti. Maecenas finibus suscipit lacus. Nulla auctor feugiat semper. Nunc posuere sem vel metus rutrum gravida. Vestibulum lectus mi, lacinia et pellentesque laoreet, luctus et sem. Quisque ultricies tincidunt augue, eu tristique dui ullamcorper at. Sed maximus interdum commodo. Nulla quis volutpat lorem, nec porttitor lorem.",
                    label: "Other - Version 7",
                    itemUUID: "homepage",
                    uid: 'homepage_version7'
                },
                {
                    creationtime: "2016-04-02T21:16:41+0000",
                    description: "Sed maximus interdum commodo.",
                    label: "Version 8",
                    itemUUID: "homepage",
                    uid: 'homepage_version8'
                },
                {
                    creationtime: "2016-04-04T21:16:41+0000",
                    description: "",
                    label: "Version 9",
                    itemUUID: "homepage",
                    uid: 'homepage_version9'
                },
                {
                    creationtime: "2016-04-08T21:16:41+0000",
                    description: "Nullam non lorem ultricies, ultrices augue in, blandit quam. Aliquam dapibus urna id dapibus finibus. Integer nec sem in leo placerat fringilla sed sit amet sapien. Suspendisse potenti. Maecenas finibus suscipit lacus. Nulla auctor feugiat semper. Nunc posuere sem vel metus rutrum gravida. Vestibulum lectus mi, lacinia et pellentesque laoreet, luctus et sem. Quisque ultricies tincidunt augue, eu tristique dui ullamcorper at. Sed maximus interdum commodo. Nulla quis volutpat lorem, nec porttitor lorem.",
                    label: "Version 10",
                    itemUUID: "homepage",
                    uid: 'homepage_version10'
                },
                {
                    creationtime: "2016-04-02T21:16:41+0000",
                    description: "Sed maximus interdum commodo.",
                    label: "Version 11",
                    itemUUID: "homepage",
                    uid: 'homepage_version11'
                },
                {
                    creationtime: "2016-04-04T21:16:41+0000",
                    description: "",
                    label: "Special - Version 12",
                    itemUUID: "homepage",
                    uid: 'homepage_version12'
                },
                {
                    creationtime: "2016-04-08T21:16:41+0000",
                    description: "Nullam non lorem ultricies, ultrices augue in, blandit quam. Aliquam dapibus urna id dapibus finibus. Integer nec sem in leo placerat fringilla sed sit amet sapien. Suspendisse potenti. Thor. Maecenas finibus suscipit lacus. Nulla auctor feugiat semper. Nunc posuere sem vel metus rutrum gravida. Vestibulum lectus mi, lacinia et pellentesque laoreet, luctus et sem. Quisque ultricies tincidunt augue, eu tristique dui ullamcorper at. Sed maximus interdum commodo. Nulla quis volutpat lorem, nec porttitor lorem.",
                    label: "Version 13",
                    itemUUID: "homepage",
                    uid: 'homepage_version13'
                },
                {
                    creationtime: "2016-04-02T21:16:41+0000",
                    description: "Sed maximus interdum commodo.",
                    label: "Version 14",
                    itemUUID: "homepage",
                    uid: 'homepage_version14'
                },
                {
                    creationtime: "2016-04-04T21:16:41+0000",
                    description: "",
                    label: "Version 15",
                    itemUUID: "homepage",
                    uid: 'homepage_version15'
                }
            ]
        }];

        var parsePageInfoFromUrl = function(url) {
            var regex = /\/sites\/([\w-]+)\/cmsitems\/([\w-]+)/;
            var parsedUrl = url.match(regex);
            return {
                siteId: parsedUrl[1],
                pageUUID: parsedUrl[2]
            };
        };

        var pageVersionsGETMock = $httpBackend
            .whenGET(/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*\/versions/)
            .respond(
                function(method, url) {

                    var params = parseQuery(url);
                    var pageInfo = parsePageInfoFromUrl(url);

                    var currentPage = parseInt(params.currentPage);
                    var mask = params.mask.replace(/\+/g, ' ');
                    var pageSize = parseInt(params.pageSize);

                    var pageMock = pageMocks.filter(function(pageMock) {
                        return pageMock.siteId === pageInfo.siteId &&
                            pageMock.pageUUID === pageInfo.pageUUID;
                    })[0];

                    var filteredResult = (pageMock === null) ? [] : pageMock.versions.filter(function(mockedPageVersion) {
                        return (!!mask) ?
                            (mockedPageVersion.label && (mockedPageVersion.label.toUpperCase().indexOf(mask.toUpperCase()) > -1)) :
                            true;
                    });

                    var slicedResult = filteredResult.slice(currentPage * pageSize, currentPage * pageSize + pageSize);

                    var pagedResults = {
                        pagination: {
                            count: pageSize,
                            page: currentPage,
                            totalCount: filteredResult.length,
                            totalPages: Math.floor(filteredResult.length / pageSize)
                        },
                        results: slicedResult
                    };

                    return [200, pagedResults];

                }
            );

        backendMocksUtils.storeBackendMock('pageVersionsGETMock', pageVersionsGETMock);

        var parsePathParamsFromRollbackUrl = function(url) {
            var regex = /\/sites\/([\w-]+)\/cmsitems\/([\w-]+)\/versions\/([\w-]+)\/rollbacks/;
            var parsedUrl = url.match(regex);
            return {
                siteId: parsedUrl[1],
                pageUUID: parsedUrl[2],
                versionId: parsedUrl[3]
            };
        };

        var rollbackVersionMock = $httpBackend
            .whenPOST(/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*\/versions\/.*\/rollbacks/)
            .respond(function(method, url) {

                var pathParams = parsePathParamsFromRollbackUrl(url);

                if (pathParams.versionId === 'homepage_version4') {
                    return [404];
                } else {
                    return [204];
                }
            });

        backendMocksUtils.storeBackendMock('rollbackVersionMock', rollbackVersionMock);

        var createVersionPOSTMock = $httpBackend
            .whenPOST(/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*\/versions/)
            .respond(function(method, url, data) {

                var dataObject = angular.fromJson(data);
                var pageInfo = parsePageInfoFromUrl(url);

                if (dataObject.label === 'New Test Version') {
                    return [201, {
                        uid: "homepage_version_new",
                        itemUUID: pageInfo.pageUUID,
                        label: dataObject.label,
                        description: dataObject.description,
                        creationtime: "2018-01-01T21:59:59+0000"
                    }];

                } else {
                    return [400, {
                        "errors": [{
                            "message": "The value provided is already in use.",
                            "reason": "invalid",
                            "subject": "label",
                            "subjectType": "parameter",
                            "type": "ValidationError"
                        }]
                    }];
                }
            });

        backendMocksUtils.storeBackendMock('createVersionPOSTMock', createVersionPOSTMock);

        var parsePathParamsFromVersionUrl = function(url) {
            var regex = /\/sites\/([\w-]+)\/cmsitems\/([\w-]+)\/versions\/([\w-]+)/;
            var parsedUrl = url.match(regex);
            return {
                siteId: parsedUrl[1],
                pageUUID: parsedUrl[2],
                versionId: parsedUrl[3]
            };
        };

        var deleteVersionMock = $httpBackend
            .whenDELETE(/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*\/versions\/.*/)
            .respond(function(method, url) {

                var pathParams = parsePathParamsFromVersionUrl(url);

                if (pathParams.versionId === 'homepage_version4') {
                    return [404];
                } else {
                    pageMocks[0].versions = pageMocks[0].versions.filter(function(version) {
                        return version.uid !== pathParams.versionId;
                    });
                    return [204];
                }
            });

        backendMocksUtils.storeBackendMock('deleteVersionMock', deleteVersionMock);

        var editVersionMock = $httpBackend
            .whenPUT(/cmswebservices\/v1\/sites\/.*\/cmsitems\/.*\/versions\/.*/)
            .respond(function(method, url, data) {

                var pathParams = parsePathParamsFromVersionUrl(url);
                var dataObject = angular.fromJson(data);

                if (pathParams.versionId === 'homepage_version4') {
                    return [400, {
                        "errors": [{
                            "message": "The value provided is already in use.",
                            "reason": "invalid",
                            "subject": "label",
                            "subjectType": "parameter",
                            "type": "ValidationError"
                        }]
                    }];
                } else {

                    var pageMock = pageMocks.filter(function(pageMock) {
                        return pageMock.siteId === pathParams.siteId &&
                            pageMock.pageUUID === pathParams.pageUUID;
                    })[0];

                    var version = pageMock.versions.find(function(version) {
                        return version.uid === pathParams.versionId;
                    });

                    version.description = dataObject.description;
                    version.label = dataObject.label;

                    return [200, dataObject];
                }
            });

        backendMocksUtils.storeBackendMock('editVersionMock', editVersionMock);

    });

try {
    angular.module('smarteditloader').requires.push('pageVersionMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('pageVersionMocks');
} catch (e) {}
