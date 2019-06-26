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
import {TreeServiceFactory} from "smarteditcommons/components/tree/TreeServiceFactory";
import {promiseHelper} from 'testhelpers';
import {IRestService, IRestServiceFactory, ITreeService, TreeNode} from "smarteditcommons";

describe('treeService', function() {
	const $q = promiseHelper.$q();

	let TreeService: {new(nodeUri: string): ITreeService};
	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
	let nodesRestService: jasmine.SpyObj<IRestService<TreeNode | TreeNode[]>>;

	const nodeUri = "asdfasdf";

	const navigationNodeList = [{
		uid: "1",
		name: "node1",
		title: {
			en: "node1_en",
			fr: "node1_fr"
		},
		parentUid: "someUid"
	}, {
		uid: "2",
		name: "node2",
		title: {
			en: "node2_en",
			fr: "node2_fr"
		},
		parentUid: "someUid"
	}];

	beforeEach(() => {
		restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', ['get']);

		nodesRestService = jasmine.createSpyObj('nodesRestService', ['get', 'save', 'remove']);
		restServiceFactory.get.and.returnValue(nodesRestService);

		const getDataFromResponse = (response: any) => {
			const dataKey = Object.keys(response).filter(function(key) {
				return response[key] instanceof Array;
			})[0];

			return response[dataKey];
		};

		TreeService = TreeServiceFactory(
			$q,
			restServiceFactory,
			getDataFromResponse
		);
	});

	it('WHEN parent is not initiated, THEN fetchChildren will retrieve its first level children, set cardinalities and mark the parent as initiated', function() {

		const response = {
			navigationNodes: navigationNodeList
		};

		const parent = {
			uid: "someUid"
		} as any;
		nodesRestService.get.and.returnValue($q.when(response));
		const treeService = new TreeService(nodeUri);

		expect(treeService.fetchChildren(parent)).toBeResolvedWithData(navigationNodeList);

		expect(parent.nodes).toEqual(navigationNodeList);
		expect(parent.initiated).toBe(true);
		expect(nodesRestService.get).toHaveBeenCalledWith({
			parentUid: 'someUid'
		});
	});

	it('GIVEN parent does not have any child, WHEN parent is not initiated, THEN fetchChildren will retrieve empty array and mark the parent as initiated', function() {

		const response = {
			navigationNodes: []
		} as any;
		const emptyArray: any = [];

		const parent = {
			uid: "someUid"
		} as any;
		nodesRestService.get.and.returnValue($q.when(response));
		const treeService = new TreeService(nodeUri);

		expect(treeService.fetchChildren(parent)).toBeResolvedWithData(emptyArray);
		expect(parent.initiated).toBe(true);
		expect(nodesRestService.get).toHaveBeenCalledWith({
			parentUid: 'someUid'
		});
	});

	it('WHEN the nodesRestService response has a nodes array and some objects and String, THEN fetchChildren will still return the nodes array and filter everything else', function() {

		const response = {
			someString: '',
			navigationNodes: navigationNodeList,
			someObj: {}
		};

		const parent = {
			uid: "someUid"
		} as any;
		nodesRestService.get.and.returnValue($q.when(response));
		const treeService = new TreeService(nodeUri);

		expect(treeService.fetchChildren(parent)).toBeResolvedWithData(navigationNodeList);

	});

	it('WHEN parent is initiated, THEN fetchChildren will simply return its nodes', function() {

		const parent = {
			uid: "someUid",
			initiated: true,
			nodes: navigationNodeList
		} as any;

		const treeService = new TreeService(nodeUri);

		expect(treeService.fetchChildren(parent)).toBeResolvedWithData(parent.nodes);

		expect(nodesRestService.get).not.toHaveBeenCalled();
	});

	it('saveNode will require the creation of an empty node (only passing the parentUid) and set the parent.hasChildren to true and set the parent of the returned child', function() {

		const treeService = new TreeService(nodeUri);

		const parent = {
			uid: "someUid",
			hasChildren: false,
			nodes: ['sdfad']
		} as any;
		const someNode: any = {};

		nodesRestService.save.and.returnValue($q.when(someNode));

		const augmented = {...someNode};
		augmented.parent = parent;
		expect(treeService.saveNode(parent)).toBeResolvedWithData(augmented);

		expect(parent.hasChildren).toBe(true);
		expect(nodesRestService.save).toHaveBeenCalledWith({
			parentUid: 'someUid',
			name: 'someUid1'
		});
	});

	it('removeNode', function() {

		const parent = {
			uid: "asdfasd",
			hasChildren: true,
			nodes: ['sdfad']
		} as any;
		const node = {
			uid: 'someUid'
		} as any;

		node.parent = parent;
		parent.nodes.push(node);

		const treeService = new TreeService(nodeUri);

		nodesRestService.remove.and.returnValue($q.when());

		expect(treeService.removeNode(node)).toBeResolvedWithData(undefined);

		expect(parent.hasChildren).toBe(true);
		expect(nodesRestService.remove).toHaveBeenCalledWith({
			identifier: 'someUid'
		});
	});

});