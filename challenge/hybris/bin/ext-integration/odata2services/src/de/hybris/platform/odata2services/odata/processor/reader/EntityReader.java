/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor.reader;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;

/**
 * EntityReader reads from the commerce suite the requested information
 */
public interface EntityReader
{
	/**
	 * Indicates whether this EntityReader can read the requested information
	 *
	 * @param uriInfo Used to determine whether this EntityReader is applicable
	 * @return {@code true} if this EntityReader can do the read, otherwise {@code false}
	 */
	boolean isApplicable(UriInfo uriInfo);

	/**
	 * Reads from the commerce suite the requested information
	 *
	 * @param itemLookupRequest Parameter object that contains the request
	 * @return The result provided as an {@link ODataResponse}
	 * @throws ODataException When an error occurs retrieving the entity
	 */
	ODataResponse read(ItemLookupRequest itemLookupRequest) throws ODataException;
}
