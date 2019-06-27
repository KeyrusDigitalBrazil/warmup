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
angular
    .module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI, parseQuery) {

        var map = [{
            "value": "[\"*\"]",
            "key": "whiteListedStorefronts"
        }, {
            "value": "\"thepreviewTicketURI\"",
            "key": "previewTicketURI"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/test/e2e/tree/outerapp.js\"}",
            "key": "applications.outerapp"
        }];

        $httpBackend.whenGET(/configuration$/).respond(
            function() {
                return [200, map];
            });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
                name: 'English',
                required: true
            }]
        });

        var nodes = [{
            uid: "1",
            name: "node1",
            title: {
                en: "node1_en",
                fr: "node1_fr"
            },
            parentUid: "root",
            hasChildren: true
        }, {
            uid: "2",
            name: "node2",
            title: {
                en: "node2_en",
                fr: "node2_fr"
            },
            parentUid: "root",
            hasChildren: true
        }, {
            uid: "4",
            name: "node4",
            title: {
                "en": "nodeA",
                "fr": "nodeA"
            },
            parentUid: "1",
            hasChildren: false
        }, {
            uid: "5",
            name: "node5",
            title: {
                "en": "nodeB",
                "fr": "nodeB"
            },
            parentUid: "1",
            hasChildren: false
        }, {
            uid: "3",
            name: "node3",
            title: {
                "en": "nodeF",
                "fr": "nodeF"
            },
            parentUid: "1",
            hasChildren: false
        }, {
            uid: "6",
            name: "node6",
            title: {
                "en": "nodeC",
                "fr": "nodeC"
            },
            parentUid: "2",
            hasChildren: false
        }];

        $httpBackend.whenGET(/someNodeURI/).respond(function(method, url) {
            var query = parseQuery(url);
            var parentUID = query.parentUid;

            return [200, {
                navigationNodes: nodes.filter(function(node) {
                    return node.parentUid === parentUID;
                })
            }];
        });

        $httpBackend.whenPOST(/someNodeURI/).respond(function(method, url, data) {
            var payload = JSON.parse(data);
            var uid = new Date().getTime().toString();
            var node = {
                uid: uid,
                name: payload.name,
                parentUid: payload.parentUid,
                hasChildren: false
            };
            nodes.push(node);
            return [200, node];
        });

        $httpBackend.whenDELETE(/someNodeURI/).respond(function(method, url) {
            var uid = /someNodeURI\/(.*)/.exec(url)[1];
            nodes = nodes.filter(function(node) {
                return node.uid !== uid;
            });
            return [204];
        });

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({});


        $httpBackend
            .whenGET("/smarteditwebservices\/v1\/i18n\/languages")
            .respond({});
    });
angular.module('smarteditloader').requires.push('e2eBackendMocks');
angular.module('smarteditcontainer').requires.push('e2eBackendMocks');
