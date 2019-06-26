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

    /*
     * generates angular.module('run').run(['$templateCache', function($templateCache) {}]) module
     * that contains template caches so that they become minifyable !!!
     */
    grunt.registerTask("multipleNGTemplates", function() {


        var conf = grunt.config.get('ngtemplates') || {};

        function failIfExists(dir) {
            if (conf[dir]) {
                grunt.fail.warn(`multipleNGTemplates grunt task: Attempting to define a grunt target with an existing name in config: ${dir}`);
            }
        }

        function getSanitizedConfigKey(dir) {
            return dir.replace(/\//g, "-");
        }

        // read all subdirectories from your modules folder
        grunt.file.expand({
            filter: 'isDirectory'
        }, "web/features/*").forEach(function(dir) {
            var key = getSanitizedConfigKey(dir);
            failIfExists(key);
            var folderName = dir.replace("web/features/", "");
            conf[key] = {
                src: [dir + '/**/*Template.html'],
                dest: 'jsTarget/' + dir + '/templates.js',
                options: {
                    standalone: true, //to declare a module as opposed to binding to an existing one
                    module: folderName + 'Templates'
                }
            };
            grunt.task.run('ngtemplates:' + key);
        });

        grunt.file.expand({
            filter: 'isDirectory'
        }, "web/featureExtensions/*/*").forEach(function(dir) {
            var key = getSanitizedConfigKey(dir);
            failIfExists(dir);
            var folderName = dir.replace("web/featureExtensions/", "");
            conf[key] = {
                src: [dir + '/**/*Template.html'],
                dest: 'jsTarget/' + dir + '/templates.js',
                options: {
                    standalone: true, //to declare a module as opposed to binding to an existing one
                    module: folderName + 'Templates'
                }
            };
            grunt.task.run('ngtemplates:' + key);

        });

        grunt.config.set('ngtemplates', conf);
    });

};
