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
import {Payload} from 'smarteditcommons';

/**
 * @name smarteditServicesModule.interface:IPreviewResponse
 *
 * @description
 * Interface for data representing a storefront preview ticket.
 *
 */
export interface IPreviewResponse extends Payload {

    /**
     * @name previewTicketId
     * @propertyOf smarteditServicesModule.interface:IPreviewResponse
     * @description
     * Identifier for the preview
     */
	previewTicketId: string;

    /**
     * @name resourcePath
     * @propertyOf smarteditServicesModule.interface:IPreviewResponse
     * @description
     * The URI of the storefront resource
     */
	resourcePath: string;
}

/**
 * @name smarteditServicesModule.interface:ICatalogVersionData
 *
 * @description
 * Interface for data representing a catalog version
 */
export interface IPreviewCatalogVersionData extends Payload {
	/**
	 * @name catalog
	 * @propertyOf smarteditServicesModule.interface:ICatalogVersionData
	 * @description
	 * the catalog id
	 */
	catalog: string;

	/**
	 * @name catalogVersion
	 * @propertyOf smarteditServicesModule.interface:ICatalogVersionData
	 * @description
	 * the catalog version
	 */
	catalogVersion: string;
}
/**
 * @name smarteditServicesModule.interface:IPreviewData
 *
 * @description
 * Interface for data sent to the preview API.
 *
 * Since the preview api is extensible, you can send more fields by adding a new interface that extends this one.
 * All additional members of the Object passed to the preview API will be included in the request.
 */
export interface IPreviewData extends Payload {

	/**
	 * @name catalogVersions
	 * @propertyOf smarteditServicesModule.interface:IPreviewData
	 * @description
	 * an array of {@link smarteditServicesModule.interface:IPreviewCatalogVersionData} to preview
	 */
	catalogVersions: IPreviewCatalogVersionData[];

	/**
	 * @name language
	 * @propertyOf smarteditServicesModule.interface:IPreviewData
	 * @description
	 * the isocode of the language to preview
	 */
	language: string;

	/**
	 * @name resourcePath
	 * @propertyOf smarteditServicesModule.interface:IPreviewData
	 * @description
	 * the resource path to preview
	 */
	resourcePath: string;

	/**
	 * @name pageId
	 * @propertyOf smarteditServicesModule.interface:IPreviewData
	 * @description
	 * the uid of the page to preview
	 */
	pageId?: string;
}