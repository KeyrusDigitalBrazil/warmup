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

    var phantomJSPattern = 'node_modules/**/phantomjs*';

    var testsRoot = 'jsTests/';
    var testsRootFiles = testsRoot + '*';

    grunt.registerTask("multiKarma", 'Executes unit tests for each project via karma separately.', function() {
        //if npmtestancillary is not present, phantomjs drivers won't be present

        if (grunt.file.expand({
                filter: 'isFile'
            }, phantomJSPattern).length > 0) {

            grunt.file.expand({
                filter: 'isDirectory'
            }, testsRootFiles).forEach(function(dir) {
                var folderName = dir.replace(testsRoot, "");
                if (folderName !== 'utils') {
                    grunt.task.run('karma:' + folderName);
                }
            });

        } else {
            grunt.log.warn('multiKarma grunt phase was not run since no phantomjs driver found under ' + phantomJSPattern);
        }
    });

};
