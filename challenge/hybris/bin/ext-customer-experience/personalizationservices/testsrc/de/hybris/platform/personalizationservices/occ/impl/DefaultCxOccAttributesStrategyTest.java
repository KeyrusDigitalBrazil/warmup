/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */

package de.hybris.platform.personalizationservices.occ.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.personalizationservices.stub.MockTimeService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxOccAttributesStrategyTest
{
	private static final String PERSONALIZATION_ID = "personalizationId";
	private static final String CUSTOM_PERSONALIZATION_HEADER = "customHeaderPersonalizationId";

	@Mock
	protected DefaultSessionTokenService defaultSessionTokenService;

	private final TimeService timeService = new MockTimeService();

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private CxConfigurationService cxConfigurationService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	Configuration configuration;

	@Mock
	CxConfigModel configModel;

	private final DefaultCxOccAttributesStrategy defaultCxOccAttributesStrategy = new DefaultCxOccAttributesStrategy();

	@Before
	public void setupTest()
	{
		MockitoAnnotations.initMocks(this);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(cxConfigurationService.getConfiguration()).thenReturn(Optional.of(configModel));

		defaultCxOccAttributesStrategy.setConfigurationService(configurationService);
		defaultCxOccAttributesStrategy.setCxConfigurationService(cxConfigurationService);
		defaultCxOccAttributesStrategy.setTimeService(timeService);
		defaultCxOccAttributesStrategy.setTokenService(defaultSessionTokenService);
	}

	@Test
	public void shouldReturnEmptyPersonalizationIdIfRequestNull()
	{
		//when
		final Optional<String> personalizationId = defaultCxOccAttributesStrategy.readPersonalizationId(null);
		//then
		assertFalse(personalizationId.isPresent());
	}

	@Test
	public void shouldReturnEmptyPersonalizationIdIfRequestEmpty()
	{
		//when
		final Optional<String> personalizationId = defaultCxOccAttributesStrategy.readPersonalizationId(request);
		//then
		assertFalse(personalizationId.isPresent());
	}

	@Test
	public void shouldReadPersonalizationIdFromHeaderIfItsNotEmpty()
	{
		//given
		when(configurationService.getConfiguration().getString(PersonalizationservicesConstants.PERSONALIZATION_ID_HEADER))
				.thenReturn(CUSTOM_PERSONALIZATION_HEADER);
		when(request.getHeader(any())).thenReturn(PERSONALIZATION_ID);
		//when
		final Optional<String> personalizationId = defaultCxOccAttributesStrategy.readPersonalizationId(request);
		//then
		assertTrue(personalizationId.isPresent() && PERSONALIZATION_ID.equals(personalizationId.get()));
	}

	@Test
	public void shouldSetCookieIfPersonalizationCookieEnabled()
	{
		//given
		when(configurationService.getConfiguration().getString(PersonalizationservicesConstants.PERSONALIZATION_ID_HEADER))
				.thenReturn(CUSTOM_PERSONALIZATION_HEADER);
		when(cxConfigurationService.getConfiguration().map(CxConfigModel::getOccPersonalizationIdCookieEnabled)
				.orElse(Boolean.FALSE).booleanValue()).thenReturn(Boolean.TRUE);
		//when
		defaultCxOccAttributesStrategy.setPersonalizationId(PERSONALIZATION_ID, request, response);
		//then
		verify(response, times(1)).addCookie(any());
	}

	@Test
	public void shouldSetDefaultCookieIfPersonalizationCustomCookieEmpty()
	{
		//given
		when(configurationService.getConfiguration().getString(PersonalizationservicesConstants.PERSONALIZATION_ID_HEADER))
				.thenReturn(StringUtils.EMPTY);
		when(cxConfigurationService.getConfiguration().map(CxConfigModel::getOccPersonalizationIdCookieEnabled)
				.orElse(Boolean.FALSE).booleanValue()).thenReturn(Boolean.TRUE);
		//when
		defaultCxOccAttributesStrategy.setPersonalizationId(PERSONALIZATION_ID, request, response);
		//then
		verify(response, times(1)).addCookie(any());
	}

	@Test
	public void shouldNotSetCookieIfPersonalizationCookieDisabled()
	{
		//given
		when(configurationService.getConfiguration().getString(PersonalizationservicesConstants.PERSONALIZATION_ID_HEADER))
				.thenReturn(CUSTOM_PERSONALIZATION_HEADER);
		when(cxConfigurationService.getConfiguration().map(CxConfigModel::getOccPersonalizationIdCookieEnabled)
				.orElse(Boolean.FALSE).booleanValue()).thenReturn(Boolean.FALSE);
		//when
		defaultCxOccAttributesStrategy.setPersonalizationId(PERSONALIZATION_ID, request, response);
		//then
		verify(response, times(0)).addCookie(any());
	}

	@Test
	public void shouldSetDefaultPersonalizationHeaderIfCustomIsEmpty()
	{
		//given
		when(configurationService.getConfiguration().getString(PersonalizationservicesConstants.PERSONALIZATION_ID_HEADER))
				.thenReturn(StringUtils.EMPTY);
		//when
		defaultCxOccAttributesStrategy.setPersonalizationId(PERSONALIZATION_ID, request, response);
		//then
		verify(response, times(1)).setHeader(PersonalizationservicesConstants.PERSONALIZATION_DEFAULT_ID_HEADER,
				PERSONALIZATION_ID);
	}

}
