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
package de.hybris.platform.sap.saprevenuecloudorder.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

public class SapRevenueCloudSubscriptionUtil {

    private static final Logger LOG = Logger.getLogger(SapRevenueCloudSubscriptionUtil.class);
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    
	private SapRevenueCloudSubscriptionUtil() {
		throw new IllegalStateException("Cannot instantiate utility class");
	}
	
    public static Date formatDate(final String stringDate)
    {
        try {
            if (stringDate != null) 
            {
				final DateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.ENGLISH);
                return format.parse(stringDate);
            } else 
            {
                final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
                return format.parse(LocalDate.now().toString());
            }
        } 
        catch (final ParseException e) 
        {
            LOG.error(e.getMessage());
        }
        return null;
    }
    
    public static Date stringToDate(final String date) 
    {
        try 
        {
            if (date != null) {
                final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
                return format.parse(date);
            }
        }
        catch (final ParseException e)
        {
            LOG.error(e.getMessage());
        }
        return null;
    }
    
    public static String dateToString(final Date currentDate)
    {
    	if(currentDate!=null)
		{
		    final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
		    return format.format(currentDate);
		}
    	return null;
    }
    
}
