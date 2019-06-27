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
package com.hybris.ymkt.consent.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class DefaultYmktConsentServiceTest
{
	Configuration configuration = Mockito.mock(Configuration.class);
	ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	ConsentFacade consentFacade = Mockito.mock(ConsentFacade.class);

	DefaultYmktConsentService service = new DefaultYmktConsentService();

	SessionService sessionService = Mockito.mock(SessionService.class);
	UserService userService = Mockito.mock(UserService.class);

	@Before
	public void setUp() throws Exception
	{
		service.setConfigurationService(configurationService);
		service.setConsentFacade(consentFacade);
		service.setSessionService(sessionService);
		service.setUserService(userService);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString("ymktsite")).thenReturn("YMKT_CONSENT");
	}

	@Test
	public void testGetAnonymousUserConsent()
	{
		Assert.assertFalse(service.getAnonymousUserConsent("ymkt"));
	}

	@Test
	public void testGetRegisteredUserConsent()
	{
		Assert.assertFalse(service.getRegisteredUserConsent("ymkt"));
	}

	@Test
	public void testgetUserConsent()
	{
		Assert.assertFalse(service.getUserConsent("ymkt"));
	}

}
