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
/* jshint unused:false, undef:false */ // ===================== CONF =====================
//
// tree,     The root of the node tree
// delay     An object containing 2 functions
//              getComponentDelay(componentId): Number  //millis
//              getContentDelay(componentId): Number  //millis
// renderer


// ===================== ROOT =====================
// pageId
// catalogVersion
// nodeType = 'root'
// children


// ===================== ALIAS =====================
//
// alias            // registered node alias in node library
// nodeType = 'alias'


// ================= SLOT/COMPONENT ================
//
// componentId
// componentType
// nodeType = 'slot' || 'component'
// catalogVersion
// children
// view: {
//     class
//     hasTemplate
//     basePath
//     showHeader
// }

var sfBuilder = function() {

    /**
     * StorefrontBuilder is a tool to generate and manipulate a storefront DOM to simulate
     * letious types of storefront and front end technologies/stacks.
     *
     * It works by having a tree of nodes, which represent a virtual DOM
     * The root node contains the page information, and and children of the root node
     * represent either components of slots.
     *
     * The builder also takes a map of delays. There are 2 types of delays, component delays
     * and content delays. Component delays represent how long after requesting th component
     * be added to the DOM will the element actually be added to the DOM. Content delays represent
     * how long after the Component element has been added to the DOM, will the component
     * content (for example, the html template) be inserted into the component element.
     *
     * @letructor
     */
    function StorefrontBuilder() {

        var domUtils = new DOMUtils();
        var treeUtils = new TreeUtils();
        var delayUtils; // = new delayUtils();

        var _conf;


        /**
         * Removes any existing components on the page, and stops any timers representing
         * pending operations (like a delayed component being aded to the page)
         * Then updates the page ID, and builds all the storefront tree.
         *
         * @param conf A config object with tree, delays and renderer strategy
         */
        this.build = function(conf) {

            conf = conf || {};
            conf = sfConfigManager.getConfig(conf);

            // Validation
            this.config = _.cloneDeep(conf);
            _conf = this.config; // need access from a function not on "this"

            // Initialization
            updateNodeIdChain(this.config.tree);
            delayUtils = new DelayUtils(this.config.delay);
            applyStorefrontRenderStrategy(conf);

            // Cleanup anything existing
            clearDelayedExecutions();
            domUtils.clearComponents();

            // Build Storefront
            domUtils.updatePageIdAttribute(this.config.tree.pageId, this.config.tree.catalogVersion);
            this.config.tree.children.forEach(addNode);
        };


        /**
         * Update the current storefront page id attributes
         * @param newPageId
         */
        this.updatePageId = function(newPageId, newCatalogVersion) {
            if (newPageId) {
                if (newPageId.indexOf(' ') !== -1) {
                    throw "StorefronBuilder.updatePageId() -  page Id cannot contains spaces: " + newPageId;
                }
                this.config.tree.pageId = newPageId;
            }
            if (newCatalogVersion) {
                this.config.tree.catalogVersion = newCatalogVersion;
            }
            domUtils.updatePageIdAttribute(this.config.tree.pageId, this.config.tree.catalogVersion);
        };


        /**
         * Rerender a branch of the tree, given by componentId
         * Removes the component (and all children) and re-creates it and re-adds it using
         * the registered delay map from the config
         *
         * @param componentId
         * @param componentType
         * @param parentComponentIds
         * @returns {boolean} True
         */
        this.rerender = function(componentId, componentType, parentComponentIds) {
            queuedOperations.forEach(function(op) {
                op();
            });
            queuedOperations.length = 0;

            // If no componentId provided, assume whole trees and rebuild all the nodes
            if (!componentId) {
                clearDelayedExecutions();
                domUtils.clearComponents();
                this.config.tree.children.forEach(addNode);
            } else {
                var idChain = parentComponentIds ? parentComponentIds + ',' + componentId : componentId;
                treeUtils.treeForEach(this.config.tree, function(node, index) {
                    var strChain = node.idChainFromRootNode.toString();
                    if (strChain.endsWith(idChain)) {
                        var element = createSelectorFromIdChain(node.idChainFromRootNode);
                        $(element).remove();
                        addNode(node, index);
                    }
                });
                return true;
            }
        };


        /**
         * Remove the component ID, removes all instances if parentComponentIds is omitted
         * @param componentId
         * @param parentComponentIds
         */
        this.removeComponent = function(componentId, parentComponentIds) {
            var idChain = parentComponentIds ? parentComponentIds + ',' + componentId : componentId;

            function breadthFirstRemoval(node) {
                var str = node.idChainFromRootNode.toString();
                if (str.endsWith(idChain)) {
                    return true; // true for: please delete me
                } else {
                    var indicesToRemove = [];
                    node.children.forEach(function(child, index) {
                        if (breadthFirstRemoval(child)) {
                            indicesToRemove.push(index);
                            var selectorForChildNode = createSelectorFromIdChain(child.idChainFromRootNode);
                            $(selectorForChildNode).remove();
                        }
                    });
                    indicesToRemove.forEach(function(indicy, index) {
                        node.children.splice(indicy - index, 1);
                    });
                }
                return false; // false for continue traversing the tree
            }
            breadthFirstRemoval(this.config.tree);

            updateNodeIdChain(this.config.tree);

        };


        /**
         * Add a new component to the given parent ID.
         * @param presetAlias A component alias defined in the node library
         * @param parentComponentIds
         */
        this.addComponent = function(presetAlias, parentComponentIds) {
            var libNode = sfNodeLib.getNode(presetAlias);

            if (!parentComponentIds) {
                var root = this.config.tree;
                var newIdChain = _.cloneDeep(root.idChainFromRootNode);
                var newNode = _.cloneDeep(libNode);
                newIdChain.push(newNode.componentId);
                newNode.idChainFromRootNode = newIdChain;
                root.children.push(newNode);
                addNode(newNode, root.children.length - 1);
            } else {
                treeUtils.treeForEach(this.config.tree, function(node) {
                    var strChain = node.idChainFromRootNode.toString();
                    if (strChain.endsWith(parentComponentIds)) {
                        // we found an insertion point!
                        if (typeof node.children === 'undefined') {
                            node.children = [];
                        }
                        var newIdChain = _.cloneDeep(node.idChainFromRootNode);
                        var newNode = _.cloneDeep(libNode);
                        newIdChain.push(newNode.componentId);
                        newNode.idChainFromRootNode = newIdChain;
                        node.children.push(newNode);
                        addNode(newNode, node.children.length - 1);
                        throw 'break'; // prevent recursive stack overflow
                    }
                });
            }
        };

        /**
         * Remove a node from the tree, but don't remove the DOM element
         * Used mainly in rerender fixtures
         * @param componentId
         * @param parentComponentIds
         */
        this.removeNodesFromTree = function(componentId, parentComponentIds) {
            var selector = parentComponentIds ? (parentComponentIds + ',' + componentId) : componentId;
            treeUtils.treeForEach(this.config.tree, function f(node) {
                var indicesToRemove = [];
                node.children.forEach(function(child, index) {
                    var strChain = child.idChainFromRootNode.toString();
                    if (strChain.endsWith(selector)) {
                        indicesToRemove.push(index);
                    }
                });
                indicesToRemove.forEach(function(storedIndex, index) {
                    node.children.splice(storedIndex - index, 1);
                });
            }.bind(this));
        }.bind(this);

        /**
         * Add a node to the tree, but don't create the DOM element
         * Used mainly in rerender fixtures
         * @param alias
         * @param parentComponentIds
         * @param position
         */
        this.addNodeToTree = function(alias, parentComponentIds, position) {
            position = position || 0;
            var libNode = sfNodeLib.getNode(alias);
            if (!parentComponentIds) {
                var root = this.config.tree;
                var newIdChain = _.cloneDeep(root.idChainFromRootNode);
                var newNode = _.cloneDeep(libNode);
                newIdChain.push(newNode.componentId);
                newNode.idChainFromRootNode = newIdChain;
                root.children.splice(position, 0, newNode);
            } else {
                treeUtils.treeForEach(this.config.tree, function(node) {
                    var strChain = node.idChainFromRootNode.toString();
                    if (strChain.endsWith(parentComponentIds)) {
                        // we found an insertion point!
                        if (typeof node.children === 'undefined') {
                            node.children = [];
                        }
                        var newIdChain = _.cloneDeep(node.idChainFromRootNode);
                        var newNode = _.cloneDeep(libNode);
                        newIdChain.push(newNode.componentId);
                        newNode.idChainFromRootNode = newIdChain;
                        node.children.splice(position, 0, newNode);
                        throw 'break'; // prevent recursive stack overflow
                    }
                });
            }
        };

        var queuedOperations = [];
        this.queueRemoveComponent = function(componentId, parentComponentIds) {
            queuedOperations.push(function() {
                return this.removeNodesFromTree(componentId, parentComponentIds);
            }.bind(this));
        }.bind(this);
        this.queueAddComponent = function(alias, parentComponentIds, position) {
            queuedOperations.push(function() {
                return this.addNodeToTree(alias, parentComponentIds, position);
            }.bind(this));
        }.bind(this);

        /**
         * Move an existing component in a parent, to the new position (index) in the same parent
         * @param componentId
         * @param parentComponentIds
         * @param newPosition
         */
        this.moveComponent = function(componentId, parentComponentIds, newPosition) {
            treeUtils.treeForEachParent(this.config.tree, parentComponentIds, function(parentNode) {

                if (parentNode.children.length < 2) {
                    return; // nothing to do
                }

                var parentElement = $(createSelectorFromIdChain(parentNode.idChainFromRootNode));
                if (parentElement.length === 0) {
                    throw "sfBuilder.moveComponent() - unable to find parent";
                }

                var nodeToMove = null;
                var oldPosition = -1;
                parentNode.children.forEach(function(node, index) {
                    if (node.componentId === componentId) {
                        nodeToMove = node;
                        oldPosition = index;
                    }
                });

                if (nodeToMove === null) {
                    throw 'moveComponent() - could not find component: ' + componentId;
                }

                if (oldPosition === newPosition || oldPosition === -1) {
                    return; // nothing to do
                }

                var elementToMove = parentElement.children(domUtils.SELECTORS.COMPONENT_AT_INDEX(oldPosition));

                // remove ------------
                elementToMove.detach();
                parentNode.children.splice(oldPosition, 1);


                // add ------------
                if (newPosition === 0) {
                    parentElement.children(domUtils.SELECTORS.ANY_COMPONENT_INDEX).first().before(elementToMove);
                } else {
                    var c = parentElement.children(domUtils.SELECTORS.ANY_COMPONENT_INDEX);
                    var onTop = $(c.get(newPosition - 1));
                    onTop.after(elementToMove);
                }
                newPosition = Math.max(newPosition, 0);
                newPosition = Math.min(newPosition, parentNode.children.length);
                parentNode.children.splice(newPosition, 0, nodeToMove);

                // update attributes
                var ctr = 0;
                parentElement.children(domUtils.SELECTORS.ANY_COMPONENT_INDEX).each(function(index, childElement) {
                    $(childElement).attr(domUtils.ATTRIBUTES.UNIQUE_INDEX, ctr++);
                });

            });
        };


        /**
         * Transform an existing node to new properties - id, type, css class etc...
         * @param componentId
         * @param parentComponentIds
         * @param presetAlias
         */
        this.transformComponent = function(componentId, parentComponentIds, presetAlias) {
            var libNode = sfNodeLib.getNode(presetAlias);
            var selector = parentComponentIds ? (parentComponentIds + ',' + componentId) : componentId;
            treeUtils.treeForEach(this.config.tree, function(node, index) {
                var strChain = node.idChainFromRootNode.toString();
                if (strChain.endsWith(selector)) {
                    var existingElement = $(createSelectorFromIdChain(node.idChainFromRootNode));
                    node.componentId = libNode.componentId;
                    node.componentType = libNode.componentType;
                    node.nodeType = libNode.nodeType;
                    node.view = libNode.view;
                    node.children = libNode.children;
                    updateNode(node, existingElement, index);
                }
            });
        };

        var componentLoadedCallbackRegistry = {};
        this.onComponentLoaded = function(componentId, fn) {
            componentLoadedCallbackRegistry[componentId] = fn;
        };

        function triggerOnComponentLoadedCallback(componentId) {
            if (componentLoadedCallbackRegistry[componentId]) {
                componentLoadedCallbackRegistry[componentId]();
            }
        }


        function createParentSelectorFromNode(node) {
            var ids = node.idChainFromRootNode || [];
            ids = _.cloneDeep(ids);
            ids.pop();
            return createSelectorFromIdChain(ids);
        }

        function createSelectorFromIdChain(ids) {
            var result = "";
            ids.forEach(function(id) {
                if (id) {
                    result = result + ' ' + domUtils.SELECTORS.COMPONENT_WITH_ID(id);
                }
            });
            return result;
        }

        function nodeIdCollector(node, value) {
            value = value || [];
            value = _.cloneDeep(value);
            if (node.componentId) {
                value.push(node.componentId);
            }
            return value;
        }

        function updateNodeIdChain(tree) {
            treeUtils.treeForEachWithDepthFirstCollector(tree, function(node, cumulatedValue) {
                node.idChainFromRootNode = _.cloneDeep(cumulatedValue);
            }, nodeIdCollector, []);
        }

        var timeouts = [];

        function executeWithDelay(fn, timeout) {
            timeouts.push(window.setTimeout(fn, timeout));
        }

        function clearDelayedExecutions() {
            timeouts.forEach(function(int) {
                window.clearTimeout(int);
            });
            timeouts.splice(0, timeouts.length);
            timeouts = [];
        }

        function applyStorefrontRenderStrategy(conf) {
            var namespace = window.smartedit || {};
            namespace.renderComponent = conf.renderer;
        }


        function insertElementAtNodeIndex(node, element, index) {
            var parentElement = createParentSelectorFromNode(node);
            if (parentElement) {
                parentElement = $(parentElement);
            } else {
                parentElement = domUtils.getContentRoot();
            }
            var siblings = parentElement.children(domUtils.SELECTORS.ANY_COMPONENT_INDEX);
            if (siblings.length === 0) {
                parentElement.append(element);
                return;
            }
            for (var i = 0; i < siblings.length; i++) {
                var jqSibling = $(siblings[i]);
                var currentIndex = Number(jqSibling.attr(domUtils.ATTRIBUTES.UNIQUE_INDEX));
                if (currentIndex > index) {
                    jqSibling.before(element);
                    return;
                }
            }
            // if we get to here its the last element
            parentElement.append(element);
        }

        function updateNode(node, element, index) {
            updateElement(node, element);
            addAttributes(node, element, index);
        }

        function addNode(node, index) {
            var newElement = createElement(node);
            addAttributes(node, newElement, index);
            var componentLoadDelay = delayUtils.getComponentLoadDelay(node.componentId);
            var addNodeFn = addElementToDOMAndProcessNodeChildren.bind(this, newElement, node, index);
            if (componentLoadDelay <= 0) {
                addNodeFn();
            } else {
                executeWithDelay(addNodeFn, componentLoadDelay);
            }
        }

        function addElementToDOMAndProcessNodeChildren(element, node, index) {
            insertElementAtNodeIndex(node, element, index);
            addNodeChildren(node);
        }

        function addNodeChildren(parent) {
            parent.children = parent.children || [];
            parent.children.forEach(function(component, index) {
                addNode(component, index);
            });
        }

        function getTemplateUrl(node) {
            var defaultTemplatePath = './layouts/';
            var base;
            if (node.view && node.view.basePath) {
                base = node.view.basePath;
            }
            base = base || defaultTemplatePath;
            if (!base.endsWith('/')) {
                base = base + '/';
            }
            var templateUrl = base + node.nodeType + '/' + node.componentId + '.html';
            return templateUrl.replace("BUNDLE_ROOT_PLACEHOLDER", _conf.bundleRoot + "/test/e2e/dummystorefront/layouts");
        }

        function updateElement(node, element) {
            element.empty();
            var contentRoot = $('<div>');
            if (!(node.view && node.view.showHeader === false)) {
                element.html(node.componentId + ' - ' + node.componentType);
            }
            if (node.view && node.view.hasTemplate) {
                contentRoot.load(getTemplateUrl(node));
            }
            var loadContentFunction = function() {
                element.append(contentRoot);
            };
            var contentLoadDelay = delayUtils.getContentLoadDelay(node.componentId);
            if (contentLoadDelay <= 0) {
                loadContentFunction();
            } else {
                executeWithDelay(loadContentFunction, contentLoadDelay);
            }
            return element;
        }

        function createElement(node) {
            var element = $('<div>');
            var contentRoot = $('<div>');
            if (!(node.view && node.view.showHeader === false)) {
                element.html(node.componentId + ' - ' + node.componentType);
            }
            if (node.view && node.view.hasTemplate) {
                contentRoot.load(getTemplateUrl(node), function() {
                    triggerOnComponentLoadedCallback(node.componentId);
                });
            }
            var loadContentFunction = function() {
                element.append(contentRoot);
            };
            var contentLoadDelay = delayUtils.getContentLoadDelay(node.componentId);
            if (contentLoadDelay <= 0) {
                loadContentFunction();
            } else {
                executeWithDelay(loadContentFunction, contentLoadDelay);
            }
            return element;
        }

        function addAttributes(node, element, index) {
            if (node.nodeType === 'slot') {
                element.attr('class', domUtils.COMPONENT_CLASSES.SLOT + (node.view && node.view.class ? node.view.class : ""));
            } else if (node.nodeType === 'component') {
                element.attr('class', domUtils.COMPONENT_CLASSES.COMPONENT + (node.view && node.view.class ? node.view.class : ""));
            }
            element.attr('id', node.componentId);
            element.attr(domUtils.ATTRIBUTES.COMPONENT_ID, node.componentId);
            element.attr(domUtils.ATTRIBUTES.COMPONENT_UUID, node.componentId);
            element.attr(domUtils.ATTRIBUTES.COMPONENT_TYPE, node.componentType);
            element.attr(domUtils.ATTRIBUTES.COMPONENT_CATALOG_VERSION, node.catalogVersion || _conf.tree.catalogVersion);
            element.attr(domUtils.ATTRIBUTES.UNIQUE_INDEX, index);
        }
    }

    function DelayUtils(delayConf) {
        this.getComponentLoadDelay = function getComponentLoadDelay(componentId) {
            return delayConf.getComponentDelay(componentId) || 0;
        };
        this.getContentLoadDelay = function getContentLoadDelay(componentId) {
            return delayConf.getContentDelay(componentId) || 0;
        };
    }


    function DOMUtils() {


        this.COMPONENT_CLASSES = {
            COMPONENT: ' component smartEditComponent ',
            SLOT: ' slot smartEditComponent '
        };

        this.ATTRIBUTES = {
            PAGE_UID_PREFIX: 'smartedit-page-uid-',
            PAGE_UUID_PREFIX: 'smartedit-page-uuid-',
            PAGE_CATALOG_VERSION_PREFIX: 'smartedit-catalog-version-uuid-',
            COMPONENT_ID: 'data-smartedit-component-id',
            COMPONENT_UUID: 'data-smartedit-component-uuid',
            COMPONENT_TYPE: 'data-smartedit-component-type',
            COMPONENT_CATALOG_VERSION: 'data-smartedit-catalog-version-uuid',
            UNIQUE_INDEX: 'data-component-index'
        };

        this.SELECTORS = {
            BODY: 'body',
            INSERTION_POINT: '#insertion-point',
            SLOTS: '.smartEditComponent[data-smartedit-component-type=ContentSlot]',
            COMPONENTS: '.smartEditComponent[data-smartedit-component-type!=ContentSlot]',
            SLOTS_AND_COMPONENTS: '.smartEditComponent',
            ANY_COMPONENT_INDEX: '[' + this.ATTRIBUTES.UNIQUE_INDEX + ']',
            COMPONENT_WITH_ID: function(id) {
                return this.SELECTORS.SLOTS_AND_COMPONENTS + '[' + this.ATTRIBUTES.COMPONENT_ID + '=' + id + ']';
            }.bind(this),
            COMPONENT_AT_INDEX: function(index) {
                return '[' + this.ATTRIBUTES.UNIQUE_INDEX + '=' + index + ']';
            }.bind(this),
            PAGE_ID_INSERTION_POINT: '#page-id-insertion-point'
        };

        this.getContentRoot = function() {
            return $(this.SELECTORS.INSERTION_POINT);
        };

        // ========================================================

        this.clearComponents = function() {
            this.getContentRoot().find(this.SELECTORS.SLOTS_AND_COMPONENTS).remove();
        };

        this.updatePageIdAttribute = function(pageId, catalogVersion) {
            var catalogClassName = this.ATTRIBUTES.PAGE_CATALOG_VERSION_PREFIX + catalogVersion;
            var uid = this.ATTRIBUTES.PAGE_UID_PREFIX + pageId;
            var uuid = this.ATTRIBUTES.PAGE_UUID_PREFIX + pageId;
            var rejectedClassPrefixes = [
                this.ATTRIBUTES.PAGE_UID_PREFIX,
                this.ATTRIBUTES.PAGE_UUID_PREFIX,
                catalogClassName
            ];
            var currentClasses = ($(this.SELECTORS.BODY).attr('class') || "").split(' ');
            currentClasses = currentClasses.filter(function(className) {
                return rejectedClassPrefixes.reduce(function(result, anA) {
                    return !className.startsWith(anA) || result;
                }, false);
            });
            var classes = uid + ' ' + uuid + ' ' + catalogClassName + ' ' + currentClasses.join(' ');
            $(this.SELECTORS.BODY).attr('class', classes);

            var piip = $(this.SELECTORS.PAGE_ID_INSERTION_POINT);
            if (piip.length > 0) {
                piip.html(pageId);
            }
        };
    }


    function TreeUtils() {
        // ===================== TREE =====================

        this.treeForEachParent = function treeForEachParent(tree, parentComponentIds, fn) {
            this.treeForEach(tree, function(node, index) {
                var strChain = node.idChainFromRootNode.toString();
                if (strChain.endsWith(parentComponentIds)) {
                    fn(node, index);
                }
            });
        };

        this.treeForEach = function treeForEach(node, fn, index) {
            index = index || 0;
            fn(node, index);
            node.children.forEach(function(node, index) {
                try {
                    treeForEach(node, fn, index);
                } catch (e) {
                    // throw break to break the current loop
                    // not sure if there's a better way to short circuit a forEach...
                    if (e !== 'break') {
                        throw e;
                    }
                }
            });
        };

        this.treeForEachWithDepthFirstCollector = function treeForEachWithDepthFirstCollector(node, fn, cumulatorFn, value) {
            value = cumulatorFn(node, value);
            fn(node, value);
            node.children.forEach(function(node) {
                treeForEachWithDepthFirstCollector(node, fn, cumulatorFn, value);
            });
        };
    }


    return new StorefrontBuilder();

}();
