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
import {GatewayProxied, IUrlService, SeInjectable} from 'smarteditcommons';

/** @internal */
@GatewayProxied('openUrlInPopup', 'path')
@SeInjectable()
export class UrlService extends IUrlService {

	private location: ng.ILocationService;
	private window: ng.IWindowService;

	constructor(
		public $location: ng.ILocationService,
		public $window: ng.IWindowService,
		PAGE_CONTEXT_SITE_ID: string,
		PAGE_CONTEXT_CATALOG: string,
		PAGE_CONTEXT_CATALOG_VERSION: string,
		CONTEXT_SITE_ID: string,
		CONTEXT_CATALOG: string,
		CONTEXT_CATALOG_VERSION: string) {

		super(PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION);
		this.location = $location;
		this.window = $window;
	}

	openUrlInPopup(url: string): void {
		const win: Window = this.window.open(url, '_blank', 'toolbar=no, scrollbars=yes, resizable=yes');
		win.focus();
	}

	path(path: string): void {
		this.location.path(path);
	}
}
