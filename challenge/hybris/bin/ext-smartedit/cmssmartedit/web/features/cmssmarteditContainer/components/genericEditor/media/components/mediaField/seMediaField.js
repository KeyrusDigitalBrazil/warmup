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
angular.module('seMediaFieldModule', [
        'seMediaSelectorModule',
        'seMediaUploadFormModule',
        'seFileValidationServiceModule',
        'seErrorsListModule',
        'cmsSmarteditServicesModule'
    ])
    .factory('seMediaFieldConstants', function(assetsService) {
        var assetsRoot = assetsService.getAssetsRoot();

        return {
            I18N_KEYS: {
                UPLOAD_IMAGE_TO_LIBRARY: 'se.upload.image.to.library'
            },
            UPLOAD_ICON_URL: assetsRoot + '/images/upload_image.png'
        };
    })
    .controller('seMediaFieldController', function(seMediaFieldConstants, seFileValidationServiceConstants, seFileValidationService) {

        this.$onInit = function() {
            this.i18nKeys = seMediaFieldConstants.I18N_KEYS;
            this.acceptedFileTypes = seFileValidationServiceConstants.ACCEPTED_FILE_TYPES;
            this.uploadIconUrl = seMediaFieldConstants.UPLOAD_ICON_URL;
            this.image = {};
            this.fileErrors = [];
        };

        this.fileSelected = function(files, format) {
            this.resetImage();
            if (files.length === 1) {
                seFileValidationService.validate(files[0], this.fileErrors).then(function() {
                    this.image = {
                        file: files[0],
                        format: format || this.image.format
                    };
                }.bind(this));
            }
        };

        this.resetImage = function() {
            this.fileErrors = [];
            this.image = {};
        };

        this.imageUploaded = function(uuid) {
            this.resetImage();
            this.model[this.qualifier] = uuid;
            if (this.field.initiated) {
                this.field.initiated.length = 0;
            }
        };

        this.showFileSelector = function() {
            //enable file selector only if model exists but field is not set
            return this.model && !this.model[this.qualifier] && !this.image.file;
        };
    })
    .directive('seMediaField', function() {
        return {
            templateUrl: 'seMediaFieldTemplate.html',
            restrict: 'E',
            controller: 'seMediaFieldController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                field: '<',
                model: '<',
                editor: '<',
                qualifier: '<'
            }
        };
    });
