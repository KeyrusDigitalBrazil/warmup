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
module.exports = {
    addPlugin: (conf, plugin) => {
        conf.plugins = conf.plugins || [];
        conf.plugins.push(plugin);
    },

    addLoader: (conf, loader) => {
        conf.module = conf.module || {};
        conf.module.rules = conf.module.rules || [];
        conf.module.rules.push(loader);
    },

    addMinimizer: (conf, mimimizer) => {
        conf.mode = "production";
        conf.optimization = conf.optimization || {};
        conf.optimization.minimizer = conf.optimization.minimizer || [];
        conf.optimization.minimizer.push(mimimizer);

        conf.optimization.minimize = true;
    },

    ngAnnotatePlugin: {
        "options": {
            "add": true,
            "sourceMap": true
        },
        apply: function(compiler) {
            const ngAnnotatePlugin = require('ng-annotate-webpack-plugin');
            return new ngAnnotatePlugin({
                add: true,
                sourceMap: true
            }).apply(compiler);
        }
    },

    uglifyJsPlugin: {
        apply: function(compiler) {
            const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
            return new UglifyJSPlugin({
                uglifyOptions: {
                    sourceMap: true,
                    keep_classnames: true,
                    keep_fnames: true
                }
            }).apply(compiler);
        }
    },

    /*
     * This plugin propagate compilation errors, it is necessary to fail the build when there is compilation errors in spec files.
     * Webpack does not include modules which have errors, which causes Karma to run all the tests without the failed spec.
     */
    karmaErrorsPlugin: {
        apply: function(compiler) {
            const KarmaErrorsPlugin = function() {};
            KarmaErrorsPlugin.prototype.apply = function(compiler) {
                compiler.hooks.done.tap('KarmaErrorsPlugin', (stats) => {
                    if (stats.compilation.errors.length && process.argv.indexOf('--watch') === -1) {
                        stats.compilation.errors.forEach(function(error) {
                            console.error(`ERROR in ${error.message || error}`);
                        });
                        process.exit(1);
                    }
                });
            };
            return new KarmaErrorsPlugin().apply(compiler);
        }
    },

    /**
     * This plugin filter out TypeScript compilation warnings stating that export does not exist when it does.
     * https://github.com/webpack/webpack/issues/7378
     * https://github.com/TypeStrong/ts-loader/issues/653#issuecomment-390889335
     */
    ignoreNotFoundExportPlugin: {
        apply: function(compiler) {
            const ModuleDependencyWarning = require("webpack/lib/ModuleDependencyWarning");
            const messageRegExp = /export '.*'( \(reexported as '.*'\))? was not found in/;

            function doneHook(stats) {
                stats.compilation.warnings = stats.compilation.warnings.filter(function(warn) {
                    if (warn instanceof ModuleDependencyWarning && messageRegExp.test(warn.message)) {
                        return false;
                    }
                    return true;
                });
            }
            if (compiler.hooks) {
                compiler.hooks.done.tap("IgnoreNotFoundExportPlugin", doneHook);
            } else {
                compiler.plugin("done", doneHook);
            }
        }
    },

    // https://github.com/webpack-contrib/istanbul-instrumenter-loader
    istanbulInstrumenterLoader: {
        test: /\.ts$/,
        exclude: /smartedit-build|Test\.ts$/,
        loader: 'istanbul-instrumenter-loader',
        enforce: 'post',
        options: {
            esModules: true,
            preserveComments: true,
            produceSourceMap: true
        }
    }

};
