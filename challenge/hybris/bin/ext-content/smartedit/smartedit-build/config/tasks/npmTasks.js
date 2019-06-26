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
     * @name npmTasks(T)
     * @description
     * # npm Tasks
     *
     * The following are the 3rd party npm tasks available through node_modules from the npmancillary extension:
     * - grunt-contrib-clean
     * - grunt-contrib-concat
     * - grunt-contrib-connect
     * - grunt-contrib-copy
     * - grunt-contrib-symlink
     * - grunt-contrib-jshint
     * - grunt-contrib-less
     * - grunt-contrib-uglify
     * - grunt-contrib-watch
     * - grunt-jsbeautifier
     * - grunt-karma
     * - grunt-ngdocs
     * - grunt-angular-templates
     * - grunt-postcss
     * - grunt-protractor-runner
     * - grunt-tslint
     * - grunt-webpack
     */

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-symlink');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.loadNpmTasks('grunt-jsbeautifier');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-ngdocs');
    grunt.loadNpmTasks('grunt-angular-templates');
    grunt.loadNpmTasks('grunt-postcss');
    grunt.loadNpmTasks('grunt-protractor-runner');
    grunt.loadNpmTasks("grunt-tslint");
    grunt.loadNpmTasks('grunt-webpack');
    grunt.loadNpmTasks('grunt-webfont');

};
