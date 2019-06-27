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
const protractorConf = require(global.smartedit.bundlePaths.test.e2e.protractor.conf);

module.exports = function(grunt) {

    const MAX_INSTANCES = "max_instances";

    function getProtractorCapabilities() {
        if (protractorConf && protractorConf.config && protractorConf.config.capabilities) {
            return protractorConf.config.capabilities;
        }
    }

    return {
        targets: [
            'run',
            'maxrun'
        ],
        config: function(data, conf) {
            conf = conf || {};

            const maxInstances = grunt.option(MAX_INSTANCES) ? parseInt(grunt.option(MAX_INSTANCES)) :
                (process.env.PROTRACTOR_CHROME_INSTANCES || getProtractorCapabilities().maxInstances || 5);

            const openBrowser = grunt.option('browser_debug') || false;

            let chromeOptionsArgs;
            if (getProtractorCapabilities().chromeOptions && getProtractorCapabilities().chromeOptions.args) {
                chromeOptionsArgs = getProtractorCapabilities().chromeOptions.args;
            }
            if (openBrowser) {
                const index = chromeOptionsArgs.indexOf('headless');
                if (index > -1) {
                    chromeOptionsArgs.splice(index, 1);
                }
            } else {
                grunt.log.writeln('Note: In headless mode, we cannot modify the browser size in run-time.');
            }

            conf.options = {
                // Required to prevent grunt from exiting with a non-zero status in CI
                keepAlive: process.env.PROTRACTOR_KEEP_ALIVE === 'true',
                configFile: global.smartedit.bundlePaths.test.e2e.protractor.conf
            };

            conf.run = {
                // standard e2e
                options: {
                    args: {
                        capabilities: {
                            chromeOptions: {
                                args: chromeOptionsArgs // pass a second dummy value to prevent grunt-protractor from trimming the [] when passing to protractor
                            }
                        }
                    }
                }
            };

            conf.maxrun = { // multiple instance e2e (more performant)
                options: {
                    args: {
                        capabilities: {
                            shardTestFiles: true,
                            maxInstances: maxInstances,
                            chromeOptions: {
                                args: chromeOptionsArgs
                            }
                        }
                    }
                }
            };

            return conf;
        }
    };
};
