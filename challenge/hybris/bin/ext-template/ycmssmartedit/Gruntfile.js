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

    require('time-grunt')(grunt);
    require('./smartedit-build')(grunt).load();

    grunt.registerTask('generate', [
        'generateWebpackConfig',
        'generateTsConfig',
        'generateKarmaConf'
    ]);

    grunt.registerTask('docs', ['clean', 'ngdocs']);
    grunt.registerTask('linting', ['jshint', 'tslint']);
    grunt.registerTask('sanitize', ['jsbeautifier', 'tsformatter']);
    grunt.registerTask('test_only', ['generate', 'multiKarma']);
    grunt.registerTask('test', ['test_only']);
    grunt.registerTask('e2e', ['connect:dummystorefront', 'connect:test', 'multiProtractor']);
    grunt.registerTask('e2e_max', ['connect:dummystorefront', 'connect:test', 'multiProtractorMax']);
    grunt.registerTask('e2e_dev', 'e2e local development mode', function() {
        grunt.option('keepalive_dummystorefront', true);
        grunt.option('open_browser', true);
        grunt.task.run(['connect:test', 'connect:dummystorefront']);
    });
};
