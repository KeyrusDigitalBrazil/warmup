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
package de.hybris.platform.odata2services.odata.persistence;

import java.util.Locale;
import java.util.Optional;

import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * Extracts the Locale to be used by odata services from ODataRequest encapsulated by the ODataContext
 */
public interface ODataContextLanguageExtractor
{
	/**
	 * Get the Content/Accept Locale based on the given ODataContext
	 * @param oDataContext The ODataContext to be used.
	 * @param headerName The header name to use. For instance: "Content-Language" or "Accept-Language"
	 * @return The locale
	 */
	Locale extractFrom(ODataContext oDataContext, String headerName);

	/**
	 * Get the Language based on the given ODataContext
	 * @param oDataContext The ODataContext to be used.
	 * @return The Locale found in the "Accept-Language" header
	 */
	Optional<Locale> getAcceptLanguage(ODataContext oDataContext);
}
