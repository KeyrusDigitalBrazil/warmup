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
angular.module('navigationEditorNodeServiceModule', ['resourceModule', 'yLoDashModule', 'functionsModule', 'cmsitemsRestServiceModule'])
    .service('_nodeAncestryService', function(lodash) {

        this._fetchAncestors = function(sourceArray, uid) {
            var parent = sourceArray.find(function(element) {
                return element.uid === uid;
            });
            if (parent) {
                return [parent].concat(this._fetchAncestors(sourceArray, parent.parentUid));
            } else {
                return [];
            }
        };

        this.buildOrderedListOfAncestors = function(sourceArray, uid) {
            var ancestry = lodash.reverse(this._fetchAncestors(sourceArray, uid));
            var level = -1;
            return ancestry.map(function(node) {
                var nextLevel = ++level;
                return lodash.assign(lodash.cloneDeep(node), {
                    level: nextLevel,
                    formattedLevel: nextLevel === 0 ? "se.cms.navigationcomponent.management.node.level.root" : "se.cms.navigationcomponent.management.node.level.non.root"
                });
            });
        };
    })
    /**
     * @ngdoc service
     * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService
     * @description
     * This service updates the navigation node by making REST call to the cmswebservices navigations API.
     */
    .service('navigationEditorNodeService', function($log, _nodeAncestryService, navigationNodeRestService, lodash, getDataFromResponse, cmsitemsRestService) {

        this.getNavigationNode = function(nodeUid, uriParams) {

            var payload = angular.extend({
                identifier: nodeUid
            }, uriParams);
            return navigationNodeRestService.get(payload);
        };

        /**
         * @ngdoc method
         * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService#updateNavigationNodePosition
         * @methodOf navigationEditorNodeServiceModule.service:navigationEditorNodeService
         *
         * @description
         * Updates the position of the navigation node within the children collection of its parent.
         * Fetches the parent node, and reorders the children, then updates the parent with the new child order.
         *
         * @param {Object} node The navigation node that needs to be updated.
         */
        this.updateNavigationNodePosition = function(node) {

            return cmsitemsRestService.getById(node.parent.uuid).then(function(parentNodeFromServer) {

                parentNodeFromServer.children = parentNodeFromServer.children || [];
                var currentIndex = parentNodeFromServer.children.findIndex(function(child) {
                    return child === node.uuid;
                });
                var targetIndex = node.position;
                if (currentIndex < 0 || node.position === undefined) {
                    throw new Error('navigationEditorNodeService.updateNavigationNodePosition() - invalid index: move FROM [' +
                        currentIndex + '] TO [' + targetIndex + ']');
                }

                parentNodeFromServer.children.splice(targetIndex, 0, parentNodeFromServer.children.splice(currentIndex, 1)[0]);
                parentNodeFromServer.identifier = parentNodeFromServer.uuid;
                return cmsitemsRestService.update(parentNodeFromServer);
            });


        };

        /**
         * @ngdoc method
         * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService#getNavigationNodeAncestry
         * @methodOf navigationEditorNodeServiceModule.service:navigationEditorNodeService
         *
         * @description
         * Returns the list of nodes belonging to the ancestry of the node identified by its uid. This list includes the queried node as well.
         *
         * @param {Object} node The navigation node that needs to be updated.
         * @param {Object} uriParams the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         * @returns {Array} an array of {@link treeModule.object:Node nodes}
         */
        this.getNavigationNodeAncestry = function(nodeUid, uriParams) {

            var payload = lodash.assign({
                ancestorTrailFrom: nodeUid
            }, uriParams);
            return navigationNodeRestService.get(payload).then(function(response) {
                return _nodeAncestryService.buildOrderedListOfAncestors(getDataFromResponse(response), nodeUid);
            });
        };

    });
