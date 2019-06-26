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

    /** @ngdoc overview
     * @name tsformatter(T)
     * @description
     * # tsformatter Task
     * The tsformatter task runs the {@link https://github.com/vvakame/typescript-formatter typescript-formatter}
     * with a default configuration, on the files of your choosing.
     *
     * # Configuration
     * ```js
     * {
     *    options: object   // json object properties for typescript-formatter
     *    files: glob[]     // array of glob pattern files to format
     * }
     * ```
     */

    var tsfmt = require('typescript-formatter');

    const taskName = 'tsformatter';

    grunt.registerTask(taskName, 'TypeScript code formatter', function() {
        var done = this.async();
        var gruntConfig = grunt.config.get(taskName);
        var files = grunt.file.expand(gruntConfig.files);
        grunt.log.writeln("tsformatter - processing " + files.length.toString().cyan + " files.");
        tsfmt.processFiles(files, gruntConfig.options).then(function() {
            grunt.log.ok();
            done();
        });
    });
};
