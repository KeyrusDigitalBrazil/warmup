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
     *  What happens to cmssmartedit, happens to cmssmarteditContainer
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
    paths.tests.cmssmarteditTestsRoot = paths.tests.testsRoot + '/cmssmartedit';
    paths.tests.cmssmarteditContainerTestsRoot = paths.tests.testsRoot + '/cmssmarteditContainer';
    paths.tests.cmssmarteditUnitTestsRoot = paths.tests.cmssmarteditTestsRoot + '/unit';
    paths.tests.cmssmarteditContainerUnitTestsRoot = paths.tests.cmssmarteditContainerTestsRoot + '/unit';
    paths.tests.cmssmarteditContainere2eTestsRoot = paths.tests.cmssmarteditContainerTestsRoot + '/e2e';

    paths.tests.cmssmarteditSpecBundle = paths.tests.cmssmarteditUnitTestsRoot + '/specBundle.ts';
    paths.tests.cmssmarteditContainerSpecBundle = paths.tests.cmssmarteditContainerUnitTestsRoot + '/specBundle.ts';
    paths.tests.cmssmartedite2eTestFiles = paths.tests.root + '/e2e/**/*Test.js';
    paths.tests.cmssmarteditContainere2eTestFiles = paths.tests.cmssmarteditContainere2eTestsRoot + '/**/*Test.js';

    // ################## SOURCES ##################

    paths.sources = {};

    paths.sources.root = 'web';
    paths.sources.features = paths.sources.root + '/features';

    paths.sources.images = paths.sources.root + '/webroot/images/**/*';
    paths.sources.commonsFiles = paths.sources.features + '/cmscommons/**/*.js';
    paths.sources.commonsTSFiles = paths.sources.features + '/cmscommons/**/*.ts';
    paths.sources.cmssmarteditFiles = paths.sources.features + '/cmssmartedit/**/*.js';
    paths.sources.cmssmarteditTSFiles = paths.sources.features + '/cmssmartedit/**/*.ts';
    paths.sources.cmssmarteditContainerFiles = paths.sources.features + '/cmssmarteditContainer/**/*.js';
    paths.sources.cmssmarteditContainerTSFiles = paths.sources.features + '/cmssmarteditContainer/**/*.ts';

    // ################## TARGET ##################

    paths.target = {};

    paths.target.features = 'jsTarget/web/features';

    paths.target.commonsTemplatesFile = paths.target.features + '/cmscommons/**/templates.js';
    paths.target.cmssmarteditTemplatesFile = paths.target.features + '/cmssmartedit/**/templates.js';
    paths.target.cmssmarteditContainerTemplatesFile = paths.target.features + '/cmssmarteditContainer/**/templates.js';
    paths.target.featureExtensionsSmartEditImport = paths.target.features + '/cmssmartedit/cmssmarteditAppModule.ts';
    paths.target.featureExtensionsSmartEditContainerImport = paths.target.features + '/cmssmarteditContainer/cmssmarteditContainerAppModule.ts';

    paths.target.commonsFiles = paths.target.features + '/cmscommons/**/*.js';
    paths.target.commonsTSFiles = paths.target.features + '/cmscommons/**/*.ts';

    paths.target.cmssmarteditFiles = paths.target.features + '/cmssmartedit/**/*.js';
    paths.target.cmssmarteditTSFiles = paths.target.features + '/cmssmartedit/**/*.ts';

    paths.target.cmssmarteditContainerFiles = paths.target.features + '/cmssmarteditContainer/**/*.js';
    paths.target.cmssmarteditContainerTSFiles = paths.target.features + '/cmssmarteditContainer/**/*.ts';

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

    paths.cmssmarteditKarmaConfFiles = lodash.concat(
        global.smartedit.bundlePaths.test.unit.smarteditThirdPartyJsFiles,
        global.smartedit.bundlePaths.test.unit.commonUtilModules,
        paths.mocks.dataFiles,
        paths.mocks.daoFiles,
        paths.mocks.serviceFiles,
        paths.testObjects.componentObjectFiles,
        paths.target.commonsTemplatesFile,
        paths.target.cmssmarteditTemplatesFile,
        paths.target.commonsFiles,
        paths.target.cmssmarteditFiles,
        paths.tests.cmssmarteditSpecBundle, {
            pattern: paths.sources.images,
            watched: false,
            included: false,
            served: true
        }
    );

    paths.cmssmarteditContainerKarmaConfFiles = lodash.concat(
        global.smartedit.bundlePaths.test.unit.smarteditContainerUnitTestFiles,
        global.smartedit.bundlePaths.test.unit.commonUtilModules,
        paths.mocks.dataFiles,
        paths.mocks.daoFiles,
        paths.mocks.serviceFiles,
        paths.testObjects.componentObjectFiles,
        paths.target.cmssmarteditContainerTemplatesFile,
        paths.target.commonsTemplatesFile,
        paths.target.cmssmarteditContainerFiles,
        paths.target.commonsFiles,
        paths.tests.cmssmarteditContainerSpecBundle, {
            pattern: 'web/webroot/images/**/*',
            watched: false,
            included: false,
            served: true
        }
    );

    paths.e2eFiles = [
        paths.tests.cmssmartedite2eTestFiles,
        paths.tests.cmssmarteditContainere2eTestFiles
        // [ 'jsTests/**/e2e/**/*slotContextualMenuTest.js' ]
    ];

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        cmssmarteditDirName: 'cmssmartedit',
        cmssmarteditcontainerDirName: 'cmssmarteditcontainer'
    };

    return paths;

}();
