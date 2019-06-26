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
package com.hybris.ymkt.common.odata;

import de.hybris.bootstrap.annotations.UnitTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class ODataConvertEdmServiceTest
{
	ODataConvertEdmService service = new ODataConvertEdmService();

	/** Date format reference : <a href="https://wiki.hybris.com/display/release5/ImpEx+Syntax">ImpEx Syntax </a> */
	@Test
	public void testConvertEdmDateTime() throws EdmException
	{
		// ImpEx default format
		DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

		// Proposed format for all timestamp in canonical model
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		final Date nowDate = new Date();
		final Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(nowDate);
		final long nowLong = nowDate.getTime();

		final String nowString = sdf.format(nowDate);
		System.out.println(nowString);
		System.out.println("2016-01-21T14:33:17.354-0500".length());

		Assert.assertEquals(new Date(nowLong), service.convertEdmDateTime(nowString, null));
	}
}
