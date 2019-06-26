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
package de.hybris.platform.sap.sapproductavailability.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
        
        private DateUtil(){
            //private constructor to hide public constructor
        }

	private static final Logger LOG = Logger.getLogger(DateUtil.class);
	
	 private static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	
	/**
	 * Convenience method to convert date to String
	 * @param date
	 * @return formatted String
	 */
	public static final String formatDate(Date date) {
		
		if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + date + "' to String with mask '"
                      + DEFAULT_DATE_PATTERN + "'");
        }

		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		return df.format(date);
		
	}
     
	
	/**
	 * Convenience method to convert String to Date
     * in the format you specify on input
	 * @param strDate
	 * @return
	 */
    public static final Date convertDate(String strDate) {
    	
       
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_PATTERN);

        if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + strDate + "' to date with mask '"
                      + DEFAULT_DATE_PATTERN + "'");
        }

        Date date = null;
        try {
        	date = df.parse(strDate);
        } catch (ParseException pe) {
        	LOG.error("error while parsing date " + pe.getMessage(), pe);
        }

        return (date);
    }
	
}
