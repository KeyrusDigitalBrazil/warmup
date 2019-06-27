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
module.exports = function(grunt) {

    /**
     * @ngdoc overview
     * @name checkNoForbiddenNameSpaces(T)
     * @description
     * # checkNoForbiddenNameSpaces Task
     * checkNoForbiddenNameSpaces is a task designed to prevent accidentally referencing 3rd party libraries via
     * their default namespace, instead of the angular service wrapper.
     *
     * Current scanned values:
     * - **jquery**: 'jQuery', '$(', '$.', 'window.$'
     * - **lodash**: '_.', 'window._'
     *
     * # Configuration
     * ```js
     * {
     *      pattern: string[]   // array of glob patterns of files to scan
     * }
     * ```
     *
     */

    const lodash = require('lodash');

    const taskName = "checkNoForbiddenNameSpaces";

    grunt.registerTask(taskName, 'fails the build if the code contains forbidden napespaces', function() {

        var GLOBAL_IGNORE_HINT = "/* forbiddenNameSpaces:false */";
        var IGNORE_HINT = "/* forbiddenNameSpaces %namespace:false */";

        var VIOLATION_TEMPLATE = "File <%= filePath %> contains forbidden namespace '<%= forbiddenNamespace %>', consider using '<%= allowedNamespace %>'";
        var DEPRECATION_TEMPLATE = "File <%= filePath %> contains namespace '<%= forbiddenNamespace %>' that is deprecated since <%= deprecatedSince %>, consider using '<%= allowedNamespace %>'";

        var REGEXP_ROOT = "REGEXP:";

        var fileMatchesAtLeastOnePattern = function(patterns, filePath) {
            return patterns.filter((pattern) => new RegExp(
                    pattern
                    .replace(/\./g, "\\.")
                    .replace(/\*\*/g, ".*")
                    .replace(/\*/g, ".*"),
                    'g')
                .test(filePath)).length;
        }

        var containsKey = function(text, key) {

            var escapedKeyForRegexp = null;
            if (key.indexOf(REGEXP_ROOT) === 0) {
                escapedKeyForRegexp = key.replace(REGEXP_ROOT, "");
            } else {
                escapedKeyForRegexp = key.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
            }

            return new RegExp("[\\s]+" + escapedKeyForRegexp, 'g').test(text);
        };

        var defaultMap = {
            patterns: [],
            namespaces: {
                'jQuery.': 'yjQuery in recipes or window.smarteditJQuery outside angular',
                'jQuery(': 'yjQuery in recipes or window.smarteditJQuery outside angular',
                '$(': 'yjQuery in recipes or window.smarteditJQuery outside angular',
                '$.': 'yjQuery in recipes or window.smarteditJQuery outside angular',
                'window.$': 'yjQuery in recipes or window.smarteditJQuery outside angular',
                '_.': 'lodash in recipes or window.smarteditLodash outside angular',
                'window._': 'lodash in recipes or window.smarteditLodash outside angular',
                'REGEXP:expect\\((.+)\\.isPresent\\(\\)\\)\\.toBeFalsy\\(\\);': 'browser.waitForAbsence(selector | element)',
                'REGEXP:expect\\((.+)\\.isPresent\\(\\)\\)\\.toBe\\(false': 'browser.waitForAbsence(selector | element)'
            }
        };

        var gruntConfig = grunt.config.get(taskName);

        if (!gruntConfig.pattern && !gruntConfig.mappings) {
            grunt.fail.warn("neither pattern nor mappings were provided for task " + taskName);
        }

        var mergedMap = lodash.cloneDeep(defaultMap);

        let patterns = [];
        let mappings = [];

        //legacy
        if (gruntConfig.pattern) {
            patterns = gruntConfig.pattern;
            mergedMap.patterns = gruntConfig.pattern;
            mappings = [mergedMap];
            //new approach
        } else {
            //aggregate all patterns from all the mappings
            patterns = gruntConfig.mappings.reduce((seed, next) => {
                seed = seed.concat(next.patterns);
                return seed;
            }, []);

            mappings = lodash.cloneDeep(gruntConfig.mappings);
            mappings.forEach((mapping) => {
                // if a mapping specifies '*' as namespaces, we swap it for the default namespaces
                if (mapping.namespaces === '*') {
                    mapping.namespaces = mergedMap.namespaces;
                }
            });
        }

        var violations = [];

        //expanding over all aggregated patterns from all the mappings for performance purposes
        grunt.file.expand({
            filter: 'isFile'
        }, patterns).filter((filePath) => {
            var fileContent = grunt.file.read(filePath);
            mappings
                //for a given file, only consider the mapping the patterns of which match the file path
                .filter((mapping) => fileMatchesAtLeastOnePattern(mapping.patterns, filePath))
                .forEach((mapping) => {
                    Object.keys(mapping.namespaces)
                        .filter((namespace) => fileContent.indexOf(GLOBAL_IGNORE_HINT) === -1 && fileContent.indexOf(IGNORE_HINT.replace("%namespace", namespace)) === -1)
                        .filter((namespace) => containsKey(fileContent, namespace))
                        .forEach((namespace) => {
                            violations.push({
                                message: grunt.template.process(mapping.deprecatedSince ? DEPRECATION_TEMPLATE : VIOLATION_TEMPLATE, {
                                    data: {
                                        filePath: filePath,
                                        forbiddenNamespace: namespace.replace(new RegExp("^" + REGEXP_ROOT), ""),
                                        allowedNamespace: mapping.namespaces[namespace],
                                        deprecatedSince: mapping.deprecatedSince
                                    }
                                }),
                                severity: mapping.level ? mapping.level : (mapping.deprecatedSince ? "INFO" : null)
                            });
                        });
                });
        });

        if (violations.length) {
            grunt.log.writeln("At least one file contains a forbidden or deprecated namespace".yellow);
            violations.forEach(function(violation) {
                if (violation.severity === "FATAL") {
                    grunt.log.writeln(("ERROR: " + violation.message).red);
                } else if (violation.severity === "INFO") {
                    grunt.log.writeln(("INFO: " + violation.message).green);
                }
            });
            if (violations.filter((violation) => violation.severity === "FATAL").length) {
                grunt.fail.warn("Make sure not to commit this!");
            }
        }

    });

};
