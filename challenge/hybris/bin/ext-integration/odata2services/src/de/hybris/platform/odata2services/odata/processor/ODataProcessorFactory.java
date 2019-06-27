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
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;

/**
 * A factory for creating {@link ODataSingleProcessor} instances, which are used by OLingo for processing the ODataRequests.
 */
public interface ODataProcessorFactory
{
	/**
	 * Creates new instance of the processor. The implementation should guarantee a different instance for not equal contexts.
	 * @param context a request context to create a processor for.
	 * @return an instance of the processor to use for processing the context.
	 */
	ODataSingleProcessor createProcessor(ODataContext context);
}
