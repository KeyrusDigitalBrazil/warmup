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
angular.module('seMediaUploadFormModule', [
        'seFileValidationServiceModule',
        'seMediaServiceModule',
        'seObjectValidatorFactoryModule',
        'seBackendValidationHandlerModule',
        'seMediaUploadFieldModule',
        'functionsModule',
        'cmsSmarteditServicesModule'
    ])
    .constant('seMediaUploadFormConstants', {
        MAX_FILENAME_CHARACTERS_DISPLAYED: 15,
        I18N_KEYS: {
            UPLOAD_IMAGE_CANCEL: 'se.upload.image.cancel',
            UPLOAD_IMAGE_SUBMIT: 'se.upload.image.submit',
            UPLOAD_IMAGE_REPLACE: 'se.upload.image.replace',
            UPLOAD_IMAGE_TO_LIBRARY: 'se.upload.image.to.library',
            DESCRIPTION: 'se.uploaded.image.description',
            CODE: 'se.uploaded.image.code',
            ALT_TEXT: 'se.uploaded.image.alt.text',
            UPLOADING: 'se.uploaded.is.uploading',
            CODE_IS_REQUIRED: 'se.uploaded.image.code.is.required'
        }
    })
    .factory('seMediaUploadFormValidators', function(seMediaUploadFormConstants) {
        return [{
            subject: 'code',
            message: seMediaUploadFormConstants.I18N_KEYS.CODE_IS_REQUIRED,
            validate: function(code) {
                return !!code;
            }
        }];
    })
    .controller('seMediaUploadFormController', function(seFileValidationServiceConstants, seMediaService, seMediaUploadFormConstants, seObjectValidatorFactory, seMediaUploadFormValidators, seBackendValidationHandler, escapeHtml, assetsService, $scope) {
        this.i18nKeys = seMediaUploadFormConstants.I18N_KEYS;
        this.acceptedFileTypes = seFileValidationServiceConstants.ACCEPTED_FILE_TYPES;

        this.imageParameters = {};
        this.isUploading = false;
        this.fieldErrors = [];

        this.assetsRoot = assetsService.getAssetsRoot();

        // TODO replace with this.$onChanges in Angular 1.5
        $scope.$watch(function() {
            return this.image;
        }.bind(this), function() {
            if (this.image && this.image.file) {
                this.imageParameters = {
                    code: this.image.file.name,
                    description: this.image.file.name,
                    altText: this.image.file.name
                };
            }
        }.bind(this));

        this.onCancel = function() {
            this.imageParameters = {};
            this.fieldErrors = [];
            this.isUploading = false;
            this.onCancelCallback();
        };

        this.onImageUploadSuccess = function(response) {
            this.imageParameters = {};
            this.fieldErrors = [];
            this.isUploading = false;
            this.onUploadCallback({
                uuid: response.uuid
            });
        };

        this.onImageUploadFail = function(response) {
            this.isUploading = false;
            seBackendValidationHandler.handleResponse(response, this.fieldErrors);
        };

        this.onMediaUploadSubmit = function() {
            this.fieldErrors = [];
            var validator = seObjectValidatorFactory.build(seMediaUploadFormValidators);
            if (validator.validate(this.imageParameters, this.fieldErrors)) {
                this.isUploading = true;

                seMediaService.uploadMedia({
                    file: this.image.file,
                    code: escapeHtml(this.imageParameters.code),
                    description: escapeHtml(this.imageParameters.description),
                    altText: escapeHtml(this.imageParameters.altText)
                }).then(this.onImageUploadSuccess.bind(this), this.onImageUploadFail.bind(this));
            }
        };

        this.getErrorsForField = function(field) {
            return this.fieldErrors.filter(function(error) {
                return error.subject === field;
            }).map(function(error) {
                return error.message;
            });
        };

        this.hasError = function(field) {
            return this.fieldErrors.some(function(error) {
                return error.subject === field;
            });
        };

        this.getTruncatedName = function() {
            var truncatedName = this.image && this.image.file && this.image.file.name || '';
            if (truncatedName.length > seMediaUploadFormConstants.MAX_FILENAME_CHARACTERS_DISPLAYED) {
                truncatedName = truncatedName.substring(0, seMediaUploadFormConstants.MAX_FILENAME_CHARACTERS_DISPLAYED);
                truncatedName += '...';
            }
            return truncatedName;
        };
    })
    .directive('seMediaUploadForm', function() {
        return {
            templateUrl: 'seMediaUploadFormTemplate.html',
            restrict: 'E',
            scope: {},
            controller: 'seMediaUploadFormController',
            controllerAs: 'ctrl',
            bindToController: {
                field: '<',
                image: '<',
                onUploadCallback: '&',
                onCancelCallback: '&',
                onSelectCallback: '&'
            }
        };
    });
