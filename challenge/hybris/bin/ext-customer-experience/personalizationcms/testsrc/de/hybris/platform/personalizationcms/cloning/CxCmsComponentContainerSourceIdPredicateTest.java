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
/**
 *
 */
package de.hybris.platform.personalizationcms.cloning;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;


@UnitTest
public class CxCmsComponentContainerSourceIdPredicateTest
{
	private CxCmsComponentContainerSourceIdPredicate predicate;
	private Configuration configuration;

	@Before
	public void setup()
	{
		final ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
		configuration = Mockito.mock(Configuration.class);

		predicate = new CxCmsComponentContainerSourceIdPredicate();
		predicate.setConfigurationService(configurationService);

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void invalidModelTest()
	{
		//given
		final ItemModel item = new SimpleCMSComponentModel();
		final String attribute = SimpleCMSComponentModel.NAME;

		//when
		final boolean actual = predicate.test(item, attribute);

		//then
		Assert.assertFalse(actual);
	}

	@Test
	public void invalidAttributeTest()
	{
		//given
		final ItemModel item = new CxCmsComponentContainerModel();
		final String attribute = CxCmsComponentContainerModel.NAME;

		//when
		final boolean actual = predicate.test(item, attribute);

		//then
		Assert.assertFalse(actual);
	}

	@Test
	public void offConfigurationTest()
	{
		//given
		final ItemModel item = new CxCmsComponentContainerModel();
		final String attribute = CxCmsComponentContainerModel.SOURCEID;
		setConfiguration(true);

		//when
		final boolean actual = predicate.test(item, attribute);

		//then
		Assert.assertFalse(actual);
	}

	@Test
	public void onConfigurationTest()
	{
		//given
		final ItemModel item = new CxCmsComponentContainerModel();
		final String attribute = CxCmsComponentContainerModel.SOURCEID;
		setConfiguration(false);

		//when
		final boolean actual = predicate.test(item, attribute);

		//then
		Assert.assertTrue(actual);
	}

	private void setConfiguration(final boolean on)
	{
		Mockito.when(configuration.getBoolean(Matchers.anyString(), Matchers.anyBoolean())).thenReturn(on);
	}

}
