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

    // Helper Functions
    function endsWith(inputStr, suffix) {
        return inputStr.match(suffix + "$");
    }

    grunt.registerTask('multiConcat', function() {
        var multiConcatTask = [];

        grunt.file.expand({
            filter: 'isDirectory'
        }, "jsTarget/web/features/*").forEach(function(dir) {
            if (!endsWith(dir, "/personalizationsearchsmarteditcommons")) {
                var folderName = dir.replace("jsTarget/web/features/", "");
                multiConcatTask.push(folderName);
            }
        });

        multiConcatTask.forEach(function(folderName) {
            grunt.task.run('concat:' + folderName);
        });
    });

};
