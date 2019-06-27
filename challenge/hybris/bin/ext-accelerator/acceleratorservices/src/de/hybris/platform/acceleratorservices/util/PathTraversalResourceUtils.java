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
package de.hybris.platform.acceleratorservices.util;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorservices.exceptions.PathTraversalException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * Utility class verify the path for security purposes.
 */
public final class PathTraversalResourceUtils
{
	private static final String FORBIDDEN_PATH_SEGMENT_REGEX_CONFIG_PARAMETER = "acceleratorservices.pathtraversal.forbidden.regex";
	private static final String FALLBACK_FORBIDDEN_PATH_SEGMENT_REGEX = "(\\.\\.\\/|\\.\\.\\\\)";

	private PathTraversalResourceUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	/**
	 * Asserts that a given String represents a single path segment that can securely be used to access a file-system or
	 * classpath resource.
	 * <p>
	 * This assertion is performed in a platform independent but very conservative manner. In particular, the following
	 * conditions must be met: <br>
	 *
	 * * The pathSegment must not contain sequence of two periods followed by a forward slash or back slash. (These
	 * represent the parent directory respectively on many file-systems.) In the form ../ or ..\ <br>
	 *
	 * @param pathSegment
	 *           the path segment to check
	 * @throws PathTraversalException
	 *            if the pathSegment is not considered secure.
	 */
	public static void assertPathSegmentIsSecure(final String pathSegment)
	{
		validateParameterNotNullStandardMessage("pathSegment", pathSegment);

		if (StringUtils.isBlank(pathSegment))
		{
			throw new PathTraversalException(String.format("Supplied Path component %s is empty. It should not be empty",
					pathSegment));
		}

		final ConfigurationService configurationService = (ConfigurationService) Registry.getApplicationContext().getBean(
				"configurationService");

		final Pattern forbiddenPathSegmentPattern = Pattern.compile(configurationService.getConfiguration().getString(
				FORBIDDEN_PATH_SEGMENT_REGEX_CONFIG_PARAMETER, FALLBACK_FORBIDDEN_PATH_SEGMENT_REGEX));

		if (forbiddenPathSegmentPattern.matcher(pathSegment).find())
		{
			throw new PathTraversalException(
					String.format(
							"Path component %s matches the forbidden pattern %s , which might constitute the attempt of a path traversal attack.",
							pathSegment, forbiddenPathSegmentPattern.toString()));
		}

	}
}