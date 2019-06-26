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
import {IPreviewData, IPreviewResponse} from './IPreview';
import {UrlUtils} from "smarteditcommons/utils/UrlUtils";
import {SeInjectable} from '../dependencyInjection/di';

/**
 * @name smarteditServicesModule.interface:IPreviewService
 *
 * @description
 *
 * Interface for previewService.
 *
 * This service is for managing the storefront preview ticket and is proxied across the gateway. (implements)
 *
 */
@SeInjectable()
export abstract class IPreviewService {

	constructor(private urlUtils: UrlUtils) {
	}

    /**
     * @name smarteditServicesModule.interface:IPreviewService#createPreview
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will create a new previewTicket for the given experience, using the preview API
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {Object} previewData Data representing storefront preview
     *
     * @returns {Object} A {@link smarteditServicesModule.interface:IPreview IPreviewResponse} object
     */
	createPreview(previewData: IPreviewData): angular.IPromise<IPreviewResponse> {
		'proxyFunction';
		return null;
	}

    /**
     * @name smarteditServicesModule.interface:IPreviewService#getResourcePathFromPreviewUrl
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will preduce a resourcePath from a given preview url
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {Object} previewUrl A URL for a storefornt with preview
     *
     * @returns {Object} A {@link smarteditServicesModule.interface:IPreview IPreviewResponse} object
     */
	getResourcePathFromPreviewUrl(previewUrl: string): angular.IPromise<string> {
		'proxyFunction';
		return null;
	}

    /**
     * @name smarteditServicesModule.interface:IPreviewService#updateUrlWithNewPreviewTicketId
     * @methodOf smarteditServicesModule.interface:IPreviewService
     *
     * @description
     * This method will create a new preview ticket, and return the given url with an updated previewTicketId query param
     * <br />
     * This method does *NOT* update the current experience.
     *
     * @param {string} storefrontUrl Existing storefront url
     * @param {Object} previewData JSON representing storefront previewData (catalog, catalaog vesion, etc...)
     *
     * @returns {string} A new string with storefrontUrl having the new ticket ID inside
     */
	updateUrlWithNewPreviewTicketId(storefrontUrl: string, previewData: IPreviewData): angular.IPromise<string> {
		return this.createPreview(previewData).then((preview: IPreviewResponse) => {
			return this.urlUtils.updateUrlParameter(storefrontUrl, 'cmsTicketId', preview.previewTicketId);
		});
	}

}
