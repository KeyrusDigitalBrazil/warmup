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

import java.util.List;

import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * Extracts request entities from an OData request
 */
public interface RequestBatchEntityExtractor
{
	/**
	 * Extracts request entity.
	 * @param context the {@code ODataContext} which contains the {@code ODataRequest} as well as other relevant request information
	 * @return a collection of extracted entities. A valid request should contain at least one entity.
	 */
	List<RequestBatchEntity> extractFrom(ODataContext context);
}
