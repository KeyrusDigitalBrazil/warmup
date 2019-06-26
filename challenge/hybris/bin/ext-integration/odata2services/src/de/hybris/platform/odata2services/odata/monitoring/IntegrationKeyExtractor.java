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
package de.hybris.platform.odata2services.odata.monitoring;

import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * Extracts the integrationKey value from the {@link ODataResponse}
 */
public interface IntegrationKeyExtractor
{
	/**
	 * Indicates whether this IntegrationKeyExtractor is applicable to the given {@link ODataResponse}
	 * @param contentType content type of the response
	 * @return true if can extract, otherwise false
	 */
	boolean isApplicable(String contentType);

	/**
	 * Gets the integrationKey value from the {@link ODataResponse}
	 *
	 * @param responseBody Response body to extract the integrationKey value from
	 * @param statusCode status code of the response
	 * @return integrationKey value
	 */
	String extractIntegrationKey(String responseBody, int statusCode);
}
