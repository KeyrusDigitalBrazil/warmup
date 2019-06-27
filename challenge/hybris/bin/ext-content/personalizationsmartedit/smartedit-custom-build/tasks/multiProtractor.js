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

    var lodash = require('lodash');
    var chromeDriverPattern = 'node_modules/**/chromedriver*';


    function execute(useMultipleInstances) {

        var paths = require("../../jsTests/paths");

        var conf = grunt.config.get('protractor');

        // make sure proper boolean
        useMultipleInstances = !!useMultipleInstances;

        //if npmtestancillary is not present, chrome drivers won't be present
        if (grunt.file.expand({
                filter: 'isFile'
            }, chromeDriverPattern).length > 0) {

            paths.getE2eFiles().forEach(function(path) {
                // start with maxrun target, defined in the base smartedit-build/config if multiInstance is set
                var baseConf = useMultipleInstances ?
                    lodash.cloneDeep(conf.maxrun || {}) :
                    lodash.cloneDeep(conf.run || {});

                var taskKey = path;
                if (conf[taskKey]) {
                    grunt.fail.warn(`multipleNGTemplates grunt task: Attempting to define a grunt target with an existing name in config: ${taskKey}`);
                }

                var options = {
                    options: { // default protractor conf will come from parent options
                        args: {
                            specs: [path]
                        }
                    }
                };
                lodash.merge(baseConf, options);

                conf[taskKey] = baseConf;
                grunt.task.run('protractor:' + taskKey);
            });

            grunt.config.set('protractor', conf);

        } else {
            grunt.log.warn('protractor grunt phase was not run since no chrome driver found under ' + chromeDriverPattern);
        }

    };

    grunt.registerTask("multiProtractor", 'Executes end to end tests for each project via protractor separately', function() {
        execute(false);
    });

    grunt.registerTask("multiProtractorMax", 'Executes end to end tests for each project via protractor separately', function() {
        execute(true);
    });

};
