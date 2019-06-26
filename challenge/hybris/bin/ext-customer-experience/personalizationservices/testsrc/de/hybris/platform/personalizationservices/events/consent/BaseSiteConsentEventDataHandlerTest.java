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
package de.hybris.platform.personalizationservices.events.consent;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventDataConsumer;
import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventDataProvider;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class BaseSiteConsentEventDataHandlerTest
{
	private AnonymousConsentChangeEventDataProvider provider;
	private AnonymousConsentChangeEventDataConsumer consumer;

	private BaseSiteService baseSiteService;
	private BaseSiteModel baseSiteModel;

	@Before
	public void setup()
	{
		final BaseSiteConsentEventDataHandler data = new BaseSiteConsentEventDataHandler();
		provider = data;
		consumer = data;

		baseSiteService = Mockito.mock(BaseSiteService.class);
		data.setBaseSiteService(baseSiteService);

		final String uid = "uid-abc";
		baseSiteModel = new BaseSiteModel(uid);
	}

	@Test
	public void providerNullTest()
	{
		//given
		doReturn(null).when(baseSiteService).getCurrentBaseSite();

		//when
		final Map<String, String> data = provider.getData();

		//then
		Assert.assertTrue("Data should not be populated when base site is not provided", data.isEmpty());
	}

	@Test
	public void providerTest()
	{
		//given
		doReturn(baseSiteModel).when(baseSiteService).getCurrentBaseSite();

		//when
		final Map<String, String> data = provider.getData();

		//then
		Assert.assertFalse("Data should be populated when base site is provided", data.isEmpty());
		final String baseSiteUid = data.get("baseSite");
		Assert.assertEquals(baseSiteModel.getUid(), baseSiteUid);

	}

	@Test
	public void consumerNullDataTest()
	{
		//given
		final Map<String, String> data = null;

		//when
		consumer.process(data);

		//then
		verify(baseSiteService, times(0)).setCurrentBaseSite(anyString(), anyBoolean());
	}

	@Test
	public void consumerNoDataTest()
	{
		//given
		final Map<String, String> data = new HashMap<>();

		//when
		consumer.process(data);

		//then
		verify(baseSiteService, times(0)).setCurrentBaseSite(anyString(), anyBoolean());
	}

	@Test
	public void consumerNoChangeTest()
	{
		//given
		final Map<String, String> data = new HashMap<>();
		data.put("baseSite", baseSiteModel.getUid());
		doReturn(baseSiteModel).when(baseSiteService).getCurrentBaseSite();
		//when
		consumer.process(data);

		//then
		verify(baseSiteService, times(0)).setCurrentBaseSite(anyString(), anyBoolean());
	}

	@Test
	public void consumerNullInSessionTest()
	{
		//given
		final Map<String, String> data = new HashMap<>();
		data.put("baseSite", baseSiteModel.getUid());
		doReturn(null).when(baseSiteService).getCurrentBaseSite();
		//when
		consumer.process(data);

		//then
		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteModel.getUid(), true);
	}

	@Test
	public void consumerDifferentInSessionTest()
	{
		//given
		final String baseSiteUid = "test";

		final Map<String, String> data = new HashMap<>();
		data.put("baseSite", baseSiteUid);
		doReturn(baseSiteModel).when(baseSiteService).getCurrentBaseSite();
		//when
		consumer.process(data);

		//then
		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteUid, true);
	}


}
