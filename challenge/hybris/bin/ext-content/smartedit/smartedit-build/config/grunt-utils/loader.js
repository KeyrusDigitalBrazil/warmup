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
    'use strict';

    const fs = require('fs-extra');
    const path = require('path');
    const lodash = require('lodash');
    const gruntHooks = require('grunt-before-after-hooks');
    const bundlePaths = require('../../bundlePaths');
    const deepFreeze = require('./deepFreeze');
    const taskUtil = require('./taskUtil')(grunt);

    if (!grunt) {
        throw `SmarteditBuildLoader - require() missing grunt object`;
    }

    /**
     * @ngdoc overview
     * @name 1-Smartedit Builder
     * @description
     *
     * # Smartedit Builder
     *
     * The Smartedit Builder is a collection of tools and resources to make development of SmartEdit
     * extensions easier.
     *
     * The main engine behind the builder is the {@link 2-SmarteditBuildLoader loader}, a grunt-based
     * task runner that lets developers setup sequences of common development tasks, and execute them from command line,
     * such as **grunt test** that might run your unit tests, or **grunt package** that might compile, obfuscate, and
     * concat your source files into a distribution file.
     *
     * See the Smartedit Builder overview on https://help.hybris.com.
     *
     * To get started using the SmartEdit Builder, **start with the {@link 2-SmarteditBuildLoader SmarteditBuildLoader}**
     *
     */

    // #############################################################################################################

    /**
     * @ngdoc overview
     * @name 3-Tasks
     * @description
     *
     * # Registering Smartedit Builder tasks
     *
     * A Smartedit Builder task is simply a grunt task, which refers to both custom tasks (created by you), as well
     * as tasks from NPM.
     *
     * ## Custom Tasks
     *
     * To register a new task, simply create a new **task file** in your extension tasks folder.
     * (<extension>/smartedit-custom-build/tasks)
     *
     * A **task file** is a NodeJS module that exports a function which receives grunt as it's only parameter.
     *
     * Inside this function, simply use the grunt API to **grunt.registerTask(..)** or **grunt.registerMultiTask(...)**
     *
     * Example of a single(normal) task registration.
     * ```js
     *  // <extension>/smartedit-custom-build/tasks/singleTaskExample.js
     *  module.exports = function(grunt) {
     *     grunt.registerTask('singleTaskExample', 'A single task', function() {
     *         console.info('singleTaskExample is running!');
     *     });
     *  };
     * ```
     *
     * Example of a multiTask registration.
     * ```js
     *  // <extension>/smartedit-custom-build/tasks/multiTaskExample.js
     *  module.exports = function(grunt) {
     *     grunt.registerMultiTask('multiTaskExample', 'A multiTask', function() {
     *         // this.data is the target specific grunt config
     *         // see {@link https://gruntjs.com/creating-tasks creating-tasks}
     *         console.info(`multiTaskExample target config: ${this.data}`);
     *     });
     *  };
     *
     * ```
     *
     * ## NPM Tasks
     *
     * NPM tasks are loaded in grunt using **grunt.loadNpmTasks(name)**
     *
     * Each extension has a node_modules and a package.json that is symlinked from the npmancillary extension.
     * This means that any NPM grunt task must be added through the npm ancillary extension.
     *
     * Once this is complete you can load it from a **task file** just like for the custom tasks. We suggest using a
     * single npm tasks file to keep track of custom vs npm tasks.
     *
     * Example of loading an npm registration.
     * ```js
     *  // <extension>/smartedit-custom-build/tasks/npmTasks.js
     *  module.exports = function(grunt) {
     *     grunt.loadNpmTasks('taskA');
     *     grunt.loadNpmTasks('taskB');
     *     // ...
     *  };
     *
     * ```
     * ## Task configuration
     *
     * Loading grunt task configuration is slightly different in the SmartEdit builder then using grunt OOTB.
     *
     * To learn about creating configuration for SmartEdit Builder tasks, please see
     * {@link 4-Configuration the Configuration page}.
     *
     */

    // #############################################################################################################

    /**
     * @ngdoc overview
     * @name 4-Configuration
     * @description
     *
     * # Creating configuration for Smartedit Builder tasks
     *
     * A configuration for a Smartedit Builder task is a modified version of standard grunt task configuration.
     * Configuration is defined in **configuration files** which can be in the bundle, in an extension, or even
     * in both. This is how the builder is able to define common configuration (in the bundle) yet allow extensions to
     * override/customize/extend this base config in different ways.
     *
     * Learn more about extensibility in the sections below.
     *
     * Extension specific configuration files go in **<extension>/smartedit-custom-build/config**
     *
     *
     * ## Configuration files
     *
     * A **configuration file** is a NodeJS module that exports a function which returns an object with 2 properties.
     * ```js
     * // <extension>/smartedit-custom-build/config/taskName.js
     * module.exports = function() {
     *     return {
     *         targets: []
     *         config: function(commonData, bundleConfig) {
     *             // more details below on this function
     *             return {};
     *         }
     *     };
     * };
     * ```
     *
     * ### targets
     * When creating a configuration for a multiTask, this property is an array of strings, which are the multiTask
     * target names.
     *
     * If the configuration file is for a single task, this property should be omitted.
     *
     * The targets are read before grunt is initialized. If there is a configuration in both the bundle and the
     * extension, both will be read and a unique list created.
     *
     * ### config
     * The config function returns the real grunt config for your custom grunt task or 3rd party grunt task.
     *
     * The config function is only executed when a task attempts to load the configuration. (lazy loading)
     *
     * The function has 2 parameters:
     * - **commonData** is a JS object created by the loader, and the same object is passed to every config function.
     * In the OOTB setup, this is simply an empty object. This was originally created for property sharing between
     * configuration files, but since all the build is running on NodeJS, we now recommended using better design
     * patterns for resource sharing, such as requiring singletons or custom node modules.
     *
     * - **bundleConfig** - If the config file is in the bundle, then bundleConfig will just be an empty object.
     * If the config file is in an extension, then bundleConfig will be either the object returned by the configuration
     * in the bundle (same filename, matching the task name) or if there is no bundle configuration for this task then
     * and empty object will be returned.
     *
     *
     * ## Examples and extensibility
     * To create a new configuration in your extension, simply create a new **config file** in your extension config
     * folder.
     *
     *
     * #### Example of a configuration for a task with configuration only in the extension
     * ```js
     * // <extension>/smartedit-custom-build/config/someSingleTaskName.js
     * module.exports = function() {
     *     return {
     *         config: function(commonData, bundleConfig) {
     *             return {
     *                  data: 'somedata'   // someSingleTaskName is expecting a string data
     *             };
     *         }
     *     };
     * };
     * ```
     *
     *
     * #### Example of extending a bundle configuration for a multiTask, by adding a new target called **targetFoo**
     * ```js
     * // <extension>/smartedit-custom-build/config/someMultiTaskName.js
     * module.exports = function() {
     *     return {
     *     targets: ['targetFoo'],
     *     config: function(commonData, bundleConfig) {
     *              bundleConfig = bundleConfig || {};  // be robust!
     *              bundleConfig.targetFoo = {
     *                  // some target-specific config
     *              };
     *             return bundleConfig;
     *         }
     *     };
     * };
     * ```
     *
     *
     * #### Another common use-case is that you want to use the default OOTB configuration, but just add extra
     * #### extension-specific files to the files list. Lets even assume this is 1 specific target for this example.
     * ```js
     * // <extension>/smartedit-custom-build/config/someMultiTaskName.js
     * module.exports = function() {
     *     return {
     *         targets: ['existingTarget'], // although not technically required, good for readability
     *         config: function(commonData, bundleConfig) {
     *             if (!bundleConfig.existingTarget) {
     *                 throw 'someMultiTaskName requires a bundle config defined for the target existingTarget';
     *             }
     *             const files = bundleConfig.existingTarget.files || [];    // be robust
     *             files.push("someNewDirectory/**\/*");
     *             bundleConfig.existingTarget.files = files;
     *             return bundleConfig;
     *         }
     *     };
     * };
     * ```
     *
     */

    // #############################################################################################################

    /**
     * @ngdoc service
     * @name 2-SmarteditBuildLoader
     * @description
     *
     * The SmarteditBuildLoader is a javascript class located in the builder bundle, which is symlinked to
     * <extension>/smartedit-build
     *
     * If you don't have a symlinked smartedit-build directory at the root of your extension, please adjust your
     * buildcallback.xml according to the setup guide in the Smartedit Builder overview at https://help.hybris.com.
     *
     * The SmarteditBuildLoader is a wrapper around {@link https://gruntjs.com/ Grunt} that enhances it to support
     * distributed task loading and task configuration, as well as adding lazing loading of task configuration.
     *
     * Since it is a grunt wrapper, it is left to the developer to execute grunt from the command line, passing in
     * any target(s) they want to run.
     *
     * To wire the loader to grunt, simply require the loader, passing the grunt object from your Gruntfile.js, and
     * execute the load() method.
     * ```js
     *      // example Gruntfile.js
     *      module.exports = function(grunt) {
     *          require('./smartedit-build')(grunt).load();
     *      };
     * ```
     * See the {@link 2-SmarteditBuildLoader#methods_load load} method for more details.
     *
     */
    class SmarteditBuildLoader {


        constructor() {
            global.smartedit = {
                bundlePaths,
                taskUtil
            };
            deepFreeze(global.smartedit);

            this.config = {
                taskPaths: [
                    bundlePaths.build.grunt.tasksDir,
                    bundlePaths.external.grunt.tasksDir
                ],
                configPaths: [
                    bundlePaths.build.grunt.configDir,
                    bundlePaths.external.grunt.configDir
                ],
                configData: {}
            };
        }


        /**
         * @ngdoc method
         * @methodOf 2-SmarteditBuildLoader
         * @name 2-SmarteditBuildLoader#load
         * @description
         * The load method replaces the initConfig method of grunt, and is responsible for loading all the configuration
         * from the bundle and the extension, before passing control to grunt to execute the tasks specified at the
         * command line.
         *
         * # Load performs the following operations
         *  1. Task loading/registration
         *  2. Grunt initialization
         *  3. Lazy configuration loading
         *  4. Task execution
         *
         *
         * ## Task loading/registration
         *
         * The first thing that load() does is to register all the grunt tasks. This means both the tasks that come
         * with the bundle, and also the extension specific tasks.
         *
         * The extension specific tasks will be located in <extension>/smartedit-custom-build/tasks
         *
         * To learn more about registering tasks, see {@link 3-Tasks the Tasks page.}
         *
         *
         * ## Grunt initialization
         *
         * After loading the tasks, all of the task configuration files are read. This means every task config file
         * in the bundle, as well as every task config file in the extension specific config is read.
         *
         * The extension specific config will be located in <extension>/smartedit-custom-build/config
         *
         * Since configuration is loaded on demand for tasks, anything in the {@link 4-Configuration config} function
         * will not get execute during this step. This is how you can handle 2 tasks with a producer consumer
         * relationship.
         *
         *
         * ## Lazy configuration loading
         *
         * At this point grunt is initialized, but with no real data/configuration. Now that we have all the task names
         * and target names, event handlers are registered so that before any of these tasks are executed, the real
         * configuration is loaded.
         *
         * To find out more about registering task configuration, please see {@link 4-Configuration the configuration
         * page}.
         *
         *
         * ## Task execution
         *
         * Finally once all the tasks are loaded, and all the configuration is setup for lazy loading, control is
         * passed to grunt to begin executing whatever task or tasks were specified from the command line.
         *
         */
        load() {

            function logStage(stageName) {
                grunt.log.writeln('');
                grunt.log.write('>>> ' ['green']);
                grunt.log.writeln(stageName.cyan);
            }

            // Make sure a bundleRoot property is specified
            if (!fs.existsSync(bundlePaths.bundleRoot)) {
                grunt.fail.fatal(`No bundle found at ${bundlePaths.bundleRoot}`);
            }

            // Load custom built tasks
            // The rest loaded via the config
            logStage('Loading grunt tasks');
            this.config.taskPaths.forEach(function(tasksRoot) {
                grunt.loadTasks(tasksRoot);
            });

            // =============================================================================
            // =============================================================================
            // LOADING DUMMY GRUNT INITCONFIG OBJECT

            function recursiveCallForEachFile(rootDir, fn) {
                const files = fs.readdirSync(rootDir);
                files.forEach(function(file) {
                    let fullPath = path.join(rootDir, file);
                    if (fs.statSync(fullPath).isDirectory()) {
                        recursiveCallForEachFile(fullPath, fn);
                    } else {
                        fn(rootDir, file, fullPath);
                    }
                });
            }
            let gruntConfig = {};
            // Iterate over all config files, and reading them into config object
            logStage('Searching for task and target names');
            this.config.configPaths.forEach(function(configRoot) {
                grunt.log.writeln(`Searching in directory:\n${configRoot}` ['cyan']);
                recursiveCallForEachFile(configRoot, function(dir, file, fullPath) {
                    const dot = file.lastIndexOf('.');
                    const simpleName = file.substr(0, dot);
                    grunt.log.debug(`Task ${simpleName}`);
                    try {
                        let conf = require(fullPath)(grunt);
                        gruntConfig[simpleName] = gruntConfig[simpleName] || {};
                        (conf.targets || []).forEach((target) => {
                            grunt.log.debug(`  - ${target}`);
                            gruntConfig[simpleName][target] = {};
                        });
                    } catch (e) {
                        grunt.log.error(`ERROR: Problem reading grunt task config for ${fullPath}` ['red']);
                        grunt.log.error(e.stack);
                        grunt.fail.fatal(e);
                    }
                });
            });
            logStage('Initializing grunt with task and target names');
            grunt.log.debug(JSON.stringify(gruntConfig, null, 4));
            grunt.initConfig(gruntConfig);
            // =============================================================================
            // =============================================================================

            let loadedConfigs = [];
            const originalConfigGet = grunt.config.get;

            grunt.loadConfigForTask = function(taskName) {
                if (loadedConfigs.indexOf(taskName) === -1) {
                    grunt.log.debug(`\nLoading config for task: ${taskName}`);
                    this.config.configPaths.forEach(function(configRoot) {
                        let configFile = path.join(configRoot, taskName + '.js');
                        grunt.log.debug(`Loading from file: ${configFile}`);
                        if (fs.existsSync(configFile)) {
                            try {
                                grunt.config.set(taskName, require(configFile)(grunt).config(this.config.configData, lodash.cloneDeep(originalConfigGet(taskName) || {})));
                            } catch (e) {
                                grunt.log.error(`ERROR: Problem reading grunt task config for ${configFile}` ['red']);
                                grunt.log.error(e.stack);
                                grunt.fail.fatal(e);
                            }
                        }
                    }.bind(this));
                    grunt.log.debug(`Final config loaded:\n${JSON.stringify(originalConfigGet(taskName), null, 2)}`);

                    loadedConfigs.push(taskName);
                }
            }.bind(this);

            grunt.config.get = function(taskName) {
                grunt.loadConfigForTask(taskName);
                return originalConfigGet(taskName);
            };

            gruntHooks(grunt, {
                beforeEach(currentTask) {
                    grunt.loadConfigForTask(currentTask.name);
                }
            });

        }
    }

    return new SmarteditBuildLoader();
};
