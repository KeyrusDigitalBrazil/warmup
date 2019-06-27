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
import {
	ICatalogService,
	ICatalogVersion,
	IHomepage,
	IUriContext,
	IYEventMessageData,
	SeInjectable,
	SystemEventService,
	TypedMap
} from "smarteditcommons";
import {ICMSPage} from 'cmscommons/dtos/ICMSPage';

import * as angular from "angular";

/** @internal */
/**
 * Expose through angular the event for sendEventShowReplaceParentHomePageInfo()
 */
export const DEFAULT_CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO = "CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO";

/** @internal */
/**
 * Expose through angular the event for sendEventHideReplaceParentHomePageInfo()
 */
export const DEFAULT_CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO = "CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO";

/** @internal */
/**
 * !NGDOC
 * Status of a ICatalogHomepageDetails
 */
export enum CatalogHomepageDetailsStatus {
	// Status is being calculated
	PENDING = 'PENDING',
	// No current homepage for the given catalogversion
	NO_HOMEPAGE = 'NO_HOMEPAGE',
	// There is a homepage is in the given catalog version (not inherited)
	LOCAL = 'LOCAL',
	// The old homepage is in the given catalog version
	OLD = 'OLD',
	// There is a homepage, but it is inherited from a parent catalog
	PARENT = 'PARENT'
}

/** @internal */
/**
 * !NGDOC
 * ICatalogHomepageDetails is a mashup of a bunch of different values needed in the UI
 * Since the logic is a big insane, to try and keep the components clean we dump all the crap into 
 * this 1 object.
 * Depending on the status, some of the other fields will be filled, but not others
 */
export interface ICatalogHomepageDetails {
	status: CatalogHomepageDetailsStatus;
	parentCatalogName?: TypedMap<string>;
	parentCatalogVersion?: string;
	targetCatalogName?: TypedMap<string>;
	targetCatalogVersion?: string;
	currentHomepageName?: string;
	currentHomepageUid?: string;
	oldHomepageUid?: string;
}

/**
 * @ngdoc object
 * @name cmsSmarteditServicesModule.object:HomepageType
 * @description
 * An enum type representing the homepage type of a cms page.
 */
export enum HomepageType {


    /**
     * @ngdoc property
     * @name OLD
     * @propertyOf cmsSmarteditServicesModule.object:HomepageType
     * @description
     * An enum value of type HomepageType describing if a cms page was previously a homepage.
     */
	OLD = "old",

    /**
     * @ngdoc property
     * @name CURRENT
     * @propertyOf cmsSmarteditServicesModule.object:HomepageType
     * @description
     * An enum value of type HomepageType describing if a cms page is a current homepage.
     */
	CURRENT = "current",

    /**
     * @ngdoc property
     * @name FALLBACK
     * @propertyOf cmsSmarteditServicesModule.object:HomepageType
     * @description
     * An enum value of type HomepageType describing if a cms page is a fallback homepage.
     */
	FALLBACK = "fallback"
}

/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:HomepageService
 *
 * @description
 * This service is used to determine if a cms page is a current, a previous homepage, or neither.
 */
@SeInjectable()
export class HomepageService {

	constructor(
		private $q: angular.IQService,
		private catalogService: ICatalogService,
		private systemEventService: SystemEventService,
		private CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO: string,
		private CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO: string,
		private CONTEXT_CATALOG: string
	) {}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#sendEventHideReplaceParentHomePageInfo
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * Send an event to show info to the user about replacing a homepage from a parent catalog.
     *
     * @param {IYEventMessageData} data A IYEventMessageData object
     */
	sendEventHideReplaceParentHomePageInfo(data: IYEventMessageData): void {
		this.systemEventService.publish(this.CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO, data);
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#sendEventShowReplaceParentHomePageInfo
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * Send an event to hide the info to the user about replacing a homepage from a parent catalog.
     *
     * @param {IYEventMessageData} data A IYEventMessageData object
     */
	sendEventShowReplaceParentHomePageInfo(data: IYEventMessageData): void {
		this.systemEventService.publish(this.CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO, data);
	}


	/** @internal */
	/**
	 * !NGDOC
	 * getHomepageDetailsForContext is a mashup of logic needed for the frontend
	 * Both the pageDisplayConditions and newPageDisplayConditions components use it for various
	 * ui related things, like enable/disable of the homepage checkbox, or show messages on the UI
	 * 
	 * Given a uriContext, basically there are 3 mains return types, mashed into 1 typescript type
	 * 
	 * 1) CatalogHomepageDetailsStatus.NO_HOMEPAGE
	 * This means that the given uriContext has no homepage whatsoever. This probably indicates an issue with the data.
	 * 
	 * 2) CatalogHomepageDetailsStatus.PARENT
	 * This means that the current homepage for the given uriContext is inherited from a parent catalog.
	 * In this case, the returned ICatalogHomepageDetails contains the parentCatalogName, parentCatalogVersion, 
	 * targetCatalogName, and targetCatalogVersion, 
	 * 
	 * 3) CatalogHomepageDetailsStatus.LOCAL
	 * This means that the current homepage for the given uriContext belongs to the catalog of that uriContext.
	 * In this case, the returned ICatalogHomepageDetails contains the currentHomepageName, currentHomepageUid, 
	 * and oldHomepageUid
	 * 
	 * @param {IUriContext} uriContext A IUriContext object
	 * 
	 * @returns {ICatalogHomepageDetails} ICatalogHomepageDetails with one of the 3 options as indicated above
	 */
	getHomepageDetailsForContext(uriContext: IUriContext): angular.IPromise<ICatalogHomepageDetails> {
		return this.catalogService.getContentCatalogVersion(uriContext).then((catalogVersion: ICatalogVersion) => {

			return catalogVersion.homepage ? catalogVersion.homepage.current : null;
		}).then((homepageForCurrentCatalog: IHomepage) => {

			return this.buildHomepageDetailsForContext(homepageForCurrentCatalog, uriContext);
		});
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#isCurrentHomepage
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * This method checks if the cms page is a current homepage.
     *
     * @param {Object} cmsPage The cms page.
     * @param {Object} uriContext The uriContext.
     *
     * @returns {Promise} A promise resolved with a boolean indicating whether the cms page is the current homepage.
     */
	isCurrentHomepage(cmsPage: ICMSPage, uriContext: IUriContext): angular.IPromise<boolean> {
		return this.isHomepageType(cmsPage, uriContext, HomepageType.CURRENT);
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#isOldHomepage
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * This method checks if the cms page is a current homepage.
     *
     * @param {Object} cmsPage The cms page.
     * @param {Object} uriContext The uriContext.
     *
     * @returns {Promise} A promise resolved with a boolean indicating whether the cms page is a previous homepage.
     */
	isOldHomepage(cmsPage: ICMSPage, uriContext: IUriContext): angular.IPromise<boolean> {
		return this.isHomepageType(cmsPage, uriContext, HomepageType.OLD);
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#getHomepageType
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * This method returns the homepage type of a cms page.
     *
     * @param {Object} cmsPage The cms page.
     * @param {Object} uriContext The uriContext.
     *
     * @returns {Promise} A promise resolved with a enum type indicating whether the cms page is a current, previous homepage or  null if neither.
     */
	getHomepageType(cmsPage: ICMSPage, uriContext: IUriContext): angular.IPromise<HomepageType> {
		return this.catalogService.getContentCatalogVersion(uriContext).then((catalog: ICatalogVersion) => {
			if (!catalog || !catalog.homepage) {
				return null;
			}

			if (catalog.homepage.current && catalog.homepage.current.uid === cmsPage.uid && catalog.homepage.current.catalogVersionUuid === cmsPage.catalogVersion) {

				return HomepageType.CURRENT;

			} else if (catalog.homepage.old && catalog.homepage.old.uid === cmsPage.uid) {

				return this.compareCatalogVersions<HomepageType>(catalog.homepage.old.catalogVersionUuid, cmsPage.catalogVersion,
					(oldHomepageCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) => {
						return oldHomepageCatalog.catalogId === pageCatalog.catalogId ? HomepageType.OLD : null;
					});

			} else if (catalog.homepage.fallback && catalog.homepage.fallback.uid === cmsPage.uid) {

				return this.compareCatalogVersions<HomepageType>(catalog.homepage.fallback.catalogVersionUuid, cmsPage.catalogVersion,
					(fallbackCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) => {
						return fallbackCatalog.uuid === pageCatalog.uuid ? HomepageType.FALLBACK : null;
					});
			}

			return null;
		});
	}

    /**
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:HomepageService#hasFallbackHomePage
     * @methodOf cmsSmarteditServicesModule.service:HomepageService
     *
     * @description
     * Returns true if the catalog has a fallback homepage.
     *
     * @param {Object} uriContext The uriContext.
     *
     * @returns {Promise} A promise resolved to true when the catalog has a fallback homepage.
     */
	hasFallbackHomePage(uriContext: IUriContext): angular.IPromise<boolean> {
		return this.catalogService.getContentCatalogVersion(uriContext).then((catalog: ICatalogVersion) => {
			if (!catalog || !catalog.homepage) {
				throw Error(`HomepageService.hasFallbackHomePage - Catalog does not have homepage fallback property`);
			}
			return !!catalog.homepage.fallback;
		});
	}

	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:HomepageService#canSyncHomepage
	 * @methodOf cmsSmarteditServicesModule.service:HomepageService
	 *
	 * @description
	 * Returns true if the page can be synced
	 *
	 * @param {ICMSPage} cmsPage The cms page.
	 * @param {IUriContext} uriContext The uriContext.
	 *
	 * @returns {Promise} A promise resolved to true when the page can be synced.
	 */
	canSyncHomepage(cmsPage: ICMSPage, uriContext: IUriContext): angular.IPromise<boolean> {
		return this.isOldHomepage(cmsPage, uriContext).then((isOld) => {
			if (!isOld) {
				return true;
			}
			return this.catalogService.getContentCatalogVersion(uriContext).then((catalog: ICatalogVersion) => {
				return this.compareCatalogVersions<boolean>(catalog.homepage.current.catalogVersionUuid, cmsPage.catalogVersion,
					(currentCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) => {
						return cmsPage.uid !== catalog.homepage.current.uid && currentCatalog.catalogId !== pageCatalog.catalogId;
					});
			});
		});
	}

	private compareCatalogVersions<T>(catalogAUuid: string, catalogBUuid: string, compare: (a: ICatalogVersion, b: ICatalogVersion) => T): angular.IPromise<T> {
		return this.catalogService.getCatalogVersionByUuid(catalogAUuid).then((catalogVersionA: ICatalogVersion) => {
			return this.catalogService.getCatalogVersionByUuid(catalogBUuid).then((catalogVersionB: ICatalogVersion) => {
				return compare(catalogVersionA, catalogVersionB);
			});
		});
	}

	private isHomepageType(cmsPage: ICMSPage, uriContext: IUriContext, type: HomepageType): angular.IPromise<boolean> {
		return this.getHomepageType(cmsPage, uriContext).then((homepageType: HomepageType) => {
			return homepageType === type;
		});
	}

	private buildHomepageDetailsForContext(homepageForCurrentCatalog: IHomepage, uriContext: IUriContext): angular.IPromise<ICatalogHomepageDetails> {
		if (!homepageForCurrentCatalog) {
			return this.$q.resolve({
				status: CatalogHomepageDetailsStatus.NO_HOMEPAGE
			});
		}

		return this.catalogService.getCatalogVersionByUuid(homepageForCurrentCatalog.catalogVersionUuid).then((homepageCatalogVersion: ICatalogVersion) => {

			const homepageComesFromParent = homepageCatalogVersion.catalogId !== uriContext[this.CONTEXT_CATALOG];
			return this.catalogService.getContentCatalogVersion(uriContext).then((currentCatalogVersion: ICatalogVersion) => {

				if (homepageComesFromParent) {
					return {
						status: CatalogHomepageDetailsStatus.PARENT,
						parentCatalogName: homepageCatalogVersion.catalogName,
						parentCatalogVersion: homepageCatalogVersion.version,
						targetCatalogName: currentCatalogVersion.catalogName,
						targetCatalogVersion: currentCatalogVersion.version
					};
				}

				return {
					status: CatalogHomepageDetailsStatus.LOCAL,
					currentHomepageName: homepageForCurrentCatalog.name,
					currentHomepageUid: homepageForCurrentCatalog.uid,
					oldHomepageUid: homepageCatalogVersion.homepage ? (homepageCatalogVersion.homepage.old ? homepageCatalogVersion.homepage.old.uid : null) : null
				};
			});
		});
	}

}
