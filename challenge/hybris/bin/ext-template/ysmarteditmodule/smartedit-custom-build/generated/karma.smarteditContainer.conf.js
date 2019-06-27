module.exports = function(config) {
    config.set({
        "singleRun": true,
        "junitReporter": {
            "outputDir": "jsTarget\u002Ftests\u002FysmarteditmoduleContainer\u002Fjunit\u002F",
            "outputFile": "testReport.xml"
        },
        "files": [
            "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Fwebroot\u002Fstatic-resources\u002Fdist\u002Fsmartedit\u002Fjs\u002Fthirdparties.js",
            "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Fwebroot\u002Fstatic-resources\u002Fthirdparties\u002Fckeditor\u002Fckeditor.js",
            "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Ftest\u002Funit\u002Fgenerated\u002F*.js",
            "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Ftest\u002Funit\u002F*.+(js|ts)",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fysmarteditmodulecommons\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002FysmarteditmoduleContainer\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002FysmarteditmoduleContainer\u002Ftemplates.js",
            "jsTests\u002Ftests\u002FysmarteditmoduleContainer\u002Funit\u002FspecBundle.ts"
        ],
        "exclude": [
            "**\u002FsharedSmarteditForTests.ts",
            "jsTarget\u002Fweb\u002Ffeatures\u002FysmarteditmoduleContainer\u002FysmarteditmodulecontainerModule.ts",
            "**\u002F*.d.ts",
            "*.d.ts"
        ],
        "webpack": {
            "devtool": "source-map",
            "externals": {
                "jasmine": "jasmine",
                "testutils": "testutils",
                "angular-mocks": "angular-mocks",
                "angular": "angular",
                "lodash": "lodash",
                "angular-route": "angular-route",
                "angular-translate": "angular-translate",
                "crypto-js": "CryptoJS",
                "Reflect": "Reflect",
                "moment": "moment",
                "smarteditcommons": "smarteditcommons"
            },
            "output": {
                "path": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002FjsTarget",
                "filename": "[name].js",
                "sourceMapFilename": "[file].map"
            },
            "resolve": {
                "modules": [
                    "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fnode_modules",
                    "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002FjsTarget\u002Fweb\u002Fapp",
                    "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002FjsTarget\u002Fweb\u002Ffeatures",
                    "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Ftest\u002Funit"
                ],
                "extensions": [
                    ".ts",
                    ".js"
                ],
                "alias": {
                    "testhelpers": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-build\u002Ftest\u002Funit",
                    "ysmarteditmodulecommons": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fysmarteditmodulecommons",
                    "ysmarteditmodulecontainer": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002FjsTarget\u002Fweb\u002Ffeatures\u002FysmarteditmoduleContainer"
                }
            },
            "module": {
                "rules": [{
                        "test": /\.ts$/,
                        "sideEffects": true,
                        "use": [{
                            "loader": "awesome-typescript-loader",
                            "options": {
                                "configFileName": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.karma.smarteditContainer.json"
                            }
                        }]
                    },
                    {
                        "test": /\.ts$/,
                        "exclude": /smartedit-build|Test\.ts$/,
                        "loader": "istanbul-instrumenter-loader",
                        "enforce": "post",
                        "options": {
                            "esModules": true,
                            "preserveComments": true,
                            "produceSourceMap": true
                        }
                    }
                ]
            },
            "performance": {
                "hints": false
            },
            "stats": {
                "colors": true,
                "modules": true,
                "reasons": true,
                "errorDetails": true
            },
            "plugins": [{
                    "apply": function(compiler) {
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
                {
                    "apply": function(compiler) {
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
                {
                    "options": {
                        "add": true,
                        "sourceMap": true
                    },
                    "apply": function(compiler) {
                        const ngAnnotatePlugin = require('ng-annotate-webpack-plugin');
                        return new ngAnnotatePlugin({
                            add: true,
                            sourceMap: true
                        }).apply(compiler);
                    }
                }
            ],
            "bail": true,
            "mode": "development"
        },
        "coverageIstanbulReporter": {
            "reports": [
                "html",
                "lcovonly",
                "text-summary"
            ],
            "dir": ".\u002FjsTarget\u002Ftest\u002Fcoverage",
            "fixWebpackSourcePaths": true,
            "skipFilesWithNoCoverage": true,
            "report-config": {
                "html": {
                    "subdir": "smarteditcontainer"
                }
            }
        },
        "basePath": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fysmarteditmodule",
        "frameworks": [
            "jasmine"
        ],
        "decorators": [
            "karma-jasmine"
        ],
        "preprocessors": {
            "**\u002F*.ts": [
                "webpack"
            ]
        },
        "reporters": [
            "spec",
            "junit",
            "coverage-istanbul"
        ],
        "specReporter": {
            "suppressPassed": true,
            "suppressSkipped": true
        },
        "port": 9876,
        "colors": true,
        "autoWatch": false,
        "autoWatchBatchDelay": 1000,
        "reportSlowerThan": 500,
        "browsers": [
            "ChromeHeadless"
        ],
        "customLaunchers": {
            "ChromeHeadless": {
                "base": "Chrome",
                "flags": [
                    "--no-sandbox",
                    "--headless",
                    "--disable-gpu",
                    "--disable-translate",
                    "--disable-extensions",
                    "--disable-web-security",
                    "--remote-debugging-port=9222"
                ],
                "debug": true
            }
        },
        "browserNoActivityTimeout": 20000,
        "mime": {
            "text\u002Fx-typescript": [
                "ts",
                "tsx"
            ]
        },
        "proxies": {
            "\u002Fstatic-resources\u002Fimages\u002F": "\u002Fbase\u002Fstatic-resources\u002Fimages\u002F"
        },
        "plugins": [
            "karma-webpack",
            "karma-jasmine",
            "karma-chrome-launcher",
            "karma-junit-reporter",
            "karma-spec-reporter",
            "karma-coverage-istanbul-reporter"
        ],
        "browserConsoleLogOptions": {
            "level": "log",
            "format": "%b %T: %m",
            "terminal": true
        }
    });
};
