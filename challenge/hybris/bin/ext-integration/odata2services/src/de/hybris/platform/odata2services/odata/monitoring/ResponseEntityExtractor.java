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

import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * Extracts entities contained an OData response.
 */
public interface ResponseEntityExtractor
{
	/**
	 * Extracts entities returned back in an OData response. A single response may contain one ore more entities, that were
	 * persisted.
	 * @param response a response to extract entities from
	 * @return a collection of extracted response entities. A valid response should have at least one response entity.
	 */
	List<ResponseChangeSetEntity> extractFrom(ODataResponse response);
}
