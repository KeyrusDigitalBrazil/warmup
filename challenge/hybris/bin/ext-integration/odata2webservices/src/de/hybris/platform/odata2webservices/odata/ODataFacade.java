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
package de.hybris.platform.odata2webservices.odata;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * Receives an ODataContext and delegates its handling. Returns an ODataResponse.
 */
public interface ODataFacade
{
	/**
	 * Obtains ODataResponse with a stream that contains odata EDMX schema specified by the {@code oDataContext}.
	 *
	 * @param oDataContext contains information about what schema should be retrieved.
	 * @return requested ODataResponse with a stream that contains EDMX schema
	 */
	ODataResponse handleGetSchema(ODataContext oDataContext);

	/**
	 * Obtains ODataResponse with a stream that contains entity data model.
	 *
	 * @param oDataContext contains information about what entity should be retrieved.
	 * @return requested ODataResponse with a stream that contains entity data.
	 */
	ODataResponse handleGetEntity(ODataContext oDataContext);

	/**
	 * Creates an integration object item based on request.
	 *
	 * @param oDataContext contains information about what item should be created
	 * @return response with information about the newly created item
	 */
	ODataResponse handlePost(ODataContext oDataContext);
}
