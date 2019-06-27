/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.asn.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;


/**
 * Test for delayed release date calculation.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DelayedReleaseDateStrategyTest
{
	@InjectMocks
	private DelayedReleaseDateStrategy delayedReleaseDateStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private Date delayedDate;
	@Mock
	private AdvancedShippingNoticeEntryModel asnEntry;
	@Mock
	private AdvancedShippingNoticeModel asn;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Date asnDate;

	@Before
	public void setUp() throws ParseException
	{
		asnDate = sdf.parse("2016-12-19");
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(asn.getReleaseDate()).thenReturn(sdf.parse("2016-12-19"));
		when(asnEntry.getAsn()).thenReturn(asn);
	}

	/**
	 * Should return release date from ASN increased by 3 days, as configuration property value is 3.
	 */
	@Test
	public void shouldGetExpectedDelayedDate() throws ParseException
	{
		//Given
		delayedDate = sdf.parse("2016-12-22");
		when(configuration.getInt(DelayedReleaseDateStrategy.DELAY_DAYS)).thenReturn(3);

		// When
		final Date releaseDate = delayedReleaseDateStrategy.getReleaseDateForStockLevel(asnEntry);

		// Then
		assertEquals(delayedDate, releaseDate);
	}

	/**
	 * Should return release date same as on ASN, as there was an Exception thrown when trying to retrieve data from
	 * configuration.
	 */
	@Test
	public void shouldGetAsnDate()
	{
		//Given
		when(configuration.getInt(DelayedReleaseDateStrategy.DELAY_DAYS)).thenThrow(new NumberFormatException());

		//When
		final Date releaseDate = delayedReleaseDateStrategy.getReleaseDateForStockLevel(asnEntry);

		// Then
		assertEquals(asnDate, releaseDate);
	}
}
