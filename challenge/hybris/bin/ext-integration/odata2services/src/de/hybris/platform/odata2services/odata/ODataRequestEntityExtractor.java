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
package de.hybris.platform.odata2services.odata;

import org.apache.olingo.odata2.api.processor.ODataRequest;

/**
 *  Extracts the entity from the {@link ODataRequest}.
 */
public interface ODataRequestEntityExtractor
{
	/**
	 * Indicates whether this Extractor should be used to extract
	 * the entity from the given {@link ODataRequest}
	 *
	 * @param request Request to check
	 * @return true if handler can handle request, else false
	 */
	boolean isApplicable(ODataRequest request);

	/**
	 * Extracts the entity from the {@link ODataRequest}.
	 *
	 * @param request Use this request to extract the entity
	 * @return The entity or empty string
	 */
	String extract(final ODataRequest request);
}
