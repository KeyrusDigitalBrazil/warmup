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
import * as angular from "angular";

/**
 * @ngdoc object
 * @name treeModule.object:YTreeDndEvent
 * @description
 * A plain JSON object, representing the event triggered when dragging and dropping nodes in the {@link treeModule.directive:ytree ytree} directive.
 *
 * @param {Object} sourceNode is the {@link treeModule.object:Node node} that is being dragged.
 * @param {Object} destinationNodes is the set of the destination's parent's children {@link treeModule.object:Node nodes}.
 * @param {Number} position is the index at which the {@link treeModule.object:Node node} was dropped.
 *
 */
export class YTreeDndEvent {
	constructor(
		/**
		 * @ngdoc property
		 * @name sourceNode
		 * @propertyOf treeModule.object:YTreeDndEvent
		 * @description
		 * the {@link treeModule.object:Node node} being dragged
		 */
		public sourceNode: TreeNode,
		/**
		 * @ngdoc property
		 * @name destinationNodes
		 * @propertyOf treeModule.object:YTreeDndEvent
		 * @description
		 * array of siblings {@link treeModule.object:Node nodes} to the location drop location
		 */
		public destinationNodes: TreeNode,
		/**
		 * @ngdoc property
		 * @name position
		 * @propertyOf treeModule.object:YTreeDndEvent
		 * @description
		 * the index at which {@link treeModule.object:Node node} was dropped amongst its siblings
		 */
		public position: number,
		/**
		 * @ngdoc property
		 * @name sourceParentHandle
		 * @propertyOf treeModule.object:YTreeDndEvent
		 * @description
		 * the  UI handle of the parent node of the source element
		 */
		public sourceParentHandle?: any,
		/**
		 * @ngdoc property
		 * @name targetParentHandle
		 * @propertyOf treeModule.object:YTreeDndEvent
		 * @description
		 * the UI handle of the targeted parent element
		 */
		public targetParentHandle?: any
	) {}
}

/**
 * @ngdoc object
 * @name treeModule.object:dragOptions
 * @description
 * A JSON object holding callbacks related to nodes drag and drop functionality in the {@link treeModule.directive:ytree ytree} directive.
 * Each callback exposes the {@link treeModule.object:YTreeDndEvent YTreeDndEvent}
 */
export interface TreeDragOptions {
	/**
	 * @ngdoc property
	 * @name onDropCallback
	 * @propertyOf treeModule.object:dragOptions
	 * @description
	 * Callback function executed after the node is dropped.
	 */
	onDropCallback: (event: YTreeDndEvent) => void;
	/**
	 * @ngdoc property
	 * @name beforeDropCallback
	 * @propertyOf treeModule.object:dragOptions
	 * @description
	 * Callback function executed before drop. Return true allows drop, false rejects, and an object {confirmDropI18nKey: 'key'} opens a confirmation modal.
	 */
	beforeDropCallback: (event: YTreeDndEvent) => void;
	/**
	 * @ngdoc property
	 * @name acceptDropCallback
	 * @propertyOf treeModule.object:dragOptions
	 * @description
	 * Callback function executed when hovering over droppable slots, return true to allow, return false to block.
	 */
	acceptDropCallback: (event: YTreeDndEvent) => void;
	allowDropCallback: (event: YTreeDndEvent) => void;
}

export interface ITreeService {
	fetchChildren(parent: TreeNode): angular.IPromise<TreeNode[]>;
	saveNode(parent: TreeNode): angular.IPromise<TreeNode>;
	removeNode(node: TreeNode): angular.IPromise<void>;
}

/**
 * @ngdoc object
 * @name treeModule.object:Node
 * @description
 * A plain JSON object, representing the node of a tree managed by the {@link treeModule.directive:ytree ytree} directive.
 */
export interface TreeNode {
	/**
	 * @ngdoc property
	 * @name hasChildren
	 * @propertyOf treeModule.object:Node
	 * @description
	 * boolean specifying whether the retrieved node has children. This is read only and ignored upon saving.
	 */
	hasChildren: boolean;
	/**
	 * @ngdoc property
	 * @name name
	 * @propertyOf treeModule.object:Node
	 * @description
	 * the non localized node name. Required upon posting.
	 */
	name: string;
	parent: TreeNode;
	/**
	 * @ngdoc property
	 * @name parentUid
	 * @propertyOf treeModule.object:Node
	 * @description
	 * the unique identifier of the parent node for the given catalog. Required upon posting.
	 */
	parentUid: string;
	position: number;
	itemtype: string;
	/**
	 * @ngdoc property
	 * @name uid
	 * @propertyOf treeModule.object:Node
	 * @description
	 * the unique identifier of a node for the given catalog. Optional upon posting.
	 */
	uid: string;
	uuid: string;
	nodes?: TreeNode[];
	initiated?: boolean;
	mouseHovered?: boolean;
}

export interface TreeNgModel extends angular.INgModelController {
	collapsed: boolean;
	toggle: () => void;
	$parentNodeScope: any;
}

/* @internal */
export interface ITreeDndOptions {
	dragEnabled: boolean;
	dragDelay: number;
}

/* @internal */
export interface TreeDndOptionsCallbacks {
	dropped?: (event: any) => void;
	beforeDrop?: (event: any) => void;
	accept?: (sourceNodeScope: any, destNodesScope: any, destIndex: any) => void;
}

/* @internal */
export interface TreeConfiguration {
	treeClass: string;
	hiddenClass: string;
	nodesClass: string;
	nodeClass: string;
	handleClass: string;
	placeholderClass: string;
	dragClass: string;
	dragThreshold: number;
	levelThreshold: number;
	defaultCollapsed: boolean;
	dragDelay: number;
}