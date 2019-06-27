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
import * as angular from 'angular';
import * as lo from 'lodash';
import {
	IRestService,
	IRestServiceFactory,
	ISeComponent,
	ITreeService,
	IUriContext,
	IURIBuilder,
	SeComponent,
	TreeNgModel
} from 'smarteditcommons';
import {NavigationNodeEditorModalService} from '../navigationNodeEditor/NavigationNodeEditorModalService';
import {NavigationNode, NavigationNodeCMSItem, NavigationNodeEntry} from "../types";

/**
 * @ngdoc directive
 * @name navigationEditorModule.directive:navigationEditor
 * @scope
 * @restrict E
 * @element ANY
 *
 * @description
 * Navigation Editor directive used to display navigation editor tree
 * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
 * @param {Boolean} readOnly when true, no CRUD facility shows on the editor. OPTIONAL, default false.
 * @param {String} rootNodeUid the uid of the node to be taken as root, OPTIONAL, default "root"
 */
@SeComponent({
	templateUrl: 'navigationEditorTreeComponentTemplate.html',
	inputs: [
		'uriContext',
		'readOnly',
		'rootNodeUid'
	]
})
export class NavigationEditorTreeComponent implements ISeComponent {

	private static readonly READY_ONLY_ERROR_I18N = "navigation.in.readonly.mode";

	public nodeTemplateUrl = 'navigationNodeRenderTemplate.html';

	public uriContext: IUriContext;
	public readOnly: boolean;
	public removeDefaultTemplate: boolean;
	public rootNodeUid: string;
	public dragOptions: any;
	public actions: any;
	public nodeURI: string;

	private navigationsRestEndpoint: IRestService<NavigationNode>;

	constructor(
		private NAVIGATION_NODE_ROOT_NODE_UID: string,
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private lodash: lo.LoDashStatic,
		private URIBuilder: {new(uri: string): IURIBuilder},
		private restServiceFactory: IRestServiceFactory,
		private NAVIGATION_MANAGEMENT_RESOURCE_URI: string,
		private navigationEditorNodeService: any,
		private navigationNodeEditorModalService: NavigationNodeEditorModalService,
		private confirmationModalService: any,
		private cmsitemsRestService: any,
		private NAVIGATION_NODE_TYPECODE: string
	) {}

	$onInit(): void {
		if (!this.readOnly) {
			this.dragOptions = this.buildDragOptions();
		}

		this.nodeURI = new this.URIBuilder(this.NAVIGATION_MANAGEMENT_RESOURCE_URI).replaceParams(this.uriContext).build();
		this.rootNodeUid = this.rootNodeUid || this.NAVIGATION_NODE_ROOT_NODE_UID;
		this.removeDefaultTemplate = true;

		this.navigationsRestEndpoint = this.restServiceFactory.get(this.nodeURI);

		this.actions = this.buildActions();
	}

	private buildDragOptions() {
		return {
			onDropCallback: (event: any) => {
				this.actions.dragAndDrop(event);
			},
			allowDropCallback: (event: any) => {
				if (event.sourceNode.parent.uid !== event.destinationNodes[0].parent.uid) {
					return false;
				}
				return true;
			},
			beforeDropCallback(event: any) {
				if (event.sourceNode.parent.uid !== event.destinationNodes[0].parent.uid) {
					return {
						confirmDropI18nKey: 'se.cms.navigationmanagement.navnode.confirmation'
					};
				} else {
					return true;
				}
			}
		};
	}

	private buildActions() {
		const vm = this;

		const dropdownItems = [{
			key: 'se.cms.navigationmanagement.navnode.edit',
			callback(handle: TreeNgModel) {
				vm.actions.editNavigationNode(handle);
			}
		}, {
			key: 'se.cms.navigationmanagement.navnode.removenode',
			callback(handle: TreeNgModel) {
				vm.actions.removeItem(handle);
			}
		}, {
			key: 'se.cms.navigationmanagement.navnode.move.up',
			condition(handle: TreeNgModel) {
				return vm.actions.isMoveUpAllowed(handle);
			},
			callback(handle: TreeNgModel) {
				vm.actions.moveUp(handle);
			}
		}, {
			key: 'se.cms.navigationmanagement.navnode.move.down',
			condition(handle: TreeNgModel) {
				return vm.actions.isMoveDownAllowed(handle);
			},
			callback(handle: TreeNgModel) {
				vm.actions.moveDown(handle);
			}
		}, {
			key: 'se.cms.navigationmanagement.navnode.addchild',
			callback(handle: TreeNgModel) {
				vm.actions.addNewChild(handle);
			}
		}, {
			key: 'se.cms.navigationmanagement.navnode.addsibling',
			callback(handle: TreeNgModel) {
				vm.actions.addNewSibling(handle);
			}
		}];

		// those functions will be closure bound inside ytree
		const actions = {
			isReadOnly() {
				return vm.readOnly;
			},

			hasChildren(treeService: ITreeService, handle: TreeNgModel) {
				const nodeData = handle.$modelValue;
				return nodeData.hasChildren;
			},

			fetchData(treeService: ITreeService, nodeData: NavigationNode) {
				if (nodeData.uid === vm.NAVIGATION_NODE_ROOT_NODE_UID) {
					nodeData.initiated = false;
				}

				if (nodeData.initiated) {
					return vm.$q.resolve(nodeData.nodes);
				}

				let promise = vm.$q.resolve(null);

				if (nodeData.uid === vm.NAVIGATION_NODE_ROOT_NODE_UID) {
					promise = vm.getNavigationNodeCMSItemByUid(this.rootNodeUid).then((node) => {
						vm.lodash.assign(nodeData, node);
					});
				}

				return promise.then(function() {
					nodeData.nodes = [];
					return treeService.fetchChildren(nodeData);
				});
			},

			removeItem(treeService: ITreeService, handle: TreeNgModel) {
				if (vm.readOnly) {
					throw NavigationEditorTreeComponent.READY_ONLY_ERROR_I18N;
				}
				vm.confirmationModalService.confirm({
					description: 'se.cms.navigationmanagement.navnode.removenode.confirmation.message',
					title: 'se.cms.navigationmanagement.navnode.removenode.confirmation.title'
				}).then(() => {
					vm.cmsitemsRestService.delete(handle.$modelValue.uuid).then(() => {
						vm.actions.refreshParentNode(handle);
					});
				});
			},

			performMove(treeService: ITreeService, nodeData: NavigationNodeCMSItem, handle?: TreeNgModel, refreshNodeItself?: boolean) {
				if (vm.readOnly) {
					throw NavigationEditorTreeComponent.READY_ONLY_ERROR_I18N;
				}
				return vm.navigationEditorNodeService.updateNavigationNodePosition(nodeData, vm.uriContext).then(() => {
					if (!handle) {
						vm.actions.fetchData(this.root);
					} else if (refreshNodeItself) {
						vm.actions.refreshNode(handle);
					} else {
						vm.actions.refreshParentNode(handle);
					}
				}, (err: any) => {
					vm.$log.error(`Error updating node position:\n${err}`);
				});
			},

			dragAndDrop(treeService: ITreeService, event: any) {
				const nodeData = event.sourceNode;
				const destinationNodes = event.destinationNodes;

				const destination = (vm.lodash.find(destinationNodes, (node) => {
					return node.uid !== nodeData.uid;
				}));

				// this method is still triggered on drop, even if drop is not allowed
				// so its possible that destination does not exist, in which case we return silently
				if (!destination) {
					return;
				}
				const destinationParent = destination.parent;

				if (vm.hasNotMoved(nodeData, event.position, destinationParent)) {
					return;
				}

				nodeData.position = event.position;
				nodeData.parentUid = destinationParent.uid;
				nodeData.parent = destinationParent;

				vm.actions.performMove(nodeData, event.targetParentHandle, true).then(() => {
					if (event.sourceParentHandle !== event.targetParentHandle) {
						vm.actions.refreshNode(event.sourceParentHandle);
					}
				});
			},

			moveUp(treeService: ITreeService, handle: TreeNgModel) {
				if (vm.readOnly) {
					throw NavigationEditorTreeComponent.READY_ONLY_ERROR_I18N;
				}
				const nodeData = handle.$modelValue;
				nodeData.position = parseInt(nodeData.position, 10) - 1;
				vm.actions.performMove(nodeData, handle);
			},

			moveDown(treeService: ITreeService, handle: TreeNgModel) {
				if (vm.readOnly) {
					throw NavigationEditorTreeComponent.READY_ONLY_ERROR_I18N;
				}
				const nodeData = handle.$modelValue;
				nodeData.position = parseInt(nodeData.position, 10) + 1;
				vm.actions.performMove(nodeData, handle);
			},

			isMoveUpAllowed(treeService: ITreeService, handle: TreeNgModel) {
				const nodeData = handle.$modelValue;
				return parseInt(nodeData.position, 10) !== 0;
			},

			isMoveDownAllowed(treeService: ITreeService, handle: TreeNgModel) {
				const nodeData = handle.$modelValue as NavigationNode;
				nodeData.parent.nodes = nodeData.parent.nodes || [];
				return parseInt(nodeData.position + '', 10) !== (nodeData.parent.nodes.length - 1);
			},

			refreshNode(treeService: ITreeService, handle: TreeNgModel) {
				return this.refresh(handle);
			},

			refreshParentNode(treeService: ITreeService, handle: TreeNgModel) {
				return this.refreshParent(handle);
			},

			editNavigationNode(treeService: ITreeService, handle: TreeNgModel) {
				const nodeData = handle.$modelValue;
				const target: any = {};

				target.nodeUid = nodeData.uid;
				target.entryIndex = undefined;

				return vm.navigationNodeEditorModalService.open(vm.uriContext, nodeData.parent, nodeData).then(() => {
					let targetNode: NavigationNode;

					if (nodeData.parent.uid === vm.NAVIGATION_NODE_ROOT_NODE_UID) {
						targetNode = nodeData;
					} else {
						targetNode = nodeData.parent;
					}

					return vm.navigationEditorNodeService.getNavigationNode(targetNode.uid, vm.uriContext).then((refreshedNode: NavigationNode) => {
						vm.lodash.assign(targetNode, refreshedNode);
						if (nodeData.parent.uid === vm.NAVIGATION_NODE_ROOT_NODE_UID) {
							return vm.actions.refreshNode(handle);
						}
						return vm.actions.refreshParentNode(handle);
					});
				});
			},

			addTopLevelNode() {
				return vm.actions.addNewChild().then(() => {
					return vm.getNavigationNodeCMSItemByUid(this.rootNodeUid).then((node) => {
						vm.actions.fetchData(node);
					});
				});
			},

			getEntryString(treeService: ITreeService, node: NavigationNode) {
				return vm.getEntriesCommaSeparated(node.entries || []);
			},

			getEntryTooltipString(treeService: ITreeService, node: NavigationNode) {
				return [
					"<div>",
					...(node.entries || []).map((entry) => `<div>${entry.name} (${entry.itemType})</div>`),
					"</div>"
				].join("");
			},

			addNewChild(treeService: ITreeService, handle: TreeNgModel) {
				const parent = handle ? handle.$modelValue : vm.actions._findNodeById(vm.rootNodeUid);
				return vm.actions._expandIfNeeded(handle).then(() => {
					return vm.navigationNodeEditorModalService.open(vm.uriContext, parent);
				}).then((node: any) => {
					node.parentUid = parent.uid;
					parent.hasChildren = true;
					vm.handleNodeCreation(node);
				});
			},

			addNewSibling(treeService: ITreeService, handle: TreeNgModel) {
				const parent = handle.$modelValue.parent;
				return vm.navigationNodeEditorModalService.open(vm.uriContext, parent).then((node: any) => {
					node.parentUid = parent.uid;
					vm.handleNodeCreation(node);
				});
			},

			getDropdownItems() {
				return dropdownItems;
			},

			_findNodeById(treeService: ITreeService, nodeUid: string): NavigationNode {
				return this.getNodeById(nodeUid);
			},
			_expandIfNeeded(treeService: ITreeService, handle: TreeNgModel) {
				return handle && handle.collapsed ? this.toggleAndfetch(handle) : vm.$q.when();
			}

		};

		return actions;
	}

	private hasNotMoved(source: NavigationNode, destinationPosition: number, destinationParent: NavigationNode): boolean {
		return source.position === destinationPosition && source.parentUid === destinationParent.uid;
	}

	private handleNodeCreation(newNode: NavigationNode): angular.IPromise<void> {
		const parent = this.actions._findNodeById(newNode.parentUid);
		parent.nodes = parent.nodes || [];
		const match = parent.nodes.find((node: NavigationNode) => {
			return node.uid === newNode.uid;
		});
		if (parent && !match) {
			return this.navigationsRestEndpoint.get({
				parentUid: parent.uid
			}).then((remoteParent: any) => {
				(remoteParent.navigationNodes || []).forEach((element: NavigationNode) => {
					element.parent = parent;
					element.nodes = element.nodes || [];
				});
				parent.nodes = remoteParent.navigationNodes || [];
				parent.hasChildren = parent.nodes.length > 0;
			});
		}
		return this.$q.when();
	}

	private getNavigationNodeCMSItemByUid(uid: string): angular.IPromise<NavigationNodeCMSItem> {
		return this.cmsitemsRestService.get({
			typeCode: this.NAVIGATION_NODE_TYPECODE,
			pageSize: 1,
			currentPage: 0,
			itemSearchParams: 'uid:' + uid
		}).then((data: {response: NavigationNodeCMSItem[]}) => {
			return data.response[0];
		});
	}

	private getEntriesCommaSeparated(entries: NavigationNodeEntry[]): string {
		return entries.map((entry) => `${entry.name} (${entry.itemType})`).join(', ');
	}

}