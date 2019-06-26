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
module.exports = function() {

    return {
        targets: ['e2eSetup'],
        config: function(data, conf) {
            conf.e2eSetup = {

                // base smartedit files
                "headerContent": `<script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/backendMocksUtils.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/PageContentSlotsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/PageContentComponentSlotsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/pageContentSlotsContainerMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/pageMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/siteAndCatalogsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/productMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/i18nMock.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/restrictionsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/LanguagesMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/synchronizationMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/configurationMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/componentMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/perspectivesMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/navigationNodesMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/mediaMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/userGroupsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/permissionsMocks.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/whoAmIMock.js"></script>
        <script src="/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/goToCustomView.js"></script>
        <style>
            .offset{
                margin-top:50px
            }
            .y-add-btn {
                height: inherit !important;
            }
        </style>`,

                // path and file to output to
                // the path must be either absolute or relative to the root of the extension
                "dest": "jsTests/tests/personalizationpromotionssmarteditContainer/e2e/features/app.html"
            };

            return conf;
        }
    };
};
