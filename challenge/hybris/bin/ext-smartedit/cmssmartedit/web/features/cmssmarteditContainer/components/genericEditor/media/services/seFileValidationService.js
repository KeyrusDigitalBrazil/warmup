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
 * @name seFileValidationServiceModule
 * @description
 * This module provides the seFileValidationService service, which validates if a specified file meets the required file
 * type and file size constraints of SAP Hybris Commerce.
 */
angular.module('seFileValidationServiceModule', ['seObjectValidatorFactoryModule', 'seFileMimeTypeServiceModule'])

    /**
     * @ngdoc object
     * @name seFileValidationServiceModule.seFileValidationServiceConstants
     * @description
     * The constants provided by the file validation service.
     *
     * <b>ACCEPTED_FILE_TYPES</b>: A list of file types supported by the platform.
     * <b>MAX_FILE_SIZE_IN_BYTES</b>: The maximum size, in bytes, for an uploaded file.
     * <b>I18N_KEYS</b>: A map of all the internationalization keys used by the file validation service.
     */
    .constant('seFileValidationServiceConstants', {
        ACCEPTED_FILE_TYPES: ['jpeg', 'jpg', 'gif', 'bmp', 'tiff', 'tif', 'png'],
        MAX_FILE_SIZE_IN_BYTES: 20 * 1024 * 1024,
        I18N_KEYS: {
            FILE_TYPE_INVALID: 'se.upload.file.type.invalid',
            FILE_SIZE_INVALID: 'se.upload.file.size.invalid'
        }
    })

    /**
     * @ngdoc object
     * @name seFileValidationServiceModule.seFileObjectValidators
     * @description
     * A list of file validators, that includes a validator for file-size constraints and a validator for file-type
     * constraints.
     */
    .factory('seFileObjectValidators', function(seFileValidationServiceConstants) {
        return [{
            subject: 'size',
            message: seFileValidationServiceConstants.I18N_KEYS.FILE_SIZE_INVALID,
            validate: function(size) {
                return size <= seFileValidationServiceConstants.MAX_FILE_SIZE_IN_BYTES;
            }
        }];
    })

    /**
     * @ngdoc service
     * @name seFileValidationServiceModule.seFileValidationService
     * @description
     * The seFileValidationService validates that the file provided is of a specified file type and that the file does not
     * exceed the maxium size limit for the file's file type.
     */
    .factory('seFileValidationService', function(seObjectValidatorFactory, seFileObjectValidators, seFileValidationServiceConstants, seFileMimeTypeService, $q) {
        /**
         * @ngdoc method
         * @name seFileValidationServiceModule.seFileValidationService.buildAcceptedFileTypesList
         * @methodOf seFileValidationServiceModule.seFileValidationService
         * @description
         * Transforms the supported file types provided by the seFileValidationServiceConstants service into a comma-
         * separated list of file type extensions.
         *
         * @returns {String} A comma-separated list of supported file type extensions
         */
        var buildAcceptedFileTypesList = function() {
            return seFileValidationServiceConstants.ACCEPTED_FILE_TYPES.map(function(fileType) {
                return '.' + fileType;
            }).join(',');
        };

        /**
         * @ngdoc method
         * @name seFileValidationServiceModule.seFileValidationService.validate
         * @methodOf seFileValidationServiceModule.seFileValidationService
         * @description
         * Validates the specified file object using validator provided by the {@link
         * seFileValidationServiceModule.seFileObjectValidators seFileObjectValidators} and the file header validator
         * provided by the {@link seFileMimeTypeServiceModule.seFileMimeTypeService seFileMimeTypeService}. It appends the
         * errors to the error context array provided or it creates a new error context array.
         *
         * @param {File} file The web API file object to be validated.
         * @param {Array} context The contextual error array to append the errors to. It is an output parameter.
         * @returns {Promise} A promise that resolves if the file is valid or a list of errors if the promise is rejected.
         */
        var validate = function(file, errorsContext) {
            seObjectValidatorFactory.build(seFileObjectValidators).validate(file, errorsContext);
            return seFileMimeTypeService.isFileMimeTypeValid(file).then(function() {
                return errorsContext.length === 0 ? $q.when() : $q.reject(errorsContext);
            }, function() {
                errorsContext.push({
                    subject: 'type',
                    message: seFileValidationServiceConstants.I18N_KEYS.FILE_TYPE_INVALID
                });
                return $q.reject(errorsContext);
            });
        };

        return {
            buildAcceptedFileTypesList: buildAcceptedFileTypesList,
            validate: validate
        };
    });
