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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;


@SuppressWarnings("javadoc")
@IntegrationTest
public class AddToCartFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(AddToCartFacadeIntegrationTest.class);

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
	public void testAddToCart_checkQty() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final long myQty = 5L;
		configData.setQuantity(myQty);
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk},{externalConfiguration} from {cartentry} where {pk}='" + cartItemKey + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());
		final CartEntryModel cartEntry = (CartEntryModel) searchResult.getResult().get(0);
		final Long qty = cartEntry.getQuantity();
		assertEquals(Long.valueOf(myQty), qty);
	}

	@Test
	public void testAddToCartAttachedConfiguration() throws Exception
	{
		assumeTrue(isPersistentLifecycle());
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);

		Assert.assertNotNull(cartItemKey);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk},{productConfiguration} from {cartentry} where {pk}='" + cartItemKey + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());
		final CartEntryModel cartEntry = (CartEntryModel) searchResult.getResult().get(0);
		final ProductConfigurationModel productConfiguration = cartEntry.getProductConfiguration();
		assertNotNull(productConfiguration);
		final Collection<ProductModel> products = productConfiguration.getProduct();
		assertNotNull(products);
		assertEquals(0, products.size());
	}


	@Test
	public void testAddToCart_xmlInDB() throws Exception
	{
		assumeFalse(isPersistentLifecycle());
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk},{externalConfiguration} from {cartentry} where {pk}='" + cartItemKey + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());
		final CartEntryModel cartEntry = (CartEntryModel) searchResult.getResult().get(0);
		final String xml = cartEntry.getExternalConfiguration();
		LOG.debug("ExternalConfig from DB: " + xml);

		// check that there is some parseable XML in DB as external configuration
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final InputStream source = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		final Document doc = dBuilder.parse(source);
		assertNotNull(doc);
	}

	@Test
	public void testAddToCart_ConfigurationProductInfos() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk} from {cartentry} where {pk}='" + cartItemKey + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());
		final CartEntryModel cartEntry = (CartEntryModel) searchResult.getResult().get(0);
		final List<AbstractOrderEntryProductInfoModel> productInfos = cartEntry.getProductInfos();
		Assert.assertEquals(1, productInfos.size());
		Assert.assertTrue(productInfos.get(0) instanceof CPQOrderEntryProductInfoModel);

		final CPQOrderEntryProductInfoModel info = (CPQOrderEntryProductInfoModel) productInfos.get(0);
		if (LOG.isDebugEnabled())
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Product Info: ").append(info.getCpqCharacteristicName()).append(", ")
					.append(info.getCpqCharacteristicAssignedValues());
			LOG.debug(sb.toString());
		}
		Assert.assertEquals(ConfiguratorType.CPQCONFIGURATOR, info.getConfiguratorType());
		Assert.assertEquals(ProductInfoStatus.SUCCESS, info.getProductInfoStatus());
		Assert.assertEquals("Simple Flag: Hide options", info.getCpqCharacteristicName());
		Assert.assertEquals("Hide", info.getCpqCharacteristicAssignedValues());
	}

	@Test
	public void testAddToCart_sameProductAddedTwice() throws CommerceCartModificationException
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey2);

		Assert.assertFalse("expected new cart item, not same one!", cartItemKey1.equals(cartItemKey2));
		Assert.assertEquals("Adding same configurable product twice should lead two distinct entries in cart", 2,
				cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCart_update() throws CommerceCartModificationException
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertEquals("new cartItem created instead of updated of existing one", cartItemKey1, cartItemKey2);
		Assert.assertEquals("new cartItem created instead of updated of existing one", 1,
				cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCart_updateRemovedProduct() throws CommerceCartModificationException
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		final Map<Integer, Long> quantities = new HashMap();
		final AbstractOrderEntryModel cartItem1 = cartService.getSessionCart().getEntries().get(0);
		quantities.put(cartItem1.getEntryNumber(), Long.valueOf(0));
		cartService.updateQuantities(cartService.getSessionCart(), quantities);
		configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		Assert.assertFalse("expected new cart item, not same one!", cartItemKey1.equals(cartItemKey2));
		Assert.assertEquals("there should be only one item in the cart", 1, cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCart_updateRemovedProductWithProductLinkPersisting() throws CommerceCartModificationException
	{
		final ConfigurationData firstConfigData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(firstConfigData);
		Assert.assertNotNull(cartItemKey1);
		final Map<Integer, Long> quantities = new HashMap();
		final AbstractOrderEntryModel cartItem1 = cartService.getSessionCart().getEntries().get(0);
		quantities.put(cartItem1.getEntryNumber(), Long.valueOf(0));
		cartService.updateQuantities(cartService.getSessionCart(), quantities);
		final ConfigurationData finalConfigData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		assertNotEquals("ConfigId should'nt be equal: ", firstConfigData.getConfigId(), finalConfigData.getConfigId());
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(finalConfigData);
		Assert.assertFalse("expected new cart item, not same one!", cartItemKey1.equals(cartItemKey2));
		Assert.assertEquals("there should be only one item in the cart", 1, cartService.getSessionCart().getEntries().size());
	}
}
