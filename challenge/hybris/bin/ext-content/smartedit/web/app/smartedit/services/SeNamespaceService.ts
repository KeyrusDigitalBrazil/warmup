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
import {SeInjectable} from "smarteditcommons";

/* @internal */
interface SmarteditNamespace {
	addOnReprocessPageListener: (callback: () => void) => void;
	reprocessPage: () => void;
	applications: string[];
	domain: string;
	smarteditroot: string;
	whiteListedStorefronts: string;
	renderComponent?: (componentId: string, componentType: string, parentId: string) => any;
}

/* @internal */
@SeInjectable()
export class SeNamespaceService {

	constructor(
		private $log: angular.ILogService
	) {}

	reprocessPage() {
		if (this.namespace && typeof this.namespace.reprocessPage === 'function') {
			return this.namespace.reprocessPage();
		}
		this.$log.warn('No reprocessPage function defined on smartedit namespace');
		return null;
	}

	// explain slot for multiple instances of component scenario
	renderComponent(componentId: string, componentType: string, parentId: string): boolean {
		return this.namespace && typeof this.namespace.renderComponent === 'function' ?
			this.namespace.renderComponent(componentId, componentType, parentId) : false;
	}

	private get namespace(): SmarteditNamespace {
		return (window as any).smartedit;
	}

}