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
package de.hybris.platform.webservicescommons.jaxb.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;


@UnitTest
public class DateAdapterTest
{
	private final DateAdapter dateAdapter = new DateAdapter();

	@Test
	public void nullUnmarshallTest() throws ParseException
	{
		assertNull(dateAdapter.unmarshal(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrongDateUnmarshallTest() throws ParseException
	{
		dateAdapter.unmarshal("blaBlaBla");
	}

	@Test
	public void correctDateUnmarshallTest() throws ParseException
	{
		//when
		final Date date = dateAdapter.unmarshal("2013-02-14T13:15:03-0800");

		//then
		assertNotNull(date);
		final DateTime dateTime = new DateTime(date, DateTimeZone.UTC);

		assertEquals(2013, dateTime.getYear());
		assertEquals(2, dateTime.getMonthOfYear());
		assertEquals(14, dateTime.getDayOfMonth());
		assertEquals(13 + 8, dateTime.getHourOfDay());
		assertEquals(15, dateTime.getMinuteOfHour());
		assertEquals(3, dateTime.getSecondOfMinute());
	}

	@Test
	public void nullMarshallTest()
	{
		assertNull(dateAdapter.marshal(null));
	}

	@Test
	public void dateMarshallTest()
	{
		//given
		final Date date = new Date(0L);

		//when
		final String actual = dateAdapter.marshal(date);

		//then
		assertEquals("1970-01-01T00:00:00+0000", actual);
	}
}
