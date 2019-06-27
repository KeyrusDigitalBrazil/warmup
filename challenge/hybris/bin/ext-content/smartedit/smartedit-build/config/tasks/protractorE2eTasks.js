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

    const CHROME_PATTERN = 'node_modules/**/chromedriver*';

    grunt.registerTask("protractorRun", 'Executes e2e tests for smarteditcontainer', function() {
        if (grunt.file.expand({
                filter: 'isFile'
            }, CHROME_PATTERN).length) {
            grunt.task.run('protractor:run');
        } else {
            grunt.log.warn('protractor:run grunt phase was not run since no driver found under ' + CHROME_PATTERN);
        }
    });

    grunt.registerTask("protractorMaxrun", 'Executes e2e tests for smarteditcontainer', function() {
        if (grunt.file.expand({
                filter: 'isFile'
            }, CHROME_PATTERN).length) {
            grunt.task.run('protractor:maxrun');
        } else {
            grunt.log.warn('protractor:maxrun grunt phase was not run since no driver found under ' + CHROME_PATTERN);
        }
    });

    grunt.registerTask('e2e_repeat', 'Execute the e2e test(s) x amount of times', function(times) {
        let repeat = times || 1;
        grunt.task.run(['connect:dummystorefront', 'connect:test']);
        for (let i = 0; i < repeat; i++) {
            grunt.task.run('protractorRun');
        }
    });

};
