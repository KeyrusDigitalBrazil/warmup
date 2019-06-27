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
import {NavigationNodeEditorModalService} from "cmssmarteditcontainer/components/navigation/navigationNodeEditor/NavigationNodeEditorModalService";
import {promiseHelper} from 'testhelpers';
import {ICatalogService, IUriContext} from "smarteditcommons";
import {NavigationNode} from "cmssmarteditcontainer/components/navigation/types";
import 'jasmine';

describe('NavigationNodeEditorModalService', () => {

	const $q = promiseHelper.$q();
	let genericEditorModalService: jasmine.SpyObj<any>;
	let catalogService: jasmine.SpyObj<ICatalogService>;

	let navigationNodeEditorModalService: NavigationNodeEditorModalService;

	const uriContext: IUriContext = {
		siteId: 'SiteId'
	};

	const parentNode: NavigationNode = {
		hasChildren: false,
		name: 'ParentNode',
		parentUid: 'ParentNodeParent',
		position: 0,
		itemtype: 'CMSNavigationNode',
		uid: 'ParentNodeUID',
		uuid: 'ParentNodeUUID',
		id: 'parent-id',
		nodes: [],
		parent: null
	};

	const currentNode: NavigationNode = {
		hasChildren: false,
		name: 'CurrentNode',
		parentUid: 'CurrentNodeParent',
		position: 0,
		itemtype: 'CMSNavigationNode',
		uid: 'CurrentNodeUID',
		uuid: 'CurrentNodeUUID',
		id: 'current-id',
		nodes: [],
		parent: parentNode
	};


	beforeEach(() => {
		genericEditorModalService = jasmine.createSpyObj<any>('genericEditorModalService', ['open']);
		catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', ['getCatalogVersionUUid']);

		genericEditorModalService.open.and.returnValue($q.resolve('success'));
		catalogService.getCatalogVersionUUid.and.returnValue($q.resolve('catalogVersion'));

		navigationNodeEditorModalService = new NavigationNodeEditorModalService(
			$q,
			genericEditorModalService,
			catalogService,
			'CMSNavigationNode'
		);
	});

	it('should open the generic editor modal in edit mode', () => {
		const result = navigationNodeEditorModalService.open(uriContext, parentNode, currentNode);

		expect(result).toBeResolvedWithData('success');
		expect(catalogService.getCatalogVersionUUid).not.toHaveBeenCalled();
		expect(genericEditorModalService.open).toHaveBeenCalledWith({
			componentUuid: 'CurrentNodeUUID',
			componentType: 'CMSNavigationNode',
			content: null,
			title: 'se.cms.navigationmanagement.node.edit.title'
		}, null, jasmine.any(Function));
	});

	it('should open the generic editor modal in create mode', () => {
		const result = navigationNodeEditorModalService.open(uriContext, parentNode);

		expect(result).toBeResolvedWithData('success');
		expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith({
			siteId: 'SiteId'
		});
		expect(genericEditorModalService.open).toHaveBeenCalledWith({
			componentUuid: null,
			componentType: 'CMSNavigationNode',
			content: {
				catalogVersion: 'catalogVersion',
				parent: 'ParentNodeUUID',
				itemtype: 'CMSNavigationNode',
				visible: true
			},
			title: 'se.cms.navigationmanagement.node.edit.title'
		}, null, jasmine.any(Function));
		expect(genericEditorModalService.open.calls.argsFor(0)[2]('payload')).toBe('payload');
	});

	it('should throw error if uri context parameter is missing', () => {

		expect(() => {
			navigationNodeEditorModalService.open(null, parentNode);
		}).toThrow(new Error(`NavigationNodeEditorModalService.open : missing [uriContext : IUriContext] parameter.`));

	});

	it('should throw error if parent node parameter is missing', () => {

		expect(() => {
			navigationNodeEditorModalService.open(uriContext, null);
		}).toThrow(new Error(`NavigationNodeEditorModalService.open : missing [parent: NavigationalNode] parameter.`));

	});

});