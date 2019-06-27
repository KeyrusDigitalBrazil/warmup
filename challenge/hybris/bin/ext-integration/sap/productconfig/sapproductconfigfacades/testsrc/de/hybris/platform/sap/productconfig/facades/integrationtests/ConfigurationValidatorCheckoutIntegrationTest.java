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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartEntryValidationStrategyImpl;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ConfigurationValidatorCheckoutIntegrationTest extends CPQFacadeLayerTest
{

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}

	@Test
	public void testValidateNotComplete() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk},{externalConfiguration} from {cartentry} where {pk}='" + cartItemKey1 + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());

		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		Assert.assertEquals("We expect a validation error to occur as default configuration is not complete", 1,
				validateCartData.size());
		Assert.assertEquals("We expect CFG error status", ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION,
				validateCartData.get(0).getStatusCode());
	}

	@Test
	public void testValidateEmptyCart() throws Exception
	{
		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		Assert.assertEquals("We expect no validation errors to occur", 0, validateCartData.size());
	}

	@Test
	public void testValidateCart() throws Exception
	{
		cartFacade.addToCart("YSAP_NOCFG", 2);
		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		Assert.assertEquals("We expect no validation errors as item is not configurable", 0, validateCartData.size());
	}

	@Test
	public void testValidateCartNoStock() throws Exception
	{
		cartFacade.addToCart("YSAP_NOSTOCK", 2);
		Assert.assertEquals("We expect that entry cannot be added to cart at all", 0,
				cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testValidate2Entries() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		cpqCartFacade.addConfigurationToCart(configData);
		cartFacade.addToCart("YSAP_NOCFG", 2);

		final List<CartModificationData> validateCartData = cartFacade.validateCartData();
		Assert.assertEquals("We expect one validation error to occur as default configuration for first item is not complete", 1,
				validateCartData.size());
	}
}
