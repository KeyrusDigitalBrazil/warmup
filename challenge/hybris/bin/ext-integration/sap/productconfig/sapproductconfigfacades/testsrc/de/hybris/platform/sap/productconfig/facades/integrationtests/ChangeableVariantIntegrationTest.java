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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class ChangeableVariantIntegrationTest extends CPQFacadeLayerTest
{
	private static final Logger LOG = Logger.getLogger(ChangeableVariantIntegrationTest.class);

	private static final String VALUE_100 = "100";
	private static final String VARIANT_CODE = "CONF_PIPE-30-15-ST";
	private static final String CPQ_PIPE_LENGTH = "CPQ_PIPE_LENGTH";



	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		login(USER_NAME, PASSWORD);
	}

	private static KBKeyData KB_KEY_CONF_PIPE_30_15_ST;

	static
	{
		KB_KEY_CONF_PIPE_30_15_ST = new KBKeyData();
		KB_KEY_CONF_PIPE_30_15_ST.setProductCode(VARIANT_CODE);
	}

	@Override
	protected void importCPQTestData() throws Exception
	{
		super.importCPQTestData();
		importCsv("/sapproductconfigservices/test/sapProductConfig_changeableVariants_testData.impex", "utf-8");
		importCPQUserData();
	}


	@Test
	public void testOrderDirectlyWithoutModification() throws CommerceCartModificationException, InvalidCartException
	{
		cartFacade.addToCart(VARIANT_CODE, 1l);
		checkCart();
		final ConfigurationOverviewData configOV = placeOrderAndLoadOrderOv();
		checkOrderOv(configOV, "");
	}

	@Test
	public void testConfigureAndOrder() throws CommerceCartModificationException, InvalidCartException
	{
		final ConfigurationData configuration = cpqFacade.getConfiguration(KB_KEY_CONF_PIPE_30_15_ST);
		checkProductCode(configuration.getKbKey().getProductCode());
		facadeConfigValueHelper.setCstic(configuration, CPQ_PIPE_LENGTH, VALUE_100);
		cpqFacade.updateConfiguration(configuration);
		cpqCartFacade.addConfigurationToCart(configuration);
		checkCart();
		final ConfigurationOverviewData configOV = placeOrderAndLoadOrderOv();
		checkOrderOv(configOV, VALUE_100);

	}


	protected ConfigurationOverviewData placeOrderAndLoadOrderOv() throws InvalidCartException, CommerceCartModificationException
	{
		final OrderData order = validateCartAndPlaceOrder();
		final ConfigurationOverviewData configOV = configOrderIntegrationFacade.getConfiguration(order.getCode(),
				order.getEntries().get(0).getEntryNumber());
		return configOV;
	}


	protected void checkOrderOv(final ConfigurationOverviewData configOV, final String expectedLength)
	{
		checkProductCode(configOV.getProductCode());

		ConfigurationData configuration = new ConfigurationData();
		configuration.setConfigId(configOV.getId());
		configuration = cpqFacade.getConfiguration(configuration);
		checkProductCode(configuration.getKbKey().getProductCode());

		final String value = facadeConfigValueHelper.getCstic(configuration, CPQ_PIPE_LENGTH).getFormattedValue();
		assertEquals(CPQ_PIPE_LENGTH + " has worng value", expectedLength, value);

	}

	protected void checkCart()
	{
		assertEquals("no cart item created ", 1, cartFacade.getSessionCart().getEntries().size());
		final String configId = cpqAbstractOrderEntryLinkStrategy
				.getConfigIdForCartEntry(cartFacade.getSessionCart().getEntries().get(0).getItemPK());
		assertNotNull("no runtime config created", configId);
		final ConfigurationData configurationData = new ConfigurationData();
		configurationData.setConfigId(configId);
		final ConfigurationData configuration = cpqFacade.getConfiguration(configurationData);
		checkProductCode(configuration.getKbKey().getProductCode());
	}


	protected void checkProductCode(final String code)
	{
		assertEquals("unexpected switch to KMAT happened", VARIANT_CODE, code);
	}


}
