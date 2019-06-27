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
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DateDataToAttributeAttributeContentConverterTest
{
	private DateDataToAttributeContentConverter converter = new DateDataToAttributeContentConverter();

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenConvertValidISOStringWillReturnMatchingDate()
	{
		final LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
		final Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
		Date expectedDate = Date.from(instant);
		assertThat(converter.convert("2000-01-01T00:00:00+0000"), equalTo(expectedDate));
	}


	@Test
	public void whenConvertValidISOStringWillReturnMatchingDateEvenWithDifferentTimeZone()
	{
		final LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
		final Instant instant = localDateTime.toInstant(ZoneOffset.ofHoursMinutes(-4, 0));
		Date expectedDate = Date.from(instant);
		assertThat(converter.convert("2000-01-01T00:00:00-0400"), equalTo(expectedDate));
	}
}
