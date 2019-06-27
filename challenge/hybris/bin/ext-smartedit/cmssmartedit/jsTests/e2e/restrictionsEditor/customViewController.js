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
angular.module('customViewModule', ['smarteditServicesModule', 'backendMocksUtilsModule', 'yLoDashModule', 'pageRestrictionsModule', 'genericEditorModule'])
    .constant('PATH_TO_CUSTOM_VIEW', 'restrictionsEditor/customView.html')
    .controller('customViewController', function($q, $filter, sharedDataService, backendMocksUtils, lodash, pageRestrictionsFacade, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, systemEventService) {
        this.pageType = sessionStorage.getItem("pageType");

        this.editable = true;
        this.page = {
            "type": "contentPageData",
            "uid": "add-edit-address",
            "uuid": "add-edit-address",
            "typeCode": this.pageType,
            "onlyOneRestrictionMustApply": false,
            "creationtime": "2016-07-15T23:35:21+0000",
            "defaultPage": true,
            "modifiedtime": "2016-07-15T23:38:01+0000",
            "name": "Add Edit Address Page",
            "pk": "8796095743024",
            "template": "AccountPageTemplate",
            "title": {
                "de": "Adresse hinzufügen/bearbeiten"
            },
            "label": "add-edit-address",
            "restrictions": [{
                'uid': 'timeRestrictionIdA',
                'uuid': 'timeRestrictionIdA',
                'name': 'Some Time restriction A',
                'typeCode': 'CMSTimeRestriction',
                'itemtype': 'CMSTimeRestriction',
                'description': 'some description'
            }, {
                'uid': 'catalogRestrictionIdD',
                'uuid': 'catalogRestrictionIdD',
                'name': 'some cat restriction',
                'typeCode': 'CMSCategoryRestriction',
                'itemtype': 'CMSCategoryRestriction',
                'description': 'some description',
                'categories': ['categoryA']
            }]
        };

        this.getRestrictionTypes = function() {
            return pageRestrictionsFacade.getRestrictionTypesByPageType(this.page.typeCode);
        }.bind(this);

        this.getSupportedRestrictionTypes = function() {
            return $q.when(['CMSTimeRestriction', 'CMSCategoryRestriction', 'CMSUserGroupRestriction']);
        };

        this.includeRestrictions = sessionStorage.getItem("existingRestrictions");
        if (this.includeRestrictions === null || this.includeRestrictions === 'false') {

            var items = JSON.parse(sessionStorage.getItem("componentMocks"));

            items.componentItems.forEach(function(item) {
                if (item.uuid === "add-edit-address") {
                    item.restrictions = [];
                }
                this.page.restrictions = [];
            }.bind(this));

            sessionStorage.setItem("componentMocks", JSON.stringify(items));
        }

        backendMocksUtils.getBackendMock('componentPOSTMock').respond(function(method, url, data) {
            var parsedData = JSON.parse(data);

            if (url.indexOf('dryRun=true')) {
                if (parsedData.itemtype === 'CMSTimeRestriction') {
                    if (parsedData.activeUntil < parsedData.activeFrom) {
                        return [400, {
                            'errors': [{
                                'message': 'The dates and times provided are not valid. The Active until date/time must be after/later than the Active from date/time.',
                                'reason': 'missing',
                                'subject': 'activeUntil',
                                'subjectType': 'parameter',
                                'type': 'ValidationError'
                            }]
                        }];
                    }
                }
            }

            switch (parsedData.itemtype) {
                case "CMSTimeRestriction":
                    var dateFrom = $filter('date')(parsedData.activeFrom, 'MM/dd/yy HH:mm a');
                    var dateUntil = $filter('date')(parsedData.activeUntil, 'MM/dd/yy HH:mm a');
                    parsedData.uid = 'restriction-time-1';
                    parsedData.uuid = 'restriction-time-1';
                    parsedData.description = "Page only applies from " + dateFrom + " to " + dateUntil;
                    break;
                case "CMSCategoryRestriction":
                    parsedData.uid = 'restriction-category-1';
                    parsedData.uuid = 'restriction-category-1';
                    parsedData.description = getCategoryRestrictionDescription(parsedData.categories);
                    break;
                case "CMSUserGroupRestriction":
                    parsedData.uid = 'restriction-usergroup-1';
                    parsedData.uuid = 'restriction-usergroup-1';
                    parsedData.description = getUserGroupRestrictionDescription(parsedData.userGroups);
                    break;
            }

            addRestrictionToList(parsedData);

            return [201, parsedData];

        });

        backendMocksUtils.getBackendMock('componentPUTMock').respond(function(method, url, data) {
            var parsedData = JSON.parse(data);

            if (url.indexOf('dryRun=true')) {
                if (parsedData.itemtype === 'CMSTimeRestriction') {
                    if (parsedData.activeUntil < parsedData.activeFrom) {
                        return [400, {
                            'errors': [{
                                'message': 'The dates and times provided are not valid. The Active until date/time must be after/later than the Active from date/time.',
                                'reason': 'missing',
                                'subject': 'activeUntil',
                                'subjectType': 'parameter',
                                'type': 'ValidationError'
                            }]
                        }];
                    }
                }
            }

            switch (parsedData.itemtype) {
                case "CMSTimeRestriction":
                    var dateFrom = $filter('date')(parsedData.activeFrom, 'MM/dd/yy HH:mm a');
                    var dateUntil = $filter('date')(parsedData.activeUntil, 'MM/dd/yy HH:mm a');
                    parsedData.description = "Page only applies from " + dateFrom + " to " + dateUntil;
                    break;
                case "CMSCategoryRestriction":
                    parsedData.description = getCategoryRestrictionDescription(parsedData.categories);
                    break;
                case "CMSUserGroupRestriction":
                    parsedData.description = getUserGroupRestrictionDescription(parsedData.userGroups);
                    break;
            }

            addRestrictionToList(parsedData);

            return [201, parsedData];
        });

        // Helpers
        function addRestrictionToList(newRestriction) {
            var items = JSON.parse(sessionStorage.getItem("componentMocks"));

            lodash.remove(items.componentItems, function(item) {
                return item.uuid === newRestriction.uuid;
            });

            items.componentItems.push(newRestriction);

            sessionStorage.setItem("componentMocks", JSON.stringify(items));
        }

        this.dialogSaveFn = function() {
            this.saveResult = (this.isDirtyFn()) ?
                'Save executed.' : 'Save cannot be executed. Editor not dirty.';

            var returnError = JSON.parse(window.sessionStorage.getItem('returnErrors'));
            if (returnError) {
                var unrelatedValidationErrors = [{
                    "message": "Error message activeFrom",
                    "reason": "invalid",
                    "subject": "restrictions.activeFrom",
                    "position": "0",
                    "type": "ValidationError"
                }, {
                    "message": "Error message name",
                    "reason": "invalid",
                    "subject": "restrictions.name",
                    "position": "2",
                    "type": "ValidationError"
                }];

                systemEventService.publishAsync(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                    messages: unrelatedValidationErrors
                });
            }
        };
    });

try {
    angular.module('smarteditcontainer').requires.push('customViewModule');
} catch (e) {}
