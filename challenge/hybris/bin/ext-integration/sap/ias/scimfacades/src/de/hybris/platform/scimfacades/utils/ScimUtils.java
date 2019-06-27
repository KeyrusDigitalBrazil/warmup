/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimfacades.utils;

import de.hybris.platform.scimfacades.ScimUserEmail;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;


/**
 * Scim Utility class
 */
public class ScimUtils
{

	/**
	 * Constructor to suppress creation of objects of utility class
	 */
	private ScimUtils()
	{

	}

	/**
	 * Get first primary email from list of emails
	 *
	 * @return emailValue
	 */
	public static ScimUserEmail getPrimaryEmail(final List<ScimUserEmail> emails)
	{
		if (CollectionUtils.isNotEmpty(emails))
		{
			final List<ScimUserEmail> filteredEmails = emails.stream().filter(item -> BooleanUtils.isTrue(item.getPrimary()))
					.collect(Collectors.toList());

			return CollectionUtils.isNotEmpty(filteredEmails) ? filteredEmails.get(0) : null;
		}
		return null;
	}

}
