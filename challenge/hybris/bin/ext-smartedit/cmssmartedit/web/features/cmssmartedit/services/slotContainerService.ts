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
export interface ContainerInfo {
	containerId: string;
	containerType: string;
	pageId: string;
	slotId: string;
	components: string[];
}
import {IExperienceService, IRestServiceFactory, SeInjectable} from 'smarteditcommons';
/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:slotContainerService
 *
 * @description
 * This service allows retrieving information about the containers found in a given page. 
 */
@SeInjectable()
export class SlotContainerService {

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	private containersInPage: ContainerInfo[];
	private containersRestService: any;

	// --------------------------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------------------------
	constructor(private $q: angular.IQService, private lodash: any, restServiceFactory: IRestServiceFactory, private experienceService: IExperienceService, PAGE_CONTEXT_SITE_ID: string, PAGE_CONTEXT_CATALOG: string, PAGE_CONTEXT_CATALOG_VERSION: string) {
		const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscontainers?pageId=:pageId`;
		this.containersRestService = restServiceFactory.get(contentSlotContainerResourceURI);
	}

	// --------------------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------------------
	/**
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:slotContainerService#getComponentContainer
	 * @methodOf cmsSmarteditServicesModule.service:slotContainerService
	 *
	 * @description
	 * This method is used to retrieve the information about the container holding the provided component.
	 * If the component is not inside a container, the method returns null. 
	 * 
	 * @param {String} slotId The SmartEdit id of the slot where the component in question is located.
	 * @param {String} componentUuid The UUID of the component as defined in the database.
	 *
	 * @returns {angular.IPromise<ContainerInfo>} A promise that resolves to the information of the container of the component provided. 
	 * Will be null if the component is not inside a container. 
	 */
	public getComponentContainer(slotId: string, componentUuid: string): angular.IPromise<ContainerInfo> {
		return this.loadContainersInPageInfo().then((containersInPage: ContainerInfo[]) => {
			const containers = containersInPage.filter((container: ContainerInfo) => {
				return container.slotId === slotId && this.lodash.includes(container.components, componentUuid);
			});

			return (containers.length > 0) ? containers[0] : null;
		});
	}

	// --------------------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------------------
	private loadContainersInPageInfo() {
		if (this.containersInPage) {
			return this.$q.when(this.containersInPage);
		} else {
			return this.experienceService.getCurrentExperience().then((experience: any) => {
				return this.containersRestService.get({
					pageId: experience.pageId
				});
			}).then((result: any) => {
				this.containersInPage = result.pageContentSlotContainerList as ContainerInfo[];
				return this.containersInPage;
			});
		}
	}

}

