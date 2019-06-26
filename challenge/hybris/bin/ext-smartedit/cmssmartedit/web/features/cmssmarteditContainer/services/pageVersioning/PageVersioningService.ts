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

import {IRestService, IRestServiceFactory, Page, Pageable, SeInjectable} from 'smarteditcommons';

/**
 * @ngdoc interface
 * @name cmsSmarteditServicesModule.interfaces:IPageVersion
 * @description
 * Interface used by {@link cmsSmarteditServicesModule.service:PageVersioningService PageVersioningService} to represent a
 * page version.
 */
export interface IPageVersion {
	/**
	 * @ngdoc property
	 * @name uid
	 * @propertyOf cmsSmarteditServicesModule.interfaces:IPageVersion
	 * @description uid of the version
	 */
	uid: string;
	/**
	 * @ngdoc property
	 * @name itemUUID
	 * @propertyOf cmsSmarteditServicesModule.interfaces:IPageVersion
	 * @description uuid of the item
	 */
	itemUUID: string;
	/**
	 * @ngdoc property
	 * @name creationtime
	 * @propertyOf cmsSmarteditServicesModule.interfaces:IPageVersion
	 * @description date time when the page was created
	 */
	creationtime: Date;
	/**
	 * @ngdoc property
	 * @name label
	 * @propertyOf cmsSmarteditServicesModule.interfaces:IPageVersion
	 * @description user friendly name of the page version
	 */
	label: string;
	/**
	 * @ngdoc property
	 * @name description
	 * @propertyOf cmsSmarteditServicesModule.interfaces:IPageVersion
	 * @description optional string that describes the page version
	 */
	description?: string;
}

/**
 * @ngdoc interface
 * @name cmsSmarteditServicesModule.interfaces:PageVersionSearchPayload
 * @description
 * Interface used by {@link cmsSmarteditServicesModule.service:PageVersioningService PageVersioningService} to query
 * page versions.
 */
export interface PageVersionSearchPayload extends Pageable {
	/**
	 * @ngdoc property
	 * @name pageUuid
	 * @propertyOf cmsSmarteditServicesModule.interfaces:PageVersionSearchPayload
	 * @description uuid of the page whose versions to retrieve
	 */
	pageUuid: string;
}

/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:PageVersioningService
 *
 * @description
 * This service is used to manage versions in a page.
 */
@SeInjectable()
export class PageVersioningService {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	private pageVersionRESTService: IRestService<IPageVersion>;
	private pageVersionsRESTService: IRestService<Page<IPageVersion>>;
	private pageVersionsRollbackRESTService: IRestService<void>;
	private pageVersionsServiceResourceURI: string;
	private pageVersionsRollbackServiceResourceURI: string;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	constructor(
		private restServiceFactory: IRestServiceFactory,
		private PAGE_CONTEXT_SITE_ID: string
	) {
		this.pageVersionsServiceResourceURI = `/cmswebservices/v1/sites/${this.PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions`;
		this.pageVersionsRollbackServiceResourceURI = `/cmswebservices/v1/sites/${this.PAGE_CONTEXT_SITE_ID}/cmsitems/:pageUuid/versions/:versionId/rollbacks`;
		this.pageVersionRESTService = this.restServiceFactory.get(this.pageVersionsServiceResourceURI);
		this.pageVersionsRESTService = this.restServiceFactory.get(this.pageVersionsServiceResourceURI);
		this.pageVersionsRollbackRESTService = this.restServiceFactory.get(this.pageVersionsRollbackServiceResourceURI);
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:PageVersioningService#findPageVersions
	 * @methodOf cmsSmarteditServicesModule.service:PageVersioningService
	 *
	 * @description
	 * Retrieves the list of versions found for the page identified by the provided id. This method is paged.
	 *
	 * @param {PageVersionSearchPayload} payload The payload containing search query params, including the pageable information.
	 *
	 * @returns {angular.IPromise<Page<IPageVersion>>} A promise that resolves to a paged list of versions.
	 */
	public findPageVersions(payload: PageVersionSearchPayload): angular.IPromise<Page<IPageVersion>> {
		return this.pageVersionsRESTService.get(payload);
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:PageVersioningService#getPageVersionForId
	 * @methodOf cmsSmarteditServicesModule.service:PageVersioningService
	 *
	 * @description
	 * Retrieves the page version information for the provided versionId.
	 *
	 * @param {string} pageUuid The uuid of the page.
	 * @param {string} versionId The uid of the version.
	 *
	 * @returns {angular.IPromise<IPageVersion>} A promise that resolves to a page version information.
	 */
	public getPageVersionForId(pageUuid: string, versionId: string): angular.IPromise<IPageVersion> {
		return this.pageVersionRESTService.get({
			pageUuid,
			identifier: versionId
		});
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:PageVersioningService#getResourceURI
	 * @methodOf cmsSmarteditServicesModule.service:PageVersioningService
	 *
	 * @description
	 * Retrieves the resource URI to manage page versions.
	 *
	 * @returns {string} the resource URI
	 */
	public getResourceURI(): string {
		return this.pageVersionsServiceResourceURI;
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:PageVersioningService#deletePageVersion
	 * @methodOf cmsSmarteditServicesModule.service:PageVersioningService
	 * 
	 * @param {string} pageUuid The uuid of the page.
	 * @param {string} versionId The uid of the version.
	 * 
	 * @returns {angular.IPromise<void>} an empty promise
	 */
	public deletePageVersion(pageUuid: string, versionId: string): angular.IPromise<void> {
		return this.pageVersionsRESTService.remove({
			pageUuid,
			identifier: versionId
		});
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:PageVersioningService#rollbackPageVersion
	 * @methodOf cmsSmarteditServicesModule.service:PageVersioningService
	 *
	 * @description
	 * Will rollback the page to the provided version. This process will automatically create a version of the current page.
	 *
	 * @param {string} pageUuid The uuid of the page.
	 * @param {string} versionId The uid of the version.
	 * 
	 * @returns {angular.IPromise<void>} an empty promise
	 */
	public rollbackPageVersion(pageUuid: string, versionId: string): angular.IPromise<void> {
		return this.pageVersionsRollbackRESTService.save({pageUuid, versionId});
	}

}