angular
    .module('personalizationsearchSearchProfilesModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditCommerceCustomizationModule',
        'personalizationsmarteditDataFactory',
        'personalizationsearchRestServiceModule',
        'waitDialogServiceModule',
        'personalizationsearchSearchProfilesContextServiceModule',
        'ui.tree'
    ])
    .constant('SEARCH_PROFILE_ACTION_TYPE', 'cxSearchProfileActionData')
    .run(function($q, $filter, personalizationsearchRestService, personalizationsmarteditCommerceCustomizationService, personalizationsearchSearchProfilesContextService, personalizationsmarteditMessageHandler, SEARCH_PROFILE_ACTION_TYPE) {

        personalizationsmarteditCommerceCustomizationService.registerType({
            type: SEARCH_PROFILE_ACTION_TYPE,
            text: 'personalizationsearchsmartedit.commercecustomization.action.type.search',
            template: 'personalizationsearchSearchProfilesTemplate.html',
            confProperty: 'personalizationsearch.commercecustomization.search.profile.enabled',
            getName: function(action) {
                return $filter('translate')('personalizationsearchsmartedit.commercecustomization.search.display.name') + " - " + action.searchProfileCode;
            },
            updateActions: function(customizationCode, variationCode, actions, respCreate) {
                var deferred = $q.defer();

                if (angular.isDefined(respCreate)) {
                    personalizationsearchSearchProfilesContextService.updateSearchActionContext(respCreate.data.actions);
                }

                var searchProfilesCtx = personalizationsearchSearchProfilesContextService.searchProfileContext;
                var rankAfterAction = searchProfilesCtx.searchProfilesOrder.splice(0, 1)[0];
                var spActionCodes = searchProfilesCtx.searchProfilesOrder.map(function(sp) {
                    return sp.code;
                }).join(',');

                var filter = {
                    customizationCode: searchProfilesCtx.customizationCode,
                    variationCode: searchProfilesCtx.variationCode,
                    rankAfterAction: rankAfterAction.code,
                    actions: spActionCodes
                };

                personalizationsearchRestService.updateSearchProfileActionRank(filter).then(function successCallback() {
                    deferred.resolve();
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingcustomization'));
                    deferred.reject();
                });

                return deferred.promise;
            }
        });
    })
    .controller('personalizationsearchSearchProfilesController', function($q, $scope, $filter, $timeout, personalizationsearchRestService, personalizationsmarteditMessageHandler, waitDialogService, personalizationsearchSearchProfilesContextService, personalizationsmarteditContextService, PaginationHelper, SEARCH_PROFILE_ACTION_TYPE, PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES) {

        $scope.selectedSearchProfile = null;
        $scope.availableSearchProfiles = [];
        $scope.searchProfileActions = [];
        $scope.helpTemplate = "<span>" + $filter('translate')('personalizationsearchsmartedit.commercecustomization.search.helpmsg') + "</span>";

        $scope.searchProfileContext = personalizationsearchSearchProfilesContextService.searchProfileContext;

        $scope.searchProfilePagination = new PaginationHelper();
        $scope.searchProfilePagination.reset();
        $scope.searchProfileFilter = {
            code: ''
        };

        var getIndexTypes = function() {
            var deferred = $q.defer();

            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;

            var promises = [];

            angular.forEach((experienceData.productCatalogVersions || []), function(productCV) {
                promises.push(personalizationsearchRestService.getIndexTypesForCatalogVersion(productCV));
            });

            $q.all(promises)
                .then(function successCallback(response) {
                    var mergedResponse = {
                        indexTypeIds: []
                    };

                    angular.forEach(response, function(res) {
                        mergedResponse.indexTypeIds = mergedResponse.indexTypeIds.concat(res.indexTypeIds.filter(function(item) {
                            return mergedResponse.indexTypeIds.indexOf(item) < 0;
                        }));
                    });

                    deferred.resolve(mergedResponse);
                }, function errorCallback(errorResponse) {
                    deferred.reject(errorResponse);
                });

            return deferred.promise;
        };

        var getSearchProfileFilterObject = function() {
            return {
                code: $scope.searchProfileFilter.code,
                pageSize: $scope.searchProfilePagination.count,
                currentPage: $scope.searchProfilePagination.page + 1
            };
        };

        var buildAction = function(item) {
            return {
                type: SEARCH_PROFILE_ACTION_TYPE,
                searchProfileCode: item.code,
                searchProfileCatalog: item.catalogVersion.split(":")[0]
            };
        };

        var getWrapperActionForAction = function(action) {
            return $scope.actions.filter(function(wrapper) {
                return personalizationsearchSearchProfilesContextService.searchProfileActionComparer(action, wrapper.action);
            })[0];
        };

        $scope.searchProfileSelected = function(item, uiSelectObject) {
            var action = buildAction(item);
            $scope.addAction(action, personalizationsearchSearchProfilesContextService.searchProfileActionComparer);
            uiSelectObject.selected = null;
        };

        $scope.isItemInSelectDisabled = function(item) {
            var action = buildAction(item);
            return $scope.isItemInSelectedActions(action, personalizationsearchSearchProfilesContextService.searchProfileActionComparer);
        };

        $scope.initUiSelect = function(uiSelectController) {
            uiSelectController.isActive = function() {
                return false;
            };
        };

        $scope.removeSelectedSearchAction = function(action) {
            var wrapperActionToRem = getWrapperActionForAction(action);

            $scope.removeSelectedAction(wrapperActionToRem);
        };

        var setStatusForUpdatedActions = function(wrapperActions) {
            wrapperActions.forEach(function(action) {
                if (action.status !== PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW) {
                    action.status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
                }
            });
        };

        $scope.setSearchActionRank = function(action, increaseValue) {
            var wrappedAction = getWrapperActionForAction(action);

            var wrappedAffectedAction = getWrapperActionForAction($scope.searchProfileActions[$scope.searchProfileActions.indexOf(action) + increaseValue]);

            var sourceIndex = $scope.actions.indexOf(wrappedAction);
            var targetIndex = $scope.actions.indexOf(wrappedAffectedAction);
            $scope.actions.splice(targetIndex, 0, $scope.actions.splice(sourceIndex, 1)[0]);

            setStatusForUpdatedActions([wrappedAction, wrappedAffectedAction]);
        };

        $scope.isDirty = function() {
            return true;
        };

        $scope.moreSearchProfilestRequestProcessing = false;
        $scope.addMoreSearchProfilesItems = function() {
            if ($scope.searchProfilePagination.page < $scope.searchProfilePagination.totalPages - 1 && !$scope.moreSearchProfilestRequestProcessing) {
                $scope.moreSearchProfilestRequestProcessing = true;

                getIndexTypes().then(
                    function successCallback(response) {
                        var filter = getSearchProfileFilterObject();

                        var param = {
                            indexTypes: response.indexTypeIds || []
                        };

                        filter = angular.extend(filter, param);

                        personalizationsearchRestService.getSearchProfiles(filter).then(function successCallback(response) {
                            Array.prototype.push.apply($scope.availableSearchProfiles, response.searchProfiles);
                            $scope.searchProfilePagination = new PaginationHelper(response.pagination);
                            $scope.moreSearchProfilestRequestProcessing = false;
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalizationsearchsmartedit.commercecustomization.search.error.gettingsearchprofiles'));
                            $scope.moreSearchProfilestRequestProcessing = false;
                        });
                    },
                    function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalizationsearchsmartedit.commercecustomization.search.error.gettingindextypes'));
                    }
                );


            }
        };

        $scope.segmentSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            $scope.searchProfilePagination.reset();
            $scope.searchProfileFilter.code = searchObj;
            $scope.availableSearchProfiles.length = 0;
            $scope.addMoreSearchProfilesItems();
        };

        $scope.treeOptions = {
            dropped: function(e) {
                if (e.source.index !== e.dest.index) {
                    $scope.aaa = true;
                    //update backing actions array
                    var sourceEl = e.source.nodeScope.$modelValue;
                    var destEl = e.dest.nodesScope.$modelValue[e.dest.index];
                    $timeout(function() {
                        $scope.actions.splice(destEl.baseIndex, 0, $scope.actions.splice(sourceEl.baseIndex, 1)[0]);
                    }, 0);

                    //set UPDATED status for modified actions
                    var startIdx = e.source.index < e.dest.index ? e.source.index : e.dest.index;
                    var increaseValue = Math.abs(e.dest.index - e.source.index) + 1;

                    var modifiedActions = $scope.searchProfileActions.slice(startIdx, increaseValue);
                    var modifiedWrappedActions = modifiedActions.map(function(action) {
                        return getWrapperActionForAction(action);
                    });
                    setStatusForUpdatedActions(modifiedWrappedActions);
                    $scope.aaa = false;
                }
            }
        };

        $scope.$watch('actions', function(newValue) {
            if (!$scope.aaa) {
                var actionsArray = newValue || [];
                $scope.searchProfileActions = actionsArray
                    .filter(function(item) {
                        return item.action.type === SEARCH_PROFILE_ACTION_TYPE;
                    }).map(function(item) {
                        var extAction = angular.extend(item.action, {
                            baseIndex: actionsArray.indexOf(item)
                        });
                        return extAction;
                    });

                $scope.searchProfileContext.searchProfilesOrder = $scope.searchProfileActions;
            }
        }, true);

        $scope.$watch('customization', function(newValue) {
            if (angular.isDefined(newValue)) {
                $scope.searchProfileContext.customizationCode = newValue.code;
            } else {
                $scope.searchProfileContext.customizationCode = undefined;
            }
        }, true);

        $scope.$watch('variation', function(newValue) {
            if (angular.isDefined(newValue)) {
                $scope.searchProfileContext.variationCode = newValue.code;
            } else {
                $scope.searchProfileContext.variationCode = undefined;
            }
        }, true);
    });
