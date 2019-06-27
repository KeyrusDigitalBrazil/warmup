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
module.exports = function() {

    return {
        targets: [
            'smartEdit',
            'smartEditContainer',
            'e2e',
            'typescript'
        ],
        config: function(data, conf) {
            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: "merchandisingsmartedit API",
                    startPage: '/#/merchandisingsmartedit',
                },
                smartEdit: {
                    api: true,
                    src: [
                        'web/features/merchandisingsmartedit/**/*.+(js|ts)',
                        'web/features/merchandisingsmarteditcommons/**/*.+(js|ts)'
                    ],
                    title: 'merchandisingsmartedit'
                },
                smartEditContainer: {
                    api: true,
                    src: [
                        'web/features/merchandisingsmartedit/**/*.+(js|ts)',
                        'web/features/merchandisingsmarteditcommons/**/*.+(js|ts)'
                    ],
                    title: 'merchandisingsmarteditContainer'
                },
                e2e: {
                    title: 'How-to: e2e Test Setup',
                    src: [
                        'smartedit-custom-build/docs/e2eSetupNgdocs.js'
                    ]
                },
                typescript: {
                    title: 'TypeScript',
                    src: [
                        'smartedit-custom-build/docs/typescript.ts'
                    ]
                }
            };
        }
    };

};
