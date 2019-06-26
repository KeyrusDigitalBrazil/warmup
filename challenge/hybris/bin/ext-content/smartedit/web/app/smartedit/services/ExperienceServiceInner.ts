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
import {GatewayProxied, IExperience, IExperienceService, IPreviewData, IPreviewService, SeInjectable} from 'smarteditcommons';

/** @internal */
@GatewayProxied('loadExperience', 'updateExperiencePageContext', 'getCurrentExperience', 'setCurrentExperience', 'hasCatalogVersionChanged', 'buildRefreshedPreviewUrl')
@SeInjectable()
export class ExperienceService extends IExperienceService {
	constructor(
		private $q: angular.IQService,
		private $location: angular.ILocationService,
		private $log: angular.ILogService,
		lodash: lo.LoDashStatic,
		private previewService: IPreviewService
	) {
		super(lodash);
	}

	buildRefreshedPreviewUrl(): angular.IPromise<string> {
		return this.getCurrentExperience().then((experience: IExperience) => {

			if (!experience) {
				throw new Error("ExperienceService.buildRefreshedPreviewUrl() - Invalid experience from ExperienceService.getCurrentExperience()");
			}

			const promise = this.previewService.getResourcePathFromPreviewUrl(experience.siteDescriptor.previewUrl);

			return promise.then((resourcePath: string) => {

				const previewData: IPreviewData = this._convertExperienceToPreviewData(experience, resourcePath);

				return this.previewService.updateUrlWithNewPreviewTicketId(this.$location.absUrl(), previewData);
			}, (err: any) => {
				this.$log.error('ExperienceService.buildRefreshedPreviewUrl() - failed to retrieve resource path');
				return this.$q.reject(err);
			});
		}, (err: any) => {
			this.$log.error('ExperienceService.buildRefreshedPreviewUrl() - failed to retrieve current experience');
			return this.$q.reject(err);
		});
	}
}