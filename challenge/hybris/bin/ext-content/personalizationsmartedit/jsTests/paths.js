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
/* jshint unused:false, undef:false */
module.exports = function() {

    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to personaliztionsmartedit, happens to personalizationsmarteditContainer
     *  Try to avoid special cases and exceptions
     */
    var lodash = require('lodash');
    var paths = {};

    // ################## CONFIG ##################

    paths.config = {};
    paths.config.root = 'jsTests/config';
    paths.config.protractorConf = paths.config.root + '/protractor-conf.js';

    // ################## TESTS ##################

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.reports = paths.tests.root + '/reports';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.personalizationcommonsTestsRoot = paths.tests.testsRoot + '/personalizationcommons';
    paths.tests.personalizationsmarteditTestsRoot = paths.tests.testsRoot + '/personalizationsmartedit';
    paths.tests.personalizationsmarteditContainerTestsRoot = paths.tests.testsRoot + '/personalizationsmarteditcontainer';
    paths.tests.personalizationcommonsUnitTestsRoot = paths.tests.personalizationcommonsTestsRoot + '/unit';
    paths.tests.personalizationsmarteditUnitTestsRoot = paths.tests.personalizationsmarteditTestsRoot + '/unit';
    paths.tests.personalizationsmarteditContainerUnitTestsRoot = paths.tests.personalizationsmarteditContainerTestsRoot + '/unit';
    paths.tests.personalizationcommons2eTestsRoot = paths.tests.personalizationcommonsTestsRoot + '/e2e';
    paths.tests.personalizationsmartedite2eTestsRoot = paths.tests.personalizationsmarteditTestsRoot + '/e2e';
    paths.tests.personalizationsmarteditContainere2eTestsRoot = paths.tests.personalizationsmarteditContainerTestsRoot + '/e2e';

    paths.tests.personalizationcommonsUnitTestFiles = paths.tests.personalizationcommonsUnitTestsRoot + '/features/**/*.js';
    paths.tests.personalizationcommonsTSUnitTestFiles = paths.tests.personalizationcommonsUnitTestsRoot + '/features/**/*.ts';
    paths.tests.personalizationsmarteditUnitTestFiles = paths.tests.personalizationsmarteditUnitTestsRoot + '/features/**/*.js';
    paths.tests.personalizationsmarteditTSUnitTestFiles = paths.tests.personalizationsmarteditUnitTestsRoot + '/features/**/*.ts';
    paths.tests.personalizationsmarteditContainerUnitTestFiles = paths.tests.personalizationsmarteditContainerUnitTestsRoot + '/features/**/*.js';
    paths.tests.personalizationsmarteditContainerTSUnitTestFiles = paths.tests.personalizationsmarteditContainerUnitTestsRoot + '/features/**/*.ts';
    paths.tests.personalizationcommonse2eTestFiles = paths.tests.personalizationcommonse2eTestsRoot + '/**/*Test.js';
    paths.tests.personalizationsmartedite2eTestFiles = paths.tests.personalizationsmartedite2eTestsRoot + '/**/*Test.js';
    paths.tests.personalizationsmarteditContainere2eTestFiles = paths.tests.personalizationsmarteditContainere2eTestsRoot + '/**/*Test.js';

    paths.tests.personalizationsmarteditSpecBundle = paths.tests.personalizationsmarteditUnitTestsRoot + '/specBundle.ts';
    paths.tests.personalizationsmarteditContainerSpecBundle = paths.tests.personalizationsmarteditContainerUnitTestsRoot + '/specBundle.ts';


    // ################## SOURCES ##################

    paths.sources = {};

    paths.sources.root = 'web';
    paths.sources.features = paths.sources.root + '/features';

    paths.sources.images = paths.sources.root + '/webroot/icons/**/*';
    paths.sources.commonsFiles = paths.sources.features + '/personalizationcommons/**/*.js';
    paths.sources.commonsTSFiles = paths.sources.features + '/personalizationcommons/**/*.ts';
    paths.sources.personalizationsmarteditFiles = paths.sources.features + '/personalizationsmartedit/**/*.js';
    paths.sources.personalizationsmarteditTSFiles = paths.sources.features + '/personalizationsmartedit/**/*.ts';
    paths.sources.personalizationsmarteditContainerFiles = paths.sources.features + '/personalizationsmarteditcontainer/**/*.js';
    paths.sources.personalizationsmarteditContainerTSFiles = paths.sources.features + '/personalizationsmarteditcontainer/**/*.ts';

    // ################## TARGET ##################

    paths.target = {};

    paths.target.features = 'jsTarget/web/features';

    paths.target.commonsTemplatesFile = paths.target.features + '/personalizationcommons/**/templates.js';
    paths.target.personalizationsmarteditTemplatesFile = paths.target.features + '/personalizationsmartedit/**/templates.js';
    paths.target.personalizationsmarteditContainerTemplatesFile = paths.target.features + '/personalizationsmarteditcontainer/**/templates.js';
    paths.target.featureExtensionsSmartEditImport = paths.target.features + '/personalizationsmartedit/PersonalizationsmarteditApp.ts';
    paths.target.featureExtensionsSmartEditContainerImport = paths.target.features + '/personalizationsmarteditcontainer/PersonalizationsmarteditcontainerApp.ts';

    paths.target.commonsFiles = paths.target.features + '/personalizationcommons/**/*.js';
    paths.target.commonsTSFiles = paths.target.features + '/personalizationcommons/**/*.ts';

    paths.target.personalizationsmarteditFiles = paths.target.features + '/personalizationsmartedit/**/*.js';
    paths.target.personalizationsmarteditTSFiles = paths.target.features + '/personalizationsmartedit/**/*.ts';

    paths.target.personalizationsmarteditContainerFiles = paths.target.features + '/personalizationsmarteditcontainer/**/*.js';
    paths.target.personalizationsmarteditContainerTSFiles = paths.target.features + '/personalizationsmarteditcontainer/**/*.ts';

    // ################## MOCKS ##################

    paths.mocks = {};
    paths.mocks.root = 'jsTests';

    paths.mocks.dataRoot = paths.mocks.root + '/mockData';
    paths.mocks.serviceRoot = paths.mocks.root + '/mockServices';
    paths.mocks.daoRoot = paths.mocks.root + '/mockDao';

    paths.mocks.dataFiles = paths.mocks.dataRoot + '/**/*.js';
    paths.mocks.serviceFiles = paths.mocks.serviceRoot + '/**/*.js';
    paths.mocks.daoFiles = paths.mocks.daoRoot + '/**/*.js';


    // ########## PAGE OBJECTS / COMPONENT OBJECTS ##########

    paths.testObjects = {};

    paths.testObjects.pageObjectsRoot = 'jsTests/pageObjects';
    paths.testObjects.componentObjectsRoot = 'jsTests/componentObjects';

    paths.testObjects.pageObjectsFiles = paths.testObjects.pageObjectsRoot + '/**/*.js';
    paths.testObjects.componentObjectFiles = paths.testObjects.componentObjectsRoot + '/**/*.js';



    // ################## MISC ##################

    paths.thirdPartiesRoot = 'buildArtifacts/static-resources/thirdparties';
    paths.seLibrariRoot = 'buildArtifacts/seLibraries';

    // ================================================================================================================
    // ================================================================================================================
    // ================================================================================================================

    paths.getPersonalizationsmarteditKarmaConfFiles = function getPersonalizationsmarteditKarmaConfFiles() {
        return lodash.concat(
            global.smartedit.bundlePaths.test.unit.smarteditThirdPartyJsFiles,
            global.smartedit.bundlePaths.test.unit.commonUtilModules,
            paths.mocks.dataFiles,
            paths.mocks.daoFiles,
            paths.mocks.serviceFiles,
            paths.testObjects.componentObjectFiles,
            paths.target.commonsTemplatesFile,
            paths.target.personalizationsmarteditTemplatesFile,
            paths.sources.commonsFiles,
            paths.sources.commonsTSFiles,
            paths.sources.personalizationsmarteditFiles,
            paths.sources.personalizationsmarteditTSFiles,
            paths.tests.personalizationsmarteditUnitTestFiles,
            paths.tests.personalizationsmarteditTSUnitTestFiles,
            // paths.tests.personalizationsmarteditSpecBundle,
            // Images
            {
                pattern: paths.sources.images,
                watched: false,
                included: false,
                served: true
            }
        );
    };

    paths.getPersonalizationsmarteditContainerKarmaConfFiles = function getPersonalizationsmarteditContainerKarmaConfFiles() {
        return lodash.concat(
            global.smartedit.bundlePaths.test.unit.smarteditContainerUnitTestFiles,
            global.smartedit.bundlePaths.test.unit.commonUtilModules,
            paths.mocks.dataFiles,
            paths.mocks.daoFiles,
            paths.mocks.serviceFiles,
            paths.testObjects.componentObjectFiles,
            paths.target.personalizationsmarteditContainerTemplatesFile,
            paths.target.commonsTemplatesFile,
            paths.sources.personalizationsmarteditContainerFiles,
            paths.sources.personalizationsmarteditContainerTSFiles,
            paths.sources.commonsFiles,
            paths.sources.commonsTSFiles,
            paths.tests.personalizationsmarteditContainerUnitTestFiles,
            paths.tests.personalizationsmarteditContainerTSUnitTestFiles,
            // paths.tests.personalizationsmarteditContainerSpecBundle,
            // Images
            {
                pattern: paths.sources.images,
                watched: false,
                included: false,
                served: true
            }
        );
    };

    paths.getE2eFiles = function getE2eFiles() {
        return [
            //paths.tests.personalizationsmartedite2eTestFiles,
            //paths.tests.personalizationsmarteditContainere2eTestFiles
        ];
    };

    return paths;

}();
