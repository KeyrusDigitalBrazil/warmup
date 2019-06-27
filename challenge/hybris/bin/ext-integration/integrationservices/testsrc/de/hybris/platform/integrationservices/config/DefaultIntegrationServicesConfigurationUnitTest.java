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

package de.hybris.platform.integrationservices.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
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
public class DefaultIntegrationServicesConfigurationUnitTest
{
	private static final String SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY = "integrationservices.sap.passport.systemid";
	private static final String SAP_PASSPORT_SERVICE_PROPERTY_KEY = "integrationservices.sap.passport.service";
	private static final String SAP_PASSPORT_USER_PROPERTY_KEY = "integrationservices.sap.passport.user";
	private static final String MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY = "integrationservices.media.persistence.media.name.prefix";

	@Mock
	private Configuration configuration;
	@Mock
	private ConfigurationService configurationService;
	@InjectMocks
	private DefaultIntegrationServicesConfiguration integrationServicesConfiguration;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void testMediaPersistenceMediaNamePrefix()
	{
		when(configuration.getString(MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY)).thenReturn("SOME_PREFIX");
		assertThat(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix()).isEqualTo("SOME_PREFIX");
	}

	@Test
	public void testMediaPersistenceMediaNamePrefixDefaultValueWhenNoProperty()
	{
		doThrow(NoSuchElementException.class)
				.when(configuration).getString(MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY);
		assertThat(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix()).isEqualTo("Payload_");
	}

	@Test
	public void testMediaPersistenceMediaNamePrefixDefaultValueWhenInvalidProperty()
	{
		doThrow(ConversionException.class)
				.when(configuration).getString(MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY);
		assertThat(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix()).isEqualTo("Payload_");
	}

	@Test
	public void testMediaPersistenceMediaNamePrefixDefaultValueWhenEmptyProperty()
	{
		when(configuration.getString(MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY)).thenReturn("");
		assertThat(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix()).isEqualTo("Payload_");
	}

	@Test
	public void testSapPassportSystemId()
	{
		when(configuration.getString(SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY)).thenReturn("SOME_SYSTEM_ID");
		assertThat(integrationServicesConfiguration.getSapPassportSystemId()).isEqualTo("SOME_SYSTEM_ID");
	}

	@Test
	public void testSapPassportSystemIdDefaultValueWhenNoProperty()
	{
		doThrow(NoSuchElementException.class)
				.when(configuration).getString(SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY);
		assertThat(integrationServicesConfiguration.getSapPassportSystemId()).isEqualTo("SAP Commerce");
	}

	@Test
	public void testSapPassportService()
	{
		when(configuration.getInt(SAP_PASSPORT_SERVICE_PROPERTY_KEY)).thenReturn(1399);
		assertThat(integrationServicesConfiguration.getSapPassportServiceValue()).isEqualTo(1399);
	}

	@Test
	public void testSapPassportServiceDefaultValueWhenNoProperty()
	{
		doThrow(NoSuchElementException.class)
				.when(configuration).getInt(SAP_PASSPORT_SERVICE_PROPERTY_KEY);
		assertThat(integrationServicesConfiguration.getSapPassportServiceValue()).isEqualTo(39);
	}

	@Test
	public void testSapPassportUser()
	{
		when(configuration.getString(SAP_PASSPORT_USER_PROPERTY_KEY)).thenReturn("SOME_USER_ID");
		assertThat(integrationServicesConfiguration.getSapPassportUser()).isEqualTo("SOME_USER_ID");
	}

	@Test
	public void testSapPassportUserDefaultValueWhenNoProperty()
	{
		doThrow(NoSuchElementException.class)
				.when(configuration).getString(SAP_PASSPORT_USER_PROPERTY_KEY);
		assertThat(integrationServicesConfiguration.getSapPassportUser()).isEqualTo("");
	}
}
