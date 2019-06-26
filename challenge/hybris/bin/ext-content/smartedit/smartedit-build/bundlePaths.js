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
module.exports = function() {
    const path = require('path');

    const SE_BUILD_FOLDER = 'smartedit-build';
    const EXTENSION_CONFIG_DIR = 'smartedit-custom-build';
    const GEN_PATH = EXTENSION_CONFIG_DIR + '/generated';
    const BUNDLE_ROOT = path.join(process.cwd(), SE_BUILD_FOLDER);
    const backwardCompatibilityResults = 'diffAnalysisResults';

    return {
        bundleDirName: SE_BUILD_FOLDER,
        bundleRoot: BUNDLE_ROOT,
        external: { // Anything outside of the bundle
            grunt: {
                configDir: path.resolve(`${EXTENSION_CONFIG_DIR}/config`),
                tasksDir: path.resolve(`${EXTENSION_CONFIG_DIR}/tasks`)
            },
            generated: {
                karma: {
                    smartedit: `${GEN_PATH}/karma.smartedit.conf.js`,
                    smarteditContainer: `${GEN_PATH}/karma.smarteditContainer.conf.js`,
                    smarteditCommons: `${GEN_PATH}/karma.smarteditcommons.conf.js`
                },
                tsconfig: {
                    prodSmartedit: `${GEN_PATH}/tsconfig.prod.smartedit.json`,
                    prodSmarteditContainer: `${GEN_PATH}/tsconfig.prod.smarteditContainer.json`,
                    devSmartedit: `${GEN_PATH}/tsconfig.dev.smartedit.json`,
                    devSmarteditContainer: `${GEN_PATH}/tsconfig.dev.smarteditContainer.json`,
                    karmaSmartedit: `${GEN_PATH}/tsconfig.karma.smartedit.json`,
                    karmaSmarteditContainer: `${GEN_PATH}/tsconfig.karma.smarteditContainer.json`,
                    ide: `${GEN_PATH}/tsconfig.ide.json`,
                    smarteditForTests: `${GEN_PATH}/tsconfig.smarteditfortests.json`
                },
                webpack: {
                    prodSmartedit: `${GEN_PATH}/webpack.prod.smartedit.config.js`,
                    prodSmarteditContainer: `${GEN_PATH}/webpack.prod.smarteditContainer.config.js`,
                    devSmartedit: `${GEN_PATH}/webpack.dev.smartedit.config.js`,
                    devSmarteditContainer: `${GEN_PATH}/webpack.dev.smarteditContainer.config.js`,
                    karmaSmartedit: `${GEN_PATH}/webpack.karma.smartedit.config.js`,
                    karmaSmarteditContainer: `${GEN_PATH}/webpack.karma.smarteditContainer.config.js`,
                    smarteditForTests: `${GEN_PATH}/webpack.smarteditfortests.config.js`
                }
            }
        },
        build: {
            tsfmt: `${SE_BUILD_FOLDER}/config/tsfmt.json`,
            jshintrc: `${SE_BUILD_FOLDER}/config/.jshintrc`,
            grunt: {
                gruntLoader: path.resolve(path.join(BUNDLE_ROOT, 'config/grunt-utils/loader.js')),
                configDir: path.resolve(path.join(BUNDLE_ROOT, 'config/config')),
                gruntUtilsDir: path.resolve(path.join(BUNDLE_ROOT, 'config/grunt-utils')),
                tasksDir: path.resolve(path.join(BUNDLE_ROOT, 'config/tasks'))
            },
            util: {
                // @deprecated since 1808 - use global.smartedit.taskUtil.protractor instead.
                e2eshardPath: path.resolve(path.join(BUNDLE_ROOT, 'config/grunt-utils/task-utils/protractor'))
            }
        },
        test: {
            unit: {
                root: path.resolve(path.join(BUNDLE_ROOT, 'test/unit')),
                commonUtilModules: [
                    path.join(BUNDLE_ROOT, 'test/unit/generated/*.js'),
                    path.join(BUNDLE_ROOT, 'test/unit/*.+(js|ts)')
                ],
                smarteditThirdPartyJsFiles: [
                    path.join(BUNDLE_ROOT, 'webroot/static-resources/dist/smartedit/js/prelibraries.js')
                ],
                smarteditContainerUnitTestFiles: [
                    path.join(BUNDLE_ROOT, 'webroot/static-resources/dist/smartedit/js/thirdparties.js'),
                    path.join(BUNDLE_ROOT, 'webroot/static-resources/thirdparties/ckeditor/ckeditor.js')
                ],
                exclude: [
                    '**/sharedSmarteditForTests.ts'
                ],
            },
            e2e: {
                root: 'jsTests/e2e',
                listTpl: path.join(BUNDLE_ROOT, 'config/templates/list.html.tpl'),
                listDest: 'jsTests/e2e/list.html',
                applicationPath: 'jsTests/e2e/smartedit.html',
                fakeAngularPage: `/${SE_BUILD_FOLDER}/test/e2e/dummystorefront/fakeAngularEmptyPage.html`,
                protractor: {
                    conf: path.join(BUNDLE_ROOT, 'test/e2e/protractor/protractor-conf.js'),
                    savePath: 'jsTarget/test/junit/protractor'
                },
                pageObjects: path.join(BUNDLE_ROOT, 'test/e2e/pageObjects'),
                componentObjects: path.join(BUNDLE_ROOT, 'test/e2e/componentObjects')
            }
        },
        tools: {
            seInjectableInstrumenter: {
                js: './smartedit-build/config/tools/tsInstrument/generated/seInjectableInstrumenter.js'
            }
        },
        report: {
            backwardCompatibilityResults: backwardCompatibilityResults,
            instrument_functions_file: `${backwardCompatibilityResults}/VERSION/instrument_functions.data`,
            instrument_directives_file: `${backwardCompatibilityResults}/VERSION/instrument_directives.data`,
            instrument_service_not_exists_file: `${backwardCompatibilityResults}/VERSION/instrument_service_not_exists.data`
        },
        webAppTargetTs: 'jsTarget/web/**/*.ts'

    };

}();
