/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
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
 * See _shared/imports to see the available 3rd party libs available.
 */


window.sfAssets = {
    LAYOUTS: {}
};

(function(sfBuilder, delayStrategyFromSessionStorage, assetsRootPath) {

    $script([
        assetsRootPath + 'test-cms-layout.js'
    ], 'bundle');

    $script.ready('bundle', function() {
        sfBuilder.build({
            layoutAlias: window.storefrontConfigManager.ALIASES.DEFAULT_LAYOUT_ALIAS,
            renderAlias: window.storefrontConfigManager.ALIASES.JS_RENDERED_ALIAS
        });
    });

}(window.StorefrontBuilder, window.delays, window.sfAssetsRootPath));
