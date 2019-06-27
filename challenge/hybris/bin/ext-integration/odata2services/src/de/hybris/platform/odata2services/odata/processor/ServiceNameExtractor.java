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
package de.hybris.platform.odata2services.odata.processor;

import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * A service to extract the service name from the {@link ODataContext}
 */
public interface ServiceNameExtractor
{
	/**
	 * Extracts the service name from the context
	 *
	 * @param context Context containing the URL with the service name
	 * @param integrationKey The integration key  
	 * @return The service name
	 */
	String extract(ODataContext context, String integrationKey);
}
