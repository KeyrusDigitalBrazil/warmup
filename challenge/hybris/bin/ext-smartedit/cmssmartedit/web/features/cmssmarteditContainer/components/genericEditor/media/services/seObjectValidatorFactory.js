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
 * @name seObjectValidatorFactoryModule
 * @description
 * This module provides the seObjectValidatorFactory service, which is used to build a validator for a specified list of
 * validator objects.
 */
angular.module('seObjectValidatorFactoryModule', [])

    /**
     * @ngdoc service
     * @name seObjectValidatorFactoryModule.seObjectValidatorFactory
     * @description
     * This service provides a factory method to build a validator for a specified list of validator objects.
     */
    .factory('seObjectValidatorFactory', function() {
        function _validate(validators, objectUnderValidation, errorsContext) {
            errorsContext = errorsContext || [];
            validators.forEach(function(validator) {
                var valueToValidate = objectUnderValidation[validator.subject];
                if (!validator.validate(valueToValidate, objectUnderValidation)) {
                    errorsContext.push({
                        'subject': validator.subject,
                        'message': validator.message
                    });
                }
            });
            return errorsContext.length === 0;
        }

        /**
         * @ngdoc method
         * @name seObjectValidatorFactoryModule.seObjectValidatorFactory.build
         * @methodOf seObjectValidatorFactoryModule.seObjectValidatorFactory
         * @description
         *
         * Builds a new validator for a specified list of validator objects. Each validator object must consist of a
         * parameter to validate, a predicate function to run against the value, and a message to associate with this
         * predicate function's fail case.
         *
         * For example, The resulting validating object has a single validate function that takes two parameters: an object
         * to validate against and a contextual error list to append errors to:
         *
         * <pre>
         * var validators = [{
         *     subject: 'code',
         *     validate: function(code) {
         *         return code !== 'Invalid';
         *     },
         *     message: 'Code must not be "Invalid"'
         * }]
         *
         * var validator = seObjectValidatorFactory.build(validators);
         * var errorsContext = []
         * var objectUnderValidation = {
         *     code: 'Invalid'
         * };
         * var isValid = validate.validate(objectUnderValidation, errorsContext);
         * </pre>
         *
         * The result of the above code block would be that isValid is false beause it failed the predicate function of the
         * single validator in the validator list and the errorsContext would be as follows:
         *
         * <pre>
         * [{
         *     subject: 'code',
         *     message: 'Code must not be "Invalid"'
         * }]
         * </pre>
         *
         * @param {Array} validators A list of validator objects as specified above.
         * @returns {Object} A validator that consists of a validate function, as described above.
         */
        var build = function(validators) {
            return {
                validate: _validate.bind(null, validators)
            };
        };

        return {
            build: build
        };
    });
