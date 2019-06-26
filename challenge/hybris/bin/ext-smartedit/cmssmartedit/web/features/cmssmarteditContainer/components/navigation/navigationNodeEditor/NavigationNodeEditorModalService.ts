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
import {ICatalogService, IUriContext, Payload, SeInjectable} from "smarteditcommons";
import {NavigationNode} from "../types";

/**
 * @ngdoc service
 * @name NavigationModule.service:navigationNodeEditorModalService
 *
 * @description
 * Convenience service to open an editor modal window for a given navigation node's data.
 */
@SeInjectable()
export class NavigationNodeEditorModalService {

	constructor(
		private $q: angular.IQService,
		private genericEditorModalService: any,
		private catalogService: ICatalogService,
		private NAVIGATION_NODE_TYPECODE: string
	) {}

	/**
	 *
	 * @ngdoc method
	 * @name NavigationModule.service:navigationNodeEditorModalService#open
	 * @methodOf NavigationModule.service:navigationNodeEditorModalService
	 *
	 * @description
	 * Opens a modal for creating and editing a navigation node with the CSM items API. Leave the current parameter to trigger
	 * a creation operation.
	 *
	 * @param {IUriContext} uriContext The uri context of the navigational node.
	 * @param {NavigationNode} parent The parent navigational node.
	 * @param {NavigationNode?} current The current navigational node. If the current node is left empty, the modal
	 * will process a creation operation.
	 * @returns {angular.IPromise<Payload>}
	 */
	open(uriContext: IUriContext, parent: NavigationNode, current?: NavigationNode): angular.IPromise<Payload> {
		// TODO: Once the application is migrated to typescript, it is sufficient to remove these parameter checks.
		if (!uriContext) {
			throw new Error(`NavigationNodeEditorModalService.open : missing [uriContext : IUriContext] parameter.`);
		}

		if (!parent) {
			throw new Error(`NavigationNodeEditorModalService.open : missing [parent: NavigationalNode] parameter.`);
		}

		// If the current node is provided then the content will resolve null to indicate an editing process.
		let resolve = this.$q.resolve(null);

		if (!current) {
			resolve = this.catalogService.getCatalogVersionUUid(uriContext).then((catalogVersion: string) => {
				return {
					catalogVersion,
					parent: parent.uuid,
					itemtype: this.NAVIGATION_NODE_TYPECODE,
					visible: true
				};
			});
		}

		return resolve.then((content) => {
			return this.genericEditorModalService.open({
				componentUuid: current ? current.uuid : null,
				componentType: this.NAVIGATION_NODE_TYPECODE,
				content,
				title: 'se.cms.navigationmanagement.node.edit.title',
			}, null, (item: Payload) => {
				return item;
			});
		});
	}

}
