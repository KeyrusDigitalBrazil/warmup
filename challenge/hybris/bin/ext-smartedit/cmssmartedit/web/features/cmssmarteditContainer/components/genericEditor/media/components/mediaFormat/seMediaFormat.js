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
angular.module('seMediaFormatModule', ['mediaServiceModule', 'seFileSelectorModule', 'seFileValidationServiceModule', 'cmsSmarteditServicesModule'])
    .constant('seMediaFormatConstants', {
        I18N_KEYS: {
            UPLOAD: 'se.media.format.upload',
            REPLACE: 'se.media.format.replace',
            UNDER_EDIT: 'se.media.format.under.edit',
            REMOVE: 'se.media.format.remove'
        },
        UPLOAD_ICON_URL: '/images/upload_image.png',
        UPLOAD_ICON_DIS_URL: '/images/upload_image_disabled.png',
        DELETE_ICON_URL: '/images/remove_image_small.png',
        REPLACE_ICON_URL: '/images/replace_image_small.png',
        ADV_INFO_ICON_URL: '/images/info_image_small.png'
    })
    .controller('seMediaFormatController', function(mediaService, seMediaFormatConstants, seFileValidationServiceConstants, assetsService, $scope) {
        this.i18nKeys = seMediaFormatConstants.I18N_KEYS;
        this.acceptedFileTypes = seFileValidationServiceConstants.ACCEPTED_FILE_TYPES;

        var assetsRoot = assetsService.getAssetsRoot();
        this.uploadIconUrl = assetsRoot + seMediaFormatConstants.UPLOAD_ICON_URL;
        this.uploadIconDisabledUrl = assetsRoot + seMediaFormatConstants.UPLOAD_ICON_DIS_URL;

        this.deleteIconUrl = assetsRoot + seMediaFormatConstants.DELETE_ICON_URL;
        this.replaceIconUrl = assetsRoot + seMediaFormatConstants.REPLACE_ICON_URL;
        this.advInfoIconUrl = assetsRoot + seMediaFormatConstants.ADV_INFO_ICON_URL;

        this.fetchMedia = function() {
            mediaService.getMedia(this.mediaUuid).then(function(val) {
                this.media = val;
            }.bind(this));
        };

        this.isMediaCodeValid = function() {
            return this.mediaUuid && typeof this.mediaUuid === 'string';
        };

        this.isMediaPreviewEnabled = function() {
            return this.isMediaCodeValid() && !this.isUnderEdit && this.media && this.media.code;
        };

        this.isMediaEditEnabled = function() {
            return !this.isMediaCodeValid() && !this.isUnderEdit;
        };

        this.getErrors = function() {
            return (this.field.messages || []).filter(function(error) {
                return error.format === this.mediaFormat;
            }.bind(this)).map(function(error) {
                return error.message;
            });
        };

        this.$onInit = function() {
            if (this.isMediaCodeValid()) {
                this.fetchMedia();
            }
            this.mediaFormatI18NKey = "se.media.format." + this.mediaFormat;
        };

        $scope.$watch(function() {
            return this.mediaUuid;
        }.bind(this), function(mediaUuid, oldMediaUuid) {
            if (mediaUuid && typeof mediaUuid === 'string') {
                if (mediaUuid !== oldMediaUuid) {
                    this.fetchMedia();
                }
            } else {
                this.media = {};
            }
        }.bind(this));

    })
    .component('seMediaFormat', {
        templateUrl: 'seMediaFormatTemplate.html',
        controller: 'seMediaFormatController',
        controllerAs: 'ctrl',
        require: {
            geField: '^^genericEditorField'
        },
        bindings: {
            mediaUuid: '<',
            mediaFormat: '<',
            isUnderEdit: '<',
            field: '<',
            onFileSelect: '&?',
            onDelete: '&?'
        }
    });
