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

    /**
     * @ngdoc overview
     * @name generateTsConfig(T)
     * @description
     * # generateTsConfig Task
     * generateTsConfig is a task that generates tsconfig.json files from a json properties object
     *
     * # Configuration
     * ```js
     * {
     *      <target>: {
     *          dest: string,  // the output path/filename of the generated tsconfig
     *          data: string,  // the json config data
     *      }
     * }
     *
     * ```
     */

    const fs = require('fs-extra');
    const taskName = 'generateTsConfig';

    function validateConfig(config) {
        if (!config.data) {
            grunt.fail.fatal(`${taskName} - invalid config, [data] param is required`);
        }
        if (!config.dest) {
            grunt.fail.fatal(`${taskName} - invalid config, [dest] param is required`);
        }
    }

    grunt.registerMultiTask(taskName, function() {

        grunt.verbose.writeln(`${taskName} config: ${JSON.stringify(this.data)}`);

        validateConfig(this.data);

        const config = this.data;

        // WRITE
        grunt.log.writeln(`Writting to: ${config.dest}`);
        fs.outputFileSync(config.dest, JSON.stringify(config.data, null, 4));

    });

};
