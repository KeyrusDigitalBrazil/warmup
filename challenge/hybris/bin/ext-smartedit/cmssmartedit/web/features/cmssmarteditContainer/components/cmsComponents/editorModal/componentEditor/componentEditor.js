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
angular.module('componentEditorModule', ['yLoDashModule', 'componentServiceModule', 'smarteditServicesModule', 'functionsModule'])
    /**
     * @ngdoc object
     * @name componentEditorModule.COMPONENT_EDITOR_CHANGED
     * @description
     * Event is triggered whenever the isDirty state changes. The payload is object {tabId, component}.
     */
    .constant('COMPONENT_EDITOR_TAB_CONTENT_CHANGED_EVENT', 'ComponentEditorTabContentChangedEvent')
    /**
     * @ngdoc service
     * @name componentEditorModule.service:componentEditorService
     *
     * @description
     * Service that aggregates calls from multiple tabs of the editorModalService
     * and makes a call to the ITEMS API when all the data is ready for save or update. 
     * This service is also responsible for loading the initial content into all the tabs when the component is being edited.
     *
     * This service can manage multiple instances associated to different components (the association is made by using the componentId). Therefore,
     * in order to use this service, the `getInstance(componentId)` method must be called in order to get the relevant ComponentEditor instance. The returned
     * instance exposes the different methods described below. An instance can also be discarded (deleted) by calling the `destroyInstance(componentId)` method.
     * 
     * Each tab loaded into the editorModalService will invoke the {@link componentEditorModule.directive:componentEditor componentEditor} component
     * that, on initialization, will register the tab with this service so that any changes made to the tab are recorded by the componentEditorService service.
     */
    .factory('componentEditorService', function($q, lodash, ComponentService) {

        var _instances = {};
        var DEFAULT_ID = "DEFAULT_ID";
        var componentId;
        /**
         * @constructor
         */
        var ComponentEditor = (function() {

            function ComponentEditor() {
                this._tabs = [];
                this._saveTabs = [];
                this._componentInfo = {};
                this._tabDirtyState = {};
                this._fullPayload = {};
                this._partialPayload = {};
                this._deferreds = [];
                this._content = {};
            }

            function _preparePartialPayload(payload, tabId, fields) {
                var filteredPayload = {};

                var tabFields = fields.map(function(field) {
                    return field.qualifier;
                }).filter(function(field) {
                    return Object.keys(payload).indexOf(field) > -1;
                });

                tabFields.forEach(function(field) {
                    filteredPayload[field] = payload[field];
                });

                this._partialPayload[tabId] = lodash.cloneDeep(filteredPayload);
            }

            function _prepareFullPayload(payload) {

                lodash.forEach(this._partialPayload, function(tabContent) {
                    this._fullPayload = lodash.merge(this._fullPayload, tabContent);
                }.bind(this));

                lodash.forEach(payload, function(value, key) {
                    if (!this._fullPayload[key]) {
                        this._fullPayload[key] = payload[key];
                    }
                }.bind(this));
            }

            function _saveTabsData(payload) {
                if (!payload.identifier) {
                    return ComponentService.createNewComponent(this._componentInfo, payload);
                } else {
                    return ComponentService.updateComponent(payload);
                }
            }


            /**
             * @ngdoc method
             * @name componentEditorModule.service:componentEditor#registerTab
             * @methodOf componentEditorModule.service:componentEditorService
             *
             * @description
             * Method used by the {@link componentEditorModule.directive:componentEditor componentEditor} component every time it is invoked by
             * a tab of the editorModalService to register the tab with the service.
             * @deprecated since 6.6 
             * @param {String} tabId The identifier of the tab to be registered.
             * @param {object} componentInfo The component data passed on from the editorModalService 
             *                 that holds the basic information about the component being created or modified.
             */
            ComponentEditor.prototype.registerTab = function(tabId, componentInfo) {
                this._tabs.push(tabId);
                this._componentInfo = (lodash.isEmpty(this._componentInfo)) ? lodash.cloneDeep(componentInfo) : lodash.cloneDeep(this._componentInfo);
            };


            /**
             * @ngdoc method
             * @name componentEditorModule.service:componentEditor#saveTabData
             * @methodOf componentEditorModule.service:componentEditorService
             *
             * @description
             * Method called once for every registered tab when saveTabs of {@link editorTabsetModule.directive:editorTabset editorTabset} is triggered.
             * @deprecated since 6.6
             * The saveTabs of {@link editorTabsetModule.directive:editorTabset editorTabset} will send any asynchronous event to all registered tabs that 
             * will trigger this method. This method will wait until it receives the content of all the tabs that are in dirty state in order to aggregate
             * the content of all said tabs. Afterwards, it will make a POST/PUT call to the ITEMS API, depending on whether componentId is present or not.
             * 
             * @param {Object} partial The content of the tab.
             * @param {String} tabId The identifier of the tab to be registered.
             * @param {Array} fields The fields parameter of the GenericEditor service service scope containing the fields present in the tab.
             */
            ComponentEditor.prototype.saveTabData = function(partial, tabId, fields) {
                //prepare partial payload's for each tab
                _preparePartialPayload.call(this, partial, tabId, fields);
                this._saveTabs.push(tabId);

                //execute save only when tab data of all tabs in dirty states are collected
                if (this._saveTabs.length === Object.keys(this._tabDirtyState).length) {
                    //prepare full payload's containing additional info and then call save
                    _prepareFullPayload.call(this, partial);
                    return _saveTabsData.call(this, this._fullPayload).then(function(response) {
                        var payloads = lodash.cloneDeep(this._partialPayload);
                        this._deferreds.forEach(function(def, index) {
                            def.resolve({
                                payload: payloads[Object.keys(payloads)[index]],
                                response: response
                            });
                        }.bind(this));
                        this.resetTabData();
                        return {
                            payload: partial,
                            response: response
                        };
                    }.bind(this), function(response) {
                        this._deferreds.forEach(function(def) {
                            def.reject(response);
                        });
                        this._deferreds = [];

                        var deferred = $q.defer();
                        deferred.reject(response);
                        this.resetTabData();
                        return deferred.promise;
                    }.bind(this));
                } else {
                    var deferred = $q.defer();
                    this._deferreds.push(deferred);
                    return deferred.promise;
                }
            };

            /**
             * @ngdoc method
             * @name componentEditorModule.service:componentEditor#fetchTabsContent
             * @methodOf componentEditorModule.service:componentEditorService
             *
             * @description
             * Method called when the component is being edited to fetch and load the initial content from the ITEMS API.
             * @deprecated since 6.6
             * @param {String} componentId The identifier of the component being edited to fetch the data.
             */
            ComponentEditor.prototype.fetchTabsContent = function(componentId) {
                if (lodash.isEmpty(this._content)) {
                    this._content = ComponentService.loadComponentItem(componentId);
                }
                return this._content;
            };

            /**
             * @ngdoc method
             * @name componentEditorModule.service:componentEditor#setTabDirtyState
             * @methodOf componentEditorModule.service:componentEditorService
             *
             * @description
             * Method invoked every time the content of a registered tab is modified.
             * Will be responsible for collecting all those registered tabs that are in dirty state (or modified when compared with initial content).
             * @deprecated since 6.6 
             * @param {String} tabId The identifier of the tab to be registered.
             * @param {boolean} isDirty The dirty state of the tab content.
             */
            ComponentEditor.prototype.setTabDirtyState = function(tabId, isDirty) {
                if (isDirty) {
                    this._tabDirtyState[tabId] = isDirty;
                } else {
                    if (this._tabDirtyState[tabId]) {
                        delete this._tabDirtyState[tabId];
                    }
                }
            };

            /**
             * @ngdoc method
             * @name componentEditorModule.service:componentEditor#setTabDirtyState
             * @methodOf componentEditorModule.service:componentEditorService
             *
             * @description
             * Method called to reset the service scope to initial state.
             * @deprecated since 6.6
             */
            ComponentEditor.prototype.resetTabData = function() {
                this._saveTabs = [];
                this._fullPayload = {};
                this._partialPayload = {};
                this._content = {};
            };

            return ComponentEditor;

        })();


        /**
         * @ngdoc method
         * @name componentEditorModule.service:componentEditor#getInstance
         * @methodOf componentEditorModule.service:componentEditorService
         *
         * @description
         * Gets the instance of the `ComponentEditor` that corresponds to the given `componentId`. If no matching instance has been found, a
         * new instance will be created and associated to the given `componentId` If no `componentId` is supplied, an instance associated
         * to `componentId = 0` will be created/returned.
         * 
         * 
         * @param {String} [componentId] componentId The identifier of the associated component
         */
        this.getInstance = function(_componentId) {
            componentId = _componentId || DEFAULT_ID;
            _instances[componentId] = _instances[componentId] || new ComponentEditor();
            return _instances[componentId];
        };


        /**
         * @ngdoc method
         * @name componentEditorModule.service:componentEditor#destroyInstance
         * @methodOf componentEditorModule.service:componentEditorService
         *
         * @description
         * Method called to discard the current instance of `ComponentEditor` associated to a given `componentId`.
         */
        this.destroyInstance = function() {
            delete _instances[componentId];
        };


        return {
            getInstance: this.getInstance,
            destroyInstance: this.destroyInstance
        };

    })
    .controller('componentEditorController', function($q, $scope, componentEditorService, systemEventService, COMPONENT_EDITOR_TAB_CONTENT_CHANGED_EVENT, copy) {
        var componentEditor;
        var componentEditorApi;
        var oldComponentData;

        this._validateStructure = function() {
            if (this.tabStructure && this.structureApi) {
                throw "please provide only one of either structure or structure api";
            } else if (!this.tabStructure && !this.structureApi) {
                throw "neither a structure nor a structure api is provided. The tab cannot be loaded with an empty structure.";
            }
        };

        // on initialization, register tabs and for update mode fetch the initial content
        this.$onInit = function() {
            componentEditor = componentEditorService.getInstance(this.componentId);
            this._validateStructure();
            componentEditor.registerTab(this.tabId, this.componentInfo);
            if (this.componentId) {
                componentEditor.fetchTabsContent(this.componentId).then(function(_content) {
                    this.content = _content;
                    this.isReady = true;
                }.bind(this));
            } else {
                this.content = this.content || {};
                this.isReady = true;
            }
        };

        this.getApi = function($api) {
            componentEditorApi = $api;
        };

        // watch on which tabs are in dirty state and track them
        $scope.$watch(function() {
            var isDirty = typeof this.isDirtyTab === 'function' && this.isDirtyTab();
            return isDirty;
        }.bind(this), function(isDirty) {
            componentEditor.setTabDirtyState(this.tabId, isDirty);
        }.bind(this));

        // override default onSubmit of generic editor to have a single save once all tab data is collected
        this.onSubmit = function() {
            var payload = copy(this.component);
            if (this.isDirty()) {
                payload = this.sanitizePayload(payload, this.fields);
                if (this.smarteditComponentId) {
                    payload.identifier = this.smarteditComponentId;
                }

                return componentEditor.saveTabData(payload, this.id, this.fields);
            }
            return $q.when({
                payload: payload,
                response: payload
            });
        };

        this.$doCheck = function() {
            if (componentEditorApi) {
                if (oldComponentData !== angular.toJson(componentEditorApi.getContent())) {
                    oldComponentData = angular.toJson(componentEditorApi.getContent());
                    systemEventService.publishAsync(COMPONENT_EDITOR_TAB_CONTENT_CHANGED_EVENT, {
                        tabId: this.tabId,
                        component: componentEditorApi.getContent()
                    });
                }
            }
        };

        this.$onDestroy = function() {
            componentEditor.resetTabData();
            componentEditorService.destroyInstance();
        }.bind(this);
    })

    /**
     * @ngdoc directive
     * @name componentEditorModule.directive:componentEditor
     * @scope
     * @restrict E
     * @element component-editor
     *
     * @description
     * Angular component responsible for loading the structure and the content of the tab that invokes this component into the generic-editor directive.
     * More specifically, this component acts as a bridge between the independent tabs that are part of an editorTabset in order to consolidate the 
     * tabs' calls (GET/POST/PUT) to the content API into one single call. This is particularly relevant when the different tabs use the same content 
     * API endpoint.
     *
     * Every tab of the editorModalService must invoke the componentEditor so that this component will
     * delegate the structure/structureAPI provided to the generic editor to display the tab info.
     * 
     * This component requires either one of tabStructure or structureApi to load the structure within the tab else an error is thrown.
     *
     * @param {Function} saveTab saveTabs passed on from {@link editorTabsetModule.directive:editorTabset editorTabset} that instructs the tab to execute the onSave callback when save button is clicked.
     * @param {Function} resetTab resetTabs passed on from {@link editorTabsetModule.directive:editorTabset editorTabset} that instructs the tab to execute its onReset callback when reset button is clicked.
     * @param {Function} cancelTab cancelTabs passed on from {@link editorTabsetModule.directive:editorTabset editorTabset} that instructs the tab to execute its onCancel callback when cancel icon is clicked.
     * @param {Boolean} isDirtyTab States whether the tab is in pristine state or not (e.g., have been modified).
     * @param {String} componentId The Universally unique identifier of the component being created/modified.
     * @param {String} componentType The typeCode of the component being created/modified.
     * @param {String} tabId The identifier of the tab (is the identifier that is passed on to the generic editor).
     * @param {<Object} componentInfo The component data that holds the basic information about the component being created or modified. 
     * @param {String} componentInfo.componenCode componentCode of the ComponentType to be created and added to the slot.
     * @param {String} componentInfo.name name of the new component to be created.
     * @param {String} componentInfo.pageId pageId used to identify the current page template.
     * @param {String} componentInfo.slotId slotId used to identify the slot in the current template.
     * @param {String} componentInfo.position position used to identify the position in the slot in the current template.
     * @param {String} componentInfo.type type (typeCode) of the component being created.
     * @param {<Object=} tabStructure An optional parameter. The structure of the tab to be displayed.
     * @param {String=} tabStructure.cmsStructureType Value that is used to determine which form widget (property editor) to display for a specified property.
     * @param {String=} tabStructure.qualifier Name of the property.
     * @param {String=} tabStructure.prefixText Text to display before the property in the editor.
     * @param {String=} tabStructure.labelText Text to display after the property in the editor.
     * @param {Boolean=} tabStructure.editable Set to false in order to make the field uneditable.
     * @param {String=} tabStructure.i18nKey The i18nKey corresponding to the property's label.
     * @param {<String=} structureApi An optional parameter. The data binding to a REST Structure API that fulfills the contract described in the  GenericEditor service service.
     * @param {<String=} content The model that is passed on to the generic editor
     */
    .component('componentEditor', {
        transclude: false,
        templateUrl: 'componentEditorTemplate.html',
        controller: 'componentEditorController',
        controllerAs: 'compCtrl',
        bindings: {
            saveTab: '=',
            resetTab: '=',
            cancelTab: '=',
            isDirtyTab: '=',
            componentId: '=',
            componentType: '=',
            tabId: '=',
            componentInfo: '<',
            tabStructure: '<?',
            structureApi: '<?',
            content: '<?'
        }
    });
