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
     * @name generateKarmaConf(T)
     * @description
     *
     * # generateKarmaConf Task
     * generateKarmaConf is a multiTask that creates karma.conf files from json karma configuration.
     *
     * # Configuration
     * ```js
     * {
     *     <target>: {
     *          dest: string    // output path and filename
     *          data: string    // json formated string of karma configuration
     *     }
     * }
     * ```
     *
     */

    const fs = require('fs-extra');
    const serialize = require('serialize-javascript');

    const taskName = 'generateKarmaConf';

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

        process.env.CHROME_BIN = require('puppeteer').executablePath();

        // const outputContent = JSON.stringify(config.data, null, 4);
        const tpl =
            `module.exports = function(config) {
    config.set(
        ${serialize(config.data, {space: 4})}
    );
};`;

        // WRITE
        grunt.log.writeln(`Writting to: ${config.dest}`);
        fs.outputFileSync(config.dest, tpl);

    });

};
