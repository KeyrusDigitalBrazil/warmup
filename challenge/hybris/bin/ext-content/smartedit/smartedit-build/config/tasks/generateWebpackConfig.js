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
     * @name generateWebpackConfig(T)
     * @description
     * # generateWebpackConfig Task
     * generateWebpackConfig is a task that generates webpack.config.json files from a json properties object
     *
     * # Configuration
     * ```js
     * {
     *      <target>: {
     *          dest: string,  // the output path/filename of the generated webpack file
     *          data: string,  // the json config data
     *          awesomeTsConfigFile: string     // location of tsConfig file for awesome-typescript-loader
     *      }
     * }
     *
     * ```
     */


    const fs = require('fs-extra');
    const path = require('path');
    const serialize = require('serialize-javascript');

    const taskName = 'generateWebpackConfig';

    const TYPESCRIPT_LOADER = 'awesome-typescript-loader';

    function validateConfig(config) {
        if (!config.data) {
            grunt.fail.fatal(`${taskName} - invalid config, [data] param is required`);
        }
        if (!config.dest) {
            grunt.fail.fatal(`${taskName} - invalid config, [dest] param is required`);
        }
    }

    function addAwesomeTypescriptLoaderConfigFile(config, tsConfigFile) {
        if (config.module && config.module.rules) {
            const typeScriptRule = config.module.rules.find((rule) => {
                return rule.use ? rule.use.find(loaderConfig => loaderConfig.loader === TYPESCRIPT_LOADER) : false;
            });
            if (!typeScriptRule) {
                grunt.fail.fatal(`Error adding configFileName [${tsConfigFile}] to ${TYPESCRIPT_LOADER} config of webpackConfig`);
            } else {
                const typeScriptLoader = typeScriptRule.use.find(loaderConfig => loaderConfig.loader === TYPESCRIPT_LOADER);
                typeScriptLoader.options = typeScriptLoader.options || {};
                typeScriptLoader.options.configFileName = path.resolve(tsConfigFile);
            }
        }
    }

    grunt.registerMultiTask(taskName, function() {

        grunt.verbose.writeln(`${taskName} config: ${JSON.stringify(this.data)}`);

        validateConfig(this.data);

        if (this.data.awesomeTsConfigFile) {
            addAwesomeTypescriptLoaderConfigFile(this.data.data, this.data.awesomeTsConfigFile);
        }

        const config = this.data;

        // WRITE
        grunt.log.writeln(`Writting to: ${config.dest}`);
        fs.outputFileSync(config.dest, 'module.exports = ' + serialize(config.data, {
            space: 4
        }) + ';');
    });

};
