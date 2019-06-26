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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;

public class LanguageNotSupportedException extends ODataRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.BAD_REQUEST;
	private static final String ERROR_CODE = "invalid_language";
	private final String language;

	public LanguageNotSupportedException(final String language, final Exception e)
	{
		super(String.format("The language provided [%s] is not available.", language), Locale.ENGLISH, STATUS_CODE, ERROR_CODE, e);
		this.language = language;
	}

	public String getLanguage()
	{
		return language;
	}
}
