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
(function() {
    var DIALOG_SAVE_EVENT = "sm-editor-save";
    var DIALOG_CANCEL_EVENT = "sm-editor-cancel";
    var DIALOG_RESET_EVENT = "sm-editor-reset";
    var DIALOG_IS_DIRTY_EVENT = "sm-editor-is-dirty";

    /**
     * @ngdoc overview
     * @name editorTabsetModule
     * @description
     * @deprecated since 6.6, {@link use genericEditorModule.service:GenericEditor genericEditor} directly and in multitab mode: spreading accross tabs is now controlled by {@link genericEditorServicesModule.service:editorFieldMappingService#addFieldTabMapping editorFieldMappingService#addFieldTabMapping}
     * The editor tabset module contains the service and the directives necessary to organize and display editing forms
     * in tabs.
     *
     * Use the {@link editorTabsetModule.service:editorTabsetService editorTabsetService} to manage the list of
     * tabs that will be present in the tabset.
     *
     * Use the {@link editorTabsetModule.directive:editorTabset editorTabset} directive to display editing
     * forms organized as tabs within a tabset (it will contain the tabs registered in the editorTabsetService).
     *
     */
    angular.module('editorTabsetModule', ['ui.bootstrap', 'functionsModule', 'experienceInterceptorModule', 'tabsetModule', 'smarteditServicesModule', 'cmssmarteditContainerTemplates'])

        /**
         * @ngdoc service
         * @name editorTabsetModule.service:editorTabsetService
         * @deprecated
         * @description
         *
         * The Editor Tabset Service keeps track of a list of tabs and their configuration, which includes the id, title,
         * and template  URL. The service allows registering, removing, and listing tabs. By default it contains the following ones:
         * - Admin Tab
         * - Basic Tab
         * - Generic Tab
         *
         * This service is used by the {@link editorTabsetModule.directive:editorTabset editorTabset}
         * directive to determine the tabs to be displayed in the tabset.
         */
        .factory('editorTabsetService', function() {
            var EditorTabsetService = function() {
                this.tabsList = [];
            };

            /**
             * @ngdoc method
             * @name editorTabsetModule.service:editorTabsetService#registerTab
             * @methodOf editorTabsetModule.service:editorTabsetService
             *
             * @description
             * This method allows registering a tab to be displayed in an editing tabset.
             *
             * Note: If the tabId is not unique, this method will override the previous tab configuration.
             *
             * @param {String} tabId An ID for the tab. It has to be unique, otherwise the previous tab configuration will be overriden.
             * @param {String} tabTitle The string displayed as the tab header.
             * @param {String} tabTemplateUrl Path to the HTML fragment to be displayed as the tab content.
             */
            EditorTabsetService.prototype.registerTab = function(tabId, tabTitle, tabTemplateUrl) {
                this._validateTab(tabId, tabTitle, tabTemplateUrl);

                this.tabsList.push({
                    id: tabId,
                    title: tabTitle,
                    templateUrl: tabTemplateUrl,
                    hasErrors: false
                });
            };

            EditorTabsetService.prototype._validateTabs = function(tabs) {
                tabs.forEach(function(tab) {
                    this._validateTab(tab.id, tab.title, tab.templateUrl);
                }.bind(this));

            };

            EditorTabsetService.prototype._validateTab = function(tabId, tabTitle, tabTemplateUrl) {
                if (!tabId) {
                    throw new Error("editorTabsetService.registerTab.invalidTabID");
                }

                if (!tabTitle) {
                    throw new Error("editorTabsetService.registerTab.missingTabTitle");
                }

                if (!tabTemplateUrl) {
                    throw new Error("editorTabsetService.registerTab.missingTemplateUrl");
                }
            };

            /**
             * @ngdoc method
             * @name editorTabsetModule.service:editorTabsetService#deleteTab
             * @methodOf editorTabsetModule.service:editorTabsetService
             *
             * @description
             * Removes a tab from the list of registered tabs.
             *
             * @param {String} tabId The ID of the tab to be removed.
             */
            EditorTabsetService.prototype.deleteTab = function(tabId) {
                var tabIndex = getIndexOfElementByAttr(this.tabsList, 'id', tabId);

                if (tabIndex >= 0) {
                    delete this.tabsList[tabIndex];
                } else {
                    throw new Error("editorTabsetService.deleteTab.tabNotFound");
                }
            };

            return new EditorTabsetService();
        })

        /**
         * @ngdoc directive
         * @name editorTabsetModule.directive:editorTabset
         * @scope
         * @restrict E
         * @element smartedit-editor-tabset
         *
         * @description
         * Directive responsible for displaying and organizing editing forms as tabs within a tabset.
         *
         * The directive allows communication with it via the control object. To do so, it exposes several methods
         * (saveTabs, resetTabs, and cancelTabs). When called, the directive will delegate the corresponding operation to
         * each of the tabs, which are represented internally as SmartEdit yTab directives.
         *
         * @param {Object} control The object that enables communication with the directive itself. It exposes the
         * following methods:
         * @param {Function} control.saveTabs Instructs internal tabs to execute their onSave callback, where they shall
         * validate and persist their changes. If a tab is unable to complete the save operation successfully, the tabset
         * will display an error in the tab's header.
         * @param {Function} control.resetTabs Instructs internal tabs to execute their onReset callback, where they shall
         * discard their modifications and return to a pristine state. It also clears all errors in the tabset.
         * @param {Function} control.cancelTabs Instructs internal tabs to execute their onCancel callback, which allows them to discard
         * their modifications and clean up as necessary. It also clears all errors in the tabset.
         * @param {Boolean} control.isDirty States whether one or more tabs are not in pristine state (e.g., have been modified).
         * @param {Object} data The custom data to pass to each of the individual tabs. When used within the editor modal
         * it must contain component information, such as the componentID and the componentType.
         *
         */
        .directive('editorTabset', function($q, $log, systemEventService, merge, copy) {
            return {
                restrict: 'E',
                transclude: false,
                templateUrl: 'editorTabsetTemplate.html',
                scope: {
                    control: '=',
                    model: '=',
                    tabs: '='
                },
                link: function(scope) {
                    var isDirty = false;

                    var removeTabErrors = function() {
                        for (var tabKey in scope.tabsList) {
                            if (scope.tabsList.hasOwnProperty(tabKey)) {
                                scope.tabsList[tabKey].hasErrors = false;
                            }
                        }
                    };

                    scope.tabs.forEach(function(tab) {
                        _validateTab(tab.id, tab.title, tab.templateUrl);
                    });
                    scope.tabsList = copy(scope.tabs);
                    scope.numTabsDisplayed = 6; //it includes more tab

                    scope.control = {

                        saveTabs: function() {
                            var result = {
                                errorsList: [],
                                item: {},
                                operationSuccessful: true
                            };
                            var deferred = $q.defer();

                            systemEventService.publishAsync(DIALOG_SAVE_EVENT, result)
                                .then(function() {
                                    removeTabErrors();

                                    for (var idx in result.errorsList) {
                                        if (result.errorsList.hasOwnProperty(idx)) {
                                            var tabId = result.errorsList[idx];
                                            var tabIndex = getIndexOfElementByAttr(scope.tabsList, "id", tabId);

                                            if (tabIndex >= 0) {
                                                scope.tabsList[tabIndex].hasErrors = true;
                                            }
                                        }
                                    }

                                    if (result.operationSuccessful) {
                                        deferred.resolve(result.item);
                                    } else {
                                        deferred.reject();
                                    }
                                });

                            return deferred.promise;
                        },

                        resetTabs: function() {
                            return systemEventService.publishAsync(DIALOG_RESET_EVENT)
                                .then(function() {
                                    removeTabErrors();
                                });
                        },

                        cancelTabs: function() {
                            return systemEventService.publishAsync(DIALOG_CANCEL_EVENT)
                                .then(function() {
                                    removeTabErrors();
                                });
                        },

                        isDirty: function() {
                            return isDirty;
                        }
                    };

                    var dirtyBook = {};

                    var onIsDirty = function(eventId, newTabEntry) {
                        dirtyBook[newTabEntry.tabId] = newTabEntry.isDirty;
                        for (var tabId in dirtyBook) {
                            if (dirtyBook[tabId]) {
                                isDirty = true;
                                return $q.when(true);
                            }
                        }
                        isDirty = false;
                        return $q.when(true);
                    };

                    //when outside of a DIALOG_SAVE_EVENT event, an editor with a tab may require to be marked in error
                    systemEventService.subscribe("EDITOR_IN_ERROR_EVENT", function(key, tabId) {
                        var tabIndex = getIndexOfElementByAttr(scope.tabsList, "id", tabId);
                        if (tabIndex >= 0) {
                            scope.tabsList[tabIndex].hasErrors = true;
                        }
                        return $q.when();
                    });


                    var dialogDirtyUnSubscribeFn = systemEventService.subscribe(DIALOG_IS_DIRTY_EVENT, onIsDirty);

                    scope.$on('$destroy', function() {
                        if (dialogDirtyUnSubscribeFn) {
                            dialogDirtyUnSubscribeFn();
                        }
                    });

                    scope.tabControl = function(tabScope) {
                        var onSave = function(event, result) {
                            return $q.when(function() {
                                if (tabScope.onSave) {
                                    return tabScope.onSave()
                                        .then(function(data) {
                                            result.item = merge(result.item, data);
                                        }, function(errResponse) {
                                            if (errResponse) {
                                                if (errResponse.data && errResponse.data.errors) {
                                                    errResponse.data.errors.forEach(function(error) {
                                                        var tabId = error.tabId || tabScope.tabId;

                                                        if (!result.errorsList.includes(tabId)) {
                                                            result.errorsList.push(tabId);
                                                        }
                                                    });
                                                } else {
                                                    result.errorsList.push(tabScope.tabId);
                                                }
                                            }

                                            result.operationSuccessful = false;

                                            return errResponse;
                                        });
                                } else {
                                    $log.warn("Cannot save tab", tabScope.tabId, ". Save callback not defined.");
                                }
                            }());
                        };

                        var onReset = function() {
                            return $q.when(function() {
                                if (tabScope.onReset) {
                                    return tabScope.onReset()
                                        .then(function(data) {
                                            return data;
                                        }, function(error) {
                                            return error;
                                        });
                                } else {
                                    $log.warn("Cannot reset tab", tabScope.tabId, ". Reset callback not defined.");
                                }
                            }());
                        };

                        var onCancel = function() {
                            return $q.when(function() {
                                if (tabScope.onCancel) {
                                    return tabScope.onCancel()
                                        .then(function(data) {
                                            return data;
                                        }, function(error) {
                                            return error;
                                        });
                                } else {
                                    $log.warn("Cannot cancel tab", tabScope.tabId, ". Cancel callback not defined.");
                                }
                            }());
                        };

                        tabScope.$watch(function() {
                            return typeof tabScope.isDirty === 'function' && tabScope.isDirty();
                        }, function(currentDirtyState, oldDirtyState) {
                            if (typeof currentDirtyState === 'boolean' && currentDirtyState !== oldDirtyState) {
                                systemEventService.publishAsync(DIALOG_IS_DIRTY_EVENT, {
                                    tabId: tabScope.tabId,
                                    isDirty: currentDirtyState
                                });
                            }
                        });

                        var dialogSaveUnSubscribeFn = systemEventService.subscribe(DIALOG_SAVE_EVENT, onSave);
                        var dialogResetUnSubscribeFn = systemEventService.subscribe(DIALOG_RESET_EVENT, onReset);
                        var dialogCancelUnSubscribeFn = systemEventService.subscribe(DIALOG_CANCEL_EVENT, onCancel);

                        tabScope.$on('$destroy', function() {
                            if (dialogSaveUnSubscribeFn) {
                                dialogSaveUnSubscribeFn();
                            }
                            if (dialogResetUnSubscribeFn) {
                                dialogResetUnSubscribeFn();
                            }
                            if (dialogCancelUnSubscribeFn) {
                                dialogCancelUnSubscribeFn();
                            }
                        });
                    };

                    function _validateTab(tabId, tabTitle, tabTemplateUrl) {
                        if (!tabId) {
                            throw new Error("editorTabset.invalidTabID");
                        }

                        if (!tabTitle) {
                            throw new Error("editorTabset.missingTabTitle");
                        }

                        if (!tabTemplateUrl) {
                            throw new Error("editorTabset.missingTemplateUrl");
                        }
                    }
                }
            };
        });

    function getIndexOfElementByAttr(arr, attr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i][attr] === value) {
                return i;
            }
        }

        return -1;
    }
})();
