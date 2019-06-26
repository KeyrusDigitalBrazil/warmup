/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service to find a field value for a given class
 */
public class ClassFieldFinder
{
	private static final Logger LOG = LoggerFactory.getLogger(ClassFieldFinder.class);

	protected static final String TYPECODE = "_TYPECODE";

	private ClassFieldFinder()
	{
	}

	/**
	 * Return the type code of a given class; expect a class of type or extending from {@code ItemModel}
	 *
	 * @param clazz
	 *           - a class from which to extract the type code
	 * @return the type code for the given class; <tt>null</tt> if the class does not define a type code field
	 */
	public static String getTypeCode(final Class<?> clazz)
	{
		try
		{
			return (String) clazz.getField(TYPECODE).get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			LOG.info(String.format("Failed to find typecode for class '%s'", clazz), e);
			return null;
		}
	}
}
