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
package com.hybris.backoffice.widgets.processes.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultTimeRangeFactoryTest
{
	@Spy
	private final DefaultTimeRangeFactory timeRangeFactory = new DefaultTimeRangeFactory();

	@Before
	public void setUp()
	{
		doAnswer(inv ->
		{
			final Long number = (Long) inv.getArguments()[0];
			return number.longValue() > 1 ? inv.getArguments()[1].toString().concat("s") : inv.getArguments()[1];
		}).when(timeRangeFactory).getLabel(anyLong(), anyString());
	}

	@Test
	public void testCreateCommaSeparatedRanges()
	{
		final List<TimeRange> timeRanges = timeRangeFactory.createTimeRanges("1w,27m , 2d,3h");
		assertThat(timeRanges).hasSize(4);
		assertThat(timeRanges.get(0).getDuration().toMillis()).isEqualTo(TimeUnit.MINUTES.toMillis(27));
		assertThat(timeRanges.get(1).getDuration().toMillis()).isEqualTo(TimeUnit.HOURS.toMillis(3));
		assertThat(timeRanges.get(2).getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(2));
		assertThat(timeRanges.get(3).getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(7));
	}

	@Test
	public void testCreateCommaSeparatedRangesWithWrongInput()
	{
		final List<TimeRange> timeRanges = timeRangeFactory.createTimeRanges("1w;w2,d,w.d2,3h");
		assertThat(timeRanges).hasSize(1);
		assertThat(timeRanges.get(0).getDuration().toMillis()).isEqualTo(TimeUnit.HOURS.toMillis(3));
	}

	@Test
	public void testCreateOneMinute()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("1m");
		assertThat(timeRange.getLabel()).isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_MINUTE);
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.MINUTES.toMillis(1));
	}

	@Test
	public void testCreateManyMinutes()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("5m");
		assertThat(timeRange.getLabel())
				.isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_MINUTE.concat(DefaultTimeRangeFactory.PLURAL_SUFFIX));
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.MINUTES.toMillis(5));
	}

	@Test
	public void testCreateOneDay()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("1d");
		assertThat(timeRange.getLabel()).isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_DAY);
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(1));
	}

	@Test
	public void testCreateManyDays()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("5d");
		assertThat(timeRange.getLabel())
				.isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_DAY.concat(DefaultTimeRangeFactory.PLURAL_SUFFIX));
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(5));
	}

	@Test
	public void testCreateWrongInput()
	{
		assertThat(timeRangeFactory.createTimeRange("5d4d")).isNull();
		assertThat(timeRangeFactory.createTimeRange("05")).isNull();
		assertThat(timeRangeFactory.createTimeRange("5ww")).isNull();
		assertThat(timeRangeFactory.createTimeRange("5hh")).isNull();
		assertThat(timeRangeFactory.createTimeRange("5kd3")).isNull();
	}

	@Test
	public void testCreateOneHour()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("1h");
		assertThat(timeRange.getLabel()).isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_HOUR);
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.HOURS.toMillis(1));
	}

	@Test
	public void testCreateManyHours()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("5h");
		assertThat(timeRange.getLabel())
				.isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_HOUR.concat(DefaultTimeRangeFactory.PLURAL_SUFFIX));
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.HOURS.toMillis(5));
	}

	@Test
	public void testCreateOneWeek()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("1w");
		assertThat(timeRange.getLabel()).isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_WEEK);
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(7));
	}

	@Test
	public void testCreateManyWeeks()
	{
		final TimeRange timeRange = timeRangeFactory.createTimeRange("5w");
		assertThat(timeRange.getLabel())
				.isEqualTo(DefaultTimeRangeFactory.LABEL_PROCESSES_RANGE_WEEK.concat(DefaultTimeRangeFactory.PLURAL_SUFFIX));
		assertThat(timeRange.getDuration().toMillis()).isEqualTo(TimeUnit.DAYS.toMillis(5 * 7));
	}

}