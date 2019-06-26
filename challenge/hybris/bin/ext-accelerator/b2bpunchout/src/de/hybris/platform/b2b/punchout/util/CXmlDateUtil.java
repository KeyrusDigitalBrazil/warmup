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
package de.hybris.platform.b2b.punchout.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Date utility for parsing and formatting {@link Date} objects in accordance with the cXML standard.
 */
public class CXmlDateUtil
{

	final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal
			.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));

	/**
	 * Parses a string into a date following the cXML specification.
	 * 
	 * @param dateString
	 *           the date String
	 * @return a parsed {@link Date} object
	 * @throws ParseException
	 *            when parsing failure occurs
	 */
	public Date parseString(final String dateString) throws ParseException
	{
		return DATE_FORMAT.get().parse(dateString);
	}

	/**
	 * Formats a {@link Date} instance into a {@link String}.
	 * 
	 * @param date
	 *           the date to use
	 * @return the String representation
	 */
	public String formatDate(final Date date)
	{
		return DATE_FORMAT.get().format(date);
	}

}
