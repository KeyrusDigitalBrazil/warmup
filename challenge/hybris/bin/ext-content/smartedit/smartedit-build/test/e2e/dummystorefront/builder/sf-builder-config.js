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
/* jshint unused:false, undef:false, esversion: 6 */
var sfConfigManager = function() {

    function ConfigManager() {

        this.ALIASES = {
            // delay
            DEFAULT_DELAY_ALIAS: 'DELAY_NONE',
            DEMO_DELAY_ALIAS: 'DELAY_DEMO',
            // layouts
            DEFAULT_LAYOUT_ALIAS: 'STATIC_LAYOUT',
            DEMO_LAYOUT_ALIAS: 'DEMO_LAYOUT',
            // render functions
            JS_RENDERED_ALIAS: 'JS_RENDERED'
        };

        var CONF_DEFAULTS = {
            tree: {
                pageId: 'sf-builder-default-page-id',
                catalogVersion: 'sf-builder-default-Catalog-version',
                nodeType: 'root', // for selectors
                children: []
            },
            delay: {
                components: {},
                contents: {}
            }
        };

        function sanitizeNode(node) {

            // Convert Alias node to regular node
            if (node.nodeType === 'alias') {
                if (!node.alias) {
                    console.error("Alias type node is miassing alias");
                    throw node;
                }
                if (sfNodeLib) {
                    node = sfNodeLib.getNode(node.alias);
                }
            }

            // VALIDATION FOR ROOT NODE

            if (node.nodeType === 'root') {
                if (typeof node.pageId !== 'string' || node.pageId.length <= 0) {
                    console.error("Root Node missing pageId");
                    throw node;
                }
                if (typeof node.catalogVersion !== 'string' || node.catalogVersion.length <= 0) {
                    console.error("Root Node missing catalogVersion");
                    throw node;
                }
            } else { // VALIDATION FOR COMPONENT/SLOT

                if (typeof node.componentId !== 'string' || node.componentId.length <= 0) {
                    console.error("Node missing componentId");
                    throw node;
                }
                if (typeof node.componentType !== 'string' || node.componentType.length <= 0) {
                    console.error("Node missing componentType");
                    throw node;
                }
                if (node.nodeType !== 'component' && node.nodeType !== 'slot') {
                    console.error("node.nodeType must be either 'component' or 'slot' for node");
                    throw node;
                }
                if (!node.children) {
                    node.children = [];
                }
            }

            node._sanitized = true;
            return node;
        }

        // Validation

        this.getSanitizedNodeTree = function getSanitizedNodeTree(node) {
            node = sanitizeNode(node);
            if (node.children) {
                for (var i = 0; i < node.children.length; i++) {
                    node.children[i] = getSanitizedNodeTree(node.children[i]);
                }
            }
            return node;
        }.bind(this);


        // =============== DELAYS ===============

        this.delays = {}; // map<alias, map<componentId, millis>>:
        this.registerDelayStrategy = function(alias, strat) {
            this.delays[alias] = strat;
        };
        this.getDelayStrategy = function(alias) {
            if (!this.delays[alias]) {
                throw "No delay strategy found for alias: " + alias;
            }
            return this.delays[alias];
        };


        // =============== LAYOUTS ===============

        this.layouts = {}; // map<alias, tree>
        this.registerLayout = function(alias, tree) {
            tree = this.getSanitizedNodeTree(tree);
            if (this.layouts[alias]) {
                throw new Error('Error: [' + alias + '] alias is already registered.');
            }
            this.layouts[alias] = tree;
        };
        this.getLayout = function(alias) {
            if (!this.layouts[alias]) {
                throw "No layout found for alias: " + alias;
            }
            return this.layouts[alias];
        };


        // =========== RENDER_STRATEGIES ==========
        this.renderFunctions = {}; // map<alias, function>
        this.registerRenderStrategy = function(alias, strat) {
            this.renderFunctions[alias] = strat;
        };
        this.getRenderStrategy = function(alias) {
            if (!this.renderFunctions[alias]) {
                throw "No renderer strategy found for alias: " + alias;
            }
            return this.renderFunctions[alias];
        };


        this.getConfig = function(configFromStorefront) {
            var conf = {
                tree: this.getLayout(configFromStorefront.layoutAlias),
                delay: this.getDelayStrategy(configFromStorefront.delayAlias),
                renderer: this.getRenderStrategy(configFromStorefront.renderAlias),
                bundleRoot: configFromStorefront.bundleRoot
            };
            return _.defaultsDeep(conf, CONF_DEFAULTS);
        };

    }

    return new ConfigManager();

}();
