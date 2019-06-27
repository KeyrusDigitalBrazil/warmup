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

import {authorizationEvictionTag, rarelyChangingContent, Cached, IRestService, IRestServiceFactory, ISite, OperationContextRegistered, SeInjectable} from 'smarteditcommons';

/** @internal */
interface ISiteDTO {
	sites: ISite[];
}
/**
 * @ngdoc service
 * @name smarteditServicesModule.service:SiteService
 *
 * @description
 * The Site Service fetches all sites configured on the hybris platform using REST calls to the cmswebservices sites API.
 */
@OperationContextRegistered('SITES_RESOURCE_URI', 'CMS')
@SeInjectable()
export class SiteService {
	private cache: ISite[];
	private siteRestService: IRestService<ISiteDTO>;

	constructor(
		restServiceFactory: IRestServiceFactory,
		SITES_RESOURCE_URI: string) {

		this.cache = null;
		this.siteRestService = restServiceFactory.get<ISiteDTO>(SITES_RESOURCE_URI);
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:SiteService#getAccessibleSites
	 * @methodOf smarteditServicesModule.service:SiteService
	 * 
	 * @description
	 * Fetches a list of sites for which user has at-least read access to one of the non-active catalog versions.
	 * 
	 * @returns {Object} A {@link https://docs.angularjs.org/api/ng/service/$q promise} of {@link smarteditServicesModule.interface:ISite ISite} array.
	 */
	@Cached({actions: [rarelyChangingContent], tags: [authorizationEvictionTag]})
	getAccessibleSites(): angular.IPromise<ISite[]> {
		return this.siteRestService.get({}).then((sitesDTO: ISiteDTO) => {
			return sitesDTO.sites;
		});
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:SiteService#getSites
     * @methodOf smarteditServicesModule.service:SiteService
     *
     * @description
     * Fetches a list of sites configured for accessible sites. The list of sites fetched using REST calls through
     * the cmswebservices sites API. 
     *
     * @returns {Object} A {@link https://docs.angularjs.org/api/ng/service/$q promise} of {@link smarteditServicesModule.interface:ISite ISite} array.
     */
	@Cached({actions: [rarelyChangingContent], tags: [authorizationEvictionTag]})
	getSites(): angular.IPromise<ISite[]> {
		//  Uses two REST API calls because of multicountry. The first call gives all the sites for which the user has permissions to. 
		return this.getAccessibleSites().then((sites) => {
			const allCatalogs: string[] = sites.reduce(function(catalogs: string[], site: ISite) {
				Array.prototype.push.apply(catalogs, site.contentCatalogs);
				return catalogs;
			}, []);

			// The second call with catalogIds gives all the corresponding sites in the hierarchy.
			return this.siteRestService.get({
				catalogIds: allCatalogs.join(',')
			}).then((allSites: ISiteDTO) => {
				this.cache = allSites.sites;
				return this.cache;
			});
		});
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:SiteService#getSiteById
     * @methodOf smarteditServicesModule.service:SiteService
     *
     * @description
     * Fetches a site, configured on the hybris platform, by its uid. The sites fetched using REST calls through
     * cmswebservices sites API.
     * @param {String} uid unique site ID
     * @returns {object} A {@link https://docs.angularjs.org/api/ng/service/$q promise} of {@link smarteditServicesModule.interface:ISite ISite}.
     */
	getSiteById(uid: string): angular.IPromise<ISite> {
		return this.getSites().then(function(sites: ISite[]) {
			return sites.filter(function(site) {
				return site.uid === uid;
			})[0];
		});
	}
}