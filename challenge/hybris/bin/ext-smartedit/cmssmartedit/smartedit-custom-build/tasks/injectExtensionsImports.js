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
/* jshint esversion: 6 */
module.exports = function(grunt) {

    var fs = require('fs');

    // For each extension in folder web/featureExtensions:
    // Inject an 'import' statement of the barrel file (if it exist) for each frame (cmssmartedit and cmssmarteditContainer)
    grunt.registerTask('injectExtensionsImports', function() {

        const paths = require("../../jsTests/paths");
        let conf = grunt.config.get('file_append') || {};

        grunt.file.expand({
            filter: 'isDirectory'
        }, "web/featureExtensions/*/").forEach(function(dir) {

            let folderName = dir.replace("web/featureExtensions/", "");
            folderName = folderName.substring(0, folderName.indexOf('/'));
            let files = [];
            if (fs.existsSync(`web/featureExtensions/${folderName}/cmssmartedit/index.ts`)) {
                files.push({
                    append: `\nimport {${folderName}Module} from '../../featureExtensions/${folderName}/cmssmartedit';\n!!${folderName}Module;`,
                    input: paths.target.featureExtensionsSmartEditImport
                });
            }
            if (fs.existsSync(`web/featureExtensions/${folderName}/cmssmarteditContainer/index.ts`)) {
                files.push({
                    append: `\nimport {${folderName}Module} from '../../featureExtensions/${folderName}/cmssmarteditContainer';\n!!${folderName}Module;`,
                    input: paths.target.featureExtensionsSmartEditContainerImport
                });
            }
            if (files.length) {
                conf[dir] = {
                    files
                };
                grunt.task.run('file_append:' + dir);
            }
        });

        grunt.config.set('file_append', conf);
    });

};
