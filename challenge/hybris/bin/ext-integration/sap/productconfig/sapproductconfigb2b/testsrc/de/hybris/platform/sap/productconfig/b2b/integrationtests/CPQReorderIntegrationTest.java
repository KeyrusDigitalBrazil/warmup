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
package de.hybris.platform.sap.productconfig.b2b.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.integrationtests.CPQFacadeLayerTest;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class CPQReorderIntegrationTest extends CPQFacadeLayerTest
{
	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade b2bCheckoutFacade;

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
	public void testReorder_OK() throws CommerceCartModificationException, InvalidCartException, CMSItemNotFoundException,
			ParseException, InvalidCredentialsException
	{
		final String oldConfigId = createConfigInCart();
		final OrderData order = placeOrder();

		// reorder
		assertTrue("order should be re-orderable", configOrderIntegrationFacade.isReorderable(order.getCode()));
		b2bCheckoutFacade.createCartFromOrder(order.getCode());
		final List<CartModificationData> modifications = cartFacade.validateCartData();

		// check messages
		for (final CartModificationData modification : modifications)
		{
			assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		}

		// check cart
		final String newConfigId = getAndCheckNewConfigId(oldConfigId);

		// check config
		ConfigurationData newConfigData = createConfigDataForGet(newConfigId);
		newConfigData = cpqFacade.getConfiguration(newConfigData);
		assertFalse(facadeConfigValueHelper.getCstic(newConfigData, "YSAP_POC_SIMPLE_FLAG").getDomainvalues().get(0).isSelected());
		assertEquals("125.0", facadeConfigValueHelper.getCstic(newConfigData, "WCEM_NUMBER_SIMPLE").getValue());
	}

	@Test
	public void testReorder_InvalidKB() throws CommerceCartModificationException, InvalidCartException, CMSItemNotFoundException,
			ParseException, InvalidCredentialsException
	{
		final String oldConfigId = createConfigInCartForProductWithInvalidKb();
		final OrderData order = placeOrder(false);
		if (isPersistentLifecycle())
		{
			makePersistentConfigInvalid(oldConfigId);
		}
		// reorder
		assertFalse("order should not be re-orderable", configOrderIntegrationFacade.isReorderable(order.getCode()));
		b2bCheckoutFacade.createCartFromOrder(order.getCode());
		final List<CartModificationData> modifications = cartFacade.validateCartData();

		// check messages
		for (final CartModificationData modification : modifications)
		{
			assertEquals(ConfigurationOrderIntegrationFacadeImpl.KB_NOT_VALID, modification.getStatusCode());
		}

		final String newConfigId = getAndCheckNewConfigId(oldConfigId);

		ConfigurationData newConfigData = createConfigDataForGet(newConfigId);
		newConfigData = cpqFacade.getConfiguration(newConfigData);
		assertTrue(facadeConfigValueHelper.getCstic(newConfigData, "YSAP_POC_SIMPLE_FLAG").getDomainvalues().get(0).isSelected());
		assertNull(facadeConfigValueHelper.getCstic(newConfigData, "WCEM_NUMBER_SIMPLE"));
	}

	protected void makePersistentConfigInvalid(final String oldConfigId)
	{
		final SearchResult<ProductConfigurationModel> searchResult = flexibleSearchService
				.search("SELECT {PK} from {productconfiguration} where {configurationid}='" + oldConfigId + "'");
		if (searchResult.getTotalCount() > 0)
		{
			final ProductConfigurationModel persitenceModel = searchResult.getResult().get(0);
			persitenceModel.setKbVersion("INVALID");
			modelService.save(persitenceModel);
		}
	}

	protected ConfigurationData createConfigDataForGet(final String newConfigId)
	{
		final ConfigurationData newConfigData = new ConfigurationData();
		newConfigData.setKbKey(KB_KEY_Y_SAP_SIMPLE_POC);
		newConfigData.setConfigId(newConfigId);
		return newConfigData;
	}

	protected String getAndCheckNewConfigId(final String oldConfigId)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		assertEquals(1, entries.size());

		final String newConfigId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entries.get(0).getItemPK());
		assertFalse(oldConfigId.equals(newConfigId));
		return newConfigId;
	}

	protected OrderData placeOrder() throws InvalidCredentialsException, InvalidCartException, CommerceCartModificationException
	{
		return placeOrder(true);
	}

	protected OrderData placeOrder(final boolean validateCart)
			throws InvalidCartException, InvalidCredentialsException, CommerceCartModificationException
	{
		login(USER_NAME, PASSWORD);
		final OrderData order = validateCart ? validateCartAndPlaceOrder() : checkoutFacade.placeOrder();
		assertTrue(CollectionUtils.isEmpty(cartFacade.getSessionCart().getEntries()));
		return order;
	}

	protected String createConfigInCart() throws CommerceCartModificationException
	{
		// create config and modify it
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		facadeConfigValueHelper.setCsticValue(configData, "YSAP_POC_SIMPLE_FLAG", "X", false);
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "WCEM_NUMBER_SIMPLE", "125");
		cpqFacade.updateConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "EXP_NO_USERS", "300");
		cpqFacade.updateConfiguration(configData);
		final String oldConfigId = configData.getConfigId();

		// add To cart and order it
		cpqCartFacade.addConfigurationToCart(configData);
		return oldConfigId;
	}

	protected String createConfigInCartForProductWithInvalidKb() throws CommerceCartModificationException
	{
		// Mock engine makes KB for WEC_DRAGON_BUS invalid
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_WEC_DRAGON_BUS);
		facadeConfigValueHelper.setCsticValue(configData, "YSAP_POC_SIMPLE_FLAG", "X", false);
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "WCEM_NUMBER_SIMPLE", "125");
		cpqFacade.updateConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "EXP_NO_USERS", "300");
		cpqFacade.updateConfiguration(configData);
		final String oldConfigId = configData.getConfigId();

		// add To cart and order it
		cpqCartFacade.addConfigurationToCart(configData);
		return oldConfigId;
	}

}
