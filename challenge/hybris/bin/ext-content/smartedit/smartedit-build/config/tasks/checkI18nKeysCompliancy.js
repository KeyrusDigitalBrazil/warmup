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
/*jshint loopfunc:true */
module.exports = function(grunt) {

    /**
     * @ngdoc overview
     * @name checkI18nKeysCompliancy(T)
     * @description
     * # checkI18nKeysCompliancy Task
     * checkI18nKeysCompliancy is a task designed to detect localization errors at compile time.
     * - Make sure all the localization properties files are properly formatted
     * - Make sure that all i18n keys have the expected prefix(es)
     * - Make sure that any keys used in the code, exist in the properties file(s)
     *
     * # Configuration
     * The configuration object should be as follows:
     * ```js
     * {
     *     prefix: {
     *         expected: string[],  // contains prefixes expected on all detected i18n keys
     *         ignored: string[]    // whitelist prefixes for keys to be ignored
     *     },
     *     paths: {
     *         files: string[], // glob pattern of source files to be scanned for errors
     *         properties: string[]   // list of i18n property files
     *     }
     * };
     * ```
     */


    const taskName = "checkI18nKeysCompliancy";

    grunt.registerTask(
        taskName,
        "Compare i18n keys parsed from JS/HTML sources with properties files content",
        function() {

            /* ----------
             [ variables ]
             ---------- */

            var gruntConfig = grunt.config.get(taskName);
            var regExs = [
                // usages for Angular attribute "data-translate"
                /(data-translate=)[\\]?["']([\w]+[\.])+[\w]+[\\]?["']/g,
                // usages for Angular filter "| translate"
                /["']([\w]+[\.])+[\w]+["'](\s?\|\s?translate)/g,
                // usages for Angular service "$translate"
                /(\$translate)(.instant)?\(["']([\w]+[\.])+[\w]+["']/g,
                // usages for HTML tag "<translate>"
                /<translate>[\s]*([\w]+[\.])+[\w]+[\s]*<\/translate>/g
            ];

            var files = {
                    sources: {},
                    i18n: {}
                },
                output = {
                    i18n: {},
                    sources: {
                        proper_prefix_and_properly_translated: [],
                        proper_prefix_and_no_translation_available: [],
                        invalid_prefix_and_properly_translated: [],
                        invalid_prefix_and_no_translation_available: []
                    }
                },
                fileName = '',
                foundKeys = [],
                foundKeysPerFile = {},
                processedKeys = [];

            var returnFileName = function(filePath) {
                return filePath.split("/").pop();
            };
            var singularOrPlurial = function(array) {
                return (array.length > 1) ? "s" : "";
            };

            /* ------------------
             [ file conversions ]
             ----------------- */

            /* converting properties files into temporary json file  */
            var convertPropertiesToJson = function() {

                grunt.file.expand(gruntConfig.paths.properties).forEach(function(filePath) {

                    fileName = returnFileName(filePath);

                    if (filePath.split(".").pop() !== "properties") {
                        grunt.fail.fatal("Can only convert file of '.properties' type.");
                    } else {

                        files.i18n[fileName] = {};
                        output.i18n[fileName] = {
                            duplicated_keys: [],
                            missing_keys: [],
                            missing_values: [],
                            not_one_equal_signs: []
                        };

                        // creating json from properties
                        grunt.file.read(filePath).split("\n").forEach(function(row, index) {

                            // checking all rows except the commented or empty ones
                            if (row.indexOf("#") !== 0 && row !== "") {

                                // missing keys
                                if (row.indexOf("=") === 0) {
                                    output.i18n[fileName].missing_keys.push((index + 1) + "|'" + row + "'");
                                }
                                // missing values
                                else if (row.indexOf("=") === (row.length - 1)) {
                                    output.i18n[fileName].missing_values.push((index + 1) + "|'" + row + "'");
                                }
                                // multiple equal signs
                                else if ((row.match(/=/g) || []).length !== 1) {
                                    output.i18n[fileName].not_one_equal_signs.push((index + 1) + "|'" + row + "'");
                                } else {
                                    row = row.split("=");
                                    // duplicated keys
                                    if (row[0] in files.i18n[fileName]) {
                                        output.i18n[fileName].duplicated_keys.push((index + 1) + "|'" + row + "'");
                                    }
                                    // validated key
                                    else {
                                        files.i18n[fileName][row[0]] = row[1];
                                    }
                                }

                            }

                        });

                    }

                });

            };

            /* saving file content as array of rows for each file */
            var convertSourcesToArray = function() {
                grunt.file.expand(gruntConfig.paths.files).forEach(function(filePath) {
                    files.sources[returnFileName(filePath)] = grunt.file.read(filePath).split("\n");
                });
            };

            /* -----------------------
             [ checking source files ]
             ---------------------- */

            // using defined regular expressions to report all i18n keys from sources files
            var checkSourceI18nCompliancy = function() {

                /* [ variables ] */

                var keyProperlyPrefixed = false,
                    translationStatus = "no_translation_available";

                /* [ private methods ] */

                // parsing i18n keys out of the regexp returned results
                var _cleaningFoundKey = function(foundKey) {
                    [/\\/g, /'/g, /"/g, /\|/g, /=/g, / /g, "data-", "<translate>", "</translate>", "translate", "\$", ".instant", "(", ")"].forEach(function(character) {
                        foundKey = foundKey.replace(character, "");
                    });
                    return foundKey.trim();
                };

                var _returnSourceReport = function(key) {

                    var sourceReport = ["'" + key + "':"],
                        foundIndexes;

                    for (var fileName in foundKeysPerFile) {

                        foundIndexes = [];

                        foundKeysPerFile[fileName].forEach(function(foundKeys, index) {
                            if (foundKeys.indexOf(key) !== -1) {
                                foundIndexes.push(index);
                            }
                        });

                        if (foundIndexes.length !== 0) {
                            sourceReport.push("- " + fileName + ":" + foundIndexes.join(", "));
                        }

                    }

                    return sourceReport;

                };

                var _updateOutputSources = function(errorType, key) {
                    output.sources[errorType].push(_returnSourceReport(key).join("\n"));
                }.bind(this);

                /* [ main ] */

                for (var fileName in files.sources) {

                    foundKeysPerFile[fileName] = [];

                    files.sources[fileName].forEach(function(row, rowIndex) {

                        regExs.forEach(function(regEx) {
                            processedKeys = row.match(regEx);
                            if (processedKeys !== null) {

                                for (var i = 0; i < processedKeys.length; i++) {
                                    // removing any reference to Angular controller found in html template
                                    if (processedKeys[i][0] === "$") {
                                        processedKeys.splice(i, 1);
                                        i--;
                                    } else {
                                        processedKeys[i] = _cleaningFoundKey(processedKeys[i]);
                                    }
                                }

                                foundKeys = foundKeys.concat(processedKeys);
                                foundKeysPerFile[fileName][rowIndex + 1] = "|" + processedKeys.join("|") + "|";

                            }
                        }.bind(this));

                    });

                }

                // removing duplicates and sorting keys
                foundKeys = Array.from(new Set(foundKeys)).sort();

                foundKeys.forEach(function(key) {

                    // invalid or missing translation
                    keyProperlyPrefixed = false;

                    for (var option in gruntConfig.prefix) {
                        gruntConfig.prefix[option].forEach(function(prefix) {
                            if (key.indexOf(prefix) === 0) {
                                keyProperlyPrefixed = true;
                            }
                        });
                    }

                    // checking whether a translation has been added for the found i18n key
                    translationStatus = "no_translation_available";

                    for (fileName in files.i18n) {
                        if (files.i18n[fileName].hasOwnProperty(key) &&
                            typeof files.i18n[fileName][key] === "string" &&
                            files.i18n[fileName][key].length !== 0) {
                            translationStatus = "properly_translated";
                            break;
                        }
                    }

                    _updateOutputSources(
                        (keyProperlyPrefixed) ?
                        "proper_prefix_and_" + translationStatus :
                        "invalid_prefix_and_" + translationStatus,
                        key
                    );

                }.bind(this));

            };

            /* ---------------------------
             [ checking properties files ]
             -------------------------- */

            // reporting conformity of each translation with properties files
            var checkPropertiesI18nCompliancy = function() {

                /* [ variable ] */

                var prefixStatus = "";

                /* [ private method ] */

                // updating output reports with i18n key being processed and line indexes
                this._updateOutputI18n = function(fileName, errorType, key, index) {
                    output.i18n[fileName][errorType].push("l." + index + ": " + key);
                };

                /* [ main ] */

                for (var fileName in files.i18n) {

                    // initializing output reports for each file being processed
                    output.i18n[fileName].not_properly_prefixed = []

                    Object.getOwnPropertyNames(files.i18n[fileName]).forEach(function(key, index) {

                        // prefix can be expected ('se.cms.' or 'extensionname.') and ignored ('se.', back-end provided keys)
                        prefixStatus = "not_properly_prefixed";
                        for (var option in gruntConfig.prefix) {
                            gruntConfig.prefix[option].forEach(function(prefix) {
                                if (key.indexOf(prefix) === 0) {
                                    prefixStatus = "prefixed";
                                }
                            });
                        }

                        if (prefixStatus === "not_properly_prefixed") {
                            // reporting whether i18n key is being properly prefixed
                            this._updateOutputI18n(fileName, prefixStatus, key, (index + 1));
                        }

                    }.bind(this));

                }

            };


            /* -------------------
             [ publishing report ]
             ------------------ */

            var publishReport = function() {

                /* [ variables ] */

                var objectKeys,
                    report = "";

                /* [ private methods ] */

                // properties
                var _publishPropertiesOutputLog = function() {

                    for (var fileName in output.i18n) {

                        var numberOfIssues = 0;

                        for (var errorType in output.i18n[fileName]) {
                            numberOfIssues += output.i18n[fileName][errorType].length;
                        };

                        // publishing name of invalid properties file
                        if (numberOfIssues > 0) {
                            grunt.log.writeln(fileName + ": " + numberOfIssues + " issue" + ((numberOfIssues > 1) ? "s" : ""));
                        }

                        // report for each available status
                        for (var status in output.i18n[fileName]) {

                            switch (status) {

                                case "not_properly_prefixed":
                                    if (output.i18n[fileName][status].length > 0) {
                                        report = "> " + status.replace(/_/g, " ") + ": " + output.i18n[fileName][status].length + " key" + singularOrPlurial(output.i18n[fileName][status]);
                                        grunt.log.writeln(report);
                                        grunt.log.writeln("- " + output.i18n[fileName][status].join("\n"));
                                    }
                                    break;

                                default:
                                    if (output.i18n[fileName][status].length > 0) {
                                        report = "> " + status.replace(/_/g, " ") + ": " + output.i18n[fileName][status].length + " occurence" + singularOrPlurial(output.i18n[fileName][status]);
                                        grunt.log.writeln(report);
                                        output.i18n[fileName][status].forEach(function(data) {
                                            if (status === "empty_rows") {
                                                grunt.log.writeln("- l." + data);
                                            } else {
                                                data = data.split("|");
                                                grunt.log.writeln("- l." + data[0] + ": " + data[1]);
                                            }

                                        });
                                    }

                            }

                        }
                    }

                };

                // sources
                var _publishSourceOutputLog = function() {

                    for (var type in output.sources) {

                        var report = "> " + type.replace(/_/g, " ") + ": " + output.sources[type].length + " key" + singularOrPlurial(output.sources[type]) + "";

                        // print out for each status type
                        if (output.sources[type].length > 0) {
                            switch (type) {
                                case "proper_prefix_and_no_translation_available":
                                case "invalid_prefix_and_properly_translated":
                                case "invalid_prefix_and_no_translation_available":
                                    grunt.log.writeln(report);
                                    output.sources[type].forEach(function(report) {
                                        grunt.log.writeln(report);
                                    });
                                    break;
                            }
                        }

                    }
                };

                /* [ main ] */

                // i18n

                var issueInI18n = false;

                for (var fileName in output.i18n) {
                    if (!issueInI18n) {
                        for (errorType in output.i18n[fileName]) {
                            if (!issueInI18n) {
                                issueInI18n = (output.i18n[fileName][errorType].length !== 0);
                            }
                        }
                    }
                }

                grunt.log.writeln("\n>> i18n:");
                if (issueInI18n) {
                    _publishPropertiesOutputLog();
                } else {
                    objectKeys = Object.keys(files.i18n);
                    grunt.log.writeln(objectKeys.length + " properties file" + singularOrPlurial(objectKeys) + " processed successfully.");
                    for (var filename in files.i18n) {
                        objectKeys = Object.keys(files.i18n[filename]);
                        grunt.verbose.writeln("- " + filename + ": " + objectKeys.length + " valid key" + singularOrPlurial(objectKeys) + ".");
                    };
                }

                // html and js

                var issueInSources = (
                    output.sources.proper_prefix_and_no_translation_available.length !== 0 ||
                    output.sources.invalid_prefix_and_properly_translated.length !== 0 ||
                    output.sources.invalid_prefix_and_no_translation_available.length !== 0
                );

                grunt.log.writeln("\n>> HTML and JS:");
                if (issueInSources) {
                    _publishSourceOutputLog();
                } else {
                    objectKeys = Object.keys(files.sources);
                    grunt.log.writeln(objectKeys.length + " HTML and JS file" + singularOrPlurial(objectKeys) + " processed successfully.");
                    for (var filename in files.i18n) {
                        grunt.verbose.writeln("- " + filename);
                    };
                }

                grunt.log.writeln("\nRun '--verbose' for more details.");
                if (issueInI18n || issueInSources) {
                    grunt.fail.fatal("Aborted due to warnings.");
                }

            };

            /* --------------
             [ running task ]
             ------------- */

            convertPropertiesToJson();

            convertSourcesToArray();

            checkSourceI18nCompliancy();

            checkPropertiesI18nCompliancy();

            publishReport();


        }
    );

};
