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
import {IRestService, IRestServiceFactory, ITreeService, TreeNode} from "smarteditcommons";

export const TreeServiceFactory = (
	$q: angular.IQService,
	restServiceFactory: IRestServiceFactory,
	getDataFromResponse: (response: any) => any
) => {
	'ngInject';

	/**
	 * @ngdoc service
	 * @name treeModule.service:TreeService
	 *
	 * @description
	 * A class to manage tree nodes through a REST API.
	 * @constructs treeModule.TreeService
	 * @param {string} nodeUri the REST entry point to handle tree nodes. it must support GET, POST, PUT and DELETE verbs:
	 * - GET nodeUri?parentUid={parentUid} will return a list of children {@link treeModule.object:Node nodes}  wrapped in an object:
	 * <pre>
	 * {
	 * 	 navigationNodes:[{
	 * 		 uid: "2",
	 * 		 name: "node2",
	 * 		 parentUid: "root"
	 * 		 hasChildren: true
	 * 	 }, {
	 * 		 uid: "4",
	 * 		 name: "node4",
	 * 		 parentUid: "1",
	 * 		 hasChildren: false
	 * 	 }]
	 * }
	 * </pre>
	 * - POST nodeUri takes a {@link treeModule.object:Node Node} payload and returns the final object.
	 * - PUT nodeUri/{uid} takes a {@link treeModule.object:Node Node} payload and returns the final object.
	 * - DELETE nodeUri/{uid}
	 */
	class TreeService implements ITreeService {

		private nodesRestService: IRestService<TreeNode | TreeNode[]>;

		constructor(nodeUri: string) {
			if (nodeUri) {
				this.nodesRestService = restServiceFactory.get(nodeUri);
			}
		}

		/**
		 * @ngdoc method
		 * @name treeModule.service:TreeService#fetchChildren
		 * @methodOf treeModule.service:TreeService
		 * @description
		 * Will fetch the children of a given node by querying GET nodeUri?parentUid={parentUid}
		 * - Once the children retrieved, the node will be marked as "initiated" and subsequent calls will not hit the server.
		 * - Each children will be given a ManyToOne reference to their parent.
		 * - The parent nodes will be assigned its children through the "nodes" property.
		 * @param {Object} parent the parent {@link treeModule.object:Node node} object the nodes of which we want to fetch
		 */
		fetchChildren(_parent: TreeNode): angular.IPromise<TreeNode[]> {
			_parent.nodes = _parent.nodes || [];
			if (_parent.initiated) {
				return $q.when(_parent.nodes);
			} else {
				return this.nodesRestService.get({
					parentUid: _parent.uid
				}).then((response: TreeNode[]) => {

					_parent.initiated = true;

					const children = getDataFromResponse(response);

					if (!children) {
						return [];
					}

					children.forEach((child: TreeNode) => {
						child.parent = _parent;
					});

					Array.prototype.push.apply(_parent.nodes, children);
					return children;
				});
			}

		}

		/**
		 * @ngdoc method
		 * @name treeModule.service:TreeService#saveNode
		 * @methodOf treeModule.service:TreeService
		 * @description
		 * Will save a new node for the given parent by POSTing to nodeUri. The payload will only contain the parentUid and a generated name.
		 * On the front end side the parent model will be marked as having children.
		 * @param {Object} parent the parent {@link treeModule.object:Node node} object from which to create a child
		 */
		saveNode(_parent: TreeNode): angular.IPromise<TreeNode> {
			return this.nodesRestService.save({
				parentUid: _parent.uid,
				name: (_parent.name ? _parent.name : _parent.uid) + _parent.nodes.length
			}).then((response: TreeNode) => {
				_parent.hasChildren = true;
				response.parent = _parent;
				return response;
			});
		}
		/**
		 * @ngdoc method
		 * @name treeModule.service:TreeService#removeNode
		 * @methodOf treeModule.service:TreeService
		 * @description
		 * Will delete a node by sending DELETE to nodeUri/{uid}.
		 * On the front end side the parent model "hasChildren" will be re-evaluated.
		 * @param {Object} node the {@link treeModule.object:Node node} object to delete.
		 */
		removeNode(node: TreeNode): angular.IPromise<void> {
			return this.nodesRestService.remove({
				identifier: node.uid
			}).then(function() {
				const parent = node.parent;
				parent.hasChildren = parent.nodes.length > 1;
				return;
			});
		}

	}

	return TreeService;
};
