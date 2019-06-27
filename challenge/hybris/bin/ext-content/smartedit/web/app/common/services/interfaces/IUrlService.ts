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
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:IUrlService
 *
 * @description
 * Provides an abstract extensible url service, Used to open a given URL
 * in a new browser url upon invocation. 
 * 
 * This class serves as an interface and should be extended, not instantiated.
 */
import {IUriContext} from 'smarteditcommons';

export abstract class IUrlService {

	constructor(
		private PAGE_CONTEXT_SITE_ID: string,
		private PAGE_CONTEXT_CATALOG: string,
		private PAGE_CONTEXT_CATALOG_VERSION: string,
		private CONTEXT_SITE_ID: string,
		private CONTEXT_CATALOG: string,
		private CONTEXT_CATALOG_VERSION: string) {
	}

	/** 
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IUrlService#openUrlInPopup
	 * @methodOf smarteditServicesModule.interface:IUrlService
	 *
	 * @description
	 * Opens a given URL in a new browser pop up without authentication.
	 *
	 * @param {String} url - the URL we wish to open.
	 */
	openUrlInPopup(url: string): void {
		'proxyFunction';
		return null;
	}
	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IUrlService#path
	 * @methodOf smarteditServicesModule.interface:IUrlService
	 *
	 * @description
	 * Navigates to the given path in the same browser tab.
	 *
	 * @param {String} path - the path we wish to navigate to.
	 */
	path(path: string): void {
		'proxyFunction';
		return null;
	}
	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IUrlService#buildUriContext
	 * @methodOf smarteditServicesModule.interface:IUrlService
	 *
	 * @description
	 * Returns a uri context array populated with the given siteId, catalogId and catalogVersion information
	 *
	 * @param {String} siteId - site Id
	 * @param {String} catalogId - catalog Id
	 * @param {String} catalogVersion - catalog version
	 * 
	 * @return {IUriContext} uri context array 
	 */
	buildUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext {
		const uriContext: IUriContext = {};
		uriContext[this.CONTEXT_SITE_ID] = siteId;
		uriContext[this.CONTEXT_CATALOG] = catalogId;
		uriContext[this.CONTEXT_CATALOG_VERSION] = catalogVersion;
		return uriContext;
	}
	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.interface:IUrlService#buildPageUriContext
	 * @methodOf smarteditServicesModule.interface:IUrlService
	 *
	 * @description
	 * Returns a page uri context array populated with the given siteId, catalogId and catalogVersion information
	 *
	 * @param {String} siteId - site Id
	 * @param {String} catalogId - catalog Id
	 * @param {String} catalogVersion - catalog version
	 * 
	 * @return {IUriContext} uri context array 
	 */
	buildPageUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext {
		const uriContext: IUriContext = {};
		uriContext[this.PAGE_CONTEXT_SITE_ID] = siteId;
		uriContext[this.PAGE_CONTEXT_CATALOG] = catalogId;
		uriContext[this.PAGE_CONTEXT_CATALOG_VERSION] = catalogVersion;
		return uriContext;
	}
}
