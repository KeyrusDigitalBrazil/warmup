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
package de.hybris.platform.odata2services.odata.processor.writer;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;

public interface ResponseWriter
{
	/**
	 * Writes a single data set as an {@link ODataResponse}
	 * @param itemLookupRequest Parameter object containing the information for the write
	 * @param entitySet Entity set the data belong to
	 * @param data Data to write
	 * @return ODataResponse
	 */
	ODataResponse write(ItemLookupRequest itemLookupRequest, EdmEntitySet entitySet, Map<String, Object> data) throws ODataException;

	/**
	 * Writes a collection of data set as an ODataResponse
	 * @param itemLookupRequest Parameter object containing the information for the write
	 * @param entitySet Entity set the data belong to
	 * @param result Collection of data to write
	 * @return ODataResponse
	 */
	ODataResponse write(ItemLookupRequest itemLookupRequest, EdmEntitySet entitySet, ItemLookupResult<ODataEntry> result) throws ODataException;
}
