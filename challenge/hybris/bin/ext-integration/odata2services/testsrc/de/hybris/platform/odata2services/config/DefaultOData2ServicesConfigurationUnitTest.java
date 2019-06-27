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
 */

package de.hybris.platform.odata2services.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOData2ServicesConfigurationUnitTest
{
	private static final int DEFAULT_VALUE = 200;
	private static final String BATCH_LIMIT_PROPERTY_KEY = "odata2services.batch.limit";
	private static final int INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE = 1000;
	private static final String DEFAULT_PAGE_SIZE_PROPERTY = "odata2services.page.size.default";
	private static final String MAX_PAGE_SIZE_PROPERTY = "odata2services.page.size.max";
	@Mock
	private Configuration configuration;
	@Mock
	private ConfigurationService configurationService;
	@InjectMocks
	private DefaultOData2ServicesConfiguration defaultOData2ServicesConfiguration;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void testBatchLimit()
	{
		when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(100);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(100);
	}

	@Test
	public void testSetBatchLimit()
	{
		final int newLimit = 10;

		defaultOData2ServicesConfiguration.setBatchLimit(newLimit);

		verify(configuration).setProperty(BATCH_LIMIT_PROPERTY_KEY, String.valueOf(newLimit));
	}

	@Test
	public void testNegativeBatchLimit()
	{
		when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(-100);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testZeroBatchLimit()
	{
		when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(0);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testConversionExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new ConversionException())
				.when(configuration).getInt(BATCH_LIMIT_PROPERTY_KEY);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testNoSuchElementExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new NoSuchElementException())
				.when(configuration).getInt(BATCH_LIMIT_PROPERTY_KEY);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testValidDefaultPageSize()
	{
		when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(15);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(15);
	}

	@Test
	public void testInvalidDefaultPageSize()
	{
		when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(-5);
		
		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testMissingDefaultPageSize()
	{
		doThrow(new NoSuchElementException()).when(configuration).getInt(DEFAULT_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testInvalidDefaultPageSizeFormat()
	{
		doThrow(new ConversionException()).when(configuration).getInt(DEFAULT_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testValidMaxPageSize()
	{
		when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(15);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(15);
	}

	@Test
	public void testInvalidMaxPageSize()
	{
		when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(-5);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testMissingMaxPageSize()
	{
		doThrow(new NoSuchElementException()).when(configuration).getInt(MAX_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testInvalidMaxPageSizeFormat()
	{
		doThrow(new ConversionException()).when(configuration).getInt(MAX_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testDefaultPageSizeExceedsMaxValue()
	{
		when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(15);
		when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(12);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(12);
		verify(configuration).setProperty(DEFAULT_PAGE_SIZE_PROPERTY, String.valueOf(12));
	}
}
