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
package de.hybris.platform.integrationservices.monitoring;

import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;

/**
 * A parser which extracts an {@code MonitoredRequestError} out of an Response.
 */
public interface MonitoredRequestErrorParser<T extends MonitoredRequestErrorModel>
{
	/**
	 * Indicates whether this MonitoredRequestErrorParser is applicable to the given Response
	 *
	 * @param contentType Response content type
	 * @param statusCode response status code
	 * @return true if can parse, otherwise false
	 */
	boolean isApplicable(String contentType, int statusCode);

	/**
	 * Gets the {@code MonitoredRequestError} value from the Response
	 *
	 * @param error class to be instantiated as error
	 * @param statusCode response status code
	 * @param responseBody Response body to extract the error from
	 * @return the error to persist as part of the {@link T}
	 */
	T parseErrorFrom(Class<T> error, int statusCode, String responseBody);
}
