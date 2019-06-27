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
angular.module('seFileMimeTypeServiceModule', [])
    .constant('seFileMimeTypeServiceConstants', {
        VALID_IMAGE_MIME_TYPE_CODES: ['FFD8FFDB', 'FFD8FFE0', 'FFD8FFE1', '474946383761', '424D', '49492A00', '4D4D002A', '89504E470D0A1A0A']
    })
    .factory('seFileReader', function() {
        var read = function(file, config) {
            var fileReader = new FileReader();

            config = config || {};
            fileReader.onloadend = config.onLoadEnd;
            fileReader.onerror = config.onError;

            fileReader.readAsArrayBuffer(file);
            return fileReader;
        };

        return {
            read: read
        };
    })
    .factory('seFileMimeTypeService', function(seFileMimeTypeServiceConstants, seFileReader, $q) {
        var _validateMimeTypeFromFile = function(loadedFile) {
            var fileAsBytes = (new Uint8Array(loadedFile)).subarray(0, 8);
            var header = fileAsBytes.reduce(function(header, byte) {
                var byteAsStr = byte.toString(16);
                if (byteAsStr.length === 1) {
                    byteAsStr = '0' + byteAsStr;
                }
                header += byteAsStr;
                return header;
            }, '');

            return seFileMimeTypeServiceConstants.VALID_IMAGE_MIME_TYPE_CODES.some(function(mimeTypeCode) {
                return header.toLowerCase().indexOf(mimeTypeCode.toLowerCase()) === 0;
            });
        };

        var isFileMimeTypeValid = function(file) {
            var deferred = $q.defer();
            seFileReader.read(file, {
                onLoadEnd: function(e) {
                    if (_validateMimeTypeFromFile(e.target.result)) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                },
                onError: function() {
                    deferred.reject();
                }
            });
            return deferred.promise;
        };

        return {
            isFileMimeTypeValid: isFileMimeTypeValid
        };
    });
