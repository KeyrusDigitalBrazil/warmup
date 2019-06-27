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
import * as lo from 'lodash';
import {IExperience, IExperienceCatalogVersion, IPreviewCatalogVersionData, IPreviewData} from 'smarteditcommons/services';
import {Payload} from 'smarteditcommons/dtos';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:ExperienceService
 *
 * @description
 * ExperienceService deals with building experience objects given a context.
 */

export abstract class IExperienceService {

	/** @internal */
	constructor(protected lodash: lo.LoDashStatic) {
	}

	/* @internal */
	updateExperiencePageContext(pageCatalogVersionUuid: string, pageId: string): angular.IPromise<IExperience> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#getCurrentExperience
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * Retrieves the active experience.
	 *
	 * @returns {IExperience} an {@link smarteditServicesModule.interface:IExperience experience}
	 */
	getCurrentExperience(): angular.IPromise<IExperience> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#setCurrentExperience
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * Stores a given experience as current experience.
	 * Invoking this method ensures that a hard refresh of the application will preserve the experience.
	 * 
	 * @returns {angular.IPromise<IExperience>} a promise returning the experience
	 */
	setCurrentExperience(experience: IExperience): angular.IPromise<IExperience> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#hasCatalogVersionChanged
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * Determines whether the catalog version has changed between the previous and current experience
	 * 
	 * @returns {angular.IPromise<boolean>} a promise returning whether thta catalog version has changed
	 */
	hasCatalogVersionChanged(): angular.IPromise<boolean> {
		'proxyFunction';
		return null;
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#buildRefreshedPreviewUrl
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Retrieves the active experience, creates a new preview ticket and returns a new preview url with an updated
     * previewTicketId query param
     *
     * @returns {angular.IPromise<string>} an url containing the new previewTicketId
     */
	buildRefreshedPreviewUrl(): angular.IPromise<string> {
		'proxyFunction';
		return null;
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#updateExperience
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * Retrieves the active experience, merges it with a new experience, creates a new preview ticket and reloads the
	 * preview within the iframeManagerService
	 *
	 * @param {Payload=} newExperience The object containing new attributes to be merged with the current experience
	 *
	 * @returns {angular.IPromise<IExperience>} An promise of the updated experience
	 */
	updateExperience(newExperience?: Payload): angular.IPromise<IExperience> {
		'proxyFunction';
		return null;
	}

	/** @internal */
	_convertExperienceToPreviewData(experience: IExperience, resourcePath: string): IPreviewData {
		const previewData = this.lodash.cloneDeep(experience) as any;
		const catalogVersions: IPreviewCatalogVersionData[] = [];

		delete previewData.catalogDescriptor;
		delete previewData.siteDescriptor;
		delete previewData.languageDescriptor;
		delete previewData.pageContext;
		delete previewData.productCatalogVersions;

		if (experience.productCatalogVersions && experience.productCatalogVersions.length) {
			experience.productCatalogVersions.forEach((productCatalogVersion: IExperienceCatalogVersion) => {
				catalogVersions.push({
					catalog: productCatalogVersion.catalog,
					catalogVersion: productCatalogVersion.catalogVersion
				});
			});
		}
		catalogVersions.push({
			catalog: experience.catalogDescriptor.catalogId,
			catalogVersion: experience.catalogDescriptor.catalogVersion
		});

		previewData.catalogVersions = catalogVersions;
		previewData.language = experience.languageDescriptor.isocode;
		previewData.resourcePath = resourcePath;

		return previewData as IPreviewData;
	}
}