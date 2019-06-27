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
/**
 * @ngdoc overview
 * @name genericEditorModalServiceModule
 * @description
 * # The genericEditorModalServiceModule
 *
 * The generic editor modal service module provides a service to open an editor modal window with a tabset inside. The editor modal is populated with a save
 * and cancel button, and allows specifying the different editor tabs.
 *
 */
angular.module('genericEditorModalServiceModule', [
        'confirmationModalServiceModule',
        'coretemplates',
        'editorTabsetModule',
        'smarteditServicesModule',
        'modalServiceModule',
        'yLoDashModule',
        'genericEditorModule',
        'functionsModule'
    ])

    /**
     * @ngdoc service
     * @name genericEditorModalServiceModule.service:genericEditorModalService
     *
     * @description
     * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
     *
     */
    .factory('genericEditorModalService', function(modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, TYPES_RESOURCE_URI, CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, CONTEXT_SITE_ID, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, confirmationModalService) {
        function GenericEditorModalService() {}

        /**
         * @ngdoc method
         * @name genericEditorModalServiceModule.service:genericEditorModalService#open
         * @methodOf genericEditorModalServiceModule.service:genericEditorModalService
         *
         * @description
         * Function that opens an editor modal. For this method, you must specify the list of tabs to be displayed in the tabset, an object to contain the edited information, and a save
         * callback that will be triggered once the Save button is clicked.
         *
         * @param {Object} data The object that contains the information to be displayed and edited in the modal.
         * @param {Object=} data.componentUuid the smartEdit component uuid of the entity to edit, empty when creating
         * @param {Object} data.componentType the smartEdit component type of the entity to edit
         * @param {Object=} data.content the optional initial content, especially in create mode
         * @param {String=} data.structure the optional data binding to a REST Structure JSON; defaults to the Structure API when not provided.
         * @param {String=} data.contentApi the optional REST API used to create, read, update, or delete content; defaults to the CMSItem API when not provided.
         * @param {String=} data.targetedQualifier the name of the qualifier the tab of which we want to initialize the editor with
         * @param {Object} tabs The list of tabs to be displayed in the tabset. Note that each of the tabs must follow the contract specified by the {@link editorTabsetModule.directive:editorTabset editor tabset}.
         * @deprectated since 6.6 : ignored, spreading accross tabs is now controlled by {@link genericEditorServicesModule.service:editorFieldMappingService#addFieldTabMapping editorFieldMappingService#addFieldTabMapping}
         * @param {String} tabs.id The ID of the current tab.
         * @param {String} tabs.title The localization key of the title to be displayed for the current tab.
         * @param {String} tabs.templateUrl The URL of the HTML template to be displayed within the current tab.
         * @param {Object} saveCallback a function executed if the user clicks the Save button and the modal closes successfully.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        GenericEditorModalService.prototype.open = function(data, tabs, saveCallback, errorCallback) {

            return modalService.open({
                title: data.title,
                titleSuffix: 'se.cms.editor.title.suffix',
                templateUrl: 'genericEditorModalTemplate.html',
                controller: ['modalManager', 'systemEventService', '$scope', '$log', '$q', 'lodash', 'generateIdentifier',
                    function(modalManager, systemEventService, $scope, $log, $q, lodash, generateIdentifier) {
                        this.isDirty = false;
                        this.controls = {};
                        this.editorStackId = data.editorStackId;
                        this.data = lodash.cloneDeep(data);
                        this.genericEditorId = generateIdentifier();

                        this.getApi = function(genericEditorAPI) {
                            this.controls.genericEditorAPI = genericEditorAPI;
                            if (this.data.targetedQualifier) {
                                genericEditorAPI.switchToTabContainingQualifier(this.data.targetedQualifier);
                            }
                            if (this.data.initialDirty) {
                                genericEditorAPI.considerFormDirty();
                            }
                        }.bind(this);

                        this.onSave = function() {
                            return this.controls.submit().then(function(item) {
                                saveCallback(item);
                                this.removeEventListeners();
                                return item;
                            }.bind(this));
                        };

                        this.onCancel = function() {
                            var deferred = $q.defer();
                            if (this.isDirty) {
                                confirmationModalService.confirm({
                                    description: 'se.editor.cancel.confirm'
                                }).then(function() {
                                    this.controls.reset().then(function() {
                                        this.removeEventListeners();
                                        deferred.resolve();
                                    }.bind(this), function() {
                                        deferred.reject();
                                    });
                                }.bind(this), function() {
                                    deferred.reject();
                                });
                            } else {
                                this.removeEventListeners();
                                deferred.resolve();
                            }

                            return deferred.promise;
                        };

                        this.init = function() {
                            modalManager.setDismissCallback(this.onCancel.bind(this));

                            modalManager.setButtonHandler(function(buttonId) {
                                switch (buttonId) {
                                    case 'save':
                                        return this.onSave();
                                    case 'cancel':
                                        return this.onCancel();
                                    default:
                                        $log.error('A button callback has not been registered for button with id', buttonId);
                                        break;
                                }
                            }.bind(this));

                            $scope.$watch(function() {
                                var isDirty = typeof this.controls.isDirty === 'function' && this.controls.isDirty();
                                var isSubmitDisabled = this.controls.genericEditorAPI && this.controls.genericEditorAPI.isSubmitDisabled();
                                return [isDirty, isSubmitDisabled];
                            }.bind(this), function(array) {
                                this.isDirty = array[0];
                                var isSubmitDisabled = array[1];
                                if (isSubmitDisabled) {
                                    modalManager.disableButton('save');
                                } else {
                                    modalManager.enableButton('save');
                                }
                            }.bind(this), true);


                            this.structure = data.structure;
                            if (!this.structure) {
                                this.structureApi = this.getStructureApiByMode('DEFAULT');
                            }
                            this.changeStructureEventListener = systemEventService.subscribe(CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, this.onChangeStructureEvent.bind(this));

                            this.unrelatedValidationErrorsEvent = systemEventService.subscribe(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, this.onUnrelatedErrors.bind(this));

                            this.contentApi = data.contentApi || '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/cmsitems';
                        };

                        this.onChangeStructureEvent = function(eventId, payload) {
                            // Structure is reloaded only for the Generic Editor that owns the component where this event came from.
                            // This is done to avoid contaminating editors in a nested set-up.
                            if (this.genericEditorId === payload.editorId) {
                                if (payload.structureApiMode) {
                                    this.structure = null;
                                    this.structureApi = this.getStructureApiByMode(payload.structureApiMode);
                                } else if (payload.structure) {
                                    this.structureApi = null;
                                    this.structure = payload.structure;
                                }
                                this.data.content = payload.content;
                            }
                        };

                        this.onUnrelatedErrors = function(eventId, eventData) {
                            if (eventData.sourceGenericEditorId === data.componentUuid && errorCallback) {
                                errorCallback(eventData.messages, this);
                            }
                        };

                        var STRUCTURE_API_BASE_URL = TYPES_RESOURCE_URI + '?code=:smarteditComponentType&mode=:structureApiMode';

                        this.getStructureApiByMode = function(structureApiMode) {
                            return STRUCTURE_API_BASE_URL.replace(/:structureApiMode/gi, structureApiMode);
                        };

                        this.removeEventListeners = function() {
                            this.unrelatedValidationErrorsEvent();
                            this.changeStructureEventListener();
                        };
                    }
                ],
                buttons: [{
                    id: 'cancel',
                    label: 'se.cms.component.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'save',
                    label: 'se.cms.component.confirmation.modal.save',
                    action: MODAL_BUTTON_ACTIONS.CLOSE,
                    disabled: true
                }]
            });
        };

        return new GenericEditorModalService();

    });
