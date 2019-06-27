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
package de.hybris.platform.personalizationyprofile.event;

import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.CONSENT_REFERENCE_SESSION_ATTR_KEY;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventDataConsumer;
import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventDataProvider;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class ConsentReferenceEventDataHandlerTest
{
	private AnonymousConsentChangeEventDataProvider provider;
	private AnonymousConsentChangeEventDataConsumer consumer;

	private SessionService sessionService;

	@Before
	public void setup()
	{
		final ConsentReferenceEventDataHandler data = new ConsentReferenceEventDataHandler();
		provider = data;
		consumer = data;

		sessionService = Mockito.mock(SessionService.class);
		data.setSessionService(sessionService);
	}

	@Test
	public void providerNullTest()
	{
		//given
		doReturn(null).when(sessionService).getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY);

		//when
		final Map<String, String> data = provider.getData();

		//then
		Assert.assertTrue("Data should not be populated when base site is not provided", data.isEmpty());
	}

	@Test
	public void providerTest()
	{
		//given
		final String id = "abc";
		doReturn(id).when(sessionService).getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY);

		//when
		final Map<String, String> data = provider.getData();

		//then
		Assert.assertFalse("Data should be populated when base site is provided", data.isEmpty());
		final String baseSiteUid = data.get(CONSENT_REFERENCE_SESSION_ATTR_KEY);
		Assert.assertEquals(id, baseSiteUid);

	}

	@Test
	public void consumerNullDataTest()
	{
		//given
		final Map<String, String> data = null;

		//when
		consumer.process(data);

		//then
		verify(sessionService, times(0)).setAttribute(anyString(), anyBoolean());
	}

	@Test
	public void consumerNoDataTest()
	{
		//given
		final Map<String, String> data = new HashMap<>();

		//when
		consumer.process(data);

		//then
		verify(sessionService, times(0)).setAttribute(anyString(), anyBoolean());
	}

	@Test
	public void consumerChangeTest()
	{
		//given
		final String id = "abc";
		final Map<String, String> data = new HashMap<>();
		data.put(CONSENT_REFERENCE_SESSION_ATTR_KEY, id);
		//when
		consumer.process(data);

		//then
		verify(sessionService, times(1)).setAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY, id);
	}

}
