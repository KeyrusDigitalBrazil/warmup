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
module.exports = function() {

    const readline = require('readline');
    const fs = require('fs');
    const lodash = require('lodash');

    class StringPropertiesFileReader {

        readFileSync(file, callback) {
            if (!fs.existsSync(file)) {
                throw `StringPropertiesFileReader.readFile() - file does not exist [${file}]`;
            }

            const fileContent = fs.readFileSync(file, 'utf-8');
            const fileLines = fileContent.split(/\r?\n/);

            const returnObject = {};

            let lineCtr = 0;

            fileLines.forEach(line => {

                ++lineCtr;
                line = lodash.trim(line);

                if (!line.startsWith('#') && line.length !== 0) { // ignore comments and blank lines

                    try {
                        const indexOfEquals = line.indexOf('=');

                        const propertyKey = line.substr(0, indexOfEquals).trim();
                        const propertyValue = line.substr(indexOfEquals + 1).trim();

                        if (indexOfEquals < 0) {
                            throw 'Missing "="';
                        }
                        if (propertyKey.length === 0) {
                            throw 'Invalid property Key';
                        }
                        if (propertyValue.length === 0) {
                            throw 'Invalid property value';
                        }

                        returnObject[propertyKey] = propertyValue;
                    } catch (e) {
                        throw `StringPropertiesFileReader.readFile() - ${file} Line ${lineCtr}: ${e}`;
                    }
                }
            });

            return returnObject;
        }

    }

    return new StringPropertiesFileReader();

}();
