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
/**
 * This file is imported by the dummy storefront index.html
 * This is the extension-specific file for registerning layouts, nodes etc...
 *
 * It is the responsibility of each extension to call sfBuilder.build(...) from this file.
 *
 * See smartedit-build/test/e2e/dummystorefront/imports to see the available 3rd party libs available.
 */


window.sfAssets = {
    LAYOUTS: {
        TEST_CMS_LAYOUT: 'TEST_CMS_LAYOUT'
    }
};

(function(sfBuilder, delayStrategyFromSessionStorage, assetsRootPath) {

    $script([
        assetsRootPath + 'test-cms-layout.js'
    ], 'bundle');

    $script.ready('bundle', function() {
        sfBuilder.build({
            // layoutAlias: sfAssets.LAYOUTS.TEST_CMS_LAYOUT,
            layoutAlias: window.sfConfigManager.ALIASES.DEFAULT_LAYOUT_ALIAS,
            renderAlias: window.sfConfigManager.ALIASES.JS_RENDERED_ALIAS
        });
    });

}(window.sfBuilder, window.delays, window.sfAssetsRootPath));
