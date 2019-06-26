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

    /**
     * @ngdoc overview
     * @name compileTs(C)
     * @description
     * Compile TypeScript files to JavaScript. It compiles files individually (one TS file is compiled to one JS file); i.e. it does NOT bundle files/modules together.
     * Use it if you have individual TypeScript file(s) that you want to compile without having to use webpack.
     * The default configuration is target: ES2015 and module: CommonJS (this can be changed by configuration).
     * 
     * # compileTs Configuration
     * 
     * The ***src*** (glob, TypeScript files) and **dest** (folder) properties are mandatory.
     */
    return {
        config: function(data, conf) {
            return {
                module: null, // see ts.ModuleKind
                target: null, // see ts.ScriptTarget

                // None in default config
                src: [],

                // Must be defined by the extension
                dest: ""
            };
        }
    };
};
