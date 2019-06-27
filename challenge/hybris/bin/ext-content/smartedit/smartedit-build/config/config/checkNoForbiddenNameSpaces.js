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
        config: function() {
            return {
                mappings: [{
                        patterns: [
                            'web/features/**/*.+(js|ts)',
                            'web/features/*.+(js|ts)',
                            'jsTests/**/*.+(js|ts)',
                            '!**/generated/**/*',
                            '!web/app/noConflict.js'
                        ],
                        //to use default set of forbidden namespaces
                        namespaces: '*',
                        level: 'FATAL'
                    },
                    {
                        patterns: [
                            'web/features/**/*.ts',
                            'web/features/*/*.ts',
                            'jsTests/**/*.ts'
                        ],
                        deprecatedSince: '1808',
                        namespaces: {
                            'angular.module': 'Smartedit DI: @SeModule, @SeDirective, @SeComponent and @SeInjectable'
                        }
                    },
                    {
                        patterns: [
                            'web/features/**/*.ts',
                            'web/features/*/*.ts',
                            '!web/features/**/*Module*.ts',
                            '!web/features/*/*Module*.ts',
                        ],
                        level: 'INFO',
                        namespaces: {
                            'useFactory': 'useFactory is part of DI and hence should only be used in Modules',
                            'useClass': 'useClass is part of DI and hence should only be used in Modules',
                            'useValue': 'useValue is part of DI and hence should only be used in Modules'
                        }
                    }
                ]
            };
        }
    };
};
