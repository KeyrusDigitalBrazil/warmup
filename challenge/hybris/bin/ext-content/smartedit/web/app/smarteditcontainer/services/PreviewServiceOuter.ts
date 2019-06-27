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
import * as angular from 'angular';
import * as lo from 'lodash';

import {
	GatewayProxied,
	IPreviewData,
	IPreviewResponse,
	IPreviewService,
	IRestService,
	IRestServiceFactory,
	SeInjectable,
	UrlUtils
} from 'smarteditcommons';

/** @internal */
@GatewayProxied()
@SeInjectable()
export class PreviewService extends IPreviewService {

	// TODO - cache invalidation on configuration changes
	private restService: IRestService<IPreviewResponse>;
	private domain: string;

	constructor(
		private $log: angular.ILogService,
		private $q: angular.IQService,
		private loadConfigManagerService: any,
		private PREVIEW_RESOURCE_URI: string,
		private restServiceFactory: IRestServiceFactory,
		private lodash: lo.LoDashStatic,
		private getAbsoluteURL: (domain: string, url: string) => string,
		urlUtils: UrlUtils) {
		super(urlUtils);
	}

	createPreview(previewData: IPreviewData): angular.IPromise<IPreviewResponse> {

        /**
         * We don't know about any fields coming from other extensions, but throw error for any of the fields
         * that we do know about, namely the IPreviewData interface fields
         */
		this.validatePreviewDataAttributes(previewData);

		return this.getRestService().then((restService: IRestService<IPreviewResponse>) => {
			return restService.save(previewData).then((response: any) => {
				return {
					previewTicketId: response.ticketId,
					resourcePath: response.resourcePath,
					versionId: response.versionId
				};
			}, (err: any) => {
				this.$log.error('PreviewService.createPreview() - Error creating preview');
				return this.$q.reject(err);
			});
		}, (err: any) => {
			this.$log.error('PreviewService.createPreview() - Error loading configuration');
			return this.$q.reject(err);
		});
	}

	getResourcePathFromPreviewUrl(previewUrl: string): angular.IPromise<string> {
		// just to trigger the get of configuration if not already done
		return this.getRestService().then(
			(restService: IRestService<IPreviewResponse>) => this.getAbsoluteURL(this.domain, previewUrl),
			(err: any) => {
				this.$log.error('PreviewService.getResourcePathFromPreviewUrl() - Error loading configuration');
				return this.$q.reject(err);
			});
	}

	private getRestService(): angular.IPromise<IRestService<IPreviewResponse>> {
		if (this.restService) {
			return this.$q.when(this.restService);
		}
		return this.loadConfigManagerService.loadAsObject().then((configurations: any) => {
			this.restService = this.restServiceFactory.get(configurations.previewTicketURI || this.PREVIEW_RESOURCE_URI);
			this.domain = configurations.domain;
			return this.$q.when(this.restService);
		}, (err: any) => {
			this.$log.error('PreviewService.getRestService() - Error loading configuration');
			return this.$q.reject(err);
		});
	}

	private validatePreviewDataAttributes(previewData: IPreviewData) {
		const requiredFields = [
			'catalogVersions',
			'language',
			'resourcePath'
		];
		requiredFields.forEach((elem) => {
			if (this.lodash.isEmpty(previewData[elem])) {
				throw new Error(`ValidatePreviewDataAttributes - ${elem} is empty`);
			}
		}
		);
	}

}
