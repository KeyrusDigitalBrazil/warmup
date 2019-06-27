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
var fs = require('fs-extra');
var _ = require('lodash');

module.exports = {
    saveResult: function(fileName, title, data) {
        var SEPARATOR = "\n\n------------------------------------------------------------\n\n";
        if (fs.existsSync(fileName)) {
            fs.removeSync(fileName);
        }
        fs.writeFileSync(fileName, title + SEPARATOR + data);
    },
    getLines: function(fileName) {
        return fs.readFileSync(fileName).toString().split('\n');
    },

    /* 
     * assuming here that APIs are consistent in that the array content is homogeneous type-wise,
     * we will only keep one element in the array
     * this element will be a "merge" of all the elements
     */

    squashArraysWithin: function(obj) {

        function squashArray(array) {

            var firstElementType = array[0];
            if (!_.isObject(firstElementType)) {
                return [firstElementType];
            } else {
                return [array.reduce((seed, element) => _.merge(seed, element), {})];
            }
        }

        function convertValue(value) {
            if (_.isNull(value)) {
                return "null";
            } else if (_.isUndefined(value)) {
                return "undefined";
            } else if (_.isArray(value)) {
                return squashArray(value.map((o) => convertValue(o)));
            } else if (_.isObject(value)) {
                for (var key in value) {
                    value[key] = convertValue(value[key]);
                }
                return value;
            } else { //primitive or jquery
                return value;
            }
        }

        return convertValue(obj);
    },
    removeDuplicatesAndMerge: function(signatureList, attributeWithSignatures) {

        return _.uniqBy(signatureList, function(obj) {
                return JSON.stringify(obj[attributeWithSignatures]);
            })
            .map((obj) => obj[attributeWithSignatures])

            .reduce((seedHolder, element) => {
                // element is an arguments array, a result object or a result array
                // if arguments array, only inner arrays are squashed
                // if result itself is an an array, it will have been squashed already + its inner arrays
                if (!seedHolder.initiated) {
                    if (_.isArray(element)) {
                        seedHolder.isArray = true;
                        seedHolder.seed = [];
                    } else {
                        seedHolder.isArray = false;
                        seedHolder.seed = {};
                    }
                }

                // if array, must merge each element of the first array with corresponding index of the other, _.merge does just that
                if (_.isArray(element) || _.isObject(element)) {
                    _.merge(seedHolder.seed, element);
                } else {
                    seedHolder.seed = element;
                }

                return seedHolder;
            }, {
                initiated: false,
                seed: null,
                isArray: false
            }).seed;
    }
};
