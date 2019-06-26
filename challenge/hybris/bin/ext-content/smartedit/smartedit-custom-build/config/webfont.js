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
    return {
        targets: [
            'webfont'
        ],
        config: function(data, conf) {
            const paths = require('../paths');
            return {
                webfont: {
                    expand: true,
                    src: paths.webfont.src,
                    dest: paths.webfont.dest,
                    destLess: paths.webfont.destLess,
                    options: {
                        font: 'hyicon',
                        types: 'eot,svg,ttf,woff2,woff',
                        stylesheets: ['less'],
                        engine: 'node',
                        normalize: true,
                        hashes: false,
                        embed: true,
                        autoHint: false, 
                        templateOptions: {
                            baseClass: 'hyicon',
                            classPrefix: 'hyicon-'
                        },
                        relativeFontPath: paths.webfont.relativeFontPath
                    }
                }
            };
        }
    };
};
