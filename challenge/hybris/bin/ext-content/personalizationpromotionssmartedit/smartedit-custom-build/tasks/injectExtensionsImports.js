/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
module.exports = function(grunt) {

    var fs = require('fs');


    // For each extension in folder web/featureExtensions:
    // Inject an 'import' statement of the barrel file (if it exist) for each frame (cmssmartedit and cmssmarteditContainer)
    grunt.registerTask('injectExtensionsImports', function() {

        var paths = require("../../jsTests/paths");

        let taskConfig;

        grunt.file.expand({
            filter: 'isDirectory'
        }, "web/featureExtensions/*/").forEach(function(dir) {

            taskConfig = taskConfig || {};

            var folderName = dir.replace("web/featureExtensions/", "");
            folderName = folderName.substring(0, folderName.indexOf('/'));
            var files = [];
            if (fs.existsSync('web/featureExtensions/' + folderName + '/personalizationpromotionssmartedit/index.ts')) {
                files.push({
                    append: "\nimport {${folderName}Module} from '../../featureExtensions/${folderName}/personalizationpromotionssmartedit';\n!!${folderName}Module;",
                    input: paths.target.featureExtensionsSmartEditImport
                });
            }
            if (fs.existsSync('web/featureExtensions/' + folderName + '/personalizationpromotionssmarteditContainer/index.ts')) {
                files.push({
                    append: "\nimport {${folderName}Module} from '../../featureExtensions/${folderName}/personalizationpromotionssmarteditContainer';\n!!${folderName}Module;",
                    input: paths.target.featureExtensionsSmartEditContainerImport
                });
            }
            if (files.length) {
                taskConfig[dir] = {
                    files: files
                };
            }
        });

        if (taskConfig) {
            grunt.config.set('file_append', taskConfig);
            grunt.task.run('file_append');
        }
    });

};
