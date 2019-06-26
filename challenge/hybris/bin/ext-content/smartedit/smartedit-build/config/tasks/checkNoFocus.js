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
     * @name checkNoFocus(T)
     * @description
     * # checkNoFocus Task
     * checkNoFocus is a task designed to prevent accidental commits with tests disabled.
     * It will scan all provided source files and fail the build if it finds a fit() or fdescribe()
     *
     * Since we're using jasmine for our karma unit tests and protractor e2e tests, it's possible to run the test
     * suite with fit or fdescribe to focus on specific tests. Developers sometimes forget to remove this focus, and
     * commit code to CI with tests disabled because of it.
     *
     * # Configuration
     * ```js
     * {
     *      pattern: string[]   // array of glob patterns of files to scan
     * }
     * ```
     *
     */

    const taskName = "checkNoFocus";

    grunt.registerTask(taskName, 'fails the build if the code contains fdescribe or fit in one of the e2e', function() {

        var gruntConfig = grunt.config.get(taskName);

        if (!gruntConfig.pattern) {
            grunt.fail.warn("pattern was not provided for task " + taskName);
        }

        var focusedFiles = grunt.file.expand({
            filter: 'isFile'
        }, gruntConfig.pattern).filter(function(filePath) {
            var fileContent = grunt.file.read(filePath);
            return fileContent.indexOf("fdescribe") > -1 || fileContent.indexOf("fit") > -1;
        });

        if (focusedFiles.length) {
            grunt.log.writeln("At least one e2e test file contains a focus (fdescribe and/or fit):".yellow);
            focusedFiles.forEach(function(filePath) {
                grunt.log.writeln(filePath.green);
            });
            grunt.fail.warn("Make sure not to commit this!");
        }
    });

};
