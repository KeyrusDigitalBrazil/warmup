/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorservices.web.payment.validation;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;


public class UrlSchemeValidator
{
	private static final Logger LOG = Logger.getLogger(UrlSchemeValidator.class);

	private UrlSchemeValidator()
	{
	}

	public static boolean validate(final String url)
	{
		if (url == null)
		{
			return false;
		}
		try
		{
			final URI uri = new URI(url);
			if (!uri.isAbsolute())
			{
				return true;
			}
			final String scheme = uri.getScheme();
			return "http".equals(scheme) || "https".equals(scheme);
		}
		catch (final URISyntaxException e)
		{
			LOG.error("UrlSchebmeValidator error", e);
			return false;
		}
	}
}
